/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.db;

import java.util.Arrays;

import com.blackrook.commons.list.List;

/**
 * An object that represents a simple query, renderable, in some cases, as other-language queries.
 * @author Matthew Tropiano
 */
public final class SelectQuery
{
	/** Is Distinct? */
	private boolean distinct;
	/** Is Explained? */
	private boolean explained;
	/** Columns to return. */
	private List<ColumnDescriptor> columnList;
	/** From tables/collections. */
	private List<TableDescriptor> fromTables;
	/** Where clauses. */
	private List<CriteriaClause> whereClauses;
	/** Group clauses. */
	private List<GroupDescriptor> groupClauses;
	/** Having clauses. */
	private List<CriteriaClause> havingClauses;
	/** Order/sort clauses. */
	private List<OrderClause> orderClauses;
	/** Union clauses. */
	private List<UnionClause> unionClauses;
	/** Limit clause. */
	private LimitClause limitClause;
	
	/**
	 * Creates a new SelectQuery with no criteria.
	 */
	SelectQuery(boolean distinct, boolean explained)
	{
		this.explained = explained;
		this.distinct = distinct;
		this.columnList = null;
		this.fromTables = null;
		this.whereClauses = null;
		this.orderClauses = null;
		this.groupClauses = null;
		this.unionClauses = null;
		this.limitClause = null;
	}
	
	/**
	 * Adds a set of columns to select to select from.
	 * Multiple executions of this append more columns.
	 * Never calling this implies all columns.
	 * @param columns the list of columns to select.
	 * @return itself, to chain calls.
	 * @see #column(String)
	 * @see #column(String, String)
	 * @see #columnAs(String, String)
	 * @see #columnAs(String, String, String)
	 */
	public SelectQuery addColumns(ColumnDescriptor ... columns)
	{
		if (columns.length == 0) return this;
		
		if (columnList == null) columnList = new List<ColumnDescriptor>(columns.length);
		for (ColumnDescriptor t : columns)
			columnList.add(t);
		return this;
	}
	
	/**
	 * Adds tables to select from.
	 * Multiple executions of this append more tables.
	 * Depending on implementor, this may auto-join the tables together.
	 * @param tables the list of tables to select from.
	 * @return itself, to chain calls.
	 * @see #table(String)
	 * @see #table(String, String)
	 */
	public SelectQuery from(TableDescriptor ... tables)
	{
		if (tables.length == 0) return this;

		if (fromTables == null) fromTables = new List<TableDescriptor>(tables.length);
		for (TableDescriptor t : tables)
			fromTables.add(t);
		return this;
	}
	
	/**
	 * Adds conditional clauses to this query.
	 * Multiple executions of this append more clauses.
	 * @param criteria the list of criteria to add.
	 * @return itself, to chain calls.
	 * @see #criterion(String, QueryOperation, Object...)
	 * @see #criterion(String, String, QueryOperation, Object...)
	 */
	public SelectQuery where(CriteriaClause ... criteria)
	{
		if (criteria.length == 0) return this;
		
		if (whereClauses == null) whereClauses = new List<CriteriaClause>(criteria.length);
		for (CriteriaClause t : criteria)
			whereClauses.add(t);
		return this;
	}
	
	/**
	 * Adds conditional clauses to this query.
	 * Multiple executions of this append more groupings.
	 * @param groups the list of sort groups to add.
	 * @return itself, to chain calls.
	 * @see #sort(String)
	 * @see #sort(String, boolean)
	 * @see #sort(String, String, boolean)
	 */
	public SelectQuery groupBy(GroupDescriptor ... groups)
	{
		if (groups.length == 0) return this;
		
		if (groupClauses == null) groupClauses = new List<GroupDescriptor>(groups.length);
		for (GroupDescriptor t : groups)
			groupClauses.add(t);
		return this;
	}
	
	/**
	 * Adds conditional clauses to this query, after grouping.
	 * Multiple executions of this append more clauses.
	 * @param criteria the list of criteria to add.
	 * @return itself, to chain calls.
	 * @see #criterion(String, QueryOperation, Object...)
	 * @see #criterion(String, String, QueryOperation, Object...)
	 */
	public SelectQuery having(CriteriaClause ... criteria)
	{
		if (criteria.length == 0) return this;
		
		if (havingClauses == null) havingClauses = new List<CriteriaClause>(criteria.length);
		for (CriteriaClause t : criteria)
			havingClauses.add(t);
		return this;
	}
	
