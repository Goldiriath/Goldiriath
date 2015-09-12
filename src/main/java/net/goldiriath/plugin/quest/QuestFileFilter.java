package net.goldiriath.plugin.quest;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;
import net.goldiriath.plugin.Goldiriath;

public class QuestFileFilter implements FileFilter {

    private final Logger logger;

    public QuestFileFilter(Goldiriath plugin) {
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean accept(File pathname) {
        final boolean accept
                = pathname.isFile()
                && pathname.getName().startsWith("quest_")
                && pathname.getName().endsWith(".yml")
                && pathname.getName().equals(pathname.getName().toLowerCase());

        if (!accept && !pathname.getName().equals("global.yml")) {
            logger.warning("Ignoring file in quest folder: " + pathname.getAbsolutePath() + ". Invalid filename format!");
        }

        return accept;
    }

}
