package me.dirkjan.goldiriath.quest.trigger;

public interface Trigger<T> {

    public void setTriggered(Triggerable<T> trigger);

    public Triggerable<T> getTriggered();

    public void trigger(T triggerer);

}
