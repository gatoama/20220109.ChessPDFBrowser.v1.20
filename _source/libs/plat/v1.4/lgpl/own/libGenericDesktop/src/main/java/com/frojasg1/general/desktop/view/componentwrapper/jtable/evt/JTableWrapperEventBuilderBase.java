/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.evt;

import com.frojasg1.general.collection.functions.ListOperationFunctions;
import com.frojasg1.general.collection.functions.ListStateContextBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.deleterecords.JTableWrapperRecordsDeletedEvent;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.moverecords.JTableWrapperRecordsMovedEvent;
import com.frojasg1.general.desktop.view.componentwrapper.evt.JComponentWrapperEventBuilderBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventBase;
import com.frojasg1.general.collection.functions.ListRecordMoveContext;
import com.frojasg1.general.desktop.view.componentwrapper.evt.MultiMapForBuildingJComponentWrapperEvents;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.sortedrecords.JTableWrapperRecordsSortedEvent;
import java.util.List;
import javax.swing.JTable;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperEventBuilderBase<RR>
	extends JComponentWrapperEventBuilderBase<JTable, JTableWrapperEventBase> {

	protected Class<RR> _recordClass;

	public JTableWrapperEventBuilderBase(JTable table, Class<RR> recordClass,
										boolean withInit)
	{
		super( table );
		_recordClass = recordClass;

		if( withInit )
			init();
	}

	@Override
	protected void fillMultiMap(MultiMapForBuildingJComponentWrapperEvents multiMap) {
        multiMap.put( JTableWrapperDropFilesEvent.EVENT_TYPE, JTableWrapperDropFilesEvent.class, JTableWrapperDropFilesEvent::new );
        multiMap.put( JTableWrapperHasBeenModifiedEvent.EVENT_TYPE, JTableWrapperHasBeenModifiedEvent.class, JTableWrapperHasBeenModifiedEvent::new );
        multiMap.put( JTableWrapperNewCurrentEvent.EVENT_TYPE, JTableWrapperNewCurrentEvent.class, JTableWrapperNewCurrentEvent::new );
        multiMap.put( JTableWrapperRecordsMovedEvent.EVENT_TYPE, JTableWrapperRecordsMovedEvent.class, JTableWrapperRecordsMovedEvent::new );
        multiMap.put( JTableWrapperRecordsDeletedEvent.EVENT_TYPE, JTableWrapperRecordsDeletedEvent.class, JTableWrapperRecordsDeletedEvent::new );
        multiMap.put( JTableWrapperFilterHasChangedEvent.EVENT_TYPE, JTableWrapperFilterHasChangedEvent.class, () -> new JTableWrapperFilterHasChangedEvent( _recordClass ) );
        multiMap.put( JTableWrapperSelectionHasChangedEvent.EVENT_TYPE, JTableWrapperSelectionHasChangedEvent.class, JTableWrapperSelectionHasChangedEvent::new );
	}

	public Class<RR> getRecordClass() {
		return _recordClass;
	}

	public ListRecordMoveContext<RR> createListRecordMoveContext(
		int selectedIndex, List<RR> list, int[] selection, int indexWhereToMove)
	{
		return( ListOperationFunctions.instance().createListRecordMoveContext(
				selectedIndex, list, selection, indexWhereToMove ) );
	}

	public JTableWrapperRecordsMovedEvent<RR> createJTableWrapperRecordsMovedEvent(
		int previouslySelectedIndex, List<RR> originalList, int[] previousSelection, int indexWhereToMove,
		int newSelectedIndex, List<RR> newList, int[] newSelection, int indexWhereMoved )
	{
		ListRecordMoveContext<RR> previous = createListRecordMoveContext(previouslySelectedIndex,
			originalList, previousSelection, indexWhereToMove );
		ListRecordMoveContext<RR> after = createListRecordMoveContext(newSelectedIndex,
			newList, newSelection, indexWhereMoved );

		return( createJTableWrapperRecordsMovedEvent( previous, after ) );
	}

	public JTableWrapperRecordsMovedEvent<RR> createJTableWrapperRecordsMovedEvent(
		ListRecordMoveContext<RR> previous, ListRecordMoveContext<RR> after )
	{
		JTableWrapperRecordsMovedEvent<RR> result = new JTableWrapperRecordsMovedEvent<>();
		result.setPrevious(previous);
		result.setAfter(after);

		return( result );
	}

	public ListStateContextBase<RR> createListStateContextBase(
		int selectedIndex, List<RR> list, int[] selection)
	{
		return( ListOperationFunctions.instance().createListStateContextBase(
				selectedIndex, list, selection ) );
	}

	protected <CC extends JTableWrapperRecordsModifiedEventBase<RR, ListStateContextBase<RR>>>
			CC createJTableWrapperRecordsModifiedGenEvent(
				Class<CC> clazz,
				int previouslySelectedIndex, List<RR> originalList, int[] previousSelection,
				int newSelectedIndex, List<RR> newList, int[] newSelection )
	{
		ListStateContextBase<RR> previous = createListStateContextBase(previouslySelectedIndex,
			originalList, previousSelection );
		ListStateContextBase<RR> after = createListStateContextBase(newSelectedIndex,
			newList, newSelection );

		return( createJTableWrapperRecordsModifiedGenEvent( clazz, previous, after ) );
	}

	public <CC extends JTableWrapperRecordsModifiedEventBase<RR, ListStateContextBase<RR>>>
			CC createJTableWrapperRecordsModifiedGenEvent(
				Class<CC> clazz,
				ListStateContextBase<RR> previous, ListStateContextBase<RR> after )
	{
		CC evt = buildEvent(clazz);
		evt.setPrevious(previous);
		evt.setAfter(after);

		return( evt );
	}

	public JTableWrapperRecordsDeletedEvent<RR> createJTableWrapperRecordsDeletedEvent(
		int previouslySelectedIndex, List<RR> originalList, int[] previousSelection,
		int newSelectedIndex, List<RR> newList, int[] newSelection )
	{
		return( createJTableWrapperRecordsModifiedGenEvent( JTableWrapperRecordsDeletedEvent.class,
			previouslySelectedIndex, originalList, previousSelection,
			newSelectedIndex, newList, newSelection) );
	}

	public JTableWrapperRecordsDeletedEvent<RR> createJTableWrapperRecordsDeletedEvent(
		ListStateContextBase<RR> previous, ListStateContextBase<RR> after )
	{
		return( createJTableWrapperRecordsModifiedGenEvent( JTableWrapperRecordsDeletedEvent.class,
					previous, after ) );
	}

	public JTableWrapperRecordsSortedEvent<RR> createJTableWrapperRecordsSortedEvent(
		int previouslySelectedIndex, List<RR> originalList, int[] previousSelection,
		int newSelectedIndex, List<RR> newList, int[] newSelection )
	{
		return( createJTableWrapperRecordsModifiedGenEvent( JTableWrapperRecordsSortedEvent.class,
			previouslySelectedIndex, originalList, previousSelection,
			newSelectedIndex, newList, newSelection) );
	}

	public JTableWrapperRecordsSortedEvent<RR> createJTableWrapperRecordsSortedEvent(
		ListStateContextBase<RR> previous, ListStateContextBase<RR> after )
	{
		return( createJTableWrapperRecordsModifiedGenEvent( JTableWrapperRecordsSortedEvent.class,
					previous, after ) );
	}
}
