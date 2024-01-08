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
package com.frojasg1.general.desktop.copypastepopup;

import com.frojasg1.applications.common.configuration.ConfigurationFromClassPath;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.GenericDesktopConstants;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.util.Properties;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ConfForTextPopupMenu extends ConfigurationFromClassPath
{
	protected static ConfForTextPopupMenu _instance = null;

	public static final String CONF_FILE_NAME = "CopyPastePopupMenu.properties";
	protected static final String sa_PROPERTIES_PATH_IN_JAR=GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR;

	public static final String CONF_REMOVE = "REMOVE";
	public static final String CONF_CUT = "CUT";
	public static final String CONF_COPY = "COPY";
	public static final String CONF_COPY_ADDRESS = "COPY_ADDRESS";
	public static final String CONF_PASTE = "PASTE";
	public static final String CONF_UNDO = "UNDO";
	public static final String CONF_REDO = "REDO";

	protected BaseApplicationConfigurationInterface _baseConf = null;
	protected boolean _alreadyRegisteredToLanguageServer = false;

	public ConfForTextPopupMenu( BaseApplicationConfigurationInterface baseConf )
	{
		super( baseConf.getConfigurationMainFolder(),
				baseConf.getApplicationNameFolder(),
				baseConf.getApplicationGroup(),
				CONF_FILE_NAME,
				sa_PROPERTIES_PATH_IN_JAR,
				null );

		_baseConf = baseConf;
	}

	public static ConfForTextPopupMenu create( BaseApplicationConfigurationInterface baseConf )
	{
		try
		{
			_instance = new ConfForTextPopupMenu( baseConf );
			_instance.M_openConfiguration();
			_instance.registerToChangeLanguageAsObserver(baseConf);

			_instance.changeLanguage( _instance._baseConf.getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( instance() );
	}

	public static ConfForTextPopupMenu instance()
	{
		return( _instance );
	}

	@Override
	public void M_openConfiguration( String fileName ) throws ConfigurationException
	{
		super.M_openConfiguration(fileName);
	}

	@Override
	public Properties M_getDefaultProperties( String language )
	{
		Properties result = new Properties();

		result.setProperty(CONF_REMOVE, "Remove" );
		result.setProperty(CONF_CUT, "Cut" );
		result.setProperty(CONF_COPY, "Copy" );
		result.setProperty(CONF_COPY_ADDRESS, "Copy Address" );
		result.setProperty(CONF_PASTE, "Paste" );
		result.setProperty(CONF_UNDO, "Undo" );
		result.setProperty(CONF_REDO, "Redo" );

		return( result );
	}
}
