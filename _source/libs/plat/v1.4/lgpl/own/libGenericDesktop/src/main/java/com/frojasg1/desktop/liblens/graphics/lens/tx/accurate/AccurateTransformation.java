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
package com.frojasg1.desktop.liblens.graphics.lens.tx.accurate;

import com.frojasg1.desktop.liblens.graphics.lens.tx.LensTransformation;
import com.frojasg1.desktop.liblens.graphics.lens.tx.RadiusTransformationInter;
import com.frojasg1.desktop.liblens.graphics.lens.util.LensUtils;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AccurateTransformation implements LensTransformation
{
	protected static final int NUMBER_OF_SAMPLES_1D = 10;

	protected AccuratePixelTransformation[][] _transformationArray = null;

	protected int _radius = -1;
	protected int _numberOfSammples1D = NUMBER_OF_SAMPLES_1D;


	public void setNumberOfSamples1D( int value )
	{
		_numberOfSammples1D = value;
	}
	
	public int getNumberOfSamples1D()
	{
		return( _numberOfSammples1D );
	}

	@Override
	public void init( RadiusTransformationInter transformation, int radius )
	{
		if( radius > 0 )
		{
			_radius = radius;

			int arrayLength = 2 * radius + 1;

			_transformationArray = new AccuratePixelTransformation[arrayLength][arrayLength];

			for( int xx=-radius; xx<=radius; xx++ )
				for( int yy=-radius; yy<=radius; yy++ )
				{
					AccuratePixelTransformation apt = new AccuratePixelTransformation( xx, yy );
					apt.init( transformation, _radius, _numberOfSammples1D );

					_transformationArray[xx+radius][yy+radius] = apt;
				}
		}
	}

	protected void checkBounds( int value )
	{
		if( ( value < 0 ) || ( value >= _transformationArray.length ) )
			throw( new IllegalArgumentException( "Value out of bounds (value=" + value + ") (radius=" + _radius + ")" ) );
	}

	protected void checkBounds( int fromX, int toX, int fromY, int toY )
	{
		checkBounds( fromX );
		checkBounds( toX );
		checkBounds( fromY );
		checkBounds( toY );
	}

	@Override
	public void transform( int fromX, int toX, int fromY, int toY,
							int lensX, int lensY, int[] pixelColors,
							BufferedImage resultImage )
	{
		checkBounds( fromX, toX, fromY, toY );

		for( int yy=fromY; yy<=toY; yy++ )
			for( int xx=fromX; xx<=toX; xx++ )
			{
				int color = _transformationArray[xx][yy].getTransformedPixel(lensX, lensY, _radius, pixelColors);

				resultImage.setRGB( xx, yy, color );
			}
	}
}
