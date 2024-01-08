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
package com.frojasg1.chesspdfbrowser.view.chess.newgame.impl.desktop;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.chesspdfbrowser.analysis.engine.EngineWrapperInstanceWrapper;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.EngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.FullEngineActionResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.impl.FullEngineActionResultImpl;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.UciInstance;
import com.frojasg1.chesspdfbrowser.game.ChessGamePlayContext;
import com.frojasg1.chesspdfbrowser.game.player.PlayerContextBase;
import com.frojasg1.chesspdfbrowser.game.player.impl.PlayerContextForEngine;
import com.frojasg1.chesspdfbrowser.game.player.impl.UciInstanceWrapperEnginePlayer;
import com.frojasg1.chesspdfbrowser.game.time.GameTimeControl;
import com.frojasg1.chesspdfbrowser.game.time.PlayTimeController;
import com.frojasg1.chesspdfbrowser.view.chess.newgame.NewGameCreator;
import com.frojasg1.chesspdfbrowser.view.chess.newgame.PlayerDataForNewGame;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JDialog;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class NewGameCreatorDesktopImpl implements NewGameCreator
{
	protected ViewComponent _parent = null;
	protected ApplicationInitContext _initContext;
	protected ChessGameControllerInterface _controller;

	protected ChessGamePlayContext _result = null;
	protected boolean _callbackInvoked = false;

	protected Consumer<ChessGamePlayContext> _callback = null;

	protected volatile int _remainingEngineInitializations = 0;

	protected ChessGamePlayContext _previousChessGamePlayContext = null;

	public void init( ViewComponent parent,
						ChessGameControllerInterface controller,
						ApplicationInitContext initContext,
						ChessGamePlayContext previousChessGamePlayContext )
	{
		_parent = parent;
		_controller = controller;
		_initContext = initContext;
		_previousChessGamePlayContext = previousChessGamePlayContext;
	}

	@Override
	public void createNewGame(Consumer<ChessGamePlayContext> callback)
	{
		try
		{
			_callback = callback;

			NewGameSetupJDial dial = createDialog(this::createNewGameCallback);
			dial.setVisible(true);

			if( dial.wasSuccessful() )
			{
				_result = new ChessGamePlayContext();

				GameTimeControl gtc = createGameTimeControl( dial.getWhitePlayerData(), dial.getBlackPlayerData() );
				_result.init( _parent, _controller,
							gtc,
							ApplicationConfiguration.instance()
							);

				List<PlayerContextForEngine> enginePlayersToInit = new ArrayList<>();
				_result.setWhitePlayerDataForNewGame( dial.getWhitePlayerData() );
				_result.setWhitePlayer( createPlayerController( dial.getWhitePlayerData(), enginePlayersToInit ) );
				_result.setBlackPlayerDataForNewGame( dial.getBlackPlayerData() );
				_result.setBlackPlayer( createPlayerController( dial.getBlackPlayerData(), enginePlayersToInit ) );
				_result.setCreateNewGame( dial.hasToCreateNewGame() );
				_result.setGameAsMainLine( dial.gameAsMainLine() );

				initEngines( enginePlayersToInit );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void createNewGameCallback(InternationalizationInitializationEndCallback iiec)
	{
		try
		{
			NewGameSetupJDial dial = (NewGameSetupJDial) iiec; 
			dial.setVisible(true);

			if( dial.wasSuccessful() )
			{
				_result = new ChessGamePlayContext();

				GameTimeControl gtc = createGameTimeControl( dial.getWhitePlayerData(), dial.getBlackPlayerData() );
				_result.init( _parent, _controller,
							gtc,
							ApplicationConfiguration.instance()
							);

				List<PlayerContextForEngine> enginePlayersToInit = new ArrayList<>();
				_result.setWhitePlayer( createPlayerController( dial.getWhitePlayerData(), enginePlayersToInit ) );
				_result.setBlackPlayer( createPlayerController( dial.getBlackPlayerData(), enginePlayersToInit ) );
				_result.setCreateNewGame( dial.hasToCreateNewGame() );

				initEngines( enginePlayersToInit );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected int secToMillis( int sec )
	{
		return( sec * 1000 );
	}

	protected GameTimeControl createGameTimeControl( PlayerDataForNewGame whitePlayerData,
													PlayerDataForNewGame blackPlayerData )
	{
		GameTimeControl result = new GameTimeControl();

		if( whitePlayerData.isTimePlusIncrement() )
		{
			result.setMilliSecWTime( secToMillis( whitePlayerData.getTotalTime() ) );
			result.setMilliSecWInc( secToMillis( whitePlayerData.getIncrement() ) );
		}
		else if( whitePlayerData.isTimePerMove() )
		{
			result.setWMilliSecondsPerMove( secToMillis( whitePlayerData.getSecondsPerMove() ) );
		}

		if( blackPlayerData.isTimePlusIncrement() )
		{
			result.setMilliSecBTime( secToMillis( blackPlayerData.getTotalTime() ) );
			result.setMilliSecBInc( secToMillis( blackPlayerData.getIncrement() ) );
		}
		else if( blackPlayerData.isTimePerMove() )
		{
			result.setBMilliSecondsPerMove( secToMillis( blackPlayerData.getSecondsPerMove() ) );
		}

		return( result );
	}

	protected NewGameSetupJDial createDialog(Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack)
	{
		NewGameSetupJDial result = null;

		if( _parent instanceof JDialog )
			result = new NewGameSetupJDial( _initContext, (JDialog) _parent, true,
											initializationEndCallBack);
		else if( _parent instanceof Frame )
			result = new NewGameSetupJDial( _initContext, (Frame) _parent, true,
											initializationEndCallBack);

		result.init(_previousChessGamePlayContext);

		return( result );
	}

	protected PlayerContextBase createPlayerController( PlayerDataForNewGame playerData,
														List<PlayerContextForEngine> enginePlayersToInit )
	{
		PlayerContextBase result = null;

		PlayTimeController tc = createTimeController( playerData );

		if( playerData.isHuman() )
		{
			result = new PlayerContextBase( tc, playerData.getPlayerName(), playerData.getElo() );
		}
		else if( playerData.isEngine() )
		{
			UciInstanceWrapperEnginePlayer enginePlayer = createUciInstanceWrapperEnginePlayer( playerData );
			result = new PlayerContextForEngine( tc,
					enginePlayer,
					getEngineInstanceConfiguration(playerData.getSelectedEngineName()) );

			enginePlayersToInit.add( (PlayerContextForEngine) result );
		}

		return( result );
	}

	protected EngineInstanceConfiguration getEngineInstanceConfiguration( String engineName )
	{
		return( _initContext.getChessEngineConfigurationPersistency().getModelContainer().get(engineName) );
	}

	protected PlayTimeController createTimeController( PlayerDataForNewGame playerData )
	{
		PlayTimeController result = null;
		
		if( playerData != null )
		{
			result = new PlayTimeController( );

			int remainingSeconds = 0;
			if( playerData.isTimePlusIncrement() )
				result.init( (long) playerData.getTotalTime(), (long) playerData.getIncrement(),
							null );
			else if( playerData.isTimePerMove() )
				result.init( null, null, (long) playerData.getSecondsPerMove() );
		}

		return( result );
	}

	protected UciInstanceWrapperEnginePlayer createUciInstanceWrapperEnginePlayer( PlayerDataForNewGame playerData )
	{
		UciInstanceWrapperEnginePlayer result = null;
		
		if( playerData != null )
		{
			UciInstance uciInstance = new UciInstance();
			uciInstance.init();

			result = new UciInstanceWrapperEnginePlayer();
			result.init(playerData.isWhite(), uciInstance);
		}

		return( result );
	}

	protected void initEngines( List<PlayerContextForEngine> enginePlayersToInit )
	{
		_remainingEngineInitializations = enginePlayersToInit.size();

		if( _remainingEngineInitializations == 0 )
			invokeCallback();
		else
		{
			for( PlayerContextForEngine enginePc: enginePlayersToInit )
			{
				EngineInstanceConfiguration eic = this.getEngineInstanceConfiguration( enginePc.getPlayerName() );
				UciInstanceWrapperEnginePlayer uiwep = enginePc.getUciInstanceWrapperEnginePlayer();
				uiwep.getEngineInstance().init( eic,	res -> engineInitializationCallback( res, eic, uiwep ) );
			}
		}
	}

	protected ChessEngineConfiguration getChessEngineConfiguration(EngineInstanceConfiguration eic)
	{
		ChessEngineConfiguration result = null;
		if( eic != null )
			result = eic.getChessEngineConfiguration();

		return( result );
	}

	protected void engineInitializationCallback( FullEngineActionResult<EngineInstanceConfiguration, EngineActionResult> result,
												EngineInstanceConfiguration eic,
												UciInstanceWrapperEnginePlayer uiwep)
	{
		if( result.getSuccessResult().isAsuccess() )
		{
			ChessEngineConfiguration cec = getChessEngineConfiguration( eic );
			if( cec != null )
			{
				uiwep.applyEngineConfiguration( cec, this::setConfigurationCallback );
			}
			else
				setConfigurationCallback(createEmptyApplyConfigurationResult());
		}
	}

	protected FullEngineActionResult<ChessEngineConfiguration, EngineActionResult> createEmptyApplyConfigurationResult()
	{
		boolean success = true;
		FullEngineActionResultImpl<ChessEngineConfiguration, EngineActionResult> result = 
			new FullEngineActionResultImpl<>(success);

		return( result );
	}

	protected synchronized void setConfigurationCallback( FullEngineActionResult<ChessEngineConfiguration, EngineActionResult> result )
	{
		if( result.getSuccessResult().isAsuccess() )
			_remainingEngineInitializations--;
		else
		{
			_result.releaseResources();
			_result = null;

			invokeCallback();
		}

		if( _remainingEngineInitializations == 0 )
			invokeCallback();
	}

	protected void invokeCallback()
	{
		if( !_callbackInvoked && ( _callback != null ) )
		{
			_callback.accept( _result );
			_callbackInvoked = true;
		}
	}
}
