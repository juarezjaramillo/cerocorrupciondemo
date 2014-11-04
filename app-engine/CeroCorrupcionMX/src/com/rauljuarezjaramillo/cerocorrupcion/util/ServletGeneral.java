/**
 * 
 */
package com.rauljuarezjaramillo.cerocorrupcion.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.gmr.web.multipart.GFileItemFactory;

/**
 * Clase base para los servlets de la aplicacion
 * @author Appix Creative
 *
 */
public class ServletGeneral extends HttpServlet {

	/**
	 * Separador de Archivos
	 */
	public static final String FILE_SEPARATOR=System.getProperty("file.separator");
	
	public static final String PDF_MIMETYPE = "application/pdf";
    public static final String DOC_MIMETYPE = "application/msword";
    public static final String DOCX_MIMETYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String XLS_MIMETYPE = "application/vnd.ms-excel";
    public static final String XLSX_MIMETYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PPT_MIMETYPE = "application/vnd.ms-powerpoint";
    public static final String PPTX_MIMETYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

    public static final int RETENER_CAMPOS_OBJETO = 1;
    public static final int USAR_CAMPOS_REQUEST = 2;
    public static final String RUTA_UPLOADS = "temp_uploads";
    public static final String[] IMAGEN_MIMETYPES = {"image/bmp", "image/png", "image/x-png", "image/png", "image/gif",
        "image/jpeg", "image/jpg", "image/pjpeg", "image/jpeg"};
    public static final String[] VIDEO_MIMETYPES = {"video/mpeg", "video/quicktime", "video/quicktime", "video/mp4",
        "video/x-flv", "video/x-ms-wmv"};
    public static final String[] AUDIO_MIMETYPES = {"audio/basic", "audio/mid", "audio/mpeg", "audio/x-wav", "audio/mp3"};
    public static final String[] DOCUMENTO_MIMETYPES = {PDF_MIMETYPE, DOC_MIMETYPE, XLS_MIMETYPE, PPT_MIMETYPE, DOCX_MIMETYPE, XLSX_MIMETYPE, PPTX_MIMETYPE};
    public static final String[] TEXTO_MIMETYPES = {"text/plain", "text/html", "text/rtf", "text/xml"};

    public static boolean esImagen(String mimeType) {
        boolean result = false;
        if (mimeType != null) {
            String type = mimeType.toLowerCase();
            for (String mime : IMAGEN_MIMETYPES) {
                if (mime.toLowerCase().equals(type)) {
                    return true;
                }
            }
        }

        return result;
    }

    public static boolean esAudio(String mimeType) {
        boolean result = false;
        if (mimeType != null) {
            String type = mimeType.toLowerCase();
            for (String mime : AUDIO_MIMETYPES) {
                if (mime.toLowerCase().equals(type)) {
                    return true;
                }
            }
        }
        return result;
    }

    public static boolean esVideo(String mimeType) {
        boolean result = false;
        if (mimeType != null) {
            String type = mimeType.toLowerCase();
            for (String mime : VIDEO_MIMETYPES) {
                if (mime.toLowerCase().equals(type)) {
                    return true;
                }
            }
        }
        return result;
    }

    public static boolean esDocumento(String mimeType) {
        boolean result = false;
        if (mimeType != null) {
            String type = mimeType.toLowerCase();
            for (String mime : DOCUMENTO_MIMETYPES) {
                if (mime.toLowerCase().equals(type)) {
                    return true;
                }
            }
        }
        return result;
    }

    public static boolean esDocumentoTexto(String mimeType) {
        boolean result = false;
        if (mimeType != null) {
            String type = mimeType.toLowerCase();
            for (String mime : TEXTO_MIMETYPES) {
                if (mime.toLowerCase().equals(type)) {
                    return true;
                }
            }
        }
        return result;
    }

    public static boolean esPdf(String mimeType) {
        return PDF_MIMETYPE.equalsIgnoreCase(mimeType);
    }

    public static boolean esDoc(String mimeType) {
        return DOC_MIMETYPE.equalsIgnoreCase(mimeType);
    }

