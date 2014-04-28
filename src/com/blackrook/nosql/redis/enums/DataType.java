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
