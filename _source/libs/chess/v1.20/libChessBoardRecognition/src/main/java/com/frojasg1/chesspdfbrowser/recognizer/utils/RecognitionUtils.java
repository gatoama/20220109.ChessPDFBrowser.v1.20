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
package com.frojasg1.chesspdfbrowser.recognizer.utils;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.position.impl.ChessGamePositionImpl;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.correlator.CorrelationResult;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.impl.ChessBoardPositionRecognizerWithStoreImpl;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ComponentsStats;
import com.frojasg1.chesspdfbrowser.recognizer.trainer.impl.BoardBoxResult;
import com.frojasg1.chesspdfbrowser.recognizer.trainer.impl.BoardBoxSyncResult;
import com.frojasg1.chesspdfbrowser.recognizer.trainer.impl.BoardGridSyncResult;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.desktop.image.pixel.impl.PixelStats;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.structures.Pair;
import com.frojasg1.libpdf.api.ImageWrapper;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RecognitionUtils implements InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "RecognitionUtils.properties";

	protected static final String CONF_EFECTIVE_EDGE_LENGTH_DOES_NOT_MATCH = "EFECTIVE_EDGE_LENGTH_DOES_NOT_MATCH";

	protected static final int NUM_OF_COLUMNS = ChessGamePositionImpl.NUM_OF_COLUMNS;
	protected static final int NUM_OF_ROWS = ChessGamePositionImpl.NUM_OF_ROWS;

	public static final double PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES_SHAPE = 0.90;
	protected static final double PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES = 0.85;
	protected static final double PROPORTION_FOR_SUMMARIZED_CORRELATION_MATCH_WITH_PIECES = 0.93;
	protected static final double PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES_RELAXED = 0.70;
	protected static final double PROPORTION_FOR_CORRELATION_MATCH_WITH_EMPTY_SQUARE = 0.95;

	protected static final long TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_AVERAGE_TOLERANCE = 13;

	protected static final long TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_COMPONENT = 33;
	protected static final long TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_ERROR_TOLERANCE = 7;
	protected static final long MIN_TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL = 7;
	protected static final long MIN_TOLERANCE_FOR_EMPTY_SQUARE_LUMINANCE_AVERAGE = 7;
	protected static final double MAX_PROPORTION_FOR_BACKGROUND_AVERAGE_CHECK_OF_SYNCHRONIZATION_BOARD_BOXES = 0.85;
	protected static final double MAX_PROPORTION_FOR_BACKGROUND_PIXEL_COMP_BACKGROUND_IMAGE_SUBTRACTION = 0.70;
	protected static final int SIGNED_GREY_SCALE_TO_DETECT_WHITE = 100;
	public static final double MAX_MEAN_ERROR_FOR_SUMMARIZED_IMAGE_MATCH = 14D;

	protected static final long MAX_NUMBER_OF_PIXELS_IN_BLOCK_TO_MAKE_BACKGROUND = 9;

	protected static RecognitionUtils _instance = null;

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected ChessGamePositionImpl _chessGamePositionImpl = new ChessGamePositionImpl();

	protected static final Pattern PATTERN_TO_GET_WB_COMBINATION_FROM_FIGURE_PATTERN = Pattern.compile( "^.*([WB])(_\\d+)?$" );
	protected static final Pattern PATTERN_TO_GET_PATTERN_TYPE_FROM_PATTERN_NAME = Pattern.compile( "^(.*)(_\\d+)$" );

	public static RecognitionUtils instance()
	{
		if( _instance == null )
			_instance = new RecognitionUtils();

		return( _instance );
	}

	public RecognitionUtils()
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public String getWbCombination( String patName )
	{
		String patWbComb = PATTERN_TO_GET_WB_COMBINATION_FROM_FIGURE_PATTERN.matcher( patName ).replaceFirst( "$1" );
		if( patWbComb.length() != 1 )
			throw( new RuntimeException( "Error getting wb combination from pattern name: " + patWbComb ) );

		return( patWbComb );
	}

	public String getPatternType( String patternName )
	{
		String result = null;
		if( patternName != null )
			result = PATTERN_TO_GET_PATTERN_TYPE_FROM_PATTERN_NAME.matcher(patternName).replaceFirst( "$1" );

		return( result );
	}

	public long getSquareError( PixelComponents p1, ComponentsStats css )
	{
		long dr = p1.getRed() - css.getComponentStats(ComponentsStats.RED).getAverage();
		long dg = p1.getGreen() - css.getComponentStats(ComponentsStats.GREEN).getAverage();
		long db = p1.getBlue() - css.getComponentStats(ComponentsStats.BLUE).getAverage();

		return( dr * dr + dg * dg + db * db );
	}

	public long getSquareError( PixelComponents p1, PixelComponents p2 )
	{
		long dr = p1.getRed() - p2.getRed();
		long dg = p1.getGreen() - p2.getGreen();
		long db = p1.getBlue() - p2.getBlue();

		return( dr * dr + dg * dg + db * db );
	}

	public CorrelationResult getCorrelationResultForSummarizedPattern( ChessFigurePattern pattern1,
							ChessFigurePattern pattern2, int borderToSkipThick )
	{
		return( getCorrelationResultForSummarizedPattern( pattern1.getSummarizedPixels(), pattern2,
															borderToSkipThick ) );
	}

	public CorrelationResult getCorrelationResultForSummarizedPattern( PixelComponents[][] blurredSummarizedImagePixels,
							ChessFigurePattern pattern, int borderToSkipThick )
	{
		CorrelationResult result = new CorrelationResult();
		result.setPattern(pattern);
		result.setIsSummary(true);

		int x1 = borderToSkipThick;
		int x2 = borderToSkipThick;
		int xlen = blurredSummarizedImagePixels.length - 2 * borderToSkipThick;
		int y1 = borderToSkipThick;
		int y2 = borderToSkipThick;
		int ylen = blurredSummarizedImagePixels[0].length - 2 * borderToSkipThick;

		PixelComponents[][] blurredImagePixels = blurredSummarizedImagePixels;
		PixelComponents[][] patternPixels = pattern.getSummarizedPixels();

		for( int jj=0; jj<xlen; jj++ )
			for( int ii=0; ii<ylen; ii++ )
			{
				PixelComponents patternPixel = patternPixels[x2+jj][y2+ii];
				PixelComponents imagePixel = blurredImagePixels[x1+jj][y1+ii];

				if( patternPixel.getAlpha() == Byte.MAX_VALUE ) // opaque
				{
					result.addSquareError( getSquareError( patternPixel, imagePixel ) );
				}
			}

		result.setNumberOfSamples( xlen * ylen );

		return( result );
	}

	protected BoardBoxSyncResult calculateBoardBoxSync( PixelComponents[][] imagePixels,
														ChessBoardGridResult grid,
														int col, int row,
														Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		final int MAX_DELTA_FOR_SYNC = 11;

		BoardBoxSyncResult result = null;

		Rectangle rect = getBoardBoxBounds( grid, col, row );

		CalculateDelta calculateDeltaFunction = null;
//		boolean fromInside = false;
		int direction = 1;
		if( isWhiteSquare( col, row ) )
		{
//			fromInside = true;
//			direction = -1;
			calculateDeltaFunction = ( boundaryAverages, direc, cssPair ) ->
										calculateDeltaForWhite( boundaryAverages, direc, cssPair );
		}
		else
		{
//			fromInside = false;
//			direction = 1;
			calculateDeltaFunction = ( boundaryAverages, direc, cssPair ) ->
										calculateDeltaForBlack( boundaryAverages, direc, cssPair );
		}

		PixelComponents[] westBoundaryAverages = calculateXBorderAverages( rect.x, rect.y, rect.height, imagePixels, MAX_DELTA_FOR_SYNC );
		PixelComponents[] eastBoundaryAverages = calculateXBorderAverages( rect.x + rect.width - 1, rect.y, rect.height, imagePixels, MAX_DELTA_FOR_SYNC );
		PixelComponents[] northBoundaryAverages = calculateYBorderAverages( rect.x, rect.y, rect.width, imagePixels, MAX_DELTA_FOR_SYNC );
		PixelComponents[] southBoundaryAverages = calculateYBorderAverages( rect.x, rect.y + rect.height - 1, rect.width, imagePixels, MAX_DELTA_FOR_SYNC );

		Integer westDelta = calculateDeltaFunction.calculateDelta( westBoundaryAverages, -direction, componentStatsForEmptySquares );
		Integer eastDelta = calculateDeltaFunction.calculateDelta( eastBoundaryAverages, direction, componentStatsForEmptySquares );
		Integer northDelta = calculateDeltaFunction.calculateDelta( northBoundaryAverages, -direction, componentStatsForEmptySquares );
		Integer southDelta = calculateDeltaFunction.calculateDelta( southBoundaryAverages, direction, componentStatsForEmptySquares );

		if( ( westDelta != null ) && ( eastDelta != null ) &&
			( northDelta != null ) && ( southDelta != null ) )
		{
/*
			if( !fromInside )	// from white to dark
			{
				westDelta += 1;
				eastDelta -= 1;
				northDelta += 1;
				southDelta -= 1;
			}
*/
			Dimension effectiveEdgeDimension = new Dimension( rect.width - westDelta + eastDelta,
																rect.height - northDelta + southDelta );

			result = new BoardBoxSyncResult( westDelta, northDelta, effectiveEdgeDimension,
											imagePixels, componentStatsForEmptySquares );
		}

		return( result );
	}

	public BoardGridSyncResult calculateSyncGrid( PixelComponents[][] imagePixels,
													ChessBoardGridResult grid,
													ChessFigurePatternSet ps,
													ChessGamePositionImpl pretendedPosition,
													BufferedImage blurredImage,
													boolean updateEmptySquareAverage )
	{
		Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares = ps.getEmptySquaresComponentsStats();

		BoardGridSyncResult result = new BoardGridSyncResult();

		ChessBoardGridResult gridResult = new ChessBoardGridResult();
		gridResult.init( grid.getEdgeLength() );
		result.setEmptySquareComponentStats(componentStatsForEmptySquares);

//		BoardBoxSyncResult result = null;

//		boolean signedComponents = true;

//		File file =  new File( "J:\\image.tiff" );
//		ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(image.getImage().getImage(), "TIFF", file) );
////		PixelComponents[][] imagePixels = ImageFunctions.instance().getPixelComponents(image.getImage().getImage(), signedComponents);

		int dx = 0;
		int dy = 0;
//		Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares = calculateLuminanceForEmptySquares( imagePixels, grid, pretendedPosition, dx, dy );

		Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquaresCurr = componentStatsForEmptySquares;
		if( ( componentStatsForEmptySquaresCurr == null ) && ( pretendedPosition != null ) )
			componentStatsForEmptySquaresCurr = calculateLuminanceForEmptySquares( imagePixels, grid,
											componentStatsForEmptySquares, pretendedPosition, dx, dy );

		break1:
		for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
			{
//				boolean expectedSquareIsEmpty = pretendedPosition.getPieceAtPosition(jj, ii) == null;
				BoardBoxSyncResult tmp = null;
//				if( isWhiteBoardBox(jj, ii) ) //&& expectedSquareIsEmpty )
				{
					tmp = calculateBoardBoxSync( imagePixels, grid, jj, ii, componentStatsForEmptySquaresCurr );

					if( squareIsEmpty( pretendedPosition, imagePixels, grid,
										componentStatsForEmptySquaresCurr, jj, ii, tmp,
										blurredImage, ps ) ) //&& ( componentStatsForEmptySquares == null ) )
					{
						if( tmp != null )
						{
							dx = tmp.getDeltaX();
							dy = tmp.getDeltaY();

							try
							{
								if( updateEmptySquareAverage )
								{
									ComponentsStats newEmptySquareCompStats = calculateComponentStats( imagePixels, grid, jj, ii, dx, dy );

									if( this.isWhiteBoardBox(jj, ii) )
										componentStatsForEmptySquaresCurr.setKey( newEmptySquareCompStats );
									else
										componentStatsForEmptySquaresCurr.setValue( newEmptySquareCompStats );
								}

								addVertexToSyncGrid( grid, tmp, jj, ii, gridResult );
							}
							catch( Exception ex )
							{
								System.out.println( "Error calculating average of empty board box, skipping" );
							}

/*
							Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares2 = calculateLuminanceForEmptySquares( imagePixels, grid,
																	componentStatsForEmptySquaresCurr, pretendedPosition, dx, dy );
							if( componentStatsForEmptySquares2 != null )
							{
								updateComponentStatsForEmptySquares( tmp, result, componentStatsForEmptySquares2 );
								tmp.setComponentsStatsForEmptySquares(componentStatsForEmptySquares2);
								result.setEmptySquareComponentStats(componentStatsForEmptySquares2);
								componentStatsForEmptySquaresCurr = componentStatsForEmptySquares2;
							}
*/
						}
					}

//					if( tmp == null )
//						throw( new RuntimeException( "Could not find accurate sync of board box square" ) );
//					result = syncMatches( result, tmp );
//					if( result == null )
//						throw( new RuntimeException( "Could not find accurate sync of board box square" ) );
/*
				if( tmp != null )
				{
					dx = tmp.getDeltaX();
					dy = tmp.getDeltaY();
					Pair<Long, Long> luminanceForEmptySquares2 = calculateLuminanceForEmptySquares( imagePixels, grid, pretendedPosition, dx, dy );
					tmp.setComponentsStatsForEmptySquares(luminanceForEmptySquares2);

					if( checkAllBoardBoxes( grid, tmp ) )
					{
						result = tmp;
						break break1;
					}
				}
*/
				}
			}

//		if( result == null )
//			throw( new RuntimeException( "Could not synchronize board boxes. Exiting training." ) );

		gridResult.calculateVertex(); // throws exception
		result.setGrid( gridResult );

		// if execution reaches this point, then it has been checked that it must be a correct BoardGridSyncResult
		// then we update componentStats of result, that probably can be the same as the ChessFigurePatternSet
		if( updateEmptySquareAverage )
			updateComponentStatsForEmptySquares( null, result, componentStatsForEmptySquaresCurr );

		return( result );
	}

	protected void updateComponentStatsForEmptySquares( BoardBoxSyncResult bbSyncResult,
						BoardGridSyncResult bgridSyncResult,
						Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares2 )
	{
		if( componentStatsForEmptySquares2 != null )
		{
			if( bbSyncResult != null )
			{
				if( bbSyncResult.getComponentsStatsForEmptySquares() == null )
					bbSyncResult.setComponentsStatsForEmptySquares(componentStatsForEmptySquares2);
				else
					update( bbSyncResult.getComponentsStatsForEmptySquares(), componentStatsForEmptySquares2 );
			}

			if( bgridSyncResult != null )
			{
				if( bgridSyncResult.getEmptySquareComponentStats() == null )
					bgridSyncResult.setEmptySquareComponentStats(componentStatsForEmptySquares2);
				else
					update( bgridSyncResult.getEmptySquareComponentStats(), componentStatsForEmptySquares2 );
			}
		}
	}

	protected void update( Pair<ComponentsStats, ComponentsStats> cssPair1,
							Pair<ComponentsStats, ComponentsStats> cssPair2 )
	{
		cssPair1.getKey().update( cssPair2.getKey() );
		cssPair1.getValue().update( cssPair2.getValue() );
	}


	protected void addVertexToSyncGrid( ChessBoardGridResult grid, BoardBoxSyncResult sync,
										int col, int row, ChessBoardGridResult result )
	{
		Rectangle boxBounds = grid.getBoxBoundsInsideImage(col, row);
		int left = boxBounds.x + sync.getDeltaX();
		int top = boxBounds.y + sync.getDeltaY();
		int right = left + sync.getEffectiveBoxEdgeLength().width;
		int bottom = top + sync.getEffectiveBoxEdgeLength().height;

//		checkEffectiveEdgeLength( grid, sync );

		result.addXcoor(col - 1, left);
		result.addXcoor(col, right);
		result.addYcoor(row, top);
		result.addYcoor(row - 1, bottom);
	}

	protected void checkEffectiveEdgeLength( ChessBoardGridResult grid, BoardBoxSyncResult sync )
	{
		checkEffectiveEdgeLength( grid.getEdgeLength(), sync.getEffectiveBoxEdgeLength() );
	}

	protected void checkEffectiveEdgeLength( int edgeLength, Dimension effectiveEdgeLength )
	{
		checkEffectiveEdgeLength( edgeLength, effectiveEdgeLength.width );
		checkEffectiveEdgeLength( edgeLength, effectiveEdgeLength.height );
	}

	protected void checkEffectiveEdgeLength( int edgeLength, int effectiveEdgeLength )
	{
		if( IntegerFunctions.abs( edgeLength - effectiveEdgeLength) > 1 )
			throw( new RuntimeException( getInternationalString( CONF_EFECTIVE_EDGE_LENGTH_DOES_NOT_MATCH ) ) );
	}
