package org.fantasme.chatirc.javascript;

import netscape.javascript.JSObject;
import org.fantasme.chatirc.view.IRCApplet;

/**
 * Test de communication Java->JS
 * @author PierreCollignon@TRINOV
 */
public class JSTest {
    public static void displayTestDiv(String text) {
        JSObject jso = JSObject.getWindow(IRCApplet.getLastInstance());
        jso.eval("testAppend('" + text + "')");
        //jso.eval("alert('testAppend')");
    }
}
