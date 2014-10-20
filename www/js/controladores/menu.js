//Controlador para la pantall de menu
App.controller('menu', function (page) {
    var $menu = $(page);
    
    //El menu 'nueva' lanza la pantalla correspondiente
    $menu.find('.menu-item .nueva').on('click', function () {
        //Crear denuncia vacia
        var denuncia = DenunciasManager.crearDenunciaVacia();
        App.load("nueva", denuncia);
    });
    //El menu 'misdenuncias' lanza la pantalla correspondiente
    $menu.find('.menu-item .mis-denuncias').on('click', function () {
        App.load("misdenuncias");
    });
    //El menu 'oficinas' lanza la pantalla correspondiente
    $menu.find('.menu-item .oficinas').on('click', function () {
        App.load("oficinas");
    });
    //El menu 'sugerencias' lanza la pantalla correspondiente
    $menu.find('.menu-item .sugerencias').on('click', function () {
        App.load("sugerencias");
    });
    //El menu 'configuracion' lanza la pantalla correspondiente
    $menu.find('.menu-item .configuracion').on('click', function () {
        App.load("configuracion");
    });
    //El menu 'quehacer' lanza la pantalla correspondiente
    $menu.find('.menu-item .que-hacer').on('click', function () {
        App.load("quehacer");
    });

});