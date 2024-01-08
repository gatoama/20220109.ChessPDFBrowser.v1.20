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
package com.frojasg1.chesspdfbrowser.recognizer.chessboard.impl;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.VertexSetInfo;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.structures.Pair;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardVertexSetsDetector
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessBoardVertexSetsDetector.properties";

	protected static final String CONF_GRID_SHOULD_NOT_BE_NULL = "GRID_SHOULD_NOT_BE_NULL";

	public static final int MINIMUM_NUMBER_OF_POINTS_ALLIGNED = 5;
	public static final int MINIMUM_NUMBER_OF_POINTS_ALLIGNED_IN_DIAGONAL = 5;
	public static final int MINIMUM_NUMBER_OF_VERTEX_IN_CHESS_BOARD_SET = 23;
	public static final int MINIMUM_NUMBER_OF_DIFFERENT_ELEMENTS_IN_EVERY_DIRECTION = 6;
	public static final int MINIMUM_SIZE_FOR_BOX_EDGE_LENGTH = 10;

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected Map< Integer, AtomicInteger > _candidatesToEdgeLength = null;
	protected BufferedImage _image = null;
	protected Rectangle _imageBounds = null;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public ChessBoardVertexSetsDetector()
	{
	}

	public List<ChessBoardGridResult> process( BufferedImage image,
												Map<Point, Long> inputMap )
	{
		_image = image;
		_imageBounds = getImageBounds( _image );

		List<ChessBoardGridResult> result = new ArrayList<>();

		List< Map<Point, Long> > rows = getSortedRows( inputMap );
		List< Map<Point, Long> > cols = getSortedColumns( inputMap );

		Set<Point> allPoints = null;
		Map.Entry<Integer, List<Set<Point>>> boxEdgeLengthEntry = null;
		ChessBoardGridResult grid = null;
		do
		{
			allPoints = getAllPoints( rows, cols );
			Map<Integer, List<Set<Point>>> classifiedCandidates = calculateBoxEdgeLength( allPoints, rows, cols );
			grid = null;
			boxEdgeLengthEntry = getBestCandidateForBoxEdgeLength( classifiedCandidates );
			while( ( grid == null ) && ( boxEdgeLengthEntry != null ) )
			{
				int boxEdgeLength = boxEdgeLengthEntry.getKey();

				Pair<Set<Point>, VertexSetInfo> bestVertexSetPair = getBestVertexSet( boxEdgeLengthEntry );
				if( bestVertexSetPair != null )
				{
					grid = getGrid( bestVertexSetPair );
					if( grid != null )
					{
						removeUsedPoints( grid, rows );
						removeUsedPoints( grid, cols );

						if( validate(grid) )
							result.add( grid );
						else
							grid = null;
					}
					else
						throw( new RuntimeException( getInternationalString( CONF_GRID_SHOULD_NOT_BE_NULL ) ) );

//					if( grid == null )
//						bestVertexSet = getBestVertexSet( boxEdgeLengthEntry );
				}

				if( grid == null )
				{
					classifiedCandidates.remove( boxEdgeLength );
					boxEdgeLengthEntry = getBestCandidateForBoxEdgeLength( classifiedCandidates );
				}
			}
		}
		while( grid != null );

		return( result );
	}

	protected Rectangle getImageBounds( BufferedImage image )
	{
		return( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
	}


	protected boolean validate( ChessBoardGridResult grid )
	{
		return( _imageBounds.contains( grid.getBoardBoundsInsideImage() ) );
	}

	protected void removeUsedPoints( ChessBoardGridResult grid,
									List< Map<Point, Long> > result )
	{
		for( Map<Point, Long> map: result )
		{
			Set<Point> set = new HashSet( map.keySet() );
			for( Point point: set )
				if( grid.getBoardBoundsInsideImage().contains( point ) )
					map.remove(point);

			for( Point pt2: grid.getListOfVertex() )
				map.remove(pt2);
		}
	}

	protected Set<Point> getAllPoints( List< Map<Point, Long> > rows,
										List< Map<Point, Long> > cols )
	{
		Set<Point> result = new HashSet<>();

		addAll( rows, result  );
		addAll( cols, result  );

		return( result );
	}

	protected void addAll( List< Map< Point, Long > > inputList, Set<Point> result )
	{
		for( Map<Point, Long> map: inputList )
			for( Point point: map.keySet() )
				if( !result.contains(point) )
					result.add( point );
	}

	protected ChessBoardGridResult getGrid( Pair<Set<Point>, VertexSetInfo> pair )
	{
		Set<Point> bestVertexSet = pair.getKey();
		VertexSetInfo vertexInfo = pair.getValue();

		int boxEdgeLength = vertexInfo.getEdgeLength();

		ChessBoardGridResult result = new ChessBoardGridResult();
		result.init(boxEdgeLength);

		int minX = vertexInfo.getMinX();
		int minY = vertexInfo.getMinY();
		int minJ = getMinJ( vertexInfo );
		int minI = getMinI( vertexInfo );
		for( Point point: bestVertexSet )
		{
			int jj = getIndexDiff( minX, point.x, boxEdgeLength ) + minJ;
			int ii = getIndexDiff( minY, point.y, boxEdgeLength ) + minI;

			result.addVertex( point, jj, ChessBoardGridResult.NUM_ROWS - ii );
		}

		return( result );
	}

	protected int getMinJ( VertexSetInfo vertexInfo )
	{
		int minCoo = vertexInfo.getMinX();
		int numElems = vertexInfo.getxElems();
		int edgeLength = vertexInfo.getEdgeLength();

		return( getMinIndex( minCoo, numElems, edgeLength ) );
	}

	protected int getMinI( VertexSetInfo vertexInfo )
	{
		int minCoo = vertexInfo.getMinY();
		int numElems = vertexInfo.getyElems();
		int edgeLength = vertexInfo.getEdgeLength();

		return( getMinIndex( minCoo, numElems, edgeLength ) );
	}

	protected int getMinIndex( int minCoo, int numElems, int edgeLength )
	{
		int result = 0;
		if( numElems == 9 )
			result = 0;
		else if( ( numElems == 8 ) || ( numElems == 7 ) )
		{
			if( minCoo >= edgeLength )
				result = 1;
			else
				result = 0;
		}

		return( result );
	}

	protected Pair<Set<Point>, VertexSetInfo> getBestVertexSet( Map.Entry<Integer, List<Set<Point>>> boxEdgeLengthEntry )
	{
		Pair<Set<Point>, VertexSetInfo> result = null;

		List<Set<Point>> list = boxEdgeLengthEntry.getValue();
		while( ( result == null ) && !list.isEmpty() )
		{
			Set<Point> maxSet = null;
			int max = -1;
			for( Set<Point> set: boxEdgeLengthEntry.getValue() )
				if( set.size() > max )
				{
					max = set.size();
					maxSet = set;
				}

			result = getFirstSetOfVertexToBuildGrid( maxSet, boxEdgeLengthEntry.getKey() );
			if( result == null )
				list.remove( maxSet );
		}

		return( result );
	}

	protected Pair<Set<Point>, VertexSetInfo> getFirstSetOfVertexToBuildGrid( Set<Point> maxSet, int boxEdgeLength )
	{
		Pair<Set<Point>, VertexSetInfo> result = null;

		int minX = getMin( maxSet, pt -> pt.x );
		int minY = getMin( maxSet, pt -> pt.y );
		int maxX = getMax( maxSet, pt -> pt.x );
		int maxY = getMax( maxSet, pt -> pt.y );

		int halfEdge = boxEdgeLength / 2;
		int rectLength = boxEdgeLength * ( ChessBoardGridResult.NUM_ELEMENTS );
		if( ( minX < maxX ) && ( minY < maxY ) )
		{
			int xIndexTo = getIndexTo( minX, maxX, boxEdgeLength );
			int yIndexTo = getIndexTo( minY, maxY, boxEdgeLength );
			List<Point> tmpRes = null;
			firstFor:
			for( int jj=0; jj<xIndexTo; jj++ )
				for( int ii=0; ii<yIndexTo; ii++ )
				{
					Rectangle rect = getRectangleForBoard( minX, minY, jj, ii, boxEdgeLength, halfEdge, rectLength );
					tmpRes = getSelection( maxSet, rect );
					VertexSetInfo vertexInfo = null;
					if( ( vertexInfo = isSetAcceptable( tmpRes, boxEdgeLength ) ) != null )
					{
						Set<Point> resultSet = new HashSet<>();
						resultSet.addAll(tmpRes);

						result = new Pair<>( resultSet, vertexInfo );
						break firstFor;
					}
				}
		}

		return( result );
	}

	protected Rectangle getRectangleForBoard( int minX, int minY, int xInd, int yInd,
						int boxEdgeLength, int halfEdge, int rectLength )
	{
		Rectangle result = new Rectangle( minX - halfEdge + xInd * boxEdgeLength,
											minY - halfEdge + yInd * boxEdgeLength,
											rectLength, rectLength );
		return( result );
	}

	protected List<Point> getSelection( Set<Point> set, Rectangle rect )
	{
		return( set.stream().filter( pt -> rect.contains(pt) ).collect( Collectors.toList() ) );
	}

	protected VertexSetInfo isSetAcceptable( List<Point> list, int boxEdgeLength )
	{
		VertexSetInfo result = createVertexInfo( list, boxEdgeLength );

		if( !isAcceptable( result ) )
			result = null;

		return( result );
	}

	protected boolean isAcceptable( VertexSetInfo vertexInfo )
	{
		boolean result = false;

		if( vertexInfo != null )
		{
			result = ( vertexInfo.getxElems() >= MINIMUM_NUMBER_OF_DIFFERENT_ELEMENTS_IN_EVERY_DIRECTION ) &&
					 ( vertexInfo.getyElems() >= MINIMUM_NUMBER_OF_DIFFERENT_ELEMENTS_IN_EVERY_DIRECTION ) &&
					 ( vertexInfo.getNumVertex() >= MINIMUM_NUMBER_OF_VERTEX_IN_CHESS_BOARD_SET );
		}

		return( result );
	}

	protected VertexSetInfo createVertexInfo( List<Point> list, int boxEdgeLength )
	{
		VertexSetInfo result = null;
		if( ( list != null ) && !list.isEmpty() )
		{
			int minX = getMin( list, pt -> pt.x );
			int minY = getMin( list, pt -> pt.y );
			int maxX = getMax( list, pt -> pt.x );
			int maxY = getMax( list, pt -> pt.y );
			int xElems = getIndexDiff( minX, maxX, boxEdgeLength ) + 1;
			int yElems = getIndexDiff( minY, maxY, boxEdgeLength ) + 1;

			result = new VertexSetInfo( minX, maxX, minY, maxY, boxEdgeLength,
										list.size(), xElems, yElems );
		}

		return( result );
	}

	protected int getIndexDiff( int minCoo, int maxCoo, int boxEdgeLength )
	{
		int indexDiff = (int) Math.round( ( (double) ( maxCoo - minCoo ) / boxEdgeLength ) );
		return( indexDiff );
	}

	protected int getIndexTo( int minCoo, int maxCoo, int boxEdgeLength )
	{
		int indexDiff = getIndexDiff( minCoo, maxCoo, boxEdgeLength );
		int result = IntegerFunctions.max( 1, indexDiff - ChessBoardGridResult.NUM_ROWS + 1 );

		return( result );
	}

	protected Integer getMin( Collection<Point> col, Function<Point, Integer> getter )
	{
		return( getLimit( col, getter, (i1, i2) -> i1 - i2 ) );
	}

	protected Integer getMax( Collection<Point> col, Function<Point, Integer> getter )
	{
		return( getLimit( col, getter, (i1, i2) -> i2 - i1 ) );
	}

	protected Integer getLimit( Collection<Point> col, Function<Point, Integer> getter,
								Comparator<Integer> comp )
	{
		Integer result = null;
		Optional<Integer> opt = col.stream().map( getter ).min( comp );
		if( opt.isPresent() )
			result = opt.get();

		return( result );
	}

	protected Map<Integer, List<Set<Point>>> calculateBoxEdgeLength( Set<Point> allPoints,
																	List< Map<Point, Long> > rows,
																	List< Map<Point, Long> > cols )
	{
		if( _candidatesToEdgeLength == null )
		{
			_candidatesToEdgeLength = new HashMap<>();

			rows.stream().map( (m) -> m.keySet() )
				.forEach( (s) -> addCandidates(s, _candidatesToEdgeLength ) );

			cols.stream().map( (m) -> m.keySet() )
				.forEach( (s) -> addCandidates(s, _candidatesToEdgeLength ) );
		}

		Map<Integer, List<Set<Point>>> result =
			classifyCandidatesToBoxEdgeLength( allPoints, _candidatesToEdgeLength, rows, cols );

		removeDiscardedEdgeLenghtCandidates( result, _candidatesToEdgeLength );

		return( result );
	}

	protected void removeDiscardedEdgeLenghtCandidates( Map<Integer, List<Set<Point>>> classifiedCandidates,
														Map< Integer, AtomicInteger > candidatesToEdgeLength )
	{
		List<Integer> discardedList = new ArrayList<>();
		for( Map.Entry<Integer, List<Set<Point>>> entry: classifiedCandidates.entrySet() )
			if( isDiscardedCandidateToEdgeLength( entry.getValue() ) )
				discardedList.add( entry.getKey() );

		for( Integer discardedEdgeLength: discardedList )
		{
			classifiedCandidates.remove( discardedEdgeLength );
			candidatesToEdgeLength.remove( discardedEdgeLength );
		}
	}

	protected boolean isDiscardedCandidateToEdgeLength( List<Set<Point>> list )
	{
		return( calculateNumMaxElems( list ) < MINIMUM_NUMBER_OF_VERTEX_IN_CHESS_BOARD_SET );
	}

	protected Map.Entry<Integer, List<Set<Point>>> getBestCandidateForBoxEdgeLength( Map<Integer, List<Set<Point>>> classifiedCandidates )
	{
		Map.Entry<Integer, List<Set<Point>>> result = null;

		int max = 0;
		for( Map.Entry<Integer, List<Set<Point>>> entry: classifiedCandidates.entrySet() )
		{
			int maxTmp = calculateNumMaxElems( entry.getValue() );
			if( maxTmp > max )
			{
				max = maxTmp;
				result = entry;
			}
		}

		return( result );
	}

	protected int calculateNumMaxElems( List<Set<Point>> list )
	{
		int result = -1;

		for( Set<Point> set: list )
			if( result < set.size() )
				result = set.size();

		result++;

		return( result );
	}

	protected Map<Integer, List<Set<Point>>> classifyCandidatesToBoxEdgeLength(
		Set<Point> allPoints, Map< Integer, AtomicInteger > candidates,
		List< Map<Point, Long> > rows, List< Map<Point, Long> > cols )
	{
		Map<Integer, List<Set<Point>>> result = new HashMap<>();

		for( Integer edgeLengthCandidate: candidates.keySet() )
			result.put(edgeLengthCandidate, calculateSetsOfVertex( edgeLengthCandidate, allPoints ) );

		return( result );
	}

	protected List<Set<Point>> calculateSetsOfVertex( int edgeLengthCandidate,
		Set<Point> allPoints )
	{
		List<Set<Point>> result = new ArrayList<>();

		for( Point point: allPoints )
			if( !contains( result, point ) )
				result.add( calculateCongruentSet( point, allPoints, edgeLengthCandidate ) );

		return( result );
	}

	protected Set<Point> calculateCongruentSet( Point point, Set<Point> allPoints,
												int edgeLength )
	{
		Set<Point> result = new HashSet<>();

		for( Point other: allPoints )
		{
			if( areCongruent( point, other, edgeLength ) )
				result.add(other);
		}

		return( result );
	}

	protected boolean areCongruent( Point pt1, Point pt2, int edgeLength )
	{
		return( areCongruent( pt1.x, pt2.x, edgeLength ) &&
				areCongruent( pt1.y, pt2.y, edgeLength ) );
	}

	protected boolean areCongruent( int v1, int v2, int modulus )
	{
		boolean result = false;
		if( modulus != 0 )
		{
			int mod = ( IntegerFunctions.abs( v1 - v2 ) % modulus );
			result = ( mod == 0 );//( mod <= 1 ) || ( mod == ( modulus - 1 ) );
		}

		return( result );
	}

	protected boolean contains( List<Set<Point>> list, Point point )
	{
		boolean result = false;
		result = list.stream().anyMatch( set -> set.contains( point ) );

		return( result );
	}

	protected void addCandidates( Set<Point> pointSet,
								Map< Integer, AtomicInteger > candidates )
	{
		Map< Integer, AtomicInteger > candidatesOfCandidates = new HashMap<>();

		for( Point point: pointSet )
			for( Point point2: pointSet )
			{
				if( point != point2 )
					addCandidate( getLongDistance( point, point2 ), candidatesOfCandidates );
			}

		candidatesOfCandidates = purgeCandidates( candidatesOfCandidates, pointSet );

		for( Map.Entry<Integer, AtomicInteger> entry: candidatesOfCandidates.entrySet() )
			addCandidate( entry, candidates );
	}

	protected int getLongDistance( Point point, Point point2 )
	{
		return( IntegerFunctions.max( IntegerFunctions.abs( point.x - point2.x ), IntegerFunctions.abs( point.y - point2.y ) ) );
	}

	protected Map< Integer, AtomicInteger > purgeCandidates( Map< Integer, AtomicInteger > candidatesOfCandidates,
																Set<Point> pointSet )
	{
		Map< Integer, AtomicInteger > result = new HashMap<>();

		candidatesOfCandidates.entrySet().stream()
			.filter( e -> countCompatibleGaps( e.getKey(), pointSet ) >= MINIMUM_NUMBER_OF_POINTS_ALLIGNED )
			.filter( e -> e.getKey() > MINIMUM_SIZE_FOR_BOX_EDGE_LENGTH )
			.forEach( e -> result.put( e.getKey(), e.getValue() ) );

		return( result );
	}

	protected int countCompatibleGaps( int candidateToEdgeLength, Set<Point> pointSet )
	{
		int result = 0;
		for( Point pt1: pointSet )
			for( Point pt2: pointSet )
				if( ( pt1 != pt2 ) && areCompatibleGaps( pt1, pt2, candidateToEdgeLength ) )
					result++;

		return( result );
	}

	protected boolean areCompatibleGaps( Point pt1, Point pt2, int candidateToEdgeLength )
	{
		int gap = getLongDistance(pt1, pt2);
		boolean result = ( gap % candidateToEdgeLength ) == 0;

		return( result );
	}

	protected void addCandidate( Map.Entry<Integer, AtomicInteger> entry,
								Map< Integer, AtomicInteger > candidates )
	{
		AtomicInteger ai = candidates.get( entry.getKey() );
		if( ai != null )
			ai.addAndGet( entry.getValue().get() );
		else
			candidates.put( entry.getKey(), entry.getValue() );
	}

	protected void addCandidate( int candidate, Map< Integer, AtomicInteger > candidates )
	{
		AtomicInteger ai = candidates.get( candidate );
		if( ai == null )
		{
			ai = new AtomicInteger(0);
			candidates.put( candidate, ai );
		}

		ai.incrementAndGet( );
	}

	protected List< Map< Point, Long > > getSortedRows( Map<Point, Long> inputMap )
	{
		return( filterPoints( inputMap, (pt) -> pt.y,
									(yy, pt) -> IntegerFunctions.abs( yy - pt.y ) == 0,
									( pt1, pt2 ) -> pt1.x - pt2.x ) );
	}

	protected List< Map< Point, Long > > getSortedColumns( Map<Point, Long> inputMap )
	{
		return( filterPoints( inputMap, (pt) -> pt.x,
									(xx, pt) -> IntegerFunctions.abs( xx - pt.x ) == 0,
									( pt1, pt2 ) -> pt1.y - pt2.y ) );
	}

	protected List< Map< Point, Long > > filterPoints( Map<Point, Long> inputMap,
				Function<Point, Integer> domainFunction,
				BiFunction<Integer, Point, Boolean> filter,	Comparator<Point> sorter )
	{
		List< Map< Point, Long > > result = new ArrayList<>();

		Set<Integer> domain = inputMap.keySet().stream().map(domainFunction)
			.sorted()
			.collect( Collectors.toSet() );

		for( Integer coordinateValue: domain )
		{
			Map< Point, Long > map = new HashMap<>();
			Integer coordinateValueFinal = coordinateValue;
			inputMap.keySet().stream().filter( (pt) -> filter.apply(coordinateValueFinal, pt ) )
				.sorted( sorter )
				.forEach( (pt) -> map.put(pt, inputMap.get(pt) ) );

			if( map.size() >= MINIMUM_NUMBER_OF_POINTS_ALLIGNED )
				result.add( map );
		}

		return( result );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_GRID_SHOULD_NOT_BE_NULL, "grid should not be null" );
	}

	public static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}
}
