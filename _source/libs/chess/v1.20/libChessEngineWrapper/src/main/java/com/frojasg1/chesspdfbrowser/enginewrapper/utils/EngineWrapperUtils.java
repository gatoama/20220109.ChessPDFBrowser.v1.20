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
package com.frojasg1.chesspdfbrowser.enginewrapper.utils;

import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.impl.FullEngineActionResultImpl;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineWrapperUtils
{

	public SuccessResult createErrorResult( String errorMessage )
	{
		boolean success = false;
		SuccessResult result = createSuccessResult( success, true );
		result.setErrorString( errorMessage );

		return( result );
	}

	public SuccessResult createErrorResult( Exception ex )
	{
		return( createErrorResult( ex, ex.getMessage() ) );
	}

	public SuccessResult createErrorResult( Exception ex, String errorMessage )
	{
		boolean success = false;
		SuccessResult result = createSuccessResult( success, true );
		result.setException(ex);
		result.setErrorString( errorMessage );

		return( result );
	}

	public SuccessResult createSuccessResult( boolean success, boolean isLast )
	{
		SuccessResult result = new SuccessResult();
		result.init( success, isLast );

		return( result );
	}

	public SuccessResult createSuccessfulResult( )
	{
		return( createSuccessResult( true, true ) );
	}

	public <AA extends EngineActionArgs, RR extends EngineActionResult>
				FullEngineActionResult<AA, RR>
		createFullResult( EngineWrapper parent,
							EngineActionRequest<AA> request,
							SuccessResult successResult,
							RR actionResult )
	{
		return( new FullEngineActionResultImpl( parent, request,
												successResult, actionResult ) );
	}

	public <AA extends EngineActionArgs, RR extends EngineActionResult>
				FullEngineActionResult<AA, RR>
		createFullResult( EngineWrapper parent,
							EngineActionRequest<AA> request, RR actionResult )
	{
		return( createFullResult(parent, request, createSuccessfulResult(), actionResult ) );
	}

	public <AA extends EngineActionArgs, RR extends EngineActionResult>
				FullEngineActionResult<AA, RR>
		createFullNonLastResult( EngineWrapper parent,
								EngineActionRequest<AA> request, RR actionResult )
	{
		return( createFullResult(parent, request, createSuccessResult(true, false),
								actionResult ) );
	}

	public <AA extends EngineActionArgs, RR extends EngineActionResult>
				FullEngineActionResult<AA, RR>
		createFullResult( EngineWrapper parent,
							EngineActionRequest<AA> request,
							SuccessResult successResult )
	{
		return( new FullEngineActionResultImpl( parent, request, successResult, null ) );
	}
}
