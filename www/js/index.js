/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var cordovaApp = {
    // Application Constructor
    initialize: function () {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function () {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function () {
        cordovaApp.receivedEvent('deviceready');
        var notificacionesPush = window.plugins.pushNotification;
        notificacionesPush.register(
                function (resultado) {
                    console.log("Notificaciones Push - Success");
                    console.log(resultado);
                },
                function (error) {
                    console.log("Notificaciones Push - Error");
                    console.log(error);
                },
                {
                    "senderID": "38852989393",
                    "ecb": "cordovaApp.onNotification"
                });
        // Register the event listener
        document.addEventListener("backbutton", function () {
            if (App.back() === false) {
                navigator.app.exitApp();
            }
        }, false);
    },
    // Update DOM on a Received Event
    receivedEvent: function (id) {

        console.log('Received Event: ' + id);
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
                    // Your GCM push server needs to know the regID before it can push to this device
                    // here is where you might want to send it the regID for later use.
                    console.log("regID = " + e.regid);
                    window.localStorage.setItem("pushid", e.regid);
                }
                break;

            case 'message':
                NotificacionesManager.procesarNotificacion(e.payload);
                // if this flag is set, this notification happened while we were in the foreground.
                // you might want to play a sound to get the user's attention, throw up a dialog, etc.
                if (e.foreground) {
                    console.log('<li>--INLINE NOTIFICATION--</li>');

                    // on Android soundname is outside the payload.
                    // On Amazon FireOS all custom attributes are contained within payload
                    //var soundfile = e.soundname || e.payload.sound;
                    // if the notification contains a soundname, play it.
                    //var my_media = new Media("/android_asset/www/" + soundfile);
                    //my_media.play();
                    navigator.notification.beep(1);
                    if (e.payload.tipo_notificacion == "cambio_estatus") {
                        App.mostrarDialogoCambioEstatus(e.payload);
                    }
                }
                else {  // otherwise we were launched because the user touched a notification in the notification tray.
                    if (e.coldstart)
                    {
                        console.log('<li>--COLDSTART NOTIFICATION--</li>');
                    }
                    else
                    {
                        console.log('<li>--BACKGROUND NOTIFICATION--</li>');
                    }
                }

                console.log('<li>MESSAGE -> MSG: ' + e.payload.message + '</li>');
                //Only works for GCM
                console.log('<li>MESSAGE -> MSGCNT: ' + e.payload.msgcnt + '</li>');
                //Only works on Amazon Fire OS
                //$status.append('<li>MESSAGE -> TIME: ' + e.payload.timeStamp + '</li>');
                //App.load('notificacion', e.payload);
                break;

            case 'error':
                console.log('<li>ERROR -> MSG:' + e.msg + '</li>');
                break;

            default:
                console.log('<li>EVENT -> Unknown, an event was received and we do not know what it is</li>');
                break;
        }
    },
    getPushId: function () {
        return window.localStorage.getItem("pushid");
    }
};

cordovaApp.initialize();

$(function () {
    $("body").removeClass("app-android app-android-4");//En el prototipo no se hacen distinciones de dispositivo
});

App.mostrarDialogoCambioEstatus = function (notif) {
    App.dialog({
        title: 'Cambio de Estatus',
        text: 'Una de sus denuncias ha cambiado de estatus',
        okButton: 'Ver',
        cancelButton: 'Cancelar'
    }, function (tryAgain) {
        if (tryAgain) {
            App.load('notificacion', notif);
        }
    });
};

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