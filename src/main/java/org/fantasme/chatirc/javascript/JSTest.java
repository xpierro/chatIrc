package org.fantasme.chatirc.javascript;

import netscape.javascript.JSObject;
import org.fantasme.chatirc.view.IRCApplet;

/**
 * Test de communication Java->JS
 * @author PierreCollignon@TRINOV
 */
public class JSTest {
    public static void displayTestDiv() {
        JSObject jso = JSObject.getWindow(IRCApplet.getLastInstance());
        jso.eval("alert('ok')");
        jso.eval("$('#conteneur').append('<div>YOUHOUU<div>');");
    }
}
