package com.rauljuarezjaramillo.cerocorrupcion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rauljuarezjaramillo.cerocorrupcion.persistencia.BD;
import com.rauljuarezjaramillo.cerocorrupcion.util.ServletGeneral;

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
                    		respuesta = respuesta.concat("<th>Servidor P�blico</th>");                   		
                    		respuesta = respuesta.concat("<th>An�nima</th>");                   		
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
}
