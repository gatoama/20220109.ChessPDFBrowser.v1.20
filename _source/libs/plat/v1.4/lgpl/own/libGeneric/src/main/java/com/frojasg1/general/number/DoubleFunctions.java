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

import com.frojasg1.general.ExecutionFunctions;
import static com.frojasg1.general.number.IntegerFunctions.max;
import static com.frojasg1.general.number.IntegerFunctions.min;
import com.frojasg1.general.string.StringFunctions;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DoubleFunctions
{
	
	protected static DoubleFunctions _instance;

	protected NumberFormat _formatter = null;

	protected Map<Locale, NumberFormat> _formatterMap = new ConcurrentHashMap<>();

	public static void changeInstance( DoubleFunctions inst )
	{
		_instance = inst;
	}

	public static DoubleFunctions instance()
	{
		if( _instance == null )
			_instance = new DoubleFunctions();
		return( _instance );
	}

	public DoubleFunctions()
	{
		_formatter = createNumberFormat();
	}

	protected NumberFormat createNumberFormat()
	{
		return( new DecimalFormat("#0.000000") );
	}

	public String format( Double value )
	{
		String result = "null";
		if( value != null )
			result = _formatter.format(value);
		return( result );
	}

	public Double min( Double d1, Double d2 )
	{
		Double result = d1;
		if( ( d1 == null ) || ( d2 != null ) && ( d2 < d1 ) )
			result = d2;

		return( result );
	}

	public Double max( Double d1, Double d2 )
	{
		Double result = d1;
		if( ( d1 == null ) || ( d2 != null ) && ( d2 > d1 ) )
			result = d2;

		return( result );
	}

	public int sgn( double dd )
	{
		return( (dd==0) ? 0 : ( (dd>0) ? 1 : -1 ) );
	}

	public double abs( double dd )
	{
		return( (dd==0) ? 0 : ( (dd>0) ? dd : -dd ) );
	}

	public double limit( double value, double lowerBound, double upperBound )
	{
		return( max( lowerBound, min( upperBound, value ) ) );
	}

	public Double parseDouble( String str )
	{
		Double result = null;
		if( str != null )
		{
			String str2 = StringFunctions.instance().replaceSetOfChars(str, ",", "." );
			result = ExecutionFunctions.instance().safeFunctionExecution( () -> Double.parseDouble( str2 ) );
		}
		return( result );
	}

	protected NumberFormat getNumberFormat( Locale locale )
	{
		NumberFormat result = _formatterMap.get(locale);
		if( result == null )
		{
			result = createNumberFormat(locale);
			_formatterMap.put(locale, result);
		}
		return( result );
	}

	protected NumberFormat createNumberFormat( Locale locale )
	{
		NumberFormat result = NumberFormat.getNumberInstance(locale);
		DecimalFormat df = (DecimalFormat)result;
		df.applyPattern("###,###.00");

		return( result );
	}

	// https://stackoverflow.com/questions/10411414/how-to-format-double-value-for-a-given-locale-and-number-of-decimal-places/10416264
	public String formatDoubleLocale( double value, Locale locale )
	{
		NumberFormat nf = getNumberFormat(locale);
		DecimalFormat df = (DecimalFormat)nf;
		String result = df.format(value);

		return( result );
	}
}
