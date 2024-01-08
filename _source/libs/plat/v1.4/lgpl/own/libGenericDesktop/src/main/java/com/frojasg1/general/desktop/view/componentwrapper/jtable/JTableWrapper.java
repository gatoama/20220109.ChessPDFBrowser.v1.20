/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable;

import com.frojasg1.desktop.libtablecolumnadjuster.TableColumnAdjuster;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.keyboard.IsKeyPressed;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.TableFunctions;
import com.frojasg1.general.desktop.view.color.renderers.TableCellRendererColorInversor;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.general.desktop.view.componentwrapper.JComponentWrapper;
import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEventBuilder;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperEventBuilderBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperFilterHasChangedEvent;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperNewCurrentEvent;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperSelectionHasChangedEvent;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.text.cellrenderers.JLabelRenderer;
import com.frojasg1.general.desktop.view.zoom.componentcopier.GenericCompCopier;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.proxy.ProxyFunctions;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.update.Updateable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.TableUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class JTableWrapper<RR, UI extends TableUI>
	extends JComponentWrapper<JTable, UI, JTableWrapperEventBase, JTableFileDragAndDropEventContext>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(JTableWrapper.class);

	protected List<RR> _list;

	protected List<RR> _filteredList;

	protected ViewAndModelSelectionValue _currentRow;
	protected ViewAndModelSelectionValue _rowToHighlight;

	protected ViewAndModelSelectionValue _rightClickRow;
	protected ViewAndModelSelectionValue _rightClickColumn;
	protected ViewAndModelSelectionArray _rowSelectionBeforeRightClick;

	protected ViewAndModelSelectionArray _previousRowSelection;
	protected boolean _previousWasSelectionAdjusting = false;

	protected List<Updateable> _viewModelUpdateableList = new ArrayList<>();

	protected int y_coordinateOfInsertionPosition = -1;

	protected MouseAdapter _mouseListener = null;

	protected TableCellRenderer _cellRenderer;

	protected BaseJPopupMenu _tableHeaderBasePopupMenu = null;

