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
import com.frojasg1.general.desktop.generic.dialogs.helper.DesktopDialogsWrapperHelper;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.zoom.filechooser.ZoomJFileChooser;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.zoom.ZoomInterface;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Usuario
 */
public class StaticDesktopDialogsWrapper
{
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticDesktopDialogsWrapper.class);

	public static final int OPEN = 0;
	public static final int SAVE = 1;

	protected static StaticDesktopDialogsWrapper _instance = null;

	public static StaticDesktopDialogsWrapper instance()
	{
		if( _instance == null )
			_instance = new StaticDesktopDialogsWrapper();
		
		return( _instance );
	}

    protected StaticDesktopDialogsWrapper(){}

	static public void M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent_static( double zoomFactor,
																				Component comp,
																				Component parent )
	{
		DesktopDialogsWrapperHelper.instance().M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( zoomFactor, comp, parent );
	}

	static public void showMessageDialog_static( Component parent, Object message, String title,
											int messageType, ZoomInterface conf )
	{
		instance().showMessageDialog( parent, message, title, messageType, conf );
	}

	/**
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 * @param messageType    an example for this parameter: JOptionPane.ERROR_MESSAGE
	 * @param conf 
	 */
	static public void showMessageDialog_static( Component parent, Object message, String title,
											int messageType, double zoomFactor )
	{
		instance().showMessageDialog( parent, message, title, messageType, zoomFactor );
	}

	static public int showOptionDialog_static( Component parentComponent, Object message, String title, int optionType,
										int messageType, Icon icon, Object[] options, Object initialValue,
										ZoomInterface conf )
	{
		return( instance().showOptionDialog( parentComponent, message, title, optionType,
										messageType, icon, options, initialValue,
										conf ) );
	}

	/**
	 * 
	 * @param parentComponent
	 * @param message
	 * @param title
	 * @param optionType		an example for this parameter: JOptionPane.YES_NO_CANCEL_OPTION
	 * @param messageType		an example for this parameter: JOptionPane.QUESTION_MESSAGE
	 * @param icon
	 * @param options
	 * @param initialValue
	 * @param conf
	 * @return 
	 */
	static public int showOptionDialog_static( Component parentComponent, Object message, String title, int optionType,
										int messageType, Icon icon, Object[] options, Object initialValue,
										double zoomFactor )
	{
		return( instance().showOptionDialog( parentComponent, message, title, optionType,
										messageType, icon, options, initialValue,
										zoomFactor ) );
	}

	/**
	 * 
	 * @param parent
	 * @param open_save_dialog	. This parameter has to contain either StaticDesktopDialogsWrapper.OPEN or
								StaticDesktopDialogsWrapper.SAVE
	 * @param conf
	 * @param fileFilterList
	 * @return 
	 */
	static public String showFileChooserDialog_static( Component parent,
													int open_save_dialog,
													ConfigurationForFileChooserInterface conf,
													List<FileFilter> fileFilterList,
													String defaultFileName )
	{
		return( instance().showFileChooserDialog( parent,
													open_save_dialog,
													conf,
													fileFilterList,
													defaultFileName ) );
	}

	/**
	 * 
	 * @param parent
	 * @param open_save_dialog	. This parameter has to contain either StaticDesktopDialogsWrapper.OPEN or
								StaticDesktopDialogsWrapper.SAVE
	 * @param conf
	 * @param fileFilterList
	 * @return 
	 */
	static public String showFileChooserDialog_static( Component parent,
													DesktopFileChooserParameters dfcp,
													ConfigurationForFileChooserInterface conf )
	{
		return( instance().showFileChooserDialog( parent,
													dfcp,
													conf ) );
	}

	public static Point getCenteredLocationForComponent_static( Component comp )
	{
		return( DesktopDialogsWrapperHelper.instance().getCenteredLocationForComponent( comp ) );
	}


	public void showMessageDialog( Component parent, Object message, String title,
											int messageType, ZoomInterface conf )
	{
		showMessageDialog( parent, message, title, messageType, conf.getZoomFactor() );
	}

	/**
	 * 
	 * @param parent
	 * @param message
	 * @param title
	 * @param messageType    an example for this parameter: JOptionPane.ERROR_MESSAGE
	 * @param conf 
	 */
	public void showMessageDialog( Component parent, Object message, String title,
											int messageType, double zoomFactor )
	{
		JOptionPane option = new JOptionPane(	message, messageType );
		M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( zoomFactor, option, parent );
		JDialog dialog = option.createDialog(parent, title );
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}

	public int showOptionDialog( Component parentComponent, Object message, String title, int optionType,
										int messageType, Icon icon, Object[] options, Object initialValue,
										ZoomInterface conf )
	{
		return( showOptionDialog( parentComponent, message, title, optionType, messageType, icon, options, initialValue,
									conf.getZoomFactor() ) );
	}

	/**
	 * 
	 * @param parentComponent
	 * @param message
	 * @param title
	 * @param optionType		an example for this parameter: JOptionPane.YES_NO_CANCEL_OPTION
	 * @param messageType		an example for this parameter: JOptionPane.QUESTION_MESSAGE
	 * @param icon
	 * @param options
	 * @param initialValue
	 * @param conf
	 * @return 
	 */
	public int showOptionDialog( Component parentComponent, Object message, String title, int optionType,
										int messageType, Icon icon, Object[] options, Object initialValue,
										double zoomFactor )
	{
		JOptionPane option = new JOptionPane(	message, messageType );
		option.setIcon(icon);
		option.setOptionType(optionType);
		option.setOptions( options );
		option.setInitialValue( initialValue );
		M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( zoomFactor, option, parentComponent );
		JDialog dialog = option.createDialog(parentComponent, title );
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);

		int result = -1;
		if( options != null )
		{
			for( int ii=0; (result==-1) && ii<options.length; ii++ )
				if( option.getValue() == options[ii] ) result = ii;
		}
		return( result );
	}
	
	/**
	 * 
	 * @param parent
	 * @param open_save_dialog	. This parameter has to contain either StaticDesktopDialogsWrapper.OPEN or
								StaticDesktopDialogsWrapper.SAVE
	 * @param conf
	 * @param fileFilterList
	 * @return 
	 */
	public String showFileChooserDialog( Component parent,
													int open_save_dialog,
													ConfigurationForFileChooserInterface conf,
													List<FileFilter> fileFilterList,
													String defaultFileName )
	{
		String result = null;

		DesktopFileChooserParameters dfcp = new DesktopFileChooserParameters();
		dfcp.setDefaultFileName( defaultFileName );
		dfcp.setListOfFileFilter(fileFilterList);
		dfcp.setOpenOrSaveDialog( open_save_dialog );
		setDefaultLocale( dfcp );

		result = showFileChooserDialog( parent, dfcp, conf );

		return( result );
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( GenericFunctions.instance().getAppliConf() );
	}

	protected void setDefaultLocale( DesktopFileChooserParameters dfcp ) {
		DesktopDialogsWrapperHelper.instance().setDefaultLocale(dfcp);
	}

	/**
	 * 
	 * @param parent
	 * @param open_save_dialog	. This parameter has to contain either StaticDesktopDialogsWrapper.OPEN or
								StaticDesktopDialogsWrapper.SAVE
	 * @param conf
	 * @param fileFilterList
	 * @return 
	 */
	public String showFileChooserDialog( Component parent,
													DesktopFileChooserParameters dfcp,
													ConfigurationForFileChooserInterface conf )
	{
		String result = null;

		int open_save_dialog = dfcp.getOpenOrSaveDialog();
		List<FileFilter> fileFilterList = dfcp.getListOfFileFilter();
		String defaultFileName = dfcp.getDefaultFileName();
		int mode = dfcp.getMode();
		FileFilter chosenFileFilter = dfcp.getChosenFileFilter();

		String path = "";

		if( ! StringFunctions.instance().isEmpty( defaultFileName ) )
			path = ( new File( defaultFileName ) ).getParentFile().getAbsolutePath();

		if( StringFunctions.instance().isEmpty( path ) && ( conf != null ) )
			path = conf.getLastDirectory();

		ZoomJFileChooser chooser=new ZoomJFileChooser(parent, path, conf,
														dfcp.getLocale(),
														isDarkMode() );

		Rectangle new100percentBounds = null;
		if( ( conf != null ) && ( conf.getLastFileChooserBounds() != null ) )
		{
			new100percentBounds = ViewFunctions.instance().calculateNewBoundsOnScreen( conf.getLastFileChooserBounds(), null, null, conf.getZoomFactor() );
		}
		chooser.setDialogType( open_save_dialog );
		chooser.init( parent, new100percentBounds );

		if( mode > 0 )
			chooser.setFileSelectionMode( mode );

		if( fileFilterList != null )
		{
			Iterator<FileFilter> it = fileFilterList.iterator();
			while( it.hasNext() )
			{
				FileFilter ff = it.next();
				chooser.addChoosableFileFilter(ff);
			}
		}

		if( chosenFileFilter != null )
			chooser.setFileFilter(chosenFileFilter);

		if( dfcp.getGenericFileFilter() != null )
			chooser.addChoosableFileFilter( dfcp.getGenericFileFilter() );

		Set<Component> invertedComponentColors = M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( conf.getZoomFactor(),
			chooser.getRootPane().getContentPane(), parent, this::notMenu );
//		Set<Component> invertedComponentColors = M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( conf.getZoomFactor(),
//			chooser, parent, this::notMenu );

		chooser.setAlreadyInvertedComponentColors( invertedComponentColors );

//		chooser.updatePopupMenu();
/*
		if( ( conf != null ) && ( conf.getLastFileChooserBounds() != null ) )
		{
			Rectangle newBounds = ViewFunctions.instance().calculateNewBoundsOnScreen( conf.getLastFileChooserBounds(), null, null, conf.getZoomFactor() );
//			chooser.setDialogBounds( newBounds );
		}
*/
//		System.out.println( ViewFunctions.instance().traceComponentTreeSizes( chooser ) );
//		ComponentFunctions.instance().inspectHierarchy(chooser);

		int returnVal = -1;

//		chooser.init( parent, new100percentBounds );

		if( defaultFileName != null )
			chooser.setSelectedFile( new File( defaultFileName ) );

		if( open_save_dialog == OPEN )		returnVal = chooser.showOpenDialog(parent);
		else if( open_save_dialog == SAVE )	returnVal = chooser.showSaveDialog(parent);

		chooser.addChoosableFileFilter( null );
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			path=chooser.getSelectedFile().getParentFile().getAbsolutePath();
			result = chooser.getSelectedFile().toString();

			if( conf != null ) conf.setLastDirectory(path );
		}

		if( conf != null )
		{
			Rectangle newBounds = ViewFunctions.instance().calculateNewBoundsOnScreen( chooser.getDialogBounds(), null, null, 1/conf.getZoomFactor() );
			conf.setLastFileChooserBounds( newBounds );
		}

		return( result );
	}

	protected boolean isDarkMode()
	{
		return( DesktopDialogsWrapperHelper.instance().isDarkMode() );
	}

	protected boolean notMenu( Component comp )
	{
		boolean result = false;
		if( comp != null )
			result = ( comp.getClass().getName().indexOf( "Menu" ) == -1 );

		return( result );
	}

	public Set<Component> M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( double zoomFactor,
																								Component comp,
																								Component parent)
	{
		return( DesktopDialogsWrapperHelper.instance().M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( zoomFactor, comp,
																						parent ) );
	}

	public Set<Component> M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( double zoomFactor,
																								Component comp,
																								Component parent,
																								Predicate<Component> compFilter)
	{
		return( DesktopDialogsWrapperHelper.instance().M_changeFontToApplicationFontSizeAndApplyColorInversion_forComponent( zoomFactor, comp,
																						parent, compFilter ) );
	}
}
