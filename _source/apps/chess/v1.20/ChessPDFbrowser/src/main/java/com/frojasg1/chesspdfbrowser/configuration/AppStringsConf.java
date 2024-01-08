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
package com.frojasg1.chesspdfbrowser.configuration;

import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class AppStringsConf extends ConfigurationParent
{
	public static final String CONF_PROBLEM_SWITCHING_TO_NEW_GAME = "PROBLEM_SWITCHING_TO_NEW_GAME";
	public static final String CONF_ERROR = "ERROR";
	public static final String CONF_ERROR_SCANNING_TEXT = "ERROR_SCANNING_TEXT";
	public static final String CONF_CLIPBOARD_CONTENT_NOT_A_STRING = "CLIPBOARD_CONTENT_NOT_A_STRING";
	public static final String CONF_ERROR_PASTING_TEXT = "ERROR_PARSING_TEXT";
	public static final String CONF_ERROR_CHANGING_CURRENT_MOVE = "ERROR_CHANGING_CURRENT_MOVE";
	public static final String CONF_ERROR_BROWSING_THE_TREE = "ERROR_BROWSING_THE_TREE";
	public static final String CONF_ERROR_LOADING_PAGE_FROM = "ERROR_LOADING_PAGE_FROM";
	public static final String CONF_ERROR_LOADING_PAGE = "ERROR_LOADING_PAGE";
	public static final String CONF_ERROR_INITIALIZING_SEGMENTATOR = "ERROR_INITIALIZING_SEGMENTATOR";
	public static final String CONF_ERROR_INITIALIZING_SEGMENTATOR2 = "ERROR_INITIALIZING_SEGMENTATOR2";
	public static final String CONF_CONFIGURATION_ERROR = "CONFIGURATION_ERROR";
	public static final String CONF_CANCEL = "CANCEL";
	public static final String CONF_CLOSE = "CLOSE";
	public static final String CONF_ERROR_SAVING_REGEXES = "ERROR_SAVING_REGEXES";

	public static final String CONF_ABOUT_1 = "ABOUT_1";
	public static final String CONF_ABOUT_2 = "ABOUT_2";
	public static final String CONF_ABOUT_3 = "ABOUT_3";
	public static final String CONF_ABOUT_4 = "ABOUT_4";
	public static final String CONF_ABOUT_5 = "ABOUT_5";
	public static final String CONF_ABOUT_6 = "ABOUT_6";
	public static final String CONF_ABOUT_7 = "ABOUT_7";
	public static final String CONF_ABOUT_8 = "ABOUT_8";
	public static final String CONF_ABOUT_9 = "ABOUT_9";
	public static final String CONF_RELEASED_ON = "RELEASED_ON";
	public static final String CONF_SEND_EMAIL_TO = "SEND_EMAIL_TO";

	public static final String CONF_VISIT_HOME_PAGE = "VISIT_HOME_PAGE";

	public static final String CONF_SHOWING_LICENSES = "SHOWING_LICENSES";
	public static final String CONF_PROBLEM_LAUNCHING_THE_APPLICATION = "PROBLEM_LAUNCHING_THE_APPLICATION";

	public static final String CONF_FROM_PAGE_MUST_BE_BETWEEN = "FROM_PAGE_MUST_BE_BETWEEN";
	public static final String CONF_TO_PAGE_MUST_BE_BETWEEN = "TO_PAGE_MUST_BE_BETWEEN";
	public static final String CONF_FROM_PAGE_MUST_BE_LESSER_OR_EQUAL_THAN_TO_PAGE = "FROM_PAGE_MUST_BE_LESSER_OR_EQUAL_THAN_TO_PAGE";

	public static final String CONF_INVALID_INTEGER = "INVALID_INTEGER";


//	protected static final String APPLICATION_GROUP = "general";
//	protected static final String APPLICATION_NAME = "FileEncoder";
	protected static final String GLOBAL_CONF_FILE_NAME = "AppStringsConf.properties";

//	protected String a_pathPropertiesInJar = null;
	private static AppStringsConf a_instance = null;

	BaseApplicationConfigurationInterface _baseConfiguration = null;

	private AppStringsConf( BaseApplicationConfigurationInterface appliConf )
	{
		super( appliConf.getConfigurationMainFolder(),
				appliConf.getApplicationNameFolder(),
				appliConf.getApplicationGroup(),
				null,
				GLOBAL_CONF_FILE_NAME );

		registerToChangeLanguageAsObserver(appliConf);

		_baseConfiguration = appliConf;
//		a_pathPropertiesInJar = pathPropertiesInJar;
	}

	public static AppStringsConf createInstance( BaseApplicationConfigurationInterface appliConf ) throws ConfigurationException
	{
		if( a_instance == null )
		{
			a_instance = new AppStringsConf( appliConf );
			if( appliConf != null )
			{
				a_instance.changeLanguage( appliConf.getLanguage() );
			}
		}

		return( a_instance );
	}

	public static AppStringsConf instance()
	{
		return( a_instance );
	}

	protected Properties M_getDefaultProperties2( String language )
	{
		Properties result = new Properties();

		result.setProperty(CONF_PROBLEM_SWITCHING_TO_NEW_GAME, "Problem when switching to new game." );
		result.setProperty(CONF_ERROR, "ERROR" );
		result.setProperty(CONF_ERROR_SCANNING_TEXT, "Error when scanning text." );
		result.setProperty(CONF_CLIPBOARD_CONTENT_NOT_A_STRING, "The clipboard content was not a string" );
		result.setProperty(CONF_ERROR_PASTING_TEXT, "Error pasting pgn text" );
		result.setProperty(CONF_ERROR_CHANGING_CURRENT_MOVE, "ERROR when changing current move in the tree of moves : " );
		result.setProperty(CONF_ERROR_BROWSING_THE_TREE, "Error browsing the tree" );
		result.setProperty(CONF_ERROR_LOADING_PAGE_FROM, "Error loading page from: " );
		result.setProperty(CONF_ERROR_LOADING_PAGE, "Error loading page" );
		result.setProperty(CONF_ERROR_INITIALIZING_SEGMENTATOR, "ERROR initializing segmentator: " );
		result.setProperty(CONF_ERROR_INITIALIZING_SEGMENTATOR2, "Error initializing segmentator" );
		result.setProperty(CONF_CONFIGURATION_ERROR, "Configuration Error" );
		result.setProperty(CONF_CANCEL, "Cancel" );
		result.setProperty(CONF_CLOSE, "Close" );
		result.setProperty(CONF_ERROR_SAVING_REGEXES, "Error saving regular expressions." );

		result.setProperty(CONF_ABOUT_1, "By Francisco Javier Rojas Garrido" );
		result.setProperty(CONF_ABOUT_2, "Encrypting application based on the chaoting pseudorandom generator." );
		result.setProperty(CONF_ABOUT_3, "Thanks to Raül Rodríguez and Quim Blesa for their suggestions about the application." );
		result.setProperty(CONF_ABOUT_4, "Thanks to Albert Sala for doing the tests over Mac and cross-platforms." );
		result.setProperty(CONF_ABOUT_5, "Thanks to José Luis Moisés for reviewing the English translation of the documents and application interface." );
		result.setProperty(CONF_ABOUT_6, "Thanks to the authors of book \"Secuencias pseudoaleatorias para telecomunicaciones\" Edicions UPC (1996)" );
		result.setProperty(CONF_ABOUT_7, "Thanks to http://stackoverflow.com for the great help that they provide to developers" );
		result.setProperty(CONF_ABOUT_8, "Thanks to Rob Camick for the post: https://tips4java.wordpress.com/2008/11/10/table-column-adjuster/ which has been used in this application." );
		result.setProperty(CONF_ABOUT_9, "For bug reports or comments, please send an e-mail to:" );
		result.setProperty(CONF_RELEASED_ON, "Released on" );
		result.setProperty(CONF_SEND_EMAIL_TO, "Send e-mail to:" );

		result.setProperty(CONF_VISIT_HOME_PAGE, "Visit home page");

		result.setProperty(CONF_SHOWING_LICENSES, "Showing licenses ..." );
		result.setProperty(CONF_PROBLEM_LAUNCHING_THE_APPLICATION, "Problem launching application" );

		result.setProperty(CONF_FROM_PAGE_MUST_BE_BETWEEN, "From page must be between" );
		result.setProperty(CONF_TO_PAGE_MUST_BE_BETWEEN, "To page must be between" );
		result.setProperty(CONF_FROM_PAGE_MUST_BE_LESSER_OR_EQUAL_THAN_TO_PAGE, "From page must be lesser or equal than" );

		result.setProperty(CONF_INVALID_INTEGER, "Invalid integer" );

		return( result );
	}

	protected String M_getPropertiesNameFromClassPath( String language )
	{
		String result;
//		result = a_pathPropertiesInJar + sa_dirSeparator + language + sa_dirSeparator + a_configurationFileName;
		result = _baseConfiguration.getInternationalPropertiesPathInJar() + "/" + language + "/" + a_configurationFileName;
		return( result );
	}
	
	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = null;
		
		try
		{
			result = cargarPropertiesClassPath( M_getPropertiesNameFromClassPath( language ) );
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
			result = null;
		}
		
		if( result == null )
		{
			result = M_getDefaultProperties2(language);
		}
		else
		{
			result = M_makePropertiesAddingDefaults( result, M_getDefaultProperties2(language) );
		}

		return( result );
	}
}
