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
package com.frojasg1.general.startapp;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationFactory;
import com.frojasg1.applications.common.configuration.start.StartStringsConf;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.ObjectFunctions;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.generic.GenericFunctions;
import com.frojasg1.generic.languages.ObtainAvailableLanguages_base;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class OpenConfigurationBase<AC extends BaseApplicationConfiguration>
{
	protected static final String HOME_FOLDER = System.getProperty("user.home");

	protected static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat( "yyyyMMdd" );

	protected AC _appliConf = null;

//	protected GenericBasicSplash _splash = null;

	protected List<PairCurrentToArrayOfPossibleImports> _listForImports = null;

	protected StartStringsConf _startStringsConf = null;

	protected BaseApplicationConfigurationFactory<AC> _applicationConfigurationFactory = null;

	protected Supplier<Boolean> _wasANewVersionFunction;

	protected String _youngestVersionFileNameEverRun;

	public OpenConfigurationBase( ) throws ConfigurationException
	{
	}

	public void setWasANewVersionFunction( Supplier<Boolean> wasANewVersionFunction )
	{
		_wasANewVersionFunction = wasANewVersionFunction;
	}

	protected String getYoungestVersionFileNameEverRun()
	{
		return( _youngestVersionFileNameEverRun );
	}

	protected void setYoungestVersionFileNameEverRun( String dateString )
	{
		_youngestVersionFileNameEverRun = dateString;
	}

	protected abstract BaseApplicationConfigurationFactory<AC> createApplicationConfigurationFactory();

	protected AC getPrevConf()
	{
		AC result = null;
		PairCurrentToArrayOfPossibleImports pair = this.getConfigurationToImport(0);
		if( pair != null )
			result = pair.getLastApplicationConfiguration();

		return( result );
	}

	protected boolean wasANewVersion()
	{
		boolean result = false;
		if( Objects.equals( true,
			NullFunctions.instance().getIfNotNull(_wasANewVersionFunction, s -> s.get() ) )
			)
		{
			result = true;
		}

		return( result );
	}

	public void init() throws ConfigurationException
	{
		_applicationConfigurationFactory = createApplicationConfigurationFactory();

		initImportFolders();

		AC prevConf = getPrevConf();

		openBasicConfiguration();

		if( getAppliConf().M_isFirstTime() && ( prevConf != null ) )
			initNewApplicationConfiguration( prevConf );
	}

	protected void initNewApplicationConfiguration( AC prevConf )
	{
		AC appliConf = getAppliConf();
		
		appliConf.setLanguage( prevConf.getLanguage() );
		appliConf.setZoomFactor( prevConf.getZoomFactor() );
	}

	protected abstract int getNumberOfSetsOfConfigurationsToImport();

	protected List<PairCurrentToArrayOfPossibleImports> createListForImports()
	{
		return( new ArrayList<>() );
	}

	protected abstract PairCurrentToArrayOfPossibleImports createImportElement( int index );

	protected void initImportFolders()
	{
		_listForImports = createListForImports();
		int count = getNumberOfSetsOfConfigurationsToImport();
		for( int ii=0; ii<count; ii++ )
		{
			PairCurrentToArrayOfPossibleImports elem = createImportElement(ii);
			elem.init();

			_listForImports.add( elem );
		}
	}

/*
	public void setDialogShower( DialogShower dialogShower )
	{
		_dialogShower = dialogShower;
	}
*/
/*
	public void setParent( GenericBasicSplash splash )
	{
		_splash = splash;
	}
*/
	public boolean hasToImport( int indexToCheck )
	{
		return( _listForImports.get(indexToCheck).hasToImport() );
	}

	public PairCurrentToArrayOfPossibleImports getConfigurationToImport( int index )
	{
		PairCurrentToArrayOfPossibleImports result = null;
		if( _listForImports.size() < index )
			result = _listForImports.get(index);

		return( result );
	}

	public String getImportedVersion( int index )
	{
		String result = null;
		PairCurrentToArrayOfPossibleImports pair = getConfigurationToImport( index );
		if( pair != null )
			result = pair.getImportedVersion();

		return( result );
	}

	public boolean hasToImport()
	{
		boolean result = false;

		for( PairCurrentToArrayOfPossibleImports pair: _listForImports )
		{
			if( pair.hasToImport() )
			{
				result = true;
				break;
			}
		}

		return( result );
	}

	protected String getMessageOfImportedConfigurations()
	{
		String message = null;

		if( getStartStrings() != null )
		{
			message = getStartStrings().M_getStrParamConfiguration( StartStringsConf.CONF_HAS_BEEN_IMPORTED ) + "\n" +
						getStartStrings().M_getStrParamConfiguration( StartStringsConf.CONF_INDEPENDENT_CONF );
		}

		return( message );
	}

	protected String getTitleOfImportedConfigurationsDialog()
	{
		String title = null;

		if( getStartStrings() != null )
		{
			title = getStartStrings().M_getStrParamConfiguration( StartStringsConf.CONF_NOTICE );
		}

		return( title );
	}

	protected StartStringsConf createStartStringsConf() throws ConfigurationException
	{
		return( StartStringsConf.createInstance( getAppliConf() ) );
	}

	protected void openMainConfiguration() throws ConfigurationException
	{
		try
		{
			if( getAppliConf().configurationFileExists() )
				getAppliConf().M_openConfiguration();

			getAppliConf().changeLanguage( getAppliConf().getLanguage() );
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			throw( ce );
		}
	}

	public boolean isNewVersion()
	{
		return( !getAppliConf().configurationFileExists() );
	}

	public boolean importConfigurationIfNecessary() throws ConfigurationException, IOException
	{
		boolean result = false;
		
		setYoungestVersionFileNameEverRun( calculateYoungestRunVersionFileName() );

/*		if( FileFunctions.instance().isFile(
				FileFunctions.instance().convertFolderSeparator( String.format( "%s/%s/%s",
											HOME_FOLDER,
											MAIN_APP_CONF_CONFIGURATION_FOLDER,
											APP_CONF_FILENAME )
																)
											)
			)
*/
		if( !isNewVersion() )
		{
			openOtherConfiguration();
		}
		else
		{
//			if( _splash != null )
//				_splash.setCurrentActivityFromLabel( _splash.CONF_IMPORTING_CONFIGURATION );

			try
			{
				if( checkAndImportConfiguration() )
				{
					openMainConfiguration();
					getAppliConf().setLicensesHaveBeenAccepted(false); // just imported from another version, which had licenses accepted.

					openOtherConfiguration();
					String message = getMessageOfImportedConfigurations();

					String title = getTitleOfImportedConfigurationsDialog();

//					ViewComponent vc = DesktopGenericFunctions.instance().getViewFacilities().createViewComponent(_splash);
					ViewComponent vc = null;
					GenericFunctions.instance().getDialogsWrapper().showMessageDialog( vc,
																						message, title,	DialogsWrapper.INFORMATION_MESSAGE );

					result = true;
				}
				else
				{
					openOtherConfiguration();
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				createGenericFunctions();
//				ViewComponent vc = DesktopGenericFunctions.instance().getViewFacilities().createViewComponent(_splash);
				ViewComponent vc = null;
				GenericFunctions.instance().getDialogsWrapper().showMessageDialog(vc,
																					"Problem reading configuration: " + th.toString(),
																					"ERROR", DialogsWrapper.ERROR_MESSAGE );
				throw( new RuntimeException( "ERROR while reading configuration" ) );
			}
		}
		return( result );
	}

	protected abstract void createGenericFunctions();

	protected Version createVersionPojo( String versionStr )
	{
		Version result = null;
		if( versionStr != null )
			result = new Version(versionStr);

		return( result );
	}

	protected String calculateYoungestRunVersionFileName()
	{
		return( getAppliConf().getYoungestVersionFileNameEverRun() );
	}

	protected String getDateStringFromVersionFileName( String fileName )
	{
		return( getAppliConf().getVersionsRunListString().getDateStringFromVersionFileName(fileName) );
	}

	protected String getVersionFromVersionFileName( String fileName )
	{
		return( getAppliConf().getVersionsRunListString().getVersionFromFileName(fileName) );
	}

	protected String getYoungestVersionFileNameDateString( )
	{
		return( getDateStringFromVersionFileName(getYoungestVersionFileNameEverRun()) );
	}

	protected String getYoungestVersionFileNameVersion( )
	{
		return( getVersionFromVersionFileName(getYoungestVersionFileNameEverRun()) );
	}

	public void initializeAfterImportingConfigurationAndPostprocessPreviousConfigurationAfterInit() throws ConfigurationException
	{
		initializeAfterImportingConfiguration();
		postprocessPreviousConfigurationAfterInit();
	}

	protected void postprocessPreviousConfigurationAfterInit()
	{
		// It has to be overriden by derived classes if needed
	}

	public void initializeAfterImportingConfiguration() throws ConfigurationException
	{
//		ToolTipLookAndFeel.instance().init();
//		ToolTipLookAndFeel.instance().registerToChangeZoomFactorAsObserver( getAppliConf() );

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
	}

	/**
	 * 
	 * @param oldDir	oldDir path to be checked
	 * @param newDir	newDir path to be checked
	 * @return			Returns if the old path exists and is a directory, and the new path is not a directory (or does not exist)
	 */
	protected boolean checkOldAndNewFolder( String oldDir, String newDir )
	{
		boolean result = !existsFolder( newDir ) && existsFolder( oldDir );

		return( result );
	}

	protected boolean existsFolder( String folderName )
	{
		boolean result = FileFunctions.instance().isDirectory( folderName );

		return( result );
	}

	protected String getFolderName( String homeFolder )
	{
		return( HOME_FOLDER + FileFunctions.DIR_SEPARATOR + homeFolder );
	}

	protected String getFolderName( String[] homeFolderParams, String version )
	{
		StringBuilder sb = new StringBuilder();
		int index = 0;
		for( String folder: homeFolderParams )
		{
			if( index > 0 )
				sb.append( "/" );
			sb.append( folder );
			index++;
		}

		if( ! StringFunctions.instance().isEmpty(version) )
			sb.append( "/" ).append( version );

		String homeFolder = FileFunctions.instance().convertFolderSeparator( sb.toString() );

		return( getFolderName( homeFolder ) );
	}

	protected boolean checkAndImportConfiguration() throws ConfigurationException, IOException
	{
		boolean imported = false;

//		imported = createOrCopyMainFolder();

		for( PairCurrentToArrayOfPossibleImports pair: _listForImports )
		{
			imported = createOrImportFolder( pair ) || imported;
		}

		return( imported );
	}

	protected boolean createOrImportFolder( PairCurrentToArrayOfPossibleImports pair ) throws IOException
	{
		boolean result = false;
		String newDir = getFolderName( pair.getCurrentVersionFolder() );
		if( !existsFolder( newDir ) )
		{
			if( pair.hasToImport() )
			{
				boolean wasImported = pair.doImport();
//				pair.setWasImported(wasImported);
				result = wasImported;
			}
			else if( !FileFunctions.instance().isDirectory( newDir ) )
			{
				(new File( newDir ) ).mkdirs();
			}
		}

		return( result );
	}

	public abstract void eraseConfiguration() throws IOException;

	protected abstract AC createApplicationConfiguration();

	public AC getAppliConf()
	{
		return( _appliConf );
	}

	protected abstract String[] getBasicLanguages();
	protected abstract String[] getAvailableLanguagesInJar();
	protected abstract String[] getWebLanguages();

	public void openBasicConfiguration() throws ConfigurationException
	{
		try
		{
			_appliConf = createApplicationConfiguration();

			boolean isNew = !getAppliConf().configurationFileExists();
			getAppliConf().M_openConfiguration();

			String[] basicLanguages = getBasicLanguages();
			String[] availableLanguagesInJar = getAvailableLanguagesInJar();

			ObtainAvailableLanguages_base availLan = ObtainAvailableLanguages_base.create(
																basicLanguages, availableLanguagesInJar, 
																getAppliConf().getDefaultLanguageConfigurationFolder(""),
																getAppliConf().getInternationalPropertiesPathInJar() );

			String[] webLanguages = getWebLanguages();
			availLan.setAvailableWebLanguageNames(webLanguages);

//			appConf.setLanguage( appConf.getLanguage() );

			if( isNew )
			{
				String defaultLanguage = ObtainAvailableLanguages_base.instance().getDefaultLanguage();
				getAppliConf().setLanguage( defaultLanguage );
			}

			initLibraries();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();

			createGenericFunctions();

			ViewComponent vc = null;
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog(vc,
																				ce.getMessage() + " Exiting from application",
																				"Configuration error", DialogsWrapper.ERROR_MESSAGE,
																				null );
			throw( ce );
		}
	}

	protected abstract void initLibraries();

	public StartStringsConf getStartStrings()
	{
		return( _startStringsConf );
	}

	public void openOtherConfiguration() throws ConfigurationException
	{
		try
		{
			_startStringsConf = createStartStringsConf();
			_startStringsConf.M_openConfiguration();
			_startStringsConf.changeLanguage( getAppliConf().getLanguage() );
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			throw( ce );
		}
	}

	public void removeParticularDocumentsOfMinorRelease()
	{
		removeParticularDocumentsOfMinorRelease( getAppliConf().getDefaultLanguageBaseConfigurationFolder() );
	}

	protected void eraseRecursive( String folder, String fileName )
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> FileFunctions.instance().eraseRecursive(folder, fileName ) );
	}

	protected void eraseRecursiveFileRegex( String folder, String fileNamePatternToErase )
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> FileFunctions.instance().eraseRecursiveFileRegex(folder, fileNamePatternToErase ) );
	}

	protected void removeParticularDocumentsOfMinorRelease( String folder )
	{
		eraseRecursiveFileRegex(folder, ".*.rtf$" );
		eraseRecursiveFileRegex( folder, ".*_GEN.properties$" );
		eraseRecursiveFileRegex( folder, ".*_LAN.properties$" );
	}

	protected boolean createOrCopyMainFolder( String oldDir, String newDir ) throws IOException
	{
		boolean imported = false;
		
		if( !FileFunctions.instance().isDirectory( newDir ) && FileFunctions.instance().isDirectory( oldDir ) )
		{
			FileFunctions.instance().copyDirectoryRecursive( oldDir, newDir );
			imported = true;

			removeParticularDocumentsOfMinorRelease( newDir );
		}
		else if( !FileFunctions.instance().isDirectory( newDir ) )
		{
			(new File( newDir ) ).mkdirs();
		}
		
		return( imported );
	}

	public static <E> E[] createArray(E... elements) {
		return Arrays.copyOf(elements, elements.length);
	}

	protected void eraseLanguageConfigurationFileRecursive( String fileName )
	{
		ExecutionFunctions.instance().safeMethodExecution( () ->
			FileFunctions.instance().eraseRecursive( getAppliConf().getDefaultLanguageBaseConfigurationFolder(),
													fileName ) );
	}

	protected String formatDate( Date date )
	{
		return( DATE_FORMATTER.format(date) );
	}

	protected interface ImportFolderFunction
	{
		public boolean importFolder( String existingFolder, String newFolder ) throws IOException;
	}

	protected class VersionToImportEntry
	{
		protected String[] _arrayOfStringsFromHomeToApplicationOrMainFolderWithoutVersion;
		protected String _version;
		protected String[] _arrayOfStringsFromApplicationOrMainFolderToConfiguration;

		public VersionToImportEntry( String[] arrayOfStringsFromHomeToApplicationOrMainFolderWithoutVersion,
									String version,
									String[] arrayOfStringsFromApplicationOrMainFolderToConfiguration )
		{
			_version = version;
			_arrayOfStringsFromHomeToApplicationOrMainFolderWithoutVersion = arrayOfStringsFromHomeToApplicationOrMainFolderWithoutVersion;
			_arrayOfStringsFromApplicationOrMainFolderToConfiguration = arrayOfStringsFromApplicationOrMainFolderToConfiguration;
		}

		public String[] getStringsToMainFolderWithoutVersion()
		{
			return( _arrayOfStringsFromHomeToApplicationOrMainFolderWithoutVersion );
		}

		public String getVersion()
		{
			return( _version );
		}

		public String[] getStringsToConfigurationFolder()
		{
			return( _arrayOfStringsFromApplicationOrMainFolderToConfiguration );
		}
	}

	public class PairCurrentToArrayOfPossibleImports
	{
		protected String _folderForCurrentVersion = null;
		protected VersionToImportEntry[] _arrayOfPossibleFoldersToImport = null;
		protected ImportFolderFunction _functionForImporting = null;

		protected int _indexToImport = -1;

		public PairCurrentToArrayOfPossibleImports( String folderForCurrentVersion,
													VersionToImportEntry[] arrayOfPossibleFoldersToImport,
													ImportFolderFunction importFunction )
		{
			_folderForCurrentVersion = getFolderName( folderForCurrentVersion );
			_arrayOfPossibleFoldersToImport = arrayOfPossibleFoldersToImport;
			_functionForImporting = importFunction;
		}

		public void init()
		{
			_indexToImport = hasToImport(_arrayOfPossibleFoldersToImport, _folderForCurrentVersion  );
		}

		public String getCurrentVersionFolder()
		{
			return( _folderForCurrentVersion );
		}

		public String getFolderToImport()
		{
			String result = null;

			if( ( _indexToImport >= 0 ) &&
				( _indexToImport < _arrayOfPossibleFoldersToImport.length ) )
			{
				result = getFolderName(_arrayOfPossibleFoldersToImport[ _indexToImport ].getStringsToMainFolderWithoutVersion(),
										_arrayOfPossibleFoldersToImport[ _indexToImport ].getVersion() );
			}

			return( result );
		}

		public boolean hasToImport()
		{
			return( _indexToImport >= 0 );
		}

		protected int hasToImport( VersionToImportEntry[] arrayOfPossibleFoldersToImport, String newFolder )
		{
			int indexToImport = -1;

			String newDir = getFolderName( newFolder );
			if( !existsFolder( newDir ) )
			{
				for( int ii = arrayOfPossibleFoldersToImport.length - 1; (ii>=0) && (indexToImport == -1); ii-- )
				{
					VersionToImportEntry versionToImport = arrayOfPossibleFoldersToImport[ii];
					String oldDir = getFolderName(versionToImport.getStringsToMainFolderWithoutVersion(),
													versionToImport.getVersion() );
					if( existsFolder( oldDir ) )
						indexToImport = ii;
				}
			}

			return( indexToImport );
		}

		public boolean wasImported()
		{
			return( getImportedVersion() != null );
		}

		public String getImportedVersion()
		{
			String result = null;
			VersionToImportEntry importedVersionEntry = getImportedVersionEntry();
			if( importedVersionEntry != null )
				result = importedVersionEntry.getVersion();

			return( result );
		}

		public VersionToImportEntry getImportedVersionEntry()
		{
			VersionToImportEntry result = null;
			if( ( _indexToImport >= 0 ) && ( _indexToImport < _arrayOfPossibleFoldersToImport.length ) )
				result = _arrayOfPossibleFoldersToImport[_indexToImport];

			return( result );
		}

		public boolean doImport() throws IOException
		{
			boolean result = false;
			try
			{
				result = _functionForImporting.importFolder( getFolderToImport(), _folderForCurrentVersion );
			}
			catch( IOException ioe )
			{
				throw( ioe );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}

			return( result );
		}

		public AC getLastApplicationConfiguration()
		{
			AC result = null;
			if( _applicationConfigurationFactory != null )
			{
				for( int ii=_arrayOfPossibleFoldersToImport.length - 1; ii>=0; ii-- )
				{
					VersionToImportEntry versionToImport = _arrayOfPossibleFoldersToImport[ii];

					String[] first = versionToImport.getStringsToMainFolderWithoutVersion();
					String version = versionToImport.getVersion();
					String[] second = versionToImport.getStringsToConfigurationFolder();
					if( second != null )
					{
						String mainFolder = first[0];
						String applicationName = first[1];

						String group = second[0];
						String globalConfFileName = second[1];
						AC tmpAc = _applicationConfigurationFactory.create( mainFolder, applicationName, version, group, globalConfFileName );

						try
						{
							tmpAc.M_openConfiguration();
						}
						catch( Exception ex )
						{}

						if( !tmpAc.M_isFirstTime() )
						{
							result = tmpAc;
							break;
						}
					}
				}
			}

			return( result );
		}
	}
}
