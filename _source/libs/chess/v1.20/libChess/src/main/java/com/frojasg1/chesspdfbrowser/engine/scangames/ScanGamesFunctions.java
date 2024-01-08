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
package com.frojasg1.chesspdfbrowser.engine.scangames;

import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.generic.GenericFunctions;
import java.util.Locale;
import java.util.Vector;

/**
 *
 * @author fjavier.rojas
 */
public class ScanGamesFunctions
{
	protected static ScanGamesFunctions _instance;

	public static void changeInstance( ScanGamesFunctions inst )
	{
		_instance = inst;
	}

	public static ScanGamesFunctions instance()
	{
		if( _instance == null )
			_instance = new ScanGamesFunctions();
		return( _instance );
	}

	protected Locale getLocale( String language )
	{
		Locale result = null;
		if( language != null )
			result = GenericFunctions.instance().getObtainAvailableLanguages().getLocaleOfLanguage(language);

		return( result );
	}

	public Vector< ChessLanguageConfiguration.LanguageConfigurationData > getListOfLanguagesToParseFrom( String language )
	{
		Vector< ChessLanguageConfiguration.LanguageConfigurationData > result =
			ChessLanguageConfiguration.getListOfLanguagesToParseFrom( getLocale( language ) );

		return( result );
	}
}
