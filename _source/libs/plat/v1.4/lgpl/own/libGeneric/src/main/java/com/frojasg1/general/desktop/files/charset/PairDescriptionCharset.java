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
package com.frojasg1.general.desktop.files.charset;

/**
 *
 * @author Usuario
 */
public class PairDescriptionCharset
{
	protected String a_description;
	protected String a_charsetName;
	
	public PairDescriptionCharset( String description, String charsetName )
	{
		a_description = description;
		a_charsetName = charsetName;
	}

	@Override
	public String toString()
	{
		return( a_description );
	}
	
	public String M_getCharsetName()
	{
		return( a_charsetName );
	}
	
	void setDescription( String description )
	{
		a_description = description;
	}
	
	void setCharsetName( String charsetName )
	{
		a_charsetName = charsetName;
	}
}
