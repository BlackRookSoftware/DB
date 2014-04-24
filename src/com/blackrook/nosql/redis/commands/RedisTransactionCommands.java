package com.blackrook.nosql.redis.commands;

/**
 * An interface detailing commands for Redis transactions.
 * @author Matthew Tropiano
 */
public interface RedisTransactionCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/discard">http://redis.io/commands/discard</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p>Flushes all previously queued commands in a <a href="/topics/transactions">transaction</a> and restores the connection state to normal.</p>
	 * @return true on completion.
	 */
	public boolean discard();

	/**
	 * <p>From <a href="http://redis.io/commands/exec">http://redis.io/commands/exec</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p>Executes all previously queued commands in a <a href="/topics/transactions">transaction</a> and restores the connection state to normal.</p>
	 * @return each element being the reply to each of the commands in the atomic transaction.
	 */
	public String[] exec();

	/**
	 * <p>From <a href="http://redis.io/commands/multi">http://redis.io/commands/multi</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p>Marks the start of a <a href="/topics/transactions">transaction</a> block. Subsequent commands will be queued for atomic execution using <a href="/commands/exec">EXEC</a>.</p>
	 * @return true on completion.
	 */
	public boolean multi();

	/**
	 * <p>From <a href="http://redis.io/commands/unwatch">http://redis.io/commands/unwatch</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Flushes all the previously watched keys for a <a href="/topics/transactions">transaction</a>.</p>
	 * @return true on completion.
	 */
	public boolean unwatch();

	/**
	 * <p>From <a href="http://redis.io/commands/watch">http://redis.io/commands/watch</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for every key.</p>
	 * <p>Marks the given keys to be watched for conditional execution of a <a href="/topics/transactions">transaction</a>.</p>
	 * @return true on completion.
	 */
	public boolean watch(String key, String... keys);

}
