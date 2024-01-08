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
package com.frojasg1.chesspdfbrowser.recognizer.chessboard.result;

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.copyable.Invariant;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardGridResult implements Invariant
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessBoardGridResult.properties";

	protected static final String CONF_COULD_NOT_FILE_COORDINATE = "COULD_NOT_FILE_COORDINATE";
	protected static final String CONF_COULD_NOT_VALIDATE_EDGE_LENGTH = "COULD_NOT_VALIDATE_EDGE_LENGTH";
	protected static final String CONF_X_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK = "X_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK";
	protected static final String CONF_Y_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK = "Y_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK";

	public static final int NUM_ROWS = ChessGamePositionBase.NUM_OF_ROWS;
	public static final int NUM_ELEMENTS = NUM_ROWS + 1;

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;


	protected Rectangle _boardBounds = null;
	protected Rectangle[][] _boxBounds = null;

	protected Point[][] _vertex = null;
	protected List<Point> _listOfVertex = null;

	protected List<Integer>[] _xCoorList = null;
	protected List<Integer>[] _yCoorList = null;

	protected int _edgeLength = 1;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public ChessBoardGridResult()
	{
	}

	public void init( int edgeLength )
	{
		_edgeLength = edgeLength;
		_boxBounds = new Rectangle[NUM_ELEMENTS][NUM_ELEMENTS];
		_vertex = new Point[NUM_ELEMENTS][NUM_ELEMENTS];
		_listOfVertex = new ArrayList<>();

		_xCoorList = createListArray();
		_yCoorList = createListArray();
	}

	protected List<Integer>[] createListArray()
	{
		List<Integer>[] result = ArrayFunctions.instance().createArrayWithLength(NUM_ELEMENTS, (List<Integer>) null);

		for( int ii=0; ii<result.length; ii++ )
			result[ii] = new ArrayList<>();

		return( result );
	}

	public int getEdgeLength() {
		return _edgeLength;
	}

	protected Rectangle calculateBoardBounds()
	{
		Point sw = getLeftBottomVertex();
		Point ne = getRightTopVertex();

		return( new Rectangle( sw.x, ne.y, ne.x - sw.x, sw.y - ne.y ) );
	}

	public Rectangle getBoardBoundsInsideImage()
	{
		if( _boardBounds == null )
			_boardBounds = calculateBoardBounds();

		return( _boardBounds );
	}

	public List<Point> getListOfVertex() {
		return _listOfVertex;
	}

	public Rectangle getBoxBoundsInsideImage(int colNum, int rowNum)
	{
		if( _boxBounds[colNum][rowNum] == null )
		{
			int left = getXCoor( colNum - 1, rowNum - 1 );
			int right = getXCoor( colNum, rowNum );
			int top = getYCoor( colNum - 1, rowNum );
			int bottom = getYCoor( colNum, rowNum - 1 );

			_boxBounds[colNum][rowNum] = new Rectangle( left, top, right - left, bottom - top );
		}

		return( _boxBounds[colNum][rowNum] );
	}

	protected int getXCoor( int colIndex, int rowIndex )
	{
		int result = -1;
		Point point = _vertex[colIndex][rowIndex];
		if( point != null )
			result = point.x;
		else
			result = (int) ( getLeftBottomVertex().x +
							( ( getRightTopVertex().x - getLeftBottomVertex().x ) * colIndex ) / (double ) NUM_ROWS );

		return( result );
	}

	protected int getYCoor( int colIndex, int rowIndex )
	{
		int result = -1;
		Point point = _vertex[colIndex][rowIndex];
		if( point != null )
			result = point.y;
		else
			result = (int) ( getRightTopVertex().y +
							( ( getLeftBottomVertex().y - getRightTopVertex().y ) * ( NUM_ROWS - rowIndex ) ) / (double ) NUM_ROWS );

		return( result );
	}

	public void addXcoor( int index, int xx )
	{
		_xCoorList[index].add( xx );
	}

	public void addYcoor( int index, int xx )
	{
		_yCoorList[index].add( xx );
	}

	public void calculateVertex()
	{
		for( int jj=0; jj<NUM_ELEMENTS; jj++ )
			for( int ii=0; ii<NUM_ELEMENTS; ii++ )
			{
				Integer xx = calculatePredominantCoordinate( _xCoorList, jj );
				Integer yy = calculatePredominantCoordinate( _yCoorList, ii );

				if( (xx == null) || (yy == null ) )
					throw( new RuntimeException( getInternationalString( CONF_COULD_NOT_FILE_COORDINATE ) ) );

				addVertex( new Point( xx, yy ), jj, ii );
			}

		checkEdgeLenghts();
	}

	protected int sortByFrequency( List<Integer> list, Integer i1, Integer i2 )
	{
		int result = count( list, i2 ) - count( list, i1 );

		if( result == 0 )
			result = i1 - i2;

		return( result );
	}

	protected Integer calculatePredominantCoordinate( List<Integer>[] coorListArr, int index )
	{
		Integer result = null;

		List<Integer> list = coorListArr[index];
		Optional<Integer> opt = list.stream().sorted( (i1, i2) -> sortByFrequency( list, i1, i2 ) )
			.findFirst();

		if( opt.isPresent() )
			result = opt.get();

		return( result );
	}

	protected void checkEdgeLenghts()
	{
		int minWidth = Integer.MAX_VALUE;
		int maxWidth = 0;
		int minHeight = Integer.MAX_VALUE;
		int maxHeight = 0;
		for( int jj=1; jj<NUM_ELEMENTS; jj++ )
			for( int ii=1; ii<NUM_ELEMENTS; ii++ )
			{
				Rectangle rectHeight = this.getBoxBoundsInsideImage(jj, ii);
				Rectangle rectWidth = this.getBoxBoundsInsideImage(ii, jj);

				minWidth = IntegerFunctions.min( minWidth, rectWidth.width );
				maxWidth = IntegerFunctions.max( maxWidth, rectWidth.width );
				minHeight = IntegerFunctions.min( minHeight, rectHeight.height );
				maxHeight = IntegerFunctions.max( maxHeight, rectHeight.height );
			}
	}

	protected void checkToleranceUnit( int min, int max )
	{
		if( abs( min - max ) > 1 )
			throw( new RuntimeException(
				createCustomInternationalString( CONF_COULD_NOT_VALIDATE_EDGE_LENGTH, min, max ) ) );
	}
