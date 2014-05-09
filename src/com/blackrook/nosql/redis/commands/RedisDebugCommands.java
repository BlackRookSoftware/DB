/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.nosql.redis.commands;

public interface RedisDebugCommands
{

	/**
	 * <p>From <a href="http://redis.io/commands/debug-object">http://redis.io/commands/debug-object</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><code>DEBUG OBJECT</code> is a debugging command that should not be used
	 * by clients. Check the {@link RedisServerCommands#object(String, String)} command instead.</p>
	 */
	public String debugObject(String key);

	/**
	 * <p>From <a href="http://redis.io/commands/debug-segfault">http://redis.io/commands/debug-segfault</a>:</p>
	 * <p><strong>Available since 1.0.0.</strong></p>
	 * <p><code>DEBUG SEGFAULT</code> performs an invalid memory access that 
	 * crashes Redis. It is used to simulate bugs during the development.</p>
	 */
	public void debugSegfault();

}
