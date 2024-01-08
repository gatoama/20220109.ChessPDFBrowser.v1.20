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
package com.frojasg1.chesspdfbrowser.recognizer.store.set;

import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.correlator.CorrelationResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.distance.PatternTypesMeanError;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.distance.PatternsMeanError;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.distance.map.PatternDistanceMap;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.distance.map.PatternTypeDistanceMap;
import com.frojasg1.chesspdfbrowser.recognizer.utils.RecognitionUtils;
import com.frojasg1.general.number.DoubleFunctions;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.image.pixel.impl.PixelStats;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.structures.Pair;
import com.frojasg1.general.xml.model.KeyModel;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessFigurePatternSet implements KeyModel<String>
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	public static final String EMPTY_BLACK_SQUARE_TYPE = "BBBB";
	public static final String EMPTY_WHITE_SQUARE_TYPE = "WWWW";

	protected static final int TOLERANCE_TO_MATCH_DIMENSION = 4;

	protected static final ChessGamePositionBase EMPTY_CHESS_POSITION = new ChessGamePositionBase();

	protected int _edgeLength = 0;

	protected Map<String, List<ChessFigurePattern>> _map = null;

	protected String _singleFolderName = null;

//	protected Pair<Long, Long> _luminanceForEmptySquares = null;
	protected Pair<ComponentsStats, ComponentsStats> _emptySquareComponentStats = null;

	protected List<Pair<Dimension, ChessBoardGridResult> > _successfulGrids = null;

	protected boolean _hasBeenModified = false;



	// the key is any combination of the two letters B and W (a total of four elements)
	// the value is an object with two attributes:
	// * the avarage of standard deviation of each component in the images with that letter combination
	// * and the minimum of the mean square errors evaluated over all pairs of patterns of that two letter combination
	// except those pairs build of two patterns of the same type.
	// Letter combinatios:
	// BB -> black piece on black box
	// BW -> black piece on white box
	// WB -> white piece on black box
	// WW -> white piece on white box
//	protected Map<String, ImageSummaryStats> _imageSummaryStatsMap = null;

//	protected double _maxMeanErrorThrehold = RecognitionUtils.MAX_MEAN_ERROR_FOR_SUMMARIZED_IMAGE_MATCH;

	// function for DefaultConstructorInitCopier
	public void ChessFigurePatternSet()
	{
		
	}

	// function for DefaultConstructorInitCopier
	public synchronized void init( ChessFigurePatternSet that )
	{
		_edgeLength = that._edgeLength;

		_map = _copier.copyMap( that._map );
		setThisAsParent();

		_singleFolderName = that._singleFolderName;

		_emptySquareComponentStats = _copier.copy( that._emptySquareComponentStats );

		_successfulGrids = _copier.copy( that._successfulGrids );

//		_imageSummaryStatsMap = _copier.copy( that._imageSummaryStatsMap );
	}

	protected void setThisAsParent()
	{
		for( ChessFigurePattern pattern: getTotalListOfPatterns() )
			pattern.setParent( this );
	}

	public void init( String singleFolderName )
	{
		setName( singleFolderName );
		_map = createMap();
		_emptySquareComponentStats = null;
		_successfulGrids = createList();

//		_imageSummaryStatsMap = createMap();
	}