//	protected int[] _previousViewSelection;

	protected ListSelectionModel _originalListSelectionModel;

	protected boolean _isFiltered = false;

	protected Object[] _previousColumnIds;

	protected AtomicInteger _changedByProgram = new AtomicInteger(0);

	protected ListSelectionListener _listSelectionListener;

	protected String _lastFilter;

	public JTableWrapper( JTable table, Class<RR> recordClass )
	{
		this( table, new JTableWrapperEventBuilderBase<>(table, recordClass, true) );
	}

	public JTableWrapper( JTable table, JComponentWrapperEventBuilder<JTableWrapperEventBase> eventBuilder )
	{
		super( table, eventBuilder );
	}

	@Override
	protected void init()
	{
		super.init();

		_tableHeaderBasePopupMenu = createTableHeaderBasePopupMenu();
		_cellRenderer = createCellRenderer();

		_originalListSelectionModel = getTable().getSelectionModel();

		_previousRowSelection = createViewAndModelRowSelectionArray(4);

		initViewAndModelAttribs();
//		SwingUtilities.invokeLater( this::initViewAndModelAttribs );

		_listSelectionListener = createListSelectionListener();
		addListSelectionListener();
	}

	protected ListSelectionListener createListSelectionListener()
	{
		return( this::valueChanged );
	}

	public String getLastFilter() {
		return _lastFilter;
	}

	public void createProxyForListSelectionModelAndSetItToTable(int[] viewRowSelection)
	{
		ListSelectionModel readOnlyListSelectionModel =
			ProxyFunctions.instance().createReadOnlyProxy( _originalListSelectionModel,
															ListSelectionModel.class );

		getTable().setSelectionModel(readOnlyListSelectionModel);

		if( viewRowSelection != null )
		{
			_originalListSelectionModel.clearSelection();
			for( Integer row: viewRowSelection )
				_originalListSelectionModel.addSelectionInterval(row, row);
		}
	}

	public List<RR> getFilteredList() {
		return _filteredList;
	}

	protected void valueChanged(ListSelectionEvent evt)
	{
//		CallStackFunctions.instance().dumpCallStack( "valueChanged" );
		List<Exception> callStacks = this.createCallStacksList();
		addCallStack( "valueChanged", callStacks );
		SwingUtilities.invokeLater( () -> valueChangedInternal(evt, callStacks) );
	}

	protected boolean isChangedByProgram()
	{
		return( _changedByProgram.get() > 0 );
	}

	protected void valueChangedInternal(ListSelectionEvent evt, List<Exception> callStacks)
	{
		_previousWasSelectionAdjusting = getTable().getSelectionModel().getValueIsAdjusting();
		if( !isChangedByProgram() && !_previousWasSelectionAdjusting )
		{
			logCallStacks( "valueChangedInternal", callStacks );
			updatePreviousRowSelection();
		}
	}

	public void updatePreviousRowSelection()
	{
		setPreviousRowSelectionViewArray( getTable().getSelectedRows() );
	}

	public void setPreviousRowSelectionViewArray( int[] viewArray)
	{
		setViewArray( _previousRowSelection, viewArray );
		notifyEvt( createSelectionChangedEvt() );
	}

	public boolean isPreviousWasSelectionAdjusting() {
		return _previousWasSelectionAdjusting;
	}

	public int[] getPreviousViewRowSelection() {
		return getViewArray( _previousRowSelection );
	}

	public void restablishListSelectionModel()
	{
		getTable().setSelectionModel(_originalListSelectionModel);
	}

	protected void initViewAndModelAttribs()
	{
		_currentRow = createViewAndModelRowSelectionValue();
		_rowToHighlight = createViewAndModelRowSelectionValue();

		_rightClickRow = createViewAndModelRowSelectionValue();
		_rightClickColumn = createViewAndModelColumnSelectionValue();
		_rowSelectionBeforeRightClick = createViewAndModelRowSelectionArray(0);
	}

	@Override
	public JTableWrapperEventBuilderBase<RR> getEventBuilder() {
		return (JTableWrapperEventBuilderBase<RR>) super.getEventBuilder();
	}

	public ViewAndModelSelectionValue createViewAndModelRowSelectionValue()
	{
		ViewAndModelSelectionValue result = new ViewAndModelSelectionValue(
													this::convertViewRowIndexToModel,
													this::convertModelRowIndexToView );
		_viewModelUpdateableList.add( result );
		return( result );
	}

	public ViewAndModelSelectionValue createViewAndModelColumnSelectionValue()
	{
		ViewAndModelSelectionValue result = new ViewAndModelSelectionValue(
													this::convertViewColumnIndexToModel,
													this::convertModelColumnIndexToView );
		_viewModelUpdateableList.add( result );
		return( result );
	}

	public ViewAndModelSelectionArray createViewAndModelRowSelectionArray( int validityOfExpected )
	{
		ViewAndModelSelectionArray result = new ViewAndModelSelectionArray(
													this::convertViewRowIndexToModel,
													this::convertModelRowIndexToView,
													validityOfExpected);
		_viewModelUpdateableList.add( result );
		return( result );
	}

	public ViewAndModelSelectionArray createViewAndModelColumnSelectionArray( int validityOfExpected )
	{
		ViewAndModelSelectionArray result = new ViewAndModelSelectionArray(
													this::convertViewColumnIndexToModel,
													this::convertModelColumnIndexToView,
													validityOfExpected );
		_viewModelUpdateableList.add( result );
		return( result );
	}

	public boolean isEmpty()
	{
		List<RR> list = getList();

		return( ( list == null ) || list.isEmpty() );
	}

	public TableCellRenderer getCellRenderer() {
		return _cellRenderer;
	}

	@Override
	protected void installUI( UI compUI )
	{
		getTable().setUI( compUI );
	}

	public List<RR> getList() {
		return _list;
	}

	protected int getSelectedRow()
	{
		return( getTable().getSelectedRow() );
	}

	protected void setRowSelectionInterval( int start, int end )
	{
		getTable().setRowSelectionInterval( start, end );
	}

	public Integer getCurrentViewRowIndex()
	{
		return( getViewIndex( _currentRow ) );
	}

	public Integer getCurrentModelRowIndex()
	{
		return( getModelIndex( _currentRow ) );
	}

	public Integer getViewIndex( ViewAndModelSelectionValue valueObj )
	{
		return( (valueObj == null) ? null : valueObj.getViewSelectedIndex() );
	}

	public Integer getModelIndex( ViewAndModelSelectionValue valueObj )
	{
		return( (valueObj == null) ? null : valueObj.getModelSelectedIndex() );
	}

	public void setViewIndex( ViewAndModelSelectionValue valueObj, Integer value )
	{
		if( valueObj != null )
			valueObj.setViewSelectedIndex(value);
	}

	public void setModelIndex( ViewAndModelSelectionValue valueObj, Integer value )
	{
		if( valueObj != null )
			valueObj.setModelSelectedIndex(value);
	}

	public int[] getViewArray( ViewAndModelSelectionArray arrayObj )
	{
		return( (arrayObj == null) ? null : arrayObj.getViewSelection() );
	}

	public int[] getModelArray( ViewAndModelSelectionArray arrayObj )
	{
		return( (arrayObj == null) ? null : arrayObj.getModelSelection() );
	}

	public void setViewArray( ViewAndModelSelectionArray arrayObj, int[] array )
	{
		if( arrayObj != null )
			arrayObj.setViewSelection(array);
	}

	public void setModelArray( ViewAndModelSelectionArray arrayObj, int[] array )
	{
		if( arrayObj != null )
			arrayObj.setModelSelection(array);
	}

	public boolean isEmpty( ViewAndModelSelectionArray arrayObj )
	{
		return( arrayObj.isEmpty() );
	}

	public RR getCurrentRecord()
	{
		return( ExecutionFunctions.instance().safeFunctionExecution(() -> _list.get( getCurrentModelRowIndex() ) ) );
	}

	public void updateData()
	{
		int selectedRow = getSelectedRow();

		showData();

		if( ( selectedRow >= 0 ) && ( _list.size() > selectedRow ) )
			setRowSelectionInterval( selectedRow, selectedRow );
	}

	protected abstract Object[] getColumnIds( );

	protected DefaultTableModel createTableModel()
	{
		return( new DefaultTableModel() );
	}

	protected DefaultTableModel getDefaultTableModel()
	{
		DefaultTableModel result = null;
		if( getTable().getModel() instanceof DefaultTableModel )
			result = (DefaultTableModel) getTable().getModel();
		else
		{
			result = createTableModel();
			getTable().setModel(result);
		}
		return( result );
	}

	protected void setModel( TableModel model )
	{
		removeModelListeners();

		getTable().setModel(model);

		addModelListeners();
	}

	protected int getColumnCount()
	{
		return( getTable().getColumnCount() );
	}

	protected void setTableCellRendererers()
	{
		TableCellRenderer renderer = getCellRenderer();
		for (int ii = 0; ii < getColumnCount(); ii++)
		{
			Class<?> col_class = getTable().getColumnClass(ii);
			setCellRendererAndEditor( col_class, renderer );
		}
		setCellRendererAndEditor( String.class, renderer );
	}

	protected void setCellRendererAndEditor( Class<?> col_class, TableCellRenderer renderer )
	{
		getTable().setDefaultEditor(col_class, null);        // remove editor
		setDefaultRenderer( col_class, renderer );
	}

	protected void setDefaultRenderer( Class<?> col_class, TableCellRenderer renderer )
	{
		TableCellRenderer prev = getTable().getDefaultRenderer(col_class);
		if( prev instanceof TableCellRendererColorInversor )
			( (TableCellRendererColorInversor) prev).setOriginalTableCellRenderer(renderer);
		else
			getTable().setDefaultRenderer( col_class, renderer );
	}

	protected Enumeration<TableColumn> getColumns()
	{
		return( getTable().getColumnModel().getColumns() );
	}

	public boolean isFiltered()
	{
		return( _isFiltered );
	}
