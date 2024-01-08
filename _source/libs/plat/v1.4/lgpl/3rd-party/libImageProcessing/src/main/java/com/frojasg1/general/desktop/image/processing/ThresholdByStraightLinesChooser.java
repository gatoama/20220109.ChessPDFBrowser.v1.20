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
package com.frojasg1.general.desktop.image.processing;

import com.frojasg1.general.desktop.image.processing.utils.ImageUtils;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ThresholdByStraightLinesChooser
{
	protected static final int WHITE_GRAY_SCALE_VALUE = 255;

	public Map<Point, Long> processStraightLineBinaryThreshold( BufferedImage image,
				double thresholdToChooseLine, double thresholdToChoosePoints )
	{
		Map<Point, Long> result = new HashMap<>();

		int max = getMax( image );
		int thresholdValue = (int) ( thresholdToChooseLine * max );
		for( int xx = 0; xx < image.getWidth(); xx++ )
		{
			int maxTmp = maxOfColumn( image, xx );
			if( maxTmp > thresholdValue )
				addPointsOfColum( image, xx, (int) (maxTmp * thresholdToChoosePoints), result );
		}

		for( int yy = 0; yy < image.getHeight(); yy++ )
		{
			int maxTmp = maxOfRow( image, yy );
			if( maxTmp > thresholdValue )
				addPointsOfRow( image, yy, (int) (maxTmp * thresholdToChoosePoints), result );
		}

		return( result );
	}

	protected int addPointsOfColum( BufferedImage image, int xx, int thresholdValue,
									Map<Point, Long> result )
	{
		int max = 0;
		for( int yy = 0; yy < image.getHeight(); yy++ )
		{
			int grayScale = getGrayScale( image.getRGB(xx, yy) );
			if( grayScale > thresholdValue )
			{
				Point point = new Point( xx, yy );
				Long prev = result.get( point );
				if( prev == null )
					prev = 0L;
				result.put( point, prev + WHITE_GRAY_SCALE_VALUE );
			}
		}

		return( max );
	}

	protected int maxOfColumn( BufferedImage image, int xx )
	{
		int max = 0;
		for( int yy = 0; yy < image.getHeight(); yy++ )
		{
			int grayScale = getGrayScale( image.getRGB(xx, yy) );
			if( max < grayScale )
				max = grayScale;
		}

		return( max );
	}

	protected int addPointsOfRow( BufferedImage image, int yy, int thresholdValue,
									Map<Point, Long> result )
	{
		int max = 0;
		for( int xx = 0; xx < image.getWidth(); xx++ )
		{
			int grayScale = getGrayScale( image.getRGB(xx, yy) );
			if( grayScale > thresholdValue )
			{
				Point point = new Point( xx, yy );
				Long prev = result.get( point );
				if( prev == null )
					prev = 0L;
				result.put( point, prev + WHITE_GRAY_SCALE_VALUE );
			}
		}

		return( max );
	}

	public int maxOfRow( BufferedImage image, int yy )
	{
		int max = 0;
		for( int xx = 0; xx < image.getWidth(); xx++ )
		{
			int grayScale = getGrayScale( image.getRGB(xx, yy) );
			if( max < grayScale )
				max = grayScale;
		}

		return( max );
	}

	public int getMax( BufferedImage image )
	{
		int max = 0;
		for( int xx = 0; xx < image.getWidth(); xx++ )
			for( int yy = 0; yy < image.getHeight(); yy++ )
			{
				int grayScale = getGrayScale( image.getRGB(xx, yy) );
				if( max < grayScale )
					max = grayScale;
			}

		return( max );
	}

	protected long abs( long value )
	{
		return( ( value > 0 ) ? value : -value );
	}

	protected int sgn( long value )
	{
		return( ( value == 0 ) ? 0 : (int) ( value / abs( value ) ) );
	}

	public int getGrayScale(int rgb)
	{
		return ImageUtils.instance().getGrayScale(rgb);
    }

	protected long getMax( Collection<Long> col )
	{
		long result = 0;
		Optional<Long> optResult = col.stream().max( (lo1, lo2) -> (int) sgn( lo1 - lo2 ) );

		if( optResult.isPresent() )
			result = optResult.get();

		return( result );
	}

	public Map<Point,Long> process( Map<Point,Long> inputMap, long thresholdValue )
	{
		Map<Point, Long> result = new HashMap<>();

		for( Map.Entry<Point, Long> entry: inputMap.entrySet() )
		{
			long value = entry.getValue();
			if( value >= thresholdValue )
				result.put( entry.getKey(), value );
		}

		return( result );
	}

	public Map<Point,Long> process( Map<Point,Long> inputMap, double threshold )
	{
		long thresholdValue = ( long ) ( getMax( inputMap.values() ) * threshold );

		return( process( inputMap, thresholdValue ) );
	}
}
