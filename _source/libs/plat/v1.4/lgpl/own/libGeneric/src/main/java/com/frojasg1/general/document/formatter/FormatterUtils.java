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

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FormatterUtils
{
	protected static FormatterUtils _instance;

	public static void changeInstance( FormatterUtils instance )
	{
		_instance = instance;
	}

	public static FormatterUtils instance()
	{
		if( _instance == null )
			_instance = new FormatterUtils();

		return( _instance );
	}

	public FormatForText offsetFormatForText( FormatForText original, int offset )
	{
		FormatForText result = null;
		if( original != null )
		{
			result = FormatterFactory.instance().createFormatForText(original.getText(),
																	original.getStart() + offset,
																	original.getStyleName() );
		}

		return( result );
	}

	public void addLeftIndentation( FormattedText ft, int indentToAddToLeft )
	{
		if( ft != null )
		{
			ft.getAttributes().setLeftIndentation( ft.getAttributes().getLeftIndentation() + indentToAddToLeft );
			for( int ii=0; ii<ft.size(); ii++ )
				addLeftIndentation( ft.getElem(ii), indentToAddToLeft );
		}
	}

	public FormattedString getSingleFormattedString( FormattedText ft )
	{
		FormattedString result = null;
		
		if( ft != null )
		{
			result = ft.getFormattedString();
			FormattedText childFt = null;

			int index = 0;
			while( ( result == null ) && ( childFt = ft.getElem(index) ) != null ) 
			{
				result = getSingleFormattedString( childFt );
				index++;
			}
		}

		return( result );
	}
}
