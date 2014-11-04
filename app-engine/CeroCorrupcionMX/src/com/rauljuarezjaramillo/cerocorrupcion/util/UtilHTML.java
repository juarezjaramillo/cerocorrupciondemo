package com.rauljuarezjaramillo.cerocorrupcion.util;

import java.util.List;
import java.util.Map;

import com.rauljuarezjaramillo.cerocorrupcion.persistencia.BD;

public class UtilHTML {

	public static String getListadoDenuncias(String idEstatus){
    	String respuesta = "";
    	BD bd = new BD();
		bd.abrir();
		
    	List<Map<String, Object>> denuncias = bd.obtenerDenuncias(Integer.parseInt(idEstatus));
    	if(denuncias!=null && !denuncias.isEmpty()){
    		respuesta = "<div class='table-responsive'>";
    		respuesta = respuesta.concat("<table class='table table-hover'>");

    		respuesta = respuesta.concat("<thead>");
    		respuesta = respuesta.concat("<tr>");
    		respuesta = respuesta.concat("<th>#</th>");                   		
    		respuesta = respuesta.concat("<th>Tipo</th>");                   		
    		//respuesta = respuesta.concat("<th>Estatus</th>");                   		
    		respuesta = respuesta.concat("<th>Fecha Est.</th>");                   		
    		respuesta = respuesta.concat("<th>Folio</th>");                   		
    		respuesta = respuesta.concat("<th>Servidor Público</th>");                   		
    		respuesta = respuesta.concat("<th>Anónima</th>");                   		
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
        		//respuesta = respuesta.concat("<td>"+den.get("ESTATUS")+"</td>");                   		
        		respuesta = respuesta.concat("<td>"+den.get("FECHAESTATUS")+"</td>");                   		
        		respuesta = respuesta.concat("<td>"+den.get("FOLIO")+"</td>");                   		
        		respuesta = respuesta.concat("<td>"+den.get("NOMBREPERSONA")+"</td>");                   		
        		respuesta = respuesta.concat("<td>"+den.get("ANONIMA")+"</td>");         
        		if(idEstatus.equals("2") || idEstatus.equals("3")){
        			respuesta = respuesta.concat("<td><span class='glyphicon glyphicon-new-window verDenuncia' iddenuncia='"+den.get("IDDENUNCIA")+"' title='Ver Denuncia' alt='Ver Denuncia'></span>");
        			respuesta = respuesta.concat("&nbsp;&nbsp;&nbsp;");
        			respuesta = respuesta.concat("<span class='glyphicon glyphicon-edit seguimientoDenuncia' iddenuncia='"+den.get("IDDENUNCIA")+"' alt='Dar Seguimiento' title='Dar Seguimiento'></span>");
        		}else{
        			respuesta = respuesta.concat("<td>&nbsp;&nbsp;&nbsp;</td>");
        		}
        		respuesta = respuesta.concat("</tr>");
			}
    		respuesta = respuesta.concat("</tbody>");
   		
    		respuesta = respuesta.concat("</table>");
    		respuesta = respuesta.concat("</div>");
    	}else{
    		respuesta = "<br/><p class='alert alert-success'><span class='glyphicon glyphicon-ok'></span>&nbsp;No hay denuncias</p>";
    	}
		
		return respuesta;
	}
}
