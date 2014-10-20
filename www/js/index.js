var cordovaApp = {
    initialize: function () {
        this.bindEvents();
    },
    // Bind Event Listeners
    bindEvents: function () {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    onDeviceReady: function () {
        //Obtener identificador para notificaciones push
        var notificacionesPush = window.plugins.pushNotification;
        notificacionesPush.register(
                function (resultado) {
                    console.log(resultado);
                },
                function (error) {
                    console.log(error);
                },
                {
                    "senderID": "38852989393",
                    "ecb": "cordovaApp.onNotification"
                });
        //Si se presiona el botón Atrás, hacemos que la app cierre la última página, 
        //y sino hay más páginas cerrar la app
        document.addEventListener("backbutton", function () {
            if (App.back() === false) {
                navigator.app.exitApp();
            }
        }, false);
    },
    receivedEvent: function (id) {
        console.log('Evento Recibido: ' + id);
    },
    getConfig: function (config) {
        var conf = window.localStorage.getObject("config");
        if (!conf) {
            conf = $.extend({}, window._plantillaConfig);
            window.localStorage.setObject("config", conf);
        }
        if (config) {
            return conf[config];
        }
        return conf;
    },
    setConfig: function (config, val) {
        var conf = this.getConfig();
        conf[config] = val;
        window.localStorage.setObject("config", conf);
    },
    onNotification: function (e) {
        console.log("onNotification");
        console.log(e);
        switch (e.event) {
            case 'registered':
                if (e.regid.length > 0) {
                    //console.log("regID = " + e.regid);
                    window.localStorage.setItem("pushid", e.regid);
                    //Enviar el push id via ajax
                    this.enviarPushId(e.regid);
                }
                break;

            case 'message':
                NotificacionesManager.procesarNotificacion(e.payload);
                if (e.foreground) {
                    console.log('Notificación Inline');
                    navigator.notification.beep(1);//Reproducir Sonido
                    if (e.payload.tipo_notificacion == "cambio_estatus") {
                        App.mostrarDialogoCambioEstatus(e.payload);
                    } else {
                        App.mostrarNotificacionGenerica(e.payload);
                    }
                }
                else {
                    if (e.coldstart) {
                        console.log('Notificación ColdStart');
                    } else {
                        console.log('Notificación Recibida en Background');
                    }
                    App.load('notificacion', e.payload);
                }
                break;
            case 'error':
                console.log('Error:' + e.msg);
                break;
            default:
                console.log('Desconocido');
                break;
        }
    },
    getPushId: function () {
        return window.localStorage.getItem("pushid");
    },
    enviarPushId: function (pushid) {
        $.ajax({
            type: "POST",
            url: window.url_enviar_configuracion,
            data: $.extend({}, cordovaApp.getConfig(), {"userid": pushid}),
            crossDomain: true,
            dataType: 'json',
            success: function (msg) {
                console.log(msg)
            },
            error: function (xhr, status, error) {
                console.log(error);
            }
        });
    }
};

cordovaApp.initialize();

$(function () {
    $("body").removeClass("app-android app-android-4");//En el prototipo no se hacen distinciones de dispositivo
});

App.mostrarNotificacion= function (notif) {
    App.dialog({
        title: notif.titulo || 'Notificacion Recibida',
        text: 'Se ha recibido una notificacion',
        okButton: 'Ver',
        cancelButton: 'Cancelar'
    }, function (ver) {
        if (ver) {
            App.load('notificacion', notif);
        }
    });
};

//Cargar pantalla principal
App.load('menu');

/*App.load('notificacion', {
 iddenuncialocal: 66960,
 tipo_notificacion: 'cambio_estatus',
 tipo: "DENUNCIA",
 nombrepersona: "Raul Juarez",
 fechadenuncia: "23 de Abril de 2014 a las 5:45pm",
 fechaestatus: "25 de Abril de 2014 a las 6:45pm",
 observaciones: "Estas son las observaciones",
 estatus: "Nuevo estatus de la denuncia"
 });*/