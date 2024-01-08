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
package com.frojasg1.desktop.liblens.graphics.filters.blur;

import com.frojasg1.desktop.liblens.graphics.filters.FilterInterface;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlurFilter implements FilterInterface
{
	// Gaussian Blur matrix ( https://en.wikipedia.org/wiki/Kernel_(image_processing) )
	protected static final double[][] _filter = {	{ 1.0D/16, 1.0D/8, 1.0D/16 },
													{ 1.0D/8, 1.0D/4, 1.0D/8 },
													{ 1.0D/16, 1.0D/8, 1.0D/16 }
												};

	protected static Integer _startOfFilter = null;

	protected BufferedImage _image = null;
	protected int[] _pixelColors = null;
	protected int _imageWidth = -1;
	protected int _imageHeight = -1;

	protected int _radius = -1;

	protected int _fromX = -1;
	protected int _toX = -1;
	protected int _fromY = -1;
	protected int _toY = -1;

	protected int _squareRadius = -1;

	protected double[] _tmpComponents = new double[4];

	public BlurFilter()
	{
		if( _startOfFilter == null )
			_startOfFilter = calculateStartOfFilter();
	}

	protected int calculateStartOfFilter()
	{
		int length = _filter.length;
		if( IntegerFunctions.isEven( length ) )
			throw( new RuntimeException( "Error in filter. It must be a square matrix, with an odd number of rows" ) );

		for( int ii=0; ii<length; ii++ )
			if( _filter[ii].length != length )
				throw( new RuntimeException( "Error in filter. It must be a square matrix." ) );

		return( - ( length - 1 ) / 2 );
	}

	protected void setRadius( int radius )
	{
		_radius = radius;
		_squareRadius = radius * radius;
	}

	public void setInputData( int radius, int fromX, int toX,
								int fromY, int toY )
	{
		setRadius( radius );

		_fromX = fromX;
		_toX = toX;
		_fromY = fromY;
		_toY = toY;
	}

	protected int applyBlurToPoint( int xx, int yy )
	{
		resetComponents();

		int xxNotLimitedRight = xx - _startOfFilter;
		int xxCoor = IntegerFunctions.min( _imageWidth-1, xxNotLimitedRight );
		int xxNotLimitedLeft = xx + _startOfFilter;
		int xxLeft = IntegerFunctions.max( 0, xxNotLimitedLeft );
		for( int jj=(xxNotLimitedRight - xxCoor); jj<( _filter.length - xxLeft + xxNotLimitedLeft ); jj++, xxCoor-- )
		{
			int yyNotLimitedBottom = yy - _startOfFilter;
			int yyCoor = IntegerFunctions.min( _imageHeight-1, yyNotLimitedBottom );
			int yyNotLimitedTop = yy + _startOfFilter;
			int yyTop = IntegerFunctions.max( 0, yyNotLimitedTop );
			for( int ii=(yyNotLimitedBottom - yyCoor); ii<( _filter[jj].length - yyTop + yyNotLimitedTop ) ; ii++, yyCoor-- )
			{
				addARGB( ImageFunctions.instance().getColorOfPixel( xxCoor, yyCoor, _imageWidth, _pixelColors),
						_filter[ii][jj] );
			}
		}

		return( getARGBOfComponents() );
	}

	@Override
	public int applyFilterToPoint(int xx, int yy)
	{
		int result = 0;

		boolean applyIdentity = ( ( xx<_fromX ) || ( xx > _toX ) ||
									( yy<_fromY ) || ( yy > _toY ) );

		if( !applyIdentity )
		{
			int xxDiff = ( xx - _radius );
			int yyDiff = ( yy - _radius );

			applyIdentity = ( ( xxDiff * xxDiff + yyDiff * yyDiff ) > _squareRadius );
		}

		if( applyIdentity )
			result = ImageFunctions.instance().getColorOfPixel( xx, yy, _imageWidth, _pixelColors );
		else
			result = applyBlurToPoint( xx, yy );

		return( result );
	}

	protected void resetComponents()
	{
		ImageFunctions.instance().resetComponents(_tmpComponents);
	}

	protected void addARGB( int color, double factor )
	{
		ImageFunctions.instance().addARGB( _tmpComponents, color, factor );
	}

	protected int getARGBOfComponents()
	{
		return( ImageFunctions.instance().getARGB(_tmpComponents) );
	}

	@Override
	public BufferedImage applyFilter(BufferedImage image)
	{
		setImage( image );

		BufferedImage result = new BufferedImage( _imageWidth, _imageHeight, BufferedImage.TYPE_INT_ARGB );

		for( int yy=0; yy<_imageHeight; yy++ )
			for( int xx=0; xx<_imageWidth; xx++ )
				result.setRGB( xx, yy, applyFilterToPoint( xx, yy ) );

		return( result );
	}

	@Override
	public BufferedImage getImage()
	{
		return( _image );
	}

	@Override
	public void setImage(BufferedImage image)
	{
		_image = image;
		_pixelColors = ImageFunctions.instance().getRGB(0, 0, image.getWidth(), image.getHeight(), image );
		_imageWidth = image.getWidth();
		_imageHeight = image.getHeight();
	}

}
