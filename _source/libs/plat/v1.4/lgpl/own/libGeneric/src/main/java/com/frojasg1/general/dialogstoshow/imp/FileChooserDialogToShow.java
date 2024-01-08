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
package com.frojasg1.general.dialogstoshow.imp;

import com.frojasg1.applications.common.configuration.application.ConfigurationForFileChooserInterface;
import com.frojasg1.general.dialogs.FileChooserParameters;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.general.dialogstoshow.GenericDialogToShowInt;
import com.frojasg1.generic.GenericFunctions;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FileChooserDialogToShow extends GenericDialogToShowBase
{
	/*
	DialogsWrapper.OPEN = 0;
	DialogsWrapper.SAVE = 1;
	*/
	protected int _openSaveDialog = -1;
	
	protected List<FilterForFile> _fileFilterList = null;
	
	protected String _defaultFileName = null;

	protected ConfigurationForFileChooserInterface _conf = null;

	protected FileChooserParameters _fcp = null;

	public FileChooserDialogToShow( ViewComponent parent,
									int open_save_dialog,
									List<FilterForFile> fileFilterList,
									String defaultFileName,
									ConfigurationForFileChooserInterface conf )
	{
		super( parent );
		_openSaveDialog = open_save_dialog;
		_fileFilterList = fileFilterList;
		_defaultFileName = defaultFileName;
		_conf = conf;
	}

	public FileChooserDialogToShow( ViewComponent parent,
									FileChooserParameters fcp,
									ConfigurationForFileChooserInterface conf )
	{
		super( parent );
		_fcp = fcp;
		_conf = conf;
	}

	public void setOpenSaveDialog( int openSaveDialog )
	{
		_openSaveDialog =  openSaveDialog;
	}

	public void setFileFilterList( List<FilterForFile> fileFilterList )
	{
		_fileFilterList = fileFilterList;
	}

	public void setDefaultFileName( String defaultFileName )
	{
		_defaultFileName = defaultFileName;
	}

	@Override
	public Result showDialog()
	{
		String chosenFile = null;
		
		if( _fcp == null )
			chosenFile = GenericFunctions.instance().getDialogsWrapper().showFileChooserDialog( _parent, _openSaveDialog,
																									_fileFilterList, _defaultFileName,
																									_conf );
		else
			chosenFile = GenericFunctions.instance().getDialogsWrapper().showFileChooserDialog( _parent, _fcp,
																									_conf );

		Result result = new Result( chosenFile );

		return( result );
	}

	public static class Result implements GenericDialogToShowInt.Result
	{
		protected String _chosenFile = null;

		public Result( String chosenFile )
		{
			_chosenFile = chosenFile;
		}

		public String getChosenFile()
		{
			return( _chosenFile );
		}
	}
}
