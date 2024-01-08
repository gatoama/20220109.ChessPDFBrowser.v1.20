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
package com.frojasg1.chesspdfbrowser.view.chess.multiwindowmanager;

import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.view.chess.ChessTreeGameTextPane;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfiguration;
import com.frojasg1.libpdf.viewer.PdfViewer;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.desktop.screen.ScreenFunctions;
import com.frojasg1.general.number.DoubleFunctions;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JFrame;

/**
 *
 * @author Usuario
 */
public class MultiwindowGameManager
{
	protected JFrame _parent;
	protected ChessTreeGameTextPane _parentChessTreeGameTextPane;
	protected BaseApplicationConfiguration _conf;
	protected ChessViewConfiguration _chessViewConfiguration = null;

	protected List< DetachedGameWindow > _gameWindows;

	protected MoveTreeNode _previousVariantSelectionMoveTreeNode = null;
	protected MoveTreeNode _variantSelectionMoveTreeNode = null;

	protected DetachedGameWindow _lastFocusedWindow = null;
	protected Rectangle _lastFocusedWindowBounds = null;

	protected Map< Frame, Integer > _stateOfWindows = null;

	protected PdfViewer _pdfViewer = null;

	public MultiwindowGameManager( JFrame parent,
								BaseApplicationConfiguration applicationConfiguration )
	{
		_parent = parent;

		_gameWindows = new ArrayList< DetachedGameWindow >();
		
		_conf = applicationConfiguration;
		
		if( _conf instanceof ChessViewConfiguration )
			_chessViewConfiguration = (ChessViewConfiguration) _conf;
	}

	public void updateAll()
	{
		_parentChessTreeGameTextPane.updateEverythingFully();

		for( DetachedGameWindow dgw: _gameWindows )
		{
			dgw.update(true);
		}
	}

	public void setParentChessTreeGameTextPane( ChessTreeGameTextPane parentChessTreeGameTextPane )
	{
		_parentChessTreeGameTextPane = parentChessTreeGameTextPane;
	}
	
	public ChessGameControllerInterface getChessGameController()
	{
		return( _parentChessTreeGameTextPane.getChessGameController() );
	}

	public PdfViewer getPdfViewer() {
		return _pdfViewer;
	}

	public void setPdfViewer(PdfViewer _pdfViewer) {
		this._pdfViewer = _pdfViewer;
	}

	public void showPage( String pdfBaseFileName, Integer index )
	{
		if( ( index != null ) && ( _pdfViewer != null ) && ( pdfBaseFileName != null ) )
		{
			if( Objects.equals( pdfBaseFileName, getBaseFileName( _pdfViewer.getPdfFileName() ) ) )
			{
				_pdfViewer.showPage(index);
			}
		}
	}

	protected String getBaseFileName( String fileName )
	{
		return( FileFunctions.instance().getBaseName(fileName) );
	}

	public void giveBackDetachedWindow( DetachedGameWindow window )
	{
		if( window.isVisible() )
		{
			window.setVisible( false );
			if( _lastFocusedWindow == window )
			{
				_lastFocusedWindow = null;
				_lastFocusedWindowBounds = null;
			}
		}
	}

	protected void closeDetachedWindowManager( DetachedGameWindow dgw )
	{
		_gameWindows.remove( dgw );
	}

	public DetachedGameWindow addOrFocusGameWindow( ChessGame cg )
	{
		boolean isNewWindow = false;
		DetachedGameWindow result = null;

		result = lookForGameWindow( cg );

		if( result == null )
		{
			result = lookForInactiveWindow();
			isNewWindow = true;
		}

		if( result == null )
		{
			result = new DetachedGameWindow( this, _conf, this::closeDetachedWindowManager );
			_gameWindows.add( result );
		}

		if( isNewWindow )
			setBoundsToWindow( result );

		result.setChessGame(cg);
		if( _chessViewConfiguration != null )
			result.setAlwaysOnTop( _chessViewConfiguration.getDetachedGameWindowsAlwaysOnTop() );

		result.setVisible( true );
		result.setState ( Frame.NORMAL );

		_lastFocusedWindow = result;
		

		return( result );
	}

	protected int calculateCoordinateInsideScreen( double initialValue, double size, double screenSize )
	{
		int result = (int) DoubleFunctions.instance().limit( initialValue, 0, screenSize - size );
		return( result );
	}

