/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.sql;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.blackrook.commons.Common;
import com.blackrook.commons.Reflect;
import com.blackrook.commons.TypeProfile;
import com.blackrook.commons.TypeProfile.MethodSignature;
import com.blackrook.commons.list.List;

/**
 * Core database utilities object.
 * This object serves as a foundation for all connection utility objects.
 * @author Matthew Tropiano
 */
public abstract class SQLUtil
{
	/**
	 * Returns the names of the columns in a ResultSet in the order that they appear in the result.
	 * @param set the ResultSet to get the columns from.
	 * @return an array of all of the columns.
	 * @throws SQLException if something goes wrong.
	 */
	public static String[] getAllColumnNamesFromResultSet(ResultSet set) throws SQLException
	{
		ResultSetMetaData md = set.getMetaData();
		String[] out = new String[md.getColumnCount()];
		for (int i = 0; i < out.length; i++)
			out[i] = md.getColumnName(i+1);
		return out;
	}

	/**
	 * Performs a query on a connection and extracts the data into a SQLResult object.
	 * @param statement the statement to execute.
	 * @param update if true, this is an update query. If false, it is a result query.
	 * @param parameters the parameters to pass to the 
	 * @return the query result returned.
	 */
	public static SQLResult callStatement(PreparedStatement statement, boolean update, Object ... parameters) throws SQLException
	{
		SQLResult out = null;
		ResultSet resultSet = null;
		
		int i = 1;
		for (Object obj : parameters)
			statement.setObject(i++, obj);
		
		if (update)
		{
			int rows = statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			out = createResult(resultSet, true, rows);
		}
		else
		{
			resultSet = statement.executeQuery();
			out = createResult(resultSet, false, -1);
		}
		
		Common.close(resultSet);
		return out;
	}
	
	/**
	 * Creates a {@link SQLResult} from a result set.
	 * The result set is assumed to be at the beginning of the set.
	 */
	private static SQLResult createResult(ResultSet resultSet, boolean update, int rows) throws SQLException
	{
		SQLResult out = null;

		if (update)
			out = new SQLResult(rows, resultSet);
		else
			out = new SQLResult(resultSet);
		
		return out;
	}
	
	/**
	 * Performs a query on a connection and extracts the data into a SQLResult.
	 * @param connection the connection to create a prepared statement and execute from.
	 * @param query the query statement to execute.
	 * @param parameters list of parameters for parameterized queries.
	 * @return the update result returned.
	 */
	public static SQLResult doQuery(Connection connection, String query, Object ... parameters) throws SQLException
	{
		PreparedStatement statement = null;
		SQLResult out = null;		
		statement = connection.prepareStatement(query);
		out = callStatement(statement, false, parameters);
		statement.close();
		return out;
	}
	
