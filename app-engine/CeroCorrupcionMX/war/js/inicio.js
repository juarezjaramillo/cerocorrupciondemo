$(function(){
	$("#btnCerrarSesion").click(function(){
		window.location='/SrvUtil/logout';
	});
	
	$(".verDenuncia").click(function(event){
		var target = $(event.target);
		var iddenuncia = target.attr("iddenuncia");
		
		$("#modalDenuncia .modal-body").html("Espere ...").load("part_denuncia.jsp?IDDENUNCIA="+iddenuncia);
		$("#modalDenuncia").modal("show");
	});
	
	$(".seguimientoDenuncia").click(function(event){
		var target = $(event.target);
		var iddenuncia = target.attr("iddenuncia");
		
		$("#modalSeguimiento .modal-body").html("Espere ...").load("part_seg_denuncia.jsp?IDDENUNCIA="+iddenuncia);
		$("#modalSeguimiento").modal("show");
	});
	
	$("#btnGuardarSeguimiento").click(function(){
		//alert($("#formSeguimiento").serialize());
	});
});