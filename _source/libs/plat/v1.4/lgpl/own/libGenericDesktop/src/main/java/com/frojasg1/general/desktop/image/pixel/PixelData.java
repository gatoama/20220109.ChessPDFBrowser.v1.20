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
package com.frojasg1.general.desktop.image.pixel;

/**
 *
 * @author Usuario
 */
public abstract class PixelData<CC>
{
	public static final int LUMINANCE = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int BLUE = 3;
	public static final int ALPHA = 4;

	public CC _alpha;
	public CC _red;
	public CC _green;
	public CC _blue;

	protected CC _greyScale;

	protected boolean _signedComponents = false;

	protected PixelData()
	{
		
	}

	protected PixelData( CC alpha, CC red, CC green, CC blue,
							boolean signedComponents )
	{
		setComponents( alpha, red, green, blue, signedComponents );
	}

	public boolean isSigned()
	{
		return( _signedComponents );
	}

	protected abstract CC getMinimumCompValue();

	public CC getComponent( int componentIndex )
	{
		CC result = getMinimumCompValue();

		switch( componentIndex )
		{
			case LUMINANCE:	result = getGreyScale(); break;
			case RED:	result = getRed(); break;
			case GREEN:	result = getGreen(); break;
			case BLUE:	result = getBlue(); break;
			case ALPHA:	result = getAlpha(); break;
		}

		return( result );
	}

	public void setComponent( int componentIndex, CC value )
	{
		switch( componentIndex )
		{
			case LUMINANCE: setGreyScale(value); break;
			case RED:	setRed(value); break;
			case GREEN:	setGreen(value); break;
			case BLUE:	setBlue(value); break;
			case ALPHA:	setAlpha(value); break;
		}
	}

	public void setComponentWithoutLimit( int componentIndex, CC value )
	{
		switch( componentIndex )
		{
			case LUMINANCE: _red = value; _green = value; _blue = value; break;
			case RED:	_red = value; break;
			case GREEN:	_green = value; break;
			case BLUE:	_blue = value; break;
			case ALPHA:	_alpha = value; break;
		}
	}

	public void setComponents( CC alpha, CC red, CC green, CC blue, boolean signedComponents )
	{
		_signedComponents = signedComponents;

		_alpha = limit( alpha );
		_red = limit( red );
		_green = limit( green );
		_blue = limit( blue );

		recalculateArgbAndGreyScale();

//		_greyScale = limit( from, to, _greyScale );
	}

	protected abstract void recalculateArgbAndGreyScale();

	protected abstract CC limit( CC value );

	protected void makeComponentsSigned()
	{
		_alpha = makeSigned( _alpha );
		_red = makeSigned( _red );
		_green = makeSigned( _green );
		_blue = makeSigned( _blue );
		_greyScale = makeSigned( _greyScale );
	}

	protected abstract CC makeSigned( CC value );

	public CC getAlpha()
	{
		return( _alpha );
	}

	public CC getRed()
	{
		return( _red );
	}
	
	public CC getGreen()
	{
		return( _green );
	}
	
	public CC getBlue()
	{
		return( _blue );
	}

	public void setAlpha( CC value )
	{
		_alpha = limit( value );
		recalculateArgbAndGreyScale();
	}

	public void setGreyScale( CC value )
	{
		setRed( value );
		setGreen( value );
		setBlue( value );
	}

	public void setRed( CC value )
	{
		_red = limit( value );
		recalculateArgbAndGreyScale();
	}

	public void setGreen( CC value )
	{
		_green = limit( value );
		recalculateArgbAndGreyScale();
	}

	public void setBlue( CC value )
	{
		_blue = limit( value );
		recalculateArgbAndGreyScale();
	}

	public CC getGreyScale()
	{
		return( _greyScale );
	}

	protected abstract CC subtractComp( CC valone, CC valtwo );

	protected abstract PixelData<CC> createEmptyCopy();

	public PixelData<CC> subtract( PixelData<CC> other )
	{
		PixelData result = createEmptyCopy();

		result._alpha = subtractComp( _alpha, other._alpha );
		result._red = subtractComp( _red, other._red );
		result._green = subtractComp( _green, other._green );
		result._blue = subtractComp( _blue, other._blue );

		result.setComponents(result._alpha, result._red, result._green, result._blue, _signedComponents);

		return( result );
	}
}
