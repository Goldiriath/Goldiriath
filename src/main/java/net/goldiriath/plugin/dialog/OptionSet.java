package net.goldiriath.plugin.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.goldiriath.plugin.quest.ParseException;
import net.goldiriath.plugin.util.ConfigLoadable;
import net.goldiriath.plugin.util.Validatable;
import net.pravian.bukkitlib.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import thirdparty.mkremlins.fanciful.FancyMessage;

public class OptionSet implements ConfigLoadable {

    private final NPCDialogHandler handler;
    private final String id;
    //
    private final List<Option> options = new ArrayList<>();
    private FancyMessage message;

    public OptionSet(NPCDialogHandler handler, String id) {
        this.handler = handler;
        this.id = id;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        options.clear();
        for (String optionId : config.getKeys(false)) {
            optionId = optionId.toLowerCase();

            final String dialogString = config.getString(optionId + ".dialog", "");
            final Dialog dialog = handler.getDialogsMap().get(dialogString);

            if (dialog == null) {
                throw new ParseException("Could not find dialog: '" + dialogString + "'");
            }

            final Option opt = new Option(optionId, dialog);
            opt.setDisplay(ChatUtils.colorize(config.getString(optionId + ".display", optionId)));
            options.add(opt);
        }

        this.message = new FancyMessage()
                .color(ChatColor.YELLOW)
                .text("Choice")
                .then(": ");

        for (Option option : options) {
            message
                    .then(option.getDisplay())
                    .style(ChatColor.ITALIC)
                    .command("/option " + option.getDialog().getHandler().getId() + " " + this.getId() + " " + option.getId() + " ")
                    .then(" | ");
        }

        // Reset last ", "
        message.text("");
    }

    public String getId() {
        return id;
    }

    public NPCDialogHandler getDialog() {
        return handler;
    }

    public List<Option> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public FancyMessage getMessage() {
        return message;
    }

    public class Option implements Validatable {

        private final String id;
        private final Dialog dialog;
        //
        private String display;

        public Option(String id, Dialog dialog) {
            this.id = id;
            this.display = id;
            this.dialog = dialog;
        }

        public String getId() {
            return id;
        }

        public Dialog getDialog() {
            return dialog;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        @Override
        public boolean isValid() {
            return id != null
                    && display != null
                    && !display.isEmpty()
                    && dialog != null;
        }

    }

}
