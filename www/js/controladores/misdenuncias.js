//Controlador para la pantalla Mis Denuncias
App.controller('misdenuncias', function (page) {
    var $paginaMisDenuncias = $(page);

    //Mapeo de meses
    var arDias = ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"];
    //Cargar la lista de denuncias
    function establecerDenuncias(denuncias) {
        //Limpiar denuncias
        $paginaMisDenuncias.find(" > .app-content .denuncia-item").remove();
        //Plantilla de la denuncia
        var $plantilla = $paginaMisDenuncias.find(" > .app-content .denuncia-item-plantilla");
        if (denuncias.length > 0) {//Hay denuncias
            $paginaMisDenuncias.find("> .app-content .mensaje").addClass("hide");
            //Agregar cada denuncia
            for (var i = 0; i < denuncias.length; i++) {
                //Clonar la plantilla
                var $elem = $plantilla.clone().removeClass("denuncia-item-plantilla hide").addClass("denuncia-item");
                var denuncia = denuncias[i];
                $elem.attr("iddenuncialocal", denuncia.iddenuncialocal);
                console.log("iddenuncialocal:",denuncia.iddenuncialocal);
                console.log("iddenuncia:",denuncia.iddenunciaproduccion);
                //Establecer los datos de la denuncia en la plantilla
                var titulo = (denuncia.tipo == "1" ? "Denuncia a" : "Queja de") + " " + ((denuncia.persona != null && denuncia.persona.length > 0) ? denuncia.persona : "<Por Definir>");
                var evidencias = denuncia.evidencias != null ? (denuncia.evidencias.length == 1 ? "1 evidencia" : denuncia.evidencias.length + " evidencias") : " 0 evidencias";
                var fechaDate = denuncia.fechacreacion != null ? new Date(denuncia.fechacreacion) : null;
                var fecha = denuncia.fechacreacion != null ? (fechaDate.getDate() + " " + arDias[fechaDate.getMonth()]) : "--";
                var estatus = denuncia.estatus != null ? denuncia.estatus : "Estatus no definido";
                $elem.find(".titulo").html(titulo);
                $elem.find(".evidencias").html(evidencias);
                $elem.find(".fecha").html(fecha);
                $elem.find(".estatus").html(estatus);
                $elem.find(".lanzador-menu").dropdown();
                //Agregar el clon dela plantilla al contenedor, insertandola despues de la plantilla (que está oculta)
                $elem.insertAfter($plantilla);
            }
        } else {//No hay denuncias, mostrar mensaje
            $paginaMisDenuncias.find("> .app-content .mensaje").removeClass("hide");
        }
    }

    //Prevenir que al hacer clic en los tres puntos del menu en la denuncia, se abra la pantalla de detalle/captura
    $paginaMisDenuncias.on("click", ".lanzador-menu", function (evt) {
        evt.preventDefault();
        evt.stopPropagation();
    });
    //Al seleccionar la opción eliminar del menu contextual
    $paginaMisDenuncias.on("click", ".opcion-menu.eliminar", function (evt) {
        //Prevenir bubbling
        evt.preventDefault();
        evt.stopPropagation();
        //Ajustar visibilidad del menu dropdown
        $(this).parent().parent().prev().dropdown('toggle');
        var $item = $(this).parents(".denuncia-item");
        var iddenuncialocal = $item.attr("iddenuncialocal");
        //Eliminar Denuncia
        window.DenunciasManager.eliminarDenuncia(iddenuncialocal);
        //Quitar de la interfaz el elemento
        $item.fadeOut('fast', function () {
            $item.remove();
        });
    });
    //Al seleccionar una denuncia
    $paginaMisDenuncias.on("click", ".denuncia-item", function (evt) {
        //Cargar la denuncia
        var denuncia = window.DenunciasManager.obtenerDenuncia($(this).attr("iddenuncialocal"));
        //Mostrar la pantalla de detalle/captura
        App.load('nueva', denuncia);
    });

    //Cada que es muestre la pantalla, ajustar la interfaz para mostrar los datos mas actuales
    $paginaMisDenuncias.on("appShow", function () {
        establecerDenuncias(window.DenunciasManager.obtenerDenuncias());
    });

});

