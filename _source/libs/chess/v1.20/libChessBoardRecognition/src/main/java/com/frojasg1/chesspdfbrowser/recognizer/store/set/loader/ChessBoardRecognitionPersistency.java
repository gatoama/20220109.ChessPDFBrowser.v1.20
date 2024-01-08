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
package com.frojasg1.chesspdfbrowser.recognizer.store.set.loader;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.recognizer.constants.DefaultConstantsForChessPatternsConf;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.store.ChessBoardRecognitionStore;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.persistency.XmlFileListPersistency;
import com.frojasg1.general.xml.persistency.container.ContainerOfModels;
import com.frojasg1.general.xml.persistency.loader.ModelToXml;
import com.frojasg1.general.xml.persistency.loader.XmlToModel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardRecognitionPersistency extends XmlFileListPersistency<String, ChessFigurePatternSet>
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessBoardRecognitionPersistency.properties";
	public static final String CONFIGURATION_PATH_IN_JAR = "com/frojasg1/app/chesspdfbrowser/xml/tagregex";

	public ChessBoardRecognitionPersistency()
	{
	}

	@Override
	public void init( String singleXmlFileName,
						String languageFolderResourceName,
						String languageFolderDiskFileName )
	{
		throw( new RuntimeException( "Do not init with this init function. try calling: " +
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
					LibConstants.sa_PROPERTIES_PATH_IN_JAR );
	}

	public ChessBoardRecognitionStore getModelContainer()
	{
		return( (ChessBoardRecognitionStore) super.getModelContainer() );
	}

	protected ChessBoardRecognitionStore getPreviousModelContainer()
	{
		return( (ChessBoardRecognitionStore) super.getPreviousModelContainer() );
	}

	public ChessBoardRecognitionStore getChessBoardRecognitionStore()
	{
		return( getModelContainer() );
	}

	@Override
	public void setPreviousXmlModelContainer( ContainerOfModels<String, ChessFigurePatternSet> prevModelContainer )
	{
		super.setPreviousXmlModelContainer(prevModelContainer);
	}

	@Override
	public void setCurrentModelContainer( ContainerOfModels<String, ChessFigurePatternSet> currentContainer )
	{
		super.setCurrentModelContainer(currentContainer);
	}

	public String getDefaultPatternSetConfigurationFileName()
	{
		return( DefaultConstantsForChessPatternsConf.DEFAULT_CONF_PATTERN_SET_FILE_NAME );
	}

	@Override
	protected String getDefaultGlobalFolderSingleName()
	{
		return( DefaultConstantsForChessPatternsConf.CONF_CHESS_OCR_PATTERNS_MAIN_FOLDER_DEFAULT );
	}

	@Override
	protected ChessBoardRecognitionStore createContainerOfXmlModels()
	{
		ChessBoardRecognitionStore result = new ChessBoardRecognitionStore();
		result.init( getAppliConf(), key -> createRelativeFileName(key) );

		return( result );
	}

	protected String getLibOrApplicationVersionFolder()
	{
		return( LibConstants.LIB_VERSION_MAIN_FOLDER );
	}

	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected ChessFigurePatternSet createFileModel( String fileName, XmlElement elem )
	{
		ChessFigurePatternSet result = super.createFileModel( fileName, elem );
		result.setHasBeenModified( false );

		getModelContainer().add( result );

		return( result );
	}

	protected List<String> getAndCopyListOfItems()
	{
		return( new ArrayList<>( getModelContainer().getComboBoxContent().getListOfItems() ) );
	}

	@Override
	public void loadItems() throws ConfigurationException, IOException,
									ParserConfigurationException, SAXException
	{
		setCurrentModelContainer( createContainerOfXmlModels() );
		getModelContainer().loadItemList();

		List<String> itemListCopy = getAndCopyListOfItems();
		for( String item: itemListCopy )
			loadAndAddDocument( item, null );
	}

	protected ChessFigurePatternSet loadAndAddDocument( String patternName,
														String relativeFileName ) throws IOException, ParserConfigurationException, SAXException
	{
		String longFolderName = getFolderName() + File.separator + patternName;
		String longFileName = longFolderName + File.separator + getDefaultPatternSetConfigurationFileName();

		XmlElement elem = loadDocumentCustomFileName( longFileName );
		ChessFigurePatternSet result = createFileModel( patternName, elem );

		loadImages( result, longFolderName );

		// for backwards compatibility
		if( result.isMeanErrorThresholdOfAnyElementEmpty() )
			result.recalculateErrorThresholds();

		return( result );
	}

	protected void loadImages( ChessFigurePatternSet ps, String longFolderName ) throws IOException
	{
		for( List<ChessFigurePattern> list: ps.getMap().values() )
			for( ChessFigurePattern pattern: list )
			{
				loadImage( pattern, longFolderName );
				loadSummarizedImage( pattern, longFolderName );
			}
	}

	protected void loadImage( ChessFigurePattern pattern, String longFolderName ) throws IOException
	{
		String longFileName = longFolderName + File.separator + pattern.getName() + ".tiff";
		pattern.setImage( loadImage(longFileName) );
	}

	protected void loadSummarizedImage( ChessFigurePattern pattern, String longFolderName ) throws IOException
	{
		String longFileName = longFolderName + File.separator + pattern.getName() + "_summarized.tiff";
		pattern.setSummarizedImage( loadImage(longFileName) );
	}

	protected BufferedImage loadImage( String longFileName ) throws IOException
	{
		BufferedImage result = null;
		File file =  new File(longFileName);
		if( FileFunctions.instance().isFile(longFileName) )
			result = ImageIO.read(file);

		return( result );
	}

	@Override
	public void save() throws ConfigurationException, IOException
	{
		super.save();
	}

	public void saveXmlConfigurationFile( ChessFigurePatternSet ps ) throws IOException, TransformerException
	{
		if( ( ps != null ) && ps.hasBeenModified() )
		{
			ChessFigurePatternSetToXml builderToXml = new ChessFigurePatternSetToXml();
			XmlElement xmlElement = builderToXml.build( ps );

			String longFolderName = getFolderName() + File.separator + ps.getSingleFolderName();

			String longFileName = longFolderName + File.separator + getDefaultPatternSetConfigurationFileName();
			FileFunctions.instance().createFolder( FileFunctions.instance().getDirName(longFileName) );
			saveDocumentCustomFileName( xmlElement, longFileName );

			saveImages( ps, longFolderName );
		}
	}

	protected void saveImages( ChessFigurePatternSet obj, String longFolderName ) throws IOException
	{
		for( List<ChessFigurePattern> list: obj.getMap().values() )
			for( ChessFigurePattern pattern: list )
			{
				saveImage( pattern, longFolderName );
				saveSummarizedImage( pattern, longFolderName );
			}
	}

	protected void saveImage( BufferedImage image, String longFileName ) throws IOException
	{
		File file =  new File(longFileName);
		ImageIO.write(image, "TIFF", file);
	}

	protected void saveImage( ChessFigurePattern pattern, String longFolderName ) throws IOException
	{
		String longFileName = longFolderName + File.separator + pattern.getName() + ".tiff";
		saveImage(pattern.getImage(), longFileName);
	}

	protected void saveSummarizedImage( ChessFigurePattern pattern, String longFolderName ) throws IOException
	{
		BufferedImage summImage = pattern.getSummarizedImage();
		if( summImage != null )
		{
			String longFileName = longFolderName + File.separator + pattern.getName() + "_summarized.tiff";
			saveImage(summImage, longFileName);
		}
	}

	protected XmlToModel<ChessFigurePatternSet> createXmlToModelTranslator()
	{
		XmlToChessFigurePatternSet result = new XmlToChessFigurePatternSet();
		result.init();
		return( result );
	}

	@Override
	protected ModelToXml<ChessFigurePatternSet> createModelToXmlTranslator() {
		ChessFigurePatternSetToXml result = new ChessFigurePatternSetToXml();

		return( result );
	}

	@Override
	protected String getXmlSingleFileName(ChessFigurePatternSet model) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected String createRelativeFileName(String key)
	{
		return( key );
	}
}
