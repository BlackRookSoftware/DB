/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.data;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.blackrook.commons.Common;

/**
 * An object, received or sent to the Redis Server.
 * This is an objectified form of RESP messages.
 * @author Matthew Tropiano
 */
public final class RedisObject
{
	/** All NULL string objects are this object. */
	public static final RedisObject NULL = new RedisObject(Type.STRING, null);
	/** All NULL array objects are this object. */
	public static final RedisObject NULL_ARRAY = new RedisObject(Type.ARRAY, null);
	
	private static final Pattern BULK_DETECT_PATTERN = Pattern.compile("(\\n\\r)+");
	
	/**
	 * Underlying object type.
	 */
	public static enum Type
	{
		INTEGER,
		STRING,
		ARRAY,
		ERROR;
	}
	
	/** Object type. */
	private Type type;
	/** Object value. */
	private Object value;
	
	/**
	 * Private constructor.
	 */
	private RedisObject(Type type, Object value)
	{
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Creates a null object (type STRING).
	 */
	public static RedisObject createNull()
	{
		return NULL;
	}

	/**
	 * Creates a null object (type ARRAY).
	 * This differs in the way it is sent.
	 */
	public static RedisObject createNullArray()
	{
		return NULL_ARRAY;
	}

	/**
	 * Creates an object of type ERROR.
	 * @param message the error message.
	 */
	public static RedisObject createError(String message)
	{
		return new RedisObject(Type.ERROR, message);
	}

	/**
	 * Creates an object of type INTEGER.
	 * @param value the integer value.
	 */
	public static RedisObject create(long value)
	{
		return new RedisObject(Type.INTEGER, value);
	}

	/**
	 * Creates an object of type STRING, using a floating-point number.
	 * Floating-point values in Redis are Strings, internally.
	 * @param value the double value.
	 */
	public static RedisObject create(double value)
	{
		return new RedisObject(Type.STRING, String.valueOf(value));
	}

	/**
	 * Creates an object of type STRING.
	 * @param value the String value.
	 */
	public static RedisObject create(String value)
	{
		if (value == null)
			return createNull();
		return new RedisObject(Type.STRING, value);
	}

	/**
	 * Creates an object of type ARRAY.
	 * @param value the String[] value.
	 */
	public static RedisObject create(String... value)
	{
		if (value == null)
			return createNullArray();
		
		RedisObject[] val = new RedisObject[value.length];
		for (int i = 0; i < value.length; i++)
			val[i] = create(value[i]);
		return new RedisObject(Type.ARRAY, val);
	}

	/**
	 * Creates an object of type ARRAY, but with all <code>null</code> elements.
	 * @param length the length of the array to create. If &lt; 0, this returns {@link #NULL_ARRAY}.
	 */
	public static RedisObject createEmptyArray(int length)
	{
		if (length < 0)
			return createNullArray();
		return new RedisObject(Type.ARRAY, new RedisObject[length]);
	}

	/**
	 * Gets the underlying type of this object.
	 */
	public Type getType()
	{
		return type;
	}

	/**
	 * Returns true if this represents an error.
	 * False, otherwise.
	 */
	public boolean isError()
	{
		return type == Type.ERROR;
	}

	/**
	 * Returns true if this is a NULL object, STRING or ARRAY typed.
	 * False, otherwise.
	 */
	public boolean isNull()
	{
		return value == null;
	}

	/**
	 * Returns true if this is an ARRAY-typed object.
	 * False, otherwise.
	 */
	public boolean isArray()
	{
		return type == Type.ARRAY;
	}

	/**
	 * If this is an ARRAY type, this returns its length in elements.
	 * Else, this returns its length in characters.
	 * Nulls return -1.
	 */
	public int length()
	{
		if (isNull())
			return -1;
		
		switch (type)
		{
			case INTEGER:
				return String.valueOf(value).length();
			case STRING:
			case ERROR:
				return ((String)value).length();
			case ARRAY:
				return ((RedisObject[])value).length;
			default:
				return -1;
		}
		
	}
	
	/**
	 * If this is an array, this returns an element at a specific index in it.
	 * @param index the array index to inspect.
	 * @throws IllegalStateException if this is NOT an array.
	 * @throws IndexOutOfBoundsException if the provided index is outside the bounds of this array.
	 */
	public RedisObject get(int index)
	{
		if (!isArray())
			throw new IllegalStateException("Object is not array type.");
		return ((RedisObject[])value)[index];
	}
	
	/**
	 * If this is an array, this sets an element at a specific index in it.
	 * @param index the array index to set.
	 * @param value the long value to add.
	 * @return the object created/set.
	 * @throws IllegalStateException if this is NOT an array.
	 * @throws IndexOutOfBoundsException if the provided index is outside the bounds of this array.
	 */
	public RedisObject set(int index, long value)
	{
		if (!isArray())
			throw new IllegalStateException("Object is not array type.");
		return (((RedisObject[])this.value)[index] = create(value));
	}
	
	/**
	 * If this is an array, this sets an element at a specific index in it.
	 * @param index the array index to set.
	 * @param value the floating-point value to add.
	 * @return the object created/set.
	 * @throws IllegalStateException if this is NOT an array.
	 * @throws IndexOutOfBoundsException if the provided index is outside the bounds of this array.
	 */
	public RedisObject set(int index, double value)
	{
		if (!isArray())
			throw new IllegalStateException("Object is not array type.");
		return (((RedisObject[])this.value)[index] = create(value));
	}
	
	/**
	 * If this is an array, this sets an element at a specific index in it.
	 * @param index the array index to set.
	 * @param value the string value to add.
	 * @return the object created/set.
	 * @throws IllegalStateException if this is NOT an array.
	 * @throws IndexOutOfBoundsException if the provided index is outside the bounds of this array.
	 */
	public RedisObject set(int index, String value)
	{
		if (!isArray())
			throw new IllegalStateException("Object is not array type.");
		return (((RedisObject[])this.value)[index] = create(value));
	}
	
	/**
	 * If this is an array, this sets an element at a specific index in it.
	 * @param index the array index to set.
	 * @param value the string array value to add.
	 * @return the object created/set.
	 * @throws IllegalStateException if this is NOT an array.
	 * @throws IndexOutOfBoundsException if the provided index is outside the bounds of this array.
	 */
	public RedisObject set(int index, String[] value)
	{
		if (!isArray())
			throw new IllegalStateException("Object is not array type.");
		return (((RedisObject[])this.value)[index] = create(value));
	}
	
	/**
	 * If this is an array, this sets an element at a specific index in it.
	 * @param index the array index to set.
	 * @param value the RedisObject to add.
	 * @return the object set.
	 * @throws IllegalStateException if this is NOT an array.
	 * @throws IndexOutOfBoundsException if the provided index is outside the bounds of this array.
	 */
	public RedisObject set(int index, RedisObject value)
	{
		if (!isArray())
			throw new IllegalStateException("Object is not array type.");
		return (((RedisObject[])this.value)[index] = value);
	}
	
	/**
	 * Returns this object as a long integer.
	 * Strings are cast to a long integer - <code>0L</code> if conversion is impossible.
	 * Arrays are cast to <code>0L</code>.
	 * Errors are cast to <code>0L</code>.
	 * Nulls are cast to <code>0L</code>. 
	 */
	public long asLong()
	{
		if (isNull())
			return 0L;
		
		switch (type)
		{
			case INTEGER:
				return (Long)value;
			case STRING:
				return Common.parseLong((String)value, 0L);
			case ARRAY:
			case ERROR:
			default:
				return 0L;
		}
	}
	
	/**
	 * Returns this object as a double.
	 * Integers are cast to a double.
	 * Strings are cast to a double - <code>0.0</code> if conversion is impossible.
	 * Arrays are cast to <code>0.0</code>.
	 * Errors are cast to <code>0.0</code>.
	 * Nulls are cast to <code>0.0</code>. 
	 */
	public double asDouble()
	{
		if (isNull())
			return 0.0;
		
		switch (type)
		{
			case INTEGER:
				return ((Long)value).doubleValue();
			case STRING:
				return Common.parseDouble((String)value, 0.0);
			case ARRAY:
			case ERROR:
			default:
				return 0.0;
		}
	}
	
	/**
	 * Returns this object as a String.
	 * Integers are cast to a String.
	 * Arrays are <code>null</code>.
	 * Errors are returned as their message.
	 * Nulls are <code>null</code>. 
	 * <p><b>NOTE:</b> Not to be confused with {@link #toString()}.</p>
	 */
	public String asString()
	{
		if (isNull())
			return null;
		
		switch (type)
		{
			case INTEGER:
				return String.valueOf(value);
			case STRING:
			case ERROR:
				return (String)value;
			case ARRAY:
			default:
				return null;
		}
	}

	/**
	 * Returns this object as a raw string, sent/received by/from Redis,
	 * replete with newline characters. The content returned by this
	 * method can be sent as-is to a Redis server.
	 * <p>Equivalent to <code>asRaw(false)</code>.</p>
	 */
	public String asRaw()
	{
		return asRaw(false);
	}
	
	/**
	 * Returns this object as a raw string, sent/received by/from Redis,
	 * replete with newline characters. The content returned by this
	 * method can be sent as-is to a Redis server.
	 * @param alwaysBulk if true, all strings are rendered as "bulk," binary-safe strings.
	 */
	public String asRaw(boolean alwaysBulk)
	{
		final String CRLF = "\r\n";
		
		switch (type)
		{
			case INTEGER:
				return ":" + String.valueOf(value) + CRLF;
			case STRING:
			{
				String v = (String)value;
				
				if (alwaysBulk || BULK_DETECT_PATTERN.matcher(v).find())
				{
					StringBuffer sb = new StringBuffer();
					sb.append('$').append(length()).append(CRLF);
					if (!isNull())
						sb.append(v).append(CRLF);
					return sb.toString();
				}
				else
				{
					return "+" + String.valueOf(value) + CRLF;
				}
			}
			
			case ERROR:
				return "-" + ((String)value) + CRLF;
			case ARRAY:
			{
				StringBuffer sb = new StringBuffer();
				sb.append('*').append(length()).append(CRLF);
				if (!isNull())
				{
					for (int i = 0; i < length(); i++)
						sb.append(get(i).asRaw(true));
				}
				return sb.toString();
			}
			default:
				return null;
		}
	}
	
	@Override
	public String toString()
	{
		if (isNull())
			return "null";
		
		switch (type)
		{
			case INTEGER:
				return String.valueOf(value);
			case STRING:
				return (String)value;
			case ERROR:
				return "ERROR:" + (String)value;
			case ARRAY:
				return Arrays.deepToString((RedisObject[])value);
			default:
				return super.toString();
		}
	}
	
}
