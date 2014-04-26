package com.blackrook.nosql.redis.commands;

public interface RedisDebugCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/debug-object">http://redis.io/commands/debug-object</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><code>DEBUG OBJECT</code> is a debugging command that should not be used
	 * by clients. Check the {@link RedisGenericCommands#object(String, String)} command instead.</p>
	 */
	public String debugObject(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/debug-segfault">http://redis.io/commands/debug-segfault</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><code>DEBUG SEGFAULT</code> performs an invalid memory access that 
	 * crashes Redis. It is used to simulate bugs during the development.</p>
	 */
	public void debugSegfault();

}
