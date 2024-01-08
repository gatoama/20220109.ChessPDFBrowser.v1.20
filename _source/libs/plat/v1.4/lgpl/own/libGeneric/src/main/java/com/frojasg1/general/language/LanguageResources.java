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
package com.frojasg1.general.language;

import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.HexadecimalFunctions;
import com.frojasg1.general.ResourceFunctions;
import com.frojasg1.general.dialogs.filefilter.impl.GenericFileFilterChooserImpl;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.docformats.rtf.ZoomFontToRtfStream;
import com.frojasg1.general.files.FileStoreHacks;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.progress.GeneralUpdatingProgress;
import com.frojasg1.generic.zoom.ZoomFactorsAvailable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LanguageResources
{
	protected static LanguageResources _instance;

	protected List<LanguageResources> _listOfLanguageResources = new ArrayList<>();

	public static void changeInstance( LanguageResources inst )
	{
		_instance = inst;
	}

	public static LanguageResources instance()
	{
		if( _instance == null )
			_instance = new LanguageResources();
		return( _instance );
	}

	public void addLanguageResource( LanguageResources lr )
	{
		_listOfLanguageResources.add( lr );
	}

	protected void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
//		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ConfigurationParent.GLOBAL_CONF_FILE_NAME );
//		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", FileFunctions.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", HexadecimalFunctions.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ZoomFontToRtfStream.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", FileStoreHacks.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", IntegerFunctions.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", GeneralUpdatingProgress.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ZoomFactorsAvailable.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", GenericFileFilterChooserImpl.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", HighLevelDialogs.GLOBAL_CONF_FILE_NAME );
	}

	public void copyLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyOwnLanguageConfigurationFilesFromJar( newFolder );

		Iterator<LanguageResources> it = _listOfLanguageResources.iterator();
		while( it.hasNext() )
		{
			it.next().copyLanguageConfigurationFilesFromJar(newFolder);
		}
	}

	protected String getPropertiesPathInJar()
	{
		return( GenericConstants.sa_PROPERTIES_PATH_IN_JAR );
	}

	protected String getFileNameFromBaseFileName( String baseFileName )
	{
		String result = baseFileName;
		if( result != null )
		{
			String extension = FileFunctions.instance().getExtension(baseFileName);

			if( extension.equals( "properties" ) )
				result = FileFunctions.instance().cutOffExtension(result);

			final String formLanguageSuffix = "_LAN";
			if( ! result.endsWith( formLanguageSuffix ) )
				result += formLanguageSuffix;

			result += ".properties";
		}

		return( result );
	}

	protected void copyFormLanguageConfigurationFileFromJarToFolder( 
							String destinationFolder,
							String originLanguage,
							String baseFileName )
	{
		String fileName = getFileNameFromBaseFileName( baseFileName );

		copyLanguageConfigurationFileFromJarToFolder( destinationFolder,
			originLanguage, fileName );
	}

	protected void copyLanguageConfigurationFileFromJarToFolder( 
							String destinationFolder,
							String originLanguage,
							String fileName )
	{
		String longFileNameInJar = getPropertiesPathInJar() + "/" + originLanguage + "/" + fileName;
		String longFileNameInDisk = destinationFolder + ConfigurationParent.sa_dirSeparator + fileName;

		ResourceFunctions.instance().copyBinaryResourceToFile(longFileNameInJar, longFileNameInDisk );
	}
}
