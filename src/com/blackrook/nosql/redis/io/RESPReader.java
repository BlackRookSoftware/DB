package com.blackrook.nosql.redis.io;

import java.io.IOException;
import java.io.InputStream;

import com.blackrook.commons.Common;
import com.blackrook.commons.list.DataList;
import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.exception.RedisException;
import com.blackrook.nosql.redis.exception.RedisParseException;

/**
 * Reader class for reading responses from a Redis Socket connection. 
 * @author Matthew Tropiano
 */
public class RESPReader
{
	/** Endline. */
	private static final String CRLF = "\r\n";
	
	private static final int BULK_STRING_LIMIT = 1024 * 1024 * 512;
	
	/** The wrapped reader. */
	private InputStream in;

	private DataList buffer;
	
	/**
	 * Creates a RedisReader attached to an input stream.
	 * @param in the {@link InputStream} to wrap.
	 */
	public RESPReader(InputStream in)
	{
		this.in = in;
		this.buffer = new DataList(1024);
	}
	
	/**
	 * Reads and expects "OK" from Redis.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF "OK" is expected.
	 * @return true.
	 * @throws RedisException if the server reports an error, or isn't OK.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public boolean readOK()
	{
		try {
			
			readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8");
			
			// is error?
			if (resp.startsWith("-"))
				throw new RedisException(resp.substring(1));
			// is OK?
			else if (resp.equals("+OK"))
				return true;
			else
				throw new RedisException("Expected OK.");
			
		} catch (IOException e) {
			throw new RedisParseException("Could not read from stream.", e);
		}
	}
	
	/**
	 * Reads and expects "QUEUED" from Redis.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF "QUEUED" is expected.
	 * @return true.
	 * @throws RedisException if the server reports an error, or isn't QUEUED.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public boolean readQueued()
	{
		try {
			
			readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8");
			
			// is error?
			if (resp.startsWith("-"))
				throw new RedisException(resp.substring(1));
			// is QUEUED?
			else if (resp.equals("+QUEUED"))
				return true;
			else
				throw new RedisException("Expected QUEUED.");
			
		} catch (IOException e) {
			throw new RedisParseException("Could not read from stream.", e);
		}
	}
	
	/**
	 * Reads and expects "PONG" from Redis.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF "PONG" is expected.
	 * @return true.
	 * @throws RedisException if the server reports an error, or isn't PONG.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public boolean readPong()
	{
		try {
			
			readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8");
			
			// is error?
			if (resp.startsWith("-"))
				throw new RedisException(resp.substring(1));
			// is OK?
			else if (resp.equals("+PONG"))
				return true;
			else
				throw new RedisException("Expected PONG.");
			
		} catch (IOException e) {
			throw new RedisParseException("Could not read from stream.", e);
		}
	}
	
	/**
	 * Reads and expects an Integer Reply from Redis.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF an Integer or Null is expected.
	 * @return a long or null reply.
	 * @throws RedisException if the server reports an error.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public Long readInteger()
	{
		try {
			
			readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8");
			
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
			throw new RedisParseException("Could not read from stream.", e);
		}
		
	}
	
	/**
	 * Reads and expects a String Reply from Redis, bulk or otherwise.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF a non-array or Null is expected. Integers are cast to Strings.
	 * @return a long or null reply.
	 * @throws RedisException if the server reports an error.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public String readString()
	{
		try {
			
			int buf = readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8"); 
			
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
					throw new RedisParseException("Server attempted to return bulk reply over MAX allowed.");

				try {
					buf = readBulk(buffer, len);
				} catch (IOException e) {
					throw new RedisParseException("Malformed response; expected "+len+"-byte string, got "+buf, e);
				}
				
				resp = new String(buffer.toByteArray(), "UTF-8");
				return resp;
			}
			else
				throw new RedisException("Expected string reply.");
				
		} catch (IOException e) {
			throw new RedisParseException("Could not read from stream.", e);
		}
		
	}
	
	/**
	 * Reads and expects an Array Reply from Redis.
	 * Will block until something is read from the stream.
	 * Should be used if AND ONLY IF an array or Null is expected. Integers are cast to Strings.
	 * @return an array or null reply. Arrays may contain null elements!
	 * @throws RedisException if the server reports an error.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public String[] readArray()
	{
		try {
			
			readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8"); 

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
			throw new RedisParseException("Could not read from stream.", e);
		}

	}
	
	/**
	 * Reads a full Redis data structure as a {@link RedisObject}.
	 * This is useful for commands that return complex or not-yet-supported responses.
	 * Will block until something is read completely from the stream.
	 * @return a full string with all breaks and newlines.
	 * @throws RedisException if the server reports an error.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public RedisObject readObject()
	{
		try {
			
			int buf = readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8"); 
			
			// is error?
			if (resp.startsWith("-")) 
				return RedisObject.createError(resp.substring(1));
			// is integer?
			else if (resp.startsWith(":")) 
				return RedisObject.create(Common.parseLong(resp.substring(1)));
			// is simple string?
			else if (resp.startsWith("+")) 
				return RedisObject.create(resp.substring(1));
			// is bulk string?
			else if (resp.startsWith("$"))
			{
				String lstr = resp.substring(1);
				int len = Common.parseInt(lstr);
				
				if (len == -1)
					return RedisObject.NULL;
				else if (len > BULK_STRING_LIMIT)
					throw new RedisParseException("Server attempted to return bulk reply over MAX allowed.");

				try {
					buf = readBulk(buffer, len);
				} catch (IOException e) {
					throw new RedisParseException("Malformed response; expected "+len+"-byte string, got "+buf, e);
				}
				
				resp = new String(buffer.toByteArray(), "UTF-8");
				return RedisObject.create(resp);
			}
			// is array
			else if (resp.startsWith("*"))
			{
				int len = Common.parseInt(resp.substring(1));
				if (len == -1)
					return RedisObject.NULL_ARRAY;

				RedisObject out = RedisObject.createEmptyArray(len);
				
				for (int i = 0; i < len; i++)
					out.set(i, readObject());

				return out;
			}
			else
				throw new RedisException("Expected RESP reply.");
				
		} catch (IOException e) {
			throw new RedisParseException("Could not read from stream.", e);
		}
		
	}
	
	/**
	 * Reads until a full Redis data structure is read.
	 * Will block until something is read completely from the stream.
	 * @return a full string with all breaks and newlines.
	 * @throws RedisException if the server reports an error.
	 * @throws RedisParseException if an error occurs during the read.
	 */
	public String readRaw()
	{
		try {
			
			int buf = readLine(buffer);
			String resp = new String(buffer.toByteArray(), "UTF-8"); 
			
			// is error?
			if (resp.startsWith("-") || resp.startsWith(":") || resp.startsWith("+"))
				return resp + CRLF;
			// is bulk string
			else if (resp.startsWith("$"))
			{
				String lstr = resp.substring(1);
				int len = Common.parseInt(lstr);
				
				if (len == -1)
					return "$-1" + CRLF;
				else if (len > BULK_STRING_LIMIT)
					throw new RedisParseException("Server attempted to return bulk reply over MAX allowed.");

				try {
					buf = readBulk(buffer, len);
				} catch (IOException e) {
					throw new RedisParseException("Malformed response; expected "+len+"-byte string, got "+buf, e);
				}
				
				resp = new String(buffer.toByteArray(), "UTF-8");
				return "$" + len + CRLF + resp + CRLF;
			}
			// is array
			else if (resp.startsWith("*"))
			{
				int len = Common.parseInt(resp.substring(1));
				if (len == -1)
					return "*-1" + CRLF;

				StringBuffer outb = new StringBuffer("*"+len+CRLF);
				
				for (int i = 0; i < len; i++)
					outb.append(readRaw());

				return outb.toString();
			}
			else
				throw new RedisException("Expected RESP reply.");
				
		} catch (IOException e) {
			throw new RedisParseException("Could not read from stream.", e);
		}
		
	}
	
