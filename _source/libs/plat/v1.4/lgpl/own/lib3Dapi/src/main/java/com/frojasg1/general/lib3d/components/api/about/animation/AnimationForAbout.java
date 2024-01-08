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
package com.frojasg1.general.lib3d.components.api.about.animation;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface AnimationForAbout<CC extends AnimationInitContext> {

	public void init( CC initContext );
	public CC getInitContext();

	public void animationReset();
	public void animationDoStep();
	public void animationStop();
	public void animationSetEnclosingDimension( Dimension dimension );
	public void animationSetDimension( Dimension dimension );

	public void animationSetMovingSpeedFactor( double factor );

	public Point animationGetPosition();
	public BufferedImage animationGetFrame();
}
