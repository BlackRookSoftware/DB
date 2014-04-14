package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis commands related to Sets.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisSetCommands
{

	/** 
	 *	Add one or more members to a set
	 *	set
	 */
	public void sadd(String key, String... members);
	
	/** 
	 *	Get the number of members in a set
	 *	set
	 */
	public void scard(String key);

	/** 
	 *	Subtract multiple sets
	 *	set
	 */
	public void sdiff(String... keys);
	
	/** 
	 *	Subtract multiple sets and store the resulting set in a key
	 *	set
	 */
	public void sdiffstore(String destination, String... keys);
	
	/** 
	 *	Intersect multiple sets
	 *	set
	 */
	public void sinter(String... keys);
	
	/** 
	 *	Intersect multiple sets and store the resulting set in a key
	 *	set
	 */
	public void sinterstore(String destination, String... keys);
	
	/** 
	 *	Determine if a given value is a member of a set
	 *	set
	 */
	public void sismember(String key, String member);

	/** 
	 *	Get all the members in a set
	 *	set
	 */
	public void smembers(String key);

	/** 
	 *	Move a member from one set to another
	 *	set
	 */
	public void smove(String source, String destination, String member);

	/** 
	 *	Remove and return a random member from a set
	 *	set
	 */
	public void spop(String key);

	/** 
	 *	Get one or multiple random members from a set
	 *	set
	 */
	public void srandmember(String key, long count);
	
	/** 
	 *	Remove one or more members from a set
	 *	set
	 */
	public void srem(String key, String... members);
	
	/** 
	 *	Add multiple sets
	 *	set
	 */
	public void sunion(String... keys);
	
	/** 
	 *	Add multiple sets and store the resulting set in a key
	 *	set
	 */
	public void sunionstore(String destination, String... keys);
	
	/** 
	 *	Incrementally iterate Set elements
	 *	set
	 */
	public void sscan(String key, String cursor);
	
	/** 
	 *	Incrementally iterate Set elements
	 *	set
	 */
	public void sscan(String key, String cursor, long count);
	
	/** 
	 *	Incrementally iterate Set elements
	 *	set
	 */
	public void sscan(String key, String cursor, String pattern);
	
	/** 
	 *	Incrementally iterate Set elements
	 *	set
	 */
	public void sscan(String key, String cursor, String pattern, long count);
	
}
