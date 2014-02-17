/*******************************************************************************
 * Copyright (c) 2013-2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.sql;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.blackrook.commons.Common;
import com.blackrook.commons.Reflect;
import com.blackrook.commons.hash.CaseInsensitiveHashMap;

/**
 * SQLRow object. 
 * Represents one row in a query result, mapped using column names.
 * Contains methods for auto-casting or converting the row data.
 */
public class SQLRow extends CaseInsensitiveHashMap<Object>
{
	/**
	 * Hidden constructor for a SQL row.
	 * @param rs the open {@link ResultSet}, set to the row to create a SQLRow from.
	 * @param columnNames the names given to the columns in the {@link ResultSet},
	 * gathered ahead of time.
	 * @throws SQLException if a parse exception occurred.
	 */
	SQLRow(ResultSet rs, String[] columnNames) throws SQLException
	{
		super(columnNames.length);
		for (int i = 0; i < columnNames.length; i++)
			put(columnNames[i], rs.getObject(i+1));
	}
	
	/**
	 * Returns if this column's value is null.
	 */
	public boolean getNull(String columnName)
	{
		return get(columnName) == null;
	}
	
	/**
	 * Returns the boolean value of this column.
	 * Can convert from Booleans, Numbers, and Strings.
	 * @see Common#parseBoolean(String)
	 */
	public boolean getBoolean(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return false;
		else if (obj instanceof Boolean)
			return (Boolean)obj;
		else if (obj instanceof Number)
			return ((Number)obj).doubleValue() != 0.0f;
		else if (obj instanceof String)
			return Common.parseBoolean((String)obj);
		return false;
	}
	
	/**
	 * Returns the byte value of this column.
	 * Can convert from Booleans, Numbers, and Strings.
	 * Booleans convert to 1 if true, 0 if false.
	 * @see Common#parseByte(String)
	 */
	public byte getByte(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return (byte)0;
		else if (obj instanceof Boolean)
			return ((Boolean)obj) ? (byte)1 : (byte)0;
		else if (obj instanceof Number)
			return ((Number)obj).byteValue();
		else if (obj instanceof String)
			return Common.parseByte((String)obj);
		return (byte)0;
	}
	
	/**
	 * Returns the byte array value of this column, if this
	 * can be represented as such (usually {@link Blob}s).
	 * Can convert from Blobs.
	 */
	public byte[] getByteArray(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return null;
		else if (obj instanceof Blob)
		{
			Blob blob = (Blob)obj;
			InputStream in = null;
			ByteArrayOutputStream bos = null;
			try {
				in = blob.getBinaryStream();
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[65536];
				int buf = 0;
				while ((buf = in.read(buffer)) > 0)
					bos.write(buffer, 0, buf);
			} catch (SQLException e) {
				return null;
			} catch (IOException e) {
				return null;
				} finally {
				Common.close(in);
			}
			
			return bos.toByteArray();
		}
		return null;
	}
	
	/**
	 * Returns the short value of this column.
	 * Can convert from Booleans, Numbers, and Strings.
	 * Booleans convert to 1 if true, 0 if false.
	 * @see Common#parseShort(String)
	 */
	public short getShort(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return (short)0;
		else if (obj instanceof Boolean)
			return ((Boolean)obj) ? (short)1 : (short)0;
		else if (obj instanceof Number)
			return ((Number)obj).shortValue();
		else if (obj instanceof String)
			return Common.parseShort((String)obj);
		return (short)0;
	}
	
	/**
	 * Returns the integer value of this column.
	 * Can convert from Booleans, Numbers, and Strings.
	 * Booleans convert to 1 if true, 0 if false.
	 * @see Common#parseInt(String)
	 */
	public int getInt(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return 0;
		else if (obj instanceof Boolean)
			return ((Boolean)obj) ? 1 : 0;
		else if (obj instanceof Number)
			return ((Number)obj).intValue();
		else if (obj instanceof String)
			return Common.parseInt((String)obj);
		return 0;
	}
	
