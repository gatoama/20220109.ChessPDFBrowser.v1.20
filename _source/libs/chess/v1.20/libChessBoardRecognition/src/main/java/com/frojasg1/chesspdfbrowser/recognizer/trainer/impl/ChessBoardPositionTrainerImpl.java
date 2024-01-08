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
package com.frojasg1.chesspdfbrowser.recognizer.trainer.impl;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.position.impl.ChessGamePositionImpl;
import com.frojasg1.chesspdfbrowser.recognizer.chessboard.result.ChessBoardGridResult;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.ChessRecognizerApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.correlator.CorrelationResult;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.impl.ChessBoardPositionRecognizerWithStoreImpl;
import com.frojasg1.chesspdfbrowser.recognizer.store.ChessBoardRecognitionStore;
import com.frojasg1.chesspdfbrowser.recognizer.store.pattern.ChessFigurePattern;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ChessFigurePatternSet;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.ComponentsStats;
import com.frojasg1.chesspdfbrowser.recognizer.trainer.ChessBoardPositionTrainer;
import com.frojasg1.chesspdfbrowser.recognizer.utils.RecognitionUtils;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.pixel.impl.PixelComponents;
import com.frojasg1.general.structures.Pair;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardPositionTrainerImpl implements ChessBoardPositionTrainer, InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessBoardPositionTrainerImpl.properties";

	protected static final String CONF_CANNOT_MATCH_ALL_PATTERNS = "CANNOT_MATCH_ALL_PATTERNS";
	protected static final String CONF_CANNOT_MATCH_ALL_PATTERNS_EMPTY_BOARD_BOX_PATTERN_DOES_NOT_MATCH = "CANNOT_MATCH_ALL_PATTERNS_EMPTY_BOARD_BOX_PATTERN_DOES_NOT_MATCH";
	protected static final String CONF_NON_MATCHING_PATTERN_OF_EXPECTED_TYPE_EXISTED = "NON_MATCHING_PATTERN_OF_EXPECTED_TYPE_EXISTED";

	protected static final int NUM_OF_COLUMNS = ChessGamePositionImpl.NUM_OF_COLUMNS;
	protected static final int NUM_OF_ROWS = ChessGamePositionImpl.NUM_OF_ROWS;

	protected InternationalizedStringConfImp _internationalizedStringConf = null;

	protected ChessBoardPositionRecognizerWithStoreImpl _positionDetector = null;

	protected ChessGamePositionImpl _positionDetected = null;

	protected ChessRecognizerApplicationConfiguration _appliConf;

	public ChessBoardPositionTrainerImpl(ChessRecognizerApplicationConfiguration appliConf)
	{
		_appliConf = appliConf;

		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	@Override
	public boolean train( String fen, InputImage image, ChessBoardRecognitionStore store )
	{
		Boolean success = ExecutionFunctions.instance().safeFunctionExecution( () -> train_internal( fen, image, store ) );

		if( (success == null) || !success )
			success = train_internal( flipFen( fen ), image, store );

		return( success );
	}

	public boolean train_internal( String fen, InputImage image, ChessBoardRecognitionStore store )
	{
		boolean success = false;

		_positionDetected = detectPosition( image, store );

		ChessFigurePatternSet ps = ( _positionDetected == null ) ?
									null :
									getPatternSet( _positionDetected );

		BufferedImage blurredImage = getBlurredImage();
		ChessBoardGridResult grid = getGrid();
		if( ( ps != null ) && ( grid != null ) )
		{
			if( !matches( _positionDetected, fen ) || ! _positionDetected.isComplete() )
			{
				ChessGamePositionImpl previousPositionDetected = null;
				ChessFigurePatternSet prevPs = ps;
				while( !success && !Objects.equals( _positionDetected, previousPositionDetected ) )
				{
					previousPositionDetected = _positionDetected;
					ChessFigurePatternSet ps2 = completePatternSet( fen, blurredImage,
													_positionDetected, grid, store, prevPs );
					if( ps2 == null ) // if ps2 is null, that means that two patterns of different type are very simiilar
						break;

					prevPs = ps2;
					if( validate( fen, image, ps2, grid ) )
					{
						ps.init( ps2 );
						success = true;
					}
				}
			}
			else
			{
				success = true;
			}

			if( success )
				updateSuccessfulDetection( _positionDetected, ps, image, getGrid() );
		}
		else
		{
			if( grid != null )
				success = buildAndAddNewPatternSetIfSuccess( fen, image, blurredImage, store, grid );

			if( ! success )
			{
				for( ChessBoardGridResult grid2: getListOfGrids() )
				{
					if( grid2 != grid )
					{
						success = buildAndAddNewPatternSetIfSuccess( fen, image, blurredImage, store, grid2 );
						if( success )
							break;
					}
				}
			}
		}

		return( success );
	}

	protected String flipFen( String fen )
	{
		String result = null;

		if( fen != null )
		{
			ChessGamePositionImpl pos = new ChessGamePositionImpl();
			pos.setFenPositionBase( fen );

			result = pos.getFlippedFenBoardStringBase();
		}

		return( result );
	}

	public ChessBoardPositionRecognizerWithStoreImpl getDetector()
	{
		return( _positionDetector );
	}

	protected PixelComponents[][] getImagePixels()
	{
		return( _positionDetector.getImagePixels() );
	}

	protected PixelComponents[][] getBlurredImagePixels()
	{
		return( _positionDetector.getBlurredImagePixels() );
	}

	protected BufferedImage getBlurredImage()
	{
		return( _positionDetector.getBlurredImage() );
	}

	protected List<ChessBoardGridResult> getListOfGrids()
	{
		return( _positionDetector.getGridDetector().getGridOfBoardsDetected() );
	}

	protected boolean validate( String fen, InputImage image,
								ChessFigurePatternSet ps, ChessBoardGridResult grid )
	{
		boolean result = false;

		_positionDetected = null;
		if( ps != null )
			_positionDetected = detectPosition( ps, grid );

		if( _positionDetected != null )
		{
			String detectedBaseBoardFen = _positionDetected.getFenBoardStringBase();
			String pretendedBaseBoardFen = _positionDetected.getFenPositionBase(fen);
			if( ( detectedBaseBoardFen != null ) &&
					detectedBaseBoardFen.equals( pretendedBaseBoardFen ) )
				result = true;
		}

		return( result );
	}

	protected ChessFigurePatternSet getPatternSet( ChessGamePositionImpl positionDetected )
	{
		ChessFigurePatternSet result = null;

		if( positionDetected != null )
		{
			break1:
			for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
				for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
				{
					CorrelationResult cr = positionDetected.getCorrelationResultAtPosition(jj, ii);
					if( cr != null )
					{
						ChessFigurePattern pattern = cr.getPattern();
						if( pattern != null )
							result = pattern.getParent();

						if( result != null )
							break break1;
					}
				}
		}

		return( result );
	}

	public ChessBoardGridResult getGrid()
	{
		return( _positionDetector.getDetectedGrid() );
	}

	protected ChessFigurePatternSet completePatternSet( String fen, BufferedImage blurredImage,
		ChessGamePositionImpl positionDetected, ChessBoardGridResult grid,
		ChessBoardRecognitionStore store, ChessFigurePatternSet ps )
	{
		ChessFigurePatternSet result = new ChessFigurePatternSet();
		result.init( ps );

		boolean isPatternSetNew = false;
		result = fillInPatternSetWithRemainingPieces( fen, blurredImage, grid, positionDetected, result, isPatternSetNew );

		return( result );
	}

	protected boolean buildAndAddNewPatternSetIfSuccess( String fen, InputImage image,
				BufferedImage blurredImage,
				ChessBoardRecognitionStore store, ChessBoardGridResult grid )
	{
		boolean success = false;

		try
		{
			ChessFigurePatternSet ps = buildNewPatternSet( fen, blurredImage, store, grid );
			if( validate( fen, image, ps, grid ) )//;  // remove ; !!
			{
				store.add( ps );
				success = true;

				updateSuccessfulDetection( _positionDetected, ps, image, grid );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( success );
	}

	protected ChessFigurePatternSet buildNewPatternSet( String fen, BufferedImage blurredImage,
				ChessBoardRecognitionStore store, ChessBoardGridResult grid )
	{
		ChessFigurePatternSet result = store.createAndAddEmptyChessFigurePatternSet(grid.getEdgeLength());

		boolean isPatternSetNew = true;
		ChessGamePositionImpl positionDetected = null;
		result = fillInPatternSetWithRemainingPieces( fen, blurredImage, grid, positionDetected,
														result, isPatternSetNew );

		return( result );
	}

	protected BoardGridSyncResult calculateBoardBoxSync( ChessBoardGridResult grid,
															ChessGamePositionImpl pretendedPosition,
															ChessFigurePatternSet result,
															boolean updateEmptySquareAverage )
	{
		// TODO: comment
		File file =  new File( "J:\\trainer.grid.blurredImage.tiff" );
		ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(getBlurredImage(), "TIFF", file) );

		return( RecognitionUtils.instance().calculateSyncGrid( getImagePixels(), grid, result,
														pretendedPosition, getBlurredImage(),
														updateEmptySquareAverage ) );
	}

	protected ChessFigurePatternSet fillInPatternSetWithRemainingPieces( String fen, BufferedImage blurredImage,
				ChessBoardGridResult grid,
				ChessGamePositionImpl positionDetected,
				ChessFigurePatternSet result,
				boolean isPatternSetNew )
	{
		ChessGamePositionImpl pretendedPosition = new ChessGamePositionImpl();
		pretendedPosition.setFenPositionBase( pretendedPosition.getFenPositionBase(fen) );

//		File file =  new File( "J:\\blurredImage.tiff" );
//		ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(image.getImage().getImage(), "TIFF", file) );

		boolean updateEmptySquareAverage = isPatternSetNew;
		BoardGridSyncResult gridSync = calculateBoardBoxSync( grid, pretendedPosition,
																result, updateEmptySquareAverage );

		Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats = null;
		
		if( isPatternSetNew )
		{
			emptySquareComponentStats = gridSync.getEmptySquareComponentStats();
			result.setEmptySquaresComponentsStats( emptySquareComponentStats );

			// we update grid, in case there is some little change in synchronization due to the change in empty square components.
			updateEmptySquareAverage = false;
			gridSync = calculateBoardBoxSync( grid, pretendedPosition,	
											result, updateEmptySquareAverage );
		}
		else
			emptySquareComponentStats = result.getEmptySquaresComponentsStats();

		ChessBoardGridResult synchronizedGrid = gridSync.getGrid();

		Set<ChessFigurePattern> newPatterns = new HashSet<>();

		List<BoardBoxResult> remainingPieces = getRemainingPieces( pretendedPosition, positionDetected, result );
		addEmptySquarePattern( blurredImage, remainingPieces, result, synchronizedGrid, ChessFigurePatternSet.EMPTY_WHITE_SQUARE_TYPE, newPatterns );
		addEmptySquarePattern( blurredImage, remainingPieces, result, synchronizedGrid, ChessFigurePatternSet.EMPTY_BLACK_SQUARE_TYPE, newPatterns );

		for( BoardBoxResult bbr: remainingPieces )
		{
			if( bbr.getPiece() != null )
			{
				String patternCode = getPatternCode( bbr );
				Rectangle rect = synchronizedGrid.getBoxBoundsInsideImage( bbr.getCol(), bbr.getRow() );
				ChessFigurePattern recognizedPattern = recognizePattern( blurredImage, rect,
																newPatterns, isWhiteBoardBox( bbr ),
																result.getEmptySquaresComponentsStats());

				// TODO: check if this comentation is valid, as is the next
//				if( recognizedPattern == null )
				if( ( recognizedPattern == null ) ||
					! getPatternCode(recognizedPattern).equals( patternCode ) )
				{
					ChessFigurePattern pattern = addPiecePattern( bbr, synchronizedGrid,
																emptySquareComponentStats,
																patternCode, blurredImage,
																result );
					newPatterns.add( pattern );
				}
//				else if( ! getPatternCode(recognizedPattern).equals( patternCode ) )
//				{
//					throw( new RuntimeException( getInternationalString( CONF_CANNOT_MATCH_ALL_PATTERNS ) ) );
//				}
			}
		}

		if( !newPatterns.isEmpty() )
			result.recalculateErrorThresholds();

		if( result.getMeanErrorThreshold() < 2.0d )
			result = null;	// just in case two patterns of different type are too similar.

		return( result );
	}

	protected boolean isWhiteBoardBox( BoardBoxResult bbr )
	{
		return( RecognitionUtils.instance().isWhiteBoardBox( bbr ) );
	}

	protected ChessFigurePattern recognizePattern( BufferedImage blurredImage, Rectangle rect,
													Collection<ChessFigurePattern> patterns,
													boolean isWhiteBoardBox,
													Pair<ComponentsStats, ComponentsStats> emptySquaresComponentsStats )
	{
		ChessFigurePattern result = null;

		CorrelationResult correlationResult = RecognitionUtils.instance().detectSummarizedPattern( blurredImage, rect,
													patterns, isWhiteBoardBox, emptySquaresComponentsStats );
		
		if( ( correlationResult != null ) && ( correlationMatches( correlationResult ) ) )
			result = correlationResult.getPattern();

		return( result );
	}

	protected boolean correlationMatches( CorrelationResult cr )
	{
		return( RecognitionUtils.instance().correlationMatches( cr ) );
	}

	public String getPatternCode( ChessFigurePattern pattern )
	{
		return( RecognitionUtils.instance().getPatternCode( pattern ) );
	}

	public String getPatternCode( BoardBoxResult bbr )
	{
		return( RecognitionUtils.instance().getPatternCode( bbr ) );
	}
/*
	protected boolean checkAllBoardBoxes( ChessBoardGridResult grid, BoardBoxSyncResult sync )
	{
		boolean result = true;
		PixelComponents[][] imagePixels = sync.getImagePixels();

		try
		{
			for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
				for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
					checkSyncBox( grid, sync, jj, ii );
		}
		catch( Exception ex )
		{
			result = false;
		}

		return( result );
	}
*/
	public boolean isWhiteSquare( int col, int row )
	{
		return( RecognitionUtils.instance().isWhiteSquare( col, row ) );
	}

	public String getSquareColorLetter( int col, int row )
	{
		return( RecognitionUtils.instance().getSquareColorLetter(col, row) );
	}

	public String getSquareColorLetter( BoardBoxResult bbr )
	{
		return( RecognitionUtils.instance().getSquareColorLetter( bbr ) );
	}

	public ChessFigurePattern getEmptySquarePattern( String squareColorLetter,
														ChessFigurePatternSet ps )
	{
		return( RecognitionUtils.instance().getEmptySquarePattern( squareColorLetter, ps ) );
	}

	protected ChessFigurePattern addPiecePattern( BoardBoxResult bbr, ChessBoardGridResult grid,
													Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
													String patternCode,
													BufferedImage blurredImage,
													ChessFigurePatternSet result )
	{
		ChessFigurePattern pattern = null;
//		String patternType = patternCode.substring(0,1);
		String squareColorLetter = getSquareColorLetter( bbr );
		ChessFigurePattern emptySquarePattern = getEmptySquarePattern( squareColorLetter, result );

		if( emptySquarePattern != null )
		{
			pattern = result.addPattern(patternCode);

//			checkSyncBox( grid, sync, bbr );

			BufferedImage subImage = getSubimageSubtractingBackground( blurredImage, bbr.getCol(),
																		bbr.getRow(), grid,
																		emptySquareComponentStats,
																		emptySquarePattern );
			Rectangle rect = grid.getBoxBoundsInsideImage( bbr.getCol(), bbr.getRow() );
			BufferedImage summSubImage = RecognitionUtils.instance().getSummarizedSubimage( blurredImage, rect );

			// TODO: comment
/*
			File file =  new File( "J:\\pattern." + pattern.getName() + ".tiff" );
			ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(subImage, "TIFF", file) );

			File summFile =  new File( "J:\\pattern." + pattern.getName() + "_summarized.tiff" );
			ExecutionFunctions.instance().safeMethodExecution( () -> ImageIO.write(summSubImage, "TIFF", summFile) );
*/
			pattern.setImage(subImage);
			pattern.setSummarizedImage(summSubImage);
		}

		return( pattern );
	}

	public BufferedImage getSubimageSubtractingBackground( BufferedImage blurredImage, int col, int row,
													ChessBoardGridResult grid,
													Pair<ComponentsStats, ComponentsStats> emptySquareComponentStats,
													ChessFigurePattern emptySquarePattern )
	{
		return( RecognitionUtils.instance().getSubimageSubtractingBackground( blurredImage,
																		col, row, grid,
																		emptySquareComponentStats,
																		emptySquarePattern ) );
	}

	public boolean isBackground( PixelComponents pc )
	{
		return( RecognitionUtils.instance().isBackground(pc) );
	}

	protected void addEmptySquarePattern( BufferedImage blurredImage, List<BoardBoxResult> remainingPieces,
		ChessFigurePatternSet result, ChessBoardGridResult grid,
		String emptySquarePatternCode, Set<ChessFigurePattern> newPatterns )
	{
		for( BoardBoxResult bbr: remainingPieces )
		{
			if( getPatternCode(bbr).equals(emptySquarePatternCode) )
			{
				Rectangle rect = grid.getBoxBoundsInsideImage( bbr.getCol(), bbr.getRow() );
				ChessFigurePattern recognizedPattern = recognizePattern( blurredImage, rect,
																newPatterns, isWhiteBoardBox( bbr ),
																result.getEmptySquaresComponentsStats());

				if( recognizedPattern == null )
				{
					ChessFigurePattern newPattern = result.addPattern(emptySquarePatternCode);
					fillInEmptySquarePattern( blurredImage, bbr, grid, newPattern );

					newPatterns.add( newPattern );
				}
				else if( ! getPatternCode(recognizedPattern).equals( emptySquarePatternCode ) )
				{
					throw( new RuntimeException( getInternationalString( CONF_CANNOT_MATCH_ALL_PATTERNS_EMPTY_BOARD_BOX_PATTERN_DOES_NOT_MATCH ) ) );
				}
			}
		}
	}

	protected void fillInEmptySquarePattern( BufferedImage blurredImage, BoardBoxResult bbr, ChessBoardGridResult grid,
											ChessFigurePattern result )
	{
		if( bbr != null )
		{
			BufferedImage patternImage = getSubimage( blurredImage, bbr, grid );
			result.setImage(patternImage); 
			BufferedImage summSubImage = RecognitionUtils.instance().getSummarizedSubimage( blurredImage, this.getBoardBoxBounds(grid, bbr.getCol(), bbr.getRow()) );
			result.setSummarizedImage(summSubImage);
		}
	}

	protected BufferedImage getSubimage( BufferedImage blurredImage, BoardBoxResult bbr, ChessBoardGridResult grid )
	{
		return( RecognitionUtils.instance().getSubimage(blurredImage, bbr, grid) );
	}

	protected Rectangle getBoardBoxBounds( ChessBoardGridResult grid, int col, int row )
	{
		return( grid.getBoxBoundsInsideImage(col, row) );
	}

	protected List<BoardBoxResult> getRemainingPieces( ChessGamePositionImpl pretendedPosition,
														ChessGamePositionImpl detectedPosition,
														ChessFigurePatternSet ps )
	{
		List<BoardBoxResult> result = new ArrayList<>();

		for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
			for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
			{
				// TODO: check if valid the commentation of the following two lines
//				Character detectedPiece = pieceAt(detectedPosition, jj, ii );
//				if( ( detectedPiece == null ) )  // square not detected or empty
				{
					CorrelationResult cr = ( detectedPosition == null ) ?
											null :
											detectedPosition.getCorrelationResultAtPosition( jj, ii );

					Character pretendedPieceCode = getPieceCode( pretendedPosition, jj, ii );
					Character detectedPieceCode = getPieceCode( detectedPosition, jj, ii );

					boolean hasToAdd = ( cr == null ); // if not detected (even with empty board boxes)
					if( ( cr != null ) && ! Objects.equals( pretendedPieceCode, detectedPieceCode ) )
					{
				// TODO: check if valid the commentation of the following lines
//						if( existsPattern( pretendedPieceCode, jj, ii, ps ) )
//							throw( new RuntimeException( getInternationalString(
//															CONF_NON_MATCHING_PATTERN_OF_EXPECTED_TYPE_EXISTED ) ) );

						hasToAdd = true;
					}

					if( hasToAdd ) 
					{
						result.add( new BoardBoxResult( jj, ii, pretendedPieceCode ) );
					}
				}
			}

		return( result );
	}

	protected Character getPieceCode( ChessGamePositionImpl position, int col, int row )
	{
		Character result = null;

		if( position != null )
			result = position.getCharacterAtPosition(col, row);

		return( result );
	}

	protected boolean existsPattern( Character pretendedPieceCode, int col,
									int row, ChessFigurePatternSet ps )
	{
		boolean result = false;
		String patternCode = RecognitionUtils.instance().getPatternCode(pretendedPieceCode, col, row);

		List<ChessFigurePattern> list = ps.getListOfPatternsByType( patternCode.substring(0,1) );
		for( ChessFigurePattern pattern: list )
			if( pattern.getName().startsWith( patternCode ) )
			{
				result = true;
				break;
			}

		return( result );
	}
/*
	protected Character pieceAt( ChessGamePositionImpl position, int jj, int ii )
	{
		Character result = null;

		if( position != null )
			result = position.getCharacterAtPosition(jj, ii);

		return( result );
	}
*/
	protected void updateSuccessfulDetection( ChessGamePositionImpl positionDetected,
												ChessFigurePatternSet ps,
												InputImage image, ChessBoardGridResult grid )
	{
		updateFenDetected( positionDetected );
		ps.addGrid( getImageSize( image ), grid);
	}

	protected Dimension getImageSize( InputImage image )
	{
		return( RecognitionUtils.instance().getImageSize(image) );
	}

	protected void updateFenDetected( ChessGamePositionImpl positionDetected )
	{
		if( positionDetected != null )
		{
			String fenDetected = positionDetected.getFenBoardStringBase();

			for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
				for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
				{
					CorrelationResult cr = positionDetected.getCorrelationResultAtPosition(jj, ii);
					if( cr != null )
					{
						ChessFigurePattern pattern = cr.getPattern();
						if( pattern != null )
							pattern.addFenOk(fenDetected);
					}
				}
		}
	}

	protected ChessRecognizerApplicationConfiguration getAppliConf()
	{
		return( _appliConf );
	}

	protected ChessGamePositionImpl detectPosition( InputImage image,
		ChessBoardRecognitionStore store )
	{
		_positionDetector = new ChessBoardPositionRecognizerWithStoreImpl();
		_positionDetector.init(store);

		String fen = _positionDetector.recognizeBoardFen(image);

		return( _positionDetector.getPositionDetected() );
	}

	protected ChessGamePositionImpl detectPosition( ChessFigurePatternSet ps, ChessBoardGridResult grid )
	{
		return( _positionDetector.detectPosition(ps, grid) );
	}

	protected boolean matches( ChessGamePositionImpl detectedPosition,
								String fen )
	{
		boolean result = false;
		if( detectedPosition != null )
		{
			ChessGamePositionImpl pos2 = new ChessGamePositionImpl();

			String fenPositionBase = pos2.getFenPositionBase( fen );
			pos2.setFenPositionBase(fenPositionBase);

			break1:
			for( int jj=1; jj<=NUM_OF_COLUMNS; jj++ )
				for( int ii=1; ii<=NUM_OF_ROWS; ii++ )
				{
					CorrelationResult cr = detectedPosition.getCorrelationResultAtPosition(ii, ii);
					Character detectedPiece = detectedPosition.getCharacterAtPosition(jj, ii);
					Character expectedPiece = pos2.getCharacterAtPosition(jj, ii);
					if( ( cr != null ) &&
						( !Objects.equals(detectedPiece, expectedPiece ) )
						)
					{
						result = false;
						break break1;
					}
					else
						result = true;
				}
		}

		return( result );
	}

	protected void registerInternationalizedStrings()
	{
		this.registerInternationalString(CONF_CANNOT_MATCH_ALL_PATTERNS, "Error! cannot match all patterns" );
		this.registerInternationalString(CONF_CANNOT_MATCH_ALL_PATTERNS_EMPTY_BOARD_BOX_PATTERN_DOES_NOT_MATCH,
							"Error! cannot match all patterns (empty board box pattern does not match)" );
		this.registerInternationalString(CONF_NON_MATCHING_PATTERN_OF_EXPECTED_TYPE_EXISTED,
							"Non matching pattern of expected type existed previously" );
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
