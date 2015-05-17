package me.dirkjan.goldiriath.quest.trigger;

import java.util.List;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ParseException;
import org.apache.commons.lang.exception.ExceptionUtils;

public class TriggerParser {

    private final Goldiriath plugin;
    private final Logger logger;
    private final String id;

    public TriggerParser(Goldiriath plugin, String id) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.id = id;
    }

    public TriggerList parse(List<String> unparsed) {
        final TriggerList trigs = new TriggerList();

        for (String reqLine : unparsed) {

            if (reqLine.isEmpty()) {
                continue;
            }

            final String[] args = reqLine.split(" ");

            try {
                PlayerEventTrigger trig = null;
                switch (args[0]) {

                    case "location":
                        trig = new LocationTrigger(plugin, args);
                        break;

                    case "obtain":
                        trig = new ObtainTrigger(plugin, args);
                        break;

                    // TODO: Implement NPC trigger
                    default:
                        logger.warning("[" + id + "] Skipping requirement: " + reqLine + ". Could not be found! (Is it supported?)");
                }

                if (trig != null) {
                    trigs.add(trig);
                }

            } catch (Exception ex) {
                final ParseException rex = (ex instanceof ParseException ? (ParseException) ex : new ParseException(ex.getMessage(), ex));
                logger.warning("[" + id + "] Skipping trigger: " + reqLine + ". Could not be parsed!");
                logger.severe(ExceptionUtils.getFullStackTrace(rex));
            }

        }

        return trigs;
    }

}
