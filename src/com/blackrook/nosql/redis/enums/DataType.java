/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.enums;

/**
 * Enumeration of Redis data types.
 * @author Matthew Tropiano
 */
public enum DataType
{
	/** No type. */
	NONE,
	/** String type. */
	STRING,
	/** List type. */
	LIST,
	/** Set type. */
	SET,
	/** Sorted set type. */
	ZSET,
	/** Hash type. */
	HASH;
}
