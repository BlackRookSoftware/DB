/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import com.blackrook.commons.Common;
import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.list.List;
import com.blackrook.nosql.redis.commands.RedisDeferredCommands;
import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.enums.Aggregation;
import com.blackrook.nosql.redis.enums.BitwiseOperation;
import com.blackrook.nosql.redis.enums.SortOrder;
import com.blackrook.nosql.redis.io.RESPWriter;


/**
 * A Redis Pipeline, created from a Redis Connection.
 * Each call in this object queues the command and sends them all at once
 * when completed.  
 * @author Matthew Tropiano
 */
public class RedisPipeline implements RedisDeferredCommands
{
	/** The connection to use. */
	private RedisConnection connection;
	/** Output buffer. */
	private StringWriter buffer;
	/** Writer to output buffer. */
	private RESPWriter writer;
	/** Amount of types to return. */
	private int queued;
	
	private RedisPipeline(RedisConnection connection)
	{
		this.connection = connection;
		this.buffer = new StringWriter();
		this.writer = new RESPWriter(buffer);
		this.queued = 0;
	}
	
	/**
	 * Creates a pipelined stream.
	 * @param connection
	 */
	static RedisPipeline createPipeline(RedisConnection connection)
	{
		return new RedisPipeline(connection);
	}
	
	/**
	 * Send all pending commands in the pipeline and returns all of
	 * their return values.
	 * @return an array of Redis objects whose contents are in the order of the commands issued.
	 */
	public RedisObject[] finish()
	{
		connection.writer.writeRaw(buffer.toString());
		RedisObject[] out = new RedisObject[queued];
		int i = 0;
		while (queued > 0)
		{
			out[i++] = connection.reader.readObject();
			queued--;
		}
		return out;
	}
	
	@Override
	public void echo(String message)
	{
		writer.writeArray("ECHO", message);
		queued++;
	}

