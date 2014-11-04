package com.rauljuarezjaramillo.cerocorrupcion;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rauljuarezjaramillo.cerocorrupcion.persistencia.BD;
import com.rauljuarezjaramillo.cerocorrupcion.util.ServletGeneral;
import com.rauljuarezjaramillo.cerocorrupcion.util.Util;
import com.rauljuarezjaramillo.cerocorrupcion.util.notificaciones.Mailer;
import com.rauljuarezjaramillo.cerocorrupcion.util.notificaciones.Push;

public class SrvUtil extends ServletGeneral {

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
	
		String idUsuario = (String) request.getSession().getAttribute("IDUSUARIO");
		Map<String, Object> params = crearMapaParametros(request);
		String path = request.getPathInfo();
		
		System.out.print("Path: "+path);
		
		if (request.getSession().getAttribute("IDUSUARIO") == null && !path.equals("/login")) {
            request.getSession().invalidate();
            response.sendRedirect("/index.jsp?mje=No Existe Sesion Activa");
        } else {
        	try {
                if (path != null && !path.trim().equals("")) {
                	if(path.equals("/login")){
        				String usuario = (String)params.get("USUARIO");
        				String pass = (String)params.get("CONTRASENA");
        				
        				BD bd = new BD();
        				bd.abrir();
        				int id = bd.validarLogin(usuario, pass);
        				bd.cerrar();
        				
        				System.out.println("ID="+id);
        				
        				if(id==0){
        					response.sendRedirect("/index.jsp?mje=Datos incorrectos");
        				}else{
        					request.getSession().setAttribute("IDUSUARIO", ""+id);
        					response.sendRedirect("/inicio.jsp");
        				}
        			}else if("/logout".equals(path)){
                        request.getSession().invalidate();
                        response.sendRedirect("/index.jsp");
        			}else if("/seguimiento/guardar".equals(path)){
        				String idDenuncia = (String)params.get("IDDENUNCIA");
        				String idEstatus = (String)params.get("IDESTATUS");
        				String observaciones = (String)params.get("OBSERVACIONES");
        				
        				BD bd = new BD();
        				bd.abrir();
        				int idSeguimiento = bd.guardarSeguimiento(idDenuncia, idEstatus, observaciones, (String)request.getSession().getAttribute("IDUSUARIO"));
        				
        				if(idSeguimiento != -1){
        					if(bd.actualizarEstatusDenuncia(Util.intOCero(idDenuncia), Util.intOCero(idEstatus), new Date())){
        						notificarCambioEstatus(idDenuncia, observaciones);
        					}
        					response.sendRedirect("/inicio.jsp?ok=Seguimiento guardado con exito!");
        				}else{
        					response.sendRedirect("/inicio.jsp?error=No fue posible guardar el seguimiento.");
        				}
        				bd.cerrar();
                    }else if("/denuncia/getListado".equals(path)){
                    	String idEstatus=(String)params.get("IDESTATUS");
                    	String respuesta = "";
                    	BD bd = new BD();
        				bd.abrir();
        				
                    	List<Map<String, Object>> denuncias = bd.obtenerDenuncias(Integer.parseInt(idEstatus));
                    	if(denuncias!=null && !denuncias.isEmpty()){
                    		respuesta = "<div class='table-responsive table-hover'>";
                    		respuesta = "<table class='table'>";

                    		respuesta = respuesta.concat("<thead>");
                    		respuesta = respuesta.concat("<tr>");
                    		respuesta = respuesta.concat("<th>#</th>");                   		
                    		respuesta = respuesta.concat("<th>Tipo</th>");                   		
                    		respuesta = respuesta.concat("<th>Estatus</th>");                   		
                    		respuesta = respuesta.concat("<th>Fecha Est.</th>");                   		
                    		respuesta = respuesta.concat("<th>Folio</th>");                   		
                    		respuesta = respuesta.concat("<th>Servidor P&oacute;blico</th>");                   		
                    		respuesta = respuesta.concat("<th>An&oacute;nima</th>");                   		
                    		respuesta = respuesta.concat("<th>&nbsp;&nbsp;&nbsp;</th>");                   		
                    		respuesta = respuesta.concat("</tr>");
                    		respuesta = respuesta.concat("</thead>");
                    		
                    		respuesta = respuesta.concat("</thead>");
                    		respuesta = respuesta.concat("<tbody>");
                    		for (int i = 0; i < denuncias.size(); i++) {
                    			Map<String, Object> den = denuncias.get(i);
                    			
                    			respuesta = respuesta.concat("<tr>");
                        		respuesta = respuesta.concat("<td>"+(i+1)+"</td>");                   		
                        		respuesta = respuesta.concat("<td>"+den.get("TIPO")+"</td>");                   		
                        		respuesta = respuesta.concat("<td>"+den.get("ESTATUS")+"</td>");                   		
                        		respuesta = respuesta.concat("<td>"+den.get("FECHAESTATUS")+"</td>");                   		
                        		respuesta = respuesta.concat("<td>"+den.get("FOLIO")+"</td>");                   		
                        		respuesta = respuesta.concat("<td>"+den.get("NOMBREPERSONA")+"</td>");                   		
                        		respuesta = respuesta.concat("<td>"+den.get("ANONIMA")+"</td>");                   		
                        		respuesta = respuesta.concat("<td>---</td>");                   		
                        		respuesta = respuesta.concat("</tr>");
							}
                    		respuesta = respuesta.concat("</tbody>");
                   		
                    		respuesta = respuesta.concat("</table>");
                    		respuesta = respuesta.concat("</div>");
                    	}else{
                    		respuesta = "<p class='alert alert-success'><span class='glyphicon glyphicon-ok'></span>No hay denuncias</p>";
                    	}
                    	bd.cerrar();
                    	response.getWriter().print(respuesta);
                    }else if("/descargarArchivo".equals(path)){
                    	forward("/cerocorrupcionmx/DescargarEvidencia", request, response);
                    }
                }
        	}catch(Exception e){
        		Logger.getLogger(SrvUtil.class.getName()).log(Level.SEVERE, null, e);
        	}
        }

	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			processRequest(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			processRequest(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void notificarCambioEstatus(String idDenuncia, String observaciones){
		BD bd=new BD();
		bd.abrir();
		Map<String,Object> denuncia=bd.getDenuncia(Util.intOCero(idDenuncia));
		boolean notificarPush = bd.notificacionesActivas((String)denuncia.get("USUARIOCREACION"));
		
		if(notificarPush){
			Map<String,String> datos=new HashMap<String,String>();
			datos.put("tipo_notificacion","cambio_estatus");
			datos.put("titulo","Su denuncia ha cambiado de estatus");
			datos.put("message","Su denuncia ha cambiado de estatus");
			datos.put("title","Notificacion de Cambio de Estatus");
			datos.put("iddenuncia", String.valueOf(denuncia.get("IDDENUNCIA")));
			datos.put("iddenuncialocal", String.valueOf(denuncia.get("IDDENUNCIALOCAL")));
			datos.put("tipo",Util.intOCero(denuncia.get("TIPODENUNCIA"))== 1 ? "DENUNCIA":"QUEJA");
			datos.put("nombrepersona", String.valueOf(denuncia.get("PERSONA")));
			datos.put("estatus", String.valueOf(denuncia.get("ESTATUS")));
			datos.put("observaciones", String.valueOf(observaciones));

			DateFormat dateFormat= DateFormat.getDateTimeInstance(
			            DateFormat.LONG, DateFormat.SHORT,new Locale("es","MX"));

			Date fechaEstatus=new Date();
			fechaEstatus=fechaEstatus==null ? new Date():fechaEstatus;
			datos.put("fechaestatus", dateFormat.format(fechaEstatus));
			Date fechaDenuncia=(Date)denuncia.get("FECHACREACION");
			fechaDenuncia=fechaDenuncia==null ? new Date():fechaDenuncia;
			datos.put("fechadenuncia", dateFormat.format(fechaDenuncia));

			Push.enviarNotificacion(datos, (String)denuncia.get("USUARIOCREACION"));
		}
		

			String correo = (String) denuncia.get("correo");
			String asunto = "Notificación de cambio de estatus en "+(Util.intOCero(denuncia.get("TIPODENUNCIA"))== 1 ? "DENUNCIA":"QUEJA");
			String cuerpo = "Se ha cambiado el estatus de la denuncia a "+String.valueOf(denuncia.get("ESTATUS"));
			DateFormat dateFormat= DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT,new Locale("es","MX"));
			cuerpo = cuerpo.concat(" en la fecha "+dateFormat.format((Date)denuncia.get("FECHAESTATUS")));

			if ((denuncia.get("CORREO") != null) && (denuncia.get("CORREO").toString().trim().length() > 0)) {
				try {
					Mailer mailer = Mailer.newMail().asunto(asunto).destino((String) denuncia.get("CORREO")).mensaje(cuerpo);
					mailer.enviar(false);
				} catch (AddressException ex) {
					Logger.getLogger(SrvUtil.class.getName()).log(Level.SEVERE, null, ex);
				} catch (MessagingException ex) {
					Logger.getLogger(SrvUtil.class.getName()).log(Level.SEVERE, null, ex);
				}catch(Exception ex){
					Logger.getLogger(SrvUtil.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		
			
		bd.cerrar();
	}
}
