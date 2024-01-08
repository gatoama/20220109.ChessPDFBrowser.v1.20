/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.collection.impl;

import com.frojasg1.general.codec.SelfStringCodec;
import com.frojasg1.general.codec.impl.DefaultGenericStringEncoderImpl;
import com.frojasg1.general.codec.impl.GenericStringDecoderBuilder;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ThreadSafeArrayListWrapperSelfStringCodec<RR>
	extends ThreadSafeGenListWrapper<RR>
	implements SelfStringCodec
{
	protected GenericStringDecoderBuilder _builder;
	
	public ThreadSafeArrayListWrapperSelfStringCodec()
	{
		this( GenericStringDecoderBuilder.instance() );
	}

	public ThreadSafeArrayListWrapperSelfStringCodec( GenericStringDecoderBuilder builder )
	{
		_builder = builder;
	}

	@Override
	public String encode() {
		return( DefaultGenericStringEncoderImpl.instance().encode(this) );
	}

	@Override
	public void decode(String encodedValue) {
		_builder.get( getClass() ).decode(encodedValue, this);
	}

	protected GenericStringDecoderBuilder getGenericStringDecoderBuilder()
	{
		return( _builder );
	}
}
