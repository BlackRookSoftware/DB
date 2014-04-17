package com.blackrook.nosql.redis;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.blackrook.nosql.redis.exception.RedisException;
import com.blackrook.nosql.redis.io.RESPReader;
import com.blackrook.nosql.redis.io.RESPWriter;

/**
 * A single connection to a Redis server.
 * @author Matthew Tropiano
 */
public class RedisConnection implements Closeable
{
	/** The info describing the server to connect to. */
	private RedisInfo info;
	
	/** The socket connection. */
	private Socket redisSocket;
	/** The input wrapper. */
	private RESPReader reader;
	/** The output wrapper. */
	private RESPWriter writer;
	
	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisConnection(String host, int port) throws UnknownHostException, IOException
	{
		this(new RedisInfo(host, port));
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
	public RedisConnection(String host, int port, String password) throws UnknownHostException, IOException
	{
		this(new RedisInfo(host, port, password));
	}

	/**
	 * Creates an open connection.
	 * @param info the {@link RedisInfo} class detailing a connection.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public RedisConnection(RedisInfo info) throws UnknownHostException, IOException
	{
		this.info = info;
		reconnect();
	}

	/**
	 * Reconnects to a server.
	 * @throws IOException if an I/O error occurs when creating the socket or the socket is already open.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public void reconnect() throws UnknownHostException, IOException
	{
		if (redisSocket != null)
			throw new IOException("Socket is already open.");

		this.redisSocket = new Socket(info.getHost(), info.getPort());
		this.reader = new RESPReader(redisSocket.getInputStream());
		this.writer = new RESPWriter(redisSocket.getOutputStream());
		
		if (info.getPassword() != null)
		{
			writer.writeArray("AUTH", info.getPassword());
			reader.readString();
		}
	}
	
	/**
	 * Checks if this connection is successfully bound to an address.
	 * @return true if so, false if not.
	 */
	public boolean isBound()
	{
		return redisSocket.isBound();
	}
	
	/**
	 * Checks if this connection is connected to an endpoint.
	 * @return true if so, false if not.
	 */
	public boolean isConnected()
	{
		return redisSocket.isConnected();
	}
	
	/**
	 * Checks if this connection is closed.
	 * @return true if so, false if not.
	 */
	public boolean isClosed()
	{
		return redisSocket.isClosed();
	}
	
	@Override
	public void close() throws IOException
	{
		if (redisSocket == null)
			return;
		
		redisSocket.close();
		this.reader = null;
		this.writer = null;
		this.redisSocket = null;
	}
	
	/**
	 * Sends a raw request to a Redis server.
	 * @param arguments the list of arguments to send as a full request.
	 */
	public String sendRaw(String... arguments)
	{
		return sendRaw((Object[])arguments);
	}
	
	/**
	 * Sends a raw request to a Redis server.
	 * Each object is converted to a String via {@link String#valueOf(Object)}. 
	 * @param arguments the list of arguments to send as a full request.
	 */
	public String sendRaw(Object... arguments)
	{
		String[] out = new String[arguments.length];
		for(int i = 0; i < arguments.length; i++)
			out[i] = String.valueOf(arguments[i]);
		writer.writeArray(out);
		return reader.readRaw();
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
	
}
