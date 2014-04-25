package com.blackrook.nosql.redis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

import com.blackrook.commons.Common;
import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.Reflect;
import com.blackrook.commons.TypeProfile;
import com.blackrook.commons.TypeProfile.MethodSignature;
import com.blackrook.commons.hash.HashMap;
import com.blackrook.commons.list.List;
import com.blackrook.db.hints.DBIgnore;
import com.blackrook.nosql.redis.commands.RedisConnectionCommands;
import com.blackrook.nosql.redis.commands.RedisGenericCommands;
import com.blackrook.nosql.redis.commands.RedisHashCommands;
import com.blackrook.nosql.redis.commands.RedisHyperlogCommands;
import com.blackrook.nosql.redis.commands.RedisListCommands;
import com.blackrook.nosql.redis.commands.RedisScriptingCommands;
import com.blackrook.nosql.redis.commands.RedisSetCommands;
import com.blackrook.nosql.redis.commands.RedisStringCommands;
import com.blackrook.nosql.redis.data.RedisCursor;
import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.enums.BitwiseOperation;
import com.blackrook.nosql.redis.enums.DataType;
import com.blackrook.nosql.redis.enums.SortOrder;
import com.blackrook.nosql.redis.exception.RedisException;

/**
 * A single connection to a Redis server.
 * @author Matthew Tropiano
 * TODO: Add more commands.
 */
