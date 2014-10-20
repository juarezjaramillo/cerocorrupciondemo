//Controlador para la pantalla 'Sugerencias'
App.controller('sugerencias', function (page) {
    var $paginaSugerencias = $(page);
    //El diálogo a mostrar mientras se envía la sugerencia al backend
    var $modal = $paginaSugerencias.find(".modal").modal({show: false, backdrop: false});
    var $titulo = $paginaSugerencias.find(".modal-title");
    var $contenido = $paginaSugerencias.find(".modal-body");
    var $footer = $paginaSugerencias.find(".modal-footer");
    var $progreso = $paginaSugerencias.find(".progress-bar");
    
    setTimeout(function () {//Forzar mostrar la etiqueta del campo
        $paginaSugerencias.find("textarea").trigger("propertychange");
    }, 100);
    //Al seleccionar la opción de enviar
    $paginaSugerencias.find(".enviar").click(function (evt) {
        $modal.modal('show');//Mostrar el diálogo de envío
        //Informacion a enviar
        var sugerencia = {"descripcion": $paginaSugerencias.find("textarea").val()};
        $.ajax({
            type: "POST",
            url: window.url_enviar_sugerencia,
            data: $.extend({}, sugerencia, {"userid": cordovaApp.getPushId()}),
            crossDomain: true,
            dataType: 'json',
            success: function (msg) {
                if (msg.estatus == "OK") {//Si todo OK
                    $progreso.css("width", "100%");//Ajustar progreso
                    //Ajustar interfaz de usuario para indicar que todo OK
                    setTimeout(function () {
                        $contenido.find(".procesando,.error").addClass("hide");
                        $contenido.find(".ok").removeClass("hide");
                        $footer.find(".reintentar").addClass("hide");
                        $footer.find(".aceptar").removeClass("hide");
                    }, 300);
                } else {
                    //Ajustar interfaz de usuario para indicar que hubo un error y mostrar botón de reintentar
                    $titulo.html("Oops!");
                    $contenido.find(".procesando,.ok").addClass("hide");
                    var $error = $contenido.find(".error").removeClass("hide");
                    if (msg.error != null) {
                        $error.html(msg.error);
                    }
                    $footer.removeClass("hide").find(".aceptar").addClass("hide").end().find(".reintentar").removeClass("hide");
                }
            },
            error: function (xhr, status, error) {
                //Ocurrió un error desconocido, mostrar mensajes al usuario y botón de reintentar
                $titulo.html("Oops!");
                $contenido.find(".procesando,.ok").addClass("hide");
                var $error = $contenido.find(".error").removeClass("hide");
                $footer.removeClass("hide").find(".aceptar").addClass("hide").end().find(".reintentar").removeClass("hide");
            }
        });
    });
    //Al seleccionar la opción de aceptar en el diálogo de envío
    $paginaSugerencias.find(".modal-footer .aceptar").click(function (evt) {
        App.back();//Cerrar la pantalla
    });
    //Reintentar el envío de la sugerencia
    $paginaSugerencias.find(".modal-footer .reintentar").click(function (evt) {
        //Ajustar interfaz para indicar el envío
        $titulo.html("Enviando Sugerencia");
        $contenido.find(".ok,.error,.evidencias").addClass("hide");
        $contenido.find(".procesando").removeClass("hide");
        $progreso.css("width", "5%");
        //Simular que se hizo tap en el botón de envíar para hacer el reintento
        $paginaSugerencias.find(".app-topbar .enviar").click();
    });
});