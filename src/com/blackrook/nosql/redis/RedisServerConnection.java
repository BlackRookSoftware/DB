package com.blackrook.nosql.redis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;

import com.blackrook.commons.Common;
import com.blackrook.commons.hash.HashMap;
import com.blackrook.commons.list.List;
import com.blackrook.nosql.redis.commands.RedisServerCommands;
import com.blackrook.nosql.redis.data.RedisObject;
import com.blackrook.nosql.redis.enums.EncodingType;
import com.blackrook.nosql.redis.enums.ReturnType;
import com.blackrook.nosql.redis.exception.RedisException;
import com.blackrook.nosql.redis.exception.RedisParseException;

/**
 * A connection to a Redis server primarily for server and administrative operations.
 * @author Matthew Tropiano
 */
public class RedisServerConnection extends RedisConnectionAbstract implements RedisServerCommands
{
	/**
	 * Creates an open connection to localhost, port 6379, the default Redis port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisServerConnection() throws IOException
	{
		super();
	}

	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 */
	public RedisServerConnection(String host, int port) throws IOException
	{
		super(new RedisInfo(host, port));
	}

	/**
	 * Creates an open connection.
	 * @param host the server hostname or address.
	 * @param port the server connection port.
	 * @param password the server database password.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public RedisServerConnection(String host, int port, String password) throws IOException
	{
		super(new RedisInfo(host, port, password));
	}

	/**
	 * Creates an open connection.
	 * @param info the {@link RedisInfo} class detailing a connection.
	 * @throws IOException if an I/O error occurs when creating the socket.
	 * @throws UnknownHostException if the IP address of the host could not be determined.
	 * @throws SecurityException if a security manager exists and doesn't allow the connection to be made.
	 * @throws RedisException if the password in the server information is incorrect. 
	 */
	public RedisServerConnection(RedisInfo info) throws IOException
	{
		super(info);
	}

