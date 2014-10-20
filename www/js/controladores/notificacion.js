//Controlador para la pantalla de 'Notificacion'
App.controller('notificacion', function (page, notificacion) {
    var $paginaNotificacion = $(page);
    //Si la notificación es de 'Cambio de Estatus'
    if (notificacion.tipo_notificacion == "cambio_estatus") {
        var $cambioEstatus = $paginaNotificacion.find(".cambio-estatus");
        //Ajustar la interfaz para reflejar la información de la notificación recibida
        $cambioEstatus.find(".denuncia .descripcion").html((notificacion.tipo == "DENUNCIA" ? "Denuncia a " : "Queja de ") + notificacion.nombrepersona);
        $cambioEstatus.find(".fecha .descripcion").html(notificacion.fechadenuncia);
        $cambioEstatus.find(".estatus .descripcion").html(notificacion.estatus);
        $cambioEstatus.find(".fecha-estatus .descripcion").html(notificacion.fechaestatus);
        $cambioEstatus.find(".observaciones .descripcion").html(notificacion.observaciones);

    }
});

