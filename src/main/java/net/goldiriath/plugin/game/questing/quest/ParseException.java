package net.goldiriath.plugin.game.questing.quest;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = 2222244414032L;

    public ParseException(String msg) {
        super(msg);
    }

    public ParseException(String msg, Exception ex) {
        super(msg, ex);
    }

}