public class RedisConnection extends RedisConnectionAbstract implements 
	RedisConnectionCommands, RedisGenericCommands, RedisStringCommands, RedisHashCommands,
	RedisHyperlogCommands, RedisListCommands, RedisScriptingCommands, RedisSetCommands
{
	/**
	 * Creates an open connection to localhost, port 6379, the default Redis port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisConnection() throws IOException
	{
		super();
	}

	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisConnection(String host, int port) throws IOException
	{
		super(new RedisInfo(host, port));
	}

	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public RedisConnection(String host, int port, String password) throws IOException
	{
		super(new RedisInfo(host, port, password));
	}

	/**
	 * Creates an open connection.
	 * @param info the {@link RedisInfo} class detailing a connection.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public RedisConnection(RedisInfo info) throws IOException
	{
		super(info);
	}

	@Override
	public String echo(String message)
	{
		writer.writeArray("ECHO", message);
		return reader.readString();
	}

	@Override
	public long ping()
	{
		long time = System.currentTimeMillis();
		writer.writeArray("PING");
		reader.readPong();
		return System.currentTimeMillis() - time;
	}

	@Override
	public boolean quit()
	{
		writer.writeArray("QUIT");
		reader.readOK();
		try {
			close();
		} catch (IOException e) {
			throw new RedisException(e);
		}
		return true;
	}

	@Override
	public boolean select(long db)
	{
		writer.writeArray("SELECT", db);
		return reader.readOK();
	}

	@Override
	public long del(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"DEL", key}, keys));
		else
			writer.writeArray("DEL", key);
		return reader.readInteger();
	}

	@Override
	public String dump(String key)
	{
		writer.writeArray("DUMP", key);
		return reader.readString();
	}

	@Override
	public boolean exists(String key)
	{
		writer.writeArray("EXISTS", key);
		return reader.readInteger() != 0;
	}

	@Override
	public boolean expire(String key, long seconds)
	{
		writer.writeArray("EXPIRE", key, seconds);
		return reader.readInteger() != 0;
	}

	@Override
	public boolean expireat(String key, long timestamp)
	{
		writer.writeArray("EXPIREAT", key, timestamp);
		return reader.readInteger() != 0;
	}

	@Override
	public String[] keys(String pattern)
	{
		writer.writeArray("KEYS", pattern);
		return reader.readArray();
	}

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
	 * @return always true.
	 */
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout)
	{
		return migrate(host, port, key, destinationDB, timeout, false, false);
	}

	@Override
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout, boolean copy, boolean replace)
	{
		List<Object> out = new List<Object>(8);
		out.add("MIGRATE");
		out.add(host);
		out.add(port);
		out.add(key);
		out.add(destinationDB);
		out.add(timeout);
		if (copy)
			out.add("COPY");
		if (replace)
			out.add("REPLACE");
		writer.writeArray(out);
		return reader.readOK();
	}

	@Override
	public boolean move(String key, long db)
	{
		writer.writeArray("MOVE", key, db);
		return reader.readInteger() != 0;
	}

	@Override
	public RedisObject object(String subcommand, String key)
	{
		writer.writeArray("OBJECT", subcommand, key);
		return reader.readObject();
	}

	@Override
	public long objectRefcount(String key)
	{
		writer.writeArray("OBJECT", "REFCOUNT", key);
		return reader.readInteger();
	}

	@Override
	public String objectEncoding(String key)
	{
		writer.writeArray("OBJECT", "ENCODING", key);
		return reader.readString();
	}

	@Override
	public long objectIdletime(String key)
	{
		writer.writeArray("OBJECT", "IDLETIME", key);
		return reader.readInteger();
	}

	@Override
	public boolean persist(String key)
	{
		writer.writeArray("PERSIST", key);
		return reader.readInteger() != 0;
	}

	@Override
	public boolean pexpire(String key, long milliseconds)
	{
		writer.writeArray("PEXPIRE", key, milliseconds);
		return reader.readInteger() != 0;
	}

	@Override
	public boolean pexpireat(String key, long timestamp)
	{
		writer.writeArray("PEXPIREAT", key, timestamp);
		return reader.readInteger() != 0;
	}

	@Override
	public long pttl(String key)
	{
		writer.writeArray("PTTL", key);
		return reader.readInteger();
	}

	@Override
	public long publish(String channel, String message)
	{
		writer.writeArray("PUBLISH", channel, message);
		return reader.readInteger();
	}

	@Override
	public String randomkey()
	{
		writer.writeArray("RANDOMKEY");
		return reader.readString();
	}

	@Override
	public boolean rename(String key, String newkey)
	{
		writer.writeArray("RENAME", key, newkey);
		return reader.readOK();
	}

	@Override
	public boolean renamenx(String key, String newkey)
	{
		writer.writeArray("RENAMENX", key, newkey);
		return reader.readInteger() != 0;
	}

	@Override
	public boolean restore(String key, long ttl, String serializedvalue)
	{
		writer.writeArray("RESTORE", key, ttl, serializedvalue);
		return reader.readOK();
	}

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @param cursor the cursor value.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor scan(long cursor)
	{
		return scan(cursor, null, null);
	}

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor scan(long cursor, String pattern)
	{
		return scan(cursor, pattern, null);
	}

	/**
	 * <p>From <a href="http://redis.io/commands/scan">http://redis.io/commands/scan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0.
	 * N is the number of elements inside the collection..</p>
	 * <p>Incrementally iterates over a collection of elements.</p>
	 * @param cursor the cursor value.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor scan(long cursor, long count)
	{
		return scan(cursor, null, count);
	}

	@Override
	public RedisCursor scan(long cursor, String pattern, Long count)
	{
		if (pattern == null)
		{
			if (count == null)
				writer.writeArray("SCAN", cursor);
			else
				writer.writeArray("SCAN", cursor, "COUNT", count);
		}
		else
		{
			if (count == null)
				writer.writeArray("SCAN", cursor, "MATCH", pattern);
			else
				writer.writeArray("SCAN", cursor, "MATCH", pattern, "COUNT", count);
		}
		return RedisCursor.create(reader.readInteger(), reader.readArray());
	}

	@Override
	public String[] sort(String key, String pattern, SortOrder sortOrder, boolean alpha, Long limitOffset, Long limitCount, String storeKey, String... getPatterns)
	{
		List<Object> out = new List<Object>(13 + getPatterns.length);

		out.add("SORT");
		out.add(key);
		
		if (pattern != null)
		{
			out.add("BY");
			out.add(pattern);
		}

		if (limitOffset != null && limitCount != null)
		{
			out.add("LIMIT");
			out.add(limitOffset);
			out.add(limitCount);
		}

		if (getPatterns.length > 0)
		{
			out.add("GET");
			for (String gp : getPatterns)
				out.add(gp);
		}
		
		out.add(sortOrder != null ? sortOrder.name() : SortOrder.ASC);

		if (alpha)
			out.add("ALPHA");

		if (storeKey != null)
		{
			out.add("STORE");
			out.add(storeKey);
		}
		
		writer.writeArray(out);
		return reader.readArray();
	}

	@Override
	public long ttl(String key)
	{
		writer.writeArray("TTL", key);
		return reader.readInteger();
	}

	@Override
	public DataType type(String key)
	{
		writer.writeArray("TYPE", key);
		return Reflect.createForType(reader.readString(), DataType.class);
	}

	@Override
	public long append(String key, String value)
	{
		writer.writeArray("APPEND", key, value);
		return reader.readInteger();
	}

	@Override
	public long bitcount(String key)
	{
		writer.writeArray("BITCOUNT", key);
		return reader.readInteger();
	}

	@Override
	public long bitcount(String key, long start, long end)
	{
		writer.writeArray("BITCOUNT", key, start, end);
		return reader.readInteger();
	}

	@Override
	public long bitop(BitwiseOperation operation, String destkey, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BITOP", operation.name(), destkey, key}, keys));
		else
			writer.writeArray("BITOP", operation.name(), destkey, key);
		return reader.readInteger();
	}

	/**
	 * <p>From <a href="http://redis.io/commands/bitpos">http://redis.io/commands/bitpos</a>:</p>
	 * <p><strong>Available since 2.8.7.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N)</p>
	 * <p>Return the position of the first bit set to 1 or 0 in a string.</p>
	 * @return the command returns the position of the first bit set to 1 or 0 according to the request.
	 */
	public long bitpos(String key, long bit)
	{
		writer.writeArray("BITPOS", key, bit);
		return reader.readInteger();
	}

	@Override
	public long bitpos(String key, long bit, Long start, Long end)
	{
		List<Object> out = new List<Object>(5);
		out.add("BITPOS");
		out.add(key);
		out.add(bit);
		if (start != null)
			out.add(start);
		if (end != null)
			out.add(end);
		writer.writeArray(out);
		return reader.readInteger();
	}

	@Override
	public long decr(String key)
	{
		writer.writeArray("DECR", key);
		return reader.readInteger();
	}

	@Override
	public long decrby(String key, long decrement)
	{
		writer.writeArray("DECRBY", key, decrement);
		return reader.readInteger();
	}

	@Override
	public String get(String key)
	{
		writer.writeArray("GET", key);
		return reader.readString();
	}
	
	/**
	 * Just like {@link #get(String)}, but it casts the result to a long integer.
	 */
	public Long getLong(String key)
	{
		String out = get(key);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public long getbit(String key, long offset)
	{
		writer.writeArray("GETBIT", key, offset);
		return reader.readInteger();
	}

	@Override
	public String getrange(String key, long start, long end)
	{
		writer.writeArray("GETRANGE", key, start, end);
		return reader.readString();
	}

	@Override
	public String getset(String key, String value)
	{
		writer.writeArray("GETSET", key, value);
		return reader.readString();
	}

	/**
	 * <p>From <a href="http://redis.io/commands/getset">http://redis.io/commands/getset</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Atomically sets <code>key</code> to <code>value</code> and returns the 
	 * old value stored at <code>key</code>. Returns an error when <code>key</code> 
	 * exists but does not hold a string value.</p>
	 * @return the old value stored at <code>key</code>, or <code>null</code> when <code>key</code> did not exist.
	 */
	public String getset(String key, Number value)
	{
		writer.writeArray("GETSET", key, value);
		return reader.readString();
	}

	/**
	 * Just like {@link #getset(String, String)}, but it casts the result to a long integer.
	 */
	public Long getsetLong(String key, String value)
	{
		String out = getset(key, value);
		return out != null ? Common.parseLong(out) : null;
	}

	/**
	 * Just like {@link #getset(String, String)}, but it casts the result to a long integer.
	 */
	public Long getsetLong(String key, Number value)
	{
		String out = getset(key, value);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public long incr(String key)
	{
		writer.writeArray("INCR", key);
		return reader.readInteger();
	}

	@Override
	public long incrby(String key, long increment)
	{
		writer.writeArray("INCRBY", key, increment);
		return reader.readInteger();
	}

	@Override
	public double incrbyfloat(String key, double increment)
	{
		writer.writeArray("INCRBYFLOAT", key, increment);
		return Common.parseDouble(reader.readString());
	}

	@Override
	public String[] mget(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"MGET", key}, keys));
		else
			writer.writeArray("MGET", key);
		return reader.readArray();
	}

	@Override
	public boolean mset(String key, String value, String... keyValues)
	{
		if (keyValues.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"MSET", key, value}, keyValues));
		else
			writer.writeArray("MSET", key, value);
		return reader.readOK();
	}

	/**
	 * Like {@link #mset(String, String, String...)}, but takes key-value pairs.
	 */
	public boolean mset(ObjectPair<String, Object>... pairs)
	{
		if (pairs.length == 0)
			return true;
		
		List<Object> out = new List<Object>(1 + (pairs.length * 2));
		out.add("MSET");
		
		for (ObjectPair<String, Object> p : pairs)
		{
			if (p.getValue() != null)
			{
				out.add(p.getKey());
				out.add(String.valueOf(p.getValue()));
			}
		}
		writer.writeArray(out);
		return reader.readOK();
	}

	@Override
	public boolean msetnx(String key, String value, String... keyValues)
	{
		if (keyValues.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"MSETNX", key, value}, keyValues));
		else
			writer.writeArray("MSETNX", key, value);
		return reader.readOK();
	}

	/** 
	 * Like {@link #msetnx(String, String, String...)}, but takes key-value pairs.
	 */
	public boolean msetnx(ObjectPair<String, Object>... pairs)
	{
		if (pairs.length == 0)
			return false;
		
		List<Object> out = new List<Object>(1 + (pairs.length * 2));
		out.add("MSETNX");
		
		for (ObjectPair<String, Object> p : pairs)
		{
			if (p.getValue() != null)
			{
				out.add(p.getKey());
				out.add(String.valueOf(p.getValue()));
			}
		}
		writer.writeArray(out);
		return reader.readInteger() != 0;
	}

	@Override
	public boolean psetex(String key, long milliseconds, String value)
	{
		writer.writeArray("PSETEX", key, milliseconds, value);
		return reader.readOK();
	}

	@Override
	public boolean set(String key, String value)
	{
		writer.writeArray("SET", key, value);
		return reader.readOK();
	}

	/**
	 * <p>From <a href="http://redis.io/commands/set">http://redis.io/commands/set</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Set <code>key</code> to hold the string <code>value</code>. If <code>key</code> 
	 * already holds a value, it is overwritten, regardless of its type. Any previous time 
	 * to live associated with the key is discarded on successful <code>SET</code> operation.</p>
	 * @return true, always.
	 */
	public boolean set(String key, Number value)
	{
		writer.writeArray("SET", key, value);
		return reader.readOK();
	}

	@Override
	public long setbit(String key, long offset, long value)
	{
		writer.writeArray("SETBIT", key, offset, value);
		return reader.readInteger();
	}

	@Override
	public boolean setex(String key, long seconds, String value)
	{
		writer.writeArray("SETEX", key, seconds, value);
		return reader.readOK();
	}

	@Override
	public boolean setnx(String key, String value)
	{
		writer.writeArray("SETNX", key, value);
		return reader.readInteger() != 0;
	}

	@Override
	public long setrange(String key, long offset, String value)
	{
		writer.writeArray("SETRANGE", key, offset, value);
		return reader.readInteger();
	}

	@Override
	public long strlen(String key)
	{
		writer.writeArray("STRLEN", key);
		return reader.readInteger();
	}

	@Override
	public long hdel(String key, String field, String... fields)
	{
		if (fields.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"HDEL", key, field}, fields));
		else
			writer.writeArray("HDEL", key, field);
		return reader.readInteger();
	}

	@Override
	public boolean hexists(String key, String field)
	{
		writer.writeArray("HEXISTS", key, field);
		return reader.readInteger() != 0;
	}

	@Override
	public String hget(String key, String field)
	{
		writer.writeArray("HGET", key, field);
		return reader.readString();
	}

	/**
	 * Just like {@link #hget(String, String)}, but it casts the result to a long integer.
	 */
	public Long hgetLong(String key, String field)
	{
		String out = hget(key, field);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public String[] hgetall(String key)
	{
		writer.writeArray("HGETALL", key);
		return reader.readArray();
	}

	/**
	 * Just like {@link #hgetall(String)}, except the keys and values are returned in a map of key -> value.
	 */
	public HashMap<String, String> hgetallMap(String key)
	{
		String[] keyvals = hgetall(key);
		HashMap<String, String> out = new HashMap<String, String>(keyvals.length / 2);
		for (int i = 0; i < keyvals.length; i += 2)
			out.put(keyvals[i], keyvals[i + 1]);
		return out;
	}

	/**
	 * Just like {@link #hgetall(String)}, except the keys and values are set on 
	 * an existing instance of a Java object via reflection. Fields/Setter Methods annotated with
	 * {@link DBIgnore} are ignored.
	 * @throws RuntimeException if instantiation cannot happen, either due to
	 * a non-existent constructor or a non-visible constructor.
	 * @throws ClassCastException if a incoming type cannot be converted to a field value.
	 */
	public <T> T hgetallObject(String key, Class<T> type)
	{
		return hgetallObject(key, Reflect.create(type));
	}

	/**
	 * Just like {@link #hgetall(String)}, except the keys and values are set on 
	 * a new instance of a Java object via reflection. Fields/Setter Methods annotated with
	 * {@link DBIgnore} are ignored.
	 * @throws RuntimeException if instantiation cannot happen, either due to
	 * a non-existent constructor or a non-visible constructor.
	 * @throws ClassCastException if a incoming type cannot be converted to a field value.
	 */
	public <T> T hgetallObject(String key, T object)
	{
		TypeProfile<?> profile = TypeProfile.getTypeProfile(object.getClass());
		
		String[] keyvals = hgetall(key);
		for (int i = 0; i < keyvals.length; i += 2)
		{
			String k = keyvals[i];
			String v = keyvals[i + 1];
			
			if (profile.getPublicFields().containsKey(k))
			{
				Field f = profile.getPublicFields().get(k);
				if (!f.isAnnotationPresent(DBIgnore.class))
					Reflect.setField(object, k, Reflect.createForType(k, v, f.getType()));
			}
			else if (profile.getSetterMethods().containsKey(k))
			{
				MethodSignature ms = profile.getSetterMethods().get(k);
				if (!ms.getMethod().isAnnotationPresent(DBIgnore.class))
					Reflect.invokeBlind(ms.getMethod(), object, Reflect.createForType(k, v, ms.getType()));
			}
		}
		
		return object;
	}

	@Override
	public long hincrby(String key, String field, long increment)
	{
		writer.writeArray("HINCRBY", key, field, increment);
		return reader.readInteger();
	}

	@Override
	public double hincrbyfloat(String key, String field, double increment)
	{
		writer.writeArray("HINCRBYFLOAT", key, field, increment);
		return Common.parseDouble(reader.readString());
	}

	@Override
	public String[] hkeys(String key)
	{
		writer.writeArray("HKEYS", key);
		return reader.readArray();
	}

	@Override
	public long hlen(String key)
	{
		writer.writeArray("HLEN", key);
		return reader.readInteger();
	}

	@Override
	public String[] hmget(String key, String field, String... fields)
	{
		if (fields.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"HMGET", key, field}, fields));
		else
			writer.writeArray("HMGET", key, field);
		return reader.readArray();
	}

	@Override
	public boolean hmset(String key, String field, String value, String... fieldvalues)
	{
		if (fieldvalues.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"HMSET", key, field}, fieldvalues));
		else
			writer.writeArray("HMSET", key, field, value);
		return reader.readOK();
	}

	/**
	 * Like {@link #hmset(String, String, String, String...)}, except abstracted as {@link ObjectPair}s of
	 * key-value pairs.
	 */
	public boolean hmset(String key, ObjectPair<String, Object>... pairs)
	{
		if (pairs.length == 0)
			return true;
		
		List<Object> out = new List<Object>(2 + (pairs.length * 2));
		out.add("HMSET");
		out.add(key);
		
		for (ObjectPair<String, Object> p : pairs)
		{
			if (p.getValue() != null)
			{
				out.add(p.getKey());
				out.add(String.valueOf(p.getValue()));
			}
		}
		writer.writeArray(out);
		return reader.readOK();
	}

	/**
	 * Like {@link #hmset(String, String, String, String...)}, except abstracted as a map of
	 * key-value pairs.
	 */
	public boolean hmsetMap(String key, HashMap<String, Object> map)
	{
		if (map.isEmpty())
			return true;
		
		List<Object> out = new List<Object>(2 + (map.size() * 2));
		out.add("HMSET");
		out.add(key);
		
		for (ObjectPair<String, Object> p : map)
		{
			if (p.getValue() != null)
			{
				out.add(p.getKey());
				out.add(String.valueOf(p.getValue()));
			}
		}
		writer.writeArray(out);
		return reader.readOK();
	}

	/**
	 * Like {@link #hmset(String, String, String, String...)}, except each field or
	 * getter sets the fields and values. Fields/Getter Methods annotated with
	 * {@link DBIgnore} are ignored.
	 */
	public boolean hmsetObject(String key, Object object)
	{
		Class<?> type = object.getClass();
		TypeProfile<?> profile = TypeProfile.getTypeProfile(type);
		
		List<Object> out = new List<Object>(2 + ((profile.getPublicFields().size() + profile.getSetterMethods().size())*2));
		out.add("HMSET");
		out.add(key);

		for (ObjectPair<String, Field> pair : profile.getPublicFields())
		{
			Field f = pair.getValue();
			if (!f.isAnnotationPresent(DBIgnore.class))
			{
				String k = pair.getKey();
				Object v = Reflect.getFieldValue(f, object);
				if (v != null && !f.isAnnotationPresent(DBIgnore.class))
				{
					out.add(k);
					out.add(v);
				}
			}
		}
		
		for (Method method : type.getMethods())
		{
			if (Reflect.isGetter(method) && !method.isAnnotationPresent(DBIgnore.class))
			{
				String k = Reflect.getFieldName(method.getName());
				Object v = Reflect.invokeBlind(method, object);
				if (v != null)
				{
					out.add(k);
					out.add(v);
				}
			}
			
		}
		
		writer.writeArray(out);
		return reader.readOK();
	}

	@Override
	public boolean hset(String key, String field, String value)
	{
		writer.writeArray("HSET", key, field, value);
		return reader.readInteger() != 0;
	}

	/**
	 * <p>From <a href="http://redis.io/commands/hset">http://redis.io/commands/hset</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Sets <code>field</code> in the hash stored at <code>key</code> to <code>value</code>. 
	 * If <code>key</code> does not exist, a new key holding a hash is created. If 
	 * <code>field</code> already exists in the hash, it is overwritten.</p>
	 * @return true if a new field, false if set, but not a new field.
	 */
	public boolean hset(String key, String field, Number value)
	{
		writer.writeArray("HSET", key, field, value);
		return reader.readInteger() != 0;
	}

	@Override
	public boolean hsetnx(String key, String field, String value)
	{
		writer.writeArray("HSETNX", key, field, value);
		return reader.readInteger() != 0;
	}

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
	public boolean hsetnx(String key, String field, Number value)
	{
		writer.writeArray("HSETNX", key, field, value);
		return reader.readInteger() != 0;
	}

	@Override
	public String[] hvals(String key)
	{
		writer.writeArray("HVALS", key);
		return reader.readArray();
	}

	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor hscan(String key, long cursor)
	{
		return hscan(key, cursor, null, null);
	}
	
	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor hscan(String key, long cursor, String pattern)
	{
		return hscan(key, cursor, pattern, null);
	}
	
	/**
	 * <p>From <a href="http://redis.io/commands/sscan">http://redis.io/commands/sscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor hscan(String key, long cursor, long count)
	{
		return hscan(key, cursor, null, count);
	}
	
	@Override
	public RedisCursor hscan(String key, long cursor, String pattern, Long count)
	{
		if (pattern == null)
		{
			if (count == null)
				writer.writeArray("HSCAN", key, cursor);
			else
				writer.writeArray("HSCAN", key, cursor, "COUNT", count);
		}
		else
		{
			if (count == null)
				writer.writeArray("HSCAN", key, cursor, "MATCH", pattern);
			else
				writer.writeArray("HSCAN", key, cursor, "MATCH", pattern, "COUNT", count);
		}
		return RedisCursor.create(reader.readInteger(), reader.readArray());
	}

	@Override
	public boolean pfadd(String key, String element, String... elements)
	{
		if (elements.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"PFADD", key, element}, elements));
		else
			writer.writeArray("PFADD", key, element);
		return reader.readInteger() != 0;
	}

	@Override
	public long pfcount(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"PFCOUNT",key}, keys));
		else
			writer.writeArray("PFCOUNT", key);
		return reader.readInteger();
	}

	@Override
	public boolean pfmerge(String destkey, String sourcekey, String... sourcekeys)
	{
		if (sourcekeys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"PFMERGE", destkey, sourcekey}, sourcekeys));
		else
			writer.writeArray("PFMERGE", destkey, sourcekey);
		return reader.readOK();
	}

	@Override
	public ObjectPair<String, String> blpop(long timeout, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BLPOP", key}, keys, new Object[]{timeout}));
		else
			writer.writeArray("BLPOP", key, timeout);
		String[] resp = reader.readArray();
		return resp != null ? new ObjectPair<String, String>(resp[0], resp[1]) : null;
	}

	/**
	 * Like {@link #blpop(long, String, String...)}, except it casts the value to a long integer. 
	 */
	public ObjectPair<String, Long> blpopLong(long timeout, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BLPOP", key}, keys, new Object[]{timeout}));
		else
			writer.writeArray("BLPOP", key, timeout);
		String[] resp = reader.readArray();
		return resp != null ? new ObjectPair<String, Long>(resp[0], Common.parseLong(resp[1])) : null;
	}

	@Override
	public ObjectPair<String, String> brpop(long timeout, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BRPOP", key}, keys, new Object[]{timeout}));
		else
			writer.writeArray("BRPOP", key, timeout);
		String[] resp = reader.readArray();
		return resp != null ? new ObjectPair<String, String>(resp[0], resp[1]) : null;
	}

	/**
	 * Like {@link #brpop(long, String, String...)}, except it casts the value to a long integer. 
	 */
	public ObjectPair<String, Long> brpopLong(long timeout, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BRPOP", key}, keys, new Object[]{timeout}));
		else
			writer.writeArray("BRPOP", key, timeout);
		String[] resp = reader.readArray();
		return resp != null ? new ObjectPair<String, Long>(resp[0], Common.parseLong(resp[1])) : null;
	}

	@Override
	public String brpoplpush(long timeout, String source, String destination)
	{
		writer.writeArray("BRPOPLPUSH", source, destination, timeout);
		return reader.readString();
	}

	/**
	 * Like {@link #brpoplpush(long, String, String)}, except it casts the value to a long integer. 
	 */
	public Long brpoplpushLong(long timeout, String source, String destination)
	{
		String out = brpoplpush(timeout, source, destination);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public String lindex(String key, long index)
	{
		writer.writeArray("LINDEX", key, index);
		return reader.readString();
	}

	/**
	 * Like {@link #lindex(String, long)}, except it casts the value to a long integer.
	 */
	public Long lindexLong(String key, long index)
	{
		String out = lindex(key, index);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public long linsert(String key, boolean before, String pivot, String value)
	{
		writer.writeArray("LINSERT", key, before ? "BEFORE" : "AFTER", pivot, value);
		return reader.readInteger();
	}

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
	public long linsert(String key, boolean before, String pivot, Number value)
	{
		writer.writeArray("LINSERT", key, before ? "BEFORE" : "AFTER", pivot, value);
		return reader.readInteger();
	}

	@Override
	public long llen(String key)
	{
		writer.writeArray("LLEN", key);
		return reader.readInteger();
	}

	@Override
	public String lpop(String key)
	{
		writer.writeArray("LPOP", key);
		return reader.readString();
	}

	/**
	 * Like {@link #lpop(String)}, except it casts the value to a long integer.
	 */
	public Long lpopLong(String key)
	{
		String out = lpop(key);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public long lpush(String key, String value, String... values)
	{
		if (values.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"LPUSH", key, value}, values));
		else
			writer.writeArray("LPUSH", key, value);
		return reader.readInteger();
	}

	@Override
	public long lpushx(String key, String value)
	{
		writer.writeArray("LPUSHX", key, value);
		return reader.readInteger();
	}

	@Override
	public String[] lrange(String key, long start, long stop)
	{
		writer.writeArray("LPUSHX", key, start, stop);
		return reader.readArray();
	}

	@Override
	public long lrem(String key, long count, String value)
	{
		writer.writeArray("LREM", key, count, value);
		return reader.readInteger();
	}

	@Override
	public boolean lset(String key, long index, String value)
	{
		writer.writeArray("LSET", key, index, value);
		return reader.readOK();
	}

	@Override
	public boolean ltrim(String key, long start, long stop)
	{
		writer.writeArray("LTRIM", key, start, stop);
		return reader.readOK();
	}

	@Override
	public String rpop(String key)
	{
		writer.writeArray("LPOP", key);
		return reader.readString();
	}

	/**
	 * Like {@link #rpop(String)}, except it casts the value to a long integer. 
	 */
	public Long rpopLong(String key)
	{
		String out = rpop(key);
		return out != null ? Common.parseLong(out) : null;
	}
	
	@Override
	public String rpoplpush(String source, String destination)
	{
		writer.writeArray("RPOPLPUSH", source, destination);
		return reader.readString();
	}

	/**
	 * Like {@link #rpoplpush(String, String)}, except it casts the value to a long integer. 
	 */
	public Long rpoplpushLong(String source, String destination)
	{
		String out = rpoplpush(source, destination);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public long rpush(String key, String value, String... values)
	{
		if (values.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"RPUSH", key, value}, values));
		else
			writer.writeArray("RPUSH", key, value);
		return reader.readInteger();
	}

	@Override
	public long rpushx(String key, String value)
	{
		writer.writeArray("RPUSHX", key, value);
		return reader.readInteger();
	}

	@Override
	public RedisObject eval(String scriptContent, String[] keys, String[] args)
	{
		writer.writeArray(Common.joinArrays(new Object[]{"EVAL", scriptContent, keys.length}, keys, args));
		return reader.readObject();
	}

	@Override
	public RedisObject evalsha(String hash, String[] keys, String[] args)
	{
		writer.writeArray(Common.joinArrays(new Object[]{"EVAL", hash, keys.length}, keys, args));
		return reader.readObject();
	}

	@Override
	public boolean[] scriptExists(String scriptHash, String... scriptHashes)
	{
		if (scriptHashes.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SCRIPT", "EXISTS", scriptHash}, scriptHashes));
		else
			writer.writeArray("SCRIPT", "EXISTS", scriptHash);
		String[] ret = reader.readArray();
		boolean[] out = new boolean[ret.length];
		for (int i = 0; i < ret.length; i++)
			out[i] = Common.parseLong(ret[i]) != 0;
		
		return out;
	}

	@Override
	public boolean scriptFlush()
	{
		writer.writeArray("SCRIPT", "FLUSH");
		return reader.readOK();
	}

	@Override
	public boolean scriptKill(String hash)
	{
		writer.writeArray("SCRIPT", "KILL", hash);
		return reader.readOK();
	}

	@Override
	public String scriptLoad(String content)
	{
		writer.writeArray("SCRIPT", "LOAD", content);
		return reader.readString();
	}

	/**
	 * <p>From <a href="http://redis.io/commands/script-load">http://redis.io/commands/script-load</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the length in bytes of the script body.</p>
	 * <p>Load a script into the scripts cache from the specified file without executing it. After the specified 
	 * command is loaded into the script cache it will be callable using {@link #evalsha(String, String[], String[])} 
	 * with the correct SHA1 digest of the script, exactly like after the first successful invocation of {@link #eval(String, String[], String[])}.</p>
	 * @return the SHA1 digest of the script added into the script cache.
	 */
	public String scriptLoad(File content) throws IOException
	{
		return scriptLoad(Common.getTextualContents(content));
	}

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
	public String scriptLoad(InputStream content) throws IOException
	{
		return scriptLoad(Common.getTextualContents(content));
	}

	@Override
	public long sadd(String key, String member, String... members)
	{
		if (members.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SADD", key, member}, members));
		else
			writer.writeArray("SADD", key, member);
		return reader.readInteger();
	}

	@Override
	public long scard(String key)
	{
		writer.writeArray("SCARD", key);
		return reader.readInteger();
	}

	@Override
	public String[] sdiff(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SDIFF", key}, keys));
		else
			writer.writeArray("SDIFF", key);
		return reader.readArray();
	}

	@Override
	public long sdiffstore(String destination, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SDIFFSTORE", destination, key}, keys));
		else
			writer.writeArray("SDIFFSTORE", destination, key);
		return reader.readInteger();
	}

	@Override
	public String[] sinter(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SINTER", key}, keys));
		else
			writer.writeArray("SINTER", key);
		return reader.readArray();
	}

	@Override
	public long sinterstore(String destination, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SINTERSTORE", destination, key}, keys));
		else
			writer.writeArray("SINTERSTORE", destination, key);
		return reader.readInteger();
	}

	@Override
	public boolean sismember(String key, String member)
	{
		writer.writeArray("SISMEMBER", key, member);
		return reader.readInteger() != 0;
	}

	@Override
	public String[] smembers(String key)
	{
		writer.writeArray("SMEMBERS", key);
		return reader.readArray();
	}

	@Override
	public boolean smove(String source, String destination, String member)
	{
		writer.writeArray("SMOVE", source, destination, member);
		return reader.readInteger() != 0;
	}

	@Override
	public String spop(String key)
	{
		writer.writeArray("SPOP", key);
		return reader.readString();
	}

	/**
	 * Like {@link #spop(String)}, except it casts the value to a long integer. 
	 */
	public Long spopLong(String key)
	{
		String out = spop(key);
		return out != null ? Common.parseLong(out) : null;
	}

	@Override
	public String srandmember(String key)
	{
		writer.writeArray("SRANDMEMBER", key);
		return reader.readString();
	}

	@Override
	public String[] srandmember(String key, long count)
	{
		writer.writeArray("SRANDMEMBER", key, count);
		return reader.readArray();
	}

	@Override
	public long srem(String key, String member, String... members)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] sunion(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SUNION", key}, keys));
		else
			writer.writeArray("SUNION", key);
		return reader.readArray();
	}

	@Override
	public long sunionstore(String destination, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SUNIONSTORE", destination, key}, keys));
		else
			writer.writeArray("SUNIONSTORE", destination, key);
		return reader.readInteger();
	}

	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, long cursor)
	{
		return hscan(key, cursor, null, null);
	}
	
	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param pattern if not null, return keys that fit a pattern.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, long cursor, String pattern)
	{
		return hscan(key, cursor, pattern, null);
	}
	
	/**
	 * <p>From <a href="http://redis.io/commands/hscan">http://redis.io/commands/hscan</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every call. O(N) for a complete 
	 * iteration, including enough command calls for the cursor to return back to 0. 
	 * N is the number of elements inside the collection..</p>
	 * @param key the key of the hash to scan.
	 * @param cursor the cursor value.
	 * @param count if not null, cap the iterable keys at a limit.
	 * @return a RedisCursor that represents the result of a SCAN call.
	 */
	public RedisCursor sscan(String key, long cursor, long count)
	{
		return hscan(key, cursor, null, count);
	}
	
	@Override
	public RedisCursor sscan(String key, String cursor, String pattern, Long count)
	{
		if (pattern == null)
		{
			if (count == null)
				writer.writeArray("SSCAN", key, cursor);
			else
				writer.writeArray("SSCAN", key, cursor, "COUNT", count);
		}
		else
		{
			if (count == null)
				writer.writeArray("SSCAN", key, cursor, "MATCH", pattern);
			else
				writer.writeArray("SSCAN", key, cursor, "MATCH", pattern, "COUNT", count);
		}
		return RedisCursor.create(reader.readInteger(), reader.readArray());
	}

}
