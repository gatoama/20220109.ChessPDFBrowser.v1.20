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
package com.frojasg1.libpdf.api.impl;

import com.frojasg1.libpdf.api.GlyphWrapper;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GlyphImpl implements GlyphWrapper
{
	protected int[] _codes = null;
	protected Rectangle _bounds = null;
	protected String _unicodeString = null;
	protected BufferedImage _image = null;

	public GlyphImpl( int[] codes, String unicodeString, Rectangle bounds )
	{
		_codes = codes;
		_bounds = bounds;
		_unicodeString = unicodeString;
	}

	@Override
	public int[] getCodes()
	{
		return( _codes );
	}

	@Override
	public Rectangle getBounds()
	{
		return( _bounds );
	}

	@Override
	public String getUnicodeString()
	{
		return( _unicodeString );
	}

	@Override
	public BufferedImage getImage()
	{
		return( _image );
	}

	public void setImage( BufferedImage image )
	{
		_image = image;
	}
}
