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
package com.frojasg1.general.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListString
{
	protected List<String> _list;

	public ListString()
	{
		_list = new ArrayList<>();
	}

	public String getLast()
	{
		String result = null;

		if( ( _list != null ) && !_list.isEmpty() )
			result = _list.get( _list.size() - 1 );

		return( result );
	}

	public boolean isPresent( String value )
	{
		boolean result = false;
		
		for( String item: _list )
		{
			if( item.equals( value ) )
			{
				result = true;
				break;
			}
		}

		return( result );
	}

	public String getListString( String separator )
	{
		StringBuilder sb = new StringBuilder();

		String sep = "";
		for( String item: _list )
		{
			if( ! StringFunctions.instance().isEmpty( item ) )
			{
				sb.append( sep ).append( item );
				sep = separator;
			}
		}

		return( sb.toString() );
	}

	public String getListString()
	{
		return( getListString( "|" ) );
	}

	
	public void setListString( String value, String separator )
	{
		_list = new ArrayList( Arrays.asList( value.split( separator ) ) );
	}

	public void setListString( String value )
	{
		setListString( value, "\\|" );
	}

	public void addElement( String elem )
	{
		_list.remove(elem);
		_list.add( elem );	// at last position
	}

	public List<String> getList()
	{
		return( _list );
	}
}
