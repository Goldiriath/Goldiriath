package net.goldiriath.plugin.questing.dialog;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;
import net.goldiriath.plugin.Goldiriath;

public class DialogFileFilter implements FileFilter {

    private final Logger logger;

    public DialogFileFilter(Goldiriath plugin) {
        this.logger = plugin.getLogger();
    }

    @Override
    public boolean accept(File pathname) {
        final boolean accept
                = pathname.isFile()
                && pathname.getName().startsWith("dialog_")
                && pathname.getName().endsWith(".yml")
                && pathname.getName().equals(pathname.getName().toLowerCase());

        if (!accept) {
            logger.warning("Ignoring file in dialog folder: " + pathname.getAbsolutePath() + ". Invalid filename format!");
        }

        return accept;
    }

}
