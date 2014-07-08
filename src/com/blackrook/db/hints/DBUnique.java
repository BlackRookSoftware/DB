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
 * hints at this field being unique in its collection in some way.
 * @author Matthew Tropiano
 * @since 2.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DBUnique
{
	/**
	 * Specifies the grouping name, if any, that this key is grouped with in a unique
	 * set. If not specified, then this is unique all on its own.
	 */
	String value() default "";
}
