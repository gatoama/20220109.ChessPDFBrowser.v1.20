/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl;

import com.frojasg1.general.codec.GenericStringDecoderBase;
import com.frojasg1.general.string.translator.GenericStringTranslator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class GenericLeafStringDecoderBase<RR> extends GenericStringDecoderBase<RR>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericLeafStringDecoderBase.class);

	protected Class<RR> _class;

	public GenericLeafStringDecoderBase( Class<RR> clazz, GenericStringDecoderBuilder builder )
	{
		super( builder );

		_class = clazz;
	}

	@Override
	public RR decode( String string )
	{
		return( super.decode( string ) );
	}

	@Override
	public RR decodeInternal( String string, String className )
	{
		String valueStr = removeClassNameAndBraces( string, className );

		RR result = createSimpleObject( valueStr );

		return( result );
	}

	@Override
	protected boolean isClassExpectedInternal( String className )
	{
		return( Objects.equals( className, _class.getName() ) );
	}

	@Override
	protected String getExpectedClassName()
	{
		return( _class.getName() );
	}

	@Override
	public void decodeInternalWithResult( String string, String className, RR result )
	{
		throw( new RuntimeException( "This functions should never be called, as we have to create the leaf object, instead of modifying it" ) );
	}

	protected abstract String getTransformedSimpleValueString( String valueStr );

	protected RR createSimpleObject( String valueStr )
	{
		return( createSimpleObjectInternal( getTransformedSimpleValueString( valueStr ) ) );
	}

	protected RR createSimpleObjectInternal( String valueStr )
	{
		return( GenericStringTranslator.instance().fromString(valueStr, _class) );
	}
}
