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
package com.frojasg1.chesspdfbrowser.view.chess.initialposition;

import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGameResult;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.King;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Pawn;
import com.frojasg1.chesspdfbrowser.view.chess.ChessBoardPanel;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.structures.Pair;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InitialPositionBoardPanel extends ChessBoardPanel
{
	protected GlobalCoordinatesInDrag _globalCoordinatesInDrag = null;
	
	protected Set<Pair<Integer, Integer>> _unrecognizedPositions = new HashSet<>();

	public InitialPositionBoardPanel( GlobalCoordinatesInDrag gcid ) throws IOException
	{
		_globalCoordinatesInDrag = gcid;
		_globalCoordinatesInDrag.setInitialPositionBoardPanel(this);
		
		ChessBoard cb = new ChessBoard( );
		
		try
		{
			cb.setInitialPosition();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		setChessBoard( cb );
	}

	public void clearUnrecognizedPositions()
	{
		_unrecognizedPositions.clear();
	}

	public void flipUnrecognizedPositions()
	{
		Set<Pair<Integer, Integer>> oldSet = _unrecognizedPositions;

		_unrecognizedPositions = new HashSet<>();
		for( Pair<Integer, Integer> pair: oldSet)
			_unrecognizedPositions.add( new Pair( 9 - pair.getKey(), 9 - pair.getValue() ) );
	}

	public void addUnrecognizedPosition( int col, int row )
	{
		_unrecognizedPositions.add( new Pair( col, row ) );
	}

	@Override
	protected void makeNewMove( ChessGameMove cgm )
	{
		if( _chessBoard != null )
		{
			ChessPiece cp = null;

			try
			{
				cp = _chessBoard.getPiece( cgm._column1, cgm._row1 );
				doTasksToSetPiece( cp, cgm._column2, cgm._row2 );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	protected void doTasksToSetPiece( ChessPiece cp, int column, int row )
	{
		try
		{
			if( cp != null )
			{
				ChessPiece destinationPiece = null;
				try
				{
					destinationPiece = _chessBoard.getPiece( column, row );
				}
				catch( Throwable th )
				{
				}

				ChessPiece cp2 = null;
				
				try
				{
					cp2 = _chessBoard.getPiece( cp.getColumn(), cp.getRow() );
				}
				catch( Throwable th )
				{
				}

				// we cannot remove a king if even though it is dragged outside the board
				if( ( cp2 instanceof King ) && ( ( column < 1 ) || ( row < 1 ) ) )
					return;

				if( ( cp instanceof Pawn ) && ( ( row == 1 ) || ( row == 8 ) ) )// we do not allow Pawns neither in the first row nor in the last.
					return;

				if( destinationPiece != null )
				{
					// if the just obtained piece is a King, we cannot remove it
					if( destinationPiece instanceof King )
					{
						return;
					}
					_chessBoard.removeCapturedPiece( destinationPiece );
				}

				if( ( cp2 != null ) && ( cp2.getColumn() > 0 ) && ( cp2.getRow() > 0 ) )
					_chessBoard.removeCapturedPiece( cp2 );

				if( ( column > 0 ) && ( row > 0 ) )
				{
					cp.setColumnAndRow( column, row );
					_chessBoard.restoreCapturedPiece(cp);
				}
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
/*
		if( _inDrag )
		{
			_inDrag = false;
			repaint();
		}
*/
	}

	@Override
	public void mousePressed(MouseEvent e)
	{	
		if( e.getButton() == MouseEvent.BUTTON1 ) 
		{
			calculateCurrentCursorColAndRow( e.getX(), e.getY() );
			ChessPiece cp = isInPositionToDrag( e.getX(), e.getY(), true );
			if( cp != null )
			{
				_globalCoordinatesInDrag.setNewDragPosition( e.getLocationOnScreen() );
				_globalCoordinatesInDrag.setDraggedPiece(cp);

				_startDragColumn = _currentCursorColumn;
				_startDragRow = _currentCursorRow;
				_globalCoordinatesInDrag.setIsInDrag( true );

				setCursor(CursorFunctions._handCursor );
				
				repaint();
			}
		}
	}

	protected boolean thereAnyPieceLikeThatOneOnBoard( ChessPiece cp )
	{
		boolean result = false;

		for( int column = 1; column <= ChessBoard.NUM_OF_COLUMNS; column++ )
			for( int row = 1; row <= ChessBoard.NUM_OF_ROWS; row++ )
			{
				try
				{
					ChessPiece piece = _chessBoard.getPiece( column, row );
					if( cp.getClass().isInstance(piece) && cp.getColor() == piece.getColor() )
					{
						result = true;
						break;
					}	
				}
				catch( Throwable th )
				{
				}
			}

		return( result );
	}

	protected boolean canAddPiece( ChessPiece cp )
	{
		boolean result = ( cp != null );
		if( result )
		{
			if( cp instanceof King )
			{
				result = ! thereAnyPieceLikeThatOneOnBoard( cp );
			}
		}

		return( result );
	}

	public void mouseReleased()
	{
		try
		{
			Point dragPoint = _globalCoordinatesInDrag.getDragPosition(this);

			if( dragPoint != null )
			{
				calculateCurrentCursorColAndRow( (int) dragPoint.getX(), (int) dragPoint.getY() );
				ChessPiece cp = _globalCoordinatesInDrag.getDraggedPiece();

				if( cp != null )
				{
					if( ( cp.getColumn() != _currentCursorColumn ) ||
						( cp.getRow() != _currentCursorRow )
					  )
					{
						ChessGameMove cgm = new ChessGameMove( cp.getColumn(), cp.getRow(), _currentCursorColumn, _currentCursorRow );

						if( ( cp.getColumn() > 0 ) && ( cp.getRow() > 0 ) )
							makeNewMove( cgm );
						else if( canAddPiece( cp ) )
							doTasksToSetPiece( cp, _currentCursorColumn, _currentCursorRow );

						_globalCoordinatesInDrag.newChangeInPosition();
					}
				}
			}
			setCursor(CursorFunctions._defaultCursor );
		}
		finally
		{
			_globalCoordinatesInDrag.setIsInDrag( false );
			_globalCoordinatesInDrag.repaint( true );
		}
	}

	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if( e.getButton() == MouseEvent.BUTTON1 )
		{
			_globalCoordinatesInDrag.setNewDragPosition( e.getLocationOnScreen() );

			_globalCoordinatesInDrag.mouseReleased();
		}
	}

	@Override
	public void mouseMoved( MouseEvent evt )
	{
		if( evt.getComponent() == this )
		{
			boolean hasChanged = calculateCurrentCursorColAndRow( evt.getX(), evt.getY() );
			if( setAppropriateCursor( evt, hasChanged ) || hasChanged )
			{
				repaint();
			}
		}
	}

	@Override
	public void mouseDragged( MouseEvent evt )
	{
		_globalCoordinatesInDrag.setNewDragPosition( evt.getLocationOnScreen() );

		_currentX = evt.getX();
		_currentY = evt.getY();

		calculateCurrentCursorColAndRow( _currentX, _currentY );
		if( _globalCoordinatesInDrag.isInDrag() )
		{
			_globalCoordinatesInDrag.repaint( false );
		}
	}

	@Override
	protected ChessPiece isInPositionToDrag( int xx, int yy, boolean hasChangedSquare )
	{
		ChessPiece result = null;

		xx = xx - _initialX_ofBoard;
		yy = yy - _initialX_ofBoard;

		if( isPositionInsideTheBoard( _currentCursorColumn, _currentCursorRow ) )
		{
			if( ( _chessBoard != null ) && (_chessBoard.getChessGameResult() == ChessGameResult.GAME_CONTINUES ) )
			{
				ChessPiece cp = null;
				boolean canMove = false;

				try
				{
					cp = _chessBoard.getPiece( _currentCursorColumn, _currentCursorRow );
//					canMove = (cp != null) && (cp.getInvertibleColor() == _chessBoard.getColorToPlay() );
					canMove = (cp != null);
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}

//				if( canMove && ( cp.getListOfLegalMoves().size() > 0 ) )
				if( canMove )
				{
					int xInsideSquare = ( xx % _currentSquareWidth );
					int yInsideSquare = ( yy % _currentSquareWidth );

					if( ( xInsideSquare >= (0.15d*_currentSquareWidth) ) &&
						( xInsideSquare <= (0.85d*_currentSquareWidth) ) &&
						( yInsideSquare >= (0.15d*_currentSquareWidth) ) &&
						( yInsideSquare <= (0.85d*_currentSquareWidth) ) )
					{
						result = cp;
					}
				}
			}
		}

		return( result );
	}

	@Override
	public boolean setAppropriateCursor( MouseEvent evt, boolean hasChangedSquare )
	{
		boolean hasChanged = false;

		Cursor cursor = CursorFunctions._defaultCursor;

		if( isInPositionToDrag( evt.getX(), evt.getY(), hasChangedSquare ) != null )
		{
			cursor = CursorFunctions._handCursor;
		}

		hasChanged = !getCursor().equals( cursor );
		setCursor( cursor );

		return( hasChanged );
	}

	@Override
	protected void paintChessBoardPosition( Graphics gc )
	{
		BufferedImage bi = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB  );
		Graphics gc1 = bi.getGraphics();

//		Color color = new Color( 0xFFb0b0b0 );
		Color color = getInvertibleColor( INVERTIBLE_GREY_INDEX );
		gc1.setColor(color);
		gc1.fillRect( 0, 0, getWidth(), getHeight() );

		if( _chessBoardImagesCache.isInitialized() )
		{
			paintBoard( gc1 );
			writeColumnLettersAndRowNumbers( gc1 );

			if( _chessBoard != null )
			{
	//			markOriginOfMoveAndPossibleDestinations( gc1 );
	//			paintTurn( gc1 );
	//			paintResult( gc1 );

				paintUnrecognizedPositions( gc1, _unrecognizedPositions );

				showColAndRow( gc1 );

				paintPieces( gc1 );
			}
			else
			{
				writeInitialPositionNotSet( gc1 );
			}
		}

		gc.drawImage( bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null );
	}

	protected void paintUnrecognizedPositions( Graphics gc, Set<Pair<Integer, Integer>> set )
	{
		for( Pair<Integer, Integer> pair: set )
		{
			int col = pair.getKey();
			int row = pair.getValue();
			putSquaredFrame( gc, col, row, Color.RED );
		}
	}


	@Override
	protected void paintPieceInDrag( Graphics gc )
	{
		if( _globalCoordinatesInDrag.isInDrag() )
		{
			try
			{
				ChessPiece cp = _globalCoordinatesInDrag.getDraggedPiece();
				Point currentDragPosition = _globalCoordinatesInDrag.getDragPosition(this);

				if( cp != null )
				{
					Point upperLeftCorner = new Point(	(int) currentDragPosition.getX() - _currentPieceWidth/2,
														(int) currentDragPosition.getY() - _currentPieceWidth/2 );
					BufferedImage bi = _chessBoardImagesCache.getPieceImage( cp.getPieceCode(), _currentPieceWidth );
					ImageFunctions.instance().paintClippedImage(this, gc, bi, upperLeftCorner);
//					paintPiece( gc, cp, bounds );
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	protected void paintPieces( Graphics gc )
	{
		if( _chessBoard != null )
		{
			for( int col=1; col<=ChessBoard.NUM_OF_COLUMNS; col++ )
				for( int row=1; row<=ChessBoard.NUM_OF_ROWS; row++ )
				{
					ChessPiece draggedPiece = _globalCoordinatesInDrag.getDraggedPiece();
					if( ! _globalCoordinatesInDrag.isInDrag() ||
						( draggedPiece == null ) ||
						(col != draggedPiece.getColumn() ) ||
						(row != draggedPiece.getRow() ) )
					{
						try
						{
							ChessPiece cp = _chessBoard.getPiece(col, row);

							if( cp != null )
							{
								Point upperLeftCorner = getUpperLeftCornerOfPieceByColRow( col, row );
								BufferedImage bi = _chessBoardImagesCache.getPieceImage( cp.getPieceCode(), _currentPieceWidth );
								ImageFunctions.instance().paintClippedImage(this, gc, bi, upperLeftCorner);
//								paintPiece( gc, cp, bounds );
							}
						}
						catch( Throwable th )
						{
							th.printStackTrace();
						}
					}
				}

			paintPieceInDrag( gc );
		}
	}

}
