package net.goldiriath.plugin.quest.trigger;

public interface Triggerable<T> {

    public void onTrigger(PlayerEventTrigger trigger, T triggerer); // Not clean, but it must

}
