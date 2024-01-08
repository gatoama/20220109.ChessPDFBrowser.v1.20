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
package com.frojasg1.chesspdfbrowser.recognizer.language;

import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.impl.ChessBoardVertexSetsDetector;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.impl.ChessBoardPositionRecognizerWithStoreImpl;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.loader.ChessBoardRecognitionPersistency;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.loader.XmlToChessFigurePatternSet;
import com.frojasg1.chesspdfbrowser.recognizer.threads.ChessBoardRecognizerCopyFenTask;
import com.frojasg1.chesspdfbrowser.recognizer.trainer.impl.ChessBoardPositionTrainerImpl;
import com.frojasg1.chesspdfbrowser.recognizer.utils.RecognitionUtils;
import com.frojasg1.general.language.LanguageResources;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LibChessBoardRecognitionLanguageResources extends LanguageResources
{
	protected static LibChessBoardRecognitionLanguageResources _instance;

	public static LibChessBoardRecognitionLanguageResources instance()
	{
		if( _instance == null )
		{
			_instance = new LibChessBoardRecognitionLanguageResources();
			LanguageResources.instance().addLanguageResource(_instance);
		}
		return( _instance );
	}

	@Override
	public void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", RecognitionUtils.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessBoardRecognizerCopyFenTask.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessBoardRecognitionPersistency.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessBoardPositionRecognizerWithStoreImpl.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessBoardGridResult.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessGamePositionBase.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessBoardVertexSetsDetector.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", XmlToChessFigurePatternSet.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ChessBoardPositionTrainerImpl.GLOBAL_CONF_FILE_NAME );
	}

	protected String getPropertiesPathInJar()
	{
		return( LibConstants.sa_PROPERTIES_PATH_IN_JAR );
	}
}
