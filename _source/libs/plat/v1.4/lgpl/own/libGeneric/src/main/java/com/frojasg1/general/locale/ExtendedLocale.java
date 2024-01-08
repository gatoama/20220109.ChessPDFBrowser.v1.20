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
package com.frojasg1.general.locale;

import java.util.Locale;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ExtendedLocale
{
	protected static Locale _defaultLocaleForOutput = null;

	protected Locale _locale;
	protected Locale _outputLocale;

	public static void setCurrentLocale( Locale localeForOutput )
	{
		_defaultLocaleForOutput = localeForOutput;
	}

	public ExtendedLocale( Locale locale )
	{
		_locale = locale;
	}

	public ExtendedLocale( Locale locale, Locale outputLocale )
	{
		this( locale );

		_outputLocale = outputLocale;
	}

	public Locale getLocale()
	{
		return( _locale );
	}

	public Locale getOutputLocale()
	{
		Locale result = _outputLocale;
		if( result == null )
			result = _defaultLocaleForOutput;

		return( result );
	}

	public String toString()
	{
		Locale outputLocale = getOutputLocale();

		String language = LocaleFunctions.instance().getLanguageName(outputLocale, LocaleFunctions.instance().getLanguageIsoCode( _locale)  );
		String country = LocaleFunctions.instance().getCountryName(outputLocale, LocaleFunctions.instance().getCountryIsoCode( _locale) );

		String str = null;
		if( country == null )
			str = language;
		else
			str = String.format( "%s (%s)",	language, country );
		
		return( str );
	}
}