	/**
	 * Reads bytes until CRLF and returns how many bytes in the "string" (before CRLF). 
	 */
	protected int readLine(DataList dl) throws IOException
	{
		dl.clear();
		final int STATE_READ = 0;
		final int STATE_CR = 1;
		final int STATE_LF = 2;
		int state = STATE_READ;
		
		byte b = 0x00;
		int buf = 0;
		
		while (state != STATE_LF)
		{
			b = (byte)(in.read() & 0x0ff);
			
			switch (state)
			{
				case STATE_READ:
				{
					if (b == '\r')
						state = STATE_CR;
					else
						dl.append(b);
					buf++;
				}
				break;
				case STATE_CR:
				{
					if (b == '\n')
						state = STATE_LF;
					else
					{
						dl.append((byte)'\r');
						dl.append(b);
						state = STATE_READ;
						buf += 2;
					}
				}
				break;
			}
			
		}
		
		return buf;
	}

	/**
	 * Reads bytes until CRLF and returns how many bytes in the "string" (before CRLF). 
	 */
	protected int readBulk(DataList dl, int len) throws IOException
	{
		dl.clear();
		final int STATE_READ = 0;
		final int STATE_CR = 1;
		final int STATE_LF = 2;
		final int STATE_END = 3;
		int state = STATE_READ;
		
		byte b = 0x00;
		int buf = 0;
		
		while (state != STATE_END)
		{
			b = (byte)(in.read() & 0x0ff);
			
			switch (state)
			{
				case STATE_READ:
				{
					dl.append(b);
					buf++;
					if (buf == len)
						state = STATE_CR;
				}
				break;
				case STATE_CR:
				{
					if (b == '\r')
						state = STATE_LF;
					else
						throw new IOException("Expected \\r at string end.");
				}
				break;
				case STATE_LF:
				{
					if (b == '\n')
						state = STATE_END;
					else
						throw new IOException("Expected \\r\\n at string end.");
				}
				break;
			}
			
		}
		
		return buf;
	}
	
}
