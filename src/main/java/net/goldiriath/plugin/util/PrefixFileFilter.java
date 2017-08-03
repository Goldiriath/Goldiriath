package net.goldiriath.plugin.util;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;
import net.goldiriath.plugin.Goldiriath;

public class PrefixFileFilter implements FileFilter {

    private final Logger logger;
    private final String prefix;

    public PrefixFileFilter(Goldiriath plugin, String prefix) {
        this.logger = plugin.getLogger();
        this.prefix = prefix;
    }

    @Override
    public boolean accept(File pathname) {
        final boolean accept
                = pathname.isFile()
                && pathname.getName().startsWith(prefix + "_")
                && pathname.getName().endsWith(".yml")
                && pathname.getName().equals(pathname.getName().toLowerCase());

        if (!accept) {
            logger.warning("Ignoring file: " + pathname.getAbsolutePath() + ". Invalid filename format!");
        }

        return accept;
    }

}
