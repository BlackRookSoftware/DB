package com.blackrook.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import com.blackrook.sql.SQLUtil;
import com.blackrook.sql.SQLResult;

/**
 * A transaction object that holds a connection that guarantees an isolation level
 * of some kind. Queries can be made through this object until it has been released. 
 * <p>
 * This object's {@link #finalize()} method attempts to roll back the transaction if it hasn't already
 * been finished.
 * @author Matthew Tropiano
 * @since 2.3.0
 */
public class SQLTransaction
{
	/** 
	 * Enumeration of transaction levels. 
	 */
	public enum Level
	{
		/**
		 * From {@link Connection}: A constant indicating that dirty reads are 
		 * prevented; non-repeatable reads and phantom reads can occur. This 
		 * level only prohibits a transaction from reading a row with uncommitted 
		 * changes in it.
		 */
		READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
		/**
		 * From {@link Connection}: A constant indicating that dirty reads, 
		 * non-repeatable reads and phantom reads can occur. This level allows 
		 * a row changed by one transaction to be read by another transaction 
		 * before any changes in that row have been committed (a "dirty read"). 
		 * If any of the changes are rolled back, the second transaction will 
		 * have retrieved an invalid row.
		 */
		READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
		/**
		 * From {@link Connection}: A constant indicating that dirty reads and 
		 * non-repeatable reads are prevented; phantom reads can occur. 
		 * This level prohibits a transaction from reading a row with 
		 * uncommitted changes in it, and it also prohibits the situation 
		 * where one transaction reads a row, a second transaction alters 
		 * the row, and the first transaction rereads the row, getting different 
		 * values the second time (a "non-repeatable read").
		 */
		REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
		/**
		 * From {@link Connection}: A constant indicating that dirty reads, 
		 * non-repeatable reads and phantom reads are prevented. This level 
		 * includes the prohibitions in TRANSACTION_REPEATABLE_READ and further 
		 * prohibits the situation where one transaction reads all rows that 
		 * satisfy a WHERE condition, a second transaction inserts a row that 
		 * satisfies that WHERE condition, and the first transaction rereads for 
		 * the same condition, retrieving the additional "phantom" row in the 
		 * second read.
		 */
		SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE),
		;
		
