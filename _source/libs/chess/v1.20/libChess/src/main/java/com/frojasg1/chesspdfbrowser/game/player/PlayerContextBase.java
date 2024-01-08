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
package com.frojasg1.chesspdfbrowser.game.player;

import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import com.frojasg1.chesspdfbrowser.game.ChessGamePlayContext;
import com.frojasg1.chesspdfbrowser.game.time.GameTimeControl;
import com.frojasg1.chesspdfbrowser.game.time.PlayTimeController;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PlayerContextBase
{
	protected PlayTimeController _timeController = null;
	protected String _playerName = null;
	protected Integer _playerElo = null;

	protected ChessGamePlayContext _parent = null;

	public PlayerContextBase( PlayTimeController timeController,
								String playerName,
								Integer playerElo )
	{
		_timeController = timeController;
		_playerName = playerName;
		_playerElo = playerElo;
	}

	public String getPlayerString()
	{
		String result = getPlayerName();
		if( getPlayerElo() != null )
			result = String.format( "$s ($d)", result, getPlayerElo() );

		return( result );
	}

	public String getPlayerName()
	{
		return( _playerName );
	}

	public Integer getPlayerElo()
	{
		return( _playerElo );
	}

	public boolean isStopped()
	{
		return( ( _timeController == null ) || _timeController.isStopped() );
	}

	public PlayTimeController getTimeController()
	{
		return( _timeController );
	}

	public void setPlayerName( String playerName )
	{
		_playerName = playerName;
	}

	public void setPlayerElo( Integer playerElo )
	{
		_playerElo = playerElo;
	}

	public void setParent( ChessGamePlayContext parent )
	{
		_parent = parent;
	}

	public void stopTime()
	{
		_timeController.stopTime();
	}

	public void startThinking( String positionFenString, Consumer<LongAlgebraicNotationMove> callback )
	{
		_timeController.startTime();
	}

	protected void timeExhausted()
	{
		_parent.fireTimeExhausted();
	}

	public long getRemainingTimeMs()
	{
		return( _timeController.getRemainingTime() );
	}

	public void releaseResources()
	{
		_parent = null;

		if( _timeController != null )
			_timeController.releaseResources();

		_timeController = null;
	}

	public void setGameTimeControl( GameTimeControl gameTimeControl )
	{
		// intentionally left blank
	}

	public ChessGamePlayContext getParent()
	{
		return( _parent );
	}

	public void pauseGame()
	{
		_timeController.pauseTime();
	}

	public boolean isEngine()
	{
		return( false );
	}
}
