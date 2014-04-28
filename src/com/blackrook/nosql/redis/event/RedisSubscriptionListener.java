package com.blackrook.nosql.redis.event;

/**
 * A listener class that listens on fired events from a Redis channel subscription.  
 * @author Matthew Tropiano
 */
public interface RedisSubscriptionListener
{
	/**
	 * Called when this subscription connection subscribes to a channel.
	 * @param channelName the subscribed channel.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	public void onSubscribe(String channelName, long channelTotal);
	
	/**
	 * Called when this subscription connection unsubscribes from a channel.
	 * @param channelName the channel that this unsubscribed from.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	public void onUnsubscribe(String channelName, long channelTotal);
	
	/**
	 * Called when this subscription connection subscribes to a set of channels via a pattern.
	 * @param channelPattern the channel pattern that this subscribed to.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	public void onPatternSubscribe(String channelPattern, long channelTotal);
	
	/**
	 * Called when this subscription connection unsubscribes from a set of channels via a pattern.
	 * @param channelPattern the channel pattern that this unsubscribed from.
	 * @param channelTotal the total number of channels that this subscriber is subscribed to.
	 */
	public void onPatternUnsubscribe(String channelPattern, long channelTotal);
	
	/**
	 * Called when this subscription connection receives a message from a channel.
	 * @param channel the channel that the message came from.
	 * @param message the message received.
	 */
	public void onMessageReceive(String channel, String message);
	
	/**
	 * Called when this subscription connection receives a message from a channel.
	 * @param channelPattern the channel pattern that was matched.
	 * @param channel the channel that the message came from.
	 * @param message the message received.
	 */
	public void onPatternMessageReceive(String channelPattern, String channel, String message);
	
}