/*
	protected void checkEdgeLength( int size, int edgeLength )
	{
		if( IntegerFunctions.abs( size - edgeLength ) > 1 )
			throw( new RuntimeException(
				String.format( "Cannot validate Edgelength. expected: %d, found: %d", edgeLength, size ) ) );
	}
*/
	protected int count( List<Integer> list, Integer ii )
	{
		int result = 0;

		if( ii != null )
		{
			for( Integer ii2: list )
				if( ii.equals( ii2 ) )
					result++;
		}

		return( result );
	}

	public void addVertex( Point point, int colIndex, int rowIndex )
	{
		checkCol( colIndex, point.x );
		checkRow( rowIndex, point.y );

		_vertex[colIndex][rowIndex] = point;

		_listOfVertex.add( point );
	}

	protected void checkCol( int colIndex, int xx )
	{
		for( int ii=0; ii<NUM_ELEMENTS; ii++ )
		{
			Point point = _vertex[colIndex][ii];
			if( point != null )
			{
				if( point.x != xx )
					throw( new RuntimeException( createCustomInternationalString( CONF_X_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK, xx, point.x ) ) );
			}
		}
	}

	protected void checkRow( int rowIndex, int yy )
	{
		for( int jj=0; jj<NUM_ELEMENTS; jj++ )
		{
			Point point = _vertex[jj][rowIndex];
			if( point != null )
			{
				if( point.y != yy )
					throw( new RuntimeException( createCustomInternationalString( CONF_Y_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK, yy, point.y ) ) );
			}
		}
	}

	protected Point getRightTopVertex()
	{
		if( _vertex[NUM_ROWS][NUM_ROWS] == null )
			_vertex[NUM_ROWS][NUM_ROWS] = calculateDiagonalElem( NUM_ROWS, -1 );

		return( _vertex[NUM_ROWS][NUM_ROWS] );
	}

	protected Point getLeftBottomVertex()
	{
		if( _vertex[0][0] == null )
			_vertex[0][0] = calculateDiagonalElem( 0, 1 );

		return( _vertex[0][0] );
	}

	protected Point calculateDiagonalElem( int index, int indexDelta )
	{
		return( new Point( calculateX( index, indexDelta ), calculateY( index, indexDelta ) ) );
	}

	protected int calculateX( int index, int indexDelta )
	{
		return( calculateCoor( index, indexDelta, (i) -> getX(i) ) );
	}

	protected int calculateY( int index, int indexDelta )
	{
		return( calculateCoor( index, indexDelta, (i) -> getY(i) ) );
	}

	protected Integer[] getCoordinates( int index, int delta,
										Function<Integer, Integer> getterFromRange, int numCoor )
	{
		Integer[] result = new Integer[NUM_ELEMENTS];

		int deltaSgn = sgn(delta);
		delta = abs(delta);
		int to = (deltaSgn>0) ? (NUM_ELEMENTS-index) : index;
		int count = 0;
		for( int ii = 0; (ii<to) && ( count < numCoor ); ii+=delta )
		{
			result[ii] = getterFromRange.apply( index + deltaSgn*ii );
			if( result[ii] != null )
				count++;
		}

		return( result );
	}

	protected Integer getX( int index )
	{
		Integer result = null;
		for( int ii=0; (ii<NUM_ELEMENTS) && ( result == null ); ii++ )
		{
			Point point = _vertex[index][ii];
			if( point != null )
				result = point.x;
		}
		return( result );
	}

	protected Integer getY( int index )
	{
		Integer result = null;
		for( int jj=0; (jj<NUM_ELEMENTS) && ( result == null ); jj++ )
		{
			Point point = _vertex[jj][index];
			if( point != null )
				result = point.y;
		}
		return( result );
	}

	protected int calculateCoor( int index, int delta,
								Function<Integer, Integer> getterFromRange )
	{
		int result = -1;
		Integer[] twoCoordinates = getCoordinates( index, delta, getterFromRange, 2 );
		int index1 = getNotNullIndex( twoCoordinates, 0 );
		int index2 = getNotNullIndex( twoCoordinates, index1 + 1 );
		if( index1 == 0 )
			result = twoCoordinates[index1];
		else
			result = (int) ( twoCoordinates[index1] +
								( ( double ) (twoCoordinates[index2] - twoCoordinates[index1]) ) /
								( index1 - index2 ) * index1 );

		return( result );
	}

	protected int getNotNullIndex( Integer[] arr, int index )
	{
		int result = -1;
		for( int ii=index; (ii<arr.length) & ( result == -1 ); ii++ )
			if( arr[ii] != null )
				result = ii;

		return( result );
	}

	protected int sgn( int value )
	{
		return( value == 0 ? 0 : value/abs(value) );
	}

	protected int abs( int value )
	{
		return( value > 0 ? value : -value );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_COULD_NOT_FILE_COORDINATE, "Could not find coordinate, xx or yy is null" );
		registerInternationalString(CONF_COULD_NOT_VALIDATE_EDGE_LENGTH, "Could not validate edgeLength. Max expected diff = 1, values found: [$1, $1]" );
		registerInternationalString(CONF_X_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK, "x coordinate micro sync for board box does not work:   expected: $1, found: $2" );
		registerInternationalString(CONF_Y_COORDINATE_MICRO_SYNC_FOR_BOARD_BOX_DOES_NOT_WORK, "y coordinate micro sync for board box does not work:   expected: $1, found: $2" );
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
