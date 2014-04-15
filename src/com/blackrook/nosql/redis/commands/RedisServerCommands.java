package com.blackrook.nosql.redis.commands;

/**
 * Interface of Redis server commands.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisServerCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/bgrewriteaof">http://redis.io/commands/bgrewriteaof</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Instruct Redis to start an <a href="/topics/persistence#append-only-file">Append Only File</a> rewrite process. 
	 * The rewrite will create a small optimized version of the current Append Only File.</p>
	 * @return true if successful, false if not.
	 */
	public boolean bgrewriteaof();

	/**
	 * <p>From <a href="http://redis.io/commands/bgsave">http://redis.io/commands/bgsave</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Save the DB in background. The OK code is immediately returned. Redis forks, 
	 * the parent continues to serve the clients, the child saves the DB on disk then 
	 * exits. A client my be able to check if the operation succeeded using the {@link #lastsave()} command.</p>
	 * @return true if successful, false if not.
	 */
	public boolean bgsave();

	/**
	 * <p>From <a href="http://redis.io/commands/client-kill">http://redis.io/commands/client-kill</a>:</p>
	 * <p><strong>Available since 2.4.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of client connections</p>
	 * <p>The <code>CLIENT KILL</code> command closes a given client connection identified by ip:port.</p>
	 * @return true if the client connection was closed, false if not.
	 */
	public void clientkill(String ipPort);

	/**
	 * <p>From <a href="http://redis.io/commands/client-list">http://redis.io/commands/client-list</a>:</p>
	 * <p><strong>Available since 2.4.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of client connections</p>
	 * <p>The <code>CLIENT LIST</code> command returns information and statistics about the client connections server in a mostly human readable format.</p>
	 * @return a list of client strings that describe each connection.
	 */
	public String[] clientlist();

	/**
	 * <p>From <a href="http://redis.io/commands/client-getname">http://redis.io/commands/client-getname</a>:</p>
	 * <p><strong>Available since 2.6.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <code>CLIENT GETNAME</code> returns the name of the current connection as set by <code>CLIENT SETNAME</code>. Since every new connection starts without an associated name, if no name was assigned a null bulk reply is returned.</p>
	 * @return the connection name, or null if no name is set.
	 */
	public void clientgetname();

	/**
	 * <p>From <a href="http://redis.io/commands/client-pause">http://redis.io/commands/client-pause</a>:</p>
	 * <p><strong>Available since 2.9.50.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><code>CLIENT PAUSE</code> is a connections control command able to suspend all the Redis clients for the specified amount of time (in milliseconds).</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean clientpause(long millis);

	/**
	 * <p>From <a href="http://redis.io/commands/client-setname">http://redis.io/commands/client-setname</a>:</p>
	 * <p><strong>Available since 2.6.9.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <code>CLIENT SETNAME</code> command assigns a name to the current connection.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean clientsetname(String name);

	/**
	 * <p>From <a href="http://redis.io/commands/config-get">http://redis.io/commands/config-get</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p>The <code>CONFIG GET</code> command is used to read the configuration 
	 * parameters of a running Redis server. Not all the configuration parameters 
	 * are supported in Redis 2.4, while Redis 2.6 can read the whole configuration 
	 * of a server using this command.</p>
	 * @return the config value.
	 */
	public String configget(String configKey);

	/**
	 * <p>From <a href="http://redis.io/commands/config-rewrite">http://redis.io/commands/config-rewrite</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p>The <code>CONFIG REWRITE</code> command rewrites the <code>redis.conf</code> 
	 * file the server was started with, applying the minimal changes needed to make 
	 * it reflecting the configuration currently used by the server, that may be 
	 * different compared to the original one because of the use of the {@link #configset(String, String)} command.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean configrewrite();

	/**
	 * <p>From <a href="http://redis.io/commands/config-set">http://redis.io/commands/config-set</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p>The <code>CONFIG SET</code> command is used in order to reconfigure the server at run time without the need to restart Redis. You can change both trivial parameters or switch from one to another persistence option using this command.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean configset(String parameter, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/config-resetstat">http://redis.io/commands/config-resetstat</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Resets the statistics reported by Redis using the <a href="/commands/info">INFO</a> command.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean configresetstat();

	/**
	 * <p>From <a href="http://redis.io/commands/dbsize">http://redis.io/commands/dbsize</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Return the number of keys in the currently-selected database.</p>
	 * @return the number of keys.
	 */
	public long dbsize();
	
	/**
	 * <p>From <a href="http://redis.io/commands/debug-object">http://redis.io/commands/debug-object</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><code>DEBUG OBJECT</code> is a debugging command that should not be used by clients. Check the {@link RedisGenericCommands#object(String, String)} command instead.</p>
	 */
	public String debugobject();

	/**
	 * <p>From <a href="http://redis.io/commands/debug-segfault">http://redis.io/commands/debug-segfault</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><code>DEBUG SEGFAULT</code> performs an invalid memory access that crashes Redis. It is used to simulate bugs during the development.</p>
	 */
	public void debugsegfault();
	
	/**
	 * <p>From <a href="http://redis.io/commands/flushall">http://redis.io/commands/flushall</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Delete all the keys of all the existing databases, not just the currently selected one. This command never fails.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean flushall();

	/**
	 * <p>From <a href="http://redis.io/commands/flushdb">http://redis.io/commands/flushdb</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Delete all the keys of the currently selected DB. This command never fails.</p>
	 * @return true if successful, false otherwise.
	 */
	public boolean flushdb();

	/**
	 * <p>From <a href="http://redis.io/commands/info">http://redis.io/commands/info</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The <a href="/commands/info">INFO</a> command returns information and statistics 
	 * about the server in a format that is simple to parse by computers and easy to read by humans.</p>
	 * @return a collection of text lines parseable for info.
	 */
	public String info();

	/**
	 * <p>From <a href="http://redis.io/commands/lastsave">http://redis.io/commands/lastsave</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Return the UNIX TIME of the last DB save executed with success. A client may 
	 * check if a {@link #bgsave()} command succeeded reading the {@link #lastsave()} value, 
	 * then issuing a {@link #bgsave()} command and checking at regular intervals every N 
	 * seconds if {@link #lastsave()} changed.</p>
	 * @return a UNIX time stamp.
	 */
	public long lastsave();

	/**
	 * <p>From <a href="http://redis.io/commands/monitor">http://redis.io/commands/monitor</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><a href="/commands/monitor">MONITOR</a> is a debugging command that streams back every command processed by the Redis server. It can help in understanding what is happening to the database. This command can both be used via <code>redis-cli</code> and via <code>telnet</code>.</p>
	 * @return <strong>Non standard return value</strong>, just dumps the received commands in an infinite flow.
	 */
	public void monitor();

	/**
	 * <p>From <a href="http://redis.io/commands/save">http://redis.io/commands/save</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The <a href="/commands/save">SAVE</a> commands performs a <strong>synchronous</strong> save of the dataset producing a <em>point in time</em> snapshot of all the data inside the Redis instance, in the form of an RDB file.</p>
	 * @return <a href="/topics/protocol#simple-string-reply">Simple string reply</a>: The commands returns OK on success.
	 */
	public void save();

	/**
	 * <p>From <a href="http://redis.io/commands/shutdown">http://redis.io/commands/shutdown</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The command behavior is the following:</p>
	 * @return <a href="/topics/protocol#simple-string-reply">Simple string reply</a> on error. On success nothing is returned since the server quits and the connection is closed.
	 */
	public void shutdown(boolean save);

	/**
	 * <p>From <a href="http://redis.io/commands/slaveof">http://redis.io/commands/slaveof</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The <a href="/commands/slaveof">SLAVEOF</a> command can change the replication settings of a slave on the fly. If a Redis server is already acting as slave, the command <a href="/commands/slaveof">SLAVEOF</a> NO ONE will turn off the replication, turning the Redis server into a MASTER. In the proper form <a href="/commands/slaveof">SLAVEOF</a> hostname port will make the server a slave of another server listening at the specified hostname and port.</p>
	 * @return <a href="/topics/protocol#simple-string-reply">Simple string reply</a>
	 */
	public void slaveof(String host, String port);

	/**
	 * <p>From <a href="http://redis.io/commands/slowlog">http://redis.io/commands/slowlog</a>:</p>
	 * <p><strong>Available since 2.2.12.</strong></p>
	 * <p>This command is used in order to read and reset the Redis slow queries log.</p>
	 */
	public void slowlog(String subcommand, String argument);

	/**
	 * <p>From <a href="http://redis.io/commands/sync">http://redis.io/commands/sync</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Examples</p>
	 */
	public void sync();

	/**
	 * <p>From <a href="http://redis.io/commands/time">http://redis.io/commands/time</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <a href="/commands/time">TIME</a> command returns the current server time as a two items lists: a Unix timestamp and the amount of microseconds already elapsed in the current second. Basically the interface is very similar to the one of the <code>gettimeofday</code> system call.</p>
	 * @return <a href="/topics/protocol#array-reply">Array reply</a>, specifically:
	 */
	public void time();

}