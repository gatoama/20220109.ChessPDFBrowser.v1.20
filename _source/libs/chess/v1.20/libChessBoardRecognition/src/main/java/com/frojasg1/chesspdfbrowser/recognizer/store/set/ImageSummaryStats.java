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
package com.frojasg1.chesspdfbrowser.recognizer.store.set;

import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.image.pixel.impl.PixelStats;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ImageSummaryStats
{
	protected static DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected int _elemCount = 0;
	protected PixelStats _averageStandardDeviation = null;
	protected double _minimumMeanSquareError = 1e6d;

	public void init( ImageSummaryStats that )
	{
		if( ( that != this ) && ( that != null ) )
		{
			_elemCount = that._elemCount;
			_averageStandardDeviation = _copier.copy( that._averageStandardDeviation );
			_minimumMeanSquareError = that._minimumMeanSquareError;
		}
	}

	public int getElemCount()
	{
		return( _elemCount );
	}

	public void setElemCount( int value )
	{
		_elemCount = value;
	}

	public PixelStats getAverageStandardDeviation()
	{
		if( _averageStandardDeviation == null )
			_averageStandardDeviation = new PixelStats( 0d, 0d, 0d, 0d, false );

		return( _averageStandardDeviation );
	}

	public void setAverageStandardDeviation( PixelStats standardDeviation )
	{
		_averageStandardDeviation = standardDeviation;
	}

	public double getMinimumMeanSquareError()
	{
		return( _minimumMeanSquareError );
	}

	public void setMinimumMeanSquareError( double value )
	{
		_minimumMeanSquareError = value;
	}

	public void updateAverageStandardDeviation( PixelStats ... newStandardDeviations )
	{
		PixelStats result = getAverageStandardDeviation().createCopy();
		result.multiplyByScalar( getElemCount() );

		for( PixelStats pe: newStandardDeviations )
			result.add(pe);
		
		setElemCount( getElemCount() + newStandardDeviations.length );

		if( getElemCount() != 0 )
		{
			double factor = 1 / ( (double) getElemCount() );
			result.multiplyByScalar(factor);
		}

		setAverageStandardDeviation( result );
	}

	public void updateMinimumMeanSquareError( Double ... newMeanSquareErrors )
	{
		double result = getMinimumMeanSquareError();
		for(Double mse: newMeanSquareErrors)
			if( mse < result )
				result = mse;

		setMinimumMeanSquareError( result );
	}
}
