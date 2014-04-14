package com.blackrook.nosql.redis.commands;

/**
 * Interface of Redis server commands.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisServerCommands
{
	/** 
	 *	Asynchronously rewrite the append-only file
	 *	server
	 */
	public void bgrewriteaof();

	/** 
	 *	Asynchronously save the dataset to disk
	 *	server
	 */
	public void bgsave();

	/** 
	 *	Kill the connection of a client
	 *	server
	 */
	public void clientkill();

	/** 
	 *	Get the list of client connections
	 *	server
	 */
	public void clientlist();

	/** 
	 *	Get the current connection name
	 *	server
	 */
	public void clientgetname();

	/** 
	 *	Stop processing commands from clients for some time
	 *	server
	 */
	public void clientpause();

	/** 
	 *	Set the current connection name
	 *	server
	 */
	public void clientsetname();

	/** 
	 *	Get the value of a configuration parameter
	 *	server
	 */
	public void configget();

	/** 
	 *	Rewrite the configuration file with the in memory configuration
	 *	server
	 */
	public void configrewrite();

	/** 
	 *	Set a configuration parameter to the given value
	 *	server
	 */
	public void configset(String parameter, String value);

	/** 
	 *	Reset the stats returned by INFO
	 *	server
	 */
	public void configresetstat();

	/** 
	 *	Return the number of keys in the selected database
	 *	server
	 */
	public void dbsize();

	/** 
	 *	Get debugging information about a key
	 *	server
	 */
	public void debugobject();

	/** 
	 *	Make the server crash
	 *	server
	 */
	public void debugsegfault();

	/** 
	 *	Remove all keys from all databases
	 *	server
	 */
	public void flushall();

	/** 
	 *	Remove all keys from the current database
	 *	server
	 */
	public void flushdb();

	/** 
	 *	Get information and statistics about the server
	 *	server
	 */
	public void info();

	/** 
	 *	Get the UNIX time stamp of the last successful save to disk
	 *	server
	 */
	public void lastsave();

	/** 
	 *	Listen for all requests received by the server in real time
	 *	server
	 */
	public void monitor();

	/** 
	 *	Synchronously save the dataset to disk
	 *	server
	 */
	public void save();

	/** 
	 *	Synchronously save the dataset to disk and then shut down the server
	 *	server
	 */
	public void shutdown(boolean save);

	/** 
	 *	Make the server a slave of another instance, or promote it as master
	 *	server
	 */
	public void slaveof(String host, String port);

	/** 
	 *	Manages the Redis slow queries log
	 *	server
	 */
	public void slowlog(String subcommand, String argument);

	/** 
	 *	Internal command used for replication
	 *	server
	 */
	public void sync();

	/** 
	 *	Return the current server time
	 *	server
	 */
	public void time();
	
}