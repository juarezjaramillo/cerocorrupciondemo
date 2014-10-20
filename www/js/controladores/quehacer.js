//Controlador para la pantalla 'Que Hacer'
App.controller('quehacer', function (page) {
    var $paginaQueHacer = $(page);
    //Al seleccionar un elemento de la lista
    $paginaQueHacer.on("click", ".que-hacer-item", function (evt) {
        //Obtener la información de ese elemento 
        var queHacerItem = window.QueHacerManager.obtenerItem($(this).attr("data-item"));
        //y mostrar la página de detalle
        App.load('quehacerdetalle', queHacerItem);
    });
});

//Controlador para la pantalla de detalle de un elemento de 'Que Hacer'
App.controller('quehacerdetalle', function (page, queHacerItem) {
    var $paginaQueHacerDetalle = $(page);
    var $titulo = $paginaQueHacerDetalle.find(".app-topbar .app-title");
    var $swipe = $paginaQueHacerDetalle.find(".swipe");
    var $indicadores = $swipe.find(".swipe-indicators");
    var $swipeWrap = $swipe.find(".swipe-wrap");
    //Ajustar el título de la pantalla, al nombre del item seleccionado
    $titulo.html(queHacerItem.titulo);
    //Generar las 'paginas' de detalle
    for (var i = 0; i < queHacerItem.paginas.length; i++) {
        var contenido = queHacerItem.paginas[i];
        //Cada div es una 'página'
        var $div = $("<div />").html(contenido);
        $swipeWrap.append($div);
        //Los li son los círculos que indican cada pagina
        $indicadores.append("<li " + (i == 0 ? " class='active'" : "") + "></li>");
    }
    //Cada que se muestre la pantalla, ajustar el swipe de las páginas
    $paginaQueHacerDetalle.on("appShow", function () {
        $swipe.Swipe({
            continuous: false,
            disableScroll: false,
            stopPropagation: false,
            callback: function (index, elem) {
                //Indicar qué círulo (li) está activo
                $indicadores.find("li").removeClass("active").eq(index).addClass("active");
            }
        });
    });
});