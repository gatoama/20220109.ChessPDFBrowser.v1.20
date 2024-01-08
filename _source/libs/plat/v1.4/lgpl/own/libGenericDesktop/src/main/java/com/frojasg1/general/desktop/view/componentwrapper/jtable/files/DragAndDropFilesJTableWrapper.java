/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.files;

import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableFileDragAndDropEventContext;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapper;
import com.frojasg1.general.desktop.view.paint.PaintTextContext;
import com.frojasg1.general.desktop.view.transfer.file.FileTransferHandler;
import java.awt.Point;
import java.util.List;
import javax.swing.JTable;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <RR>	- Record
 * @param <UI>	- JComponentUI derived class
 */
public abstract class DragAndDropFilesJTableWrapper<RR, UI extends BaseFileListJTableUI>
	extends JTableWrapper<RR, UI>
{
	protected DragAndDropFilesJTableWrapperContextBase<RR, UI> _dragAndDropJTableWrapperContext;

	public DragAndDropFilesJTableWrapper( JTable table, Class<RR> recordClass )
	{
		super( table, recordClass );
	}

	@Override
	protected void init()
	{
		_dragAndDropJTableWrapperContext = createDragAndDropFilesJTableWrapperContextBase();

		super.init();
	}

	@Override
	protected void notifyRecordsDeleted()
	{
		getDragAndDropJTableWrapperContext().notifyRecordsDeleted();
	}

	protected DragAndDropFilesJTableWrapperContextBase<RR, UI> createDragAndDropFilesJTableWrapperContextBase()
	{
		DragAndDropFilesJTableWrapperContextBase result = new DragAndDropFilesJTableWrapperContextBase(this);
		result.init();

		return( result );
	}

	public DragAndDropFilesJTableWrapperContextBase<RR, UI> getDragAndDropJTableWrapperContext() {
		return _dragAndDropJTableWrapperContext;
	}

	@Override
	protected FileTransferHandler<JTableFileDragAndDropEventContext> createFileTransferHandler()
	{
		return( getDragAndDropJTableWrapperContext().createFileTransferHandler() );
	}

	@Override
	public void releaseResources()
	{
		getDragAndDropJTableWrapperContext().releaseResources();

		super.releaseResources();
	}

	protected abstract PaintTextContext createPaintTextContext();
	protected abstract UI createEmptyUI();

	protected boolean canStartDragging( Point point )
	{
		return( getDragAndDropJTableWrapperContext().canStartDragging(point) );
	}

	@Override
	protected UI createUI()
	{
		UI result = createEmptyUI();

		return( result );
	}

	@Override
	protected void updateUI( UI result )
	{
		result.setPaintTextContext( createPaintTextContext() );
		getDragAndDropJTableWrapperContext().updateUI(result);
	}

	@Override
	protected void setRowSelection( int[] viewRowSelection, List<Exception> callStacks )
	{
		super.setRowSelection(viewRowSelection, callStacks);
		if( viewRowSelection != null )
			updatePreviousRowSelection();
	}
}
