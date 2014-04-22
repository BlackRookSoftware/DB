package com.blackrook.nosql.redis;

import java.io.IOException;
import java.net.UnknownHostException;

import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.exception.RedisException;

/**
 * A connection to Redis that allows completely open submission
 * to Redis. All requests are set via on type of method, and all
 * responses are returned with minimal interpretation.
 * @author Matthew Tropiano
 */
public class RedisUserConnection extends RedisConnectionAbstract
{

	/**
	 * Creates an open connection to localhost, port 6379, the default Redis port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisUserConnection() throws IOException
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
	public RedisUserConnection(String host, int port) throws IOException
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
	public RedisUserConnection(String host, int port, String password) throws IOException
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
	public RedisUserConnection(RedisInfo info) throws IOException
	{
		super(info);
	}

	/**
	 * Sends a request to a Redis server in the form of a literal command.
	 * This command is NOT sent verbatim - it is converted to a raw request before send.
	 * @param arguments the list of arguments to send as a full request.
	 * @return a {@link RedisObject} representing the response from the server.
	 */
	public RedisObject sendCommandString(String commandString)
	{
		writer.writeCommand(commandString);
		return reader.readObject();
	}
	
	/**
	 * Sends a request to a Redis server in the form of a literal command, already separated into tokens.
	 * This command is NOT sent verbatim - it is converted to a raw request before send.
	 * @param arguments the list of arguments to send as a full request.
	 * @return a {@link RedisObject} representing the response from the server.
	 */
	public RedisObject sendRequest(String... arguments)
	{
		writer.writeArray(arguments);
		return reader.readObject();
	}
	
	/**
	 * Sends a raw request to a Redis server in the form of a literal command, already separated into tokens.
	 * Each object is converted to a String via {@link String#valueOf(Object)}. 
	 * @param arguments the list of arguments to send as a full request.
	 * @return a {@link RedisObject} representing the response from the server.
	 */
	public RedisObject sendRequest(Object... arguments)
	{
		String[] out = new String[arguments.length];
		for(int i = 0; i < arguments.length; i++)
			out[i] = (arguments[i] instanceof String) ? (String)arguments[i] : String.valueOf(arguments[i]);
	
		writer.writeArray(out);
		return reader.readObject();
	}
	
}
