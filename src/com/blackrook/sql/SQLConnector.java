/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Core database JDBC connector object.
 * From this object, representing a potential link to a remote (or local) database, connections can be spawned.
 * @author Matthew Tropiano
 */
public class SQLConnector
{
	/** JDBC URL. */
	private String jdbcURL;
	
	/**
	 * Constructs a new database connector.
	 * @param className		The fully qualified class name of the driver.
	 * @param jdbcURL		The JDBC URL to use.
	 * @throws RuntimeException if the driver class cannot be found.
	 */
	public SQLConnector(String className, String jdbcURL)
	{
		this.jdbcURL = jdbcURL;
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the full JDBC URL for this specific connector.
	 * This differs by implementation and driver.
	 */
	public String getJDBCURL()
	{
		return jdbcURL;
	}

	/**
	 * Returns a new, opened JDBC Connection, without using any parameters or credentials (some DB engines can use this).
	 * @throws SQLException	if a connection can't be procured.
	 */
	public Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(getJDBCURL());
	}

	/**
	 * Returns a new, opened JDBC Connection.
	 * @param info the set of {@link Properties} to pass along to the JDBC {@link DriverManager}.
	 * @throws SQLException	if a connection can't be procured.
	 * @see DriverManager#getConnection(String, Properties)
	 */
	public Connection getConnection(Properties info) throws SQLException
	{
		return DriverManager.getConnection(getJDBCURL(), info);
	}

	/**
	 * Returns a new, opened JDBC Connection using a user name and password.
	 * @throws SQLException	if a connection can't be procured.
	 */
	public Connection getConnection(String username, String password) throws SQLException
	{
		return DriverManager.getConnection(getJDBCURL(), username, password);
	}

}
