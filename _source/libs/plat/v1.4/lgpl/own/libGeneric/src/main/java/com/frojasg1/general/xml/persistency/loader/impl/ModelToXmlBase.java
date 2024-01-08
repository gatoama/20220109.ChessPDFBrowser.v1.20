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
package com.frojasg1.general.xml.persistency.loader.impl;

import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.number.DoubleFunctions;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.XmlFunctions;
import com.frojasg1.general.xml.persistency.loader.ModelToXml;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ModelToXmlBase<CC> implements ModelToXml<CC>
{

	protected <CCC> List<CCC> reverse( List<CCC> input )
	{
		return( CollectionFunctions.instance().reverseList(input) );
	}

	protected XmlElement createElement( XmlElement parent, String name )
	{
		XmlElement result = createElement( name );
		parent.addChild( result );

		return( result );
	}

	protected XmlElement createElement( String name )
	{
		return( XmlFunctions.instance().createXmlElement( name ) );
	}

	protected XmlElement createLeafElement( String name, String text )
	{
		XmlElement result = createElement( name );
		result.setText( text );

		return( result );
	}

	protected XmlElement createLeafElement( XmlElement parent, String name, String text )
	{
		XmlElement result = createElement( name );
		result.setText( text );

		parent.addChild( result );

		return( result );
	}

	protected XmlElement createLeafElementIfNotNull( XmlElement parent, String name, String text )
	{
		XmlElement result = null;
		if( text != null )
			result = createLeafElement( parent, name, text );

		return( result );
	}

	protected <CC> String toString( CC obj )
	{
		String result = "null";
		if( obj != null )
			result = obj.toString();

		return( result );
	}

	protected String format( double value )
	{
		return( DoubleFunctions.instance().format( value ) );
	}
}
