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
package com.frojasg1.chesspdfbrowser.enginewrapper.uci.action;

import com.frojasg1.chesspdfbrowser.enginewrapper.EngineWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.EngineWrapperActionBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionRequest;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.builder.ChessEngineConfigurationBuilder;
import com.frojasg1.chesspdfbrowser.enginewrapper.utils.EngineWrapperUtils;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UciNewGameEngineWrapperAction extends EngineWrapperActionBase<EngineActionArgs, EngineActionResult>
{
	protected ChessEngineConfigurationBuilder _chessEngineConfigurationBuilder = null;

	public UciNewGameEngineWrapperAction( EngineWrapperUtils utils )
	{
		super( utils );
	}

	@Override
	public void init( String name, EngineWrapper engineWrapper, EngineActionRequest<EngineActionArgs> request,
		Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFunction )
	{
		throw( new RuntimeException( "cannot use: init( String name, EngineWrapper engineWrapper, EngineActionRequest<EngineActionArgs> request,\n" +
									"		Consumer<FullEngineActionResult> callbackFunction )" ) );
	}

	@Override
	public void init( EngineWrapper engineWrapper, EngineActionRequest<EngineActionArgs> request,
					Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFunction,
					Class<EngineActionArgs> argsClass )
	{
		// TODO: translate
		super.init( "Uci ucinewgame", engineWrapper, request, callbackFunction );

		_chessEngineConfigurationBuilder = createChessEngineConfigurationBuilder();
	}

	@Override
	public void start()
	{
		super.start();

		safeMethodExecution( () -> this.initTimer( 100000 ) );
		safeMethodExecution( () -> send( "ucinewgame" ) );
		safeMethodExecution( () -> send( "isready" ) );
	}

	protected ChessEngineConfigurationBuilder createChessEngineConfigurationBuilder()
	{
		ChessEngineConfigurationBuilder result = new ChessEngineConfigurationBuilder();
		result.init();

		return( result );
	}

	@Override
	public void accept_internal( String line )
	{
		if( line.equals( "readyok" ) )
		{
			clearTemporalOutput();

			callbackInvocation( createSuccessResult() );
		}
	}

	@Override
	protected void callbackInvocation(SuccessResult result)
	{
		super.callbackInvocation(createFullResult( result) );
	}
}
