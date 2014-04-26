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
	private Socket socket;
	/** The input wrapper. */
	protected RESPReader reader;
	/** The output wrapper. */
	protected RESPWriter writer;
	
	/**
	 * Creates an open connection to localhost, port 6379, the default Redis port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisConnectionAbstract() throws IOException
	{
		this(new RedisInfo("localhost", 6379));
	}

	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisConnectionAbstract(String host, int port) throws IOException
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
		if (socket != null)
			throw new IOException("Socket is already open.");

		this.socket = new Socket(info.getHost(), info.getPort());
		
		if (info.getTimeout() > 0)
			socket.setSoTimeout(info.getTimeout());
		
		this.reader = new RESPReader(socket.getInputStream());
		this.writer = new RESPWriter(socket.getOutputStream());
		
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
		return socket.isBound();
	}
	
	/**
	 * Checks if this connection is connected to an endpoint.
	 * @return true if so, false if not.
	 */
	public boolean isConnected()
	{
		return socket.isConnected();
	}
	
	/**
	 * Checks if this connection is closed.
	 * @return true if so, false if not.
	 */
	public boolean isClosed()
	{
		return socket.isClosed();
	}
	
	@Override
	public void close() throws IOException
	{
		if (socket == null)
			return;
		
		socket.close();
		this.reader = null;
		this.writer = null;
		this.socket = null;
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		close();
		super.finalize();
	}
	
}
