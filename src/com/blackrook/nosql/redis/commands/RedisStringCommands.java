package com.blackrook.nosql.redis.commands;

import com.blackrook.commons.ObjectPair;

/**
 * An interface detailing commands that operate on Redis Strings.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisStringCommands
{
	/** 
	 *	Append a value to a key
	 *	string
	 */
	public void append(String key, String value);

	/** 
	 *	Count set bits in a string
	 *	string
	 */
	public void bitcount(String key);

	/** 
	 *	Count set bits in a string
	 *	string
	 */
	public void bitcount(String key, long start, long end);

	/** 
	 *	Perform bitwise operations between strings
	 *	string
	 */
	public void bitop(String operation, String destkey, String... keys);

	/** 
	 *	Find first bit set or clear in a string
	 *	string
	 */
	public void bitpos(String key, String bit);

	/** 
	 *	Find first bit set or clear in a string
	 *	string
	 */
	public void bitpos(String key, String bit, long start, long end);

	/** 
	 *	Decrement the integer value of a key by one
	 *	string
	 */
	public void decr(String key);

	/** 
	 *	Decrement the integer value of a key by the given number
	 *	string
	 */
	public void decrby(String key, String decrement);

	/** 
	 *	Get the value of a key
	 *	string
	 */
	public void get(String key);

	/** 
	 *	Returns the bit value at offset in the string value stored at key
	 *	string
	 */
	public void getbit(String key, String offset);

	/** 
	 *	Get a substring of the string stored at a key
	 *	string
	 */
	public void getrange(String key, String start, String end);

	/** 
	 *	Set the string value of a key and return its old value
	 *	string
	 */
	public void getset(String key, String value);

	/** 
	 *	Increment the integer value of a key by one
	 *	string
	 */
	public void incr(String key);

	/** 
	 *	Increment the integer value of a key by the given amount
	 *	string
	 */
	public void incrby(String key, String increment);

	/** 
	 *	Increment the float value of a key by the given amount
	 *	string
	 */
	public void incrbyfloat(String key, String increment);

	/** 
	 *	Get the values of all the given keys
	 *	string
	 */
	public void mget(String... keys);
	
	/** 
	 *	Set multiple keys to multiple values
	 *	string
	 */
	public void mset(String... keyValues);
	
	/** 
	 *	Set multiple keys to multiple values
	 *	string
	 */
	public void mset(ObjectPair<String, String>... keyValues);
	
	/** 
	 *	Set multiple keys to multiple values, only if none of the keys exist
	 *	string
	 */
	public void msetnx(String... keyValues);
	
	/** 
	 *	Set multiple keys to multiple values, only if none of the keys exist
	 *	string
	 */
	public void msetnx(ObjectPair<String, String>... pairs);
	
	/** 
	 *	Set the value and expiration in milliseconds of a key
	 *	string
	 */
	public void psetex(String key, long milliseconds, String value);

	/** 
	 *	Set the string value of a key
	 *	string
	 */
	public void set(String key, String value);
	
	/** 
	 *	Sets or clears the bit at offset in the string value stored at key
	 *	string
	 */
	public void setbit(String key, long offset, String value);

	/** 
	 *	Set the value and expiration of a key
	 *	string
	 */
	public void setex(String key, long seconds, String value);

	/** 
	 *	Set the value of a key, only if the key does not exist
	 *	string
	 */
	public void setnx(String key, String value);

	/** 
	 *	Overwrite part of a string at key starting at the specified offset
	 *	string
	 */
	public void setrange(String key, long offset, String value);

	/** 
	 *	Get the length of the value stored in a key
	 *	string
	 */
	public void strlen(String key);


}
