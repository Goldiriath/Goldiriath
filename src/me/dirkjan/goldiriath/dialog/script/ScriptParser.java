package me.dirkjan.goldiriath.dialog.script;

import java.util.List;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.dialog.Dialog;
import me.dirkjan.goldiriath.quest.ParseException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ScriptParser {

    private final Dialog dialog;
    private final Logger logger;

    public ScriptParser(Dialog dialog) {
        this.dialog = dialog;
        this.logger = dialog.getHandler().getManager().getPlugin().getLogger();
    }

    public Script parse(List<String> unparsed) {
        final Script script = new Script(dialog);

        for (String scriptLine : unparsed) {
            if (scriptLine.isEmpty()) {
                continue;
            }

            final String[] parts = scriptLine.split(" ");

            int delay = 0;
            try {
                delay = Integer.parseInt(parts[0]);
            } catch (NumberFormatException ex) {
                logger.warning("[" + dialog.getId() + "] Skipping script line: '" + scriptLine + "'. Invalid delay!");
                continue;
            }

            ScriptItem si = null;
            final String[] args = (String[]) ArrayUtils.subarray(parts, 1, parts.length); // Shift front

            try {
                switch (parts[1]) {

                    case "out":
                        si = new OutScript(script, args);
                        break;

                    case "note":
                        si = new NoteScript(script, args);
                        break;

                    case "zap":
                        final Dialog newDialog = dialog.getHandler().getDialogsMap().get(args[1].toLowerCase());
                        if (newDialog == null) {
                            logger.warning("[" + dialog.getId() + "] Skipping script line: '" + scriptLine + "'. Could not find command! (Is it supported?)");
                            continue;
                        }

                        si = new ZapScript(script, newDialog);
                        break;

                    // TODO: rest of the script items
                    default:
                        logger.warning("[" + dialog.getId() + "] Skipping script line: '" + scriptLine + "'. Could not find command! (Is it supported?)");
                }
            } catch (Exception ex) {
                final ParseException pex = (ex instanceof ParseException ? (ParseException) ex : new ParseException(ex.getMessage(), ex));
                logger.warning("[" + dialog.getId() + "] Skipping script line: " + scriptLine + ". Could not be parsed!");
                logger.severe(ExceptionUtils.getFullStackTrace(pex));
            }

            if (si == null) {
                continue;
            }

            // Set script item delay
            si.setDelay(delay);

            // Add script item
            script.add(si);
        }

        return script;
    }

}
