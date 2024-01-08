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

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.language.LanguageFlagIcons;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import com.frojasg1.general.desktop.view.zoom.components.ZoomJRadioButtonMenuItem;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LanguageRadioButtonManagerInstance extends RadioButtonManagerInstance
{
	protected static final Dimension DIMENSION_FOR_HUNDRED_PERCENT_ZOOM_FLAG = new Dimension( 24, 14 );


	public LanguageRadioButtonManagerInstance( ButtonGroup bg, JMenu menu, Component ancestor,
										BaseApplicationConfigurationInterface appConf )
	{
		super( bg, menu, ancestor, appConf );
	}

	@Override
	public void addItemToList(String item)
	{
//		GenericFunctions.instance().getObtainAvailableLanguages().newLanguageSetToConfiguration(item);
	}

	@Override
	public String getConfiguredItemToBeSelected()
	{
		return( getAppliConf().getLanguage() );
	}

	@Override
	public Collection<String>[] getListsOfElementsForMenu()
	{
		List<String> list1 = GenericFunctions.instance().getObtainAvailableLanguages().getTotalListOfAvailableLanguages().getListOfFixedLanguages();
		List<String> list2 = GenericFunctions.instance().getObtainAvailableLanguages().getTotalListOfAvailableLanguages().getListOfOtherLanguages();

		Collection<String>[] result = new Collection[2];

		result[0] = list1;
		result[1] = list2;

		return( result );
	}

	@Override
	public void distpatchEvent(EventObject evt)
	{
		AbstractButton btn = getSelectedRadioButton();

		String currentLanguage = getConfiguredItemToBeSelected();
		
		if( !currentLanguage.equals( btn.getText() ) )
		{
			try
			{
				getAppliConf().changeLanguage( btn.getText() );
				currentLanguage = btn.getText();
			}
			catch( Throwable ex )
			{
				ex.printStackTrace();
				String languageTmp = currentLanguage;
				try
				{
					getAppliConf().changeLanguage( languageTmp );
				}
				catch( Exception ex2 )
				{}
			}
		}
	}
/*
	@Override
	protected ZoomJRadioButtonMenuItem createAndUpdateRadioButton( String elem )
	{
		ZoomJRadioButtonMenuItem result = (ZoomJRadioButtonMenuItem) super.createAndUpdateRadioButton( elem );

		// language flags have not to be inverted.
		// as the UI does the inversion of the whole component,
		// the flag has to be inverted when necessary.
		// (double inversion makes colors be unaltered)
		result.setCanInvertIcons(true);

		invertIfNecessary( result );

		return( result );
	}
*/
	@Override
	protected ZoomJRadioButtonMenuItem createZoomJRadioButtonMenuItem( String elem )
	{
		ZoomJRadioButtonMenuItem result = null;

		ZoomIconImp zi = LanguageFlagIcons.instance().getFlagOfLanguage(elem, DIMENSION_FOR_HUNDRED_PERCENT_ZOOM_FLAG);

		if( zi == null )
			result = super.createZoomJRadioButtonMenuItem( elem );
		else
			result = new ZoomJRadioButtonMenuItem( elem, zi );

		return( result );
	}
}
