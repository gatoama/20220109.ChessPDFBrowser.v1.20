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
package com.frojasg1.applications.pdf2pgn.commandline.helpers;

import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.scangames.ScanGamesFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.util.Locale;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author fjavier.rojas
 */
public class ScanGamesForCommandLineFunctions extends ScanGamesFunctions
{
	protected static ScanGamesForCommandLineFunctions _instance;

	public static void changeInstance( ScanGamesForCommandLineFunctions inst )
	{
		_instance = inst;
	}

	public static ScanGamesForCommandLineFunctions instance()
	{
		if( _instance == null )
			_instance = new ScanGamesForCommandLineFunctions();
		return( _instance );
	}

	public String getDefaultChessInputLanguage()
	{
		Locale loc = JComponent.getDefaultLocale();
		String result = loc.getDisplayLanguage( Locale.ENGLISH );
		if( ! isValidLanguageConfigurationName(result) )
			result = "English";

		return( result );
	}

	public boolean isValidLanguageConfiguration( ChessLanguageConfiguration.LanguageConfigurationData lc )
	{
		boolean result = ( lc != null ) && ( lc != ChessLanguageConfiguration.getCustomLanguage() );

		return( result );
	}

	public boolean isValidLanguageConfigurationName( String chessGameInputLanguageName )
	{
		ChessLanguageConfiguration.LanguageConfigurationData lc = getLanguageConfigurationData( chessGameInputLanguageName );
		return( isValidLanguageConfiguration( lc ) );
	}

	public ChessLanguageConfiguration.LanguageConfigurationData getLanguageConfigurationData( String chessGameInputLanguageName )
	{
		ChessLanguageConfiguration.LanguageConfigurationData result = null;
		for( ChessLanguageConfiguration.LanguageConfigurationData lcd: this.getListOfLanguagesToParseFrom() )
			if( lcd._languageName.equals( chessGameInputLanguageName ) )
			{
				result = lcd;
				break;
			}
		return( result );
	}

	public Vector< ChessLanguageConfiguration.LanguageConfigurationData > getListOfLanguagesToParseFrom( )
	{
		return( getListOfLanguagesToParseFrom( "EN" ) );
	}

	public String getChessLanguageForConfiguration( String languageToParseGames,
														String lettersForPiecesToParseGames )
	{
		String result = languageToParseGames;

		if( !isValidLanguageConfigurationName( languageToParseGames ) )
			result = lettersForPiecesToParseGames;

		return( result );
	}
}
