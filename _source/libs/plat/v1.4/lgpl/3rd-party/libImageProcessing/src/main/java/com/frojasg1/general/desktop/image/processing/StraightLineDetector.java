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
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class StraightLineDetector
{
	protected static final int WHITE_GRAY_SCALE_VALUE = 255;

	protected List<Integer> _yCoorOfHorizontalLines = null;
	protected List<Integer> _xCoorOfVerticalLines = null;

	protected void init()
	{
		_yCoorOfHorizontalLines = new ArrayList<>();
		_xCoorOfVerticalLines = new ArrayList<>();
	}

	public List<Integer> getyCoorOfHorizontalLines()
	{
		return _yCoorOfHorizontalLines;
	}

	public List<Integer> getxCoorOfVerticalLines()
	{
		return _xCoorOfVerticalLines;
	}

	public Map<Point, Long> getVertexCandidates()
	{
		Map<Point, Long> result = new HashMap<>();

		Long value = 1L;
		for( Integer xx: _xCoorOfVerticalLines )
			for( Integer yy: _yCoorOfHorizontalLines )
				result.put( new Point( xx, yy ), value );

		return( result );
	}


	public BufferedImage process( BufferedImage image, double thresholdToCheckPeak,
									double thresholdIncrementToDetectPeak,
									double minimumThresholdIncrementToDetectPeak,
									double toleranceToMaintainPeak )
	{
		init();

		BufferedImage result = new BufferedImage( image.getWidth(), image.getHeight(),
			BufferedImage.TYPE_INT_ARGB );
//		paint( result, 0xFF000000 );

		List<Long> integrations = getColumnIntegrations( image );
		long thresholdToCheckPeakValue = calculateThresholdToCheckPeak(integrations, thresholdToCheckPeak );
		long prev = 0;
		boolean on = false;
		int xx = 0;
		for( Long curr: integrations )
		{
			double diffProp =  getDiffProportion( curr, prev );
			if( !on )
				on = ( ( curr > prev ) && ( curr >= thresholdToCheckPeakValue ) &&
						( ( diffProp > thresholdIncrementToDetectPeak ) ||
							( diffProp > minimumThresholdIncrementToDetectPeak ) &&
							( isMaximum(integrations, xx, toleranceToMaintainPeak ) )
					  )
					);
			else if( curr < prev )
				on = ( diffProp < toleranceToMaintainPeak );

			if( on )
			{
//				paintColumnGrey( xx, WHITE_GRAY_SCALE_VALUE, result );
				paintColumn( xx, Color.GREEN.getRGB(), result );
				_xCoorOfVerticalLines.add( xx );
			}

			prev = curr;
			xx++;
		}

		integrations = getRowIntegrations( image );
		thresholdToCheckPeakValue = calculateThresholdToCheckPeak(integrations, thresholdToCheckPeak );
		prev = 0;
		on = false;
		int yy = 0;
		for( Long curr: integrations )
		{
			double diffProp =  getDiffProportion( curr, prev );
			if( !on )
				on = ( ( curr > prev ) && ( curr >= thresholdToCheckPeakValue ) &&
						( ( diffProp > thresholdIncrementToDetectPeak ) ||
							( diffProp > minimumThresholdIncrementToDetectPeak ) &&
							( isMaximum(integrations, yy, toleranceToMaintainPeak ) )
					  )
					);
			else if( curr < prev )
				on = ( diffProp < toleranceToMaintainPeak );

			if( on )
			{
//				paintRowGrey( yy, WHITE_GRAY_SCALE_VALUE, result );
				paintRow( yy, Color.GREEN.getRGB(), result );
				_yCoorOfHorizontalLines.add( yy );
			}

			prev = curr;
			yy++;
		}

		return( result );
	}

	protected boolean isMaximum( List<Long> integrations, int index, double torelanceToMaintainPeak )
	{
		boolean result = false;

		long orig = integrations.get(index);
		double diffProp = 1.0D;
		long prev = 0;
		while( !result && ( integrations.size() > index ) )
		{
			long curr = integrations.get( index );
			if( prev > 0 )
			{
				diffProp = getDiffProportion( curr, prev );
				if( curr < orig )
					result = diffProp > torelanceToMaintainPeak;
				else if( diffProp > torelanceToMaintainPeak )
					break;
			}

			prev = curr;
			index++;
		}

		return( result );
	}

	protected long calculateThresholdToCheckPeak( List<Long> integrations,
				double thresholdToCheckPeak )
	{
		long result = 0;
		Optional<Long> opt = integrations.stream().max( (v1, v2) -> (int) sgn( v1 - v2 ) );
		if( opt.isPresent() )
			result = (long) ( opt.get() * thresholdToCheckPeak );

		return( result );
	}

	protected void paint( BufferedImage image, int argb )
	{
		for( int xx=0; xx<image.getWidth(); xx++ )
			for( int yy=0; yy<image.getHeight(); yy++ )
				image.setRGB( xx, yy, argb );
	}


	protected List<Long> getColumnIntegrations( BufferedImage image )
	{
		List<Long> result = new ArrayList<>();
		for( int xx = 0; xx < image.getWidth(); xx++ )
		{
			long curr = integrationOfColumn( image, xx );
			result.add( curr );
		}

		return( result );
	}

	protected List<Long> getRowIntegrations( BufferedImage image )
	{
		List<Long> result = new ArrayList<>();
		for( int yy = 0; yy < image.getHeight(); yy++ )
		{
			long curr = integrationOfRow( image, yy );
			result.add( curr );
		}

		return( result );
	}

	protected double getDiffProportion( long value1, long value2 )
	{
		double result = 0;
		if( ( value1 > 0 ) || ( value2 > 0 ) )
		{
			result = ( (double) abs( value1 - value2 ) ) / max( value1, value2 );
		}

		return( result );
	}

	protected long integrationOfColumn( BufferedImage image, int xx )
	{
		long result = 0;
		for( int yy = 0; yy < image.getHeight(); yy++ )
		{
			int grayScale = getGrayScale( image.getRGB(xx, yy) );

			result += grayScale;
		}

		return( result );
	}

	protected void paintColumnGrey( int xx, int greyScale, BufferedImage result )
	{
		greyScale = 0xff000000 | (greyScale << 16) | (greyScale << 8) | greyScale;
		paintColumn( xx, greyScale, result );
	}

	protected void paintColumn( int xx, int argb, BufferedImage result )
	{
		for( int yy = 0; yy < result.getHeight(); yy++ )
			result.setRGB(xx, yy, argb);
	}

	protected void paintRowGrey( int yy, int greyScale, BufferedImage result )
	{
		greyScale = 0xff000000 | (greyScale << 16) | (greyScale << 8) | greyScale;
		paintRow( yy, greyScale, result );
	}

	protected void paintRow( int yy, int argb, BufferedImage result )
	{
		for( int xx = 0; xx < result.getWidth(); xx++ )
			result.setRGB(xx, yy, argb);
	}

	protected long max( long v1, long v2 )
	{
		return( v1 > v2 ? v1 : v2 );
	}

	protected long abs( long v1 )
	{
		return( v1 > 0 ? v1 : -v1 );
	}

	protected long sgn( long v1 )
	{
		return( v1 == 0 ? 0 : v1 / abs(v1) );
	}

	public long integrationOfRow( BufferedImage image, int yy )
	{
		long result = 0;
		for( int xx = 0; xx < image.getWidth(); xx++ )
		{
			int grayScale = getGrayScale( image.getRGB(xx, yy) );

			result += grayScale;
		}

		return( result );
	}

    public int getGrayScale(int rgb) {
        return ImageUtils.instance().getGrayScale(rgb);
    }
}