	/**
	 * Adds ordering clauses to this query.
	 * Multiple executions of this append more clauses.
	 * @param ordering the list of ordering criteria to add.
	 * @return itself, to chain calls.
	 * @see #sort(String)
	 * @see #sort(String, boolean)
	 * @see #sort(String, String)
	 * @see #sort(String, String, boolean)
	 */
	public SelectQuery orderBy(OrderClause ... ordering)
	{
		if (ordering.length == 0) return this;
		
		if (orderClauses == null) orderClauses = new List<OrderClause>(ordering.length);
		for (OrderClause t : ordering)
			orderClauses.add(t);
		return this;
	}
	
	/**
	 * Adds a UNION with another query.
	 * Multiple executions of this append more unions.
	 * @param query the query to union with.
	 * @return itself, to chain calls.
	 */
	public SelectQuery union(SelectQuery query)
	{
		if (unionClauses == null) unionClauses = new List<UnionClause>(2);
		unionClauses.add(new UnionClause(false, query));
		return this;
	}
	
	/**
	 * Adds a UNION ALL with another query.
	 * Multiple executions of this append more unions.
	 * @param query the query to union with.
	 * @return itself, to chain calls.
	 */
	public SelectQuery unionAll(SelectQuery query)
	{
		if (unionClauses == null) unionClauses = new List<UnionClause>(2);
		unionClauses.add(new UnionClause(true, query));
		return this;
	}
	
	/**
	 * Sets a limit on the amount of rows returned from this query.
	 * Multiple executions of this REPLACE this criteria.
	 * @param count the amount of rows to limit.
	 * @return itself, to chain calls.
	 */
	public SelectQuery limit(int count)
	{
		return limit(count, 0);
	}
	
	/**
	 * Sets a limit on the amount of rows returned from this query, plus offset.
	 * Multiple executions of this REPLACE this criteria.
	 * @param count the amount of rows to limit.
	 * @param offset the row offset to start from.
	 * @return itself, to chain calls.
	 */
	public SelectQuery limit(int count, int offset)
	{
		limitClause = new LimitClause(count, offset);
		return this;
	}
	
	/**
	 * Is this a DISTINCT query?
	 * @return true if so, false if not.
	 */
	public boolean isDistinct()
	{
		return distinct;
	}

	/**
	 * Is this an EXPLAIN SELECT query?
	 * @return true if so, false if not.
	 */
	public boolean isExplained()
	{
		return explained;
	}

	/**
	 * Returns the list of columns to return.
	 * Can be null, for ALL COLUMNS.
	 */
	public List<ColumnDescriptor> getColumnList()
	{
		return columnList;
	}

	/**
	 * Returns the list of tables to pull from.
	 * Can be null, for NO TABLES.
	 */
	public List<TableDescriptor> getFromTables()
	{
		return fromTables;
	}

	/**
	 * Returns the list of where clauses.
	 * Can be null.
	 */
	public List<CriteriaClause> getWhereClauses()
	{
		return whereClauses;
	}

	/**
	 * Returns the list of grouping clauses.
	 * Can be null.
	 */
	public List<GroupDescriptor> getGroupClauses()
	{
		return groupClauses;
	}

	/**
	 * Returns the list of having clauses.
	 * Can be null.
	 */
	public List<CriteriaClause> getHavingClauses()
	{
		return havingClauses;
	}

	/**
	 * Returns the list of ordering clauses.
	 * Can be null.
	 */
	public List<OrderClause> getOrderClauses()
	{
		return orderClauses;
	}

	/**
	 * Returns the list of unions.
	 * Can be null.
	 */
	public List<UnionClause> getUnionClauses()
	{
		return unionClauses;
	}

	/**
	 * Gets the limit clause.
	 * Can be null.
	 */
	public LimitClause getLimitClause()
	{
		return limitClause;
	}

	/**
	 * Returns a new table descriptor.
	 * @param name the name of the table.
	 */
	public static TableDescriptor table(String name)
	{
		return table(name, null);
	}

	/**
	 * Returns a new table descriptor.
	 * @param name the name of the table.
	 * @param alias the table alias.
	 */
	public static TableDescriptor table(String name, String alias)
	{
		return new TableDescriptor(name, alias);
	}

	/**
	 * Returns a new column descriptor.
	 * @param name the name of the column.
	 */
	public static ColumnDescriptor column(String name)
	{
		return new ColumnDescriptor(null, name, null);
	}
	
