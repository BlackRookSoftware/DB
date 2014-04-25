package com.blackrook.nosql.redis.commands;

import com.blackrook.nosql.redis.data.RedisObject;

/**
 * Interface for Redis commands related to Lua scripting.
 * @author Matthew Tropiano
 */
public interface RedisScriptingCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/eval">http://redis.io/commands/eval</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> Depends on the script that is executed.</p>
	 * <p>Evaluates a Lua script. The keys specified in <code>keys</code> should 
	 * be used as a hint for Redis as to what keys are touched during the script call.</p>
	 * @return the content returned by the script. Can be null.
	 */
	public RedisObject eval(String scriptContent, String[] keys, String[] args);

	/**
	 * <p>From <a href="http://redis.io/commands/evalsha">http://redis.io/commands/evalsha</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> Depends on the script that is executed.</p>
	 * <p>Evaluates a script cached on the server side by its SHA1 digest. 
	 * Scripts are cached on the server side using the {@link #scriptload(String)} command. 
	 * The command is otherwise identical to {@link #eval(String, String[], String[])}.</p>
	 * @return the content returned by the script. Can be null.
	 */
	public RedisObject evalsha(String hash, String[] keys, String[] args);

	/**
	 * <p>From <a href="http://redis.io/commands/script-exists">http://redis.io/commands/script-exists</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the number of scripts 
	 * to check (so checking a single script is an O(1) operation).</p>
	 * <p>Returns information about the existence of the scripts in the script cache.</p>
	 * @return The command returns an array of booleans that correspond to the specified 
	 * SHA1 digest arguments. For every corresponding SHA1 digest of a script that actually 
	 * exists in the script cache, true is returned, otherwise false is returned.
	 */
	public boolean[] scriptExists(String scriptHash, String... scriptHashes);

	/**
	 * <p>From <a href="http://redis.io/commands/script-flush">http://redis.io/commands/script-flush</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the number of scripts in cache</p>
	 * <p>Flush the Lua scripts cache.</p>
	 * @return always true. 
	 */
	public boolean scriptFlush();

	/**
	 * <p>From <a href="http://redis.io/commands/script-kill">http://redis.io/commands/script-kill</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Kills the currently executing Lua script, assuming no write operation was yet performed by the script.</p>
	 * @return always true. 
	 */
	public boolean scriptKill(String hash);

	/**
	 * <p>From <a href="http://redis.io/commands/script-load">http://redis.io/commands/script-load</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the length in bytes of the script body.</p>
	 * <p>Load a script into the scripts cache, without executing it. After the specified 
	 * command is loaded into the script cache it will be callable using {@link #evalsha(String, String[], String[])} 
	 * with the correct SHA1 digest of the script, exactly like after the first successful invocation of {@link #eval(String, String[], String[])}.</p>
	 * @return the SHA1 digest of the script added into the script cache.
	 */
	public String scriptLoad(String content);

}