/*
	public void setMaxMeanErrorThreshold( double value )
	{
		_maxMeanErrorThrehold = value;
	}

	public double getMaxMeanErrorThreshold()
	{
		return( _maxMeanErrorThrehold );
	}

	public Map<String, ImageSummaryStats> getImageSummaryStatsMap()
	{
		return( _imageSummaryStatsMap );
	}

	public void putImageSummaryStats( String wbCombination, ImageSummaryStats imageSummaryStats )
	{
		if( imageSummaryStats != null )
		{
			if( imageSummaryStats.getElemCount() == 0 )
				imageSummaryStats.setElemCount( countNumElems( wbCombination ) );

			_imageSummaryStatsMap.put( wbCombination, imageSummaryStats );
		}
	}
*/
	protected int countNumElems( String wbCombination )
	{
		int result = (int) getTotalListOfPatterns().stream()
			.filter( (pt) -> isWbCombination(pt, wbCombination ) )
			.count();

		return( result );
	}

	protected boolean isWbCombination( ChessFigurePattern pattern, String wbCombination )
	{
		String patWbComb = getWbCombination( pattern );

		return( Objects.equals( patWbComb, wbCombination ) );
	}

	protected String getWbCombination( ChessFigurePattern pattern )
	{
		return( pattern.getWbCombination() );
	}

	public void setName( String singleFolderName )
	{
		_singleFolderName = singleFolderName;
	}

	public boolean hasBeenModified()
	{
		return( _hasBeenModified );
	}

	public void setHasBeenModified( boolean value )
	{
		_hasBeenModified = value;
	}

	protected ComponentsStats createComponentsStats()
	{
		ComponentsStats result = new ComponentsStats();
		result.init();

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

	public int getEdgeLength() {
		return _edgeLength;
	}

	public void setEdgeLength(int _edgeLength) {
		this._edgeLength = _edgeLength;
	}

	public String getSingleFolderName() {
		return _singleFolderName;
	}

	public void setSingleFolderName(String _singleFolderName) {
		this._singleFolderName = _singleFolderName;
	}

/*
	public Pair<Long, Long> getLuminanceForEmptySquares() {
		return _luminanceForEmptySquares;
	}

	public void setLuminanceForEmptySquares(Pair<Long, Long> _luminanceForEmptySquares) {
		this._luminanceForEmptySquares = _luminanceForEmptySquares;
	}
*/

	public Pair<ComponentsStats, ComponentsStats> getEmptySquaresComponentsStats()
	{
		return( _emptySquareComponentStats );
	}

	public void setEmptySquaresComponentsStats(Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats)
	{
		this._emptySquareComponentStats = emptySquareComponentStats;
	}

	public Map<String, List<ChessFigurePattern>> getMap()
	{
		return( _map );
	}

	public List<Pair<Dimension, ChessBoardGridResult> > getListOfPairsImageSizeGrid()
	{
		List<Pair<Dimension, ChessBoardGridResult> > result = new ArrayList<>();
		synchronized( _successfulGrids )
		{
			for( Pair<Dimension, ChessBoardGridResult> pair: _successfulGrids )
				result.add( pair );
		}

		return( result );
	}


	public List<ChessBoardGridResult> getListOfGrids( Dimension imageSize )
	{
		List<ChessBoardGridResult> result = new ArrayList<>();

		synchronized( _successfulGrids )
		{
			for( Pair<Dimension, ChessBoardGridResult> pair: _successfulGrids )
			{
				if( matches( pair.getKey(), imageSize ) )
					result.add( pair.getValue() );
			}
		}

		return( result );
	}

	public void addGrid( Dimension imageSize, ChessBoardGridResult grid )
	{
		if( ( imageSize != null ) && ( grid != null ) )
		{
			synchronized( _successfulGrids )
			{
				if( !contains( _successfulGrids, imageSize, grid ) )
				{
					setHasBeenModified( true );
					_successfulGrids.add( new Pair<>( imageSize, grid ) );
				}
			}
		}
	}

	protected boolean contains( List<Pair<Dimension, ChessBoardGridResult> > sizeGridList,
								Dimension imageSize, ChessBoardGridResult grid )
	{
		boolean result = false;

		for( Pair<Dimension, ChessBoardGridResult> pair: sizeGridList )
		{
			if( matches( pair, imageSize, grid ) )
			{
				result = true;
				break;
			}
		}

		return( result );
	}

	protected boolean matches( Pair<Dimension, ChessBoardGridResult> pair,
								Dimension imageSize, ChessBoardGridResult grid )
	{
		boolean result = false;
		if( ( pair != null ) && ( imageSize != null ) && ( grid != null ) )
		{
			result = matches( pair.getKey(), imageSize ) && matches( pair.getValue(), grid );
		}

		return( result );
	}

	protected boolean matches( Dimension dimen1, Dimension dimen2 )
	{
		boolean result = false;
		if( ( dimen1 != null ) && ( dimen2 != null ) )
			result = matchesDimension( dimen1.width, dimen2.width ) &&
					matchesDimension( dimen1.height, dimen2.height );

		return( result );
	}

	protected boolean matchesDimension( int v1, int v2 )
	{
		return( IntegerFunctions.abs( v1 - v2 ) <= TOLERANCE_TO_MATCH_DIMENSION );
	}

	protected boolean matches( ChessBoardGridResult grid1, ChessBoardGridResult grid2 )
	{
		boolean result = Objects.equals( grid1, grid2 );
		if( ! result && ( grid1 != null ) && ( grid2 != null ) )
		{
			result = ( grid1.getEdgeLength() == grid2.getEdgeLength() ) &&
					Objects.equals( grid1.getBoxBoundsInsideImage(1, 1),
									grid2.getBoxBoundsInsideImage(1, 1) );
		}

		return( result );
	}

	public synchronized List<ChessFigurePattern> getListOfPatternsByType( String type )
	{
		List<ChessFigurePattern> result = new ArrayList<>();
		List<ChessFigurePattern> list = _map.get(type);
		if( list != null )
			result.addAll( list );

		return( result );
	}

	public List<ChessFigurePattern> getEmptyThresholdPatternlist()
	{
		List<ChessFigurePattern> list = getTotalListOfPatterns();
		List<ChessFigurePattern> result = list.stream()
			.filter( (cfp) -> cfp.getMeanErrorThreshold() == null )
			.collect( Collectors.toList() );

		return( result );
	}

	public synchronized List<ChessFigurePattern> getTotalListOfPatterns()
	{
		List<ChessFigurePattern> result = createList();
		for( List<ChessFigurePattern> list: _map.values() )
			result.addAll( list );

		return( result );
	}

	public synchronized int getTotalNumberOfPatterns()
	{
		int result = 0;
		for( List<ChessFigurePattern> list: _map.values() )
			result += list.size();

		return( result );
	}

	protected synchronized List<ChessFigurePattern> getOrCreateListOfPatternsByType( String type )
	{
		List<ChessFigurePattern> result = _map.get(type);

		if( result == null )
		{
			result = createList();
			_map.put( type, result );
		}

		return( result );
	}

	public synchronized void add( ChessFigurePattern pattern )
	{
		if( pattern != null )
		{
			setHasBeenModified( true );
			getOrCreateListOfPatternsByType( pattern.getType() ).add( pattern );
		}
	}

	public synchronized ChessFigurePattern addPattern( String type )
	{
		ChessFigurePattern result = null;
		if( type != null )
		{
			validatePatternType( type );
			String newName = getNewPatternName( type );

			result = new ChessFigurePattern();
			result.init( newName, type, this );

			add( result );
		}

		return( result );
	}

	protected boolean validatePatternType( String type )
	{
		boolean result = ( type.equals( EMPTY_BLACK_SQUARE_TYPE ) ||
							type.equals( EMPTY_WHITE_SQUARE_TYPE ) ||
							(type != null) && !type.isEmpty() &&
							EMPTY_CHESS_POSITION.isPieceCode(type.substring(0,1) ) );

		return( result );
	}

	protected synchronized int getNewPatternTypeIndex( String type )
	{
		int result = 1;
		List<ChessFigurePattern> list = _map.get( type );
		if( list != null )
			result = list.size() + 1;

		return( result );
	}

	protected synchronized String getNewPatternName( String type )
	{
		return( String.format("%s_%d", type, getNewPatternTypeIndex( type ) ) );
	}

	public String getType( List<ChessFigurePattern> list )
	{
		String result = null;

		if( ( list != null ) && ! list.isEmpty() )
			result = list.get(0).getType();

		return( result );
	}

	@Override
	public String getKey()
	{
		return( getSingleFolderName() );
	}

	@Override
	public void setKey(String key)
	{
		setSingleFolderName(key);
	}

	protected List<ChessFigurePattern> getFigurePatternsWithWbCombination( String wbComb )
	{
		return( getTotalListOfPatterns().stream()
			.filter( (pt) -> isWbCombination(pt, wbComb ) )
			.collect( Collectors.toList() ) );
	}

	public PatternDistanceMap calculatePatternMap(List<ChessFigurePattern> list1)
	{
		PatternDistanceMap result = new PatternDistanceMap();
		result.init();

		Map<String, PatternsMeanError> map = result.getMap();

		Set<String> wbCombinationsAlreadyCalculated = new HashSet<>();

		for( ChessFigurePattern pat: list1 )
		{
			String patWbComb = getWbCombination( pat );
			if( !wbCombinationsAlreadyCalculated.contains( patWbComb ) )
			{
				List<ChessFigurePattern> list2 = getFigurePatternsWithWbCombination( patWbComb );
				for( ChessFigurePattern pat1: list2 )
				{
					for( ChessFigurePattern pat2: list2 )
					{
						if( pat1 != pat2 )
						{
							String key = result.getKey(pat1, pat2);
							if( map.get(key) == null )
							{
								PatternsMeanError pme = createPatternsMeanError( pat1, pat2 );
								map.put( key, pme );
		/*
								String revKey = result.getKey(pat2, pat1);
								PatternsMeanError pme2 = createPatternsMeanError( pat2, pat1, pme.getMeanError() );
								map.put( revKey, pme2 );
		*/
							}
						}
					}
				}
				wbCombinationsAlreadyCalculated.add( patWbComb );
			}
		}

		return( result );
	}

	protected double getMeanError( ChessFigurePattern pat1, ChessFigurePattern pat2 )
	{
		int borderToSkipThick = 1;
		CorrelationResult cr = RecognitionUtils.instance().getCorrelationResultForSummarizedPattern( pat1, pat2, borderToSkipThick );

		return( cr.getMeanError() );
	}

	protected PatternsMeanError createPatternsMeanError( ChessFigurePattern pat1,
														ChessFigurePattern pat2 )
	{
		return( createPatternsMeanError( pat1, pat2, getMeanError( pat1, pat2 ) ) );
	}

	protected PatternsMeanError createPatternsMeanError( ChessFigurePattern pat1,
														ChessFigurePattern pat2,
														double meanError )
	{
		PatternsMeanError result = new PatternsMeanError( pat1, pat2, meanError );

		return( result );
	}

	protected String patternTypeDistanceKey( PatternTypeDistanceMap patTypeDistMap,
										PatternsMeanError pme )
	{
		String type1 = pme.getElement1().getType();
		String type2 = pme.getElement2().getType();
		String key = patTypeDistMap.getKey(type1, type2);

		return( key );
	}

	protected PatternTypesMeanError createPatternTypesMeanError( PatternsMeanError pme,
																	double minMeanError,
																	double maxMeanError)
	{
		PatternTypesMeanError result = new PatternTypesMeanError( pme.getElement1().getType(),
					pme.getElement2().getType(), minMeanError, maxMeanError );

		return( result );
	}
/*
	public PatternTypeDistanceMap calculateTypeMap( PatternDistanceMap patternMap )
	{
		final double INITIAL_MIN_MEAN_ERROR = 1e6d;
		PatternTypeDistanceMap result = new PatternTypeDistanceMap();
		result.init();

		Map<String, PatternsMeanError> patMap = patternMap.getMap();
		Map<String, PatternTypesMeanError> typeMap = result.getMap();

		for( PatternsMeanError pme1: patMap.values() )
		{
			String key1 = patternTypeDistanceKey( result, pme1 );
			if( typeMap.get(key1) == null )
			{
				double minMeanError = INITIAL_MIN_MEAN_ERROR;
				double maxMeanError = 1.0d;
				for( PatternsMeanError pme2: patMap.values() )
				{
					String key2 = patternTypeDistanceKey( result, pme2 );
					if( key1.equals( key2 ) )
					{
						if( minMeanError > pme2.getMeanError() ) 
							minMeanError = pme2.getMeanError();

						if( maxMeanError < pme2.getMeanError() ) 
							maxMeanError = pme2.getMeanError();
					}
				}
				if( minMeanError < INITIAL_MIN_MEAN_ERROR )
					typeMap.put( key1, createPatternTypesMeanError( pme1, minMeanError, maxMeanError ) );
			}
		}

		return( result );
	}

	protected double calculateMeanErrorThreshold(PatternTypeDistanceMap typeMap)
	{
		double result = typeMap.getMap().values().stream()
						.filter( (ptme) -> !ptme.getElement1().equals( ptme.getElement2() ) )
						.map( (ptme) -> ptme.getMinMeanError() )
						.reduce( 1e6d, (subtotal, element) -> ( (subtotal>element) ? element : subtotal ) );

		return Math.max( 1.0d, result * 0.9d );
	}

	protected double calculateMeanErrorThreshold()
	{
		double result = _imageSummaryStatsMap.values().stream()
						.map( (ise) -> ise.getMinimumMeanSquareError() )
						.reduce( 1e6d, (subtotal, element) -> ( (subtotal>element) ? element : subtotal ) );

		return Math.max( 1.0d, result * 0.85d );
	}


	protected synchronized ChessFigurePattern getFirst()
	{
		ChessFigurePattern result = null;
		for( List<ChessFigurePattern> list: _map.values() )
			if( ! list.isEmpty() ) {
				result = list.get(0);
				break;
			}

		return( result );
	}
*/
	public Double getMeanErrorThreshold()
	{
		Double result = null;
		List<ChessFigurePattern> list = getTotalListOfPatterns();

		Optional<ChessFigurePattern> opt = list.stream()
			.filter( (cfp) -> ( cfp.getMeanErrorThreshold() != null ) )
			.findAny();

		if( opt.isPresent() )
			result = opt.get().getMeanErrorThreshold();

		return( result );
	}

	public boolean isMeanErrorThresholdOfAnyElementEmpty()
	{
		List<ChessFigurePattern> list = getTotalListOfPatterns();
		boolean result = list.stream().anyMatch( (cfp) -> ( cfp.getMeanErrorThreshold() == null ) );

		return( result );
	}

	public void setMeanErrorThrehold( double value )
	{
		List<ChessFigurePattern> list2 = getTotalListOfPatterns();
		for( ChessFigurePattern cfp: list2 )
			cfp.setMeanErrorThreshold(value);
	}
/*
	protected void updateImageSummaryStatsMap(List<ChessFigurePattern> newPatList,
												PatternDistanceMap patternMap )
	{
		Set<String> wbSet = getWbStringSet( newPatList );
		for( String wb: wbSet )
		{
			ImageSummaryStats iss = _imageSummaryStatsMap.get( wb );
			iss = updateImageSummaryStats( iss, wb, newPatList, patternMap );
			_imageSummaryStatsMap.put( wb, iss );
		}
	}
*/
	protected ImageSummaryStats updateImageSummaryStats( ImageSummaryStats iss, String wb,
		List<ChessFigurePattern> newPatList, PatternDistanceMap patternMap )
	{
		ImageSummaryStats result = iss;
		if( result == null )
			result = new ImageSummaryStats();

		PixelStats[] stdDevCompsArr = newPatList.stream()
			.filter( (pat) -> this.isWbCombination(pat, wb))
			.map( (pat) -> pat.calculateStandardDeviationOfSummaryImage() )
			.collect( Collectors.toList() )
			.toArray( new PixelStats[0] );
		result.updateAverageStandardDeviation(stdDevCompsArr);

		Double[] meanSquareErrorArr = patternMap.getMap().values().stream()
			.filter( (pme) -> isWbCombination(pme.getElement1(), wb) )
			.filter( (pme) -> !pme.getElement1().getType().equals( pme.getElement2().getType() ) )
			.map( (pme) -> pme.getMeanError() )
			.collect( Collectors.toList() )
			.toArray( new Double[0] );
		result.updateMinimumMeanSquareError(meanSquareErrorArr);

		return( result );
	}

	public Set<String> getWbStringSet( List<ChessFigurePattern> patList )
	{
		Set<String> result = patList.stream()
			.map( (pt) -> this.getWbCombination(pt) )
			.collect( Collectors.toSet() );

		return( result );
	}

	protected List<ChessFigurePattern> getWbPatList( List<ChessFigurePattern> newPatList, String wbBox )
	{
		return( newPatList.stream().filter((pat) -> this.isWbCombination(pat, wbBox))
			.collect( Collectors.toList() ) );
	}

	protected Map<String, Double> calculateMinMeanErrorByWbMap( List<ChessFigurePattern> patList )
	{
		Map<String, Double> result = new HashMap<>();
		Set<String> wbSet = getWbStringSet( patList );
		for( String wb: wbSet )
		{
			Double minMeanError = calculateMinMeanError( getWbPatList( patList, wb ) );
			if( minMeanError != null )
				result.put( wb, minMeanError );
		}

		return( result );
	}

	protected Map<String, Double> calculateMinMeanErrorByWbMap( List<ChessFigurePattern> patList,
																PatternDistanceMap patternDistanceMap )
	{
		Map<String, Double> result = new HashMap<>();
		Set<String> wbSet = getWbStringSet( patList );
		for( String wb: wbSet )
		{
			Double minMeanError = calculateMinMeanErrorFromMeanErrList( getWbPatMeanErrorList( patternDistanceMap, wb ) );
			if( minMeanError != null )
				result.put( wb, minMeanError );
		}

		return( result );
	}

	protected String getWbCombination( String patName )
	{
		return( RecognitionUtils.instance().getWbCombination( patName ) );
	}

	protected String getFirstPatName( String pairOfPatNames )
	{
		String result = null;
		if( pairOfPatNames != null )
			result = pairOfPatNames.split( "-" )[0];

		return( result );
	}

	protected boolean isWbCombination( String pairOfPatNames, String wbBox )
	{
		return( Objects.equals( getWbCombination( getFirstPatName( pairOfPatNames ) ), wbBox ) );
	}

	protected String getPatternType( String patternName )
	{
		return( RecognitionUtils.instance().getPatternType(patternName) );
	}

	protected boolean patternsAreNotSamePatType( String pairOfPatNames )
	{
		String[] patNames = pairOfPatNames.split( "-" );
		return( !Objects.equals( getPatternType(patNames[0]), getPatternType(patNames[1]) ) );
	}

	protected List<PatternsMeanError> getWbPatMeanErrorList( PatternDistanceMap patternDistanceMap, String wbBox )
	{
		List<PatternsMeanError> result = null;
		if( patternDistanceMap != null )
			result = patternDistanceMap.getMap().entrySet().stream()
				.filter( (entry) -> isWbCombination( entry.getKey(), wbBox ) && patternsAreNotSamePatType( entry.getKey() ) )
				.map( (entry) -> entry.getValue() )
				.collect( Collectors.toList() );
		return( result );
	}

	protected Double calculateMinMeanErrorFromMeanErrList( List<PatternsMeanError> patErrList )
	{
		return( calculateMinMeanErrorGen( patErrList, (pme) -> pme.getMeanError() ) );
	}

	protected Double calculateMinMeanError( List<ChessFigurePattern> patList )
	{
		return( calculateMinMeanErrorGen( patList, (pat) -> pat.getMeanErrorThreshold() ) );
	}

	protected <CC> Double calculateMinMeanErrorGen( List<CC> list, Function<CC, Double> mapper )
	{
		Double result = null;
		if( list != null )
			result = list.stream().map( mapper )
				.reduce( null, (met1, met2) -> DoubleFunctions.instance().min( met1, met2 ) );

		return( result );
	}

	public void recalculateErrorThresholds()
	{
		List<ChessFigurePattern> list = getEmptyThresholdPatternlist();

		PatternDistanceMap patternDistanceMap = calculatePatternMap(list);

		Map<String, Double> minMeanErrorByWbMap = calculateMinMeanErrorByWbMap( list, patternDistanceMap );

//		PatternTypeDistanceMap typeMap = calculateTypeMap(patternMap);
//		updateImageSummaryStatsMap( list, patternMap );

//		double meanErrorThreshold = calculateMeanErrorThreshold();

//		System.out.println( "PatternSet: " + getKey() );

//		System.out.println( "Pattern errors: " );
//		System.out.println( "Pattern name 1;Pattern name 2;mean error" );
//		System.out.println( patternMap.toString() );


//		System.out.println( "Pattern type 1;Pattern type 2;min mean error;max mean error" );
//		System.out.println( typeMap.toString() );

//		System.out.println( "PatternSet name;wbComb;min mean error;avg std dev" );
//		System.out.println( imageSummaryStatsMapToString() );

//		System.out.println( );
//		System.out.println( );
//		System.out.println( );
//		System.out.println( );

		updateMinMeanErrors( minMeanErrorByWbMap );


//		Double previousMeanErrorThreshold = getMeanErrorThreshold();
//		if( previousMeanErrorThreshold != null )
//			meanErrorThreshold = DoubleFunctions.instance().min( meanErrorThreshold, previousMeanErrorThreshold );

//		meanErrorThreshold = DoubleFunctions.instance().min( meanErrorThreshold, getMaxMeanErrorThreshold() );

//		setMeanErrorThrehold(meanErrorThreshold);

//		double meanErrorThreshold = RecognitionUtils.MAX_MEAN_ERROR_FOR_SUMMARIZED_IMAGE_MATCH;
//		return( meanErrorThreshold );
	}

	protected Double calculateMeanErrorThresholdFromMeanError( Double meanError )
	{
		Double result = null;
		if( meanError != null )
			result = meanError * 0.85d;

		return( result );
	}

	protected void updateMinMeanErrors( Map<String, Double> newMinMeanErrorByWbMap )
	{
		List<ChessFigurePattern> list = getTotalListOfPatterns();
		Map<String, Double> currentErrorThresholdByMap = calculateMinMeanErrorByWbMap( list );
		Set<String> wbSet = new HashSet<>();
		wbSet.addAll( newMinMeanErrorByWbMap.keySet() );
		wbSet.addAll( currentErrorThresholdByMap.keySet() );

		for( String wb: wbSet )
		{
			Double current = currentErrorThresholdByMap.get( wb );
			Double newErrorThreshold = calculateMeanErrorThresholdFromMeanError( newMinMeanErrorByWbMap.get( wb ) );

			Double result = DoubleFunctions.instance().min( current, newErrorThreshold );
			result = DoubleFunctions.instance().min( result, RecognitionUtils.MAX_MEAN_ERROR_FOR_SUMMARIZED_IMAGE_MATCH );

			if( !Objects.equals( result, current ) )
				setHasBeenModified( true );

			updateMeanErrorThreshold( getWbPatList(list, wb), result );
		}
	}

	protected void updateMeanErrorThreshold( List<ChessFigurePattern> list, Double errorThreshold )
	{
		if( list != null )
			for( ChessFigurePattern pat: list )
				pat.setMeanErrorThreshold(errorThreshold);
	}

	protected String format( Double value )
	{
		return( DoubleFunctions.instance().format( value ) );
	}
/*
	protected String imageSummaryStatsMapToString()
	{
		StringBuilder sb = new StringBuilder();

		String rc = String.format( "%n" );
		String separator = ";";
		for( Map.Entry<String, ImageSummaryStats> entry: _imageSummaryStatsMap.entrySet() ) {
			sb.append( this.getKey() ).append( separator )
				.append( entry.getKey() ).append( separator )
				.append( format( entry.getValue().getMinimumMeanSquareError() ) )
				.append( separator )
				.append( format( entry.getValue().getAverageStandardDeviation().getComponentsAverage() ) )
				.append( rc );
		}

		return( sb.toString() );
	}
*/
}
