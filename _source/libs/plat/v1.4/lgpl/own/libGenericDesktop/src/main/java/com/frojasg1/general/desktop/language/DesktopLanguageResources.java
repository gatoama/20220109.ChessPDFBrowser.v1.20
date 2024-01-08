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
package com.frojasg1.general.desktop.language;

import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindowFunctions;
import com.frojasg1.general.desktop.GenericDesktopConstants;
import com.frojasg1.general.desktop.copypastepopup.ConfForTextPopupMenu;
import com.frojasg1.general.desktop.generic.files.DesktopBinaryFile;
import com.frojasg1.general.desktop.view.newversion.NewVersionFoundJDialog;
import com.frojasg1.general.desktop.view.panels.volume.VolumePanelInternationalTexts;
import com.frojasg1.general.desktop.view.search.DesktopSearchAndReplaceWindow;
import com.frojasg1.general.desktop.view.text.CustomizedJPasswordField;
import com.frojasg1.general.desktop.view.whatisnew.WhatIsNewJDialogBase;
import com.frojasg1.general.language.LanguageResources;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopLanguageResources extends LanguageResources
{
	protected static DesktopLanguageResources _instance;

	public static DesktopLanguageResources instance()
	{
		if( _instance == null )
		{
			_instance = new DesktopLanguageResources();
			LanguageResources.instance().addLanguageResource(_instance);
		}
		return( _instance );
	}

	@Override
	public void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ConfForTextPopupMenu.CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", CustomizedJPasswordField.sa_configurationBaseFileName );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", DesktopBinaryFile.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", JFrameInternationalization.GLOBAL_CONF_FILE_NAME );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", DesktopSearchAndReplaceWindow.sa_configurationBaseFileName );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", NewVersionFoundJDialog.sa_configurationBaseFileName );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", InternationalizedWindowFunctions.GLOBAL_CONF_FILE_NAME );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", WhatIsNewJDialogBase._configurationBaseFileName );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", DesktopSystemStringsConf.GLOBAL_CONF_FILE_NAME );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", VolumePanelInternationalTexts.GLOBAL_CONF_FILE_NAME );
	}

	protected String getPropertiesPathInJar()
	{
		return( GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR );
	}
}
