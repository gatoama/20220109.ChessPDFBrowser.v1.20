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
package com.frojasg1.general.desktop.view.zoom;

import com.frojasg1.general.desktop.view.zoom.ui.ComponentUIforZoomInterface;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.general.string.StringFunctions;
import javax.swing.AbstractButton;
import javax.swing.JSlider;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SliderUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomFunctions {
	
	protected static final String ROOT_PACKAGE_FOR_UI = "com.frojasg1.general.desktop.view.zoom.ui";

	protected static ZoomFunctions _instance;

	public static void changeInstance( ZoomFunctions inst )
	{
		_instance = inst;
	}

	public static ZoomFunctions instance()
	{
		if( _instance == null )
			_instance = new ZoomFunctions();
		return( _instance );
	}

	public void switchToZoomUI( ZoomComponentInterface comp,
								DoubleReference zoomFactor )
	{
		ComponentUI compUi = null;

		compUi = comp.getUI();

		if( compUi != null )
		{
			Class newClassCompUi = getZoomUIClass( compUi );

			changeZoomUI( comp, zoomFactor, newClassCompUi );
		}
	}

	public void updateZoomUI( ZoomComponentInterface comp,
								DoubleReference zoomFactor )
	{
		ComponentUI compUi = null;

		compUi = comp.getUI();

		if( compUi instanceof ComponentUIforZoomInterface )
		{
			Class newClassCompUi = compUi.getClass();

			changeZoomUI( comp, zoomFactor, newClassCompUi );
		}
	}

	protected void changeZoomUI( ZoomComponentInterface comp,
								DoubleReference zoomFactor,
								Class newClassCompUi )
	{
		ComponentUI compUi = null;

		if( newClassCompUi != null )
		{
			ComponentUI newUi = createZoomUI( newClassCompUi, zoomFactor );

			if( newUi instanceof ComponentUIforZoomInterface )
			{
				( (ComponentUIforZoomInterface) newUi).setZoomFactorReference(zoomFactor);
				( (ComponentUIforZoomInterface) newUi).init();
			}

			if( ( comp instanceof AbstractButton ) &&
				( newUi instanceof ButtonUI ) )
			{
				( (AbstractButton) comp ).setUI( ( ButtonUI ) newUi );
			}
			else if( ( comp instanceof JSlider ) &&
					( newUi instanceof SliderUI ) )
			{
				( (JSlider) comp ).setUI( ( SliderUI ) newUi );
			}
			else
			{
				comp.setUI( newUi );
			}
		}
	}

	protected Class getZoomUIClass( ComponentUI compUi )
	{
		Class result = null;

		String uiClassName = StringFunctions.instance().getSimpleClassName( compUi.getClass() );
		String zoomClassName = ROOT_PACKAGE_FOR_UI + ".Zoom" + uiClassName;

		try
		{
			result = Class.forName( zoomClassName );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			result = null;
		}

		return( result );
	}

	protected ComponentUI createZoomUI( Class zoomUiClass, DoubleReference zoomFactor )
	{
		ComponentUI result = null;

		try
		{
			Object resultObj = zoomUiClass.getConstructor().newInstance();
			if( resultObj instanceof ComponentUI )
			{
				result = (ComponentUI) resultObj;
				
				if( result instanceof ComponentUIforZoomInterface )
				{
					ComponentUIforZoomInterface newUi = (ComponentUIforZoomInterface) result;
					
					newUi.init();
					newUi.setZoomFactorReference( zoomFactor );
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}
}
