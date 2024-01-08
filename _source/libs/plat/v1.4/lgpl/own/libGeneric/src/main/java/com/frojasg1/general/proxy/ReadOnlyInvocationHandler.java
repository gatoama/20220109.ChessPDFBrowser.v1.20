/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.proxy;

import java.lang.reflect.Method;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ReadOnlyInvocationHandler<OO> extends InvocationHandlerBase<OO, ProxyContextBase<OO>>
{
	public ReadOnlyInvocationHandler( ProxyContextBase<OO> proxyContext )
	{
		super(proxyContext);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		if( isProxyInterfaceFunction( method ) )
			result = getProxyContext();
		else if( !method.getName().startsWith( "set" ) )
			result = invoke( method, args );

		return( result );
	}
}
