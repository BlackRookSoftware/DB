package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis HyperlogLog management.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisHyperlogCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/pfadd">http://redis.io/commands/pfadd</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) to add every element.</p>
	 * <p>Adds all the element arguments to the HyperLogLog data structure stored at the variable name specified as first argument.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>, specifically:
	 */
	public void pfadd(String key, String... elements);

	/**
	 * <p>From <a href="http://redis.io/commands/pfcount">http://redis.io/commands/pfcount</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>Returns the approximated cardinality computed by the HyperLogLog data structure stored at the specified variable, which is 0 if the variable does not exist.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>, specifically:
	 */
	public long pfcount();

	/**
	 * <p>From <a href="http://redis.io/commands/pfmerge">http://redis.io/commands/pfmerge</a>:</p>
	 * <p><strong>Available since 2.8.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) to merge N HyperLogLogs, but with high constant times.</p>
	 * <p>Merge multiple HyperLogLog values into an unique value that will approximate the cardinality of the union of the observed Sets of the source HyperLogLog structures.</p>
	 * @return true if successful, false if not.
	 */
	public void pfmerge(String destkey, String... sourcekey);

}
