/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer.file;

import com.frojasg1.general.desktop.view.transfer.DragAndDropEvent;
import com.frojasg1.general.desktop.view.transfer.DragAndDropEventType;
import java.awt.Component;
import java.awt.Point;
import java.io.File;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <CC>	- Context (depending on the particular component class)
 */
public class FileDragAndDropEvent<CC> extends DragAndDropEvent {
	protected List<File> _fileList;
	protected CC _context;

	public FileDragAndDropEvent( DragAndDropEventType type, Component source,
								Point dropLocation, List<File> fileList )
	{
		super( type, source, dropLocation );
		_fileList = fileList;
	}

	public List<File> getFileList() {
		return _fileList;
	}

	public CC getContext() {
		return _context;
	}

	public void setContext(CC _context) {
		this._context = _context;
	}
}
