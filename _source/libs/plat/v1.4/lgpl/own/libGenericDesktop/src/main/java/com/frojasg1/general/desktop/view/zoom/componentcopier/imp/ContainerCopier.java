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
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.plaf.basic.DefaultMenuLayout;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ContainerCopier extends CompCopierBase<Container>
{

	@Override
	protected List<CompCopier<Container>> createCopiers() {

		List<CompCopier<Container>> result = new ArrayList<>();

		result.add( createLayoutCopier() );

		return( result );
	}

	protected CompCopier<Container> createLayoutCopier()
	{
		return( (originalComponent, newComponent) -> copyLayout( originalComponent, newComponent ) );
	}

	@Override
	public Class<Container> getParameterClass() {
		return( Container.class );
	}

	protected BoxLayout duplicateBoxLayout( BoxLayout original, Container newContainer )
	{
		BoxLayout result = new BoxLayout( newContainer, original.getAxis() );

		return( result );
	}

	protected DefaultMenuLayout duplicateDefaultMenuLayout( DefaultMenuLayout original, Container newContainer )
	{
		DefaultMenuLayout result = new DefaultMenuLayout( newContainer, original.getAxis() );

		return( result );
	}

	protected LayoutManager duplicate( LayoutManager original, Container newContainer )
	{
		LayoutManager result = null;
		if( original != null )
		{
			String className = original.getClass().getName();
			if( className.equals( "javax.swing.BoxLayout" ) )
			{
				result = duplicateBoxLayout( (BoxLayout) original, newContainer );
			}
			else if( className.equals( "javax.swing.plaf.basic.DefaultMenuLayout" ) )
			{
				result = duplicateDefaultMenuLayout( (DefaultMenuLayout) original, newContainer );
			}
			else
			{
				throw( new IllegalArgumentException( "Not implemented duplication of layout class: " + className ) );
			}
		}

		return( result );
	}

	protected void copyLayout( Container originalComponent, Container newComponent )
	{
		LayoutManager lm = originalComponent.getLayout();

		if( ( lm != null ) && ( newComponent.getLayout() == null ) )
		{
			LayoutManager newLm = duplicate( lm, newComponent );
			newComponent.setLayout( newLm );
		}
	}
}
