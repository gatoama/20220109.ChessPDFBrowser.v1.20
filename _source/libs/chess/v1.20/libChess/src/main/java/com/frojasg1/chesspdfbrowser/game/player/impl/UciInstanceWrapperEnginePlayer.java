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

import com.frojasg1.chesspdfbrowser.analysis.engine.UciInstanceWrapper;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.UciInstance;
import com.frojasg1.chesspdfbrowser.enginewrapper.uci.go.ChessEngineGoAttributes;
import com.frojasg1.chesspdfbrowser.game.time.GameTimeControl;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class UciInstanceWrapperEnginePlayer extends UciInstanceWrapper
{
	protected GameTimeControl _timeControl = null;

	protected boolean _isWhite = false;

	public void init( Integer id, UciInstance uciInstance )
	{
		throw( new RuntimeException( "You cannot use that init function. Use init( UciInstance uciInstance ) instead" ) );
	}

	public void init( boolean isWhite, UciInstance uciInstance )
	{
		super.init( null, uciInstance );

		_isWhite = isWhite;
	}

	public boolean isWhite()
	{
		return( _isWhite );
	}

	public void setGameTimeControl( GameTimeControl timeControl )
	{
		_timeControl = timeControl;
	}

	@Override
	protected ChessEngineGoAttributes createGoAttributes()
	{
		ChessEngineGoAttributes result = null;
		
		if( _timeControl != null )
		{
			result = new ChessEngineGoAttributes();

			if( isWhite() )
				result.setMoveTime( calculateTime( _timeControl.getWMilliSecondsPerMove() ) );
			else
				result.setMoveTime( calculateTime( _timeControl.getBMilliSecondsPerMove() ) );
	
			result.setwInc( _timeControl.getMilliSecWInc() );
			result.setbInc( _timeControl.getMilliSecBInc() );
			result.setwTime( calculateTime( _timeControl.getMilliSecWTime() ) );
			result.setbTime( calculateTime( _timeControl.getMilliSecBTime() ) );
		}

		return( result );
	}

	protected Integer calculateTime( Integer input )
	{
		int MARGIN = 1000;
		Integer result = input;
		if( result != null )
			result = result - MARGIN;
		
		return( result );
	}
}
