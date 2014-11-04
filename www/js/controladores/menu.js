//Controlador para la pantall de menu
App.controller('menu', function (page) {
    var $menu = $(page);
    
    //El menu 'nueva' lanza la pantalla correspondiente
    $menu.find('.menu-item .nueva').on('click', function () {
        //Crear denuncia vacia
        var denuncia = DenunciasManager.crearDenunciaVacia();
        console.log("denuncia en menu");
        console.log(denuncia);
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
    //El tutorial no está disponible en el prototipo
    $menu.find('.app-topbar .tutorial').on('click', function () {
        swal({title: "No disponible", text: "El tutorial no se encuentra disponible en la versi\u00F3n prototipo de esta aplicaci\u00F3n debido a que se entreg\u00F3 un video y manual de uso como parte de los requisitos", type: "info", confirmButtonText: "Aceptar"});
    });
});