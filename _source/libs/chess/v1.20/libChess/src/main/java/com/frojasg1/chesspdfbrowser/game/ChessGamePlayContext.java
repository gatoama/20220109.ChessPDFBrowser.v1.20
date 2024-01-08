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
package com.frojasg1.chesspdfbrowser.game;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessMoveGenerator;
import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import com.frojasg1.chesspdfbrowser.enginewrapper.variant.EngineMoveVariant;
import com.frojasg1.chesspdfbrowser.game.player.PlayerContextBase;
import com.frojasg1.chesspdfbrowser.game.time.GameTimeControl;
import com.frojasg1.chesspdfbrowser.game.time.PlayTimeController;
import com.frojasg1.chesspdfbrowser.view.chess.newgame.PlayerDataForNewGame;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.view.ViewComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessGamePlayContext implements InternationalizedStringConf, ChessMoveGenerator
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessGamePlayContext.properties";

	protected static final String CONF_WHITES_TIME_EXHAUSTED = "WHITES_TIME_EXHAUSTED";
	protected static final String CONF_BLACKS_TIME_EXHAUSTED = "BLACKS_TIME_EXHAUSTED";
	public static final String CONF_ERROR_SETTING_POSITION = "ERROR_SETTING_POSITION";
	public static final String CONF_ERROR_GETTING_ENGINE_MOVE = "ERROR_GETTING_ENGINE_MOVE";

	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

	protected PlayerDataForNewGame _whitePlayerDataInitialValues = null;
	protected PlayerDataForNewGame _blackPlayerDataInitialValues = null;

	protected PlayerContextBase _whitePlayerContext = null;
	protected PlayerContextBase _blackPlayerContext = null;

	protected MoveTreeNode _currentMove = null;
	protected MoveTreeNode _firstPosition = null;

	protected ChessGameControllerInterface _controller = null;

	protected boolean _gameHasEnded = false;

	protected String _currentFenString = null;

	protected GameTimeControl _gameTimeControl = null;

	protected ViewComponent _parent = null;

	protected ChessViewConfiguration _chessViewConfiguration = null;

	protected boolean _isPaused = false;

	protected boolean _hasToCreateNewGame = true;

	protected boolean _nextMoveWillBeTheFirst = true;

	protected boolean _gameAsMainLine = false;

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void init( ViewComponent parent,
						ChessGameControllerInterface controller,
						GameTimeControl gtc,
						ChessViewConfiguration chessViewConfiguration )
	{
		_parent = parent;
		_controller = controller;
		_gameTimeControl = gtc;
		_chessViewConfiguration = chessViewConfiguration;
	}

	public void setWhitePlayerDataForNewGame( PlayerDataForNewGame playerData )
	{
		this._whitePlayerDataInitialValues = playerData;
	}

	public PlayerDataForNewGame getWhitePlayerDataForNewGame()
	{
		return( _whitePlayerDataInitialValues );
	}

	public void setBlackPlayerDataForNewGame( PlayerDataForNewGame playerData )
	{
		this._blackPlayerDataInitialValues = playerData;
	}

	public PlayerDataForNewGame getBlackPlayerDataForNewGame()
	{
		return( _blackPlayerDataInitialValues );
	}

	public void setCreateNewGame( boolean value )
	{
		_hasToCreateNewGame = value;
	}

	public boolean hasToCreateNewGame()
	{
		return( _hasToCreateNewGame );
	}

	public PlayerContextBase getWhitePlayerContext()
	{
		return( _whitePlayerContext );
	}

	public PlayerContextBase getBlackPlayerContext()
	{
		return( _blackPlayerContext );
	}

	public MoveTreeNode getCurrentMove()
	{
		return( _currentMove );
	}

	public void pauseGame()
	{
/*
		try
		{
			String result = null;
			result.length();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
*/
		PlayerContextBase pc = getPlayerContext( nextMoveIsWhite() );
		pc.pauseGame();
		clearRemainingTimes();

		_isPaused = true;
	}

	protected void updateRemainingTime()
	{
		setRemainingTime( (int) _whitePlayerContext.getRemainingTimeMs(),
							(int) _blackPlayerContext.getRemainingTimeMs() );
	}

	protected void clearRemainingTimes()
	{
		setRemainingTime( null, null );
	}

	protected void setRemainingTime( Integer whiteRemainingMs, Integer blackRemainingMs )
	{
		if( _controller != null )
			_controller.setRemainingTime(whiteRemainingMs, blackRemainingMs);
	}

	public boolean isPaused()
	{
		return( _isPaused );
	}

	public ChessGameControllerInterface getController()
	{
		return( _controller );
	}

	public void setGameTimeControl( GameTimeControl gtc )
	{
		_gameTimeControl = gtc;
	}

	public void setWhitePlayer( PlayerContextBase pc )
	{
		_whitePlayerContext = pc;
		updateTimerControllerCallbacks( pc.getTimeController() );
	}

	public void setBlackPlayer( PlayerContextBase pc )
	{
		_blackPlayerContext = pc;
		updateTimerControllerCallbacks( pc.getTimeController() );
	}

	protected void updateTimerControllerCallbacks( PlayTimeController tc )
	{
		tc.setRefreshTimesFunction( () -> updateRemainingTime() );
		tc.setTimeExhaustedCallback( () -> fireTimeExhausted() );
	}

	protected PlayerContextBase getPlayerContext( boolean isWhite )
	{
		return( isWhite ? _whitePlayerContext : _blackPlayerContext );
	}

	public synchronized void stopClock()
	{
		boolean wasWhite = nextMoveIsWhite();

		PlayerContextBase pc = getPlayerContext( wasWhite );

		if ( !isInitialMove() && !isPaused() )
			pc.stopTime();
	}

	public boolean nextMoveIsOfEngine()
	{
		boolean result = false;
		PlayerContextBase pc = this.getPlayerContext( nextMoveIsWhite() );
		if( pc != null )
			result = pc.isEngine();

		return( result );
	}

	public boolean gameStarted()
	{
		return( _currentMove != null );
	}

	public boolean isGameEnded()
	{
		return( isGameEnded( _currentMove ) );
	}

	protected boolean isGameEnded( MoveTreeNode mtn)
	{
		boolean result = false;

		if( mtn != null )
		{
			ChessGameMove cgm = mtn.getMove();
			if( cgm != null )
				result = ( cgm.getResultOfGame() != null );
		}

		return( result );
	}

	public synchronized void setNewMove( MoveTreeNode mtn )
	{
		if( ( mtn != null ) && !( !isInitialPosition() && isGameEnded() ) &&
			( isInitialMove() ||
				isPaused() && ( mtn ==  _currentMove ) ||
				( mtn.getParent() == _currentMove ) )
			)
		{
			if( mtn != _currentMove )
				_currentFenString = calculateFenString(mtn);

			if( isInitialMove() || !isGameEnded(mtn) )
				switchTimeControl(mtn );
			else if( _currentMove != null )
				stopClock();

			if( _currentMove != null )
				setNextMoveWillBeTheFirst( false );

			setCurrentMove( mtn );
		}
	}

	protected void setCurrentMove( MoveTreeNode mtn )
	{
		if( _currentMove == null )
			_firstPosition = mtn;
		_currentMove = mtn;
	}

	protected boolean isInitialPosition()
	{
		return( _firstPosition == _currentMove );
	}

	public void setNextMoveWillBeTheFirst( boolean value )
	{
		_nextMoveWillBeTheFirst = value;
	}

	public boolean nextMoveWillBeTheFirst()
	{
		return( _nextMoveWillBeTheFirst );
	}

	public String getCurrentFenString()
	{
		return( _currentFenString );
	}

	protected boolean nextMoveIsWhite()
	{
		return( !wasWhiteToMove(_currentMove) );
	}

	protected boolean wasWhiteToMove( MoveTreeNode mtn )
	{
		return( mtn.wasWhiteToMove() );
	}

	protected boolean isInitialMove()
	{
		return( _currentMove == null );
	}

	protected void switchTimeControl( MoveTreeNode mtn )
	{
		boolean wasWhite = wasWhiteToMove(mtn);

		PlayerContextBase pc = getPlayerContext( wasWhite );
		PlayerContextBase nextPc = getPlayerContext( !wasWhite );

		if ( !isInitialMove() && !isPaused() &&
			!pc.isStopped() )
		{
			pc.stopTime();
		}

		updateGameTimeControl();

		nextPc.setGameTimeControl(_gameTimeControl);
		nextPc.startThinking(_currentFenString, mo -> processNewMove( mo ) );
	}

	protected void processNewMove( LongAlgebraicNotationMove newMove )
	{
		ChessGameMove newCgm = calculateNewChessGameMove( newMove );
		makeNewMove( newCgm );
	}

	protected void makeNewMove( ChessGameMove newMove )
	{
		if( _controller != null )
			_controller.newPositionInTheMovesTree(_currentMove, newMove, this);
	}

	protected ChessGameMove calculateNewChessGameMove( LongAlgebraicNotationMove newMove )
	{
		ChessGameMove result = null;

		if( newMove != null )
		{
			SubvariantAnalysisResult subvariantAnalysisResult = createSubvariantAnalysisResult( createEngineMoveVariant( newMove ) );
			result = subvariantAnalysisResult.getSubvariantChessGame().getMoveTreeGame().getLastNodeOfMainVariant().getMove();
		}

		return( result );
	}

	protected EngineMoveVariant createEngineMoveVariant( LongAlgebraicNotationMove newMove )
	{
		EngineMoveVariant result = new EngineMoveVariant();
		result.init();
		result.add( newMove );

		return( result );
	}

	protected SubvariantAnalysisResult createSubvariantAnalysisResult( EngineMoveVariant emv )
	{
		return( MoveTreeNodeUtils.instance().createSubvariantAnalysisResult( _currentFenString,
																				_chessViewConfiguration,
																				emv ) );
	}

	protected void updateGameTimeControl()
	{
		int secWTime = (int) ( _whitePlayerContext.getRemainingTimeMs() );
		_gameTimeControl.setMilliSecWTime(secWTime);

		int secBTime = (int) ( _blackPlayerContext.getRemainingTimeMs() );
		_gameTimeControl.setMilliSecBTime(secBTime);
	}

	public void resumeGame()
	{
		if( ! _gameHasEnded )
		{
			_controller.newPositionInTheMovesTree(_currentMove, null, this);

			_isPaused = false;
//			setNewMove( _currentMove );
		}
	}

	public void fireTimeExhausted()
	{
		boolean isWhitesTimeExhausted = !_currentMove.getMove().getMoveToken().isWhiteToMove();

		setTimeExhaustedResult( _currentMove, isWhitesTimeExhausted );
		_controller.newPositionInTheMovesTree(_currentMove, null, null);

		gameHasEnded();
	}

	protected void setTimeExhaustedResult( MoveTreeNode currentMtn, boolean isWhitesTimeExhausted )
	{
		if( currentMtn != null )
		{
			if( isWhitesTimeExhausted )
				whitesTimeExhausted(currentMtn);
			else
				blacksTimeExhausted(currentMtn);
		}
	}

	protected void whitesTimeExhausted( MoveTreeNode currentMtn )
	{
		String comment = this.getInternationalString(CONF_WHITES_TIME_EXHAUSTED);
		timeExhausted( currentMtn, comment, blackWinsResult() );
	}

	protected void blacksTimeExhausted( MoveTreeNode currentMtn )
	{
		String comment = this.getInternationalString(CONF_BLACKS_TIME_EXHAUSTED);
		timeExhausted( currentMtn, comment, whiteWinsResult() );
	}

	public boolean isGameAsMainLine() {
		return _gameAsMainLine;
	}

	public void setGameAsMainLine(boolean _gameAsMainLine) {
		this._gameAsMainLine = _gameAsMainLine;
	}




	protected String blackWinsResult()
	{
		return( "0-1" );
	}

	protected String whiteWinsResult()
	{
		return( "1-0" );
	}

	protected void timeExhausted( MoveTreeNode currentMtn, String comment, String resultOfGame )
	{
		currentMtn.setComment( resultOfGame + " " + comment );
		currentMtn.getMove().setResultOfGame(resultOfGame);
	}

	public void gameHasEnded()
	{
		_gameHasEnded = true;

		_controller.gameIsOver();

		releaseResources();
	}

	public void releaseResources()
	{
		if( _whitePlayerContext != null )
			_whitePlayerContext.releaseResources();
		_whitePlayerContext = null;

		if( _blackPlayerContext != null )
			_blackPlayerContext.releaseResources();
		_blackPlayerContext = null;

		_currentMove = null;

		_controller = null;

		_gameHasEnded = false;

		_currentFenString = null;

		_gameTimeControl = null;

		_parent = null;
	}

	protected String calculateFenString( MoveTreeNode mtn )
	{
		return( MoveTreeNodeUtils.instance().getFen( mtn ) );
	}

	public void showError( String text )
	{
		HighLevelDialogs.instance().errorMessageDialog(_parent, text);
	}

	protected static void registerInternationalizedStrings()
	{
		staticRegisterInternationalString(CONF_WHITES_TIME_EXHAUSTED, "White's time exhausted" );
		staticRegisterInternationalString(CONF_BLACKS_TIME_EXHAUSTED, "Black's time exhausted" );
		staticRegisterInternationalString(CONF_ERROR_SETTING_POSITION, "Error setting position $1 \n $2" );
		staticRegisterInternationalString(CONF_ERROR_GETTING_ENGINE_MOVE, "Error getting engine move: \n $1" );
	}

	public static void staticRegisterInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		staticRegisterInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
