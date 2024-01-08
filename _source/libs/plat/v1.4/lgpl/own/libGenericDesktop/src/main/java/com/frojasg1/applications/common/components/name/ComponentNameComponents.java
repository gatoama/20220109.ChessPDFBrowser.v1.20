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
package com.frojasg1.applications.common.components.name;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentNameComponents
{
	public static final String NAME_COMPONENT = "name";
	public static final String ICON_COMPONENT = "icon";
	public static final String URL_COMPONENT = "url";
	public static final String HINT_CAPS_COMPONENT = "CapsHint";		// for CustomizedJPasswordField

	protected Map< String, String > _map = new HashMap<>();

	public ComponentNameComponents()
	{}

	public ComponentNameComponents( String name )
	{
		init( name );
	}

	protected String[] split( String str, String splitter )
	{
		String[] result = null;

		int pos = str.indexOf( splitter );
		if( pos >= 0 )
		{
			String key = str.substring(0, pos );
			String value = str.substring( pos + splitter.length() );

			result = new String[]{ key, value };
		}
		else
		{
			result = new String[]{ str };
		}

		return( result );
	}

	public void init( String name )
	{
		if( name != null )
		{
			String[] components = name.split( "," );
			for( int ii=0; ii<components.length; ii++ )
			{
				String[] assignation = split( components[ii], "=" );

				if( assignation.length == 2 )
					_map.put( assignation[0],  assignation[1] );
				else if( ( components.length == 1 ) && ( assignation.length == 1 ) )
					_map.put( NAME_COMPONENT, components[0] );
			}
		}
	}

	public String getComponent( String component )
	{
		return( _map.get( component ) );
	}

	public String getName()
	{
		return( getComponent( NAME_COMPONENT ) );
	}

	public void setComponent( String component, String value )
	{
		_map.put( component, value );
	}

	public void setName( String newName )
	{
		_map.put( NAME_COMPONENT, newName );
	}

	public String getCompoundNameForComponentName()
	{
		String result = null;

		StringBuilder sb = new StringBuilder();
		Iterator< Map.Entry< String, String > > it = _map.entrySet().iterator();
		String separator = "";
		while( it.hasNext() )
		{
			Map.Entry< String, String > entry = it.next();
			sb.append( separator );
			sb.append( entry.getKey() );
			sb.append( "=" );
			sb.append( entry.getValue() );

			separator = ",";
		}

		String sbStr = sb.toString();
		if( sbStr.length() > 0 )
			result = sbStr;

		return( result );
	}
}
