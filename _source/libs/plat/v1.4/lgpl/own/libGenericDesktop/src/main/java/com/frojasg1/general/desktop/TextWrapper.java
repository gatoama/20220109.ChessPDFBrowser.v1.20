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
package com.frojasg1.general.desktop;

import com.frojasg1.general.desktop.files.charset.CharsetManager;

/**
 *
 * @author axpe
 */
public class TextWrapper
{
	protected String _charsetName = null;

	public String getStringFromBytes( byte[] buffer )
	{
		String result = null;

		_charsetName = null;

		try
		{
			_charsetName = CharsetManager.instance().M_detectCharset( buffer );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		if( _charsetName == null )
			result = new String( buffer );
		else
		{
			try
			{
				result = new String( buffer, _charsetName );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
		return( result );
	}

	public byte[] encodeString( String str, String charsetName )
	{
		byte[] result = null;

		if( ( charsetName == null ) ||
			charsetName.equals( CharsetManager.instance().getAutodetectString() ) )
		{
			charsetName = _charsetName;
		}

		if( charsetName == null )
			result = str.getBytes();
		else
		{
			try
			{
				result = str.getBytes( charsetName );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				result = str.getBytes();
			}
		}

		return( result );
	}
}
