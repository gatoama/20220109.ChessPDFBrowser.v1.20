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
package com.frojasg1.general.number;

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.HexadecimalFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Usuario
 */
public class IntegerFunctions
{
	public static final String GLOBAL_CONF_FILE_NAME = "IntegerFunctions.properties";

	public static final String CONF_ERROR = "ERROR";
	public static final String CONF_NUMBER_OUT_OF_RANGE = "NUMBER_OUT_OF_RANGE";
	public static final String CONF_BAD_RANGE_SIZE = "BAD_RANGE_SIZE";
	public static final String CONF_BAD_LOW_BOUND = "BAD_LOW_BOUND";
	public static final String CONF_BAD_UPPER_BOUND = "BAD_UPPER_BOUND";


	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_ERROR, "Error" );
		registerInternationalString(CONF_NUMBER_OUT_OF_RANGE, "number out of range" );
		registerInternationalString(CONF_BAD_RANGE_SIZE, "bad range size" );
		registerInternationalString(CONF_BAD_LOW_BOUND, "bad low bound" );
		registerInternationalString(CONF_BAD_UPPER_BOUND, "bad upper bound" );
	}

	public static int max( int i1, int i2 )
	{
		return( i1>i2 ? i1 : i2 );
	}

	public static int min( int i1, int i2 )
	{
		return( i1<i2 ? i1 : i2 );
	}

	public static int limit( int value, int lowerBound, int upperBound )
	{
		return( max( lowerBound, min( upperBound, value ) ) );
	}

	public static int abs( int ii )
	{
		return( ii>=0 ? ii : -ii );
	}

	public static int sgn( int ii )
	{
		return( ii>0 ? 1 : ( ii<0 ? -1 : 0 ) );
	}

	public static long max( long i1, long i2 )
	{
		return( i1>i2 ? i1 : i2 );
	}

	public static long min( long i1, long i2 )
	{
		return( i1<i2 ? i1 : i2 );
	}

	public static long limit( long value, long lowerBound, long upperBound )
	{
		return( max( lowerBound, min( upperBound, value ) ) );
	}

	public static long abs( long ii )
	{
		return( ii>=0 ? ii : -ii );
	}

	public static long sgn( long ii )
	{
		return( ii>0 ? 1 : ( ii<0 ? -1 : 0 ) );
	}
	
	public static Integer parseInt( String str )
	{
		Integer result = null;

		try
		{
			result = Integer.parseInt(str);
		}
		catch( Throwable th )
		{}

		return( result );
	}
	
	public static boolean isPresentInArray( int value, int[] array )
	{
		boolean result = false;
		
		if( array != null )
		{
			for( int ii=0; (ii<array.length) && !result; ii++ )
				result = (array[ii] == value);
		}
		
		return( result );
	}

	public static int get_1_to_256_ValueOfByte( byte value )
	{
		int result = value;

		if( result < 1 )
			result = result + 256;

		return( result );
	}

	public static byte getByteValueFor_1_to_256( int value )
	{
		if( ( value < 1 ) || ( value > 256 ) )
			throw( new RuntimeException( String.format( "%s, %s: %d",
						getInternationalString( CONF_ERROR ),
						getInternationalString( CONF_NUMBER_OUT_OF_RANGE ),
						value
														)
										)
				);

		byte result = (byte) value;
		
		if( value > 127 )
			result = (byte) ( value - 256 );
		
		return( result );
	}

	public static byte getByteValueFor_0_to_255( int value )
	{
		boolean checkBounds = false;
		byte result = getByteValueForCustomLimits( 0, 255, 128, value, checkBounds );
		
		return( result );
	}

	public static boolean isByteRange( int value )
	{
		return( ( value >= -128 ) && ( value < 128 ) );
	}

	protected static void checkBoundsForByteLimits( int lowBound, int upperBound, int valueForZero )
	{
		int range = upperBound - lowBound;
		if( ( range < 1 ) || ( range > 256 ) )
			throw( new RuntimeException( String.format( "%s, %s",
						getInternationalString( CONF_ERROR ),
						getInternationalString( CONF_BAD_RANGE_SIZE )
														)
										)
				);

		int leastValue = lowBound - valueForZero;
		if( !isByteRange( leastValue ) )
			throw( new RuntimeException( String.format( "%s, %s: %d",
						getInternationalString( CONF_ERROR ),
						getInternationalString( CONF_BAD_LOW_BOUND ),
						leastValue
														)
										)
				);

		int maxValue = upperBound - valueForZero;
		if( !isByteRange( maxValue ) )
			throw( new RuntimeException( "Error, bad upper bound: " + maxValue ) );
			throw( new RuntimeException( String.format( "%s, %s: %d",
						getInternationalString( CONF_ERROR ),
						getInternationalString( CONF_BAD_UPPER_BOUND ),
						maxValue
														)
										)
				);
	}

	public static byte getByteValueForCustomLimits( int lowBound, int upperBound, int valueForZero, int value, boolean checkBounds )
	{
		if( checkBounds )
			checkBoundsForByteLimits( lowBound, upperBound, valueForZero );

		if( ( value < lowBound ) || ( value > upperBound ) )
			throw( new RuntimeException( String.format( "%s, %s: %d",
						getInternationalString( CONF_ERROR ),
						getInternationalString( CONF_NUMBER_OUT_OF_RANGE ),
						value
														)
										)
				);

		byte result = (byte) ( value - valueForZero );

		return( result );
	}

	public static int getIntValueOfByteForCustomLimits( int lowBound, int upperBound, int valueForZero, byte value, boolean checkBounds )
	{
		if( checkBounds )
			checkBoundsForByteLimits( lowBound, upperBound, valueForZero );

		int result = value + valueForZero;

		return( result );
	}

	public static int get_0_to_255_valueOfByte( byte value )
	{
		boolean checkBounds = false;
		int result = getIntValueOfByteForCustomLimits( 0, 255, 128, value, checkBounds );

		return( result );
	}

	public static int roundToInt( double value )
	{
		return( ( new Double( Math.round( value ) ) ).intValue() );
	}

	public static String formatNumber( long number, Locale locale )
	{
		if( locale == null )
			locale = Locale.US;
		
		NumberFormat nf = NumberFormat.getInstance( locale );
		String result = nf.format(number);

		return( result );
	}

	public static Long parseLong( String str )
	{
		Long result = null;

		try
		{
			result = Long.parseLong(str);
		}
		catch( Exception ex )
		{}

		return( result );
	}

	public static Long parseLong( String str, Locale locale )
	{
		Long result = null;

		if( str != null )
		{
			if( locale == null )
				result = parseLong( str );
			else
			{
				try
				{
					if( StringFunctions.instance().indexOfAnyCharDistinctFrom(str, "0123456789,.", 0) == -1 )
					{
						NumberFormat nf = NumberFormat.getInstance( locale );
						result = nf.parse(str).longValue();
					}
				}
				catch( Exception ex )
				{
					result = parseLong( str );
				}
			}
		}

		return( result );
	}

	public static int round( double value )
	{
		return( (int) Math.round( value ) );
	}

	public static Integer getIntegerFromHex( String hex )
	{
		Integer result = null;
		if( hex != null )
		{
			int resInt = 0;
			try
			{
				for( int shift = 0, ii=hex.length(); ii>0; ii--, shift += 4 )
				{
					resInt = resInt | ( HexadecimalFunctions.instance().M_convertHexDigitInValue(hex.charAt(ii-1) ) << shift );
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}

			result = resInt;
		}

		return( result );
	}

	public static String formatNumber( String numberStr, Locale locale )
	{
		String result = "";

		Long number = parseLong( numberStr, locale );
		if( number != null )
			result = formatNumber( number, locale );

		return( result );
	}

	public static int zoomValueCeil( double value, double zoomFactor )
	{
		Double result = Math.ceil( zoomFactor * value );

		return( result.intValue() );
	}

	public static int zoomValueFloor( double value, double zoomFactor )
	{
		Double result = Math.floor( zoomFactor * value );

		return( result.intValue() );
	}

	public static int zoomValueInt( double value, double zoomFactor )
	{
		Double result = zoomFactor * value;

		return( result.intValue() );
	}

	public static int zoomValueRound( int value, double zoomFactor )
	{
		double result = Math.round( zoomFactor * value );
		if( result > Integer.MAX_VALUE )
			result = Integer.MAX_VALUE;

		return( (int) result );
	}

	public static boolean isEven( int value )
	{
		return( ( value % 2 == 0 ) );
	}

	public static boolean isOdd( int value )
	{
		return( !isEven( value ) );
	}

	public static boolean match( int i1, int i2, int tolerance )
	{
		return( abs( i1 - i2 ) <= tolerance );
	}

	protected static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	protected static String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}
}
