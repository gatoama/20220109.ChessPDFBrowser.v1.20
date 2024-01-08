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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComboConfigurationItem extends ConfigurationItem<String>
{
	protected Set<String> _allowedValues = null;

	// for Copier
	public ComboConfigurationItem()
	{
		
	}

	public ComboConfigurationItem( String defaultValue, String ... allowedValues )
	{
		super( defaultValue );
		_allowedValues = new HashSet<>( Arrays.asList( allowedValues ) );
	}

	public void init( ComboConfigurationItem that )
	{
		super.init( that );

		_allowedValues = _copier.copy( that._allowedValues );
	}

	@Override
	public void init( String name, String value )
	{
		super.init( name, value );
	}

	public Set<String> getAllowedValues()
	{
		return( _allowedValues );
	}

	@Override
	protected void check( String value )
	{
		super.check( value );

		// TODO: translate
		if( ( ( getDefaultValue() == null ) || ( value != null ) ) &&
			( ! _allowedValues.contains( value ) ) )
			throw( new RuntimeException( createCustomInternationalString( CONF_COMBO_VALUE_IS_NOT_AMONG_ALLOWED_VALUES, value, _allowedValues ) ) );
	}

	@Override
	public Class<String> getValueClass()
	{
		return( String.class );
	}
}
