/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl;

import com.frojasg1.general.codec.GenericStringDecoder;
import com.frojasg1.general.codec.SelfStringCodec;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class SelfStringCodecBase implements SelfStringCodec
{
	protected GenericStringDecoderBuilder _builder;
	
	public SelfStringCodecBase()
	{
		this( GenericStringDecoderBuilder.instance() );
	}

	public SelfStringCodecBase( GenericStringDecoderBuilder builder )
	{
		_builder = builder;
	}

	@Override
	public String encode()
	{
		return( DefaultGenericStringEncoderImpl.instance().encode(this) );
	}

	@Override
	public void decode(String encodedValue)
	{
		getGenericStringDecoder().decode(encodedValue, this);
	}

	protected GenericStringDecoder getGenericStringDecoder( )
	{
		return( getGenericStringDecoderBuilder().get( getClass() ) );
	}

	protected GenericStringDecoderBuilder getGenericStringDecoderBuilder()
	{
		return( _builder );
	}
}
