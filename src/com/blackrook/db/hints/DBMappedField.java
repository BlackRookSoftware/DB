/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.db.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Placing this annotation on public fields or getter/setter methods on POJOs
 * hints that this field is mapped to a different collection's field in some way.
 * @author Matthew Tropiano
 * @since 2.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DBMappedField
{
	/**
	 * The field on this object that serves as the source key.
	 */
	String key() default "";
	
	/**
	 * The destination table in which to look up the matching key value.
	 */
	String table() default "";
	
	/**
	 * The destination table key to match the value of the source key to.
	 * If not specified, this uses the source key name.
	 */
	String tableKey() default "";
	
}
