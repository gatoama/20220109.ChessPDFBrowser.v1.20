/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <EE>	- Encoded
 * @param <DD>	- Decoded
 */
public interface GenericDecoder<DD, EE>
{
	public DD decode( EE serializedValue );
	public void decode( EE serializedValue, DD result );
}
