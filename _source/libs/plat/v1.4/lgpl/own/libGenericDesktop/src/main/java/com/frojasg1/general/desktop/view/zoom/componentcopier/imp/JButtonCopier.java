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
import javax.swing.JButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JButtonCopier extends CompCopierBase<JButton>
{

	@Override
	protected List<CompCopier<JButton>> createCopiers() {

		List<CompCopier<JButton>> result = new ArrayList<>();

		result.add( createDefaultCapableCopier() );

		return( result );
	}

	protected CompCopier<JButton> createDefaultCapableCopier()
	{
		return( (originalComponent, newComponent) -> copyDefaultCapable( originalComponent, newComponent ) );
	}

	@Override
	public Class<JButton> getParameterClass() {
		return( JButton.class );
	}

	protected void copyDefaultCapable( JButton originalComponent, JButton newComponent )
	{
		newComponent.setDefaultCapable( originalComponent.isDefaultCapable() );
	}
}
