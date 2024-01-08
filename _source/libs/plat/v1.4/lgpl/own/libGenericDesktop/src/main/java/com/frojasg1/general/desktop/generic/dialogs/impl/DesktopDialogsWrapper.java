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

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ConfigurationForFileChooserInterface;
import com.frojasg1.general.desktop.dialogs.implementation.filefilter.DesktopGenericFileFilter;
import com.frojasg1.general.desktop.dialogs.implementation.filefilter.impl.DesktopGenericFileFilterImpl;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import com.frojasg1.general.dialogs.FileChooserParameters;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilter;
import com.frojasg1.general.dialogs.filefilter.GenericFileFilterChooser;
import com.frojasg1.general.dialogs.filefilter.impl.GenericFileFilterChooserImpl;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.general.zoom.ZoomInterface;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Usuario
 */
public class DesktopDialogsWrapper implements DialogsWrapper
{
	BaseApplicationConfigurationInterface _conf = null;

	protected static DesktopDialogsWrapper _instance = null;

	protected GenericFileFilterChooser _genericFileFilterChooser = null;

	public static DesktopDialogsWrapper createInstance( BaseApplicationConfigurationInterface conf )
	{
		_instance = new DesktopDialogsWrapper( conf );

		return( _instance );
	}

	public static DesktopDialogsWrapper instance()
	{
		return( _instance );
	}

	protected DesktopDialogsWrapper( BaseApplicationConfigurationInterface conf )
	{
		_conf = conf;
		_genericFileFilterChooser = createGenericFileFilterChooser();
	}

	protected GenericFileFilterChooser createGenericFileFilterChooser()
	{
		GenericFileFilterChooserImpl result = new GenericFileFilterChooserImpl();
		result.init();

		return( result );
	}

	@Override
	public void showMessageDialog(ViewComponent parent, Object message, String title, int messageType, ZoomInterface conf)
	{
		StaticDesktopDialogsWrapper.showMessageDialog_static( getAncestor( parent ),
														getMessage( message ), title,
														translateMessageType( messageType ),
														conf);
	}

	@Override
	public void showMessageDialog(ViewComponent parent, Object message, String title, int messageType)
	{
		showMessageDialog( parent, getMessage( message ), title,messageType, _conf);
	}

	@Override
	public int showOptionDialog(ViewComponent parent, Object message, String title,
								int optionType, int messageType, Object[] options,
								Object initialValue, ZoomInterface conf)
	{
		return(
				StaticDesktopDialogsWrapper.showOptionDialog_static( getAncestor( parent ),
																message, title,
																translateOptionType( optionType ),
																translateMessageType( messageType ),
																null, options,
																initialValue,
																conf )
			);
	}

	@Override
	public int showOptionDialog(ViewComponent parent, Object message,
								String title, int optionType, int messageType,
								Object[] options, Object initialValue)
	{
		return( showOptionDialog( parent, getMessage( message ), title, optionType, messageType,
									options, initialValue, _conf ) );
	}

	@Override
	public String showFileChooserDialog(ViewComponent parent, int open_save_dialog,
										List<FilterForFile> fileFilterList, String defaultFileName,
										ConfigurationForFileChooserInterface conf)
	{
		List<FileFilter> ffl = translateFilterForFileList( fileFilterList );

		int osd = translateOpenSaveDialog( open_save_dialog );

		String file = StaticDesktopDialogsWrapper.showFileChooserDialog_static( getAncestor( parent ),
													osd,
													conf,
													ffl,
													defaultFileName );

		return( file );
	}

	@Override
	public String showFileChooserDialog(ViewComponent parent,
										FileChooserParameters fcp,
										ConfigurationForFileChooserInterface conf)
	{
		DesktopFileChooserParameters dfcp = translateFileChooserParameters( fcp );
		String file = StaticDesktopDialogsWrapper.showFileChooserDialog_static( getAncestor( parent ),
													dfcp,
													( conf == null ? _conf : conf ) );

		return( file );
	}

	@Override
	public String showFileChooserDialog(ViewComponent parent, int open_save_dialog,
										List<FilterForFile> fileFilterList,
										String defaultFileName)
	{
		return( showFileChooserDialog( parent, open_save_dialog, fileFilterList,
										defaultFileName, _conf ) );
	}

	protected List<FileFilter> translateFilterForFileList( List<FilterForFile> fileFilterList )
	{
		List<FileFilter> ffl = null;
		
		if( fileFilterList != null )
		{
			ffl = new ArrayList<FileFilter>();
			Iterator<FilterForFile> it = fileFilterList.iterator();
			while( it.hasNext() )
			{
				FilterForFile fff = it.next();

				FileNameExtensionFilter fnef = new FileNameExtensionFilter( fff.getDescription(),
																				fff.getExtension() );

				ffl.add( fnef );
			}
		}
		
		return( ffl );
	}
	
	protected Component getAncestor( ViewComponent vc )
	{
		return( getAncestor( getComponent( vc ) ) );
	}

	protected Component getAncestor( Component comp )
	{
		return( ViewFunctions.instance().getRootAncestor(comp) );
	}

	protected Component getComponent( ViewComponent vc )
	{
		Component comp = null;
		
		if( vc instanceof DesktopViewComponent )
		{
			DesktopViewComponent dvc = (DesktopViewComponent) vc;

			comp = dvc.getComponent();
		}
		
		return( comp );
	}

