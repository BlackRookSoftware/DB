/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.db;

/**
 * Implementations of this class render queries into the target SQL dialect
 * or other equivalent query language.
 * @author Matthew Tropiano
 * @since 2.4.0
 */
public interface QueryRenderer
{

	/**
	 * Renders a {@link SelectQuery} into a target language.
	 * @param select the query to render.
	 */
	public String renderQuery(SelectQuery select);
	
}
