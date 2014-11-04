<%@ page contentType="text/html; charset=ISO-8859-1" language="java" import="java.util.*,com.rauljuarezjaramillo.cerocorrupcion.persistencia.*" errorPage=""%>
<%
	String idDenuncia = request.getParameter("IDDENUNCIA");

	BD bd = new BD();
	bd.abrir();
	Map<String, Object> den =  bd.getDenuncia(Integer.parseInt(idDenuncia));
	bd.cerrar();
%>
<html> 
<head><jsp:include page="/part/includes.jsp"></jsp:include></head>
<body>
<h3>Seguimiento a folio <%=den.get("FOLIO")%></h3><hr/><br/>
<form class="form-horizontal" role="form" id='formSeguimiento'>
  <div class="form-group">
    <label for="estatus" class="col-sm-2 control-label">Estatus</label>
    <div class="col-sm-8">
      <select id='estatus' name='estatus' class='form-control'>
      	<option value='2'>Queja o Denuncia Recibida</option>
      	<option value='3'>Queja o Denuncia en proceso o investigaci&oacute;n</option>
      	<option value='4'>Queja o Denuncia Resuelta</option>
      </select>
    </div>
  </div>
  <div class="form-group">
    <label for="observaciones" class="col-sm-2 control-label">Observaciones</label>
    <div class="col-sm-10">
      <textarea id='observaciones' name='observaciones' class='form-control' rows='5'></textarea>
    </div>
  </div>
  <input type="hidden" name='IDDENUNCIA' id='IDDENUNCIA' value='<%=den.get("IDDENUNCIA")%>'/>
</form>
</body>
</html>