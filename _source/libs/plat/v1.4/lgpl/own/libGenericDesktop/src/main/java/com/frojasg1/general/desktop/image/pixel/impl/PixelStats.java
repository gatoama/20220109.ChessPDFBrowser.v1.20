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

import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.image.pixel.PixelData;

/**
 *
 * @author Usuario
 */
public class PixelStats extends PixelData<Double>
{
	protected static DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	public PixelStats()
	{
		
	}

	public PixelStats( Double alpha, Double red, Double green, Double blue,
							boolean signedComponents )
	{
		super( alpha, red, green, blue, signedComponents );
	}

	public void init( PixelStats that )
	{
		if( ( that != this ) && ( that != null ) )
		{
			setAlpha( _copier.copy( that.getAlpha() ) );
			setRed( _copier.copy( that.getRed() ) );
			setGreen( _copier.copy( that.getGreen() ) );
			setBlue( _copier.copy( that.getBlue() ) );
		}
	}

	@Override
	protected void recalculateArgbAndGreyScale()
	{
	}

	@Override
	protected Double limit( Double value )
	{
		return( value );
	}

	@Override
	protected Double makeSigned( Double value )
	{
		return( value );
	}

	@Override
	protected Double subtractComp( Double valone, Double valtwo )
	{
		return( valone - valtwo );
	}

	@Override
	protected PixelStats createEmptyCopy()
	{
		return( new PixelStats( null, null, null, null, _signedComponents ) );
	}

	@Override
	public PixelStats subtract( PixelData<Double> other )
	{
		return( (PixelStats) super.subtract(other) );
	}

	@Override
	protected Double getMinimumCompValue() {
		return 0.0d;
	}

	public void add( PixelData<Double> other )
	{
		for( int ii=PixelData.LUMINANCE; ii<=PixelData.ALPHA; ii++ )
		{
			Double cmpValone = getComponent(ii);
			Double cmpValtwo = other.getComponent(ii);
			if( ( cmpValone != null ) && ( cmpValtwo != null ) )
				setComponent(ii, cmpValone + cmpValtwo );
		}
	}

	public void multiplyByScalar( double factor )
	{
		for( int ii=PixelData.LUMINANCE; ii<=PixelData.ALPHA; ii++ )
		{
			Double compVal = getComponent(ii);
			if( compVal != null )
				setComponent(ii, compVal * factor );
		}
	}

	public PixelStats createCopy()
	{
		return( new PixelStats( getAlpha(), getRed(), getGreen(), getBlue(), isSigned() ) );
	}

	public double getComponentsAverage()
	{
		return( ( getRed() + getGreen() + getBlue() ) / 3 );
	}
}
