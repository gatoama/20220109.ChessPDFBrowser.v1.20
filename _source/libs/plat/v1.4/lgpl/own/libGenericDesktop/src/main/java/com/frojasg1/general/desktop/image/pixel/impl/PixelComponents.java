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
package com.frojasg1.general.desktop.image.pixel.impl;

import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.pixel.PixelData;
import com.frojasg1.general.number.IntegerFunctions;

/**
 *
 * @author Usuario
 */
public class PixelComponents extends PixelData<Short>
{
	protected int _argb;

	public PixelComponents( int argb, boolean signedComponents )
	{
		setArgb( argb, signedComponents );
	}

	public PixelComponents( short alpha, short red, short green, short blue,
							boolean signedComponents )
	{
		super( alpha, red, green, blue, signedComponents );
	}

	@Override
	protected void recalculateArgbAndGreyScale()
	{
		_argb = ImageFunctions.instance().getARGB( absoluteValue( _alpha ),
			absoluteValue( _red ), absoluteValue( _green ),
			absoluteValue( _blue ) );

		_greyScale = ImageFunctions.instance().getGrayScale(_argb);
		if( _signedComponents )
			_greyScale = makeSigned( _greyScale );
	}

	protected int absoluteValue( int value )
	{
		return( _signedComponents ? value + 128 : value );
	}

	@Override
	protected Short limit( Short value )
	{
		short from = 0;
		short to = 255;
		if( _signedComponents )
		{
			from = (short) -128;
			to = (short) 127;
		}

		short result = limit( from, to, value );

		return( result );
	}

	protected Short limit( Short from, Short to, Short value )
	{
		short result = value;
		if( result > to )
			result = to;
		if( result < from )
			result = from;

		return( result );
	}

	@Override
	protected Short makeSigned( Short value )
	{
		return( (short) ( value - 128 ) );
	}

	public void setArgb( int argb, boolean signedComponents )
	{
		_signedComponents = signedComponents;

		_greyScale = ImageFunctions.instance().getGrayScale(argb);
		_argb = argb;
		_alpha = (short) ( ( argb >>> 24 ) & 0xff );
		_red = (short) ( ( argb >>> 16 ) & 0xff );
		_green = (short) ( ( argb >>> 8 ) & 0xff );
		_blue = (short) ( argb & 0xff );

		if( _signedComponents )
			makeComponentsSigned();
	}

	public boolean nearlyEquals( short[] pixelComponents, int tolerance )
	{
		boolean result = false;

		if( pixelComponents != null )
		{
			int redDiff = IntegerFunctions.abs( pixelComponents[0] - getRed() );
			int greenDiff = IntegerFunctions.abs( pixelComponents[1] - getGreen() );
			int blueDiff = IntegerFunctions.abs( pixelComponents[2] - getBlue() );

			if( ( getRed() >= 0 ) && ( redDiff <= tolerance ) &&
				( getGreen() >= 0 ) && ( greenDiff <= tolerance ) &&
				( getBlue() >= 0 ) && ( blueDiff <= tolerance ) )
			{
				result = true;
			}
		}

		return( result );
	}

	public int getPixelValue()
	{
		return( _argb );
	}

	@Override
	protected Short subtractComp( Short valone, Short valtwo )
	{
		return( (short) (valone - valtwo) );
	}

	@Override
	protected PixelComponents createEmptyCopy()
	{
		return( new PixelComponents( 0, _signedComponents ) );
	}

	@Override
	public PixelComponents subtract( PixelData<Short> other )
	{
		return( (PixelComponents) super.subtract(other) );
	}

	@Override
	protected Short getMinimumCompValue() {
		return Short.MIN_VALUE;
	}
}
