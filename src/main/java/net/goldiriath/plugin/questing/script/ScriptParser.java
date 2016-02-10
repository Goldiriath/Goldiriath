package net.goldiriath.plugin.questing.script;

import java.util.List;
import java.util.logging.Logger;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.questing.script.item.FlagScript;
import net.goldiriath.plugin.questing.script.item.ItemScript;
import net.goldiriath.plugin.questing.script.item.MoneyScript;
import net.goldiriath.plugin.questing.script.item.NoteScript;
import net.goldiriath.plugin.questing.script.item.OptionScript;
import net.goldiriath.plugin.questing.script.item.OutScript;
import net.goldiriath.plugin.questing.script.item.PotionScript;
import net.goldiriath.plugin.questing.script.item.QuestScript;
import net.goldiriath.plugin.questing.script.item.ScriptItem;
import net.goldiriath.plugin.questing.script.item.SkillpointScript;
import net.goldiriath.plugin.questing.script.item.XpScript;
import net.goldiriath.plugin.questing.script.item.ZapScript;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ScriptParser {

    private final Goldiriath plugin;
    private final Logger logger;
    private final ScriptContext context;

    public ScriptParser(Goldiriath plugin, ScriptContext context) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.context = context;
    }

    public Script parse(List<String> unparsed) {
        final Script script = new Script(plugin, context);

        for (String scriptLine : unparsed) {
            if (scriptLine.isEmpty()) {
                continue;
            }

            final String[] parts = scriptLine.split(" ");

            int delay = 0;
            try {
                delay = Integer.parseInt(parts[0]);
            } catch (NumberFormatException ex) {
                logger.warning("[" + context.getId() + "] Skipping script line: '" + scriptLine + "'. Invalid delay!");
                continue;
            }

            ScriptItem si = null;
            final String[] args = (String[]) ArrayUtils.subarray(parts, 1, parts.length); // Shift front

            try {
                switch (parts[1]) {
                    case "flag":
                        si = new FlagScript(script, args);
                        break;

                    case "item":
                        si = new ItemScript(script, args);
                        break;

                    case "money":
                        si = new MoneyScript(script, args);
                        break;

                    case "note":
                        si = new NoteScript(script, args);
                        break;

                    case "option":
                        si = new OptionScript(script, args);
                        break;

                    case "out":
                        si = new OutScript(script, args);
                        break;

                    case "potion":
                        si = new PotionScript(script, args);
                        break;

                    case "quest":
                        si = new QuestScript(script, args);
                        break;

                    case "skillpoint":
                        si = new SkillpointScript(script, args);
                        break;

                    case "xp":
                        si = new XpScript(script, args);
                        break;

                    case "zap":
                        si = new ZapScript(script, args);
                        break;

                    default:
                        logger.warning("[" + context.getId() + "] Skipping script line: '" + scriptLine + "'. Could not find command! (Is it supported?)");
                }
            } catch (Exception ex) {
                ParseException pex = (ex instanceof ParseException ? (ParseException) ex : null);
                if (pex == null) {
                    pex = new ParseException(ex);
                }
                logger.warning("[" + context.getId() + "] Skipping script line: " + scriptLine + ". " + pex.getMessage());
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
