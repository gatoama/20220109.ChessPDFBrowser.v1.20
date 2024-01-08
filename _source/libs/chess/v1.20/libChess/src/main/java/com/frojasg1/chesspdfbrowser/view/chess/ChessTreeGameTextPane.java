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

import com.frojasg1.applications.common.components.resizecomp.ResizableComponentInterface;
import com.frojasg1.chesspdfbrowser.engine.io.writers.WrittenNodeInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameTreeAdditionalInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.view.chess.editcomment.EditCommentFrame;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameViewInterface;
import com.frojasg1.chesspdfbrowser.view.chess.multiwindowmanager.MultiwindowGameManager;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.SelectionPreservingCaret;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.CreateCustomString;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.Objects;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;

/**
 *
 * @author Usuario
 */
public class ChessTreeGameTextPane extends JTextPane
	implements MouseMotionListener, MouseListener,
				InternationalizedStringConf, DesktopViewComponent,
				ResizableComponentInterface, ColorThemeInvertible
{
	protected static final String CONF_COULD_NOT_COPY_ALREADY_A_MOVE = "COULD_NOT_COPY_ALREADY_A_MOVE";
	protected static final String CONF_PROBLEM_WHILE_COPYING_A_SUBVARIANT = "PROBLEM_WHILE_COPYING_A_SUBVARIANT";
	protected static final String CONF_COULD_NOT_COPY_NUMBER_OF_PLY_DOES_NOT_MATCH = "COULD_NOT_COPY_NUMBER_OF_PLY_DOES_NOT_MATCH";
	protected static final String CONF_COULD_NOT_COPY_NO_SUBVARIANT = "COULD_NOT_COPY_NO_SUBVARIANT";
	protected static final String CONF_NODE_TO_PASTE_TO_THE_COPIED_SUBVARIANT_NOT_SELECTED = "NODE_TO_PASTE_TO_THE_COPIED_SUBVARIANT_NOT_SELECTED";

	protected static final Cursor sa_handCursor = new Cursor( Cursor.HAND_CURSOR );
	protected static final Cursor sa_defaultCursor = new Cursor( Cursor.DEFAULT_CURSOR );

	// The next values are for size of font:
	protected static final int _NORMAL_SIZE = 0;		// normal size
	protected static final int _BIG = 1;				// normal size


	// The next values are for background type:
	protected static final int _NORMAL = 0;				// white background
	protected static final int _SELECTED = 1;			// yellow background
	protected static final int _COPY_SELECTION = 2;		// grey background
	protected static final int _ILLEGAL_MOVE = 3;		// red background
	protected static final int _NUMBER_OF_BT = 4;

	// The next values are for the style names for moves.
	protected static final int _PLAIN = 0;		// illegal or undefined move
	protected static final int _BOLD_BLACK = 1;	// main variant
	protected static final int _BLUE = 2;		// secondary variant
	protected static final int _GREEN = 3;		// comment
	protected static final int _BLACK = 4;		// secondary variant for figurines
	protected static final int _NUMBER_OF_STYLE_NAMES = 5;

	protected Color DEFAULT_FOREGROUND_COLOR = Color.BLACK;
	protected Color GREEN_FOREGROUND_COLOR = Color.green.darker();
	protected Color BLUE_FOREGROUND_COLOR = Color.BLUE;
//	protected Color BRIGHT_GRAY_BACKGROUND_COLOR = new Color( 224, 224, 224 );
	protected Color BRIGHT_GRAY_BACKGROUND_COLOR = new Color( 200, 200, 200 );
	protected Color RED_BACKGROUND_COLOR = new Color( 253, 109, 90 );
	protected Color YELLOW_BACKGROUND_COLOR = new Color( 253, 250, 9 );

	protected Style[][][] _styles = null;


	protected final String BLACK = "ChessTreeGameTextPane_BLACK";
	protected final String BOLD_BLACK = "ChessTreeGameTextPane_BOLD_BLACK";
//	protected final String BOLD_BLACK_SELECTED = "ChessTreeGameTextPane_BOLD_BLACK_SELECTED";
	protected final String GREEN = "ChessTreeGameTextPane_GREEN";
	protected final String BLUE = "ChessTreeGameTextPane_BLUE";
//	protected final String BLUE_SELECTED = "ChessTreeGameTextPane_BLUE_SELECTED";
	protected final String PLAIN = "ChessTreeGameTextPane_PLAIN";
	protected final String LOCAL_DEFAULT_STYLE = "ChessTreeGameTextPane_DEFAULT_STYLE";

	protected final String SELECTED_SUFFIX = "_SELECTED";
	protected final String ILLEGAL_MOVE_SUFFIX = "_ILLEGAL_MOVE";
	protected final String COPY_SELECTION_SUFFIX = "_COPY_SELECTION";

	protected final String NORMAL_SIZE_SUFFIX = "_NORMAL_SIZE";
	protected final String BIG_SUFFIX = "_BIG";

	protected MultiwindowGameManager _parent;

	protected ChessGame _chessGame = null;

	protected ChessGameControllerInterface _controller = null;
	protected ChessGameViewInterface _chessBoardPanel = null;

	protected WrittenNodeInfo _previousSelectedMoveNodeInfo = null;

//	protected MoveTreeNode _previousVariantSelectionMoveTreeNode = null;
//	protected MoveTreeNode _variantSelectionMoveTreeNode = null;

	protected MoveTreeNode _lastClickMoveTreeNodeForPopupMenu = null;
	protected MoveTreeNode _previousSelectedMove = null;

	protected ContextualMenu _popupMenu = null;

	protected boolean _browseGameEnabled = false;

	protected InternationalizedStringConf _languageConfiguration = null;

	protected int _previousDefaultFontSize = -2;

	protected double _currentZoomFactorForComponentResized = 1.0D;

	protected String _figurineChars = null;

	protected Integer _openingBracketPos = null;
	protected Integer _closingBracketPos = null;
/*
	protected static enum BackgroundType
	{
		NORMAL,
		SELECTED,
		COPY_SELECTION,
		ILLEGAL_MOVE
	}
*/

	protected boolean _updating = false;

	public ChessTreeGameTextPane( ChessGameViewInterface chessBoardPanel,
									ChessGameControllerInterface controller,
									MultiwindowGameManager parent,
									boolean browseGameEnabled )
	{
		_chessBoardPanel = chessBoardPanel;
		_controller = controller;

		setEditable( false );
		setCaret( new SelectionPreservingCaret() );
		
		int defaultFontSize = -1;
		addStyles( defaultFontSize );

		createStyleArrayCached();
		
//		System.out.println( "ChessTreeGameTextPane, addMouseListener" );
		addMouseListener( this );
		addMouseMotionListener( this );

		_popupMenu = new ContextualMenu();

		_parent = parent;

		_browseGameEnabled = browseGameEnabled;

		ChessLanguageConfiguration figurineConf = ChessLanguageConfiguration.getConfiguration(
											ChessLanguageConfiguration.ALGEBRAIC_FIGURINE_NOTATION );
		_figurineChars = figurineConf.getCharsForPieces() + figurineConf.getCharsForPiecesForBlack();
	}

	public ChessGameControllerInterface getChessGameController()
	{
		return( _controller );
	}
	
	public JPopupMenu getJPopupMenu()
	{
		return( _popupMenu );
	}

	public void setChessGame( ChessGame chessGame )
	{
		_chessGame = chessGame;
	}

	public ChessGame getChessGame()
	{
		return( _chessGame );
	}
	
	public MoveTreeNode getPreviousVariantSelectionMoveTreeNode()
	{
		return( _parent.getPreviousVariantSelectionMoveTreeNode() );
	}

	public MoveTreeNode getVariantSelectionMoveTreeNode()
	{
		return( _parent.getVariantSelectionMoveTreeNode() );
	}
/*
	public void setPreviousVariantSelectionMoveTreeNode( MoveTreeNode node )
	{
		_parent.setPreviousVariantSelectionMoveTreeNode(node);
	}
*/
	public void setVariantSelectionMoveTreeNode( MoveTreeNode node )
	{
		_parent.setVariantSelectionMoveTreeNode(node);
	}

	public void update( boolean everyThing )
	{
		if( ! everyThing )
			updateViewPositionInMovesTree();
		else
			updateAfterinsertionOfMovesInTree();
	}

	protected void changeSelectedMove( WrittenNodeInfo nodeInfo, Boolean isTypeOfCommentOfMove )
	{
		try
		{
	//		System.out.println( "Actualizando lista de movimientos." );
//			_chessGame.setCurrentListOfMoves( nodeInfo.getMoveTreeNode().getGameMoveList() );
			_chessGame.setCurrentListOfMoves( nodeInfo.getMoveTreeNode() );
	//		updateAfterinsertionOfMovesInTree();
			updateViewPositionInMovesTree();
	//		System.out.println( "actualizada insercion de movimientos en arbol." );
			EditCommentFrame.instance().setMove( _chessGame, _chessGame.getCurrentMove(), isTypeOfCommentOfMove );
			_parent.getChessGameController().getAnalysisController().setNewPosition( _chessGame.getCurrentMove() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent evt)
	{
		try
		{
	//		System.out.println( "mouseClicked." );
			JEditorPane editor = (JEditorPane) evt.getSource();
			Point pt = new Point(evt.getX(), evt.getY());
			int pos = editor.viewToModel(pt);

			ChessGameTreeAdditionalInfo cmtai = _chessGame.getChessGameTreeAdditionalInfo();

			if( cmtai != null )
			{
				WrittenNodeInfo nodeInfo = cmtai.getMoveAdditionalInfo(pos);

				if( SwingUtilities.isLeftMouseButton(evt) )
				{
					if( ( nodeInfo != null ) && nodeInfo.isInsideMove(pos) )
					{
						if( _browseGameEnabled &&
							( _chessGame != null ) ) //&& _chessGame.isValid() &&
	//						!nodeInfo.getIsIllegalMove() &&
	//						!nodeInfo.getIsAnyParentIllegalMove() )	// if chessGame do not have initial position, we do not let to go to the movement
						{
							changeSelectedMove( nodeInfo, null );
						}
						else
						{
							doPopup( evt, nodeInfo );
						}
					}
					else if( ( nodeInfo != null ) &&
							( nodeInfo.isInsideComment(pos) || nodeInfo.isInsideCommentForVariant(pos) )
							)
					{
						boolean isTypeOfCommentOfMove = nodeInfo.isInsideComment(pos);
						ExecutionFunctions.instance().safeMethodExecution( () -> editComment( nodeInfo, isTypeOfCommentOfMove ) );
					}
				}
				else if( SwingUtilities.isRightMouseButton(evt) )
				{
					doPopup( evt, nodeInfo );
				}
			}
			else
			{
				if( SwingUtilities.isRightMouseButton(evt) )
				{
					doPopup( evt, null );
				}
			}
		}
		catch( Exception ex )
		{
			String message = "Exception: " + ex.getMessage() + "\n";
			message += ex.getStackTrace()[0].toString();
			HighLevelDialogs.instance().errorMessageDialog(this, message );
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		
	}

	protected String safeGetText()
	{
		return( safeGetText( 0, getStyledDocument().getLength() ) );
	}

	protected String safeGetText( int pos, int length )
	{
		return( ExecutionFunctions.instance().safeFunctionExecution( () -> getStyledDocument().getText(pos, length) ) );
	}

	protected void restoreOpeningClosingBrackets()
	{
		boolean bigSize = false;
		if( _openingBracketPos != null )
		{
			formatText( _openingBracketPos, 1, _PLAIN, _NORMAL, bigSize );
			_openingBracketPos = null;
		}

		if( _closingBracketPos != null )
		{
			formatText( _closingBracketPos, 1, _PLAIN, _NORMAL, bigSize );
			_closingBracketPos = null;
		}
	}

	protected int valueForBracket( char charAt )
	{
		int result = 0;
		switch( charAt )
		{
			case '(': result = 1;
			break;
			
			case ')': result = -1;
			break;
		}

		return( result );
	}

	protected void markBrackets( int pos )
	{
		if( findMatchingBracket( pos ) )
		{
			boolean bigSize = true;
			formatText( _openingBracketPos, 1, _BOLD_BLACK, _NORMAL, bigSize );
			formatText( _closingBracketPos, 1, _BOLD_BLACK, _NORMAL, bigSize );
		}
	}

	protected boolean findMatchingBracket( int pos )
	{
		boolean result = false;

		String text = safeGetText();

		if( text != null )
		{
			char charAt = ' ';
			int ii=pos;
			charAt = text.charAt( pos );
			int step = valueForBracket( charAt );
			int count = 0;
			int matchingPos = -1;
			do
			{
				charAt = text.charAt( ii );
				count += valueForBracket( charAt );
				if( count == 0 )
				{
					matchingPos = ii;
					break;
				}
				ii += step;
			}
			while( ( ii>= 0 ) && ( ii<text.length() ) );

			if( matchingPos != -1 )
			{
				if( pos > matchingPos )
				{
					_openingBracketPos = matchingPos;
					_closingBracketPos = pos;
				}
				else if( pos < matchingPos )
				{
					_openingBracketPos = pos;
					_closingBracketPos = matchingPos;
				}
				else
				{
					_openingBracketPos = null;
					_closingBracketPos = null;
				}
			}
		}

		result = ( _openingBracketPos != null );

		return( result );
	}

	@Override
	public void mouseMoved(MouseEvent evt)
	{
		if( _browseGameEnabled &&
			( _chessGame != null ) &&
			! getCursor().getName().equals(CursorFunctions._waitCursor.getName() ) )	// if we are loading or saving, we must not set the hand cursor.
		{
			JEditorPane editor = (JEditorPane) evt.getSource();
			Point pt = new Point(evt.getX(), evt.getY());
			int pos = editor.viewToModel(pt);
			String belowChar = safeGetText(pos, 1);

			if( !Objects.equals( pos, _openingBracketPos ) && !Objects.equals( pos, _closingBracketPos ) )
			{
				restoreOpeningClosingBrackets();

				if( "(".equals( belowChar ) ) //|| ")".equals( belowChar ) )
				{
					markBrackets( pos );
					return;
				}
			}

			if( _chessGame.isValid() ) // if the chessGame does not have intial position, we dont show the hand.)
			{
				ChessGameTreeAdditionalInfo cmtai = _chessGame.getChessGameTreeAdditionalInfo();

				if( cmtai != null )
				{
					WrittenNodeInfo nodeInfo = cmtai.getMoveAdditionalInfo(pos);
					if( ( nodeInfo == null ) ||
							!nodeInfo.isInsideMove(pos) &&
							!nodeInfo.isInsideComment(pos) &&
							!nodeInfo.isInsideCommentForVariant(pos) ||
							nodeInfo.getIsIllegalMove() ||
							nodeInfo.getIsAnyParentIllegalMove() )
					{
						setCursor(sa_defaultCursor);
					}
					else
					{
						setCursor( sa_handCursor );
					}
				}
			}
		}
	}

	public void updateViewPositionInMovesTree()
	{
		if( !_updating && ( _chessGame != null ) )
		{
			_updating = true;

			try
			{
				if( ( getPreviousVariantSelectionMoveTreeNode() != null ) &&
					( getPreviousVariantSelectionMoveTreeNode().getRootNode() == _chessGame.getMoveTreeGame() ) )
				{
					formatCompleteVariant(_NORMAL, getPreviousVariantSelectionMoveTreeNode() );
				}

				if( ( getVariantSelectionMoveTreeNode() != null ) &&
					( getVariantSelectionMoveTreeNode().getRootNode() == _chessGame.getMoveTreeGame() ) )
				{
					formatCompleteVariant( _COPY_SELECTION, getVariantSelectionMoveTreeNode() );
				}

	//			setPreviousVariantSelectionMoveTreeNode(null);

				if( _browseGameEnabled )
				{
					if( ( _previousSelectedMoveNodeInfo != null ) &&
						( _previousSelectedMoveNodeInfo.getMoveTreeNode() != getVariantSelectionMoveTreeNode() ) &&
						!_previousSelectedMoveNodeInfo.getMoveTreeNode().isParent(getVariantSelectionMoveTreeNode()) )
					{
						formatMoveText( _NORMAL, _previousSelectedMoveNodeInfo, false );
					}

					WrittenNodeInfo nodeInfo = _chessGame.getCurrentMove().getAdditionalInfo();

					if( _previousSelectedMove != null )
					{
						if( _previousSelectedMove.getAdditionalInfo().getIsIllegalMove() )
						{
							formatMoveText( _ILLEGAL_MOVE, _previousSelectedMove.getAdditionalInfo(), false );
						}
						else if( ( _previousSelectedMove == getVariantSelectionMoveTreeNode() ) ||
								( _previousSelectedMove.isParent( getVariantSelectionMoveTreeNode() ) ) )
						{
							formatMoveText( _COPY_SELECTION, _previousSelectedMove.getAdditionalInfo(), false );
						}
						else
						{
							formatMoveText( _NORMAL, _previousSelectedMove.getAdditionalInfo(), false );
						}
					}

					if( nodeInfo != null )
					{
						requestFocusIfSuitable();
						setSelectionStart( nodeInfo.getInitialPositionOfMove() );
						setSelectionEnd( nodeInfo.getInitialPositionOfMove() );
						formatMoveText( _SELECTED, nodeInfo, false );
						_previousSelectedMove = _chessGame.getCurrentMove();
					}
					else
					{
						requestFocusIfSuitable();
						setSelectionStart( 0 );
						setSelectionEnd( 0 );
					}

					_previousSelectedMoveNodeInfo = nodeInfo;
				}

				repaint();
				if( _controller != null )
				{
					_controller.updateMoveNavigator();
				}

	//			if( _chessBoardPanel != null )
	//			{
	//				_chessBoardPanel.refresh();
	//			}
			}
			finally
			{
				_updating = false;
			}
		}
	}

	protected void requestFocusIfSuitable()
	{
		Component comp = ComponentFunctions.instance().getFocusedComponent();
		Component compAncestor = ComponentFunctions.instance().getAncestor(comp);

		Component ancestor = ComponentFunctions.instance().getAncestor(this);
		if( ancestor == compAncestor )
			requestFocus();
	}

	protected void formatCompleteVariant( int bt, MoveTreeNode mtn )
	{
		WrittenNodeInfo wni = mtn.getAdditionalInfo();
		formatMoveText( bt, wni, false );

		for( int ii=0; ii<mtn.getNumberOfChildren(); ii++ )
			formatCompleteVariant( bt, mtn.getChild( ii ) );
	}

	protected String getSuffixForSize( boolean bigSize )
	{
		return( bigSize ? BIG_SUFFIX : NORMAL_SIZE_SUFFIX );
	}

	protected String getSuffixForStyleNameFromBackgroundType( int bt )
	{
		String result = "";
		switch( bt )
		{
			case _NORMAL:			result = ""; break;
			case _SELECTED:			result = SELECTED_SUFFIX; break;
			case _COPY_SELECTION:	result = COPY_SELECTION_SUFFIX; break;
			case _ILLEGAL_MOVE:		result = ILLEGAL_MOVE_SUFFIX; break;
			default:
				result = "";
		}
		return( result );
	}

	protected String getStyleName( int styleName )
	{
		String result = "";
		switch( styleName )
		{
			case _PLAIN:				result = PLAIN; break;
			case _BLACK:				result = BLACK; break;
			case _BOLD_BLACK:			result = BOLD_BLACK; break;
			case _BLUE:					result = BLUE; break;
			case _GREEN:				result = GREEN; break;
			default:
				result = "";
		}
		return( result );
	}

	protected Style getStyle( int styleName, int bt, boolean bigSize )
	{
		Style result = null;

		String styleNameStr = getStyleName( styleName );
		String suffixForStyleName = getSuffixForStyleNameFromBackgroundType( bt );

		String suffixForSize = getSuffixForSize( bigSize );

//		result = getStyle( styleNameStr + suffixForStyleName + suffixForSize );
		result = getStyle( styleNameStr + suffixForSize + suffixForStyleName );

		return( result );
	}

	protected Style getStyleCached( int styleName, int bt, boolean bigSize )
	{
//		return( _styles[styleName][bt] );
		return( getStyle(styleName, bt, bigSize) );
	}

	protected void createStyleArrayCached()
	{
		_styles = new Style[_NUMBER_OF_STYLE_NAMES][_NUMBER_OF_BT][2];

		for( int styleName = 0; styleName < _NUMBER_OF_STYLE_NAMES; styleName++ )
			for( int bt = 0; bt < _NUMBER_OF_BT; bt++ )
			{
				for( int size = 0; size < 2; size ++ )
					_styles[styleName][bt][size] = getStyle( styleName, bt, (size==1) );
			}
	}

	protected void formatMoveText( int bt, WrittenNodeInfo wni, boolean hasToFormatComment )
	{
		if( wni.getIsIllegalMove()  )
			bt = _ILLEGAL_MOVE;

		int styleName = -1;
		Style style = null;

		if( //!_chessGame.isValid() ||
			wni.getIsAnyParentIllegalMove() ||
			wni.getIsIllegalMove() )
		{
			styleName = _PLAIN;
		}
		else if( wni.isMainLine() )										styleName = _BOLD_BLACK;
		else if( !wni.isMainLine() )									styleName = _BLUE;

		boolean bigSize = false;

		int tmpStyleName = -1;
		String str = null;
		for( int ii=wni.getInitialPositionOfMove(); ii<wni.getFinalPositionOfMove(); ii++ )
		{
			tmpStyleName = styleName;
			int index = ii;
			str = ExecutionFunctions.instance().safeSilentFunctionExecution( () -> getStyledDocument().getText( index, 1 ) );
			if( str != null )
			{
				bigSize = isBig( str ); // figurine
				if( bigSize )			// figurine cannot be blue
					tmpStyleName = _BLACK;
				formatText( ii, 1, tmpStyleName, bt, bigSize );
			}
		}

		if( hasToFormatComment )
		{
			if( wni.getInitialPositionOfComment() > 0 )
			{
				bigSize = false;

				formatText( wni.getInitialPositionOfComment(),
							wni.getFinalPositionOfComment() - wni.getInitialPositionOfComment() + 1,
							_GREEN, bt, bigSize );
			}
		}
	}

	protected void formatText( int pos, int length, int styleName, int bt, boolean bigSize )
	{
		Style style = getStyleCached( styleName, bt, bigSize );
		getStyledDocument().setCharacterAttributes(pos, length, style, true);
	}

	protected boolean isBig( String str )
	{
		return( StringFunctions.instance().isAnyChar( str, _figurineChars ) );
	}

	protected boolean isDarkMode()
	{
		return( FrameworkComponentFunctions.instance().isDarkMode( this ) );
	}

	protected String translateText( String text )
	{
		String result = text;
		if( isDarkMode() )
			result = StringFunctions.instance().replaceSetOfChars(text, "♔♕♖♗♘♚♛♜♝♞", "♚♛♜♝♞♔♕♖♗♘");

		return( result );
	}

	protected void formatChessGameMoves( String newString )
	{
		_previousSelectedMoveNodeInfo = null;
		setText( translateText( newString ) );

		getStyledDocument().setCharacterAttributes(0, getStyledDocument().getLength(), getStyle( PLAIN ), true);

		ChessGameTreeAdditionalInfo cgtai = _chessGame.getChessGameTreeAdditionalInfo();

		Style green = getStyle( GREEN );
		Style green_copySelection = getStyle( GREEN + COPY_SELECTION_SUFFIX );

		for( int ii=0; ii<cgtai.size(); ii++ )
		{
			WrittenNodeInfo wni = cgtai.getElement(ii);

			int bt = -1;

			if( wni.getIsIllegalMove() )
			{
				bt = _ILLEGAL_MOVE;
			}
			else
			{
				bt = _NORMAL;
			}

//			if( _chessGame.isValid() ) // if we have intial position, we format the moves. If we do not have initial position we do not format, to distinguish
			{
				formatMoveText( bt, wni, false );
			}

			if( wni.getInitialPositionOfCommentForVariant() > 0 )
			{
				getStyledDocument().setCharacterAttributes(wni.getInitialPositionOfCommentForVariant(),
															wni.getFinalPositionOfCommentForVariant() - wni.getInitialPositionOfCommentForVariant() + 1,
															green, true);
			}

			if( wni.getInitialPositionOfComment() > 0 )
			{
				getStyledDocument().setCharacterAttributes(wni.getInitialPositionOfComment(),
															wni.getFinalPositionOfComment() - wni.getInitialPositionOfComment() + 1,
															green, true);
			}
		}
	}

	protected void updateAfterinsertionOfMovesInTree()
	{
		if( _chessGame != null )
		{
			int selectionStart = getSelectionStart();
			int selectionEnd = getSelectionEnd();
			
			String newString = _chessGame.getChessGameTreeAdditionalInfo().getMoveTreeString();
//			if( ! getText().equals( newString ) )
			{
				formatChessGameMoves( newString );
			}
			updateViewPositionInMovesTree();

			if( ! _browseGameEnabled )
			{
				setSelectionStart( IntegerFunctions.min( selectionStart, getText().length() - 1 ) );
				setSelectionEnd( IntegerFunctions.min( selectionEnd, getText().length() ) );
			}
		}
	}

	public void updateEverythingFully()
	{
		addStyles();

		if( _chessGame != null )
		{
			restoreOpeningClosingBrackets();

			int selectionStart = getSelectionStart();
			int selectionEnd = getSelectionEnd();

			String newString = "";

			try
			{
				newString = _chessGame.getChessGameTreeAdditionalInfo().updateAdditionalInfo();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}


//			if( ! getText().equals( newString ) )
			{
				formatChessGameMoves( newString );
			}
			updateViewPositionInMovesTree();

			if( ! _browseGameEnabled )
			{
				setSelectionStart( IntegerFunctions.min( selectionStart, getText().length() - 1 ) );
				setSelectionEnd( IntegerFunctions.min( selectionEnd, getText().length() ) );
			}
		}
	}

	protected void addStyles()
	{
		int defaultFontSize = -1;
		if( getFont() != null )
			defaultFontSize = getFont().getSize();

		addStyles( defaultFontSize );
	}

	protected int getBigSize( int defaultSize )
	{
		return( IntegerFunctions.zoomValueRound( defaultSize, 1.40D ) );
	}
/*
	protected void addStylesForSize( String styleName, StyledDocument sd, Style defaultStyle )
	{
		final Style baseStyle = defaultStyle;
		String newStyleName = styleName + NORMAL_SIZE_SUFFIX;

		final Style newStyle = addStyle(newStyleName, sd, baseStyle );

//		logStyle( newStyle );

		final Style baseStyle2 = newStyle;
		newStyleName = styleName + BIG_SUFFIX;

		final Style newStyle2 = addStyle(newStyleName, sd, baseStyle2 );
		StyleConstants.setFontSize(newStyle2, getBigSize( StyleConstants.getFontSize(baseStyle2) ) );

//		logStyle( newStyle2 );
	}
*/
	protected void logStyle( Style style )
	{
		System.out.println( styleToString( style ) );
	}

	protected void appendAttributeSet( StringBuilder sb, AttributeSet as )
	{
		if( as != null )
		{
			sb.append( "attributes: [ " );
			String separator = "";
			Enumeration attNamesEnum = as.getAttributeNames();
			while( attNamesEnum.hasMoreElements()  )
			{
				Object attName = attNamesEnum.nextElement();
				sb.append( separator )
					.append( "{ name: " ).append( attName )
					.append( ", value: " ).append( as.getAttribute(attName) )
					.append( " }" );
				separator = ", ";
			}

			sb.append( " ]" );
		}
	}

	protected String styleToString( Style style )
	{
		String result = null;
		if( style != null )
		{
			StringBuilder sb = new StringBuilder();

			sb.append( "Style: { name: " ).append( style.getName() )
				.append( ", " );

			appendAttributeSet( sb, style );

			sb.append( " }\n" );

			sb.append( "resolver: { " );
			appendAttributeSet( sb, (AttributeSet) style.getAttribute( "resolver" ) );
			sb.append( " }" );

			result = sb.toString();
		}

		return( result );
	}

	protected Style addStyle( String styleName, StyledDocument sd, Style defaultStyle )
	{
		Style result = null;

		Style tmp = sd.getStyle( styleName );
		if( tmp != null )
			sd.removeStyle( styleName );
		
		result = sd.addStyle( styleName, defaultStyle );

		return( result );
	}

	protected void addStyles( int defaultFontSize )
	{
		addStyles(defaultFontSize, false);
	}

	protected void addStyles( int defaultFontSize, boolean force )
	{
		if( force || ( _previousDefaultFontSize != defaultFontSize ) )
		{
			addStylesInternal( defaultFontSize );
		}
	}

	protected void addStylesInternal( int defaultFontSize )
	{
		StyledDocument sd = getStyledDocument();

		Style defaultStyle = sd.getStyle(StyleContext.DEFAULT_STYLE);

		Style defaultStyleFontSize = sd.addStyle( LOCAL_DEFAULT_STYLE, defaultStyle);
		if( defaultFontSize > 0 )
			StyleConstants.setFontSize(defaultStyleFontSize, defaultFontSize );

		// Create and add the main document style
		final Style black = addStyle(BLACK, sd, defaultStyle);
		StyleConstants.setFontSize(black, StyleConstants.getFontSize( defaultStyleFontSize ) );
		StyleConstants.setForeground(black, DEFAULT_FOREGROUND_COLOR );

//			addStylesForSize( BLACK, sd, black );

		// Create and add the main document style
		final Style bold = addStyle(BOLD_BLACK, sd, defaultStyle);
		StyleConstants.setFontSize(bold, StyleConstants.getFontSize( defaultStyleFontSize ) );
		StyleConstants.setForeground(bold, DEFAULT_FOREGROUND_COLOR );
		StyleConstants.setBold(bold, true);

//			addStylesForSize( BOLD_BLACK, sd, bold );

		final Style green = addStyle(GREEN, sd, defaultStyleFontSize);
		StyleConstants.setForeground(green, GREEN_FOREGROUND_COLOR );
		StyleConstants.setBold(green, false);

//			addStylesForSize( GREEN, sd, green );

		final Style blue = addStyle(BLUE, sd, defaultStyleFontSize);
		StyleConstants.setForeground(blue, BLUE_FOREGROUND_COLOR );
		StyleConstants.setBold(blue, false);

//			addStylesForSize( BLUE, sd, blue );

		final Style plain = addStyle(PLAIN, sd, defaultStyleFontSize);
		StyleConstants.setForeground(plain, DEFAULT_FOREGROUND_COLOR );
		StyleConstants.setBold(plain, false);

//			addStylesForSize( PLAIN, sd, plain );


		String[] styleNames= new String[] { BLACK, BOLD_BLACK, BLUE, GREEN, PLAIN };
		addStyles_size_background( styleNames, SELECTED_SUFFIX, BRIGHT_GRAY_BACKGROUND_COLOR );		// bright grey
		addStyles_size_background( styleNames, ILLEGAL_MOVE_SUFFIX, RED_BACKGROUND_COLOR );		// red
		addStyles_size_background( styleNames, COPY_SELECTION_SUFFIX, YELLOW_BACKGROUND_COLOR );	// yellow

		_previousDefaultFontSize = defaultFontSize;
	}

	protected void addBackgroundStyle( Style baseStyle, String baseStyleName,
										String suffix, Color backGroundColor )
	{
		StyledDocument sd = getStyledDocument();

		String newStyleName = baseStyleName + suffix;

		final Style newStyle = addStyle(newStyleName, sd, baseStyle );
		StyleConstants.setBackground(newStyle, backGroundColor );

//		logStyle( newStyle );
	}

	protected void addStyles_size_background( String[] styleNames, String suffix, Color backGroundColor )
	{
		StyledDocument sd = getStyledDocument();

		for( int ii=0; ii<styleNames.length; ii++ )
		{
			String styleName = styleNames[ii];
			Style baseStyle = sd.getStyle( styleName );
			String newStyleName = styleName + NORMAL_SIZE_SUFFIX;

			Style newStyle = addStyle(newStyleName, sd, baseStyle );
			addBackgroundStyle( newStyle, newStyleName, suffix, backGroundColor );

//		logStyle( newStyle );

			newStyleName = styleName + BIG_SUFFIX;

			newStyle = addStyle(newStyleName, sd, baseStyle );
			StyleConstants.setFontSize(newStyle, getBigSize( StyleConstants.getFontSize(baseStyle) ) );

			addBackgroundStyle( newStyle, newStyleName, suffix, backGroundColor );
//		logStyle( newStyle2 );
		}
	}

	protected boolean gameDoNotHaveInitialPosition()
	{
		return( !_chessGame.isValid() );
	}
	
	protected void doPopup( MouseEvent evt, WrittenNodeInfo nodeInfo )
	{
		_lastClickMoveTreeNodeForPopupMenu = null;
		if( nodeInfo != null )
			_lastClickMoveTreeNodeForPopupMenu = nodeInfo.getMoveTreeNode();	
			
		_popupMenu.doPopup(evt);
	}

	protected void enablePopupMenuItems()
	{
		_popupMenu.setAllEnabled(true);
		
		if( _lastClickMoveTreeNodeForPopupMenu == null )
		{
			_popupMenu.setSelectAndCopyEnabled(false);
			_popupMenu.setEraseEnabled(false);
			_popupMenu.setEditCommentEnabled(false);
		}

		if( getVariantSelectionMoveTreeNode() == null )
		{
			_popupMenu.setClearSelectionEnabled(false);
			_popupMenu.setPasteEnabled(false);
		}
	}

	protected void selectAndCopy()
	{
//		setPreviousVariantSelectionMoveTreeNode( getVariantSelectionMoveTreeNode() );
		setVariantSelectionMoveTreeNode( _lastClickMoveTreeNodeForPopupMenu );
		_lastClickMoveTreeNodeForPopupMenu = null;

//		updateViewPositionInMovesTree();
	}

	protected void clearSelection()
	{
//		setPreviousVariantSelectionMoveTreeNode( getVariantSelectionMoveTreeNode() );
		setVariantSelectionMoveTreeNode( null );
		_lastClickMoveTreeNodeForPopupMenu = null;

//		updateViewPositionInMovesTree();
	}

	protected boolean canSetPosition( ChessGamePosition position )
	{
		return( ( position == null ) || ( position.isStandardInitialPosition() ) );
	}

	protected void pasteSubvariant()
	{
		MoveTreeNode nodeToPaste = getVariantSelectionMoveTreeNode();
		if( nodeToPaste != null )
		{
			MoveTreeNode nodeToPasteTo = _lastClickMoveTreeNodeForPopupMenu;
			if( ( _lastClickMoveTreeNodeForPopupMenu == null ) &&
				( _chessGame.getMoveTreeGame().getNumberOfChildren() == 0 ) )
			{
				nodeToPasteTo = _chessGame.getMoveTreeGame();

				ChessGamePosition gamePos = nodeToPaste.getPreviousPosition();
				if( ( gamePos != null ) && canSetPosition( _chessGame.getInitialPosition() ) )
					_chessGame.setInitialPosition( gamePos );
			}

			if( nodeToPasteTo != null )
			{
				if( (nodeToPasteTo instanceof MoveTreeGame ) ||
					( nodeToPaste.getLevel() ==
						( nodeToPasteTo.getLevel() + 1 ) ) ) 	// if the plyNumber match
				{
					if ( nodeToPasteTo.findChild( nodeToPaste.getMove() ) == null )	// if the child does not exist
					{
						pasteSubvariant_internal( nodeToPasteTo, nodeToPaste );
/*
						try
						{
							_chessGame.updateAdditionalInfo();
						}
						catch( Throwable th )
						{
							th.printStackTrace();
						}
*/
						_controller.newChessGameChosen( _chessGame, true );
	//					updateAfterinsertionOfMovesInTree();
						boolean everyThing = true;
						_parent.updateChessGameWindows(_chessGame, everyThing );

						_lastClickMoveTreeNodeForPopupMenu = null;
					}
					else
					{
						GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, getInternationalString( CONF_COULD_NOT_COPY_ALREADY_A_MOVE ) +
																							": " + nodeToPaste.getMove(),
																							getInternationalString( CONF_PROBLEM_WHILE_COPYING_A_SUBVARIANT ),
																							DialogsWrapper.ERROR_MESSAGE );
					}
				}
				else
				{
					GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, String.format( getInternationalString(CONF_COULD_NOT_COPY_NUMBER_OF_PLY_DOES_NOT_MATCH ),
																			_lastClickMoveTreeNodeForPopupMenu.getLevel(),
																			nodeToPaste.getLevel() ),
																			getInternationalString( CONF_PROBLEM_WHILE_COPYING_A_SUBVARIANT ),
																			DialogsWrapper.ERROR_MESSAGE );
				}
			}
			else
			{
				GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, getInternationalString( CONF_NODE_TO_PASTE_TO_THE_COPIED_SUBVARIANT_NOT_SELECTED ),
																			getInternationalString( CONF_PROBLEM_WHILE_COPYING_A_SUBVARIANT ),
																			DialogsWrapper.ERROR_MESSAGE );
			}
		}
		else
		{
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, getInternationalString(CONF_COULD_NOT_COPY_NO_SUBVARIANT ),
													getInternationalString( CONF_PROBLEM_WHILE_COPYING_A_SUBVARIANT ),
													DialogsWrapper.ERROR_MESSAGE );
		}
	}

	@Override
	public Component getComponent()
	{
		return( this );
	}

	@Override
	public String createCustomInternationalString( String label, Object ... args )
	{
		return( CreateCustomString.instance().createCustomString( getInternationalString( label ), args) );
	}

	protected void pasteSubvariant_internal( MoveTreeNode parent, MoveTreeNode variantToCopy )
	{
		MoveTreeNode mtn = parent.simpleInsert( variantToCopy );

		for( int ii=0; ii<variantToCopy.getNumberOfChildren(); ii++ )
		{
			pasteSubvariant_internal( mtn, variantToCopy.getChild( ii ) );
		}
	}

	protected void erase()
	{
		if( _lastClickMoveTreeNodeForPopupMenu != null )
		{
			MoveTreeNode selectedNode = getVariantSelectionMoveTreeNode();
			
			if( selectedNode != null )
			{
				if( ( selectedNode == _lastClickMoveTreeNodeForPopupMenu ) ||
					( _lastClickMoveTreeNodeForPopupMenu.isParent( selectedNode ) ) ||
					( selectedNode.isParent( _lastClickMoveTreeNodeForPopupMenu ) ) )
				{
					setVariantSelectionMoveTreeNode(null);
					setVariantSelectionMoveTreeNode(null);
				}
			}

			if( ( _chessGame.getCurrentMove() == _lastClickMoveTreeNodeForPopupMenu ) ||
				( _chessGame.getCurrentMove().isParent( _lastClickMoveTreeNodeForPopupMenu ) ) )
			{
				try
				{
					_chessGame.setCurrentMove( _lastClickMoveTreeNodeForPopupMenu.getParent() );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}

			_chessGame.remove( _lastClickMoveTreeNodeForPopupMenu );
//			_lastClickMoveTreeNodeForPopupMenu.remove();

			_controller.newChessGameChosen( _chessGame, true );
/*
			try
			{
				_chessGame.updateAdditionalInfo();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
*/
//			updateAfterinsertionOfMovesInTree();
			boolean everyThing = true;
			_parent.updateChessGameWindows( _chessGame, everyThing );
		}

		_lastClickMoveTreeNodeForPopupMenu = null;

		updateViewPositionInMovesTree();
	}

	protected void setInitialPosition()
	{
		_controller.editInitialPosition( _chessGame );
	}

	protected void editComment( WrittenNodeInfo nodeInfo, Boolean isTypeOfCommentOfMove )
	{
		if( nodeInfo != null )
		{
			changeSelectedMove( nodeInfo, isTypeOfCommentOfMove );
//			_controller.editComment( _chessGame, nodeInfo.getMoveTreeNode(), isTypeOfCommentOfMove );

			EditCommentFrame.instance().setMove( _chessGame, nodeInfo.getMoveTreeNode(), isTypeOfCommentOfMove );
			EditCommentFrame.instance().setVisible(true);
			EditCommentFrame.instance().setState ( Frame.NORMAL );
		}
	}

	@Override
	public void changeLanguage(String language) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		if( _languageConfiguration != null )
			_languageConfiguration.registerInternationalString( label, value );
	}

	@Override
	public String getInternationalString(String label)
	{
		String result = null;

		if( _languageConfiguration != null )
			result = _languageConfiguration.getInternationalString( label );

		return( result );
	}

	public void setLanguageConfiguration( InternationalizedStringConf languageConfiguration )
	{
		_languageConfiguration = languageConfiguration;

		registerInternationalString(CONF_COULD_NOT_COPY_ALREADY_A_MOVE, "Could not copy: position to copy the variant, had already a move" );
		registerInternationalString(CONF_PROBLEM_WHILE_COPYING_A_SUBVARIANT, "Problem while copying a subvariant" );
		registerInternationalString(CONF_COULD_NOT_COPY_NUMBER_OF_PLY_DOES_NOT_MATCH, "Could not copy: number of ply does not match: " +
														"Ply number of parent: %d. Ply number of root of variant to copy %d" );
		registerInternationalString(CONF_COULD_NOT_COPY_NO_SUBVARIANT, "Could not copy: There was no subvariant to copy" );
		registerInternationalString(CONF_NODE_TO_PASTE_TO_THE_COPIED_SUBVARIANT_NOT_SELECTED, "Node to paste to the copied subvariant not selected" );
	}

	@Override
	public DesktopViewComponent getParentViewComponent()
	{
		return( DesktopGenericFunctions.instance().getViewFacilities().getParentViewComponent(this) );
	}

	@Override
	public void doTasksAfterResizingComponent(double zoomFactor)
	{
		if( _currentZoomFactorForComponentResized != zoomFactor )
		{
			updateEverythingFully();
			_currentZoomFactorForComponentResized = zoomFactor;
		}
	}

	@Override
	public void releaseResources() {
		_parent = null;
		_chessGame = null;
		_controller = null;
		_chessBoardPanel = null;
		_previousSelectedMoveNodeInfo = null;
		_lastClickMoveTreeNodeForPopupMenu = null;
		_previousSelectedMove = null;
		_popupMenu = null;
		_browseGameEnabled = false;
		_languageConfiguration = null;
		_figurineChars = null;
		_openingBracketPos = null;
		_closingBracketPos = null;
	}


	protected ColorThemeChangeableBase createColorThemeChangeableBase()
	{
		return( new ColorThemeChangeableBase() );
	}

	protected void invertFormatColors(ColorInversor colorInversor)
	{
		DEFAULT_FOREGROUND_COLOR = colorInversor.invertColor(DEFAULT_FOREGROUND_COLOR);
		GREEN_FOREGROUND_COLOR = colorInversor.invertColor(GREEN_FOREGROUND_COLOR);
		BLUE_FOREGROUND_COLOR = colorInversor.invertColor(BLUE_FOREGROUND_COLOR);
		BRIGHT_GRAY_BACKGROUND_COLOR = colorInversor.invertColor(BRIGHT_GRAY_BACKGROUND_COLOR);
		RED_BACKGROUND_COLOR = colorInversor.invertColor(RED_BACKGROUND_COLOR);
		YELLOW_BACKGROUND_COLOR = colorInversor.invertColor(YELLOW_BACKGROUND_COLOR);
	}

	protected void invertStyleColors( ColorInversor colorInversor )
	{
		invertFormatColors( colorInversor );
		updateStyles();
		updateEverythingFully();
	}

	protected void updateStyles()
	{
		if( _previousDefaultFontSize > 0 )
			addStylesInternal( _previousDefaultFontSize );
		else
			addStyles();
	}

	protected void invertSingleComponentColors(ColorInversor colorInversor)
	{
		colorInversor.invertSingleColorsGen(this);
		invertStyleColors(colorInversor);
	}

	@Override
	public void invertColors( ColorInversor colorInversor)
	{
		invertSingleComponentColors(colorInversor);
	}

	@Override
	public void setBackground( Color bc )
	{
		super.setBackground(bc);
	}


	protected class ContextualMenu extends BaseJPopupMenu
	{
		JMenuItem _menuItem_selectAndCopy = null;
		JMenuItem _menuItem_clearSelection = null;
		JMenuItem _menuItem_paste = null;
		JMenuItem _menuItem_erase = null;
		JMenuItem _menuItem_editComment = null;
		JMenuItem _menuItem_setInitialPosition = null;

		public ContextualMenu()
		{
			super(ChessTreeGameTextPane.this);

			_menuItem_selectAndCopy = new JMenuItem( "Select and copy subvariant" );
			_menuItem_selectAndCopy.setName( "_menuItem_selectAndCopy" );

			_menuItem_clearSelection = new JMenuItem( "Clear selection" );
			_menuItem_clearSelection.setName( "_menuItem_clearSelection" );

			_menuItem_paste = new JMenuItem( "Paste" );
			_menuItem_paste.setName( "_menuItem_paste" );

			_menuItem_erase = new JMenuItem( "Erase" );
			_menuItem_erase.setName( "_menuItem_erase" );

			_menuItem_editComment = new JMenuItem( "Edit Comment" );
			_menuItem_editComment.setName( "_menuItem_editComment" );

			_menuItem_setInitialPosition = new JMenuItem( "Edit Initial position of game" );
			_menuItem_setInitialPosition.setName( "_menuItem_setInitialPosition" );

			addMenuComponent( _menuItem_selectAndCopy );
			addMenuComponent( _menuItem_clearSelection );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_paste );
			addMenuComponent( _menuItem_erase );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_editComment );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_setInitialPosition );
			
			addMouseListenerToAllComponents();
		}

		public void setAllEnabled( boolean value )
		{
			_menuItem_selectAndCopy.setEnabled(value);
			_menuItem_clearSelection.setEnabled(value);
			_menuItem_paste.setEnabled(value);
			_menuItem_erase.setEnabled(value);
			_menuItem_editComment.setEnabled(value);
			_menuItem_setInitialPosition.setEnabled(value);
		}

		public void setSelectAndCopyEnabled( boolean value )
		{
			_menuItem_selectAndCopy.setEnabled(value);
		}

		public void setClearSelectionEnabled( boolean value )
		{
			_menuItem_clearSelection.setEnabled(value);
		}

		public void setPasteEnabled( boolean value )
		{
			_menuItem_paste.setEnabled(value);
		}

		public void setEraseEnabled( boolean value )
		{
			_menuItem_erase.setEnabled(value);
		}

		public void setEditCommentEnabled( boolean value )
		{
			_menuItem_editComment.setEnabled(value);
		}

		public void setInitialPositionEnabled( boolean value )
		{
			_menuItem_setInitialPosition.setEnabled(value);
		}

		@Override
		public void actionPerformed( ActionEvent evt )
		{
			Component comp = (Component) evt.getSource();

			if( comp == _menuItem_selectAndCopy )
				selectAndCopy();
			if( comp == _menuItem_clearSelection )
				clearSelection();
			else if( comp == _menuItem_paste )
				pasteSubvariant();
			else if( comp == _menuItem_erase )
				erase();
			else if( comp == _menuItem_editComment )
			{
				if( _lastClickMoveTreeNodeForPopupMenu != null )
				{
					boolean isTypeOfCommentOfMove = true;
					editComment( _lastClickMoveTreeNodeForPopupMenu.getAdditionalInfo(), isTypeOfCommentOfMove );
				}
			}
			else if( comp == _menuItem_setInitialPosition )
				setInitialPosition();

			setVisible(false);
		}

		@Override
		protected void preparePopupMenuItems() {
			enablePopupMenuItems();
		}

		@Override
		public void setComponentMapper(ComponentMapper mapper) {
			_menuItem_selectAndCopy = mapper.mapComponent( _menuItem_selectAndCopy );
			_menuItem_clearSelection = mapper.mapComponent( _menuItem_clearSelection );
			_menuItem_paste = mapper.mapComponent( _menuItem_paste );
			_menuItem_erase = mapper.mapComponent( _menuItem_erase );
			_menuItem_editComment = mapper.mapComponent( _menuItem_editComment );
			_menuItem_setInitialPosition = mapper.mapComponent( _menuItem_setInitialPosition );

			super.setComponentMapper(mapper);
		}
	}
}
