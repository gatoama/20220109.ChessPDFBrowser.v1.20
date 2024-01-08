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

import com.frojasg1.general.string.StringFunctions;
import java.util.Collection;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FormattedString
{
	protected String _string = null;
	protected Collection< FormatForText > _formatCol = null;

	public FormattedString( String string, Collection< FormatForText > formatCol )
	{
		_string = string;
		_formatCol = formatCol;
	}

	public String getString()
	{
		return( _string );
	}

	public Collection< FormatForText > getFormatCol()
	{
		return( _formatCol );
	}

	public void append( FormattedString fs )
	{
		if( fs != null )
		{
			int size = length();

			append( fs.getString() );

			Collection< FormatForText > col = updateFormats( fs.getFormatCol(), size );

			if( _formatCol == null )
				_formatCol = col;
			else if( fs.getFormatCol() != null )
				_formatCol.addAll( col );
		}
	}

	public int length()
	{
		int result = 0;
		if( _string != null )
			result = _string.length();

		return( result );
	}

	public void append( String str )
	{
		if( str != null )
		{
			_string += str;
		}
		else
		{
			_string = str;
		}
	}

	public boolean isEmpty()
	{
		return( StringFunctions.instance().isEmpty( getString() ) );
	}

	protected Collection< FormatForText > updateFormats( Collection< FormatForText > inputCol,
														int size )
	{
		for( FormatForText fft: inputCol )
			fft.addOffset(size);

		return( inputCol );
	}
}