	protected void setBoundsToWindow( DetachedGameWindow dgw )
	{
		Dimension screenSize = ScreenFunctions.getScreenSize( dgw, true );

		if( _lastFocusedWindow == null )
		{
			dgw.setLocation(	calculateCoordinateInsideScreen( _parent.getLocationOnScreen().getX() + 50,
																	dgw.getWidth(),
																	screenSize.getWidth() ),
								calculateCoordinateInsideScreen( _parent.getLocationOnScreen().getY() + 50,
																	dgw.getHeight(),
																	screenSize.getHeight() )
							);
		}
		else
		{
			if( _lastFocusedWindow.isVisible() && ( _lastFocusedWindow.getState() == Frame.NORMAL ) )
				dgw.setBounds(	calculateCoordinateInsideScreen( _lastFocusedWindow.getLocationOnScreen().getX() + 50,
																	_lastFocusedWindow.getWidth(),
																	screenSize.getWidth() ),
								calculateCoordinateInsideScreen( _lastFocusedWindow.getLocationOnScreen().getY() + 50,
																	_lastFocusedWindow.getHeight(),
																	screenSize.getHeight() ),
								(int) _lastFocusedWindow.getWidth(),
								(int) _lastFocusedWindow.getHeight() );
			else
				dgw.setBounds(	calculateCoordinateInsideScreen( _lastFocusedWindowBounds.getX() + 50,
																	_lastFocusedWindow.getWidth(),
																	screenSize.getWidth() ),
								calculateCoordinateInsideScreen( _lastFocusedWindowBounds.getY() + 50,
																	_lastFocusedWindow.getHeight(),
																	screenSize.getHeight() ),
								(int) _lastFocusedWindow.getWidth(),
								(int) _lastFocusedWindow.getHeight() );
		}
	}

	public DetachedGameWindow lookForGameWindow( ChessGame cg )
	{
		DetachedGameWindow result = null;

		Iterator<DetachedGameWindow> it = _gameWindows.iterator();

		while( it.hasNext() && ( result == null ) )
		{
			DetachedGameWindow dgw = it.next();
			if( dgw.isVisible() && ( dgw.getChessTreeGameTextPane().getChessGame() == cg ) )
				result = dgw;
		}

		return( result );
	}

	protected DetachedGameWindow lookForInactiveWindow()
	{
		DetachedGameWindow result = null;

		Iterator<DetachedGameWindow> it = _gameWindows.iterator();

		while( it.hasNext() && ( result == null ) )
		{
			DetachedGameWindow dgw = it.next();
			if( !dgw.isVisible() )
				result = dgw;
		}

		return( result );
	}

	public void updateChessGameWindows( ChessGame cg, boolean everyThing )
	{
		updateChessGamePane( _parentChessTreeGameTextPane, cg, everyThing );

		boolean found = false;
		Iterator<DetachedGameWindow> it = _gameWindows.iterator();
		while( it.hasNext() && !found )
		{
			found = updateChessGamePane( it.next().getChessTreeGameTextPane(), cg, everyThing );
		}
	}

	protected boolean updateChessGamePane( ChessTreeGameTextPane gamePane, ChessGame cg, boolean everyThing )
	{
		boolean result = false;

		if( gamePane.getChessGame() == cg )
		{
			result = true;
			gamePane.update( everyThing );
		}

		return( result );
	}

	public void updateChessGameWindowsFully( ChessGame cg )
	{
		updateChessGamePaneFully( _parentChessTreeGameTextPane, cg );

		boolean found = false;
		Iterator<DetachedGameWindow> it = _gameWindows.iterator();
		while( it.hasNext() && !found )
		{
			found = updateChessGamePaneFully( it.next().getChessTreeGameTextPane(), cg );
		}
	}

	protected boolean updateChessGamePaneFully( ChessTreeGameTextPane gamePane, ChessGame cg )
	{
		boolean result = false;

		if( gamePane.getChessGame() == cg )
		{
			result = true;
			gamePane.updateEverythingFully();
		}

		return( result );
	}
	
	
	public MoveTreeNode getPreviousVariantSelectionMoveTreeNode()
	{
		return( _previousVariantSelectionMoveTreeNode );
	}

/*
	public void setPreviousVariantSelectionMoveTreeNode( MoveTreeNode node )
	{
		_previousVariantSelectionMoveTreeNode = node;
	}
*/	
	public MoveTreeNode getVariantSelectionMoveTreeNode()
	{
		return( _variantSelectionMoveTreeNode );
	}

	public void setVariantSelectionMoveTreeNode( MoveTreeNode node )
	{
		_previousVariantSelectionMoveTreeNode = _variantSelectionMoveTreeNode;
		_variantSelectionMoveTreeNode = node;

		_parentChessTreeGameTextPane.updateViewPositionInMovesTree();

		ChessTreeGameTextPane previousSelectionPane = getTextPaneFromMoveTreeNode( _previousVariantSelectionMoveTreeNode );
		ChessTreeGameTextPane currentSelectionPane = getTextPaneFromMoveTreeNode( _variantSelectionMoveTreeNode );

		if( previousSelectionPane != null )
			previousSelectionPane.updateViewPositionInMovesTree();

		if( ( currentSelectionPane != null ) && ( previousSelectionPane != currentSelectionPane ) )
			currentSelectionPane.updateViewPositionInMovesTree();

		_previousVariantSelectionMoveTreeNode = null;
	}

