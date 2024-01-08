/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.proxy;

import java.lang.reflect.Proxy;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ProxyFunctions {
	
	protected static class LazyHolder
	{
		public static final ProxyFunctions INSTANCE = new ProxyFunctions();
	}

	public static ProxyFunctions instance()
	{
		return( LazyHolder.INSTANCE );
	}

	protected <OO> ProxyContextBase<OO> createProxyContextBase( OO obj )
	{
		return( new ProxyContextBase().setObj(obj) );
	}

	public <CC> CC createReadOnlyProxy( CC obj, Class<CC> interfaceToImplement )
	{
		return( (CC) Proxy.newProxyInstance( ProxyFunctions.class.getClassLoader(), 
											new Class[] { interfaceToImplement, ProxyInterface.class }, 
											new ReadOnlyInvocationHandler<CC>(createProxyContextBase(obj)) ) );
	}
}
