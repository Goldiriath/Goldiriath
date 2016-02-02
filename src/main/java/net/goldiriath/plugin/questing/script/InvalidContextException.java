/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.goldiriath.plugin.questing.script;

public class InvalidContextException extends ParseException {

    private static final long serialVersionUID = 6677727773L;

    public InvalidContextException(ScriptContext.ScriptContextType type) {
        super("Invalid script context: " + type.name() + "!");
    }

}
