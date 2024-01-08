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
package com.frojasg1.general.desktop.view.zoom.componentcopier.imp;

import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopier;
import com.frojasg1.general.desktop.view.zoom.componentcopier.CompCopierBase;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BasicArrowButtonCopier extends CompCopierBase<BasicArrowButton>
{

	@Override
	protected List<CompCopier<BasicArrowButton>> createCopiers() {

		List<CompCopier<BasicArrowButton>> result = new ArrayList<>();

		result.add( createDirectionCopier() );

		return( result );
	}

	protected CompCopier<BasicArrowButton> createDirectionCopier()
	{
		return( (originalComponent, newComponent) -> copyDirection( originalComponent, newComponent ) );
	}

	@Override
	public Class<BasicArrowButton> getParameterClass() {
		return( BasicArrowButton.class );
	}

	protected void copyDirection( BasicArrowButton originalComponent, BasicArrowButton newComponent )
	{
		newComponent.setDirection( originalComponent.getDirection() );
	}
}
