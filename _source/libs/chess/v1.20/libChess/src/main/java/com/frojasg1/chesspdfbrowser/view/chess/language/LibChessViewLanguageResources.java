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
package com.frojasg1.chesspdfbrowser.view.chess.language;

import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.view.chess.editcomment.EditCommentFrame;
import com.frojasg1.chesspdfbrowser.view.chess.edittags.EditTAGsJFrame;
import com.frojasg1.chesspdfbrowser.view.chess.gamedata.GameDataJDialog;
import com.frojasg1.chesspdfbrowser.view.chess.initialposition.InitialPositionDialog;
import com.frojasg1.chesspdfbrowser.view.chess.multiwindowmanager.DetachedGameWindow;
import com.frojasg1.chesspdfbrowser.view.chess.regex.controller.RegexComboControllerForLineOfProfile;
import com.frojasg1.chesspdfbrowser.view.chess.regex.profile.LineOfTagsJPanel;
import com.frojasg1.general.language.LanguageResources;


/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LibChessViewLanguageResources extends LanguageResources
{
	protected static LibChessViewLanguageResources _instance;

	public static LibChessViewLanguageResources instance()
	{
		if( _instance == null )
		{
			_instance = new LibChessViewLanguageResources();
			LanguageResources.instance().addLanguageResource(_instance);
		}
		return( _instance );
	}

	@Override
	public void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", DetachedGameWindow.sa_configurationBaseFileName );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", EditCommentFrame.sa_configurationBaseFileName );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", EditTAGsJFrame.sa_configurationBaseFileName );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", InitialPositionDialog.a_configurationBaseFileName );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", LineOfTagsJPanel.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", RegexComboControllerForLineOfProfile.GLOBAL_CONF_FILE_NAME );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", GameDataJDialog.sa_configurationBaseFileName );
	}

	protected String getPropertiesPathInJar()
	{
		return( ApplicationConfiguration.sa_PROPERTIES_PATH_IN_JAR );
	}
}
