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
package com.frojasg1.desktop.liblens.graphics.lens.tx;

import com.frojasg1.desktop.liblens.graphics.Coordinate2D;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface RadiusTransformationInter
{
	// values for mode in the constructor
	public static final int SA_MODE_AMPLIFY = 0;
	public static final int SA_MODE_REDUCE = 1;

	public double transform( double radiusRatio );

	// Mode can be equal to Lens.SA_MODE_AMPLIFY or to Lens.SA_MODE_REDUCE
	public Coordinate2D calculateTransformation( double xx, double yy, double radius );
}
