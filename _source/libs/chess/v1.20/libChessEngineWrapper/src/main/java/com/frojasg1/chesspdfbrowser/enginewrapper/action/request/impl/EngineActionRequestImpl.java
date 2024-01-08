/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.chesspdfbrowser.enginewrapper.action.request.impl;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineActionRequestImpl<AA extends EngineActionArgs>
		implements EngineActionRequest<AA>
{
	protected int _requestType = -1;
	protected AA _args = null;
	protected Class<AA> _argsClass = null;

	public EngineActionRequestImpl( int requestType, AA args, Class<AA> clazz )
	{
		_requestType = requestType;
		_args = args;
		_argsClass = clazz;
	}

	@Override
	public int getRequestType()
	{
		return( _requestType );
	}

	@Override
	public AA getArgs()
	{
		return( _args );
	}

	@Override
	public void setArgs(AA args)
	{
		_args = args;
	}

	@Override
	public Class<AA> getArgsClass()
	{
		return( _argsClass );
	}
}
