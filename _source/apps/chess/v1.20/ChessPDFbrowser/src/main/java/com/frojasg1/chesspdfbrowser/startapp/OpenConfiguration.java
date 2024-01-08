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

import com.frojasg1.chesspdfbrowser.configuration.AppStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.init.InitGenericDesktop;
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
		DesktopGenericFunctions.createInstance( getAppliConf() );
	}

	@Override
	protected void initLibraries()
	{
		InitGenericDesktop.init( getAppliConf() );
		ChessPDFbrowserInit.init();
	}

	@Override
	public void openOtherConfiguration() throws ConfigurationException
	{
		super.openOtherConfiguration();

		try
		{
			AppStringsConf.createInstance( ApplicationConfiguration.instance() );
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			throw( ce );
		}
	}

	@Override
	public void initializeAfterImportingConfiguration() throws ConfigurationException
	{
//		ToolTipLookAndFeel.instance().init();
//		ToolTipLookAndFeel.instance().registerToChangeZoomFactorAsObserver( getAppliConf() );

		super.initializeAfterImportingConfiguration();
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
}