	/**
	 * Returns a new column descriptor.
	 * @param table the table that the column originates from (can be null).
	 * @param name the name of the column.
	 */
	public static ColumnDescriptor column(String table, String name)
	{
		return new ColumnDescriptor(table, name, null);
	}
	
	/**
	 * Returns a new column descriptor with alias.
	 * @param name the name of the column.
	 * @param as the column alias to return.
	 */
	public static ColumnDescriptor columnAs(String name, String as)
	{
		return new ColumnDescriptor(null, name, as);
	}
	
	/**
	 * Returns a new column descriptor with alias.
	 * @param table the table that the column originates from (can be null).
	 * @param name the name of the column.
	 * @param as the column alias to return.
	 */
	public static ColumnDescriptor columnAs(String table, String name, String as)
	{
		return new ColumnDescriptor(table, name, as);
	}
	
	/**
	 * Returns a new criteria clause.
	 * @param name the name of the column.
	 * @param operation the operation to perform.
	 * @param operands the operands in the clause. 
	 * @throws IllegalArgumentException if the operands provided do not match the required operands.
	 */
	public static CriteriaClause criterion(String name, QueryOperation operation, Object ... operands)
	{
		return new CriteriaClause(null, name, operation, operands);
	}
	
	/**
	 * Returns a new criteria clause.
	 * @param table the table that the column originates from (can be null).
	 * @param name the name of the column.
	 * @param operation the operation to perform.
	 * @param operands the operands in the clause. 
	 * @throws IllegalArgumentException if the operands provided do not match the required operands.
	 */
	public static CriteriaClause criterion(String table, String name, QueryOperation operation, Object ... operands)
	{
		return new CriteriaClause(table, name, operation, operands);
	}
	
	/**
	 * Returns an "identity" clause (1 = 1).
	 */
	public static CriteriaClause criterionIdentity()
	{
		return new CriteriaClause(null, null, QueryOperation.EQUALS, 1, 1);
	}
	
	/**
	 * Returns a sorting criteria for ordering clauses, ascending order.
	 * @param name the name of the column.
	 */
	public static OrderClause sort(String name)
	{
		return new OrderClause(null, name, false);
	}

	/**
	 * Returns a sorting criteria for ordering clauses.
	 * @param name the name of the column.
	 * @param descending in descending order?
	 */
	public static OrderClause sort(String name, boolean descending)
	{
		return new OrderClause(null, name, descending);
	}
	
	/**
	 * Returns a sorting criteria for ordering clauses, ascending order.
	 * @param table the table that the column originates from (can be null).
	 * @param name the name of the column.
	 */
	public static OrderClause sort(String table, String name)
	{
		return new OrderClause(table, name, false);
	}
	
	/**
	 * Returns a sorting criteria for ordering clauses.
	 * @param table the table that the column originates from (can be null).
	 * @param name the name of the column.
	 * @param descending in descending order?
	 */
	public static OrderClause sort(String table, String name, boolean descending)
	{
		return new OrderClause(table, name, descending);
	}
	
	/**
	 * Returns a new grouping descriptor.
	 * @param name the name of the column.
	 */
	public static GroupDescriptor group(String name)
	{
		return new GroupDescriptor(null, name);
	}
	
	/**
	 * Returns a new grouping descriptor.
	 * @param table the table that the column originates from (can be null).
	 * @param name the name of the column.
	 */
	public static GroupDescriptor group(String table, String name)
	{
		return new GroupDescriptor(table, name);
	}
	
	
	/** 
	 * Descriptor for a single column to return. 
	 */
	public static class ColumnDescriptor
	{
		/** Column table. */
		private String table;
		/** Column name. */
		private String name;
		/** Column as. */
		private String as;

		ColumnDescriptor(String table, String name, String as)
		{
			this.table = table;
			this.name = name;
			this.as = as;
		}

		/** Column table. Null for no specific table. */
		public String getTable()
		{
			return table;
		}
		
		/** Column name. */
		public String getName()
		{
			return name;
		}
		
		/** Column as. Null for no as. */
		public String getAlias()
		{
			return as;
		}
		
		@Override
		public String toString()
		{
			return (table != null ? table + "." : "") + name + (as != null ? " AS " + as : "");
		}
	}
	
	/** 
	 * Descriptor for a single table in the query.
	 */
	public static class TableDescriptor
	{
		/** Table name. */
		private String name;
		/** Table as. */
		private String alias;
		
