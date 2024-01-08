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
package com.frojasg1.applications.common.components.internationalization.radiobuttonmenu;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.util.Collection;
import java.util.EventObject;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ZoomFactorRadioButtonManagerInstance extends RadioButtonManagerInstance
{
	protected InternationalizedJFrame _frame;

	public ZoomFactorRadioButtonManagerInstance( ButtonGroup bg, JMenu menu, Component ancestor,
										BaseApplicationConfigurationInterface appConf,
										InternationalizedJFrame frame )
	{
		super( bg, menu, ancestor, appConf );
		_frame = frame;
	}

	@Override
	public void addItemToList(String item)
	{
		GenericFunctions.instance().getZoomFactorsAvailable().addZoomFactorPercentaje( item );
	}

	@Override
	public String getConfiguredItemToBeSelected()
	{
		double zoomFactor = getAppliConf().getZoomFactor();
		String item = GenericFunctions.instance().getZoomFactorsAvailable().getPercentageStr( zoomFactor );
		return( item );
	}

	@Override
	public Collection<String>[] getListsOfElementsForMenu()
	{
		Collection<String> col = GenericFunctions.instance().getZoomFactorsAvailable().getZoomFactorsAvailable();

		Collection<String>[] result = new Collection[1];

		result[0] = col;

		return( result );
	}

	@Override
	public void distpatchEvent(EventObject evt)
	{
		AbstractButton btn = getSelectedRadioButton();

		String currentZoomFactor = getConfiguredItemToBeSelected();
		
		if( !currentZoomFactor.equals( btn.getText() ) )
		{
			try
			{
				double newZoomFactor = GenericFunctions.instance().getZoomFactorsAvailable().getZoomFactor( btn.getText() );
				_frame.changeZoomFactor_centerMousePointer(newZoomFactor);
				getAppliConf().serverChangeZoomFactor(newZoomFactor);
			}
			catch( Throwable ex )
			{
				ex.printStackTrace();
			}
		}
	}
}
