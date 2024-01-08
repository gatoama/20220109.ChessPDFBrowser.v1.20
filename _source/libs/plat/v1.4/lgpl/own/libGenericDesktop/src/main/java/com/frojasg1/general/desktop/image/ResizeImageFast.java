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
package com.frojasg1.general.desktop.image;

import java.awt.image.BufferedImage;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ResizeImageFast implements ResizeImageInterface
{
	protected static ResizeImageFast _instance = null;
	
	public static ResizeImageFast instance()
	{
		if( _instance == null )
			_instance = new ResizeImageFast();

		return( _instance );
	}

	public BufferedImage resizeImage( BufferedImage originalImage, int newWidth,
										int newHeight )
	{
		return( resizeImage( originalImage, newWidth, newHeight, null, null, null ) );
	}

	public BufferedImage resizeImage( BufferedImage original, int newWidth, int newHeight, Integer switchColorFrom,
											Integer switchColorTo, Integer alphaForPixelsDifferentFromColorFrom ) throws IllegalArgumentException
	{
		return( resizeImage( original, newWidth, newHeight,
							col -> ImageUtilFunctions.instance().getPixelValue( col, switchColorFrom,
									switchColorTo,
									alphaForPixelsDifferentFromColorFrom ) ) );
	}

	public BufferedImage resizeImage( BufferedImage original, int newWidth, int newHeight,
										Function<Integer, Integer> colorTranslator ) throws IllegalArgumentException
	{
		if( ( newWidth < 1 ) || ( newHeight < 1 ) )		throw( new IllegalArgumentException( "Bad size for image.   Width: " + newWidth + ". Height: " + newHeight ) );

		BufferedImage result = new BufferedImage( newWidth, newHeight, BufferedImage.TYPE_INT_ARGB );

		double factorX = ((double) original.getWidth()) / newWidth ;
		double factorY = ( (double) original.getHeight() ) / newHeight;

		int[] pixels = ImageFunctions.instance().getRGB( 0, 0, original.getWidth(), original.getHeight(), original );

		double transformedY = 0.5d;
		for( int tY = 0; tY < newHeight; transformedY += 1, tY++ )
		{
			int originalOffsetY = (int) ( Math.floor(transformedY*factorY) ) * original.getWidth();
			double transformedX = 0.5D;
			for( int tX = 0; tX < newWidth; transformedX += 1, tX++ )
			{
				int originalX = (int) ( Math.floor( transformedX*factorX ) );

				int pixelColor = getPixelValue(pixels,
										originalOffsetY+originalX, colorTranslator);

				result.setRGB( tX, tY, pixelColor );
			}
		}

		return( result );
	}

	protected int getPixelValue( int[] pixels, int index,
									Function<Integer, Integer> colorTranslator )
	{
		int inputColor = pixels[index];
		int result = inputColor;
		if( colorTranslator != null )
			result = colorTranslator.apply(inputColor);
		return( result );
	}
}
