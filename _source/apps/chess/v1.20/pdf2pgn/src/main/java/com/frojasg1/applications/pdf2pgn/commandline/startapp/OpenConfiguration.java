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
package com.frojasg1.applications.pdf2pgn.commandline.startapp;

import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.startapp.ChessOpenConfigurationBase;
import com.frojasg1.general.commandline.init.InitGenericCommandLine;
import com.frojasg1.general.exceptions.ConfigurationException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class OpenConfiguration extends ChessOpenConfigurationBase
{
	public OpenConfiguration( ApplicationInitContext initContext ) throws ConfigurationException
	{
		super(initContext);
	}

	@Override
	public void init() throws ConfigurationException
	{
		super.init();
	}

	@Override
	protected void createGenericFunctions()
	{
		// intentionally left blank
	}

	@Override
	protected void initLibraries()
	{
		InitGenericCommandLine.init( getAppliConf() );
	}

	protected void changeConfigurationDependingOnTheVersionImported()
	{
		// intentionally left blank
	}

	@Override
	protected String[] getBasicLanguages()
	{
		String[] basicLanguages = new String[]{ "EN" };

		return( basicLanguages );
	}

	@Override
	protected String[] getAvailableLanguagesInJar()
	{
		String[] availableLanguagesInJar = new String[]{ "EN" };

		return( availableLanguagesInJar );
	}

	@Override
	protected String[] getWebLanguages()
	{
		String[] webLanguages = new String[]{ "English" };

		return( webLanguages );
	}
}