	@Override
	public boolean bgrewriteaof()
	{
		writer.writeArray("BGREWRITEAOF");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public boolean bgsave()
	{
		writer.writeArray("BGSAVE");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public boolean clientKill(String ip, int port)
	{
		writer.writeArray("CLIENT", "KILL", ip+":"+port);
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public String[] clientList()
	{
		writer.writeArray("CLIENT", "LIST");
		return ReturnType.ARRAY.readFrom(reader);
	}

	@Override
	public boolean clientPause(long millis)
	{
		writer.writeArray("CLIENT", "PAUSE", millis);
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public String configGet(String configKey)
	{
		writer.writeArray("CONFIG", "GET", configKey);
		return ReturnType.STRING.readFrom(reader);
	}

	@Override
	public boolean configRewrite()
	{
		writer.writeArray("CONFIG", "REWRITE");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public boolean configSet(String parameter, String value)
	{
		writer.writeArray("CONFIG", "GET", parameter, value);
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public boolean configResetStat()
	{
		writer.writeArray("CONFIG", "RESETSTAT");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public long dbsize()
	{
		writer.writeArray("DBSIZE");
		return ReturnType.INTEGER.readFrom(reader);
	}

	@Override
	public boolean flushall()
	{
		writer.writeArray("FLUSHALL");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public boolean flushdb()
	{
		writer.writeArray("FLUSHDB");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public String info()
	{
		return info(null);
	}

	@Override
	public String info(String section)
	{
		if (section != null)
			writer.writeArray("INFO", section);
		else
			writer.writeArray("INFO");
		return ReturnType.STRING.readFrom(reader);
	}

	public HashMap<String, String> infoMap()
	{
		return infoMap(null);
	}
	
	public HashMap<String, String> infoMap(String section)
	{
		BufferedReader br = new BufferedReader(new StringReader(info(section)));
		HashMap<String, String> out = new HashMap<String, String>(30);
		String line = null;
		
		try {
			while ((line = br.readLine()) != null)
			{
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				int cidx = line.indexOf(':');
				out.put(line.substring(0, cidx), line.substring(cidx + 1));
			}
		} catch (IOException e) {
			throw new RedisParseException("Could not parse INFO from info call.");
		} finally {
			Common.close(br);
		}
		
		return out;
	}
	
	@Override
	public long lastsave()
	{
		writer.writeArray("LASTSAVE");
		return ReturnType.INTEGER.readFrom(reader);
	}

	/**
	 * <p>From <a href="http://redis.io/commands/migrate">http://redis.io/commands/migrate</a>:</p>
	 * <p><strong>Available since 2.6.0.</strong></p>
	 * <p><strong>Time complexity:</strong> This command actually executes a {@link #dump} 
	 * and {@link #del} in the source instance, and a {@link #restore} in the target 
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
	 * @return always true.
	 */
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout)
	{
		return migrate(host, port, key, destinationDB, timeout, false, false);
	}

	@Override
	public boolean migrate(String host, int port, String key, long destinationDB, long timeout, boolean copy, boolean replace)
	{
		List<Object> out = new List<Object>(8);
		out.add("MIGRATE");
		out.add(host);
		out.add(port);
		out.add(key);
		out.add(destinationDB);
		out.add(timeout);
		if (copy)
			out.add("COPY");
		if (replace)
			out.add("REPLACE");
		writer.writeArray(out);
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public RedisObject object(String subcommand, String key)
	{
		writer.writeArray("OBJECT", subcommand, key);
		return ReturnType.OBJECT.readFrom(reader);
	}

	@Override
	public long objectRefcount(String key)
	{
		writer.writeArray("OBJECT", "REFCOUNT", key);
		return ReturnType.INTEGER.readFrom(reader);
	}

	@Override
	public EncodingType objectEncoding(String key)
	{
		writer.writeArray("OBJECT", "ENCODING", key);
		return ReturnType.ENCODING.readFrom(reader);
	}

	@Override
	public long objectIdletime(String key)
	{
		writer.writeArray("OBJECT", "IDLETIME", key);
		return ReturnType.INTEGER.readFrom(reader);
	}

	@Override
	public RedisObject pubsub(String subcommand, String... arguments)
	{
		writer.writeArray(Common.joinArrays(new String[]{"PUBSUB", subcommand}, arguments));
		return ReturnType.OBJECT.readFrom(reader);
	}

	@Override
	public String[] pubsubChannels(String pattern)
	{
		writer.writeArray("PUBSUB", "CHANNELS", pattern);
		return ReturnType.ARRAY.readFrom(reader);
	}

	@Override
	public String[] pubsubNumsub(String... arguments)
	{
		writer.writeArray(Common.joinArrays(new String[]{"PUBSUB", "NUMSUB"}, arguments));
		return ReturnType.ARRAY.readFrom(reader);
	}

	@Override
	public long pubsubNumpat()
	{
		writer.writeArray("PUBSUB", "NUMPAT");
		return ReturnType.INTEGER.readFrom(reader);
	}

	@Override
	public boolean save()
	{
		writer.writeArray("SAVE");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public void shutdown(boolean save)
	{
		writer.writeArray("SHUTDOWN", save ? "SAVE" : "NOSAVE");
		RedisObject obj = ReturnType.OBJECT.readFrom(reader);
		if (obj.isError())
			throw new RedisException(obj.asString());
		else
			Common.close(this);
	}

	@Override
	public boolean slaveof(String host, String port)
	{
		writer.writeArray("SLAVEOF", host, port);
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public boolean slaveofNoOne()
	{
		writer.writeArray("SLAVEOF", "NO", "ONE");
		return ReturnType.OK.readFrom(reader);
	}

	@Override
	public RedisObject slowlog(String subcommand, String argument)
	{
		if (argument != null)
			writer.writeArray("SLOWLOG", subcommand, argument);
		else
			writer.writeArray("SLOWLOG", subcommand);
		return ReturnType.OBJECT.readFrom(reader);
	}

	@Override
	public RedisObject slowlogGet(long recentCount)
	{
		return slowlog("GET", String.valueOf(recentCount));
	}

	@Override
	public RedisObject slowlogLen()
	{
		return slowlog("LEN", null);
	}

	@Override
	public RedisObject slowlogReset()
	{
		return slowlog("RESET", null);
	}

	@Override
	public long[] time()
	{
		writer.writeArray("TIME");
		String[] out = ReturnType.ARRAY.readFrom(reader);
		return new long[]{Common.parseLong(out[0]), Common.parseLong(out[1])};
	}

}
