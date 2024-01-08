/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer.file;

import com.frojasg1.general.desktop.view.transfer.DragAndDropEventType;
import com.frojasg1.general.desktop.view.transfer.ListOfDragAndDropListenersBase;
import com.frojasg1.general.desktop.view.transfer.TransferHandlerBase;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import javax.swing.TransferHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *  https://stackoverflow.com/questions/9192371/dragn-drop-files-from-the-os-to-java-application-swing
 */
public class FileTransferHandler<CC> extends TransferHandlerBase<FileDragAndDropEvent<CC>>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileTransferHandler.class);

	public FileTransferHandler()
	{
		init();
	}

	@Override
	protected boolean canImportInternal(TransferSupport info) {
		return( info.isDataFlavorSupported(DataFlavor.javaFileListFlavor) );
	}

	@Override
	protected FileDragAndDropEvent<CC> createCanImportEvent(TransferSupport info) {
		return( createEvent( DragAndDropEventType.CAN_IMPORT_FIRED, info, null ) );
	}

	@Override
	protected FileDragAndDropEvent<CC> createDroppedEvent(TransferSupport info) {
		FileDragAndDropEvent<CC> result = null;
		// Get the fileList that is being dropped.
		Transferable t = info.getTransferable();
		List<File> data;
		try {
			data = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
			result = createEvent( DragAndDropEventType.DROPPED, info, data );
		}
		catch (Exception e) {
			LOGGER.error( "Error dropping files", e );
		}

		return( result );
	}

	protected FileDragAndDropEvent<CC> createEvent( DragAndDropEventType type,
				TransferHandler.TransferSupport info, List<File> fileList )
	{
		return( new FileDragAndDropEvent<>( type, info.getComponent(),
				info.getDropLocation().getDropPoint(), fileList ) );
	}
}
