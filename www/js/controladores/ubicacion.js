//Controlador para la pantalla 'Ubicación'
App.controller('ubicacion', function (page) {
    var $paginaSeleccionarUbicacion = $(page);
    var $gmap = $paginaSeleccionarUbicacion.find(".gmap");
    var ubicacion = null;

//    var latlng = new google.maps.LatLng(20.9872563, -101.2849372);
    //Opciones para el Google Map
    var mapOptions = {
        zoom: 16,
//        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map($gmap[0], mapOptions);
    var marker = null;
    //Calcular posicion del dispositivo y poner un marcador en la ubicacion detectada
    function calcularPosicion() {
        navigator.geolocation.getCurrentPosition(function (position) {
            var latlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
            //Agregar Marcador
            marker = new google.maps.Marker({
                map: map,
                position: latlng
            });
            //Centrar el mapa en la ubicacion del usuario
            map.setCenter(marker.getPosition());
            if (!ubicacion) {
                ubicacion = latlng;
            }
        }, window.logError);
    }
    //Si no se tiene activa la geolocalización, mostrar mensaje para cambiarlo
    if (cordovaApp.getConfig("geo") == 0) {
        //Esperar un segundo para mostrar el mensaje
        setTimeout(function () {
            //Mostrar mensaje al usuario indicado si desea activar la geolocalizacion
            swal({title: "¿Desea que se utilize su ubicaci\u00F3n para mejores resultados?", text: "", type: "info", showCancelButton: true, confirmButtonText: "Si!", cancelButtonText: "No"}, function (isConfirm) {
                if (isConfirm) {//Dijo que sí
                    cordovaApp.setConfig("geo", 1);//Cambiar configuración
                    calcularPosicion();//Calcular ubicación
                }
            });
        }, 1000);
    } else {
        calcularPosicion();//Si se activó la geolocalización, calcular
    }
    //Al hacer clic en algún lugar del mapa, ubicar el marcador en ese lugar
    google.maps.event.addListener(map, 'click', function (e) {
        if (!marker) {
            marker = new google.maps.Marker({
                map: map,
                position: e.latlng
            });
        } else {
            marker.setPosition(e.latLng);
        }
        ubicacion = e.latLng;//Ajustar la ubicacion en la posicion seleccionada
    });
    var that = this;
    //Si se seleccionó la opción de aceptar
    $paginaSeleccionarUbicacion.find(".ok").click(function () {
        if (ubicacion) {//Si hay ubicacion, regresar la ubicacion a la pantalla que nos invocó
            that.reply({latitud: ubicacion.lat(), longitud: ubicacion.lng()});
        } else {//Si no, mostrar mensaje
            App.dialog({
                title: 'No ha seleccionado la ubicacion',
                okButton: 'Aceptar',
            }, function (choice) {

            });
        }
    });

    //Esperarnos unos milisegundos para ajustar el mapa al contenedor y volver a establecer el centro
    //previniendo errores
    setTimeout(function () {
        google.maps.event.trigger(map, 'resize');
        if (ubicacion != null) {
            map.setCenter(ubicacion);
        }
    }, 200);
});
