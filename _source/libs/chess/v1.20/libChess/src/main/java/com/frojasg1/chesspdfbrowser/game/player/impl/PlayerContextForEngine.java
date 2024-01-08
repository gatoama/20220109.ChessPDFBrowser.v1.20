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
package com.frojasg1.chesspdfbrowser.game.player.impl;

import com.frojasg1.chesspdfbrowser.analysis.EngineAnalysisProcessData;
import com.frojasg1.chesspdfbrowser.analysis.engine.EngineWrapperInstanceWrapper;
import com.frojasg1.chesspdfbrowser.analysis.impl.StartEngineAnalysis;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.args.EnginePositionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.engineresult.ChessEngineResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.game.ChessGamePlayContext;
import com.frojasg1.chesspdfbrowser.game.player.PlayerContextBase;
import com.frojasg1.chesspdfbrowser.game.time.GameTimeControl;
import com.frojasg1.chesspdfbrowser.game.time.PlayTimeController;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PlayerContextForEngine extends PlayerContextBase
{
	protected UciInstanceWrapperEnginePlayer _enginePlayer = null;

	protected StartEngineAnalysis _startEngineAnalysis;

	protected EngineInstanceConfiguration _conf;

	public PlayerContextForEngine( PlayTimeController timeController,
									UciInstanceWrapperEnginePlayer enginePlayer,
									EngineInstanceConfiguration conf )
	{
		super( timeController, conf.getName(), null );

		_conf = conf;
		_enginePlayer = enginePlayer;
		_startEngineAnalysis = createStartEngineAnalysis();
	}

	public UciInstanceWrapperEnginePlayer getUciInstanceWrapperEnginePlayer()
	{
		return( _enginePlayer );
	}

	public void setGameTimeControl( GameTimeControl gameTimeControl )
	{
		_enginePlayer.setGameTimeControl(gameTimeControl);
	}

	protected void startEngine( EngineWrapperInstanceWrapper engineWrapper,
								EngineAnalysisProcessData engineData )
	{
		Integer id = null;
		if( _conf != null )
			_startEngineAnalysis.initEngine(id, engineWrapper, _conf, null );
	}

	protected StartEngineAnalysis createStartEngineAnalysis()
	{
		return( new StartEngineAnalysisForPlayer() );
	}

	@Override
	public void startThinking( String positionFenString, Consumer<LongAlgebraicNotationMove> callback )
	{
		_enginePlayer.setCurrentPosition( positionFenString,
											res -> processSetPositionResult( positionFenString, res, callback ) );
	}

	protected void processSetPositionResult( String positionFenString,
											FullEngineActionResult<EnginePositionArgs, EngineActionResult> result,
											Consumer<LongAlgebraicNotationMove> callback )
	{
		if( ! result.getSuccessResult().isAsuccess() )
		{
			getParent().showError( getParent().createCustomInternationalString(ChessGamePlayContext.CONF_ERROR_SETTING_POSITION,
																				getParent().getCurrentFenString(),
																				result.getSuccessResult().getErrorString() ) );
			getParent().getController().clearGame();
		}
		else
		{
			_enginePlayer.go( res -> processGoResult(res, callback) );
			super.startThinking( positionFenString, callback );
		}
	}

	protected void processGoResult( FullEngineActionResult<ChessEngineGoAttributes, ChessEngineResult> result,
											Consumer<LongAlgebraicNotationMove> callback )
	{
		if( ! hasReleased() && ! getTimeController().isStopped() )
		{
			if( ! result.getSuccessResult().isAsuccess() )
			{
				getParent().showError( getParent().createCustomInternationalString(ChessGamePlayContext.CONF_ERROR_GETTING_ENGINE_MOVE,
																					result.getSuccessResult().getErrorString() ) );
				getParent().getController().clearGame();
			}
			else
			{
				LongAlgebraicNotationMove bestMove = result.getActionResult().getBestMove();
				if( bestMove != null )
					callback.accept( bestMove );
			}
		}
	}

	public boolean hasReleased()
	{
		return( _enginePlayer == null );
	}

	public void releaseResources()
	{
		if( _enginePlayer != null )
		{
			_enginePlayer.closeEngine();
			_enginePlayer = null;
		}
		super.releaseResources();
	}

	@Override
	public synchronized void pauseGame()
	{
		getTimeController().cancelMove();

		_enginePlayer.stopThinking( null );
	}

	protected static class StartEngineAnalysisForPlayer extends StartEngineAnalysis
	{
		public StartEngineAnalysisForPlayer()
		{
			super(null, null);
		}

		protected void launchGo( Integer id, EngineWrapperInstanceWrapper engineWrapperInstanceWrapper )
		{
			// intentionallyLeftBlank
		}
	}

	public boolean isEngine()
	{
		return( true );
	}
}
