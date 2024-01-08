/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.general.genericdesktop.about.animations;

import com.frojasg1.general.lib3d.components.api.about.animation.torus.TorusAnimationForAbout;
import com.frojasg1.general.lib3d.components.api.about.animation.torus.TorusAnimationInitContext;
import com.frojasg1.general.lib3d.animations.impl.TorusAnimation;
import com.frojasg1.general.lib3d.components.Canvas3dJPanel;
import com.frojasg1.general.lib3d.scenarios.impl.TorusScenario;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TorusAnimationForAboutImpl implements TorusAnimationForAbout {

	protected TorusScenario _scenario = null;
	protected TorusAnimation _animation = null;
	protected Canvas3dJPanel _jPanel = null;

	protected TorusAnimationInitContext _initContext = null;

	@Override
	public void init(TorusAnimationInitContext initContext)
	{
		_initContext = initContext;
		_scenario = createScenario( _initContext );

		_animation = createAnimation();
		
		_jPanel = createCanvas3dJPanel();
	}

	@Override
	public TorusAnimationInitContext getInitContext()
	{
		return( _initContext );
	}

	protected TorusScenario createScenario( TorusAnimationInitContext initContext )
	{
		TorusScenario result = new TorusScenario();
		
		result.init( initContext );

		return( result );
	}

	protected TorusAnimation createAnimation()
	{
		TorusAnimation result = new TorusAnimation();
		result.init( _scenario );

		return( result );
	}

	protected Canvas3dJPanel createCanvas3dJPanel()
	{
		Canvas3dJPanel result = new Canvas3dJPanel();
		result.init( _scenario );

		result.add( _scenario.getCanvas3D() );
		// black to transparent
		result.setColorTranslation( 0xff000000, 0x0 );

		return( result );
	}

	@Override
	public void animationReset()
	{
		_animation.reset();
	}

	@Override
	public void animationDoStep()
	{
		_animation.doStep();
	}

	@Override
	public void animationStop()
	{
		_animation.stopAnimation();
	}

	@Override
	public void animationSetEnclosingDimension(Dimension dimension)
	{
		_animation.setEnclosingDimension(dimension);
	}

	@Override
	public void animationSetDimension(Dimension dimension)
	{
		_jPanel.setSize( dimension );
	}

	@Override
	public Point animationGetPosition()
	{
		return( _animation.getPosition() );
	}

	@Override
	public BufferedImage animationGetFrame()
	{
		return( _jPanel.getContentImage() );
	}

	@Override
	public void animationSetMovingSpeedFactor(double factor)
	{
		_animation.setMovingSpeedFactor(factor);
	}
}
