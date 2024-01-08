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
package com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;

/**
 *
 * @author Usuario
 */
public class NAG
{
	protected final int _nag;

	public NAG( int nag )
	{
		if( ( nag < 0 ) || ( nag > 255 ) )
			throw( new IllegalArgumentException( getChessStrConf().getProperty( ChessStringsConf.CONF_NAG_OUT_OF_RANGE ) +
												": " + nag ) );

		_nag = nag;
	}

	public NAG( String nagStr ) throws ChessParserException
	{
		if( ( nagStr.length() > 1 ) && ( nagStr.charAt(0) == '$' ) )
		{
			try
			{
				_nag = Integer.parseInt(nagStr.substring( 1 ) );
				if( ( _nag < 0 ) || ( _nag > 255 ) )
					throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_NAG_OUT_OF_RANGE ) +
													": " + _nag ) );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				throw( new ChessParserException( th.getMessage() ) );
			}
		}
		else
		{
			_nag = getNAGfromStringToShow( nagStr );
			if( _nag == -1 )
				throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_BAD_FORMAT_FOR_NAG ) + ": " + nagStr ) );
		}
	}

	public String getStringToShow()
	{
		String result = "";
		switch( _nag )
		{
			case 1:			result = "!";	break;
			case 2:			result = "?";	break;
			case 3:			result = "!!";	break;
			case 4:			result = "??";	break;
			case 5:			result = "!?";	break;
			case 6:			result = "?!";	break;
			case 11:		result = " =";	break;
			case 14:		result = " +=";	break;
			case 15:		result = " =+";	break;
			case 16:		result = " +/-";	break;
			case 17:		result = " -/+";	break;
			case 18:		result = " +-";	break;
			case 19:		result = " -+";	break;
			case 20:		result = " +--";	break;
			case 21:		result = " -++";	break;
		}

		return( result );
	}

	public static int getNAGfromStringToShow( String nagStr )
	{
		int result = -1;

		if( nagStr.equals( "!" ) )								result = 1;
		else if( nagStr.equals( "?" ) )							result = 2;
		else if( nagStr.equals( "!!" ) )						result = 3;
		else if( nagStr.equals( "??" ) )						result = 4;
		else if( nagStr.equals( "!?" ) )						result = 5;
		else if( nagStr.equals( "?!" ) )						result = 6;
		else if( nagStr.trim().equals( " =".trim() ) )			result = 11;
		else if( nagStr.trim().equals( " +=".trim() ) )			result = 14;
		else if( nagStr.trim().equals( " =+".trim() ) )			result = 15;
		else if( nagStr.trim().equals( " +/=".trim() ) )		result = 14;
		else if( nagStr.trim().equals( " =/+".trim() ) )		result = 15;
		else if( nagStr.trim().equals( " +/-".trim() ) )		result = 16;
		else if( nagStr.trim().equals( " -/+".trim() ) )		result = 17;
		else if( nagStr.trim().equals( " +-".trim() ) )			result = 18;
		else if( nagStr.trim().equals( " -+".trim() ) )			result = 19;
		else if( nagStr.trim().equals( " +--".trim() ) )		result = 20;
		else if( nagStr.trim().equals( " -++".trim() ) )		result = 21;

		else if( nagStr.trim().equals( " ±".trim() ) )			result = 16;
		// with minus UTF
		else if( nagStr.trim().equals( " +/–".trim() ) )			result = 16;
		else if( nagStr.trim().equals( " –/+".trim() ) )			result = 17;
		else if( nagStr.trim().equals( " +–".trim() ) )			result = 18;
		else if( nagStr.trim().equals( " –+".trim() ) )			result = 19;
		else if( nagStr.trim().equals( " +––".trim() ) )			result = 20;
		else if( nagStr.trim().equals( " –++".trim() ) )			result = 21;

		return( result );
	}

	public String getStringForPGNfile()
	{
		return( "$" + String.valueOf( _nag ) );
	}

	public static String getLastNAGStr( String moveStr )
	{
		String result = "";

		String nagCandidate = null;
		int nagLength = 3;
		while( ( result.length() == 0 ) && (nagLength>0) )
		{
			if( moveStr.length() >= nagLength )
			{
				nagCandidate = moveStr.substring( moveStr.length() - nagLength );
				if( getNAGfromStringToShow( nagCandidate ) != -1 )
					result = nagCandidate;
			}
			nagLength--;
		}

		return( result );
	}

	public String toString()
	{
		return( getStringForPGNfile() + "      '" + getStringToShow() + "'" );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
