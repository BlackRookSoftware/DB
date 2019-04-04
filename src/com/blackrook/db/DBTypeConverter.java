/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import com.blackrook.commons.AbstractMap;
import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.Reflect;
import com.blackrook.commons.TypeConverter;
import com.blackrook.commons.util.IOUtils;

/**
 * Type converter class for converting types, built from Commons {@link TypeConverter}, 
 * but also handles Timestamps and Clobs/Blobs.
 * @author Matthew Tropiano
 * @since 2.4.0
 */
public class DBTypeConverter extends TypeConverter
{

	/**
	 * Reflect.creates a new instance of an object for placement in a POJO or elsewhere.
	 * @param <T> the return object type.
	 * @param object the object to convert to another object
	 * @param targetType the target class type to convert to, if the types differ.
	 * @return a suitable object of type <code>targetType</code>. 
	 * @throws ClassCastException if the incoming type cannot be converted.
	 */
	public <T> T createForType(Object object, Class<T> targetType)
	{
		return createForType("source", object, targetType);
	}

	/**
	 * Reflect.creates a new instance of an object for placement in a POJO or elsewhere.
	 * @param <T> the return object type.
	 * @param memberName the name of the member that is being converted (for reporting). 
	 * @param object the object to convert to another object
	 * @param targetType the target class type to convert to, if the types differ.
	 * @return a suitable object of type <code>targetType</code>. 
	 * @throws ClassCastException if the incoming type cannot be converted.
	 */
	@SuppressWarnings("unchecked")
	public <T> T createForType(String memberName, Object object, Class<T> targetType)
	{
		if (object == null)
		{
			if (targetType == Boolean.TYPE)
				return (T)Boolean.valueOf(false);
			else if (targetType == Byte.TYPE)
				return (T)Byte.valueOf((byte)0x00);
			else if (targetType == Short.TYPE)
				return (T)Short.valueOf((short)0);
			else if (targetType == Integer.TYPE)
				return (T)Integer.valueOf(0);
			else if (targetType == Float.TYPE)
				return (T)Float.valueOf(0f);
			else if (targetType == Long.TYPE)
				return (T)Long.valueOf(0L);
			else if (targetType == Double.TYPE)
				return (T)Double.valueOf(0.0);
			else if (targetType == Character.TYPE)
				return (T)Character.valueOf('\0');
			return null;
		}
		
		if (targetType.isAssignableFrom(object.getClass()))
			return targetType.cast(object);
		else if (Object.class == targetType)
			return targetType.cast(object);
		else if (Reflect.isArray(object.getClass()))
			return convertArray(memberName, object, targetType);
		else if (object instanceof AbstractMap)
		{
			T out = Reflect.create(targetType);
			for (ObjectPair<?, ?> pair : ((AbstractMap<?,?>)object))
				applyMemberToObject(String.valueOf(pair.getKey()), pair.getValue(), out);
			return out;
		}
		else if (object instanceof Map)
		{
			T out = Reflect.create(targetType);
			for (Map.Entry<?, ?> pair : ((Map<?,?>)object).entrySet())
				applyMemberToObject(String.valueOf(pair.getKey()), pair.getValue(), out);
			return out;
		}
		else if (object instanceof Iterable)
			return convertIterable(memberName, (Iterable<?>)object, targetType);
		else if (object instanceof Enum<?>)
			return convertEnum(memberName, (Enum<?>)object, targetType);
		else if (object instanceof Boolean)
			return convertBoolean(memberName, (Boolean)object, targetType);
		else if (object instanceof Number)
			return convertNumber(memberName, (Number)object, targetType);
		else if (object instanceof Character)
			return convertCharacter(memberName, (Character)object, targetType);
		else if (object instanceof Date)
			return convertDate(memberName, (Date)object, targetType);
		else if (object instanceof String)
			return convertString(memberName, (String)object, targetType);
		else if (object instanceof Timestamp)
			return convertTimestamp(memberName, (Timestamp)object, targetType);
		else if (object instanceof Blob)
			return convertBlob(memberName, (Blob)object, targetType);
		else if (object instanceof Clob)
			return convertClob(memberName, (Clob)object, targetType);
		
		throw new ClassCastException("Object could not be converted: "+memberName+" is "+object.getClass()+", target is "+targetType);
	}

	/**
	 * Converts a timestamp value to a target type.
	 */
	@SuppressWarnings("unchecked")
	protected final <T> T convertTimestamp(String memberName, Timestamp t, Class<T> targetType)
	{
		if (targetType == Long.TYPE)
			return t != null ? (T)Long.valueOf(t.getTime()) : (T)Long.valueOf(0);
		else if (targetType == Long.class)
			return targetType.cast(t.getTime());
		else if (targetType == String.class)
			return targetType.cast(String.valueOf(t));
		else if (targetType == Date.class)
			return targetType.cast(new Date(t.getTime()));
		else if (targetType == Timestamp.class)
			return targetType.cast(t);
		else if (Reflect.isArray(targetType))
		{
			Class<?> atype = Reflect.getArrayType(targetType);
			Object out = Array.newInstance(atype, 1);
			Array.set(out, 0, createForType(t, atype));
			return targetType.cast(out);
		}
	
		throw new ClassCastException("Object could not be converted: "+memberName+" is Timestamp, target is "+targetType);
	}

	/**
	 * Converts an SQL Clob value to a target type.
	 */
	protected final <T> T convertClob(String memberName, Clob clob, Class<T> targetType)
	{
		Reader reader = null;
		StringWriter sw = null;
		try {
			reader = clob.getCharacterStream();
			sw = new StringWriter();
			char[] charBuffer = new char[1024 * 8];
			int cbuf = 0;
			while ((cbuf = reader.read(charBuffer)) > 0)
				sw.write(charBuffer, 0, cbuf);
		} catch (SQLException e) {
			return null;
		} catch (IOException e) {
			return null;
		} finally {
			IOUtils.close(reader);
		}
	
		char[] out = new char[sw.getBuffer().length()];
		sw.getBuffer().getChars(0, out.length, out, 0);
		return convertArray(memberName, out, targetType);
	}

	/**
	 * Converts an SQL Blob value to a target type.
	 */
	protected final <T> T convertBlob(String memberName, Blob blob, Class<T> targetType)
	{
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
			IOUtils.close(in);
		}
		
		return convertArray(memberName, bos.toByteArray(), targetType);
	}
	
}
