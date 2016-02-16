package net.goldiriath.plugin.chat;

import org.bukkit.ChatColor;

public enum ChatChannel {

    LOCAL(ChatColor.RESET),
    PARTY(ChatColor.GREEN);
    //
    private final String channel;
    private final ChatColor color;

    private ChatChannel(ChatColor color) {
        this.channel = name().toLowerCase();
        this.color = color;
    }

    public String getPrefix() {
        return "[" + color + channel + "]";
    }

    public static ChatChannel fromString(String string) {
        return ChatChannel.valueOf(string.toUpperCase());
    }

}
