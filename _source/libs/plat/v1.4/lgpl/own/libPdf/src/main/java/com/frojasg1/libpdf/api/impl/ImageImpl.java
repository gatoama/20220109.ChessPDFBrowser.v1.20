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

import com.frojasg1.libpdf.api.ImageWrapper;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ImageImpl implements ImageWrapper
{
	protected BufferedImage _image = null;
	protected Rectangle _bounds = null;
	protected boolean _isBackground = false;
	protected int _numberOfOverlappingGlyphs = 0;

	public ImageImpl( BufferedImage image, Rectangle bounds )
	{
		_image = image;
		_bounds = bounds;
	}

	@Override
	public BufferedImage getImage()
	{
		return( _image );
	}

	@Override
	public Rectangle getBounds()
	{
		return( _bounds );
	}

	public boolean isBackground()
	{
		return( _isBackground );
	}

	public void setIsBackground( boolean value )
	{
		_isBackground = value;
	}

	public int getNumberOfOverlappingGlyphs() {
		return _numberOfOverlappingGlyphs;
	}

	public void setNumberOfOverlappingGlyphs(int _numberOfOverlappingGlyphs) {
		this._numberOfOverlappingGlyphs = _numberOfOverlappingGlyphs;
	}
}
