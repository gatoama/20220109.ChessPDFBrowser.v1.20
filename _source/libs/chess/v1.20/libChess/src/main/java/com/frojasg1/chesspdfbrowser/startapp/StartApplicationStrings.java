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

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class StartApplicationStrings extends InternationalizedStringConfImp
{
	public static final String GLOBAL_CONF_FILE_NAME = "StartApplication.properties";

	protected static final String CONF_ERROR_WHEN_LOADING_REGEX_FOR_TAGS_CONFIGURATION = "ERROR_WHEN_LOADING_REGEX_FOR_TAGS_CONFIGURATION";
	protected static final String CONF_ERROR_WHEN_LOADING_CHESS_BOARD_RECOGNIZER_FILES = "ERROR_WHEN_LOADING_CHESS_BOARD_RECOGNIZER_FILES";
	protected static final String CONF_ERROR_WHEN_LOADING_CHESS_ENGINE_CONFIGURATION_FILES = "ERROR_WHEN_LOADING_CHESS_ENGINE_CONFIGURATION_FILES";
	protected static final String CONF_ERROR_WHEN_TRYING_TO_BACKUP_PREVIOUS_REGEX_CONFIGURATION = "ERROR_WHEN_TRYING_TO_BACKUP_PREVIOUS_REGEX_CONFIGURATION";

	public StartApplicationStrings(BaseApplicationConfigurationInterface appliConf)
	{
		super( GLOBAL_CONF_FILE_NAME, appliConf.getInternationalPropertiesPathInJar() );		

		registerInternationalizedStrings();
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_ERROR_WHEN_LOADING_REGEX_FOR_TAGS_CONFIGURATION, "Error when loading regex for tags configuration files. $1" );
		this.registerInternationalString(CONF_ERROR_WHEN_LOADING_CHESS_BOARD_RECOGNIZER_FILES, "Error when loading ocr files. $1" );
		this.registerInternationalString(CONF_ERROR_WHEN_LOADING_CHESS_ENGINE_CONFIGURATION_FILES, "Error when loading chess engine configuration files. $1" );
		this.registerInternationalString(CONF_ERROR_WHEN_TRYING_TO_BACKUP_PREVIOUS_REGEX_CONFIGURATION, "Error when trying to backup previous regex configuration for TAG extraction. $1" );
	}
}
