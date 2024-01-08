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
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.clipboard.SystemClipboard;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Point;
import javax.swing.JPanel;

/**
 *
 * @author Usuario
 */
public class GlobalCoordinatesInDrag
{
	protected Point _dragPosition = new Point( 0, 0 );		// position at the screen.

	protected boolean _inDrag = false;
	protected ChessPiece _draggedPiece = null;

	protected PiecesToChoosePanel _piecesToChoosePanel = null;
	protected InitialPositionBoardPanel _initialPositionBoardPanel = null;

	protected InitialPositionDialog _parent = null;

	public GlobalCoordinatesInDrag( InitialPositionDialog parent )
	{
		_parent = parent;
	}

	public void setPiecesToChoosePanel( PiecesToChoosePanel ptcp )
	{
		_piecesToChoosePanel = ptcp;
	}

	public void setInitialPositionBoardPanel( InitialPositionBoardPanel ipbp )
	{
		_initialPositionBoardPanel = ipbp;
	}

	// position at the screen
	public void setNewDragPosition( Point newDragPosition )
	{
		_dragPosition = newDragPosition;
	}

	// returns the position inside the component
	public Point getDragPosition( JPanel requestor )
	{
		Point result = null;

		if( _parent.isVisible() )
		{
			result = _dragPosition;
			if( result != null )
			{
				Point leftUpperCorner = requestor.getLocationOnScreen();

				result = new Point( (int) (result.getX() - leftUpperCorner.getX()),
									(int) (result.getY() - leftUpperCorner.getY()) );
			}
		}

		return( result );
	}

	public void setIsInDrag( boolean isInDrag )
	{
		_inDrag = isInDrag;
		
		if( ! isInDrag )
		{
			_draggedPiece = null;
			_dragPosition = null;
		}
	}

	public boolean isInDrag()
	{
		return( _inDrag );
	}

	public ChessPiece getDraggedPiece()
	{
		return( _draggedPiece );
	}

	public void setDraggedPiece( ChessPiece cp )
	{
		_draggedPiece = cp;
	}

	public boolean dragIsInsideSightOfInitialPositionBoardPanel()
	{
		return( dragIsInsidePanel( _initialPositionBoardPanel ) );
	}

	public boolean dragIsInsideOfPiecesToChoosePanel()
	{
		return( dragIsInsidePanel( _piecesToChoosePanel ) );
	}
	
	protected boolean dragIsInsidePanel( JPanel panel )
	{
		Point position = getDragPosition( panel );
		double squareWidthHalfs = getSquareWidth() / 2;

		return( ( position.getY() > -squareWidthHalfs ) &&
				( position.getY() < ( panel.getHeight() + squareWidthHalfs ) ) &&
				( position.getX() > -squareWidthHalfs ) &&
				( position.getX() < ( panel.getWidth() + squareWidthHalfs ) )  );
	}

	public int getSquareWidth()
	{
		return( _initialPositionBoardPanel.getCurrentSquareWidth()  );
	}

	public int getPieceWidth()
	{
		return( _initialPositionBoardPanel.getCurrentPieceWidth()  );
	}

	public void repaint( boolean forced )
	{
		if( forced || !isInDrag() || dragIsInsidePanel( _initialPositionBoardPanel ) )
			_initialPositionBoardPanel.repaint();

		if( forced || !isInDrag() || dragIsInsidePanel( _piecesToChoosePanel ) )
			_piecesToChoosePanel.repaint();
	}

	public ChessBoard getChessBoard()
	{
		return( _initialPositionBoardPanel.getChessBoard() );
	}

	public void removeDraggedPiece()
	{
		if( _draggedPiece != null )
			_initialPositionBoardPanel.doTasksToSetPiece(_draggedPiece, -1, -1);
	}

	public void setPosition( ChessGamePosition cgp )
	{
		try
		{
			if( ( cgp.getIsWhitesTurn() == null ) || cgp.getIsWhitesTurn() )
				_piecesToChoosePanel.setWhiteToPlay();
			else
				_piecesToChoosePanel.setBlackToPlay();

			getChessBoard().setPosition( cgp );
			boolean forced = true;
			repaint(forced);
			newChangeInPosition();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( _parent, th.getMessage(),
											_parent.getInternationalString( InitialPositionDialog.CONF_INTERNAL_ERROR ),
												DialogsWrapper.ERROR_MESSAGE );
		}
	}

	public void setWhitePlaysFromBottom( boolean value )
	{
		_initialPositionBoardPanel.setWhitePlaysFromTheBottom(value);
	}
	
	public void mouseReleased()
	{
		_initialPositionBoardPanel.mouseReleased();
	}

	public void pasteFenPosition()
	{
		ChessGamePosition cgp = null;
		try
		{
			String fen = SystemClipboard.instance().getClipboardContents();
			cgp = new ChessGamePosition();
			ChessGamePosition cgpFinal = cgp;
			Exception ex = ExecutionFunctions.instance().safeMethodExecution( () -> cgpFinal.setFenPosition( fen ) );

			if( ex != null )
			{
				String fenPositionBase = fen.split( "\\s" )[0];
				cgp.setFenPositionBase( fenPositionBase );
			}
		}
		catch( Throwable th )
		{
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( _parent, th.getMessage(),
											_parent.getInternationalString( InitialPositionDialog.CONF_PROBLEM_GETTING_FEN ),
												DialogsWrapper.ERROR_MESSAGE );
		}

		if( cgp != null )
			setPosition( cgp );
	}

	public void newChangeInPosition()
	{
		_parent.newChangeInPosition();
	}

	public void flipBoard()
	{
		try
		{
			boolean throwException = false;
			ChessGamePosition cgp = _parent.createChessGamePosition(throwException);

			cgp.flipBoard();
			_initialPositionBoardPanel.getChessBoard().setPosition( cgp );
			_initialPositionBoardPanel.flipUnrecognizedPositions();

			newChangeInPosition();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
}
