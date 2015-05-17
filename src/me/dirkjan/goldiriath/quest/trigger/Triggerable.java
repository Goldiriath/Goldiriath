package me.dirkjan.goldiriath.quest.trigger;

public interface Triggerable<T> {

    public void onTrigger(PlayerEventTrigger trigger, T triggerer); // Not clean, but it must

}
