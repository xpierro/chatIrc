
var fenetreIrc = new Ext.Window({
	id: 'chatIrcWindow',
	title: 'Fantasme IRC Client',
	width: 800,
	height: 600,
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

Ext.onReady(function() {
	fenetreIrc.show();
         Ext.get("ircApplet").setVisible(true);

fenetreIrc.on('close', function() {
	// Ferme l'applet (ne peut pas fermer la JVM)
        document.ircApplet.jsQuit();
});

    //fenetreIrc.update(undefined);
});


