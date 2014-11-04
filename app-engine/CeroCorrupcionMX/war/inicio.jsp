<%@page import="com.rauljuarezjaramillo.cerocorrupcion.util.UtilHTML"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
if (session.getAttribute("IDUSUARIO") == null) {
    response.sendRedirect("/index.jsp?mje=No Existe Sesion Activa");
}
%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<jsp:include page="/part/includes.jsp"></jsp:include>
<script src="js/inicio.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>#CeroCorrupciónMX</title>
</head>
<body>
	<div class="container">
	  <h1 class='page-header'>
	  #CeroCorrupcionMX
	  <p class='pull-right'>
	  	<button type="button" id='btnCerrarSesion' class="btn btn-danger btn-xs">Cerrar Sesi&oacute;n</button>
	  </p>
	  </h1>
	  <div class='col-md-3'>
	  	  <jsp:include page="/part/nav.jsp?t=dr"></jsp:include>
	  </div>
	  <div class='col-md-9'>
	  	  
			    <ul class="nav nav-tabs" role="tablist">
				  <li role="presentation" class="active"><a href="#recibidas" role="tab" data-toggle="tab">Recibidas</a></li>
				  <li role="presentation"><a href="#enproceso" role="tab" data-toggle="tab">En Proceso</a></li>
				  <li role="presentation"><a href="#resueltas" role="tab" data-toggle="tab">Resueltas (últimos 7 d&iacute;as)</a></li>
				</ul>
				<div class="tab-content">
				  <div role="tabpanel" class="tab-pane active" id="recibidas">
				  	<%=UtilHTML.getListadoDenuncias("2")%>
				  </div>
				  <div role="tabpanel" class="tab-pane" id="enproceso">
					<%=UtilHTML.getListadoDenuncias("3")%>
				  </div>
				  <div role="tabpanel" class="tab-pane" id="resueltas">
				  	<%=UtilHTML.getListadoDenuncias("4")%>
				  </div>
				</div>
	  </div>
	  <div class="modal fade" id="modalDenuncia" tabindex="-1" role="dialog" aria-hidden="true">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-body">
				        
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
		      </div>
		    </div>
		  </div>
		</div>
	  <div class="modal fade" id="modalSeguimiento" tabindex="-1" role="dialog" aria-hidden="true">
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-body">
				 
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Cerrar</button>
		        <button type="button" class="btn btn-primary" id='btnGuardarSeguimiento'>Guardar</button>
		      </div>
		    </div>
		  </div>
		</div>
    </div>
</body>
</html>