/*
	public BoardBoxSyncResult calculateBoardBoxSync( PixelComponents[][] imagePixels,
														ChessBoardGridResult grid,
														Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		List<BoardBoxSyncResult> list = new ArrayList<>();

		BoardBoxSyncResult result = null;

//		File file =  new File( "J:\\image.tiff" );
//		ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(image.getImage().getImage(), "TIFF", file) );
		boolean signedComponents = true;
//		PixelComponents[][] imagePixels = ImageFunctions.instance().getPixelComponents(image.getImage().getImage(), signedComponents);

		break1:
		for( int jj=2; jj<NUM_OF_COLUMNS; jj++ )
			for( int ii=2; ii<NUM_OF_ROWS; ii++ )
			{
				BoardBoxSyncResult tmp = null;
				if( isWhiteBoardBox(jj, ii) )
				{
					tmp = calculateBoardBoxSync( imagePixels, grid, jj, ii, componentStatsForEmptySquares );

					if( tmp != null )
					{
						list.add( tmp );
					}
				}
			}

		Optional<BoardBoxSyncResult> opt = list.stream()
			.map( oo -> new Pair<BoardBoxSyncResult, Integer>( oo, countMatches( list, oo ) ) )
			.sorted( ( p1, p2 ) ->  p2.getValue() - p1.getValue() )
			.map( pp -> pp.getKey() )
			.findFirst();

		if( opt.isPresent() )
			result = opt.get();

		return( result );
	}
*/
	protected int countMatches( List<BoardBoxSyncResult> list, BoardBoxSyncResult bbsr )
	{
		int result = 0;

		for( BoardBoxSyncResult bbsr2: list )
			if( syncMatches( bbsr2, bbsr ) != null )
				result++;

		return( result );
	}

	protected BoardBoxSyncResult syncMatches( BoardBoxSyncResult partialResult, BoardBoxSyncResult tmp )
	{
		BoardBoxSyncResult result = tmp;

		if( ( partialResult != null ) && ( tmp != null ) )
		{
			if( ( partialResult.getDeltaX() != tmp.getDeltaX() ) ||
				( partialResult.getDeltaY() != tmp.getDeltaY() ) ||
				( ! partialResult.getEffectiveBoxEdgeLength().equals( tmp.getEffectiveBoxEdgeLength() ) ) )
			{
				result = null;
			}
		}

		return( result );
	}

	protected Pair<ComponentsStats, ComponentsStats> calculateLuminanceForEmptySquares( PixelComponents[][] imagePixels,
																						ChessBoardGridResult grid,
																						Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares,
																						ChessGamePositionImpl pretendedPosition,
																						int deltaX,
																						int deltaY)
	{
		ComponentsStats whiteCompStats = null;
		ComponentsStats blackCompStats = null;

		break1:
		for( int jj=2; jj<NUM_OF_COLUMNS; jj++ )	// we avoid boundaries of chess board to avoid the frame
			for( int ii=2; ii<NUM_OF_ROWS; ii++ )	// we avoid boundaries of chess board to avoid the frame
			{
				boolean isEmpty = false;
				if( pretendedPosition != null )
					isEmpty = ( pretendedPosition.getPieceAtPosition(jj, ii) == null );
				else if( componentStatsForEmptySquares != null )
					isEmpty = ( squareIsEmpty( imagePixels, grid, componentStatsForEmptySquares, jj, ii, 0, 0 ) );
				

				if( isEmpty )
				{
					ComponentsStats compStats = calculateComponentStats( imagePixels, grid, jj, ii, deltaX, deltaY );
					if( isWhiteBoardBox(jj, ii) )
						whiteCompStats = compStats;
					else
						blackCompStats = compStats;
				}

				if( ( whiteCompStats != null ) && ( blackCompStats != null ) )
					break break1;
			}

		Pair<ComponentsStats, ComponentsStats> result = null;
		
		if( ( whiteCompStats != null ) && ( blackCompStats != null ) )
			result = new Pair<>( whiteCompStats, blackCompStats );

		return( result );
	}

	public ComponentsStats calculateComponentStats( PixelComponents[][] imagePixels,
														ChessBoardGridResult grid,
														int col, int row,
														int deltaX, int deltaY )
	{
		ComponentsStats result = new ComponentsStats();
		result.init();

		Rectangle rect = getBoardBoxBounds( grid, col, row );

		for( int compIndex=ComponentsStats.INITIAL_COMPONENT_INDEX; compIndex<ComponentsStats.TOTAL_ELEMENTS; compIndex++ )
			addComponentStats( imagePixels, rect, deltaX, deltaY, compIndex, result );

		return( result );
	}

	protected void addComponentStats( PixelComponents[][] imagePixels, Rectangle rect,
													int deltaX, int deltaY, int compIndex,
													ComponentsStats result )
	{
		long compAvgerage = calculateMeanCompOfSquare( imagePixels, rect, deltaX, deltaY, compIndex );
		long compStandardDeviation = calculateStandardDeviationOfCompOfSquare(imagePixels, rect, deltaX, deltaY,
																				compAvgerage, compIndex );
		result.setComponentStats(compAvgerage, compStandardDeviation, compIndex);
	}

	protected long calculateStandardDeviationOfCompOfSquare( PixelComponents[][] imagePixels, Rectangle rect,
													int deltaX, int deltaY, long average, int compIndex )
	{
		return( calculateStandardDeviationOfCompOfSquare( imagePixels, rect, deltaX, deltaY,
															pi -> (int) pi.getComponent(compIndex), average ) );
	}

	protected long calculateStandardDeviationOfCompOfSquare( PixelComponents[][] imagePixels, Rectangle rect,
																int deltaX, int deltaY,
																Function<PixelComponents, Integer> componentGetter,
																long average)
	{
		int xx = rect.x + deltaX;
		int yy = rect.y + deltaY;
		int width = rect.width;
		int height = rect.height;

		long diff = 0;
		long result = 0;
		for( int jj = 0; jj<width; jj++ )
			for( int ii = 0; ii<height; ii++ )
			{
				diff = abs( average - componentGetter.apply(imagePixels[xx+jj][yy+ii]) );
				result += diff * diff;
			}

		int length = width * height;
		if( length > 0 )
			result /= length;

		result = (long) Math.sqrt( (double) result );

		return( result );
	}

	protected long calculateMeanLuminanceOfSquare( PixelComponents[][] imagePixels,
													ChessBoardGridResult grid,
													int col, int row,
													int deltaX, int deltaY )
	{
		Rectangle rect = getBoardBoxBounds( grid, col, row );

		long result = calculateMeanCompOfSquare( imagePixels, rect, deltaX, deltaY, PixelComponents.LUMINANCE );

		return( result );
	}

	protected long calculateMeanCompOfSquare( PixelComponents[][] imagePixels, Rectangle rect,
													int deltaX, int deltaY, int compIndex )
	{
		return( calculateMeanCompOfSquare( imagePixels, rect, deltaX, deltaY, pi -> (int) pi.getComponent(compIndex) ) );
	}

	protected long calculateMeanCompOfSquare( PixelComponents[][] imagePixels, Rectangle rect,
													int deltaX, int deltaY,
													Function<PixelComponents, Integer> componentGetter )
	{
		int xx = rect.x + deltaX;
		int yy = rect.y + deltaY;
		int width = rect.width;
		int height = rect.height;

		long result = 0;
		for( int jj = 0; jj<width; jj++ )
			for( int ii = 0; ii<height; ii++ )
				result += componentGetter.apply(imagePixels[xx+jj][yy+ii]);

		int length = width * height;
		if( length > 0 )
			result /= length;

		return( result );
	}

	public Rectangle getBoardBoxBounds( ChessBoardGridResult grid, int col, int row )
	{
		return( grid.getBoxBoundsInsideImage(col, row) );
	}

	protected PixelComponents[] calculateXBorderAverages( int xx, int yy, int edgeLength, PixelComponents[][] imagePixels, int maxDelta )
	{
		PixelComponents[] result = new PixelComponents[maxDelta*2+1];

		int height = imagePixels[0].length;
		int width = imagePixels.length;

		int fromDelta = IntegerFunctions.max( -maxDelta, -xx  );
		int toDelta = IntegerFunctions.min( maxDelta, width - 1 - xx );

		for( int ii=fromDelta; ii<=toDelta; ii++ )
			result[ii+maxDelta] = calculateXborderAverage( xx+ii, yy, edgeLength, imagePixels );

		return( result );
	}

	protected PixelComponents[] calculateYBorderAverages( int xx, int yy, int edgeLength, PixelComponents[][] imagePixels, int maxDelta )
	{
		PixelComponents[] result = new PixelComponents[maxDelta*2+1];

		int height = imagePixels[0].length;
		int width = imagePixels.length;

		int fromDelta = IntegerFunctions.max( -maxDelta, -yy );
		int toDelta = IntegerFunctions.min( maxDelta, height - 1 - yy );

		for( int ii=fromDelta; ii<=toDelta; ii++ )
			result[ii+maxDelta] = calculateYborderAverage( xx, yy+ii, edgeLength, imagePixels );

		return( result );
	}

	public String getPatternCode( ChessFigurePattern pattern )
	{
		String result = null;
		if( pattern != null )
		{
			if( isFigurePattern(pattern) )
				result = pattern.getName().substring( 0, 3 );
			else
				result = pattern.getName().substring( 0, 4 );	// empty board box pattern code
		}

		return( result );
	}

	public String getPatternCode( BoardBoxResult bbr )
	{
		return( getPatternCodeGen( bbr, ()->bbr.getPiece(),
							()->bbr.isWhiteBoardBox(), ()->pieceColorLetter( bbr ),
							()->getSquareColorLetter( bbr ) ) );
	}

	public String getPatternCode( Character pieceCode, int col, int row )
	{
		return( getPatternCodeGen( 1, () -> pieceCode,
							()->isWhiteBoardBox(col,row), ()->pieceColorLetter( pieceCode ),
							()->getSquareColorLetter( col, row ) ) );
	}

	protected String getPatternCodeGen( Object obj, Supplier<Character> getPieceFunc,
										Supplier<Boolean> isWhiteBoxFunc,
										Supplier<String> getPieceColorLetterFunc,
										Supplier<String> getSquareColorLetterFunc )
	{
		String result = null;

		if( obj != null )
		{
			if( getPieceFunc.get() == null )
			{
				if( isWhiteBoxFunc.get() )
					result = ChessFigurePatternSet.EMPTY_WHITE_SQUARE_TYPE;
				else
					result = ChessFigurePatternSet.EMPTY_BLACK_SQUARE_TYPE;
			}
			else
				result = new StringBuilder().append( getPieceFunc.get() )
					.append( getPieceColorLetterFunc.get() )
					.append( getSquareColorLetterFunc.get() ).toString();
		}

		return( result );
	}

	protected String pieceColorLetter( BoardBoxResult bbr )
	{
		String result = null;
		if( bbr != null )
			result = pieceColorLetter( bbr.getPiece() );

		return( result );
	}

	protected String pieceColorLetter( Character pieceCode )
	{
		String result = null;
		if( pieceCode != null )
		{
			if( StringFunctions.instance().isUpperCase( pieceCode ) )
				result = "W";
			else
				result = "B";
		}

		return( result );
	}

	public boolean isWhiteBoardBox( BoardBoxResult bbr )
	{
		return( isWhiteBoardBox( bbr.getCol(), bbr.getRow() ) );
	}

	public boolean isWhiteBoardBox( int col, int row )
	{
		return( ( col + row ) % 2 == 1 );
	}

	public String getSquareColorLetter( int col, int row )
	{
		String result = null;

		if( isWhiteBoardBox(col, row) )
			result = "W";
		else
			result = "B";

		return( result );
	}

	public String getSquareColorLetter( BoardBoxResult bbr )
	{
		String result = null;
		if( bbr != null )
			result = getSquareColorLetter( bbr.getCol(), bbr.getRow() );

		return( result );
	}

	protected boolean isWhiteSquare( BoardBoxResult bbr )
	{
		boolean result = false;

		result = Objects.equals( "W", getSquareColorLetter( bbr ) );

		return( result );
	}

	public boolean isWhiteSquare( int col, int row )
	{
		boolean result = "W".equals( getSquareColorLetter( col, row ) );

		return( result );
	}

	protected Integer calculateDeltaForBlack( PixelComponents[] boundaryAverages, int direction,
										Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		Integer result = null;

		int length = boundaryAverages.length;
		int offset = (direction > 0) ? 0 : length - 1;
		int numBlacks = 0;
		for( int ii=0; ii<length; ii++ )
		{
			int index = offset + ii * direction;
			PixelComponents value = boundaryAverages[ index ];
			if( value != null )
			{
				if( isBlackBoardBox( value, componentStatsForEmptySquares ) )
					numBlacks++;
				else
				{
					if( numBlacks > 3 )
					{
						result = index - ( length - 1 ) / 2 - direction;
						break;
					}
					numBlacks = 0;
				}
			}
		}

		return( result );
	}

	protected Integer calculateDeltaForWhite( PixelComponents[] boundaryAverages, int direction,
										Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		Integer result = null;

		int length = boundaryAverages.length;
		int offset = (direction > 0) ? 0 : length - 1;
		int numWhites = 0;
		for( int ii=0; ii<length; ii++ )
		{
			int index = offset + ii * direction;
			PixelComponents value = boundaryAverages[ index ];
			if( value != null )
			{
				if( isWhiteBoardBox( value, componentStatsForEmptySquares ) )
					numWhites++;
				else
				{
					if( numWhites > 3 )
					{
						result = index - ( length - 1 ) / 2 - direction;
						break;
					}
					numWhites = 0;
				}
			}
		}

		return( result );
	}

	protected Long getAverageLuminance( ComponentsStats componentsStats )
	{
		return( getAverageComp( componentsStats, ComponentsStats.LUMINANCE ) );
	}

	protected Long getAverageComp( ComponentsStats componentsStats, int compIndex )
	{
		Long result = null;
		if( componentsStats != null )
			result = componentsStats.getComponentStats( compIndex ).getAverage();

		return( result );
	}
