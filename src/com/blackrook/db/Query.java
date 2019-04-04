/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
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
