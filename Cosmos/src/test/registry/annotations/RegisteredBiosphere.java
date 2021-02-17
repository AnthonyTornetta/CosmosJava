package test.registry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Make this annotation accessible at runtime via reflection.
@Target({ElementType.TYPE})  // This annotation can only be applied to classes.
public @interface RegisteredBiosphere
{
	public String id();
}