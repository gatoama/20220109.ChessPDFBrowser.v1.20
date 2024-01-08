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
package com.frojasg1.general.desktop.startapp;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationFactory;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.init.InitGenericDesktop;
import com.frojasg1.general.desktop.lookAndFeel.ToolTipLookAndFeel;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.startapp.OpenConfigurationBase;
import java.util.function.Supplier;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class OpenConfigurationDesktopBase<AC extends BaseApplicationConfiguration>
	extends OpenConfigurationBase<AC>
{
	public OpenConfigurationDesktopBase( ) throws ConfigurationException
	{
	}

	@Override
	protected abstract BaseApplicationConfigurationFactory<AC> createApplicationConfigurationFactory();

	@Override
	public void init() throws ConfigurationException
	{
		super.init();
	}

	@Override
	protected abstract int getNumberOfSetsOfConfigurationsToImport();

	@Override
	protected abstract PairCurrentToArrayOfPossibleImports createImportElement( int index );

	@Override
	protected void createGenericFunctions()
	{
		DesktopGenericFunctions.createInstance( getAppliConf() );
	}

	@Override
	public void initializeAfterImportingConfiguration() throws ConfigurationException
	{
		ToolTipLookAndFeel.instance().init();
		ToolTipLookAndFeel.instance().registerToChangeZoomFactorAsObserver( getAppliConf() );
//		ToolTipLookAndFeel.instance().setAppliConf( getAppliConf() );

		super.initializeAfterImportingConfiguration();
	}


	@Override
	protected abstract String[] getBasicLanguages();
	@Override
	protected abstract String[] getAvailableLanguagesInJar();
	@Override
	protected abstract String[] getWebLanguages();

	@Override
	protected void initLibraries()
	{
		InitGenericDesktop.init( getAppliConf() );
	}
}
