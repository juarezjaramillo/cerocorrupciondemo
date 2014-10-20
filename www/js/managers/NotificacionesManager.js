window.NotificacionesManager = {
    init: function () {
        //Verificar que exista un arreglo de denuncias, sino crearlo
        //window.localStorage.clear();
        var denuncias = window.localStorage.getObject("denuncias");
        if (denuncias == null) {
            denuncias = [];
            window.localStorage.setObject("denuncias", denuncias);
        }
    },
    procesarNotificacion: function (notif) {
        if (notif.tipo_notificacion == "cambio_estatus") {
            var denuncia = DenunciasManager.obtenerDenuncia(notif.iddenuncialocal);
            console.log(denuncia);
            denuncia.estatus = notif.estatus;
            DenunciasManager.guardarDenuncia(denuncia);
            
        }
    }
};
window.NotificacionesManager.init();