	protected ChessTreeGameTextPane getTextPaneFromMoveTreeNode( MoveTreeNode node )
	{
		ChessTreeGameTextPane result = null;
		
		if( node != null )
		{
			MoveTreeGame mtg = node.getMoveTreeGame();

			DetachedGameWindow dgw = lookForMoveTreeGame( mtg );
			if( dgw != null )
				result = dgw.getChessTreeGameTextPane();
		}

		return( result );
	}

	public DetachedGameWindow lookForMoveTreeGame( MoveTreeGame mtg )
	{
		DetachedGameWindow result = null;

		Iterator<DetachedGameWindow> it = _gameWindows.iterator();

		while( it.hasNext() && ( result == null ) )
		{
			DetachedGameWindow dgw = it.next();
			if( dgw.isVisible() && ( dgw.getChessTreeGameTextPane().getChessGame().getMoveTreeGame() == mtg ) )
				result = dgw;
		}

		return( result );
	}

	public void closeAllWindows()
	{
		Iterator<DetachedGameWindow> it = _gameWindows.iterator();

		while( it.hasNext() )
		{
			it.next().setVisible(false);
		}
	}
	
	public void setLastFocusedOrMovedWindow( DetachedGameWindow dgw )
	{
		if( dgw.getState() == Frame.NORMAL )
		{
			_lastFocusedWindow = dgw;
			_lastFocusedWindowBounds = new Rectangle( (int) dgw.getLocationOnScreen().getX(),
														(int) dgw.getLocationOnScreen().getY(),
														(int) _lastFocusedWindow.getWidth(),
														(int) _lastFocusedWindow.getHeight() );
		}
	}

	public void setWindowsAlwaysOnTop( boolean value )
	{
		Iterator<DetachedGameWindow> it = _gameWindows.iterator();

		while( it.hasNext() )
		{
			DetachedGameWindow dgw = it.next();
			if( dgw != _lastFocusedWindow )
				dgw.setAlwaysOnTop(value);
		}

		if( _lastFocusedWindow != null )
		{
			_lastFocusedWindow.setAlwaysOnTop( value );
		}
	}

	public void changeToWaitCursor()
	{
		boolean found = false;
		Iterator<DetachedGameWindow> it = _gameWindows.iterator();
		while( it.hasNext() )
		{
			it.next().changeToWaitCursor();
		}
	}

	public void revertChangeToWaitCursor()
	{
		boolean found = false;
		Iterator<DetachedGameWindow> it = _gameWindows.iterator();
		while( it.hasNext() )
		{
			it.next().revertChangeToWaitCursor();
		}
	}

	public void applyNewConfiguration()
	{
		_parentChessTreeGameTextPane.updateEverythingFully();

		Iterator<DetachedGameWindow> it = _gameWindows.iterator();

		while( it.hasNext() )
		{
			DetachedGameWindow dgw = it.next();

			if( _chessViewConfiguration != null )
				dgw.setAlwaysOnTop( _chessViewConfiguration.getDetachedGameWindowsAlwaysOnTop() );

			boolean everything = true;
			dgw.update( everything );
		}
	}

	protected void saveStateOfWindowAndSetNewState( Frame frame, Integer state )
	{
		if( frame != null )
		{
			int previousState = frame.getState();

			if( previousState != Frame.ICONIFIED )
				_stateOfWindows.put( frame, previousState );

			if( state != null )
				frame.setState( state );
		}
	}

	public void saveStateOfOpenWindowsAndIconifyThem()
	{
		_stateOfWindows = new HashMap< Frame, Integer >();

		Iterator<DetachedGameWindow> it = _gameWindows.iterator();

		while( it.hasNext() )
		{
			DetachedGameWindow dgw = it.next();

			saveStateOfWindowAndSetNewState( dgw, Frame.ICONIFIED );
		}
	}

	public void restoreStateOfOpenWindows()
	{
		if( _stateOfWindows != null )
		{
			Iterator< Map.Entry< Frame, Integer> > it = _stateOfWindows.entrySet().iterator();

			while( it.hasNext() )
			{
				Map.Entry< Frame, Integer > entry = it.next();
				Frame frame = entry.getKey();

				if( frame.isVisible() )
					frame.setState( entry.getValue() );
			}

			_stateOfWindows = null;
		}
	}
}



