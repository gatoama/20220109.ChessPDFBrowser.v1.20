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

import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentsStats
{
	public static final int LUMINANCE = PixelComponents.LUMINANCE;
	public static final int RED = PixelComponents.RED;
	public static final int GREEN = PixelComponents.GREEN;
	public static final int BLUE = PixelComponents.BLUE;
	public static final int TOTAL_ELEMENTS = 4;
	public static final int INITIAL_COMPONENT_INDEX = LUMINANCE;

	protected ComponentStats[] _values = null;

	// function for DefaultConstructorInitCopier
	public void ComponentsStats()
	{
		
	}

	// function for DefaultConstructorInitCopier
	public synchronized void init( ComponentsStats that )
	{
		init();

		for( int ii=0; ii<TOTAL_ELEMENTS; ii++ )
			_values[ii] = that._values[ii];
	}

	public void init()
	{
		_values = new ComponentStats[TOTAL_ELEMENTS];
	}
	
	public void update( ComponentsStats that )
	{
		for( int ii=0; ii<TOTAL_ELEMENTS; ii++ )
			_values[ii].setAverage( that._values[ii].getAverage() );
	}

	public ComponentStats getComponentStats( int componentIndex )
	{
		return( _values[componentIndex] );
	}

	public void setComponentStats( long average, long standardDeviation, int componentIndex )
	{
		_values[componentIndex] = createComponentStats( average, standardDeviation );
	}

	public ComponentStats createComponentStats( long average, long standardDeviation )
	{
		return( new ComponentStats( average, standardDeviation ) );
	}

	public static class ComponentStats
	{
		protected long _average = 0;
		protected long _standardDeviation = 0;

		public ComponentStats( long average, long standardDeviation )
		{
			_average = average;
			_standardDeviation = standardDeviation;
		}

		public void setAverage( long value ) {
			_average = value;
		}

		public long getAverage() {
			return _average;
		}

		public long getStandardDeviation() {
			return _standardDeviation;
		}
	}
}
