package com.blackrook.db.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Placing this annotation on public fields or getter/setter methods on POJOs
 * hints at this field being a primary key of a record of some kind.
 * The convention is: Not included in INSERT, used on DELETE and UPDATE. 
 * @author Matthew Tropiano
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DBPrimaryKey
{

}
