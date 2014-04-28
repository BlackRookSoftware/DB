/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.data;

/**
 * A cursor encapsulation for the result of a SCAN call.
 * @author Matthew Tropiano
 */
public final class RedisCursor
{
	/** Cursor value. */
	private long cursor;
	/** Key collection. */
	private String[] keys;
	
	private RedisCursor(long cursor, String[] keys)
	{
		this.cursor = cursor;
		this.keys = keys;
	}
	
	/**
	 * Creates a new scan cursor with a set of keys.
	 * @param cursor the cursor value to use for the next call.
	 * @param keys the keys to use for this cursor.
	 * @return a new cursor.
	 */
	public static RedisCursor create(long cursor, String[] keys)
	{
		return new RedisCursor(cursor, keys);
	}
	
	/**
	 * Returns the value of the next cursor handle to use for retrieving
	 * later keys in the iteration.
	 */
	public long getCursor()
	{
		return cursor;
	}
	
	/**
	 * Returns the keys to iterate through in this cursor iteration.
	 * May be empty.
	 */
	public String[] getKeys()
	{
		return keys;
	}
	
	/**
	 * If true, this cursor should be used again in order to complete
	 * a full iteration. Equivalent to {@link #getCursor()}<code> != 0</code>.
	 */
	public boolean hasNext()
	{
		return getCursor() != 0L;
	}
	
}
