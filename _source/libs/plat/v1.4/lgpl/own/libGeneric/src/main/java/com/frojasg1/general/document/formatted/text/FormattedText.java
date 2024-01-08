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
package com.frojasg1.general.document.formatted.text;

import com.frojasg1.general.document.formatted.FormattedString;
import java.util.ArrayList;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class FormattedText
{
	protected ArrayList<FormattedText> _children = new ArrayList<>();

	protected FormattedString _formattedString = null;
	protected FormattedTextAttributes _attributes = new FormattedTextAttributes();

	protected FormattedText _parent = null;

	public void add( FormattedText elem )
	{
		if( elem != null )
		{
			_children.add( elem );
			elem.setParent( this );
		}
	}

	public FormattedText getElem( int index )
	{
		FormattedText result = null;
		if( ( index >= 0 ) && ( index < size() ) )
		{
			result = _children.get(index);
		}

		return( result );
	}

	public int size()
	{
		return( _children.size() );
	}

	public FormattedString getFormattedString()
	{
		return( _formattedString );
	}

	public void setFormattedString( FormattedString fs )
	{
		_formattedString = fs;
	}

	public FormattedTextAttributes getAttributes()
	{
		return( _attributes );
	}

	public void setAttributes( FormattedTextAttributes attributes )
	{
		_attributes = attributes;
	}

	protected void setParent( FormattedText parent )
	{
		_parent = parent;
	}

	public FormattedText getParent()
	{
		return( _parent );
	}
}
