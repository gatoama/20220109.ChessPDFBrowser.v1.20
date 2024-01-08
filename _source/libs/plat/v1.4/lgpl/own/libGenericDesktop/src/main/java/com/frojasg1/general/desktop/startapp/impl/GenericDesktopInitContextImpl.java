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
package com.frojasg1.general.desktop.startapp.impl;

import com.frojasg1.general.desktop.startapp.GenericDesktopInitContext;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.factory.impl.ColorInversorFactoryImpl;
import com.frojasg1.general.lib3d.components.api.about.animation.AnimationForAboutFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenericDesktopInitContextImpl implements GenericDesktopInitContext
{
	protected String _applicationName = null;
	protected AnimationForAboutFactory _animationFactory = null;
	protected ColorInversor _colorInversor = null;

	@Override
	public void setApplicationName( String value )
	{
		_applicationName = value;
	}

	@Override
	public String getApplicationName()
	{
		return( _applicationName );
	}

	@Override
	public void setAnimationForAboutFactory( AnimationForAboutFactory animationFactory )
	{
		_animationFactory = animationFactory;
	}

	@Override
	public AnimationForAboutFactory getAnimationForAboutFactory()
	{
		return( _animationFactory );
	}

	@Override
	public void setColorInversor(ColorInversor ci)
	{
		_colorInversor = ci;
	}

	protected ColorInversor createColorInversor()
	{
		return( ColorInversorFactoryImpl.instance().createColorInversor() );
	}

	@Override
	public ColorInversor getColorInversor()
	{
		if( _colorInversor == null )
			_colorInversor = createColorInversor();

		return( _colorInversor );
	}
}
