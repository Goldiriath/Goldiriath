package me.dirkjan.goldiriath.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.dirkjan.goldiriath.quest.ParseException;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.Validatable;
import thirdparty.mkremlins.fanciful.FancyMessage;
import net.pravian.bukkitlib.util.ChatUtils;
import static org.bukkit.ChatColor.*;
import org.bukkit.configuration.ConfigurationSection;

public class OptionSet implements ConfigLoadable {

    private final NPCDialogHandler handler;
    private final String id;
    private final List<Option> options = new ArrayList<>();

    public OptionSet(NPCDialogHandler handler, String id) {
        this.handler = handler;
        this.id = id;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        options.clear();
        for (String optionId : config.getKeys(false)) {
            optionId = optionId.toLowerCase();

            final String dialogString = config.getString("dialog", "");
            final Dialog dialog = handler.getDialogsMap().get(dialogString);

            if (dialog == null) {
                throw new ParseException("Could not find dialog: '" + dialogString + "'");
            }

            final Option opt = new Option(optionId, dialog);
            opt.setDisplay(ChatUtils.colorize(config.getString("display", optionId)));
            options.add(opt);
        }
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
        final FancyMessage message = new FancyMessage()
                .style(YELLOW)
                .text("Choice")
                .then(":");

        for (Option option : options) {
            message
                    .then(option.getDisplay())
                    .style(ITALIC)
                    .command("/option " + option.getDialog().getHandler().getId() + " " + this.getId() + " " + option.getId() + " ")
                    .then(", ");
        }

        // Reset last ", "
        message.text("");
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
