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
package com.frojasg1.general.xml.persistency.container;

import com.frojasg1.applications.common.configuration.ParameterListConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.combohistory.impl.TextComboBoxHistoryWithProperties;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.xml.model.KeyModel;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ContainerOfModels<KK, MM extends KeyModel<KK>>
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected ParameterListConfiguration _itemsConf = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

	protected TextComboBoxHistoryWithProperties _cbContent = null;

	protected Function<KK, String> _relativeFileNameCreatorFunction = null;

	// function for DefaultConstructorInitCopier
	public ContainerOfModels()
	{
	}

	// function for DefaultConstructorInitCopier
	public void init( BaseApplicationConfigurationInterface appliConf,
						Function<KK, String> fileNameCreatorFunction )
	{
		_appliConf = appliConf;

//		_map = new HashMap<>();
		_itemsConf = createListOfModelNamesConfiguration();

		_relativeFileNameCreatorFunction = fileNameCreatorFunction;
	}

	// function for DefaultConstructorInitCopier
	public void init( ContainerOfModels<KK, MM> that )
	{
		_itemsConf = _copier.copy( that._itemsConf );
//		if( _itemsConf == null )
//			_itemsConf = createListOfModelNamesConfiguration();
//		_copier.copy( _itemsConf, that._itemsConf );
		_cbContent = _copier.copy( that._cbContent );
//		_copier.copy( getComboBoxContent(), that._cbContent );
	}

	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected abstract ParameterListConfiguration createListOfModelNamesConfiguration();

	public abstract void add( MM model );

	protected abstract MM createModelObject();

	public void loadItemList() throws ConfigurationException
	{
		if( _itemsConf.configurationFileExists() )
		{
			_itemsConf.M_openConfiguration();
		}
/*
		else
		{
			getComboBoxContent().addItem( getDefaultGlobalRegexConfigurationFileName() );
		}
*/
	}

	public TextComboBoxHistoryWithProperties getComboBoxContent()
	{
		if( _cbContent == null )
			_cbContent = createComboBoxHistory();

		return( _cbContent );
	}

	public TextComboBoxHistoryWithProperties createComboBoxHistory()
	{
		TextComboBoxHistoryWithProperties result = new TextComboBoxHistoryWithProperties( null, _itemsConf );

		return( result );
	}
/*
	public List<String> getListOfFiles()
	{
		return( this.getComboBoxContent().getListOfItems().stream()
					.map( item -> getRelativeFileNameFromItemList(item) )
					.collect( Collectors.toList() ));
	}
*/
	public abstract Collection<MM> getCollectionOfModelItems();

	public ParameterListConfiguration getItemsConf()
	{
		return( _itemsConf );
	}

	protected String createRelativeFileName( KK key )
	{
		return( _relativeFileNameCreatorFunction.apply( key ) );
	}

	public boolean elementExists( KK key )
	{
		boolean result = false;
		String newFileName = createRelativeFileName( key );
		if( newFileName != null )
		{
			for( MM model: getCollectionOfModelItems() )
				if( newFileName.equals(createRelativeFileName( model.getKey() ) ) )
				{
					result = true;
					break;
				}
		}

		return( result );
	}

	public boolean elementExists( MM model )
	{
		boolean result = false;
		if( model != null )
			result = elementExists( model.getKey() );

		return( result );
	}

	public String getRelativeFileNameFromItemList( String item )
	{
		return( item + ".xml" );
	}
}
