//Controlador para la pantalla de 'Oficinas'
App.controller('oficinas', function (page) {
    var $paginaOficinas = $(page);
    var $gmap = $paginaOficinas.find(".gmap");

    //En el prototipo el centro del mapa es una de las oficinas (para que puedan verse los marcadores)
    var latlng = new google.maps.LatLng(19.3811483, -99.1687451);
    //Opciones para el mapa
    var mapOptions = {
        zoom: 13,//Nivel de Zoom
        center: latlng,//El centro
        mapTypeId: google.maps.MapTypeId.ROADMAP//Tipo de mapa
    };
    var map = new google.maps.Map($gmap[0], mapOptions);//Crear Google Map
    var marker = null;
    //Esperarnos unos milisegundos para ajustar el mapa al contenedor y volver a establecer el centro
    //previniendo errores
    setTimeout(function () {
        google.maps.event.trigger(map, 'resize');
        map.setCenter(latlng);
    }, 100);
    //Calcular posicion del dispositivo y poner un marcador en la ubicacion detectada
    function calcularPosicion() {
        navigator.geolocation.getCurrentPosition(function (position) {//Obtener posicion actual
            var latlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
            //Agregar Marcador
            marker = new google.maps.Marker({
                map: map,
                position: latlng
            });
            //Centrar el mapa en la ubicacion del usuario
            map.setCenter(marker.getPosition());
        }, window.logError);
    }
    //Si no se tiene activa la geolocalización, mostrar mensaje para cambiarlo
    if (cordovaApp.getConfig("geo") == 0) {
        //Esperar un segundo para mostrar el mensaje
        setTimeout(function () {
            //Mostrar mensaje al usuario indicado si desea activar la geolocalizacion
            swal({title: "¿Desea que se utilize su ubicaci\u00F3n para mejores resultados?", text: "", type: "info", showCancelButton: true, confirmButtonText: "Si!", cancelButtonText: "No"}, function (isConfirm) {
                if (isConfirm) {//Dijo que si
                    cordovaApp.setConfig("geo", 1);//Cambiar configuración
                    calcularPosicion();//Calcular ubicación
                }
            });
        }, 1000);
    } else {
        calcularPosicion();//Si se activo la geolocalización, calcular
    }

    //En éste prototipo se tienen hardcodeadas las oficinas
    var oficinas = window.OficinasManager.obtenerOficinas();
    //Agregar Markers al Mapa
    for (var i = 0; i < oficinas.length; i++) {
        var oficina = oficinas[i];
        //Ubicacion de la oficina
        var latlng2 = new google.maps.LatLng(oficina.latitud, oficina.longitud);
        //Agregar marcador para la oficina
        new google.maps.Marker({
            map: map,
            position: latlng2
        });
    }

    //El botón de la barra de acciones
    var $botonRuta = $paginaOficinas.find(".app-topbar .ruta");
    //Cuando se seleccion una oficina de la lista...
    $paginaOficinas.on("click", ".oficina-item", function (evt) {
        //La oficina a mostrar
        var oficina = $(this).data("oficina");
        //Se muestra la pantalla de detalle
        App.load('detalleoficina', oficina);
    });
    //Al mostrar el tab del mapa
    $paginaOficinas.find('a[href="#mapa"]').on('shown.bs.tab', function (e) {
        //if (google) {
        //Obtener el centro
        var center = map.getCenter();
        //Ajustar el mapa, para evitar problemas con la interfaz
        google.maps.event.trigger(map, 'resize');
        map.setCenter(center);
        //}
        //El botón de cálculo de ruta debe mostrarse
        $botonRuta.removeClass("hide");
    });
    //Al mostrar el tab del listado de oficinas se debe ocultar el botón de cálculo de ruta
    $paginaOficinas.find('a[href="#listado"]').on('shown.bs.tab', function (e) {
        $botonRuta.addClass("hide");
    });
    //Al seleccionar el botón de cálculo de ruta
    $paginaOficinas.find('.ruta').on('click', function (e) {
        if (marker) {//Si ya tenemos la ubicación del usuario
            var ubicacion = marker.getPosition();//Ubicacion del usuario
            //Las oficinas
            var oficinas = window.OficinasManager.obtenerOficinas();
            var distancia = -1;//Guardará a distancia de la oficina mas cercana
            var ix = -1;//Guardará el índice de la oficina mas cercana
            var destino = null;//La ubicación de la oficina más cercana
            for (var i = 0; i < oficinas.length; i++) {
                var oficina = oficinas[i];
                //Ubicación de la oficina
                var latlng = new google.maps.LatLng(oficina.latitud, oficina.longitud);
                //Calcular distancia
                var temp = getDistance(ubicacion, latlng);
                if (temp < distancia || distancia == -1) {//Si la distancia calculada es menor a la que se tiene (o es la inicial)
                    //La nueva oficina mas cercana es la actual
                    distancia = temp;
                    ix = i;
                    destino = latlng;
                }
            }
            //Sí se encontró una oficina más cercana
            if (ix != -1) {
                //Tomado de Google API Docs
                //Calcular la ruta hacia la oficina destino (la más cercana)
                var directionsService = new google.maps.DirectionsService();
                var directionsDisplay = new google.maps.DirectionsRenderer();
                directionsDisplay.setMap(map);
                var request = {
                    origin: ubicacion,
                    destination: latlng,
                    travelMode: google.maps.TravelMode.DRIVING
                };
                directionsService.route(request, function (result, status) {
                    if (status == google.maps.DirectionsStatus.OK) {
                        directionsDisplay.setDirections(result);
                    } else {
                        //En el prototipo se ignora el error
                    }
                });
            }
        } else {//Indicar al usuario que no tenemos su ubicación y no podemos calcular la ruta
            swal("¡No se conoce su ubicaci\u00F3n!", "Puede ser que a\u00FAn no se haya detectado o que no tenga habilitada esta funcionalidad. Verifique en la Configuraci\u00F3n.");
        }
    });

    //Cargar lista de oficinas
    function establecerOficinas() {
        //Limpiar lista
        $paginaOficinas.find(" > .app-content .oficina-item").remove();
        //La plantilla
        var $plantilla = $paginaOficinas.find(" > .app-content .oficina-item-plantilla");
        //Agregar cada oficina
        for (var i = 0; i < oficinas.length; i++) {
            //Clonar plantilla
            var $elem = $plantilla.clone().removeClass("oficina-item-plantilla hide").addClass("oficina-item");
            var oficina = oficinas[i];
            //Establecer datos de la oficina
            $elem.data("oficina", oficina);
            $elem.find(".unidad").html(oficina.unidad);
            $elem.find(".direccion").html(oficina.direccion);
            $elem.find(".responsable").html(oficina.responsable);
            //Agregar el elemento a la lista, insertándolo después de la plantilla (que está oculta)
            $elem.insertAfter($plantilla);
        }
    }

    //Para el calculo de distancia
    var rad = function (x) {
        return x * Math.PI / 180;
    };

    //Para el calculo de distancia
    var getDistance = function (p1, p2) {
        var R = 6378137; // Earth’s mean radius in meter
        var dLat = rad(p2.lat() - p1.lat());
        var dLong = rad(p2.lng() - p1.lng());
        var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1.lat())) * Math.cos(rad(p2.lat())) *
                Math.sin(dLong / 2) * Math.sin(dLong / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        var d = R * c;
        return d; // returns the distance in meter
    };

    //Cargar la lista de oficinas
    establecerOficinas();
});

//Controlador para la pantalla 'Detalle de Oficina'
App.controller('detalleoficina', function (page, oficina) {
    var $paginaDetalleOficina = $(page);
 
    var $gmap = $paginaDetalleOficina.find(".gmap");
    //Configurar el Google Map, con la ubicación de la oficina en el centro
    var latlng = new google.maps.LatLng(oficina.latitud, oficina.longitud);
    var mapOptions = {
        zoom: 16,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map($gmap[0], mapOptions);
    //Agregar marcador en laubicación de la oficina
    new google.maps.Marker({
        map: map,
        position: latlng
    });

    //Para prevenir errores en la interfaz
    setTimeout(function () {
        google.maps.event.trigger(map, 'resize');
        map.setCenter(latlng);
    }, 100);

    //Establecer datos de la oficina en la interfaz
    var $elem = $paginaDetalleOficina.find(".oficina");
    $elem.data("oficina", oficina);
    $elem.find(".unidad").html(oficina.unidad);
    $elem.find(".direccion").html(oficina.direccion);
    $elem.find(".responsable").html(oficina.responsable);
    $elem.find(".telefono").html(oficina.telefono);
    $elem.find(".horario").html(oficina.horario);
});