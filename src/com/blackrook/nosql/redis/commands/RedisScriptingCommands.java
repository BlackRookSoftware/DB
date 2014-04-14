package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis commands related to Lua scripting.
 * @author Matthew Tropiano
 */
public interface RedisScriptingCommands
{

	/** 
	 *	Execute a Lua script server side
	 *	scripting
	 */
	public void eval(String scriptContent, String[] keys, String[] args);
	
	/** 
	 *	Execute a Lua script server side
	 *	scripting
	 */
	public void evalsha(String hash, String[] keys, String[] args);
	
	/** 
	 *	Check existence of scripts in the script cache.
	 *	scripting
	 */
	public void scriptexists(String... scripts);
	
	/** 
	 *	Remove all the scripts from the script cache.
	 *	scripting
	 */
	public void scriptflush();

	/** 
	 *	Kill the script currently in execution.
	 *	scripting
	 */
	public void scriptkill(String hash);

	/** 
	 *	Load the specified Lua script into the script cache.
	 *	scripting
	 */
	public void scriptload(String content);

}
