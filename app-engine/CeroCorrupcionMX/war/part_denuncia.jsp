<%@page import="com.rauljuarezjaramillo.cerocorrupcion.persistencia.BD"%>
<%@page import="java.text.DateFormat"%>
<%@ page contentType="text/html; charset=ISO-8859-1" language="java" import="java.util.*" errorPage=""%>
<html>
<%
	String idDenuncia = request.getParameter("IDDENUNCIA");

	BD bd = new BD();
	bd.abrir();
	Map<String, Object> den =  bd.getDenuncia(Integer.parseInt(idDenuncia));
	List<Map<String,Object>> archivos=bd.getArchivos((Integer)den.get("IDDENUNCIA"));
	bd.cerrar();
	
	Date fechaEstatus = (Date)den.get("FECHAESTATUS");
	DateFormat dateFormat= DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT,new Locale("es","MX"));
%>
		<h3><%=((Integer)den.get("TIPODENUNCIA")).intValue()==1 ? "QUEJA":"DENUNCIA" %><small>&nbsp;&nbsp;<%=den.get("FOLIO")%></small></h3>
		<hr/>
		<div class='well'>
			<dl class="dl-horizontal">
			  <dt>Estatus</dt>
			  <dd><%=den.get("ESTATUS")%> (<%=dateFormat.format(fechaEstatus)%>)</dd>
			  <dt>Funcionario</dt>
			  <dd><%=den.get("PERSONA") %></dd>
			  <dt>Hechos</dt>
			  <dd><%=den.get("HECHOS") %></dd>
			  <dt>Ubicación</dt>
			  <dd><%=den.get("UBICACION") %></dd>
			  <dt>Evidencias</dt>
			  <%if(archivos!=null && archivos.size()>0){ %>
			  <dd>
			  <%for (int i = 0; i<archivos.size();i++){
				  Map<String,Object> archivo=archivos.get(i);
			  %>
			  <p>
			  <a href='/SrvUtil/descargarArchivo?i=<%=archivo.get("IDEVIDENCIA") %>'><%=archivo.get("NOMBRE") %></a>
			  <%if(archivo.get("UBICACION")!=null && archivo.get("UBICACION").toString().trim().length()>0){ %>
			  <br />
			  <%=archivo.get("UBICACION") %>
			  <%if(archivo.get("LATITUD")!=null && ((Double)archivo.get("LATITUD")).intValue()!=0){ %>
			  - <a target='blank' href="http://maps.google.com/maps?q=<%=archivo.get("LATITUD") %>,<%=archivo.get("LONGITUD") %>">Ver en Mapa</a>
			  <%} %>
			  <%} %>
			  </p>
			  <%} %>
			  </dd>
			  <%}else{ %>
			  <dd>No se agregaron evidencias</dd>
			  <%}%>
			  <dt>&nbsp;</dt>
			  <dd>&nbsp;</dd>
			  <dt>Denunciante</dt>
			  <dd>
			  	<address>
				  <strong><%=den.get("NOMBRE") %></strong><br>
				  <%=den.get("DIRECCION") %><br>
				  <%=(den.get("TELEFONO")!= null && den.get("TELEFONO").toString().trim().length()>0 )? "<abbr title='Teléfono'>Tel:</abbr>"+den.get("TELEFONO"):"" %><br/>
				  <%=den.get("CORREO") %>
				</address>
			  </dd>
			</dl> 
		</div>
</html>