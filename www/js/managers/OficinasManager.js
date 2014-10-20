window.OficinasManager = {
    init: function () {
        //Oficinas Hardcodeadas
        this.oficinas = [
            {
                "unidad": "Dirección General Adjunta de Política de Contrataciones Públicas",
                "responsable": "Lic. Ana Cristina Hernández Velázquez",
                "correo": "ahernandezv@funcionpublica.gob.mx",
                "direccion": "Insurgentes Sur 1735 piso 2 ala norte, Col. Guadalupe Inn, Del. Álvaro Obregón, D.F.",
                "telefono": "Tel.: +52 (55) 2000-3000 Exts. 2140, 2430 y 2172.",
                "horario": "De lunes a viernes, en horario de 9:00 a 17:00 hrs.",
                "latitud": 19.3589663,
                "longitud": -99.1838256
            },
            {
                "unidad": "Unidad de Evaluación de la Gestión y el Desempeño Gubernamental",
                "responsable": "Lic. Juan Carlos Hernández Durán",
                "coreo": "jhernandezd@funcionpublica.gob.mx",
                "direccion": "Miguel Laurent 235 Piso 2. Colonia Del Valle, Delegación Benito Juárez, D.F.",
                "telefono": "Tel.: +52 (55) 2000-3000 Exts. 2149, 2439.",
                "horario": "De lunes a viernes, en horario de 9:00 a 17:00 hrs.",
                "latitud": 19.3761701,
                "longitud": -99.1705656
            },
            {
                "unidad": "UCAOP - Unidad de Control y Auditoría a Obra Pública",
                "responsable": "Ing. José Alfredo Montero Rojas",
                "coreo": "jmonteror@funcionpublica.gob.mx",
                "direccion": "San Borja 1003, Colonia Del Valle, Delegación Benito Juárez, D.F.",
                "telefono": "Tel.: +52 (55) 2000-3000 Exts. 2310.",
                "horario": "De lunes a viernes, en horario de 9:00 a 17:00 hrs.",
                "latitud": 19.3811483,
                "longitud": -99.1687451
            },
            {
                "unidad": "Unidad de Normatividad de Contrataciones Públicas",
                "responsable": "Lic. Yazmin Ramírez Valades",
                "coreo": "normatadq@funcionpublica.gob.mx",
                "direccion": "Av Coyoacan 965, Delegación Coyoacan, D.F.",
                "telefono": "Tel.: +52 (55) 2000-3000 Exts. 2355",
                "horario": "De lunes a viernes, en horario de 9:00 a 17:00 hrs.",
                "latitud": 19.3811483,
                "longitud": -99.1687451
            },
            {
                "unidad": "Unidad de Política de Contrataciones Públicas",
                "responsable": "Lic. Rocio Martínez González",
                "coreo": "rmartinezg@funcionpublica.gob.mx",
                "direccion": "San Borja 204, Colonia Del Valle, Delegación Benito Juárez, D.F.",
                "telefono": "Tel.: +52 (55) 2000-3000 Exts. 2201.",
                "horario": "De lunes a viernes, en horario de 9:00 a 17:00 hrs.",
                "latitud": 19.3828992,
                "longitud": -99.1577606
            },
            {
                "unidad": "Unidad de Operación Regional y Contraloría Social",
                "responsable": "Lic. Dora Laura Martínez García",
                "coreo": "dlmartinez@funcionpublica.gob.mx",
                "direccion": "Av Universidad 150, Narvarte Poniente, D.F.",
                "telefono": "Tel.: +52 (55) 2000-3000 Exts. 3126.",
                "horario": "De lunes a viernes, en horario de 9:00 a 17:00 hrs.",
                "latitud": 19.3936704,
                "longitud": -99.1496894
            },
            {
                "unidad": "Unidad de Auditoría Gubernamental",
                "responsable": "Lic. María Azucena Salguero Lameda",
                "coreo": "msalguero@funcionpublica.gob.mx",
                "direccion": "Diagonal San Antonio 1021, Narvarte Poniente, D.F.",
                "telefono": "Tel.: +52 (55) 2000-3000 Exts. 3059.",
                "horario": "De lunes a viernes, en horario de 9:00 a 17:00 hrs.",
                "latitud": 19.3932746,
                "longitud": -99.160315
            }
        ];

    },
    obtenerOficinas: function () {
        return this.oficinas;
    }
};
window.OficinasManager.init();