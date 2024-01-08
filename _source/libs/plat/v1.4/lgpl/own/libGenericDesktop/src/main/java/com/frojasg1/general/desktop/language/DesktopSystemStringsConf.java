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
package com.frojasg1.general.desktop.language;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.GenericConstants;
import com.frojasg1.general.desktop.GenericDesktopConstants;

/**
 *
 * @author Usuario
 */
public class DesktopSystemStringsConf extends InternationalizedStringConfImp
{
	public static final String CONF_DETAILS_MODE = "DETAILS_MODE";
	public static final String CONF_LIST_MODE = "LIST_MODE";

	public static final String GLOBAL_CONF_FILE_NAME = "DesktopSystemStringsConf.properties";

	protected static class LazyHolder
	{
		protected static final DesktopSystemStringsConf INSTANCE = new DesktopSystemStringsConf();
	}

	protected BaseApplicationConfigurationInterface _baseConfiguration = null;

	public static DesktopSystemStringsConf instance()
	{
		return( LazyHolder.INSTANCE );
	}

	protected DesktopSystemStringsConf()
	{
		super( GLOBAL_CONF_FILE_NAME, GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalStrings();
	}

	protected void registerInternationalStrings()
	{
		registerInternationalString(CONF_DETAILS_MODE, "Details" );
		registerInternationalString(CONF_LIST_MODE, "List" );
	}
}
