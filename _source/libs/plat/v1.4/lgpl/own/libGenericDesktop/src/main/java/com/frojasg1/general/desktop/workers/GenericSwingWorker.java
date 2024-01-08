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
package com.frojasg1.general.desktop.workers;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ConfigurationForFileChooserInterface;
import com.frojasg1.general.desktop.generic.view.imp.DesktopViewFacilitiesImp;
import com.frojasg1.general.dialogs.DialogShower;
import com.frojasg1.general.dialogs.FileChooserParameters;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilterChooser;
import com.frojasg1.general.dialogs.filefilter.impl.GenericFileFilterChooserImpl;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.general.dialogstoshow.GenericDialogToShowInt;
import com.frojasg1.general.dialogstoshow.imp.FileChooserDialogToShow;
import com.frojasg1.general.dialogstoshow.imp.MessageDialogToShow;
import com.frojasg1.general.dialogstoshow.imp.OptionDialogToShow;
import com.frojasg1.general.zoom.ZoomInterface;
import com.frojasg1.generic.workers.GenericWorkerChunk;
import com.frojasg1.generic.workers.GenericWorkerResult;
import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class GenericSwingWorker
	extends SwingWorker< GenericWorkerResult, GenericWorkerChunk >
	implements DialogShower
{
	protected ViewComponent _parent = null;

	private boolean _isWaitingForDialogToComplete = false;
	private GenericDialogToShowInt.Result _dialogResult = null;

	protected BaseApplicationConfigurationInterface _conf = null;

	protected GenericFileFilterChooser _genericFileFilterChooser = null;

	public GenericSwingWorker( Component parent )
	{
		_parent = DesktopViewFacilitiesImp.instance().createViewComponent(parent);
		_genericFileFilterChooser = createGenericFileFilterChooser();
	}

	protected GenericFileFilterChooser createGenericFileFilterChooser()
	{
		GenericFileFilterChooserImpl result = new GenericFileFilterChooserImpl();
		result.init();

		return( result );
	}

	public void setConfiguration( BaseApplicationConfigurationInterface conf )
	{
		_conf = conf;
	}

	@Override
	public ViewComponent getParentViewComponent()
	{
		return( _parent );
	}

	/**
	 * Function to be used only by the working thread.
	 * @param dialog
	 * @return 
	 */
	public GenericDialogToShowInt.Result showDialog( GenericDialogToShowInt dialog )
	{
		_isWaitingForDialogToComplete = true;
		GenericDialogToShowInt[] array = new GenericDialogToShowInt[1];
		array[0]=dialog;
		publish(array);

		while( _isWaitingForDialogToComplete )
		{
			try
			{
				Thread.sleep( 100 );
			}
			catch( Throwable th )
			{}
		}

		return( _dialogResult );
	}

	/**
	 * This function is called in the EDT (Event Dispatcher Thread).
	 * @param chunks 
	 */
	@Override
	protected void process( List<GenericWorkerChunk> chunks )
	{
		if( chunks != null )
		{
			Iterator<GenericWorkerChunk> it = chunks.iterator();
			while( it.hasNext() )
			{
				GenericWorkerChunk gswc = it.next();

				if( gswc instanceof GenericDialogToShowInt )
				{
					try
					{
						_dialogResult = null;
						GenericDialogToShowInt dialogToShow = (GenericDialogToShowInt) gswc;
						_dialogResult = processGenericDialogToShow( dialogToShow );
					}
					finally
					{
						_isWaitingForDialogToComplete = false;
					}
				}
				else
					processOtherChunkTypes( gswc );
			}
		}
	}

	protected GenericDialogToShowInt.Result processGenericDialogToShow( GenericDialogToShowInt dialogToShow )
	{
		GenericDialogToShowInt.Result result = null;
		if( dialogToShow != null )
		{
			result = dialogToShow.showDialog();
		}
		return( result );
	}

	/**
	 * Function to be overriden by the subclasses if they have other types of chunks to process
	 * This function is executed in the EDT (Event Dispatch Thread).
	 * 
	 * @param gswc 
	 */
	protected void processOtherChunkTypes( GenericWorkerChunk gswc )
	{}

	/**
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 * @param messageType    an example for this parameter: DialogsWrapper.ERROR_MESSAGE
	 */
	public void showMessageDialog( ViewComponent parent, Object message, String title, int messageType )
	{
		showMessageDialog( parent, message, title, messageType, _conf );
	}

	public void showMessageDialog( ViewComponent parent, Object message, String title, int messageType,
									ZoomInterface conf )
	{
		GenericDialogToShowInt dialToShow = new MessageDialogToShow( parent, message,
																	title, messageType,
																	conf );
		showDialog( dialToShow );
	}

	/**
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 * @param optionType		an example for this parameter: DialogsWrapper.YES_NO_CANCEL_OPTION
	 * @param messageType		an example for this parameter: DialogsWrapper.QUESTION_MESSAGE
	 * @param options
	 * @param initialValue
	 * @return 
	 */
	public int showOptionDialog( ViewComponent parent, Object message, String title, int optionType,
										int messageType, Object[] options, Object initialValue )
	{
		return( showOptionDialog( parent, message, title, optionType, messageType,
									options, initialValue, _conf ) );
	}

	public int showOptionDialog( ViewComponent parent, Object message, String title, int optionType,
										int messageType, Object[] options, Object initialValue,
										ZoomInterface conf )
	{
		int chosenIndex = -1;
		
		GenericDialogToShowInt dialToShow = new OptionDialogToShow( parent, message, title,
																	optionType, messageType,
																	options, initialValue,
																	conf );
		GenericDialogToShowInt.Result result = (OptionDialogToShow.Result) showDialog( dialToShow );

		OptionDialogToShow.Result resultOption = null;
		if( result instanceof OptionDialogToShow.Result )
		{
			resultOption = (OptionDialogToShow.Result) result;
			chosenIndex = resultOption.getChosenIndex();
		}

		return( chosenIndex );
	}

	/**
	 * 
	 * @param parent
	 * @param open_save_dialog	. This parameter has to contain either DialogsWrapper.OPEN or
								DialogsWrapper.SAVE
	 * @param fileFilterList
	 * @return 
	 */
	public String showFileChooserDialog( ViewComponent parent,
													int open_save_dialog,
													List<FilterForFile> fileFilterList,
													String defaultFileName )
	{
		return( showFileChooserDialog( parent, open_save_dialog, fileFilterList,
										defaultFileName, _conf ) );
	}

	public String showFileChooserDialog( ViewComponent parent,
													int open_save_dialog,
													List<FilterForFile> fileFilterList,
													String defaultFileName,
													ConfigurationForFileChooserInterface conf )
	{
		String chosenFile = "";

		GenericDialogToShowInt dialToShow = new FileChooserDialogToShow( parent, open_save_dialog,
																		fileFilterList, defaultFileName,
																		conf );
		GenericDialogToShowInt.Result result = showDialog( dialToShow );

		FileChooserDialogToShow.Result resultFC = null;
		if( result instanceof FileChooserDialogToShow.Result )
		{
			resultFC = (FileChooserDialogToShow.Result) result;
			chosenFile = resultFC.getChosenFile();
		}
		return( chosenFile );
	}

	public String showFileChooserDialog( ViewComponent parent,
													FileChooserParameters fcp,
													ConfigurationForFileChooserInterface conf )
	{
		String chosenFile = "";

		GenericDialogToShowInt dialToShow = new FileChooserDialogToShow( parent, fcp,
																		conf );
		GenericDialogToShowInt.Result result = showDialog( dialToShow );

		FileChooserDialogToShow.Result resultFC = null;
		if( result instanceof FileChooserDialogToShow.Result )
		{
			resultFC = (FileChooserDialogToShow.Result) result;
			chosenFile = resultFC.getChosenFile();
		}
		return( chosenFile );
	}

	@Override
	public GenericFileFilterChooser getGenericFileFilterChooser() {
		return( _genericFileFilterChooser );
	}
}
