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
package com.frojasg1.chesspdfbrowser.engine.language;

import com.frojasg1.chesspdfbrowser.analysis.impl.AnalysisWindowViewControllerImpl;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.game.ChessGamePlayContext;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.parser.BlockToReplaceWith;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexLexicalAnalyser;
import com.frojasg1.chesspdfbrowser.model.regex.whole.items.ListOfRegexWholeFiles;
import com.frojasg1.chesspdfbrowser.model.regex.whole.loader.RegexFilesPersistency;
import com.frojasg1.chesspdfbrowser.model.regex.whole.loader.XmlToWholeRegexModel;
import com.frojasg1.chesspdfbrowser.startapp.StartApplicationStrings;
import com.frojasg1.chesspdfbrowser.view.chess.completion.TranslatorOfTypeForCompletion;
import com.frojasg1.chesspdfbrowser.view.chess.regex.RegexEditionJPanel;
import com.frojasg1.chesspdfbrowser.view.chess.regex.controller.RegexComboControllerBase;
import com.frojasg1.chesspdfbrowser.view.chess.regex.impl.BlockRegexOrProfileNameJPanel;
import com.frojasg1.chesspdfbrowser.view.chess.regex.impl.TagRegexNameJPanel;
import com.frojasg1.general.language.LanguageResources;


/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LibChessEngineLanguageResources extends LanguageResources
{
	protected static LibChessEngineLanguageResources _instance;

	public static LibChessEngineLanguageResources instance()
	{
		if( _instance == null )
		{
			_instance = new LibChessEngineLanguageResources();
			LanguageResources.instance().addLanguageResource(_instance);
		}
		return( _instance );
	}

	@Override
	public void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessStringsConf.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", RegexEditionJPanel.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder(newFolder, "EN", BlockRegexOrProfileNameJPanel.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder(newFolder, "EN", RegexComboControllerBase.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder(newFolder, "EN", RegexFilesPersistency.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", BlockToReplaceWith.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", LineModel.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", BlockRegexBuilder.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", RegexLexicalAnalyser.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ListOfRegexWholeFiles.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", XmlToWholeRegexModel.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "NewGameSetupJDial_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessGamePlayContext.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", AnalysisWindowViewControllerImpl.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "XmlFileNameValidationJDialog_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "TagsProfileConfJDialog_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "TagRegexConfJDialog_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", TagRegexNameJPanel.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "BlockRegexConfJDialog_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", TagRegexNameJPanel.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "AnalysisWindowJFrame_LAN.properties" );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", TranslatorOfTypeForCompletion.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", StartApplicationStrings.GLOBAL_CONF_FILE_NAME );
	}

	protected String getPropertiesPathInJar()
	{
		return( ApplicationConfiguration.sa_PROPERTIES_PATH_IN_JAR );
	}
}
