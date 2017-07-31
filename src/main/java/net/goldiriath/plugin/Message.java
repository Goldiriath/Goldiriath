package net.goldiriath.plugin;

import net.pravian.aero.base.PluginMessage;
import org.bukkit.ChatColor;

public enum Message implements PluginMessage {

    NO_MESSAGE(""),
    QUEST_GENERIC_REQUIREMENT("You don't have everything ready to start this quest yet."),
    QUEST_NOT_ENOUGH_HEALTH("You don't have enough health to start this quest."),
    QUEST_NOT_ENOUGH_MONEY("You don't have enough money to start this quest."),
    QUEST_NOT_ENOUGH_MANA("You don't have enough mana to start this quest."),
    QUEST_SKILL_NOT_OWNED("You don't have the right skills to start this quest."),
    QUEST_QUEST_NOT_DONE("You haven't done the required quests"),
    QUEST_QUEST_NOT_STARTED("You haven't started the required quests"),
    QUEST_LEVEL_TO_LOW("Your level is not high enough to atempt this quest"),
    QUEST_NEED_ITEMS("You don't have the required items"),
    //
    COMMAND_PLAYER_NOT_FOUND(ChatColor.RED + "Player not found!"),
    COMMAND_SKILL_NOT_FOUND(ChatColor.RED + "Skill not found");
    //
    private final String message;

    private Message(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
