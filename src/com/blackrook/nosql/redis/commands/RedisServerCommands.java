/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.commands;

import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.enums.EncodingType;

/**
 * Interface of Redis server commands.
 * @author Matthew Tropiano
 */
public interface RedisServerCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/bgrewriteaof">http://redis.io/commands/bgrewriteaof</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Instruct Redis to start an <a href="/topics/persistence#append-only-file">Append Only File</a> rewrite process. 
	 * The rewrite will create a small optimized version of the current Append Only File.</p>
	 * @return always true.
	 */
	public boolean bgrewriteaof();

	/**
	 * <p>From <a href="http://redis.io/commands/bgsave">http://redis.io/commands/bgsave</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Save the DB in background. The OK code is immediately returned. Redis forks, 
	 * the parent continues to serve the clients, the child saves the DB on disk then 
	 * exits. A client my be able to check if the operation succeeded using the {@link #lastsave()} command.</p>
	 * @return always true.
	 */
	public boolean bgsave();

	/**
	 * <p>From <a href="http://redis.io/commands/client-kill">http://redis.io/commands/client-kill</a>:</p>
	 * <p><strong>Available since 2.4.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of client connections</p>
	 * <p>The <code>CLIENT KILL</code> command closes a given client connection identified by ip:port.</p>
	 * @return true once the client connection was closed.
	 */
	public boolean clientKill(String ip, int port);

	/**
	 * <p>From <a href="http://redis.io/commands/client-list">http://redis.io/commands/client-list</a>:</p>
	 * <p><strong>Available since 2.4.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of client connections</p>
	 * <p>The <code>CLIENT LIST</code> command returns information and statistics about the client connections server in a mostly human readable format.</p>
	 * @return a list of client strings that describe each connection.
	 */
	public String[] clientList();

	/**
	 * <p>From <a href="http://redis.io/commands/client-pause">http://redis.io/commands/client-pause</a>:</p>
	 * <p><strong>Available since 2.9.50.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><code>CLIENT PAUSE</code> is a connections control command able to suspend all
	 * the Redis clients for the specified amount of time (in milliseconds).</p>
	 * @return always true.
	 */
	public boolean clientPause(long millis);

	/**
	 * <p>From <a href="http://redis.io/commands/config-get">http://redis.io/commands/config-get</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p>The <code>CONFIG GET</code> command is used to read the configuration 
	 * parameters of a running Redis server. Not all the configuration parameters 
	 * are supported in Redis 2.4, while Redis 2.6 can read the whole configuration 
	 * of a server using this command.</p>
	 * @return the config value.
	 */
	public String configGet(String configKey);

	/**
	 * <p>From <a href="http://redis.io/commands/config-rewrite">http://redis.io/commands/config-rewrite</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p>The <code>CONFIG REWRITE</code> command rewrites the <code>redis.conf</code> 
	 * file the server was started with, applying the minimal changes needed to make 
	 * it reflecting the configuration currently used by the server, that may be 
	 * different compared to the original one because of the use of the {@link #configSet} command.</p>
	 * @return always true.
	 */
	public boolean configRewrite();

	/**
	 * <p>From <a href="http://redis.io/commands/config-set">http://redis.io/commands/config-set</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p>The <code>CONFIG SET</code> command is used in order to reconfigure the 
	 * server at run time without the need to restart Redis. You can change both 
	 * trivial parameters or switch from one to another persistence option using this command.</p>
	 * @return always true.
	 */
	public boolean configSet(String parameter, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/config-resetstat">http://redis.io/commands/config-resetstat</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Resets the statistics reported by Redis using the <a href="/commands/info">INFO</a> command.</p>
	 * @return always true.
	 */
	public boolean configResetStat();

	/**
	 * <p>From <a href="http://redis.io/commands/dbsize">http://redis.io/commands/dbsize</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Return the number of keys in the currently-selected database.</p>
	 * @return the number of keys.
	 */
	public long dbsize();
	
	/**
	 * <p>From <a href="http://redis.io/commands/flushall">http://redis.io/commands/flushall</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>Delete all the keys of all the existing databases, not just the 
	 * currently selected one. This command never fails.</p>
	 * @return always true.
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
	 * <p>From <a href="http://redis.io/commands/info">http://redis.io/commands/info</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The <a href="/commands/info">INFO</a> command returns information and statistics 
	 * about the server in a format that is simple to parse by computers and easy to read by humans.</p>
	 * @return a collection of text lines parseable for info.
	 */
	public String info(String section);

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
	 * <p>From <a href="http://redis.io/commands/migrate">http://redis.io/commands/migrate</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> This command actually executes a {@link RedisConnectionCommands#dump} 
	 * and {@link RedisConnectionCommands#del} in the source instance, and a {@link RedisConnectionCommands#restore} in the target 
	 * instance. See the pages of these commands for time complexity. Also an O(N) 
	 * data transfer between the two instances is performed.</p>
	 * <p>Atomically transfer a key from a source Redis instance to a destination 
	 * Redis instance. On success the key is deleted from the original instance 
	 * and is guaranteed to exist in the target instance.</p>
	 * @param host the hostname/address of the target server.
	 * @param port the port.
	 * @param key the key to migrate.
	 * @param destinationDB the database to target on the server.
	 * @param timeout the timeout for the connection.
	 * @param copy if true, the key is copied, not removed from the source.
	 * @param replace if true, the remote key is replaced.
	 * @return always true.
	 */
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout, boolean copy, boolean replace);

	/**
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals of Redis 
	 * Objects associated with keys. It is useful for debugging or to understand if 
	 * your keys are using the specially encoded data types to save space. Your 
	 * application may also use the information reported by the <code>object</code> 
	 * command to implement application level key eviction policies when using 
	 * Redis as a Cache.</p>
	 * <p>This call is here in order to support commands that don't have signatures.</p>
	 */
	public RedisObject object(String subcommand, String key);

	/**
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals of 
	 * Redis Objects associated with keys. It is useful for debugging or to 
	 * understand if your keys are using the specially encoded data types to 
	 * save space. Your application may also use the information reported by 
	 * the <code>object</code> command to implement application level key eviction 
	 * policies when using Redis as a Cache.</p>
	 * @param key the key to count.
	 * @return the number of references of the value associated with the 
	 * specified key. This command is mainly useful for debugging.
	 */
	public long objectRefcount(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals 
	 * of Redis Objects associated with keys. It is useful for debugging or 
	 * to understand if your keys are using the specially encoded data types 
	 * to save space. Your application may also use the information reported 
	 * by the <code>object</code> command to implement application level key 
	 * eviction policies when using Redis as a Cache.</p>
	 * @param key the key to inspect.
	 * @return the kind of internal representation used in order to store the 
	 * value associated with a key, or null for missing key.
	 */
	public EncodingType objectEncoding(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/object">http://redis.io/commands/object</a>:</p>
	 * <p><strong>Available since 2.2.3.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1).</p>
	 * <p>The <code>object</code> command allows to inspect the internals of 
	 * Redis Objects associated with keys. It is useful for debugging or to 
	 * understand if your keys are using the specially encoded data types to 
	 * save space. Your application may also use the information reported by 
	 * the <code>object</code> command to implement application level key eviction 
	 * policies when using Redis as a Cache.</p>
	 * @param key the key to inspect.
	 * @return the number of seconds since the object stored at the specified key 
	 * is idle (not requested by read or write operations). While the value is 
	 * returned in seconds the actual resolution of this timer is 10 seconds, 
	 * but may vary in future implementations.
	 */
	public long objectIdletime(String key);

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
	public RedisObject pubsub(String subcommand, String... arguments);

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
	public String[] pubsubChannels(String pattern);

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
	public String[] pubsubNumsub(String... arguments);

	/**
	 * <p>From <a href="http://redis.io/commands/pubsub">http://redis.io/commands/pubsub</a>:</p>
	 * <p><strong>Available since 2.8.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1) for the NUMPAT subcommand.</p>
	 * <p>The PUBSUB command is an introspection command that allows to inspect 
	 * the state of the Pub/Sub subsystem.</p>
	 * @return the number of patterns all the clients are subscribed to.
	 */
	public long pubsubNumpat();

	/**
	 * <p>From <a href="http://redis.io/commands/save">http://redis.io/commands/save</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The <a href="/commands/save">SAVE</a> commands performs a <strong>synchronous</strong> 
	 * save of the dataset producing a <em>point in time</em> snapshot of all the data 
	 * inside the Redis instance, in the form of an RDB file.</p>
	 * @return always true.
	 */
	public boolean save();

	/**
	 * <p>From <a href="http://redis.io/commands/shutdown">http://redis.io/commands/shutdown</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The command behavior is the following:</p>
	 */
	public void shutdown(boolean save);

	/**
	 * <p>From <a href="http://redis.io/commands/slaveof">http://redis.io/commands/slaveof</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The <code>SLAVEOF</code> command can change the replication 
	 * settings of a slave on the fly. If a Redis server is already acting as slave, the 
	 * command <code>SLAVEOF</code> NO ONE will turn off the replication, 
	 * turning the Redis server into a MASTER. In the proper form {@link #slaveof(String, String)}
	 * hostname port will make the server a slave of another server listening at the specified 
	 * hostname and port.</p>
	 * @return always true.
	 */
	public boolean slaveof(String host, String port);

	/**
	 * <p>From <a href="http://redis.io/commands/slaveof">http://redis.io/commands/slaveof</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p>The <code>SLAVEOF</code> command can change the replication 
	 * settings of a slave on the fly. If a Redis server is already acting as slave, the 
	 * command <code>SLAVEOF</code> NO ONE will turn off the replication, 
	 * turning the Redis server into a MASTER. In the proper form {@link #slaveof(String, String)}
	 * hostname port will make the server a slave of another server listening at the specified 
	 * hostname and port.</p>
	 * @return always true.
	 */
	public boolean slaveofNoOne();

	/**
	 * <p>From <a href="http://redis.io/commands/slowlog">http://redis.io/commands/slowlog</a>:</p>
	 * <p><strong>Available since 2.2.12.</strong></p>
	 * <p>This command is used in order to read and reset the Redis slow queries log.</p>
	 */
	public RedisObject slowlog(String subcommand, String argument);

	/**
	 * <p>From <a href="http://redis.io/commands/slowlog">http://redis.io/commands/slowlog</a>:</p>
	 * <p><strong>Available since 2.2.12.</strong></p>
	 * <p>This command is used in order to read and reset the Redis slow queries log.</p>
	 * @param recentCount the amount of recent entries to view.
	 */
	public RedisObject slowlogGet(long recentCount);

	/**
	 * <p>From <a href="http://redis.io/commands/slowlog">http://redis.io/commands/slowlog</a>:</p>
	 * <p><strong>Available since 2.2.12.</strong></p>
	 * <p>Gets just the length of the slow log.</p>
	 */
	public RedisObject slowlogLen();

	/**
	 * <p>From <a href="http://redis.io/commands/slowlog">http://redis.io/commands/slowlog</a>:</p>
	 * <p><strong>Available since 2.2.12.</strong></p>
	 * <p>Resets the slow log. Once deleted the information is lost forever.</p>
	 */
	public RedisObject slowlogReset();

	/**
	 * <p>From <a href="http://redis.io/commands/time">http://redis.io/commands/time</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>The <a href="/commands/time">TIME</a> command returns the current server time 
	 * as a two items lists: a Unix timestamp and the amount of microseconds already 
	 * elapsed in the current second. Basically the interface is very similar to the 
	 * one of the <code>gettimeofday</code> system call.</p>
	 * @return UNIX time in seconds, microseconds.
	 */
	public long[] time();

}
