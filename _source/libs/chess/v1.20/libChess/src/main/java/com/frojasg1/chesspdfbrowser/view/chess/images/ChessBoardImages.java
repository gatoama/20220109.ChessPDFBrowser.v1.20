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
package com.frojasg1.chesspdfbrowser.view.chess.images;

import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedListener;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedObserved;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author Usuario
 */
public class ChessBoardImages implements FigureSetChangedListener
{
//	protected static final String _resourcePath = "com/frojasg1/chesspdfbrowser/resources/chess/images";

	protected String _resourcePath = "com/frojasg1/chesspdfbrowser/resources/chess/images/virtualpieces";

//	protected static final int TRANSPARENT_COLOR = 0xFFB0B0B0;
//	protected int _transparentColor = 0x00000000;

	protected Map< String, BufferedImage > _originalPiecesMap;
	protected Map< Integer, Map< String, BufferedImage > > _resizedPiecesMap;
	protected Map< Integer, Map< String, BufferedImage > > _resizedSemiTransparentPiecesMap;

	protected BufferedImage[] _originalSquaresArray;
	protected Map< Integer, BufferedImage[] > _resizedSquaresMap;

	protected int _currentSquareWidth = -1;
	protected int _currentPieceWidth = -1;

	protected FigureSetChangedObserved _figureSetChangedObserved;

	protected static ChessBoardImages _instance = null;

	public static ChessBoardImages instance()
	{
		if( _instance == null )
		{
			try
			{
				_instance = new ChessBoardImages();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				throw( new RuntimeException( "Error loading the images" ) );
			}
		}

		return( _instance );
	}

	protected ChessBoardImages() throws IOException
	{
		// default constructor
		// initialize();
	}

	protected <KK, CC> Map<KK, CC> createMap()
	{
		return( new HashMap<>() );
	}

	protected String getResourceName( String shortResourceName )
	{
		return( _resourcePath + "/" + shortResourceName );
	}

	public String getSimpleResourceName( ChessFigure chessFigure )
	{
		String result = null;
		if( chessFigure != null )
			result = chessFigure.getSimpleResourceName();

		return( result );
	}

	protected String getResourceName( ChessFigure chessFigure )
	{
		return( getResourceName( getSimpleResourceName( chessFigure ) ) );
	}

	public boolean isInitialized()
	{
		return( _resourcePath != null );
	}

	public String getResourceNameForWhitePawn()
	{
		return( getResourceName( ChessFigure.WHITE_PAWN ) );
	}

	public String getResourceNameForWhiteRook()
	{
		return( getResourceName( ChessFigure.WHITE_ROOK ) );
	}

	public String getResourceNameForWhiteKnight()
	{
		return( getResourceName( ChessFigure.WHITE_KNIGHT ) );
	}

	public String getResourceNameForWhiteBishop()
	{
		return( getResourceName( ChessFigure.WHITE_BISHOP ) );
	}

	public String getResourceNameForWhiteQueen()
	{
		return( getResourceName( ChessFigure.WHITE_QUEEN ) );
	}

	public String getResourceNameForWhiteKing()
	{
		return( getResourceName( ChessFigure.WHITE_KING ) );
	}

	public String getResourceNameForBlackPawn()
	{
		return( getResourceName( ChessFigure.BLACK_PAWN ) );
	}

	public String getResourceNameForBlackRook()
	{
		return( getResourceName( ChessFigure.BLACK_ROOK ) );
	}

	public String getResourceNameForBlackKnight()
	{
		return( getResourceName( ChessFigure.BLACK_KNIGHT ) );
	}

	public String getResourceNameForBlackBishop()
	{
		return( getResourceName( ChessFigure.BLACK_BISHOP ) );
	}

	public String getResourceNameForBlackQueen()
	{
		return( getResourceName( ChessFigure.BLACK_QUEEN ) );
	}

	public String getResourceNameForBlackKing()
	{
		return( getResourceName( ChessFigure.BLACK_KING ) );
	}

	public synchronized void initialize(FigureSet figureSet) throws IOException
	{
		initialize( figureSet.getResourcePath() );
	}

