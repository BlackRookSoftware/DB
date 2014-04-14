package com.blackrook.nosql.redis.commands;

import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.hash.HashMap;

/**
 * Interface for Redis commands related to Hashes.
 * @author Matthew Tropiano
 */
public interface RedisHashCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/hdel">http://redis.io/commands/hdel</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of fields to be removed.</p>
	 * <p>Removes the specified fields from the hash stored at <code>key</code>. Specified fields that do not exist within this hash are ignored. If <code>key</code> does not exist, it is treated as an empty hash and this command returns <code>0</code>.</p>
	 * @return the number of fields that were removed from the hash, not including specified but non existing fields.
	 */
	public long hdel(String key, String... fields);

	/**
	 * <p>From <a href="http://redis.io/commands/hexists">http://redis.io/commands/hexists</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns if <code>field</code> is an existing field in the hash stored at <code>key</code>.</p>
	 * @return true if successful, false if not.
	 */
	public boolean hexists(String key, String field);

	/**
	 * <p>From <a href="http://redis.io/commands/hget">http://redis.io/commands/hget</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the value associated with <code>field</code> in the hash stored at <code>key</code>.</p>
	 * @return the value associated with <code>field</code>, or <code>null</code> when <code>field</code> is not present in the hash or <code>key</code> does not exist.
	 */
	public String hget(String key, String field);

	/**
	 * <p>From <a href="http://redis.io/commands/hget">http://redis.io/commands/hget</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the value associated with <code>field</code> in the hash stored at <code>key</code>, cast to a Long.</p>
	 * @return the value associated with <code>field</code>, or <code>null</code> when <code>field</code> is not present in the hash or <code>key</code> does not exist.
	 */
	public Long hgetAsLong(String key, String field);

	/**
	 * <p>From <a href="http://redis.io/commands/hget">http://redis.io/commands/hget</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the value associated with <code>field</code> in the hash stored at <code>key</code>, cast to a primitive long.</p>
	 * @return the value associated with <code>field</code>, or <code>0</code> when <code>field</code> is not present in the hash or <code>key</code> does not exist.
	 */
	public long hgetLong(String key, String field);

	/**
	 * <p>From <a href="http://redis.io/commands/hgetall">http://redis.io/commands/hgetall</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the size of the hash.</p>
	 * <p>Returns all fields and values of the hash stored at <code>key</code>. In the returned value, every field name is followed by its value, so the length of the reply is twice the size of the hash.</p>
	 * @return a list of fields and their values stored in the hash, or an empty list when <code>key</code> does not exist.
	 */
	public String[] hgetall(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/hgetall">http://redis.io/commands/hgetall</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the size of the hash.</p>
	 * <p>Returns all fields and values of the hash stored at <code>key</code>. The value is returned as a {@link HashMap}.</p>
	 * @return a list of fields and their values stored in the hash, or an empty list when <code>key</code> does not exist.
	 */
	public HashMap<String, String> hgetallAsMap(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/hincrby">http://redis.io/commands/hincrby</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Increments the number stored at <code>field</code> in the hash stored at <code>key</code> by <code>increment</code>. If <code>key</code> does not exist, a new key holding a hash is created. If <code>field</code> does not exist the value is set to <code>0</code> before the operation is performed.</p>
	 * @return the value at <code>field</code> after the increment operation.
	 */
	public long hincrby(String key, String field, long increment);

	/**
	 * <p>From <a href="http://redis.io/commands/hincrbyfloat">http://redis.io/commands/hincrbyfloat</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Increment the specified <code>field</code> of an hash stored at <code>key</code>, and representing a floating point number, by the specified <code>increment</code>. If the field does not exist, it is set to <code>0</code> before performing the operation. An error is returned if one of the following conditions occur:</p>
	 * @return the value of <code>field</code> after the increment.
	 */
	public double hincrbyfloat(String key, String field, double increment);

	/**
	 * <p>From <a href="http://redis.io/commands/hkeys">http://redis.io/commands/hkeys</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the size of the hash.</p>
	 * <p>Returns all field names in the hash stored at <code>key</code>.</p>
	 * @return the list of fields in the hash, or an empty list when <code>key</code> does not exist.
	 */
	public String[] hkeys(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/hlen">http://redis.io/commands/hlen</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the number of fields contained in the hash stored at <code>key</code>.</p>
	 * @return the number of fields in the hash, or <code>0</code> when <code>key</code> does not exist.
	 */
	public long hlen(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/hmget">http://redis.io/commands/hmget</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of fields being requested.</p>
	 * <p>Returns the values associated with the specified <code>fields</code> in the hash stored at <code>key</code>.</p>
	 * @return a list of values associated with the given fields, in the same order as they are requested.
	 */
	public String[] hmget(String key, String... fields);

	/**
	 * <p>From <a href="http://redis.io/commands/hmset">http://redis.io/commands/hmset</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of fields being set.</p>
	 * <p>Sets the specified fields to their respective values in the hash stored at <code>key</code>. This command overwrites any existing fields in the hash. If <code>key</code> does not exist, a new key holding a hash is created.</p>
	 * <p>Parameters should alternate between field, value, field, value ...</p>
	 * @return true if successful, false if not.
	 */
	public boolean hmset(String key, String... fieldvalues);

	/**
	 * <p>From <a href="http://redis.io/commands/hmset">http://redis.io/commands/hmset</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of fields being set.</p>
	 * <p>Sets the specified fields to their respective values in the hash stored at <code>key</code>. This command overwrites any existing fields in the hash. If <code>key</code> does not exist, a new key holding a hash is created.</p>
	 * <p>Parameters should alternate between field, value, field, value ...</p>
	 * @return true if successful, false if not.
	 */
	public boolean hmset(String key, ObjectPair<String, String>... pair);

	/**
	 * <p>From <a href="http://redis.io/commands/hset">http://redis.io/commands/hset</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets <code>field</code> in the hash stored at <code>key</code> to <code>value</code>. If <code>key</code> does not exist, a new key holding a hash is created. If <code>field</code> already exists in the hash, it is overwritten.</p>
	 * @return true if successful, false if not.
	 */
	public void hset(String key, String field, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/hsetnx">http://redis.io/commands/hsetnx</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets <code>field</code> in the hash stored at <code>key</code> to <code>value</code>, only if <code>field</code> does not yet exist. If <code>key</code> does not exist, a new key holding a hash is created. If <code>field</code> already exists, this operation has no effect.</p>
	 * @return true if successful, false if not.
	 */
	public void hsetnx(String key, String field, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/hvals">http://redis.io/commands/hvals</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the size of the hash.</p>
	 * <p>Returns all values in the hash stored at <code>key</code>.</p>
	 * @return a list of values in the hash, or an empty list when <code>key</code> does not exist.
	 */
	public String[] hvals(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..</p>
	 * @return a two-element multi-bulk reply, where the first element is a string representing an unsigned 64 bit number (the cursor), and the second element is a multi-bulk with an array of elements.
	 */
	public Object hscan(String key, String cursor);
	
	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..</p>
	 * @return a two-element multi-bulk reply, where the first element is a string representing an unsigned 64 bit number (the cursor), and the second element is a multi-bulk with an array of elements.
	 */
	public Object hscan(String key, String cursor, String pattern);
	
	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..</p>
	 * @return a two-element multi-bulk reply, where the first element is a string representing an unsigned 64 bit number (the cursor), and the second element is a multi-bulk with an array of elements.
	 */
	public Object hscan(String key, String cursor, long count);
	
	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete iteration, including enough command calls for the cursor to return back to 0. N is the number of elements inside the collection..</p>
	 * @return a two-element multi-bulk reply, where the first element is a string representing an unsigned 64 bit number (the cursor), and the second element is a multi-bulk with an array of elements.
	 */
	public Object hscan(String key, String cursor, String pattern, long count);
	
}
