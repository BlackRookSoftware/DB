package com.blackrook.nosql.redis.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import com.blackrook.nosql.redis.RedisObject;

/**
 * Writer class for writing requests to a Redis Socket. 
 * @author Matthew Tropiano
 */
public class RESPWriter implements Closeable
{
	/** Endline. */
	private static final String CRLF = "\r\n";
	
	/** The wrapped writer. */
	private PrintWriter out;
	
	/**
	 * Opens a RedisWriter attached to an output stream. 
	 * @param out the {@link OutputStream} to use.
	 */
	public RESPWriter(OutputStream out)
	{
		try {
			this.out = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), false);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException
	{
		out.close();
	}
	
	/**
	 * Writes a null object.
	 */
	public void writeNull()
	{
		writeNull(true);
	}

	/**
	 * Writes a null object.
	 */
	public void writeNullArray()
	{
		out.write("*-1" + CRLF);
		out.flush();
	}

	/**
	 * Writes a number to output.
	 * @param n the number to write.
	 */
	public void writeNumber(Number n)
	{
		writeNumber(n, true);
	}

	/**
	 * Writes a simple string to output.
	 * @param s the string to write.
	 */
	public void writeSimpleString(String s)
	{
		writeSimpleString(s, true);
	}

	/**
	 * Writes a bulk string to output.
	 * @param s the string to write.
	 */
	public void writeBulkString(String s)
	{
		writeBulkString(s, true);
	}

	/**
	 * Writes an integer array.
	 * @param numbers the series of numbers.
	 */
	public void writeArray(Number ...numbers)
	{
		out.write("*" + numbers.length + CRLF);
		for (Number n : numbers)
			writeNumber(n, false);
		out.flush();
	}

	/**
	 * Writes a bulk string array.
	 * @param strings the series of strings.
	 */
	public void writeArray(String ...strings)
	{
		out.write("*" + strings.length + CRLF);
		for (String s : strings)
			writeBulkString(s, true);
		out.flush();
	}
	
	/**
	 * Writes a full object that represents a Redis request.
	 * @param strings the series of strings.
	 */
	public void writeObject(RedisObject object)
	{
		out.write(object.asRaw());
		out.flush();
	}
	
	/**
	 * Writes a character to output.
	 * @param c the character.
	 */
	public void writeChar(char c)
	{
		writeSimpleString(String.valueOf(c), true);
	}

	/**
	 * Writes an error to output.
	 * @param s the error string to write.
	 */
	public void writeError(String s)
	{
		out.write("-" + s + CRLF);
		out.flush();
	}

	/**
	 * Writes a null object.
	 */
	protected void writeNull(boolean flush)
	{
		out.write("$-1" + CRLF);
		if (flush) 
			out.flush();
	}

	/**
	 * Writes a number to output.
	 * @param n the number to write.
	 */
	protected void writeNumber(Number n, boolean flush)
	{
		if (n == null)
			writeNull(flush);
		if (n instanceof BigDecimal)
			writeSimpleString(String.valueOf(n), flush);
		else if (n instanceof Double || n instanceof Float)
			writeSimpleString(String.valueOf(n), flush);
		else
		{
			out.write(":" + String.valueOf(n) + CRLF);
		}
		if (flush) 
			out.flush();
	}

	/**
	 * Writes a bulk string to output.
	 * @param s the string to write.
	 */
	protected void writeSimpleString(String s, boolean flush)
	{
		out.write("+" + s + CRLF);
		if (flush) 
			out.flush();
	}

	/**
	 * Writes a bulk string.
	 * @param s the string to write.
	 */
	protected void writeBulkString(String s, boolean flush)
	{
		if (s == null)
			out.write("$-1" + CRLF);
		else
		{
			out.write("$" + s.length() + CRLF);
			out.write(s + CRLF);
		}
		if (flush) 
			out.flush();
	}
	
}
