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
package com.frojasg1.general.commandline.generic.dialogs;

import com.frojasg1.applications.common.configuration.application.ConfigurationForFileChooserInterface;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.dialogs.DialogsWrapperFunctions;
import com.frojasg1.general.dialogs.FileChooserParameters;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilterChooser;
import com.frojasg1.general.dialogs.filefilter.impl.GenericFileFilterChooserImpl;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.general.zoom.ZoomInterface;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EmptyDialogsWrapper implements DialogsWrapper
{
	protected static EmptyDialogsWrapper _instance = null;

	protected GenericFileFilterChooser _genericFileFilterChooser = null;

	public static EmptyDialogsWrapper instance()
	{
		if( _instance == null )
			_instance = new EmptyDialogsWrapper();

		return( _instance );
	}

	protected EmptyDialogsWrapper()
	{
		_genericFileFilterChooser = createGenericFileFilterChooser();
	}

	protected GenericFileFilterChooser createGenericFileFilterChooser()
	{
		GenericFileFilterChooserImpl result = new GenericFileFilterChooserImpl();
		result.init();

		return( result );
	}

	@Override
	public void showMessageDialog(ViewComponent parent, Object message, String title, int messageType)
	{
	}

	@Override
	public void showMessageDialog(ViewComponent parent, Object message, String title, int messageType, ZoomInterface conf)
	{
	}

	@Override
	public int showOptionDialog(ViewComponent parent, Object message, String title, int optionType, int messageType, Object[] options, Object initialValue)
	{
		return( -1 );
	}

	@Override
	public int showOptionDialog(ViewComponent parent, Object message, String title, int optionType, int messageType, Object[] options, Object initialValue, ZoomInterface conf)
	{
		return( -1 );
	}

	@Override
	public String showFileChooserDialog(ViewComponent parent, int open_save_dialog, List<FilterForFile> fileFilterList, String defaultFileName)
	{
		return( null );
	}

	@Override
	public String showFileChooserDialog(ViewComponent parent, int open_save_dialog, List<FilterForFile> fileFilterList, String defaultFileName, ConfigurationForFileChooserInterface conf)
	{
		return( null );
	}

	@Override
	public String showFileChooserDialog(ViewComponent parent, FileChooserParameters pffc, ConfigurationForFileChooserInterface conf)
	{
		return( null );
	}

	@Override
	public GenericFileFilterChooser getGenericFileFilterChooser() {
		return( _genericFileFilterChooser );
	}
}
