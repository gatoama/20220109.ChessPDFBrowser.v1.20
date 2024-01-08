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
package com.frojasg1.chesspdfbrowser.threads;

import com.frojasg1.chesspdfbrowser.engine.io.file.implementation.PgnChessFile;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class SavePGNThread extends Thread
{
	protected LoadChessControllerInterface _controller = null;
	protected String _fileName = null;
	protected List<ChessGame> _list = null;
	protected PgnChessFile _pgnFile = null;

	public SavePGNThread( LoadChessControllerInterface controller, PgnChessFile pgnFile, List<ChessGame> list, String fileName )
	{
		_pgnFile = pgnFile;
		_list = list;
		_controller = controller;
		_fileName = fileName;
	}

	@Override
	public void run()
	{
		try
		{
			if( _controller != null )	_controller.startLoading();

			_pgnFile.saveToFile( _list, _fileName, null );

			if( _controller != null )
				_controller.newChessListGameLoaded( _list, _pgnFile );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			if( _controller != null )
			{
				_controller.showLoadingError( "Error saving PGN file " + _fileName + ". ERROR: " + th.getMessage(), "Error saving file" );
			}
		}
		finally
		{
			if( _controller != null )
				_controller.endLoading();
		}
	}
}
