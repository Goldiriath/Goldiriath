package me.dirkjan.goldiriath.player.persist;

import me.dirkjan.goldiriath.player.persist.delegate.DefaultConfigDelegate;
import me.dirkjan.goldiriath.player.persist.delegate.ConfigDelegate;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DelegatePersistence {

    public Class<? extends ConfigDelegate<?>> value() default DefaultConfigDelegate.class;

}
