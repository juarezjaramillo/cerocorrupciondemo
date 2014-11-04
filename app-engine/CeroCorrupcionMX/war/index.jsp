<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css">
<link rel="stylesheet" href="css/login.css">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="js/login.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 
<title>#CeroCorrupciónMX</title> 
</head>
<body>
<div class="container">
	  <%if(request.getParameter("mje")!=null){%>
		<div class="alert alert-warning" role="alert">
		<%=request.getParameter("mje")%>
		</div>      
      <%}%>
      <form class="form-signin" role="form" method="post" action="/SrvUtil/login">
        <h2 class="form-signin-heading">#CeroCorrupciónMX</h2>
        <input name='USUARIO' type="text" class="form-control" placeholder="Usuario" required autofocus="">
        <input name='CONTRASENA' type="password" class="form-control" placeholder="Contraseña" required>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Ingresar</button>
      </form>
    </div>
</body>
</html>