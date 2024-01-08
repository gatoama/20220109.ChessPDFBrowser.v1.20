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

import com.frojasg1.general.map.MapFunctions;
import com.frojasg1.general.document.formatted.FormattedString;
import com.frojasg1.general.document.formatted.text.FormattedText;
import com.frojasg1.general.document.formatted.text.imp.FormattedTextParagraph;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.xml.XmlElement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class SingleXmlFormatterBase extends XmlOutputFormatterBase
{
	protected String _tagName = null;
	protected String _styleAttributeName = null;

	// Key1 = typeOfFormat,   Key2 = attributeValue,   datum = StyleName
	protected Map< String, Map< String, String > > _typeOfFormatToAttributeToStyleNamesMapMap = new HashMap<>();

	public SingleXmlFormatterBase( String tagNameForParagrahElem,
											String attributeNameForStyle )
	{
		_tagName = tagNameForParagrahElem;
		_styleAttributeName = attributeNameForStyle;
	}

	public void init()
	{
		fillTypeOfFormatToAttributeToStypeMapMap(_typeOfFormatToAttributeToStyleNamesMapMap );
	}

	protected abstract void fillTypeOfFormatToAttributeToStypeMapMap( Map< String, Map< String, String > > map );

	protected Map< String, String > createEmptyAttributeToStyleMap( )
	{
		return( new HashMap<>() );
	}


	@Override
	public FormattedText formatXml( String typeOfFormat, XmlElement elem )
	{
		FormattedText result = null;

		if( ( elem != null ) && ( elem.getName().equals( _tagName ) ) &&
			!StringFunctions.instance().isEmpty( elem.getText() ) )
		{
			String styleName = getStyleName(typeOfFormat, elem.getAttributeValue(_styleAttributeName ) );

			result = createEmptyFormattedText();
			FormattedString fs = createSingleFormattedString( elem.getText(), styleName );
			result.setFormattedString(fs);

/*
			if( styleName != null )
			{
//				result = createSingleFormattedString( elem.getText(), 0, styleName );
				result = createSingleFormattedText( elem.getText(), 0, styleName );
			}
*/
		}

		return( result );
	}

	protected String getStyleName( String type, String styleAttributeValue )
	{
		String result = null;
		if( styleAttributeValue != null )
		{
			Map< String, String > attributeValueToStyleNameMap = MapFunctions.instance().get(_typeOfFormatToAttributeToStyleNamesMapMap, type);

			result = MapFunctions.instance().get(attributeValueToStyleNameMap, styleAttributeValue);
			result = translateStyleName( result );
		}

		return( result );
	}

	@Override
	protected FormattedText createEmptyFormattedText()
	{
		return( new FormattedTextParagraph() );
	}
}
