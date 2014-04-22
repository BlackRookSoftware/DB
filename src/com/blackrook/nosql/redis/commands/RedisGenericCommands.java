package com.blackrook.nosql.redis.commands;

import com.blackrook.nosql.redis.data.RedisObject;

/**
 * Interface for generic Redis commands.
 * @author Matthew Tropiano
 */
public interface RedisGenericCommands
{
	/** Sort constant: ascending. */
	public static final boolean SORT_ASCENDING = false;
	/** Sort constant: descending. */
	public static final boolean SORT_DESCENDING = true;

	/** PTTL error - no expire. */
	public static final long TTL_NO_EXPIRE = -1L;
	/** PTTL error - not exist. */
	public static final long TTL_NOT_EXIST = -2L;
	
	/**
	 * <p>From <a href="http://redis.io/commands/del">http://redis.io/commands/del</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of keys that 
	 * will be removed. When a key to remove holds a value other than a string, the 
	 * individual complexity for this key is O(M) where M is the number of elements 
	 * in the list, set, sorted set or hash. Removing a single key that holds a 
	 * string value is O(1).</p>
	 * <p>Removes the specified keys. A key is ignored if it does not exist.</p>
	 * @param keys the keys to delete.
	 * @return The number of keys that were removed.
	 */
	public long del(String... keys);

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
	 * <p>From <a href="http://redis.io/commands/migrate">http://redis.io/commands/migrate</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> This command actually executes 
	 * a {@link #dump} and {@link #del} in the source instance, and a {@link #restore} 
	 * in the target instance. See the pages of these commands for time complexity. 
	 * Also an O(N) data transfer between the two instances is performed.</p>
	 * <p>Atomically transfer a key from a source Redis instance to a 
	 * destination Redis instance. On success the key is deleted from the 
	 * original instance and is guaranteed to exist in the target instance.</p>
	 * @param host the hostname/address of the target server.
	 * @param port the port.
	 * @param key the key to migrate.
	 * @param destinationDB the database to target on the server.
	 * @param timeout the timeout for the connection.
	 * @return always true.
	 */
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout);

	/**
	 * <p>From <a href="http://redis.io/commands/migrate">http://redis.io/commands/migrate</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> This command actually executes a {@link #dump} 
	 * and {@link #del} in the source instance, and a {@link #restore} in the target 
	 * instance. See the pages of these commands for time complexity. Also an O(N) 
	 * data transfer between the two instances is performed.</p>
	 * <p>Atomically transfer a key from a source Redis instance to a destination 
	 * Redis instance. On success the key is deleted from the original instance 
	 * and is guaranteed to exist in the target instance.</p>
	 * @param host the hostname/address of the target server.
	 * @param port the port.
	 * @param key the key to migrate.
	 * @param destinationDB the database to target on the server.
	 * @param timeout the timeout for the connection.
	 * @param copy if true, the key is copied, not removed from the source.
	 * @param replace if true, the remote key is replaced.
	 * @return always true.
	 */
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout, boolean copy, boolean replace);

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
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals of Redis 
	 * Objects associated with keys. It is useful for debugging or to understand if 
	 * your keys are using the specially encoded data types to save space. Your 
	 * application may also use the information reported by the <code>object</code> 
	 * command to implement application level key eviction policies when using 
	 * Redis as a Cache.</p>
	 * <p>This call is here in order to support commands that don't have signatures.</p>
	 */
	public RedisObject object(String subcommand, String key);

	/**
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals of 
	 * Redis Objects associated with keys. It is useful for debugging or to 
	 * understand if your keys are using the specially encoded data types to 
	 * save space. Your application may also use the information reported by 
	 * the <code>object</code> command to implement application level key eviction 
	 * policies when using Redis as a Cache.</p>
	 * @param the key to count.
	 * @return the number of references of the value associated with the 
	 * specified key. This command is mainly useful for debugging.
	 */
	public long objectRefcount(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals 
	 * of Redis Objects associated with keys. It is useful for debugging or 
	 * to understand if your keys are using the specially encoded data types 
	 * to save space. Your application may also use the information reported 
	 * by the <code>object</code> command to implement application level key 
	 * eviction policies when using Redis as a Cache.</p>
	 * @param the key to inspect.
	 * @return the kind of internal representation used in order to store the 
	 * value associated with a key, or null for missing key.
	 * TODO: Return an enum?
	 */
	public String objectEncoding(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals of 
	 * Redis Objects associated with keys. It is useful for debugging or to 
	 * understand if your keys are using the specially encoded data types to 
	 * save space. Your application may also use the information reported by 
	 * the <code>object</code> command to implement application level key eviction 
	 * policies when using Redis as a Cache.</p>
	 * @param the key to inspect.
	 * @return the number of seconds since the object stored at the specified key 
	 * is idle (not requested by read or write operations). While the value is 
	 * returned in seconds the actual resolution of this timer is 10 seconds, 
	 * but may vary in future implementations.
	 */
	public long objectIdletime(String key);

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
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @return a two-element multi-bulk reply, where the first element is a string 
	 * representing an unsigned 64 bit number (the cursor), and the second element 
	 * is a multi-bulk with an array of elements.
	 */
	public Object scan(String cursor);

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @return a two-element multi-bulk reply, where the first element is a string 
	 * representing an unsigned 64 bit number (the cursor), and the second element 
	 * is a multi-bulk with an array of elements.
	 */
	public Object scan(String cursor, String pattern);

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a 
	 * complete iteration, including enough command calls for the cursor to return 
	 * back to 0. N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @return a two-element multi-bulk reply, where the first element is a string 
	 * representing an unsigned 64 bit number (the cursor), and the second element 
	 * is a multi-bulk with an array of elements.
	 */
	public Object scan(String cursor, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @return a two-element multi-bulk reply, where the first element is a string 
	 * representing an unsigned 64 bit number (the cursor), and the second element 
	 * is a multi-bulk with an array of elements.
	 */
	public Object scan(String cursor, String pattern, long count);

	/**
	 * <p>From <a href="http://redis.io/commands/sort">http://redis.io/commands/sort</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N+M*log(M)) where N is the number of 
	 * elements in the list or set to sort, and M the number of returned elements. 
	 * When the elements are not sorted, complexity is currently O(N) as there is 
	 * a copy step that will be avoided in next releases.</p>
	 * <p>Returns or stores the elements contained in the list, set, or sorted set 
	 * at <code>key</code>. By default, sorting is numeric and elements are compared 
	 * by their value interpreted as double precision floating point number.
	 * @param key the key to sort the contents of.
	 * @param desc if true, sort descending. if false, sort ascending.
	 * @param alpha if true, sort lexicographically, not by a score.
	 * @param offset the starting offset into the list (0-based).
	 * @param count the amount of objects from the offset to sort.
	 * @param storeKey if not null, this is the key to store the result in.
	 * @param getPatterns the patterns for finding the sort score.
	 * @return the list of sorted elements.
	 */
	public String[] sort(String key, boolean desc, boolean alpha, long offset, long count, String storeKey, String... getPatterns);

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
	 * @return the list of sorted elements.
	 */
	public String[] sortBy(String key, String pattern, boolean desc, boolean alpha, long offset, long count, String storeKey, String... getPatterns);

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
	 * <code>key</code>. The different types that can be returned are: <code>string</code>, 
	 * <code>list</code>, <code>set</code>, <code>zset</code> and <code>hash</code>.</p>
	 * @return the type of <code>key</code>, or <code>none</code> when <code>key</code> does not exist.
	 * TODO: Return as enum?
	 */
	public String type();

}
