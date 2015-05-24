package me.dirkjan.goldiriath.quest.requirement;

import java.util.List;
import java.util.logging.Logger;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.ParseException;
import org.apache.commons.lang.exception.ExceptionUtils;

public class RequirementParser {

    private final Goldiriath plugin;
    private final Logger logger;
    private final String id;

    public RequirementParser(Goldiriath plugin, String id) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.id = id;
    }

    public RequirementList parse(List<String> unparsed) {
        final RequirementList reqs = new RequirementList();

        for (String reqLine : unparsed) {

            if (reqLine.isEmpty()) {
                continue;
            }

            final String[] args = reqLine.split(" ");

            // Inverted?
            boolean inverted = args[0].startsWith("-");
            if (inverted) {
                args[0] = args[0].substring(1);
            }

            try {
                Requirement req = null;
                switch (args[0]) {

                    case "global":
                        req = plugin.qm.getGlobalRequirements().get(args[1]);
                        if (req == null) {
                            logger.warning("[\" + id + \"] Skipping global requirement: " + args[1] + ". Could not find global!");
                            continue;
                        }
                        break;

                    case "money":
                        req = new MoneyRequirement(plugin, args);
                        break;

                    case "health":
                        req = new HealthRequirement(plugin, args);
                        break;

                    case "mana":
                        req = new ManaRequirement(plugin, args);
                        break;

                    case "flag":
                        req = new FlagRequirement(plugin, args);
                        break;

                    // TODO: Implement the rest of the requirements: https://github.com/Goldiriath/Goldiriath/issues/1
                    default:
                        logger.warning("[" + id + "] Skipping requirement: '" + reqLine + "'. Could not be found! (Is it supported?)");
                }

                // Inverted?
                if (req != null) {
                    if (inverted) {
                        req = new InvertedRequirement(req);
                    }

                    reqs.add(req);
                }

            } catch (Exception ex) {
                final ParseException pex = (ex instanceof ParseException ? (ParseException) ex : new ParseException(ex.getMessage(), ex));
                logger.warning("[" + id + "] Skipping requirement: " + reqLine + ". Could not be parsed!");
                logger.severe(ExceptionUtils.getFullStackTrace(pex));
            }

        }

        return reqs;
    }

}
