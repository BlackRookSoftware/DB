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
