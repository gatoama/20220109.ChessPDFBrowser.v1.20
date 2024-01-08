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

import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.io.file.implementation.PgnChessFile;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import java.util.List;

/**
 *
 * @author Usuario
 */
public class LoadPGNThread extends Thread
{
	protected LoadChessControllerInterface _controller = null;
	protected String _fileName = null;
	protected List<ChessGame> _list = null;

	public LoadPGNThread( LoadChessControllerInterface controller, String fileName )
	{
		_controller = controller;
		_fileName = fileName;
	}

	@Override
	public void run()
	{
		try
		{
//			System.out.println( "creando pgnFile ..." );
			
			PgnChessFile pgnFile = new PgnChessFile();

//			System.out.println( "bloqueando las ventanas ..." );

			if( _controller != null )	_controller.startLoading();
		
//			System.out.println( "Abriendo y parseando el fichero pgn ..." );

			_list = pgnFile.loadFromFile( _fileName );

//			System.out.println( "Cargando la lista de partidas en la ventana principal ..." );

			if( _controller != null )
				_controller.newChessListGameLoaded( _list, pgnFile );

//			System.out.println( "Ok ..." );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			if( _controller != null )
			{
				_controller.showLoadingError( "Error loading PGN file " + _fileName + ". ERROR: " + th.getMessage(), "Error opening file" );
			}
		}
		finally
		{
			if( _controller != null )
				_controller.endLoading();
		}
	}

	public List<ChessGame> getGameList()
	{
		return( _list );
	}
}
