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
package com.frojasg1.general.xml.persistency;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.persistence.PersistentConfiguration;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.language.LanguageXmlConfiguration;
import com.frojasg1.general.xml.model.KeyModel;
import com.frojasg1.general.xml.persistency.container.ContainerOfModels;
import com.frojasg1.general.xml.persistency.loader.ModelToXml;
import com.frojasg1.general.xml.persistency.loader.XmlToModel;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class XmlFileListPersistency<KK, MM extends KeyModel<KK>> extends LanguageXmlConfiguration
									implements PersistentConfiguration, InternationalizedStringConf
{
	protected String _languageGlobalConfFileName = null;
	protected static final String CONF_ERROR_WHEN_SAVING_XML_CONFIGURATION_FILE = "ERROR_WHEN_SAVING_XML_CONFIGURATION_FILE";

	protected  String _configurationPathInJar = null;
	protected String _languagePropertiesFolderInJar = null;
	protected InternationalizedStringConfImp _internationalizedStringConf = null;

//	protected RegexWholeFileModel _model = null;

	protected ContainerOfModels<KK, MM> _containerOfXmlModels = null;
	protected ContainerOfModels<KK, MM> _lastContainerOfXmlModels = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;


	@Override
	public void init( String singleXmlFileName,
						String languageFolderResourceName,
						String languageFolderDiskFileName )
	{
		throw( new RuntimeException( "Do not init with this function. try calling: " +
									"init( BaseApplicationConfigurationInterface appliConf )" ) );
	}

	public void init( BaseApplicationConfigurationInterface appliConf,
						String configurationPathInJar,
						String languageGlobalConfFileName,
						String languagePropertiesFolderInJar )
	{
		_languageGlobalConfFileName = languageGlobalConfFileName;
		_languagePropertiesFolderInJar = languagePropertiesFolderInJar;

		_internationalizedStringConf = new InternationalizedStringConfImp( languageGlobalConfFileName,
								languagePropertiesFolderInJar );

		registerInternationalizedStrings();


		_configurationPathInJar = configurationPathInJar;

		_appliConf = appliConf;

		_containerOfXmlModels = createContainerOfXmlModels();

		super.init( null,
					_configurationPathInJar,
					getFolderName() );
	}

	public ContainerOfModels<KK, MM> getModelContainer()
	{
		return( _containerOfXmlModels );
	}

	protected ContainerOfModels<KK, MM> getPreviousModelContainer()
	{
		return( _lastContainerOfXmlModels );
	}

	public void setPreviousXmlModelContainer( ContainerOfModels<KK, MM> lastContainer )
	{
		_lastContainerOfXmlModels = lastContainer;
	}

	public void setCurrentModelContainer( ContainerOfModels<KK, MM> currentContainer )
	{
		_containerOfXmlModels = currentContainer;
	}

	protected abstract String getDefaultGlobalFolderSingleName();

	protected abstract ContainerOfModels<KK, MM> createContainerOfXmlModels();

	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected abstract String getLibOrApplicationVersionFolder();

	protected String getFolderName()
	{
		return( System.getProperty("user.home") + File.separator  +
						getAppliConf().getConfigurationMainFolder() + File.separator +
						getLibOrApplicationVersionFolder() + File.separator +
						getDefaultGlobalFolderSingleName() );
	}

	@Override
	public void loadItems() throws ConfigurationException, IOException,
									ParserConfigurationException, SAXException
	{
		_containerOfXmlModels = createContainerOfXmlModels();
		_containerOfXmlModels.loadItemList();

		for( String item: _containerOfXmlModels.getComboBoxContent().getListOfItems() )
		{
			String relativeFileName = _containerOfXmlModels.getRelativeFileNameFromItemList(item);
			loadAndAddDocument( item, relativeFileName );
		}
	}

	protected MM createFileModel( String item, XmlElement elem )
	{
		MM result = createModelObject( elem );
//		result.setFileName(fileName);

//		_containerOfXmlModels.add( result );

		return( result );
	}

	protected MM loadAndAddDocument( String item, String relativeFileName ) throws IOException, ParserConfigurationException, SAXException
	{
		MM result = null;
		try
		{
			XmlElement elem = loadDocument( null, relativeFileName );
			result = createFileModel( item, elem );
			getModelContainer().add( result );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected Collection<MM> getCollectionOfModels()
	{
		return( getModelContainer().getCollectionOfModelItems() );
	}

	@Override
	public void save() throws ConfigurationException, IOException
	{
		_containerOfXmlModels.getComboBoxContent().save();

		for( MM model: getCollectionOfModels() )
		{
			try {
				saveXmlConfigurationFile( model );
			} catch (TransformerException ex) {
				ex.printStackTrace();
				throw( new RuntimeException( this.createCustomInternationalString( CONF_ERROR_WHEN_SAVING_XML_CONFIGURATION_FILE, ex.getMessage(), ex ) ) );
			}
		}

//		eraseNonExisting( );

		_lastContainerOfXmlModels = null;
	}

	protected boolean deleteFile( String xmlSingleFileName )
	{
		String folderName = getFolderName();
		String longFileName = folderName + File.separator + xmlSingleFileName;
		return( FileFunctions.instance().delete( longFileName ) );
	}

	protected abstract ModelToXml<MM> createModelToXmlTranslator();

	protected abstract String createRelativeFileName( KK key );

	protected String getXmlSingleFileName( MM model )
	{
		return( createRelativeFileName( model.getKey() ) );
	}

	public void saveXmlConfigurationFile( MM model ) throws IOException, TransformerException
	{
		ModelToXml<MM> builderToXml = createModelToXmlTranslator();
		XmlElement xmlElement = builderToXml.build( model );

		String language = null;
		saveDocument(xmlElement, language, getXmlSingleFileName( model ) );
	}

	protected abstract XmlToModel<MM> createXmlToModelTranslator();

	protected MM createModelObject( XmlElement xmlElement )
	{
		XmlToModel<MM> builder = createXmlToModelTranslator();
		MM model = builder.build( xmlElement );

		return( model );
	}

	public void exportXmlFile( MM model, String xmlFileName ) throws IOException, TransformerException
	{
		ModelToXml<MM> builderToXml = createModelToXmlTranslator();
		XmlElement xmlElement = builderToXml.build( model );

		saveDocumentCustomFileName( xmlElement, xmlFileName );
	}

	protected MM loadAndAddDocumentCustomFileName( String longFileName ) throws IOException, ParserConfigurationException, SAXException
	{
		String singleFileName = FileFunctions.instance().getBaseName(longFileName);
		XmlElement elem = loadDocumentCustomFileName( longFileName );

		MM result = createFileModel( singleFileName, elem );

		return( result );
	}

	public MM importOriginalXmlFile( String xmlFileName ) throws IOException, TransformerException, ParserConfigurationException, SAXException
	{
		MM result = loadAndAddDocument( xmlFileName, xmlFileName );

		saveXmlConfigurationFile( result );

		return( result );
	}

	public MM importXmlFile( String xmlFileName ) throws IOException, TransformerException, ParserConfigurationException, SAXException
	{
		MM result = loadAndAddDocumentCustomFileName( xmlFileName );

		saveXmlConfigurationFile( result );

		return( result );
	}

	protected void eraseNonExisting( )
	{
/*
		File dir = new File( getFolderName() );
		File[] filesToErase = dir.listFiles(
			(dir2, name) -> ( ( "xml".compareToIgnoreCase( FileFunctions.instance().getExtension(name) ) == 0 )
							&& !listOfFiles.contains( name ) )
											);
		for( File file: filesToErase )
			file.delete();
*/
		if( getPreviousModelContainer() != null )
		{
			getPreviousModelContainer().getComboBoxContent().getListOfItems().stream()
				.map( item -> getPreviousModelContainer().getRelativeFileNameFromItemList(item) )
				.filter( (fn) -> !getModelContainer().getComboBoxContent().contains( fn ) )
				.forEach( (fn) -> deleteFile( fn ) );
		}
	}

	/*
	public RegexWholeFileModel getRegexWholeContainer()
	{
		return( _model );
	}

	public void setRegexWholeContainer( RegexWholeFileModel rewc )
	{
		_model = rewc;
	}
*/

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_ERROR_WHEN_SAVING_XML_CONFIGURATION_FILE, "Error when saving xml configuration file name $1" );
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}
}
