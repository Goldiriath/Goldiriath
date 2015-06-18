package me.dirkjan.goldiriath.dialog.script;

import java.util.List;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.dialog.Dialog;
import org.apache.commons.lang.ArrayUtils;

public class ScriptParser {

    private final Dialog dialog;
    private final Logger logger;

    public ScriptParser(Dialog dialog) {
        this.dialog = dialog;
        this.logger = dialog.getDialogContainer().getManager().getPlugin().getLogger();
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

            switch (parts[1]) {
                case "out":
                    si = new OutScript(script, args);
                    break;

                // TODO: rest of the script items
            }

            if (si == null) {
                logger.warning("[" + dialog.getId() + "] Skipping script line: '" + scriptLine + "'. Unknown script command '" + parts[1] + "'");
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
