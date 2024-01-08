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
public class Normalizer
{
	public BufferedImage process( BufferedImage image )
	{
		BufferedImage result = new BufferedImage( image.getWidth(), image.getHeight(),
			BufferedImage.TYPE_INT_ARGB );

		int max = getMax( image );
		for( int xx=0; xx<image.getWidth(); xx++ )
			for( int yy=0; yy<image.getHeight(); yy++ )
			{
				int greyScale = getGrayScale( image.getRGB( xx, yy ) );
				if( max > 0 )
					greyScale = ( 255 * greyScale ) / max;
                greyScale = 0xff000000 | (greyScale << 16) | (greyScale << 8) | greyScale;

                result.setRGB(xx, yy, greyScale);
			}

		return( result );
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

	public int getGrayScale(int rgb)
	{
		return ImageUtils.instance().getGrayScale(rgb);
    }
}
