package com.blackrook.nosql.redis.commands;

import com.blackrook.nosql.redis.data.RedisCursor;

/**
 * Interface for Redis commands related to Sets.
 * @author Matthew Tropiano
 */
public interface RedisSetCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/sadd">http://redis.io/commands/sadd</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of members to be added.</p>
	 * <p>Add the specified members to the set stored at <code>key</code>. Specified members 
	 * that are already a member of this set are ignored. If <code>key</code> does not exist, 
	 * a new set is created before adding the specified members.</p>
	 * @return the number of elements that were added to the set, not including all the elements already present into the set.
	 */
	public long sadd(String key, String member, String... members);

	/**
	 * <p>From <a href="http://redis.io/commands/scard">http://redis.io/commands/scard</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the set cardinality (number of elements) of the set stored at <code>key</code>.</p>
	 * @return the cardinality (number of elements) of the set, or <code>0</code> if <code>key</code> does not exist.
	 */
	public long scard(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/sdiff">http://redis.io/commands/sdiff</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the total number of elements in all given sets.</p>
	 * <p>Returns the members of the set resulting from the difference between the first set and all the successive sets.</p>
	 * @return list with members of the resulting set.
	 */
	public String[] sdiff(String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/sdiffstore">http://redis.io/commands/sdiffstore</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the total number of elements in all given sets.</p>
	 * <p>This command is equal to <a href="/commands/sdiff">SDIFF</a>, but instead of 
	 * returning the resulting set, it is stored in <code>destination</code>.</p>
	 * @return the number of elements in the resulting set.
	 */
	public long sdiffstore(String destination, String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/sinter">http://redis.io/commands/sinter</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N*M) worst case where N is the cardinality 
	 * of the smallest set and M is the number of sets.</p>
	 * <p>Returns the members of the set resulting from the intersection of all the given sets.</p>
	 * @return list with members of the resulting set.
	 */
	public String[] sinter(String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/sinterstore">http://redis.io/commands/sinterstore</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N*M) worst case where N is the cardinality of the smallest set and M is the number of sets.</p>
	 * <p>This command is equal to <a href="/commands/sinter">SINTER</a>, but instead of 
	 * returning the resulting set, it is stored in <code>destination</code>.</p>
	 * @return the number of elements in the resulting set.
	 */
	public long sinterstore(String destination, String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/sismember">http://redis.io/commands/sismember</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns if <code>member</code> is a member of the set stored at <code>key</code>.</p>
	 * @return true if the member is in the set, or false if not.
	 */
	public boolean sismember(String key, String member);

	/**
	 * <p>From <a href="http://redis.io/commands/smembers">http://redis.io/commands/smembers</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the set cardinality.</p>
	 * <p>Returns all the members of the set value stored at <code>key</code>.</p>
	 * @return all elements of the set.
	 */
	public String[] smembers(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/smove">http://redis.io/commands/smove</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Move <code>member</code> from the set at <code>source</code> to the set at 
	 * <code>destination</code>. This operation is atomic. In every given moment the element 
	 * will appear to be a member of <code>source</code> <strong>or</strong> <code>destination</code> for other clients.</p>
	 * @return true if the move of the member is successful, or false if the source list did not contain the member to move. 
	 */
	public boolean smove(String source, String destination, String member);

	/**
	 * <p>From <a href="http://redis.io/commands/spop">http://redis.io/commands/spop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns a random element from the set value stored at <code>key</code>.</p>
	 * @return the removed element, or <code>null</code> when <code>key</code> does not exist.
	 */
	public String spop(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/srandmember">http://redis.io/commands/srandmember</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>When called with just the <code>key</code> argument, return a random element 
	 * from the set value stored at <code>key</code>.</p>
	 * @return the randomly selected element, or <code>null</code> when <code>key</code> does not exist.
	 */
	public String srandmember(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/srandmember">http://redis.io/commands/srandmember</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the absolute value of the passed count.</p>
	 * <p>When called with just the <code>key</code> argument, return a random element 
	 * from the set value stored at <code>key</code>.</p>
	 * @return an array of elements, or an empty array when <code>key</code> does not exist.
	 */
	public String[] srandmember(String key, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/srem">http://redis.io/commands/srem</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of members to be removed.</p>
	 * <p>Remove the specified members from the set stored at <code>key</code>. Specified 
	 * members that are not a member of this set are ignored. If <code>key</code> does not 
	 * exist, it is treated as an empty set and this command returns <code>0</code>.</p>
	 * @return the number of members that were removed from the set, not including non existing members.
	 */
	public long srem(String key, String member, String... members);

	/**
	 * <p>From <a href="http://redis.io/commands/sunion">http://redis.io/commands/sunion</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the total number of elements in all given sets.</p>
	 * <p>Returns the members of the set resulting from the union of all the given sets.</p>
	 * @return list with members of the resulting set.
	 */
	public String[] sunion(String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/sunionstore">http://redis.io/commands/sunionstore</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the total number of elements in all given sets.</p>
	 * <p>This command is equal to <a href="/commands/sunion">SUNION</a>, but instead 
	 * of returning the resulting set, it is stored in <code>destination</code>.</p>
	 * @return the number of elements in the resulting set.
	 */
	public long sunionstore(String destination, String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, String cursor, String pattern, Long count);
	
}
