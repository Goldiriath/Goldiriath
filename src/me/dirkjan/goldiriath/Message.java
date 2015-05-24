package me.dirkjan.goldiriath;

import net.pravian.bukkitlib.command.BukkitMessage;

public enum Message implements BukkitMessage {

    NO_MESSAGE(""),
    QUEST_GENERIC_REQUIREMENT("You don't have everything ready to start this quest yet."),
    QUEST_NOT_ENOUGH_HEALTH("You don't have enough health to start this quest."),
    QUEST_NOT_ENOUGH_MONEY("You don't have enough money to start this quest."),
    QUEST_NOT_ENOUGH_MANA("You don't have enough mana to start this quest.");
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
