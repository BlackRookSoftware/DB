package com.blackrook.nosql.redis.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.blackrook.commons.ObjectPair;
import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.enums.Aggregation;
import com.blackrook.nosql.redis.enums.BitwiseOperation;
import com.blackrook.nosql.redis.enums.DataType;
import com.blackrook.nosql.redis.enums.SortOrder;

/**
 * Interface for Redis connection stuff.
 * @author Matthew Tropiano
 */
public interface RedisConnectionCommands
{
	/** TTL error - no expire. */
	public static final long TTL_NO_EXPIRE = -1L;
	/** TTL error - not exist. */
	public static final long TTL_NOT_EXIST = -2L;

	/**
	 * <p>From <a href="http://redis.io/commands/ping">http://redis.io/commands/ping</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>This command is often used to test if a connection is still alive, or to measure latency.</p>
	 * @return milliseconds between the call and the response. Ordinarily, Redis just returns "PONG", which is not very useful API-wise.
	 */
	public long ping();

	/**
	 * <p>From <a href="http://redis.io/commands/echo">http://redis.io/commands/echo</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Returns <code>message</code>.</p>
	 * @return the string sent to the server.
	 */
	public String echo(String message);

	/**
	 * <p>From <a href="http://redis.io/commands/quit">http://redis.io/commands/quit</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Ask the server to close the connection. The connection is closed as soon as all pending replies have been written to the client.</p>
	 * @return always true.
	 */
	public boolean quit();

	/**
	 * <p>From <a href="http://redis.io/commands/client-getname">http://redis.io/commands/client-getname</a>:</p>
	 * <p><strong>Available since 2.6.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <code>CLIENT GETNAME</code> returns the name of the current connection as set by 
	 * <code>CLIENT SETNAME</code>. Since every new connection starts without an associated 
	 * name, if no name was assigned a null bulk reply is returned.</p>
	 * @return the connection name, or null if no name is set.
	 */
	public String clientGetName();

