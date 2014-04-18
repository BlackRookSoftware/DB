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
public class RedisConnectionAbstract implements Closeable
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
	public RedisConnectionAbstract(String host, int port) throws UnknownHostException, IOException
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
	public RedisConnectionAbstract(String host, int port, String password) throws IOException
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
	public RedisConnectionAbstract(RedisInfo info) throws IOException
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
	public void reconnect() throws IOException
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
		writer.writeArray(arguments);
		return reader.readRaw();
	}
	
	/**
	 * Sends a raw request to a Redis server.
	 * Each object is converted to a String via {@link String#valueOf(Object)}. 
	 * @param arguments the list of arguments to send as a full request.
	 */
	public String sendRaw(Object... arguments)
	{
		writer.writeArray(toStringArray(arguments));
		return reader.readRaw();
	}

	/**
	 * Turns a series of objects into an array of strings.
	 */
	public static String[] toStringArray(Object... objects)
	{
		String[] out = new String[objects.length];
		for(int i = 0; i < objects.length; i++)
			out[i] = (objects[i] instanceof String) ? (String)objects[i] : String.valueOf(objects[i]);
		return out;
	}
	
}
