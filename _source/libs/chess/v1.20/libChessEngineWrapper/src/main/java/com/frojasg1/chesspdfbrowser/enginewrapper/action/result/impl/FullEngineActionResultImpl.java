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
package com.frojasg1.chesspdfbrowser.enginewrapper.action.result.impl;

import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FullEngineActionResultImpl<AA extends EngineActionArgs, RR extends EngineActionResult>
	implements FullEngineActionResult<AA, RR>
{

	protected EngineWrapper _sender = null;
	protected EngineActionRequest<AA> _request = null;
	protected SuccessResult _successResult = null;
	protected RR _actionResult = null;

	public FullEngineActionResultImpl( boolean success )
	{
		this( null, null, new SuccessResult(success, true), null );
	}

	public FullEngineActionResultImpl( EngineWrapper sender, EngineActionRequest<AA> request,
										SuccessResult successResult, RR actionResult )
	{
		_sender = sender;
		_request = request;
		_successResult = successResult;
		_actionResult = actionResult;
	}

	@Override
	public EngineWrapper getSender()
	{
		return( _sender );
	}

	@Override
	public EngineActionRequest<AA> getRequest()
	{
		return( _request );
	}

	@Override
	public SuccessResult getSuccessResult()
	{
		return( _successResult );
	}

	@Override
	public RR getActionResult()
	{
		return( _actionResult );
	}
}
