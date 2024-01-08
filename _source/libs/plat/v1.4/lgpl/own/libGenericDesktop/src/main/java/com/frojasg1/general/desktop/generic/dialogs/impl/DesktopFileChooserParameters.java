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
package com.frojasg1.general.desktop.generic.dialogs.impl;

import com.frojasg1.general.desktop.dialogs.implementation.filefilter.DesktopGenericFileFilter;
import com.frojasg1.general.dialogs.FileChooserParameters;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilter;
import java.util.List;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopFileChooserParameters extends FileChooserParameters
{
	protected List<FileFilter> _fileFilterList = null;

	protected int _openOrSaveDialog = -1;
	protected String _defaultFileName = null;
	protected int _mode = -1;	// this attribute can take the values of the predefined constants.

	protected FileFilter _chosenFileFilter = null;

	protected DesktopGenericFileFilter _genericFileFilter = null;

	public int getOpenOrSaveDialog()
	{
		return( _openOrSaveDialog );
	}

	public void setOpenOrSaveDialog( int value )
	{
		_openOrSaveDialog = value;
	}

	public String getDefaultFileName()
	{
		return( _defaultFileName );
	}

	public void setDefaultFileName( String value )
	{
		_defaultFileName = value;
	}

	public int getMode()
	{
		return( _mode );
	}

	public void setMode( int value )
	{
		_mode = value;
	}

	public List<FileFilter> getListOfFileFilter()
	{
		return( _fileFilterList );
	}

	public void setListOfFileFilter( List<FileFilter> list )
	{
		_fileFilterList = list;
	}

	public FileFilter getChosenFileFilter()
	{
		return( _chosenFileFilter );
	}

	public void setChosenFileFilter( FileFilter cff )
	{
		_chosenFileFilter = cff;
	}

	public DesktopGenericFileFilter getGenericFileFilter()
	{
		return( _genericFileFilter );
	}

	public void setGenericFileFilter( GenericFileFilter gff )
	{
		_genericFileFilter = (DesktopGenericFileFilter) gff;
	}
}
