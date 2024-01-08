/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.codec.GenericEncoder;
import com.frojasg1.general.collection.ThreadSafeListWrapperBase;
import com.frojasg1.general.reflection.ReflectionFunctionMultliStartWithFilters;
import com.frojasg1.general.string.StringFunctions;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DefaultGenericStringEncoderImpl implements GenericEncoder<Object, String>
{
	protected static final char SINGLE_QUOTE = '\'';
	protected static final char DOUBLE_QUOTE = '"';

	protected static final String LIST_SEPARATOR = ";";

	protected static class LazyHolder
	{
		protected static final DefaultGenericStringEncoderImpl INSTANCE = new DefaultGenericStringEncoderImpl();
	}

	public static DefaultGenericStringEncoderImpl instance()
	{
		return( LazyHolder.INSTANCE );
	}

	@Override
	public String encode(Object obj)
	{
		StringBuilder result = new StringBuilder();

		return( encodeToString( obj, obj.getClass(), result ).toString() );
	}

	protected StringBuilder encodeToString( Object obj, Class<?> clazz, StringBuilder result )
	{
		result.append( clazz.getName() ).append( "{" );

		if( obj == null )
			result.append( "null" );
		else if( obj instanceof String )
			encodeString( (String) obj, result );
		else if( ThreadSafeListWrapperBase.class.isAssignableFrom(clazz) )
			encodeToString( ( ThreadSafeListWrapperBase ) obj, result, getListSeparator() );
		else
		{
			List<Map<String, Method>> list = getListOfFunctionsByPrefix( clazz );

			Map<String, Method> map = list.get(0);

			encodeToString( obj, map, result );
		}

		result.append( "}" );

		return( result );
	}

	protected String getListSeparator()
	{
		return( LIST_SEPARATOR );
	}

	protected void encodeToString( ThreadSafeListWrapperBase listWrapper, StringBuilder result, String separator )
	{
		String currentSeparator = "";
		for( Object elem: listWrapper.getListCopy() )
		{
			result.append(currentSeparator);
			encodeToString( elem, elem.getClass(), result );

			currentSeparator = separator;
		}
	}

	protected void encodeString( String str, StringBuilder result )
	{
		Character delimiter = getDelimiter(str);
		if( delimiter == null )
			throw( new RuntimeException( String.format( "Error, String('%s') has both possible delimiters inside. Cannot encode", str ) ) );

		result.append( delimiter ).append( str ).append( delimiter );
	}

	protected Character getDelimiter( String str )
	{
		Character result = null;

		if( str.indexOf( DOUBLE_QUOTE ) == -1 )
			result = DOUBLE_QUOTE;
		else if( str.indexOf( SINGLE_QUOTE ) == -1 )
			result = SINGLE_QUOTE;

		return( result );
	}

	protected StringBuilder encodeToString( Object obj, Map<String, Method> map, StringBuilder result )
	{
		if( map.isEmpty() )
			result.append( obj.toString() );
		else
		{
			String attributeName;
			String separator = "";
			Class<?> gotClass;
			Object gotObj;
			Method method;
			for( Map.Entry<String, Method> entry: map.entrySet() )
			{
				method = entry.getValue();
				attributeName = StringFunctions.instance().removeAtStart( entry.getKey(), "get" );
				gotClass = method.getReturnType();
				gotObj = ExecutionFunctions.instance().safeFunctionExecution(
					() -> entry.getValue().invoke( obj ) );

				result.append(separator);
				result.append( attributeName ).append( ":" );
				encodeToString( gotObj, gotClass, result );

				separator = ",";
			}
		}
		return( result );
	}

	protected List<Map<String, Method>> getListOfFunctionsByPrefix( Class<?> clazz )
	{
		return( getListOfFunctionsByPrefix( clazz, "get", "set" ) );
	}

	protected List<Map<String, Method>> getListOfFunctionsByPrefix( Class<?> clazz, String ... prefixes )
	{
		return( ReflectionFunctionMultliStartWithFilters.instance().get( clazz, prefixes ) );
	}
}
