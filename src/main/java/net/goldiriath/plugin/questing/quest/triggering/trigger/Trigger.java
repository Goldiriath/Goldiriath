package net.goldiriath.plugin.questing.quest.triggering.trigger;

import net.goldiriath.plugin.questing.quest.triggering.TriggerSource;
import net.goldiriath.plugin.questing.quest.triggering.Triggerable;

public interface Trigger<T> {

    public TriggerSource getSource();

    public void setTriggered(Triggerable<T> trigger);

    public Triggerable<T> getTriggered();

    public void trigger(T triggerer);

}
