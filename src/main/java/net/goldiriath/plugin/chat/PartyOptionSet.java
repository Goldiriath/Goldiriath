package net.goldiriath.plugin.chat;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import net.goldiriath.plugin.util.Validatable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import thirdparty.mkremlins.fanciful.FancyMessage;

public class PartyOptionSet {

    @Getter
    private final List<PartyOptionSet.Option> options = Lists.newArrayList();
    @Getter
    private final FancyMessage message;

    public PartyOptionSet(Player inviter) {
        options.add(new Option("accept", ChatColor.GREEN));
        options.add(new Option("deny", ChatColor.RED));
        message = new FancyMessage().text(inviter.getName() + " has invited you to his party ");
        for (Option option : options) {
            message
                    .then(option.getDisplay())
                    .color(option.getColor())
                    .command("/party " + option.display + " " + inviter.getName())
                    .then(" | ");
        }
    }

    public class Option implements Validatable {

        private final ChatColor color;
        //
        private String display;

        public Option(String id, ChatColor color) {
            this.display = id;
            this.color = color;
        }

        public ChatColor getColor() {
            return color;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        @Override
        public boolean isValid() {
            return display != null
                    && !display.isEmpty();
        }
    }

}