/*
	protected boolean isWhite( long avg, Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		return( abs( getAverageLuminance( componentStatsForEmptySquares.getKey() ) - avg ) <
				abs( getAverageLuminance( componentStatsForEmptySquares.getValue() ) - avg ) );
	}
*/
	protected boolean isWhiteBoardBox( PixelComponents avgPixel,
								Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		return( isFirst(avgPixel, componentStatsForEmptySquares.getKey(), componentStatsForEmptySquares.getValue() ) );
	}

	protected boolean isBlackBoardBox( PixelComponents avgPixel,
								Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares )
	{
		return( isFirst(avgPixel, componentStatsForEmptySquares.getValue(), componentStatsForEmptySquares.getKey() ) );
	}

	protected boolean isFirst( PixelComponents avgPixel, ComponentsStats css1, ComponentsStats css2 )
	{
		boolean result = true;
		for( int ii= ComponentsStats.RED; ii<ComponentsStats.TOTAL_ELEMENTS; ii++ )
			if( !isFirst( avgPixel.getComponent(ii), css1.getComponentStats(ii).getAverage(), css2.getComponentStats(ii).getAverage() ) )
			{
				result = false;
				break;
			}

		return( result );
	}

	protected boolean isFirst( int value, long first, long second )
	{
		long tolerance = (long) (Math.round( IntegerFunctions.abs( first - second ) * 2 / 3d ) );

		long error1 = IntegerFunctions.abs( value - first );
		long error2 = IntegerFunctions.abs( value - second );
		boolean result = ( (error1<=error2) && ( error1 <= tolerance ) );

		return( result );
	}

	public double getProportion( long v1, long v2 )
	{
		double result = 0D;

		long absDiff = abs( v1 - v2 );
		long maxAbs = max( abs(v1) , abs( v2 ) );

		if( maxAbs != 0 )
			result = 1.0D - ( (double) absDiff ) / (double) maxAbs;

		return( result );
	}

	protected long abs( long value )
	{
		return( ( value > 0 ) ? value : -value );
	}

	protected long max( long v1, long v2 )
	{
		return( ( v1 > v2 ) ? v1 : v2 );
	}

	protected PixelComponents calculateYborderAverage( int xx, int yy, int length, PixelComponents[][] imagePixels )
	{
		boolean signedComponents = true;
		PixelComponents result = new PixelComponents( 0x80808080, signedComponents );

		int quarter = length / 4;
		int from = quarter;
		int to = quarter + IntegerFunctions.min( length/2, imagePixels.length - xx + quarter );	// we only sum the central half of border
//		length = IntegerFunctions.min( length, imagePixels.length - xx );
		int count = 0;
		for( int ii = from; ii<to; ii++ )
		{
			count++;
			addComponents( result, imagePixels[xx+ii][yy] );
		}

		if( count > 0 )
			divideComponents( result, count );

		return( result );
	}

	protected long matchCompForForeground( PixelComponents pc1, PixelComponents pc2,
											ComponentsStats css, int compIndex )
	{
		long error = 0;
		long tolerance = calculateRelaxedTolerance( css.getComponentStats( compIndex ) );
		error = IntegerFunctions.max( 0, abs( pc1.getComponent(compIndex) - pc2.getComponent(compIndex) ) - tolerance );

		return( error );
	}

	protected boolean matchCompForBackground( PixelComponents pc, ComponentsStats css, int compIndex )
	{
		long tolerance = TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_COMPONENT;
		boolean result = ( abs( getAverageComp( css, compIndex ) - pc.getComponent(compIndex) ) <= tolerance );

		return( result );
	}
