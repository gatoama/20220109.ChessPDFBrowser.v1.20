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
package com.frojasg1.general.document.formatter.xml;

import com.frojasg1.general.document.formatted.FormattedString;
import com.frojasg1.general.document.formatter.FormattedStringByName;
import com.frojasg1.general.document.formatted.text.FormattedText;
import com.frojasg1.general.xml.XmlElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ComplexXmlFormatterBase implements FormattedStringByName
{
	protected XmlElement _xmlElem = null;

	protected Map<String, FormattedStringByName> _formattedStringByNameMap = new HashMap<>();
	protected Map<String, XmlLocationFormatterData> _xmlLocationFormatterPairByNameMap = new HashMap<>();

	protected String _name;

	public ComplexXmlFormatterBase( String name, XmlElement xmlElement )
	{
		_name = name;
		_xmlElem = xmlElement;
	}

//	@Override
	public Set<String> initFormattedStringByName()
	{
		fillFormattedStringByNameMap();

		Collection<FormattedStringByName> col = new ArrayList<>();
		col.addAll( _formattedStringByNameMap.values() );

		// we add child names
		Iterator<FormattedStringByName> it = col.iterator();
		while( it.hasNext() )
		{
			FormattedStringByName item = it.next();
/*
			Set<String> set = item.initFormattedStringByName();
			if( set != null )
			{
				Iterator<String> it2 = set.iterator();
				while( it2.hasNext() )
				{
					_formattedStringByNameMap.put( it2.next(), item );
				}
			}
*/
		}

		return( _formattedStringByNameMap.keySet() );
	}

	protected FormattedStringByName createFormattedStringByNameForXmlLocations()
	{
		FormattedStringByName result = new FormattedStringByName() {

			@Override
			public FormattedText getFormattedString(String type, String name) {
				return( getFormattedTextOfXmlLocation( type, name ) );
			}

			@Override
			public FormattedText getFormattedString(String name) {
				return( getFormattedString( null, name ) );
			}
		};

		return( result );
	}

	protected void fillFormattedStringByNameMap()
	{
		putFormatedStringByName( _name, this );

		fillXmlLocationFormatterPairByNameMap();

		if( _xmlLocationFormatterPairByNameMap.size() > 0 )
		{
			FormattedStringByName fsbn = createFormattedStringByNameForXmlLocations();

			Iterator<String> it = _xmlLocationFormatterPairByNameMap.keySet().iterator();
			while( it.hasNext() )
			{
				putFormatedStringByName( it.next(), fsbn );
			}
		}
	}

	protected void putFormatedStringByName( String name, FormattedStringByName fsbn )
	{
		_formattedStringByNameMap.put( name, fsbn );
	}

	protected abstract FormattedText selfFormattedString();

	protected abstract void fillXmlLocationFormatterPairByNameMap();

	protected abstract FormattedText getFormattedTextOfChild( String type, XmlElement elem, XmlOutputFormatter formatter );

	@Override
	public FormattedText getFormattedString( String type, String name)
	{
		FormattedText result = null;
		if( name != null )
		{
			if( name.equals( _name ) )
				result = selfFormattedString();
			else
			{
				FormattedStringByName fsbn = _formattedStringByNameMap.get( name );
				if( fsbn != null )
					result = fsbn.getFormattedString(type, name);
			}
		}

		return( result );
	}

	protected FormattedText getFormattedTextOfXmlLocation( String type, String name )
	{
		FormattedText result = null;
		XmlLocationFormatterData lfd = _xmlLocationFormatterPairByNameMap.get( name );
		if( lfd != null )
		{
			XmlOutputFormatter formatter = lfd.getXmlFormatter();
			if( formatter != null )
			{
				XmlElement elem = _xmlElem.getChildFromComposedLocation( lfd.getComposedLocation() );
				if( elem != null )
//					result = formatter.formatXml(elem );
					result = getFormattedTextOfChild( type, elem, formatter );
			}
		}

		return( result );
	}
}
