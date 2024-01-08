/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.reflection;

import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.ExecutionFunctions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ReflectionFunctions
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionFunctions.class);

	protected static ReflectionFunctions _instance;

	protected Map< String, Map< String, Field > > _mapOfMapsOfFields = new HashMap<>();
	protected Map< String, Map< String, Method > > _mapOfMapsOfMethods = new HashMap<>();

	protected Map< Class<?>, Map< Class<?>, Boolean > > _mapOfSuperClasses = new HashMap();

	protected Map< Class<?>, List<Class<?>> > _mapOfParentClasses = new ConcurrentHashMap<>();

	public static void changeInstance( ReflectionFunctions inst )
	{
		_instance = inst;
	}

	public static ReflectionFunctions instance()
	{
		if( _instance == null )
			_instance = new ReflectionFunctions();
		return( _instance );
	}

	protected Map< String, Field > inspectFields( Class<?> clazz )
	{
		Map< String, Field > result = new HashMap<>();

		List<Field> fields = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields())
		{
			makeItAccessible( field );
//			field.setAccessible(true);	// not valid from jdk-16 on
			if( field.isAccessible() )
				result.put( field.getName(), field );
		}

		return( result );
	}

	public Object createInstance( String fullClassName, Object ... classesAndParameters )
	{
		Object result = null;
		Class<?> clazz = ClassFunctions.instance().classForName( fullClassName );
		if( clazz != null )
		{
			Class[] signature = new Class[classesAndParameters.length/2];
			for(int ii=0; ii<classesAndParameters.length; ii += 2 )
				signature[ii/2] = (Class<?>) classesAndParameters[ii];

			Object[] params = new Object[classesAndParameters.length/2];
			for(int ii=1; ii<classesAndParameters.length; ii += 2 )
				params[ii/2] = classesAndParameters[ii];

			result = ExecutionFunctions.instance().runtimeExceptionMethodExecution(
				() -> clazz.getConstructor(signature).newInstance(params),
				() -> "Error in createInstance function of" + fullClassName );
		}

		return( result );
	}

	protected boolean isPublic( Field field )
	{
		boolean result = false;
		if( field != null )
		{
			int modifiers = field.getModifiers();
			result = Modifier.isPublic(modifiers);
		}
		return( result );
	}

	protected void makeItAccessible( Field field )
	{
		if( isPublic( field ) )
			ExecutionFunctions.instance().safeMethodExecution( () -> field.setAccessible(true) );
	}

	protected boolean isPublic( Method method )
	{
		boolean result = false;
		if( method != null )
		{
			int modifiers = method.getModifiers();
			result = Modifier.isPublic(modifiers);
		}
		return( result );
	}

	protected void makeItAccessible( Method method )
	{
		if( isPublic( method ) )
			ExecutionFunctions.instance().safeMethodExecution( () -> method.setAccessible(true) );
	}

	protected Map< String, Method > inspectAllMethods( Class<?> clazz )
	{
		Map< String, Method > result = new HashMap<>();

		for( Class<?> currentClass : getListOfClassesOfClassFromTheMostGenericToTheMostParticular(clazz) )
			result.putAll( inspectMethods(currentClass) );

		return( result );
	}

	protected Map< String, Method > inspectMethods( Class<?> clazz )
	{
		Map< String, Method > result = new HashMap<>();

		List<Method> methods = new ArrayList<>();
		for (Method method : clazz.getDeclaredMethods())
		{
//			method.setAccessible(true);	// not valid from jdk-16 on
			makeItAccessible( method );
			if( method.isAccessible() )
				result.put( method.getName(), method );
		}

		return( result );
	}

	public Map< String, Field > getFieldsMap( Class<?> clazz )
	{
		Map< String, Field > result = null;
		if( clazz != null )
		{
			result = _mapOfMapsOfFields.get( clazz.getName() );

			if( result == null )
			{
				result = inspectFields( clazz );

				_mapOfMapsOfFields.put( clazz.getName(), result );
			}
		}

		return( result );
	}

	public Map< String, Method > getMethodsMap( Class<?> clazz )
	{
		Map< String, Method > result = null;
		if( clazz != null )
		{
			result = _mapOfMapsOfMethods.get( clazz.getName() );

			if( result == null )
			{
				result = inspectAllMethods( clazz );

				_mapOfMapsOfMethods.put( clazz.getName(), result );
			}
		}

		return( result );
	}

	public <T> T getStaticAttribute( String attributeName, Class<T> returnClazz,
									Class<?> classOwnerOfTheAttribute )
	{
		T result = null;
		try
		{
			Field field = classOwnerOfTheAttribute.getDeclaredField(attributeName);
//			field.setAccessible(true); // Suppress Java language access checking	// not valid from jdk-16 on
			makeItAccessible( field );

			// Get value
			if(field.isAccessible())
				result = (T) field.get(null);
//			else
//				LOGGER.info( "Cannot get static attribute: {} as it is not accessible", field );
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error getting attribute: {} of class {}", attributeName, classOwnerOfTheAttribute );
		}

		return( result );
	}

	public <T> void setStaticAttribute( String attributeName,
										Class<?> classOwnerOfTheAttribute, T value)
	{
		try
		{
			Field field = classOwnerOfTheAttribute.getDeclaredField(attributeName);
//			field.setAccessible(true); // Suppress Java language access checking	// not valid from jdk-16 on
			makeItAccessible( field );

			// Set value
			if( field.isAccessible() )
				field.set(null, value);
//			else
//				LOGGER.info( "field: {} not accessible, cannot set value: {}", field, value );
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error setting attribute: {} of class {}", attributeName, classOwnerOfTheAttribute );
		}
	}

	public <T> T getAttribute( String attributeName, Class<T> returnClazz,
								Object object )
	{
		if( object == null )
			return( null );

		return( getAttribute( attributeName, returnClazz, object, object.getClass() ) );
	}

	protected Field getField( String attributeName, Class<?> clazz )
	{
		Map< String, Field > map = getFieldsMap( clazz );

		Field result = map.get( attributeName );

		return( result );
	}

	protected Method getMethod( String methodName, Class<?> clazz )
	{
		Map< String, Method > map = getMethodsMap( clazz );

		Method result = map.get( methodName );

		return( result );
	}

	public Object invokeClassMethod( String methodName, Class<?> clazz, Object obj,
									AtomicBoolean hasBeenInvoked, Object ... args )
	{
		hasBeenInvoked.set(false);
		Object result = null;
		Method method = getMethod( methodName, clazz );
		if( method != null )
		{
			result = invokeMethod( methodName, method, obj, args );
			hasBeenInvoked.set(true);
		}

		return( result );
	}

	public Object invokeClassMethod( String methodName, Class<?> clazz, Object obj,
									Object ... args )
	{
		AtomicBoolean hasBeenInvoked = new AtomicBoolean(false);
		return( invokeClassMethod(methodName, clazz, obj, hasBeenInvoked, args ) );
	}

	public Object invokeMethod( String methodName, Object obj, Object ... args )
	{
		Object result = null;

//		if( obj != null )
		{
			List<Class<?>> classList = getListOfClassesOfObjectFromTheMostGenericToTheMostParticular( obj );

			Collections.reverse(classList);

			AtomicBoolean hasBeenInvoked = new AtomicBoolean(false);
			for( Class<?> clazz: classList )
			{
				result = invokeClassMethod( methodName, clazz, obj, hasBeenInvoked, args );
				if( hasBeenInvoked.get() )
					break;
/*
				Method method = getMethod( methodName, clazz );
				if( method != null )
					result = invokeMethod( methodName, method, obj, args );
*/
			}
		}

		return( result );
	}

	public Object invokeStaticMethod( String methodName, Class<?> clazz, Object ... args )
	{
		Method method = getMethod( methodName, clazz );
		
		return( invokeMethod( methodName, method, null, args ) );
	}

	public Object invokeMethod( String methodName, Class<?> clazz, Object obj, Object ... args )
	{
		Method method = getMethod( methodName, clazz );
		
		return( invokeMethod( methodName, method, obj, args ) );
	}

	protected boolean isStatic( Method method )
	{
		boolean result = false;

		if( method != null )
			result = ( method.getModifiers() | Modifier.STATIC ) != 0;

		return( result );
	}

	protected Object invokeMethod( String methodName, Method method, Object obj, Object ... args )
	{
		Object result = null;

		try
		{
			if( ( method != null ) && !( !isStatic( method ) && ( obj == null ) ) )
				result = method.invoke(obj, args);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public <T> T getAttribute( String attributeName, Class<T> returnClazz,
								Object object, Class<?> objectClass )
	{
		T result = null;

		if( //( object != null ) &&
			( returnClazz != null ) &&
			( attributeName != null ) &&
			( objectClass != null ) )
		{
			Field field = getField( attributeName, objectClass );

			if( field != null )
			{
				Object resObj = null;
				try
				{
					resObj = field.get(object);

					if( returnClazz.isInstance( resObj ) )
					{
						result = (T) resObj;
					}
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
		}

		return( result );
	}

	public <T> boolean setAttribute( String attributeName,
										Object object, Class<?> objectClass,
										T value )
	{
		boolean result = false;

		Field field = getField( attributeName, objectClass );
		if( field != null )
		{
//			if( ( value == null ) || ( field.getType().isInstance( value ) ) )
			{
				try
				{
					field.set( object, value );
					result = true;
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
		}

		return( result );
	}

	public boolean isSuperClass( Class<?> clazz, Class<?> probablyParentClass )
	{
		Boolean result = getMapIsSuperClass( clazz, probablyParentClass );

		if( result == null )
		{
			result = calculateIsSuperClass( clazz, probablyParentClass );

			putInMapOfSuperClasses( clazz, probablyParentClass, result );
		}

		return( result );
	}

	protected boolean calculateIsSuperClass( Class<?> clazz, Class<?> probablyParentClass )
	{
		boolean result = false;

		if( ( clazz != null ) && ( probablyParentClass != null ) )
		{
			String parentClassName = probablyParentClass.getName();
			do
			{
				result = clazz.getName().equals( parentClassName );

				clazz = clazz.getSuperclass();
			}
			while( ( clazz != null ) && !result );
		}

		return( result );
	}

	public Boolean getMapIsSuperClass( Class<?> clazz, Class<?> probablyParentClass )
	{
		Boolean result = null;

		Map< Class<?>, Boolean > tmpMap = _mapOfSuperClasses.get( clazz );

		if( tmpMap != null )
			result = tmpMap.get( probablyParentClass );

		return( result );
	}

	public void putInMapOfSuperClasses( Class<?> clazz, Class<?> probablyParentClass, boolean value )
	{
		Map< Class<?>, Boolean > tmpMap = _mapOfSuperClasses.get( clazz );

		if( tmpMap == null )
		{
			tmpMap = new HashMap<>();
			_mapOfSuperClasses.put( clazz, tmpMap );
		}

		tmpMap.put( probablyParentClass, value );
	}

	public List<Class<?>> calculateListOfClassesAndInterfacesOfClassFromTheMostGenericToTheMostParticular( Class<?> clazz )
	{
		List<Class<?>> result = new ArrayList<>();

		int delta = 1;
		for( int depth = 0; delta > 0; depth++ )
			delta = insertParentClasses( clazz, depth, result );

		return( result );
	}

	protected int insertParentClasses( Class<?> clazz, int depth, List<Class<?>> resultList )
	{
		int result = 0;
		
		if( depth < 0 )
		{
			// intentionally left blank
		}
		else if( depth == 0 )
		{
			if( !resultList.contains(clazz) )
			{
				resultList.add( clazz );
				result++;
			}
		}
		else if( ! Object.class.equals(clazz) && ( clazz != null ) )
		{
			depth--;
			result += insertParentClasses( clazz.getSuperclass(), depth, resultList );
			for( Class<?> ifzClass: clazz.getInterfaces() )
				result += insertParentClasses( ifzClass, depth, resultList );
		}

		return( result );
	}

	public List<Class<?>> getListOfClassesAndInterfacesOfClassFromTheMostGenericToTheMostParticular( Class<?> clazz )
	{
		return( _mapOfParentClasses.computeIfAbsent(clazz, this::calculateListOfClassesAndInterfacesOfClassFromTheMostGenericToTheMostParticular ) );
	}

	public List<Class<?>> getListOfClassesOfObjectFromTheMostGenericToTheMostParticular( Object object )
	{
		List<Class<?>> result = new ArrayList<>();
		
		if( object != null )
			result = getListOfClassesOfClassFromTheMostGenericToTheMostParticular( object.getClass() );

		return( result );
	}

	public List<Class<?>> getListOfClassesOfClassFromTheMostGenericToTheMostParticular( Class<?> clazz )
	{
		List<Class<?>> result = new ArrayList<>();
		
		Class<?> currentClass = clazz;
		do
		{
			result.add( currentClass );
			currentClass = currentClass.getSuperclass();
		}
		while( currentClass != null );

		Collections.reverse(result);

		return( result );
	}

	// https://stackoverflow.com/questions/203475/how-do-i-identify-immutable-objects-in-java
	public boolean isImmutable(Object obj)
	{
		Class<?> objClass = obj.getClass();

		if( String.class.equals( objClass ) )
			return( true );
		else if( Integer.class.equals( objClass ) )
			return( true );
		else if( Long.class.equals( objClass ) )
			return( true );
		else if( Double.class.equals( objClass ) )
			return( true );
		else if( Float.class.equals( objClass ) )
			return( true );
		else if( Boolean.class.equals( objClass ) )
			return( true );
		else if( objClass.isEnum() )
			return( true );

		// Class must be final
		if (!Modifier.isFinal(objClass.getModifiers())) {
			return false;
		}

		// Check all fields defined in the class for type and if they are final
		Field[] objFields = objClass.getDeclaredFields();
		for (int i = 0; i < objFields.length; i++) {
			if (!Modifier.isFinal(objFields[i].getModifiers())
				|| !isValidFieldType(objFields[i].getType())) {
					return false;
			}
		}

		// Lets hope we didn't forget something
		return true;
	}

	public boolean hasCopyConstructor( Object obj )
	{
		boolean result = false;
		if( obj != null )
			result = ( ExecutionFunctions.instance().safeSilentFunctionExecution( () -> obj.getClass().getConstructor( obj.getClass() ) ) != null );

		return( result );
	}

	public boolean isValidFieldType(Class<?> type) {
		// Check for all allowed property types...
		return type.isPrimitive() || String.class.equals(type);
	}
}
