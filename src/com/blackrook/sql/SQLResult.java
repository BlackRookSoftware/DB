/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.blackrook.commons.ResettableIterable;
import com.blackrook.commons.ResettableIterator;
import com.blackrook.commons.list.List;

/**
 * The data encapsulation of the result of a query {@link java.sql.ResultSet}.
 * @author Matthew Tropiano
 */
public class SQLResult implements ResettableIterable<SQLRow>
{
	private static final String[] EMPTY_ARRAY = new String[0];
	
	/** Query Columns. */
	protected String[] columnNames;
	/** Rows affected or returned in the query. */
	protected int rowCount;
	/** Next id, if generated. */
	protected long[] nextId;
	/** Was this an update query? */
	protected boolean update;
	/** Set of hash maps of associative data. */
	protected List<SQLRow> rows;
	
	/**
	 * Creates a new query result from an update query. 
	 */
	SQLResult(int rowsAffected) throws SQLException
	{
		columnNames = EMPTY_ARRAY;
		update = true;
		rowCount = rowsAffected;
		rows = null;
	}

	/**
	 * Creates a new query result from an update query, plus generated keys. 
	 */
	SQLResult(int rowsAffected, ResultSet genKeys) throws SQLException
	{
		this(rowsAffected);
		
		List<Long> vect = new List<Long>();
		while (genKeys.next())
			vect.add(genKeys.getLong(1));
		
		nextId = new long[vect.size()];
		int x = 0;
		for (long lng : vect)
			nextId[x++] = lng; 
	}

	/**
	 * Creates a new query result from a result set. 
	 */
	SQLResult(ResultSet rs) throws SQLException
	{
		update = false;
		rowCount = 0;

		columnNames = SQLUtil.getAllColumnNamesFromResultSet(rs);

		rows = new List<SQLRow>();
		while (rs.next())
		{
			rows.add(new SQLRow(rs, columnNames));
			rowCount++;
		}
	}
	
	/**
	 * Gets the names of the columns.
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**
	 * Gets the amount of affected/returned rows from this query. 
	 */
	public int getRowCount()
	{
		return rowCount;
	}

	/**
	 * Returns true if this came from an update.
	 */
	public boolean isUpdate()
	{
		return update;
	}
	
	/**
	 * Retrieves the rows from the query result.
	 */
	public List<SQLRow> getRows()
	{
		return rows;
	}

	/**
	 * Gets the first row, or only row in this result,
	 * or null if no rows.
	 */
	public SQLRow getRow()
	{
		return rows.size() > 0 ? rows.getByIndex(0) : null;
	}
	
	/**
	 * Returns the generated id from the last query, if any, or 0L if none.
	 */
	public long getId()
	{
		return nextId.length > 0 ? nextId[0] : 0L;
	}
	
	/**
	 * Returns the list of generated ids from the last query.
	 */
	public long[] getIds()
	{
		return nextId;
	}
	
	@Override
	public ResettableIterator<SQLRow> iterator()
	{
		return rows.iterator();
	}

}
