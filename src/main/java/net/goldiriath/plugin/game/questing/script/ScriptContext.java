package net.goldiriath.plugin.game.questing.script;

import lombok.Getter;
import net.goldiriath.plugin.game.questing.dialog.Dialog;
import net.goldiriath.plugin.game.questing.quest.Quest;

public class ScriptContext {

    @Getter
    private final ScriptContextType type;
    @Getter
    private Dialog dialog;

    @Getter
    private Quest quest;

    private ScriptContext(ScriptContextType type) {
        this.type = type;
    }

    public ScriptContext() {
        this(ScriptContextType.NONE);
    }

    public ScriptContext(Dialog dialog) {
        this(ScriptContextType.DIALOG);
        this.dialog = dialog;
    }

    public ScriptContext(Quest quest) {
        this(ScriptContextType.QUEST);
        this.quest = quest;
    }

    public String getId() {
        switch (type) {
            case DIALOG:
                return dialog.getId();
            case QUEST:
                return quest.getId();
            default:
                return "";
        }
    }

    public static enum ScriptContextType {

        NONE,
        DIALOG,
        QUEST;
    }

}
