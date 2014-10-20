//Controlador para la pantalla de 'Notificacion'
App.controller('notificacion', function (page, notificacion) {
    var $paginaNotificacion = $(page);
    //Si la notificación es de 'Cambio de Estatus'
    if (notificacion.tipo_notificacion == "cambio_estatus") {
        var $cambioEstatus = $paginaNotificacion.find(".cambio-estatus").removeClass("hide");
        //Ajustar la interfaz para reflejar la información de la notificación recibida
        $cambioEstatus.find(".denuncia .descripcion").html((notificacion.tipo == "DENUNCIA" ? "Denuncia a " : "Queja de ") + notificacion.nombrepersona);
        $cambioEstatus.find(".fecha .descripcion").html(notificacion.fechadenuncia);
        $cambioEstatus.find(".estatus .descripcion").html(notificacion.estatus);
        $cambioEstatus.find(".fecha-estatus .descripcion").html(notificacion.fechaestatus);
        $cambioEstatus.find(".observaciones .descripcion").html(notificacion.observaciones);
        $paginaNotificacion.find(".ver-denuncia").click(function () {
            var denuncia = window.DenunciasManager.obtenerDenuncia(notificacion.iddenuncialocal);
            if (denuncia == null) {
                denuncia = window.DenunciasManager.obtenerDenuncia(notificacion.iddenunciaproduccion);
            }
            if (denuncia != null) {
                App.load("nueva", denuncia);
            } else {
                swal("No se pudo obtener la informacion de la denuncia");
            }
        });
    } else {
        var $notificacionGenerica = $paginaNotificacion.find(".notificacion-generica").removeClass("hide");
        //Ajustar la interfaz para reflejar la información de la notificación recibida
        $notificacionGenerica.find(".titulo .descripcion").html(notificacion.titulo);
        $notificacionGenerica.find(".descripcion-corta .descripcion").html(notificacion.descripcioncorta);
        $notificacionGenerica.find(".descripcion-larga .descripcion").html(notificacion.descripcionlarga);
    }
});

