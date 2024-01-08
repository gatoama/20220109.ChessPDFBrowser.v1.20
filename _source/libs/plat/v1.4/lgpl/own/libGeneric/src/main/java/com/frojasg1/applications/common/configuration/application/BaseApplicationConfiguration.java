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
package com.frojasg1.applications.common.configuration.application;

import com.frojasg1.applications.common.VersionsRunListString;
import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 *
 * @author Usuario
 */
public abstract class BaseApplicationConfiguration extends ConfigurationParent
	implements BaseApplicationConfigurationInterface
{
	public static final String CONF_LAST_DIRECTORY = "LAST_DIRECTORY";
	public static final String CONF_LANGUAGE = "LANGUAGE";
//	public static final String CONF_ADDITIONAL_LANGUAGE = "ADDITIONAL_LANGUAGE";
//	public static final String CONF_APPLICATION_FONT_FACTOR = "APPLICATION_FONT_FACTOR";
	public static final String CONF_FILE_CHOOSER_X = "FILE_CHOOSER_X";
	public static final String CONF_FILE_CHOOSER_Y = "FILE_CHOOSER_Y";
	public static final String CONF_FILE_CHOOSER_WIDTH = "FILE_CHOOSER_WIDTH";
	public static final String CONF_FILE_CHOOSER_HEIGHT = "FILE_CHOOSER_HEIGHT";
	public static final String CONF_ZOOM_FACTOR = "ZOOM_FACTOR";

	public static final String CONF_WHAT_IS_NEW_SHOWN_OF_DOWNLOAD_FILES = "WHAT_IS_NEW_SHOWN_OF_DOWNLOAD_FILES";
	public static final String CONF_DOWNLOAD_FILE_TO_IGNORE = "DOWNLOAD_FILE_TO_IGNORE";

	public static final String CONF_IS_DARK_MODE_ACTIVATED = "IS_DARK_MODE_ACTIVATED";

	public static final String CONF_IS_FILE_DETAILS_ACTIVATED = "IS_FILE_DETAILS_ACTIVATED";

//	public static final String CONF_URL_FOR_NEW_VERSION_QUERY = "URL_FOR_NEW_VERSION_QUERY";
//	public static final String CONF_URL_FOR_RESOURCE_COUNTER = "URL_FOR_RESOURCE_COUNTER";

	public static final String ES_LANGUAGE = "ES";	 // Spanish language
	public static final String EN_LANGUAGE = "EN";	 // English language

	protected static final String EMAIL_ADDRESS = "frojasg1@hotmail.com";
	protected static final String HOME_PAGE_URL = "https://frojasg1.com";
	protected static final String URL_FOR_NEW_VERSION_QUERY_VALUE = "https://frojasg1.com:8443/downloads_web";
	protected static final String URL_FOR_RESOURCE_COUNTER_VALUE = "https://frojasg1.com:8443/resource_counter/resourceCounter";

//	protected boolean _avoidRecursionZoomFactor = false;
	protected List<ChangeZoomFactorClientInterface> _listOfObserversOfZoomFactorChanges = new ArrayList<ChangeZoomFactorClientInterface>();

	protected VersionsRunListString _whatIsNewShownDownloadFileListString = null;


	public BaseApplicationConfiguration( String mainFolder, String applicationName, String group,
								String language, String configurationFileName )
	{
		super( mainFolder, applicationName, group, language, configurationFileName );

		_whatIsNewShownDownloadFileListString = new VersionsRunListString();
	}

	@Override
	public void M_openConfiguration( String fileName ) throws ConfigurationException
	{
		super.M_openConfiguration( fileName );

		_whatIsNewShownDownloadFileListString.setListString( getWhatIsNewShownDownladFileListString() );

		setLanguage( getLanguage() );
	}

	@Override
	public Properties M_getDefaultProperties( String language )
	{
		Properties result = new Properties();

		result.setProperty(CONF_LANGUAGE, EN_LANGUAGE );
		result.setProperty(CONF_LAST_DIRECTORY, "" );

		result.setProperty(CONF_ZOOM_FACTOR, "1.0" );
		result.setProperty(CONF_WHAT_IS_NEW_SHOWN_OF_DOWNLOAD_FILES, "" );
		result.setProperty(CONF_IS_DARK_MODE_ACTIVATED, _stringTranslator.toString( isDefaultModeDark() ) );
		result.setProperty(CONF_IS_FILE_DETAILS_ACTIVATED, "0" );


//		result.setProperty(CONF_URL_FOR_NEW_VERSION_QUERY, URL_FOR_NEW_VERSION_QUERY_VALUE );
//		result.setProperty(CONF_URL_FOR_RESOURCE_COUNTER, URL_FOR_RESOURCE_COUNTER_VALUE );

		return( result );
	}

	@Override
	public double getZoomFactor()
	{
		double zoomFactor = M_getDoubleParamConfiguration( CONF_ZOOM_FACTOR );
		if( zoomFactor <= 0 )
			zoomFactor = 1.0D;

		return( zoomFactor );
	}

	@Override
	public void setZoomFactor( double zoomFactor )
	{
		M_setDoubleParamConfiguration( CONF_ZOOM_FACTOR, zoomFactor );
	}

/*
	@Override
	public float getFontSizeFactor()
	{
		String factorStr = M_getStrParamConfiguration( CONF_APPLICATION_FONT_FACTOR );
		float factor = 1.0F;
		try
		{
			factor = Float.valueOf( factorStr );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		return( factor );
	}
	
	@Override
	public void setFontSizeFactor( float factor )
	{
		M_setFloatParamConfiguration( CONF_APPLICATION_FONT_FACTOR, factor );
	}
*/

	@Override
	public boolean isFileDetailsActivated()
	{
		return( M_getBoolParamConfiguration(CONF_IS_FILE_DETAILS_ACTIVATED ) );
	}

	@Override
	public void setFileDetailsSelected( boolean value )
	{
		M_setBoolParamConfiguration(CONF_IS_FILE_DETAILS_ACTIVATED, value );
	}


	@Override
	public String getLanguage()
	{
		return( M_getStrParamConfiguration( CONF_LANGUAGE ) );
	}

	@Override
	public void setLanguage( String language )
	{
		M_setStrParamConfiguration( CONF_LANGUAGE, language );
	}

	@Override
	public String getWhatIsNewShownDownladFileListString()
	{
		return( M_getStrParamConfiguration(CONF_WHAT_IS_NEW_SHOWN_OF_DOWNLOAD_FILES ) );
	}

	@Override
	public String getLastExecutionDownloadFileName()
	{
		return( _whatIsNewShownDownloadFileListString.getLast() );
	}

	@Override
	public boolean hasBeenShownWhatIsNewOfDownloadFile(String downloadFile)
	{
		return( _whatIsNewShownDownloadFileListString.isPresent(downloadFile) );
	}

	protected List<String> getFileNamesOfVersionsRunSortedByDateAsc()
	{
		return( _whatIsNewShownDownloadFileListString.getFileNamesOfVersionsRunSortedByDateAsc() );
	}

	@Override
	public VersionsRunListString getVersionsRunListString()
	{
		return( _whatIsNewShownDownloadFileListString );
	}

	@Override
	public String getYoungestVersionFileNameEverRun()
	{
		return( CollectionFunctions.instance().getLastOf(getFileNamesOfVersionsRunSortedByDateAsc()) );
	}

	@Override
	public void setWhatIsNewShownDownladFileListString( String value )
	{
		M_setStrParamConfiguration(CONF_WHAT_IS_NEW_SHOWN_OF_DOWNLOAD_FILES, value );
	}

	@Override
	public void addDownloadFileWhatIsNewShown( String value )
	{
		_whatIsNewShownDownloadFileListString.addElement( value );
		setWhatIsNewShownDownladFileListString(_whatIsNewShownDownloadFileListString.getListString() );
	}

	@Override
	public String getDownloadFileToIgnore()
	{
		return( M_getStrParamConfiguration( CONF_DOWNLOAD_FILE_TO_IGNORE ) );
	}

	@Override
	public void setDownloadFileToIgnore( String value )
	{
		M_setStrParamConfiguration( CONF_DOWNLOAD_FILE_TO_IGNORE, value );
	}

	@Override
	public String getLastDirectory()
	{
		return( M_getStrParamConfiguration( CONF_LAST_DIRECTORY ) );
	}

	@Override
	public String getUrlForResourceCounter()
	{
		return( URL_FOR_RESOURCE_COUNTER_VALUE );
//		return( M_getStrParamConfiguration( CONF_URL_FOR_RESOURCE_COUNTER ) );
	}
/*
	@Override
	public void setUrlForResourceCounter( String value )
	{
		M_setStrParamConfiguration( CONF_URL_FOR_RESOURCE_COUNTER, value );
	}
*/
	@Override
	public String getUrlForNewVersionQuery()
	{
		return( URL_FOR_NEW_VERSION_QUERY_VALUE );
//		return( M_getStrParamConfiguration( CONF_URL_FOR_NEW_VERSION_QUERY ) );
	}
/*
	@Override
	public void setUrlForNewVersionQuery( String value )
	{
		M_setStrParamConfiguration( CONF_URL_FOR_NEW_VERSION_QUERY, value );
	}
*/
	@Override
	public Rectangle getLastFileChooserBounds()
	{
		Rectangle result = null;
		
		Integer xx = M_getIntParamConfiguration( CONF_FILE_CHOOSER_X );
		Integer yy = M_getIntParamConfiguration( CONF_FILE_CHOOSER_Y );
		Integer width = M_getIntParamConfiguration( CONF_FILE_CHOOSER_WIDTH );
		Integer height = M_getIntParamConfiguration( CONF_FILE_CHOOSER_HEIGHT );

		if( isGreaterOrEqualThanZero(xx) &&
			isGreaterOrEqualThanZero(yy) &&
			isGreaterOrEqualThanZero(width) &&
			isGreaterOrEqualThanZero(height) )
		{
			result = new Rectangle( xx, yy, width, height );
		}

		return( result );
	}

	protected boolean isGreaterOrEqualThanZero( Integer value )
	{
		return( ( value != null ) && ( value >= 0 ) );
	}

	@Override
	public void setLastDirectory( String directory )
	{
		M_setStrParamConfiguration( CONF_LAST_DIRECTORY, directory );
	}

	@Override
	public void setLastFileChooserBounds( Rectangle newBounds )
	{
		if( newBounds != null )
		{
			M_setIntParamConfiguration( CONF_FILE_CHOOSER_X, (int) newBounds.getX() );
			M_setIntParamConfiguration( CONF_FILE_CHOOSER_Y, (int) newBounds.getY() );
			M_setIntParamConfiguration( CONF_FILE_CHOOSER_WIDTH, (int) newBounds.getWidth() );
			M_setIntParamConfiguration( CONF_FILE_CHOOSER_HEIGHT, (int) newBounds.getHeight() );
		}
	}

	@Override
	public boolean hasToEnableUndoRedoForTextComponents()
	{
		return( true );
	}

	@Override
	public boolean hasToEnableTextCompPopupMenus()
	{
		return( true );
	}

	@Override
	public abstract String getConfigurationMainFolder();
	@Override
	public abstract String getApplicationNameFolder();
	@Override
	public abstract String getApplicationGroup();

	@Override
	public abstract String getInternationalPropertiesPathInJar();

	@Override
	public abstract String getAdditionalLanguage();

	@Override
	public abstract void setAdditionalLanguage( String language );

	@Override
	public abstract boolean getLicensesHaveBeenAccepted();

	@Override
	public abstract void setLicensesHaveBeenAccepted( boolean value );

	@Override
	public abstract String getDefaultLanguageBaseConfigurationFolder();

	@Override
	public abstract String getDefaultLanguageConfigurationFolder( String language );

	@Override
	public abstract String getDefaultLanguage();

	@Override
	public String getAuthorEmailAddress()
	{
		return( EMAIL_ADDRESS );
	}

	@Override
	public String getHomePageUrl()
	{
		return( HOME_PAGE_URL );
	}

	@Override
	protected void changeLanguage_internal_simple( String language ) throws ConfigurationException
	{
		// this method is called from the parent. It loads the version of the configuration file for the new language
		// in the case of BaseApplicationConfiguration, it has not to be reloaded.
	}

	@Override
	public void serverChangeZoomFactor( double zoomFactor )
	{
		boolean revertOnError = true;
		
		try
		{
			changeZoomFactor_internal( zoomFactor, revertOnError );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected void changeZoomFactor_internal( double zoomFactor, boolean revertOnError ) throws ConfigurationException
	{
//		if( !_avoidRecursionZoomFactor )
		{
			double oldZoomFactor = getZoomFactor();

//			try
			{
				setZoomFactor(zoomFactor);

				Iterator<ChangeZoomFactorClientInterface> it = _listOfObserversOfZoomFactorChanges.iterator();
				while( it.hasNext() )
				{
					try
					{
						it.next().changeZoomFactor( zoomFactor );
					}
					catch( Exception ex )
					{
						ex.printStackTrace();
					}
				}

//				_avoidRecursionZoomFactor = true;
			}
/*			catch( Throwable th )
			{
				th.printStackTrace();
				if( revertOnError )
				{
					_avoidRecursionZoomFactor = false;
					changeZoomFactor_internal( oldZoomFactor, false );
					throw( new ConfigurationException( "Could not change zoomFactor, because of an internal error: " + th.getMessage() ) );
				}
			}
			finally
			{
				_avoidRecursionZoomFactor = false;
			}
*/
		}
	}

	@Override
	public void registerZoomFactorObserver( ChangeZoomFactorClientInterface client )
	{
		if( client != null )
			_listOfObserversOfZoomFactorChanges.add( client );
	}

	@Override
	public void unregisterZoomFactorObserver( ChangeZoomFactorClientInterface client )
	{
		_listOfObserversOfZoomFactorChanges.remove( client );
	}

	@Override
	public void toggleDarkMode()
	{
		setDarkModeActivated( ! isDarkModeActivated() );
	}

	@Override
	public boolean isDarkModeActivated()
	{
		return( M_getBoolParamConfiguration( CONF_IS_DARK_MODE_ACTIVATED ) );
	}

	@Override
	public void setDarkModeActivated( boolean value )
	{
		M_setBoolParamConfiguration( CONF_IS_DARK_MODE_ACTIVATED, value );
	}

	@Override
	public boolean isDefaultModeDark()
	{
		return( false );
	}
}
