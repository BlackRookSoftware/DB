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
