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

import com.frojasg1.general.document.formatter.FormatterFactory;
import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.general.document.formatted.FormattedString;
import com.frojasg1.general.document.formatted.text.FormattedText;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class XmlOutputFormatterBase implements XmlOutputFormatter
{
	protected Map< String, String > _translationOfStyleNamesMap = null;


	protected String translateStyleName( String originalStyleName )
	{
		String result = originalStyleName;
		if( _translationOfStyleNamesMap != null )
		{
			String tmp = _translationOfStyleNamesMap.get( originalStyleName );

			if( tmp != null )
				result = tmp;
		}

		return( result );
	}

	protected abstract FormattedText createEmptyFormattedText();


	protected Collection<FormatForText> createCollectionOfFormatForText()
	{
		return( FormatterFactory.instance().createCollectionOfFormatForText() );
	}

	protected FormatForText createFormatForText( String text, int start, String styleName )
	{
		FormatForText result = FormatterFactory.instance().createFormatForText( text, start, styleName );
		return( result );
	}

	protected Collection<FormattedString> createSingleCollectionOfFormattedString( String text, String styleName )
	{
		return( FormatterFactory.instance().createSingleCollectionOfFormattedString( text, styleName ) );
	}

	protected FormattedString createSingleFormattedString( String text, String styleName )
	{
		return( FormatterFactory.instance().createSingleFormattedString( text, styleName ) );
	}

	protected Collection<FormatForText> createSingleCollectionOfFormatForText( String text, String styleName )
	{
		return( FormatterFactory.instance().createSingleCollectionOfFormatForText( text, styleName ) );
	}

	public void setTranslationStyleMap( Map< String, String > map )
	{
		_translationOfStyleNamesMap = map;
	}

	public XmlLocationFormatterData createXmlLocationFormatterData( String composedLocation,
															XmlOutputFormatter xmlFormatter )
	{
		return( FormatterFactory.instance().createXmlLocationFormatterData( composedLocation, xmlFormatter) );
	}

	public Collection<XmlLocationFormatterData> createCollectionOfXmlLocationFormatterData()
	{
		return( FormatterFactory.instance().createCollectionOfXmlLocationFormatterData() );
	}

	public FormattedString createFormattedString( String text, Collection<FormatForText> styles )
	{
		return( FormatterFactory.instance().createFormattedString( text, styles ) );
	}

	public FormattedString createEmptyFormattedString( )
	{
		return( createFormattedString( "", null ) );
	}
}
