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
package com.frojasg1.general.commandline.generic;

import com.frojasg1.general.commandline.generic.dialogs.EmptyDialogsWrapper;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.commandline.generic.application.CommandLineApplicationFacilities;
import com.frojasg1.general.commandline.generic.files.DesktopFileFacilities;
import com.frojasg1.general.commandline.generic.system.DesktopSystem;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.files.GenericFileFacilities;
import com.frojasg1.general.keyboard.GenericKeyboard;
import com.frojasg1.general.view.ViewFacilities;
import com.frojasg1.generic.GenericFunctions;
import com.frojasg1.generic.GenericInterface;
import com.frojasg1.generic.application.ApplicationFacilitiesInterface;
import com.frojasg1.generic.languages.ObtainAvailableLanguages_base;
import com.frojasg1.generic.languages.ObtainAvailableLanguages_int;
import com.frojasg1.generic.system.SystemInterface;
import com.frojasg1.generic.zoom.ZoomFactorsAvailable;

/**
 *
 * @author Usuario
 */
public class CommandLineGenericFunctions implements GenericInterface
{
	BaseApplicationConfigurationInterface _appliConf = null;
	
	protected static CommandLineGenericFunctions _instance = null;

	protected ObtainAvailableLanguages_int _obtainAvailableLanguages = null;

	public static CommandLineGenericFunctions createInstance( BaseApplicationConfigurationInterface conf )
	{
		_instance = new CommandLineGenericFunctions( conf );
		GenericFunctions.register( _instance );

		return( _instance );
	}

	public static CommandLineGenericFunctions instance()
	{
		return( _instance );
	}

	public void setObtainAvailableLanguages( ObtainAvailableLanguages_int oal )
	{
		_obtainAvailableLanguages = oal;
	}

	public CommandLineGenericFunctions( BaseApplicationConfigurationInterface conf )
	{
		_appliConf = conf;
//		DesktopDialogsWrapper.createInstance( conf );

		if( ObtainAvailableLanguages_base.instance() == null )
		{
			if( conf != null )
			{
				String[] languages = new String[]{ "EN", "ES", "CAT" };
				String[] webLanguageNames = new String[]{ "English", "Espanyol", "Catala" };
				_obtainAvailableLanguages = ObtainAvailableLanguages_base.create(languages, languages,
																					webLanguageNames,
																					conf.getDefaultLanguageConfigurationFolder(""),
																					conf.getInternationalPropertiesPathInJar() );
			}
		}
		else
			_obtainAvailableLanguages = ObtainAvailableLanguages_base.instance();
	}

	@Override
	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	@Override
	public DialogsWrapper getDialogsWrapper()
	{
		return( EmptyDialogsWrapper.instance() );
	}

	@Override
	public GenericFileFacilities getFileFacilities()
	{
		return( DesktopFileFacilities.instance() );
	}

	@Override
	public GenericKeyboard getKeyboardFacilities()
	{
//		return( DesktopKeyboardFacilities.instance() );
		return( null );
	}

	@Override
	public ObtainAvailableLanguages_int getObtainAvailableLanguages()
	{
		return( _obtainAvailableLanguages );
	}

	@Override
	public ZoomFactorsAvailable getZoomFactorsAvailable()
	{
		return( ZoomFactorsAvailable.instance() );
	}

	@Override
	public ViewFacilities getViewFacilities()
	{
//		return( DesktopViewFacilitiesImp.instance() );
		return( null );
	}

	@Override
	public SystemInterface getSystem()
	{
		return( DesktopSystem.instance() );
	}

	@Override
	public ApplicationFacilitiesInterface getApplicationFacilities()
	{
		return( CommandLineApplicationFacilities.instance() );
	}
}
