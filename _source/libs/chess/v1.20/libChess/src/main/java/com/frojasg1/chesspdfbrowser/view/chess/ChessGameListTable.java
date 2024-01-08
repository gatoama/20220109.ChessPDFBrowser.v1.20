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
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.view.chess.renderer.cellrenderer.JLabelRenderer;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.chesspdfbrowser.view.chess.multiwindowmanager.MultiwindowGameManager;
import com.frojasg1.desktop.libtablecolumnadjuster.TableColumnAdjuster;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.CreateCustomString;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Usuario
 */
public class ChessGameListTable extends JTable
	implements InternationalizedStringConf, ResizableComponentInterface
{
	protected static final String CONF_CREATE_NEW_GAME_AT_THE_END = "CREATE_NEW_GAME_AT_THE_END";
	protected static final String CONF_CREATE_GAME_BEFORE = "CREATE_GAME_BEFORE";
	protected static final String CONF_CREATE_GAME_AFTER = "CREATE_GAME_AFTER";
	protected static final String CONF_OPEN_SELECTED_GAME_DETACHED = "OPEN_SELECTED_GAME_DETACHED";
	protected static final String CONF_MOVE_SELECTED_GAME = "MOVE_SELECTED_GAME";
	protected static final String CONF_ERASE_SELECTED_GAME = "ERASE_SELECTED_GAME";
	protected static final String CONF_OPEN_SELECTED_GAMES_DETACHED = "OPEN_SELECTED_GAMES_DETACHED";
	protected static final String CONF_MOVE_SELECTED_GAMES = "MOVE_SELECTED_GAMES";
	protected static final String CONF_ERASE_SELECTED_GAMES = "ERASE_SELECTED_GAMES";
	protected static final String CONF_OPEN_TAG_REGEX_CONF = "OPEN_TAG_REGEX_CONF";
	protected static final String CONF_ANALYZE_GAME = "ANALYZE_GAME";

	protected MultiwindowGameManager _manager = null;
	
	protected List<ChessGame> _list = null;
	
	protected ChessGameControllerInterface _controller = null;
	protected ChessViewConfiguration _chessViewConfiguration = null;

	protected Object[] _columnTitles = null;

	protected ContextualMenu _popupMenu = null;

	protected int _rightClickRow = -1;
	protected int _rightClickColumn = -1;
	protected int[] _selectionBeforeRightClick = null;

	protected int y_coordinateOfInsertionPosition = -1;

	protected MouseListener _mouseListener = null;

	protected InternationalizedStringConf _languageConfiguration = null;

	protected double _currentZoomFactorForComponentResized = 1.0D;

	protected Boolean _hasToAddControl = null;

	protected Integer _currentGameIndex = null;
	protected Integer _rowToHighlight = null;

	protected BaseApplicationConfigurationInterface _appliConf;

	public ChessGameListTable( ChessGameControllerInterface controller, ChessViewConfiguration cvc,
								MultiwindowGameManager manager,
								BaseApplicationConfigurationInterface appliConf)
	{
		_appliConf = appliConf;
		_controller = controller;

		setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

//		System.out.println( "ChessGameListTable, addMouseListener" );

		this.setSelectionBackground( Colors.COOL_BLUE );
		this.setSelectionForeground( Color.YELLOW.brighter() );

		_chessViewConfiguration = cvc;
		_manager = manager;
	}

	public JPopupMenu getJPopupMenu()
	{
		return( _popupMenu );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	/**
	 * Before initializing this object, the parent must be set.
	 * If not it will not work properly.
	 */
	public void initialize()
	{
		_mouseListener = new ClickListener( this );
		addMouseListener( _mouseListener );
		getTableHeader().addMouseListener( _mouseListener );

		if( getParent() != null )
			getParent().addMouseListener( _mouseListener );

		_popupMenu = new ContextualMenu( this );
	}

	public void setChessGame( ChessGame cg )
	{
		setCurrentIndex( _list.indexOf(cg) );
	}

	protected void setCurrentIndex(Integer index)
	{
		if( index == null )
			_currentGameIndex = index;
		else if( ( index > -1 ) && ( index < _list.size() ) )
		{
			_currentGameIndex = index;
//			setRowSelectionInterval( index, index );
			scrollRectToVisible(new Rectangle(getCellRect(index, 0, true)));
		}

		repaint();
	}

	public void setChessGameList( List<ChessGame> list )
	{
//		if( _list != list )
		{
//			System.out.println( "setChessGameList, _list != list ..." );

			_hasToAddControl = null;
			_list = list;

			updateData();

			if( _list.size() > 0 )
			{
				setCurrentIndex( 0 );
				setRowSelectionInterval( 0, 0 );
			}
			else
				setCurrentIndex( null );
		}
	}

	public void updateData()
	{
//		System.out.println( "showData ..." );

		int selectedRow = getSelectedRow();

		showData();

		if( ( selectedRow >= 0 ) && ( _list.size() > selectedRow ) )
			setRowSelectionInterval( selectedRow, selectedRow );
//		System.out.println( "setRowSelectionInterval ..." );
	}

	protected boolean hasToAddControl()
	{
		if( _hasToAddControl == null )
		{
			_hasToAddControl = false;

			if( ( _list != null ) && !_list.isEmpty() )
				for( ChessGame game: _list )
					if( game.getChessGameHeaderInfo().getControlName() != null )
					{
						_hasToAddControl = true;
						break;
					}
		}

		return( _hasToAddControl );
	}

	protected void showData()
	{
		DefaultTableModel dtm = new DefaultTableModel();

//		System.out.println( "setColumnIdentifiers ..." );

		boolean hasToAddControl = hasToAddControl();
		dtm.setColumnIdentifiers( getColumnIds(hasToAddControl) );

		if( _list != null )
		{
//		System.out.println( "setRows ..." );
			int index = 1;
			for( ChessGame cg: _list )
			{
				Object[] row = getRow( index, cg, hasToAddControl );
				dtm.addRow( row );
				index++;
			}
		}
//		System.out.println( "setModel ..." );

		setModel( dtm );

//		getTableHeader().addMouseListener( _mouseListener );

//		System.out.println( "setDefaultEditor ..." );

		TableCellRenderer renderer = createCellRenderer();
		for (int ii = 0; ii < getColumnCount(); ii++)
		{
			Class<?> col_class = getColumnClass(ii);
			setDefaultEditor(col_class, null);        // remove editor
			setDefaultRenderer( col_class, renderer );
		}

//		System.out.println( "AdjustColumnWidths ..." );

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				M_adjustColumnWidths();
			}
		});
	}

	protected boolean wasLatestModeDark()
	{
		return( FrameworkComponentFunctions.instance().wasLatestModeDark(this) );
	}

	protected boolean isGameShown( int row, int column )
	{
		return( Objects.equals( row, _currentGameIndex ) );
	}

	protected Color getBackgroundColorForGameShown( JTable table, boolean isSelected, int row, int column )
	{
		Color result = null;
		if( isSelected )
			result = Colors.PURPLE;
		else
			result = Colors.BUTAN;

		return( result );
	}

	protected Color getForegroundColorForGameShown( JTable table, boolean isSelected, int row, int column )
	{
		Color result = null;
		if(isSelected)
			result = table.getSelectionForeground();
		else
			result = table.getForeground();

		return( result );
	}

	protected ColorInversor getColorInversor()
	{
		return( FrameworkComponentFunctions.instance().getColorInversor(this) );
	}

	protected TableCellRenderer createCellRenderer()
	{
		TableCellRenderer result = new JLabelRenderer( getFont() ) {

			protected Color getBackgroundColor( JTable table, boolean isSelected, int row, int column )
			{
				Color result = null;

				if( isGameShown( row, column ) )
					result = getBackgroundColorForGameShown( table, isSelected, row, column );
				else
					result = super.getBackgroundColor(table, isSelected, row, column);

//				if( isDarkMode() )
//					result = getColorInversor().invertColor(result);

				return( result );
			}

			protected Color getForegroundColor( JTable table, boolean isSelected, int row, int column )
			{
				Color result = null;

				if( isGameShown( row, column ) )
					result = getForegroundColorForGameShown( table, isSelected, row, column );
				else
					result = super.getForegroundColor(table, isSelected, row, column);

//				if( isDarkMode() )
//					result = getColorInversor().invertColor(result);

				return( result );
			}
		};

		if( wasLatestModeDark() )
			result = getColorInversor().createTableCellRendererColorInversor(result, "cells");

		return( result );
	}

	protected Object[] getRow( int index, ChessGame chessGame, boolean hasToAddControl )
	{
		Object[] titles = getColumnIds(hasToAddControl);
		Object[] result = new Object[ titles.length ];

		for( int ii=0; ii<titles.length; ii++ )
		{
			String columnTitle = (String) titles[ii];
			String value = null;
			
			if( columnTitle.equals( "Num" ) )			value = String.valueOf( index );
			else if( columnTitle.equals( "Moves" ) )	value = String.valueOf( chessGame.getNumberOfMovesOfMainLine() );
			else if( columnTitle.equals( "Control" ) )	value = chessGame.getChessGameHeaderInfo().getControlName();
			else
			{
				value = chessGame.getChessGameHeaderInfo().get( columnTitle );
			}

			if( value == null )
				value = "";
			
			result[ii] = value;
		}
		return( result );
	}

	protected Object[] getColumnIds( boolean hasToAddControl )
	{
		int numElems = 12;
		if( hasToAddControl )
			numElems = 13;

		if( ( _columnTitles == null ) || ( _columnTitles.length != numElems ) )
		{
			if( ! hasToAddControl )
			{
				_columnTitles = new String[12];

				_columnTitles[0] = "Num";
				_columnTitles[1] = "White";
				_columnTitles[2] = "WhiteElo";
				_columnTitles[3] = "Black";
				_columnTitles[4] = "BlackElo";
				_columnTitles[5] = "Event";
				_columnTitles[6] = "Site";
				_columnTitles[7] = "Round";
				_columnTitles[8] = "Date";
				_columnTitles[9] = "Result";
				_columnTitles[10] = "ECO";
				_columnTitles[11] = "Moves";
			}
			else
			{
				_columnTitles = new String[13];

				_columnTitles[0] = "Num";
				_columnTitles[1] = "White";
				_columnTitles[2] = "WhiteElo";
				_columnTitles[3] = "Black";
				_columnTitles[4] = "BlackElo";
				_columnTitles[5] = "Control";
				_columnTitles[6] = "Event";
				_columnTitles[7] = "Site";
				_columnTitles[8] = "Round";
				_columnTitles[9] = "Date";
				_columnTitles[10] = "Result";
				_columnTitles[11] = "ECO";
				_columnTitles[12] = "Moves";
			}
		}

		return( _columnTitles );
	}

	protected double getZoomFactor()
	{
		return( _controller.getAppliConf().getZoomFactor() );
	}

	protected void M_adjustColumnWidths()
	{
//		System.out.println( "TableColumnAdjuster ..." );
			
		TableColumnAdjuster tca = new TableColumnAdjuster(this);

		try
		{
//			System.out.println( "setMaxWidthAllowed ..." );
			
			int maxWidth = IntegerFunctions.zoomValueCeil( 250, getZoomFactor() );
			tca.setMaxWidthAllowed(maxWidth);
		}
		catch( Throwable th )
		{
		}

//		System.out.println( "adjustColumns ..." );
			
		tca.adjustColumns();
	}

	protected void doPopup( MouseEvent evt )
	{
		_popupMenu.doPopup(evt);
	}

	protected boolean isGameAlreadyOpen( ChessGame cg )
	{
		boolean result = false;

		result = ( _manager.lookForMoveTreeGame( cg.getMoveTreeGame() ) != null );

		return( result );
	}

	protected boolean destinationRowInsideSelection()
	{
		return( (_selectionBeforeRightClick == null) ||
				(_selectionBeforeRightClick.length==0) ||
				IntegerFunctions.isPresentInArray( _rightClickRow, _selectionBeforeRightClick ) );
	}

	protected boolean allGamesAreInvalid()
	{
		boolean result = true;
		
		if( _selectionBeforeRightClick != null )
		{
			for( int ii=0; (ii<_selectionBeforeRightClick.length) && result; ii++ )
				result = selectedGameInvalid( _selectionBeforeRightClick[ii] );
		}

		return( result );
	}

	protected boolean selectedGameInvalid( int index )
	{
		boolean result = true;

		int selectedRow = convertRowIndexToModel(_rightClickRow);
		if( (_list != null) && ( selectedRow<_list.size() ) && ( selectedRow >= 0 ) )
		{
			ChessGame cg = _list.get( selectedRow );
			result = false;
//			result = isGameAlreadyOpen( cg );
		}

		return( result );
	}
	
	protected boolean selectedGamesAreAtTheEndOfTheList()
	{
		boolean result = true;

		if( (_list != null) &&
			( _selectionBeforeRightClick != null ) &&
			( _selectionBeforeRightClick.length > 0 ) &&
			( _selectionBeforeRightClick[_selectionBeforeRightClick.length-1]<(_list.size() - 1) ) )
		{
			result = false;
		}

		return( result );
	}

	protected void openGameDetached()
	{
		browseSelectedGames( (selectedRow) -> {
					ChessGame cg = _list.get( selectedRow );
					_manager.addOrFocusGameWindow(cg);
				});
	}

	protected Integer getFirstSelectedRowIndex()
	{
		AtomicReference<Integer> ai = new AtomicReference<>();
		browseSelectedGames( (selectedRow) -> {
					if( ai.get() == null )
						ai.set(selectedRow);
				});
		return( ai.get() );
	}

	protected void browseSelectedGames( Consumer<Integer> functionForSelectedIndex )
	{
		if( (_list != null)  &&
			( _selectionBeforeRightClick != null ) )
		{
			for( int ii=0; ii<_selectionBeforeRightClick.length; ii++ )
			{
				int selectedRow = convertRowIndexToModel(_selectionBeforeRightClick[ii]);

				if( ( selectedRow >= 0 ) && ( selectedRow<_list.size() ) )
					functionForSelectedIndex.accept( selectedRow );
			}
		}
	}

	@Override
	public void paintComponent(Graphics gc)
	{
		synchronized( this )
		{
			super.paintComponent( gc );

			if( y_coordinateOfInsertionPosition > -1 )
			{
				showInsertionPosition( gc, y_coordinateOfInsertionPosition );
			}

			drawRectAtFirstSelectedLine(gc);

			y_coordinateOfInsertionPosition = -1;
		}
	}

	protected void drawRectAtFirstSelectedLine(Graphics gc)
	{
		if( _rowToHighlight != null )
		{
			Rectangle rect = getCellRect(_rowToHighlight, 0, true);
			Rectangle rect2 = getCellRect(_rowToHighlight, getModel().getColumnCount()-1, true);
			int width = rect2.x - rect.x + rect2.width;
			ImageFunctions.instance().drawRect(gc, rect.x, rect.y, width, rect.height, Color.RED, getZoomedValue(-2) );

			_rowToHighlight = null;
		}
	}

	protected int getZoomedValue( int value )
	{
		return( IntegerFunctions.zoomValueRound(value, getAppliConf().getZoomFactor() ) );
	}

	protected void showInsertionPosition( Graphics gc, int yy )
	{
		gc.setColor( Color.RED );
		ImageFunctions.instance().drawBoldStraightLine(gc, 0, yy, getWidth()-1, yy, getZoomedValue(2) );
//		gc.drawLine( 0, yy, getWidth()-1, yy );
//		gc.drawLine( 0, yy+1, getWidth()-1, yy+1 );
	}

	protected void moveSelectedGames()
	{
		List<ChessGame> listOfSelectedGames = getListOfSelectedGamesRemovingThem();
		if( listOfSelectedGames.size() > 0 )
		{
			ChessGame firstSelectedItem = listOfSelectedGames.get(0);
			
			int positionToInsert = calculatePositionToInsertGames( _rightClickRow );

			_list.addAll( positionToInsert, listOfSelectedGames );
			updateData();

			int index = _list.indexOf( firstSelectedItem );
			changeSelection(index, 0, false, false);
			changeSelection(index+listOfSelectedGames.size()-1, 0, false, true);
			
			_controller.setHasBeenModified(true);
		}
	}

	protected void eraseSelectedGames()
	{
		int index = _rightClickRow;
		getListOfSelectedGamesRemovingThem();
		updateData();
		
		if( _list.size() == 0 )
			createGame( 0 );
		else
		{
			if( index < 0 )
				index = 0;
			else if( index >= _list.size() )
				index = _list.size()-1;
			
			changeSelection(index, 0, false, false);
			setNewCurrentGame( index );

			_controller.setHasBeenModified(true);
		}
	}

	protected List<ChessGame> getListOfSelectedGamesRemovingThem()
	{
		List<ChessGame> result = new ArrayList<ChessGame>();

		if( ( _selectionBeforeRightClick != null ) && ( _selectionBeforeRightClick.length > 0 ) )
		{
			ListIterator< ChessGame > it = _list.listIterator( _selectionBeforeRightClick[0] );

			int nextIndex = _selectionBeforeRightClick[0];

			while( it.hasNext() && ( nextIndex <= _selectionBeforeRightClick[_selectionBeforeRightClick.length-1] ) )
			{
				result.add( it.next() );
				it.remove();
				nextIndex++;
			}
		}

		return( result );
	}

	protected int calculatePositionToInsertGames( int destinationPositionPreviousToTheExtractionOfGames )
	{
		int result = destinationPositionPreviousToTheExtractionOfGames;

		if( ( _selectionBeforeRightClick != null ) && ( _selectionBeforeRightClick.length > 0 ) )
		{
			if( result >= convertRowIndexToModel( _selectionBeforeRightClick[ _selectionBeforeRightClick.length -1 ] ) )
			{
				result = result - _selectionBeforeRightClick.length + 1;		// +1 because we want to insert it after the current row.
			}
		}

		return( result );
	}

	protected void setInitialPosition()
	{
		if( ( _selectionBeforeRightClick != null ) && ( _selectionBeforeRightClick.length > 0 ) )
		{
			int index = convertRowIndexToModel( _selectionBeforeRightClick[ 0 ] );
			if( index < _list.size() )
			{
				ChessGame cg = _list.get( index );

				_controller.editInitialPosition( cg );
			}
		}
	}

	protected ChessGame createGame_internal( int index )
	{
		ChessGame cg = null;
		
		try
		{
			cg = new ChessGame( _chessViewConfiguration );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		
		if( cg != null )
		{
			_list.add( index, cg );
		}
		
		return( cg );
	}

	protected ChessGame getGameAtRightClick()
	{
		return( getGameAtRowClick(_rightClickRow) );
	}

	protected ChessGame getFirstSelectedGameAtRightClick()
	{
		ChessGame result = null;
		
		if( ( _selectionBeforeRightClick != null ) &&
			( _selectionBeforeRightClick.length > 0 ) )
		{
			result = getGameAtRowClick( _selectionBeforeRightClick[0] );
		}
		if( result == null )
			result = getGameAtRightClick();

		return( result );
	}

	protected ChessGame getGameAtRowClick( Integer rowNum )
	{
		ChessGame result = null;
		if( (rowNum != null) && (_list != null ) )
		{
			int index = convertRowIndexToModel( rowNum );
			if( ( index >= 0 ) && ( index < _list.size() ) )
				result = _list.get( index );
		}

		return( result );
	}

	public void createGame( int unitsToAddToIndex )
	{
		int index = convertRowIndexToModel( _rightClickRow ) + unitsToAddToIndex;
		index = IntegerFunctions.max( 0, IntegerFunctions.min( index, _list.size() ) );

		createGame_internal( index );
		showData();
		changeSelection(index, 0, false, false);
		setNewCurrentGame( index );

		_controller.setHasBeenModified(true);
	}

	protected void createGameBefore()
	{
		createGame( 0 );
	}

	protected void createGameAfter()
	{
		createGame( 1 );
	}

	protected Rectangle getRightClickCellRect()
	{
		return( getCellRect(_rightClickRow,_rightClickColumn,true) );
	}

	protected int getRightClickYcoordinate( boolean atTop )
	{
		int result = -1;

		Rectangle rect = getRightClickCellRect();

		if( rect != null )
		{
			if( !atTop )
			{
				result = (int) ( rect.getY() + rect.getHeight() - 2 );
			}
			else
			{
				result = (int) rect.getY();
			}
		}

		return( result );
	}

	protected boolean setNewCurrentGame( int index )
	{
		boolean success = false;

		if( ( _controller != null ) && ( _list != null ) &&
			( index < _list.size() ) && ( index >= 0 ) )
		{
			_currentGameIndex = index;
			_controller.newChessGameChosen( _list.get( index ), false );
			SwingUtilities.invokeLater( () -> repaint() );
			success = true;
		}

		return( success );
	}

	protected void updateChessGame( ChessGame cg )
	{
		updateTableRow( cg );
	}

	public void updateTableRow( ChessGame cg )
	{
		int index = _list.indexOf(cg);

		if( ( index >= 0 ) && ( index < _list.size() ) )
		{
			TableModel model = getModel();

			Object[] row = this.getRow( index+1, cg, hasToAddControl() );

			boolean hasToResizeColumns = false;
			for( int ii=0; ii<row.length; ii++ )
			{
				if( !hasToResizeColumns )
				{
					String stringInList = (String) model.getValueAt( index, ii );
					String stringToSet = (String) row[ii];

					hasToResizeColumns = !stringInList.equals( stringToSet );
				}
				
				model.setValueAt( row[ii], index, ii );
			}
			
			if( hasToResizeColumns )
			{
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						M_adjustColumnWidths();
					}
				});
			}
		}
	}

	@Override
	public String createCustomInternationalString( String label, Object ... args )
	{
		return( CreateCustomString.instance().createCustomString( getInternationalString( label ), args) );
	}

	@Override
	public void doTasksAfterResizingComponent(double zoomFactor)
	{
		SwingUtilities.invokeLater( () -> {
				if( _currentZoomFactorForComponentResized != zoomFactor )
				{
					updateData();
					_currentZoomFactorForComponentResized = zoomFactor;
				}
			});
	}

	protected ProfileModel getProfileModelOfRightClickGame()
	{
		ProfileModel result = null;

		ChessGame game = getFirstSelectedGameAtRightClick();
		if( game != null )
		{
			result = game.getChessGameHeaderInfo().getProfileModel();
		}

		return( result );
	}

	public int insertGameAfter_internal( ChessGame gameToBeInserted, ChessGame afterThis )
	{
		int index = IntegerFunctions.max( 0, _list.indexOf( afterThis ) );
		_list.add( index, gameToBeInserted );
		
		return( index );
	}

	public void insertGameAfter( ChessGame gameToBeInserted, ChessGame afterThis )
	{
		int index = insertGameAfter_internal( gameToBeInserted, afterThis );

		showData();
		changeSelection(index, 0, false, false);
		setNewCurrentGame( index );

		_controller.setHasBeenModified(true);
	}

	protected void analyzeGame()
	{
		int index = convertRowIndexToModel( _rightClickRow );
		index = IntegerFunctions.max( 0, IntegerFunctions.min( index, _list.size() - 1 ) );
		ChessGame cg = _list.get(index);

		_controller.analyzeGame(cg);
	}

	protected void openTagRegexConfiguration()
	{
		ProfileModel profile = getProfileModelOfRightClickGame();

		if( profile != null )
			_controller.openConfiguration(true, profile,
							(ViewComponent) ComponentFunctions.instance().getAncestor( this ) );
	}

	protected class ClickListener extends com.frojasg1.general.desktop.controller.ClickListener
	{
		protected JTable _parent = null;

		public ClickListener( JTable parent )
		{
			_parent = parent;
		}
		
		@Override
		public void doubleClick(MouseEvent e)
		{
			Component comp = (Component) e.getSource();

			if( comp == _parent )
			{
				int[] selection = getSelectedRows();
				if( (selection != null) && (selection.length > 0) )
				{
					int selectedRow = convertRowIndexToModel(selection[0]);
					setNewCurrentGame( selectedRow );
				}
			}
		}

		@Override
		public void rightClick( MouseEvent evt )
		{
			Component comp = (Component) evt.getSource();

			_selectionBeforeRightClick = getSelectedRows();

			if( comp == _parent )
			{
				_rightClickRow = rowAtPoint( evt.getPoint() );
				_rightClickColumn = columnAtPoint( evt.getPoint() );
			}
			else if( comp == _parent.getParent() )
			{
				Point modifiedPoint = new Point( 5, (int) ( evt.getPoint().getY() - getTableHeader().getHeight() ) );

				_rightClickRow = rowAtPoint( modifiedPoint );
				
				if( _rightClickRow < 0 )
					_rightClickRow = _list.size()-1;

				_rightClickColumn = 0;
			}
			else if( comp == _parent.getTableHeader() )
			{
				_rightClickRow = 0;
				_rightClickColumn = 0;
			}

/*
			if (! isRowSelected(_rightClickRow))
				changeSelection(_rightClickRow, column, false, false);
*/
			if( ( _selectionBeforeRightClick == null ) ||
				( _selectionBeforeRightClick.length == 0 ) )
			{
				setRowSelectionInterval( _rightClickRow, _rightClickRow );
				_selectionBeforeRightClick = new int[] {_rightClickRow};
			}

			doPopup( evt );
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
		_languageConfiguration.registerInternationalString( label, value );
	}

	public void setLanguageConfiguration( InternationalizedStringConf languageConfiguration )
	{
		_languageConfiguration = languageConfiguration;

		registerInternationalString(CONF_CREATE_NEW_GAME_AT_THE_END, "Create New Game At The End Of List" );
		registerInternationalString(CONF_CREATE_GAME_BEFORE, "Create Game Before" );
		registerInternationalString(CONF_CREATE_GAME_AFTER, "Create Game After" );
		registerInternationalString(CONF_OPEN_SELECTED_GAME_DETACHED, "Open Selected Game Detached" );
		registerInternationalString(CONF_MOVE_SELECTED_GAME, "Move Selected Game" );
		registerInternationalString(CONF_ERASE_SELECTED_GAME, "Erase Selected Game" );
		registerInternationalString(CONF_OPEN_SELECTED_GAMES_DETACHED, "Open Selected Games Detached" );
		registerInternationalString(CONF_MOVE_SELECTED_GAMES, "Move Selected Games" );
		registerInternationalString(CONF_ERASE_SELECTED_GAMES, "Erase Selected Games" );
		registerInternationalString(CONF_OPEN_TAG_REGEX_CONF, "Profile regex configuration of game tags" );
		registerInternationalString(CONF_ANALYZE_GAME, "Analyze game" );
	}

	@Override
	public String getInternationalString(String label)
	{
		String result = null;

		if( _languageConfiguration != null )
			result = _languageConfiguration.getInternationalString( label );

		return( result );
	}

	protected class ContextualMenu extends BaseJPopupMenu
	{
		ChessGameListTable _table = null;

		JMenuItem _menuItem_openGameDetached = null;
		JMenuItem _menuItem_moveSelectedGames = null;
		JMenuItem _menuItem_eraseSelectedGames = null;
		JMenuItem _menuItem_setInitialPosition = null;
		JMenuItem _menuItem_createGameBefore = null;
		JMenuItem _menuItem_createGameAfter = null;
		JMenuItem _menuItem_analyzeGame = null;
		JMenuItem _menuItem_goProfileOfTagsExtraction = null;

		public ContextualMenu( ChessGameListTable table )
		{
			super(ChessGameListTable.this);

			_menuItem_openGameDetached = new JMenuItem( "Open Selected Games Detached" );
			_menuItem_moveSelectedGames = new JMenuItem( "Move Selected Games" );
			_menuItem_eraseSelectedGames = new JMenuItem( "Erase Selected Games" );
			_menuItem_setInitialPosition = new JMenuItem( "Edit Initial Position of Game" );
			_menuItem_setInitialPosition.setName( "_menuItem_setInitialPosition" );
			_menuItem_createGameBefore = new JMenuItem( "Create Game Before" );
			_menuItem_createGameAfter = new JMenuItem( "Create Game After" );
			_menuItem_analyzeGame = new JMenuItem( "Analyze game" );
			_menuItem_goProfileOfTagsExtraction =  new JMenuItem( "Profile regex configuration of game tags" );

			addMenuComponent( _menuItem_setInitialPosition );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_openGameDetached );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_moveSelectedGames );
			addMenuComponent( _menuItem_eraseSelectedGames );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_createGameBefore );
			addMenuComponent( _menuItem_createGameAfter );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_analyzeGame );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_goProfileOfTagsExtraction );

			_menuItem_openGameDetached.setName( "_menuItem_openGameDetached" );
			_menuItem_goProfileOfTagsExtraction.setName( "_menuItem_goProfileOfTagsExtraction" );

			_table = table;

			addMouseListenerToAllComponents();
		}

		protected void preparePopupMenuItems()
		{
			setAllEnabled(true);

			int numberOfRowsSelected = 0;
			if( _selectionBeforeRightClick != null )
				numberOfRowsSelected = _selectionBeforeRightClick.length;

			setNumberOfSelectedGames(numberOfRowsSelected);

			if( destinationRowInsideSelection() )
				setMoveSelectedGamesEnabled(false);

			if( allGamesAreInvalid() )
				setOpenGameDetachedEnabled( false );

			setOpenProfileRegexConfigurationEnabled( getProfileModelOfRightClickGame() != null );

			int selectedRow = convertRowIndexToModel(_rightClickRow);
			if( ( selectedRow < 0 ) || ( selectedRow >= _table._list.size() ) )
			{
				_menuItem_createGameBefore.setText( getInternationalString( CONF_CREATE_NEW_GAME_AT_THE_END ) );
				_menuItem_createGameAfter.setText( getInternationalString( CONF_CREATE_NEW_GAME_AT_THE_END ) );
				_menuItem_createGameBefore.setEnabled(false);
			}
			else
			{
				_menuItem_createGameBefore.setText( getInternationalString( CONF_CREATE_GAME_BEFORE ) );
				_menuItem_createGameAfter.setText( getInternationalString( CONF_CREATE_GAME_AFTER ) );
			}

			_menuItem_analyzeGame.setText( getInternationalString( CONF_ANALYZE_GAME ) );

//			_menuItem_goProfileOfTagsExtraction.setText( getInternationalString( CONF_OPEN_TAG_REGEX_CONF ) );
		}

		public void setNumberOfSelectedGames( int numberOfSelectedGames )
		{
			if( numberOfSelectedGames == 1 )
				_menuItem_setInitialPosition.setEnabled( true );
			else
				_menuItem_setInitialPosition.setEnabled( false );

			if( numberOfSelectedGames <= 1 )
			{
				_menuItem_openGameDetached.setText( getInternationalString( CONF_OPEN_SELECTED_GAME_DETACHED ) );
				_menuItem_moveSelectedGames.setText( getInternationalString( CONF_MOVE_SELECTED_GAME ) );
				_menuItem_eraseSelectedGames.setText( getInternationalString( CONF_ERASE_SELECTED_GAME ) );
			}
			else
			{
				_menuItem_openGameDetached.setText( getInternationalString( CONF_OPEN_SELECTED_GAMES_DETACHED ) );
				_menuItem_moveSelectedGames.setText( getInternationalString( CONF_MOVE_SELECTED_GAMES ) );
				_menuItem_eraseSelectedGames.setText( getInternationalString( CONF_ERASE_SELECTED_GAMES ) );
			}
		}

		public void setAllEnabled( boolean value )
		{
			_menuItem_openGameDetached.setEnabled(value);
			_menuItem_moveSelectedGames.setEnabled(value);
			_menuItem_eraseSelectedGames.setEnabled(value);
			_menuItem_setInitialPosition.setEnabled(value);
			_menuItem_createGameBefore.setEnabled(value);
			_menuItem_createGameAfter.setEnabled(value);
			_menuItem_goProfileOfTagsExtraction.setEnabled(value);
			_menuItem_analyzeGame.setEnabled(value);
		}

		public void setOpenProfileRegexConfigurationEnabled( boolean value )
		{
			_menuItem_goProfileOfTagsExtraction.setEnabled(value);
		}

		public void setOpenGameDetachedEnabled( boolean value )
		{
			_menuItem_openGameDetached.setEnabled(value);
		}

		public void setMoveSelectedGamesEnabled( boolean value )
		{
			_menuItem_moveSelectedGames.setEnabled(value);
		}

		public void setEraseSelectedGamesEnabled( boolean value )
		{
			_menuItem_eraseSelectedGames.setEnabled(value);
		}

		@Override
		public void mouseEntered(MouseEvent me)
		{
			Component comp = (Component) me.getSource();

			Boolean showRightClickRowLineAtTop = null;
			if( ( comp == _menuItem_moveSelectedGames ) && comp.isEnabled() )
			{
//				Rectangle rect = getCellRect(_rightClickRow,_rightClickColumn,true);
				showRightClickRowLineAtTop = !( (_selectionBeforeRightClick.length > 0) &&
												( _rightClickRow > _selectionBeforeRightClick[0] ) );
			}
			else if( ( comp == _menuItem_createGameBefore ) && comp.isEnabled() )
			{
				showRightClickRowLineAtTop = true;
			}
			else if( ( comp == _menuItem_createGameAfter ) && comp.isEnabled() )
			{
				showRightClickRowLineAtTop = false;
			}
			else if( ( comp == _menuItem_analyzeGame ) && comp.isEnabled() )
			{
				_rowToHighlight = getFirstSelectedRowIndex();
				_table.scrollRectToVisible(new Rectangle(_table.getCellRect(_rowToHighlight, 0, true)));
				_table.repaint();
			}

			if( showRightClickRowLineAtTop != null )
			{
				y_coordinateOfInsertionPosition = getRightClickYcoordinate( showRightClickRowLineAtTop );
				_table.scrollRectToVisible( _table.getRightClickCellRect() );
				_table.repaint();
			}
		}

		@Override
		public void actionPerformed( ActionEvent evt )
		{
			try
			{
				Component comp = (Component) evt.getSource();

				if( comp == _menuItem_openGameDetached )
					openGameDetached();
				else if( comp == _menuItem_moveSelectedGames )
					moveSelectedGames();
				else if( comp == _menuItem_eraseSelectedGames )
					eraseSelectedGames();
				else if( comp == _menuItem_setInitialPosition )
					setInitialPosition();
				else if( comp == _menuItem_createGameBefore )
					createGameBefore();
				else if( comp == _menuItem_createGameAfter )
					createGameAfter();
				else if( comp == _menuItem_goProfileOfTagsExtraction )
					openTagRegexConfiguration();
				else if( comp == _menuItem_analyzeGame )
					analyzeGame();
			}
			finally
			{
				setVisible(false);

				if( _table != null )
					_table.repaint();
			}
		}

		@Override
		public void mouseExited(MouseEvent me)
		{
			Component comp = (Component) me.getSource();

			_table.repaint();

			super.mouseExited(me);
		}

		@Override
		public void setComponentMapper(ComponentMapper mapper)
		{
			_table = mapper.mapComponent(_table);

			_menuItem_openGameDetached = mapper.mapComponent(_menuItem_openGameDetached);
			_menuItem_moveSelectedGames = mapper.mapComponent(_menuItem_moveSelectedGames);
			_menuItem_eraseSelectedGames = mapper.mapComponent(_menuItem_eraseSelectedGames);
			_menuItem_setInitialPosition = mapper.mapComponent(_menuItem_setInitialPosition);
			_menuItem_createGameBefore = mapper.mapComponent(_menuItem_createGameBefore);
			_menuItem_createGameAfter = mapper.mapComponent(_menuItem_createGameAfter);
			_menuItem_goProfileOfTagsExtraction = mapper.mapComponent(_menuItem_goProfileOfTagsExtraction);

			super.setComponentMapper(mapper);
		}
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
/*
	protected void invertSingleComponentColors(ColorInversor colorInversor)
	{
		colorInversor.invertSingleColorsGen(this);
	}

	@Override
	public void invertColors( ColorInversor colorInversor )
	{
		invertSingleComponentColors(colorInversor);
	}
*/
	@Override
	public void setBackground( Color bc )
	{
		super.setBackground(bc);
	}
/*
	public boolean isDarkMode()
	{
		return( FrameworkComponentFunctions.instance().isDarkMode(this) );
	}
*/
}
