package com.blackrook.db;

import com.blackrook.db.SelectQuery.ColumnDescriptor;

/**
 * Query generator class.
 * @author Matthew Tropiano
 */
public final class Query
{
	/** Parameter for parameterized queries. */
	public static final String PARAMETER = "?";	

	/**
	 * Prepares and creates a new select query.
	 * @param columns the columns to add.
	 */
	public static SelectQuery select(ColumnDescriptor ... columns)
	{
		return new SelectQuery(false, false).addColumns(columns);
}
}
