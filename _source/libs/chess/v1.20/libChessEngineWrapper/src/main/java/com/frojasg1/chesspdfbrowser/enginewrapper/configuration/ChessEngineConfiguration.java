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
package com.frojasg1.chesspdfbrowser.enginewrapper.configuration;

import com.frojasg1.chesspdfbrowser.enginewrapper.action.request.EngineActionArgs;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.SpinConfigurationItem;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineConfiguration implements EngineActionArgs
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected static final String MULTI_PV_UCI_STRING = "MultiPV";

	protected Map< String, ConfigurationItem > _map = null;

	// for Copier
	public ChessEngineConfiguration()
	{
		
	}

	public void init( ChessEngineConfiguration that )
	{
		_map = _copier.copy( that._map );
	}

	public void init()
	{
		_map = createMap();
	}

	protected <KK, VV> Map<KK, VV> createMap()
	{
		return( new HashMap<>() );
	}

	public ChessEngineConfiguration createMultiPVConf( int numberOfVariants )
	{
		ChessEngineConfiguration result = null;

		ConfigurationItem ci = _map.get(MULTI_PV_UCI_STRING);
		if( ci != null )
		{
			result = new ChessEngineConfiguration();
			result.init();

			SpinConfigurationItem copy = _copier.copy( (SpinConfigurationItem) ci );
			copy.setValue(numberOfVariants);

			result.add(copy);
		}

		return( result );
	}

	public void add( ConfigurationItem ci )
	{
		if( ci != null )
			_map.put( ci.getName(), ci );
	}

	public Map< String, ConfigurationItem > getMap()
	{
		return( _map );
	}
}
