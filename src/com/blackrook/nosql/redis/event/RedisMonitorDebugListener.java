/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.event;

import java.io.PrintStream;

/**
 * An adapter class for {@link RedisMonitorListener} that prints messages to a {@link PrintStream}
 * when events are heard.
 * @author Matthew Tropiano
 */
public class RedisMonitorDebugListener implements RedisMonitorListener
{
	/** Output stream for messages. */
	private PrintStream out;
	
	/**
	 * Creates a listener that dumps to {@link System#out}.
	 */
	public RedisMonitorDebugListener()
	{
		this(System.out);
	}
	
	/**
	 * Creates a listener.
	 * @param out the {@link PrintStream} to dump output to.
	 */
	public RedisMonitorDebugListener(PrintStream out)
	{
		this.out = out;
	}

	@Override
	public void onMonitorEvent(RedisMonitorEvent event)
	{
		StringBuilder sb = new StringBuilder();
		for (String s : event.getArguments())
			sb.append('"').append(s).append('"').append(' ');
		
		out.printf("MONITOR [%tF %tT.%tL] (%s:%d DB %d): \"%s\" %s\n", 
			event.getServerTime(), event.getServerTime(), event.getServerTime(),
			event.getAddress(), event.getPort(), event.getDBId(),
			event.getCommand(), sb.toString()
		);
	}

}
