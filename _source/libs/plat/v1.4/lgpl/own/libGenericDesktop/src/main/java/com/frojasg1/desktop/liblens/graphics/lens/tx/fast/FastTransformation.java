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
package com.frojasg1.desktop.liblens.graphics.lens.tx.fast;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import com.frojasg1.desktop.liblens.graphics.lens.tx.LensTransformation;
import com.frojasg1.desktop.liblens.graphics.lens.tx.RadiusTransformationInter;
import com.frojasg1.desktop.liblens.graphics.lens.util.LensUtils;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FastTransformation implements LensTransformation
{
	protected Coordinate2D[][] _transformationArray = null;

	protected int _radius = -1;

	@Override
	public void init(RadiusTransformationInter transformation, int radius)
	{
		if( radius > 0 )
		{
			_radius = radius;

			int arrayLength = 2 * radius + 1;

			_transformationArray = new Coordinate2D[arrayLength][arrayLength];

			_transformationArray[_radius][_radius] = new Coordinate2D( _radius, _radius );

			for( int xx=1; xx<=radius; xx++ )
				for( int yy=0; yy<=radius; yy++ )
				{
					Coordinate2D transformedCoor = transformation.calculateTransformation( xx, yy, _radius );

					int transformed_X = transformedCoor.M_getX();
					int transformed_Y = transformedCoor.M_getY();

					if( yy>0 )
					{
						_transformationArray[_radius + xx][_radius + yy] = new Coordinate2D( _radius + transformed_X, _radius + transformed_Y );
						_transformationArray[_radius - xx][_radius + yy] = new Coordinate2D( _radius - transformed_X, _radius + transformed_Y );
						_transformationArray[_radius + xx][_radius - yy] = new Coordinate2D( _radius + transformed_X, _radius - transformed_Y );
						_transformationArray[_radius - xx][_radius - yy] = new Coordinate2D( _radius - transformed_X, _radius - transformed_Y );
					}
					else if( yy==0 )
					{
						_transformationArray[_radius + xx][_radius + yy] = new Coordinate2D( _radius + transformed_X, _radius + transformed_Y );
						_transformationArray[_radius - xx][_radius + yy] = new Coordinate2D( _radius - transformed_X, _radius + transformed_Y );
						_transformationArray[_radius + yy][_radius + xx] = new Coordinate2D( _radius + transformed_Y, _radius + transformed_X );
						_transformationArray[_radius + yy][_radius - xx] = new Coordinate2D( _radius + transformed_Y, _radius - transformed_X );
					}
				}
		}
	}

	public void M_getTransformation( int xx, int yy, Coordinate2D outputCoor )
	{
		if( (xx >= 0) && (xx < _transformationArray.length) &&
			(yy >= 0) && (yy < _transformationArray.length) )
		{
			outputCoor.copy(_transformationArray[ xx ][ yy ] );
		}
		else
		{
			outputCoor.M_setX(xx);
			outputCoor.M_setY(yy);
		}
	}

	@Override
	public void transform(int fromX, int toX, int fromY, int toY, int lensX, int lensY, int[] pixelColors, BufferedImage resultImage)
	{
		Coordinate2D transformedCoor = new Coordinate2D( -1, -1 );
		for( int yy=fromY; yy<=toY; yy++ )
			for( int xx=fromX; xx<=toX; xx++ )
			{
				M_getTransformation( xx, yy, transformedCoor );
				int xx_original = lensX - _radius + transformedCoor.M_getX();
				int yy_original = lensY - _radius + transformedCoor.M_getY();
/*
				if( xx_original<0 ) xx_original=0;
				else if( xx_original>=bi.getWidth() ) xx_original=bi.getWidth()-1;

				if( yy_original<0 ) yy_original=0;
				else if( yy_original>=bi.getHeight() ) yy_original=bi.getHeight()-1;
*/
				resultImage.setRGB( xx, yy, LensUtils.getTransformedPixel( xx_original, yy_original,
																lensX, lensY, _radius,
																pixelColors ) );
			}
	}
	
}
