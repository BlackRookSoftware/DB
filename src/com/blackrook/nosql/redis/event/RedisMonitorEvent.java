/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.event;

import java.util.Date;

import com.blackrook.commons.linkedlist.Queue;
import com.blackrook.nosql.redis.RedisMonitorConnection;
import com.blackrook.nosql.redis.exception.RedisParseException;

/**
 * An event class that is emitted from a {@link RedisMonitorConnection} whenever it receives
 * an event from the connection.
 * @author Matthew Tropiano
 */
public final class RedisMonitorEvent
{
	private static final String[] EMPTY_ARGS = new String[0];
	
	/** Server time in seconds. */
	private long serverTimeSeconds;
	/** Server time microsecond remainder. */
	private long serverTimeMicros;
	/** Server time as date. */
	private Date serverTime;
	/** Originating database id. */
	private int dbId;
	/** Source address. */
	private String address;
	/** Source port. */
	private int port;
	/** Command. */
	private String command;
	/** Arguments. */
	private String[] arguments;
	
	private RedisMonitorEvent()
	{
		arguments = EMPTY_ARGS;
	}
	
	/**
	 * Parses a string read by a MONITORed connection and
	 * turns it into an event.
	 * @throws RedisParseException if the monitor string is malformed.
	 */
	public static RedisMonitorEvent parse(String monitorString)
	{
		final int STATE_SECS = 0;
		final int STATE_MICROS = 1;
		final int STATE_AFTERMICROS = 2;
		final int STATE_DBID = 3;
		final int STATE_ADDRESS = 4;
		final int STATE_PORT = 5;
		final int STATE_BEFORECOMMAND = 6;
		final int STATE_COMMAND = 7;
		final int STATE_BEFOREARGUMENT = 8;
		final int STATE_ARGUMENT = 9;
		
		if (monitorString == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		RedisMonitorEvent out = new RedisMonitorEvent();
		boolean escape = false;
		
		int i = 0;
		int state = STATE_SECS;
		Queue<String> args = new Queue<String>();
		
		while (i < monitorString.length())
		{
			char c = monitorString.charAt(i);
			
			switch(state)
			{
			
				case STATE_SECS:
				{
					if (Character.isDigit(c))
						sb.append(c);
					else if (c == '.')
					{
						out.serverTimeSeconds = Long.parseLong(sb.toString());
						sb.delete(0, sb.length());
						state = STATE_MICROS;
					}
					else throw new RedisParseException("Malformed MONITOR response. Seconds: Expected digit or '.'");
				}
				break;
				
				case STATE_MICROS:
				{
					if (Character.isDigit(c))
						sb.append(c);
					else if (c == ' ')
					{
						out.serverTimeMicros = Long.parseLong(sb.toString());
						sb.delete(0, sb.length());
						out.serverTime = new Date((out.serverTimeSeconds * 1000) + (out.serverTimeMicros / 1000));
						state = STATE_AFTERMICROS;
					}
					else throw new RedisParseException("Malformed MONITOR response. Micros: Expected digit or space");
				}
				break;
				
				case STATE_AFTERMICROS:
				{
					if (c == '[')
					{
						state = STATE_DBID;
					}
					else throw new RedisParseException("Malformed MONITOR response. Expected '['");
				}
				break;
				
				case STATE_DBID:
				{
					if (Character.isDigit(c))
						sb.append(c);
					else if (c == ' ')
					{
						out.dbId = Integer.parseInt(sb.toString());
						sb.delete(0, sb.length());
						state = STATE_ADDRESS;
					}
					else throw new RedisParseException("Malformed MONITOR response. DB: Expected digit or space");
				}
				break;

				case STATE_ADDRESS:
				{
					if (Character.isDigit(c) || c == '.' || Character.isLetter(c))
						sb.append(c);
					else if (c == ':')
					{
						out.address = sb.toString();
						sb.delete(0, sb.length());
						state = STATE_PORT;
					}
					else throw new RedisParseException("Malformed MONITOR response. Address: Expected digit, letter, '.' or ':'");
				}
				break;

				case STATE_PORT:
				{
					if (Character.isDigit(c))
						sb.append(c);
					else if (c == ']')
					{
						out.port = Integer.parseInt(sb.toString());
						sb.delete(0, sb.length());
						state = STATE_BEFORECOMMAND;
					}
					else throw new RedisParseException("Malformed MONITOR response. Port: Expected digit or ']'");
				}
				break;
				
				case STATE_BEFORECOMMAND:
				{
					if (c == ' ')
					{
						// Do nothing
					}
					else if (c == '"')
					{
						state = STATE_COMMAND;
					}
					else throw new RedisParseException("Malformed MONITOR response. Expected start of command token");
				}
				break;
				
				case STATE_COMMAND:
				{
					if (c == '"')
					{
						out.command = sb.toString();
						sb.delete(0, sb.length());
						state = STATE_BEFOREARGUMENT;
					}
					else
					{
						sb.append(c);
					}
				}
				break;
				
				case STATE_BEFOREARGUMENT:
				{
					if (c == ' ')
					{
						// Do nothing
					}
					else if (c == '"')
					{
						state = STATE_ARGUMENT;
					}
					else throw new RedisParseException("Malformed MONITOR response. Expected start of argument token");
				}
				break;
				
				case STATE_ARGUMENT:
				{
					if (escape)
					{
						escape = false;
						sb.append(c);
					}
					else if (c == '\\')
					{
						sb.append(c);
						escape = true;
					}
					else if (c == '"')
					{
						args.add(sb.toString());
						sb.delete(0, sb.length());
						state = STATE_BEFOREARGUMENT;
					}
					else
					{
						sb.append(c);
					}
				}
				break;
				
			}
			
			i++;
		}
		
		if (state != STATE_BEFOREARGUMENT)
			throw new RedisParseException("Malformed MONITOR response. Message is incomplete.");
		
		if (args.size() > 0)
		{
			out.arguments = new String[args.size()];
			args.toArray(out.arguments);
		}
		
		return out;
	}

	/**
	 * Returns the server time of the event in seconds since the Epoch.
	 */
	public long getServerTimeSeconds()
	{
		return serverTimeSeconds;
	}

	/**
	 * Returns the server time remainder of the event in microseconds.
	 */
	public long getServerTimeMicros()
	{
		return serverTimeMicros;
	}

	/**
	 * Returns the server time as a {@link Date}.
	 */
	public Date getServerTime()
	{
		return serverTime;
	}

	/**
	 * Returns the originating Database id.
	 */
	public int getDBId()
	{
		return dbId;
	}

	/**
	 * Returns the originating IP address.
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * Returns the originating IP remote port.
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Returns the issued command.
	 */
	public String getCommand() 
	{
		return command;
	}

	/**
	 * Returns the commands arguments.
	 */
	public String[] getArguments()
	{
		return arguments;
	}
	
}
