/*******************************************************************************
 * Copyright (c) 2013-2019 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.blackrook.commons.AbstractMap;
import com.blackrook.commons.ObjectPair;
import com.blackrook.commons.Reflect;
import com.blackrook.commons.TypeConverter;
import com.blackrook.commons.TypeProfile;
import com.blackrook.commons.TypeProfile.MethodSignature;

/**
 * A convenience utility class that holds several helpful Reflection
 * functions and methods, mostly for passive error-handling.
 * Compare with Commons {@link Reflect}.
 * @author Matthew Tropiano
 * @since 2.4.0
 */
public final class DBReflect
{
	/** Default converter for {@link #createForType(Object, Class)}. */
	private static final TypeConverter DEFAULT_CONVERTER = new DBTypeConverter();

	
	// applies a value, converting, to an object.
	private static <T> void applyMemberToObject(String fieldName, Object value, T targetObject)
	{
		@SuppressWarnings("unchecked")
		TypeProfile<T> profile = TypeProfile.getTypeProfile((Class<T>)targetObject.getClass());
	
		Field field = null; 
		MethodSignature setter = null;
		if ((field = profile.getPublicFields().get(fieldName)) != null)
		{
			Class<?> type = field.getType();
			Reflect.setField(targetObject, fieldName, createForType(fieldName, value, type));
		}
		else if ((setter = profile.getSetterMethods().get(fieldName)) != null)
		{
			Class<?> type = setter.getType();
			Method method = setter.getMethod();
			Reflect.invokeBlind(method, targetObject, createForType(fieldName, value, type));
		}			
	}

	/**
	 * Takes the contents of an AbstractMap and applies it to a newly-created POJO 
	 * (Plain Old Java Object) via its public fields and/or getters and setters.
	 * The values in the map applied to the object may be converted.
	 * @param <T> the return object type.
	 * @param map the source map.
	 * @param clazz the class to instantiate (must have a default public constructor).
	 * @return a new object.
	 */
	public static <T> T mapToNewObject(AbstractMap<String, ?> map, Class<T> clazz)
	{
		T out = Reflect.create(clazz);
		mapToObject(map, out);
		return out;
	}

	/**
	 * Takes the contents of a Map and applies it to a newly-created POJO 
	 * (Plain Old Java Object) via its public fields and/or getters and setters.
	 * The values in the map applied to the object may be converted.
	 * @param <T> the return object type.
	 * @param map the source map.
	 * @param clazz the class to instantiate (must have a default public constructor).
	 * @return a new object.
	 */
	public static <T> T mapToNewObject(Map<String, ?> map, Class<T> clazz)
	{
		T out = Reflect.create(clazz);
		mapToObject(map, out);
		return out;
	}

	/**
	 * Takes the contents of an AbstractMap and applies it to a POJO 
	 * (Plain Old Java Object) via its public fields and/or getters and setters.
	 * The values in the map applied to the object may be converted.
	 * @param <T> the return object type.
	 * @param map the source map.
	 * @param object the object to apply the field map to.
	 */
	public static <T> void mapToObject(AbstractMap<String, ?> map, T object)
	{
		for (ObjectPair<String, ?> pair : map)
			applyMemberToObject(pair.getKey(), pair.getValue(), object);
	}

	/**
	 * Takes the contents of a Map and applies it to a POJO 
	 * (Plain Old Java Object) via its public fields and/or getters and setters.
	 * The values in the map applied to the object may be converted.
	 * @param <T> the return object type.
	 * @param map the source map.
	 * @param object the object to apply the field map to.
	 */
	public static <T> void mapToObject(Map<String, ?> map, T object)
	{
		for (Map.Entry<String, ?> pair : map.entrySet())
			applyMemberToObject(pair.getKey(), pair.getValue(), object);
	}

	/**
	 * Creates a new instance of an object for placement in a POJO or elsewhere.
	 * @param <T> the return object type.
	 * @param object the object to convert to another object
	 * @param targetType the target class type to convert to, if the types differ.
	 * @return a suitable object of type <code>targetType</code>. 
	 * @since 2.16.0
	 * @throws ClassCastException if the incoming type cannot be converted.
	 */
	public static <T> T createForType(Object object, Class<T> targetType)
	{
		return DEFAULT_CONVERTER.createForType("source", object, targetType);
	}

	/**
	 * Creates a new instance of an object for placement in a POJO or elsewhere.
	 * @param <T> the return object type.
	 * @param memberName the name of the member that is being converted (for reporting). 
	 * @param object the object to convert to another object
	 * @param targetType the target class type to convert to, if the types differ.
	 * @return a suitable object of type <code>targetType</code>. 
	 * @since 2.16.0
	 * @throws ClassCastException if the incoming type cannot be converted.
	 */
	public static <T> T createForType(String memberName, Object object, Class<T> targetType)
	{
		return DEFAULT_CONVERTER.createForType(memberName, object, targetType);
	}

}
