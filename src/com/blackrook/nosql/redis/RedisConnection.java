package com.blackrook.nosql.redis;

import java.io.IOException;
import java.net.UnknownHostException;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.List;
import com.blackrook.nosql.redis.commands.RedisConnectionCommands;
import com.blackrook.nosql.redis.commands.RedisGenericCommands;
import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.exception.RedisException;

/**
 * A single connection to a Redis server.
 * @author Matthew Tropiano
 * TODO: Add more commands.
 */
public class RedisConnection extends RedisConnectionAbstract implements 
	RedisConnectionCommands, RedisGenericCommands
{
	
	private static final String[] COMMAND_DEL = new String[]{"DEL"};
	
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
	public long del(String... keys)
	{
		writer.writeArray(Common.joinArrays(COMMAND_DEL, keys));
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

	@Override
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout)
	{
		writer.writeArray("MIGRATE", host, port, key, destinationDB, timeout);
		return reader.readOK();
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

	@Override
	public Object scan(String cursor)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object scan(String cursor, String pattern)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object scan(String cursor, long count)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object scan(String cursor, String pattern, long count)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] sort(String key, boolean desc, boolean alpha, long offset,
			long count, String storeKey, String... getPatterns)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] sortBy(String key, String pattern, boolean desc,
			boolean alpha, long offset, long count, String storeKey,
			String... getPatterns)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long ttl(String key)
	{
		writer.writeArray("TTL", key);
		return reader.readInteger();
	}

	@Override
	public String type()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * <p>From <a href="http://redis.io/commands/hget">http://redis.io/commands/hget</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the value associated with <code>field</code> in the hash stored 
	 * at <code>key</code>, cast to a Long.</p>
	 * @return the value associated with <code>field</code>, or <code>null</code> 
	 * when <code>field</code> is not present in the hash or <code>key</code> does not exist.
	 */
//	public Long hgetAsLong(String key, String field);

	/**
	 * <p>From <a href="http://redis.io/commands/hget">http://redis.io/commands/hget</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the value associated with <code>field</code> in the hash stored 
	 * at <code>key</code>, cast to a primitive long.</p>
	 * @return the value associated with <code>field</code>, or <code>0</code> 
	 * when <code>field</code> is not present in the hash or <code>key</code> does not exist.
	 */
//	public long hgetLong(String key, String field);

	/**
	 * <p>From <a href="http://redis.io/commands/hgetall">http://redis.io/commands/hgetall</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the size of the hash.</p>
	 * <p>Returns all fields and values of the hash stored at <code>key</code>. 
	 * The value is returned as a {@link HashMap}.</p>
	 * @return a list of fields and their values stored in the hash, or an empty
	 * list when <code>key</code> does not exist.
	 */
//	public HashMap<String, String> hgetallAsMap(String key);

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
	 * @return the requested element cast to a long integer, or <code>null</code> when <code>index</code> is out of range.
	 */
//	public Long lindexAsLong(String key, long index);

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
	 * @return the requested element cast to a primitive long integer, or <code>0</code> when <code>index</code> is out of range.
	 */
//	public long lindexLong(String key, long index);

	/**
	 * <p>From <a href="http://redis.io/commands/lpop">http://redis.io/commands/lpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the first element of the list stored at <code>key</code>.</p>
	 * @return the value of the first element cast as a Long, or <code>null</code> when <code>key</code> does not exist.
	 */
//	public Long lpopAsLong(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/lpop">http://redis.io/commands/lpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the first element of the list stored at <code>key</code>.</p>
	 * @return the value of the first element cast as a primitive long integer, or <code>0</code> when <code>key</code> does not exist.
	 */
//	public long lpopLong(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/rpop">http://redis.io/commands/rpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the last element of the list stored at <code>key</code>.</p>
	 * @return the value of the last element cast to a Long, or <code>null</code> when <code>key</code> does not exist.
	 */
//	public Long rpopAsLong(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/rpop">http://redis.io/commands/rpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the last element of the list stored at <code>key</code>.</p>
	 * @return the value of the last element cast to a primitive long integer, 
	 * or <code>0</code> when <code>key</code> does not exist.
	 */
//	public long rpopLong(String key);
	
	/** 
	 *	Set multiple keys to multiple values
	 *	string
	 */
//	public void mset(ObjectPair<String, String>... keyValues);

	/** 
	 *	Set multiple keys to multiple values, only if none of the keys exist
	 *	string
	 */
//	public void msetnx(ObjectPair<String, String>... pairs);


}
