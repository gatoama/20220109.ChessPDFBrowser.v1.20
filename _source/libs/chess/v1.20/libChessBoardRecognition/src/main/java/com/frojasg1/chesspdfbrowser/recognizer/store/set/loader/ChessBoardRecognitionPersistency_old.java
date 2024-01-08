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

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.recognizer.constants.DefaultConstantsForChessPatternsConf;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.store.ChessBoardRecognitionStore;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.persistence.PersistentConfiguration;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.language.LanguageXmlConfiguration;
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
public class ChessBoardRecognitionPersistency_old extends LanguageXmlConfiguration
									implements PersistentConfiguration, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessBoardRecognitionPersistency.properties";
	protected static final String CONF_ERROR_WHEN_SAVING_XML_CONFIGURATION_FILE = "ERROR_WHEN_SAVING_XML_CONFIGURATION_FILE";

	public static final String CONFIGURATION_PATH_IN_JAR = "com/frojasg1/app/chesspdfbrowser/xml/tagregex";

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

//	protected RegexWholeFileModel _model = null;

	protected ChessBoardRecognitionStore _chessBoardRecognitionStore = null;
	protected ChessBoardRecognitionStore _lastChessBoardRecognitionStore = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

	public ChessBoardRecognitionPersistency_old()//( BaseApplicationConfigurationInterface appConf )
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
/*		super( appConf, GLOBAL_CONF_FILE_NAME,
				CONFIGURATION_PATH_IN_JAR,
				appConf.getDefaultLanguageBaseConfigurationFolder() );
*/
	}

	@Override
	public void init( String singleXmlFileName,
						String languageFolderResourceName,
						String languageFolderDiskFileName )
	{
		throw( new RuntimeException( "Do not init with this function. try calling: " +
									"init( BaseApplicationConfigurationInterface appliConf )" ) );
	}

	public void init( BaseApplicationConfigurationInterface appliConf )
	{
		_appliConf = appliConf;

		super.init( null,
					CONFIGURATION_PATH_IN_JAR,
					getFolderName() );
	}

	public ChessBoardRecognitionStore getChessBoardRecognitionStore()
	{
		return( _chessBoardRecognitionStore );
	}

	public void setPreviousChessboardRecognitionStore( ChessBoardRecognitionStore ocrStore )
	{
		_lastChessBoardRecognitionStore = ocrStore;
	}

	public void setChessBoardRecognitionStore( ChessBoardRecognitionStore ocrStore )
	{
		_chessBoardRecognitionStore = ocrStore;
	}

	public String getDefaultPatternSetConfigurationFileName()
	{
		return( DefaultConstantsForChessPatternsConf.DEFAULT_CONF_PATTERN_SET_FILE_NAME );
	}

	protected String getDefaultGlobalFolderSingleName()
	{
		return( DefaultConstantsForChessPatternsConf.CONF_CHESS_OCR_PATTERNS_MAIN_FOLDER_DEFAULT );
	}

	protected ChessBoardRecognitionStore createChessBoardRecognitionStore()
	{
		ChessBoardRecognitionStore result = new ChessBoardRecognitionStore();
		result.init( getAppliConf(), null );

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
		_chessBoardRecognitionStore = createChessBoardRecognitionStore();
		_chessBoardRecognitionStore.loadItemList();
/*
		List<String> patternNameList = new ArrayList<>( _chessBoardRecognitionStore.getListOfFiles() );
		for( String patternName: patternNameList )
		{
			loadAndAddDocument( patternName );
		}
*/
	}

	protected ChessFigurePatternSet createAndAddChessFigurePatternSet( String fileName, XmlElement elem )
	{
		ChessFigurePatternSet result = createChessFigurePatternSet( elem );
		result.setHasBeenModified( false );
//		result.setFileName(fileName);

		_chessBoardRecognitionStore.add( result );

		return( result );
	}

	protected ChessFigurePatternSet loadAndAddDocument( String patternName ) throws IOException, ParserConfigurationException, SAXException
	{
		String longFolderName = getFolderName() + File.separator + patternName;
		String longFileName = longFolderName + File.separator + getDefaultPatternSetConfigurationFileName();

		XmlElement elem = loadDocumentCustomFileName( longFileName );
		ChessFigurePatternSet result = createAndAddChessFigurePatternSet( patternName, elem );

		loadImages( result, longFolderName );

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
		_chessBoardRecognitionStore.getComboBoxContent().save();

		for( List<ChessFigurePatternSet> list: _chessBoardRecognitionStore.getMap().values() )
			for( ChessFigurePatternSet ps: list )
			{
				try {
					saveXmlConfigurationFile( ps );
				} catch (TransformerException ex) {
					ex.printStackTrace();
					throw( new RuntimeException( this.createCustomInternationalString( CONF_ERROR_WHEN_SAVING_XML_CONFIGURATION_FILE, ex.getMessage(), ex ) ) );
				}
			}

//		eraseNonExisting( );

		_lastChessBoardRecognitionStore = null;
	}
/*
	protected void eraseNonExisting( )
	{
		if( _lastListOfRegexFiles != null )
		{
			_lastListOfRegexFiles.getListOfFiles().stream()
				.filter( (fn) -> !_listOfRegexFiles.getComboBoxContent().contains( fn ) )
				.forEach( (fn) -> deleteFile( fn ) );
		}
	}

	protected boolean deleteFile( String xmlSingleFileName )
	{
		String folderName = getFolderName();
		String longFileName = folderName + File.separator + xmlSingleFileName;
		return( FileFunctions.instance().delete( longFileName ) );
	}
*/
	protected void saveXmlConfigurationFile( ChessFigurePatternSet ps ) throws IOException, TransformerException
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

	protected ChessFigurePatternSet createChessFigurePatternSet( XmlElement xmlElement )
	{
		XmlToChessFigurePatternSet builder = new XmlToChessFigurePatternSet();
		ChessFigurePatternSet result = builder.build( xmlElement );

		return( result );
	}
/*
	public void exportXmlFile( RegexWholeFileModel refm, String xmlFileName ) throws IOException, TransformerException
	{
		ChessFigurePatternSetToXml builderToXml = new ChessFigurePatternSetToXml();
		XmlElement xmlElement = builderToXml.build( refm );

		saveDocumentCustomFileName( xmlElement, xmlFileName );
	}

	protected RegexWholeFileModel loadAndAddDocumentCustomFileName( String longFileName ) throws IOException, ParserConfigurationException, SAXException
	{
		String singleFileName = FileFunctions.instance().getBaseName(longFileName);
		XmlElement elem = loadDocumentCustomFileName( longFileName );

		RegexWholeFileModel result = createAndAddFileModel( singleFileName, elem );

		return( result );
	}

	public RegexWholeFileModel importOriginalXmlFile( String xmlFileName ) throws IOException, TransformerException, ParserConfigurationException, SAXException
	{
		RegexWholeFileModel result = loadAndAddDocument( xmlFileName );

		saveXmlConfigurationFile( result );

		return( result );
	}

	public RegexWholeFileModel importXmlFile( String xmlFileName ) throws IOException, TransformerException, ParserConfigurationException, SAXException
	{
		RegexWholeFileModel result = loadAndAddDocumentCustomFileName( xmlFileName );

		saveXmlConfigurationFile( result );

		return( result );
	}


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
