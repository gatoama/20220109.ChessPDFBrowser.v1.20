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
package com.frojasg1.chesspdfbrowser.application.language;

import com.frojasg1.chesspdfbrowser.application.tasks.OpenSetPositionWindowAndTrainer;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.startapp.StartApplication;
import com.frojasg1.general.language.LanguageResources;


/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessPDFbrowserApplicationLanguageResources extends LanguageResources
{
	protected static ChessPDFbrowserApplicationLanguageResources _instance;

	public static ChessPDFbrowserApplicationLanguageResources instance()
	{
		if( _instance == null )
		{
			_instance = new ChessPDFbrowserApplicationLanguageResources();
			LanguageResources.instance().addLanguageResource(_instance);
		}
		return( _instance );
	}

	@Override
	public void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "AboutText.rtf" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "AppStringsConf.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "JDial_about_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "JDial_applicationConfiguration_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "LicenseJDialog_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "MainWindow_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "PDFScanProgressWindow_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "PDFviewerWindow_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "Splash_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "StartStringsConf.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", OpenSetPositionWindowAndTrainer.GLOBAL_CONF_FILE_NAME );

		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "Application.License.rtf" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "What.is.new.rtf" );
	}

	protected String getPropertiesPathInJar()
	{
		return( ApplicationConfiguration.sa_PROPERTIES_PATH_IN_JAR );
	}
}
