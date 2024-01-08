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
package com.frojasg1.general.desktop.init;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.files.DesktopFileFunctions;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.language.DesktopLanguageResources;
import com.frojasg1.general.desktop.matchers.InitDesktopMatchers;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InitGenericDesktop
{
	public static void preInit()
	{
//		if( System.getProperty( "java.locale.providers" ) == null )
//			System.setProperty("java.locale.providers", "COMPAT,SPI");	// compatibility of locales with the previous releases (of java-11).

		if( System.getProperty("sun.java2d.uiScale" ) == null )
			System.setProperty("sun.java2d.uiScale", "1.0");	// to avoid java resize view with high dpi.
	}

	public static void init( BaseApplicationConfigurationInterface conf )
	{
		DesktopGenericFunctions.createInstance( conf );

		DesktopLanguageResources.instance();

		DesktopFileFunctions.instance();

		new InitDesktopMatchers().init();

		Thread.setDefaultUncaughtExceptionHandler( new GenericDesktopDefaultExceptionHandler() );
	}
}