/*
	protected void storeAndResetSelection()
	{
		_previousViewSelection = this.getSelectedRows();
		resetSelection();
		createProxyForListSelectionModelAndSetItToTable( _previousViewSelection );
	}
*/
	protected void filterData( String filter )
	{
/*
		boolean isEmptyFilter = StringFunctions.instance().isEmpty(filter);
		if( !isEmptyFilter && !isFiltered() )
			storeAndResetSelection();
*/
		onlyFilterData( filter );
/*
		if( isEmptyFilter && isFiltered() )
			restoreSelection();
*/
	}

	protected void onlyFilterData( String filter )
	{
		showDataInternal( this::autoResizeAllColumns, filter );
	}
/*
	protected void restoreSelection()
	{
		List<Exception> callStacks = this.createCallStacksList();
		addCallStack( "restoreSelection", callStacks );
		restablishListSelectionModel();

		SwingUtilities.invokeLater( () -> {
			setRowSelection( _previousViewSelection, callStacks );
			_previousViewSelection = null;
		});
	}
*/
	protected void showData()
	{
		showDataInternal(this::autoResizeAllColumns, getLastFilter() );
	}

	protected void showDataInternal( Runnable adjustColumnsFunction, String filter )
	{
		this.taskWithClock( () -> showDataInternalFunction( adjustColumnsFunction, filter ) );
	}

	protected void emptyModel(DefaultTableModel dtm)
	{
		increaseChangedByProgram();
		while( dtm.getRowCount() > 0 )
			dtm.removeRow(0);
		decreaseChangedByProgram();
	}

	protected <CC> boolean equals( CC[] array1, CC[] array2 )
	{
		return( ArrayFunctions.instance().equals(array1, array2) );
	}

	protected void setColumnIds( DefaultTableModel dtm, Object[] columnIds )
	{
		if( !equals( _previousColumnIds, columnIds ) )
		{
			_previousColumnIds = columnIds;
			dtm.setColumnIdentifiers(columnIds);
		}
	}

	protected void increaseChangedByProgram()
	{
		synchronized( _changedByProgram )
		{
			if( _changedByProgram.incrementAndGet() == 1 )
				removeListSelectionListener();
		}
	}

	protected void decreaseChangedByProgramInternal()
	{
		synchronized( _changedByProgram )
		{
			if( _changedByProgram.decrementAndGet() == 0 )
				addListSelectionListener();
		}
	}

	protected void decreaseChangedByProgram()
	{
		SwingUtilities.invokeLater( this::decreaseChangedByProgramInternal );
	}

	protected void showDataInternalFunction( Runnable adjustColumnsFunction, String filter )
	{
		_lastFilter = filter;
		_isFiltered = !StringFunctions.instance().isEmpty(filter);

		_filteredList = new ArrayList<>();
		
//		DefaultTableModel dtm = createTableModel();
		DefaultTableModel dtm = getDefaultTableModel();

		Object[] columnIds = getColumnIds( );
		setColumnIds( dtm, columnIds );

		emptyModel(dtm);

		if( ! isEmpty() )
		{
			int index = 1;
			increaseChangedByProgram();
			for( RR record: _list )
			{
				if( matches( record, filter ) )
				{
					_filteredList.add(record);

					Object[] row = getRow( index, record );
					dtm.addRow( row );
				}
				index++;
			}
			decreaseChangedByProgram();
		}
		else
		{
			for( int ii=0; ii < 2; ii++ )
				dtm.addRow( getRow( ii, null ) );
		}

//		block();
//		setModel( dtm );

		updateViewModel();

		notifyEvt( createFilterChangedEvt() );

		SwingUtilities.invokeLater( () -> setColumnHeaders(columnIds) );

		setTableCellRendererers();

//		System.out.println( "AdjustColumnWidths ..." );

		if( adjustColumnsFunction != null )
			SwingUtilities.invokeLater( adjustColumnsFunction );
	}

	protected void updateViewModel()
	{
		for( Updateable updateable: _viewModelUpdateableList )
			updateable.update();

		continueSelection();
	}

	protected void continueSelection()
	{
//		CallStackFunctions.instance().dumpCallStack( "setModelRowSelection" );
		List<Exception> callStacks = this.createCallStacksList();
		addCallStack( "continueSelection", callStacks );
		int[] previousModelSelection = getModelArray(_previousRowSelection);
		int[] copy = ArrayFunctions.instance().copy(previousModelSelection);
		SwingUtilities.invokeLater ( () -> {
				setModelRowSelection( copy, callStacks );
//				getTable().repaint();
		});
	}


	protected abstract boolean matches( RR record, String filter );

	protected JTableHeader getTableHeader()
	{
		return( getTable().getTableHeader() );
	}

	protected TableColumn getResizingColumn()
	{
		TableColumn result = null;
		JTableHeader header = getTableHeader();
		if( header != null )
			result = header.getResizingColumn();

		return( result );
	}

	protected void setColumnHeaders(Object[] columnIds)
	{
		Enumeration<TableColumn> columns = getColumns();
		
		int index = 0;
		while( columns.hasMoreElements() )
			columns.nextElement().setHeaderValue( columnIds[index++] );
	}

	protected void addModelListeners()
	{
		
	}

	protected void removeModelListeners()
	{
		
	}

	protected void afterResizingColumns()
	{
//		unblock();
		continueSelection();

		increaseChangedByProgram();
		getDefaultTableModel().fireTableDataChanged();
//		SwingUtilities.invokeLater( () ->
//			SwingUtilities.invokeLater(
//				() -> _changedByProgram.decrementAndGet()
//			)
//		);
		decreaseChangedByProgram();

		updateViewport();
//		getTable().repaint();
	}

	protected void updateViewport()
	{
		if( getTable().getParent() instanceof JViewport )
			( (JViewport) getTable().getParent() ).setView( getTable() );
	}

	protected TableColumnAdjuster createTableColumnAdjuster()
	{
		return( createTableColumnAdjuster( null ) );
	}

	protected TableColumnAdjuster createTableColumnAdjuster( Integer hundredPercentMaxWidth )
	{
		TableColumnAdjuster result = new TableColumnAdjuster( getTable() );

		if( hundredPercentMaxWidth != null )
		{
			int maxWidth = IntegerFunctions.zoomValueCeil( 250, getZoomFactor() );
			result.setMaxWidthAllowed(maxWidth);
		}

		return( result );
	}

	protected int getIndex( TableColumn tc )
	{
		int result = -1;

		Enumeration<TableColumn> columns = getColumns();
		for( int ii=0; columns.hasMoreElements(); ii++ )
			if( columns.nextElement() == tc )
			{
				result = ii;
				break;
			}

		return( result );
	}

	protected Rectangle getColumnHeaderBounds( int index )
	{
		return( TableFunctions.instance().getColumnHeaderBounds( getTable(), index ) );
	}

	protected void autoResizeNoLimit( TableColumn tc )
	{
		autoResizeNoLimit( getIndex(tc) );
	}

	protected void autoResizeNoLimit( int columnIndex )
	{
		try
		{
			TableColumnAdjuster tca = createTableColumnAdjuster();

			tca.adjustColumn(columnIndex);
		}
		catch( Throwable th )
		{
			LOGGER.warn( "Exception when adjusting columns of table" );
		}

		SwingUtilities.invokeLater( this::afterResizingColumns );
	}

	protected void autoResizeAllColumns()
	{
		try
		{
			TableColumnAdjuster tca = createTableColumnAdjuster(250);

			tca.adjustColumns();
		}
		catch( Throwable th )
		{
			LOGGER.warn( "Exception when adjusting columns of table" );
		}

		SwingUtilities.invokeLater( this::afterResizingColumns );
	}

	protected void showViewRowIndex(Integer viewRowIndex)
	{
		if( ( viewRowIndex != null ) && ( viewRowIndex != -1 ) )
		{
			if( ! isRowVisible(viewRowIndex) )
				setRowVisible(viewRowIndex - 5 );

			if( ! isRowVisible(viewRowIndex) )
				setRowVisible(viewRowIndex + 5 );
		}
	}

	protected boolean isRowVisible( int viewRowIndex )
	{
		return( TableFunctions.instance().isRowVisible( getTable(), viewRowIndex ) );
	}

	protected boolean isValidModelIndex( Integer modelIndex )
	{
		return( ( modelIndex != null ) && ( modelIndex > -1 ) && ( modelIndex < _list.size() ) );
	}

	protected Integer getFirstIndex()
	{
		return( ( (_list == null) || _list.isEmpty() ) ? null : 0 );
	}

	protected void setCurrentIndex(Integer modelIndex)
	{
		if( ( modelIndex != null ) && !isValidModelIndex( modelIndex ) )
			modelIndex = getFirstIndex();

		if( modelIndex == null )
			setModelIndex( _currentRow, modelIndex );
		else
		{
			setModelIndex( _currentRow, modelIndex );
	//			setRowSelectionInterval( index, index );
			showCurrentRow();
		}

		getTable().repaint();
	}

	protected void showCurrentRow()
	{
		showViewRowIndex( getCurrentRowViewIndex() );
	}

	protected Integer getCurrentRowViewIndex()
	{
		return( getViewIndex( _currentRow ) );
	}

	protected void makeRowBeVisible( int viewRowIndex )
	{
		TableFunctions.instance().makeRowBeVisible( getTable(), viewRowIndex );
	}

	protected void setRowVisible( int index )
	{
		int size = getList().size();
		if( size > 0 )
		{
			int index2 = IntegerFunctions.limit(index, 0, size - 1 );
			makeRowBeVisible(index2);
		}
	}

	public void setRecord( RR record )
	{
		SwingUtilities.invokeLater( () -> setCurrentIndex( _list.indexOf(record) ) );
	}

	protected void setRowSelection( int[] viewRowSelection, List<Exception> callStacks )
	{
		if( viewRowSelection != null )
		{
			ListSelectionModel selModel = getTable().getSelectionModel();

//			logCallStacks( "setRowSelection", callStacks );
			increaseChangedByProgram();
			selModel.clearSelection();
			for( Integer elem: viewRowSelection )
				selModel.addSelectionInterval(elem, elem);
			decreaseChangedByProgram();

			setPreviousRowSelectionViewArray( viewRowSelection );
			showViewRowIndex( ArrayFunctions.instance().getFirst(viewRowSelection) );

			getTable().repaint();
		}
	}

	public void setModelRowSelection( int[] modelRowSelection, List<Exception> callStacks )
	{
//		CallStackFunctions.instance().dumpCallStack( "setModelRowSelection" );
		addCallStack( "setModelRowSelection", callStacks );
		SwingUtilities.invokeLater(
			() -> setRowSelection( modelRowArrayToView( modelRowSelection ), callStacks )
								);
	}

	protected int[] modelRowArrayToView( int[] modelRowSelection )
	{
		return( translateArray( modelRowSelection, this::convertModelRowIndexToView ) );
	}

	protected int[] translateArray( int[] viewIndexArray,
									Function<Integer, Integer> elementTranslatorFunction )
	{
		return( ArrayFunctions.instance().translateArray(viewIndexArray, elementTranslatorFunction) );
	}


	public void setRecordList( List<? extends RR> list )
	{
//		if( _list != list )
		{
//			System.out.println( "setChessGameList, _list != list ..." );

			List<RR> newList = list.stream().collect( Collectors.toList() );

			if( !newList.equals( _list ) )
			{
				_list = newList;
				SwingUtilities.invokeLater( () -> {
					updateData();

					if( _list.size() > 0 )
					{
						setCurrentIndex( 0 );
						setRowSelectionInterval( 0, 0 );
					}
					else
						setCurrentIndex( null );
				} );
			}
		}
	}

	protected int[] calculateExpectedViewSelection()
	{
		return( _previousRowSelection.calculateExpectedViewSelection() );
	}

	public List<RR> calculateExpectedViewSelectionRecords()
	{
		List<RR> result = null;
		if( getList() != null )
			result = CollectionFunctions.instance().translateArrayIntoList(
							calculateExpectedModelSelection(), getList()::get);

		return( result );
	}

	protected int[] calculateExpectedModelSelection()
	{
		return( _previousRowSelection.viewArrayToModel( calculateExpectedViewSelection() ) );
	}

	protected JTableWrapperSelectionHasChangedEvent createSelectionChangedEvt()
	{
		JTableWrapperSelectionHasChangedEvent result = createEvt( JTableWrapperSelectionHasChangedEvent.class );

		result.setNewWholeModelSelection( getModelArray( _previousRowSelection ) );
		result.setNewVisibleModelSelection( calculateExpectedViewSelection() );

		return( result );
	}

	protected JTableWrapperFilterHasChangedEvent createFilterChangedEvt()
	{
		JTableWrapperFilterHasChangedEvent result = createEvt( JTableWrapperFilterHasChangedEvent.class );

		result.setNewFilteredList(CollectionFunctions.instance().copyList( _filteredList ) );

		return( result );
	}

	protected JTableWrapperNewCurrentEvent createNewCurrentEvt( int newCurrentRow )
	{
		JTableWrapperNewCurrentEvent result = createEvt( JTableWrapperNewCurrentEvent.class );

		result.setNewCurrentRow( newCurrentRow );

		return( result );
	}

	protected boolean setNewCurrentModelRow( int index )
	{
		return( setNewCurrentRow( index, this::setModelIndex ) );
	}

	protected boolean setNewCurrentViewRow( int index )
	{
		return( setNewCurrentRow( index, this::setViewIndex ) );
	}

	protected boolean setNewCurrentRow( int index,
										BiConsumer<ViewAndModelSelectionValue, Integer> indexSetter )
	{
		boolean success = false;

		if( ( _list != null ) &&
			( index < _list.size() ) && ( index >= 0 ) )
		{
			indexSetter.accept(_currentRow, index );
//			_controller.newChessGameChosen( _list.get( index ), false );
			notifyEvt( createNewCurrentEvt( getModelIndex( _currentRow ) ) );
			SwingUtilities.invokeLater( getTable()::repaint );
			success = true;
		}

		return( success );
	}

	public void insertRecordAfter( RR recordToBeInserted, RR afterThis )
	{
		int index = insertRecordAfterInternal( recordToBeInserted, afterThis );

		showData();
		getTable().changeSelection(index, 0, false, false);
		setNewCurrentModelRow( index );

		notifyHasBeenModified();
	}

	public int insertRecordAfterInternal( RR recordToBeInserted, RR afterThis )
	{
		int index = IntegerFunctions.max( 0, _list.indexOf( afterThis ) );
		_list.add( index, recordToBeInserted );
		
		return( index );
	}

	public JTable getTable()
	{
		return( getJComponent() );
	}

	protected TableCellRenderer createSimpleCellRenderer()
	{
		return( new ModifiedCellRenderer( getFont() ) );
	}

	protected TableCellRenderer createCellRenderer()
	{
		TableCellRenderer result = createSimpleCellRenderer();

		if( wasLatestModeDark() )
			result = getColorInversor().createTableCellRendererColorInversor(result, "cells");

		return( result );
	}

	protected boolean isCurrentRow( int row, int column )
	{
		return( Objects.equals( row, getViewIndex( _currentRow ) ) );
	}

	protected abstract Object[] getRow( int index, RR record );

	public int[] getRowModelSelectionBeforeRightClick() {
		return getModelArray(_rowSelectionBeforeRightClick );
	}

	public int[] getAppropriateRowModelSelection() {
		int[] result = getRowModelSelectionBeforeRightClick();
		if( isEmpty(result) )
			result = getModelSelectionRows();

		return result;
	}

	protected List<RR> getListOfSelectedRecordsRemovingThem()
	{
		CollectionFunctions.instance();
		List<RR> result = new ArrayList<RR>();

		if( ! isEmpty( _rowSelectionBeforeRightClick ) )
		{
			int[] rowModelSelectionBeforeRightClick = getModelArray( _rowSelectionBeforeRightClick );

			for( int ii=rowModelSelectionBeforeRightClick.length - 1; ii>=0; ii-- )
				result.add( _list.remove( rowModelSelectionBeforeRightClick[ii] ) );
		}

		return( result );
	}

	protected int calculatePositionToInsertRecords( int destinationPositionPreviousToTheExtractionOfRecords )
	{
		int result = destinationPositionPreviousToTheExtractionOfRecords;

		if( ! isEmpty( _rowSelectionBeforeRightClick ) )
		{
			int[] rowModelSelectionBeforeRightClick = getModelArray( _rowSelectionBeforeRightClick );
			if( result >= rowModelSelectionBeforeRightClick[ rowModelSelectionBeforeRightClick.length -1 ] )
			{
				result = result - rowModelSelectionBeforeRightClick.length + 1;		// +1 because we want to insert it after the current row.
			}
		}

		return( result );
	}

	protected boolean isSelectionEmpty()
	{
		return( ArrayFunctions.instance().isEmpty( getSelectedRows() ) );
	}
