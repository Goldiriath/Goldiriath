/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.goldiriath.plugin.questing.script;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 757127466L;

    public ParseException(Throwable cause) {
        super(getMessage(cause), cause);
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    private static String getMessage(Throwable t) {
        return t.getMessage() == null ? "Script line could not be parsed!" : t.getMessage();
    }

}
