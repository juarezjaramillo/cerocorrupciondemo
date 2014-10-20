//Controlador para la pantalla de Enviar Denuncia
App.controller('enviardenuncia', function (page, denuncia) {
    var $paginaEnviarDenuncia = $(page)
    //El dialogo donde se muestra el progreso, y sus elementos
    var $modal = $paginaEnviarDenuncia.find(".modal").modal({show: true, backdrop: false});
    var $titulo = $paginaEnviarDenuncia.find(".modal-title");
    var $contenido = $paginaEnviarDenuncia.find(".modal-body");
    var $footer = $paginaEnviarDenuncia.find(".modal-footer");
    var $progreso = $paginaEnviarDenuncia.find(".progress-bar");

    //Porcentaje de aumentos para la barra de progreso
    var incremento = 100 / (1 + denuncia.evidencias.length);
    //Progreso Inicial
    var progreso = 5;
    //La denuncia a enviar
    var denTemp = $.extend({}, denuncia);
    denTemp.evidencias = null;//Las evidencias se procesan diferente, se dejan en null por ahora
    $.ajax({
        type: "POST",
        url: window.url_enviar_denuncia,
        data: $.extend({}, denTemp, {"userid": cordovaApp.getPushId()}),
        crossDomain: true,
        dataType: 'json',
        success: function (msg) {
            if (msg.estatus == "OK") {//Si los datos de la denuncia se guardaron correctamente
                progreso += incremento;
                $progreso.css("width", progreso + "%");//Ajustamos el progreso
                //Establecemos los valores obtenidos en la denuncia original
                denuncia.iddenunciaproduccion = msg.iddenuncia;
                denuncia.idestatus = msg.idestatus;
                denuncia.estatus = msg.estatus_descripcion;
                denuncia.folio = msg.folio;
                window.DenunciasManager.guardarDenuncia(denuncia);//Guardar 
                //Enviar las evidencias, una a una
                var total = denuncia.evidencias.length;
                if (total > 0) {//Hay evidencias
                    function enviar(k) {
                        if (k == total) {//LLegamos al final
                            //Mostrar mensaje
                            $progreso.css("width", "100%");
                            //Ajustar la interfaz para mostrar el resultado (folio,mensajes,etc)
                            setTimeout(function () {
                                $contenido.find(".procesando,.error").addClass("hide");
                                var $ok = $contenido.find(".ok").removeClass("hide");
                                $ok.find(".folio").html(msg.folio);
                                $footer.removeClass("hide");
                                $footer.find(".reintentar").addClass("hide");
                                $footer.find(".aceptar").removeClass("hide");
                            }, 300);
                            return;
                        }
                        if (k > 0) {
                            //Ajustar progreso
                            progreso += incremento;
                            $progreso.css("width", progreso + "%");
                        }
                        //La evidencia a enviar
                        var evidencia = denuncia.evidencias[k];
                        //En caso de envío exitoso
                        var win = function (r) {
                            if (r.responseCode == null /*iOS*/ || r.responseCode == 200) {
                                //Parsear respuesta
                                var respuesta = JSON.parse(r.response);
                                if (respuesta.estatus == "OK") {
                                    enviar(k + 1);//Enviar siguiente evidencia
                                } else {
                                    //Mandar error al log
                                    window.logError(respuesta.error);
                                    //En version prototipo se ignora el error y se continua con la siguiente evidencia
                                    enviar(k + 1);
                                }
                            } else {
                                //En version prototipo se ignora el error y se continua con la siguiente evidencia
                                enviar(k + 1);
                            }
                        };
                        //En caso de un error en el envío
                        var fail = function (error) {
                            //En version prototipo se ignora el error y se continua con la siguiente evidencia
                            console.log("upload error source " + error.source);
                            console.log("upload error target " + error.target);
                            enviar(k + 1);
                        };

                        //Informacion a enviar
                        var options = new FileUploadOptions();
                        options.fileKey = "archivo";
                        options.fileName = evidencia.uri.substr(evidencia.uri.lastIndexOf('/') + 1);
                        options.mimeType = evidencia.mimetype||'image/jpeg';

                        //Datos extras como ubicacion, id de usuario (para notificaciones), etc.
                        var params = {};
                        params.ubicacion = evidencia.ubicacion;
                        params.latitud = evidencia.latitud;
                        params.longitud = evidencia.longitud;
                        params.iddenuncia = denuncia.iddenunciaproduccion;
                        params.userid = cordovaApp.getPushId();
                        options.params = params;

                        //Enviar archivo al backend
                        var ft = new FileTransfer();
                        ft.upload(evidencia.uri, encodeURI(window.url_enviar_denuncia_evidencia), win, fail, options);
                    }
                    //Iniciamos enviando la primer evidencia
                    enviar(0);
                } else {
                    //No hay evidencias, ajustar interfaz indicando folio y que todo OK
                    $progreso.css("width", "100%");
                    $contenido.find(".procesando,.error").addClass("hide");
                    var $ok = $contenido.find(".ok").removeClass("hide");
                    $ok.find(".folio").html(msg.folio);
                    $footer.find(".reintentar").addClass("hide");
                    $footer.find(".aceptar").removeClass("hide");
                }
            } else {
                //Si ocurrio un error, ajustar la interfaz y mostrar boton de reintento
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
            //Error desonocido, ajustar la interfaz
            $titulo.html("Oops!");
            $contenido.find(".procesando,.ok").addClass("hide");
            $contenido.find(".error").removeClass("hide");
            $footer.removeClass("hide").find(".aceptar").addClass("hide").end().find(".reintentar").removeClass("hide");
        }
    });

    $paginaEnviarDenuncia.find(".modal-footer .aceptar").click(function (evt) {
        //Eliminar del back stack la pantalla de captura
        var cont = App.getStack().length;
        App.removeFromStack(cont - 2, cont - 1);
        App.back();//Regresar

    });
    $paginaEnviarDenuncia.find(".modal-footer .reintentar").click(function (evt) {
        //Reintentar el envío terminando esta pantall y volviendola a lanzar
        App.back();
        App.load('enviardenuncia', denuncia);
    });
});