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
package com.frojasg1.chesspdfbrowser.recognizer.store;

import com.frojasg1.applications.common.configuration.ParameterListConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.general.combohistory.impl.TextComboBoxHistoryWithProperties;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardRecognitionStore_old
{
	protected Map< Integer, List<ChessFigurePatternSet> > _mapOfSetsOfPatternsByEdgeLength = null;

	protected ListOfChessFigurePatternSetNamesConfiguration _itemsConf = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

	protected TextComboBoxHistoryWithProperties _cbContent = null;



	// function for DefaultConstructorInitCopier
	public ChessBoardRecognitionStore_old()
	{
		
	}

	public List<ChessBoardGridResult> getListOfGrids( Dimension imageSize )
	{
		List<ChessBoardGridResult> result = new ArrayList<>();

		for( ChessFigurePatternSet patternSet: getAllPatternSets() )
			for( ChessBoardGridResult grid: patternSet.getListOfGrids(imageSize) )
				result.add( grid );

		return( result );
	}

	public synchronized List<ChessFigurePatternSet> getAllPatternSets()
	{
		List<ChessFigurePatternSet> result = new ArrayList<>();

		for( List<ChessFigurePatternSet> list: _mapOfSetsOfPatternsByEdgeLength.values() )
			result.addAll( list );

		return( result );
	}

	protected <K,V> Map<K,V> createMap()
	{
		return new ConcurrentHashMap<>();
	}

	protected <V> List<V> createList()
	{
		return( Collections.synchronizedList( new ArrayList<>() ) );
	}

	// function for DefaultConstructorInitCopier
	public void init( BaseApplicationConfigurationInterface appliConf )
	{
		_appliConf = appliConf;

		_mapOfSetsOfPatternsByEdgeLength = createMap();
		_itemsConf = createListOfRegexWholeFilesConfiguration();
	}
/*
	// function for DefaultConstructorInitCopier
	public void init( ListOfRegexWholeFiles that )
	{
		_map = _copier.copyMap( that._map );
		_itemsConf = _copier.copy( that._itemsConf );

		_cbContent = _copier.copy( that._cbContent );
	}
*/
	public Map< Integer, List<ChessFigurePatternSet> > getMap()
	{
		return( _mapOfSetsOfPatternsByEdgeLength );
	}

	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected ListOfChessFigurePatternSetNamesConfiguration createListOfRegexWholeFilesConfiguration()
	{
		return( new ListOfChessFigurePatternSetNamesConfiguration( getAppliConf() ) );
	}

	public synchronized List<ChessFigurePatternSet> getListOfPatternSetOfEdgeLength( int edgeLength )
	{
		List<ChessFigurePatternSet> result = null;
		List<ChessFigurePatternSet> result2 = _mapOfSetsOfPatternsByEdgeLength.get( edgeLength );
		if( result2 != null )
		{
			result = createList();
			result.addAll( result2 );
		}

		return( result );
	}

	protected synchronized List<ChessFigurePatternSet> getOrCreateListOfPatternSetOfEdgeLength( int edgeLength )
	{
		List<ChessFigurePatternSet> result = _mapOfSetsOfPatternsByEdgeLength.get( edgeLength );

		if( result == null )
		{
			result = createList();
			synchronized(this)
			{
				_mapOfSetsOfPatternsByEdgeLength.put( edgeLength, result );
			}
		}

		return( result );
	}

	public void add( ChessFigurePatternSet elem )
	{
		if( elem != null )
		{
			int edgeLength = elem.getEdgeLength();
			synchronized(this)
			{
				List<ChessFigurePatternSet> list = getOrCreateListOfPatternSetOfEdgeLength( edgeLength );
				if( !list.contains( elem ) )
				{
					String newName = elem.getSingleFolderName();
					if( newName == null )
					{
						newName = getNewEmptyName( edgeLength );
						elem.setName( newName );
					}
					list.add( elem );

					_cbContent.addItem( newName );
				}
			}
		}
	}

	protected ChessFigurePatternSet createChessFigurePatternSet(String name)
	{
		ChessFigurePatternSet result = new ChessFigurePatternSet();
		result.init(name);

		return( result );
	}

	protected synchronized int getNewItemNumberForPatternSet( int edgeLength )
	{
		int result = 1;
		List<ChessFigurePatternSet> list = _mapOfSetsOfPatternsByEdgeLength.get( edgeLength );
		if( list != null )
			result = list.size() + 1;

		return( result );
	}

	protected synchronized String getNewEmptyName( int edgeLength )
	{
		return( String.format("patternSet.%d_%d", edgeLength, getNewItemNumberForPatternSet( edgeLength ) ) );
	}

	public synchronized ChessFigurePatternSet createAndAddEmptyChessFigurePatternSet( int edgeLength )
	{
//		String name = getNewEmptyName( edgeLength );
		String name = null;

		ChessFigurePatternSet result = createChessFigurePatternSet(name);
		result.setEdgeLength(edgeLength);
//		result.setFileName(newSingleFileName);

		return( result );
	}

	public ChessFigurePatternSet get( int edgeLength, String name )
	{
		ChessFigurePatternSet result = null;

		List<ChessFigurePatternSet> list = getListOfPatternSetOfEdgeLength( edgeLength );
		if( list != null )
			result = getChessFigurePatternSet( list, name );

		return( result );
	}

	protected ChessFigurePatternSet getChessFigurePatternSet( List<ChessFigurePatternSet> list,
		String name )
	{
		ChessFigurePatternSet result = null;
		if( list != null )
		{
			Optional<ChessFigurePatternSet> opt = list.stream().filter( ps -> name.equals( ps.getSingleFolderName() ) ).findFirst();
			if( opt.isPresent() )
				result = opt.get();
		}

		return( result );
	}
/*
	public RegexWholeFileModel remove( String fileName )
	{
		return( _map.remove( fileName ) );
	}

	public void rename( String oldFileName, String newFileName )
	{
		RegexWholeFileModel rwc = _map.remove( oldFileName );
		if( rwc != null )
		{
			rwc.setFileName(newFileName);
			add( rwc );
		}
	}
*/
	public void loadItemList() throws ConfigurationException
	{
		if( _itemsConf.configurationFileExists() )
			_itemsConf.M_openConfiguration();
/*
		else
		{
			getComboBoxContent().addItem( getDefaultGlobalRegexConfigurationFileName() );
		}
*/
	}
/*
	protected String getDefaultGlobalRegexConfigurationFileName()
	{
		return( DefaultConstantsForRegexConf.DEFAULT_GLOBAL_CONF_FILE_NAME );
	}
*/
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

	public List<String> getListOfFiles()
	{
		return( getComboBoxContent().getListOfItems());
	}
/*
	public Collection<RegexWholeFileModel> getColOfRegexWholeContainer()
	{
		return( _map.values() );
	}
*/
	public ParameterListConfiguration getItemsConf()
	{
		return( _itemsConf );
	}
}