/*
	protected void eraseSelectedRecords()
	{
		int index = getModelIndex( _rightClickRow );
		getListOfSelectedRecordsRemovingThem();
		updateData();
		
		if( _list.size() == 0 )
			createRecord( 0 );
		else
		{
			if( index < 0 )
				index = 0;
			else if( index >= _list.size() )
				index = _list.size()-1;

			getTable().changeSelection(index, 0, false, false);
			setNewCurrentModelRow( index );

			notifyHasBeenModified();
		}
	}
*/
	public void createRecord( int unitsToAddToIndex )
	{
		int index = getModelIndex( _rightClickRow ) + unitsToAddToIndex;
		index = IntegerFunctions.max( 0, IntegerFunctions.min( index, _list.size() ) );

		createRecordInternal( index );
		showData();
		getTable().changeSelection(index, 0, false, false);
		setNewCurrentModelRow( index );

		notifyHasBeenModified();
	}

	protected abstract RR createEmptyRecord();

	protected RR createRecordInternal( int index )
	{
		RR record = null;
		
		try
		{
			record = createEmptyRecord();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		
		if( record != null )
		{
			_list.add( index, record );
		}
		
		return( record );
	}

	protected RR getRecordAtRightClick()
	{
		return( getRecordAtModelRowIndex( getModelIndex( _rightClickRow ) ) );
	}

	protected RR getFirstSelectedRecordAtRightClick()
	{
		RR result = null;
		
		if( ! isEmpty( _rowSelectionBeforeRightClick ) )
		{
			int modelRowIndex = getModelArray( _rowSelectionBeforeRightClick )[0];
			result = getRecordAtModelRowIndex( modelRowIndex );
		}
		if( result == null )
			result = getRecordAtRightClick();

		return( result );
	}

	protected RR getRecordAtModelRowIndex( Integer modelRowIndex )
	{
		RR result = null;
		if( (modelRowIndex != null) && (_list != null ) )
		{
			int index = modelRowIndex;
			if( ( index >= 0 ) && ( index < _list.size() ) )
				result = _list.get( index );
		}

		return( result );
	}

	protected MouseAdapter createMouseListener( JTable table )
	{
		return( new ClickListener( table ) );
	}

	protected void addDefaultTableListeners()
	{
		_mouseListener = createMouseListener( getTable() );
		getTable().addMouseListener( _mouseListener );
		getTableHeader().addMouseListener( _mouseListener );

		if( getTable().getParent() != null )
			getTable().getParent().addMouseListener( _mouseListener );
	}

	@Override
	protected void addListeners()
	{
		addDefaultTableListeners();
		addMouseWheelListeners();
	}

	protected void addListSelectionListener()
	{
		addListSelectionListener( _listSelectionListener );
	}

	protected void addListSelectionListener( ListSelectionListener listSelectionListener )
	{
		if( listSelectionListener != null )
			getTable().getSelectionModel().addListSelectionListener( listSelectionListener );
	}

	protected void addMouseWheelListeners()
	{
		JScrollPane sp = getScrollPane( getTable() );
		ComponentFunctions.instance().browseComponentHierarchy( sp, comp -> {
			comp.addMouseWheelListener(_mouseListener);
			return( null );
			});
	}

	@Override
	protected void removeListeners()
	{
		removeDefaultTableListeners();
		removeMouseWheelListeners();
		removeModelListeners();
		removeListSelectionListener();
		removeModelListeners();
	}

	protected void removeListSelectionListener()
	{
		removeListSelectionListener(_listSelectionListener);
	}

	protected void removeListSelectionListener( ListSelectionListener listener )
	{
		if( listener != null )
			getTable().getSelectionModel().removeListSelectionListener( listener );
	}

	protected void removeMouseWheelListeners()
	{
		JScrollPane sp = getScrollPane( getTable() );
		ComponentFunctions.instance().browseComponentHierarchy( sp, comp -> {
			comp.removeMouseWheelListener(_mouseListener);
			return( null );
			});
	}

	protected void removeDefaultTableListeners()
	{
		getTable().removeMouseListener( _mouseListener );
		getTableHeader().removeMouseListener( _mouseListener );

		if( getTable().getParent() != null )
			getTable().getParent().removeMouseListener( _mouseListener );
	}
/*
	public void addFileDragAndDropListener( Consumer<FileDragAndDropEvent> listener )
	{
		getFileTransferHandler().addListener(listener);
	}
*/

	public void filter( String filter )
	{
		filterData(filter);
	}

	public void updateInternal()
	{
		showData();
	}

	protected abstract BaseJPopupMenu createTableHeaderBasePopupMenu();

	protected BaseJPopupMenu getTableHeaderBasePopupMenu()
	{
		return( _tableHeaderBasePopupMenu );
	}

	protected void doTableHeaderPopup( MouseEvent evt )
	{
		BaseJPopupMenu popupMenu = getTableHeaderBasePopupMenu();
		if( popupMenu != null )
			popupMenu.doPopup(evt);
	}

	protected int columnAtPoint( Point point )
	{
		return( TableFunctions.instance().columnAtPoint( getTable(), point) );
	}

	protected boolean listHasElements()
	{
		List<RR> list = getList();
		return( (list != null) && ! list.isEmpty() );
	}

	protected int rowAtPoint( Point point )
	{
		return( TableFunctions.instance().rowAtPoint( getTable(), point) );
	}

	protected RR get( List<RR> list, int index )
	{
		return( CollectionFunctions.instance().get( list, index) );
	}

	protected int translateIndex( List<RR> fromList, List<RR> toList, int fromIndex )
	{
		return( CollectionFunctions.instance().translateIndex( fromList, toList, fromIndex ) );
	}

	protected int getModelRowIndex( int filteredModelRowNum )
	{
		return( translateIndex( _filteredList, getList(), filteredModelRowNum ) );
	}

	protected int getFilteredModelRowIndex( int modelRowNum )
	{
		return( translateIndex( getList(), _filteredList, modelRowNum ) );
	}

	protected int convertViewRowIndexToModel( int viewRownum )
	{
		int filteredModelRowNum = getTable().convertRowIndexToModel( viewRownum );

		return( getModelRowIndex( filteredModelRowNum ) );
	}

	protected int convertViewColumnIndexToModel( int viewColumnNum )
	{
		return( getTable().convertColumnIndexToModel( viewColumnNum ) );
	}

	protected int convertModelRowIndexToView( int modelRownum )
	{
		int filteredModelRownum = getFilteredModelRowIndex( modelRownum );

		return( getTable().convertRowIndexToView( filteredModelRownum ) );
	}

	protected int convertModelColumnIndexToView( int modelColumnNum )
	{
		return( getTable().convertColumnIndexToView( modelColumnNum ) );
	}

	public boolean isRowSelected( Point point )
	{
		return( isRowSelected( getSelectedRows(), point ) );
	}

	public boolean isRowSelected( int[] rowSelection, Point point )
	{
		int rowNum = rowAtPoint( point );
		return( isRowSelected( rowSelection, rowNum ) );
	}

	public boolean isRowSelected( int[] rowSelection, int viewRowNum )
	{
		boolean result = false;
		if( rowSelection != null )
			result = Arrays.stream( rowSelection ).anyMatch( row -> row == viewRowNum );
		return( result );
	}

	protected int[] getSelectedRows()
	{
		return( getTable().getSelectedRows() );
	}

	protected int[] getModelSelectionRows()
	{
		return( ArrayFunctions.instance().translateArray( getSelectedRows(),
														this::convertViewRowIndexToModel ) );
	}

	protected TableColumn getTableColumn( Integer columnIndex )
	{
		return( TableFunctions.instance().getTableColumn( getTable(), columnIndex ) );
	}

	@Override
	protected List<Exception> createCallStacksList()
	{
		return( null );
	}

	public void resetSelection()
	{
		List<Exception> callStacks = this.createCallStacksList();
		addCallStack( "resetSelection", callStacks );
		setModelRowSelection( new int[0], callStacks );
	}

	@Override
	public void setComponentMapper( ComponentMapper mapper )
	{
		super.setComponentMapper(mapper);

		addModelListeners();
	}

	protected abstract void notifyRecordsDeleted();

	protected class ModifiedCellRenderer extends JLabelRenderer
	{
		protected Font _originalFont = null;

		public ModifiedCellRenderer( Font font )
		{
			super(font);

			_originalFont = font;
		}

		public Font getOriginalFont() {
			return _originalFont;
		}

		protected boolean isAnErroneusCell( int row, int column )
		{
			return( false );
		}

		protected Color getBackgroundColorForRecordShown( JTable table, boolean isSelected, int row, int column )
		{
			Color result = null;
			if( isSelected )
				result = Colors.PURPLE;
			else
				result = Colors.BUTAN;

			return( result );
		}

		protected Color getForegroundColorForRecordShown( JTable table, boolean isSelected, int row, int column )
		{
			Color result = null;
			if(isSelected)
				result = table.getSelectionForeground();
			else
				result = table.getForeground();

			return( result );
		}

		protected Color getErroneusBackgroundColor( JTable table, boolean isSelected, int row, int column )
		{
			Color result = null;

			if( isSelected )
				result = table.getSelectionBackground();
			else
				result = Colors.PALE_RED;

			return( result );
		}

		protected Color getBackgroundColor( JTable table, boolean isSelected, int row, int column )
		{
			Color result = null;
			boolean isErroneus = isAnErroneusCell( row, column );

			if( isErroneus )
				result = getErroneusBackgroundColor( table, isSelected, row, column );
			else if( isCurrentRow( row, column ) )
				result = getBackgroundColorForRecordShown( table, isSelected, row, column );
			else
				result = super.getBackgroundColor(table, isSelected, row, column);

//				if( isDarkMode() )
//					result = getColorInversor().invertColor(result);

			return( result );
		}

		protected Color getForegroundColor( JTable table, boolean isSelected, int row, int column )
		{
			Color result = null;

			if( isCurrentRow( row, column ) )
				result = getForegroundColorForRecordShown( table, isSelected, row, column );
			else
				result = super.getForegroundColor(table, isSelected, row, column);

//				if( isDarkMode() )
//					result = getColorInversor().invertColor(result);

			return( result );
		}

		protected JLabel copyAndZoomLabel( String text, JTable table )
		{
			setText( text );
//			JLabel result = this;
			JLabel result = new JLabel(text);
			result.setBorder( getBorder() );
			changeFont( result, table );

			int width = result.getPreferredSize().width;

//			JLabel result = new JLabel();
			GenericCompCopier.instance().copyToNew(this, result);

			Dimension size = result.getPreferredSize();
			size.width = width;

			result.setPreferredSize(size);

			changeFont(result, table);

			return( result );
		}

		@Override
		public Component getTableCellRendererComponent(
								JTable table, Object string,
								boolean isSelected, boolean hasFocus,
								int row, int column) {
			String str = (String) string;

			JLabel result = copyAndZoomLabel(str, table);

			result.setBackground(getBackgroundColor( table, isSelected, row, column ));
			result.setForeground(getForegroundColor( table, isSelected, row, column ));

//			result.setText( str );

			Integer value = IntegerFunctions.parseInt( str );
			if( value != null )
				result.setHorizontalAlignment( SwingConstants.RIGHT );
			else
				result.setHorizontalAlignment( SwingConstants.LEFT );

			return result;
		}

		protected void changeFont( Component result, JTable table )
		{
			int size = result.getFont().getSize();
			int zoomedFontSize = zoomValue( getOriginalFont().getSize() );

			if( zoomedFontSize != getFont().getSize() )
				result.setFont(
					FontFunctions.instance().getResizedFont(getOriginalFont(),
															zoomedFontSize));
		}
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
//				int[] selection = getSelectedRows();
//				if( (selection != null) && (selection.length > 0) )
				{
//					int selectedRow = selection[0];
//					setNewCurrentViewRow( selectedRow );
				}
				setNewCurrentViewRow( rowAtPoint( e.getPoint() ) );
			}
		}

		@Override
		public void rightClick( MouseEvent evt )
		{
			Component comp = (Component) evt.getSource();

			setViewArray( _rowSelectionBeforeRightClick, getSelectedRows() );

			if( comp == _parent )
			{
				setViewIndex( _rightClickRow, rowAtPoint( evt.getPoint() ) );
				setViewIndex( _rightClickColumn, columnAtPoint( evt.getPoint() ) );
			}
			else if( comp == _parent.getParent() )
			{
				Point modifiedPoint = new Point( 5, (int) ( evt.getPoint().getY() - getTableHeader().getHeight() ) );

				int row = rowAtPoint( modifiedPoint );
				setViewIndex( _rightClickRow, row );

				if( row < 0 )
					setModelIndex( _rightClickRow, _list.size()-1 );

				setModelIndex( _rightClickColumn, 0 );
			}
			else if( comp == _parent.getTableHeader() )
			{
				setModelIndex( _rightClickRow, 0 );
				setModelIndex( _rightClickColumn, 0 );
			}

/*
			if (! isRowSelected(_rightClickRow))
				changeSelection(_rightClickRow, column, false, false);
*/
			if( ! isEmpty( _rowSelectionBeforeRightClick ) )
			{
//				if( listHasElements() )
//				{
//					int viewRowIndex = getViewIndex( _rightClickRow );
//					if( viewRowIndex != -1 )
//					{
//						setRowSelectionInterval( viewRowIndex, viewRowIndex );
//						setViewArray( _rowSelectionBeforeRightClick, new int[] {viewRowIndex} );
//					}
//				}
//				else
				if( ! listHasElements() )
					setModelArray( _rowSelectionBeforeRightClick, new int[0] );
			}

			if( comp == _parent.getTableHeader() )
				doTableHeaderPopup( evt );
			else
				doPopup( evt );
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent evt)
		{
			int units = evt.getUnitsToScroll();

			JScrollPane scrollPane = getScrollPane( getTable() );

			if( scrollPane != null )
			{
				JScrollBar scrollBar = null;

				if( IsKeyPressed.isKeyPressed( KeyEvent.VK_SHIFT ) )
					scrollBar = scrollPane.getHorizontalScrollBar();
				else
					scrollBar = scrollPane.getVerticalScrollBar();

				int value;
				if (evt.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
					value = incrementScrollBarValue( scrollBar, ( units * scrollBar.getVisibleAmount() ) / 12 );
				else
					value = incrementScrollBarValue( scrollBar, scrollBar.getVisibleAmount() / 12 );

				if( scrollBar != null )
					SwingUtilities.invokeLater( () ->
						MouseFunctions.triggerMouseMoveEvent( getJComponent() ) );
			}
		}

		protected int incrementScrollBarValue( JScrollBar scrollBar, int increment )
		{
			return( setScrollBarValue( scrollBar, scrollBar.getValue() + increment ) );
		}

		protected int setScrollBarValue( JScrollBar scrollBar, int value )
		{
			int valueToSet = IntegerFunctions.min( scrollBar.getMaximum(),
													IntegerFunctions.max( scrollBar.getMinimum(), value) );

			scrollBar.setValue(valueToSet);

			return( valueToSet );
		}
	}
}
