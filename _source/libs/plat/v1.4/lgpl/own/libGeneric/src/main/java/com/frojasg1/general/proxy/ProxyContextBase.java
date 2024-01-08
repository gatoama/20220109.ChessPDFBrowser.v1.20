/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.proxy;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ProxyContextBase<OO>
{
	protected OO _obj;

	public OO getObj() {
		return _obj;
	}

	public ProxyContextBase<OO> setObj(OO _obj) {
		this._obj = _obj;

		return( this );
	}
}
