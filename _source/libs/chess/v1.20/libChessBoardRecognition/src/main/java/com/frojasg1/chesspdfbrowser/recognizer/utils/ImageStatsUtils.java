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
package com.frojasg1.chesspdfbrowser.recognizer.utils;

import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.desktop.image.pixel.impl.PixelStats;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ImageStatsUtils
{
	protected static ImageStatsUtils _instance = null;

	public static ImageStatsUtils instance()
	{
		if( _instance == null )
			_instance = new ImageStatsUtils();

		return( _instance );
	}

	public PixelStats calculateAverage( PixelComponents[][] pixels, int borderToSkipThick )
	{
		PixelStats result = null;
		if( checkPixels( pixels, borderToSkipThick ) )
		{
			long[] addition = new long[3];
			for( int jj=0; jj<pixels.length; jj++ )
				for( int ii=0; ii<pixels[0].length; ii++ )
					for( int cc=0; cc<3; cc++ )
						addition[cc] += pixels[jj][ii].getComponent(cc + PixelComponents.RED);

			int count = pixels.length * pixels[0].length;

			result = new PixelStats(0.0d,
									getComponentAverage(addition[0], count ),
									getComponentAverage( addition[1], count ),
									getComponentAverage( addition[2], count ),
									pixels[0][0].isSigned() );
		}

		return( result );
	}

	protected double getComponentAverage( long compAddition, int count )
	{
		return( ((double) compAddition) / count );
	}

	protected double getComponentStandardDeviation( long compAddition, int count )
	{
		return( Math.sqrt( ( (double) compAddition) / count ) );
	}

	public PixelStats calculateStandardDeviation( PixelComponents[][] pixels, int borderToSkipThick )
	{
		PixelStats average = calculateAverage( pixels, borderToSkipThick );

		PixelStats result = null;
		if( checkPixels( pixels, borderToSkipThick ) )
		{
			long[] addition = new long[3];
			for( int jj=0; jj<pixels.length; jj++ )
				for( int ii=0; ii<pixels[0].length; ii++ )
					for( int cc=0; cc<3; cc++ )
					{
						int compIndex = cc + PixelComponents.RED;
						short diffCompVal = (short) ( pixels[jj][ii].getComponent(compIndex) - average.getComponent(compIndex) );
						addition[cc] += diffCompVal * diffCompVal;
					}

			int count = pixels.length * pixels[0].length;

			result = new PixelStats(0.0d,
									getComponentStandardDeviation( addition[0], count ),
									getComponentStandardDeviation( addition[1], count ),
									getComponentStandardDeviation( addition[2], count ),
									pixels[0][0].isSigned() );
		}

		return( result );
	}

	protected boolean checkPixels( PixelComponents[][] pixels, int borderToSkipThick )
	{
		return( ( pixels != null ) && ( pixels.length > 0 ) && (pixels[0].length > 0 ) &&
				( borderToSkipThick >= 0 ) &&
				( borderToSkipThick < pixels.length / 2 ) &&
				( borderToSkipThick < pixels[0].length / 2 ) );
	}
}