/*
	protected long matchCompForBackground( PixelComponents pc, ComponentsStats css, int compIndex )
	{
		long error = 0;
		long tolerance = calculateRelaxedTolerance( css.getComponentStats( compIndex ) );
		error = IntegerFunctions.max( 0, abs( getAverageComp( css, compIndex ) - pc.getComponent(compIndex) ) - tolerance );

		return( error );
	}
*/
	public boolean isBackground( PixelComponents pc, ComponentsStats cs )
	{
		boolean result = true;
//		long squaredError = 0;
		for( int compIndex = ComponentsStats.RED; compIndex<ComponentsStats.TOTAL_ELEMENTS; compIndex++ )
		{
//			long error = matchCompForBackground( pc, cs, compIndex );
//			squaredError += error * error;
			result = matchCompForBackground( pc, cs, compIndex );
			if( ! result )
				break;
		}

//		result = ( squaredError <= TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_ERROR_TOLERANCE * TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_ERROR_TOLERANCE );

		return( result );
	}

	public boolean pixelsMatch( PixelComponents pc1, PixelComponents pc2, ComponentsStats cs )
	{
		boolean result = true;
		long squaredError = 0;
		for( int compIndex = ComponentsStats.RED; compIndex<ComponentsStats.TOTAL_ELEMENTS; compIndex++ )
		{
			long error = matchCompForForeground( pc1, pc2, cs, compIndex );
			squaredError += error * error;
		}

		result = ( squaredError == 0 );//<= TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_ERROR_TOLERANCE * TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_ERROR_TOLERANCE );

		return( result );
	}

	public boolean isBackground( PixelComponents pc )
	{
		return( isBackground( pc.getBlue() ) && isBackground( pc.getGreen() ) && isBackground( pc.getRed() ) );
	}

	protected boolean isBackground( int componentValue )
	{
		return( IntegerFunctions.abs(componentValue) <=
				ChessBoardPositionRecognizerWithStoreImpl.TOLERANCE_FOR_GREY_SCALE_WITH_EMPTY_SQUARE );
	}

	public ChessFigurePattern getEmptySquarePattern( String squareColorLetter,
														ChessFigurePatternSet ps )
	{
		String patternType = StringFunctions.instance().buildStringFromRepeatedChar( squareColorLetter.charAt(0), 4 );

		ChessFigurePattern result = null;
		Optional<ChessFigurePattern> opt = ps.getListOfPatternsByType(patternType).stream().findFirst();

		if( opt.isPresent() )
			result = opt.get();

		return( result );
	}

	protected PixelComponents calculateXborderAverage( int xx, int yy, int length, PixelComponents[][] imagePixels )
	{
		boolean signedComponents = true;
		PixelComponents result = new PixelComponents(0x80808080, signedComponents );

		int quarter = length / 4;
		int from = quarter;
		int to = quarter + IntegerFunctions.min( length/2, imagePixels[0].length - yy + quarter );	// we only sum the central half of border
//		length = IntegerFunctions.min( length, imagePixels[0].length - yy );
		int count = 0;
		for( int ii = from; ii<to; ii++ )
		{
			count++;
			addComponents( result, imagePixels[xx][yy+ii] );
		}

		if( count > 0 )
			divideComponents( result, count );

		return( result );
	}

	protected void addComponents( PixelComponents result, PixelComponents summand )
	{
		for( int ii = PixelComponents.RED; ii <= PixelComponents.ALPHA; ii++ )
			result.setComponentWithoutLimit( ii, (short) ( result.getComponent(ii) + summand.getComponent(ii) ) );
	}

	protected void divideComponents( PixelComponents result, int length )
	{
		for( int ii = PixelComponents.RED; ii <= PixelComponents.ALPHA; ii++ )
			result.setComponent( ii, (short) ( Math.round( ( (double) result.getComponent(ii) /length ) ) ) );
	}

	protected long calculateStrictTolerance( ComponentsStats.ComponentStats componentStats )
	{
		long result = 0;
		if( componentStats != null )
			result = IntegerFunctions.limit(componentStats.getStandardDeviation() / 2,
									MIN_TOLERANCE_FOR_EMPTY_SQUARE_LUMINANCE_AVERAGE,
									(long) ( ( 1 - MAX_PROPORTION_FOR_BACKGROUND_AVERAGE_CHECK_OF_SYNCHRONIZATION_BOARD_BOXES ) * componentStats.getAverage() )
											 );

		return( result );
	}

	protected long calculateRelaxedTolerance( ComponentsStats.ComponentStats componentStats )
	{
		long result = 0;
		if( componentStats != null )
			result = IntegerFunctions.max( (int) ( componentStats.getStandardDeviation() * 1.5d ),
									MIN_TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL);
//			result = IntegerFunctions.limit(componentStats.getStandardDeviation() * 2,
//									MIN_TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL,
//									(long) ( ( 1 - MAX_PROPORTION_FOR_BACKGROUND_PIXEL_COMP_BACKGROUND_IMAGE_SUBTRACTION ) * componentStats.getAverage() )
//											 );

		return( result );
	}

	protected boolean checkBorderGreyScaleAverage( ComponentsStats emptySquareComponentsStats, long luminanceOfBorder )
	{
		boolean result = false;

		long tolerance = calculateStrictTolerance( emptySquareComponentsStats.getComponentStats( ComponentsStats.LUMINANCE ) );
		if( abs( getAverageLuminance( emptySquareComponentsStats ) - luminanceOfBorder ) <= tolerance );
			result = true;
//		if( getProportion( sum1, sum2 ) >= MAX_PROPORTION_FOR_BACKGROUND_AVERAGE_CHECK_OF_SYNCHRONIZATION_BOARD_BOXES )
//			result = true;
//		if( !getProportion( sum1, sum2 ) >= MAX_PROPORTION_FOR_BACKGROUND_AVERAGE_CHECK_OF_SYNCHRONIZATION_BOARD_BOXES )
//			throw( new RuntimeException( "Porportion not enough to pass the checking of background for synchornization of board boxes" ) );

		return( result );
	}
