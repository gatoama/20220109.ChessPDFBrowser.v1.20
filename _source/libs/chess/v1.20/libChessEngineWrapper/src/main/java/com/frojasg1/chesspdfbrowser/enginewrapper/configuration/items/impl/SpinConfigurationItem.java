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
package com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl;

import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SpinConfigurationItem extends ConfigurationItem<Integer>
{
	protected Integer _min = null;
	protected Integer _max = null;

	// for Copier
	public SpinConfigurationItem()
	{
		
	}

	public SpinConfigurationItem( Integer defaultValue, Integer min, Integer max )
	{
		super( defaultValue );
		_min = min;
		_max = max;
	}

	public void init( SpinConfigurationItem that )
	{
		super.init( that );

		_min = _copier.copy( that._min );
		_max = _copier.copy( that._max );
	}

	@Override
	public void init( String name, Integer value )
	{
		super.init( name, value );
	}

	public Integer getMin()
	{
		return( _min );
	}

	public Integer getMax()
	{
		return( _max );
	}

	@Override
	protected void check( Integer value )
	{
		if( ( getDefaultValue() == null ) || ( value != null ) )
		{
			super.check( value );

			// TODO: translate
			if( ( _min != null ) && ( value < _min ) )
				throw( new RuntimeException( createCustomInternationalString( CONF_LOW_BOUND_NOT_FULFILLLED, value, _min ) ) );

			if( ( _max != null ) && ( value > _max ) )
				throw( new RuntimeException( createCustomInternationalString( CONF_HIGH_BOUND_NOT_FULFILLLED, value, _max ) ) );
		}
	}

	@Override
	public Class<Integer> getValueClass()
	{
		return( Integer.class );
	}
}
