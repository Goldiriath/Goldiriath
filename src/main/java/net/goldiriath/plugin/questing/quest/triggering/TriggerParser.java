package net.goldiriath.plugin.questing.quest.triggering;

import java.util.List;
import java.util.logging.Logger;
import net.goldiriath.plugin.Goldiriath;
import net.goldiriath.plugin.questing.script.ParseException;
import net.goldiriath.plugin.questing.quest.triggering.trigger.AbstractTrigger;
import net.goldiriath.plugin.questing.quest.triggering.trigger.LocationTrigger;
import net.goldiriath.plugin.questing.quest.triggering.trigger.ObtainTrigger;
import org.apache.commons.lang.exception.ExceptionUtils;

public class TriggerParser {

    private final Goldiriath plugin;
    private final Logger logger;
    private final TriggerSource source;
    private final String id;

    public TriggerParser(Goldiriath plugin, TriggerSource source) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.source = source;
        this.id = source.getId();
    }

    public TriggerList parse(List<String> unparsed) {
        final TriggerList trigs = new TriggerList();

        for (String reqLine : unparsed) {

            if (reqLine.isEmpty()) {
                continue;
            }

            final String[] args = reqLine.split(" ");

            try {
                AbstractTrigger trig = null;
                switch (args[0]) {

                    case "location":
                        trig = new LocationTrigger(source, args);
                        break;

                    case "obtain":
                        trig = new ObtainTrigger(source, args);
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