	/**
	 * Performs a query on a connection and creates objects from it, setting relevant fields.
	 * <p>
	 * Each result row is applied via the target object's public fields and setter methods.
	 * <p>
	 * For instance, if there is a column is a row called "color", its value
	 * will be applied via the public field "color" or the setter "setColor()". Public
	 * fields take precedence over setters.
	 * <p>
	 * Only certain types are converted without issue. Below is a set of source types
	 * and their valid target types:
	 * <table>
	 * <tr>
	 * 		<td><b>Boolean</b></td>
	 * 		<td>
	 * 			Boolean, all numeric primitives and their autoboxed equivalents, String. 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>Number</b></td>
	 * 		<td>
	 * 			Boolean (zero is false, nonzero is true), all numeric primitives and their autoboxed equivalents, String,
	 * 			Date, Timestamp. 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>Timestamp</b></td>
	 * 		<td>
	 * 			Long (both primitive and object as milliseconds since the Epoch), Timestamp, Date, String 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>Date</b></td>
	 * 		<td>
	 * 			Long (both primitive and object as milliseconds since the Epoch), Timestamp, Date, String 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>String</b></td>
	 * 		<td>
	 * 			Boolean, all numeric primitives and their autoboxed equivalents, 
	 * 			String, byte[], char[]. 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>Clob</b></td>
	 * 		<td>
	 * 			Boolean, all numeric primitives and their autoboxed equivalents, 
	 * 			String, byte[], char[]. 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>Blob</b></td>
	 * 		<td> 
	 * 			String, byte[], char[]. 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>Clob</b></td>
	 * 		<td>
	 * 			Boolean, all numeric primitives and their autoboxed equivalents, 
	 * 			String, byte[], char[]. 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>byte[]</b></td>
	 * 		<td>
	 *			String, byte[], char[]. 
	 * 		</td>
	 * </tr>
	 * <tr>
	 * 		<td><b>char[]</b></td>
	 * 		<td>
	 * 			Boolean, all numeric primitives and their autoboxed equivalents, 
	 * 			String, byte[], char[]. 
	 * 		</td>
	 * </tr>
	 * </table>
	 * @param type the class type to instantiate.
	 * @param connection the connection to create a prepared statement and execute from.
	 * @param query the query statement to execute.
	 * @param parameters list of parameters for parameterized queries.
	 * @return an array of instantiated objects with the pertinent fields set for each row.
	 * @throws ClassCastException if one object type cannot be converted to another.
	 * @see #createObjectFromResultRow(Class, ResultSet, String[])
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] doQuery(Class<T> type, Connection connection, String query, Object ... parameters) throws SQLException
	{
		PreparedStatement statement = null;
		T[] out = null;		
		statement = connection.prepareStatement(query);
		
		ResultSet resultSet = null;
		
		int i = 1;
		for (Object obj : parameters)
			statement.setObject(i++, obj);

		resultSet = statement.executeQuery();
		String[] columnNames = getAllColumnNamesFromResultSet(resultSet);
		
		List<T> rows = new List<T>();
		while (resultSet.next())
			rows.add(createObjectFromResultRow(type, resultSet, columnNames));

		statement.close();
		resultSet.close();
		
		rows.toArray(out = (T[])Array.newInstance(type, rows.size()));
		return out;
	}

	/**
	 * Performs a query on a connection and extracts the data into a SQLResult.
	 * @param connection the connection to create a prepared statement and execute from.
	 * @param query the query statement to execute.
	 * @param parameters list of parameters for parameterized queries.
	 * @return the update result returned.
	 */
	public static SQLResult doQueryUpdate(Connection connection, String query, Object ... parameters) throws SQLException
	{
		PreparedStatement statement = null;
		SQLResult out = null;		
		statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		out = callStatement(statement, true, parameters);
		statement.close();
		return out;
	}
	
	/**
	 * Creates a new object from a result row and sets the fields on it using row information.
	 * @param objectType the object type to instantiate.
	 * @param resultSet the result set.
	 * @param columnNames the names of the columns in the set.
	 * @return a new object with the relevant fields set.
	 * @throws ClassCastException if any incoming types cannot be converted.
	 */
	public static <T> T createObjectFromResultRow(Class<T> objectType, ResultSet resultSet, String[] columnNames) throws SQLException
	{
		T object = Reflect.create(objectType);
		
		TypeProfile<T> profile = TypeProfile.getTypeProfile(objectType);

		for (int i = 0; i < columnNames.length; i++)
		{
			String column = columnNames[i];
			
			Field field = null; 
			MethodSignature setter = null;
			if ((field = profile.getPublicFields().get(column)) != null)
			{
				Class<?> type = field.getType();
				Reflect.setField(object, column, Reflect.createForType(column, resultSet.getObject(i + 1), type));
			}
			else if ((setter = profile.getSetterMethods().get(column)) != null)
			{
				Class<?> type = setter.getType();
				Method method = setter.getMethod();
				Reflect.invokeBlind(method, object, Reflect.createForType(column, resultSet.getObject(i + 1), type));
			}			
		}

		return object;
	}
	
}
