/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.files;

import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableFileDragAndDropEventContext;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapper;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperDropFilesEvent;
import com.frojasg1.general.desktop.view.transfer.DragAndDropEventType;
import com.frojasg1.general.desktop.view.transfer.file.FileDragAndDropEvent;
import com.frojasg1.general.desktop.view.transfer.file.FileTransferHandler;
import com.frojasg1.general.listeners.filterevents.EventListenerContextBase;
import com.frojasg1.general.view.ReleaseResourcesable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperDragAndDropFilesDecorator<UI extends BaseFileListJTableUI>
	implements ReleaseResourcesable
{
	protected WeakReference<JTableWrapper<?, UI>> _tableWrapper;

	protected EventListenerContextBase<FileDragAndDropEvent<JTableFileDragAndDropEventContext>, DragAndDropEventType> _fileTransferListener;
	protected EventListenerContextBase<FileDragAndDropEvent<JTableFileDragAndDropEventContext>, DragAndDropEventType> _uiListener;

	public JTableWrapperDragAndDropFilesDecorator( JTableWrapper tableWrapper )
	{
		_tableWrapper = new WeakReference<>(tableWrapper);
	}

	protected UI getCompUI()
	{
		return( getTableWrapper().getCompUI() );
	}

	protected <EVT extends JTableWrapperEventBase> EVT createEvt( Class<EVT> clazz )
	{
		return( getTableWrapper().createEvt(clazz) );
	}

	public FileTransferHandler<JTableFileDragAndDropEventContext> createFileTransferHandler()
	{
		removeListeners();

		FileTransferHandler<JTableFileDragAndDropEventContext> result = new JTableFileTransferHandler<>();
		_uiListener = result.addListener( getCompUI()::processDragAndDropEvent );
		_fileTransferListener = result.addListenerContext( this::filesDropped, DragAndDropEventType.DROPPED );

		return( result );
	}

	protected JTableWrapper<?, UI> getTableWrapper()
	{
		return( _tableWrapper.get() );
	}

	protected FileTransferHandler<JTableFileDragAndDropEventContext> getFileTransferHandler()
	{
		return( getTableWrapper().getFileTransferHandler() );
	}

	protected void removeListeners()
	{
		FileTransferHandler<JTableFileDragAndDropEventContext> fth = getFileTransferHandler();
		if( fth != null )
		{
			fth.removeListener(_uiListener);
			fth.removeListener(_fileTransferListener);
		}
	}

	protected void notifyEvt(JTableWrapperEventBase evt)
	{
		getTableWrapper().notifyEvt(evt);
	}

	protected void filesDropped( FileDragAndDropEvent evt )
	{
		JTableWrapperDropFilesEvent result = createEvt( JTableWrapperDropFilesEvent.class );
		result.setFileList( evt.getFileList() );
		result.setIndexForInsertion( getIndexWhereToDropFiles() );

		notifyEvt(result);
	}

	protected JTableWrapperDropFilesEvent createFilesDropped( FileDragAndDropEvent evt )
	{
		JTableWrapperDropFilesEvent result = null;
		if( evt != null )
		{
			result = createEvt( JTableWrapperDropFilesEvent.class );
			result.setFileList( evt.getFileList() );
			result.setIndexForInsertion( getIndexWhereToDropFiles() );
		}

		return( result );
	}

	protected int getNumOfRecords()
	{
		int result = 0;
		List list = getTableWrapper().getList();
		if( list != null )
			result = list.size();

		return( result );
	}

	public int getIndexWhereToDropFiles()
	{
		return( Math.min( getNumOfRecords(), getCompUI().getRowIndexWhereToDrop() ) );
	}

	@Override
	public void releaseResources()
	{
		removeListeners();
		_fileTransferListener = null;
		_uiListener = null;
	}

	protected class JTableFileTransferHandler<CC> extends FileTransferHandler<CC>
	{
		@Override
		protected boolean canImportInternal(TransferSupport info) {
			return( !getTableWrapper().isFiltered() && super.canImportInternal(info) );
		}
	}
}
