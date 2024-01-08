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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.impl;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items.ChessEngineConfigurationMap;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.EngineInstanceConfiguration_JDialog;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.updater.EngineInstanceConfigurationUpdater;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineComboControllerBase;
import com.frojasg1.general.ClassFunctions;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.view.ViewComponent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineComboControllerForChessEngineConfiguration extends EngineComboControllerBase
{
	protected BiConsumer< EngineInstanceConfiguration, ChessEngineConfiguration > _functionToExecuteOnChessEngineConfigurationSuccessfulodification;
	protected ChessEngineConfigurationPersistency _chessEngineConfigurationPersistency = null;
	protected EngineInstanceConfigurationUpdater _confUpdat = null;
	protected Runnable _runnableCallbackOnChessEngineConfigurationModification = null;

	public EngineComboControllerForChessEngineConfiguration( ChessEngineConfigurationPersistency chessEngineConfigurationPersistency,
						BiConsumer< EngineInstanceConfiguration, ChessEngineConfiguration > functionToExecuteOnChessEngineConfigurationSuccessfulodification )
	{
		_chessEngineConfigurationPersistency = chessEngineConfigurationPersistency;
		_functionToExecuteOnChessEngineConfigurationSuccessfulodification = functionToExecuteOnChessEngineConfigurationSuccessfulodification;
	}

	@Override
	public void init( BaseApplicationConfigurationInterface appliConf,
						ChessEngineConfigurationMap chessEngineConfigurationMap,
						ChainedParentComboBoxGroupManager enginesCbgMan,
						ViewComponent parentWindow )
	{
		super.init( appliConf, chessEngineConfigurationMap, enginesCbgMan, parentWindow );

		_confUpdat = createAndInitEngineInstanceConfigurationUpdater(parentWindow);
	}

	protected EngineInstanceConfigurationUpdater createAndInitEngineInstanceConfigurationUpdater(ViewComponent parentWindow)
	{
		EngineInstanceConfigurationUpdater result = createEngineInstanceConfigurationUpdater();

		result.setParentJDial( ClassFunctions.instance().cast( parentWindow, JDialog.class ) );
		result.setParentJFrame( ClassFunctions.instance().cast( parentWindow, JFrame.class ) );

		result.init( _chessEngineConfigurationPersistency, getAppliConf() );
		result.setChessEngineConfigurationMap(getChessEngineConfigurationMap());

		return( result );
	}

	protected EngineInstanceConfigurationUpdater getConfUpdat()
	{
		return( _confUpdat );
	}

	protected EngineInstanceConfigurationUpdater createEngineInstanceConfigurationUpdater()
	{
		return( new EngineInstanceConfigurationUpdater( (JFrame) null ) {

				@Override
				public void processConfigurationDialog( InternationalizationInitializationEndCallback iiec,
															EngineInstanceConfiguration eic )
				{
					super.processConfigurationDialog( iiec, eic );

					if( getRunnableCallback() != null )
						getRunnableCallback().run();
				}

				@Override
				protected void processOnSuccess( EngineInstanceConfiguration eic, ChessEngineConfiguration dialResult )
				{
					_copier.copy( eic.getChessEngineConfiguration(), dialResult );

					if( getFunctionToExecuteOnChessEngineConfigurationSuccessfulodification() != null )
						getFunctionToExecuteOnChessEngineConfigurationSuccessfulodification().accept(eic, dialResult);
				}
		});
	}

	@Override
	public String modifyEngineConfiguration( String engineName, Consumer<String> callback )
	{
		String result = null;

		setRunnableCallback( () -> invokeCallback( callback, engineName ) );
/*	
		EngineInstanceConfiguration eiConf = getChessEngineConfigurationMap().get(engineName);
		if( eiConf != null )
		{
			EngineInstanceConfiguration_JDialog dial = createEngineInstanceConfiguration_JDialog( eiConf,
				(iiec) -> processModifyEngineConfiguration( iiec, eiConf, callback ) );
		}
*/
		_confUpdat.launchConfigurationDialog(engineName);

		return( result );
	}
/*
	public void processModifyEngineConfiguration( InternationalizationInitializationEndCallback iiec,
									EngineInstanceConfiguration eiConf,
									Consumer<String> callback )
	{
		String result = null;

		EngineInstanceConfiguration_JDialog dial = (EngineInstanceConfiguration_JDialog) iiec;
		dial.setTitle( dial.getInternationalString(EngineInstanceConfiguration_JDialog.CONF_VIEW_ENGINE) );

		dial.setVisibleWithLock(true);
		if( dial.wasSuccessful() )
		{
			EngineInstanceConfiguration resultEiConf = dial.getResult();
			result = eiConf.getName();

			_copier.copy( eiConf, resultEiConf );
		}

		invokeCallback( callback, result );
	}
*/
	protected BiConsumer< EngineInstanceConfiguration, ChessEngineConfiguration > getFunctionToExecuteOnChessEngineConfigurationSuccessfulodification()
	{
		return( _functionToExecuteOnChessEngineConfigurationSuccessfulodification );
	}

	protected void setRunnableCallback( Runnable runnableCallbackOnChessEngineConfigurationModification )
	{
		_runnableCallbackOnChessEngineConfigurationModification = runnableCallbackOnChessEngineConfigurationModification;
	}

	protected Runnable getRunnableCallback()
	{
		return( _runnableCallbackOnChessEngineConfigurationModification );
	}
}