		TableDescriptor(String name, String alias)
		{
			this.name = name;
			this.alias = alias;
		}
		
		/** Table name. */
		public String getName()
		{
			return name;
		}
		
		/** Table as. Null for no as. */
		public String getAlias()
		{
			return alias;
		}
		
		@Override
		public String toString()
		{
			return name + (alias != null ? " " + alias : "");
		}
	}
	
	/**
	 * Query "where" selection clause.
	 */
	public static class CriteriaClause
	{
		/** Column table. */
		private String table;
		/** Column name. */
		private String name;
		/** Clause operation. */
		private QueryOperation operation;
		/** Clause operation. */
		private Object[] operands;

		CriteriaClause(String table, String name, QueryOperation operation, Object ... operands)
		{
			this.table = table;
			this.name = name;
			this.operation = operation;
			this.operands = operands;
			
			if (operation != QueryOperation.IN_LIST && operation != QueryOperation.NOT_IN_LIST)
			{
				int targlen = operands.length + (this.name != null ? 1 : 0);
				if (operation.getOperandCount() != targlen)
					throw new IllegalArgumentException("Where clause operation needs "+operation.getOperandCount()+" operands; found "+targlen);
			}
			else if (name == null)
			{
				throw new IllegalArgumentException("IN LIST where clause requires a name.");
			}
		}
		
		/** Column table. Null for no specific table. */
		public String getTable()
		{
			return table;
		}
		
		/** Column name. Null for an extra operand. */
		public String getName()
		{
			return name;
		}
		
		/** Get operation */
		public QueryOperation getOperation()
		{
			return operation;
		}
		
		public Object[] getOperands()
		{
			return operands;
		}
		
		@Override
		public String toString()
		{
			return (name != null ? (table != null ? table + "." : "") + name + " " : "") + operation.name() + " " + Arrays.toString(operands);
		}

	}
	
	/**
	 * Query "order" or "sort" selection clause.
	 */
	public static class OrderClause
	{
		/** Column table. */
		private String table;
		/** Column name. */
		private String name;
		/** Descending? */
		private boolean descending;

		OrderClause(String table, String name, boolean descending)
		{
			this.table = table;
			this.name = name;
			this.descending = descending;
		}
		
		/** Column table. Null for no specific table. */
		public String getTable()
		{
			return table;
		}
		
		/** Column name. */
		public String getName()
		{
			return name;
		}

		/** Descending? */
		public boolean isDescending()
		{
			return descending;
		}

		@Override
		public String toString()
		{
			return (table != null ? table + "." : "") + name + " " + (descending ? "DESC" : "ASC") ;
		}
	}
	
	/**
	 * Query "group" column.
	 */
	public static class GroupDescriptor
	{
		/** Column table. */
		private String table;
		/** Column name. */
		private String name;

		GroupDescriptor(String table, String name)
		{
			this.table = table;
			this.name = name;
		}
		
		/** Column table. Null for no specific table. */
		public String getTable()
		{
			return table;
		}
		
		/** Column name. */
		public String getName()
		{
			return name;
		}
		
		@Override
		public String toString()
		{
			return (table != null ? table + "." : "") + name ;
		}
	}
	
	/**
	 * Union clause.
	 */
	public static class UnionClause
	{
		/** Union all? */
		private boolean all;
		/** Query to union with. */
		private SelectQuery query;
		
		public UnionClause(boolean all, SelectQuery query)
		{
			this.all = all;
			this.query = query;
		}
		
		/** Union is an "ALL" union? */
		public boolean isAll()
		{
			return all;
		}
		
		/** Gets the query to union with. */
		public SelectQuery getQuery()
		{
			return query;
		}
		
		@Override
		public String toString()
		{
			return "UNION" + (all ? "ALL" : "");
		}
	}
	
	/**
	 * Record limit clause.
	 */
	public static class LimitClause
	{
		/** Limit count. */
		private int limit;
		/** Offset count. */
		private int offset;
		
		LimitClause(int limit, int offset)
		{
			this.limit = limit;
			this.offset = offset;
		}
		
		/** Gets the limit offset.*/
		public int getLimit()
		{
			return limit;
		}
		
		/** Gets the limit offset. */
		public int getOffset()
		{
			return offset;
		}
		
		@Override
		public String toString()
		{
			return "LIMIT "+limit+" OFFSET "+offset;
		}
		
	}
	
	
}
