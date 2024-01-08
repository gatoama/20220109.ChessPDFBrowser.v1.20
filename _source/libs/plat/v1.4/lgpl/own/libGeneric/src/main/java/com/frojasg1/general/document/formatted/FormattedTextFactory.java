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
package com.frojasg1.general.document.formatted;

import com.frojasg1.general.document.formatted.text.FormattedText;
import com.frojasg1.general.document.formatted.text.imp.FormattedGenericText;
import com.frojasg1.general.document.formatted.text.imp.FormattedTextList;
import com.frojasg1.general.document.formatted.text.imp.FormattedTextListElem;
import com.frojasg1.general.document.formatted.text.imp.FormattedTextParagraph;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FormattedTextFactory
{
	protected static FormattedTextFactory _instance;

	public static void changeInstance( FormattedTextFactory instance )
	{
		_instance = instance;
	}

	public static FormattedTextFactory instance()
	{
		if( _instance == null )
			_instance = new FormattedTextFactory();

		return( _instance );
	}

	public FormattedText createFormattedTextList( FormattedTextList.Type type )
	{
		return( new FormattedTextList( type ) );
	}

	public FormattedText createFormattedTextListElem()
	{
		return( new FormattedTextListElem( ) );
	}

	public FormattedText createFormattedTextParagraph()
	{
		return( new FormattedTextParagraph( ) );
	}

	public FormattedText createFormattedGenericText()
	{
		return( new FormattedGenericText() );
	}
}
