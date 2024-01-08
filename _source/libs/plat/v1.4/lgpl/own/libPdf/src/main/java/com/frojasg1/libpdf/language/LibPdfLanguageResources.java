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
package com.frojasg1.libpdf.language;

import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindowFunctions;
import com.frojasg1.general.desktop.view.newversion.NewVersionFoundJDialog;
import com.frojasg1.general.language.LanguageResources;
import com.frojasg1.libpdf.constants.LibPdfConstants;
import com.frojasg1.libpdf.view.panels.PdfContentPanel;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LibPdfLanguageResources extends LanguageResources
{
	protected static LibPdfLanguageResources _instance;

	public static LibPdfLanguageResources instance()
	{
		if( _instance == null )
		{
			_instance = new LibPdfLanguageResources();
			LanguageResources.instance().addLanguageResource(_instance);
		}
		return( _instance );
	}

	@Override
	public void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", PdfContentPanel.GLOBAL_CONF_FILE_NAME );
	}

	protected String getPropertiesPathInJar()
	{
		return( LibPdfConstants.sa_PROPERTIES_PATH_IN_JAR );
	}
}
