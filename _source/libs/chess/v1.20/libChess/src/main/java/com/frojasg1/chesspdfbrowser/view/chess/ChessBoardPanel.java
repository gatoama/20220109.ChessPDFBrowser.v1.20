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
package com.frojasg1.chesspdfbrowser.view.chess;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGameResult;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessBoardImages;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameViewInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageServerInterface;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedListener;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedObserved;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessMoveGenerator;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessBoardImagesCache;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.image.ImageUtilFunctions;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class ChessBoardPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase implements ComponentListener, MouseListener,
														MouseMotionListener, ChessGameViewInterface,
														PromotionControllerInterface, ChangeLanguageClientInterface,
														ChessMoveGenerator,
														FigureSetChangedListener
{
	protected static final double HUNDRED_PERCENT_SQUARE_WIDTH = 40D;
	protected static final int HUNDRED_PERCENT_SQUARED_FRAME_THICK = 2;

	protected static final int INVERTIBLE_BLACK_INDEX = 0;
	protected static final int INVERTIBLE_GREY_INDEX = 1;
	protected static final int INVERTIBLE_BRIGHT_GREEN_INDEX = 2;
//	protected static final int INVERTIBLE_RED_INDEX = 3;
	protected static final int INVERTIBLE_WHITE_INDEX = 3;

	protected static final int PUTOUTABLE_ORANGE_INDEX = 0;
	protected static final int PUTOUTABLE_YELLOW_INDEX = 1;
	protected static final int PUTOUTABLE_GREEN_INDEX = 2;
	protected static final int PUTOUTABLE_BLACK_INDEX = 3;
	protected static final int PUTOUTABLE_WHITE_INDEX = 4;

	protected static final Color RED = Color.RED;

	protected Color[] _invertibleColorModeColors = new Color[] {
		Color.BLACK,
		Colors.LIGHT_GREY,
		Colors.BRIGHT_GREEN,
//		Color.RED,
		Color.WHITE
	};

	protected final Color[] _originalPutOutableColorModeColors = new Color[] {
		Color.ORANGE,
		Color.YELLOW,
		Color.GREEN.darker(),
		Color.BLACK,
		Color.WHITE
	};

	protected Color[] _putOutableColorModeColors = _originalPutOutableColorModeColors;


	protected ChangeLanguageServerInterface _changeLanguageServer = null;

	protected ChessGameControllerInterface _listener = null;

	protected ChessBoard _chessBoard = null;
	protected boolean _whitePlaysFromTheBottom = true;

	protected double _fractionOfPieceWidthOverSquareWidth = 0.95d;

	protected int _currentSquareWidth = -1;
	protected int _currentSquareWidthHalves = -1;
	protected int _currentPieceWidth = -1;
	protected int _initialX_ofBoard = -1;
	protected int _initialY_ofBoard = -1;

	protected static final int MIN_SQUARE_WIDTH = 32;

	protected ChessBoardImagesCache _chessBoardImagesCache = null;

	protected String _pieceCodeToShow = null;
	protected boolean _hasToShowPicture = false;

	protected int _currentCursorColumn = -1;
	protected int _currentCursorRow = -1;
	
	protected boolean _inDrag = false;
	protected int _startDragColumn = -1;
	protected int _startDragRow = -1;

	protected boolean _lastIsInPositionToDrag = false;
	protected boolean _lastMouseIsOverAPiece = false;
	
	protected int _currentX = -1;
	protected int _currentY = -1;
	
//	protected static final Cursor _handCursor = new Cursor( Cursor.HAND_CURSOR );
//	protected static final Cursor _waitCursor = new Cursor( Cursor.WAIT_CURSOR );
//	protected static final Cursor _defaultCursor = new Cursor( Cursor.DEFAULT_CURSOR );

	protected PromotionJPanel _promotionJPanel = null;
	protected ChessGameMove _promotionMove = null;

	protected String _whitePlayerName = null;
	protected String _blackPlayerName = null;
	protected String _whiteElo = null;
	protected String _blackElo = null;

	protected String _whiteNameElo = null;
	protected String _blackNameElo = null;

	protected AtomicReference<Font> _arSmallFont = new AtomicReference<>();
	protected AtomicReference<Font> _arMediumFont = new AtomicReference<>();
	protected AtomicReference<Font> _arBigFont = new AtomicReference<>();

	protected MoveTreeNode _currentMoveTreeNode = null;

	protected Integer _remainingSecondsWhite = null;
	protected Integer _remainingSecondsBlack = null;

	protected boolean _isBlocked = false;

	protected FigureSetChangedObserved _figureSetChangedObserved;

	public ChessBoardPanel() throws IOException
	{
		super();

		super.init();

		Dimension msize = new Dimension(	MIN_SQUARE_WIDTH * (ChessBoard.NUM_OF_COLUMNS+2),
										MIN_SQUARE_WIDTH * (ChessBoard.NUM_OF_ROWS+2) );

		Dimension zmsize = ViewFunctions.instance().getNewDimension( msize, 1.5D );

		setMinimumSize( zmsize );
		setLayout(null);
		setOpaque( false );

		_chessBoardImagesCache = new ChessBoardImagesCache();

		_promotionJPanel = new PromotionJPanel( this );
		_promotionJPanel.setVisible( false );

		add( _promotionJPanel );

//		_promotionJPanel.setBounds( 0, 0, 160, 40 );

		registerToChangeLanguageAsObserver( ChessStringsConf.instance() );

		addComponentListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public void setRemainingTime( Integer remainingSecondsWhite,
										Integer remainingSecondsBlack )
	{
		SwingUtilities.invokeLater( () -> {
					_remainingSecondsWhite = remainingSecondsWhite;
					_remainingSecondsBlack = remainingSecondsBlack;

					repaint();
		} );
	}

	protected Color getInvertibleColor( int index )
	{
		return( _invertibleColorModeColors[index] );
	}

	protected Color getPutOutableColor( int index )
	{
		return( _putOutableColorModeColors[index] );
	}

	protected Font getSmallFont()
	{
		return( createSmallFontGeneric( _arSmallFont, _currentSquareWidth * 0.25d, 6, 33 ) );
	}

	protected Font getMediumFont()
	{
		return( createSmallFontGeneric( _arMediumFont, _currentSquareWidth * 0.33d, 6, 50 ) );
	}

	protected Font getBigFont()
	{
		return( createSmallFontGeneric( _arBigFont, _currentSquareWidth * 0.66d, 16, 100 ) );
	}

	protected Font createSmallFontGeneric( AtomicReference<Font> arFont,
											double fontSize,
											int minimum, int maximum )
	{
		int fontSizeI = (int) Math.floor( fontSize );
		fontSizeI = IntegerFunctions.max( fontSizeI, minimum );
		fontSizeI = IntegerFunctions.min( fontSizeI, maximum );

		if( hasToCreateFont(arFont, fontSizeI) )
			arFont.set( new Font( "Lucida", Font.BOLD, fontSizeI ) );

		return( arFont.get() );
	}

	protected boolean hasToCreateFont( AtomicReference<Font> arFont, int fontSize )
	{
		return( (arFont.get()==null) || (arFont.get().getSize()!=fontSize) );
	}

	public String getWhitePlayerName()
	{
		return( _whitePlayerName );
	}

	public void setWhitePlayerName( String name )
	{
		_whiteNameElo = null;
		_whitePlayerName = name;
	}

	public String getBlackPlayerName()
	{
		return( _blackPlayerName );
	}

	public void setBlackPlayerName( String name )
	{
		_blackNameElo = null;
		_blackPlayerName = name;
	}

	public String getWhitePlayerElo()
	{
		return( _whiteElo );
	}

	public void setWhitePlayerElo( String value )
	{
		_whiteNameElo = null;
		_whiteElo = value;
	}

	public String getBlackPlayerElo()
	{
		return( _blackElo );
	}

	public void setBlackPlayerElo( String value )
	{
		_blackNameElo = null;
		_blackElo = value;
	}

	public void setListener( ChessGameControllerInterface listener )
	{
		_listener = listener;
	}

	public void setShowPicture( String pieceCode )
	{
		_pieceCodeToShow = pieceCode;
		_hasToShowPicture = true;
		repaint();
	}

	@Override
	public void setChessBoard( ChessBoard cb )
	{
		synchronized( this )
		{
			_chessBoard = cb;
		}
	}

	public ChessBoard getChessBoard()
	{
		synchronized( this )
		{
			return( _chessBoard );
		}
	}

	public int getCurrentSquareWidth()
	{
		return( _currentSquareWidth );
	}
	
	public int getCurrentPieceWidth()
	{
		return( _currentPieceWidth );
	}
	
	public void setWhitePlaysFromTheBottom( boolean whitePlaysInTheBottom )
	{
		_whitePlaysFromTheBottom = whitePlaysInTheBottom;

		repaint();
	}

	public boolean getWhitePlaysInTheBottom()
	{
		return( _whitePlaysFromTheBottom );
	}

	protected boolean isLegalMovePromotion( ChessGameMove cgm )
	{
		boolean result = false;
		
		if( _chessBoard != null )
		{
			ChessPiece piece = _chessBoard.getPiece_fast( cgm._column1, cgm._row1 );
			if( piece != null )
			{
				result = piece.legalMoveIsPromotion(cgm);
			}
		}

		return( result );
	}
	
	protected void makeVisiblePromotionPanel( Point location )
	{
		Point locationOfQueenButton = _promotionJPanel.getCenterPointOfQueenButton();

		int xx = (int) (location.getX() - locationOfQueenButton.getX());
		int yy = (int) (location.getY() - locationOfQueenButton.getY());

		xx = IntegerFunctions.min( IntegerFunctions.max( 0, xx ), getWidth() - _promotionJPanel.getWidth() );
		yy = IntegerFunctions.min( IntegerFunctions.max( 0, yy ), getHeight() - _promotionJPanel.getHeight() );

		_promotionJPanel.setBounds( xx, yy, (int) _promotionJPanel.getSize().getWidth(), (int) _promotionJPanel.getSize().getHeight() );
		_promotionJPanel.setColor( _chessBoard.getColorToPlay() );

		_promotionJPanel.setVisible( true );
	}

	protected void makeNewMove( ChessGameMove cgm )
	{
//		if( _chessBoard != null )
//		{
//			List<ChessGameMove> list = _chessBoard.getListOfMoves();
//			list.add(cgm);
//			_listener.newPositionInTheMovesTree(list);
//		}

		_listener.newPositionInTheMovesTree( _currentMoveTreeNode, cgm, this );
	}

	
	

	@Override
	public void componentHidden(ComponentEvent e)
	{
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		if( e.getComponent() == this )
		{
			changeCurrentSquareWidth();

			repaint();
		}
	}

	// Five methods from MouseListener:
	/** Called when the mouse has been clicked on a component. */
	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	/** Called when the mouse enters a component. */
	@Override
	public void mouseEntered(MouseEvent e)
	{
		_inDrag = false;
	}

	/** Called when the mouse exits a component. */
	@Override
	public void mouseExited(MouseEvent e)
	{
		if( _inDrag )
		{
			_currentCursorColumn = -1;
			_currentCursorRow = -1;
			_inDrag = false;
			repaint();
		}
	}

	/** Called when the mouse has been pressed. */
	@Override
	public void mousePressed(MouseEvent e)
	{	
		if( ! getCursor().getName().equals(CursorFunctions._waitCursor.getName() ) )	// if we are loading or saving, we must not set the hand cursor.
		{
/*			if( ( e.getButton() == MouseEvent.BUTTON2 ) || ( e.getButton() == MouseEvent.BUTTON3 ) )
			{
				int color = -1;
				if( e.getButton() == MouseEvent.BUTTON2 )		color = ChessPiece.WHITE;
				else if( e.getButton() == MouseEvent.BUTTON3 )	color = ChessPiece.BLACK;

				_promotionJPanel.setColor( color );
				_promotionJPanel.setBounds( e.getX(), e.getY(), (int) _promotionJPanel.getSize().getWidth(), (int) _promotionJPanel.getSize().getHeight() );
				_promotionJPanel.setVisible(true);
				repaint();
			}
			else */
			if( e.getButton() == MouseEvent.BUTTON1 ) 
			{
				calculateCurrentCursorColAndRow( e.getX(), e.getY() );
				if( isInPositionToDrag( e.getX(), e.getY(), true ) != null )
				{
					_currentX = e.getX();
					_currentY = e.getY();
					_startDragColumn = _currentCursorColumn;
					_startDragRow = _currentCursorRow;
					_inDrag = true;

					repaint();
				}
			}
		}
	}

	/** Called when the mouse has been released. */
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if( ! getCursor().getName().equals(CursorFunctions._waitCursor.getName() ) )	// if we are loading or saving, we must not set the hand cursor.
		{
/*			if( ( e.getButton() == MouseEvent.BUTTON2 ) || ( e.getButton() == MouseEvent.BUTTON3 ) )
				_promotionJPanel.setVisible(false);
			else */
			if( e.getButton() == MouseEvent.BUTTON1 )
			{
				_inDrag = false;

				calculateCurrentCursorColAndRow( e.getX(), e.getY() );

				ChessGameMove cgm = new ChessGameMove( _startDragColumn, _startDragRow, _currentCursorColumn, _currentCursorRow );

				try
				{
					cgm.checkIsMoveInsideBoard();  // throws exception if not

					if( (_listener != null) && ( _chessBoard != null ) && _chessBoard.isLegalThisMove( cgm, false ) )
					{
						if( isLegalMovePromotion( cgm ) )
						{
							makeVisiblePromotionPanel( e.getPoint() );
							_promotionMove = cgm;
						}
						else
						{
							makeNewMove( cgm );
						}
					}
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}

				repaint();
			}
		}
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		if( e.getComponent() == this )
		{
			changeCurrentSquareWidth();
		}
	}

	@Override
	public void mouseMoved( MouseEvent evt )
	{
		if( ! getCursor().getName().equals(CursorFunctions._waitCursor.getName() ) )	// if we are loading or saving, we must not set the hand cursor.
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
		else
		{
			
		}
	}

	@Override
	public void mouseDragged( MouseEvent evt )
	{
		if( ! getCursor().getName().equals(CursorFunctions._waitCursor.getName() ) )	// if we are loading or saving, we must not set the hand cursor.
		{
			_currentX = evt.getX();
			_currentY = evt.getY();

			calculateCurrentCursorColAndRow( _currentX, _currentY );
			if( _inDrag )
			{
				repaint();
			}
		}
	}

	public boolean calculateCurrentCursorColAndRow( int currentX, int currentY )
	{
		int previousCursorColumn = _currentCursorColumn;
		int previousCursorRow = _currentCursorRow;

		_currentCursorColumn = -1;
		_currentCursorRow = -1;

		int xx = currentX - _initialX_ofBoard + _currentSquareWidth;
		int yy = currentY - _initialY_ofBoard + _currentSquareWidth;

		int column = xx/_currentSquareWidth - 1;
		int row = yy/_currentSquareWidth - 1;

		if( isPositionInsideTheBoard( column+1, row+1 ) )
		{
			if( _whitePlaysFromTheBottom )
			{
				_currentCursorColumn = column+1;
				_currentCursorRow = ChessBoard.NUM_OF_ROWS - row;
			}
			else
			{
				_currentCursorColumn = ChessBoard.NUM_OF_COLUMNS - column;
				_currentCursorRow = row+1;
			}
		}
		
		boolean result =	( previousCursorColumn != _currentCursorColumn ) ||
							( previousCursorRow != _currentCursorRow );

		return( result );
	}

	protected boolean isBlocked()
	{
		return( _isBlocked );
	}

	public void setBlocked( boolean value )
	{
		_isBlocked = value;
	}

	protected ChessPiece isInPositionToDrag( int xx, int yy, boolean hasChangedSquare )
	{
		ChessPiece result = null;
		
		xx = xx - _initialX_ofBoard;
		yy = yy - _initialY_ofBoard;

		if( isPositionInsideTheBoard( _currentCursorColumn, _currentCursorRow ) )
		{
			if( ( _chessBoard != null ) && !isBlocked() //&&
//				( (_chessBoard.getChessGameResult() == null ) ||
//				  (_chessBoard.getChessGameResult() == ChessGameResult.GAME_CONTINUES )
//				)
			  )
			{
				ChessPiece cp = null;
				boolean canMove = false;

				try
				{
					cp = _chessBoard.getPiece( _currentCursorColumn, _currentCursorRow );
					canMove = (cp != null) && (cp.getColor() == _chessBoard.getColorToPlay() );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}

				int xInsideSquare = ( xx % _currentSquareWidth );
				int yInsideSquare = ( yy % _currentSquareWidth );

				if( ( xInsideSquare >= (0.15d*_currentSquareWidth) ) &&
					( xInsideSquare <= (0.85d*_currentSquareWidth) ) &&
					( yInsideSquare >= (0.15d*_currentSquareWidth) ) &&
					( yInsideSquare <= (0.85d*_currentSquareWidth) ) )
				{
					if( _lastMouseIsOverAPiece && !hasChangedSquare )
					{
						if( _lastIsInPositionToDrag )
							result = cp;
					}
					else if( canMove && ( cp.getListOfLegalMoves().size() > 0 ) )
					{
						result = cp;
						_lastIsInPositionToDrag = true;
					}
					else
					{
						_lastIsInPositionToDrag = false;
					}
				}
				else
				{
					_lastMouseIsOverAPiece = false;
				}
			}
		}

		return( result );
	}

	public boolean setAppropriateCursor( MouseEvent evt, boolean hasChangedSquare )
	{
		boolean hasChanged = false;

		if( ! getCursor().getName().equals(CursorFunctions._waitCursor.getName() ) )	// if we are loading or saving, we must not set the hand cursor.
		{
			Cursor cursor = CursorFunctions._defaultCursor;

	//		calculateCurrentCursorColAndRow( evt.getX(), evt.getY() );

			if( isInPositionToDrag( evt.getX(), evt.getY(), hasChangedSquare ) != null )
			{
				cursor = CursorFunctions._handCursor;
			}

			hasChanged = !getCursor().getName().equals( cursor.getName() );
			setCursor( cursor );
		}

		return( hasChanged );
	}

	protected String getDescriptionParentSize( Component comp, int depth )
	{
		String result = "null";
		Component parent = comp;
		for( int ii=0; (parent!=null) && (ii<depth); ii++ )
		{
			parent = parent.getParent();
		}

		if( parent != null )
		{
			String dividerLocationStr = ( parent instanceof JSplitPane ? "-dl(" + ((JSplitPane)parent).getDividerLocation() + ")" : "" );
			result = parent.getClass().getName() + "-" + parent.getName() +
					"(" + ((int)parent.getSize().getWidth()) + "," +
					((int)parent.getSize().getHeight()) + ")" +
					dividerLocationStr;
		}

		return( result );
	}

	@Override
	public void paintComponent(Graphics gc)
	{
		synchronized( this )
		{
			changeCurrentSquareWidth();
//			super.paintComponent( gc );
/*
		System.out.println( "Size: " + getDescriptionParentSize( this, 0 ) +
							". Parent Size: " + getDescriptionParentSize( this, 1 ) +
							". Parent.Parent Size: " + getDescriptionParentSize( this, 2 ) +
							". Parent.Parent.Parent Size: " + getDescriptionParentSize( this, 3 ) +
							". Parent.Parent.Parent.Parent Size: " + getDescriptionParentSize( this, 4 )
							);
*/

			if( ! _hasToShowPicture )
				paintChessBoardPosition( gc );
			else
				showPicture( gc );

			super.paintComponent( gc );

			_hasToShowPicture = false;
		}
	}

	public void showPicture( Graphics gc )
	{
		
		BufferedImage bi = null;
		if( _pieceCodeToShow.equals( "WS" ) )
			bi = _chessBoardImagesCache.getSquareImage( ChessPiece.WHITE, _currentSquareWidth );
		else if( _pieceCodeToShow.equals( "BS" ) )
			bi = _chessBoardImagesCache.getSquareImage( ChessPiece.BLACK, _currentSquareWidth );
		else
			bi = _chessBoardImagesCache.getPieceImage(_pieceCodeToShow, _currentPieceWidth );

		if( bi != null )
			gc.drawImage( bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null );
		else
		{
			gc.setColor( Color.BLACK );
			gc.drawString( "null", 30, 30 );
		}
	}

	protected void paintChessBoardPosition( Graphics gc )
	{
		BufferedImage bi = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB  );
		Graphics gc1 = bi.getGraphics();

		gc1.setColor(getInvertibleColor(INVERTIBLE_GREY_INDEX ) );
		gc1.fillRect( 0, 0, getWidth(), getHeight() );

		if( _chessBoardImagesCache.isInitialized() )
		{
			paintBoard( gc1 );
			writeColumnLettersAndRowNumbers( gc1 );

			if( ( _chessBoard != null ) && ! _chessBoard.isCurrentNodeIllegal() )
			{
				markOriginOfMoveAndPossibleDestinations( gc1 );
				paintTurn( gc1 );
				paintResult( gc1 );
				paintNames( gc1 );

				showColAndRow( gc1 );

				paintPieces( gc1 );
				paintCapturedPieces( gc1 );

				paintRemainingTimes( gc1 );
			}
			else if( ( _chessBoard != null ) && _chessBoard.isCurrentNodeIllegal() )
			{
				writeIllegalPosition( gc1 );
			}
			else
			{
				writeInitialPositionNotSet( gc1 );
			}
		}

		gc.drawImage( bi, 0, 0, bi.getWidth(), bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null );
	}

	protected void showColAndRow( Graphics gc )
	{
//		gc.drawString( _currentCursorColumn + ", " + _currentCursorRow, 10, 25 );
	}

	protected double getZoomFactor()
	{
		return( _currentSquareWidth / HUNDRED_PERCENT_SQUARE_WIDTH );
	}

	protected void putSquaredFrame( Graphics gc, int col, int row, Color color )
	{
		int thick = IntegerFunctions.zoomValueCeil(HUNDRED_PERCENT_SQUARED_FRAME_THICK, getZoomFactor() );

		putSquaredFrame( gc, thick, col, row, color );
	}

	protected void putSquaredFrame( Graphics gc, int thick, int col, int row, Color color )
	{
		gc.setColor( color );
		
		Rectangle bounds = getRectangleBoundsOfSquareByColRow( col, row );

/*		
		gc.drawRect( (int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight() );
		gc.drawRect( (int) bounds.getX()+1, (int) bounds.getY()+1, (int) bounds.getWidth()-2, (int) bounds.getHeight()-2 );
		gc.drawRect( (int) bounds.getX()+2, (int) bounds.getY()+2, (int) bounds.getWidth()-4, (int) bounds.getHeight()-4 );
*/
		for( int ii=0; ii<thick; ii++ )
			gc.drawRect( bounds.x + ii, bounds.y + ii, bounds.width - 2 * ii, bounds.height - 2 * ii );
	}

	protected boolean isPositionInsideTheBoard( int column, int row )
	{
		boolean result = ( ( column <= ChessBoard.NUM_OF_COLUMNS ) && ( column > 0 ) &&
							( row <= ChessBoard.NUM_OF_ROWS ) && ( row > 0 ) );
		return( result );
	}

	protected void markOriginOfMoveAndPossibleDestinations( Graphics gc )
	{
		if( _chessBoard != null )
		{
			ChessGameMove cgm = _chessBoard.peekLastMove();

			if( cgm != null )
			{
				putSquaredFrame( gc, cgm._column1, cgm._row1, getPutOutableColor(PUTOUTABLE_ORANGE_INDEX) );
				putSquaredFrame( gc, cgm._column2, cgm._row2, getPutOutableColor(PUTOUTABLE_ORANGE_INDEX) );
			}

			try
			{
				if( _inDrag )
				{
					putSquaredFrame( gc, _startDragColumn, _startDragRow, getPutOutableColor(PUTOUTABLE_YELLOW_INDEX) );
					cgm = new ChessGameMove( _startDragColumn, _startDragRow, _currentCursorColumn, _currentCursorRow );
					if( _chessBoard.isLegalThisMove(cgm, false) )
					{
						putSquaredFrame( gc, _currentCursorColumn, _currentCursorRow, getPutOutableColor(PUTOUTABLE_GREEN_INDEX) );
					}
				}
				else if( (getCursor().equals(CursorFunctions._handCursor)) &&
							isPositionInsideTheBoard( _currentCursorColumn, _currentCursorRow ) )
				{
					ChessPiece cp = _chessBoard.getPiece( _currentCursorColumn, _currentCursorRow );

					if( ( cp != null ) && ( cp.getColor() == _chessBoard.getColorToPlay() ) )
					{
						List<ChessGameMove> listOfMoves = cp.getListOfLegalMoves();
						if( listOfMoves.size() > 0 )
						{
							putSquaredFrame( gc, cp.getColumn(), cp.getRow(), getPutOutableColor(PUTOUTABLE_YELLOW_INDEX) );

							Iterator<ChessGameMove> it = listOfMoves.iterator();

							while( it.hasNext() )
							{
								cgm = it.next();
								putSquaredFrame( gc, cgm._column2, cgm._row2, getPutOutableColor(PUTOUTABLE_GREEN_INDEX) );
							}
						}
					}
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	protected void paintRemainingTimes( Graphics gc )
	{
		Rectangle whiteRemainingTimeSquareBound = null;
		Rectangle blackRemainingTimeSquareBound = null;

		if( _whitePlaysFromTheBottom )
		{
			whiteRemainingTimeSquareBound = getRectangleBoundsOfSquareByColRow( 10, 3 );
			blackRemainingTimeSquareBound = getRectangleBoundsOfSquareByColRow( 10, 5 );
		}
		else
		{
			whiteRemainingTimeSquareBound = getRectangleBoundsOfSquareByColRow( -1, 4 );
			blackRemainingTimeSquareBound = getRectangleBoundsOfSquareByColRow( -1, 6 );
		}

		ImageFunctions.instance().paintStringLeftTopJustified(gc, getBigFont(),
						getWhiteRemainingTimeStr(), getPutOutableColor( PUTOUTABLE_WHITE_INDEX ), whiteRemainingTimeSquareBound.x,
						whiteRemainingTimeSquareBound.y);

		ImageFunctions.instance().paintStringLeftTopJustified(gc, getBigFont(),
						getBlackRemainingTimeStr(), getPutOutableColor( PUTOUTABLE_BLACK_INDEX ), blackRemainingTimeSquareBound.x,
						blackRemainingTimeSquareBound.y );
	}

	protected String getWhiteRemainingTimeStr()
	{
		return( getRemainingTimeStr( _remainingSecondsWhite ) );
	}

	protected String getBlackRemainingTimeStr()
	{
		return( getRemainingTimeStr( _remainingSecondsBlack ) );
	}

	protected String getRemainingTimeStr( Integer remainingSeconds )
	{
		String result = "";

		if( remainingSeconds != null )
			result = String.format( "%d:%02d:%02d", remainingSeconds/3600,
													(remainingSeconds/60)%60,
													remainingSeconds%60 );

		return( result );
	}

	protected void paintNames( Graphics gc )
	{
		Rectangle whitePlayerSquareBound = null;
		Rectangle blackPlayerSquareBound = null;
		int white_yIncrement = 0;
		int black_yIncrement = 0;

		if( _whitePlaysFromTheBottom )
		{
			whitePlayerSquareBound = getRectangleBoundsOfSquareByColRow( 1, -1 );
//			white_yIncrement = - _currentSquareWidthHalves+12;
			white_yIncrement = - _currentSquareWidth/4;
			blackPlayerSquareBound = getRectangleBoundsOfSquareByColRow( 1, 9 );
//			black_yIncrement = _currentSquareWidthHalves-5;
			black_yIncrement = ( 3 * _currentSquareWidth )/8;
		}
		else
		{
			whitePlayerSquareBound = getRectangleBoundsOfSquareByColRow( 8, 0 );
			blackPlayerSquareBound = getRectangleBoundsOfSquareByColRow( 8, 10 );
			black_yIncrement = - _currentSquareWidthHalves-3;
		}

		ImageFunctions.instance().paintStringLeftTopJustified(gc, getMediumFont(),
						getWhiteNameElo(), getPutOutableColor( PUTOUTABLE_WHITE_INDEX ), whitePlayerSquareBound.x,
						whitePlayerSquareBound.y + white_yIncrement);

		ImageFunctions.instance().paintStringLeftTopJustified(gc, getMediumFont(),
						getBlackNameElo(), getPutOutableColor( PUTOUTABLE_BLACK_INDEX ), blackPlayerSquareBound.x,
						blackPlayerSquareBound.y + black_yIncrement);
	}

	protected String getWhiteNameElo()
	{
		if( _whiteNameElo == null )
		{
			_whiteNameElo = createNameEloString( _whitePlayerName, _whiteElo );
		}
		return( _whiteNameElo );
	}

	protected String getBlackNameElo()
	{
		if( _blackNameElo == null )
		{
			_blackNameElo = createNameEloString( _blackPlayerName, _blackElo );
		}
		return( _blackNameElo );
	}

	protected String createNameEloString( String name, String elo )
	{
		String result = "";
		if( !StringFunctions.instance().isEmpty(name) && !StringFunctions.instance().isEmpty( elo ) )
		{
			result = String.format( "%s (%s)", name, elo );
		}
		else if( !StringFunctions.instance().isEmpty( name ) )
		{
			result = name;
		}

		return( result );
	}

	protected boolean gameContinues()
	{
		return( ( _chessBoard.getChessGameResult() == null ) ||
				( _chessBoard.getChessGameResult() == ChessGameResult.GAME_CONTINUES ) );
	}

	protected void paintTurn( Graphics gc )
	{
		if( ( _chessBoard != null ) && ( _chessBoard.getCurrentPosition() != null ) )
		{
//			System.out.println( "" + _chessBoard.getChessGameResult() );
			if( gameContinues() )
			{
				int sizeOfTurnSquare = _currentSquareWidthHalves - 7;

				Color rectangleColor = null;
				Color boundColor = null;
				int row = 0;

				int y_increment = 0;
				int x_increment = 0;

				if( _whitePlaysFromTheBottom )	x_increment = 4;
				else							x_increment = 2;

				if( _chessBoard.getCurrentPosition().getIsWhitesTurn() )
				{
					rectangleColor = getPutOutableColor(PUTOUTABLE_WHITE_INDEX);
					boundColor = getPutOutableColor(PUTOUTABLE_BLACK_INDEX);
					row = 0;

					if( _whitePlaysFromTheBottom ) y_increment = _currentSquareWidthHalves + 12;

					y_increment -= sizeOfTurnSquare;
				}
				else
				{
					rectangleColor = getPutOutableColor(PUTOUTABLE_BLACK_INDEX);
					boundColor = getPutOutableColor(PUTOUTABLE_WHITE_INDEX);
					row = 9;

					if( !_whitePlaysFromTheBottom ) y_increment = +4;
				}

				int column = -1;
				
				if( _whitePlaysFromTheBottom )
					column = 0;
				else
					column = 9;

				Rectangle squareBound = getRectangleBoundsOfSquareByColRow( column, row );

				gc.setColor( boundColor );
				gc.drawRect(	(int) ( squareBound.getX() + x_increment ),
								(int) ( squareBound.getY() + y_increment + 2 ),
								(int) ( sizeOfTurnSquare ),
								(int) ( sizeOfTurnSquare ));

				gc.setColor( rectangleColor );
				gc.fillRect(	(int) ( squareBound.getX() + x_increment + 1 ),
								(int) ( squareBound.getY() + y_increment + 3 ),
								(int) ( sizeOfTurnSquare - 2 ),
								(int) ( sizeOfTurnSquare - 2 ));
			}
		}
	}

	protected void paintResult( Graphics gc )
	{
		String status = null;

		if( ( _chessBoard != null ) && ( _chessBoard.getCurrentPosition() != null ) )
		{
			if( gameContinues() )
			{
				if( _chessBoard.getCurrentPosition().getIsWhitesTurn() )
					status = getChessStrConf().getProperty( ChessStringsConf.CONF_WHITE_TO_PLAY );
				else
					status = getChessStrConf().getProperty( ChessStringsConf.CONF_BLACK_TO_PLAY );

				if( _chessBoard.isCheck() )
					status = getChessStrConf().getProperty( ChessStringsConf.CONF_CHECK ) + " " + status;
			}
			else if( _chessBoard.getChessGameResult() == ChessGameResult.WHITE_WINS )
				status = "1-0 " + getChessStrConf().getProperty( ChessStringsConf.CONF_BLACK_RESIGNS );
			else if( _chessBoard.getChessGameResult() == ChessGameResult.BLACK_WINS )
				status = "0-1" + getChessStrConf().getProperty( ChessStringsConf.CONF_WHITE_RESINGS );
			else if( _chessBoard.getChessGameResult() == ChessGameResult.WHITE_WINS_CHECK_MATE )
				status = getChessStrConf().getProperty( ChessStringsConf.CONF_CHECK_MATE ) + " 1-0 " +
					getChessStrConf().getProperty( ChessStringsConf.CONF_WHITE_WINS );
			else if( _chessBoard.getChessGameResult() == ChessGameResult.BLACK_WINS_CHECK_MATE )
				status = getChessStrConf().getProperty( ChessStringsConf.CONF_CHECK_MATE ) + " 0-1 " +
					getChessStrConf().getProperty( ChessStringsConf.CONF_BLACK_WINS );
			else
			{
				status = getChessStrConf().getProperty( ChessStringsConf.CONF_DRAW ) + "  1/2-1/2";

				if( _chessBoard.getChessGameResult() == ChessGameResult.DRAW_STALE_MATE )
					status = status + "  " + getChessStrConf().getProperty( ChessStringsConf.CONF_STALE_MATE );
				else if( _chessBoard.getChessGameResult() == ChessGameResult.DRAW_THIRD_REPETITION )
					status = status + "  " + getChessStrConf().getProperty( ChessStringsConf.CONF_THIRD_REPETITION );
				else if( _chessBoard.getChessGameResult() == ChessGameResult.DRAW_FIFTY_MOVES_WITHOUT_PROGRESS )
					status = status + getChessStrConf().getProperty( ChessStringsConf.CONF_FIFTY_MOVES_WITHOUT_PROGRESS );
				else if( _chessBoard.getChessGameResult() == ChessGameResult.DRAW_MUTUAL_AGREEMENT )
					status = status + "  " + getChessStrConf().getProperty( ChessStringsConf.CONF_BY_MUTUAL_AGREEMENT );
				else if( _chessBoard.getChessGameResult() == ChessGameResult.DRAW_INSUFFICIENT_MATERIAL )
					status = status + "  " + getChessStrConf().getProperty( ChessStringsConf.CONF_INSUFFICIENT_MATERIAL );

			}

			Rectangle rect = getRectangleBoundsOfSquareByColRow( 0, 10 );

			Rectangle statusRectangle = new Rectangle(	_currentSquareWidthHalves,
														(int) rect.getY() + 4,
														_currentSquareWidth * ChessBoard.NUM_OF_COLUMNS,
														(int) rect.getHeight() - 8 );

			Font font = getSmallFont();
			gc.setFont(font);

			ImageFunctions.instance().paintStringCentered(gc, font, status, getInvertibleColor(INVERTIBLE_BLACK_INDEX ),
														statusRectangle, getInvertibleColor(INVERTIBLE_BRIGHT_GREEN_INDEX ) );
		}
	}
/*
	protected void paintPiece( Graphics gc, ChessPiece cp, Rectangle bounds )
	{
		if( cp != null )
		{
			// if some part of the piece is inside the visible zone (the screen)
			if( ( bounds.getX() < getWidth() ) && ( bounds.getX() + bounds.getWidth() >= 0 ) &&
				( bounds.getY() < getHeight() ) && ( bounds.getY() + bounds.getHeight() >= 0 ) )
			{
				int dx1 = IntegerFunctions.max( 0, (int) bounds.getX() );
				int dy1 = IntegerFunctions.max( 0, (int) bounds.getY() );
				int dx2 = IntegerFunctions.min( getWidth(), (int) ( bounds.getX() + bounds.getWidth() ) );
				int dy2 = IntegerFunctions.min( getHeight(), (int) ( bounds.getY() + bounds.getHeight() ) );

				int sx1 = dx1 - (int) bounds.getX();
				int sy1 = dy1 - (int) bounds.getY();
				int sx2 = sx1 + dx2 - dx1;
				int sy2 = sy1 + dy2 - dy1;

				BufferedImage bi = _chessBoardImagesCache.getPieceImage( cp.getPieceCode() );

				gc.drawImage( bi, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null );
			}
		}
	}
*/
	protected void paintPieces( Graphics gc )
	{
		if( _chessBoard != null )
		{
			for( int col=1; col<=ChessBoard.NUM_OF_COLUMNS; col++ )
				for( int row=1; row<=ChessBoard.NUM_OF_ROWS; row++ )
				{
					if( ! _inDrag || (col != _startDragColumn) || (row != _startDragRow) )
					{
						try
						{
							ChessPiece cp = _chessBoard.getPiece(col, row);

							if( ( cp != null ) &&
								( ( _promotionMove == null ) ||
								  ( cp.getColumn() != _promotionMove._column1 ) ||
								  ( cp.getRow() != _promotionMove._row1 ) ) )
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

	protected void paintCapturedPieces( Graphics gc )
	{
		Rectangle whitePlayerSquareBound = null;
		Rectangle blackPlayerSquareBound = null;
		int white_yIncrement = 0;
		int black_yIncrement = 0;
		if( _whitePlaysFromTheBottom )
		{
			whitePlayerSquareBound = getRectangleBoundsOfSquareByColRow( 1, -1 );
//			white_yIncrement = - _currentSquareWidthHalves + 15;
			white_yIncrement = 0;
			blackPlayerSquareBound = getRectangleBoundsOfSquareByColRow( 1, 9 );
			black_yIncrement = - _currentSquareWidthHalves;
		}
		else
		{
			whitePlayerSquareBound = getRectangleBoundsOfSquareByColRow( 8, -1 );
//			white_yIncrement = _currentSquareWidthHalves - 15;
			white_yIncrement = 0;
			blackPlayerSquareBound = getRectangleBoundsOfSquareByColRow( 8, 9 );
			black_yIncrement = _currentSquareWidthHalves;
		}

		paintCapturedPiecesOfColor( gc, whitePlayerSquareBound,
									-_currentSquareWidth/4,
									white_yIncrement, ChessPiece.BLACK );
		paintCapturedPiecesOfColor( gc, blackPlayerSquareBound,
									-_currentSquareWidth/4,
									black_yIncrement, ChessPiece.WHITE );
	}

	protected void paintCapturedPiecesOfColor( Graphics gc, Rectangle initialRectangle,
												int xIncrement,
												int yIncrement, int color )
	{
		int index = 1;
		int xx = initialRectangle.x + xIncrement;
		int yy = initialRectangle.y + yIncrement;
		List<ChessPiece> list = _chessBoard.getCapturedPieces(color);
		for( int ii=list.size()-1; ii>=0; ii-- )
		{
			ChessPiece piece = list.get(ii);

			Point upperLeftCorner = getUpperLeftCornerOfCapturedPieceByColRow( initialRectangle, xx, yy );
			BufferedImage bi = _chessBoardImagesCache.getPieceImage( piece.getPieceCode(), _currentPieceWidth/2 );
			ImageFunctions.instance().paintClippedImage(this, gc, bi, upperLeftCorner);

			if( index % 2 == 0 )
				xx += ( _currentSquareWidth - _currentSquareWidthHalves );
			else
				xx += _currentSquareWidthHalves;
		}
	}

	protected void paintPieceInDrag( Graphics gc )
	{
		if( _inDrag )
		{
			try
			{
				ChessPiece cp = _chessBoard.getPiece( _startDragColumn, _startDragRow );
				if( ( cp != null ) &&
					( ( _promotionMove == null ) ||
					  ( cp.getColumn() != _promotionMove._column1 ) ||
					  ( cp.getRow() != _promotionMove._row1 ) ) )
				{
					Point upperLeftCorner = new Point(	_currentX - _currentPieceWidth/2,
														_currentY - _currentPieceWidth/2 );
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

	protected boolean isDarkMode()
	{
		return( FrameworkComponentFunctions.instance().isDarkMode(this) );
	}

	protected boolean boxImageIsSuitable( BufferedImage image, int width )
	{
		boolean result = ( image != null );
		if( result )
			result = ( image.getWidth() == width );

		return( result );
	}

	protected BufferedImage getBoxImage( int parity, int width )
	{
		return( _chessBoardImagesCache.getSquareImage( parity, width ) );
	}

	protected void paintBoard( Graphics gc )
	{
		for( int col=1; col<=ChessBoard.NUM_OF_COLUMNS; col++ )
			for( int row=1; row<=ChessBoard.NUM_OF_ROWS; row++ )
			{
				Rectangle bounds = getRectangleBoundsOfSquareByColRow( col, row );
//				BufferedImage bi = _chessBoardImagesCache.getSquareImage( (col + row)%2, _currentSquareWidth );
				BufferedImage bi = getBoxImage( (col + row)%2, _currentSquareWidth );

				if( bi != null )
				{
					gc.drawImage( bi, (int)bounds.getX(), (int)bounds.getY(),
										(int)(bounds.getX()+bounds.getWidth()),
										(int)(bounds.getHeight()+bounds.getY()),
									0, 0, bi.getWidth(), bi.getHeight(), null );
				}
			}

		gc.setColor( Color.BLACK );

		int x1 = _initialX_ofBoard;
		int y1 = _initialY_ofBoard;
		int deltaX = ChessBoard.NUM_OF_COLUMNS*_currentSquareWidth-1;
		int deltaY = ChessBoard.NUM_OF_ROWS*_currentSquareWidth-1;
		gc.drawRect( x1, y1, deltaX, deltaY);
		gc.drawRect( x1-1, y1-1, deltaX+2, deltaY+2);
		gc.drawRect( x1-2, y1-2, deltaX+4, deltaY+4);
		gc.drawRect( x1-3, y1-3, deltaX+6, deltaY+6);
	}

	protected void writeColumnLettersAndRowNumbers( Graphics gc )
	{
		final String COLUMN_NAMES="abcdefgh";

		Font font = getMediumFont();
//		gc.setColor( Color.WHITE );
		gc.setFont(font);

		for( int ii=1; ii<9; ii++ )
		{
			String columnName = COLUMN_NAMES.substring( ii-1, ii );

			Rectangle rect = getRectangleBoundsOfSquareByColRow( ii, 0 );
			ImageFunctions.instance().paintStringCentered(gc, font, columnName,
														getInvertibleColor(INVERTIBLE_WHITE_INDEX ), rect, null );

			rect = getRectangleBoundsOfSquareByColRow( 0, ii );
			ImageFunctions.instance().paintStringCentered(gc, font, String.valueOf(ii),
														getInvertibleColor(INVERTIBLE_WHITE_INDEX ), rect, null );
		}
	}

	protected void writeInitialPositionNotSet( Graphics gc )
	{
		final String TEXT[]= new String[] {
			getChessStrConf().getProperty( ChessStringsConf.CONF_INITIAL_POSITION_NOT_SET_1 ),
			getChessStrConf().getProperty( ChessStringsConf.CONF_INITIAL_POSITION_NOT_SET_2 ),
			getChessStrConf().getProperty( ChessStringsConf.CONF_INITIAL_POSITION_NOT_SET_3 ) };

		Font font = getBigFont();
//		gc.setColor( Color.RED );
		gc.setFont(font);

		int initialX = _initialX_ofBoard;
		int width = 8 * _currentSquareWidth;
		
		int initialY = 4 * _currentSquareWidth;
		int yIncrement = _currentSquareWidth;

		int currentY = initialY;
		for( int ii=0; ii<TEXT.length; ii++ )
		{
			Rectangle rect = new Rectangle( initialX, currentY, width, yIncrement );
			ImageFunctions.instance().paintStringCentered(gc, font, TEXT[ii], RED, rect, null );
			currentY += yIncrement;
		}
	}

	protected void writeIllegalPosition( Graphics gc )
	{
		final String TEXT[]= new String[] {
			getChessStrConf().getProperty( ChessStringsConf.CONF_ILLEGAL_POSITION_1 ),
			getChessStrConf().getProperty( ChessStringsConf.CONF_ILLEGAL_POSITION_2 ) };

		Font font = getBigFont();
//		gc.setColor( Color.RED );
		gc.setFont(font);

		int initialX = _initialX_ofBoard;
		int width = 8 * _currentSquareWidth;
		
		int initialY = 4 * _currentSquareWidth;
		int yIncrement = _currentSquareWidth;

		int currentY = initialY;
		for( int ii=0; ii<TEXT.length; ii++ )
		{
			Rectangle rect = new Rectangle( initialX, currentY, width, yIncrement );
			ImageFunctions.instance().paintStringCentered(gc, font, TEXT[ii], RED, rect, null );
			currentY += yIncrement;
		}
	}

	protected Point getUpperLeftCornerOfPieceByColRow( int col, int row )
	{
		return( getUpperLeftCornerOfPieceByColRowGen( col, row, _currentPieceWidth ) );
	}

	protected Point getUpperLeftCornerOfCapturedPieceByColRow( Rectangle rect,
																int xx, int yy )
	{
		return( getUpperLeftCornerOfPieceByColRowGen( rect,
													xx, yy,
													_currentPieceWidth / 2 ) );
	}

	protected Point getUpperLeftCornerOfPieceByColRowGen( int col, int row,
															int pieceWidth )
	{
		Rectangle rect = getRectangleBoundsOfSquareByColRow( col, row );

		return( getUpperLeftCornerOfPieceByColRowGen( rect, rect.x, rect.y,
														pieceWidth ) );
	}

	protected Point getUpperLeftCornerOfPieceByColRowGen( Rectangle rect,
															int xx, int yy,
															int pieceWidth )
	{
		int x1 = (int) Math.floor( xx + ( rect.getWidth() - pieceWidth ) / 2 );
		int y1 = (int) Math.floor( yy + ( rect.getHeight() - pieceWidth ) / 2 );

		Point result = new Point( x1, y1 );

		return( result );
	}

	// coordinates have a range from (0,0)-(9-9)
	// squares from (1,1)-(8,8) are inside the board, and have a width of _currentSquareWidth.
	// (1,1) corresponds with "a1" chess square, and so on, so their coordinates change depending on if white plays from bottom or not.
	// the remaining squares (the external boundary), have the dimension not shared with the board, half of _currentSquareWidth.
	// =======================================================================================================
	// this has been slightly changed in version 1.12:
	// now coordinates can vary from (0,-1)-(9-10), giving room for the name and captured pieces, at the top and bottom of the board.
	protected Rectangle getRectangleBoundsOfSquareByColRow( int col, int row )
	{
		int x1 = 0;
		int y1 = 0;
		int deltaX = 0;
		int deltaY = 0;
		int yIncrement = 0;

		if( (col>0) && (col<9) ) deltaX = _currentSquareWidth;
		else					 deltaX = _currentSquareWidthHalves;

		if( (row>0) && (row<9) ) deltaY = _currentSquareWidth;
		else					 deltaY = _currentSquareWidthHalves;

		if( _whitePlaysFromTheBottom )
		{
			if( col>0 ) x1 = (col-1)*_currentSquareWidth + _initialX_ofBoard;
			else		x1 = 0;

//			if( row<10 ) y1 = (ChessBoard.NUM_OF_ROWS-row)*_currentSquareWidth + _initialY_ofBoard;
//			else		y1 = 0;
			y1 = (ChessBoard.NUM_OF_ROWS-row)*_currentSquareWidth + _initialY_ofBoard;

			if( row > 8 ) yIncrement = _currentSquareWidthHalves;
		}
		else
		{
			if( col<9 ) x1 = (ChessBoard.NUM_OF_COLUMNS-col)*_currentSquareWidth + _initialX_ofBoard;
			else		x1 = 0;

//			if( row>-1 ) y1 = (row-1)*_currentSquareWidth + _initialY_ofBoard;
//			else		y1 = 0;
			y1 = (row-1)*_currentSquareWidth + _initialY_ofBoard;

			if( row < 1 ) yIncrement = _currentSquareWidthHalves;
		}
		
		Rectangle result = new Rectangle( x1, y1 + yIncrement, deltaX, deltaY );
		return( result );
	}

	protected static int calculateSquareWidth( int width, int height )
	{
		int result = -1;

		if( (width>0) && (height>0) )
		{
			int squareSize1 = width / ( ChessBoard.NUM_OF_COLUMNS+1 );
			int squareSize2 = height / ( ChessBoard.NUM_OF_ROWS+3 );

			result = IntegerFunctions.min( squareSize1, squareSize2 );
		}

		result = IntegerFunctions.max( MIN_SQUARE_WIDTH, result );
		result = result & 0xFFFFFFFE; // we want an even width

		return( result );
	}

	protected void changeCurrentSquareWidth()
	{
		_currentSquareWidth = calculateSquareWidth( getWidth(), getHeight() );
		_currentPieceWidth = (int) Math.floor( _currentSquareWidth * _fractionOfPieceWidthOverSquareWidth );

		_currentSquareWidthHalves = _currentSquareWidth / 2;
		_initialX_ofBoard = _currentSquareWidthHalves;
		_initialY_ofBoard = _currentSquareWidthHalves + _currentSquareWidth;
/*
		try
		{
			_chessBoardImagesCache.resizeSquaresAndPieces(_currentSquareWidth, _currentPieceWidth);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
*/
	}

	public void flipBoard()
	{
		_chessBoard.flipBoard();
		repaint();
	}

	@Override
	public void setSize( Dimension dim )
	{
		super.setSize( dim );

		changeCurrentSquareWidth();
	}

	@Override
	public void setSize( int width, int height )
	{
		super.setSize( width, height );
		
		changeCurrentSquareWidth();
	}
	
	@Override
	public void refresh()
	{
		repaint();
	}

	@Override
	public void pieceTopromotePawnChosen( String pieceTypeCode )
	{
		if( ( pieceTypeCode != null ) && (_promotionMove != null ) )
		{
			_promotionMove.setPromotionPiece( pieceTypeCode );
			makeNewMove( _promotionMove );
		}
		_promotionMove = null;
		_promotionJPanel.setVisible(false);
		repaint();
	}


	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}

	@Override
	public void changeLanguage(String newLanguage) throws Exception
	{
		refresh();
	}

	@Override
	public void unregisterFromChangeLanguageAsObserver()
	{
		if( _changeLanguageServer == null )
		{
			_changeLanguageServer.unregisterChangeLanguageObserver( this );
		}
	}

	@Override
	public void registerToChangeLanguageAsObserver( ChangeLanguageServerInterface conf)
	{
		if( _changeLanguageServer == null )
		{
			_changeLanguageServer = conf;
			_changeLanguageServer.registerChangeLanguageObserver( this );
		}
	}

	@Override
	public String getLanguage()
	{
		String result = null;
		if( _changeLanguageServer == null )
		{
			result = _changeLanguageServer.getLanguage();
		}
		return( result );
	}

	@Override
	public void invertColorsChild( ColorInversor colorInversor )
	{
		_invertibleColorModeColors = colorInversor.invertColors(_invertibleColorModeColors);

		if( isDarkMode() )
			_putOutableColorModeColors = putOutColors(colorInversor, _originalPutOutableColorModeColors);
		else
			_putOutableColorModeColors = _originalPutOutableColorModeColors;

		_chessBoardImagesCache.clearAllResized();
		_chessBoardImagesCache.setDarkMode( isDarkMode() );
	}

	protected Color[] putOutColors( ColorInversor colorInversor, Color ... colors )
	{
		return( colorInversor.putOutColors( 0.5, colors ) );
	}

	public void setFigureSetChangedObserved( FigureSetChangedObserved figureSetChangedObserved )
	{
		registerToFigureSetChangedObserved( figureSetChangedObserved );
	}

	protected void registerToFigureSetChangedObserved( FigureSetChangedObserved figureSetChangedObserved )
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
		SwingUtilities.invokeLater( () -> {
			_chessBoardImagesCache.clearAllResized();
			repaint();
		});
	}

	@Override
	public void releaseResources()
	{
		unregisterToFigureSetChangedObserved();
	}
}