/*
	protected void checkSyncBox( ChessBoardGridResult grid, BoardBoxSyncResult sync, BoardBoxResult bbr )
	{
		checkSyncBox( grid, sync, bbr.getCol(), bbr.getRow() );
	}

	protected void checkSyncBox( ChessBoardGridResult grid, BoardBoxSyncResult sync, int col, int row )
	{
		PixelComponents[][] imagePixels = sync.getImagePixels();

		Rectangle rect = getBoardBoxBounds( grid, col, row );

		int xx = rect.x + sync.getDeltaX();
		int yy = rect.y + sync.getDeltaY();
		int width = sync.getEffectiveBoxEdgeLength().width;
		int height = sync.getEffectiveBoxEdgeLength().height;

		long wBorderAverage = calculateXborderAverage( xx, yy, height, imagePixels );
		long eBorderAverage = calculateXborderAverage( xx + width - 1, yy, height, imagePixels );
		long nBorderAverage = calculateYborderAverage( xx, yy, width, imagePixels );
		long sBorderAverage = calculateYborderAverage( xx, yy + height - 1, width, imagePixels );

		Pair<ComponentsStats, ComponentsStats> componentsStatsForEmptySquares = sync.getComponentsStatsForEmptySquares();
		ComponentsStats emptySquareComponentsStats = isWhiteBoardBox(col, row) ?
													componentsStatsForEmptySquares.getKey() :
													componentsStatsForEmptySquares.getValue();
		int count = 0;
		if( checkBorderGreyScaleAverage( emptySquareComponentsStats, wBorderAverage ) )
			count++;
		if( checkBorderGreyScaleAverage( emptySquareComponentsStats, eBorderAverage ) )
			count++;
		if( checkBorderGreyScaleAverage( emptySquareComponentsStats, nBorderAverage ) )
			count++;
		if( checkBorderGreyScaleAverage( emptySquareComponentsStats, sBorderAverage ) )
			count++;

		if( count < 4 )
			throw( new RuntimeException( String.format( "checkSyncBox: less than 100% of edges of box [%d, %d] of expected colour", col, row ) ) );
	}
*/
	public BufferedImage getSubimage( BufferedImage image, BoardBoxResult bbr, ChessBoardGridResult grid )
	{
		int boundaryThick = -1;
		return( getSubimage( image, bbr, grid, boundaryThick ) );
	}

	public BufferedImage getSubimage( BufferedImage image, BoardBoxResult bbr, ChessBoardGridResult grid,
										int boundaryThick)
	{
		return( getSubimage( image, bbr.getCol(), bbr.getRow(), grid, boundaryThick ) );
	}

	public BufferedImage getSubimage( BufferedImage image, int col, int row, ChessBoardGridResult grid,
										int boundaryThick)
	{
		Rectangle rect = grid.getBoxBoundsInsideImage( col, row );
		BufferedImage patternImage = getSubimage( image, rect, boundaryThick );

		return( patternImage );
	}

	protected BufferedImage getSubimage( BufferedImage image, Rectangle rect,
											int boundaryThick)
	{
		Rectangle boardBoxBounds = rect;//sync.getEffectiveBoxEdgeLength();

		// skipping a border of one pixel thick
		int width = IntegerFunctions.max( 0, boardBoxBounds.width + 2*boundaryThick );
		int height = IntegerFunctions.max( 0, boardBoxBounds.height + 2*boundaryThick );

		BufferedImage result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		Graphics2D grp = result.createGraphics();

		// skipping a border of one pixel thick
		int xx = rect.x - boundaryThick;// + sync.getDeltaX();
		int yy = rect.y - boundaryThick;// + sync.getDeltaY();

		grp.drawImage(image, 0, 0, width, height,
							xx, yy,
							xx + width,
							yy + height, null );

		grp.dispose();

		return( result );
	}

	public BufferedImage getSummarizedImage( BufferedImage inputImage )
	{
		return( ImageFunctions.instance().resizeImageAccurately(inputImage, 12, 12) );
	}

	public BufferedImage getSummarizedSubimage( BufferedImage inputImage, Rectangle rect )
	{
		return( getSummarizedImage( getSubimage(inputImage, rect, 0 ) ) );
	}

	public ComponentsStats getEmptySquareComponentsStats( Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
															int col, int row )
	{
		boolean isWhiteBoardBox = ( RecognitionUtils.instance().isWhiteBoardBox(col, row) );
		return( getEmptySquareComponentsStats( emptySquareComponentStats, isWhiteBoardBox ) );
	}

	public ComponentsStats getEmptySquareComponentsStats( Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
															boolean isWhiteBoardBox )
	{
		ComponentsStats result = null;

		if( emptySquareComponentStats != null )
		{
			if( isWhiteBoardBox )
				result = emptySquareComponentStats.getKey();
			else
				result = emptySquareComponentStats.getValue();
		}

		return( result );
	}

	public BufferedImage getSubimageSubtractingBackground( BufferedImage image,
													int col, int row,
													ChessBoardGridResult grid,
													Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
													ChessFigurePattern emptySquarePattern)
	{
		int boundaryThick = 0;
		return( getSubimageSubtractingBackground( image, col, row, grid,
													emptySquareComponentStats,
													emptySquarePattern,	boundaryThick) );
	}

	public BufferedImage getSubimageSubtractingBackground( BufferedImage image,
													int col, int row,
													ChessBoardGridResult grid,
													Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
													ChessFigurePattern emptySquarePattern,
													int boundaryThick)
	{
		BufferedImage result = getSubimage( image, col, row, grid, boundaryThick );
		boolean signedComponents = true;
		PixelComponents[][] pixels = ImageFunctions.instance().getPixelComponents(result, signedComponents);

		ComponentsStats css = getEmptySquareComponentsStats( emptySquareComponentStats, col, row );

//		PixelComponents[][] subtractionImage = ImageFunctions.instance().subtract( pixels, emptySquarePattern.getPixels() );

		for( int xx=0; xx<pixels.length; xx++ )
			for( int yy=0; yy<pixels[0].length; yy++ )
//				if( isBackground( subtractionImage[xx][yy] ) )
				if( isBackground( pixels[xx][yy], css ) )
				{
					pixels[xx][yy].setAlpha( (short) -128 );
					result.setRGB(xx, yy, pixels[xx][yy].getPixelValue() );
				}

		refineSubimage( result );
		refineSubimage( result );	// to improve result

		return( result );
	}

	public boolean squareIsEmpty( ChessGamePositionImpl pretendedPosition,
									PixelComponents[][] imagePixels,
									ChessBoardGridResult grid,
									Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
									int col, int row, BoardBoxSyncResult tmpSync,
									BufferedImage blurredImage, ChessFigurePatternSet ps )
	{
		boolean result = false;

		if( pretendedPosition != null )
			result = ( pretendedPosition.getCharacterAtPosition(col, row) == null );
		else if( tmpSync != null )
		{
			result = squareIsEmpty( imagePixels, grid, emptySquareComponentStats, col, row,
									tmpSync.getDeltaX(), tmpSync.getDeltaY() );
			if( ! result )
				result = squareIsEmptySummarizedPattern( col, row, grid, blurredImage, ps );
		}

		return( result );
	}

	protected boolean squareIsEmptySummarizedPattern( int col, int row, ChessBoardGridResult grid,
														BufferedImage blurredImage, ChessFigurePatternSet ps )
	{
		boolean result = false;

		ChessFigurePattern pattern = getEmptySquarePattern( getSquareColorLetter(col, row), ps);
		if( pattern != null )
		{
			Rectangle rect = grid.getBoxBoundsInsideImage(col, row);
			BufferedImage summSubimage = RecognitionUtils.instance().getSummarizedSubimage(blurredImage, rect);
/*
			ExecutionFunctions.instance().safeMethodExecution( () -> 
			ImageIO.write( summSubimage, "TIFF", new File( "J:\\summarizedImage.tiff" ) ) );
*/
			boolean signedComponents = true;
			PixelComponents[][] summSubimagePixels = ImageFunctions.instance().getPixelComponents(summSubimage, signedComponents);

			int borderToSkipThick = 1;
			CorrelationResult cr = getCorrelationResultForSummarizedPattern( summSubimagePixels, pattern, borderToSkipThick );
			result = this.correlationMatchesSummarizedImage( cr );
		}

		return( result );
	}


	public boolean squareIsEmpty( PixelComponents[][] imagePixels,
									ChessBoardGridResult grid,
									Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
									int col, int row, BoardBoxSyncResult tmpSync )
	{
		boolean result = false;

		if( tmpSync != null )
			result = squareIsEmpty( imagePixels, grid, emptySquareComponentStats, col, row,
									tmpSync.getDeltaX(), tmpSync.getDeltaY() );
		return( result );
	}

	public boolean squareIsEmpty( PixelComponents[][] imagePixels,
									ChessBoardGridResult grid,
									Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
									int col, int row, int dx, int dy )
	{
		Rectangle rect = grid.getBoxBoundsInsideImage(col, row);

		ComponentsStats css = getEmptySquareComponentsStats( emptySquareComponentStats, col, row );

		CorrelationResult result = new CorrelationResult();
		result.init(null);

//		PixelComponents[][] subtractionImage = ImageFunctions.instance().subtract( pixels, emptySquarePattern.getPixels() );

		int xto = IntegerFunctions.min( rect.x + rect.width, imagePixels.length - dx );
		int yto = IntegerFunctions.min( rect.y + rect.height, imagePixels[0].length - dy );
		for( int xx=rect.x; xx<xto; xx++ )
			for( int yy=rect.y; yy<yto; yy++ )
//				if( isBackground( subtractionImage[xx][yy] ) )
				if( isBackground( imagePixels[xx+dx][yy+dy], css ) )
					result.incHits();
				else
					result.incNoHits();

		return( correlationMatchesColor(result) );
	}

	public boolean correlationMatches( CorrelationResult cr )
	{
		boolean result = false;

		if( cr != null )
			result = correlationMatchesColor( cr ) ||
//					correlationMatchesShapeAndRelaxedColor( cr ) ||
					correlationMatchesSummarizedImage(cr);

		return( result );
	}

	public boolean correlationMatchesSummarizedImage( CorrelationResult cr )
	{
		boolean result = false;
		double meanErrorThreshold = MAX_MEAN_ERROR_FOR_SUMMARIZED_IMAGE_MATCH;
		if( ( cr != null ) && ( cr.getPattern() != null ) )
		{
			Double particularThreshold = cr.getPattern().getMeanErrorThreshold();
			if( particularThreshold != null )
				meanErrorThreshold = particularThreshold;
		}
		result = ( cr.isSummary() &&
					( cr.getMeanError() <= meanErrorThreshold ) );
		return result;
//			( cr.getMeanError() <= MAX_MEAN_ERROR_FOR_SUMMARIZED_IMAGE_MATCH ) );
	}
