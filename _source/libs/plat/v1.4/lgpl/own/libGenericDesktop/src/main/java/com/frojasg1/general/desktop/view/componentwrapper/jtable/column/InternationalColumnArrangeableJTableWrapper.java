/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.column;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.desktop.libtablecolumnadjuster.TableColumnAdjuster;
import com.frojasg1.general.comparators.ReverseComparator;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapper;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_AUTORESIZE_ALL_COLUMNS;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_AUTORESIZE_ALWAYS_ALL_COLUMNS;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_AUTORESIZE_THIS_COLUMN;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_DELETE_SELECTED_ROWS;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_RESET_SELECTION;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_SORT_ASCENTING_BY_THIS_COLUMN;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_SORT_DESCENTING_BY_THIS_COLUMN;
import static com.frojasg1.general.desktop.view.componentwrapper.jtable.labels.JTableWrapperInternationalLabels.CONF_SHOW_CURRENT_ROW;
import com.frojasg1.general.desktop.view.icons.IconResources;
import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.getters.LazyHolder;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.plaf.TableUI;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class InternationalColumnArrangeableJTableWrapper<RR, UI extends TableUI>
	extends JTableWrapper<RR, UI>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(InternationalColumnArrangeableJTableWrapper.class);

	protected JTableArrangeableColumnsContext<RR> _columnsDataContext;
	protected JTableArrangeableColumnsContext<RR> _tmpColumnsDataContext;
	protected Map<String, TableColumnData<RR>> _columnDataMap;

	protected InternationalizedStringConf _internationalStrings;

	protected TableColumnModelListener _tableColumnModelListener;

	protected RecordToTableCellStringsBase<RR> _recordToCellStrings;

	protected boolean _columnsAreResizedByProgram = false;

	protected boolean _autoresizeAlwaysAllColumns = false;

	public InternationalColumnArrangeableJTableWrapper( JTable table, Class<RR> recordClass,
														InternationalizedStringConf internationalStrings,
														RecordToTableCellStringsBase<RR> recordToCellStrings)
	{
		super( table, recordClass );

		_internationalStrings = internationalStrings;
		_recordToCellStrings = recordToCellStrings;
	}

	@Override
	protected void init()
	{
		_columnDataMap = createColumnDataMap();
		_columnsDataContext = createColumnsDataContext();
		fillInitial( _columnsDataContext );
		_tmpColumnsDataContext = createColumnsDataContext();

		super.init();

		_tableColumnModelListener = createTableColumnModelListener();
	}

	@Override
	protected boolean matches( RR record, String filter )
	{
		boolean result = true;
		if( filter != null )
			result = _recordToCellStrings.matches( record, filter, _columnsDataContext.getList() );

		return( result );
	}

	protected Map<String, TableColumnData<RR>> createColumnDataMap()
	{
		return( fillColumnDataMap( new LinkedHashMap<>() ) );
	}

	protected Map<String, TableColumnData<RR>> fillColumnDataMap(Map<String, TableColumnData<RR>> map)
	{
		return( _recordToCellStrings.fillColumnDataMap(map) );
	}

	protected JTableArrangeableColumnsContext<RR> createColumnsDataContext()
	{
		return( new JTableArrangeableColumnsContext<>() );
	}

	protected TableColumnModelListener createTableColumnModelListener()
	{
		return( new TableColumnModelListenerImpl() );
	}

	public boolean isAutoresizeAlwaysAllColumns() {
		return _autoresizeAlwaysAllColumns;
	}

	public void setAutoresizeAlwaysAllColumns(boolean _autoresizeAlwaysAllColumns) {
		this._autoresizeAlwaysAllColumns = _autoresizeAlwaysAllColumns;
	}

	public void setTableColumnDataString( String serialized )
	{
		getTmpColumnsContext().decode(serialized);

		updateColumnsContext( getTmpColumnsContext(), getColumnsContext() );

		showData();
	}

	protected void updateColumnsContext( JTableArrangeableColumnsContext<RR> tmpColumnsContext,
										JTableArrangeableColumnsContext<RR> result )
	{
		result.clear();
		for( TableColumnData<RR> tcd: tmpColumnsContext.getList() )
		{
			TableColumnData resTcd = updateTableColumnData(tcd);
			if( resTcd != null )
				result.add(resTcd);
		}
	}

	protected TableColumnData<RR> updateTableColumnData( TableColumnData<RR> tmpTcd )
	{
		TableColumnData<RR> result = getTableColumnData( tmpTcd.getLabel() );
		if( result != null )
			result.setWidth( tmpTcd.getWidth() );

		return( result );
	}

	public String getTableColumnDataString()
	{
		return( getColumnsContext().encode() );
	}

	protected boolean columnsHaveChangedOrder()
	{
		boolean result = false;
		List<TableColumnData<RR>> oldList = this.getColumnsContext().getListCopy();

		int index = 0;
		Enumeration<TableColumn> columns = getColumns();
		while( !result && (index < oldList.size() ) && columns.hasMoreElements() )
			result = oldList.get(index++) != columns.nextElement().getHeaderValue();

		return( result );
	}

	protected void updateOnlyChangedColumnWidth()
	{
		updateAndGetTableColumnData( getResizingColumn() );
	}

	protected void updateTableColumnData()
	{
		if( columnsHaveChangedOrder() )
			updateAllTableColumnData();
		else
			updateOnlyChangedColumnWidth();
	}

	protected void updateAllTableColumnData()
	{
		getColumnsContext().clear();

		Enumeration<TableColumn> columns = getColumns();
		while( columns.hasMoreElements() )
		{
			TableColumn elem = columns.nextElement();
//			TableColumnData tcd = updateAndGetTableColumnData( elem );
			TableColumnData<RR> tcd = getTableColumnData( elem );
			if( tcd != null )
				getColumnsContext().add( tcd );
		}
	}

	protected TableColumnData<RR> updateAndGetTableColumnData( TableColumn tc )
	{
		TableColumnData<RR> result = getTableColumnData(tc);
		if( ( result != null ) && ! _columnsAreResizedByProgram )
			result.setWidth( unzoomValue( tc.getWidth() ) );

		return( result );
	}

	protected TableColumnData<RR> getTableColumnData( TableColumn tc )
	{
		TableColumnData<RR> result = null;
		if( (tc != null ) && ( tc.getHeaderValue() instanceof TableColumnData ) )
			result = (TableColumnData<RR>) tc.getHeaderValue();

		return( result );
	}

	protected <R> LazyHolder<R> createLazyHolder( Supplier<R> supplier )
	{
		return( new LazyHolder(supplier) );
	}

	protected Map<String, TableColumnData<RR>> getColumnDataMap()
	{
		return( _columnDataMap );
	}

	protected TableColumnData<RR> getTableColumnData( String label )
	{
		return( getColumnDataMap().get(label) );
	}

	protected JTableArrangeableColumnsContext<RR> getColumnsContext() {
		return _columnsDataContext;
	}

	protected JTableArrangeableColumnsContext<RR> getTmpColumnsContext() {
		return _tmpColumnsDataContext;
	}

	protected InternationalizedStringConf getInternationalStrings()
	{
		return( _internationalStrings );
	}

	protected String getInternationalString( String label )
	{
		InternationalizedStringConf internationalStrings = getInternationalStrings();
		String result = null;
		if( internationalStrings != null )
			result = internationalStrings.getInternationalString(label);

		return( result );
	}

	protected void fillInitial( JTableArrangeableColumnsContext<RR> columnsDataContext )
	{
		Map<String, TableColumnData<RR>> map = getColumnDataMap();
		for( Map.Entry<String, TableColumnData<RR>> entry: map.entrySet() )
			if( entry.getValue().isInitial() )
				columnsDataContext.add( entry.getValue() );
	}

	@Override
	protected void onlyFilterData( String filter )
	{
		showDataInternal( this::resizeColumnWidths, filter );
	}

	@Override
	protected void showData()
	{
		showDataInternal( this::resizeColumnWidths, getLastFilter() );
	}

	protected void resizeColumnWidths()
	{
		try
		{
//			if( ! getList().isEmpty() )
			{
				_columnsAreResizedByProgram = true;

				TableColumnAdjuster tca = createTableColumnAdjuster(250);

				int index = 0;
				Enumeration<TableColumn> columns = getColumns();
				while( columns.hasMoreElements() )
				{
					TableColumn tc = columns.nextElement();
					TableColumnData<RR> tcd = getTableColumnData( tc );

					if( ( tcd == null ) || ( tcd.getWidth() == -1 ) )
					{
						tca.adjustColumn(index);
						updateAndGetTableColumnData( getTable().getColumnModel().getColumn(index) );
					}
					else if( tcd.getWidth() == -2 )
						autoResizeNoLimit(index);
					else
						updateTableColumn( tc, tcd );

					index++;
				}

				getTableHeader().setResizingColumn(null);
				getTable().doLayout();
			}
		}
		catch( Exception ex )
		{
			LOGGER.warn( "Error when resizing the columns of a table" );
		}
		finally
		{
			_columnsAreResizedByProgram = false;

			SwingUtilities.invokeLater( this::afterResizingColumns );
		}
	}

	@Override
	protected Object[] getColumnIds() {
		JTableArrangeableColumnsContext<RR> columnsDataContext = getColumnsContext();
		if( columnsDataContext.size() == 0 )
			fillInitial( columnsDataContext );

		Object[] result = columnsDataContext.getList().toArray();

		return( result );
	}

	protected void updateTableColumn( TableColumn tc, TableColumnData<RR> tcd )
	{
		if( tc != null )
		{
			if( tc.getResizable() )
			{
				getTableHeader().setResizingColumn(tc);
				tc.setWidth( zoomValue( tcd.getWidth() ) );
			}
		}
	}

	@Override
	protected Object[] getRow( int index, RR record )
	{
		JTableArrangeableColumnsContext<RR> columnsDataContext = getColumnsContext();

		Object[] result = columnsDataContext.getList().stream()
			.map( tcd -> tcd.getCellValue(index, record) ).toArray();

		return( result );
	}

	@Override
	protected void addModelListeners()
	{
		this.getTable().getColumnModel().addColumnModelListener( _tableColumnModelListener );
	}

	@Override
	protected void removeModelListeners()
	{
		this.getTable().getColumnModel().removeColumnModelListener( _tableColumnModelListener );
	}

	@Override
	protected MouseAdapter createMouseListener( JTable table )
	{
		return( new ClickListener( table ) );
	}

	@Override
	protected BaseJPopupMenu createTableHeaderBasePopupMenu()
	{
		TableHeaderPopupMenu result = new TableHeaderPopupMenu(false);
		result.init();

		return( result );
	}

	@Override
	protected BaseJPopupMenu createBasePopupMenu() {
		TablePopupMenu result = new TablePopupMenu();
		result.init();

		return( result );
	}

	protected void addColumn( String label )
	{
		TableColumnData<RR> tcd = getTableColumnData(label);

		if( ! getColumnsContext().contains( tcd ) )
		{
			getColumnsContext().add(tcd);

			updateInternal();
		}
	}

	protected void removeColumn( String label )
	{
		TableColumnData<RR> tcd = getTableColumnData(label);

		if( getColumnsContext().contains( tcd ) )
		{
			getColumnsContext().remove(tcd);

			updateInternal();
		}
	}

	protected void sortByColumn( Integer columnIndex, boolean ascending )
	{
		sort( getComparatorForColumn( getTableColumn( columnIndex ), ascending ) );
	}

	protected Comparator<RR> getComparatorForColumn( TableColumn tc, boolean ascending )
	{
		TableColumnData<RR> tcd = getTableColumnData(tc);

		Comparator<RR> result = null;
		if( tcd != null )
		{
			result = tcd.getComparator();
			if( ! ascending )
				result = getReverseComparator( result );
		}

		return( result );
	}

	protected Comparator<RR> getReverseComparator( Comparator<RR> comparator )
	{
		return( new ReverseComparator( comparator ) );
	}

	protected abstract void sort( Comparator<RR> comparator );

	protected boolean canSortColumn( Integer columnIndex )
	{
		return( canSortColumn( getTableColumn( columnIndex ) ) );
	}

	protected void autoResizeNoLimit( int columnIndex )
	{
		super.autoResizeNoLimit(columnIndex);

		TableColumnData<RR> tcd = getTableColumnData(getTableColumn(columnIndex));
		if( tcd != null )
			tcd.setWidth( -2 );	// setting it to be autoresized without limit from now on
	}

	protected boolean canSortColumn( TableColumn tc )
	{
		boolean result = false;
		TableColumnData<RR> tcd = getTableColumnData(tc);

		if( tcd != null )
			result = ( tcd.getComparator() != null );

		return( result );
	}

	protected class TablePopupMenu extends BaseJPopupMenu
	{
		public TablePopupMenu()
		{
			super( getTable(), String.class );
		}

		public void init()
		{
			super.init();

			createFixedPart();
		}

		protected void createFixedPart()
		{
			addMenuComponent( CONF_DELETE_SELECTED_ROWS, new JMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_DELETE_SELECTED_ROWS),
				() -> notifyRecordsDeleted(),
				IconResources.DELETE_ICON_RESOURCE);
			addMenuComponent( CONF_RESET_SELECTION, new JMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_RESET_SELECTION),
				() -> resetSelection(),
				IconResources.UNDO_ICON_RESOURCE );
			addMenuComponent( CONF_SHOW_CURRENT_ROW, new JMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_SHOW_CURRENT_ROW),
				() -> showCurrentRow(),
				IconResources.LOCATE_ICON_RESOURCE );
		}

		@Override
		protected void preparePopupMenuItems() {
			preparePopupMenuItemsFixedPart();
			setCurrentLanguageTexts();
		}

		protected void preparePopupMenuItemsFixedPart() {
			boolean enable = !isSelectionEmpty();
			getMenuItem(CONF_DELETE_SELECTED_ROWS).setEnabled( enable );
			getMenuItem(CONF_RESET_SELECTION).setEnabled( enable );

			enable = ( getCurrentRowViewIndex() != null );
			getMenuItem(CONF_SHOW_CURRENT_ROW).setEnabled( enable );
		}
	}

	protected class TableHeaderPopupMenu extends BaseJPopupMenu
	{
		protected int _variableStartIndex;
		protected boolean _addSortColumnMenuItems;


		public TableHeaderPopupMenu( boolean addSortColumnMenuItems )
		{
			super( getTable(), String.class );
			_addSortColumnMenuItems = addSortColumnMenuItems;
		}

		public void init()
		{
			super.init();

			createFixedPart();
			if( _addSortColumnMenuItems )
				createSortPart();
			createVariablePart();
		}

		protected void createFixedPart()
		{
			addMenuComponent( CONF_AUTORESIZE_THIS_COLUMN, new JMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_AUTORESIZE_THIS_COLUMN),
				() -> autoResizeNoLimit( calculatePopupColumnIndex() ),
				"com/frojasg1/generic/resources/othericons/adjust.png");
			addMenuComponent( CONF_AUTORESIZE_ALL_COLUMNS, new JMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_AUTORESIZE_ALL_COLUMNS),
				() -> autoResizeAllColumns(),
				"com/frojasg1/generic/resources/othericons/adjust.png" );
			addMenuComponent( CONF_AUTORESIZE_ALWAYS_ALL_COLUMNS, new JCheckBoxMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_AUTORESIZE_ALWAYS_ALL_COLUMNS),
				() -> {
					setAutoresizeAlwaysAllColumns(true);
					autoResizeAllColumns();
					},
				"com/frojasg1/generic/resources/othericons/adjust.png");

			add( new Separator() );
		}

		protected void createSortPart()
		{
			addMenuComponent( CONF_SORT_ASCENTING_BY_THIS_COLUMN, new JMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_SORT_ASCENTING_BY_THIS_COLUMN),
				() -> sortByColumn( calculatePopupColumnIndex(), true ),
				"com/frojasg1/generic/resources/upanddown/up1.png");
			addMenuComponent( CONF_SORT_DESCENTING_BY_THIS_COLUMN, new JMenuItem(),
				() -> getInternationalStrings().getInternationalString(CONF_SORT_DESCENTING_BY_THIS_COLUMN),
				() -> sortByColumn( calculatePopupColumnIndex(), false ),
				"com/frojasg1/generic/resources/upanddown/down1.png" );

			add( new Separator() );
		}

		protected void createVariablePart()
		{
			_variableStartIndex = getComponentCount();

			for( TableColumnData<RR> tcd: getColumnDataMap().values() )
			{
				String label = tcd.getLabel();
				addMenuComponent( label, new JCheckBoxMenuItem(),
					() -> getInternationalStrings().getInternationalString( label ),
					() -> changeColumnVisibility( label ),
					null);
			}
		}

		protected void changeColumnVisibility( String label )
		{
			JCheckBoxMenuItem cb = getCheckbox(label);

			if( cb.isSelected() )
				addColumn(label);
			else
				removeColumn(label);
		}

		protected int calculatePopupColumnIndex()
		{
			return( columnAtPoint( getPoupPoint() ) );
		}

		@Override
		protected void preparePopupMenuItems() {
			preparePopupMenuItemsFixedPart();
			if( _addSortColumnMenuItems )
				preparePopupMenuItemsSortPart();
			preparePopupMenuItemsVariablePart();
		}

		protected void preparePopupMenuItemsFixedPart() {
			getCheckbox(CONF_AUTORESIZE_ALWAYS_ALL_COLUMNS).setSelected( isAutoresizeAlwaysAllColumns() );
		}

		protected void preparePopupMenuItemsSortPart() {
			boolean enabled = canSortColumn( calculatePopupColumnIndex() );
			getMenuItem(CONF_SORT_ASCENTING_BY_THIS_COLUMN).setEnabled( enabled );
			getMenuItem(CONF_SORT_DESCENTING_BY_THIS_COLUMN).setEnabled( enabled );
		}

		protected void preparePopupMenuItemsVariablePart() {

			// removing variable part, after separator, which have the names of the columns to be made visible or invisible
			// in order to add them in the current right order
			while( getComponentCount() > _variableStartIndex )
				this.remove(_variableStartIndex);

			List<TableColumnData<RR>> list = getColumnsContext().getListCopy();
			for( TableColumnData<RR> tcd: list )
				add( tcd, true );

			Map<String, TableColumnData<RR>> map = getColumnDataMap();
			for( TableColumnData<RR> tcd: map.values() )
				if( ! list.contains(tcd) )
					add( tcd, false );

			setCurrentLanguageTexts();
		}

		protected void add( TableColumnData<RR> tcd, boolean selected )
		{
			JCheckBoxMenuItem cb = (JCheckBoxMenuItem) getComponentForKey( tcd.getLabel() );
			cb.setSelected(selected);
			cb.setEnabled( tcd.canBeRemoved() );
			add( cb );
		}
	}

	protected class ClickListener extends JTableWrapper.ClickListener
	{
		public ClickListener( JTable table )
		{
			super( table );
		}

		protected boolean isNearestLeft( Rectangle rect, Point point )
		{
			return( ViewFunctions.instance().isNearestLeft( rect, point ) );
		}

		protected int getColumnToResizeIndex( MouseEvent evt )
		{
			Point point = evt.getPoint();
			int result = _parent.columnAtPoint( point );

			if( result >= 0 )
			{
				Rectangle rect = getColumnHeaderBounds(result);

				if( isNearestLeft( rect, point ) )
					result--;
			}
			else
			{
				result = _parent.getColumnCount() - 1;
			}

			return( result );
		}

		protected void triggerMouseMoveEvent( JComponent component )
		{
			MouseFunctions.triggerMouseMoveEvent( component );
		}

		@Override
		public void doubleClick(MouseEvent e)
		{
			super.doubleClick(e);

			if( ( e.getSource() == _parent.getTableHeader() ) &&
				( CursorFunctions.instance().isResizeDragging(_parent.getTableHeader().getCursor()) )
				)
			{
				int index = getColumnToResizeIndex(e);
				autoResizeNoLimit(index);
				triggerMouseMoveEvent(_parent.getTableHeader());
			}
		}
	}

	protected class TableColumnModelListenerImpl implements javax.swing.event.TableColumnModelListener
	{
		@Override
		public void columnAdded(TableColumnModelEvent e) {
		}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {

		}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
			updateTableColumnData();
		}

		@Override
		public void columnMarginChanged(ChangeEvent e) {
			updateTableColumnData();
		}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {

		}
	};
}
