/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.collection.codec;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.analysers.GenListSplitterBase;
import com.frojasg1.general.analysers.impl.DefaultListQuotedDataSplitter;
import com.frojasg1.general.codec.GenericStringDecoder;
import com.frojasg1.general.codec.GenericStringDecoderBase;
import com.frojasg1.general.codec.impl.GenericStringDecoderBuilder;
import com.frojasg1.general.collection.impl.ThreadSafeGenListWrapper;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ThreadSafeArrayListWrapperStringDecoder
	extends GenericStringDecoderBase<ThreadSafeGenListWrapper>
{
	protected String _separator;

	public ThreadSafeArrayListWrapperStringDecoder( GenericStringDecoderBuilder decoderBuilder )
	{
		this( decoderBuilder, ";" );
	}

	public ThreadSafeArrayListWrapperStringDecoder( GenericStringDecoderBuilder decoderBuilder, String separator )
	{
		super( decoderBuilder );
		_separator = separator;
	}

	public String getSeparator() {
		return _separator;
	}

	@Override
	public ThreadSafeGenListWrapper decodeInternal( String string, String className )
	{
		ThreadSafeGenListWrapper result = createEmptyObject((Class<? extends ThreadSafeGenListWrapper>) classForName( className ) );

		decode( string, result );

		return( result );
	}

	@Override
	public void decodeInternalWithResult( String string, String className, ThreadSafeGenListWrapper result )
	{
		String valueStr = removeClassNameAndBraces(string, className);

		result.clear();

		GenListSplitterBase splitter = splitter( valueStr, getSeparator() );
		String elemStr;
		do
		{
			elemStr = splitter.next();
			if( elemStr != null )
				result.add( decodeElem( elemStr ) );
		}
		while( elemStr != null );
	}

	protected Object decodeElem( String elemStr )
	{
		String elemClassName = getFirstClassName(elemStr);

		GenericStringDecoder decoder = getGenericStringDecoder(elemClassName);

		return( decoder.decode( elemStr ) );
	}

	protected GenListSplitterBase splitter( String string, String separator )
	{
		return( new DefaultListQuotedDataSplitter( string, separator ) );
	}


	protected ThreadSafeGenListWrapper createEmptyObject( Class<? extends ThreadSafeGenListWrapper> clazz)
	{
		return( ExecutionFunctions.instance().runtimeExceptionFunctionExecution( () -> clazz.newInstance() ) );
	}

	@Override
	protected boolean isClassExpectedInternal( String className )
	{
		return( ThreadSafeGenListWrapper.class.isAssignableFrom( classForName(className) ) );
	}

	@Override
	protected String getExpectedClassName()
	{
		return( "derived from: " + ThreadSafeGenListWrapper.class.getName() );
	}
}