		private final int id;
		private Level(int id)
		{
			this.id = id;
		}
	}

	/** The encapsulated connection. */
	private Connection connection;
	
	/** Previous level state on the incoming connection. */
	private int previousLevelState;
	/** Previous auto-commit state on the incoming connection. */
	private boolean previousAutoCommit;
	
	/**
	 * Wraps a connection in a transaction.
	 * The connection gets {@link Connection#setAutoCommit(boolean)} called on it with a FALSE parameter,
	 * and sets the transaction isolation level. These 
	 * @param toolkit the {@link Toolkit}.
	 * @param connection the connection to the database to use for this transaction.
	 * @param transactionLevel the transaction level to set on this transaction.
	 * @throws RuntimeException if the transaction could not be created.
	 */
	SQLTransaction(Connection connection, Level transactionLevel)
	{
		this.connection = connection;
		try {
			this.previousLevelState = connection.getTransactionIsolation();
			this.previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(transactionLevel.id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns true if this transaction has been completed or false if
	 * no more methods can be invoked on it.
	 */
	public boolean isFinished()
	{
		return connection == null; 
	}	
	
	/**
	 * Completes this transaction and prevents further calls on it.
	 * This calls {@link Connection#commit()} and {@link Connection#close()} 
	 * on the encapsulated connection and resets its previous transaction level state plus its auto-commit state.
	 * @throws IllegalStateException if this transaction was already finished.
	 * @throws RuntimeException if this causes a database error.
	 */
	public void complete()
	{
		if (isFinished())
			throw new IllegalStateException("This transaction is already finished.");
		
		commit();
		try {
			connection.setTransactionIsolation(previousLevelState);
			connection.setAutoCommit(previousAutoCommit);
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		connection = null;
	}
	
	/**
	 * Aborts this transaction and prevents further calls on it.
	 * This calls {@link Connection#rollback()} and {@link Connection#close()} 
	 * on the encapsulated connection and resets its previous transaction level state plus its auto-commit state.
	 * @throws IllegalStateException if this transaction was already finished.
	 * @throws RuntimeException if this causes a database error.
	 */
	public void abort()
	{
		if (isFinished())
			throw new IllegalStateException("This transaction is already finished.");
		
		rollback();
		try {
			connection.setTransactionIsolation(previousLevelState);
			connection.setAutoCommit(previousAutoCommit);
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		connection = null;
	}
	
	/**
	 * Commits the actions completed so far in this transaction.
	 * This is also called during {@link #finish()}.
	 * @throws IllegalStateException if this transaction was already finished.
	 * @throws RuntimeException if this causes a database error.
	 */
	public void commit()
	{
		if (isFinished())
			throw new IllegalStateException("This transaction is already finished.");
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Rolls back this entire transaction.
	 * @throws IllegalStateException if this transaction was already finished.
	 * @throws SQLException if this causes a database error.
	 */
	public void rollback()
	{
		if (isFinished())
			throw new IllegalStateException("This transaction is already finished.");
		try {
			connection.rollback();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Rolls back this transaction to a {@link Savepoint}. Everything executed
	 * after the {@link Savepoint} passed into this method will be rolled back.
	 * @param savepoint the {@link Savepoint} to roll back to.
	 * @throws IllegalStateException if this transaction was already finished.
	 * @throws RuntimeException if this causes a database error.
	 */
	public void rollback(Savepoint savepoint)
	{
		if (isFinished())
			throw new IllegalStateException("This transaction is already finished.");
		try {
			connection.rollback(savepoint);
	} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Calls {@link Connection#setSavepoint()} on the encapsulated connection.
	 * @return a generated {@link Savepoint} of this transaction.
	 * @throws IllegalStateException if this transaction was already finished.
	 * @throws RuntimeException if this causes a database error.
	 */
	public Savepoint setSavepoint()
	{
		if (isFinished())
			throw new IllegalStateException("This transaction is already finished.");
		Savepoint out = null;
		try {
			out = connection.setSavepoint();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return out;
	}
	
	/**
	 * Calls {@link Connection#setSavepoint()} on the encapsulated connection.
	 * @param name the name of the savepoint.
	 * @return a generated {@link Savepoint} of this transaction.
	 * @throws IllegalStateException if this transaction was already finished.
	 * @throws RuntimeException if this causes a database error.
	 */
	public Savepoint setSavepoint(String name)
	{
		if (isFinished())
			throw new IllegalStateException("This transaction is already finished.");
		Savepoint out = null;
		try {
			out = connection.setSavepoint(name);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return out;
	}
	
	/**
	 * Performs a query on this transaction.
	 * @param query the query to execute.
	 * @param parameters list of parameters for parameterized queries.
	 * @return the SQLResult returned.
	 * @throws RuntimeException if the query cannot be resolved or the query causes an error.
	 */
	public SQLResult doQuery(String query, Object ... parameters)
	{
		SQLResult result = null;
		try {
			result = SQLUtil.doQuery(connection, query, parameters); 
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Attempts to grab an available connection from the default servlet connection pool 
	 * and performs a query and creates objects from it, setting relevant fields.
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
	 * @param query the query to execute.
	 * @param parameters list of parameters for parameterized queries.
	 * @return the SQLResult returned.
	 * @throws RuntimeException if the query cannot be resolved or the query causes an error.
	 * @throws ClassCastException if one object type cannot be converted to another.
	 */
	public <T> T[] doQuery(Class<T> type, String query, Object ... parameters)
	{
		T[] result = null;
		try {
			result = SQLUtil.doQuery(type, connection, query, parameters); 
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Performs an update query on this transaction.
	 * @param query the query to execute.
	 * @param parameters list of parameters for parameterized queries.
	 * @return the update result returned (usually number of rows affected).
	 * @throws RuntimeException if the query cannot be resolved or the query causes an error.
	 */
	public SQLResult doUpdateQuery(String query, Object ... parameters)
	{
		SQLResult result = null;
		try {
			result = SQLUtil.doQueryUpdate(connection, query, parameters); 
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	protected void finalize() throws Throwable
	{
		if (!isFinished())
			abort();
		super.finalize();
	}
	
}