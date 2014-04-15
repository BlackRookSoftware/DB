package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis connection stuff.
 * @author Matthew Tropiano
 */
public interface RedisConnectionCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/auth">http://redis.io/commands/auth</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Request for authentication in a password-protected Redis server. 
	 * Redis can be instructed to require a password before allowing clients 
	 * to execute commands. This is done using the <code>requirepass</code> 
	 * directive in the configuration file.</p>
	 * @return true if authenticated with the sent password, false otherwise.
	 */
	public boolean auth(String password);

	/**
	 * <p>From <a href="http://redis.io/commands/echo">http://redis.io/commands/echo</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Returns <code>message</code>.</p>
	 * @return the string sent to the server.
	 */
	public String echo(String message);

	/**
	 * <p>From <a href="http://redis.io/commands/ping">http://redis.io/commands/ping</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>This command is often used to test if a connection is still alive, or to measure latency.</p>
	 * @return milliseconds between the call and the response. Ordinarily, Redis just returns "PONG", which is not very useful API-wise.
	 */
	public long ping();

	/**
	 * <p>From <a href="http://redis.io/commands/quit">http://redis.io/commands/quit</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Ask the server to close the connection. The connection is closed as soon as all pending replies have been written to the client.</p>
	 * @return true if server wants to close the connection, false if not.
	 */
	public boolean quit();

	/**
	 * <p>From <a href="http://redis.io/commands/select">http://redis.io/commands/select</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Select the DB with having the specified zero-based numeric index. New connections always use DB 0.</p>
	 * @return true if successful, false if not.
	 */
	public boolean select(long db);

	/**
	 * <p>From <a href="http://redis.io/commands/pubsub">http://redis.io/commands/pubsub</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) for the CHANNELS subcommand, 
	 * where N is the number of active channels, and assuming constant time
	 * pattern matching (relatively short channels and patterns). O(N) for 
	 * the NUMSUB subcommand, where N is the number of requested channels. 
	 * O(1) for the NUMPAT subcommand.</p>
	 * <p>The PUBSUB command is an introspection command that allows to inspect 
	 * the state of the Pub/Sub subsystem.</p>
	 * <p>This call is here in order to support commands that don't have signatures.</p>
	 */
	public String pubsub(String subcommand, String... arguments);

	/**
	 * <p>From <a href="http://redis.io/commands/pubsub">http://redis.io/commands/pubsub</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N), where N is the number 
	 * of active channels, and assuming constant time pattern matching 
	 * (relatively short channels and patterns).</p>
	 * <p>The PUBSUB command is an introspection command that allows to inspect 
	 * the state of the Pub/Sub subsystem.</p>
	 * @return a list of active channels, optionally matching the specified pattern.
	 */
	public String[] pubsubChannels(String... arguments);

	/**
	 * <p>From <a href="http://redis.io/commands/pubsub">http://redis.io/commands/pubsub</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N), where N is the number of requested channels.</p>
	 * <p>The PUBSUB command is an introspection command that allows to inspect 
	 * the state of the Pub/Sub subsystem.</p>
	 * @return a list of channels and number of subscribers for every channel. 
	 * The format is channel, count, channel, count, ..., so the list is flat. 
	 * The order in which the channels are listed is the same as the order of 
	 * the channels specified in the command call.
	 */
	public void pubsubNumsub(String... arguments);

	/**
	 * <p>From <a href="http://redis.io/commands/pubsub">http://redis.io/commands/pubsub</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for the NUMPAT subcommand.</p>
	 * <p>The PUBSUB command is an introspection command that allows to inspect 
	 * the state of the Pub/Sub subsystem.</p>
	 * @return the number of patterns all the clients are subscribed to.
	 */
	public long pubsubNumpat(String... arguments);

}
