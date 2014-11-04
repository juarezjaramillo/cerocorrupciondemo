window.NotificacionesManager = {
    init: function () {
    },
    procesarNotificacion: function (notif) {
        if (notif.tipo_notificacion == "cambio_estatus") {
            var denuncia = DenunciasManager.obtenerDenuncia(notif.iddenuncialocal);
            if (denuncia != null) {
                denuncia.estatus = notif.estatus;
                DenunciasManager.guardarDenuncia(denuncia);
            }
        }
    }
};
window.NotificacionesManager.init();