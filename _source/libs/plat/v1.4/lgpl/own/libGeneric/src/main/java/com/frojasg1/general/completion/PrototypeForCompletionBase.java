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
package com.frojasg1.general.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PrototypeForCompletionBase
{
	protected String _name = null;
	protected String _type = null;

	protected ArrayList<String> _listOfParams = new ArrayList<>();

	public PrototypeForCompletionBase( String name, String type )
	{
		_type = type;

		_name = name;
	}

	public String getParam( int index )
	{
		String result = null;
		if( index < _listOfParams.size() )
			result = _listOfParams.get( index );

		return( result );
	}

	public void addParam( String param )
	{
		_listOfParams.add( param );
	}

	public String getName()
	{
		return( _name );
	}

	public Collection<String> getListOfParams()
	{
		return( _listOfParams );
	}

	public String getType()
	{
		return( _type );
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "(" ).append( _type ).append( ") " ).append( _name );

		if( _listOfParams.size() > 0 )
		{
			sb.append( "( " );
			String separator = "";
			for( String param: _listOfParams )
			{
				sb.append( separator ).append( param );
				separator = ", ";
			}

			sb.append( " )" );
		}

		return( sb.toString() );
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + Objects.hashCode(this._name);
		hash = 97 * hash + Objects.hashCode(this._type);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PrototypeForCompletionBase other = (PrototypeForCompletionBase) obj;
		if (!Objects.equals(this._name, other._name)) {
			return false;
		}
		if (!Objects.equals(this._type, other._type)) {
			return false;
		}
		return true;
	}
}
