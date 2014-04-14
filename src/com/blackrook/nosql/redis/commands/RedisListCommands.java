package com.blackrook.nosql.redis.commands;

/**
 * Interface for Redis commands related to Lists.
 * @author Matthew Tropiano
 * TODO: Proper docs and return types.
 */
public interface RedisListCommands
{
	/**
	 * <p>From <a href="http://redis.io/commands/blpop">http://redis.io/commands/blpop</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><a href="/commands/blpop">BLPOP</a> is a blocking list pop primitive. It is the blocking version of <a href="/commands/lpop">LPOP</a> because it blocks the connection when there are no elements to pop from any of the given lists. An element is popped from the head of the first list that is non-empty, with the given keys being checked in the order that they are given.</p>
	 * @return <a href="/topics/protocol#array-reply">Array reply</a>: specifically:
	 */
	public void blpop(long timeout, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/brpop">http://redis.io/commands/brpop</a>:</p>
	 * <p><strong>Available since 2.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><a href="/commands/brpop">BRPOP</a> is a blocking list pop primitive. It is the blocking version of <a href="/commands/rpop">RPOP</a> because it blocks the connection when there are no elements to pop from any of the given lists. An element is popped from the tail of the first list that is non-empty, with the given keys being checked in the order that they are given.</p>
	 * @return <a href="/topics/protocol#array-reply">Array reply</a>: specifically:
	 */
	public void brpop(long timeout, String... keys);

	/**
	 * <p>From <a href="http://redis.io/commands/brpoplpush">http://redis.io/commands/brpoplpush</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p><a href="/commands/brpoplpush">BRPOPLPUSH</a> is the blocking variant of <a href="/commands/rpoplpush">RPOPLPUSH</a>. When <code>source</code> contains elements, this command behaves exactly like <a href="/commands/rpoplpush">RPOPLPUSH</a>. When <code>source</code> is empty, Redis will block the connection until another client pushes to it or until <code>timeout</code> is reached. A <code>timeout</code> of zero can be used to block indefinitely.</p>
	 * @return <a href="/topics/protocol#bulk-string-reply">Bulk string reply</a>: the element being popped from <code>source</code> and pushed to <code>destination</code>. If <code>timeout</code> is reached, a <a href="/topics/protocol#nil-reply">Null reply</a> is returned.
	 */
	public void brpoplpush(long timeout, String source, String destination);

	/**
	 * <p>From <a href="http://redis.io/commands/lindex">http://redis.io/commands/lindex</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of elements to traverse to get to the element at index. This makes asking for the first or the last element of the list O(1).</p>
	 * <p>Returns the element at index <code>index</code> in the list stored at <code>key</code>. The index is zero-based, so <code>0</code> means the first element, <code>1</code> the second element and so on. Negative indices can be used to designate elements starting at the tail of the list. Here, <code>-1</code> means the last element, <code>-2</code> means the penultimate and so forth.</p>
	 * @return <a href="/topics/protocol#bulk-string-reply">Bulk string reply</a>: the requested element, or <code>nil</code> when <code>index</code> is out of range.
	 */
	public void lindex(String key, String index);

	/**
	 * <p>From <a href="http://redis.io/commands/linsert">http://redis.io/commands/linsert</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of elements to traverse before seeing the value pivot. This means that inserting somewhere on the left end on the list (head) can be considered O(1) and inserting somewhere on the right end (tail) is O(N).</p>
	 * <p>Inserts <code>value</code> in the list stored at <code>key</code> either before or after the reference value <code>pivot</code>.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>: the length of the list after the insert operation, or <code>-1</code> when the value <code>pivot</code> was not found.
	 */
	public void linsert(String key, boolean before, String pivot, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/llen">http://redis.io/commands/llen</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Returns the length of the list stored at <code>key</code>. If <code>key</code> does not exist, it is interpreted as an empty list and <code>0</code> is returned. An error is returned when the value stored at <code>key</code> is not a list.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>: the length of the list at <code>key</code>.
	 */
	public void llen(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/lpop">http://redis.io/commands/lpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the first element of the list stored at <code>key</code>.</p>
	 * @return <a href="/topics/protocol#bulk-string-reply">Bulk string reply</a>: the value of the first element, or <code>nil</code> when <code>key</code> does not exist.
	 */
	public void lpop(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/lpush">http://redis.io/commands/lpush</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Insert all the specified values at the head of the list stored at <code>key</code>. If <code>key</code> does not exist, it is created as empty list before performing the push operations. When <code>key</code> holds a value that is not a list, an error is returned.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>: the length of the list after the push operations.
	 */
	public void lpush(String key, String... values);

	/**
	 * <p>From <a href="http://redis.io/commands/lpushx">http://redis.io/commands/lpushx</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Inserts <code>value</code> at the head of the list stored at <code>key</code>, only if <code>key</code> already exists and holds a list. In contrary to <a href="/commands/lpush">LPUSH</a>, no operation will be performed when <code>key</code> does not yet exist.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>: the length of the list after the push operation.
	 */
	public void lpushx(String key, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/lrange">http://redis.io/commands/lrange</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(S+N) where S is the start offset and N is the number of elements in the specified range.</p>
	 * <p>Returns the specified elements of the list stored at <code>key</code>. The offsets <code>start</code> and <code>stop</code> are zero-based indexes, with <code>0</code> being the first element of the list (the head of the list), <code>1</code> being the next element and so on.</p>
	 * @return <a href="/topics/protocol#array-reply">Array reply</a>: list of elements in the specified range.
	 */
	public void lrange(String key, String start, String stop);
	
	/**
	 * <p>From <a href="http://redis.io/commands/lrem">http://redis.io/commands/lrem</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the length of the list.</p>
	 * <p>Removes the first <code>count</code> occurrences of elements equal to <code>value</code> from the list stored at <code>key</code>. The <code>count</code> argument influences the operation in the following ways:</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>: the number of removed elements.
	 */
	public void lrem(String key, String count, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/lset">http://redis.io/commands/lset</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the length of the list. Setting either the first or the last element of the list is O(1).</p>
	 * <p>Sets the list element at <code>index</code> to <code>value</code>. For more information on the <code>index</code> argument, see <a href="/commands/lindex">LINDEX</a>.</p>
	 * @return <a href="/topics/protocol#simple-string-reply">Simple string reply</a>
	 */
	public void lset(String key, String index, String value);

	/**
	 * <p>From <a href="http://redis.io/commands/ltrim">http://redis.io/commands/ltrim</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(N) where N is the number of elements to be removed by the operation.</p>
	 * <p>Trim an existing list so that it will contain only the specified range of elements specified. Both <code>start</code> and <code>stop</code> are zero-based indexes, where <code>0</code> is the first element of the list (the head), <code>1</code> the next element and so on.</p>
	 * @return <a href="/topics/protocol#simple-string-reply">Simple string reply</a>
	 */
	public void ltrim(String key, String start, String stop);

	/**
	 * <p>From <a href="http://redis.io/commands/rpop">http://redis.io/commands/rpop</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Removes and returns the last element of the list stored at <code>key</code>.</p>
	 * @return <a href="/topics/protocol#bulk-string-reply">Bulk string reply</a>: the value of the last element, or <code>nil</code> when <code>key</code> does not exist.
	 */
	public void rpop();

	/**
	 * <p>From <a href="http://redis.io/commands/rpoplpush">http://redis.io/commands/rpoplpush</a>:</p>
	 * <p><strong>Available since 1.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Atomically returns and removes the last element (tail) of the list stored at <code>source</code>, and pushes the element at the first element (head) of the list stored at <code>destination</code>.</p>
	 * @return <a href="/topics/protocol#bulk-string-reply">Bulk string reply</a>: the element being popped and pushed.
	 */
	public void rpoplpush(String source, String destination);

	/**
	 * <p>From <a href="http://redis.io/commands/rpush">http://redis.io/commands/rpush</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Insert all the specified values at the tail of the list stored at <code>key</code>. If <code>key</code> does not exist, it is created as empty list before performing the push operation. When <code>key</code> holds a value that is not a list, an error is returned.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>: the length of the list after the push operation.
	 */
	public void rpush(String key, String... values);

	/**
	 * <p>From <a href="http://redis.io/commands/rpushx">http://redis.io/commands/rpushx</a>:</p>
	 * <p><strong>Available since 2.2.0.</strong></p>
	 * <p><strong>Time complexity:</strong> O(1)</p>
	 * <p>Inserts <code>value</code> at the tail of the list stored at <code>key</code>, only if <code>key</code> already exists and holds a list. In contrary to <a href="/commands/rpush">RPUSH</a>, no operation will be performed when <code>key</code> does not yet exist.</p>
	 * @return <a href="/topics/protocol#integer-reply">Integer reply</a>: the length of the list after the push operation.
	 */
	public void rpushx(String key, String value);
	
}
