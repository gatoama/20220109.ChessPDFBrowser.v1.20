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
package com.frojasg1.chesspdfbrowser.recognizer.recognizer.impl;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.position.impl.ChessGamePositionImpl;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.ChessBoardGridDetector;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.impl.ChessBoardGridDetectorImpl;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.correlator.CorrelationResult;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.ChessBoardPositionRecognizerWithStore;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.result.RecognitionResult;
import com.frojasg1.chesspdfbrowser.recognizer.store.ChessBoardRecognitionStore;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ComponentsStats;
import com.frojasg1.chesspdfbrowser.recognizer.trainer.impl.BoardGridSyncResult;
import com.frojasg1.chesspdfbrowser.recognizer.utils.RecognitionUtils;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.structures.Pair;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardPositionRecognizerWithStoreImpl
	implements ChessBoardPositionRecognizerWithStore, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessBoardPositionRecognizerWithStoreImpl.properties";

	protected static final String CONF_ONLY_WHITE_MATCHES_BLACK_MATCHES = "ONLY_WHITE_MATCHES_BLACK_MATCHES";

	protected static final double PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES_SHAPE = RecognitionUtils.PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES_SHAPE;

	protected static final int MAX_DELTA_FOR_CORRELATION = 3;
	protected static final int TOLERANCE_FOR_PIXEL_COMPONENT_WITH_PIECES = 26;
	protected static final int TOLERANCE_FOR_SUMMARIZED_PIXEL_COMPONENT_WITH_PIECES = 33;
	public static final int TOLERANCE_FOR_GREY_SCALE_WITH_EMPTY_SQUARE = 20;

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected ChessBoardRecognitionStore _store = null;

	protected ChessGamePositionImpl _positionDetected = null;

	protected ChessGamePositionImpl _partialPositionResult = null;

	protected ChessBoardGridDetector _gridDetector = null;

	protected ChessBoardGridResult _grid = null;
	protected ChessBoardGridResult _detectedGrid = null;

	protected InputImage _image = null;
	protected PixelComponents[][] _imagePixels = null;

	protected BufferedImage _blurredImage = null;
	protected PixelComponents[][] _blurredImagePixels = null;

	protected List<ChessBoardGridResult> _previouslyDetectedGridsForImageSize = null;

	protected RecognitionResult _result = null;

	public ChessBoardPositionRecognizerWithStoreImpl()
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public void init( ChessBoardRecognitionStore store )
	{
		setChessBaordRecognitionStore( store );
		_positionDetected = null;
		_partialPositionResult = null;
		_gridDetector = null;
		_grid = null;
		_blurredImagePixels = null;
	}

	public PixelComponents[][] getImagePixels()
	{
		return( _imagePixels );
	}

	public BufferedImage getBlurredImage()
	{
		return( _blurredImage );
	}

	public PixelComponents[][] getBlurredImagePixels()
	{
		return( _blurredImagePixels );
	}

	@Override
	public void setChessBaordRecognitionStore(ChessBoardRecognitionStore store)
	{
		_store = store;
	}

	@Override
	public String recognizeBoardFen(InputImage image)
	{
		_image = image;

		_blurredImage = ImageFunctions.instance().getBlurredImage(image.getImage().getImage());
		boolean signedComponents = true;
		_blurredImagePixels = ImageFunctions.instance().getPixelComponents(_blurredImage, signedComponents);


		_gridDetector = createGridDetector();
		_gridDetector.process(image);

		File file =  new File( "J:\\image.tiff" );
		ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(image.getImage().getImage(), "TIFF", file) );

		File file2 =  new File( "J:\\blurredImage.tiff" );
		ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(_blurredImage, "TIFF", file2) );

		_grid = getNearest( _gridDetector, image.getPointToStartRecognition() );

		String fenStr = null;
		if( _grid != null )
			fenStr = detectFen( _grid );

		if( fenStr == null )
		{
			ChessBoardGridResult prevGrid = _grid;
			for( ChessBoardGridResult grid: _gridDetector.getGridOfBoardsDetected() )
			{
				if( prevGrid != grid )
				{
					fenStr = detectFen( grid );
					if( fenStr != null )
						break;
				}
			}
		}

		if( fenStr == null )
		{
			List<ChessBoardGridResult> listOfGrids = _store.getListOfGrids(RecognitionUtils.instance().getImageSize( image ) );

			for( ChessBoardGridResult grid: listOfGrids )
			{
				fenStr = detectFen( grid );
				if( fenStr != null )
					break;
			}
		}

		_result = createRecognitionResult();

		return( fenStr );
	}

	protected RecognitionResult createRecognitionResult()
	{
		return( new RecognitionResult( _positionDetected, ( _detectedGrid != null ) ? _detectedGrid : _grid ) );
	}

	public RecognitionResult getRecognitionResult()
	{
		return( _result );
	}

	protected String detectFen( ChessBoardGridResult grid )
	{
		_grid = grid;

		ChessGamePositionImpl positionDetected = detectPosition( grid );

		String fenStr = null;
		if( positionDetected != null )
		{
			if( positionDetected.isComplete() )
			{
				_positionDetected = positionDetected;
				fenStr = _positionDetected.getFenBoardStringBase();
			}
			else
				_positionDetected = chooseBest( _positionDetected, positionDetected );
		}

		if( _positionDetected == positionDetected )
			_detectedGrid = grid;

		return( fenStr );
	}

	protected ChessGamePositionImpl detectPosition( ChessBoardGridResult grid )
	{
		ChessGamePositionImpl positionDetected = null;

		if( grid != null )
		{
			boolean signedComponents = true;

			_imagePixels = ImageFunctions.instance().getPixelComponents(_image.getImage().getImage(), signedComponents);

			int edgeLength = grid.getEdgeLength();

			List<ChessFigurePatternSet> listOfPatterns = _store.getListOfPatternSetOfEdgeLength(edgeLength);
			if( listOfPatterns != null )
			{
				for( ChessFigurePatternSet ps: listOfPatterns )
				{
					try
					{
						boolean updateEmptySquareAverage = false;
						ChessGamePositionImpl tmpPos = detectPosition( getImagePixels(), getBlurredImagePixels(),
																		grid, ps, updateEmptySquareAverage );
						positionDetected = chooseBest( positionDetected, tmpPos );
					}
					catch( Exception ex )
					{
						ex.printStackTrace();
					}
				}
			}
		}
		else
		{
			throw( new RuntimeException( "Grid not recognized" ) );
		}

		if( positionDetected != null )
			positionDetected.setDetectedGrid(grid);

		return( positionDetected );
	}

	public ChessBoardGridDetector getGridDetector()
	{
		return( _gridDetector );
	}

	public ChessBoardGridResult getGrid()
	{
		return( _grid );
	}

	public ChessBoardGridResult getDetectedGrid()
	{
		return( _detectedGrid );
	}

	protected ChessGamePositionImpl chooseBest_internal( ChessGamePositionImpl pos1, ChessGamePositionImpl pos2 )
	{
		ChessGamePositionImpl result = null;
		
		int hits1 = countHits( pos1 );
		int hits2 = countHits( pos2 );
		if( hits1 > hits2 )
			result = pos1;
		else if( hits2 > hits1 )
			result = pos2;
		else
		{
			double hitPropSum1 = addHitProp( pos1 );
			double hitPropSum2 = addHitProp( pos2 );

			if( hitPropSum1 > hitPropSum2 )
				result = pos1;
			else
				result = pos2;
		}

		return( result );
	}

	protected int countHits( ChessGamePositionImpl pos )
	{
		int result = 0;

		for( int jj=1; jj<=ChessGamePositionImpl.NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=ChessGamePositionImpl.NUM_OF_ROWS; ii++ )
			{
				if( pos.getCorrelationResultAtPosition(jj, ii) != null )
					result++;
			}

		return( result );
	}

	protected double addHitProp( ChessGamePositionImpl pos )
	{
		double result = 0;

		for( int jj=1; jj<=ChessGamePositionImpl.NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=ChessGamePositionImpl.NUM_OF_ROWS; ii++ )
			{
				CorrelationResult cr = pos.getCorrelationResultAtPosition(jj, ii);
				if( cr != null )
					result += cr.getProportionOfSuccess();
			}

		return( result );
	}

	protected ChessGamePositionImpl chooseBest( ChessGamePositionImpl pos1, ChessGamePositionImpl pos2 )
	{
		ChessGamePositionImpl result = null;

		return( RecognitionUtils.instance().chooseBest( pos1, pos2, (p1, p2) -> chooseBest_internal( p1, p2 ) ) );
	}

	public ChessGamePositionImpl getPositionDetected()
	{
		return( _positionDetected );
	}

	protected ChessBoardGridDetector createGridDetector()
	{
		return( new ChessBoardGridDetectorImpl() );
	}

	protected int getSquaredDistance( ChessBoardGridResult gr1,
		ChessBoardGridDetector gridDetector, Point normalizedPoint )
	{
		Point pt1 = ViewFunctions.instance().getCenter( gridDetector.getNormalizedGlobalBoardBounds(gr1));
		int dx = (pt1.x - normalizedPoint.x);
		int dy = (pt1.y - normalizedPoint.y);
		int result = dx * dx + dy * dy;

		return( result );
	}

	protected int nearestFirstComparator( ChessBoardGridResult gr1, ChessBoardGridResult gr2,
		ChessBoardGridDetector gridDetector, Point normalizedPoint )
	{
		int squaredDistance1 = getSquaredDistance( gr1, gridDetector, normalizedPoint );
		int squaredDistance2 = getSquaredDistance( gr2, gridDetector, normalizedPoint );

		return( squaredDistance1 - squaredDistance2 );
	}

	protected ChessBoardGridResult getNearest( ChessBoardGridDetector gridDetector,
												Point normalizedPoint )
	{
		ChessBoardGridResult result = null;
		Optional<ChessBoardGridResult> opt = gridDetector.getGridOfBoardsDetected()
			.stream().sorted( (gr1, gr2) -> nearestFirstComparator( gr1, gr2, gridDetector, normalizedPoint ) )
			.findFirst();

		if( opt.isPresent() )
			result = opt.get();

		return( result );
	}

	public ChessGamePositionImpl detectPosition( ChessFigurePatternSet ps, ChessBoardGridResult grid )
	{
		boolean updateEmptySquareAverage = false;
		return( detectPosition( getImagePixels(), getBlurredImagePixels(), grid,
								ps, updateEmptySquareAverage ) );
	}

	public BoardGridSyncResult calculateSyncGrid( PixelComponents[][] imagePixels,
														ChessBoardGridResult grid,
														ChessFigurePatternSet ps,
														BufferedImage blurredImage,
														boolean updateEmptySquareAverage )
	{
		return( RecognitionUtils.instance().calculateSyncGrid( imagePixels, grid,
														ps, null, blurredImage,
														updateEmptySquareAverage ) );
	}

	protected ChessGamePositionImpl detectPosition( PixelComponents[][] imagePixels,
													PixelComponents[][] blurredImagePixels,
													ChessBoardGridResult grid,
													ChessFigurePatternSet ps,
													boolean updateEmptySquareAverage )
	{
		_partialPositionResult = new ChessGamePositionImpl();

		// TODO: comment
/*
		File file =  new File( "J:\\recognizer.grid.blurredImage.tiff" );
		ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(_blurredImage, "TIFF", file) );
*/

		BoardGridSyncResult gridSync = calculateSyncGrid( imagePixels, grid, ps,
															_blurredImage,
															updateEmptySquareAverage );

		checkEmptySquareColors( blurredImagePixels, gridSync, ps );

		Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats = gridSync.getEmptySquareComponentStats();
		ChessBoardGridResult synchronizedGrid = gridSync.getGrid();

		for( int jj=1; jj<=ChessGamePositionImpl.NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=ChessGamePositionImpl.NUM_OF_ROWS; ii++ )
			{
//				int boundaryThick = -1;
//				PixelComponents[][] boardBoxImagePixels = getBoardBoxImagePixelsSubtractingBackground(jj, ii, synchronizedGrid,
//																									emptySquareComponentStats, _blurredImage,
//																									ps, boundaryThick );
				ComponentsStats css = getEmptySquareComponentsStats(emptySquareComponentStats, jj, ii);

				Rectangle boxBounds = synchronizedGrid.getBoxBoundsInsideImage(jj, ii);
				CorrelationResult correlationResult = null;//detectPattern( blurredImagePixels,
//												boxBounds, ps, boardBoxImagePixels, css );

//				if( !correlationMatches(correlationResult) )
				{
					correlationResult = detectSummarizedPattern(_blurredImage,
												boxBounds, ps, RecognitionUtils.instance().isWhiteBoardBox(jj, ii) );
				}

				if( ! correlationMatches(correlationResult) )
				{
//					correlationResult = detectEmptySquarePattern( blurredImagePixels,
//												boxBounds, ps, boardBoxImagePixels, css );
					correlationResult = detectEmptySquarePattern( blurredImagePixels,
												boxBounds, ps, css );
				}

				if( ! correlationMatches(correlationResult) )
				{
					int borderSupresssionThick = 1;
					correlationResult = detectEmptySquare( blurredImagePixels,
												boxBounds,
												getEmptySquareComponentsStats( emptySquareComponentStats, jj, ii ),
												borderSupresssionThick );
				}

				if( !correlationMatches(correlationResult) )
					correlationResult = null;

				if( correlationResult != null )
				{
					_partialPositionResult.putCorrelationResultAtPositionBase(correlationResult, jj, ii);
				}
			}

		return( _partialPositionResult );
	}

	protected void checkEmptySquareColors( PixelComponents[][] blurredImagePixels,
											BoardGridSyncResult gridSync, ChessFigurePatternSet ps )
	{
		Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats = gridSync.getEmptySquareComponentStats();

		AtomicInteger[] colorSuccess = new AtomicInteger[] { new AtomicInteger(0), new AtomicInteger(0) };
		for( int jj=1; jj<=ChessGamePositionImpl.NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=ChessGamePositionImpl.NUM_OF_ROWS; ii++ )
			{
				int colorIndex = RecognitionUtils.instance().isWhiteBoardBox(jj, ii) ? 0 : 1;

				Rectangle boxBounds = gridSync.getGrid().getBoxBoundsInsideImage(jj, ii);
				ComponentsStats css = getEmptySquareComponentsStats( emptySquareComponentStats, jj, ii );
				int borderSupresssionThick = 1;
				CorrelationResult correlationResult = detectEmptySquare( blurredImagePixels,
																		boxBounds, css, borderSupresssionThick );

				if( !correlationMatches(correlationResult) )
				{
					correlationResult = detectEmptySquarePattern( blurredImagePixels,
													boxBounds, ps, //boardBoxImagePixels,
													css );
				}

				if( correlationMatches(correlationResult) )
				{
					int deltaX = 0;
					int deltaY = 0;
					ComponentsStats compStats = RecognitionUtils.instance().calculateComponentStats( blurredImagePixels, gridSync.getGrid(),
																								jj, ii, deltaX, deltaY );

					if( averageMatches( css, compStats ) )
						colorSuccess[colorIndex].addAndGet(1);
				}
			}

		if( ( colorSuccess[0].get() < 3 ) || ( colorSuccess[1].get() < 3 ) )
		{
			throw( new RuntimeException( createCustomInternationalString( CONF_ONLY_WHITE_MATCHES_BLACK_MATCHES,
																			colorSuccess[0].get(),
																			colorSuccess[1].get() ) ) );
		}
	}

	protected boolean averageMatches( ComponentsStats css, ComponentsStats compStats )
	{
		return( RecognitionUtils.instance().averageMatches( css, compStats ) );
	}

	protected CorrelationResult detectEmptySquare( PixelComponents[][] blurredImagePixels, Rectangle rect,
							ComponentsStats cs, int borderSupresssionThick )
	{
		CorrelationResult result = new CorrelationResult();
		result.setPattern(null);

		int x1 = rect.x + borderSupresssionThick;
		x1 = IntegerFunctions.limit(x1, 0, blurredImagePixels.length - borderSupresssionThick );
		int width = IntegerFunctions.limit(rect.width - 2*borderSupresssionThick,
										0, blurredImagePixels.length - rect.x - 2*borderSupresssionThick );

		int y1 = rect.y + borderSupresssionThick;
		y1 = IntegerFunctions.limit(y1, 0, blurredImagePixels[0].length - borderSupresssionThick );
		int height = IntegerFunctions.limit(rect.height - 2 * borderSupresssionThick,
										0, blurredImagePixels[0].length - rect.y - 2 * borderSupresssionThick );

		for( int jj=0; jj<width; jj++ )
			for( int ii=0; ii<height; ii++ )
			{
				PixelComponents imagePixel = blurredImagePixels[x1+jj][y1+ii];

				if( isBackground( imagePixel, cs ) )
					result.incHits();
				else
					result.incNoHits();
			}

		return( result );
	}

	protected boolean isBackground( PixelComponents pc, ComponentsStats cs )
	{
		return( RecognitionUtils.instance().isBackground(pc, cs) );
	}

	public ComponentsStats getEmptySquareComponentsStats( Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
															int col, int row )
	{
		return( RecognitionUtils.instance().getEmptySquareComponentsStats( emptySquareComponentStats, col, row ) );
	}

	public ComponentsStats getEmptySquareComponentsStats( Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
															boolean isWhiteBoardBox )
	{
		return( RecognitionUtils.instance().getEmptySquareComponentsStats( emptySquareComponentStats, isWhiteBoardBox ) );
	}

	protected PixelComponents[][] getBoardBoxImagePixelsSubtractingBackground( int col, int row,
																				ChessBoardGridResult grid,
																				Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
																				BufferedImage image,
																				ChessFigurePatternSet ps,
																				int boundaryThick)
	{
		PixelComponents[][] result = null;

		if( emptySquareComponentStats != null )
		{
			String squareColorLetter = getSquareColorLetter( col, row );
			ChessFigurePattern emptySquarePattern = getEmptySquarePattern( squareColorLetter, ps );

			if( emptySquarePattern != null )
			{
				BufferedImage subImage = getSubimageSubtractingBackground( image, col,
																			row, grid,
																			emptySquareComponentStats,
																			emptySquarePattern,
																			boundaryThick );

	//			File file =  new File( "J:\\square.tiff" );
	//			ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(subImage, "TIFF", file) );

				boolean signedComponents = true;
				result = ImageFunctions.instance().getPixelComponents(subImage, signedComponents);
			}
		}

		return( result );
	}

	public BufferedImage getSubimageSubtractingBackground( BufferedImage image, int col, int row,
													ChessBoardGridResult grid,
													Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
													ChessFigurePattern emptySquarePattern,
													int boundaryThick)
	{
		return( RecognitionUtils.instance().getSubimageSubtractingBackground( image,
																	col, row,
																	grid, emptySquareComponentStats,
																	emptySquarePattern,
																	boundaryThick) );
	}

	public ChessFigurePattern getEmptySquarePattern( String squareColorLetter,
														ChessFigurePatternSet ps )
	{
		return( RecognitionUtils.instance().getEmptySquarePattern( squareColorLetter, ps ) );
	}

	protected String getSquareColorLetter( int col, int row )
	{
		return( RecognitionUtils.instance().getSquareColorLetter( col, row ) );
	}

	protected CorrelationResult detectSummarizedPattern( BufferedImage blurredImage, Rectangle rect,
												ChessFigurePatternSet ps, boolean isWhiteBoardBox )
	{
		return( detectSummarizedPattern( blurredImage, rect, ps.getTotalListOfPatterns(),
										isWhiteBoardBox, ps.getEmptySquaresComponentsStats() ) );
	}

	protected CorrelationResult detectSummarizedPattern( BufferedImage blurredImage, Rectangle rect,
												Collection<ChessFigurePattern> patterns, boolean isWhiteBoardBox,
												Pair<ComponentsStats, ComponentsStats> emptySquaresComponentsStats )
	{
		return( RecognitionUtils.instance().detectSummarizedPattern( blurredImage, rect, patterns,
																	isWhiteBoardBox, emptySquaresComponentsStats ) );
	}

	protected boolean isWhiteBoardBox( ChessFigurePattern pattern )
	{
		return( RecognitionUtils.instance().isWhiteBoardBox(pattern) );
	}

	protected CorrelationResult detectPattern( PixelComponents[][] blurredImagePixels, Rectangle rect,
												ChessFigurePatternSet ps,
												PixelComponents[][] boardBoxImagePixels,
												ComponentsStats css)
	{
		CorrelationResult result = null;

		for( ChessFigurePattern pattern: ps.getTotalListOfPatterns() )
		{
			if( isFigurePattern(pattern) )
			{
				CorrelationResult current = getCorrelationResult(blurredImagePixels, rect,
																	pattern, //boardBoxImagePixels,
																	css);
				result = chooseBest( result, current );
			}
		}

		return( result );
	}

	protected CorrelationResult detectEmptySquarePattern( PixelComponents[][] blurredImagePixels, Rectangle rect,
															ChessFigurePatternSet ps,
//															PixelComponents[][] boardBoxImagePixels,
															ComponentsStats css)
	{
		CorrelationResult result = null;

		for( ChessFigurePattern pattern: ps.getTotalListOfPatterns() )
		{
			if( !isFigurePattern(pattern) )
			{
				CorrelationResult current = getCorrelationResult(blurredImagePixels, rect,
																	pattern, //boardBoxImagePixels,
																	css);
				result = chooseBest( result, current );
			}
		}

		return( result );
	}

	protected CorrelationResult getCorrelationResult( PixelComponents[][] blurredImagePixels, Rectangle rect,
							ChessFigurePattern pattern,
//							PixelComponents[][] boardBoxImagePixels,
							ComponentsStats css)
	{
		CorrelationResult result = null;

		for( int delta = 0; delta < MAX_DELTA_FOR_CORRELATION; delta ++ )
		{
			CorrelationResult cr = null;
			int dx1 = -delta;
			int dx2 = delta;
			CorrelationResult tmpCr = null;
			for( int dy = -delta; dy <= delta; dy++ )
			{
				tmpCr = getCorrelationResult(blurredImagePixels, rect, pattern, dx1, dy, css );
				cr = chooseBest( cr, tmpCr );
				tmpCr = getCorrelationResult(blurredImagePixels, rect, pattern, dx2, dy, css );
				cr = chooseBest( cr, tmpCr );
			}

			int dy1 = -delta;
			int dy2 = delta;
			for( int dx = -delta+1; dx <= delta-1; dx++ )
			{
				tmpCr = getCorrelationResult(blurredImagePixels, rect, pattern, dx, dy1, css );
				cr = chooseBest( cr, tmpCr );
				tmpCr = getCorrelationResult(blurredImagePixels, rect, pattern, dx, dy2, css );
				cr = chooseBest( cr, tmpCr );
			}

			CorrelationResult newResult = chooseBest( result, cr );
			if( ( newResult == result ) && correlationMatches( result ) )
				break;
			else
				result = newResult;
		}

//		if( !correlationMatches(result) && ( boardBoxImagePixels != null ) )
//			result = correlateShape( boardBoxImagePixels, pattern, result );

		return( result );
	}

	protected boolean outOfBounds( int value, int delta, int total )
	{
		return( ( value + delta ) > total );
	}

	protected CorrelationResult correlateShape( PixelComponents[][] boardBoxImagePixels,
												PixelComponents[][] patternPixels,
												int dx, int dy )
	{
		CorrelationResult result = new CorrelationResult();

		int width = IntegerFunctions.min( boardBoxImagePixels.length, patternPixels.length );
		int height = IntegerFunctions.min( boardBoxImagePixels[0].length, patternPixels[0].length );

		int dx1 = ( boardBoxImagePixels.length > width ) ? dx : 0;
		int dx2 = ( patternPixels.length > width ) ? dx : 0;
		int dy1 = ( boardBoxImagePixels[0].length > height ) ? dy : 0;
		int dy2 = ( patternPixels[0].length > height ) ? dy : 0;

		if( outOfBounds( width, dx1, boardBoxImagePixels.length ) ||
			outOfBounds( width, dx2, patternPixels.length ) ||
			outOfBounds( height, dy1, boardBoxImagePixels[0].length ) ||
			outOfBounds( height, dy2, patternPixels[0].length ) )
		{
			int kk=0;
		}

		int total = 0;
		int empty = 0;
		for( int xx=0; xx<width; xx++ )
			for( int yy=0; yy<height; yy++ )
			{
				PixelComponents boardBoxImagePixel = boardBoxImagePixels[xx+dx1][yy+dy1];
				PixelComponents patternPixel = patternPixels[xx+dx2][yy+dy2];

				total++;
				if( patternPixel.getAlpha() == Byte.MAX_VALUE ) // opaque
				{
					if( boardBoxImagePixel.getAlpha() == Byte.MAX_VALUE )
						result.incHitsShape();
					else
						result.incNoHitsShape();
				}
				else
				{
					empty++;
					if( boardBoxImagePixel.getAlpha() == Byte.MAX_VALUE )
						result.incNoHitsShape();
					else
						result.incHitsShape();
				}
			}

			// if boardBox empty, we forget shape
			if( getProportion( total, empty ) >= PROPORTION_FOR_CORRELATION_MATCH_WITH_PIECES_SHAPE )
				result = null;

		return( result );
	}

	protected CorrelationResult correlateShape( PixelComponents[][] boardBoxImagePixels,
												PixelComponents[][] patternPixels )
	{
		CorrelationResult result = null;

		// just in case sizes do not match.
		int dxTo = IntegerFunctions.abs( boardBoxImagePixels.length - patternPixels.length );
		int dyTo = IntegerFunctions.abs( boardBoxImagePixels[0].length - patternPixels[0].length );

		for( int dx=0; dx<=dxTo; dx++ )
			for( int dy=0; dy<=dyTo; dy++ )
			{
				CorrelationResult tmp = correlateShape( boardBoxImagePixels, patternPixels, dx, dy );

				result = chooseBest( result, tmp );
			}

		return( result );
	}

	protected CorrelationResult correlateShape( PixelComponents[][] boardBoxImagePixels,
												ChessFigurePattern pattern,
												CorrelationResult result )
	{
		PixelComponents[][] patternPixels = pattern.getPixels();

		CorrelationResult partialResult = correlateShape( boardBoxImagePixels, patternPixels );

		if( partialResult != null )
		{
			if( result != null )
				result.copyShape( partialResult );
			else
			{
				partialResult.setPattern( pattern );
				result = partialResult;
			}
		}

		return( result );
	}

	protected double getProportion( long v1, long v2 )
	{
		return( RecognitionUtils.instance().getProportion( v1, v2 ) );
	}

	protected boolean sameSize( PixelComponents[][] pixels1, PixelComponents[][] pixels2 )
	{
		boolean result = false;

		if( ( pixels1 != null ) && ( pixels2 != null ) )
			result = ( pixels1.length == pixels2.length ) &&
					( ( pixels1.length == 0 ) || ( pixels1[0].length == pixels2[0].length ) );

		return( result );
	}

	protected CorrelationResult getCorrelationResult( PixelComponents[][] blurredImagePixels, Rectangle rect,
							ChessFigurePattern pattern, int dx, int dy, ComponentsStats css )
	{
		CorrelationResult result = new CorrelationResult();
		result.setPattern(pattern);

		int x1 = rect.x + dx;
		int x2 = 0;
		if( x1 < 0 )
			x2 = -x1;
		x1 = IntegerFunctions.limit(x1, 0, blurredImagePixels.length );
		int x1f = IntegerFunctions.limit(rect.x + dx + pattern.getImage().getWidth(), 0, blurredImagePixels.length );
		int xlen = x1f - x1;

		int y1 = rect.y + dy;
		int y2 = 0;
		if( y1 < 0 )
			y2 = -y1;
		y1 = IntegerFunctions.limit(y1, 0, blurredImagePixels[0].length );
		int y1f = IntegerFunctions.limit(rect.y + dy + pattern.getImage().getHeight(), 0, blurredImagePixels[0].length );
		int ylen = y1f - y1;

		if( isFigurePattern(pattern) )
			correlateFigureForNormalPattern(blurredImagePixels, pattern, x1, x2, xlen, y1, y2, ylen, css, result );
		else
			correlateEmptySquarePattern(blurredImagePixels, pattern, x1, x2, xlen, y1, y2, ylen, css, result );

		return( result );
	}

	protected void correlateFigureForNormalPattern( PixelComponents[][] blurredImagePixels,
							ChessFigurePattern pattern, int x1, int x2, int xlen,
							int y1, int y2, int ylen, ComponentsStats css,
							CorrelationResult result )
	{
		correlateFigurePattern( blurredImagePixels,
							pattern.getPixels(),
							pattern, x1, x2, xlen,
							y1, y2, ylen, css,
							result, TOLERANCE_FOR_PIXEL_COMPONENT_WITH_PIECES );
	}

	protected long getSquareError( PixelComponents pc, ComponentsStats css )
	{
		return( RecognitionUtils.instance().getSquareError( pc, css ) );
	}

	protected CorrelationResult getCorrelationResultForSummarizedPattern(
							PixelComponents[][] blurredSummarizedImagePixels,
							ChessFigurePattern pattern, int borderToSkipThick )
	{
		return( RecognitionUtils.instance()
			.getCorrelationResultForSummarizedPattern( blurredSummarizedImagePixels,
														pattern, borderToSkipThick ) );
	}

	protected void correlateFigurePattern( PixelComponents[][] blurredImagePixels,
							PixelComponents[][] patternPixels,
							ChessFigurePattern pattern, int x1, int x2, int xlen,
							int y1, int y2, int ylen, ComponentsStats css,
							CorrelationResult result,
							int toleranceForComponentToMatch )
	{
		for( int jj=0; jj<xlen; jj++ )
			for( int ii=0; ii<ylen; ii++ )
			{
				PixelComponents patternPixel = patternPixels[x2+jj][y2+ii];
				PixelComponents imagePixel = blurredImagePixels[x1+jj][y1+ii];

				if( patternPixel.getAlpha() == Byte.MAX_VALUE ) // opaque
				{
//					if( componentsMatch( patternPixel, imagePixel, TOLERANCE_FOR_PIXEL_COMPONENT_WITH_PIECES ) )
					if( componentsMatch( patternPixel, imagePixel, toleranceForComponentToMatch ) )
//					if( pixelsMatch( patternPixel, imagePixel, css ) )
						result.incHits();
					else
						result.incNoHits();
/*
					if( greyMatches( patternPixel, imagePixel, TOLERANCE_FOR_PIXEL_COMPONENT_WITH_PIECES ) )
						result.incHitsGrey();
					else
						result.incNoHitsGrey();
*/
				}
			}
	}

	public boolean pixelsMatch( PixelComponents pc1, PixelComponents pc2, ComponentsStats cs )
	{
		return( RecognitionUtils.instance().pixelsMatch( pc1, pc2, cs) );
	}

	protected void correlateEmptySquarePattern( PixelComponents[][] blurredImagePixels,
							ChessFigurePattern pattern, int x1, int x2, int xlen,
							int y1, int y2, int ylen, ComponentsStats css,
							CorrelationResult result )
	{
		PixelComponents[][] patternPixels = pattern.getPixels();
		for( int jj=0; jj<xlen; jj++ )
			for( int ii=0; ii<ylen; ii++ )
			{
				PixelComponents patternPixel = patternPixels[x2+jj][y2+ii];
				PixelComponents imagePixel = blurredImagePixels[x1+jj][y1+ii];

//				if( greyMatches( patternPixel, imagePixel, TOLERANCE_FOR_GREY_SCALE_WITH_EMPTY_SQUARE ) )
				if( pixelsMatch( patternPixel, imagePixel, css ) )
					result.incHits();
				else
					result.incNoHits();
			}
	}

	protected boolean componentsMatch( PixelComponents p1, PixelComponents p2, int tolerance )
	{
		return( componentMatch( p1.getRed(), p2.getRed(), tolerance ) &&
				componentMatch( p1.getGreen(), p2.getGreen(), tolerance ) &&
				componentMatch( p1.getBlue(), p2.getBlue(), tolerance ) );
	}

	protected boolean componentMatch( int v1, int v2, int tolerance )
	{
		return( IntegerFunctions.abs( v1 - v2 ) <= tolerance );
	}

	protected boolean greyMatches( PixelComponents p1, PixelComponents p2, int tolerance )
	{
		return( componentMatch( p1.getGreyScale(), p2.getGreyScale(), tolerance ) );
	}

	protected boolean correlationMatches( CorrelationResult cr )
	{
		return( RecognitionUtils.instance().correlationMatches( cr ) );
	}

	protected boolean correlationMatchesColor( CorrelationResult cr )
	{
		return( RecognitionUtils.instance().correlationMatchesColor( cr ) );
	}

	protected boolean correlationMatchesShape( CorrelationResult cr )
	{
		return( RecognitionUtils.instance().correlationMatchesShapeAndRelaxedColor( cr ) );
	}

	protected boolean correlationMatchesSummarizedImage( CorrelationResult cr )
	{
		return( RecognitionUtils.instance().correlationMatchesSummarizedImage( cr ) );
	}

	protected boolean isFigurePattern( ChessFigurePattern pattern )
	{
		return( RecognitionUtils.instance().isFigurePattern( pattern ) );
	}

	protected CorrelationResult chooseBest( CorrelationResult one, CorrelationResult another )
	{
		return( RecognitionUtils.instance().chooseBest( one, another ) );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_ONLY_WHITE_MATCHES_BLACK_MATCHES, "Only white matches: $1, black matches: $2" );
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
}
