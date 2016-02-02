package net.goldiriath.plugin.util.persist;

import net.goldiriath.plugin.util.persist.delegate.DefaultConfigDelegate;
import net.goldiriath.plugin.util.persist.delegate.ConfigDelegate;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DelegatePersistence {

    public Class<? extends ConfigDelegate<?>> value() default DefaultConfigDelegate.class;

}
