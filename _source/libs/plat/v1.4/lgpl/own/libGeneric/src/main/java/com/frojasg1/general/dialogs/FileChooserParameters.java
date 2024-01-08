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
package com.frojasg1.general.dialogs;

import com.frojasg1.general.dialogs.filefilter.GenericFileFilter;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FileChooserParameters
{
	// next constants are for the integer parameter open_save_dialog of fileChooser
	public static final int OPEN = DialogsWrapper.OPEN;
	public static final int SAVE = DialogsWrapper.SAVE;

	// next constaants are for the integer mode parameter
	public static final int DIRECTORIES_ONLY = 0;
	public static final int FILES_ONLY = 1;
	public static final int FILES_AND_DIRECTORIES = 2;
	

	protected int _openOrSaveDialog = -1;
	protected List<FilterForFile> _fileFilterList = null;
	protected String _defaultFileName = null;
	protected int _mode = -1;	// this attribute can take the values of the predefined constants.

	protected FilterForFile _chosenFilterForFile = null;

	protected GenericFileFilter _genericFileFilter = null;

	protected Locale _locale = null;

	public FileChooserParameters()
	{}

	public Locale getLocale()
	{
		return( _locale );
	}

	public void setLocale( Locale locale )
	{
		_locale = locale;
	}

	public int getOpenOrSaveDialog()
	{
		return( _openOrSaveDialog );
	}

	public void setOpenOrSaveDialog( int value )
	{
		_openOrSaveDialog = value;
	}

	public List<FilterForFile> getListOfFilterForFile()
	{
		return( _fileFilterList );
	}

	public void setListOfFilterForFile( List<FilterForFile> list )
	{
		_fileFilterList = list;
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

	public FilterForFile getChosenFilterForFile()
	{
		return( _chosenFilterForFile );
	}

	public void setChosenFilterForFile( FilterForFile cfff )
	{
		_chosenFilterForFile = cfff;
	}

	public GenericFileFilter getGenericFileFilter()
	{
		return( _genericFileFilter );
	}

	public void setGenericFileFilter( GenericFileFilter gff )
	{
		_genericFileFilter = gff;
	}
}
