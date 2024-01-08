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

import com.frojasg1.general.number.IntegerFunctions;
import java.util.function.Function;

/**
 *
 * @author Usuario
 */
public class ImageUtilFunctions
{
	protected static ImageUtilFunctions _instance = null;

	public static void changeInstance( ImageUtilFunctions inst )
	{
		_instance = inst;
	}

	public static ImageUtilFunctions instance()
	{
		if( _instance == null )
			_instance = new ImageUtilFunctions();

		return( _instance );
	}

	public int getPixelValue( int[] pixels, int index,
								Integer switchColorFrom,
								Integer switchColorTo,
								Integer alphaForPixelsDifferentFromColorFrom )
	{
		return( getPixelValue( pixels[index], switchColorFrom, switchColorTo,
								alphaForPixelsDifferentFromColorFrom) );
	}

	public int translateSimpleComponentsOfPixelARGB( int inPixelValue,
								Function<Integer, Integer> componentTranslator )
	{
		int rr = ( inPixelValue & 0xff0000 ) >> 16;
		int gg = ( inPixelValue & 0xff00 ) >> 8;
		int bb = ( inPixelValue & 0xff );

		return( ( inPixelValue & 0xff000000 ) + (componentTranslator.apply(rr) << 16) +
				(componentTranslator.apply(gg) << 8) + componentTranslator.apply(bb) );
	}

	public int putOutColor( int inPixelValue, double percentage )
	{
		return( translateSimpleComponentsOfPixelARGB(inPixelValue, comp -> putOutComp(comp, percentage) ) );
	}

	public int invertColor( int inPixelValue )
	{
		return( translateSimpleComponentsOfPixelARGB(inPixelValue, this::invertComponent ) );
	}

	public int putOutComp( int componentValue, double component )
	{
		return( IntegerFunctions.zoomValueFloor(componentValue, component) );
	}

	public int invertComponent( int componentValue )
	{
		return( 255 - componentValue );
	}

	public int getPixelValue( int inPixelValue,
								Integer switchColorFrom,
								Integer switchColorTo,
								Integer alphaForPixelsDifferentFromColorFrom )
	{
		int result = inPixelValue;

		int alpha = 0xFF000000;
		if( alphaForPixelsDifferentFromColorFrom != null )
			alpha = ( (alphaForPixelsDifferentFromColorFrom) & 0xFF ) << 24;
			
		if( ( switchColorFrom != null ) && ( result == switchColorFrom ) )
		{
			if( switchColorTo != null ) result = switchColorTo;
			else						result = result & 0xFFFFFF;
		}
		else if( alphaForPixelsDifferentFromColorFrom != null )
		{
			result = result & 0xFFFFFF | alpha;
		}

		return( result );
	}
}
