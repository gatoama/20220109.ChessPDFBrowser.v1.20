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
package com.frojasg1.general.combohistory.impl;

import com.frojasg1.applications.common.configuration.ParameterListConfiguration;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.ExecutionFunctions;
import java.util.List;
import com.frojasg1.general.combohistory.TextComboBoxContentWithConfiguration;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.util.ArrayList;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TextComboBoxHistoryWithProperties extends TextComboBoxHistory
	implements TextComboBoxContentWithConfiguration
{
	protected ParameterListConfiguration _conf = null;

	// function for DefaultConstructorInitCopier
	public TextComboBoxHistoryWithProperties( )
	{
		
	}

	public TextComboBoxHistoryWithProperties( Integer maxItemsToSave,
								ParameterListConfiguration conf )
	{
		this( maxItemsToSave );

		_conf = conf;
		init( _conf.getList() );
	}

	public TextComboBoxHistoryWithProperties( Integer maxItemsToSave )
	{
		super( maxItemsToSave );
	}

	public void init( List<String> initialList )
	{
		super.init( initialList );
	}

	// function for DefaultConstructorInitCopier
	public void init(TextComboBoxHistoryWithProperties that)
	{
		super.init( (TextComboBoxHistory) that );

		_conf = _copier.copy( that._conf );
	}

	protected List<String> loadItems_internal()
	{
/*
		if( _conf == null )
		{
			throw( new RuntimeException( "loadItems_internal:     _conf is null. It must be set, or loadItems_internal function must be overriden" ) );
		}
*/
		List<String> result = null;
		if( _conf != null )
		{
			ExecutionFunctions.instance().safeMethodExecution( () -> _conf.M_openConfiguration() );
			result = _conf.getList();
		}

		return( result );
	}

	@Override
	public void loadItems()
	{
		List<String> list = loadItems_internal();

		setColectionOfItems( list );
	}

	@Override
	public void save() throws ConfigurationException
	{
		if( _conf != null )
		{
			List<String> list = _listOfItems;

			if( ( _maxItemsToSave != null ) && ( _listOfItems.size() > _maxItemsToSave ) )
				list = CollectionFunctions.instance().copyLimitingNumElems(_listOfItems, _maxItemsToSave);

			_conf.setList(list);

			_conf.M_saveConfiguration();
		}
//		else
//			throw( new RuntimeException( "save function called and _conf is null. You must set _conf in the constructor or to override save function." ) );
	}

	public ParameterListConfiguration getConf()
	{
		return( _conf );
	}
}
