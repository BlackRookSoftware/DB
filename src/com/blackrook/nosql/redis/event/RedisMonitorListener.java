package com.blackrook.nosql.redis.event;

import com.blackrook.nosql.redis.RedisMonitorConnection;

/**
 * A listener for {@link RedisMonitorConnection}s that encapsulate
 * events received via a MONITOR connection.
 * @author Matthew Tropiano
 */
public interface RedisMonitorListener
{
	/**
	 * Called when a MONITOR message is received.
	 * @param event the encapsulated event.
	 * @see RedisMonitorEvent
	 */
	public void onMonitorEvent(RedisMonitorEvent event);
}
