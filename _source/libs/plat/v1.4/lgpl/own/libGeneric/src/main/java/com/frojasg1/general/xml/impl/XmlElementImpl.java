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
import com.frojasg1.general.xml.XmlFunctions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class XmlElementImpl implements XmlElement
{
	protected String _name = null;
	protected Map<String, String> _attributeMap = new HashMap<>();

	protected Map<String, XmlElementList> _childrenMap = new HashMap<>();

	protected String _text = null;

	protected XmlElement _parent = null;

	protected Collection< XmlElement > _allChildren = new ArrayList<>();

	public XmlElementImpl( String name )
	{
		_name = name;
	}

	@Override
	public String getName()
	{
		return( _name );
	}

	@Override
	public Collection<String> getAttributeNames()
	{
		return( _attributeMap.keySet() );
	}

	@Override
	public String getAttributeValue(String attributeName)
	{
		return( _attributeMap.get( attributeName ) );
	}

	@Override
	public XmlElementList getChildrenByName(String name)
	{
		XmlElementList result = XmlFunctions.instance().copy( _childrenMap.get( name ) );

		return( result );
	}

	@Override
	public Collection<String> getChildNames()
	{
		return( _childrenMap.keySet() );
	}

	@Override
	public String getText()
	{
		return( _text );
	}

	@Override
	public void putAttribute(String attributeName, String value)
	{
		_attributeMap.put( attributeName, value );
	}

	@Override
	public void removeAttribute(String attributeName)
	{
		_attributeMap.remove( attributeName );
	}

	@Override
	public XmlElement getChild( String childName, int index )
	{
		XmlElement result = null;
		XmlElementList elList = _childrenMap.get( childName );
		if( ( elList != null ) && ( index >= 0 ) &&
			( index < elList.getSize() ) )
		{
			result = elList.getElementAt( index );
		}

		return( result );
	}

	@Override
	public XmlElement getChild(String childName)
	{
		return( getChild( childName, 0 ) );
	}

	@Override
	public void addChild(XmlElement newChild)
	{
		if( newChild != null )
		{
			String childName = newChild.getName();

			XmlElementList elList = _childrenMap.get( childName );
			if( elList == null )
			{
				elList = createNewXmlElementList( childName );
				_childrenMap.put( childName, elList );
			}

			newChild.setParent( this );
			elList.addElement( newChild );

			_allChildren.add( newChild );
		}
	}

	@Override
	public XmlElementList removeChildList(String childName)
	{
		XmlElementList result = _childrenMap.remove( childName );
		if( result != null )
			_allChildren.removeAll( result.getCollection() );

		return( result );
	}

	@Override
	public XmlElement removeChild(String childName, int index)
	{
		XmlElement removedElement = null;
		
		XmlElementList elList = _childrenMap.get( childName );
		if( ( elList != null ) && ( index >= 0 ) &&
			( index < elList.getSize() ) )
		{
			removedElement = elList.removeElement( index );
			_allChildren.remove( removedElement );
		}

		return( removedElement );
	}

	public XmlElementList createNewXmlElementList( String name )
	{
		return( new XmlElementListImpl( name ) );
	}

	@Override
	public void setText(String text)
	{
		_text = text;
	}

	@Override
	public XmlElement getParent()
	{
		return( _parent );
	}

	@Override
	public void setParent(XmlElement parent)
	{
		_parent = parent;
	}

	@Override
	public XmlElementList getSelfAndSibilingItems()
	{
		XmlElementList result = null;
		if( _parent != null )
			result = _parent.getChildrenByName( getName() );

		return( result );
	}

	@Override
	public Collection<XmlElement> getAllChildren()
	{
		return( _allChildren );
	}

	@Override
	// input parameter has format:    "attribute1=value1,attribute2=value2,...,attributeN=valueN"
	public boolean attributesMatch(String attributeValuePairList)
	{
		boolean result = true;

		if( attributeValuePairList != null )
		{
			String[] pairAttValArray = attributeValuePairList.split( "," );

			for( int ii=0; result && (ii<pairAttValArray.length); ii++ )
			{
				String[] pairAttVal = pairAttValArray[ii].split( "=" );

				String val = getAttributeValue( pairAttVal[0] );
				result = ( val != null ) && ( val.matches( pairAttVal[1] ) );
			}
		}

		return( result );
	}

	@Override
	// input parameter has format:    "attribute1=value1,attribute2=value2,...,attributeN=valueN"
	public boolean attributesMatch(Map<String, String> attValMap)
	{
		boolean result = true;

		if( attValMap != null )
		{
			Iterator< Map.Entry< String, String > > it = attValMap.entrySet().iterator();
			while( it.hasNext() )
			{
				Map.Entry<String,String> entry = it.next();

				String val = getAttributeValue( entry.getKey() );
				result = ( val != null ) && ( val.matches( entry.getValue() ) );
			}
		}

		return( result );
	}

	@Override
	// input parameter has format:    "tag1<att1=val1,...,attN=valN>[index]. ... .tagN<att1=val1,...attN=valN>[index]"
	public XmlElement getChildFromComposedLocation(String composedLocation)
	{
		return( XmlFunctions.instance().getElemOfComposedLocation( this, composedLocation ) );
	}

	@Override
	public XmlElement getRoot()
	{
		XmlElement result = this;

		while( result.getParent() != null )
			result = result.getParent();

		return( result );
	}
}
