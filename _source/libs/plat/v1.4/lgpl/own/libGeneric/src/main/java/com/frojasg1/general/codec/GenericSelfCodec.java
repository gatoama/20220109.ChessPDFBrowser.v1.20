/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <EE>	- Encoded class
 */
public interface GenericSelfCodec<EE> {

	public EE encode();
	public void decode( EE encodedValue );
}
