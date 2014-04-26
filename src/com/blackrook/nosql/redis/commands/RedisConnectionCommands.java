package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis connection stuff.
 * @author Matthew Tropiano
 */
public interface RedisConnectionCommands
{
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
	 * @return always true.
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
	 * <p>From <a href="http://redis.io/commands/client-getname">http://redis.io/commands/client-getname</a>:</p>
	 * <p><strong>Available since 2.6.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <code>CLIENT GETNAME</code> returns the name of the current connection as set by 
	 * <code>CLIENT SETNAME</code>. Since every new connection starts without an associated 
	 * name, if no name was assigned a null bulk reply is returned.</p>
	 * @return the connection name, or null if no name is set.
	 */
	public String clientGetName();

	/**
	 * <p>From <a href="http://redis.io/commands/client-setname">http://redis.io/commands/client-setname</a>:</p>
	 * <p><strong>Available since 2.6.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <code>CLIENT SETNAME</code> command assigns a name to the current connection.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean clientSetName(String name);

}
