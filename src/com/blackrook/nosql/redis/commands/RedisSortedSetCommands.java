package com.blackrook.nosql.redis.commands;

import com.blackrook.commons.ObjectPair;
import com.blackrook.nosql.redis.enums.Aggregation;

/**
 * Interface for Redis commands related to Sorted Sets.
 * @author Matthew Tropiano
 */
public interface RedisSortedSetCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/zadd">http://redis.io/commands/zadd</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)) where N is the number of elements in the sorted set.</p>
	 * <p>Adds all the specified members with the specified scores to the sorted set 
	 * stored at <code>key</code>. It is possible to specify multiple score/member pairs. 
	 * If a specified member is already a member of the sorted set, the score is updated 
	 * and the element reinserted at the right position to ensure the correct ordering. 
	 * If <code>key</code> does not exist, a new sorted set with the specified members as 
	 * sole members is created, like if the sorted set was empty. If the key exists but 
	 * does not hold a sorted set, an error is returned.</p>
	 * @return the number of elements added to the sorted sets, not including elements 
	 * already existing for which the score was updated.
	 */
	public long zadd(String key, double score, String member);

	/**
	 * <p>From <a href="http://redis.io/commands/zadd">http://redis.io/commands/zadd</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)) where N is the number of elements in the sorted set.</p>
	 * <p>Adds all the specified members with the specified scores to the sorted set 
	 * stored at <code>key</code>. It is possible to specify multiple score/member pairs. 
	 * If a specified member is already a member of the sorted set, the score is updated 
	 * and the element reinserted at the right position to ensure the correct ordering. 
	 * If <code>key</code> does not exist, a new sorted set with the specified members as 
	 * sole members is created, like if the sorted set was empty. If the key exists but 
	 * does not hold a sorted set, an error is returned.</p>
	 * @return the number of elements added to the sorted sets, not including elements 
	 * already existing for which the score was updated.
	 */
	public long zadd(String key, ObjectPair<Double, String>... pairs);

	/**
	 * <p>From <a href="http://redis.io/commands/zcard">http://redis.io/commands/zcard</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the sorted set cardinality (number of elements) of the sorted set stored at <code>key</code>.</p>
	 * @return the cardinality (number of elements) of the sorted set, or <code>0</code> if <code>key</code> does not exist.
	 */
	public long zcard(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/zcount">http://redis.io/commands/zcount</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)) with N being the number of elements in the sorted set.</p>
	 * <p>Returns the number of elements in the sorted set at <code>key</code> with a score between <code>min</code> and <code>max</code>.</p>
	 * @return the number of elements in the specified score range.
	 */
	public long zcount(String key, double min, double max);

	/**
	 * <p>From <a href="http://redis.io/commands/zincrby">http://redis.io/commands/zincrby</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)) where N is the number of elements in the sorted set.</p>
	 * <p>Increments the score of <code>member</code> in the sorted set stored at <code>key</code> by <code>increment</code>. If <code>member</code> does not exist in the sorted set, it is added with <code>increment</code> as its score (as if its previous score was <code>0.0</code>). If <code>key</code> does not exist, a new sorted set with the specified <code>member</code> as its sole member is created.</p>
	 * @return the new score of <code>member</code> (a double precision floating point number).
	 */
	public Double zincrby(String key, double increment, String member);

	/**
	 * <p>From <a href="http://redis.io/commands/zrange">http://redis.io/commands/zrange</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of 
	 * elements in the sorted set and M the number of elements returned.</p>
	 * <p>Returns the specified range of elements in the sorted set stored at <code>key</code>. 
	 * The elements are considered to be ordered from the lowest to the highest score. 
	 * Lexicographical order is used for elements with equal score.</p>
	 * @return list of elements in the specified range (optionally with their scores).
	 */
	public String[] zrange(String key, long start, long stop, boolean withScores);

	/**
	 * <p>From <a href="http://redis.io/commands/zrangebyscore">http://redis.io/commands/zrangebyscore</a>:</p>
	 * <p><strong>Available since 1.0.5.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of elements 
	 * in the sorted set and M the number of elements being returned. If M is constant (e.g. 
	 * always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).</p>
	 * <p>Returns all the elements in the sorted set at <code>key</code> with a score between 
	 * <code>min</code> and <code>max</code> (including elements with score equal to <code>min</code> 
	 * or <code>max</code>). The elements are considered to be ordered from low to high scores.</p>
	 * @return list of elements in the specified score range (optionally with their scores).
	 */
	public String[] zrangebyscore(String key, double min, double max, boolean withScores);

	/**
	 * <p>From <a href="http://redis.io/commands/zrangebyscore">http://redis.io/commands/zrangebyscore</a>:</p>
	 * <p><strong>Available since 1.0.5.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of elements 
	 * in the sorted set and M the number of elements being returned. If M is constant (e.g. 
	 * always asking for the first 10 elements with LIMIT), you can consider it O(log(N)).</p>
	 * <p>Returns all the elements in the sorted set at <code>key</code> with a score 
	 * between <code>min</code> and <code>max</code> (including elements with score equal 
	 * to <code>min</code> or <code>max</code>). The elements are considered to be ordered 
	 * from low to high scores.</p>
	 * @return list of elements in the specified score range (optionally with their scores).
	 */
	public String[] zrangebyscore(String key, double min, double max, long offset, long count, boolean withScores);
	
	/**
	 * <p>From <a href="http://redis.io/commands/zrank">http://redis.io/commands/zrank</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N))</p>
	 * <p>Returns the rank of <code>member</code> in the sorted set stored at <code>key</code>,
	 * with the scores ordered from low to high. The rank (or index) is 0-based, which
	 * means that the member with the lowest score has rank <code>0</code>.</p>
	 * @return If <code>member</code> exists in the sorted set, the rank of <code>member</code>. 
	 * If <code>member</code> does not exist in the sorted set or <code>key</code> 
	 * does not exist, <code>null</code>.
	 */
	public Long zrank(String key, String member);

	/**
	 * <p>From <a href="http://redis.io/commands/zrem">http://redis.io/commands/zrem</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(M*log(N)) with N being the number of 
	 * elements in the sorted set and M the number of elements to be removed.</p>
	 * <p>Removes the specified members from the sorted set stored at <code>key</code>. 
	 * Non existing members are ignored.</p>
	 * @return the number of members removed from the sorted set, not including non existing members.
	 */
	public long zrem(String key, String... members);

	/**
	 * <p>From <a href="http://redis.io/commands/zremrangebyrank">http://redis.io/commands/zremrangebyrank</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of 
	 * elements in the sorted set and M the number of elements removed by the operation.</p>
	 * <p>Removes all elements in the sorted set stored at <code>key</code> with rank 
	 * between <code>start</code> and <code>stop</code>. Both <code>start</code> and 
	 * <code>stop</code> are <code>0</code> -based indexes with <code>0</code> being 
	 * the element with the lowest score. These indexes can be negative numbers, where 
	 * they indicate offsets starting at the element with the highest score. For 
	 * example: <code>-1</code> is the element with the highest score, <code>-2</code> 
	 * the element with the second highest score and so forth.</p>
	 * @return the number of elements removed.
	 */
	public long zremrangebyrank(String key, long start, long stop);

	/**
	 * <p>From <a href="http://redis.io/commands/zremrangebyscore">http://redis.io/commands/zremrangebyscore</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of 
	 * elements in the sorted set and M the number of elements removed by the operation.</p>
	 * <p>Removes all elements in the sorted set stored at <code>key</code> with a 
	 * score between <code>min</code> and <code>max</code> (inclusive).</p>
	 * @return the number of elements removed.
	 */
	public long zremrangebyscore(String key, double min, double max);

	/**
	 * <p>From <a href="http://redis.io/commands/zrevrank">http://redis.io/commands/zrevrank</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N))</p>
	 * <p>Returns the rank of <code>member</code> in the sorted set stored at <code>key</code>, 
	 * with the scores ordered from high to low. The rank (or index) is 0-based, which means 
	 * that the member with the highest score has rank <code>0</code>.</p>
	 * @return If <code>member</code> exists in the sorted set, the rank of <code>member</code>. 
	 * If <code>member</code> does not exist in the sorted set or <code>key</code> does not exist, <code>null</code>.
	 */
	public Long zrevrank(String key, String member);

	/**
	 * <p>From <a href="http://redis.io/commands/zrevrange">http://redis.io/commands/zrevrange</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of 
	 * elements in the sorted set and M the number of elements returned.</p>
	 * <p>Returns the specified range of elements in the sorted set stored at 
	 * <code>key</code>. The elements are considered to be ordered from the highest 
	 * to the lowest score. Descending lexicographical order is used for elements with equal score.</p>
	 * @return list of elements in the specified range (optionally with their scores).
	 */
	public String[] zrevrange(String key, long start, long stop, boolean withScores);

	/**
	 * <p>From <a href="http://redis.io/commands/zrevrangebyscore">http://redis.io/commands/zrevrangebyscore</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of 
	 * elements in the sorted set and M the number of elements removed by the operation.</p>
	 * <p>Returns all the elements in the sorted set at key with a score between 
	 * <code>max</code> and <code>min</code> (including elements with score equal 
	 * to max or min). In contrary to the default ordering of sorted sets, for this 
	 * command the elements are considered to be ordered from high to low scores.</p>
	 * @return list of elements in the specified score range (optionally with their scores).
	 */
	public String[] zrevrangebyscore(String key, double min, double max, long offset, long count, boolean withScores);

	/**
	 * <p>From <a href="http://redis.io/commands/zscore">http://redis.io/commands/zscore</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the score of <code>member</code> in the sorted set at <code>key</code>.</p>
	 * @return the score of <code>member</code> (a double precision floating point number).
	 */
	public Double zscore(String key, String member);

	/**
	 * <p>From <a href="http://redis.io/commands/zinterstore">http://redis.io/commands/zinterstore</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N*K)+O(M*log(M)) worst case with N being the 
	 * smallest input sorted set, K being the number of input sorted sets and M being the 
	 * number of elements in the resulting sorted set.</p>
	 * <p>Computes the intersection of <code>numkeys</code> sorted sets given by the 
	 * specified keys, and stores the result in <code>destination</code>. It is mandatory 
	 * to provide the number of input keys (<code>numkeys</code>) before passing the 
	 * input keys and the other (optional) arguments.</p>
	 * @return the number of elements in the resulting sorted set at <code>destination</code>.
	 */
	public long zinterstore(String destination, String[] keys, String[] weights, Aggregation aggregation);

	/**
	 * <p>From <a href="http://redis.io/commands/zunionstore">http://redis.io/commands/zunionstore</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N)+O(M log(M)) with N being the sum of 
	 * the sizes of the input sorted sets, and M being the number of elements in the 
	 * resulting sorted set.</p>
	 * <p>Computes the union of <code>numkeys</code> sorted sets given by the specified 
	 * keys, and stores the result in <code>destination</code>. It is mandatory to 
	 * provide the number of input keys (<code>numkeys</code>) before passing the input 
	 * keys and the other (optional) arguments.</p>
	 * @return the number of elements in the resulting sorted set at <code>destination</code>.
	 */
	public long zunionstore(String destination, String[] keys, String[] weights, Aggregation aggregation);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * <p>See <a href="/commands/scan">SCAN</a> for <a href="/commands/zscan">ZSCAN</a> documentation.</p>
	 */
	public void zscan(String key, String cursor);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * <p>See <a href="/commands/scan">SCAN</a> for <a href="/commands/zscan">ZSCAN</a> documentation.</p>
	 */
	public void zscan(String key, String cursor, String pattern);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * <p>See <a href="/commands/scan">SCAN</a> for <a href="/commands/zscan">ZSCAN</a> documentation.</p>
	 */
	public void zscan(String key, String cursor, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/zscan">http://redis.io/commands/zscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, 
	 * including enough command calls for the cursor to return back to 0. N is the number of 
	 * elements inside the collection..</p>
	 * <p>See <a href="/commands/scan">SCAN</a> for <a href="/commands/zscan">ZSCAN</a> documentation.</p>
	 */
	public void zscan(String key, String cursor, String pattern, long count);

}
