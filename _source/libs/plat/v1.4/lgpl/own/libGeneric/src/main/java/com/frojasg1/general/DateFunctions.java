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
package com.frojasg1.general;

import com.frojasg1.general.string.StringFunctions;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFunctions
{
	protected static DateFunctions _instance;

	public static void changeInstance( DateFunctions inst )
	{
		_instance = inst;
	}

	public static DateFunctions instance()
	{
		if( _instance == null )
			_instance = new DateFunctions();
		return( _instance );
	}
	
	// Both dateFormat and timeFormat, may take one of the following values:
	// { DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };
	public String formatDateTime( Date date, Locale locale, int dateFormat, int timeFormat )
	{
		String result = null;

		if( timeFormat < 0 ) result = DateFormat.getDateInstance( dateFormat, locale).format( date );
		else
		{
			DateFormat df = DateFormat.getDateTimeInstance( dateFormat, timeFormat, locale);
			result = df.format(date);
		}

		return( result );
	}

	public String formatDate( Date date, String format, TimeZone tz )
	{
		String result = null;

		SimpleDateFormat sdf = new SimpleDateFormat( format );
		
		if( tz != null )
			sdf.setTimeZone(tz);

		result = sdf.format( date );

		return( result );
	}

	public String formatDate( Date date )
	{
		return( formatDate( date, "dd/MM/yyyy", null ) );
	}

	public String formatDate( Date date, Locale locale )
	{
		return( formatDate( date, getSimpleDayDateFormat(locale), null ) );
	}

	public String getSimpleDayDateFormat(Locale locale)
	{
		String result = "yyyy/MM/dd";
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		if( formatter instanceof SimpleDateFormat )
		{
			SimpleDateFormat sdf = (SimpleDateFormat) formatter;
			result = sdf.toPattern().replaceAll( "d+", "dd" )
				.replaceAll( "M+", "MM" )
				.replaceAll( "y+", "yyyy" );
		}

		return( result );
	}

	public String formatDateTime( Date date )
	{
		return( formatDate( date, "dd/MM/yyyy HH:mm:ss.SSS", null ) );
	}
	
	public String formatDate_yyyy( Date date, int format  )
	{
		DateFormat df = DateFormat.getDateInstance( DateFormat.SHORT );
		String result = df.format( date );

		SimpleDateFormat sdf = null;
		if( df instanceof SimpleDateFormat )
		{
			sdf = (SimpleDateFormat) df;
			String pattern = sdf.toPattern();
			pattern = pattern.replaceAll( "y+", "yyyy" );

			result = formatDate( date, pattern, null );
		}

		return( result );
	}

	public Date parseSheetDate( String cadena, Locale locale )
	{
		Date date = null;

//		Locale locale = new Locale( "es", "ES" );

		int arrayOfTimeOrDateFormats[] = { DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };

		for( int ii=0; ii<arrayOfTimeOrDateFormats.length; ii ++ )
		{
			try
			{
				date = DateFormat.getDateInstance( arrayOfTimeOrDateFormats[ii], locale).parse( cadena );
				return( date );
			}
			catch( ParseException ex )
			{
			}
		}

		for( int ii=0; ii<arrayOfTimeOrDateFormats.length; ii ++ )
			for( int jj=0; ii<arrayOfTimeOrDateFormats.length; ii ++ )
			{
				try
				{
					DateFormat df = DateFormat.getDateTimeInstance( arrayOfTimeOrDateFormats[ii], arrayOfTimeOrDateFormats[jj], locale);
					date = df.parse( cadena );
					return( date );
				}
				catch( ParseException ex )
				{
				}
			}

		return( date );
	}

	public static void main( String args[] )
	{
		Locale locale = new Locale( "es", "ES" );
		DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		if( formatter instanceof SimpleDateFormat )
		{
			SimpleDateFormat sdf = (SimpleDateFormat) formatter;
			System.out.println( "locale:" + locale.getDisplayCountry() + "   Formato de fechas: " + sdf.toPattern() + "   DateFormat.SHORT :" + DateFormat.SHORT );
		}
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols( locale );
		System.out.println( dfs.getDecimalSeparator() );
		
		DateFormat formatter1 = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.MEDIUM, locale);
		if( formatter1 instanceof SimpleDateFormat )
		{
			SimpleDateFormat sdf = (SimpleDateFormat) formatter1;
			System.out.println( "locale:" + locale.getDisplayCountry() + "   Formato de fechas: " + sdf.toPattern() );
		}
		
		Date date = new Date();
		System.out.println("now: " + instance().formatDateTime(date, locale, DateFormat.SHORT, DateFormat.MEDIUM ) );
	}

	public String formatDurationLong( long durationMs )
	{
		String result = null;
		int hours = (int) ( durationMs / 3600000 );
		int minutes = (int) ( ( durationMs % 3600000 ) / 60000 );
		int seconds = (int) ( ( durationMs % 60000 ) / 1000 );
		int ms = (int) ( durationMs % 1000 );

		return( String.format( "%d:%02d:%02d.%03d", hours, minutes, seconds, ms ) );
	}

	public String formatDurationSimplified( long durationMs )
	{
		String result = formatDurationLong( durationMs );
		if( result.startsWith( "0:" ) )
			result = StringFunctions.instance().removeAtStart(result, "0:");

		if( result.endsWith( ".000" ) )
			result = StringFunctions.instance().removeAtEnd(result, ".000" );

		return( result );
	}
}
