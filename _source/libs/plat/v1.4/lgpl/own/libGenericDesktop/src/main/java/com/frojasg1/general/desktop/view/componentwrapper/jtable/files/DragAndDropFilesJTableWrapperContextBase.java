/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.files;

import com.frojasg1.general.collection.functions.ListOperationFunctions;
import com.frojasg1.general.collection.functions.ListRecordMoveContext;
import com.frojasg1.general.collection.functions.ListStateContextBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableFileDragAndDropEventContext;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapper;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.ViewAndModelSelectionArray;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperEventBuilderBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperRecordsModifiedEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.deleterecords.JTableWrapperRecordsDeletedEvent;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.moverecords.JTableWrapperRecordsMovedEvent;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.sortedrecords.JTableWrapperRecordsSortedEvent;
import com.frojasg1.general.desktop.view.transfer.file.FileTransferHandler;
import com.frojasg1.general.proxy.ProxyFunctions;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <RR>	- Record
 * @param <UI>	- JComponentUI derived class
 */
public class DragAndDropFilesJTableWrapperContextBase<RR, UI extends BaseFileListJTableUI>
		implements ReleaseResourcesable
{
	protected JTableWrapperDragAndDropFilesDecorator<UI> _dadFilesDecorator;

	protected WeakReference<JTableWrapper<RR, UI>> _tableWrapper;

	protected ViewAndModelSelectionArray _startDraggingRowSelection;

	public DragAndDropFilesJTableWrapperContextBase( JTableWrapper<RR, UI> tableWrapper )
	{
		_tableWrapper = new WeakReference<>(tableWrapper);
	}

	protected void init()
	{
		_dadFilesDecorator = createDragAndDropFilesDecorator();

		_startDraggingRowSelection = getTableWrapper().createViewAndModelRowSelectionArray(0);
	}

	protected JTableWrapper<RR, UI> getTableWrapper()
	{
		return( _tableWrapper.get() );
	}

	protected JTable getTable()
	{
		return( getTableWrapper().getTable() );
	}

	public int[] getViewArray( ViewAndModelSelectionArray arrayObj )
	{
		return( (arrayObj == null) ? null : arrayObj.getViewSelection() );
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

	protected JTableWrapperDragAndDropFilesDecorator<UI> createDragAndDropFilesDecorator()
	{
		return( new JTableWrapperDragAndDropFilesDecorator<>(getTableWrapper()) );
	}

	public JTableWrapperDragAndDropFilesDecorator<UI> getDragAndDropFilesDecorator() {
		return _dadFilesDecorator;
	}

	protected FileTransferHandler<JTableFileDragAndDropEventContext> createFileTransferHandler()
	{
		return( getDragAndDropFilesDecorator().createFileTransferHandler() );
	}

	@Override
	public void releaseResources()
	{
		getDragAndDropFilesDecorator().releaseResources();
	}

	protected Predicate<Point> getCanStartDraggingFunction() {
		return( this::canStartDragging );
	}

	public int[] getPreviousViewRowSelection() {
		return( getTableWrapper().getPreviousViewRowSelection() );
	}

	protected boolean isFiltered()
	{
		return( getTableWrapper().isFiltered() );
	}

	public boolean canStartDragging( Point point )
	{
		boolean result = !isFiltered() && previousWasRowSelected(point); //&& !isPreviousWasSelectionAdjusting();
		if( result )
		{
			setViewArray( _startDraggingRowSelection, getPreviousViewRowSelection() );

			createProxyForListSelectionModelAndSetItToTable();
		}

		return( result );
	}

	protected boolean previousWasRowSelected( Point point )
	{
		return( getTableWrapper().isRowSelected(getPreviousViewRowSelection(), point ) );
	}

	protected void updateUI( UI result )
	{
		result.setCanStartDraggingFunction( getCanStartDraggingFunction() );
		result.setDropListener( this::notifyDrop );
		result.setDeleteListener( this::notifyRecordsDeleted );
		result.setStartDraggingListener( this::startDragging );
		result.setStopDraggingListener( this::stopDragging );
	}

	public void notifyEvt( JTableWrapperEventBase evt )
	{
		getTableWrapper().notifyEvt(evt);
	}

	protected void notifyDrop()
	{
		notifyEvt( createRecordsMovedEvt() );
	}

	protected boolean isEmpty( int[] array )
	{
		return( ( array == null ) || ( array.length == 0 ) ||
				( array.length == 1 ) && ( array[0] == -1 ) );
	}

	public void notifyRecordsDeleted()
	{
		if( ! isEmpty( getTableWrapper().getTable().getSelectedRows() ) )
			notifyEvt( createRecordsDeletedEvt() );
	}

	public void notifyRecordsSorted( Comparator<RR> comparator )
	{
		notifyEvt( createRecordsSortedEvt(comparator) );
	}

	protected void createProxyForListSelectionModelAndSetItToTable()
	{
		int[] startDraggingRowSelection = getViewArray( _startDraggingRowSelection );

		getTableWrapper().createProxyForListSelectionModelAndSetItToTable( startDraggingRowSelection );
	}

	protected void restablishListSelectionModel()
	{
		getTableWrapper().restablishListSelectionModel();
	}

	protected void startDragging()
	{
//		createProxyForListSelectionModelAndSetItToTable();
	}

	protected void stopDragging()
	{
		SwingUtilities.invokeLater( this::restablishListSelectionModel );
	}

	protected ListOperationFunctions getListOperFunctions()
	{
		return( ListOperationFunctions.instance() );
	}

	public <RR> ListRecordMoveContext<RR> move( ListRecordMoveContext<RR> input )
	{
		return( getListOperFunctions().move(input) );
	}

	public <RR> ListStateContextBase<RR> delete( ListStateContextBase<RR> input )
	{
		return( getListOperFunctions().delete(input) );
	}

	public <RR> ListStateContextBase<RR> sort( ListStateContextBase<RR> input,
												Comparator<RR> comparator )
	{
		return( getListOperFunctions().sort(input, comparator) );
	}

	protected JTableWrapperRecordsMovedEvent<RR> createRecordsMovedEvt()
	{
		Integer currentIndex = getTableWrapper().getCurrentModelRowIndex();
		List<RR> originalList = getTableWrapper().getList();
		int[] currentSelection = getTableWrapper().getAppropriateRowModelSelection();
		int indexWhereToMove = getDragAndDropFilesDecorator().getIndexWhereToDropFiles();

		ListRecordMoveContext<RR> previous = getEventBuilder().createListRecordMoveContext(
			currentIndex, originalList, currentSelection, indexWhereToMove );
		ListRecordMoveContext<RR> after = move( previous );

		JTableWrapperRecordsMovedEvent<RR> result =
			getEventBuilder().createJTableWrapperRecordsMovedEvent(	previous, after );

		return( result );
	}

	protected JTableWrapperRecordsDeletedEvent<RR> createRecordsDeletedEvt()
	{
		return( createRecordsModifiedGenEvt( JTableWrapperRecordsDeletedEvent.class, this::delete ) );
	}

	protected JTableWrapperRecordsSortedEvent<RR> createRecordsSortedEvt(Comparator<RR> comparator)
	{
		return( createRecordsModifiedGenEvt( JTableWrapperRecordsSortedEvent.class,
											input -> sort( input, comparator ) ) );
	}

	protected <CC extends JTableWrapperRecordsModifiedEventBase<RR, ListStateContextBase<RR>>>
			CC createRecordsModifiedGenEvt( Class<CC> clazz,
							Function<ListStateContextBase<RR>, ListStateContextBase<RR>> modificationFunction )
	{
		Integer currentIndex = getTableWrapper().getCurrentModelRowIndex();
		List<RR> originalList = getTableWrapper().getList();
		int[] currentSelection = getTableWrapper().getAppropriateRowModelSelection();

		ListStateContextBase<RR> previous = getEventBuilder().createListStateContextBase(
			currentIndex, originalList, currentSelection );
		ListStateContextBase<RR> after = modificationFunction.apply( previous );

		CC result =	getEventBuilder().createJTableWrapperRecordsModifiedGenEvent(
															clazz, previous, after );

		return( result );
	}

	public JTableWrapperEventBuilderBase<RR> getEventBuilder() {
		return getTableWrapper().getEventBuilder();
	}
}