	protected synchronized void initialize(String resourcePath) throws IOException
	{
		_originalPiecesMap = new HashMap< String, BufferedImage >();
		_originalSquaresArray = new BufferedImage[2];

		_resourcePath = resourcePath;

//		int transparentColor = 0xFFB0B0B0;
		Integer transparentColor = null;
/*
		insertNewOriginalPiece( ChessPiece.getPieceCode(Pawn.PIECE_TYPE_CODE, ChessPiece.WHITE ), getResourceNameForWhitePawn(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Knight.PIECE_TYPE_CODE, ChessPiece.WHITE ), getResourceNameForWhiteKnight(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Bishop.PIECE_TYPE_CODE, ChessPiece.WHITE ), getResourceNameForWhiteBishop(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Rook.PIECE_TYPE_CODE, ChessPiece.WHITE ), getResourceNameForWhiteRook(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Queen.PIECE_TYPE_CODE, ChessPiece.WHITE ), getResourceNameForWhiteQueen(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(King.PIECE_TYPE_CODE, ChessPiece.WHITE ), getResourceNameForWhiteKing(), transparentColor );

		insertNewOriginalPiece( ChessPiece.getPieceCode(Pawn.PIECE_TYPE_CODE, ChessPiece.BLACK ), getResourceNameForBlackPawn(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Knight.PIECE_TYPE_CODE, ChessPiece.BLACK ), getResourceNameForBlackKnight(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Bishop.PIECE_TYPE_CODE, ChessPiece.BLACK ), getResourceNameForBlackBishop(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Rook.PIECE_TYPE_CODE, ChessPiece.BLACK ), getResourceNameForBlackRook(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(Queen.PIECE_TYPE_CODE, ChessPiece.BLACK ), getResourceNameForBlackQueen(), transparentColor );
		insertNewOriginalPiece( ChessPiece.getPieceCode(King.PIECE_TYPE_CODE, ChessPiece.BLACK ), getResourceNameForBlackKing(), transparentColor );
*/
		for( ChessFigure cf: ChessFigure.values() )
			insertNewOriginalPiece( cf, transparentColor );

		_originalSquaresArray[0] = getResourceImage( getResourceName( "BlackSquare.png" ) );
		_originalSquaresArray[1] = getResourceImage( getResourceName( "WhiteSquare.png" ) );

		initResizedMaps();

		resizeSquares( _originalSquaresArray[0].getWidth() );
	}

	protected void initResizedMaps()
	{
		_resizedSquaresMap = new HashMap< Integer, BufferedImage[] >();
		_resizedPiecesMap = new HashMap< Integer, Map< String, BufferedImage > >();
		_resizedSemiTransparentPiecesMap = new HashMap< Integer, Map< String, BufferedImage > >();
	}

	public static BufferedImage getResourceImage( String resourceImageName ) throws IOException
	{
		return( ImageFunctions.instance().getResourceImage( resourceImageName ) );
	}

	protected void insertNewOriginalPiece( ChessFigure chessFigure, Integer transparentColor ) throws IOException
	{
		insertNewOriginalPiece( chessFigure.getPieceCode(), getResourceName(chessFigure), transparentColor );
	}

	protected void insertNewOriginalPiece( String pieceCode, String resourceImageName, Integer transparentColor ) throws IOException
	{
		BufferedImage bi = getResourceImage( resourceImageName );
		
		BufferedImage imageWithTransparentColor = ImageFunctions.instance().resizeImage( bi, bi.getWidth(), bi.getHeight(), transparentColor, null, null );

		_originalPiecesMap.put( pieceCode, imageWithTransparentColor );
	}
/*
	protected void resizeSquaresAndPieces( int newSquareWidth, int newPieceWidth )
	{
		if( newSquareWidth != _currentSquareWidth )
		{
			resizeSquares( newSquareWidth );
			_currentSquareWidth = newSquareWidth;
		}
		
		if( newPieceWidth != _currentPieceWidth )
		{
			_resizedPiecesMap = resizePieces( newPieceWidth, 0 );
			_resizedSemiTransparentPiecesMap =  resizePieces( newPieceWidth, 200 );
			_currentPieceWidth = newPieceWidth;
		}
	}
*/
	protected BufferedImage[] resizeSquares( int newSquareWidth )
	{
		BufferedImage[] result = new BufferedImage[2];
		result[ ChessPiece.WHITE ] = ImageFunctions.instance().resizeSquaredImage(_originalSquaresArray[ChessPiece.WHITE],
																		newSquareWidth, null, null, 255 );
		result[ ChessPiece.BLACK ] = ImageFunctions.instance().resizeSquaredImage(_originalSquaresArray[ChessPiece.BLACK],
																		newSquareWidth, null, null, 255 );
		return( result );
	}

	protected Map<String, BufferedImage> resizePieces( int newPieceWidth, Integer alpha )
	{		
		Map<String, BufferedImage> newMap = new HashMap<String, BufferedImage>();

		Iterator< Map.Entry<String, BufferedImage> > it = _originalPiecesMap.entrySet().iterator();
		
		while( it.hasNext() )
		{
			Map.Entry<String, BufferedImage> pair = it.next();
			
			String key = pair.getKey();
			BufferedImage originalImage = pair.getValue();

			int transparentColor = originalImage.getRGB(0, 0);
			BufferedImage transformedImage = ImageFunctions.instance().resizeSquaredImage( originalImage, newPieceWidth, transparentColor, 0x00F8E2E2, alpha );
			
			newMap.put( key, transformedImage );
		}

		return( newMap );
	}

