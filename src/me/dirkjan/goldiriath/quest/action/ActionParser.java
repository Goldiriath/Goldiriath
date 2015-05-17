package me.dirkjan.goldiriath.quest.action;

import java.util.List;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ParseException;
import me.dirkjan.goldiriath.quest.Quest;
import me.dirkjan.goldiriath.util.SafeArrayList;
import org.apache.commons.lang.exception.ExceptionUtils;

public class ActionParser extends SafeArrayList<Action> {

    private static final long serialVersionUID = 355222119421L;

    private final Goldiriath plugin;
    private final Logger logger;
    private final String id;
    //
    private Quest quest;

    public ActionParser(Quest quest) {
        this(quest.getManager().getPlugin(), quest.getId());
        this.quest = quest;
    }

    public ActionParser(Goldiriath plugin, String id) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.id = id;
    }

    public ActionList parse(List<String> unparsed) {
        final ActionList acs = new ActionList();

        for (String acLine : unparsed) {

            if (acLine.isEmpty()) {
                continue;
            }

            final String[] args = acLine.split(" ");

            try {
                Action ac = null;
                switch (args[0]) {

                    case "global":
                        ac = plugin.qm.getGlobalActions().get(args[1]);
                        if (ac == null) {
                            logger.warning("[\" + id + \"] Skipping global action: " + args[1] + ". Could not find global!");
                            continue;
                        }
                        break;

                    case "command":
                        ac = new CommandAction(plugin, args);
                        break;

                    case "money":
                        ac = new MoneyAction(plugin, args);
                        break;

                    case "zap":
                        if (quest == null) {
                            logger.warning("[" + id + "] Skipping action: " + acLine + ". 'zap' can only be used in quest stages!");
                            break;
                        }
                        ac = new ZapAction(quest, args);

                    // TODO: Implement the rest of the actions: https://github.com/Goldiriath/Goldiriath/issues/1
                    default:
                        logger.warning("[" + id + "] Skipping action: " + acLine + ". Could not be found! (Is it supported?)");
                }

                if (ac != null) {
                    acs.add(ac);
                }

            } catch (Exception ex) {
                final ParseException pex = (ex instanceof ParseException ? (ParseException) ex : new ParseException(ex.getMessage(), ex));
                logger.warning("[" + id + "] Skipping action: " + acLine + ". Could not be parsed!");
                logger.severe(ExceptionUtils.getFullStackTrace(pex));
            }
        }

        return acs;
    }

}
