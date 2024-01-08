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
package com.frojasg1.general.desktop.view;

import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import java.awt.Component;
import java.awt.Container;
import java.util.function.Function;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ContainerFunctions
{
	protected static ContainerFunctions _instance;

	public static void changeInstance( ContainerFunctions inst )
	{
		_instance = inst;
	}

	public static ContainerFunctions instance()
	{
		if( _instance == null )
			_instance = new ContainerFunctions();
		return( _instance );
	}

	public void addComposedComponentToParent( Container parent, Component comp )
	{
		if( ( parent != null ) && ( comp instanceof ComposedComponent ) )
		{
			ComposedComponent compComp = ( ComposedComponent ) comp;
			parent.setLayout( null );
			parent.add(comp);
			comp.setBounds( compComp.getInternalBounds() );
		}
	}

	public void addComponentToCompletelyFillParent( Container parent, Component comp )
	{
		if( ( parent != null ) && ( comp != null ) )
		{
//			parent.setLayout( null );
			parent.add(comp);
			comp.setBounds( 0, 0, parent.getWidth(), parent.getHeight() );
		}
	}

	public boolean isContentPane( Component comp )
	{
		boolean result = ( comp instanceof JPanel );
		if( result )
			result = "null.contentPane".equals( comp.getName() );

		return( result );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	public boolean isInsideSplitPane( Component comp )
	{
		
		Component pp = getIfNotNull(
						getIfNotNull( comp, Component::getParent ),
					Component::getParent );

		return( pp instanceof JSplitPane );
	}
}
