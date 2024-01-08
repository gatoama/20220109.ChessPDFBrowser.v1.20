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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.updater;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items.ChessEngineConfigurationMap;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.ChessEngineConfiguration_JDialog;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineInstanceConfigurationUpdater
{
	protected ChessEngineConfigurationPersistency _chessEnginePersistency = null;
	protected BaseApplicationConfigurationInterface _appliConf = null;

	protected ChessEngineConfigurationMap _chessEngineConfigurationMap = null;

	protected JDialog _parentJDial = null;
	protected JFrame _parentJFrame = null;

	public EngineInstanceConfigurationUpdater( JDialog parent )
	{
		_parentJDial = parent;
	}

	public EngineInstanceConfigurationUpdater( JFrame parent )
	{
		_parentJFrame = parent;
	}

	public void init( ChessEngineConfigurationPersistency chessEnginePersistency,
					BaseApplicationConfigurationInterface appliConf )
	{
		setChessEngineConfigurationPersistency( chessEnginePersistency );
		setApplicationConfiguration( appliConf );
	}

	public void init( ChessEngineConfigurationMap chessEngineConfigurationMap,
					BaseApplicationConfigurationInterface appliConf )
	{
		setChessEngineConfigurationMap( chessEngineConfigurationMap );
		setApplicationConfiguration( appliConf );
	}

	public void setParentJDial( JDialog parent )
	{
		_parentJDial = parent;
	}

	public void setParentJFrame( JFrame parent )
	{
		_parentJFrame = parent;
	}

	public void setChessEngineConfigurationPersistency( ChessEngineConfigurationPersistency chessEnginePersistency )
	{
		_chessEnginePersistency = chessEnginePersistency;
	}

	public void setChessEngineConfigurationMap( ChessEngineConfigurationMap chessEngineConfigurationMap )
	{
		_chessEngineConfigurationMap = chessEngineConfigurationMap;
	}

	public void setApplicationConfiguration( BaseApplicationConfigurationInterface appliConf )
	{
		_appliConf = appliConf;
	}

	protected ChessEngineConfigurationPersistency getChessEngineConfigurationPersistency()
	{
		return( _chessEnginePersistency );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected ChessEngineConfigurationMap getChessEngineConfigurationMap()
	{
		ChessEngineConfigurationMap result = null;
		if( ( getChessEngineConfigurationPersistency() != null ) )
		{
			result = getChessEngineConfigurationPersistency().getModelContainer();
		}

		if( result == null )
			result = _chessEngineConfigurationMap;

		return( result );
	}

	protected EngineInstanceConfiguration getEngineInstanceConfiguration( String engineName )
	{
		return( getChessEngineConfigurationMap().get(engineName) );
	}

	public void launchConfigurationDialog( String engineName )
	{
		EngineInstanceConfiguration eic = getEngineInstanceConfiguration( engineName );
		launchConfigurationDialog( eic );
	}

	public void launchConfigurationDialog(EngineInstanceConfiguration eic )
	{
		if( eic != null )
		{
			ChessEngineConfiguration model = eic.getChessEngineConfiguration();
			launchConfigurationDialog( model, eic );
		}
	}

	public void launchConfigurationDialog( ChessEngineConfiguration model )
	{
		launchConfigurationDialog( model, null );
	}

	protected void launchConfigurationDialog( ChessEngineConfiguration model, EngineInstanceConfiguration eic )
	{
		if( model != null )
		{
			ChessEngineConfiguration_JDialog dial = createChessEngineConfiguration_JDialog(eic);
			dial.init( model );
		}
	}

	protected ChessEngineConfiguration_JDialog createChessEngineConfiguration_JDialog(EngineInstanceConfiguration eic)
	{
		ChessEngineConfiguration_JDialog result = null;
		if( _parentJDial != null )
			result = new ChessEngineConfiguration_JDialog(_parentJDial, true, getAppliConf(), iiec -> processConfigurationDialog( iiec, eic ) );
		else
			result = new ChessEngineConfiguration_JDialog(_parentJFrame, true, getAppliConf(), iiec -> processConfigurationDialog( iiec, eic ) );

		return( result );
	}
							
	public void processConfigurationDialog( InternationalizationInitializationEndCallback iiec,
												EngineInstanceConfiguration eic )
	{
		ChessEngineConfiguration_JDialog dial = (ChessEngineConfiguration_JDialog) iiec;

		dial.setVisibleWithLock( true );

		if( dial.wasSuccessful() )
			processOnSuccess( eic, dial.getResult() );
	}

	protected void processOnSuccess( EngineInstanceConfiguration eic, ChessEngineConfiguration dialResult )
	{
		if( eic != null )
			eic.setChessEngineConfiguration( dialResult );
	}
}
