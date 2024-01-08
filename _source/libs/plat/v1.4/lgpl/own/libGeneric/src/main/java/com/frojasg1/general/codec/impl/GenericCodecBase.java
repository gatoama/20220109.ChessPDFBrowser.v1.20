/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl;

import com.frojasg1.general.codec.GenericCodec;
import com.frojasg1.general.codec.GenericDecoder;
import com.frojasg1.general.codec.GenericEncoder;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericCodecBase<DD, EE> implements GenericCodec<DD, EE> {
	
	protected GenericEncoder<DD, EE> _encoder;
	protected GenericDecoder<DD, EE> _decoder;

	public GenericCodecBase( GenericEncoder<DD, EE> encoder, GenericDecoder<DD, EE> decoder )
	{
		_encoder = encoder;
		_decoder = decoder;
	}

	@Override
	public EE encode(DD record) {
		return( _encoder.encode(record) );
	}

	@Override
	public DD decode(EE serializedValue) {
		return( _decoder.decode(serializedValue) );
	}

	@Override
	public void decode(EE serializedValue, DD result) {
		_decoder.decode( serializedValue, result );
	}
}
