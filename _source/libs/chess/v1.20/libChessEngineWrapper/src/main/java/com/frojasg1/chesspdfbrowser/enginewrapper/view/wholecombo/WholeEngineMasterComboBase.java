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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items.ChessEngineConfigurationMap;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineComboControllerBase;
import com.frojasg1.general.desktop.view.combobox.MasterComboBoxJPanel;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.impl.ChainedParentChildComboBoxManagerBase;
import com.frojasg1.general.view.ViewComponent;
import java.util.function.BiConsumer;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineMasterComboChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class WholeEngineMasterComboBase
{
	protected MasterComboBoxJPanel _masterComboForEngines = null;

	protected EngineComboControllerBase _engineComboController = null;

	protected ChessEngineConfigurationMap _chessEngineConfigurationMap = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

	protected ViewComponent _parentWindow = null;

	protected ChessEngineConfigurationPersistency _chessEngineConfigurationPersistency = null;


	public WholeEngineMasterComboBase( BaseApplicationConfigurationInterface appliConf,
									ChessEngineConfigurationMap chessEngineConfigurationMap,
									ViewComponent parentWindow )
	{
		_chessEngineConfigurationMap = chessEngineConfigurationMap;
		_appliConf = appliConf;
		_parentWindow = parentWindow;
	}

	public WholeEngineMasterComboBase( BaseApplicationConfigurationInterface appliConf,
										ChessEngineConfigurationPersistency chessEngineConfigurationPersistency,
										ViewComponent parentWindow)
	{
		_chessEngineConfigurationPersistency = chessEngineConfigurationPersistency;
		_appliConf = appliConf;
		_parentWindow = parentWindow;
	}

	public void init()
	{
		createObjects();
	}

	public ChessEngineConfigurationPersistency getChessEngineConfigurationPersistency()
	{
		return( _chessEngineConfigurationPersistency );
	}

	public MasterComboBoxJPanel getMasterComboBoxJPanel()
	{
		return( _masterComboForEngines );
	}

	public EngineComboControllerBase getEngineComboController()
	{
		return( _engineComboController );
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
/*
	protected ChessEngineConfigurationMap getChessEngineConfigurationMap()
	{
		return( _chessEngineConfigurationMap );
	}
*/
	protected void createObjects()
	{
		CreateEngineComboControllerBaseResult creationResult = createEngineComboControllerBase();

		_engineComboController = creationResult.getEngineComboControllerBase();
		_masterComboForEngines = creationResult.getMasterComboForEngines();
	}

	protected abstract EngineComboControllerBase createEngineComboController();

	protected CreateEngineComboControllerBaseResult createEngineComboControllerBase()
	{
		EngineComboControllerBase engineComboController = createEngineComboController();
		ChainedParentChildComboBoxManagerBase engineCbgMan = new ChainedParentChildComboBoxManagerBase( null,
											getChessEngineConfigurationMap().getComboBoxContent(),
											null );
		engineCbgMan.init();

		MasterComboBoxJPanel masterComboForEngines = null;
		masterComboForEngines = createMasterComboBoxJPanel( engineCbgMan, false );

		engineComboController.init( getAppliConf(), getChessEngineConfigurationMap(), engineCbgMan, _parentWindow );

		masterComboForEngines.init();

		CreateEngineComboControllerBaseResult result = new CreateEngineComboControllerBaseResult();
		result.setEngineComboControllerBase(engineComboController);
		result.setMasterComboForEngines(masterComboForEngines);

//		masterComboForEngines.getModifyButton().setName( "name=jB_configuration,icon=com/frojasg1/generic/resources/othericons/configuration.png" );

		return( result );
	}

	protected MasterComboBoxJPanel createMasterComboBoxJPanel( ComboBoxGroupManager cbgm, boolean init )
	{
		MasterComboBoxJPanel result = new MasterComboBoxJPanel( cbgm );
		if( init )
			result.init();

		return( result );
	}

	public MasterComboBoxJPanel getMasterComboboxForEngines()
	{
		return( _masterComboForEngines );
	}


	protected static class CreateEngineComboControllerBaseResult
	{
		protected EngineComboControllerBase _engineComboControllerBase = null;
		protected MasterComboBoxJPanel _masterComboForEngines = null;

		public EngineComboControllerBase getEngineComboControllerBase() {
			return _engineComboControllerBase;
		}

		public void setEngineComboControllerBase(EngineComboControllerBase _engineComboControllerBase) {
			this._engineComboControllerBase = _engineComboControllerBase;
		}

		public MasterComboBoxJPanel getMasterComboForEngines() {
			return _masterComboForEngines;
		}

		public void setMasterComboForEngines(MasterComboBoxJPanel _masterComboForEngines) {
			this._masterComboForEngines = _masterComboForEngines;
		}

		
	}
}
