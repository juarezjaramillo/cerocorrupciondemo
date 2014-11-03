//Urls del backend
//window.url_servidor = "http://192.168.1.66:8888/";
window.url_servidor = "http://cerocorrupcionmx.appspot.com/";
window.url_enviar_denuncia = window.url_servidor + "cerocorrupcionmx/GuardarDenuncia";
window.url_enviar_denuncia_evidencia = window.url_servidor + "cerocorrupcionmx/GuardarEvidencia";
window.url_buscar_personas = window.url_servidor + "cerocorrupcionmx/BuscarPersonas";
window.url_enviar_sugerencia = window.url_servidor + "cerocorrupcionmx/GuardarSugerencia";
window.url_enviar_configuracion = window.url_servidor + "cerocorrupcionmx/GuardarConfiguracion";
//Plantilla para una denuncia nueva
window._plantillaDenuncia = {"iddenuncialocal": null, "estatus": "Formulario No Enviado", "tipo": "1", "anonima": "1", evidencias: []}
//Plantilla para la configuracion inicial de la app
window._plantillaConfig = {"geo": 1, "push": 1};
//Para poder guardar objetos json en local storage
window.Storage.prototype.setObject = function (key, value) {
    this.setItem(key, JSON.stringify(value));
}
window.Storage.prototype.getObject = function (key) {
    var value = this.getItem(key);
    return value && JSON.parse(value);
}
$(function () {
    //Para las etiquetas flotantes de las cajas de texto
    $("body").on("input propertychange", ".floating-label-form-group", function (e) {
        $(this).toggleClass("floating-label-form-group-with-value", !!$(e.target).val());
    }).on("focus", ".floating-label-form-group", function () {
        $(this).addClass("floating-label-form-group-with-focus");
    }).on("blur", ".floating-label-form-group", function () {
        $(this).removeClass("floating-label-form-group-with-focus");
    });
    //Disparar evento al cargar el DOM
    $("input,textarea").trigger("propertychange");
    //Reproducir sonido del 'tap' nativo
    $(document).on('touchend','.app-button,button,.color-checkbox,.lanzador-menu,.dropdown-menu li', function () {
        window.plugins.deviceFeedback.acoustic();
    })
});
//Para mostrar errores en la consola
window.logError = function (error) {
    console.log(error);
}
//Generacion de aleatorios (para identificadores, temporales, etc)
window.random = function (n) {
    return Math.floor((Math.random() * (n != null && n > 0 ? n : 100000)) + 1);
}
//Generar thumbnails a partir del uri de una imagen,
window.generarThumb = function (uri, $elem) {

    function thumb(src, tsize) {
        //Canvas donde dibujar el thumbnail
        var myCan = document.createElement('canvas');
        var img = new Image();
        img.src = src;
        img.onload = function () {//Al cargar la imagen

            myCan.id = "myTempCanvas";
            myCan.width = Number(tsize);
            myCan.height = Number(tsize);
            if (myCan.getContext) {//Contexto para dibujar
                var cntxt = myCan.getContext("2d");
                //Dibujar imagen
                cntxt.drawImage(img, 0, 0, myCan.width, myCan.height);
                var dataURL = myCan.toDataURL();//Transofmar la imagen codificacion base64

                if (dataURL != null && dataURL != undefined) {
                    var nImg = $elem;
                    nImg.attr("src", dataURL);//Establecer el thumbnail en el elemento que se pasó como parametro
                }
                else {
                    window.logError('unable to get context');
                }
            }
        };
    }
    //Transformar la url a un entry y poder leerlo
    window.resolveLocalFileSystemURL(uri, function (fileEntry) {
        var reader = new FileReader();
        if (reader != null) {
            fileEntry.file(function (file) {//Obtener la informacion del archivo
                reader.onload = function (e) {
                    thumb(e.target.result, 200);//Generar thumbnail, con ancho y alto de 200
                };
                reader.readAsDataURL(file);//Leer el archivo en codificacion base64
            });
        }
    }, window.logError);

};
//Transformar latitud y longitud a  una direccion textual
window.ubicacionATexto = function (latitud, longitud, callback) {
    if (google) {//Si ya se cargo el api de Google Maps
        var geocoder = new google.maps.Geocoder();//Geocodificador
        //Transformar usando api de Google
        geocoder.geocode({'location': new google.maps.LatLng(latitud, longitud)}, function (results, status) {
            //Si todo OK
            if (status == google.maps.GeocoderStatus.OK) {
                //Invocar el callback con la direccion del primer resultado
                if (callback) {
                    callback(results[0].formatted_address);
                }
            } else {//Si hay error, invocar el callback con null (indicando que no se pudo convertir)
                if (callback) {
                    callback(null);
                }
            }
        });
    }
};