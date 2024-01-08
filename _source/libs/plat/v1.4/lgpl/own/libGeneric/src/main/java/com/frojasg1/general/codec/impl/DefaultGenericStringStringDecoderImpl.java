/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DefaultGenericStringStringDecoderImpl extends GenericLeafStringDecoderBase<String>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGenericStringStringDecoderImpl.class);

	public DefaultGenericStringStringDecoderImpl( GenericStringDecoderBuilder builder )
	{
		super( String.class, builder );
	}

	protected String getTransformedSimpleValueString( String valueStr )
	{
		String result = removeQuotes( valueStr );

		return( result );
	}

	protected boolean hasQuotes( String str )
	{
		return( hasQuotes( str, '\'' ) || hasQuotes( str, '"' ) );
	}

	protected boolean hasQuotes( String str, char quote )
	{
		boolean result = ( str != null ) && ( str.length() >= 2 );

		if( result )
			result = ( str.charAt(0) == quote ) && ( str.charAt( str.length() - 1 ) == quote );

		return( result );
	}

	protected String removeQuotes( String str )
	{
		if( ! hasQuotes(str) )
			throw( new RuntimeException( String.format( "Lack of quotation: '%s'", str ) ) );

		return( str.substring( 1, str.length() - 1 ) );
	}
}
