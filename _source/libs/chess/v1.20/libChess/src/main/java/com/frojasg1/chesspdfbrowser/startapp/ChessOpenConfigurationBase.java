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

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfigurationFactory;
import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.impl.TagsExtractorImpl;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.loader.RegexFilesPersistency;
import com.frojasg1.chesspdfbrowser.recognizer.store.whole.ChessBoardRecognizerWhole;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.startapp.OpenConfigurationDesktopBase;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.startapp.OpenConfigurationBase.PairCurrentToArrayOfPossibleImports;
import com.frojasg1.general.startapp.Version;
import com.frojasg1.general.string.StringFunctions;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ChessOpenConfigurationBase extends OpenConfigurationDesktopBase<ApplicationConfiguration>
												implements InternationalizedStringConf
{
	protected static final int INDEX_MAIN_CONFIGURATION = 0;
	protected static final int NUMBER_OF_SETS_OF_CONFIGURATIONS_TO_IMPORT = 1;

	protected static final Version V_1_20_VERSION = new Version( "v1.20" );
	protected static final Version CURRENT_VERSION = V_1_20_VERSION;
	protected static final String MAIN_APP_CONF_FOLDER = FileFunctions.instance()
		.convertFolderSeparator( ".frojasg1.apps/ChessPDFBrowser/" + CURRENT_VERSION.getFolderName() );

	protected String _applicationName = null;

	protected static final String[] PARAMETERS_FOR_APPLICATION_CONFIGURATION = 	{
																					"Configuration",
																					"GlobalConfiguration.properties"
																				};

	protected StartApplicationStrings _internationalizedStringConf = null;

	protected ApplicationInitContext _initContext = null;

	public ChessOpenConfigurationBase( ApplicationInitContext initContext ) throws ConfigurationException
	{
		super();
		_initContext = initContext;
		_applicationName = initContext.getApplicationName();
	}

	@Override
	public void init() throws ConfigurationException
	{
		super.init();
	}

	@Override
	protected abstract void initLibraries();

	@Override
	protected int getNumberOfSetsOfConfigurationsToImport()
	{
		return( NUMBER_OF_SETS_OF_CONFIGURATIONS_TO_IMPORT );
	}

	@Override
	protected PairCurrentToArrayOfPossibleImports createImportElement(int index)
	{
		PairCurrentToArrayOfPossibleImports result = null;
		if( index == INDEX_MAIN_CONFIGURATION )
		{
			result = createPairForMainConfiguration();
		}

		return( result );
	}

	protected PairCurrentToArrayOfPossibleImports createPairForMainConfiguration()
	{
		return( new PairCurrentToArrayOfPossibleImports( MAIN_APP_CONF_FOLDER,
								createArray( new VersionToImportEntry(
											new String[] { ".frojasg1.apps", "ChessPDFBrowser" },
											"v1.0",
											PARAMETERS_FOR_APPLICATION_CONFIGURATION
									),
									new VersionToImportEntry(
											new String[] { ".frojasg1.apps", "ChessPDFBrowser" },
											"v1.1",
											PARAMETERS_FOR_APPLICATION_CONFIGURATION
									),
									new VersionToImportEntry(
											new String[] { ".frojasg1.apps", "ChessPDFBrowser" },
											"v1.11",
											PARAMETERS_FOR_APPLICATION_CONFIGURATION
									),
									new VersionToImportEntry(
											new String[] { ".frojasg1.apps", "ChessPDFBrowser" },
											"v1.2",
											PARAMETERS_FOR_APPLICATION_CONFIGURATION
									)
								),
								(existingFolder, newFolder) -> createOrCopyMainFolder(existingFolder, newFolder)
														)
				);
	}

	@Override
	protected ApplicationConfiguration createApplicationConfiguration() {
		ApplicationConfiguration result = ApplicationConfiguration.create( _applicationName );

		return( result );
	}

	@Override
	protected String[] getBasicLanguages()
	{
		String[] basicLanguages = new String[]{ "EN", "ES", "CAT", "RU" };

		return( basicLanguages );
	}

	@Override
	protected String[] getAvailableLanguagesInJar()
	{
		String[] availableLanguagesInJar = new String[]{ "EN", "ES", "CAT", "RU" };

		return( availableLanguagesInJar );
	}

	@Override
	protected String[] getWebLanguages()
	{
		String[] webLanguages = new String[]{ "Espanyol", "English", "Catala" };

		return( webLanguages );
	}

	@Override
	public void openOtherConfiguration() throws ConfigurationException
	{
		super.openOtherConfiguration();

		ChessStringsConf chessStrConf = null;
		try
		{
			chessStrConf = ChessStringsConf.createInstance( ApplicationConfiguration.instance() );
//			chessStrConf.M_openConfiguration();
//			chessStrConf.changeLanguage( ApplicationConfiguration.instance().M_getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

//		StartPDFLib.start( ApplicationConfiguration.instance(), ChessMoveAlgebraicNotation.instance() );
//		WarningsOfEncryptingConfigurations.createInstance( ApplicationConfiguration.instance() );
	}

	@Override
	public void eraseConfiguration() throws IOException
	{
		FileFunctions.instance().eraseDirCompletely( getFolderName( MAIN_APP_CONF_FOLDER ) );
	}

	@Override
	protected ApplicationConfigurationFactory createApplicationConfigurationFactory()
	{
		return( new ApplicationConfigurationFactory() );
	}

	public void initInternationalizedStrings()
	{
		_internationalizedStringConf = new StartApplicationStrings( getAppliConf() );
	}

	@Override
	public void initializeAfterImportingConfiguration() throws ConfigurationException
	{
		super.initializeAfterImportingConfiguration();

		try
		{
			if( getAppliConf().configurationFileExists() )
				getAppliConf().M_openConfiguration();

			getAppliConf().changeLanguage( getAppliConf().getLanguage() );
			getAppliConf().serverChangeZoomFactor( getAppliConf().getZoomFactor() );
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			throw( ce );
		}

		initInternationalizedStrings();

//		_initContext.setRegexConfWholeContainer( createRegexConfWholeContainer() );
		RegexFilesPersistency regexWholeModelConf = new RegexFilesPersistency( );
		regexWholeModelConf.init( getAppliConf() );
		Exception ex = ExecutionFunctions.instance().safeMethodExecution( () -> regexWholeModelConf.loadItems() );

		if( ex != null )
			throw( new ConfigurationException( createCustomInternationalString( StartApplicationStrings.CONF_ERROR_WHEN_LOADING_REGEX_FOR_TAGS_CONFIGURATION, ex.getMessage() ),
												ex ) );

		ChessEngineConfigurationPersistency chessEngineConfigurationPersistency = new ChessEngineConfigurationPersistency( );
		chessEngineConfigurationPersistency.init( getAppliConf() );
		ex = ExecutionFunctions.instance().safeMethodExecution(
			() -> chessEngineConfigurationPersistency.loadItems()
		);

		if( ex != null )
			throw( new ConfigurationException( createCustomInternationalString( StartApplicationStrings.CONF_ERROR_WHEN_LOADING_CHESS_ENGINE_CONFIGURATION_FILES, ex.getMessage() ),
												ex ) );

		WholeCompletionManager wholeCompletionManager = new WholeCompletionManager();
		wholeCompletionManager.init( getAppliConf() );
//		wholeCompletionManager.setRegexConfigurations(regexWholeModelConf.getRegexWholeContainer());

		TagsExtractor tagsExtractor = new TagsExtractorImpl( regexWholeModelConf.getModelContainer() );

		_initContext.setRegexWholeContainerPersistency(regexWholeModelConf);
		_initContext.setChessEngineConfigurationPersistency(chessEngineConfigurationPersistency);
		_initContext.setWholeCompletionManager(wholeCompletionManager);
		_initContext.setTagsExtractor( tagsExtractor );

		ChessBoardRecognizerWhole ocr = new ChessBoardRecognizerWhole();
		ex = ExecutionFunctions.instance().safeMethodExecution( () -> ocr.init( getAppliConf() ) );

		if( ex != null )
			throw( new ConfigurationException( createCustomInternationalString( StartApplicationStrings.CONF_ERROR_WHEN_LOADING_CHESS_BOARD_RECOGNIZER_FILES, ex.getMessage() ),
												ex ) );

		_initContext.setChessBoardRecognizerWhole( ocr );

		changeConfigurationDependingOnTheVersionImported();
	}

	protected void changeConfigurationDependingOnTheVersionImported()
	{
//		String importedVersion = getOpenConfiguration().getImportedVersion( OpenConfiguration.INDEX_MAIN_CONFIGURATION );
//		if( ( importedVersion != null ) && ( importedVersion.compareTo( "v1.2" ) < 0 ) )
		if( wasANewVersion() )
		{
			getAppliConf().setConfigurationOfChessLanguageToShow( ChessLanguageConfiguration.ALGEBRAIC_FIGURINE_NOTATION );
			getAppliConf().setChessFigurineSet( FigureSet.VIRTUAL_PIECES_SET );

			eraseLanguageConfigurationFileRecursive( "MainWindow_LAN.properties" );
		}
	}

	// an error of regex configuration for extracting TAGs has been fixed:
	//		When Date existed but not Site, and they where not mandatory splitted.
	//	E-5 profile:
	//  line-3:
	//	previous regex:		.... %SITE%\s*,?\s*%DATE%\s*$
	//	correction:			.... %SITE%\s*[,\s]\s*%DATE%\s*$
	//	based on this error, other similar errors have been fixed.
	protected void ifRegExConfigurationForExtractingTagsNotUpdatedV1_20()
	{
		String latestDateString = this.getYoungestVersionFileNameDateString();
		final String DATE = "20210805";

		if( !StringFunctions.instance().isEmpty(latestDateString) &&
			DATE.compareTo(latestDateString) > 0 )
		{
			Version youngestVersion = createVersionPojo( getYoungestVersionFileNameVersion() );
			if( V_1_20_VERSION.compareTo(youngestVersion) == 0 )
			{
				RegexFilesPersistency regexWholeModelConf = _initContext.getRegexWholeContainerPersistency();

				ifDefaultConfigurationSaveItToOldAndSetTheInitialOne( regexWholeModelConf );
			}
		}
	}

	protected String getRegexFileNameToStorePreviousConfiguration( String defaultRegexFileName,
																	RegexFilesPersistency regexWholeModelConf )
	{
		String result = null;
		String extension = FileFunctions.instance().getExtension(defaultRegexFileName);
		String nameWithoutExtension = FileFunctions.instance().cutOffExtension(defaultRegexFileName);
		String sufix = formatDate( new Date() );
		String dotExt = ( StringFunctions.instance().isEmpty(extension) ? "" : "." + extension );
		for( int ii=0; (result==null) && ii<100; ii++)
		{
			String indexStr = ( ii == 0 ) ? "" : String.format( "_%02d", ii );
			String regexFileName = String.format( "%s_%s%s%s", nameWithoutExtension, sufix, indexStr, dotExt );
			if( !regexWholeModelConf.getModelContainer().elementExists(regexFileName) )
				result = regexFileName;
		}

		return( result );
	}

	protected void ifDefaultConfigurationSaveItToOldAndSetTheInitialOne( RegexFilesPersistency regexWholeModelConf )
	{
		try
		{
			String defaultRegexFileName = regexWholeModelConf.getDefaultGlobalRegexConfigurationFileName();
			RegexWholeFileModel defaultRegexWholeFileModel = regexWholeModelConf.getModelContainer().get(defaultRegexFileName);
			if( defaultRegexWholeFileModel != null )
			{
				for( ProfileModel profile: defaultRegexWholeFileModel.getSetOfProfiles() )
					profile.setActive(false);

				String backupFileName = getRegexFileNameToStorePreviousConfiguration( defaultRegexFileName, regexWholeModelConf );
				regexWholeModelConf.getModelContainer().rename(defaultRegexFileName, backupFileName);
				regexWholeModelConf.getModelContainer().getComboBoxContent().removeItem(defaultRegexFileName);
				regexWholeModelConf.getModelContainer().getComboBoxContent().addItem(backupFileName);

				regexWholeModelConf.save();
				FileFunctions.instance().delete( regexWholeModelConf.getLongFileName(defaultRegexFileName) );

				regexWholeModelConf.importOriginalXmlFile(defaultRegexFileName);
				defaultRegexWholeFileModel = regexWholeModelConf.getModelContainer().get(defaultRegexFileName);
				for( ProfileModel profile: defaultRegexWholeFileModel.getSetOfProfiles() )
					profile.setActive(true);
				regexWholeModelConf.getModelContainer().getComboBoxContent().addItem(defaultRegexFileName);

				regexWholeModelConf.save();
			}
		}
		catch( Exception ex )
		{
			throw( new RuntimeException(
				createCustomInternationalString(
					StartApplicationStrings.CONF_ERROR_WHEN_TRYING_TO_BACKUP_PREVIOUS_REGEX_CONFIGURATION,
					ex.getMessage() ),
				ex )
				);
		}
	}

	@Override
	protected void postprocessPreviousConfigurationAfterInit()
	{
		ifRegExConfigurationForExtractingTagsNotUpdatedV1_20();
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
