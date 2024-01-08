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
package com.frojasg1.generic.zoom;

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomFactorsAvailable
{
	public static final String GLOBAL_CONF_FILE_NAME = "ZoomFactorsAvailable.properties";

	public static final String CONF_BAD_FORMAT_FOR_PERCENTAGE_STRING = "BAD_FORMAT_FOR_PERCENTAGE_STRING";
	public static final String CONF_PERCENTAGE_STRING_PARAMETER_WAS_EMPTY = "PERCENTAGE_STRING_PARAMETER_WAS_EMPTY";

	protected static final String[] _initialPercentages = { "75%", "100%", "133%", "175%", "250%" };
	protected static ZoomFactorsAvailable _instance = null;

	protected ArrayList< Double > _colOfZoomFactors = new ArrayList<>();

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

	public static void setInstance( ZoomFactorsAvailable instance )
	{
		_instance = instance;
	}

	public static ZoomFactorsAvailable instance()
	{
		if( _instance == null )
			_instance = new ZoomFactorsAvailable();

		return( _instance );
	}

	protected ZoomFactorsAvailable()
	{
		initialize_final( _initialPercentages );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_BAD_FORMAT_FOR_PERCENTAGE_STRING, "Bad format for percentage string" );
		registerInternationalString(CONF_PERCENTAGE_STRING_PARAMETER_WAS_EMPTY, "Percentage string parameter was empty" );
	}

	protected final void initialize_final( String[] array )
	{
		for( int ii=0; ii<array.length; ii++ )
			addZoomFactorPercentaje( array[ii] );
	}

	public void addZoomFactorPercentage( int newZoomFactorPercentage )
	{
		if( newZoomFactorPercentage > 0 )
			addZoomFactorPercentaje( "" + newZoomFactorPercentage + "%" );
	}

	/**
	 * 
	 * @param percentageStr		parameter in format:  xxx%, as example: 200%
	 * @return					The value returned is directly the factor , in the case of 200%, would be result=2.0D
	 */
	public double getZoomFactor( String percentageStr )
	{
		double result = 1.0D;

		if( ( percentageStr != null ) &&
			( percentageStr.length() > 0 ) )
		{
			if( percentageStr.charAt( percentageStr.length() - 1 ) == '%' )
			{
				String numberStr = percentageStr.substring( 0, percentageStr.length() - 1 );
				Integer number = IntegerFunctions.parseInt( numberStr );
				if( number == null )
					throw( new RuntimeException( String.format( "%s: %s",
							getInternationalString(CONF_BAD_FORMAT_FOR_PERCENTAGE_STRING),
							percentageStr
																)
												)
						);

				result = ( (double) number ) / 100;
			}
			else
				throw( new RuntimeException( String.format( "%s: %s",
						getInternationalString(CONF_BAD_FORMAT_FOR_PERCENTAGE_STRING),
						percentageStr
															)
											)
					);
		}
		else
			throw( new RuntimeException( getInternationalString(CONF_PERCENTAGE_STRING_PARAMETER_WAS_EMPTY) ) );

		return( result );
	}


	/**
	 * 
	 * @param percentageStr the format of this parameter is xxx% where xxx is an integer number.
	 *						it is a percentaje, so to zoom x2 this parameter must be set to 200%
	 */
	public void addZoomFactorPercentaje( String percentageStr )
	{
		try
		{
			double newZoomFactorPercentage = getZoomFactor( percentageStr );

			if( newZoomFactorPercentage > 0 )
				addZoomFactor( newZoomFactorPercentage );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void addZoomFactor( double newZoomFactorPercentage )
	{
		int size = _colOfZoomFactors.size();

		int ii = 0;
		double value = -1;
		for( ; (value<0) && ( ii<size ); ii++ )
		{
			value = _colOfZoomFactors.get(ii) - newZoomFactorPercentage;
		}

		if( value < 0 )
			_colOfZoomFactors.add( newZoomFactorPercentage );
		else if( value > 0 )
			_colOfZoomFactors.add( ii, newZoomFactorPercentage );
	}

	public String getPercentageStr( Double zoomFactor )
	{
		String result = null;
		if( zoomFactor != null )
		{
			result = "" + IntegerFunctions.roundToInt( zoomFactor * 100 ) + "%";
		}
		return( result );
	}

	public Collection< String > getZoomFactorsAvailable()
	{
		ArrayList< String > result = new ArrayList<>();
		
		Iterator< Double > it = _colOfZoomFactors.iterator();
		while( it.hasNext() )
		{
			result.add( getPercentageStr( it.next() ) );
		}
		return( result );
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
