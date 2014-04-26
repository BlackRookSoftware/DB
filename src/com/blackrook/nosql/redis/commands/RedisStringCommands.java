package com.blackrook.nosql.redis.commands;

import com.blackrook.commons.ObjectPair;
import com.blackrook.nosql.redis.enums.BitwiseOperation;

/**
 * An interface detailing commands that operate on Redis Strings or string values.
 * @author Matthew Tropiano
 */
public interface RedisStringCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/append">http://redis.io/commands/append</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1). The amortized time complexity is O(1) assuming the appended value is small and the already present value is of any size, since the dynamic string library used by Redis will double the free space available on every reallocation.</p>
	 * <p>If <code>key</code> already exists and is a string, this command appends the <code>value</code> at the end of the string. If <code>key</code> does not exist it is created and set as an empty string, so <a href="/commands/append">APPEND</a> will be similar to <a href="/commands/set">SET</a> in this special case.</p>
	 * @return the length of the string after the append operation.
	 */
	public long append(String key, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/bitcount">http://redis.io/commands/bitcount</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N)</p>
	 * <p>Count the number of set bits (population counting) in a string.</p>
	 * @return the count.
	 */
	public long bitcount(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/bitcount">http://redis.io/commands/bitcount</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N)</p>
	 * <p>Count the number of set bits (population counting) in a string between a start and end bit.</p>
	 * @return the count.
	 */
	public long bitcount(String key, long start, long end);

	/**
	 * <p>From <a href="http://redis.io/commands/bitop">http://redis.io/commands/bitop</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N)</p>
	 * <p>Perform a bitwise operation between multiple keys (containing 
	 * string values) and store the result in the destination key.</p>
	 * @return the size of the string stored in the destination key, 
	 * equal to the size of the longest input string.
	 */
	public long bitop(BitwiseOperation operation, String destkey, String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/bitpos">http://redis.io/commands/bitpos</a>:</p>
	 * <p><strong>Available since 2.8.7.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N)</p>
	 * <p>Return the position of the first bit set to 1 or 0 in a string.</p>
	 * @return the command returns the position of the first bit set to 1 or 0 according to the request.
	 */
	public long bitpos(String key, long bit);

	/**
	 * <p>From <a href="http://redis.io/commands/bitpos">http://redis.io/commands/bitpos</a>:</p>
	 * <p><strong>Available since 2.8.7.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N)</p>
	 * <p>Return the position of the first bit set to 1 or 0 in a string.</p>
	 * @return the command returns the position of the first bit set to 1 or 0 according to the request.
	 */
	public long bitpos(String key, long bit, Long start, Long end);

	/**
	 * <p>From <a href="http://redis.io/commands/decr">http://redis.io/commands/decr</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Decrements the number stored at <code>key</code> by one. If the key does not exist, 
	 * it is set to <code>0</code> before performing the operation. An error is returned if 
	 * the key contains a value of the wrong type or contains a string that can not be 
	 * represented as integer. This operation is limited to <strong>64 bit signed integers</strong>.</p>
	 * @return the value of <code>key</code> after the decrement.
	 */
	public long decr(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/decrby">http://redis.io/commands/decrby</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Decrements the number stored at <code>key</code> by <code>decrement</code>. If 
	 * the key does not exist, it is set to <code>0</code> before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a 
	 * string that can not be represented as integer. This operation is limited to 64 
	 * bit signed integers.</p>
	 * @return the value of <code>key</code> after the decrement.
	 */
	public long decrby(String key, long decrement);

	/**
	 * <p>From <a href="http://redis.io/commands/get">http://redis.io/commands/get</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Get the value of <code>key</code>. If the key does not exist the special value 
	 * <code>null</code> is returned. An error is returned if the value stored at 
	 * <code>key</code> is not a string, because <a href="/commands/get">GET</a> only 
	 * handles string values.</p>
	 * @return the value of <code>key</code>, or <code>null</code> when <code>key</code> 
	 * does not exist.
	 */
	public String get(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/getbit">http://redis.io/commands/getbit</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the bit value at <em>offset</em> in the string value stored at <em>key</em>.</p>
	 * @return the bit value stored at <em>offset</em>.
	 */
	public long getbit(String key, long offset);

	/**
	 * <p>From <a href="http://redis.io/commands/getrange">http://redis.io/commands/getrange</a>:</p>
	 * <p><strong>Available since 2.4.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the length of the returned 
	 * string. The complexity is ultimately determined by the returned length, but because 
	 * creating a substring from an existing string is very cheap, it can be considered 
	 * O(1) for small strings.</p>
	 * <p><strong>Warning</strong>: this command was renamed to {@link #getrange(String, String, String)}, 
	 * it is called <code>SUBSTR</code> in Redis versions <code>&lt;= 2.0</code>.</p>
	 * @return the resultant substring.
	 */
	public String getrange(String key, long start, long end);

	/**
	 * <p>From <a href="http://redis.io/commands/getset">http://redis.io/commands/getset</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Atomically sets <code>key</code> to <code>value</code> and returns the 
	 * old value stored at <code>key</code>. Returns an error when <code>key</code> 
	 * exists but does not hold a string value.</p>
	 * @return the old value stored at <code>key</code>, or <code>null</code> when <code>key</code> did not exist.
	 */
	public String getset(String key, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/getset">http://redis.io/commands/getset</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Atomically sets <code>key</code> to <code>value</code> and returns the 
	 * old value stored at <code>key</code>. Returns an error when <code>key</code> 
	 * exists but does not hold a string value.</p>
	 * @return the old value stored at <code>key</code>, or <code>null</code> when <code>key</code> did not exist.
	 */
	public String getset(String key, Number value);

	/**
	 * <p>From <a href="http://redis.io/commands/incr">http://redis.io/commands/incr</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Increments the number stored at <code>key</code> by one. If the key does not 
	 * exist, it is set to <code>0</code> before performing the operation. An error is 
	 * returned if the key contains a value of the wrong type or contains a string that 
	 * can not be represented as integer. This operation is limited to 64 bit signed integers.</p>
	 * @return the value of <code>key</code> after the increment,
	 */
	public long incr(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/incrby">http://redis.io/commands/incrby</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Increments the number stored at <code>key</code> by <code>increment</code>. 
	 * If the key does not exist, it is set to <code>0</code> before performing the operation. 
	 * An error is returned if the key contains a value of the wrong type or contains a 
	 * string that can not be represented as integer. This operation is limited to 64 bit 
	 * signed integers.</p>
	 * @return the value of <code>key</code> after the increment.
	 */
	public long incrby(String key, long increment);

	/**
	 * <p>From <a href="http://redis.io/commands/incrbyfloat">http://redis.io/commands/incrbyfloat</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Increment the string representing a floating point number stored at <code>key</code> by the specified <code>increment</code>. If the key does not exist, it is set to <code>0</code> before performing the operation. An error is returned if one of the following conditions occur:</p>
	 * @return the value of <code>key</code> after the increment.
	 */
	public double incrbyfloat(String key, double increment);

	/**
	 * <p>From <a href="http://redis.io/commands/mget">http://redis.io/commands/mget</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of keys to retrieve.</p>
	 * <p>Returns the values of all specified keys. For every key that does not hold a string 
	 * value or does not exist, the special value <code>nil</code> is returned. Because of 
	 * this, the operation never fails.</p>
	 * @return list of values at the specified keys.
	 */
	public String[] mget(String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/mset">http://redis.io/commands/mset</a>:</p>
	 * <p><strong>Available since 1.0.1.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of keys to set.</p>
	 * <p>Sets the given keys to their respective values. <code>MSET</code> replaces existing 
	 * values with new values, just as regular <a href="/commands/set">SET</a>. See {@link #msetnx(String...)} 
	 * if you don't want to overwrite existing values.</p>
	 * @return true, always.
	 */
	public boolean mset(String key, String value, String... keyValues);

	/**
	 * Like {@link #mset(String, String, String...)}, but takes key-value pairs.
	 */
	public boolean mset(ObjectPair<String, Object>... pairs);

	/**
	 * <p>From <a href="http://redis.io/commands/msetnx">http://redis.io/commands/msetnx</a>:</p>
	 * <p><strong>Available since 1.0.1.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of keys to set.</p>
	 * <p>Sets the given keys to their respective values. <code>MSETNX</code> will not 
	 * perform any operation at all even if just a single key already exists.</p>
	 * @return true if all of the keys were set, false if no key was set.
	 */
	public boolean msetnx(String key, String value, String... keyValues);

	/** 
	 * Like {@link #msetnx(String, String, String...)}, but takes key-value pairs.
	 */
	public boolean msetnx(ObjectPair<String, Object>... pairs);

	/**
	 * <p>From <a href="http://redis.io/commands/psetex">http://redis.io/commands/psetex</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><code>PSETEX</code> works exactly like {@link #setex(String, long, String)} with the 
	 * sole difference that the expire time is specified in milliseconds instead of seconds.</p>
	 * @return true, always.
	 */
	public boolean psetex(String key, long milliseconds, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/set">http://redis.io/commands/set</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Set <code>key</code> to hold the string <code>value</code>. If <code>key</code> 
	 * already holds a value, it is overwritten, regardless of its type. Any previous time 
	 * to live associated with the key is discarded on successful <code>SET</code> operation.</p>
	 * @return true, always.
	 */
	public boolean set(String key, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/set">http://redis.io/commands/set</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Set <code>key</code> to hold the string <code>value</code>. If <code>key</code> 
	 * already holds a value, it is overwritten, regardless of its type. Any previous time 
	 * to live associated with the key is discarded on successful <code>SET</code> operation.</p>
	 * @return true, always.
	 */
	public boolean set(String key, Number value);

	/**
	 * <p>From <a href="http://redis.io/commands/setbit">http://redis.io/commands/setbit</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets or clears the bit at <em>offset</em> in the string value stored at <em>key</em>.</p>
	 * @return the original bit value stored at <em>offset</em>.
	 */
	public long setbit(String key, long offset, long value);

	/**
	 * <p>From <a href="http://redis.io/commands/setex">http://redis.io/commands/setex</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Set <code>key</code> to hold the string <code>value</code> and set <code>key</code> 
	 * to timeout after a given number of seconds. This command is equivalent to executing 
	 * the following commands:</p>
	 * @return true, always.
	 */
	public boolean setex(String key, long seconds, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/setnx">http://redis.io/commands/setnx</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Set <code>key</code> to hold string <code>value</code> if <code>key</code> does not 
	 * exist. In that case, it is equal to {@link #set(String, String)}. When <code>key</code> 
	 * already holds a value, no operation is performed. <code>SETNX</code> is short for &quot;<strong>SET</strong> 
	 * if <strong>N</strong> ot e <strong>X</strong> ists&quot;.</p>
	 * @return true if the key was set, false if not.
	 */
	public boolean setnx(String key, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/setrange">http://redis.io/commands/setrange</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1), not counting the time taken to copy 
	 * the new string in place. Usually, this string is very small so the amortized 
	 * complexity is O(1). Otherwise, complexity is O(M) with M being the length of 
	 * the value argument.</p>
	 * <p>Overwrites part of the string stored at <em>key</em>, starting at the specified 
	 * offset, for the entire length of <em>value</em>. If the offset is larger than the 
	 * current length of the string at <em>key</em>, the string is padded with zero-bytes 
	 * to make <em>offset</em> fit. Non-existing keys are considered as empty strings, so 
	 * this command will make sure it holds a string large enough to be able to 
	 * set <em>value</em> at <em>offset</em>.</p>
	 * @return the length of the string after it was modified by the command.
	 */
	public long setrange(String key, long offset, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/strlen">http://redis.io/commands/strlen</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the length of the string value stored at <code>key</code>. 
	 * An error is returned when <code>key</code> holds a non-string value.</p>
	 * @return the length of the string at <code>key</code>, or <code>0</code> when <code>key</code> does not exist.
	 */
	public long strlen(String key);

}
