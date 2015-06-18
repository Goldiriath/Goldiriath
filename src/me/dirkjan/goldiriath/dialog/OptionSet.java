package me.dirkjan.goldiriath.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.dirkjan.goldiriath.Goldiriath;
import me.dirkjan.goldiriath.quest.action.ActionList;
import me.dirkjan.goldiriath.quest.action.ActionParser;
import me.dirkjan.goldiriath.util.ConfigLoadable;
import me.dirkjan.goldiriath.util.Validatable;
import net.pravian.bukkitlib.util.ChatUtils;
import org.bukkit.configuration.ConfigurationSection;

public class OptionSet implements ConfigLoadable {

    private final DialogContainer dialog;
    private final String id;
    private final List<Option> options = new ArrayList<>();

    public OptionSet(DialogContainer dialog, String id) {
        this.dialog = dialog;
        this.id = id;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        options.clear();
        for (String optionId : config.getKeys(false)) {

            final Option opt = new Option(optionId);

            opt.setDisplay(ChatUtils.colorize(config.getString("display", optionId)));
            opt
                    .getActions()
                    .addAll(new ActionParser(Goldiriath.plugin, id) // TODO: Fix 'zap' parsing
                            .parse(config.getStringList("actions")));

            options.add(opt);
        }
    }

    public String getId() {
        return id;
    }

    public DialogContainer getDialog() {
        return dialog;
    }

    public List<Option> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public class Option implements Validatable {

        private final String id;
        //
        private String display;
        private final ActionList actions = new ActionList();

        public Option(String id) {
            this.id = id;
            this.display = id;
        }

        public String getId() {
            return id;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        @SuppressWarnings("ReturnOfCollectionOrArrayField")
        public ActionList getActions() {
            return actions;
        }

        @Override
        public boolean isValid() {
            return id != null
                    && display != null
                    && !display.isEmpty()
                    && actions != null;
        }

    }

}
