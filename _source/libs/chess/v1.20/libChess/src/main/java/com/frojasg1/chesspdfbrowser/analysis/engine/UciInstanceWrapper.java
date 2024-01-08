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
package com.frojasg1.chesspdfbrowser.analysis.engine;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EnginePositionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.UciInstance;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UciInstanceWrapper extends EngineWrapperInstanceWrapper
{
	protected Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> _goCallbackFunction;
	protected ChessEngineGoAttributes _goAttributes;
	protected int _correlationIdForGo = 0;

	public void init( Integer id, UciInstance uciInstance )
	{
		super.init( id, uciInstance );
	}

	@Override
	public UciInstance getEngineInstance()
	{
		return( (UciInstance) super.getEngineInstance() );
	}

	@Override
	public void initEngine( EngineInstanceConfiguration configuration,
		Consumer<FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult>> callbackFun )
	{
		getEngineInstance().init( configuration, callbackFun );
	}

	@Override
	public void go(Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callbackFun)
	{
		_goCallbackFunction = callbackFun;
		_goAttributes = createGoAttributes();
		getEngineInstance().go( _goAttributes, callbackFun );
	}

	public void stopThinking(Consumer<FullEngineActionResult<EngineActionArgs, EngineActionResult>> callbackFun)
	{
		getEngineInstance().stopThinking( callbackFun );
	}

	@Override
	public void go(ChessEngineGoAttributes goAttr,
					Consumer<FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult>> callbackFun)
	{
		_goCallbackFunction = callbackFun;
		_goAttributes = goAttr;
		getEngineInstance().go( goAttr, callbackFun );
	}

	@Override
	public void setCurrentPosition(String fenString, Consumer<FullEngineActionResult<EnginePositionArgs, EngineActionResult>> callbackFun)
	{
		getEngineInstance().stopThinking( res -> processStopThinkingForPriorToSettingCurrentPosition( res, fenString, callbackFun ) );
	}

	protected void processStopThinkingForPriorToSettingCurrentPosition( FullEngineActionResult<EngineActionArgs, EngineActionResult> result,
																		String fenString,
																		Consumer<FullEngineActionResult<EnginePositionArgs, EngineActionResult>> callbackFun)
	{
		if( ( fenString != null ) && ( result != null ) && result.getSuccessResult().isAsuccess() )
		{
			EnginePositionArgs args = new EnginePositionArgs(fenString);
			if( callbackFun == null )
			{
				callbackFun = res -> processCurrentPositionChangeResult(res);
			}

			getEngineInstance().position(args, callbackFun);
		}
	}

	protected void processCurrentPositionChangeResult( FullEngineActionResult<EnginePositionArgs, EngineActionResult> result )
	{
		if( ( result != null ) && result.getSuccessResult().isAsuccess() )
			updateEverything();
	}

	@Override
	protected void updateEverything()
	{
		if( ( _goAttributes != null ) && ( _goCallbackFunction != null ) )
			go( _goAttributes, _goCallbackFunction );
	}

	protected ChessEngineGoAttributes createGoAttributes()
	{
		ChessEngineGoAttributes result = new ChessEngineGoAttributes();

		result.setCorrelationId(_correlationIdForGo++);
		result.setMoveTime( getNumberOfSecondsToSpendInAnalysis() );

		return( result );
	}
}
