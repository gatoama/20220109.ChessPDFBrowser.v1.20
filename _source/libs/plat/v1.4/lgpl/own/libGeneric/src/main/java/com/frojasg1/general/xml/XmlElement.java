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
package com.frojasg1.general.xml;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface XmlElement
{
	public String getName();
	public Collection<String> getAttributeNames( );
	public String getAttributeValue( String attributeName );

	public void putAttribute( String attributeName, String value );
	public void removeAttribute( String attributeName );

	public XmlElementList getSelfAndSibilingItems();

	public Collection<XmlElement> getAllChildren();

	public XmlElementList getChildrenByName( String name );
	public Collection<String> getChildNames();

	public XmlElement getChild( String childName, int index );
	public XmlElement getChild( String childName );

	public void addChild( XmlElement newChild );
	public XmlElementList removeChildList( String childName );
	public XmlElement removeChild( String childName, int index );

	public String getText();
	public void setText( String text );

	public XmlElement getRoot();

	public XmlElement getParent();
	public void setParent( XmlElement parent );

	// input parameter has format:    "attribute1=value1,attribute2=value2,...,attributeN=valueN"
	public boolean attributesMatch( String attributeValuePairList );
	public boolean attributesMatch( Map<String, String> attValMap );

	// input parameter has format:    "tag1<att1=val1,...,attN=valN>[index]. ... .tagN<att1=val1,...attN=valN>[index]"
	public XmlElement getChildFromComposedLocation( String elementLocation );
}
