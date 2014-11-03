//Controlador para la pantalla de Configuración
App.controller('configuracion', function (page) {
    var $paginaConfiguracion = $(page);
    var $configGeo = $paginaConfiguracion.find("#config-geo");
    var $configPush = $paginaConfiguracion.find("#config-push");
    //Cuando se cambie el checkbox de geolocalizacion cambiar el mensaje que aparece debajo
    $configGeo.on("change", function (evt) {
        var checked = $(this).prop("checked");
        cordovaApp.setConfig("geo", checked ? 1 : 0);//Cambiar en la configuracion de la app
        $(this).parent().next().find("span").addClass("hide").filter(checked ? ".on" : ".off").removeClass("hide");
        enviarConfiguracion();//Enviar al backend
    });
    //Cuando se cambie el checkbox de notificaciones push cambiar el mensaje que aparece debajo
    $configPush.on("change", function (evt) {
        var checked = $(this).prop("checked");
        cordovaApp.setConfig("push", checked ? 1 : 0);//Cambiar en la configuracion de la app
        $(this).parent().next().find("span").addClass("hide").filter(checked ? ".on" : ".off").removeClass("hide");
        enviarConfiguracion();//Enviar al backend
    });

    //Al hacer click en el titulo del checkbox, cambiar el estatus del mismo
    $paginaConfiguracion.find(".color-checkbox h4").click(function (evt) {
        $(this).parent().find("input[type=checkbox]").click();
        //enviarConfiguracion();
    });
    //La configuracion de la app
    var config = cordovaApp.getConfig();
    //Cambiar el estatus de los checkbox en base a la configuracion
    $configGeo.prop("checked", config.geo == 1).change();
    $configPush.prop("checked", config.push == 1).change();


    //Enviar la configuracion al backend
    function enviarConfiguracion() {
        $.ajax({
            type: "POST",
            url: window.url_enviar_configuracion,
            data: $.extend({}, cordovaApp.getConfig(), {"userid": cordovaApp.getPushId()}),
            crossDomain: true,
            dataType: 'json',
            success: function (msg) {
                console.log("Configuracion Enviada");
                console.log(msg);
            },
            error: function (xhr, status, error) {
                console.log(xhr);
                console.log(status);
                console.log(error);
            }
        });
    }
});
