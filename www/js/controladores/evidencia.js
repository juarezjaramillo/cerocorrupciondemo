//Controlador para la pantalla Anexar Evidencia
App.controller('evidencia', function (page) {
    var $paginaEvidencia = $(page);
    var evidencia = {};
    //Cada que se muestra la pagina, ajustar el visor de thumbnail para mostrar el correcto
    $paginaEvidencia.on("appShow", function () {
        $paginaEvidencia.find(".seleccion-opcion .vista-previa").addClass("hide");//Ocultar todas las vistas previas
        if (evidencia.tipo > 0) {
            //Mostrar la vista previa correcta
            switch (evidencia.tipo) {
                case 1:
                    $paginaEvidencia.find(".seleccion-opcion .vista-previa.foto").removeClass("hide");
                    break;
                case 2:
                    $paginaEvidencia.find(".seleccion-opcion .vista-previa.video").removeClass("hide");
                    break;
                case 3:
                    $paginaEvidencia.find(".seleccion-opcion .vista-previa.audio").removeClass("hide");
                    break;
                case 4:
                    $paginaEvidencia.find(".seleccion-opcion .vista-previa.documento").removeClass("hide");
                    break;
            }
        } else {
            $paginaEvidencia.find(".seleccion-opcion .flecha-arriba,.seleccion-opcion .mensaje").removeClass("hide");
        }
    });

    //Al seleccionar la opcion de tomar foto, lanzar el buscador en el dispositivo y al escoger una
    //ajustar el objeto evidencia y activar su vista previa
    $paginaEvidencia.find(".tomar-foto").click(function (evt) {
        window.fileChooser.pick("image", function (resp) {
            $paginaEvidencia.find(".seleccion-opcion .vista-previa,.seleccion-opcion .flecha-arriba,.seleccion-opcion .mensaje").addClass("hide");
            var $imagen = $paginaEvidencia.find(".seleccion-opcion .vista-previa.foto").removeClass("hide");
            $imagen.html("<img src='" + resp.url + "' />");
            evidencia.url = resp.url;
            evidencia.mimetype = resp.type;
            evidencia.tipo = 1;
        }, window.logError);
    });
    //Al seleccionar la opcion de tomar video, lanzar el buscador en el dispositivo y al escoger uno
    //ajustar el objeto evidencia y activar su vista previa
    $paginaEvidencia.find(".tomar-video").click(function (evt) {
        window.fileChooser.pick("video", function (resp) {
            $paginaEvidencia.find(".seleccion-opcion .vista-previa,.seleccion-opcion .flecha-arriba,.seleccion-opcion .mensaje").addClass("hide");
            var $video = $paginaEvidencia.find(".seleccion-opcion .vista-previa.video").removeClass("hide");
            //Se crea un elemento de tipo 'video' para que se pueda reproducir desde la pantalla y el usuario pueda verlo
            $video.html("<video width='240' height='240' controls><source src='" + resp.url + "' type='" + resp.type + "'>No hay soporte para incrustar videos</video>");
            evidencia.url = resp.url;
            evidencia.mimetype = resp.type;
            evidencia.tipo = 2;
        }, window.logError);
    });
    //Al seleccionar la opcion de tomar audio, lanzar el buscador en el dispositivo y al escoger uno
    //ajustar el objeto evidencia y activar su vista previa
    $paginaEvidencia.find(".tomar-audio").click(function (evt) {
        window.fileChooser.pick("audio", function (resp) {
            $paginaEvidencia.find(".seleccion-opcion .vista-previa,.seleccion-opcion .flecha-arriba,.seleccion-opcion .mensaje").addClass("hide");
            var $audio = $paginaEvidencia.find(".seleccion-opcion .vista-previa.audio").removeClass("hide");
            //Se crea un elemento de tipo 'audio' para que se pueda reproducir desde la pantalla y el usuario pueda escucharlo
            $audio.html("<audio  controls><source src='" + resp.url + "' type='" + resp.type + "'>Your browser does not support the audio tag.</audio>");
            evidencia.url = resp.url;
            evidencia.mimetype = resp.type;
            evidencia.tipo = 3;
        }, window.logError);
    });
    //Al seleccionar la opcion de tomar documento, lanzar el buscador en el dispositivo y al escoger uno
    //ajustar el objeto evidencia y activar su vista previa
    $paginaEvidencia.find(".tomar-documento").click(function (evt) {
        window.fileChooser.pick("*", function (resp) {
            $paginaEvidencia.find(".seleccion-opcion .vista-previa,.seleccion-opcion .flecha-arriba,.seleccion-opcion .mensaje").addClass("hide");
            $paginaEvidencia.find(".seleccion-opcion .vista-previa.documento").removeClass("hide");
            evidencia.url = resp.url;
            evidencia.mimetype = resp.type;
            evidencia.tipo = 4;
        }, window.logError);
    });
    //Al seleccionar la opcion de ubicar
    $paginaEvidencia.find(".ubicar").click(function (evt) {
        var $ubicacion = $paginaEvidencia.find("[name=ubicacion]");
        var $latitud = $paginaEvidencia.find("[name=latitud]");
        var $longitud = $paginaEvidencia.find("[name=longitud]");
        //Mostrar pantalla de selección de ubicación y obtener el punto seleccionado
        App.pick("ubicacion", function (posicion) {
            if (posicion) {//Si hay punto seleccionado
                $ubicacion.attr("placeholder", "Calculando...");
                //Intentar transformar la ubicacion (latitud,longitud) a texto
                ubicacionATexto(posicion.latitud, posicion.longitud, function (direccion) {
                    if (direccion) {
                        $ubicacion.attr("placeholder", "Seleccione un punto").val(direccion).trigger("propertychange")[0].focus();
                    } else {
                        $ubicacion.attr("placeholder", "Introduzca la direccion manualmente");
                    }
                });
                $latitud.val(posicion.latitud);
                $longitud.val(posicion.longitud);
            }
        });
    });
    var that = this;
    $paginaEvidencia.find(".app-topbar .ok").click(function () {
        if (evidencia.url) {//Si hay un url, quiere decir que ya se seleccionó archivo
            //Establecer los valores restantes en la vi
            evidencia.latitud = $paginaEvidencia.find("[name=latitud]").val();
            evidencia.longitud = $paginaEvidencia.find("[name=longitud]").val();
            evidencia.ubicacion = $paginaEvidencia.find("[name=ubicacion]").val();
            //Transformar el directorio de datos de la app a un entry
            window.resolveLocalFileSystemURL(cordova.file.dataDirectory, function (directoryEntry) {
                //Transformar la url de la evidencia en un entry para copiarlo
                window.resolveLocalFileSystemURL(evidencia.url, function (fileEntry) {
                    var nombreArchivo = "evidencia_" + window.random() + "_" + window.random();
                    //Copiar la evidencia al directorio de la app para su posterior envio
                    fileEntry.copyTo(directoryEntry, nombreArchivo, function () {
                        evidencia.uri = cordova.file.dataDirectory + nombreArchivo;
                        that.reply(evidencia);//Indicamos la evidencia seleccionada, a la pantalla que nos haya invocado
                    }, window.logError);

                }, window.logError);
            }, window.logError);
        } else {//Si no hay url, mostrar mensaje
            App.dialog({
                title: 'No ha seleccionado la evidencia a agregar',
                okButton: 'Aceptar',
            }, function (choice) {

            });
        }
    });

});