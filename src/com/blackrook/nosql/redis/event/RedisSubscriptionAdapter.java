package com.blackrook.nosql.redis.event;

/**
 * An adapter class for {@link RedisSubscriptionListener}. All methods do nothing on call.
 * @author Matthew Tropiano
 */
public class RedisSubscriptionAdapter implements RedisSubscriptionListener
{

	@Override
	public void onSubscribe(String channelName, long channelTotal)
	{
		// Do nothing.
	}

	@Override
	public void onUnsubscribe(String channelName, long channelTotal)
	{
		// Do nothing.
	}

	@Override
	public void onPatternSubscribe(String channelPattern, long channelTotal)
	{
		// Do nothing.
	}

	@Override
	public void onPatternUnsubscribe(String channelPattern, long channelTotal)
	{
		// Do nothing.
	}

	@Override
	public void onMessageReceive(String channel, String message)
	{
		// Do nothing.
	}

	@Override
	public void onPatternMessageReceive(String channelPattern, String channel, String message)
	{
		// Do nothing.
	}

}
