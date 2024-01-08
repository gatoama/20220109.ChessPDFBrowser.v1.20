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
package com.frojasg1.chesspdfbrowser.model.regex.whole.loader;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.model.regex.conf.DefaultConstantsForRegexConf;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.items.ListOfRegexWholeFiles;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.persistency.XmlFileListPersistency;
import com.frojasg1.general.xml.persistency.container.ContainerOfModels;
import com.frojasg1.general.xml.persistency.loader.ModelToXml;
import com.frojasg1.general.xml.persistency.loader.XmlToModel;
import java.io.IOException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RegexFilesPersistency extends XmlFileListPersistency<String, RegexWholeFileModel>
{
	public static final String GLOBAL_CONF_FILE_NAME = "RegexFilesPersistency.properties";
	public static final String CONFIGURATION_PATH_IN_JAR = "com/frojasg1/app/chesspdfbrowser/xml/tagregex";

//	protected ListOfRegexWholeFiles _listOfRegexFiles = null;
//	protected ListOfRegexWholeFiles _lastListOfRegexFiles = null;

	public RegexFilesPersistency()//( BaseApplicationConfigurationInterface appConf )
	{
	}

	@Override
	public void init( String singleXmlFileName,
						String languageFolderResourceName,
						String languageFolderDiskFileName )
	{
		throw( new RuntimeException( "Do not init with this function. try calling: " +
									"init( BaseApplicationConfigurationInterface appliConf )" ) );
	}

	@Override
	public void init( BaseApplicationConfigurationInterface appliConf,
						String configurationPathInJar,
						String languageGlobalConfFileName,
						String languagePropertiesFolderInJar )
	{
		throw( new RuntimeException( "Do not init with this init function. try calling: " +
									"init( BaseApplicationConfigurationInterface appliConf )" ) );
	}

	public void init( BaseApplicationConfigurationInterface appliConf )
	{
		super.init( appliConf, CONFIGURATION_PATH_IN_JAR, GLOBAL_CONF_FILE_NAME,
					appliConf.getInternationalPropertiesPathInJar() );
	}

	@Override
	public ListOfRegexWholeFiles getModelContainer()
	{
		return( (ListOfRegexWholeFiles) super.getModelContainer() );
	}

	@Override
	protected ListOfRegexWholeFiles getPreviousModelContainer()
	{
		return( (ListOfRegexWholeFiles) super.getPreviousModelContainer() );
	}

	@Override
	public void setPreviousXmlModelContainer( ContainerOfModels<String, RegexWholeFileModel> prevModelContainer )
	{
		super.setPreviousXmlModelContainer(prevModelContainer);
	}

	@Override
	public void setCurrentModelContainer( ContainerOfModels<String, RegexWholeFileModel> currentContainer )
	{
		super.setCurrentModelContainer(currentContainer);
	}

	public String getDefaultGlobalRegexConfigurationFileName()
	{
		return( DefaultConstantsForRegexConf.DEFAULT_GLOBAL_CONF_FILE_NAME );
	}

	@Override
	protected String getDefaultGlobalFolderSingleName()
	{
		return( DefaultConstantsForRegexConf.CONF_REGEX_MAIN_FOLDER_DEFAULT );
	}

	@Override
	protected ListOfRegexWholeFiles createContainerOfXmlModels()
	{
		ListOfRegexWholeFiles result = new ListOfRegexWholeFiles();
		result.init( getAppliConf(), key -> createRelativeFileName(key) );

		return( result );
	}

	@Override
	protected String getLibOrApplicationVersionFolder()
	{
		return( getAppliConf().getApplicationNameFolder() );
	}

	@Override
	protected RegexWholeFileModel createFileModel( String fileName, XmlElement elem )
	{
		RegexWholeFileModel result = super.createFileModel( fileName, elem );
		result.setFileName(fileName);

		return( result );
	}

	@Override
	public void save() throws ConfigurationException, IOException
	{
		super.save();

		eraseNonExisting( );
	}

	@Override
	protected ModelToXml<RegexWholeFileModel> createModelToXmlTranslator()
	{
		return( new WholeRegexModelToXml() );
	}

	@Override
	protected XmlToModel<RegexWholeFileModel> createXmlToModelTranslator()
	{
		return( new XmlToWholeRegexModel() );
	}

	@Override
	protected String createRelativeFileName(String key) {
		return( key );
	}
}
