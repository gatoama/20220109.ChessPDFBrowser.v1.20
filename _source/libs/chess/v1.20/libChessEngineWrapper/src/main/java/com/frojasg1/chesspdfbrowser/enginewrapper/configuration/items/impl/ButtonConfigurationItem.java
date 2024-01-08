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
public class ButtonConfigurationItem extends ConfigurationItem<Object>
{
	public ButtonConfigurationItem()
	{
		super( null );
	}

	public void init( ButtonConfigurationItem that )
	{
		super.init( that );
	}

	@Override
	public void init( String name, Object value )
	{
		super.init( name, value );
	}

	@Override
	protected void check( Object value )
	{
		// TODO: translate
		if( value != null )
			throw( new RuntimeException( getInternationalString(CONF_BUTTON_CONFIGURATION_ITEM_NOT_NULL) ) );
	}

	@Override
	public Class<Object> getValueClass()
	{
		return( Object.class );
	}

	public String getCommand()
	{
		return( null );
	}
}
