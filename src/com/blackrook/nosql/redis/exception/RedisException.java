/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.exception;

/**
 * An exception thrown during a Redis command or a read error.
 * @author Matthew Tropiano
 */
public class RedisException extends RuntimeException
{
	private static final long serialVersionUID = -299077146531902487L;

	public RedisException()
	{
		super("An exception occurred.");
	}

	public RedisException(String message)
	{
		super(message);
	}

	public RedisException(Throwable cause)
	{
		super(cause);
	}
	
	public RedisException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
}