	@Override
	public void del(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"DEL", key}, keys));
		else
			writer.writeArray("DEL", key);
		queued++;
	}

	@Override
	public void dump(String key)
	{
		writer.writeArray("DUMP", key);
		queued++;
	}

	@Override
	public void exists(String key)
	{
		writer.writeArray("EXISTS", key);
		queued++;
	}

	@Override
	public void expire(String key, long seconds)
	{
		writer.writeArray("EXPIRE", key, seconds);
		queued++;
	}

	@Override
	public void expireat(String key, long timestamp)
	{
		writer.writeArray("EXPIREAT", key, timestamp);
		queued++;
	}

	@Override
	public void keys(String pattern)
	{
		writer.writeArray("KEYS", pattern);
		queued++;
	}

	@Override
	public void move(String key, long db)
	{
		writer.writeArray("MOVE", key, db);
		queued++;
	}

	@Override
	public void persist(String key)
	{
		writer.writeArray("PERSIST", key);
		queued++;
	}

	@Override
	public void pexpire(String key, long milliseconds)
	{
		writer.writeArray("PEXPIRE", key, milliseconds);
		queued++;
	}

	@Override
	public void pexpireat(String key, long timestamp)
	{
		writer.writeArray("PEXPIREAT", key, timestamp);
		queued++;
	}

	@Override
	public void pttl(String key)
	{
		writer.writeArray("PTTL", key);
		queued++;
	}

	@Override
	public void publish(String channel, String message)
	{
		writer.writeArray("PUBLISH", channel, message);
		queued++;
	}

	@Override
	public void randomkey()
	{
		writer.writeArray("RANDOMKEY");
		queued++;
	}

	@Override
	public void rename(String key, String newkey)
	{
		writer.writeArray("RENAME", key, newkey);
		queued++;
	}

	@Override
	public void renamenx(String key, String newkey)
	{
		writer.writeArray("RENAMENX", key, newkey);
		queued++;
	}

	@Override
	public void restore(String key, long ttl, String serializedvalue)
	{
		writer.writeArray("RESTORE", key, ttl, serializedvalue);
		queued++;
	}

	@Override
	public void sort(String key, String pattern, SortOrder sortOrder, boolean alpha, Long limitOffset, Long limitCount, String storeKey, String... getPatterns)
	{
		List<Object> out = new List<Object>(13 + getPatterns.length);

		out.add("SORT");
		out.add(key);
		
		if (pattern != null)
		{
			out.add("BY");
			out.add(pattern);
		}

		if (limitOffset != null && limitCount != null)
		{
			out.add("LIMIT");
			out.add(limitOffset);
			out.add(limitCount);
		}

		if (getPatterns.length > 0)
		{
			out.add("GET");
			for (String gp : getPatterns)
				out.add(gp);
		}
		
		out.add(sortOrder != null ? sortOrder.name() : SortOrder.ASC);

		if (alpha)
			out.add("ALPHA");

		if (storeKey != null)
		{
			out.add("STORE");
			out.add(storeKey);
		}
		
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void ttl(String key)
	{
		writer.writeArray("TTL", key);
		queued++;
	}

	@Override
	public void type(String key)
	{
		writer.writeArray("TYPE", key);
		queued++;
	}

	@Override
	public void append(String key, String value)
	{
		writer.writeArray("APPEND", key, value);
		queued++;
	}

	@Override
	public void bitcount(String key)
	{
		writer.writeArray("BITCOUNT", key);
		queued++;
	}

	@Override
	public void bitcount(String key, long start, long end)
	{
		writer.writeArray("BITCOUNT", key, start, end);
		queued++;
	}

	@Override
	public void bitop(BitwiseOperation operation, String destkey, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BITOP", operation.name(), destkey, key}, keys));
		else
			writer.writeArray("BITOP", operation.name(), destkey, key);
		queued++;
	}

	@Override
	public void bitpos(String key, long bit)
	{
		writer.writeArray("BITPOS", key, bit);
		queued++;
	}

	@Override
	public void bitpos(String key, long bit, Long start, Long end)
	{
		List<Object> out = new List<Object>(5);
		out.add("BITPOS");
		out.add(key);
		out.add(bit);
		if (start != null)
			out.add(start);
		if (end != null)
			out.add(end);
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void decr(String key)
	{
		writer.writeArray("DECR", key);
		queued++;
	}

	@Override
	public void decrby(String key, long decrement)
	{
		writer.writeArray("DECRBY", key, decrement);
		queued++;
	}

	@Override
	public void get(String key)
	{
		writer.writeArray("GET", key);
		queued++;
	}
	
	@Override
	public void getbit(String key, long offset)
	{
		writer.writeArray("GETBIT", key, offset);
		queued++;
	}

	@Override
	public void getrange(String key, long start, long end)
	{
		writer.writeArray("GETRANGE", key, start, end);
		queued++;
	}

	@Override
	public void getset(String key, String value)
	{
		writer.writeArray("GETSET", key, value);
		queued++;
	}

	@Override
	public void getset(String key, Number value)
	{
		writer.writeArray("GETSET", key, value);
		queued++;
	}

	@Override
	public void incr(String key)
	{
		writer.writeArray("INCR", key);
		queued++;
	}

	@Override
	public void incrby(String key, long increment)
	{
		writer.writeArray("INCRBY", key, increment);
		queued++;
	}

	@Override
	public void incrbyfloat(String key, double increment)
	{
		writer.writeArray("INCRBYFLOAT", key, increment);
		queued++;
	}

	@Override
	public void mget(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"MGET", key}, keys));
		else
			writer.writeArray("MGET", key);
		queued++;
	}

	@Override
	public void mset(String key, String value, String... keyValues)
	{
		if (keyValues.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"MSET", key, value}, keyValues));
		else
			writer.writeArray("MSET", key, value);
		queued++;
	}

	@Override
	public void mset(ObjectPair<String, Object>... pairs)
	{
		if (pairs.length == 0)
			throw new IllegalArgumentException("This requires more than 0 arguments.");
		
		List<Object> out = new List<Object>(1 + (pairs.length * 2));
		out.add("MSET");
		
		for (ObjectPair<String, Object> p : pairs)
		{
			if (p.getValue() != null)
			{
				out.add(p.getKey());
				out.add(String.valueOf(p.getValue()));
			}
		}
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void msetnx(String key, String value, String... keyValues)
	{
		if (keyValues.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"MSETNX", key, value}, keyValues));
		else
			writer.writeArray("MSETNX", key, value);
		queued++;
	}

	@Override
	public void msetnx(ObjectPair<String, Object>... pairs)
	{
		if (pairs.length == 0)
			throw new IllegalArgumentException("This requires more than 0 arguments.");
		
		List<Object> out = new List<Object>(1 + (pairs.length * 2));
		out.add("MSETNX");
		
		for (ObjectPair<String, Object> p : pairs)
		{
			if (p.getValue() != null)
			{
				out.add(p.getKey());
				out.add(String.valueOf(p.getValue()));
			}
		}
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void psetex(String key, long milliseconds, String value)
	{
		writer.writeArray("PSETEX", key, milliseconds, value);
		queued++;
	}

	@Override
	public void set(String key, String value)
	{
		writer.writeArray("SET", key, value);
		queued++;
	}

	@Override
	public void set(String key, Number value)
	{
		writer.writeArray("SET", key, value);
		queued++;
	}

	@Override
	public void setbit(String key, long offset, long value)
	{
		writer.writeArray("SETBIT", key, offset, value);
		queued++;
	}

	@Override
	public void setex(String key, long seconds, String value)
	{
		writer.writeArray("SETEX", key, seconds, value);
		queued++;
	}

	@Override
	public void setnx(String key, String value)
	{
		writer.writeArray("SETNX", key, value);
		queued++;
	}

	@Override
	public void setrange(String key, long offset, String value)
	{
		writer.writeArray("SETRANGE", key, offset, value);
		queued++;
	}

	@Override
	public void strlen(String key)
	{
		writer.writeArray("STRLEN", key);
		queued++;
	}

	@Override
	public void hdel(String key, String field, String... fields)
	{
		if (fields.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"HDEL", key, field}, fields));
		else
			writer.writeArray("HDEL", key, field);
		queued++;
	}

	@Override
	public void hexists(String key, String field)
	{
		writer.writeArray("HEXISTS", key, field);
		queued++;
	}

	@Override
	public void hget(String key, String field)
	{
		writer.writeArray("HGET", key, field);
		queued++;
	}

	@Override
	public void hgetall(String key)
	{
		writer.writeArray("HGETALL", key);
		queued++;
	}

	@Override
	public void hincrby(String key, String field, long increment)
	{
		writer.writeArray("HINCRBY", key, field, increment);
		queued++;
	}

	@Override
	public void hincrbyfloat(String key, String field, double increment)
	{
		writer.writeArray("HINCRBYFLOAT", key, field, increment);
		queued++;
	}

	@Override
	public void hkeys(String key)
	{
		writer.writeArray("HKEYS", key);
		queued++;
	}

	@Override
	public void hlen(String key)
	{
		writer.writeArray("HLEN", key);
		queued++;
	}

	@Override
	public void hmget(String key, String field, String... fields)
	{
		if (fields.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"HMGET", key, field}, fields));
		else
			writer.writeArray("HMGET", key, field);
		queued++;
	}

	@Override
	public void hmset(String key, String field, String value, String... fieldvalues)
	{
		if (fieldvalues.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"HMSET", key, field, value}, fieldvalues));
		else
			writer.writeArray("HMSET", key, field, value);
		queued++;
	}

	@Override
	public void hset(String key, String field, String value)
	{
		writer.writeArray("HSET", key, field, value);
		queued++;
	}

	@Override
	public void hset(String key, String field, Number value)
	{
		writer.writeArray("HSET", key, field, value);
		queued++;
	}

	@Override
	public void hsetnx(String key, String field, String value)
	{
		writer.writeArray("HSETNX", key, field, value);
		queued++;
	}

	@Override
	public void hsetnx(String key, String field, Number value)
	{
		writer.writeArray("HSETNX", key, field, value);
		queued++;
	}

	@Override
	public void hvals(String key)
	{
		writer.writeArray("HVALS", key);
		queued++;
	}

	@Override
	public void blpop(long timeout, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BLPOP", key}, keys, new Object[]{timeout}));
		else
			writer.writeArray("BLPOP", key, timeout);
		queued++;
	}

	@Override
	public void brpop(long timeout, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"BRPOP", key}, keys, new Object[]{timeout}));
		else
			writer.writeArray("BRPOP", key, timeout);
		queued++;
	}

	@Override
	public void brpoplpush(long timeout, String source, String destination)
	{
		writer.writeArray("BRPOPLPUSH", source, destination, timeout);
		queued++;
	}

	@Override
	public void lindex(String key, long index)
	{
		writer.writeArray("LINDEX", key, index);
		queued++;
	}

	@Override
	public void linsert(String key, boolean before, String pivot, String value)
	{
		writer.writeArray("LINSERT", key, before ? "BEFORE" : "AFTER", pivot, value);
		queued++;
	}

	@Override
	public void linsert(String key, boolean before, String pivot, Number value)
	{
		writer.writeArray("LINSERT", key, before ? "BEFORE" : "AFTER", pivot, value);
		queued++;
	}

	@Override
	public void llen(String key)
	{
		writer.writeArray("LLEN", key);
		queued++;
	}

	@Override
	public void lpop(String key)
	{
		writer.writeArray("LPOP", key);
		queued++;
	}

	@Override
	public void lpush(String key, String value, String... values)
	{
		if (values.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"LPUSH", key, value}, values));
		else
			writer.writeArray("LPUSH", key, value);
		queued++;
	}

	@Override
	public void lpushx(String key, String value)
	{
		writer.writeArray("LPUSHX", key, value);
		queued++;
	}

	@Override
	public void lrange(String key, long start, long stop)
	{
		writer.writeArray("LPUSHX", key, start, stop);
		queued++;
	}

	@Override
	public void lrem(String key, long count, String value)
	{
		writer.writeArray("LREM", key, count, value);
		queued++;
	}

	@Override
	public void lset(String key, long index, String value)
	{
		writer.writeArray("LSET", key, index, value);
		queued++;
	}

	@Override
	public void ltrim(String key, long start, long stop)
	{
		writer.writeArray("LTRIM", key, start, stop);
		queued++;
	}

	@Override
	public void rpop(String key)
	{
		writer.writeArray("LPOP", key);
		queued++;
	}

	@Override
	public void rpoplpush(String source, String destination)
	{
		writer.writeArray("RPOPLPUSH", source, destination);
		queued++;
	}

	@Override
	public void rpush(String key, String value, String... values)
	{
		if (values.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"RPUSH", key, value}, values));
		else
			writer.writeArray("RPUSH", key, value);
		queued++;
	}

	@Override
	public void rpushx(String key, String value)
	{
		writer.writeArray("RPUSHX", key, value);
		queued++;
	}

	@Override
	public void eval(String scriptContent, String[] keys, String[] args)
	{
		writer.writeArray(Common.joinArrays(new Object[]{"EVAL", scriptContent, keys.length}, keys, args));
		queued++;
	}

	@Override
	public void evalsha(String hash, String[] keys, String[] args)
	{
		writer.writeArray(Common.joinArrays(new Object[]{"EVAL", hash, keys.length}, keys, args));
		queued++;
	}

	@Override
	public void scriptExists(String scriptHash, String... scriptHashes)
	{
		if (scriptHashes.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SCRIPT", "EXISTS", scriptHash}, scriptHashes));
		else
			writer.writeArray("SCRIPT", "EXISTS", scriptHash);
		queued++;
	}

	@Override
	public void scriptFlush()
	{
		writer.writeArray("SCRIPT", "FLUSH");
		queued++;
	}

	@Override
	public void scriptKill(String hash)
	{
		writer.writeArray("SCRIPT", "KILL", hash);
		queued++;
	}

	@Override
	public void scriptLoad(String content)
	{
		writer.writeArray("SCRIPT", "LOAD", content);
		queued++;
	}

	/**
	 * <p>From <a href="http://redis.io/commands/script-load">http://redis.io/commands/script-load</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the length in bytes of the script body.</p>
	 * <p>Load a script into the scripts cache from the specified file without executing it. After the specified 
	 * command is loaded into the script cache it will be callable using {@link #evalsha(String, String[], String[])} 
	 * with the correct SHA1 digest of the script, exactly like after the first successful invocation of {@link #eval(String, String[], String[])}.</p>
	 * @return the SHA1 digest of the script added into the script cache.
	 */
	public void scriptLoad(File content) throws IOException
	{
		scriptLoad(Common.getTextualContents(content));
	}

	/**
	 * <p>From <a href="http://redis.io/commands/script-load">http://redis.io/commands/script-load</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) with N being the length in bytes of the script body.</p>
	 * <p>Load a script into the scripts cache from the specified input stream (until the end is reached) without executing it. 
	 * The stream is not closed after read. After the specified command is loaded into the 
	 * script cache it will be callable using {@link #evalsha(String, String[], String[])} 
	 * with the correct SHA1 digest of the script, exactly like after the first 
	 * successful invocation of {@link #eval(String, String[], String[])}.</p>
	 * @return the SHA1 digest of the script added into the script cache.
	 */
	public void scriptLoad(InputStream content) throws IOException
	{
		scriptLoad(Common.getTextualContents(content));
	}

	@Override
	public void sadd(String key, String member, String... members)
	{
		if (members.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SADD", key, member}, members));
		else
			writer.writeArray("SADD", key, member);
		queued++;
	}

	@Override
	public void scard(String key)
	{
		writer.writeArray("SCARD", key);
		queued++;
	}

	@Override
	public void sdiff(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SDIFF", key}, keys));
		else
			writer.writeArray("SDIFF", key);
		queued++;
	}

	@Override
	public void sdiffstore(String destination, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SDIFFSTORE", destination, key}, keys));
		else
			writer.writeArray("SDIFFSTORE", destination, key);
		queued++;
	}

	@Override
	public void sinter(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SINTER", key}, keys));
		else
			writer.writeArray("SINTER", key);
		queued++;
	}

	@Override
	public void sinterstore(String destination, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SINTERSTORE", destination, key}, keys));
		else
			writer.writeArray("SINTERSTORE", destination, key);
		queued++;
	}

	@Override
	public void sismember(String key, String member)
	{
		writer.writeArray("SISMEMBER", key, member);
		queued++;
	}

	@Override
	public void smembers(String key)
	{
		writer.writeArray("SMEMBERS", key);
		queued++;
	}

	@Override
	public void smove(String source, String destination, String member)
	{
		writer.writeArray("SMOVE", source, destination, member);
		queued++;
	}

	@Override
	public void spop(String key)
	{
		writer.writeArray("SPOP", key);
		queued++;
	}

	@Override
	public void srandmember(String key)
	{
		writer.writeArray("SRANDMEMBER", key);
		queued++;
	}

	@Override
	public void srandmember(String key, long count)
	{
		writer.writeArray("SRANDMEMBER", key, count);
		queued++;
	}

	@Override
	public void srem(String key, String member, String... members)
	{
		if (members.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SREM", key, member}, members));
		else
			writer.writeArray("SREM", key, member);
		queued++;
	}

	@Override
	public void sunion(String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SUNION", key}, keys));
		else
			writer.writeArray("SUNION", key);
		queued++;
	}

	@Override
	public void sunionstore(String destination, String key, String... keys)
	{
		if (keys.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"SUNIONSTORE", destination, key}, keys));
		else
			writer.writeArray("SUNIONSTORE", destination, key);
		queued++;
	}

	@Override
	public void zadd(String key, double score, String member)
	{
		writer.writeArray("ZADD", score, member);
		queued++;
	}

	@Override
	public void zadd(String key, ObjectPair<Double, String>... pairs)
	{
		List<Object> out = new List<Object>(2 + (pairs.length * 2));
		out.add("ZADD");
		out.add(key);
		
		for (ObjectPair<Double, String> p : pairs)
		{
			out.add(p.getKey());
			out.add(p.getValue());
		}
		
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void zcard(String key)
	{
		writer.writeArray("ZCARD", key);
		queued++;
	}

	@Override
	public void zcount(String key, String min, String max)
	{
		writer.writeArray("ZCOUNT", key, min, max);
		queued++;
	}

	/**
	 * Like {@link #zcount(String, String, String)}, 
	 * except it accepts doubles for min and max, not strings.
	 */
	public void zcount(String key, double min, double max)
	{
		queued++;
	}

	@Override
	public void zincrby(String key, double increment, String member)
	{
		writer.writeArray("ZINCRBY", key, increment, member);
		queued++;
	}

	@Override
	public void zrange(String key, long start, long stop, boolean withScores)
	{
		if (withScores)
			writer.writeArray("ZRANGE", key, start, stop, "WITHSCORES");
		else
			writer.writeArray("ZRANGE", key, start, stop);
		queued++;
	}

	@Override
	public void zrangebyscore(String key, String min, String max, boolean withScores, Long limitOffset, Long limitCount)
	{
		List<Object> out = new List<Object>(8);
		out.add("ZRANGEBYSCORE");
		out.add(key);

		out.add(min);
		out.add(max);
		
		if (withScores)
			out.add("WITHSCORES");

		if (limitOffset != null && limitCount != null)
		{
			out.add("LIMIT");
			out.add(limitOffset);
			out.add(limitCount);
		}
		
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void zrangebyscore(String key, double min, double max, boolean withScores)
	{
		zrangebyscore(key, specialDouble(min), specialDouble(max), withScores, null, null);
	}

	@Override
	public void zrangebyscore(String key, String min, String max, boolean withScores)
	{
		zrangebyscore(key, min, max, withScores, null, null);
	}

	@Override
	public void zrangebyscore(String key, double min, double max, boolean withScores, Long limitOffset, Long limitCount)
	{
		zrangebyscore(key, specialDouble(min), specialDouble(max), withScores, limitOffset, limitCount);
	}

	@Override
	public void zrank(String key, String member)
	{
		writer.writeArray("ZRANK", key, member);
		queued++;
	}

	@Override
	public void zrem(String key, String member, String... members)
	{
		if (members.length > 0)
			writer.writeArray(Common.joinArrays(new String[]{"ZREM", key, member}, members));
		else
			writer.writeArray("ZREM", key, member);
		queued++;
	}

	@Override
	public void zremrangebyrank(String key, long start, long stop)
	{
		writer.writeArray("ZREMRANGEBYRANK", key, start, stop);
		queued++;
	}

	@Override
	public void zremrangebyscore(String key, String min, String max)
	{
		writer.writeArray("ZREMRANGEBYSCORE", key, min, max);
		queued++;
	}

	/**
	 * Like {@link #zremrangebyscore(String, String, String)},
	 * except it accepts doubles for min and max, not strings.
	 */
	public void zremrangebyscore(String key, double min, double max)
	{
		zremrangebyscore(key, specialDouble(min), specialDouble(max));
	}

	@Override
	public void zrevrank(String key, String member)
	{
		writer.writeArray("ZREVRANK", key, member);
		queued++;
	}

	@Override
	public void zrevrange(String key, long start, long stop, boolean withScores)
	{
		if (withScores)
			writer.writeArray("ZREVRANGE", key, start, stop, "WITHSCORES");
		else
			writer.writeArray("ZREVRANGE", key, start, stop);
		queued++;
	}

	@Override
	public void zrevrangebyscore(String key, String min, String max, boolean withScores, Long limitOffset, Long limitCount)
	{
		List<Object> out = new List<Object>(8);
		out.add("ZREVRANGEBYSCORE");
		out.add(key);

		out.add(min);
		out.add(max);
		
		if (withScores)
			out.add("WITHSCORES");

		if (limitOffset != null && limitCount != null)
		{
			out.add("LIMIT");
			out.add(limitOffset);
			out.add(limitCount);
		}
		
		writer.writeArray(out);
		queued++;
	}
	
	@Override
	public void zrevrangebyscore(String key, double min, double max, boolean withScores)
	{
		zrevrangebyscore(key, specialDouble(min), specialDouble(max), withScores, null, null);
	}

	@Override
	public void zrevrangebyscore(String key, String min, String max, boolean withScores)
	{
		zrevrangebyscore(key, min, max, withScores, null, null);
	}

	@Override
	public void zrevrangebyscore(String key, double min, double max, boolean withScores, Long limitOffset, Long limitCount)
	{
		zrevrangebyscore(key, specialDouble(min), specialDouble(max), withScores, limitOffset, limitCount);
	}

	@Override
	public void zscore(String key, String member)
	{
		writer.writeArray("ZSCORE", key, member);
		queued++;
	}

	@Override
	public void zinterstore(String destination, double[] weights, Aggregation aggregation, String key, String... keys)
	{
		List<Object> out = new List<Object>(6 + keys.length + (weights != null ? weights.length : 0));
		out.add("ZINTERSTORE");
		out.add(destination);

		out.add(keys.length + 1);
		out.add(key);
		for (String k : keys)
			out.add(k);
		
		if (weights.length > 0)
		{
			out.add("WEIGHTS");
			for (double w : weights)
				out.add(w);
		}
		
		if (aggregation != null)
			out.add(aggregation.name());
		
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void zinterstore(String destination, Aggregation aggregation, String key, String... keys)
	{
		zinterstore(destination, null, aggregation, key, keys);
	}

	@Override
	public void zinterstore(String destination, double[] weights, String key, String... keys)
	{
		zinterstore(destination, weights, null, key, keys);
	}

	@Override
	public void zinterstore(String destination, String key, String... keys)
	{
		zinterstore(destination, null, null, key, keys);
	}
	
	@Override
	public void zunionstore(String destination, double[] weights, Aggregation aggregation, String key, String... keys)
	{
		List<Object> out = new List<Object>(6 + keys.length + (weights != null ? weights.length : 0));
		out.add("ZUNIONSTORE");
		out.add(destination);

		out.add(keys.length + 1);
		out.add(key);
		for (String k : keys)
			out.add(k);
		
		if (weights.length > 0)
		{
			out.add("WEIGHTS");
			for (double w : weights)
				out.add(w);
		}
		
		if (aggregation != null)
			out.add(aggregation.name());
		
		writer.writeArray(out);
		queued++;
	}

	@Override
	public void zunionstore(String destination, Aggregation aggregation, String key, String... keys)
	{
		zunionstore(destination, null, aggregation, key, keys);
	}

	@Override
	public void zunionstore(String destination, double[] weights, String key, String... keys)
	{
		zunionstore(destination, weights, null, key, keys);
	}

	@Override
	public void zunionstore(String destination, String key, String... keys)
	{
		zunionstore(destination, null, null, key, keys);
	}
	
	@Override
	public void zlexcount(String key, String min, String max)
	{
		writer.writeArray("ZLEXCOUNT", key, min, max);
		queued++;
	}

	@Override
	public void zlexcount(String key, double min, double max)
	{
		zlexcount(key, specialDouble(min), specialDouble(max));
	}

	@Override
	public void zrangebylex(String key, String min, String max, Long limitOffset, Long limitCount)
	{
		List<Object> out = new List<Object>(8);
		out.add("ZRANGEBYLEX");
		out.add(key);

		out.add(min);
		out.add(max);
		
		if (limitOffset != null && limitCount != null)
		{
			out.add("LIMIT");
			out.add(limitOffset);
			out.add(limitCount);
		}
		
		writer.writeArray(out);
		queued++;
	}

	/**
	 * Like {@link #zrangebylex(String, String, String, Long, Long)},
	 * except it accepts doubles for min and max, not strings.
	 */
	public void zrangebylex(String key, double min, double max, Long limitOffset, Long limitCount)
	{
		zrangebylex(key, specialDouble(min), specialDouble(max), limitOffset, limitCount);
	}

	/**
	 * Like {@link #zrangebylex(String, String, String, Long, Long)}, with no limit.
	 */
	public void zrangebylex(String key, String min, String max)
	{
		zrangebylex(key, min, max, null, null);
	}

	/**
	 * Like {@link #zrangebylex(String, String, String)},
	 * except it accepts doubles for min and max, not strings, with no limit.
	 */
	public void zrangebylex(String key, double min, double max)
	{
		zrangebylex(key, specialDouble(min), specialDouble(max), null, null);
	}

	@Override
	public void zremrangebylex(String key, String min, String max)
	{
		writer.writeArray("ZREMRANGEBYLEX", key, min, max);
		queued++;
	}

	@Override
	public void zremrangebylex(String key, double min, double max)
	{
		zremrangebylex(key, specialDouble(min), specialDouble(max));
	}

	/**
	 * Converts some of Java's primitive doubles to Redis interval values.
	 * <ul>
	 * <li>{@link Double#NEGATIVE_INFINITY} converts to "-".</li>
	 * <li>{@link Double#POSITIVE_INFINITY} converts to "+".</li>
	 * <li>All others convert to their string representation.</li>
	 * </ul>
	 * @param d the input double.
	 */
	protected String specialDouble(double d)
	{
		if (d == Double.POSITIVE_INFINITY)
			return "+";
		else if (d == Double.NEGATIVE_INFINITY)
			return "-";
		else
			return String.valueOf(d);
	}

}
