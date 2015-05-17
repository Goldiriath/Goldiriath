package me.dirkjan.goldiriath.quest.trigger;

public interface TriggerCapturer<T, O> {

    public void trigger(T triggerer, O triggered);

}
