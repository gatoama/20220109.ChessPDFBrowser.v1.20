/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec;

import com.frojasg1.general.codec.impl.*;
import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.string.StringFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class GenericStringDecoderBase<RR> implements GenericStringDecoder<RR>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericStringDecoderBase.class);

	protected GenericStringDecoderBuilder _builder;

	public GenericStringDecoderBase( GenericStringDecoderBuilder builder )
	{
		_builder = builder;
	}

	@Override
	public RR decode( String string )
	{
		RR result;
		try
		{
			String className = getFirstClassNameAndCheck( string );

			result = decodeInternal(string, className);
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error decoding object from string: {}", string, ex );
			throw( new RuntimeException( "Error decoding object", ex ) );
		}

		return( result );
	}

	protected String removeClassNameAndBraces( String string, String className )
	{
		String valueStr = string.substring( className.length() ).trim();

		return( removeBraces(valueStr) );
	}

	protected String removeBraces( String valueStr )
	{
		return( removeStartAndEnd(valueStr, "{", "}" ) );
	}

	protected String removeStartAndEnd( String valueStr, String start, String end )
	{
		if( ! valueStr.startsWith(start) )
			throw( new RuntimeException( String.format( "Expected start: '%s' not found in: '%s'", start, valueStr ) ) );

		if( ! valueStr.endsWith(end) )
			throw( new RuntimeException( String.format( "Expected end: '%s' not found in: '%s'", end, valueStr ) ) );

		return( valueStr.substring( start.length(), valueStr.length() - end.length() ) );
	}

	public abstract RR decodeInternal( String string, String className );


	protected void checkClassNameAndCheck( String className )
	{
		if( ! isClassExpected( className ) )
			throw( new RuntimeException( String.format( "Class name ('%s') not expected. The expected one: '%s'",
												className, getExpectedClassName() ) ) );
	}

	protected Class<?> getPrimitiveWrapper( String className )
	{
		return( ClassFunctions.instance().getPrimitiveWrapper(className) );
	}

	protected String translatePrimitiveClassName( String className )
	{
		String result = className;
		Class<?> primitiveWrapperClazz = getPrimitiveWrapper( className );

		if( primitiveWrapperClazz != null )
			result = primitiveWrapperClazz.getName();

		return( result );
	}

	protected boolean isClassExpected( String className )
	{
		return( isClassExpectedInternal( translatePrimitiveClassName( className ) ) );
	}

	protected abstract boolean isClassExpectedInternal( String className );
	protected abstract String getExpectedClassName();

	protected String getFirstClassNameAndCheck( String string )
	{
		String className = getFirstClassName( string );
		checkClassNameAndCheck( className );

		return( className );
	}

	protected boolean isEmpty( String str )
	{
		return( StringFunctions.instance().isEmpty(str) );
	}

	@Override
	public void decode( String string, RR result )
	{
		if( isEmpty(string) )
			return;

		try
		{
			String className = getFirstClassNameAndCheck( string );
			decodeInternalWithResult( string, className, result );
		}
		catch( Exception ex )
		{
			LOGGER.error( "Error decoding object from string: {}", string, ex );
			throw( new RuntimeException( "Error decoding object", ex ) );
		}
	}

	public abstract void decodeInternalWithResult( String string, String className, RR result );

	protected GenericStringDecoderBuilder getGenericStringDecoderBuilder()
	{
		return( _builder );
	}

	protected GenericStringDecoder getGenericStringDecoder( String className )
	{
		return( getGenericStringDecoderBuilder().get( className ) );
	}

	public <CC> CC runtimeExceptionFunctionExecution( ExecutionFunctions.UnsafeFunction<CC> run )
	{
		return( ExecutionFunctions.instance().runtimeExceptionFunctionExecution( run ) );
	}

	public void runtimeExceptionMethodExecution( ExecutionFunctions.UnsafeMethod run )
	{
		ExecutionFunctions.instance().runtimeExceptionMethodExecution( run );
	}

	protected String getFirstClassName( String str )
	{
		return( str.substring(0, str.indexOf( "{" ) ) );
	}

	protected Class<?> classForName( String className )
	{
		return( ClassFunctions.instance().classForName(className) );
	}
}
