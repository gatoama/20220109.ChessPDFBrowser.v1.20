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
package com.frojasg1.chesspdfbrowser.engine.model.chess.configuration;

import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;

/**
 *
 * @author Usuario
 */
public class EnglishChessConfiguration extends ChessLanguageConfiguration
{
	protected static EnglishChessConfiguration _instance = null;
	
	public static final char WHITE_CHAR = 'W';
	public static final char BLACK_CHAR = 'B';
	public static final char PAWN_CHAR = 'p';

	public static EnglishChessConfiguration instance()
	{
		if( _instance == null ) _instance = new EnglishChessConfiguration();
		
		return( _instance );
	}
	
	public EnglishChessConfiguration()
	{
		super( ChessLanguageConfiguration.ENGLISH, "KQRBN" );
	}

	@Override
	public String translateMoveStringToEnglish( String moveString, ChessGameMove cgm )
	{
		return( moveString );
	}

	@Override
	public String translateMoveStringFromEnglish( String moveString, ChessGameMove cgm )
	{
		return( moveString );
	}

}