	protected DesktopFileChooserParameters translateFileChooserParameters( FileChooserParameters fcp )
	{
		DesktopFileChooserParameters result = null;

		if( fcp != null )
		{
			result = new DesktopFileChooserParameters();

			int open_save_dialog = fcp.getOpenOrSaveDialog();
			int mode = fcp.getMode();
			List<FilterForFile> filterForFileList = fcp.getListOfFilterForFile();
			String defaultFileName = fcp.getDefaultFileName();

			List<FileFilter> fileFilterList = translateFilterForFileList(filterForFileList);
			result.setDefaultFileName(defaultFileName);
			result.setListOfFileFilter( fileFilterList );
			result.setMode( translateModeForFileChooser(mode));
			result.setOpenOrSaveDialog( translateOpenSaveDialog( open_save_dialog ) );

			FilterForFile chosenFilterForFile = fcp.getChosenFilterForFile();
			FileFilter chosenFileFilter = translateChosenFileFilter( filterForFileList, fileFilterList, chosenFilterForFile );

			result.setChosenFileFilter(chosenFileFilter);

			GenericFileFilter gff = fcp.getGenericFileFilter();
			if( gff != null )
				result.setGenericFileFilter( translateGenericFileFilter( gff ) );

			Locale locale = fcp.getLocale();
			if( locale == null )
				locale = GenericFunctions.instance().getObtainAvailableLanguages().getLocaleOfLanguage( _conf.getLanguage() );

			result.setLocale(locale);
		}

		return( result );
	}

	protected DesktopGenericFileFilter translateGenericFileFilter( GenericFileFilter gff )
	{
		DesktopGenericFileFilter result = null;
		if( gff != null )
			result = new DesktopGenericFileFilterImpl( gff );

		return( result );
	}

	protected FileFilter translateChosenFileFilter( List<FilterForFile> filterForFileList,
													List<FileFilter> fileFilterList,
													FilterForFile chosenFilterForFile )
	{
		FileFilter result = null;

		if( chosenFilterForFile != null )
		{
			if( ( filterForFileList != null ) &&
				( fileFilterList != null ) )
			{
				int index = filterForFileList.indexOf( chosenFilterForFile );
				if( ( index > -1 ) && ( index < fileFilterList.size() ) )
					result = fileFilterList.get(index);
			}
			
			if( result == null )
				result = new FileNameExtensionFilter( chosenFilterForFile.getDescription(),
														chosenFilterForFile.getExtension() );
		}

		return( result );
	}

	protected int translateOpenSaveDialog( int open_save_dialog )
	{
		int result = StaticDesktopDialogsWrapper.OPEN;

		switch( open_save_dialog )
		{
			case OPEN:		result = StaticDesktopDialogsWrapper.OPEN;		break;
			case SAVE:		result = StaticDesktopDialogsWrapper.SAVE;		break;
		}
		
		return( result );
	}

	protected int translateModeForFileChooser( int mode )
	{
		int result = -1;

		switch( mode )
		{
			case FileChooserParameters.FILES_ONLY:			result = JFileChooser.FILES_ONLY;		break;
			case FileChooserParameters.DIRECTORIES_ONLY:		result = JFileChooser.DIRECTORIES_ONLY;		break;
			case FileChooserParameters.FILES_AND_DIRECTORIES:result = JFileChooser.FILES_AND_DIRECTORIES;		break;
		}

		return( result );
	}

	protected int translateMessageType( int messageType )
	{
		int result = JOptionPane.INFORMATION_MESSAGE;

		switch( messageType )
		{
			case ERROR_MESSAGE:				result = JOptionPane.ERROR_MESSAGE;				break;
			case INFORMATION_MESSAGE:		result = JOptionPane.INFORMATION_MESSAGE;		break;
			case PLAIN_MESSAGE:				result = JOptionPane.PLAIN_MESSAGE;				break;
			case QUESTION_MESSAGE:			result = JOptionPane.QUESTION_MESSAGE;			break;
			case WARNING_MESSAGE:			result = JOptionPane.WARNING_MESSAGE;			break;
		}
		
		return( result );
	}

	protected int translateOptionType( int optionType )
	{
		int result = JOptionPane.INFORMATION_MESSAGE;

		switch( optionType )
		{
			case CANCEL_OPTION:				result = JOptionPane.CANCEL_OPTION;				break;
			case CLOSED_OPTION:				result = JOptionPane.CLOSED_OPTION;				break;
			case DEFAULT_OPTION:			result = JOptionPane.DEFAULT_OPTION;			break;
			case OK_CANCEL_OPTION:			result = JOptionPane.OK_CANCEL_OPTION;			break;
			case OK_OPTION:					result = JOptionPane.OK_OPTION;					break;
			case YES_NO_CANCEL_OPTION:		result = JOptionPane.YES_NO_CANCEL_OPTION;		break;
			case YES_NO_OPTION:				result = JOptionPane.YES_NO_OPTION;				break;
			case YES_OPTION:				result = JOptionPane.YES_OPTION;				break;
		}

		return( result );
	}

	protected Object getMessage( Object message )
	{
		return( ( message != null ) ? message : "null" );
	}

	@Override
	public GenericFileFilterChooser getGenericFileFilterChooser() {
		return( _genericFileFilterChooser );
	}
}
