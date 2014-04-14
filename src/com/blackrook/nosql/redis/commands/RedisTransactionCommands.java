package com.blackrook.nosql.redis.commands;

/**
 * An interface detailing commands for Redis transactions.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisTransactionCommands
{

	/** 
	 *	Discard all commands issued after MULTI
	 *	transactions
	 */
	public void discard();

	/** 
	 *	Execute all commands issued after MULTI
	 *	transactions
	 */
	public void exec();

	/** 
	 *	Mark the start of a transaction block
	 *	transactions
	 */
	public void multi();

	/** 
	 *	Forget about all watched keys
	 *	transactions
	 */
	public void unwatch();

	/** 
	 *	Watch the given keys to determine execution of the MULTI/EXEC block
	 *	transactions
	 */
	public void watch(String... keys);
	
}
