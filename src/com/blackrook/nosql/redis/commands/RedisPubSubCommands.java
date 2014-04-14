package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis commands related to Pub/Sub messaging.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisPubSubCommands
{

	/** 
	 *	Listen for messages published to channels matching the given patterns
	 *	pubsub
	 */
	public void psubscribe(String... patterns);
	
	/** 
	 *	Inspect the state of the Pub/Sub subsystem
	 *	pubsub
	 */
	public void pubsub(String subcommand, String... arguments);

	/** 
	 *	Post a message to a channel
	 *	pubsub
	 */
	public void publish(String channel, String message);

	/** 
	 *	Stop listening for messages posted to channels matching the given patterns
	 *	pubsub
	 */
	public void punsubscribe(String... patterns);

	/** 
	 *	Listen for messages published to the given channels
	 *	pubsub
	 */
	public void subscribe(String... channels);
	
	/** 
	 *	Stop listening for messages posted to the given channels
	 *	pubsub
	 */
	public void unsubscribe(String... channels);
	
	

}
