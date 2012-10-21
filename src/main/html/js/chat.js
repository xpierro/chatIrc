/*
 * Classe principale de la fenetre de chat
 * Cette classe devrait posséder certaines fonctions permettant de mettre à jour la fenetre de chat
 * Idées:
 *   - Des tabs pour les différents canaux
 *   - Des fonctions de mises à jour du texte des textArea et des listes de pseudos
 *   - Des fonctions pour communiquer avec l'applet et lui signaler les entrées utilisateur
 */
var MainWindow = function() {
    var mainWindow = null;
    if (typeof MainWindow.initialized == "undefined") {
        MainWindow.initialized = true;

        MainWindow.prototype.init = function() {
            mainWindow = new Ext.Window({
                id: 'mainWindow',
                title: 'Yet Anoter IRC Client - JS',
                width: 600,
                height: 480,
                x: 0,
                y: 0,
                layout: 'fit',
                items: [
                    {
                        id: 'mainText',
                        xtype: 'panel'
                    }
                ]
            });
            mainWindow.show();
        };
        this.init();


        MainWindow.prototype.appendText = function(text) {
            $('#mainText').html("<div>" + text + "</div>");
        };
    }
};

var fenetreIrc = new Ext.Window({
	id: 'chatIrcWindow',
	title: 'Fantasme IRC Client',
	width: 600,
	height: 480,
    x: 250,
    y: 250,
	layout: 'fit',
	items: [
	    {
		id: 'box',
		xtype : 'box',
		allowDomMove: false,
		autoEl: {
		    tag: 'object',
		    type: "application/x-java-applet",
		    id: 'ircApplet',
		    classid: 'java:org.fantasme.chatirc.view.IRCApplet.class',
		    code: 'java:org.fantasme.chatirc.view.IRCApplet.class',
		    archive: '/testJS/chatIrc.jar',
		    classloader_cache: false,
		    // Permet d'appeler des méthodes de l'applet à partir de javascript.
		    mayscript: true,
		    scriptable: true,
		    cn: [
		        {tag: 'param', name: 'type', value: 'application/x-java-applet;version=1.6'},
		        {tag: 'param', name: 'code', value: 'org.fantasme.chatirc.view.IRCApplet.class'},
		        {tag: 'param', name: 'codebase', value: '.'},
		        {tag: 'param', name: 'nickname', value: 'xwolfi'},
		        {tag: 'param', name: 'ident', value: 'fanta'},
		        {tag: 'param', name: 'classloader_cache', value: 'false'}
		    ]
		}
	    }
	]
});

var mainWindow2 = null;

var testAppend = function(text) {
    mainWindow2.appendText(text);
};

Ext.onReady(function() {
    mainWindow2 = new MainWindow();
	fenetreIrc.show();
    Ext.get("ircApplet").setVisible(true);

    fenetreIrc.on('close', function() {
        // Ferme l'applet (ne peut pas fermer la JVM)
        document.ircApplet.jsQuit();
    });

});