    public static boolean esDocx(String mimeType) {
        return DOCX_MIMETYPE.equalsIgnoreCase(mimeType);
    }

    public static boolean esXls(String mimeType) {
        return XLS_MIMETYPE.equalsIgnoreCase(mimeType);
    }

    public static boolean esXlsx(String mimeType) {
        return XLSX_MIMETYPE.equalsIgnoreCase(mimeType);
    }

    public static boolean esPpt(String mimeType) {
        return PPT_MIMETYPE.equalsIgnoreCase(mimeType);
    }

    public static boolean esPptx(String mimeType) {
        return PPTX_MIMETYPE.equalsIgnoreCase(mimeType);
    }
	
	/**
	 * Metodo de utileria para parsear el request (parametros y, si se incluyen, archivos)
	 * @param request el request del cual obtener los parametros
	 * @return mapa de parametros obtenido
	 */
	public Map<String, Object> crearMapaParametros(HttpServletRequest request) {
	    Map<String,Object> map = new HashMap<String,Object>();
	    Map<String,String> originales = new HashMap<String,String>();
 
	    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	    if (isMultipart) {
	      GFileItemFactory factory = new GFileItemFactory();
	      ServletFileUpload upload = new ServletFileUpload(factory);
	      List<FileItem> items = null;
	      try
	      {
	        items = upload.parseRequest(request);
	        for (FileItem item : items)
	          if (item.isFormField()) {
	            String name = item.getFieldName();
	            String value = item.getString();
	            if (!name.trim().equals("")) {
	              map.put(name, value != null ? value.trim() : null);
	            }
	            originales.put(name, value);
	          }
	          else {
	            try {
	              String itemName = item.getName();

	              if ((itemName != null) && (!itemName.trim().equals(""))) {
	                Random r = new Random(System.currentTimeMillis());
	                String nombreArchivo = getServletContext().getRealPath("/uploads") + FILE_SEPARATOR + r.nextInt() + "_" + r.nextInt();
	                String nombreOriginal = itemName;
	                int dotPos = itemName.lastIndexOf(".");
	                int indexNombreArchivo = itemName.lastIndexOf(FILE_SEPARATOR);
	                String extension = "";
	                if (dotPos != -1) {
	                  extension = itemName.substring(dotPos + 1);
	                  nombreArchivo = nombreArchivo.concat(".").concat(extension);
	                }
	                if (indexNombreArchivo != -1) {
	                  nombreOriginal = itemName.substring(indexNombreArchivo + 1);
	                }

	                InputStream fis = item.getInputStream();
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                byte[] temp = new byte[4096];
	                while (fis.read(temp) != -1) {
	                  baos.write(temp);
	                }
	                fis.close();
	                baos.close();
	                String nombreFinal = String.valueOf(r.nextInt()).concat(nombreOriginal);
	                map.put(item.getFieldName(), nombreFinal);
	                map.put(nombreFinal, baos.toByteArray());
	                map.put(item.getFieldName() + "_NOMBREORIGINAL", nombreOriginal);
	                map.put(item.getFieldName() + "_CONTENTTYPE", item.getContentType().toLowerCase());
	                map.put(item.getFieldName() + "_SIZE", item.getSize());
	                map.put(item.getFieldName() + "_EXTENSION", extension.toLowerCase());
	              }
	            } catch (Exception e) {
	              Logger.getLogger(ServletGeneral.class.getName()).log(Level.SEVERE, null, e);
	            }
	          }
	      }
	      catch (FileUploadException e) {
	        Logger.getLogger(ServletGeneral.class.getName()).log(Level.SEVERE, null, e);
	      }
	    } else {
	      Enumeration<String> params = request.getParameterNames();
	      while (params.hasMoreElements()) {
	        String param = params.nextElement();
	        map.put(param, request.getParameter(param) != null ? request.getParameter(param).trim() : null);
	        originales.put(param, request.getParameter(param));
	      }
	    }
	    request.setAttribute("PARAMETROSORIGINALES", originales);
	    return map;
	  }
	
	public static void forward(String path, HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (ServletException ex) {
            Logger.getLogger(ServletGeneral.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServletGeneral.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
