package com.blackrook.nosql.redis.enums;

/**
 * List of data encoding types.
 * @author Matthew Tropiano
 */
public enum EncodingType
{
	RAW,
	INTEGER,
	ZIPLIST,
	LINKEDLIST,
	INTSET,
	HASHTABLE,
	ZIPMAP,
	SKIPLIST;
}
