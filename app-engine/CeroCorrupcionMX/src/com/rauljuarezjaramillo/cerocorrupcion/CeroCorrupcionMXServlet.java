package com.rauljuarezjaramillo.cerocorrupcion;

import static com.rauljuarezjaramillo.cerocorrupcion.util.Util.doubleOCero;
import static com.rauljuarezjaramillo.cerocorrupcion.util.Util.intOCero;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimeType;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.MimeTypes;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.net.MediaType;
import com.rauljuarezjaramillo.cerocorrupcion.persistencia.BD;
import com.rauljuarezjaramillo.cerocorrupcion.seguridad.Encrypter;
import com.rauljuarezjaramillo.cerocorrupcion.seguridad.Encrypter.Identificador;
import com.rauljuarezjaramillo.cerocorrupcion.util.QR;
import com.rauljuarezjaramillo.cerocorrupcion.util.ServletGeneral;
import com.rauljuarezjaramillo.cerocorrupcion.util.notificaciones.Mailer;

@SuppressWarnings("serial")
public class CeroCorrupcionMXServlet extends ServletGeneral {
	private final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
	private static final String BUCKET_NAME = "cerocorrupcionmx.appspot.com";

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, JSONException {
		String pathInfo = null;
		if ((request.getPathInfo() != null) && (!request.getPathInfo().trim().equals("")) && (!request.getPathInfo().trim().equals("/"))) {
			pathInfo = request.getPathInfo().substring(1);
		}

		Map<String, Object> params = crearMapaParametros(request);
		params.put("userId", params.get("userid"));
		System.out.println("params = " + params);
		if (pathInfo != null) {
			if (pathInfo.startsWith("BuscarPersonas")) {
				if (params.get("q") != null) {
					BD bd = new BD();
					bd.abrir();
					List<Map<String, String>> listaResultados = bd.buscarPersonas(((String) params.get("q")).toUpperCase());
					bd.cerrar();
					JSONArray jsonArray = new JSONArray(listaResultados);
					response.setContentType("application/json");
					response.getWriter().print(jsonArray.toString());
				} else {
					response.setContentType("application/json");
					response.getWriter().print("[]");
				}
			} else if (pathInfo.equals("GuardarDenuncia")) {
				List<Map<String, Object>> archivos = null;
				BD bd = new BD();
				bd.abrir();
				int idDenunciaGenerado = bd.insertarDenuncia(intOCero((String) params.get("tipo")), 2/* Estatus Inicial */, "X",
						intOCero((String) params.get("idpersona")), (String) params.get("persona"), (String) params.get("hechos"),
						(String) params.get("ubicacion"), doubleOCero((String) params.get("latitud")), doubleOCero((String) params.get("longitud")),
						intOCero((String) params.get("anonima")) == 0 ? 2 : 1, (String) params.get("nombre"), (String) params.get("direccion"),
						(String) params.get("telefono"), (String) params.get("correo"), intOCero((String) params.get("iddenuncialocal")),
						(String) params.get("userId"));
				boolean b = idDenunciaGenerado != -1;
				// Encriptar folio
				ServletContext context = getServletContext();
				String fullPath = context.getRealPath("/WEB-INF/encrypter_ccmx.key");
				Encrypter enc = new Encrypter();
				try {
					enc.setUp(fullPath);
					String encriptado = enc.encrypt(new Identificador(idDenunciaGenerado));
					System.out.println(encriptado);
					bd.actualizarFolioDenuncia(idDenunciaGenerado, encriptado);
					// Actualizar denuncia
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("denunciaGuarada = " + b);
				System.out.println("denunciaGenerada = " + idDenunciaGenerado);
				response.setContentType("application/json");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("estatus", b ? "OK" : "ERROR");
				if (b) {
					// String correo = (String) params.get("correo");
					String asunto = "Denuncia o Queja recibida";
					String cuerpo = "Se ha recibido la denuncia. Estos son los datos: <br />";
					Map<String, Object> denuncia = bd.getDenuncia(idDenunciaGenerado);

					// Datos de salida
					jsonObject.put("iddenuncia", idDenunciaGenerado);
					jsonObject.put("idestatus", denuncia.get("IDESTATUS") != null ? denuncia.get("IDESTATUS").toString() : "2");
					jsonObject.put("estatus_descripcion", (String) denuncia.get("ESTATUS"));
					jsonObject.put("folio", (String) denuncia.get("FOLIO"));

					archivos = bd.getArchivos(intOCero((String) params.get("iddenuncia")));

					for (Map.Entry<String, Object> elem : denuncia.entrySet()) {
						cuerpo = cuerpo.concat("<strong>").concat("ID".equals(elem.getKey()) ? "FOLIO" : (String) elem.getKey())
								.concat(":</strong> ");
						if (elem.getValue() != null) {
							if ("TIPO".equals(elem.getKey())) {
								if (new Integer(1).equals(elem.getValue()))
									cuerpo = cuerpo.concat("Denuncia");
								else
									cuerpo = cuerpo.concat("Queja");
							} else if ("ANONIMA".equals(elem.getKey())) {
								if (new Integer(1).equals(elem.getValue()))
									cuerpo = cuerpo.concat("Si");
								else
									cuerpo = cuerpo.concat("No");
							} else {
								cuerpo = cuerpo.concat(elem.getValue().toString());
							}
						}

						cuerpo = cuerpo.concat("<br />");
					}
					cuerpo = cuerpo.concat("<br /><strong>" + (archivos != null ? archivos.size() : 0)
							+ " Evidencia(s) </strong>(No incluidas en este correo)");
					System.out.println("cuerpo = " + cuerpo);
					jsonObject.put("estatus", "OK");
					if ((denuncia.get("CORREO") != null) && (denuncia.get("CORREO").toString().trim().length() > 0)) {
						try {
							Mailer mailer = Mailer.newMail().asunto(asunto).destino((String) denuncia.get("CORREO")).mensaje(cuerpo);
							// Generar codigo qr
							byte[] archivo = QR.generarQR(String.valueOf(idDenunciaGenerado));
							mailer.archivo(archivo, "image/jpeg", "codigoQR.jpg");
							mailer.enviar(false);
						} catch (AddressException ex) {
							jsonObject.put("estatus", "ERROR");
							Logger.getLogger(CeroCorrupcionMXServlet.class.getName()).log(Level.SEVERE, null, ex);
						} catch (MessagingException ex) {
							jsonObject.put("estatus", "ERROR");
							Logger.getLogger(CeroCorrupcionMXServlet.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}

				bd.cerrar();
				response.setContentType("application/json");
				response.getWriter().print(jsonObject.toString());
			} else if (pathInfo.equals("GuardarSugerencia")) {
				BD bd = new BD();
				bd.abrir();
				int idDenunciaGenerado = bd.insertarSugerencia((String) params.get("descripcion"), (String) params.get("userId"));
				bd.cerrar();
				boolean b = idDenunciaGenerado != -1;
				System.out.println("sugerenciaGenerada = " + idDenunciaGenerado);
				response.setContentType("application/json");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("estatus", b ? "OK" : "ERROR");
				jsonObject.put("idsugerencia", idDenunciaGenerado);
				response.setContentType("application/json");
				response.getWriter().print(jsonObject.toString());
			} else if (pathInfo.equals("GuardarConfiguracion")) {
				BD bd = new BD();
				bd.abrir();
				boolean b = bd.insertarOActualizarConfiguracion((String) params.get("userId"), intOCero(params.get("geo")),
						intOCero(params.get("push")));
				bd.cerrar();
				System.out.println("sugerenciaGenerada = " + b);
				response.setContentType("application/json");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("estatus", b ? "OK" : "ERROR");
				response.setContentType("application/json");
				response.getWriter().print(jsonObject.toString());
			} else if (pathInfo.equals("Denuncia")) {
				BD bd = new BD();
				bd.abrir();
				Map<String, Object> denuncia = bd.getDenuncia(intOCero((String) params.get("id")));
				System.out.println("denuncia = " + denuncia);
				response.setContentType("text/html");
				response.getWriter().print(denuncia.toString());
				bd.cerrar();
			} else if (pathInfo.equals("Encriptar")) {
				ServletContext context = getServletContext();
				String fullPath = context.getRealPath("/WEB-INF/encrypter_ccmx.key");

				Encrypter enc = new Encrypter();
				try {
					enc.setUp(fullPath);
					String encriptado = enc.encrypt(new Identificador(intOCero(params.get("id"))));
					System.out.println(encriptado);
					response.getOutputStream().write(QR.generarQR(encriptado));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (pathInfo.equals("PruebaPush")) {
				Sender sender = new Sender("AIzaSyCHs6xT-8Yik5DUxT79cWkLilAnsuDf_Aw");

				Message message = new Message.Builder()
						// .addData("tipo_notificacion","cambio_estatus")
						// .addData("titulo","Su denuncia ha cambiado de estatus")
						.addData("iddenuncia", "25")
						.addData("iddenuncialocal", "55831")
						.addData("tipo", "DENUNCIA")
						.addData("nombrepersona", "Jose Alfredo Sanchez Canelo")
						.addData("estatus", "Queja o Denuncia Resuelta")
						.addData("observaciones", "Los  hechos de la denuncia se han comprobado. Se tomarán medidas administrativas.")
						.addData("fechadenuncia", "1 de Noviembre, 4:24pm")
						.addData("fechaestatus", "3 de Noviembre, 4:42pm")
						.addData("titulo", "Meta: Cero Corrupción")
						.addData("descripcioncorta", "Nuestra meta ha sido alcanzada")
						.addData(
								"descripcionlarga",
								"Agradecemos a todos a los que han denunciado los actos de corrupción y/o responsabilidades administrativas. Llegamos a la meta de tener un 0% de corrupción. ¡Muchas Gracias!")
						.build();

				try {
					Result result = sender
							.send(message,
									"APA91bEM8Bm34UUd7MolRGMl2Kc-0N57kNGxxuFoIHFFcGQKCm7dcmcuVdUDGyCqPwPNEPXduQWKnEDnd7roF5xaZ0e4dlVrEPMQbR32eWQQDSPIg5rtYHgTxPWwdYC__ghzmstGVe6vkPUJsL-p15qgPekPJDzgunbbUDl5Iqx-uG8XhVsQXDZEelGJejj5Nb-G9nRirR4q",
									1);
					response.getWriter().print(result.toString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (pathInfo.equals("PruebaCorreo")) {
				Mailer mailer = Mailer.newMail().asunto("Prueba").destino("juarezjaramillo@gmail.com").mensaje("Cuerpo del mensaje");
				// Generar codigo qr
				byte[] archivo = QR.generarQR(String.valueOf(99));
				response.setContentType("image/jpeg");
				response.getOutputStream().write(archivo);

				mailer.archivo(archivo, "image/png", "codigoQR.png");
				try {
					mailer.enviar(false);

				} catch (AddressException e) {
					throw new IOException(e);
				} catch (MessagingException e) {
					throw new IOException(e);
				}
			} else if (pathInfo.equals("GuardarEvidencia")) {
				Random r = new Random(System.currentTimeMillis());
				int idArchivo = -1;
				byte[] bytes = (byte[]) params.get(params.get("archivo"));
				String nombreArchivo = "evi_den_" + intOCero(params.get("iddenuncia")) + "_" + r.nextInt(100000) + "_"
						+ params.get("archivo_NOMBREORIGINAL");
				GcsFilename filename = new GcsFilename(BUCKET_NAME, nombreArchivo);
				writeToFile(filename, bytes);
				System.out.println("bytes archivo:" + bytes);

				BD bd = new BD();
				bd.abrir();
				idArchivo = bd.insertarArchivo(intOCero(params.get("iddenuncia")), nombreArchivo, (String) params.get("archivo_NOMBREORIGINAL"),
						(String) params.get("archivo_EXTENSION"), (String) params.get("archivo_CONTENTTYPE"), (String) params.get("ubicacion"),
						doubleOCero((String) params.get("latitud")), doubleOCero((String) params.get("longitud")), (String) params.get("userId"));

				bd.cerrar();

				// this.mapa.put((String) params.get("archivo"), null);
				response.setContentType("application/json");

				JSONObject jsonObject = new JSONObject();
				jsonObject.put("estatus", "OK");
				jsonObject.put("idevidencia", String.valueOf(idArchivo));
				response.getWriter().print(jsonObject.toString());
				System.out.println("jsonObject = " + jsonObject);
			} else if (pathInfo.equals("DescargarEvidencia")) {
				

				BD bd = new BD();
				bd.abrir();
				Map<String, Object> archivo = bd.getArchivo(intOCero(params.get("i")));
				bd.cerrar();
				if (archivo != null) {
					GcsFilename filename = new GcsFilename(BUCKET_NAME, (String) archivo.get("ARCHIVO"));
					byte[] bytes = readFromFile(filename);
					String mimeType = (String) archivo.get("MIMETYPE");
					String nombreArchivo = (String) archivo.get("NOMBRE");
					
					response.setHeader("Content-Disposition", "attachment;filename="+nombreArchivo);
					response.getOutputStream().write(bytes);
				}
			}
		}
	}

	/**
	 * Writes the byte array to the specified file. Note that the close at the end is not in a finally.This is intentional. Because the file only exists for
	 * reading if close is called, if there is an exception thrown while writing the file won't ever exist. (This way there is no need to worry about cleaning
	 * up partly written files)
	 */
	private void writeToFile(GcsFilename fileName, byte[] content) throws IOException {
		@SuppressWarnings("resource")
		GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
		outputChannel.write(ByteBuffer.wrap(content));
		outputChannel.close();
	}

	/**
	 * Reads the contents of an entire file and returns it as a byte array. This works by first requesting the length, and then fetching the whole file in a
	 * single call. (Because it calls openReadChannel instead of openPrefetchingReadChannel there is no buffering, and thus there is no need to wrap the read
	 * call in a loop)
	 *
	 * This is really only a good idea for small files. Large files should be streamed out using the prefetchingReadChannel and processed incrementally.
	 */
	private byte[] readFromFile(GcsFilename fileName) throws IOException {
		int fileSize = (int) gcsService.getMetadata(fileName).getLength();
		ByteBuffer result = ByteBuffer.allocate(fileSize);
		 GcsInputChannel readChannel = gcsService.openReadChannel(fileName, 0); 
			readChannel.read(result);
		readChannel.close();
		return result.array();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (JSONException ex) {
			throw new ServletException(ex);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (JSONException ex) {
			throw new ServletException(ex);
		}
	}
}