	protected BufferedImage resizePiece( String pieceCode, int newPieceWidth, Integer alpha )
	{		
		BufferedImage originalImage = _originalPiecesMap.get( pieceCode );

		int transparentColor = originalImage.getRGB(0, 0);
		BufferedImage transformedImage = ImageFunctions.instance().resizeSquaredImage( originalImage, newPieceWidth, transparentColor, 0x00F8E2E2, alpha );
		return( transformedImage );
	}

	protected BufferedImage[] getSquareImages( int squareWidth )
	{
		BufferedImage[] result = _resizedSquaresMap.get( squareWidth );

		if( result == null )
		{
			result = resizeSquares( squareWidth );
			_resizedSquaresMap.put( squareWidth, result );
		}

		return( result );
	}


	/**
	 * It returns the transformed image for an Square.
	 * 
	 * @param color     0: BLACK, 1: WHITE
	 * @return 
	 */
	public synchronized BufferedImage getSquareImage( int color, int squareWidth )
	{
//		System.out.println( "_resizedSquaresMap is " + ( _resizedSquaresMap != null ? "NOT" : "" ) + " NULL.   color=" + color );
		BufferedImage bi = null;

		if( squareWidth > 0 )
		{
			BufferedImage[] tmpArray = getSquareImages( squareWidth );

			bi = tmpArray[ color ];
		}

		return( bi );
	}

	public synchronized BufferedImage getPieceImage( String pieceCode, int pieceWidth )
	{
		BufferedImage result = null;
		if( ( pieceCode != null ) && ( pieceWidth > 0 ) )
		{
			if( (pieceCode.length()==3) && (pieceCode.charAt(0)=='O' ) )
				result = _originalPiecesMap.get( pieceCode.substring( 1, 3 ) );
			else
			{
				Map< String, BufferedImage > tmpMap = getSizeNormalPiecesMap( pieceWidth );
				result = tmpMap.get( pieceCode );
			}
		}
		return( result );
	}

	protected synchronized Map< String, BufferedImage > getSizePiecesMap( Map< Integer, Map< String, BufferedImage > > totalMap,
																		int pieceWidth, int alpha )
	{
		Map< String, BufferedImage > result = totalMap.get( pieceWidth );

		if( result == null )
		{
			result = resizePieces( pieceWidth, alpha );
			totalMap.put( pieceWidth, result );
		}

		return( result );
	}

	protected synchronized Map< String, BufferedImage > getSizeNormalPiecesMap( int pieceWidth )
	{
		int alpha = 255;
		return( getSizePiecesMap( _resizedPiecesMap, pieceWidth, alpha ) );
	}

	protected synchronized Map< String, BufferedImage > getSizeSemiTransparentPiecesMap( int pieceWidth )
	{
		int alpha = 60;
		return( getSizePiecesMap( _resizedSemiTransparentPiecesMap, pieceWidth, alpha ) );
	}

	public synchronized void clearAllResized()
	{
		_resizedSquaresMap.clear();
		_resizedPiecesMap.clear();
		_resizedSemiTransparentPiecesMap.clear();
	}

	public BufferedImage getSemiTransparentPieceImage( String pieceCode, int pieceWidth )
	{
		BufferedImage result = null;
		if( ( pieceCode != null ) && ( pieceWidth > 0 ) )
		{
			Map< String, BufferedImage > tmpMap = getSizeSemiTransparentPiecesMap( pieceWidth );
			result = tmpMap.get( pieceCode );
		}
		return( result );
	}

	public void registerToFigureSetChangedObserved( FigureSetChangedObserved figureSetChangedObserved )
	{
		unregisterToFigureSetChangedObserved();
		_figureSetChangedObserved = figureSetChangedObserved;
		_figureSetChangedObserved.addFigureSetChangedListener(this);
	}

	public void unregisterToFigureSetChangedObserved()
	{
		if( _figureSetChangedObserved != null )
			_figureSetChangedObserved.removeFigureSetChangedListener(this);
	}

	@Override
	public void figureSetChanged(FigureSetChangedObserved observed, FigureSet oldValue, FigureSet newFigureSet) {
		ExecutionFunctions.instance().safeMethodExecution( () -> initialize( newFigureSet ) );
	}

	public static void main( String[] args )
	{
		try
		{
			ChessBoardImages cbi = new ChessBoardImages();
//			cbi.resizeSquaresAndPieces(240, 135);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}
}
