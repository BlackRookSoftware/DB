/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.db;

/**
 * Enumerations of query operations.
 * @author Matthew Tropiano
 */
public enum QueryOperation
{
	EQUALS(2),
	NOT_EQUALS(2),
	GREATER(2),
	GREATER_EQUAL(2),
	LESSER(2),
	LESSER_EQUAL(2),
	BETWEEN(3),
	IS_NULL,
	IS_NOT_NULL,
	IN_LIST(2),
	NOT_IN_LIST(2),
	LIKE(2);
	
	final int operandCount;

	private QueryOperation()
	{
		this(0);
	}
	
	private QueryOperation(int operandCount)
	{
		this.operandCount = operandCount; 
	}
	
	/** Operand count. */
	public int getOperandCount()
	{
		return operandCount;
	}
	
}
