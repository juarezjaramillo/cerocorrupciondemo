window.QueHacerManager = {
    init: function () {
        //Datos Hardcodeadas
        this.quehacer = {
            'soborno': {
                "titulo": "Soborno",
                "paginas": [
                    "Cuando un ciudadano que realiza tr&aacute;mites da dinero a un empleado p&uacute;blico por un servicio m&aacute;s r&aacute;pido o una compa&ntilde;&iacute;a constructora da d&aacute;divas a un funcionario para conceder un contrato, ¡Es un soborno!<br/><br/>Si eres testigo de este tipo de conductas ¡No dudes en denunciar!.<br/><br />Usa &eacute;sta aplicaci&oacute;n para reportarlo o dir&iacute;gete a la oficina m&aacute;s cercana a presentar tu denuncia.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia.",
                    "Este es un prototipo, el contenido de este elemento debera ser validado por la dependencia."]
            },
            'extorsion': {
                "titulo": "Extorsi&oacute;n",
                "paginas": ["<strong><i>Contenido Prototipo obtenido de Wikipedia y con uso de carácter méramente ilustrativo</i></strong><br /><br />La extorsi&oacute;n es un hecho punible consistente en obligar a una persona, a través de la utilización de violencia o intimidación, a realizar u omitir un [[acto jurídico o negocio jurídico con ánimo de lucro y con la intención de producir un perjuicio de carácter patrimonial o bien del sujeto pasivo",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia."]
            },
            'trafico': {
                "titulo": "Tr&aacute;fico de Influencias",
                "paginas": ["<strong><i>Contenido Prototipo obtenido de Wikipedia y con uso de carácter méramente ilustrativo</i></strong><br /><br />El tráfico de influencias es una práctica ilegal, o al menos moralmente objetable, consistente en utilizar la influencia personal en ámbitos de gobierno o incluso empresariales, a través de conexiones con personas, y con el fin de obtener favores o tratamiento preferencial. Naturalmente se buscan conexiones con amistades o conocidos para tener información, y con personas que ejerzan autoridad o que tengan poder de decisión, y a menudo esto ocurre a cambio de un pago en dinero o en especie, u otorgando algún tipo de privilegio. No obstante, la naturaleza ilegal del tráfico de influencias es relativa: la OECD ha utilizado a menudo la expresión 'tráfico indebido de influencias' para referirse a actos ilegales o cuestionables de lobbying.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia."]
            },
            'fraude': {
                "titulo": "Fraude",
                "paginas": ["<strong><i>Contenido Prototipo obtenido de Wikipedia y con uso de carácter méramente ilustrativo</i></strong><br /><br />El fraude es la acción contraria a la verdad y a la rectitud o ley -fraude de ley-, que perjudica a la persona contra quien se comete.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia."]
            },
            'nepotismo': {
                "titulo": "Nepotismo",
                "paginas": ["<strong><i>Contenido Prototipo obtenido de Wikipedia y con uso de carácter méramente ilustrativo</i></strong><br /><br />El nepotismo es la preferencia que tienen funcionarios públicos para dar empleos a familiares o amigos, sin importar el mérito para ocupar el cargo, sino su lealtad o alianza.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia.",
                    "Este es un prototipo, el contenido de este elemento deberá ser validado por la dependencia."]
            },
        };

    },
    obtenerItem: function (nombreItem) {
        return this.quehacer[nombreItem];
    }
};
window.QueHacerManager.init();