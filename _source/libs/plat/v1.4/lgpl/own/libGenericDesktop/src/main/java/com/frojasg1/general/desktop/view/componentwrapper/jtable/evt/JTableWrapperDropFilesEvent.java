/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.evt;

import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventType;
import java.io.File;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperDropFilesEvent extends JTableWrapperEventBase
{
	public static final long EVENT_TYPE = JTableWrapperEventType.FILES_DROPPED;

	protected List<File> _fileList;
	protected int _indexForInsertion;

	public JTableWrapperDropFilesEvent()
	{
		super( EVENT_TYPE );
	}

	public List<File> getFileList() {
		return _fileList;
	}

	public void setFileList(List<File> _fileList) {
		this._fileList = _fileList;
	}

	public int getIndexForInsertion() {
		return _indexForInsertion;
	}

	public void setIndexForInsertion(int _indexForInsertion) {
		this._indexForInsertion = _indexForInsertion;
	}
}
