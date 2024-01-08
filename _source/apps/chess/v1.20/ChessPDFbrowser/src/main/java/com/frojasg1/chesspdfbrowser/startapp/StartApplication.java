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
package com.frojasg1.chesspdfbrowser.startapp;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.chesspdfbrowser.application.LicenseJDialog;
import com.frojasg1.chesspdfbrowser.application.MainWindow;
import com.frojasg1.chesspdfbrowser.application.Splash;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.general.desktop.lookAndFeel.ToolTipLookAndFeel;
import com.frojasg1.general.desktop.startapp.OpenConfigurationDesktopBase;
import com.frojasg1.general.desktop.startapp.StartApplicationBase;
import com.frojasg1.general.desktop.view.license.GenericLicenseJDialog;
import com.frojasg1.general.desktop.view.splash.GenericBasicSplash;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.LookAndFeel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class StartApplication extends StartApplicationBase<ApplicationInitContext>
{

//	protected static final int MINIMUM_NUMBER_OF_STEPS = 3;
//	protected StartApplicationStrings _internationalizedStringConf = null;

	@Override
	protected GenericBasicSplash createSplash()
	{
		return( Splash.instance() );
	}

	@Override
	protected OpenConfigurationDesktopBase createOpenConfiguration() throws ConfigurationException
	{
		return( new OpenConfiguration( getApplicationContext() ) );
	}

	@Override
	protected ApplicationConfiguration getAppliConf()
	{
		return( ApplicationConfiguration.instance() );
	}

	@Override
	protected GenericLicenseJDialog createLicenseJDialog(Consumer<InternationalizationInitializationEndCallback> initializationCallback)
	{
		return( new LicenseJDialog( (JDialog)null, getAppliConf(), initializationCallback ) );
	}

	@Override
	protected void saveApplicationConfiguration() throws ConfigurationException
	{
		getAppliConf().M_saveConfiguration();
	}

	@Override
	protected Component createMainWindow() throws ConfigurationException
	{
//		_initContext.getRegexConfWholeContainer().init( );

		return( new MainWindow( getApplicationContext() ) );
	}
/*
	@Override
	protected int getMinimumNumberOfSteps()
	{
		return( MINIMUM_NUMBER_OF_STEPS );
	}

	protected RegexWholeContainer createRegexConfWholeContainer()
	{
		RegexWholeContainer result = new RegexWholeContainer();

		return( result );
	}
*/
/*
	protected void changeConfigurationDependingOnTheVersionImported()
	{
//		String importedVersion = getOpenConfiguration().getImportedVersion( OpenConfiguration.INDEX_MAIN_CONFIGURATION );
//		if( ( importedVersion != null ) && ( importedVersion.compareTo( "v1.2" ) < 0 ) )
		if( wasANewVersion() )
		{
			getAppliConf().setConfigurationOfChessLanguageToShow( ChessLanguageConfiguration.ALGEBRAIC_FIGURINE_NOTATION );
			getAppliConf().setChessFigurineSet( FigureSet.VIRTUAL_PIECES_SET );

			eraseLanguageConfigurationFileRecursive( "MainWindow_LAN.properties" );
		}
	}
*/
}
