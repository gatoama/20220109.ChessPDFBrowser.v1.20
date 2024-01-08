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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.impl;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items.ChessEngineConfigurationMap;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineComboControllerBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.impl.EngineComboControllerForEngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.WholeEngineMasterComboBase;
import com.frojasg1.general.view.ViewComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class WholeEngineMasterComboForEngineInstanceConfiguration extends WholeEngineMasterComboBase
{
	public WholeEngineMasterComboForEngineInstanceConfiguration( BaseApplicationConfigurationInterface appliConf,
																ChessEngineConfigurationMap chessEngineConfigurationMap,
																ViewComponent parentWindow )
	{
		super( appliConf, chessEngineConfigurationMap, parentWindow );
	}

	@Override
	protected EngineComboControllerBase createEngineComboController()
	{
		return( new EngineComboControllerForEngineInstanceConfiguration() );
	}
}
