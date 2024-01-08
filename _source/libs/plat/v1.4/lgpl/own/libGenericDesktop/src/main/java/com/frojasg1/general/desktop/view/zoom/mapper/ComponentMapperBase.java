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
package com.frojasg1.general.desktop.view.zoom.mapper;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentMapperBase implements ComponentMapper
{
	protected Map< Component, Component > _map = new HashMap<>();
	
	public void addAll( Map< Component, Component > map )
	{
		_map.putAll(map);
	}

	public void clear()
	{
		_map.clear();
	}

	public <CC> CC mapComponent( CC originalComponent )
	{
		CC result = (CC) _map.get( originalComponent );
		if( result == null )
			result = originalComponent;

		return( result );
	}

	public void put( Component originalComp, Component mappedComp )
	{
		_map.put( originalComp, mappedComp );
	}

	public Map< Component, Component > getMap()
	{
		return( _map );
	}
}