	/**
	 * Returns the float value of this column.
	 * Can convert from Booleans, Numbers, and Strings.
	 * Booleans convert to 1 if true, 0 if false.
	 * @see Common#parseFloat(String)
	 */
	public float getFloat(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return 0f;
		else if (obj instanceof Boolean)
			return ((Boolean)obj) ? 1f : 0f;
		else if (obj instanceof Number)
			return ((Number)obj).floatValue();
		else if (obj instanceof String)
			return Common.parseFloat((String)obj);
		return 0f;
	}
	
	/**
	 * Returns the long value of this column.
	 * Can convert from Booleans, Numbers, Strings, and Dates/Timestamps.
	 * Booleans convert to 1 if true, 0 if false.
	 * Dates and Timestamps convert to milliseconds since the Epoch.
	 * @see Common#parseLong(String)
	 */
	public long getLong(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return 0L;
		else if (obj instanceof Boolean)
			return ((Boolean)obj) ? 1L : 0L;
		else if (obj instanceof Number)
			return ((Number)obj).longValue();
		else if (obj instanceof String)
			return Common.parseLong((String)obj);
		else if (obj instanceof Date)
			return ((Date)obj).getTime();
		return 0L;
	}
	
	/**
	 * Returns the double value of this column.
	 * Can convert from Booleans, Numbers, and Strings.
	 * Booleans convert to 1 if true, 0 if false.
	 * @see Common#parseDouble(String)
	 */
	public double getDouble(String columnName)
	{
		Object obj = get(columnName);
		if (obj == null)
			return 0.0;
		else if (obj instanceof Boolean)
			return ((Boolean)obj) ? 1.0 : 0.0;
		else if (obj instanceof Number)
			return ((Number)obj).doubleValue();
		else if (obj instanceof String)
			return Common.parseDouble((String)obj);
		return 0.0;
	}
	
	/**
	 * Returns the string value of this column.
	 * Can convert from Booleans, Numbers, byte and char arrays,
	 * Blobs, and Clobs.
	 * Booleans convert to 1 if true, 0 if false.
	 * Byte arrays and Blobs are converted using the native charset encoding.
	 * Char arrays and Clobs are read entirely and converted to Strings.
	 * @see String#valueOf(Object)
	 */
	public String getString(String columnName)
	{
		Object obj = get(columnName);
		
		if (Reflect.isArray(obj))
		{
			if (Reflect.getArrayType(obj) == Byte.TYPE)
				return new String((byte[])obj);
			else if (Reflect.getArrayType(obj) == Character.TYPE)
				return new String((char[])obj);
			else
				return null;
		}
		else if (obj instanceof Clob)
		{
			Clob clob = (Clob)obj;
			Reader reader = null;
			StringWriter sw = null;
			try {
				reader = clob.getCharacterStream();
				sw = new StringWriter();
				char[] charBuffer = new char[1024];
				int cbuf = 0;
				while ((cbuf = reader.read(charBuffer)) > 0)
					sw.write(charBuffer, 0, cbuf);
			} catch (SQLException e) {
				return null;
			} catch (IOException e) {
				return null;
				} finally {
				Common.close(reader);
			}
			
			return sw.toString();
		}
		else if (obj instanceof Blob)
		{
			Blob blob = (Blob)obj;
			InputStream in = null;
			ByteArrayOutputStream bos = null;
			try {
				in = blob.getBinaryStream();
				bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[65536];
				int buf = 0;
				while ((buf = in.read(buffer)) > 0)
					bos.write(buffer, 0, buf);
			} catch (SQLException e) {
				return null;
			} catch (IOException e) {
				return null;
				} finally {
				Common.close(in);
			}
			
			return new String(bos.toByteArray());
		}
		else
			return obj != null ? String.valueOf(obj) : null;
	}
	
	/**
	 * Returns the Timestamp value of the object, or
	 * null if not a Timestamp or Date.
	 */
	public Timestamp getTimestamp(String columnName)
	{
		Object obj = get(columnName);
		if (obj instanceof Timestamp)
			return ((Timestamp)obj);
		else if (obj instanceof Date)
			return new Timestamp(((Date)obj).getTime());
		return null;
	}
	
	/**
	 * Returns the Date value of the object, or null if not a Date.
	 */
	public Date getDate(String columnName)
	{
		Object obj = get(columnName);
		if (obj instanceof Date)
			return (Date)obj;
		return null;
	}
	
}

