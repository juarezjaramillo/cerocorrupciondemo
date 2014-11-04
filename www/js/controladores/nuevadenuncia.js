//Controlador para la pantalla 'Nueva'
App.controller('nueva', function (page, denuncia) {
    var $paginaCaptura = $(page);
    console.log(denuncia);
    //Cargar las evidencias de la denuncia
    function cargarEvidencias() {
        //Limpiar evidencias
        $paginaCaptura.find(" > .app-content .form-evidencias .evidencias-item").remove();
        //La plantilla para cada evidencia
        var $plantilla = $paginaCaptura.find(" > .app-content .evidencias-item-plantilla");
        //Agregar cada evidencia
        for (var i = 0; i < denuncia.evidencias.length; i++) {
            //Clonar la plantilla
            var $elem = $plantilla.clone().removeClass("evidencias-item-plantilla hide").addClass("evidencias-item");
            var $thumb = $elem.find(".evidencia-thumbnail");
            var evidencia = denuncia.evidencias[i];
            if (evidencia.tipo == 1) {//Si es imagen, entonces generamos thumbnail
                window.generarThumb(evidencia.uri, $thumb);
            } else {
                //Para los demas tipos de evidencia se establece una imagen como thumbnail
                //Esto es en la version prototipo, en la version final, podria generarse tambien un thumbnail de los videos
                var src = "evidencia_documento.png";
                switch (evidencia.tipo) {
                    case 2:
                        src = "evidencia_video.png";
                        break;
                    case 3:
                        src = "evidencia_audio.png";
                        break;
                }
                $thumb.attr("src", "img/" + src);
            }
            if (evidencia.latitud == null || evidencia.latitud == "") {
                $elem.find(".evidencia-ubicacion").addClass("hide");
            }
            //Agregar el elemento a la lista, insertándolo despues de la plantilla (que está oculta)
            $elem.insertBefore($plantilla);
        }
    }

    //Establecer los datos de la denuncia en la pantalla
    function establecerDenuncia() {
        //Poner en cada campo la informacion de la denuncia
        $paginaCaptura.find("[name=tipo][value=" + denuncia.tipo + "]").prop("checked", true).change();
        $paginaCaptura.find('[name=folio]').val(denuncia.folio || "");
        $paginaCaptura.find('[name=estatus]').val(denuncia.estatus || "");
        $paginaCaptura.find('[name=persona]').typeahead('val', denuncia.persona || "");
        $paginaCaptura.find("[name=idpersona]").val(denuncia.idpersona || "");
        $paginaCaptura.find("[name=hechos]").val(denuncia.hechos || "");
        $paginaCaptura.find("[name=ubicacion]").val(denuncia.ubicacion || "");
        $paginaCaptura.find("[name=latitud]").val(denuncia.latitud || "");
        $paginaCaptura.find("[name=longitud]").val(denuncia.longitud || "");
        $paginaCaptura.find("[name=anonima]").prop("checked", denuncia.anonima == "1");
        $paginaCaptura.find(".denuncia-" + (denuncia.anonima == "1" ? "si" : "no") + "-anonima").removeClass("hide").find("input").prop("disabled", false);
        $paginaCaptura.find(".denuncia-" + (denuncia.anonima != "1" ? "si" : "no") + "-anonima").addClass("hide").find("input").prop("disabled", true);
        $paginaCaptura.find("[name=correo]").val(denuncia.correo || "");
        $paginaCaptura.find("[name=nombre]").val(denuncia.nombre || "");
        $paginaCaptura.find("[name=direccion]").val(denuncia.direccion || "");
        $paginaCaptura.find("[name=telefono]").val(denuncia.telefono || "");
        setTimeout(function () {
            //Disparar el evento propertychange para que se ajusten las etiquetas de los campos
            $("input,textarea", $paginaCaptura).not(".tt-hint").trigger("propertychange");
            if (denuncia.persona) {
                //Forzamos el cambio en el campo de persona, por el uso
                $paginaCaptura.find("[name=persona]").parent().parent().addClass("floating-label-form-group-with-value");
            }
        }, 100);
        //Cargar la lista de evidencias
        cargarEvidencias();
    }
    //Al hacer click en la etiqueta, ajustar el checkbox
    $paginaCaptura.find(".color-checkbox h4").click(function (evt) {
        $(this).parent().find("input[type=checkbox],input[type=radio]").click();
    });
    //Si se invocó esta pantalla con una denuncia que ya no puede modificarse
    //poner los campos en solo lectura 
    //y ajustar la interfaz a mostrar/ocultar la informacion segun sea el caso
    if (denuncia.iddenunciaproduccion != null && denuncia.iddenunciaproduccion > 0) {
        $paginaCaptura.find("input[type=text],textarea").attr("readonly", "true");
        $paginaCaptura.find("input[type=checkbox],input[type=radio]").prop("disabled", true);
        $paginaCaptura.addClass("detalle-denuncia");
        $paginaCaptura.removeClass("capturar-denuncia");
    } else {//Sí se puede modificar, ajustar la interfaz
        $paginaCaptura.find("input[type=text],textarea").removeAttr("readonly", "true");
        $paginaCaptura.find("input[type=checkbox],input[type=radio]").prop("disabled", false);
        $paginaCaptura.removeClass("detalle-denuncia");
        $paginaCaptura.addClass("capturar-denuncia");
    }

    //Al seleccionar la opcion de ubicar
    $paginaCaptura.find(".ubicar").click(function (evt) {
        var $ubicacion = $paginaCaptura.find("[name=ubicacion]");
        var $latitud = $paginaCaptura.find("[name=latitud]");
        var $longitud = $paginaCaptura.find("[name=longitud]");
        //Mostrar pantalla de selección de ubicación y obtener el punto seleccionado
        App.pick("ubicacion", function (posicion) {
            if (posicion) {
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

    //Inicializar TypeAhead para las personas
    var personas = new Bloodhound({
        datumTokenizer: function (d) {
            return Bloodhound.tokenizers.obj.whitespace(d);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: window.url_buscar_personas + "?q=%QUERY",
            ajax: {
                crossDomain: true,
                dataType: 'json'
            }
        }
    });
    personas.initialize();
    //Agregar el typeahead al campo de persona, para que al escribir un poco de texto
    //se dispara la búsqueda de personas y se autocomplete el nombre
    var $persona = $('#persona', $paginaCaptura).typeahead({
        hint: true,
        highlight: true,
        minLength: 1
    }, {
        name: 'personas',
        displayKey: 'nombre',
        source: personas.ttAdapter(),
        templates: {
            empty: [
                '<div class="empty-message">',
                'No se encontraron coincidencias',
                '</div>'
            ].join('\n'),
            suggestion: Handlebars.compile('<span class="nombre">{{nombre}}</span><span>{{subarea}}</span><span>{{area}}</span>')
        }
    });

    //AL presionar la tecla 'Enter' en el teclado, seleccionar la primera opcion de la lista de sugerencias
    var $lista = $persona.parent().find(".tt-dropdown-menu");
    $persona.on("keypress", function (evt) {
        if (evt.keyCode == 13) {
            $lista.find(".tt-suggestion:eq(0)").click();
        }
    });

    //Al cambiar la opcion de 'Denuncia' o 'Queja' ajustamos la interfaz para mostrar los mensajes correctos al usuario
    //(descripciones, nombres de campos, etc)
    $paginaCaptura.find("[name=tipo]").on("change", function () {
        var $this = $(this);
        var attr = $this.val() == "1" ? "denuncia" : attr = "queja";

        //Ajustar placeholders
        $paginaCaptura.find("[data-placeholder-" + attr + "]").not(".tt-hint").each(function (ix, el) {
            $(el).attr("placeholder", $(el).attr("data-placeholder-" + attr));
        });
        //Ajustar etiquetas
        $paginaCaptura.find("[data-text-" + attr + "]").each(function (ix, el) {
            $(el).html($(el).attr("data-text-" + attr));
        });
    });

    //Al cambiar el checkbox de Denuncia Anonima
    $paginaCaptura.find("[name=anonima]").on("change", function () {
        var $this = $(this);
        if ($this.prop("checked")) {//Sí, es denuncia anónima, ajustar interfaz
            $paginaCaptura.find(".denuncia-si-anonima").removeClass("hide").find("input").prop("disabled", false);
            $paginaCaptura.find(".denuncia-no-anonima").addClass("hide").find("input").prop("disabled", true);
        } else {//No es denuncia anónima, ajustar interfaz
            $paginaCaptura.find(".denuncia-no-anonima").removeClass("hide").find("input").prop("disabled", false);
            $paginaCaptura.find(".denuncia-si-anonima").addClass("hide").find("input").prop("disabled", true);
        }
    });

    //Hay dos campos de correo (una para denuncia anónima y el otro para cuando no lo es)
    //Se sincronizan para que ambos reflejen el mismo valor puesto que para el usuario es el mismo campo
    $paginaCaptura.find("#correo,#correo-no-anonima").blur(function () {
        $paginaCaptura.find("#correo,#correo-no-anonima").val($(this).val()).trigger("propertychange");
    });

    //Poner los datos de la denuncia en la interfaz
    establecerDenuncia();
    //Al seleccionar la opcion de enviar
    $paginaCaptura.find(".enviar").click(function (evt) {
        guardarDenuncia();//Guardar la denuncia localmente
        if ($paginaCaptura.find("[name=persona]").val() == "") {//Deben introducir la persona
            App.dialog({
                title: 'Indique el nombre de la persona',
                okButton: 'Aceptar',
            }, function (choice) {

            });
            return;
        } else if ($paginaCaptura.find("[name=hechos]").val() == "") {//Deben introducir los hechos
            App.dialog({
                title: 'Introduzca los hechos',
                okButton: 'Aceptar',
            }, function (choice) {

            });
            return;
        }
        App.load('enviardenuncia', denuncia);//Mostrar pantalla de envío de denuncia, le pasamos la denuncia a enviar
    });
    //Al seleccionar le botón de agregar evidencia
    $paginaCaptura.find(".evidencias-nuevo-item button").click(function (evt) {
        App.pick('evidencia', function (data) {//Mostrar la pantalla de Anexar Evidencia
            if (data) {//Evidencia seleccionada
                denuncia.evidencias.push(data);//Agregar evidenci a la lista de evidencias de la denuncia
                cargarEvidencias();//Recargar lista
            }
        });
    });

    //Antes de salir de la pantalla, guardamos los datos localmente
    $paginaCaptura.on("appBeforeBack", function (evt) {
        //Guardar la denuncia
        guardarDenuncia();
        return true;
    });

    //Guardar la denuncia
    function guardarDenuncia() {
        //Establecer los datos de la denuncia obteniéndolos de la interfaz de usuario
        denuncia.tipo = $paginaCaptura.find("[name=tipo]:checked").val();
        denuncia.persona = $paginaCaptura.find('[name=persona]', $paginaCaptura).val();
        denuncia.idpersona = $paginaCaptura.find("[name=idpersona]", $paginaCaptura).val();
        denuncia.hechos = $paginaCaptura.find("[name=hechos]", $paginaCaptura).val();
        denuncia.ubicacion = $paginaCaptura.find("[name=ubicacion]", $paginaCaptura).val();
        denuncia.latitud = $paginaCaptura.find("[name=latitud]", $paginaCaptura).val();
        denuncia.longitud = $paginaCaptura.find("[name=longitud]", $paginaCaptura).val();
        denuncia.anonima = $paginaCaptura.find("[name=anonima]", $paginaCaptura).prop("checked") ? "1" : "2";
        denuncia.correo = denuncia.anonima == "1" ? $("#correo", $paginaCaptura).val() : $("#correo-no-anonima", $paginaCaptura).val();
        if (denuncia.anonima != "1") {//Es anónima?
            denuncia.nombre = $paginaCaptura.find("[name=nombre]").val();
            denuncia.direccion = $paginaCaptura.find("[name=direccion]").val();
            denuncia.telefono = $paginaCaptura.find("[name=telefono]").val();
        } else {
            denuncia.nombre = "";
            denuncia.direccion = "";
            denuncia.telefono = "";
        }
        //Guardar la denuncia localmente
        window.DenunciasManager.guardarDenuncia(denuncia);
    }
});

