$(function(){
			
	$("#btnCerrarSesion").click(function(){
		window.location='/SrvUtil/logout';
	});
	
	$(".verDenuncia").click(function(event){
		var target = $(event.target);
		var iddenuncia = target.attr("iddenuncia");
		
		$("#modalDenuncia .modal-body").load("part_denuncia.jsp?IDDENUNCIA="+iddenuncia);
		$("#modalDenuncia").modal("show");
	});
	
	$(".seguimientoDenuncia").click(function(event){
		var target = $(event.target);
		var iddenuncia = target.attr("iddenuncia");
		
		$("#modalSeguimiento .modal-body").load("part_seg_denuncia.jsp?IDDENUNCIA="+iddenuncia);
		$("#modalSeguimiento").modal("show");
	});
	
	$("#btnGuardarSeguimiento").click(function(){
		$("#formSeguimiento").attr('method','POST').attr('action','/SrvUtil/seguimiento/guardar').submit();
	});
	
	$(".no-en-demo").click(function(){
		swal({   title: "Version Demo",   text: "Esta funcionalidad no esta implementada en la version demo",   timer: 3000 });
	});
	
	if($("#ok").val()!=''){
		setTimeout(function(){
			swal({   title: "Seguimiento",   text:$("#ok").val() ,   timer: 2000, type: "success" });
		},100);
		//swal({   title: "Seguimiento",   text:$("#ok").val() ,   timer: 2000, type: "success" });
		//alert('ok');
	}
	
	if($("#error").val()!=''){
		setTimeout(function(){
			swal({   title: "Seguimiento",   text:$("#error").val() ,   timer: 2000, type: "error" });
		},100);
		//swal({   title: "Seguimiento",   text:$("#error").val() ,   timer: 2000, type: "error" });
		alert('error');
	}
	
});