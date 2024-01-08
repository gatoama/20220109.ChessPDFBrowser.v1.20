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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl;

import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.InputElement;
import com.frojasg1.libpdf.api.ImageWrapper;
import java.awt.Point;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InputImage implements InputElement
{
	protected ImageWrapper _image = null;
	protected Point _pointToStartRecognition = null;

	public InputImage( ImageWrapper image, Point pointToStartRecognition )
	{
		_image = image;
		_pointToStartRecognition = pointToStartRecognition;
	}

	public ImageWrapper getImage() {
		return _image;
	}

	public Point getPointToStartRecognition() {
		return _pointToStartRecognition;
	}
}
