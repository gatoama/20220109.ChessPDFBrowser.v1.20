/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.chesspdfbrowser.application;

import com.frojasg1.libpdfbox.impl.PdfboxFactory;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.startapp.StartApplication;
import com.frojasg1.general.desktop.init.InitGenericDesktop;
import com.frojasg1.general.genericdesktop.about.animations.DefaultTorusAnimationForAboutImplFactory;
import com.frojasg1.general.jersey.queries.init.InitJersey;

/**
 *
 * @author Usuario
 */
public class Main
{
	protected static final String APPLICATION_NAME = "ChessPDFBrowser";

	public static String getApplicationName()
	{
		return( APPLICATION_NAME );
	}

	protected static void init()
	{
		InitGenericDesktop.preInit();
		InitJersey.init();
	}

	protected static ApplicationInitContext createInitContext()
	{
		ApplicationInitContext result = new ApplicationInitContext();
		result.setApplicationName(APPLICATION_NAME);
		result.setAnimationForAboutFactory( new DefaultTorusAnimationForAboutImplFactory() );
		result.setPdfFactory( new PdfboxFactory() );

		return( result );
	}

	public static void main( String[] args )
	{
		init();

		StartApplication sa = new StartApplication();
		sa.setApplicationContext( createInitContext() );

		sa.startApplication();
	}
}
