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
package com.frojasg1.desktop.liblens.graphics.lens;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Usuario
 */
public class LensTransformationResult
{
	protected BufferedImage a_resultImage = null;
	protected Coordinate2D a_upLeftCorner = null;
	protected Coordinate2D a_downRightCorner = null;
	
	public LensTransformationResult( BufferedImage bi, Coordinate2D upLeftCorner,
										Coordinate2D downRightCorner )
	{
		a_resultImage = bi;
		a_upLeftCorner = upLeftCorner;
		a_downRightCorner = downRightCorner;
	}

	public BufferedImage M_getResultImage()	{	return( a_resultImage );	}
	public Coordinate2D M_getUpLeftCorner()	{	return( a_upLeftCorner );	}
	public Coordinate2D M_getDownRightCorner()	{	return( a_downRightCorner );	}
}
