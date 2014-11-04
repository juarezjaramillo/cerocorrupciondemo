window.DenunciasManager = {
    init: function () {
        //Verificar que exista un arreglo de denuncias, sino crearlo
        var denuncias = window.localStorage.getObject("denuncias");
        if (denuncias == null) {
            denuncias = [];
            window.localStorage.setObject("denuncias", denuncias);
        }
    },
    obtenerDenuncias: function () {
        return window.localStorage.getObject("denuncias");
    },
    guardarDenuncia: function (denunciaDirty) {
        var ix = denunciaDirty.iddenuncialocal != null ? this.obtenerIndiceDenuncia(denunciaDirty.iddenuncialocal) : -1;
        var denuncias = this.obtenerDenuncias();
        if (ix == -1) {
            denunciaDirty.iddenuncialocal = Math.floor((Math.random() * 100000) + 1);
            console.log("iddenuncia generado:"+denunciaDirty.iddenuncialocal);
            denunciaDirty.fechacreacion = new Date().getTime();
            denuncias.push(denunciaDirty);
        } else {
            denuncias[ix] = denunciaDirty;
        }
        //if(denunciaDirty.fechacreacion==null){
        //    denunciaDirty.fechacreacion=new Date().getTime();
        //}
        window.localStorage.setObject("denuncias", denuncias);
    },
    obtenerIndiceDenuncia: function (idLocal) {
        var denuncias = this.obtenerDenuncias();
        for (var i = 0; i < denuncias.length; i++) {
            if (denuncias[i].iddenuncialocal == idLocal) {
                return i;
            }
        }
        return -1;
    },
    obtenerDenuncia: function (idLocal) {
        var ix = this.obtenerIndiceDenuncia(idLocal);
        return ix != -1 ? this.obtenerDenuncias()[ix] : null;
    },
    obtenerDenunciaProduccion: function (idProduccion) {

    },
    crearDenunciaVacia: function () {
        //var den = $.extend({}, window._plantillaDenuncia);
        var den={"iddenuncialocal": null, "estatus": "Formulario No Enviado", "tipo": "1", "anonima": "1", evidencias: []};
        den.fechacreacion = new Date().getTime();
        return den;
    },
    eliminarDenuncia:function(idLocal){
        var ix = this.obtenerIndiceDenuncia(idLocal);
        if(ix!=-1){
            var arDenuncias = this.obtenerDenuncias();
            arDenuncias.splice(ix,1);
            window.localStorage.setObject("denuncias", arDenuncias);
        }
    }
};
window.DenunciasManager.init();