package com.blackrook.nosql.redis.commands;

import com.blackrook.commons.ObjectPair;

/**
 * Interface for Redis commands related to Sorted Sets.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisSortedSetCommands
{

	/** 
	 *	Add one or more members to a sorted set, or update its score if it already exists
	 *	sorted_set
	 */
	public void zadd(String key, double score, String member);
	
	/** 
	 *	Add one or more members to a sorted set, or update its score if it already exists
	 *	sorted_set
	 */
	public void zadd(String key, ObjectPair<Double, String>... pairs);
	
	/** 
	 *	Get the number of members in a sorted set
	 *	sorted_set
	 */
	public void zcard(String key);

	/** 
	 *	Count the members in a sorted set with scores within the given values
	 *	sorted_set
	 */
	public void zcount(String key, double min, double max);

	/** 
	 *	Increment the score of a member in a sorted set
	 *	sorted_set
	 */
	public void zincrby(String key, double increment, String member);

	/** 
	 *	Return a range of members in a sorted set, by index
	 *	sorted_set
	 */
	public void zrange(String key, long start, long stop);
	
	/** 
	 *	Return a range of members in a sorted set, by index
	 *	sorted_set
	 */
	public void zrangeWithScores(String key, long start, long stop);
	
	/** 
	 *	Return a range of members in a sorted set, by score
	 *	sorted_set
	 */
	public void zrangebyscore(String key, double min, double max);

	/** 
	 *	Return a range of members in a sorted set, by score
	 *	sorted_set
	 */
	public void zrangebyscore(String key, double min, double max, long offset, long count);

	/** 
	 *	Return a range of members in a sorted set, by score
	 *	sorted_set
	 */
	public void zrangebyscoreWithScores(String key, double min, double max);

	/** 
	 *	Return a range of members in a sorted set, by score
	 *	sorted_set
	 */
	public void zrangebyscoreWithScores(String key, double min, double max, long offset, long count);

	/** 
	 *	Determine the index of a member in a sorted set
	 *	sorted_set
	 */
	public void zrank(String key, String member);

	/** 
	 *	Remove one or more members from a sorted set
	 *	sorted_set
	 */
	public void zrem(String key, String... members);
	
	/** 
	 *	Remove all members in a sorted set within the given indexes
	 *	sorted_set
	 */
	public void zremrangebyrank(String key, long start, long stop);

	/** 
	 *	Remove all members in a sorted set within the given scores
	 *	sorted_set
	 */
	public void zremrangebyscore(String key, double min, double max);

	/** 
	 *	Return a range of members in a sorted set, by index, with scores ordered from high to low
	 *	sorted_set
	 */
	public void zrevrange(String key, long start, long stop);
	
	/** 
	 *	Return a range of members in a sorted set, by index, with scores ordered from high to low
	 *	sorted_set
	 */
	public void zrevrangeWithScores(String key, long start, long stop);
	
	/** 
	 *	Return a range of members in a sorted set, by score, with scores ordered from high to low
	 *	sorted_set
	 */
	public void zrevrangebyscore(String key, double min, double max);
	
	/** 
	 *	Return a range of members in a sorted set, by score, with scores ordered from high to low
	 *	sorted_set
	 */
	public void zrevrangebyscore(String key, double min, double max, long offset, long count);
	
	/** 
	 *	Return a range of members in a sorted set, by score, with scores ordered from high to low
	 *	sorted_set
	 */
	public void zrevrangebyscoreWithScores(String key, double min, double max);
	
	/** 
	 *	Return a range of members in a sorted set, by score, with scores ordered from high to low
	 *	sorted_set
	 */
	public void zrevrangebyscoreWithScores(String key, double min, double max, long offset, long count);
	
	/** 
	 *	Determine the index of a member in a sorted set, with scores ordered from high to low
	 *	sorted_set
	 */
	public void zrevrank(String key, String member);

	/** 
	 *	Get the score associated with the given member in a sorted set
	 *	sorted_set
	 */
	public void zscore(String key, String member);

	/** 
	 *	Intersect multiple sorted sets and store the resulting sorted set in a new key
	 *	sorted_set
	 */
//	public void zinterstore(String destination, String numkeys, String key, String [key, String ...], String [WEIGHTS, String weight, String [weight, String ...]], String [AGGREGATE, String SUM|MIN|MAX]);
	
	/** 
	 *	Add multiple sorted sets and store the resulting sorted set in a new key
	 *	sorted_set
	 */
//	public void zunionstore(String destination, String numkeys, String key, String [key, String ...], String [WEIGHTS, String weight, String [weight, String ...]], String [AGGREGATE, String SUM|MIN|MAX]);
	
	/** 
	 *	Incrementally iterate sorted sets elements and associated scores
	 *	sorted_set
	 */
	public void zscan(String key, String cursor);
	
	/** 
	 *	Incrementally iterate sorted sets elements and associated scores
	 *	sorted_set
	 */
	public void zscan(String key, String cursor, String pattern);
	
	/** 
	 *	Incrementally iterate sorted sets elements and associated scores
	 *	sorted_set
	 */
	public void zscan(String key, String cursor, long count);
	
	/** 
	 *	Incrementally iterate sorted sets elements and associated scores
	 *	sorted_set
	 */
	public void zscan(String key, String cursor, String pattern, long count);
	
}
