/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class InvocationHandlerBase<OO, CC extends ProxyContextBase<OO>>
	implements InvocationHandler, ProxyInterface<OO, CC>
{
	protected CC _proxyContext;

	public InvocationHandlerBase( CC proxyContext )
	{
		_proxyContext = proxyContext;
	}

	public OO getObject()
	{
		return( getProxyContext().getObj() );
	}

	public CC getProxyContext()
	{
		return( _proxyContext );
	}

	public Object invoke(Method method, Object[] args) throws Throwable {
		return( method.invoke( getObject(), args ) );
	}

	protected boolean isProxyInterfaceFunction( Method method )
	{
		return( "getProxyContext".equals( method.getName() ) &&
				( method.getParameterTypes().length == 0 ) );
	}

	@Override
	public abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
