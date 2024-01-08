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
package com.frojasg1.general.xml.impl;

import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.XmlElementList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class XmlElementListImpl implements XmlElementList
{
	protected String _name = null;

	protected List<XmlElement> _list = new ArrayList<>();

	protected XmlElement _parent = null;

	public XmlElementListImpl( String name )
	{
		_name = name;
	}

	@Override
	public String getName()
	{
		return( _name );
	}

	@Override
	public int getSize()
	{
		return( _list.size() );
	}

	@Override
	public XmlElement getElementAt(int index)
	{
		XmlElement result = null;
		if( (index >= 0) && ( index < _list.size() ) )
			result = _list.get(index);

		return( result );
	}

	@Override
	public Collection<XmlElement> getCollection()
	{
		return( _list );
	}

	@Override
	public void addElement(XmlElement elem)
	{
		if( elem != null )
		{
			if( ! _name.equals( elem.getName() ) )
				throw( new RuntimeException( "Element added to XmlElementList with a different name." ) );

			_list.add( elem );
		}
	}

	@Override
	public XmlElement removeElement(int index)
	{
		XmlElement removedElement = null;
		if( (index >= 0 ) && ( index < _list.size() ) )
			removedElement = _list.remove(index);

		return( removedElement );
	}

}
