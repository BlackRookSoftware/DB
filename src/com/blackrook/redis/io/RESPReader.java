package com.blackrook.redis.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.blackrook.commons.Common;
import com.blackrook.redis.exception.RedisException;
import com.blackrook.redis.exception.RedisIOException;

/**
 * Reader class for reading responses from a Redis Socket connection. 
 * @author Matthew Tropiano
 */
public class RESPReader
{
	private static final int BULK_STRING_LIMIT = 1024 * 1024 * 512;
	
	/** The wrapped reader. */
	private BufferedReader in;

	/**
	 * Creates a RedisReader attached to an input stream.
	 * @param in the {@link InputStream} to wrap.
	 */
	public RESPReader(InputStream in)
	{
		try {
			this.in = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Reads a response from the stream.
	 * Will block until it reads.
	 * @return a RedisMixed object containing the response.
	public RedisMixed readMixed()
	{
		try {
			
		} catch (IOException e) {
			throw new RedisException(e);
		}
	}
	 */
	
	/**
	 * Reads and expects an Integer Reply from Redis.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF an Integer or Null is expected.
	 * @return a long or null reply.
	 * @throws RedisException if the server reports an error.
	 * @throws RedisIOException if an error occurs during the read.
	 */
	public Long readInteger()
	{
		try {
			
			String resp = in.readLine();
			// is error?
			if (resp.startsWith("-"))
				throw new RedisException(resp.substring(1));
			// is integer
			else if (resp.startsWith(":"))
				return Common.parseLong(resp.substring(1));
			// is null
			else if (resp.equals("$-1"))
				return null;
			else
				throw new RedisException("Expected integer reply.");
				
		} catch (IOException e) {
			throw new RedisIOException("Could not read from stream.", e);
		}
		
	}
	
	/**
	 * Reads and expects a String Reply from Redis, bulk or otherwise.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF a non-array or Null is expected. Integers are cast to Strings.
	 * @return a long or null reply.
	 * @throws RedisException if the server reports an error.
	 * @throws RedisIOException if an error occurs during the read.
	 */
	public String readString()
	{
		try {
			
			String resp = in.readLine();
			// is error?
			if (resp.startsWith("-"))
				throw new RedisException(resp.substring(1));
			// is integer
			else if (resp.startsWith(":"))
				return resp.substring(1);
			// is simple string
			else if (resp.startsWith("+"))
				return resp.substring(1);
			// is bulk string
			else if (resp.startsWith("$"))
			{
				String lstr = resp.substring(1);
				int len = Common.parseInt(lstr);
				
				if (len == -1)
					return null;
				else if (len > BULK_STRING_LIMIT)
					throw new RedisIOException("Server attempted to return bulk reply over MAX allowed.");
				
				resp = in.readLine();
				if (resp.length() != len)
					throw new RedisIOException("Malformed response; expected "+len+"-byte string, got "+lstr.getBytes().length);
				
				return resp;
			}
			else
				throw new RedisException("Expected string reply.");
				
		} catch (IOException e) {
			throw new RedisIOException("Could not read from stream.", e);
		}
		
	}
	
	/**
	 * Reads and expects an Array Reply from Redis.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF an array or Null is expected. Integers are cast to Strings.
	 * @return an array or null reply. Arrays may contain null elements!
	 * @throws RedisException if the server reports an error.
	 * @throws RedisIOException if an error occurs during the read.
	 */
	public String[] readArray()
	{
		try {
			
			String resp = in.readLine();
			// is error?
			if (resp.startsWith("-"))
				throw new RedisException(resp.substring(1));
			// is array
			else if (resp.startsWith("*"))
			{
				int len = Common.parseInt(resp.substring(1));
				if (len == -1)
					return null;

				String[] out = new String[len];
				for (int i = 0; i < len; i++)
					out[i] = readString();

				return out;
			}
			else
				throw new RedisException("Expected array reply.");
				
		} catch (IOException e) {
			throw new RedisIOException("Could not read from stream.", e);
		}

	}
	
}
