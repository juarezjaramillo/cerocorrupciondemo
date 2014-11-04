<%
	String t = request.getParameter("t");
%>
<html> 
<body>
		<div class='well'>
			  <ul class="nav nav-pills nav-stacked" role="tablist">
			  	<li role="presentation" class="<%=t.equals("dr")?"active":""%>"><a href="inicio.jsp">Denuncias recibidas</a></li>
			    <li role="presentation" class="<%=t.equals("su")?"active":""%>"><a href="#" class='no-en-demo'>Sugerencias</a></li>
			    <li role="presentation" class="<%=t.equals("do")?"active":""%>"><a href="#" class='no-en-demo'>Directorio de oficinas</a></li>
			    <li role="presentation" class="<%=t.equals("en")?"active":""%>"><a href="#" class='no-en-demo'>Envío de Notificaciones</a></li>
			    <li role="presentation" class="<%=t.equals("qh")?"active":""%>"><a href="#" class='no-en-demo'>Qué hacer?</a></li>
			    <li role="presentation" class="<%=t.equals("bd")?"active":""%>"><a href="#" class='no-en-demo'>Buscar Denuncias</a></li>
			  </ul>
		  </div>
</body>
</html>