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
import com.frojasg1.general.xml.persistency.container.ContainerOfModels;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardRecognitionStore extends ContainerOfModels<String, ChessFigurePatternSet>
{
	protected Map< Integer, List<ChessFigurePatternSet> > _mapOfSetsOfPatternsByEdgeLength = null;

	// function for DefaultConstructorInitCopier
	public ChessBoardRecognitionStore()
	{
		
	}

	public void init( ContainerOfModels that )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	// function for DefaultConstructorInitCopier
	@Override
	public void init( BaseApplicationConfigurationInterface appliConf,
						Function<String, String> fileNameCreatorFunction )
	{
		super.init( appliConf, fileNameCreatorFunction );

		_mapOfSetsOfPatternsByEdgeLength = createMap();
	}

	// function for DefaultConstructorInitCopier
	public void init( ChessBoardRecognitionStore that )
	{
		super.init( that );
		_mapOfSetsOfPatternsByEdgeLength = _copier.copyMap( that._mapOfSetsOfPatternsByEdgeLength );
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

	public Map< Integer, List<ChessFigurePatternSet> > getMap()
	{
		return( _mapOfSetsOfPatternsByEdgeLength );
	}

	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
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

	@Override
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
		ChessFigurePatternSet result = createModelObject();
		result.init(name);

		return( result );
	}

	@Override
	protected ChessFigurePatternSet createModelObject()
	{
		ChessFigurePatternSet result = new ChessFigurePatternSet();

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

	@Override
	protected ParameterListConfiguration createListOfModelNamesConfiguration() {
		return( new ListOfChessFigurePatternSetNamesConfiguration( getAppliConf() ) );
	}

	@Override
	public Collection<ChessFigurePatternSet> getCollectionOfModelItems() {
		List<ChessFigurePatternSet> result = new ArrayList<>();
		for( List<ChessFigurePatternSet> list: _mapOfSetsOfPatternsByEdgeLength.values() )
			result.addAll( list );

		return( result );
	}

	@Override
	public boolean elementExists(ChessFigurePatternSet element)
	{
		boolean result = false;
		if( element != null )
		{
			List<ChessFigurePatternSet> list = _mapOfSetsOfPatternsByEdgeLength.get( element.getEdgeLength() );

			if( list != null )
			{
				for( ChessFigurePatternSet ps: list )
				{
					if( ps.getKey().equals(element.getKey() ) )
					{
						result = true;
						break;
					}
				}
			}
		}

		return( result );
	}

	@Override
	public String getRelativeFileNameFromItemList( String item )
	{
		return( this.createRelativeFileName(item) );
	}
}