/*
	public boolean summarizedCorrelationMatches( CorrelationResult cr )
	{
		return( cr.isSummary() &&
			correlationMatchesColorGen( cr, PROPORTION_FOR_SUMMARIZED_CORRELATION_MATCH_WITH_PIECES ) );
	}
*/
	public boolean correlationMatchesColorRelaxed( CorrelationResult cr )
	{
		return( correlationMatchesColorGen( cr, PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES_RELAXED ) );
	}

	public boolean correlationMatchesColor( CorrelationResult cr )
	{
		return( !cr.isSummary() &&
//			( cr.isPiece() && correlationMatchesColorGen( cr, PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES ) ||
//				cr.isEmptySquare() &&
				correlationMatchesColorGen( cr, PROPORTION_FOR_CORRELATION_MATCH_WITH_EMPTY_SQUARE ) //)
			);
	}

	public boolean correlationMatchesColorGen( CorrelationResult cr, double proportionToReachForMatch )
	{
		boolean result = false;

		if( cr != null )
		{
//			if( isFigurePattern( cr.getPattern() ) )
			result = cr.getProportionOfSuccess() >= proportionToReachForMatch;
		}

		return( result );
	}

	public boolean correlationMatchesShapeAndRelaxedColor( CorrelationResult cr )
	{
		return( correlationMatchesShape( cr ) && correlationMatchesColorRelaxed( cr ) );
	}

	public boolean correlationMatchesShape( CorrelationResult cr )
	{
		boolean result = false;

		if( cr != null )
		{
			if( isFigurePattern( cr.getPattern() ) )
				result = cr.getProportionOfSuccessShape() >= PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES_SHAPE;
//			else
//				result = cr.getProportionOfSuccessShape() >= PROPORTION_FOR_CORRELATION_MATCH_WITH_EMPTY_SQUARE;
		}

		return( result );
	}

	public boolean isFigurePattern( ChessFigurePattern pattern )
	{
		boolean result = false;
		if( pattern != null )
			result = _chessGamePositionImpl.isPieceCode( pattern.getPieceCode() );

		return( result );
	}

	public boolean isWhiteBoardBox( ChessFigurePattern pattern )
	{
		boolean result = false;
		if( pattern != null )
			result = pattern.getName().substring( 2, 3 ).equals( "W" );

		return( result );
	}

	public void refineSubimage( BufferedImage subimage )
	{
		ImageRefinementResult result = new ImageRefinementResult( subimage );
		for( int xx=0; xx<subimage.getWidth(); xx++ )
			for( int yy=0; yy<subimage.getHeight(); yy++ )
			{
				Point point = new Point( xx, yy );

				if( !result.hasBeenProcessed(point) )
				{
					if( isTransparentBackground( subimage.getRGB(xx, yy) ) )
						processBackground( point, result );
					else
						processForeground( point, result );
				}
			}
	}

	protected void processBackground( Point point, ImageRefinementResult result )
	{
		processBackgroundRecursive( point, result );

		if( !result.getHasReachedBorder() )
			makeBackgroundForeground( result );

		result.resetCurrent();
	}

	protected void processBackgroundRecursive( Point point, ImageRefinementResult result )
	{
		if( !result.hasBeenProcessed(point) )
		{
			result.addToCurrentSet(point);
			int xx = point.x;
			int yy = point.y;
//			processBackground( xx + 1, yy + 1, result );
			processBackground( xx + 1, yy, result );
//			processBackground( xx + 1, yy - 1, result );
			processBackground( xx, yy + 1, result );
			processBackground( xx, yy - 1, result );
//			processBackground( xx - 1, yy + 1, result );
			processBackground( xx - 1, yy, result );
//			processBackground( xx - 1, yy - 1, result );
		}
	}

	protected void processBackground( int xx, int yy, ImageRefinementResult result )
	{
		if( result.isInside(xx, yy) && isTransparentBackground( result.getSubimage().getRGB(xx, yy ) ) )
		{
			Point point = new Point( xx, yy );

			if( ! result.hasBeenProcessed(point) )
			{
				processBackgroundRecursive( point, result );
			}
		}
	}

	protected void processForeground( Point point, ImageRefinementResult result )
	{
		processForegroundRecursive( point, result );

		if( result.getCurrentSet().size() <= MAX_NUMBER_OF_PIXELS_IN_BLOCK_TO_MAKE_BACKGROUND )
			makeForegroundBackground( result );

		result.resetCurrent();
	}

	protected void processForegroundRecursive( Point point, ImageRefinementResult result )
	{
		if( !result.hasBeenProcessed(point) )
		{
			result.addToCurrentSet(point);
			int xx = point.x;
			int yy = point.y;
			processForeground( xx + 1, yy, result );
			processForeground( xx, yy + 1, result );
			processForeground( xx, yy - 1, result );
			processForeground( xx - 1, yy, result );
		}
	}

	protected void processForeground( int xx, int yy, ImageRefinementResult result )
	{
		if( result.isInside(xx, yy) && !isTransparentBackground( result.getSubimage().getRGB(xx, yy ) ) )
		{
			Point point = new Point( xx, yy );

			if( ! result.hasBeenProcessed(point) )
			{
				processForegroundRecursive( point, result );
			}
		}
	}

	protected int getAlpha( int argb )
	{
		return( ( argb >>> 24 ) & 0xff );
	}

	protected boolean isTransparentBackground( int argb )
	{
		return( getAlpha( argb ) == 0 );
	}

	protected void makeBackgroundForeground( ImageRefinementResult result )
	{
		BufferedImage subimage = result.getSubimage();
		for( Point point: result.getCurrentSet() )
			subimage.setRGB( point.x, point.y, makeForeground( subimage.getRGB( point.x, point.y ) ) );
	}

	protected void makeForegroundBackground( ImageRefinementResult result )
	{
		BufferedImage subimage = result.getSubimage();
		for( Point point: result.getCurrentSet() )
			subimage.setRGB( point.x, point.y, makeBackground( subimage.getRGB( point.x, point.y ) ) );
	}

	protected int makeBackground( int argb )
	{
		return( changeAlpha( argb, 0 ) );
	}

	protected int makeForeground( int argb )
	{
		return( changeAlpha( argb, 0xff ) );
	}

	protected int changeAlpha( int argb, int alpha )
	{
		return( ( argb & 0xffffff ) | ( alpha << 24 ) );
	}

	public Dimension getImageSize( InputImage image )
	{
		Dimension result = null;
		if( image != null )
		{
			ImageWrapper im1 = image.getImage();
			if( im1 != null )
			{
				BufferedImage im2 = im1.getImage();
				if( im2 != null )
					result = new Dimension( im2.getWidth(), im2.getHeight() );
			}
		}

		return( result );
	}

	protected boolean averageMatches( long avg1, long avg2 )
	{
		long tolerance = TOLERANCE_FOR_EMPTY_SQUARE_COMPONENT_PIXEL_COMPONENT;
		boolean result = ( abs( avg1 - avg2 ) <= tolerance );

		return( result );
	}

	public boolean averageMatches( ComponentsStats css, ComponentsStats compStats )
	{
		boolean result = true;

		for( int ii=ComponentsStats.RED; ii<ComponentsStats.TOTAL_ELEMENTS; ii++ )
		{
			if( ! averageMatches( getAverageComp( css, ii), getAverageComp( compStats, ii ) ) )
			{
				result = false;
				break;
			}
		}

		return( result );
	}


	public CorrelationResult detectSummarizedPattern( BufferedImage blurredImage, Rectangle rect,
												Collection<ChessFigurePattern> patterns, boolean isWhiteBoardBox,
												Pair<ComponentsStats, ComponentsStats> emptySquaresComponentsStats )
	{
		CorrelationResult result = null;

		BufferedImage summSubimage = RecognitionUtils.instance().getSummarizedSubimage(blurredImage, rect);

		// TODO: comment
/*
		ExecutionFunctions.instance().safeMethodExecution( () -> 
			ImageIO.write( summSubimage, "TIFF", new File( "J:\\summarizedImage.tiff" ) ) );
*/
		boolean signedComponents = true;
		PixelComponents[][] summSubimagePixels = ImageFunctions.instance().getPixelComponents(summSubimage, signedComponents);

		CorrelationResult current = null;
		for( ChessFigurePattern pattern: patterns )
		{
			if( isWhiteBoardBox == isWhiteBoardBox(pattern) )
			{
				if( isFigurePattern(pattern) )
				{
					int borderToSkipThick = 1;
					current = getCorrelationResultForSummarizedPattern( summSubimagePixels, pattern, borderToSkipThick );
				}
				else
					current = getCorrelationResultForEmptySquare( summSubimagePixels,
									getEmptySquareComponentsStats( emptySquaresComponentsStats, isWhiteBoardBox ) );

				result = chooseBest( result, current );
			}
		}

		return( result );
	}

	public <CC> CC chooseBest( CC one, CC another, BiFunction<CC, CC, CC> fun )
	{
		CC result = null;

		if( one == null )
			result = another;
		else if( another == null )
			result = one;
		else if( one != null )
			result = fun.apply( one, another );

		return( result );
	}

	protected CorrelationResult chooseBest_internal( CorrelationResult one, CorrelationResult another )
	{
		CorrelationResult result = null;

		if( correlationMatchesColor( one ) && correlationMatchesColor( another ) )
			result = chooseBestColor_internal( one, another );
		else if( correlationMatchesColor( one ) )
			result = one;
		else if( correlationMatchesColor( another ) )
			result = another;
		else if( correlationMatchesShape( one ) && correlationMatchesShape( another ) )
			result = chooseBestShape_internal( one, another );
		else if( correlationMatchesShape( one ) )
			result = one;
		else if( correlationMatchesShape( another ) )
			result = another;
		else if( correlationMatchesSummarizedImage( one ) && correlationMatchesSummarizedImage( another ) )
			result = chooseBestSummarized_internal( one, another );
		else if( correlationMatchesSummarizedImage( one ) )
			result = one;
		else if( correlationMatchesSummarizedImage( another ) )
			result = another;
		else if( one.isSummary() && another.isSummary() )
			result = chooseBestSummarized_internal( one, another );
		else
			result = chooseBestColor_internal( one, another );
//			result = null;

		return( result );
	}

	protected CorrelationResult chooseBestSummarized_internal( CorrelationResult one, CorrelationResult another )
	{
		CorrelationResult result = null;

		double e1 = one.getMeanError();
		double e2 = another.getMeanError();

		if( e1 <= e2 )
			result = one;
		else
			result = another;

		return( result );
	}

	protected CorrelationResult chooseBestShape_internal( CorrelationResult one, CorrelationResult another )
	{
		CorrelationResult result = null;

		double p1 = one.getProportionOfSuccessShape();
		double p2 = another.getProportionOfSuccessShape();

		if( p1 > p2 )
			result = one;
		else if( p1 < p2 )
			result = another;
		else
		{
			if( one.getNumHitsShape() >= another.getNumHitsShape() )
				result = one;
			else
				result = another;
		}

		return( result );
	}

	protected CorrelationResult chooseBestColor_internal( CorrelationResult one, CorrelationResult another )
	{
		CorrelationResult result = null;

		double p1 = one.getProportionOfSuccess();
		double p2 = another.getProportionOfSuccess();

		if( p1 > p2 )
			result = one;
		else if( p1 < p2 )
			result = another;
		else
		{
			if( one.getNumHits() > another.getNumHits() )
				result = one;
			else if( one.getNumHits() < another.getNumHits() )
				result = another;
			else
				result = chooseBestShape_internal( one, another );
		}

		return( result );
	}

	public CorrelationResult chooseBest( CorrelationResult one, CorrelationResult another )
	{
		return( chooseBest( one, another, (cr1, cr2) -> chooseBest_internal( cr1, cr2 ) ) );
	}

	public CorrelationResult getCorrelationResultForEmptySquare( PixelComponents[][] blurredSummarizedImagePixels,
							ComponentsStats css )
	{
		CorrelationResult result = new CorrelationResult();
		result.setPattern(null);
		result.setIsSummary(true);

		int xlen = blurredSummarizedImagePixels.length;
		int ylen = blurredSummarizedImagePixels[0].length;

		PixelComponents[][] blurredImagePixels = blurredSummarizedImagePixels;

		for( int xx=0; xx<xlen; xx++ )
			for( int yy=0; yy<ylen; yy++ )
			{
				PixelComponents imagePixel = blurredImagePixels[xx][yy];

				result.addSquareError( getSquareError( imagePixel, css ) );
			}

		result.setNumberOfSamples( xlen * ylen );

		return( result );
	}

	public PixelStats calculateAverage( PixelComponents[][] pixels, int borderToSkipThick )
	{
		PixelStats result = null;
		if( checkPixels( pixels, borderToSkipThick ) )
		{
			long[] addition = new long[3];
			for( int jj=0; jj<pixels.length; jj++ )
				for( int ii=0; ii<pixels[0].length; ii++ )
					for( int cc=0; cc<3; cc++ )
						addition[cc] += pixels[jj][ii].getComponent(cc + PixelComponents.RED);

			int count = pixels.length * pixels[0].length;

			result = new PixelStats(0.0d,
									getComponentAverage(addition[0], count ),
									getComponentAverage( addition[1], count ),
									getComponentAverage( addition[2], count ),
									pixels[0][0].isSigned() );
		}

		return( result );
	}

	protected double getComponentAverage( long compAddition, int count )
	{
		return( ((double) compAddition) / count );
	}

	protected double getComponentStandardDeviation( long compAddition, int count )
	{
		return( Math.sqrt( ( (double) compAddition) / count ) );
	}

	public PixelStats calculateStandardDeviation( PixelComponents[][] pixels, int borderToSkipThick )
	{
		PixelStats average = calculateAverage( pixels, borderToSkipThick );

		PixelStats result = null;
		if( checkPixels( pixels, borderToSkipThick ) )
		{
			long[] addition = new long[3];
			for( int jj=0; jj<pixels.length; jj++ )
				for( int ii=0; ii<pixels[0].length; ii++ )
					for( int cc=0; cc<3; cc++ )
					{
						int compIndex = cc + PixelComponents.RED;
						short diffCompVal = (short) ( pixels[jj][ii].getComponent(compIndex) - average.getComponent(compIndex) );
						addition[cc] += diffCompVal * diffCompVal;
					}

			int count = pixels.length * pixels[0].length;

			result = new PixelStats(0.0d,
									getComponentStandardDeviation( addition[0], count ),
									getComponentStandardDeviation( addition[1], count ),
									getComponentStandardDeviation( addition[2], count ),
									pixels[0][0].isSigned() );
		}

		return( result );
	}

	protected boolean checkPixels( PixelComponents[][] pixels, int borderToSkipThick )
	{
		return( ( pixels != null ) && ( pixels.length > 0 ) && (pixels[0].length > 0 ) &&
				( borderToSkipThick >= 0 ) &&
				( borderToSkipThick < pixels.length / 2 ) &&
				( borderToSkipThick < pixels[0].length / 2 ) );
	}



	protected static class ImageRefinementResult
	{
		protected BufferedImage _subimage = null;
		protected Set<Point> _processedPoints = new HashSet<>();
		protected Set<Point> _currentSetOfPoints = new HashSet<>();

		protected boolean _hasReachedBorder = false;

		public ImageRefinementResult( BufferedImage subimage )
		{
			_subimage = subimage;
		}

		public boolean isInside( int xx, int yy )
		{
			return( ( xx >= 0 ) && ( xx < _subimage.getWidth() ) &&
				( yy >= 0 ) && ( yy < _subimage.getHeight() ) );
		}

		public boolean hasBeenProcessed( Point point )
		{
			return( _processedPoints.contains( point ) );
		}

		public void addProcessed( Point point )
		{
			_processedPoints.add( point );
		}

		public void addToCurrentSet( Point point )
		{
			addProcessed( point );
			_currentSetOfPoints.add( point );
			if( isBorder( point ) )
				setHasReachedBorder();
		}

		protected boolean isBorder( Point point )
		{
			return( isBorder( point.x, 0, _subimage.getWidth() - 1 ) ||
					isBorder( point.y, 0, _subimage.getHeight() - 1 ) );
		}

		protected boolean isBorder( int value, int lbound, int ubound )
		{
			return( ( value == lbound ) || ( value == ubound ) );
		}

		public Set<Point> getCurrentSet()
		{
			return( _currentSetOfPoints );
		}

		public void resetCurrent()
		{
			_currentSetOfPoints.clear();
			_hasReachedBorder = false;
		}

		public void setHasReachedBorder()
		{
			_hasReachedBorder = true;
		}

		public boolean getHasReachedBorder()
		{
			return( _hasReachedBorder );
		}

		public BufferedImage getSubimage()
		{
			return( _subimage );
		}
	}

	@Override
	public void changeLanguage(String language) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	@Override
	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	@Override
	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}

	protected void registerInternationalizedStrings()
	{
		registerInternationalString( CONF_EFECTIVE_EDGE_LENGTH_DOES_NOT_MATCH, "Efective edge length does not match" );
	}

	protected interface CalculateDelta
	{
		public Integer calculateDelta( PixelComponents[] boundaryAverages, int direction,
										Pair<ComponentsStats, ComponentsStats> componentStatsForEmptySquares );
	}
}
