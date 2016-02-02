package net.goldiriath.plugin.questing.quest.triggering;

public interface Triggerable<T> {

    public void onTrigger(T triggerer); // Not clean, but it must

}
