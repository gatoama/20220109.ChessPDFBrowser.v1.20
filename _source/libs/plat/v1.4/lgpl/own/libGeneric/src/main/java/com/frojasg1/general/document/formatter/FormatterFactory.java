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
package com.frojasg1.general.document.formatter;

import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.general.document.formatted.FormattedString;
import com.frojasg1.general.document.formatted.text.FormattedText;
import com.frojasg1.general.document.formatter.xml.XmlLocationFormatterData;
import com.frojasg1.general.document.formatter.xml.XmlOutputFormatter;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FormatterFactory
{
	protected static FormatterFactory _instance;

	public static void changeInstance( FormatterFactory instance )
	{
		_instance = instance;
	}

	public static FormatterFactory instance()
	{
		if( _instance == null )
			_instance = new FormatterFactory();

		return( _instance );
	}

	public FormatForText createFormatForText( String text, int start, String styleName )
	{
		return( new FormatForText( text, start, styleName ) );
	}
/*
	public FormatForText createFormatForText( int start, int length, String styleName )
	{
		return( new FormatForText( start, length, styleName ) );
	}
*/
	public Collection<FormatForText> createCollectionOfFormatForText( )
	{
		return( new ArrayList<>() );
	}

	public XmlLocationFormatterData createXmlLocationFormatterData( String composedLocation,
															XmlOutputFormatter xmlFormatter )
	{
		return( new XmlLocationFormatterData( composedLocation, xmlFormatter) );
	}

	public Collection<XmlLocationFormatterData> createCollectionOfXmlLocationFormatterData()
	{
		return( new ArrayList<>() );
	}

	public FormattedString createFormattedString( String text, Collection<FormatForText> styles )
	{
		return( new FormattedString( text, styles ) );
	}
/*
	public FormattedText createFormattedText( String text, Collection<FormatForText> styles )
	{
		FormattedText result = createFormattedText();
		result.setFormattedString( createFormattedString( text, styles ) );

		return( result );
	}
*/
	public Collection<FormattedString> createCollectionOfFormattedString()
	{
		return( new ArrayList<>() );
	}

	public Collection<FormattedString> createSingleCollectionOfFormattedString( String text, String styleName )
	{
		Collection<FormattedString> result = createCollectionOfFormattedString();

		result.add( createSingleFormattedString( text, styleName ) );

		return( result );
	}

	public FormattedString createSingleFormattedString( String text, String styleName )
	{
		Collection<FormatForText> fftCol = createSingleCollectionOfFormatForText( text, styleName );
		FormattedString result = createFormattedString( text, fftCol );

		return( result );
	}

	public Collection<FormatForText> createSingleCollectionOfFormatForText( String text, String styleName )
	{
		Collection<FormatForText> result = createCollectionOfFormatForText();

		FormatForText fft = createFormatForText( text, 0, styleName );
		result.add( fft );

		return( result );
	}
/*
	public FormattedText createFormattedText()
	{
		return( new FormattedText() );
	}
*/
}