	/**
	 * <p>From <a href="http://redis.io/commands/client-setname">http://redis.io/commands/client-setname</a>:</p>
	 * <p><strong>Available since 2.6.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <code>CLIENT SETNAME</code> command assigns a name to the current connection.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean clientSetName(String name);

	/**
	 * <p>From <a href="http://redis.io/commands/del">http://redis.io/commands/del</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of keys that 
	 * will be removed. When a key to remove holds a value other than a string, the 
	 * individual complexity for this key is O(M) where M is the number of elements 
	 * in the list, set, sorted set or hash. Removing a single key that holds a 
	 * string value is O(1).</p>
	 * <p>Removes the specified keys. A key is ignored if it does not exist.</p>
	 * @param key the first key to delete.
	 * @param keys the additional keys to delete.
	 * @return The number of keys that were removed.
	 */
	public long del(String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/dump">http://redis.io/commands/dump</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) to access the key and 
	 * additional O(N*M) to serialized it, where N is the number of Redis objects 
	 * composing the value and M their average size. For small string values the 
	 * time complexity is thus O(1)+O(1*M) where M is small, so simply O(1).</p>
	 * <p>Serialize the value stored at key in a Redis-specific format and return
	 * it to the user. The returned value can be synthesized back into a Redis 
	 * key using the {@link #restore} command.</p>
	 * @return the serialized value.
	 */
	public String dump(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/exists">http://redis.io/commands/exists</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns if <code>key</code> exists.</p>
	 * @return true if the key exists, false if not.
	 */
	public boolean exists(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/expire">http://redis.io/commands/expire</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Set a timeout on <code>key</code>. After the timeout has expired, the key
	 * will automatically be deleted. A key with an associated timeout is often 
	 * said to be <em>volatile</em> in Redis terminology.</p>
	 * @param key the key to expire.
	 * @param seconds the time-to-live in seconds.
	 * @return true if set, false if not set.
	 */
	public boolean expire(String key, long seconds);

	/**
	 * <p>From <a href="http://redis.io/commands/expireat">http://redis.io/commands/expireat</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><b>expireat</b> has the same effect and semantic as {@link #expire}, but 
	 * instead of specifying the number of seconds representing the TTL (time to 
	 * live), it takes an absolute <a href="http://en.wikipedia.org/wiki/Unix_time">Unix timestamp</a> 
	 * (seconds since January 1, 1970).</p>
	 * @param key the key to expire.
	 * @param timestamp the timestamp in from-Epoch milliseconds.
	 * @return true if set, false if not set.
	 */
	public boolean expireat(String key, long timestamp);

	/**
	 * <p>From <a href="http://redis.io/commands/keys">http://redis.io/commands/keys</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the number of keys 
	 * in the database, under the assumption that the key names in the database 
	 * and the given pattern have limited length.</p>
	 * <p>Returns all keys matching <code>pattern</code>.</p>
	 * @param pattern a wildcard pattern for matching key names.
	 * @return a list of keys matching <code>pattern</code>.
	 */
	public String[] keys(String pattern);

	/**
	 * <p>From <a href="http://redis.io/commands/move">http://redis.io/commands/move</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Move <code>key</code> from the currently selected database (see {@link RedisStringCommands#select}) 
	 * to the specified destination database. When <code>key</code> already exists in the 
	 * destination database, or it does not exist in the source database, it does nothing.
	 * It is possible to use <a href="/commands/move">MOVE</a> as a locking primitive because of this.</p>
	 * @param the key to move.
	 * @param the target database. 
	 * @return true if the key was moved, false if not.
	 */
	public boolean move(String key, long db);

	/**
	 * <p>From <a href="http://redis.io/commands/persist">http://redis.io/commands/persist</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Remove the existing timeout on <code>key</code>, turning the key 
	 * from <em>volatile</em> (a key with an expire set) to <em>persistent</em> 
	 * (a key that will never expire as no timeout is associated).</p>
	 * @param the key to persist (remove TTL).
	 * @return true if successful, false if not.
	 */
	public boolean persist(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/pexpire">http://redis.io/commands/pexpire</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>This command works exactly like {@link #expire} but the time to 
	 * live of the key is specified in milliseconds instead of seconds.</p>
	 * @param the key to expire.
	 * @param milliseconds the time-to-live in milliseconds.
	 * @return true if successful, false if not.
	 */
	public boolean pexpire(String key, long milliseconds);

	/**
	 * <p>From <a href="http://redis.io/commands/pexpireat">http://redis.io/commands/pexpireat</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>PEXPIREAT has the same effect and semantic as {@link #expireat}, but 
	 * the Unix time at which the key will expire is specified in milliseconds 
	 * instead of seconds.</p>
	 * @param the key to expire.
	 * @param timestamp the timestamp in from-Epoch milliseconds.
	 * @return true if successful, false if not.
	 */
	public boolean pexpireat(String key, long timestamp);

	/**
	 * <p>From <a href="http://redis.io/commands/pttl">http://redis.io/commands/pttl</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Like {@link #ttl}, this command returns the remaining time to live 
	 * of a key that has an expire set, with the sole difference that TTL returns 
	 * the amount of remaining time in seconds while PTTL returns it in milliseconds.</p>
	 * @param the key to inspect.
	 * @return TTL in milliseconds, or a negative value in order to signal an error (see the description above).
	 */
	public long pttl(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/publish">http://redis.io/commands/publish</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N+M) where N is the number of clients subscribed 
	 * to the receiving channel and M is the total number of subscribed patterns (by any client).</p>
	 * <p>Posts a message to the given channel.</p>
	 * @return the number of clients that received the message.
	 */
	public long publish(String channel, String message);

	/**
	 * <p>From <a href="http://redis.io/commands/randomkey">http://redis.io/commands/randomkey</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Return a random key from the currently selected database.</p>
	 * @return the random key, or <code>null</code> when the database is empty.
	 */
	public String randomkey();

	/**
	 * <p>From <a href="http://redis.io/commands/rename">http://redis.io/commands/rename</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Renames <code>key</code> to <code>newkey</code>. It returns an error
	 * when the source and destination names are the same, or when <code>key</code> 
	 * does not exist. If <code>newkey</code> already exists it is overwritten, when 
	 * this happens {@link #rename} executes an implicit {@link #del} operation, so 
	 * if the deleted key contains a very big value it may cause high latency even 
	 * if {@link #rename} itself is usually a constant-time operation.</p>
	 * @param key the old name. 
	 * @param newkey the new name. 
	 * @return always true.
	 */
	public boolean rename(String key, String newkey);

	/**
	 * <p>From <a href="http://redis.io/commands/renamenx">http://redis.io/commands/renamenx</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Renames <code>key</code> to <code>newkey</code> if <code>newkey</code> 
	 * does not yet exist. It returns an error under the same conditions as {@link #rename}.</p>
	 * @param key the old name. 
	 * @param newkey the new name. 
	 * @return true if successful, false if not.
	 */
	public boolean renamenx(String key, String newkey);

	/**
	 * <p>From <a href="http://redis.io/commands/restore">http://redis.io/commands/restore</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) to create the new key and 
	 * additional O(N*M) to recostruct the serialized value, where N is the number
	 * of Redis objects composing the value and M their average size. For small string 
	 * values the time complexity is thus O(1)+O(1*M) where M is small, so simply O(1). 
	 * However for sorted set values the complexity is O(N*M*log(N)) because inserting 
	 * values into sorted sets is O(log(N)).</p>
	 * <p>Create a key associated with a value that is obtained by deserializing 
	 * the provided serialized value (obtained via {@link #dump}).</p>
	 * @param key the key to restore.
	 * @param ttl the time-to-live in milliseconds.
	 * @param serializedvalue the serialized value (from a {@link #dump()} call). 
	 * @return always true.
	 */
	public boolean restore(String key, long ttl, String serializedvalue);

	/**
	 * <p>From <a href="http://redis.io/commands/sort">http://redis.io/commands/sort</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N+M*log(M)) where N is the number 
	 * of elements in the list or set to sort, and M the number of returned elements. 
	 * When the elements are not sorted, complexity is currently O(N) as there is a 
	 * copy step that will be avoided in next releases.</p>
	 * <p>Returns or stores the elements contained in the list, set, or sorted set
	 * at <code>key</code>. By default, sorting is numeric and elements are compared 
	 * by their value interpreted as double precision floating point number.
	 * @param key the key to sort the contents of.
	 * @param pattern if not null, 
	 * @param sortOrder if true, sort descending. if false or null, sort ascending.
	 * @param alpha if true, sort lexicographically, not by a score.
	 * @param limitOffset if not null, the starting offset into the list (0-based).
	 * @param limitCount if not null, the amount of objects from the offset to sort. else, return all the way to the end.
	 * @param storeKey if not null, this is the key to store the result in.
	 * @param getPatterns the patterns for finding the sort score.
	 * @return the list of sorted elements.
	 */
	public String[] sort(String key, String pattern, SortOrder sortOrder, boolean alpha, Long limitOffset, Long limitCount, String storeKey, String... getPatterns);

	/**
	 * <p>From <a href="http://redis.io/commands/ttl">http://redis.io/commands/ttl</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the remaining time to live of a key that has a timeout. This 
	 * introspection capability allows a Redis client to check how many seconds 
	 * a given key will continue to be part of the dataset.</p>
	 * @param the key to inspect.
	 * @return TTL in seconds, or a negative value in order to signal an error (see description).
	 */
	public long ttl(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/type">http://redis.io/commands/type</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the string representation of the type of the value stored at 
	 * <code>key</code>.</p>
	 * @return the type of <code>key</code>, or {@link DataType#NONE} when <code>key</code> does not exist.
	 */
	public DataType type(String key);

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

	/**
	 * <p>From <a href="http://redis.io/commands/hdel">http://redis.io/commands/hdel</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of fields to be removed.</p>
	 * <p>Removes the specified fields from the hash stored at <code>key</code>. 
	 * Specified fields that do not exist within this hash are ignored. If <code>key</code> 
	 * does not exist, it is treated as an empty hash and this command returns <code>0</code>.</p>
	 * @return the number of fields that were removed from the hash, not including 
	 * specified but non existing fields.
	 */
	public long hdel(String key, String field, String... fields);

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
	 * <p>Returns the value associated with <code>field</code> in the hash stored 
	 * at <code>key</code>.</p>
	 * @return the value associated with <code>field</code>, or <code>null</code> 
	 * when <code>field</code> is not present in the hash or <code>key</code> does not exist.
	 */
	public String hget(String key, String field);

	/**
	 * <p>From <a href="http://redis.io/commands/hgetall">http://redis.io/commands/hgetall</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the size of the hash.</p>
	 * <p>Returns all fields and values of the hash stored at <code>key</code>. In 
	 * the returned value, every field name is followed by its value, so the length 
	 * of the reply is twice the size of the hash.</p>
	 * @return a list of fields and their values stored in the hash, or an empty 
	 * list when <code>key</code> does not exist.
	 */
	public String[] hgetall(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/hincrby">http://redis.io/commands/hincrby</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Increments the number stored at <code>field</code> in the hash stored 
	 * at <code>key</code> by <code>increment</code>. If <code>key</code> does 
	 * not exist, a new key holding a hash is created. If <code>field</code> does 
	 * not exist the value is set to <code>0</code> before the operation is performed.</p>
	 * @return the value at <code>field</code> after the increment operation.
	 */
	public long hincrby(String key, String field, long increment);

	/**
	 * <p>From <a href="http://redis.io/commands/hincrbyfloat">http://redis.io/commands/hincrbyfloat</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Increment the specified <code>field</code> of an hash stored at <code>key</code>, 
	 * and representing a floating point number, by the specified <code>increment</code>. 
	 * If the field does not exist, it is set to <code>0</code> before performing the 
	 * operation.</p>
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
	public String[] hmget(String key, String field, String... fields);

	/**
	 * <p>From <a href="http://redis.io/commands/hmset">http://redis.io/commands/hmset</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of fields being set.</p>
	 * <p>Sets the specified fields to their respective values in the hash stored 
	 * at <code>key</code>. This command overwrites any existing fields in the hash. 
	 * If <code>key</code> does not exist, a new key holding a hash is created.</p>
	 * <p>Parameters should alternate between field, value, field, value ...</p>
	 * @return always true.
	 */
	public boolean hmset(String key, String field, String value, String... fieldvalues);

	/**
	 * <p>From <a href="http://redis.io/commands/hset">http://redis.io/commands/hset</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets <code>field</code> in the hash stored at <code>key</code> to <code>value</code>. 
	 * If <code>key</code> does not exist, a new key holding a hash is created. If 
	 * <code>field</code> already exists in the hash, it is overwritten.</p>
	 * @return true if a new field, false if set, but not a new field.
	 */
	public boolean hset(String key, String field, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/hset">http://redis.io/commands/hset</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets <code>field</code> in the hash stored at <code>key</code> to <code>value</code>. 
	 * If <code>key</code> does not exist, a new key holding a hash is created. If 
	 * <code>field</code> already exists in the hash, it is overwritten.</p>
	 * @return true if a new field, false if set, but not a new field.
	 */
	public boolean hset(String key, String field, Number value);

	/**
	 * <p>From <a href="http://redis.io/commands/hsetnx">http://redis.io/commands/hsetnx</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets <code>field</code> in the hash stored at <code>key</code> to <code>value</code>, 
	 * only if <code>field</code> does not yet exist. If <code>key</code> does not exist, a 
	 * new key holding a hash is created. If <code>field</code> already exists, this 
	 * operation has no effect.</p>
	 * @return true if a new field, false if set, but not a new field.
	 */
	public boolean hsetnx(String key, String field, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/hsetnx">http://redis.io/commands/hsetnx</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets <code>field</code> in the hash stored at <code>key</code> to <code>value</code>, 
	 * only if <code>field</code> does not yet exist. If <code>key</code> does not exist, a 
	 * new key holding a hash is created. If <code>field</code> already exists, this 
	 * operation has no effect.</p>
	 * @return true if a new field, false if set, but not a new field.
	 */
	public boolean hsetnx(String key, String field, Number value);

	/**
	 * <p>From <a href="http://redis.io/commands/hvals">http://redis.io/commands/hvals</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the size of the hash.</p>
	 * <p>Returns all values in the hash stored at <code>key</code>.</p>
	 * @return a list of values in the hash, or an empty list when <code>key</code> does not exist.
	 */
	public String[] hvals(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/blpop">http://redis.io/commands/blpop</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><code>BLPOP</code> is a blocking list pop primitive. It is the blocking version 
	 * of {@link #lpop(String)} because it blocks the connection when there are no elements 
	 * to pop from any of the given lists. An element is popped from the head of the first 
	 * list that is non-empty, with the given keys being checked in the order that they are 
	 * given. A <code>timeout</code> of zero can be used to block indefinitely. Timeout is in seconds.</p>
	 * @return an object pair consisting of popped list key and the value popped, or null on timeout.
	 */
	public ObjectPair<String, String> blpop(long timeout, String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/brpop">http://redis.io/commands/brpop</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><code>BRPOP</code> is a blocking list pop primitive. It is the blocking 
	 * version of {@link #rpop()} because it blocks the connection when there are 
	 * no elements to pop from any of the given lists. An element is popped from 
	 * the tail of the first list that is non-empty, with the given keys being 
	 * checked in the order that they are given. A <code>timeout</code> of zero 
	 * can be used to block indefinitely. Timeout is in seconds.</p>
	 * @return an object pair consisting of popped list key and the value popped, or null on timeout.
	 */
	public ObjectPair<String, String> brpop(long timeout, String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/brpoplpush">http://redis.io/commands/brpoplpush</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><code>BRPOPLPUSH</code> is the blocking variant of {@link #rpoplpush(String, String)}. 
	 * When <code>source</code> contains elements, this command behaves exactly like 
	 * {@link #rpoplpush(String, String)}. When <code>source</code> is empty, 
	 * Redis will block the connection until another client pushes to it or 
	 * until <code>timeout</code> is reached. A <code>timeout</code> of zero 
	 * can be used to block indefinitely. Timeout is in seconds.</p>
	 * @return the value popped-then-pushed to destination from source, or null on timeout.
	 */
	public String brpoplpush(long timeout, String source, String destination);

	/**
	 * <p>From <a href="http://redis.io/commands/lindex">http://redis.io/commands/lindex</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of elements 
	 * to traverse to get to the element at index. This makes asking for the first 
	 * or the last element of the list O(1).</p>
	 * <p>Returns the element at index <code>index</code> in the list stored at 
	 * <code>key</code>. The index is zero-based, so <code>0</code> means the first 
	 * element, <code>1</code> the second element and so on. Negative indices can be 
	 * used to designate elements starting at the tail of the list. Here, <code>-1</code> 
	 * means the last element, <code>-2</code> means the penultimate and so forth.</p>
	 * @return the requested element, or <code>null</code> when <code>index</code> is out of range.
	 */
	public String lindex(String key, long index);

	/**
	 * <p>From <a href="http://redis.io/commands/linsert">http://redis.io/commands/linsert</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of elements to 
	 * traverse before seeing the value pivot. This means that inserting somewhere on 
	 * the left end on the list (head) can be considered O(1) and inserting somewhere 
	 * on the right end (tail) is O(N).</p>
	 * <p>Inserts <code>value</code> in the list stored at <code>key</code> either 
	 * before or after the reference value <code>pivot</code>.</p>
	 * @return the length of the list after the insert operation, or <code>-1</code> 
	 * when the value <code>pivot</code> was not found.
	 */
	public long linsert(String key, boolean before, String pivot, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/linsert">http://redis.io/commands/linsert</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of elements to 
	 * traverse before seeing the value pivot. This means that inserting somewhere on 
	 * the left end on the list (head) can be considered O(1) and inserting somewhere 
	 * on the right end (tail) is O(N).</p>
	 * <p>Inserts <code>value</code> in the list stored at <code>key</code> either 
	 * before or after the reference value <code>pivot</code>.</p>
	 * @return the length of the list after the insert operation, or <code>-1</code> 
	 * when the value <code>pivot</code> was not found.
	 */
	public long linsert(String key, boolean before, String pivot, Number value);

	/**
	 * <p>From <a href="http://redis.io/commands/llen">http://redis.io/commands/llen</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the length of the list stored at <code>key</code>. If <code>key</code> does 
	 * not exist, it is interpreted as an empty list and <code>0</code> is returned. An error 
	 * is returned when the value stored at <code>key</code> is not a list.</p>
	 * @return the length of the list at <code>key</code>.
	 */
	public long llen(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/lpop">http://redis.io/commands/lpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the first element of the list stored at <code>key</code>.</p>
	 * @return the value of the first element, or <code>null</code> when <code>key</code> does not exist.
	 */
	public String lpop(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/lpush">http://redis.io/commands/lpush</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Insert all the specified values at the head of the list stored at <code>key</code>. 
	 * If <code>key</code> does not exist, it is created as empty list before performing the 
	 * push operations. When <code>key</code> holds a value that is not a list, an error is returned.</p>
	 * @return the length of the list after the push operations.
	 */
	public long lpush(String key, String value, String... values);

	/**
	 * <p>From <a href="http://redis.io/commands/lpushx">http://redis.io/commands/lpushx</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Inserts <code>value</code> at the head of the list stored at <code>key</code>,
	 * only if <code>key</code> already exists and holds a list. In contrary to 
	 * {@link #lpush(String, String...)}, no operation will be performed when 
	 * <code>key</code> does not yet exist.</p>
	 * @return the length of the list after the push operation.
	 */
	public long lpushx(String key, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/lrange">http://redis.io/commands/lrange</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(S+N) where S is the start offset 
	 * and N is the number of elements in the specified range.</p>
	 * <p>Returns the specified elements of the list stored at <code>key</code>. 
	 * The offsets <code>start</code> and <code>stop</code> are zero-based indexes, 
	 * with <code>0</code> being the first element of the list (the head of the 
	 * list), <code>1</code> being the next element and so on.</p>
	 * @return list of elements in the specified range.
	 */
	public String[] lrange(String key, long start, long stop);

	/**
	 * <p>From <a href="http://redis.io/commands/lrem">http://redis.io/commands/lrem</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the length of the list.</p>
	 * <p>Removes the first <code>count</code> occurrences of elements equal to 
	 * <code>value</code> from the list stored at <code>key</code>. The 
	 * <code>count</code> argument influences the operation in the following ways:</p>
	 * <ul>
	 * <li><code>count &gt; 0</code>: Remove elements equal to <code>value</code> moving from head to tail.</li>
	 * <li><code>count &lt; 0</code>: Remove elements equal to <code>value</code> moving from tail to head.</li>
	 * <li><code>count = 0</code>: Remove all elements equal to <code>value</code>.</li>
	 * </ul>
	 * @return the number of removed elements.
	 */
	public long lrem(String key, long count, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/lset">http://redis.io/commands/lset</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the length 
	 * of the list. Setting either the first or the last element of the list is O(1).</p>
	 * <p>Sets the list element at <code>index</code> to <code>value</code>. For 
	 * more information on the <code>index</code> argument, see {@link #lindex(String, long)}.</p>
	 * @return always true.
	 */
	public boolean lset(String key, long index, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/ltrim">http://redis.io/commands/ltrim</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the 
	 * number of elements to be removed by the operation.</p>
	 * <p>Trim an existing list so that it will contain only the 
	 * specified range of elements specified. Both <code>start</code> and <code>stop</code> 
	 * are zero-based indexes, where <code>0</code> is the first element of the list 
	 * (the head), <code>1</code> the next element and so on.</p>
	 * @return always true.
	 */
	public boolean ltrim(String key, long start, long stop);

	/**
	 * <p>From <a href="http://redis.io/commands/rpop">http://redis.io/commands/rpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the last element of the list stored at <code>key</code>.</p>
	 * @return the value of the last element, or <code>null</code> when <code>key</code> does not exist.
	 */
	public String rpop(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/rpoplpush">http://redis.io/commands/rpoplpush</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Atomically returns and removes the last element (tail) of the list stored 
	 * at <code>source</code>, and pushes the element at the first element (head) 
	 * of the list stored at <code>destination</code>.</p>
	 * @return the element being popped and pushed.
	 */
	public String rpoplpush(String source, String destination);

	/**
	 * <p>From <a href="http://redis.io/commands/rpush">http://redis.io/commands/rpush</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Insert all the specified values at the tail of the list stored at <code>key</code>. 
	 * If <code>key</code> does not exist, it is created as empty list before performing the 
	 * push operation. When <code>key</code> holds a value that is not a list, an error is 
	 * returned.</p>
	 * @return the length of the list after the push operation.
	 */
	public long rpush(String key, String value, String... values);

	/**
	 * <p>From <a href="http://redis.io/commands/rpushx">http://redis.io/commands/rpushx</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Inserts <code>value</code> at the tail of the list stored at <code>key</code>, 
	 * only if <code>key</code> already exists and holds a list. In contrary to 
	 * {@link #rpush(String, String...)}, no operation will be performed when 
	 * <code>key</code> does not yet exist.</p>
	 * @return the length of the list after the push operation.
	 */
	public long rpushx(String key, String value);

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
	 * <p>Returns the number of elements in the sorted set at <code>key</code> with 
	 * a score between <code>min</code> and <code>max</code>.</p>
	 * <p>The arguments <code>min</code> and <code>max</code> are Strings so they can accept special ranges.</p>
	 * @return the number of elements in the specified score range.
	 */
	public long zcount(String key, String min, String max);

	/**
	 * Like {@link #zcount(String, String, String)}, 
	 * except it accepts doubles for min and max, not strings.
	 */
	public long zcount(String key, double min, double max);

	/**
	 * <p>From <a href="http://redis.io/commands/zincrby">http://redis.io/commands/zincrby</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)) where N is the number of elements in the sorted set.</p>
	 * <p>Increments the score of <code>member</code> in the sorted set stored at 
	 * <code>key</code> by <code>increment</code>. If <code>member</code> does not exist in the 
	 * sorted set, it is added with <code>increment</code> as its score (as if its previous 
	 * score was <code>0.0</code>). If <code>key</code> does not exist, a new sorted set with 
	 * the specified <code>member</code> as its sole member is created.</p>
	 * @return the new score of <code>member</code> (a double precision floating point number).
	 */
	public double zincrby(String key, double increment, String member);

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
	 * <p>Returns all the elements in the sorted set at <code>key</code> with a score 
	 * between <code>min</code> and <code>max</code> (including elements with score equal 
	 * to <code>min</code> or <code>max</code>). The elements are considered to be ordered 
	 * from low to high scores.</p>
	 * <p>The optional <code>LIMIT</code> argument can be used to only get a range of the matching
	 * elements (similar to <em>SELECT LIMIT offset, count</em> in SQL).
	 * Keep in mind that if <code>offset</code> is large, the sorted set needs to be traversed for
	 * <code>offset</code> elements before getting to the elements to return, which can add up to
	 * <span class="math">O(N) </span>time complexity.</p>
	 * <p>The arguments <code>min</code> and <code>max</code> are Strings so they can accept special ranges.</p>
	 * @return list of elements in the specified score range (optionally with their scores).
	 */
	public String[] zrangebyscore(String key, String min, String max, boolean withScores, Long limitOffset, Long limitCount);

	/**
	 * Like {@link #zrangebyscore(String, String, String, boolean)},
	 * except it accepts doubles for min and max, not strings.
	 */
	public String[] zrangebyscore(String key, double min, double max, boolean withScores);

	/**
	 * Like {@link #zrangebyscore(String, String, String, boolean, Long, Long)}, except specifies no limit.
	 */
	public String[] zrangebyscore(String key, String min, String max, boolean withScores);

	/**
	 * Like {@link #zrangebyscore(String, String, String, boolean, Long, Long)}, 
	 * except it accepts doubles for min and max, not strings.
	 */
	public String[] zrangebyscore(String key, double min, double max, boolean withScores, Long limitOffset, Long limitCount);

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
	public long zrem(String key, String member, String... members);

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
	 * <p>The arguments <code>min</code> and <code>max</code> are Strings so they can accept special ranges.</p>
	 * @return the number of elements removed.
	 */
	public long zremrangebyscore(String key, String min, String max);

	/**
	 * Like {@link #zremrangebyscore(String, String, String)},
	 * except it accepts doubles for min and max, not strings.
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
	 * <p>The optional <code>LIMIT</code> argument can be used to only get a range of the matching
	 * elements (similar to <em>SELECT LIMIT offset, count</em> in SQL).
	 * Keep in mind that if <code>offset</code> is large, the sorted set needs to be traversed for
	 * <code>offset</code> elements before getting to the elements to return, which can add up to
	 * <span class="math">O(N) </span>time complexity.</p>
	 * <p>The arguments <code>min</code> and <code>max</code> are Strings so they can accept special ranges.</p>
	 * @return list of elements in the specified score range (optionally with their scores).
	 */
	public String[] zrevrangebyscore(String key, String min, String max, boolean withScores, Long limitOffset, Long limitCount);

	/**
	 * Like {@link #zrevrangebyscore(String, String, String, boolean)},
	 * except it accepts doubles for min and max, not strings.
	 */
	public String[] zrevrangebyscore(String key, double min, double max, boolean withScores);

	/**
	 * Like {@link #zrevrangebyscore(String, String, String, boolean, Long, Long)}, except specifies no limit.
	 */
	public String[] zrevrangebyscore(String key, String min, String max, boolean withScores);

	/**
	 * Like {@link #zrevrangebyscore(String, String, String, boolean, Long, Long)}, 
	 * except it accepts doubles for min and max, not strings.
	 */
	public String[] zrevrangebyscore(String key, double min, double max, boolean withScores, Long limitOffset, Long limitCount);

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
	public long zinterstore(String destination, double[] weights, Aggregation aggregation, String key, String... keys);

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
	public long zunionstore(String destination, double[] weights, Aggregation aggregation, String key, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/zlexcount">http://redis.io/commands/zlexcount</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)) with N being the number of elements in the sorted set.</p>
	 * <p>When all the elements in a sorted set are inserted with the same score, in 
	 * order to force lexicographical ordering, this command returns the number of elements 
	 * in the sorted set at <code>key</code> with a value between <code>min</code> and <code>max</code>.</p>
	 * <p>The arguments <code>min</code> and <code>max</code> are Strings so they can accept special ranges.</p>
	 * @return the number of elements in the specified score range.
	 */
	public long zlexcount(String key, String min, String max);

	/**
	 * <p>From <a href="http://redis.io/commands/zrangebylex">http://redis.io/commands/zrangebylex</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of elements in 
	 * the sorted set and M the number of elements being returned. If M is constant (e.g. always 
	 * asking for the first 10 elements with LIMIT), you can consider it O(log(N)).</p>
	 * <p>When all the elements in a sorted set are inserted with the same score, in order 
	 * to force lexicographical ordering, this command returns all the elements in the sorted 
	 * set at <code>key</code> with a value between <code>min</code> and <code>max</code>.</p>
	 * <p>The optional <code>LIMIT</code> argument can be used to only get a range of the matching
	 * elements (similar to <em>SELECT LIMIT offset, count</em> in SQL).
	 * Keep in mind that if <code>offset</code> is large, the sorted set needs to be traversed for
	 * <code>offset</code> elements before getting to the elements to return, which can add up to
	 * <span class="math">O(N) </span>time complexity.</p>
	 * <p>The arguments <code>min</code> and <code>max</code> are Strings so they can accept special ranges.</p>
	 * @return list of elements in the specified score range.
	 */
	public long zrangebylex(String key, String min, String max, Long limitOffset, Long limitCount);

	/**
	 * <p>From <a href="http://redis.io/commands/zremrangebylex">http://redis.io/commands/zremrangebylex</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(log(N)+M) with N being the number of elements 
	 * in the sorted set and M the number of elements removed by the operation.</p>
	 * <p>When all the elements in a sorted set are inserted with the same score, in order to 
	 * force lexicographical ordering, this command removes all elements in the sorted set stored 
	 * at <code>key</code> between the lexicographical range specified by <code>min</code> and <code>max</code>.</p>
	 * <p>The arguments <code>min</code> and <code>max</code> are Strings so they can accept special ranges.</p>
	 * @return the number of elements removed.
	 */
	public long zremrangebylex(String key, String min, String max);

	/**
	 * Like {@link #zinterstore(String, double[], Aggregation, String, String...)}, except no weights are
	 * applied to the source value scores.
	 * <p>Equivalent to: <code>zinterstore(destination, null, aggregation, key, keys)</code></p>
	 */
	public long zinterstore(String destination, Aggregation aggregation, String key, String... keys);

	/**
	 * Like {@link #zinterstore(String, double[], Aggregation, String, String...)}, except it does no
	 * aggregation of scores.
	 * <p>Equivalent to: <code>zinterstore(destination, weights, null, key, keys)</code></p>
	 */
	public long zinterstore(String destination, double[] weights, String key, String... keys);

	/**
	 * Like {@link #zinterstore(String, double[], Aggregation, String, String...)}, except no weights are
	 * applied to the source value scores, and does no aggregation of scores.
	 * <p>Equivalent to: <code>zinterstore(destination, null, null, key, keys)</code></p>
	 */
	public long zinterstore(String destination, String key, String... keys);

	/**
	 * Like {@link #zunionstore(String, double[], Aggregation, String, String...)}, except no weights are
	 * applied to the source value scores.
	 * <p>Equivalent to: <code>zunionstore(destination, null, aggregation, key, keys)</code></p>
	 */
	public long zunionstore(String destination, Aggregation aggregation, String key, String... keys);

	/**
	 * Like {@link #zunionstore(String, double[], Aggregation, String, String...)}, except it does no
	 * aggregation of scores.
	 * <p>Equivalent to: <code>zunionstore(destination, weights, null, key, keys)</code></p>
	 */
	public long zunionstore(String destination, double[] weights, String key, String... keys);

	/**
	 * Like {@link #zunionstore(String, double[], Aggregation, String, String...)}, except no weights are
	 * applied to the source value scores, and does no aggregation of scores.
	 * <p>Equivalent to: <code>zunionstore(destination, null, null, key, keys)</code></p>
	 */
	public long zunionstore(String destination, String key, String... keys);

	/**
	 * Like {@link #zlexcount(String, String, String)}, 
	 * except it accepts doubles for min and max, not strings.
	 */
	public long zlexcount(String key, double min, double max);

	/**
	 * Like {@link #zrangebylex(String, String, String, Long, Long)},
	 * except it accepts doubles for min and max, not strings.
	 */
	public long zrangebylex(String key, double min, double max, Long limitOffset, Long limitCount);

	/**
	 * Like {@link #zrangebylex(String, String, String, Long, Long)}, with no limit.
	 */
	public long zrangebylex(String key, String min, String max);

	/**
	 * Like {@link #zrangebylex(String, String, String)},
	 * except it accepts doubles for min and max, not strings, with no limit.
	 */
	public long zrangebylex(String key, double min, double max);

	/**
	 * Like {@link #zrangebylex(String, String, String)},
	 * except it accepts doubles for min and max.
	 */
	public long zremrangebylex(String key, double min, double max);

	/**
	 * <p>From <a href="http://redis.io/commands/eval">http://redis.io/commands/eval</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> Depends on the script that is executed.</p>
	 * <p>Evaluates a Lua script. The keys specified in <code>keys</code> should 
	 * be used as a hint for Redis as to what keys are touched during the script call.</p>
	 * @return the content returned by the script. Can be null.
	 */
	public RedisObject eval(String scriptContent, String[] keys, String[] args);

	/**
	 * <p>From <a href="http://redis.io/commands/evalsha">http://redis.io/commands/evalsha</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> Depends on the script that is executed.</p>
	 * <p>Evaluates a script cached on the server side by its SHA1 digest. 
	 * Scripts are cached on the server side using the {@link #scriptload(String)} command. 
	 * The command is otherwise identical to {@link #eval(String, String[], String[])}.</p>
	 * @return the content returned by the script. Can be null.
	 */
	public RedisObject evalsha(String hash, String[] keys, String[] args);

	/**
	 * <p>From <a href="http://redis.io/commands/script-exists">http://redis.io/commands/script-exists</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the number of scripts 
	 * to check (so checking a single script is an O(1) operation).</p>
	 * <p>Returns information about the existence of the scripts in the script cache.</p>
	 * @return The command returns an array of booleans that correspond to the specified 
	 * SHA1 digest arguments. For every corresponding SHA1 digest of a script that actually 
	 * exists in the script cache, true is returned, otherwise false is returned.
	 */
	public boolean[] scriptExists(String scriptHash, String... scriptHashes);

	/**
	 * <p>From <a href="http://redis.io/commands/script-flush">http://redis.io/commands/script-flush</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the number of scripts in cache</p>
	 * <p>Flush the Lua scripts cache.</p>
	 * @return always true. 
	 */
	public boolean scriptFlush();

	/**
	 * <p>From <a href="http://redis.io/commands/script-kill">http://redis.io/commands/script-kill</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Kills the currently executing Lua script, assuming no write operation was yet performed by the script.</p>
	 * @return always true. 
	 */
	public boolean scriptKill(String hash);

	/**
	 * <p>From <a href="http://redis.io/commands/script-load">http://redis.io/commands/script-load</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the length in bytes of the script body.</p>
	 * <p>Load a script into the scripts cache, without executing it. After the specified 
	 * command is loaded into the script cache it will be callable using {@link #evalsha(String, String[], String[])} 
	 * with the correct SHA1 digest of the script, exactly like after the first successful invocation of {@link #eval(String, String[], String[])}.</p>
	 * @return the SHA1 digest of the script added into the script cache.
	 */
	public String scriptLoad(String content);

	/**
	 * <p>From <a href="http://redis.io/commands/script-load">http://redis.io/commands/script-load</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the length in bytes of the script body.</p>
	 * <p>Load a script into the scripts cache from the specified file without executing it. After the specified 
	 * command is loaded into the script cache it will be callable using {@link #evalsha(String, String[], String[])} 
	 * with the correct SHA1 digest of the script, exactly like after the first successful invocation of {@link #eval(String, String[], String[])}.</p>
	 * @return the SHA1 digest of the script added into the script cache.
	 */
	public String scriptLoad(File content) throws IOException;

	/**
	 * <p>From <a href="http://redis.io/commands/script-load">http://redis.io/commands/script-load</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the length in bytes of the script body.</p>
	 * <p>Load a script into the scripts cache from the specified input stream (until the end is reached) without executing it. 
	 * The stream is not closed after read. After the specified command is loaded into the 
	 * script cache it will be callable using {@link #evalsha(String, String[], String[])} 
	 * with the correct SHA1 digest of the script, exactly like after the first 
	 * successful invocation of {@link #eval(String, String[], String[])}.</p>
	 * @return the SHA1 digest of the script added into the script cache.
	 */
	public String scriptLoad(InputStream content) throws IOException;

}
