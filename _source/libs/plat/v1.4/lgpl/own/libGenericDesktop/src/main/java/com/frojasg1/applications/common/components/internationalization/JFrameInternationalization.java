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
package com.frojasg1.applications.common.components.internationalization;

import com.frojasg1.applications.common.components.data.MapOfComponents;
import com.frojasg1.applications.common.components.data.ComponentData;
import com.frojasg1.applications.common.configuration.imp.FormLanguageConfiguration;
import com.frojasg1.applications.common.configuration.imp.FormGeneralConfiguration;
import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem_JSplitPane;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem_parent;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.copypastepopup.ConfForTextPopupMenu;
import com.frojasg1.general.desktop.copypastepopup.TextCompPopupManager;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.desktop.generic.dialogs.impl.StaticDesktopDialogsWrapper;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.screen.ScreenFunctions;
import com.frojasg1.general.desktop.threads.Generic_getResultEDT;
import com.frojasg1.general.desktop.threads.GetResultEDT;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.number.IntegerReference;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.general.zoom.ZoomParam;
import com.frojasg1.applications.common.components.data.InfoForResizingPanels;
import com.frojasg1.applications.common.components.hints.HintConfiguration;
import com.frojasg1.applications.common.components.hints.HintForComponent;
import com.frojasg1.applications.common.components.internationalization.radiobuttonmenu.ChangeRadioButtonMenuItemListResult;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.MouseListenerForRepaint;
import com.frojasg1.applications.common.components.zoom.SwitchToZoomComponents;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.labels.UrlJLabel;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TextComponent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.text.JTextComponent;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.GenericDesktopConstants;
import com.frojasg1.general.desktop.edt.EventDispatchThreadInvokeLaterPurge;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.threads.ThreadFunctions;
import java.util.HashMap;
import java.util.function.Consumer;
import javax.swing.JScrollPane;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableStatus;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Fran
 */
public class JFrameInternationalization implements ComponentListener, WindowStateListener,
													FocusListener, ResizeRelocateItem_parent,
													InternationalizedStringConf
{
	public static final String GLOBAL_CONF_FILE_NAME = "JFrameInternationalization.properties";

	public static final String CONF_IS = "IS";
	public static final String CONF_EXCEPTION_SAVING_FORM_CONFIGURATION = "EXCEPTION_SAVING_FORM_CONFIGURATION";
	public static final String CONF_CLASS_OF_COMPONENT_NOT_EXPECTED = "CLASS_OF_COMPONENT_NOT_EXPECTED";


//	protected float a_lastFactor = 1.0F;

	protected BaseApplicationConfigurationInterface _baseConf = null;

	protected FormLanguageConfiguration a_formLanguageConfiguration = null;
	protected FormGeneralConfiguration a_formGeneralConfiguration = null;

	protected Properties a_languageProperties = null;		// except on the constructor, it is the same as a_formLanguageConfiguration
	protected Properties a_generalProperties = null;		// except on the constructor, it is the same as a_formGeneralConfiguration

	protected static String	sa_dirSeparator = System.getProperty( "file.separator" );
	protected static String	sa_lineSeparator = System.getProperty( "line.separator" );

	protected static final String TXT_TEXT = "TEXT";
	protected static final String TXT_ELEMENT_AT = "ELEMENT_AT";
	protected static final String TXT_LABEL = "LABEL";
	protected static final String TXT_ITEM = "ITEM";
	protected static final String TXT_TITLE = "TITLE";
	protected static final String TXT_BORDER = "BORDER";
	protected static final String TXT_DIVIDER_LOCATION = "DIVIDER_LOCATION";
	protected static final String TXT_HINT = "HINT";
	protected static final String TXT_URL = "URL"; // for UrlJLabel

	protected static final RunResizeRelocateItemProcedure CHANGE_ZOOM_PROCEDURE = (rri,zp)->{	rri.newExpectedZoomParam(zp ); };
//	protected static final RunResizeRelocateItemProcedure RESIZE_OR_RELOCATE_PROCEDURE = (rri,zp)->{	SwingUtilities.invokeLater( () -> rri.execute(zp) ); };
	protected static final RunResizeRelocateItemProcedure RESIZE_OR_RELOCATE_PROCEDURE = (rri,zp)-> rri.execute(zp);
	protected static final RunResizeRelocateItemProcedure PICK_PREVIOUS_DATA_PROCEDURE = (rri,zp)->{	rri.pickPreviousData(zp); };
	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR );

	protected Vector<JPopupMenu> a_vectorJpopupMenus = null;
	protected Component a_parentFrame = null;
	protected Component a_parentParentFrame = null;
	protected String a_configurationBaseFileName = null;

	protected MapOfComponents _mapOfComponents = null;

//	protected Map< Component, Boolean > _map_resize_parents = null;
//	protected Map< Component, ResizeRelocateItem >	a_mapResizeRelocateComponents = null;
//	protected Map< Component, InfoForResizingPanels > a_mapResizingPanels = null;

	protected Map< Component, HintForComponent > _mapHints = new HashMap<>();

	protected boolean a_hasComponentListenerBeenSet = false;

	protected ComponentListener a_componentListener_this = null;
	protected WindowStateListener a_windowStateListener_this = null;

	protected boolean _limitToMaxWindowWidth = false;
	protected boolean _limitToMaxWindowHeight = false;

	protected boolean _enableUndoRedoForTextComponents = false;
	protected boolean _enableTextPopupMenu = false;

	protected ReentrantLock _lockWaitingForFrameSizeInformation = new ReentrantLock();
	protected Condition _frameSizeInformationCaught = _lockWaitingForFrameSizeInformation.newCondition();

	protected boolean _internationalizeFont = false;

	/*
		The following rectangle, has:
			X -> left border
			Y -> top border
			Width -> right border
			Height -> bottom border
	*/
	protected Insets _frameBorder = null;
	protected Dimension _differenceOfFrameResize = null;

	protected boolean _hasCaughtFrameSizeInformation = false;

	protected boolean _isInitialized = false;

//	protected ZoomParam _previousZoomParam = new ZoomParam( 1.0D );
	protected double _previousZoomFactor = 1.0D;
	protected ZoomParam _newZoomParam = new ZoomParam( 1.0D );

	protected boolean _adjustMinSizeOFFrameToContents = false;

	protected HintConfiguration _hintConfiguration = null;
//	protected HintConfiguration _hintConfiguration = null;

	protected MouseListenerForRepaint _mouseListenerForRepaint = null;

	protected boolean _isResizeRelocateItemsResizeListenersBlocked = false;
	protected boolean _isMainMouseButtonClicked = false;

	protected Consumer<InternationalizationInitializationEndCallback> _initializationEndCallBack = null;

	protected int _delayToInvokeCallback = 300;

	protected boolean _isOnTheFly = false;

//	protected EventDispatchThreadInvokeLaterPurge _invokeLaterPurge = new EventDispatchThreadInvokeLaterPurge( 10 );

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public JFrameInternationalization( BaseApplicationConfigurationInterface baseConf )
	{
		_baseConf = baseConf;

		_mapOfComponents = new MapOfComponents( this );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString_own(CONF_IS, "is" );
		registerInternationalString_own(CONF_EXCEPTION_SAVING_FORM_CONFIGURATION, "EXCEPTION: Saving form configuration" );
		registerInternationalString_own(CONF_CLASS_OF_COMPONENT_NOT_EXPECTED, "Class of component not expected" );
	}

	public void initialize(	String mainFolder,
							String applicationName,
							String group,
							String paquetePropertiesIdiomas,
							String configurationBaseFileName,
							Component parentFrame,
							Component parentParentFrame,
							Vector<JPopupMenu> vPUMenus,
							boolean hasToPutWindowPosition,
							MapResizeRelocateComponentItem map,
							boolean adjustSizeOfFrameToContents,
							boolean adjustMinSizeOFFrameToContents,
							double zoomFactor,
							boolean enableUndoRedoForTextComponents,
							boolean enableTextPopupMenu,
							boolean switchToZoomComponents,
							boolean internationalizeFont
						)
	{
		if( ( parentFrame instanceof JInternalFrame ) &&
			SwingUtilities.isEventDispatchThread() )
		{
			new Thread( new Runnable() {
				@Override
				public void run()
				{
					initialize_internal(	mainFolder,
											applicationName,
											group,
											paquetePropertiesIdiomas,
											configurationBaseFileName,
											parentFrame,
											parentParentFrame,
											vPUMenus,
											hasToPutWindowPosition,
											map,
											adjustSizeOfFrameToContents,
											adjustMinSizeOFFrameToContents,
											zoomFactor,
											enableUndoRedoForTextComponents,
											enableTextPopupMenu,
											switchToZoomComponents,
											internationalizeFont
										);

					delayedInvokeCallback();
				}
			}).start();
		}
		else
		{
			initialize_internal(	mainFolder,
									applicationName,
									group,
									paquetePropertiesIdiomas,
									configurationBaseFileName,
									parentFrame,
									parentParentFrame,
									vPUMenus,
									hasToPutWindowPosition,
									map,
									adjustSizeOfFrameToContents,
									adjustMinSizeOFFrameToContents,
									zoomFactor,
									enableUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont
								);

			delayedInvokeCallback();
		}
	}
/*
	public void initialize(	String mainFolder,
							String applicationName,
							String group,
							String paquetePropertiesIdiomas,
							String configurationBaseFileName,
							Component parentFrame,
							Component parentParentFrame,
							Vector<JPopupMenu> vPUMenus,
							boolean hasToPutWindowPosition,
							MapResizeRelocateComponentItem map,
							boolean adjustSizeOfFrameToContents,
							boolean adjustMinSizeOFFrameToContents,
							double zoomFactor,
							boolean enableUndoRedoForTextComponents,
							boolean enableTextPopupMenu,
							boolean switchToZoomComponents,
							boolean internationalizeFont
						)
	{
		if( ( parentFrame instanceof JInternalFrame ) &&
			SwingUtilities.isEventDispatchThread() )
		{
			new Thread( new Runnable() {
				@Override
				public void run()
				{
					initialize_internal(	mainFolder,
											applicationName,
											group,
											paquetePropertiesIdiomas,
											configurationBaseFileName,
											parentFrame,
											parentParentFrame,
											vPUMenus,
											hasToPutWindowPosition,
											map,
											adjustSizeOfFrameToContents,
											adjustMinSizeOFFrameToContents,
											zoomFactor,
											enableUndoRedoForTextComponents,
											enableTextPopupMenu,
											switchToZoomComponents,
											internationalizeFont
										);

					delayedInvokeCallback();
				}
			}).start();
		}
		else
		{
			initialize_internal(	mainFolder,
									applicationName,
									group,
									paquetePropertiesIdiomas,
									configurationBaseFileName,
									parentFrame,
									parentParentFrame,
									vPUMenus,
									hasToPutWindowPosition,
									map,
									adjustSizeOfFrameToContents,
									adjustMinSizeOFFrameToContents,
									zoomFactor,
									enableUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont
								);

			delayedInvokeCallback();
		}
	}
*/


	public void initialize_internal(	String mainFolder,
										String applicationName,
										String group,
										String paquetePropertiesIdiomas,
										String configurationBaseFileName,
										Component parentFrame,
										Component parentParentFrame,
										Vector<JPopupMenu> vPUMenus,
										boolean hasToPutWindowPosition,
										MapResizeRelocateComponentItem map,
										boolean adjustSizeOfFrameToContents,
										boolean adjustMinSizeOFFrameToContents,
										double zoomFactor,
										boolean enableUndoRedoForTextComponents,
										boolean enableTextPopupMenu,
										boolean switchToZoomComponents,
										boolean internationalizeFont
									)
	{

		ZoomParam firstZoom = createZoomParam( 1.0D );

		a_vectorJpopupMenus = vPUMenus;
		a_parentFrame = parentFrame;
		a_parentParentFrame = parentParentFrame;

		_internationalizeFont = internationalizeFont;

		_frameBorder = catchFrameBorders();

		internationalizeFont( _baseConf.getLanguage() );

		if( switchToZoomComponents )
			switchToZoomComponents( a_parentFrame, map );

		if( ( (a_parentFrame instanceof JFrame ) ||
				(a_parentFrame instanceof JDialog ) ||
				( a_parentFrame instanceof JInternalFrame )) &&
			adjustSizeOfFrameToContents
			)
		{
			_adjustMinSizeOFFrameToContents = adjustMinSizeOFFrameToContents;
			resizeFrameToContents();
		}

//		_mapOfComponents = new MapOfComponents( this );

		a_configurationBaseFileName = configurationBaseFileName;
		String languageConfigurationFileName = configurationBaseFileName + "_LAN.properties";
		String generalConfigurationFileName = configurationBaseFileName + "_GEN.properties";

		a_languageProperties = new Properties();
		a_generalProperties = new Properties();

		_enableUndoRedoForTextComponents = enableUndoRedoForTextComponents;
		_enableTextPopupMenu = enableTextPopupMenu;

		if( enableUndoRedoForTextComponents || enableTextPopupMenu )
		{
			if( ConfForTextPopupMenu.instance() == null )
				ConfForTextPopupMenu.create( _baseConf );

			createTextPopupManagers(a_parentFrame);

			if( switchToZoomComponents )
			{
				switchToZoomComponents( getUndoRedoPopups(), map );
				switchToZoomComponents( vPUMenus, map );
			}
		}

		if( getInternationalizedWindow() != null )
			getInternationalizedWindow().invokeConfigurationParameterColorThemeChanged();

		try
		{
			convertAttributesIntoProperties( a_parentFrame, 0 );
		}
		catch (InternException ex)
		{
			ex.printStackTrace();
		}

		a_formLanguageConfiguration = new FormLanguageConfiguration(
								mainFolder,
								applicationName, group,
								languageConfigurationFileName,
								paquetePropertiesIdiomas,
								a_languageProperties);
		a_formGeneralConfiguration = new FormGeneralConfiguration(
								mainFolder,
								applicationName, group,
								generalConfigurationFileName,
								a_generalProperties);

		if( map != null )
		{
			map.setParent(this);
			pickPreviousDataOrResizeOrChangeZoomFactor(RESIZE_OR_RELOCATE_PROCEDURE, a_parentFrame, firstZoom, true );	// we get the information of the root panels for maximize event.
			addMapResizeRelocateComponents( map );
//			a_mapResizeRelocateComponents = map;
		}

		try
		{
			a_formGeneralConfiguration.M_openConfiguration();
//			if( hasToPutWindowPosition && !a_formGeneralConfiguration.M_isFirstTime() ) putWindowPosition();
			putWindowPosition( 1.0D, hasToPutWindowPosition );
			convertPropertiesIntoAttributes(a_parentFrame, firstZoom, 0 );
//			pickPreviousDataOrResizeOrChangeZoomFactor();	// we comment this line, because we have already done it in convertPropertiesIntoAttributes.
											// it was needed to resize all components there, because we needed it to set properly the JSplitPanes initial state
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		a_languageProperties = a_formLanguageConfiguration;
		a_generalProperties = a_formGeneralConfiguration;
		
//		a_lastFactor = 1.0F;

		if( zoomFactor != 1.0D )
			SwingUtilities.invokeLater( () -> { changeZoomFactor( zoomFactor, null ); });

//		setListeners( a_mapResizeRelocateComponents );
		setListeners( _mapOfComponents );
		_mouseListenerForRepaint = new MouseListenerForRepaint( a_parentFrame );

		_isInitialized = true;
	}

	protected void switchToZoomComponents( Collection<JPopupMenu> col,
											MapResizeRelocateComponentItem map )
	{
		if( col != null )
			for( JPopupMenu elem: col )
				switchToZoomComponents( elem, map );
	}

	public 	Vector<JPopupMenu> getVectorOfPopupMenus()
	{
		return( a_vectorJpopupMenus );
	}

	protected List<JPopupMenu> getUndoRedoPopups()
	{
		List<JPopupMenu> result = new ArrayList<>();
		ComponentFunctions.instance().browseComponentHierarchy( a_parentFrame, (comp, res) -> {
			JPopupMenu menu = NullFunctions.instance().getIfNotNull(
				getMapOfComponents().getOrCreateOnTheFly(comp).getTextCompPopupManager(),
				TextCompPopupManager::getJPopupMenu );
			if( menu != null )
				result.add(menu);
		});

		return( result );
	}

	public InternationalizedWindow getInternationalizedWindow()
	{
		InternationalizedWindow result = null;
		if( a_parentFrame instanceof InternationalizedWindow)
			result = (InternationalizedWindow) a_parentFrame;

		return( result );
	}

	public void doInternationalizationTasksOnTheFly( Component comp )
	{
		boolean isOnTheFly = true;
		createTextPopupManagers( comp, isOnTheFly );
		int level = 1;
		boolean onlyText = true;
		_isOnTheFly = true;
		ExecutionFunctions.instance().safeMethodExecution( () -> convertPropertiesIntoAttributes( comp, _newZoomParam, level, onlyText ) );
		_isOnTheFly = false;
	}

	public ResizeRelocateItem createDefaultResizeRelocateItem( Component comp )
	{
		return( ExecutionFunctions.instance().safeFunctionExecution( () -> _mapOfComponents.createDefaultResizeRelocateItem( comp ) ) );
	}

	public ResizeRelocateItem createAndStoreResizeRelocateItemIntoMap( Component comp, MapResizeRelocateComponentItem mapRrci )
	{
		ResizeRelocateItem result = null;
		if( mapRrci != null )
		{
			if( mapRrci.get( comp ) == null )
			{
				result = createDefaultResizeRelocateItem( comp );
				mapRrci.put( comp, result );
			}
		}

		return( result );
	}

	public void setInitializationEndCallBack( Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		_initializationEndCallBack = initializationEndCallBack;
	}

	public void setDelayToInvokeCallback( int value )
	{
		_delayToInvokeCallback = value;
	}

	protected void delayedInvokeCallback()
	{
		ThreadFunctions.instance().delayedSafeInvoke( () -> SwingUtilities.invokeLater( () -> invokeCallback() ), _delayToInvokeCallback );
	}

	protected void invokeCallback()
	{
//		prepareResizeOrRelocateToZoom(); // just in case any JTextPane free resizable was not updated its _previousZoomFactorWhenPickingData

		InternationalizationInitializationEndCallback iiec = null;
		if( a_parentFrame instanceof InternationalizationInitializationEndCallback )
		{
			iiec = (InternationalizationInitializationEndCallback) a_parentFrame;
			iiec.internationalizationInitializationEndCallback();
		}

		if( a_parentFrame instanceof InternationalizedWindow )
		{
			InternationalizedWindow iw = (InternationalizedWindow) a_parentFrame;
			InternationalizationInitializationEndCallback iiecFinal = iiec;
			ThreadFunctions.instance().delayedSafeInvokeEventDispatchThread( () -> {

				iw.setInitialized();
				
				if( ( _initializationEndCallBack != null ) && ( iiecFinal != null ) )
				{
					_initializationEndCallBack.accept(iiecFinal);
					iiecFinal.setAlreadyInitializedAfterCallback();
				}
			}, 500 );
		}
	}

	public void releaseResources()
	{
		if( _mouseListenerForRepaint != null )
		{
			try
			{
				_mouseListenerForRepaint.releaseResources();
				_mouseListenerForRepaint = null;
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	protected void internationalizeFont( String language )
	{
		if( _internationalizeFont )
		{
			FontFunctions.instance().internationalizeFont( a_parentFrame, language );

			if( a_vectorJpopupMenus != null )
			{
				Iterator< JPopupMenu > it = a_vectorJpopupMenus.iterator();
				while( it.hasNext() )
				{
					FontFunctions.instance().internationalizeFont( it.next(), language );
				}
			}
		}
	}

	protected ZoomParam createZoomParam( double zoomFactor )
	{
		return( new ZoomParam( zoomFactor ) );
	}

	public boolean isInitialized()
	{
		return( _isInitialized );
	}

	public Insets getFrameBorder()
	{
		return( _frameBorder );
	}

	public Component switchToZoomComponents( Component comp,
											MapResizeRelocateComponentItem map)
	{
		return( switchToZoomComponentsGen( comp, map, false ) );
	}

	public Component switchToZoomComponentsGen( Component comp,
											MapResizeRelocateComponentItem map,
											boolean isOnTheFly )
	{
		SwitchToZoomComponents stzc = new SwitchToZoomComponents( _baseConf, map );

		Component result = stzc.switchToZoomComponents(comp);

		Map<Component, Component> switchComponentsMap = stzc.getSwitchedComponents().getMap();
		if( map != null )
			map.switchComponents( switchComponentsMap );
		_mapOfComponents.switchComponentsGen( switchComponentsMap, isOnTheFly );

		return( result );
	}

	public void updateComponents( ChangeRadioButtonMenuItemListResult changedElements )
	{
		_mapOfComponents.switchComponents( changedElements.getMapOfOldElements() );
		_mapOfComponents.createComponents( changedElements.getColOfNewElements() );

		boolean onlyGetInfo = false;
		prepareResizeOrRelocateToZoom(	changedElements.getPopupMenu(),
									_newZoomParam,
									onlyGetInfo);
	}

	public void resizeFrameToContents()
	{
		resizeFrameToContents( _adjustMinSizeOFFrameToContents );
	}

	public void resizeFrameToContents(boolean setMinSize)
	{
		Generic_getResultEDT gen = new Generic_getResultEDT( new AdjustFrameSize(setMinSize) );
		gen.getResultEDT();
	}

	protected void resizeFrameToContentsEDT(boolean setMinSize)
	{
		Dimension newPreferredSize = getFramePreferredSizeAdjustedToContents();
		Dimension newSize = getFrameSizeAdjustedToContents();

//		_differenceOfFrameResize = new Dimension( 0, 0 );

		int preferredWidth = IntegerFunctions.max( (int) a_parentFrame.getPreferredSize().getWidth(),
													(int) a_parentFrame.getMinimumSize().getWidth());
		int preferredHeight = IntegerFunctions.max( (int) a_parentFrame.getPreferredSize().getHeight(),
													(int) a_parentFrame.getMinimumSize().getHeight());

		_differenceOfFrameResize = new Dimension( (int) ( newPreferredSize.getWidth() - preferredWidth ),
													(int) (newPreferredSize.getHeight() - preferredHeight ));

//		if( !a_parentFrame.isMinimumSizeSet() && setMinSize )
		if( setMinSize )
			a_parentFrame.setMinimumSize(newSize);

		a_parentFrame.setPreferredSize(newPreferredSize);
		a_parentFrame.setSize(newSize);

		autoResizeFrameDesdendantPanels( a_parentFrame, _differenceOfFrameResize );
	}

	protected void doTasksPriorToFrameResizing( double relativeFactor )// double zoomFactor )
	{
//		ZoomParam zp = createZoomParam( zoomFactor );
		ZoomParam zp = _newZoomParam;
		double zoomFactor = zp.getZoomFactor();
		if( a_parentFrame instanceof JFrame )
			pickPreviousDataOrResizeOrChangeZoomFactor(RESIZE_OR_RELOCATE_PROCEDURE,
														( (JFrame) a_parentFrame ).getJMenuBar(),
														zp, false );

		if( a_vectorJpopupMenus != null )
		{
			Iterator< JPopupMenu > it = a_vectorJpopupMenus.iterator();
			while( it.hasNext() )
				pickPreviousDataOrResizeOrChangeZoomFactor(RESIZE_OR_RELOCATE_PROCEDURE,
															it.next(),
															zp, false );
		}

		ResizeRelocateItem rri = _mapOfComponents.getResizeRelocateItem( a_parentFrame );
		if( rri == null )
			throw( new RuntimeException( String.format( "%s %s %s",
						"ResizeRelocateItem( a_parentFrame )",
						getInternationalString_own( CONF_IS ),
						"null"
														)
										)
				);

		zoomMinimumSizeIf( ( relativeFactor < 1.0D ), relativeFactor );

		Dimension previousMaximumSize = a_parentFrame.getMaximumSize();
		Dimension newMaximumSize = ViewFunctions.instance().getNewDimension(previousMaximumSize, _frameBorder, zoomFactor/_previousZoomFactor );//_previousZoomParam.getZoomFactor() );
		a_parentFrame.setMaximumSize( newMaximumSize );

//		Dimension newMinimumSize = ViewFunctions.instance().getNewDimension( rri.getComponentOriginalDimensions().getOriginalMinimumSize(), _frameBorder, zoomFactor );
//		Dimension newMaximumSize = ViewFunctions.instance().getNewDimension( rri.getComponentOriginalDimensions().getOriginalMaximumSize(), _frameBorder, zoomFactor );
	}

	protected void doTasksAfterToFrameResizing( double relativeFactor )// double zoomFactor )
	{
		zoomMinimumSizeIf( ( relativeFactor > 1.0D ), relativeFactor );
	}

	protected void zoomMinimumSizeIf( boolean hasToZoom, double relativeFactor )
	{
		if( a_parentFrame.isMinimumSizeSet() && hasToZoom )
		{
			Dimension previousMinimumSize = a_parentFrame.getMinimumSize();
			Dimension newMinimumSize = ViewFunctions.instance().getNewDimension(previousMinimumSize, _frameBorder, relativeFactor );//zoomFactor/_previousZoomFactor );//_previousZoomParam.getZoomFactor() );
			a_parentFrame.setMinimumSize( newMinimumSize );
		}
	}

	public void changeZoomFactorCenteredForFrame( double zoomFactor, Point center )
	{
//		_newZoomParam = createZoomParam( zoomFactor );
		_newZoomParam.setZoomFactor( zoomFactor );
		double originalFactor = _previousZoomFactor;//_previousZoomParam.getZoomFactor();
//		double relativeFactor = originalFactor * zoomFactor;
		double relativeFactor = zoomFactor/originalFactor;

		Rectangle previousBounds = a_parentFrame.getBounds();
		if( center == null )
		{
			if( a_parentFrame instanceof JInternalFrame )
				center = new Point( previousBounds.x, previousBounds.y );
			else
				center = ViewFunctions.instance().getCenter( previousBounds );
		}

		if( a_parentFrame instanceof JInternalFrame )
		{
//			a_parentFrame.setBounds( newBounds );

//			_previousZoomParam = createZoomParam( zoomFactor );
			_previousZoomFactor = zoomFactor;

			prepareResizeOrRelocateToZoom( );

			SwingUtilities.invokeLater(() -> {
				a_parentFrame.setSize(ViewFunctions.instance().getNewDimension(a_parentFrame.getSize(), relativeFactor) );
				a_parentFrame.repaint();
			});
		}
		else
		{
			prepareResizeOrRelocateToZoom( );
			doTasksPriorToFrameResizing(relativeFactor);// zoomFactor );

			Rectangle newBounds = ViewFunctions.instance().calculateNewBoundsOnScreen(previousBounds, _frameBorder, center, relativeFactor );
			a_parentFrame.setPreferredSize( new Dimension( (int) newBounds.getWidth(),
														(int) newBounds.getHeight() ) );

			Rectangle boundsOfMaxWindow = ScreenFunctions.getBoundsOfMaxWindow();

			Insets insets = _frameBorder; //ViewFunctions.instance().getBorders(a_parentFrame);
			int maximumYYBound = boundsOfMaxWindow.y + boundsOfMaxWindow.height -
								( insets != null ? insets.top : 0 );
			int minimumYYBound = boundsOfMaxWindow.y;

			int maximumXXBound = boundsOfMaxWindow.width - IntegerFunctions.min( 70, newBounds.width );
			int minimumXXBound = boundsOfMaxWindow.x - IntegerFunctions.max( newBounds.width - 150, 0 );

			int yyCoordinate = IntegerFunctions.limit( newBounds.y, minimumYYBound, maximumYYBound );
			int xxCordinate = IntegerFunctions.limit( newBounds.x, minimumXXBound, maximumXXBound );

			try
			{
//				blockResizeRelocateItemsResizeListeners();
//				a_parentFrame.setBounds( xxCordinate, yyCoordinate, newBounds.width, newBounds.height );
				a_parentFrame.setBounds( newBounds.x, newBounds.y, newBounds.width, newBounds.height );
				a_parentFrame.repaint();

				// as notify is deactivated, we must do it by hand.
				executeResizeRelocateItemRecursive(a_parentFrame);

				doTasksAfterToFrameResizing(relativeFactor);// zoomFactor );
			}
			finally
			{
//				delayedInvocationToUnblockResizeRelocateItemsResizeListeners();
			}

//			_previousZoomParam = createZoomParam( zoomFactor );
			_previousZoomFactor = zoomFactor;
		}

		recreateAllUIs();
	}

	protected void delayedInvocationToUnblockResizeRelocateItemsResizeListeners()
	{
		Thread th = new Thread() {
			@Override
			public void run()
			{
				try
				{
					Thread.sleep( 20 );
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}

				SwingUtilities.invokeLater( () -> unblockResizeRelocateItemsResizeListeners() );
			}
		};

		th.start();
	}

	protected void blockResizeRelocateItemsResizeListeners()
	{
		_isResizeRelocateItemsResizeListenersBlocked = true;
	}

	protected void unblockResizeRelocateItemsResizeListeners()
	{
		_isResizeRelocateItemsResizeListenersBlocked = true;
	}

	protected void recreateAllUIs()
	{
//		RecreateUIs ruis = new RecreateUIs( a_parentFrame );
		
//		ruis.execute();
	}
	
	public void changeZoomFactor( double zoomFactor, Point center )
	{
		changeZoomFactorCenteredForFrame( zoomFactor, center );

//		pickPreviousDataOrResizeOrChangeZoomFactor( zoomFactor );
	}

	protected void processComponentForDimensionOfContentsOfFrame( Component comp, IntegerReference width, IntegerReference height )
	{
		Rectangle rect = comp.getBounds();
		int tmpWidth = (int) ( rect.getX() + rect.getWidth() );
		int tmpHeight = (int) ( rect.getY() + rect.getHeight() );

		width._value = IntegerFunctions.max( width._value, tmpWidth );
		height._value = IntegerFunctions.max( height._value, tmpHeight );
	}
	
	protected Dimension getDimensionOfContentsOfFrame()
	{
		Dimension result = a_parentFrame.getPreferredSize();

		IntegerReference width = new IntegerReference( 0 );
		IntegerReference height = new IntegerReference( 0 );

		if( ( a_parentFrame instanceof JFrame ) ||
			( a_parentFrame instanceof JDialog ) ||
			( a_parentFrame instanceof JInternalFrame ) )
		{
			try
			{
				// Frame -> RootPane -> LayeredPane -> ContentPane
//				Container contentPane = (Container) ( (Container) ( (Container) ( (Container) a_parentFrame ).getComponent(0) ).getComponent(1) ).getComponent(0);
				Container contentPane = ViewFunctions.instance().getContentPane( a_parentFrame );

				if( contentPane != null )
				{
/*
					JMenuBar menuBar = null;

					if( a_parentFrame instanceof JFrame )
					{
						JFrame frame = (JFrame) a_parentFrame;

						menuBar = frame.getJMenuBar();
						if( menuBar != null )
							processComponentForDimensionOfContentsOfFrame( menuBar, width, height );
					}
*/
					for( int ii=0; ii<contentPane.getComponentCount(); ii++ )
					{
						Component child = contentPane.getComponent(ii);
						processComponentForDimensionOfContentsOfFrame( child, width, height );
					}
/*
				if( menuBar != null )
					height._value = height._value +  menuBar.getHeight();
*/
					height._value = height._value + contentPane.getY();
					result = new Dimension( width._value, height._value );
				}
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		return( result );
	}

	protected Dimension getFrameSizeAdjustedToContents()
	{
		return( adjustFrameSizeToContents( a_parentFrame.getSize() ) );
	}

	protected Dimension getFramePreferredSizeAdjustedToContents()
	{
		return( adjustFrameSizeToContents( a_parentFrame.getPreferredSize() ) );
	}

	protected Dimension adjustFrameSizeToContents( Dimension size )
	{
		Dimension result = size;

		if( ( result != null ) && ( _frameBorder != null ) )
		{
			Dimension dimensionOfContents = getDimensionOfContentsOfFrame();

			result = new Dimension( (int) ( dimensionOfContents.getWidth() + _frameBorder.left + _frameBorder.right ),
									(int) ( dimensionOfContents.getHeight() + _frameBorder.top + _frameBorder.bottom ) );
		}

		return( result );
	}

	protected Insets getFrameBorders_internal()
	{
		Insets result = ViewFunctions.instance().getBorders(a_parentFrame);

		return( result );
	}

	protected Insets catchFrameBorders( )
	{
		boolean calculateBorders = ! ViewFunctions.instance().haveBeenBordersCalculated( a_parentFrame );
		if( calculateBorders && ( a_parentFrame instanceof JInternalFrame ) )
		{
			a_parentFrame.setSize( a_parentFrame.getPreferredSize() );
		}

		Generic_getResultEDT<Insets> gen = new Generic_getResultEDT( new GetFrameBorders() );
		Insets result = gen.getResultEDT();

		return( result );
	}

	public TextUndoRedoInterface getTextUndoRedoManager( Component comp )
	{
		return( _mapOfComponents.getTextUndoRedoManager( comp ) );
	}

	public void setMaxWindowWidthNoLimit( boolean value )
	{
		_limitToMaxWindowWidth = !value;
	}

	public void setMaxWindowHeightNoLimit( boolean value )
	{
		_limitToMaxWindowHeight = !value;
	}

	public ConfigurationParent getLanguageConfiguration()
	{
		return( a_formLanguageConfiguration );
	}

	protected boolean isResizable( Component comp )
	{
		boolean result = false;
		if( comp instanceof JFrame )
		{
			result = ((JFrame) comp ).isResizable();
		}
		else if( comp instanceof JDialog )
		{
			result = ((JDialog) comp ).isResizable();
		}

		return( result );
	}

	protected void putWindowPosition( double zoomFactor, boolean hasToPutWindowPosition)
	{
		try
		{
			double relativeFactor = 1.0D;
			if( ! a_formGeneralConfiguration.M_isFirstTime() && isResizable(a_parentFrame) )
			{
				Dimension dimen = new Dimension( (int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ANCHO_VENTANA),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ALTO_VENTANA)
												);

				double originalFactor = a_formGeneralConfiguration.M_getDoubleParamConfiguration(FormGeneralConfiguration.CONF_ZOOM_FACTOR);
				relativeFactor = zoomFactor / originalFactor;

				Dimension resizedDim = ViewFunctions.instance().getNewDimension(dimen, _frameBorder, relativeFactor );

				Rectangle storedBounds = new Rectangle( (int) a_formGeneralConfiguration.M_getIntParamConfiguration( FormGeneralConfiguration.CONF_POSICION_X),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_POSICION_Y),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ANCHO_VENTANA),
												(int) a_formGeneralConfiguration.M_getIntParamConfiguration(FormGeneralConfiguration.CONF_ALTO_VENTANA)
														);

				Point center = ViewFunctions.instance().getCenter( storedBounds );
				double relativeFactor2 = 1.0D;	// zoomFactor is 1.0D, with original size
				doTasksPriorToFrameResizing(relativeFactor2);// zoomFactor );

				if( hasToPutWindowPosition )
				{
					Rectangle newBounds = ViewFunctions.instance().calculateNewBoundsOnScreen(storedBounds, _frameBorder, center, relativeFactor );
//					a_parentFrame.setBounds( newBounds );
					try
					{
//						blockResizeRelocateItemsResizeListeners();

						SwingUtilities.invokeLater( () ->
							a_parentFrame.setBounds( newBounds ) );
					}
					finally
					{
//						delayedInvocationToUnblockResizeRelocateItemsResizeListeners();
					}
				}
				else if( a_parentFrame instanceof Frame )
				{
					Frame frame = (Frame) a_parentFrame;
					if( frame.isResizable() )
					{
						SwingUtilities.invokeLater( () ->
							a_parentFrame.setSize( resizedDim ) );
					}
				}
				else
				{
					SwingUtilities.invokeLater( () ->
						a_parentFrame.setSize( resizedDim ) );
				}
				doTasksAfterToFrameResizing(relativeFactor);// zoomFactor );
			}
			else
			{
				relativeFactor = zoomFactor;
				Dimension dimen = a_parentFrame.getSize();

				Dimension resizedDim = ViewFunctions.instance().getNewDimension( dimen, _frameBorder, zoomFactor );

				doTasksPriorToFrameResizing(relativeFactor);// zoomFactor );

				SwingUtilities.invokeLater( () ->
						a_parentFrame.setSize( resizedDim ) );

				if( (a_parentParentFrame != null) && (a_parentParentFrame.isVisible() ) && hasToPutWindowPosition )
				{
					a_parentFrame.setLocation(	(int) a_parentParentFrame.getLocationOnScreen().getX(),
												(int) a_parentParentFrame.getLocationOnScreen().getY() + 50 );
				}
				else if ( hasToPutWindowPosition )
				{
					a_parentFrame.setLocation(StaticDesktopDialogsWrapper.getCenteredLocationForComponent_static( a_parentFrame ) );
				}

				doTasksAfterToFrameResizing(relativeFactor);// zoomFactor );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public void setHintConfiguration( HintConfiguration conf )
	{
		_hintConfiguration = conf;

		Iterator<Map.Entry<Component, HintForComponent>> entries = _mapHints.entrySet().iterator();
		while (entries.hasNext())
		{
			Map.Entry<Component, HintForComponent> thisEntry = entries.next();
			HintForComponent hfc = thisEntry.getValue();
			hfc.setHintConfiguration( conf );
		}
	}

	public HintForComponent getHintForComponent( Component comp )
	{
		return( _mapHints.get( comp ) );
	}

	public String getLabelConfigurationForHint( Component comp, String name )
	{
		String result = null;

//		if( ( comp != null ) && ( comp.getName() != null ) && ( comp.getName().length() > 0 ) )
		if( ( comp != null ) && ( name!= null ) && ( name.length() > 0 ) )
		{
			result = name + "." + TXT_HINT;
		}

		return( result );
	}

	protected String getComponentName( Component comp )
	{
		String result = ViewFunctions.instance().instance().getComponentName(comp);

		return( result );
	}

	public String getLabelConfigurationForHint( JTabbedPane tpane, String tpaneName, int index )
	{
		String result = null;

		if( ( tpane != null ) &&
			( tpaneName != null ) &&
			( tpaneName.length() > 0 ) &&
			( index >= 0 ) &&
			( index < tpane.getTabCount() ) )
		{
			Component tab = tpane.getComponentAt( index );
			String tabName = getComponentName( tab );
			if( ( tabName != null ) &&
				( tabName.length() > 0 ) )
			{
				result = tpaneName + "." + tabName + "." + TXT_HINT;
			}
		}

		return( result );
	}

	public HintForComponent createHintForComponent( Component comp, String hint )
	{
		HintForComponent hfc = _mapHints.get( comp );
		if( hfc == null )
		{
			hfc = new HintForComponent( comp, hint );
			_mapHints.put(comp, hfc);
/*
			String label = getLabelConfigurationForHint( comp );
			if( ( label != null ) && ( hint != null ) )
			{
				if( a_languageProperties.getProperty( label ) == null )
				{
					a_languageProperties.setProperty( label, hint );
				}
			}
*/
		}
		return( hfc );
	}

	protected void convertPropertiesIntoHints( JComponent jcomp, String name,
												boolean onlyText )
	{
		String label = getLabelConfigurationForHint( jcomp, name );

		if( label != null )
		{
			String hint = a_languageProperties.getProperty( label );
			if( ( hint != null ) && ( hint.length() > 0 ) )
			{
				jcomp.setToolTipText( hint );
//					HintForComponent hfc = createHintForComponent( comp, hint );
//					hfc.setHint( hint );  // in case it was already created and the language has changed.
			}
		}

		if( jcomp instanceof JTabbedPane )
		{
			convertPropertiesIntoHints_Tabs( (JTabbedPane) jcomp, name, onlyText );
		}
	}

	public void setHintForComponent( JComponent jcomp, String hint )
	{
		HintForComponent hfc = createHintForComponent( jcomp, hint );
		hfc.setHint( hint );  // in case it was already created and the language has changed.		
	}

	protected void convertPropertiesIntoHints_Tabs( JTabbedPane tpane, String name,
												boolean onlyText )
	{
		for( int ii=0; ii<tpane.getTabCount(); ii++ )
		{
			String label = getLabelConfigurationForHint( tpane, name, ii );
			
			if( label != null )
			{
				String hint = a_languageProperties.getProperty( label );
				if( ( hint != null ) && ( hint.length() > 0 ) )
				{
					tpane.setToolTipTextAt( ii, hint );
				}
			}
		}
	}
/*
	public HintConfiguration getHintConfiguration()
	{
		HintConfiguration result = _hintConfiguration;
		if( _hintConfiguration == null )
			result = _hintConfiguration;
		return( result );
	}
*/
	protected void convertHintsIntoProperties( JComponent jcomp, String name )
	{
//		HintForComponent hfc = getHintForComponent( jcomp );
//		if( hfc != null )
		String hint = jcomp.getToolTipText();
		if( ( hint != null ) && ( hint.length() > 0 ) )
		{
			String label = getLabelConfigurationForHint( jcomp, name );
//			String hint = hfc.getHint();
//			if( ( label != null ) && ( hint != null ) )
			if( label != null )
				addProperty( a_languageProperties, label, hint );
		}

		if( jcomp instanceof JTabbedPane )
		{
			convertHintsIntoProperties_Tabs( (JTabbedPane) jcomp, name );
		}
	}


	protected void convertHintsIntoProperties_Tabs( JTabbedPane tpane, String name )
	{
		for( int ii=0; ii<tpane.getTabCount(); ii++ )
		{
			String hint = tpane.getToolTipTextAt( ii );
			if( ( hint != null ) && ( hint.length() > 0 ) )
			{
				String label = getLabelConfigurationForHint( tpane, name, ii );
				if( label != null )
					addProperty( a_languageProperties, label, hint );
			}
		}
	}

	protected void getConfigurationChanges()
	{
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_ALTO_VENTANA, a_parentFrame.getHeight() );
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_ANCHO_VENTANA, a_parentFrame.getWidth() );
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_POSICION_X, a_parentFrame.getX() );
		a_formGeneralConfiguration.M_setIntParamConfiguration(FormGeneralConfiguration.CONF_POSICION_Y, a_parentFrame.getY() );

		a_formGeneralConfiguration.M_setDoubleParamConfiguration(FormGeneralConfiguration.CONF_ZOOM_FACTOR, _newZoomParam.getZoomFactor() );
	}

	public void saveGeneralConfiguration() throws ConfigurationException
	{
		getConfigurationChanges();
		a_formGeneralConfiguration.M_saveConfiguration();
	}
	
	public void saveConfiguration( ) throws InternException
	{
		if( _isInitialized )
		{
			try
			{
				convertAttributesIntoProperties( a_parentFrame, 0 );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			try
			{
				saveGeneralConfiguration();
				saveLanguageConfiguration_internal();
			}
			catch( ConfigurationException ex )
			{
				ex.printStackTrace();
				throw( new InternException( String.format( "%s : %s%n%s",
						getInternationalString_own( CONF_EXCEPTION_SAVING_FORM_CONFIGURATION ),
						a_configurationBaseFileName,
						ex.getMessage()
															)
											)
					);
			}
		}
	}

	public void saveLanguageConfiguration( ) throws InternException
	{
		try
		{
			convertAttributesIntoProperties( a_parentFrame, 0 );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		try
		{
			saveLanguageConfiguration_internal();
		}
		catch( InternException ex )
		{
			ex.printStackTrace();
			throw( new InternException( String.format( "%s : %s%n%s",
					getInternationalString_own( CONF_EXCEPTION_SAVING_FORM_CONFIGURATION ),
					a_configurationBaseFileName,
					ex.getMessage()
														)
										)
				);
		}
	}

	protected void convertJPopUpMenuTextsIntoProperties( JPopupMenu jpumnu, String name )
	{
//		String name = jpumnu.getName();
//		addProperty( prop, name + "." + TXT_TEXT, jpumnu.getText() );
	}

	protected void convertAbstractButtonTextsIntoProperties( AbstractButton absbtn, String name ) throws InternException
	{
//		String name = absbtn.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			addProperty( a_languageProperties, name + "." + TXT_TEXT, absbtn.getText() );

			if( absbtn instanceof JMenu )
			{
				JMenu jmnu = (JMenu) absbtn;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					convertAttributesIntoProperties( jmnu.getMenuComponent( ii ), 2 );

				convertAttributesIntoProperties( jmnu.getPopupMenu(), 2 );
			}
		}
	}

	protected void convertUrlJLabelTextsIntoProperties( UrlJLabel urlJLabel, String name )
	{
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_URL, urlJLabel.getUrl() );
	}

	protected void convertJLabelTextsIntoProperties( JLabel jlbl, String name )
	{
//		String name = jlbl.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TEXT, jlbl.getText() );
		if( jlbl instanceof UrlJLabel )
			convertUrlJLabelTextsIntoProperties( (UrlJLabel) jlbl, name );
	}
/*
	protected void convertJListTextsIntoProperties( JList jlst )
	{
		String name = jlst.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			ListModel lm = jlst.getModel();
			for( int ii=0; ii<lm.getSize(); ii++ )
				addProperty( a_languageProperties, name + "." + TXT_ELEMENT_AT + "." + String.valueOf(ii), lm.getElementAt(ii).toString() );
		}
	}
*/
	protected void convertJTextComponentTextsIntoProperties( JTextComponent jtxtcmp, String name )
	{
//		String name = jtxtcmp.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TEXT, jtxtcmp.getText() );
	}

	protected void convertJTabbedPaneTextsIntoProperties( JTabbedPane tpane, String name )
	{
//		String name = tpane.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			for( int ii=0; ii<tpane.getTabCount(); ii++ )
			{
				Component tab = tpane.getComponentAt( ii );
				String tabName = getComponentName( tab );
				if( ( tabName != null ) &&
					( tabName.length() > 0 ) )
				{
					addProperty( a_languageProperties, name + "." + tabName + "." + TXT_TITLE, tpane.getTitleAt(ii) );
				}
			}
		}
	}

	protected void convertJSplitPaneAttributesIntoProperties( JSplitPane jsp, String name ) throws InternException
	{
//		String name = jsp.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			addProperty( a_generalProperties, name + "." + TXT_DIVIDER_LOCATION,
						String.valueOf( ( jsp.getDividerLocation() /*+ jsp.getDividerSize() / 2.0D */ ) /
											this._newZoomParam.getZoomFactor() ) );
			addProperty( a_generalProperties, name + "." + FormGeneralConfiguration.CONF_WIDTH,
						String.valueOf( jsp.getWidth() / _newZoomParam.getZoomFactor() ) );
			addProperty( a_generalProperties, name + "." + FormGeneralConfiguration.CONF_HEIGHT,
						String.valueOf( jsp.getHeight() / _newZoomParam.getZoomFactor() ) );
		}
	}

	protected void convertContainerAttributesIntoProperties( Container contnr, String name ) throws InternException
	{
//		String name = contnr.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			if( contnr instanceof JComponent )
			{
				JComponent jcomp = (JComponent) contnr;
				if( jcomp.getBorder() instanceof TitledBorder )
				{
					TitledBorder tb = (TitledBorder) jcomp.getBorder();
					String title = tb.getTitle();
					if( title != null )
						addProperty( a_languageProperties, name + "." + TXT_BORDER + "." + TXT_TITLE, title );
				}
			}

			if( contnr instanceof JSplitPane )
			{
				JSplitPane jsp = (JSplitPane) contnr;
				convertJSplitPaneAttributesIntoProperties( jsp, name );
			}
			if( contnr instanceof AbstractButton )
			{
				AbstractButton absbtn = (AbstractButton) contnr;
				convertAbstractButtonTextsIntoProperties( absbtn, name );
			}
			else if( contnr instanceof JLabel )
			{
				JLabel jlbl = (JLabel) contnr;
				convertJLabelTextsIntoProperties( jlbl, name );
			}
			else if( contnr instanceof JList )
			{
				JList jlst = (JList) contnr;
//				convertJListTextsIntoProperties( jlst );
			}
			else if( contnr instanceof JTextComponent )
			{
				JTextComponent jtxtcmp = (JTextComponent) contnr;
				convertJTextComponentTextsIntoProperties( jtxtcmp, name );
			}
			else if( contnr instanceof JPopupMenu )
			{
				JPopupMenu jpumnu = (JPopupMenu) contnr;
				convertJPopUpMenuTextsIntoProperties( jpumnu, name );
			}
			else if( contnr instanceof JFrame )
			{
				JFrame jfr = (JFrame) contnr;
				convertJFrameTextsIntoProperties( jfr, name );
			}
			else if( contnr instanceof JDialog )
			{
				JDialog jdial = (JDialog) contnr;
				convertJDialogTextsIntoProperties( jdial, name );
			}
			else if( contnr instanceof JInternalFrame )
			{
				JInternalFrame jif = (JInternalFrame) contnr;
				convertJInternalFrameTextsIntoProperties( jif, name );
			}
		}
	}

	protected void addProperty( Properties prop, String label, String value )
	{
		if( ( prop != null ) && ( label != null ) && ( value != null ) )
			prop.setProperty( label, value);
	}

	protected void convertButtonTextsIntoProperties( Button btn, String name )
	{
//		String name = btn.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_LABEL, btn.getLabel() );
	}

	protected void convertCheckBoxTextsIntoProperties( Checkbox ckb, String name )
	{
//		String name = ckb.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_LABEL, ckb.getLabel() );
	}

	protected void convertChoiceTextsIntoProperties( Choice chc, String name )
	{
//		String name = chc.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			for( int ii=0; ii<chc.getItemCount(); ii++ )
				addProperty( a_languageProperties, name + "." + TXT_ITEM + "." + String.valueOf(ii), chc.getItem(ii) );
		}
	}

	protected void convertLabelTextsIntoProperties( Label lbl, String name )
	{
//		String name = lbl.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TEXT, lbl.getText() );
	}

	protected void convertListTextsIntoProperties( List lst, String name )
	{
//		String name = lst.getName();
	}

	protected void convertTextComponentTextsIntoProperties( TextComponent txtcmp, String name )
	{
//		String name = txtcmp.getName();
	}

	protected void convertJFrameTextsIntoProperties( JFrame jfr, String name )
	{
//		String name = jfr.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TITLE, jfr.getTitle() );
	}

	protected void convertJDialogTextsIntoProperties( JDialog jdial, String name )
	{
//		String name = jdial.getName();
		if( ( name != null ) && ( !name.equals("") ) ) addProperty( a_languageProperties, name + "." + TXT_TITLE, jdial.getTitle() );
	}

	protected void convertJInternalFrameTextsIntoProperties( JInternalFrame jif, String name )
	{
//		String name = jif.getName();

//		addProperty( prop, name + "." + TXT_TITLE, jif.getTitle() );
	}

	public TextCompPopupManager getTextCompPopupManager( JTextComponent textComp )
	{
		TextCompPopupManager result = null;
		if( _mapOfComponents != null )
			result = _mapOfComponents.getTextCompPopupManager( textComp );
		return( result );
	}

	@Override
	public JPopupMenu getNonInheritedPopupMenu( JComponent jcomp )
	{
		JPopupMenu result = null;

		if( ( jcomp.getComponentPopupMenu() != null ) &&
			! jcomp.getInheritsPopupMenu() )
		{
			result = jcomp.getComponentPopupMenu();
		}
		else if( jcomp instanceof JTextComponent )
		{
			JTextComponent jtc = (JTextComponent) jcomp;
			TextCompPopupManager tcpm = getTextCompPopupManager( jtc );
			if( tcpm != null )
				result = tcpm.getJPopupMenu();
		}

		return( result );
	}

	protected void convertAttributesIntoProperties( Component comp, int level ) throws InternException
	{
/*
		Font font = comp.getFont();
		if( font != null )
			_mapOfComponents.setOriginalTextSize(comp, font.getSize() );
*/
		if( ( comp.getName() != null ) &&
			( comp.getName().equals( "jCb_locale1" ) ) )
		{
			int kk=1;
		}

		level = level + 1;

		String name = getComponentName(comp);
		if( !_isInitialized )	// when the object is initializing, we add an empty ResizeRelocateItem for every Component
								// to be able to change the zoomFactor.
								// Later, the specific ResizeRelocateItems in the MapResizeRelocateComponentItem map
								// will be updated.
		{
			_mapOfComponents.createAndStoreNewResizeRelocateItem( comp );
		}

		if( level == 1 )
		{
			if( a_vectorJpopupMenus != null )
			{
				for( int ii=0; ii<a_vectorJpopupMenus.size(); ii++ )
				{
					convertAttributesIntoProperties( a_vectorJpopupMenus.elementAt(ii), level );
				}
			}
		}

		// we browse all popup menus (at least the ones of JTextComponents that allow undo/redo, copy/paste).
		if( comp instanceof JComponent )
		{
			JPopupMenu jppm = getNonInheritedPopupMenu( (JComponent) comp );
			if( jppm != null )
			{
				convertAttributesIntoProperties( jppm, level );
			}
		}

		if( comp instanceof JComboBox )
		{
			JComboBox combo = (JComboBox) comp;
			BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

			convertAttributesIntoProperties( popup, level );
		}

		if( comp instanceof JComponent )
			convertHintsIntoProperties( (JComponent) comp, name );

		if( comp instanceof JDesktopPane )
		{
		}
		else if( comp instanceof JTabbedPane )
		{
			JTabbedPane tabbedPane = (JTabbedPane) comp;
			convertJTabbedPaneTextsIntoProperties( tabbedPane, name );
			for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
			{
				convertAttributesIntoProperties( tabbedPane.getComponentAt(ii), level );
			}
		}
		else if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			convertContainerAttributesIntoProperties( contnr, name );

			if( comp instanceof JComboBox )
			{
				int kk=1;
			}

			if( comp instanceof JMenu )
				convertAttributesIntoProperties( ( ( JMenu ) comp ).getPopupMenu(), level );
			
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				convertAttributesIntoProperties( contnr.getComponent(ii), level );
			}
		}

		if( ( ( name != null ) && ( !name.equals("") ) ) || ( comp instanceof JRootPane ) )
		{
			if( comp instanceof JDesktopPane )
			{
			}
			else if( comp instanceof Container	)
			{
			}
			else if( comp instanceof Button )
			{
				Button btn = (Button) comp;
				convertButtonTextsIntoProperties( btn, name );
			}
			else if( comp instanceof Checkbox )
			{
				Checkbox ckb = (Checkbox) comp;
				convertCheckBoxTextsIntoProperties( ckb, name );
			}
			else if( comp instanceof Choice )
			{
				Choice chc = (Choice) comp;
				convertChoiceTextsIntoProperties( chc, name );
			}
			else if( comp instanceof Label )
			{
				Label lbl = (Label) comp;
				convertLabelTextsIntoProperties( lbl, name );
			}
			else if( comp instanceof	List )
			{
				List lst = (List) comp;
				convertListTextsIntoProperties( lst, name );
			}
			else if( comp instanceof TextComponent )
			{
				TextComponent txtcmp = (TextComponent) comp;
				convertTextComponentTextsIntoProperties( txtcmp, name );
			}
			else
			{
				throw( new InternException( String.format( "%s. %s",
						getInternationalString_own( CONF_CLASS_OF_COMPONENT_NOT_EXPECTED ),
						comp.getClass().getName()
															)
											)
					);
			}
		}
	}

	protected void convertPropertiesIntoAbstractButtonTexts( AbstractButton absbtn,
															String name, ZoomParam zp,
															boolean onlyText ) throws InternException
	{
//		String name = absbtn.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TEXT );

			if( value != null )
				absbtn.setText( value );
			else
				System.out.println("Error cargando propiedades de idioma de " + name + " en " + a_parentFrame.getName() );

			if( absbtn instanceof JMenu )
			{
				JMenu jmnu = (JMenu) absbtn;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					convertPropertiesIntoAttributes( jmnu.getMenuComponent( ii ), zp, 2, onlyText );

				convertPropertiesIntoAttributes( jmnu.getPopupMenu(), zp, 2, onlyText );
			}
		}
	}

	protected void convertPropertiesIntoUrlJLabelTexts( UrlJLabel urlJLabel, String name,
												boolean onlyText ) throws InternException
	{
//		String name = jlbl.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_URL );

			if( value != null )
				urlJLabel.setUrl( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoJLabelTexts( JLabel jlbl, String name,
												boolean onlyText ) throws InternException
	{
//		String name = jlbl.getName();

		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TEXT );

			if( value != null )
				jlbl.setText( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}

		if( jlbl instanceof UrlJLabel )
		{
			convertPropertiesIntoUrlJLabelTexts( (UrlJLabel) jlbl, name, onlyText );
		}
	}

	protected void convertPropertiesIntoJListTexts( JList jlst, String name,
												boolean onlyText ) throws InternException
	{
/*
		String name = jlst.getName();

		DefaultListModel dlm = new DefaultListModel();
		String value = "";
		for( int ii=0; ( value != null ); ii++ )
		{
			value = prop.getProperty( name + "." + TXT_TEXT + "." + String.valueOf(ii) );
			if( value != null )
				dlm.setElementAt( value, ii );
		}
		jlst.setModel( dlm );
 */
	}

	protected void convertPropertiesIntoJTextComponentTexts( JTextComponent jtxtcmp, String name,
												boolean onlyText ) throws InternException
	{
//		String name = jtxtcmp.getName();
	}

	protected void convertPropertiesIntoJTabbedPaneTexts( JTabbedPane tpane, String name,
												boolean onlyText ) throws InternException
	{
//		String name = tpane.getName();
		
		if( ( name != null ) && ( name.length() > 0 ) )
		{
			for( int ii=0; ii<tpane.getTabCount(); ii++ )
			{
				Component tab = tpane.getComponentAt( ii );
				String tabName = getComponentName( tab );
				if( ( tabName != null ) &&
					( tabName.length() > 0 ) )
				{
					String value = a_languageProperties.getProperty( name + "." + tabName + "." + TXT_TITLE );
					if( value != null )
						tpane.setTitleAt(ii, value);
				}
			}
		}
	}

	protected void convertPropertiesIntoJPopUpMenuTexts( JPopupMenu jpumnu, String name,
												boolean onlyText ) throws InternException
	{
//		String name = jpumnu.getName();
//		addProperty( prop, name + "." + TXT_TEXT, jpumnu.getText() );
	}

	protected void convertPropertiesIntoJSplitPaneAttributes( JSplitPane jsp, String name, ZoomParam zp,
												boolean onlyText ) throws InternException
	{
//		String name = jsp.getName();

		if( ! onlyText )
		{
			double value = a_formGeneralConfiguration.M_getDoubleParamConfiguration( name + "." + TXT_DIVIDER_LOCATION );
			int width = (int) ( (double) a_formGeneralConfiguration.M_getDoubleParamConfiguration( name + "." + FormGeneralConfiguration.CONF_WIDTH ) );
			int height = (int) ( (double) a_formGeneralConfiguration.M_getDoubleParamConfiguration( name + "." + FormGeneralConfiguration.CONF_HEIGHT ) );

			if( value > 0 )
			{
				Dimension size = null;
				if( ( width > 0 ) && ( height > 0 ) )
				{
					size = new Dimension( width, height );
				}

				int dividerLocation = (int) value;
//				jsp.setDividerLocation( (int) Math.round((value * zp.getZoomFactor() ) ) );
				ResizeRelocateItem rri = null;
	//			rri = a_mapResizeRelocateComponents.get(jsp);
				rri = _mapOfComponents.getResizeRelocateItem(jsp);
				if( rri instanceof ResizeRelocateItem_JSplitPane )
					((ResizeRelocateItem_JSplitPane)rri).setDividerLocationWithoutZoom( dividerLocation, size );
//					((ResizeRelocateItem_JSplitPane)rri).saveStateOfLastDraggedDividerLocation();

//				resizeOrRelocateComponent_simple( jsp, zp );
	//			resizeOrRelocateComponentsAtJSplitPane( jsp );
			}
		}
	}

	protected void convertPropertiesIntoContainerAttributes( Container contnr, ZoomParam zp,
												boolean onlyText ) throws InternException
	{
//		String name = contnr.getName();
		String name = getComponentName(contnr);

		if( contnr instanceof JFrame )
		{
			JFrame jfr = (JFrame) contnr;
			convertPropertiesIntoJFrameTexts( jfr, name, onlyText );
		}
		else if( contnr instanceof JDialog )
		{
			JDialog jdial = (JDialog) contnr;
			convertPropertiesIntoJDialogTexts( jdial, name, onlyText );
		}
		else if( ( name != null ) && ( !name.equals("") ) )
		{
			if( contnr instanceof JComponent )
			{
				JComponent jcomp = (JComponent) contnr;
				if( jcomp.getBorder() instanceof TitledBorder )
				{
					String value = a_languageProperties.getProperty( name + "." + TXT_BORDER + "." + TXT_TITLE );
					TitledBorder tb = (TitledBorder) jcomp.getBorder();
					if( value != null )
					{
						tb.setTitle( value );
						jcomp.repaint();
					}
				}
			}

			if( contnr instanceof JSplitPane )
			{
				JSplitPane jsp = (JSplitPane) contnr;
				convertPropertiesIntoJSplitPaneAttributes( jsp, name, zp, onlyText );
				int a=0;
			}
			else if( contnr instanceof AbstractButton )
			{
				AbstractButton absbtn = (AbstractButton) contnr;
				convertPropertiesIntoAbstractButtonTexts( absbtn, name, zp, onlyText );
			}
			else if( contnr instanceof JLabel )
			{
				JLabel jlbl = (JLabel) contnr;
				convertPropertiesIntoJLabelTexts( jlbl, name, onlyText );
			}
			else if( contnr instanceof JList )
			{
				JList jlst = (JList) contnr;
				convertPropertiesIntoJListTexts( jlst, name, onlyText );
			}
			else if( contnr instanceof JTextComponent )
			{
				JTextComponent jtxtcmp = (JTextComponent) contnr;
				convertPropertiesIntoJTextComponentTexts( jtxtcmp, name, onlyText );
			}
			else if( contnr instanceof JPopupMenu )
			{
				JPopupMenu jpumnu = (JPopupMenu) contnr;
				convertPropertiesIntoJPopUpMenuTexts( jpumnu, name, onlyText );
			}
			else if( contnr instanceof JInternalFrame )
			{
				JInternalFrame jif = (JInternalFrame) contnr;
				convertPropertiesIntoJInternalFrameTexts( jif, name, onlyText );
			}
		}
	}

	protected void convertPropertiesIntoButtonTexts( Button btn, String name,
												boolean onlyText ) throws InternException
	{
//		String name = btn.getName();
		if( name !=  null )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_LABEL );

			if( value != null )
				btn.setLabel( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoCheckBoxTexts( Checkbox ckb, String name,
												boolean onlyText ) throws InternException
	{
//		String name = ckb.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_LABEL );

			if( value != null )
				ckb.setLabel( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoChoiceTexts( Choice chc, String name,
												boolean onlyText ) throws InternException
	{
//		String name = chc.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			chc.removeAll();

			String value = "";
			for( int ii=0; (value != null) && (ii<chc.getItemCount()); ii++ )
			{
				value = a_languageProperties.getProperty( name + "." + TXT_ITEM + "." + String.valueOf(ii) );

				if( value != null )
					chc.add( value );
				else
				{
/*					throw new InternException(	"Needed property not found. " + name + "." + TXT_ITEM + "." + String.valueOf(ii) +
																			" in " + a_frameParent.getName() ); */
				}
			}
		}
	}

	protected void convertPropertiesIntoLabelTexts( Label lbl, String name,
												boolean onlyText ) throws InternException
	{
//		String name = lbl.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TEXT );

			if( value != null )
				lbl.setText( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void convertPropertiesIntoListTexts( List lst, String name,
												boolean onlyText ) throws InternException
	{
//		String name = lst.getName();
	}

	protected void convertPropertiesIntoTextComponentTexts( TextComponent txtcmp, String name,
												boolean onlyText ) throws InternException
	{
//		String name = txtcmp.getName();
	}

	protected void convertPropertiesIntoJFrameTexts( JFrame jfr, String name,
												boolean onlyText ) throws InternException
	{
//		String name = jfr.getName();
		String value = a_languageProperties.getProperty( FormLanguageConfiguration.CONF_WINDOW_TITLE );

		if( ( value == null ) && ( name != null ) && ( !name.equals("") ) )
			value = a_languageProperties.getProperty( name + "." + TXT_TITLE );

		if( value != null )
			jfr.setTitle( value );
		else
		{
//			throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
		}
	}

	protected void convertPropertiesIntoJDialogTexts( JDialog jdial, String name,
												boolean onlyText ) throws InternException
	{
//		String name = jdial.getName();
		String value = a_languageProperties.getProperty( FormLanguageConfiguration.CONF_WINDOW_TITLE );

		if( ( value == null ) && ( name != null ) && ( !name.equals("") ) )
			value = a_languageProperties.getProperty( name + "." + TXT_TITLE );

		if( value != null )
			jdial.setTitle( value );
		else
		{
//			throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
		}
	}

	protected void convertPropertiesIntoJInternalFrameTexts( JInternalFrame jif, String name,
												boolean onlyText ) throws InternException
	{
//		String name = jif.getName();
		if( ( name != null ) && ( !name.equals("") ) )
		{
			String value = a_languageProperties.getProperty( name + "." + TXT_TITLE );

			if( value != null )
				jif.setTitle( value );
			else
			{
//				throw new InternException( "Error cargando propiedades de idioma de " + name + " en " + a_frameParent.getName() );
			}
		}
	}

	protected void setTitleToWindow( String title )
	{
		if( a_parentFrame instanceof JFrame )
		{
			( (JFrame)a_parentFrame ).setTitle( title );
		}
		else if( a_parentFrame instanceof JDialog )
		{
			( (JDialog)a_parentFrame ).setTitle( title );
		}
		else if( a_parentFrame instanceof JInternalFrame )
		{
			( (JInternalFrame)a_parentFrame ).setTitle( title );
		}
	}

	protected void convertPropertiesIntoAttributes( Component comp, ZoomParam zp,
													int level ) throws InternException
	{
		boolean onlyText = false;
		convertPropertiesIntoAttributes( comp, zp, level, onlyText );
	}

	protected void convertPropertiesIntoAttributes( Component comp, ZoomParam zp,
													int level, boolean onlyText ) throws InternException
	{
		level = level + 1;

		String name = getComponentName( comp );

		if( level == 1 )
		{
			_newZoomParam = zp;

			if( a_vectorJpopupMenus != null )
			{
				for( int ii=0; ii<a_vectorJpopupMenus.size(); ii++ )
				{
					convertPropertiesIntoAttributes( a_vectorJpopupMenus.get(ii), zp, level, onlyText );
				}
			}

			String title = a_languageProperties.getProperty( TXT_TITLE );
			if( ( title != null ) && ( title.length() > 0 ) )
			{
				setTitleToWindow( title );
			}
		}

		if( comp instanceof JComponent )
			convertPropertiesIntoHints( (JComponent) comp, name, onlyText );

		// to resize before setting the initialstate of JSplitPanes.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		ResizeRelocateItem rri = a_mapResizeRelocateComponents.get(comp);
		ResizeRelocateItem rri = _isOnTheFly ?
								_mapOfComponents.getResizeRelocateItemOnTheFly(comp) :
								_mapOfComponents.getResizeRelocateItem(comp);
		if( ( rri != null ) ||
			( comp instanceof JFrame ) ||
			( comp instanceof JRootPane ) ||
			( comp instanceof JLayeredPane ) ||
			( comp instanceof JPanel ) ||
			( comp instanceof JInternalFrame )  ||
			( comp instanceof Container ) )
		{
			if( ( comp instanceof JLayeredPane ) &&
				!( comp instanceof JDesktopPane ) || ( comp instanceof JRootPane ) ||
				( comp instanceof JPanel ) && ( name != null ) &&
				( name.length() >= 5 ) &&
				name.substring( 0, 5 ).equals( "null." ) )
			{
				Component parent = comp.getParent();
				if( parent != null )
				{
					setSizeForResizePanels( comp );
				}
			}
			else if( ( rri != null ) && ! onlyText )
			{
				rri.execute( zp );
				rri.newExpectedZoomParam(_newZoomParam);
				rri.registerListeners();
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// we browse all popup menus (at least the ones of JTextComponents that allow undo/redo, copy/paste).
		if( comp instanceof JComponent )
		{
			JPopupMenu jppm = getNonInheritedPopupMenu( (JComponent) comp );
			if( jppm != null )
			{
				convertPropertiesIntoAttributes( jppm, zp, level, onlyText );
			}
		}

		if( comp instanceof JDesktopPane )
		{
		}
		else if( comp instanceof JTabbedPane )
		{
			JTabbedPane tabbedPane = (JTabbedPane) comp;
			convertPropertiesIntoJTabbedPaneTexts( tabbedPane, name, onlyText );
			for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
			{
				convertPropertiesIntoAttributes( tabbedPane.getComponentAt(ii), zp, level, onlyText );
			}
		}
		else if( comp instanceof JComboBox )
		{
			JComboBox combo = (JComboBox) comp;
			BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

			convertPropertiesIntoAttributes( popup, zp, level, onlyText );
		}
		else if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			convertPropertiesIntoContainerAttributes( contnr, zp, onlyText );

			if( comp instanceof JMenu )
				convertPropertiesIntoAttributes( ( ( JMenu ) comp ).getPopupMenu(), zp, level, onlyText );

			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				convertPropertiesIntoAttributes( contnr.getComponent(ii), zp, level, onlyText );
			}
		}

//		String name = comp.getName();
		if( ( name != null ) && ( !name.equals("") ) || ( comp instanceof JRootPane ) )
		{
			if( comp instanceof JDesktopPane )
			{
			}
			else if( comp instanceof Container	)
			{
			}
			else if( comp instanceof Button )
			{
				Button btn = (Button) comp;
				convertPropertiesIntoButtonTexts( btn, name, onlyText );
			}
			else if( comp instanceof Checkbox )
			{
				Checkbox ckb = (Checkbox) comp;
				convertPropertiesIntoCheckBoxTexts( ckb, name, onlyText );
			}
			else if( comp instanceof Choice )
			{
				Choice chc = (Choice) comp;
				convertPropertiesIntoChoiceTexts( chc, name, onlyText );
			}
			else if( comp instanceof Label )
			{
				Label lbl = (Label) comp;
				convertPropertiesIntoLabelTexts( lbl, name, onlyText );
			}
			else if( comp instanceof	List )
			{
				List lst = (List) comp;
				convertPropertiesIntoListTexts( lst, name, onlyText );
			}
			else if( comp instanceof TextComponent )
			{
				TextComponent txtcmp = (TextComponent) comp;
				convertPropertiesIntoTextComponentTexts( txtcmp, name, onlyText );
			}
			else
			{
				throw( new InternException( String.format( "%s. %s",
						getInternationalString_own( CONF_CLASS_OF_COMPONENT_NOT_EXPECTED ),
						comp.getClass().getName()
															)
											)
					);
			}
		}

		if( level == 1 )
		{
//			_previousZoomParam = zp;
			_previousZoomFactor = zp.getZoomFactor();
		}
	}

	@Override
	public void changeLanguage( String language ) throws InternException
	{
		try
		{
			if( _isInitialized )
				saveLanguageConfiguration();

			internationalizeFont( language );

			if( a_formLanguageConfiguration != null )
			{
				a_formLanguageConfiguration.changeLanguage( language );
				boolean onlyText = true;
				convertPropertiesIntoAttributes(a_parentFrame, _newZoomParam, 0, onlyText );
//				pickPreviousDataOrResizeOrChangeZoomFactor();
			}
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
			throw( new InternException( ce.getMessage() ) );
		}
	}

	protected void saveLanguageConfiguration_internal() throws InternException
	{
		if( a_formLanguageConfiguration != null )
		{
			try
			{
				if( a_formLanguageConfiguration.getLanguage() != null )
					a_formLanguageConfiguration.M_saveConfiguration();
			}
			catch( ConfigurationException ex )
			{
				ex.printStackTrace();
				throw( new InternException( String.format( "%s : %s%n%s",
						getInternationalString_own( CONF_EXCEPTION_SAVING_FORM_CONFIGURATION ),
						a_configurationBaseFileName,
						ex.getMessage()
															)
											)
					);
			}
		}
	}

	public String M_getLanguage()
	{
		String result = null;
		
		if( a_formLanguageConfiguration != null ) result = a_formLanguageConfiguration.M_getLanguage();
		
		return( result );
	}

/*
	protected void changeFontSize( Component comp, float factor )
	{
		Font oldFont = comp.getFont();
		if( oldFont != null )
		{
//			Font newFont = oldFont.deriveFont( (float) Math.round( factor * comp.getFont().getSize() ) );
			Font newFont = oldFont.deriveFont( (float) _mapOfComponents.getFactoredTextSize(comp, factor) );
			comp.setFont( newFont );
		}
	}

	protected void changeFontSizeRecursive( Component comp, float factor )
	{

		if( !(comp instanceof JInternalFrame) )
		{
			changeFontSize( comp, factor );
			if( comp instanceof Container	)
			{
				Container contnr = (Container) comp;
				for( int ii=0; ii<contnr.getComponentCount(); ii++ )
				{
					changeFontSizeRecursive( contnr.getComponent(ii), factor );
				}

				if( comp instanceof JMenu )
				{
					JMenu jmnu = (JMenu) comp;
					for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
						changeFontSizeRecursive( jmnu.getMenuComponent( ii ), factor );
				}
			}
		}
	}

	public void M_changeFontSize( float factor )
	{
		if( factor > 0 )
		{
//			float relativeFactor = factor / a_lastFactor;
			
			if( (a_parentFrame != null) && (a_parentFrame instanceof Container ) )
			{
				Container contentPane = (Container) a_parentFrame;
				for( int ii=0; ii<cont.getComponentCount(); ii++ )
				{
					changeFontSizeRecursive( contentPane.getComponent(ii), factor );
				}
			}
			else
			{
				changeFontSizeRecursive( a_parentFrame, factor );
			}

			if( a_vectorJpopupMenus != null )
			{
				for( int ii=0; ii<a_vectorJpopupMenus.size(); ii++ )
				{
					changeFontSizeRecursive( a_vectorJpopupMenus.get(ii), factor );
				}
			}

//			a_lastFactor = factor;
		}
	}
*/
	@Override
	public void componentHidden(ComponentEvent e)
	{
		
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
		
	}

	protected boolean limitResizeOnParent()
	{
		boolean hasBeenLimitedXX = false;
		boolean hasBeenLimitedYY = false;
		
		Point ml = MouseFunctions.getMouseLocation();
		int xx = (int) ml.getX();
		int yy = (int) ml.getY();

//		Dimension ps = a_parentFrame.getPreferredSize();
		Dimension ps = a_parentFrame.getMaximumSize();
		
		if( _limitToMaxWindowWidth )
		{
			if( a_parentFrame.getWidth() > ps.getWidth() )
			{
				hasBeenLimitedXX = true;
				if( xx > ( a_parentFrame.getX() + (int) ps.getWidth() - 15 ) )
					xx = (int) ( xx + ps.getWidth() - a_parentFrame.getWidth() );
				else if( xx < ( a_parentFrame.getX() + 15 ) )
					xx = (int) ( xx + a_parentFrame.getWidth() - ps.getWidth() );
			}
		}

		if( _limitToMaxWindowHeight )
		{
			if( a_parentFrame.getHeight() > ps.getHeight() )
			{
				hasBeenLimitedYY = true;
				if( yy > ( a_parentFrame.getY() + (int) ps.getHeight() - 15 ) )
					yy = (int) ( yy + ps.getHeight() - a_parentFrame.getHeight() );
				else if( yy < ( a_parentFrame.getY() + 15 ) )
					yy = (int) ( yy + a_parentFrame.getHeight() - ps.getHeight() );
			}
		}

		if( hasBeenLimitedXX || hasBeenLimitedYY )
		{
			MouseFunctions.moveMouse( new Point( xx, yy ) );

			int width = a_parentFrame.getWidth();
			int height = a_parentFrame.getHeight();
			if( hasBeenLimitedXX )
			{
				width = (int) ps.getWidth();
			}

			if( hasBeenLimitedYY )
			{
				height = (int) ps.getHeight();
			}

			int widthFinal = width;
			int heightFinal = height;
			SwingUtilities.invokeLater( () -> a_parentFrame.setSize( new Dimension( widthFinal, heightFinal ) ) );
		}

		return( hasBeenLimitedXX || hasBeenLimitedYY );
	}

	public JRootPane getRootPane()
	{
		JRootPane jrp = null;
		if( a_parentFrame instanceof JDialog )
			jrp = ((JDialog) a_parentFrame).getRootPane();
		else if( a_parentFrame instanceof JFrame )
			jrp = ((JFrame) a_parentFrame).getRootPane();

		return( jrp );
	}

	protected Component getJLayeredPane( Component jrootpane )
	{
		Component result = null;
		
		if( jrootpane instanceof Container )
		{
			Container cont = (Container) jrootpane;

			for( int ii=0; ( ii<cont.getComponentCount() ) && ( result == null ); ii++ )
			{
				Component tmpComp = cont.getComponent(ii);
				if( tmpComp instanceof JLayeredPane )
					result = tmpComp;
			}
		}

		return( result );
	}

	protected void componentResized( Component comp )
	{
//		if( ! SwingUtilities.isEventDispatchThread() )
		{

			SwingUtilities.invokeLater(new Runnable() {
					public void run()
					{
						try
						{
							JRootPane jrp = getRootPane();

							boolean hasBeenLimited = false;
							if( comp == jrp )
							{
								hasBeenLimited = limitResizeOnParent();
							}

							if( !hasBeenLimited )
							{
						//				resizeComponentRecursive( a_parentFrame );
		
						/*
										if( isRootPanel( comp ) )
										{
											resizeRootPanels( comp );
										}
						*/
/*
								if( comp == jrp )
								{
									Component jLayeredPane = getJLayeredPane( jrp );
									if( jLayeredPane != null )
										pickPreviousDataOrResizeOrChangeZoomFactor(RESIZE_OR_RELOCATE_PROCEDURE,
																					jLayeredPane,
																					_newZoomParam,
																					false );
								}
								else
									resizeOrRelocateComponent_simple(comp, _newZoomParam );
*/
							}
							//		pickPreviousDataOrResizeOrChangeZoomFactor( );
						}
						catch( Throwable th )
						{
							th.printStackTrace();
						}
					}
			});
			return;

		}

/*		else
		{
			try
			{
				JRootPane jrp = getRootPane();

				boolean hasBeenLimited = false;
				if( comp == jrp )
				{
					hasBeenLimited = limitResizeOnParent();
				}

				if( !hasBeenLimited )
				{
					if( comp == jrp )
					{
						Component jLayeredPane = getJLayeredPane( jrp );
						if( jLayeredPane != null )
							pickPreviousDataOrResizeOrChangeZoomFactor(jLayeredPane, _previousZoomParam, false );
					}
					else
						resizeOrRelocateComponent_simple(comp, _previousZoomParam );
				}
				//		pickPreviousDataOrResizeOrChangeZoomFactor( );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
*/
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		Component comp = e.getComponent();
		componentResized( comp );
	}

	protected boolean isRootPanel( Component comp )
	{
		boolean result = ( ( comp instanceof JLayeredPane ) && !( comp instanceof JDesktopPane ) ||
						( comp instanceof JRootPane ) ||
						( comp instanceof JPanel ) && ( comp.getName() != null ) &&
						( comp.getName().length() >= 5 ) && comp.getName().substring( 0, 5 ).equals( "null." ) );
		return( result );
	}

	protected void resizeRootPanels( Component comp )
	{
		LinkedList<Component> list = new LinkedList<Component>();
		list.addFirst(comp);
		Component current = comp;
		
		while( current.getParent() != null )
		{
			current = current.getParent();
			list.addFirst(current);
		}

		Iterator<Component> it = list.iterator();
		while( it.hasNext() )
			setSizeForResizePanels( it.next() );
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
		
	}

	@Override
	public void windowStateChanged(WindowEvent arg0)
	{
		SwingUtilities.invokeLater( new Runnable() {
			public void run()
			{
				prepareResizeOrRelocateToZoom( );
			}
		});
	}
/*
	protected void findEresizeCursor( Component comp )
	{
		if( comp.getCursor().getName().equals( CursorFunctions._eResizeCursor.getName() ) &&
			! (comp.getParent() instanceof JSplitPane ) )
		{
			System.out.println( "Found" );
		}

		if( comp instanceof Container )
		{
			Container contentPane = (Container) comp;

			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				findEresizeCursor( contentPane.getComponent( ii ) );
		}
	}
*/
	protected void recursiveAddFocusListener( Component comp, FocusListener lsnr )
	{
		comp.addFocusListener( lsnr );

		if( comp instanceof JTabbedPane )
		{
			JTabbedPane tabbedPane = (JTabbedPane) comp;

			for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
				recursiveAddFocusListener( tabbedPane.getComponentAt( ii ), lsnr );
		}
		else if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			
			if( comp instanceof JMenu )
				recursiveAddFocusListener( ( ( JMenu ) comp ).getPopupMenu(), lsnr );

			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				recursiveAddFocusListener( cont.getComponent( ii ), lsnr );
		}
	}
	
	protected void setListeners( MapOfComponents mapOfComponents )
	{
		JRootPane jrp = getRootPane();
		if( jrp != null )
			jrp.addComponentListener(this);

		if( a_parentFrame instanceof InternationalizedWindow )
			recursiveAddFocusListener( a_parentFrame, this );

		if( ! a_hasComponentListenerBeenSet )
		{
			a_hasComponentListenerBeenSet = true;

			a_componentListener_this = this;

//			_map_resize_parents = new HashMap< Component, Boolean >();

			if( mapOfComponents != null )
			{
				Iterator<Map.Entry<Component, ComponentData>> it = mapOfComponents.getEntrySetIterator();

				while( it.hasNext() )
				{
					Map.Entry<Component, ComponentData> entry = it.next();
					Component comp = entry.getKey();
					Component parent = comp.getParent();
					ComponentData cd = entry.getValue();

					if( ( cd.getResizeRelocateItem() != null ) && ( parent != null ) && ( _mapOfComponents.getResizeParents( parent ) ) )
					{
						SetComponentListenerThread sclt = new SetComponentListenerThread( parent, this );
						SwingUtilities.invokeLater(sclt);

						_mapOfComponents.setResizeParents( parent, true );
//						_map_resize_parents.put( parent, true );
					}
				}

		//			a_parentFrame.addComponentListener( this );
			}
			if( a_parentFrame instanceof JFrame )
			{
				JFrame jframe = (JFrame) a_parentFrame;
				jframe.addWindowStateListener(this);
			}
		}
	}

	public void addMapResizeRelocateComponents( MapResizeRelocateComponentItem map )
	{
		if( _mapOfComponents != null )
			_mapOfComponents.addMapResizeRelocateComponents( map, getZoomFactor() );
	}

	public void removeResizeRelocateComponentItem( Component comp )
	{
		if( _mapOfComponents != null )
			_mapOfComponents.removeResizeRelocateComponentItem( comp );
	}

	@Override
	public ResizeRelocateItem getResizeRelocateComponentItem( Component comp )
	{
		ResizeRelocateItem result = null;
		if( _mapOfComponents != null )
			result = _mapOfComponents.getResizeRelocateItem( comp );

		return( result );
	}

	@Override
	public ResizeRelocateItem getResizeRelocateComponentItemOnTheFly( Component comp )
	{
		ResizeRelocateItem result = null;
		if( _mapOfComponents != null )
			result = _mapOfComponents.getResizeRelocateItemOnTheFly( comp );

		return( result );
	}

	public void prepareResizeOrRelocateToZoom( )
	{
		prepareResizeOrRelocateToZoom(_newZoomParam );
	}

	public void prepareResizeOrRelocateToZoom( ZoomParam zp )
	{
//		CursorFunctions.instance().storeCurrentCursor( a_parentFrame.getCursor() );

		prepareResizeOrRelocateToZoom( a_parentFrame, zp, false );

//		resizeOrRelocateComponent_simple(a_parentFrame, zp); // resize or relocate will be called enchained by resize listener.
	}

	public void prepareResizeOrRelocateToZoom( Component comp, ZoomParam zp, boolean onlyGetInfo )
	{
//		SwingUtilities.invokeLater( 
//			() -> prepareResizeOrRelocateToZoomInternal( comp, zp, onlyGetInfo ) );
		prepareResizeOrRelocateToZoomInternal( comp, zp, onlyGetInfo );
	}

	public void prepareResizeOrRelocateToZoomInternal( Component comp, ZoomParam zp, boolean onlyGetInfo )
	{
		pickPreviousDataOrResizeOrChangeZoomFactor( PICK_PREVIOUS_DATA_PROCEDURE, comp, zp, onlyGetInfo );
//		pickPreviousDataOrResizeOrChangeZoomFactor( RESIZE_OR_RELOCATE_PROCEDURE, comp, zp, onlyGetInfo );

		// invoke later as when window is resized, internal JPanels get coherence later.
		// do not change!!
		SwingUtilities.invokeLater(
			() -> pickPreviousDataOrResizeOrChangeZoomFactor( CHANGE_ZOOM_PROCEDURE, comp, zp, onlyGetInfo ) );
	}

	protected double getZoomFactor()
	{
		double result = 1.0D;
		if( _isInitialized )
			result = _newZoomParam.getZoomFactor();

		return( result );
	}

	public ZoomParam getZoomParam()
	{
		return( _newZoomParam );
	}

	protected boolean isContentPane( Component comp )
	{
		return( ContainerFunctions.instance().isContentPane(comp) );
	}

	protected void pickPreviousDataOrResizeOrChangeZoomFactor( RunResizeRelocateItemProcedure rrrip,
																Component comp, ZoomParam zp, boolean onlyGetInfo )
	{
		if( comp != null )
		{
			if( comp == a_parentFrame )
			{
				_newZoomParam = zp;
			}

			if( comp instanceof JTabbedPane )
			{
				int ii=0;
			}


			ResizeRelocateItem rri = getResizeRelocateComponentItem(comp);
			if( ( rri != null ) ||
				( comp instanceof JFrame ) ||
				( comp instanceof JRootPane ) ||
				( comp instanceof JLayeredPane ) ||
				( comp instanceof JPanel ) ||
				( comp instanceof JInternalFrame )  ||
				( comp instanceof Container ) )
			{
				Component parent = comp.getParent();
				if( ( comp instanceof JLayeredPane )  && !( comp instanceof JDesktopPane ) ||
					( comp instanceof JRootPane ) ||
					isContentPane( comp ) )
//					( comp instanceof JPanel ) && ( comp.getName() != null ) &&
//					( comp.getName().length() >= 5 ) && comp.getName().substring( 0, 5 ).equals( "null." ) )
				{
	/*
					if( parent != null )
					{
						SwingUtilities.invokeLater( () -> {
							if( onlyGetInfo )	insertComponentIntoResizingPanels( comp );
							else				setSizeForResizePanels( comp );
						} );
					}
	*/
				}
				else if( rri != null )
					rrrip.run( rri, zp );
	//				rri.execute( zp );

	/*
				if( comp instanceof JSplitPane	)
				{
					JSplitPane jsp = (JSplitPane) comp;
	//				resizeOrRelocateComponentsAtJSplitPane( jsp );
					int a=0;
				}
	*/
				if( comp instanceof JSplitPane	)
				{
					JSplitPane jsp = (JSplitPane) comp;
					SwingUtilities.invokeLater( () -> {
						if( jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
						{
							pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, jsp.getLeftComponent(), zp, onlyGetInfo );
							pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, jsp.getRightComponent(), zp, onlyGetInfo );
						}
						else if( jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
						{
							pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, jsp.getTopComponent(), zp, onlyGetInfo );
							pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, jsp.getBottomComponent(), zp, onlyGetInfo );
						}
					} );
				}
				else if( comp instanceof JScrollPane )
				{
					JScrollPane sp = (JScrollPane) comp;
					pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, sp.getViewport(), zp, onlyGetInfo );
				}

				if( comp instanceof JTextComponent )
				{
					int ii=0;
				}

				// we browse all popup menus (at least the ones of JTextComponents that allow undo/redo, copy/paste).
				if( comp instanceof JComponent )
				{
					JPopupMenu jppm = getNonInheritedPopupMenu( (JComponent) comp );
					if( jppm != null )
					{
						pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, jppm, zp, onlyGetInfo );
					}
				}

				if( comp instanceof JComboBox )
				{
					JComboBox combo = (JComboBox) comp;
					BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

					pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, popup, zp, onlyGetInfo );
				}

				if( comp instanceof JTabbedPane )
				{
					JTabbedPane tabbedPane = (JTabbedPane) comp;
					for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
					{
						pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, tabbedPane.getComponentAt(ii), zp, onlyGetInfo );
					}
				}
				else if( ( comp instanceof Container ) &&
						!( comp instanceof JSplitPane ) )
				{
					Container contnr = (Container) comp;

					if( comp instanceof JMenu )
						pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, ( ( JMenu ) comp ).getPopupMenu(), zp, onlyGetInfo );

					for( int ii=0; ii<contnr.getComponentCount(); ii++ )
					{
						pickPreviousDataOrResizeOrChangeZoomFactor( rrrip, contnr.getComponent(ii), zp, onlyGetInfo );
					}
				}
			}

			if( comp == a_parentFrame )
			{
	//			_previousZoomParam = zp;
				_previousZoomFactor = zp.getZoomFactor();
			}
		}
	}
/*
	protected void resizeComponentRecursive( Component comp ) throws InternException
	{
		// to resize before setting the initialstate of JSplitPanes.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		ResizeRelocateItem rri = a_mapResizeRelocateComponents.get(comp);
		if( ( rri != null ) ||
			( comp instanceof JFrame ) ||
			( comp instanceof JRootPane ) ||
			( comp instanceof JLayeredPane ) ||
			( comp instanceof JPanel ) ||
			( comp instanceof JInternalFrame )  ||
			( comp instanceof Container ) )
		{
			if( ( comp instanceof JLayeredPane ) || ( comp instanceof JRootPane ) ||
				( comp instanceof JPanel ) && ( comp.getName() != null ) &&
				( comp.getName().length() >= 5 ) &&
				comp.getName().substring( 0, 5 ).equals( "null." ) )
			{
				Component parent = comp.getParent();
				if( parent != null )
				{
					setSizeForResizePanels( comp );
				}
			}
			else if( rri != null )
				rri.execute();
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		if( comp instanceof JDesktopPane )
		{
		}
		else if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				resizeComponentRecursive( contnr.getComponent(ii) );
			}
		}
	}
*/
/*
	protected void resizeChangingPreferredSize( Component  comp )
	{
		if( comp != null )
		{
			ResizeRelocateItem rri = a_mapResizeRelocateComponents.get( comp );

			if( rri != null )
			{
				rri.execute();
				comp.setPreferredSize(comp.getSize() );
			}
		}
	}

	protected void resizeOrRelocateComponentsAtJSplitPane( JSplitPane jsp )
	{
		if( jsp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
		{
			resizeChangingPreferredSize( jsp.getLeftComponent() );
			resizeChangingPreferredSize( jsp.getRightComponent() );
		}
		else if( jsp.getOrientation() == JSplitPane.VERTICAL_SPLIT )
		{
			resizeChangingPreferredSize( jsp.getTopComponent() );
			resizeChangingPreferredSize( jsp.getBottomComponent() );
		}
		jsp.resetToPreferredSizes();
	}
*/	
	protected void resizeOrRelocateComponent_simple( Component parent, ZoomParam zp )
	{
		ResizeRelocateItem rri = null;

		if( parent instanceof JSplitPane	)
		{
			rri = _mapOfComponents.getResizeRelocateItem(parent);
			if( rri != null ) rri.execute( zp );

//			JSplitPane jsp = (JSplitPane) parent;
//			resizeOrRelocateComponentsAtJSplitPane( jsp );
/*
			for( int ii=0; ii<jsp.getComponentCount(); ii++ )
			{
				resizeOrRelocateComponent_simple( jsp.getComponent(ii) );
			}
*/
		}

		if( parent instanceof JTabbedPane )
		{
			JTabbedPane tabbedPane = (JTabbedPane) parent;
			for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
			{
				rri = _mapOfComponents.getResizeRelocateItem(tabbedPane.getComponentAt(ii));
				if( rri != null ) rri.execute( zp );
//				resizeOrRelocateComponent_simple( contnr.getComponent(ii) );
			}

//			parent.repaint();
		}
		else if( parent instanceof Container )
		{
			Container contnr = (Container) parent;

			if( contnr instanceof JMenu )
			{
				rri = _mapOfComponents.getResizeRelocateItem(( ( JMenu ) contnr ).getPopupMenu() );
				if( rri != null ) rri.execute( zp );
			}

			for( int ii=0; ii<contnr.getComponentCount(); ii++ )
			{
				rri = _mapOfComponents.getResizeRelocateItem(contnr.getComponent(ii));
				if( rri != null ) rri.execute( zp );
//				resizeOrRelocateComponent_simple( contnr.getComponent(ii) );
			}

//			parent.repaint();
		}
	}

	protected void insertComponentIntoResizingPanels( Component comp )
	{
		_mapOfComponents.setInfoForResizingPanels(comp, new InfoForResizingPanels( comp ) );
	}
	
	protected void setSizeForResizePanels( Component comp )
	{
		if( _mapOfComponents != null )
		{
			InfoForResizingPanels ifrp = _mapOfComponents.getInfoForResizingPanels(comp );
			if( ifrp != null )	ifrp.resize();
		}
	}

	@Override
	public void focusGained(FocusEvent fe)
	{
		if( a_parentFrame instanceof InternationalizedWindow )
		{
			InternationalizedWindow ijf = (InternationalizedWindow) a_parentFrame;
			if( ijf.getAlwaysHighlightFocus() &&
				( getFocusedComponent() != a_parentFrame.getParent() ) )
			{
				a_parentFrame.repaint();
			}
		}
	}

	@Override
	public void focusLost(FocusEvent fe)
	{
		if( a_parentFrame instanceof InternationalizedWindow )
		{
			InternationalizedWindow ijf = (InternationalizedWindow) a_parentFrame;
			if( ijf.hasToClearMarkedComponent() )
				a_parentFrame.repaint();
		}
	}
/*
	protected class InfoForResizingPanels
	{
		protected int _widthDifference = -1;
		protected int _heightDifference = -1;
		protected Component _component = null;
		
		public InfoForResizingPanels( Component comp )
		{
			_component = comp;
			Component parent = comp.getParent();
			if( parent != null )
			{
				_widthDifference = parent.getWidth() - comp.getWidth();
				_heightDifference = parent.getHeight() - comp.getHeight();
			}
		}
		
		public void resize()
		{
			Component parent = _component.getParent();
			if( parent != null )
			{
				_component.setSize( parent.getWidth() - _widthDifference, parent.getHeight() - _heightDifference);
			}
		}
	}
*/

	
	// the Map returned will contain the original cursors in every component.
	// so the change of the cursor can be reverted in the future.
	public Map< Component, Cursor > M_changeCursor( Cursor cursor )
	{
		Map< Component, Cursor > result = new Hashtable< Component, Cursor >();
		
		M_changeCursor( a_parentFrame, cursor, result );
		
		return( result );
	}

	protected static void M_changeCursor( Component comp, Cursor cursor, Map< Component, Cursor > map )
	{
		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;

			if( contnr instanceof JTabbedPane )
			{
				JTabbedPane tabbedPane = (JTabbedPane) contnr;
				for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
				{
					M_changeCursor( tabbedPane.getComponentAt(ii), cursor, map );
				}
			}
			else if( comp instanceof JComboBox )
			{
				JComboBox combo = (JComboBox) comp;
				BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

				M_changeCursor( popup, cursor, map );
			}
			else
			{
				if( contnr instanceof JMenu )
				{
					M_changeCursor(( ( JMenu ) contnr ).getPopupMenu(), cursor, map  );
				}

				for( int ii=0; ii<contnr.getComponentCount(); ii++ )
				{
					M_changeCursor( contnr.getComponent(ii), cursor, map );
				}
			}

			if( contnr instanceof JMenu )
			{
				JMenu jmnu = (JMenu) contnr;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_changeCursor( jmnu.getMenuComponent( ii ), cursor, map );
			}
		}
		Cursor c2 = map.get(comp);
		if( c2 == null )
		{
			map.put(comp, comp.getCursor() );
			comp.setCursor(cursor);
		}
	}

	public void M_rollbackChangeCursor( Map< Component, Cursor > rollbackMap )
	{
		if( rollbackMap == null ) rollbackMap = new Hashtable<Component, Cursor>();
		Cursor defaultCursor = CursorFunctions._defaultCursor;

		M_rollbackChangeCursor(a_parentFrame, rollbackMap, defaultCursor );
	}

	protected static void M_rollbackChangeCursor( Component comp, Map< Component, Cursor > rollbackMap, Cursor defaultCursor )
	{
		Cursor cursor = rollbackMap.get( comp );
		if( cursor == null )	cursor = defaultCursor;
		comp.setCursor(cursor);
		
		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;

			if( contnr instanceof JTabbedPane )
			{
				JTabbedPane tabbedPane = (JTabbedPane) contnr;
				for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
				{
					M_rollbackChangeCursor( tabbedPane.getComponentAt(ii), rollbackMap, defaultCursor );
				}
			}
			else if( comp instanceof JComboBox )
			{
				JComboBox combo = (JComboBox) comp;
				BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

				M_rollbackChangeCursor( popup, rollbackMap, defaultCursor );
			}
			else
			{
				if( contnr instanceof JMenu )
				{
					M_rollbackChangeCursor(( ( JMenu ) contnr ).getPopupMenu(), rollbackMap, defaultCursor  );
				}

				for( int ii=0; ii<contnr.getComponentCount(); ii++ )
				{
					M_rollbackChangeCursor(contnr.getComponent(ii), rollbackMap, defaultCursor );
				}
			}

			if( contnr instanceof JMenu )
			{
				JMenu jmnu = (JMenu) contnr;
				for( int ii=0; ii<jmnu.getMenuComponentCount(); ii++ )
					M_rollbackChangeCursor(jmnu.getMenuComponent( ii ), rollbackMap, defaultCursor );
			}
		}
	}

	public static Component getFocusedComponent()
	{
		return( ComponentFunctions.instance().getFocusedComponent() );
	}

	public void setFocus( Component comp, boolean showFocus )
	{
		if( (comp != null) && ( a_parentFrame instanceof Container ) )
		{
			Container cont = (Container) a_parentFrame;

			if( cont.isAncestorOf(comp) )
			{
				Component current = comp;
				Component parent = comp.getParent();
				while( parent != null )
				{
					if( parent instanceof JTabbedPane )
					{
						JTabbedPane jtb = (JTabbedPane) parent;
						jtb.setSelectedComponent(current);
					}

					current = parent;
					parent = parent.getParent();
				}

				comp.requestFocus();
				if( showFocus && (a_parentFrame instanceof InternationalizedWindow ) )
				{
					java.awt.EventQueue.invokeLater(new Runnable(){
						public void run()
						{
							InternationalizedWindow iw = (InternationalizedWindow) a_parentFrame;
							iw.setAlwaysHighlightFocus(true);
						}
					});
				}
			}
		}
	}

	@Override
	public void registerInternationalString( String label, String defaultValue )
	{
		a_formLanguageConfiguration.registerInternationalString(label, defaultValue);
	}

	@Override
	public String getInternationalString( String label )
	{
		String result = null;
		if( a_formLanguageConfiguration != null )
			result = a_formLanguageConfiguration.getInternationalString(label);

		return( result );
	}

	protected void createComplementsForTextComponent( JTextComponent textComp )
	{
		createComplementsForTextComponent( textComp, false );
	}

	protected void createComplementsForTextComponent( JTextComponent textComp, boolean isOnTheFly )
	{
		textComp.getCaret().setBlinkRate( 125 );
		
		if( _enableUndoRedoForTextComponents || _enableTextPopupMenu )
		{
			DesktopViewTextComponent tvc = DesktopGenericFunctions.instance().getViewFacilities().createTextViewComponent(textComp);
			TextCompPopupManager tcompMan = new TextCompPopupManager( tvc, null );
			_mapOfComponents.setTextCompPopupManager( textComp, tcompMan, isOnTheFly);

			if( _enableTextPopupMenu )
			{
				tcompMan.startPopupMenu();
			}

			if( _enableUndoRedoForTextComponents  && !( textComp instanceof JPasswordField ))
			{
				tcompMan.startUndoRedoManager();
			}
		}
	}

	protected void createTextPopupManagers( Component comp )
	{
		createTextPopupManagers( comp, false );
	}

	protected void createTextPopupManagers( Component comp, boolean isOnTheFly )
	{
		if( ( comp instanceof JFrame ) ||
			( comp instanceof JRootPane ) ||
			( comp instanceof JLayeredPane ) ||
			( comp instanceof JPanel ) ||
			( comp instanceof JInternalFrame )  ||
			( comp instanceof Container ) )
		{
			if( comp instanceof JTextComponent )
			{
				JTextComponent tc = (JTextComponent) comp;
				createComplementsForTextComponent( tc, isOnTheFly );
				}

			if( comp instanceof JTabbedPane )
			{
				JTabbedPane tabbedPane = (JTabbedPane) comp;

				for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
					createTextPopupManagers( tabbedPane.getComponentAt(ii), isOnTheFly );
			}
			else if( comp instanceof Container )
			{
				Container cont = (Container) comp;

				if( cont instanceof JMenu )
				{
					createTextPopupManagers(( ( JMenu ) cont ).getPopupMenu(), isOnTheFly );
				}

				for( int ii=0; ii<cont.getComponentCount(); ii++ )
					createTextPopupManagers( cont.getComponent(ii), isOnTheFly );
			}
		}
	}

/*
	public static void setInitialSizesForContentPanes( Component comp )
	{
		if( ( comp instanceof JFrame ) ||
			( comp instanceof JRootPane ) ||
			( comp instanceof JLayeredPane ) ||
			( comp instanceof JPanel ) ||
			( comp instanceof JInternalFrame )  ||
			( comp instanceof Container ) )
		{
			if( ( comp instanceof JLayeredPane ) || ( comp instanceof JRootPane ) ||
				( comp instanceof JPanel ) && ( comp.getName() != null ) &&
				( comp.getName().length() >= 5 ) &&
				comp.getName().substring( 0, 5 ).equals( "null." ) )
			{
				Component parent = comp.getParent();
				if( parent != null )
				{
					comp.setSize( parent.getSize() );
				}
			}
			
			if( comp instanceof Container )
			{
				Container contentPane = (Container) comp;
				
				for( int ii=0; ii<cont.getComponentCount(); ii++ )
					setInitialSizesForContentPanes( contentPane.getComponent(ii) );
			}
		}
	}
*/

	protected void autoResizeFrameDesdendantPanels( Component comp, Dimension differenceResizingFrame )
	{
		if( ( comp instanceof JFrame ) ||
			( comp instanceof JRootPane ) ||
			( comp instanceof JLayeredPane ) ||
			( comp instanceof JPanel ) ||
			( comp instanceof JInternalFrame )  ||
			( comp instanceof Container ) )
		{
			Component parent = comp.getParent();
			if( ( comp instanceof JLayeredPane ) &&
				!( comp instanceof JDesktopPane ) || ( comp instanceof JRootPane ) ||
				( comp instanceof JPanel ) && ( comp.getName() != null ) &&
				( comp.getName().length() >= 5 ) && comp.getName().substring( 0, 5 ).equals( "null." ) )
			{
				if( parent != null )
				{
					Dimension newSize = new Dimension( (int) ( comp.getWidth() + differenceResizingFrame.getWidth() ) ,
									(int) ( comp.getHeight() + differenceResizingFrame.getHeight() ) );
					comp.setSize( newSize );

					Dimension oldPreferredSize = comp.getPreferredSize();
					if( oldPreferredSize != null )
					{
						Dimension newPreferredSize = new Dimension( (int) ( oldPreferredSize.getWidth() + differenceResizingFrame.getWidth() ) ,
										(int) ( oldPreferredSize.getHeight() + differenceResizingFrame.getHeight() ) );
						comp.setPreferredSize( newPreferredSize );
					}
				}
			}

			if( comp instanceof JTabbedPane )
			{
				JTabbedPane tabbedPane = (JTabbedPane) comp;
				for( int ii=0; ii<tabbedPane.getTabCount(); ii++ )
				{
					autoResizeFrameDesdendantPanels( tabbedPane.getComponentAt(ii), differenceResizingFrame );
				}
			}
			else if( comp instanceof JComboBox )
			{
				JComboBox combo = (JComboBox) comp;
				BasicComboPopup popup = (BasicComboPopup) combo.getUI().getAccessibleChild(combo, 0);

				autoResizeFrameDesdendantPanels( popup, differenceResizingFrame );
			}
			else if( comp instanceof Container )
			{
				Container contnr = (Container) comp;

				if( contnr instanceof JMenu )
				{
					autoResizeFrameDesdendantPanels(( ( JMenu ) contnr ).getPopupMenu(), differenceResizingFrame );
				}

				for( int ii=0; ii<contnr.getComponentCount(); ii++ )
				{
					autoResizeFrameDesdendantPanels( contnr.getComponent(ii), differenceResizingFrame );
				}
			}
		}
	}

	public void setMinimumPreferredSize( Component comp, Dimension minimumPreferredSize )
	{
		ResizeRelocateItem rri = getResizeRelocateComponentItem(comp);
		if( rri != null )
			rri.setMinimumPreferredSize(minimumPreferredSize);
	}

	@Override
	public String createCustomInternationalString( String label, Object ... args )
	{
		String result = null;
		if( a_formLanguageConfiguration != null )
			result = a_formLanguageConfiguration.createCustomInternationalString(label, args);

		return( result );
	}
/*
	@Override
	public void setIsMainMouseButtonClicked(boolean value)
	{
		_isMainMouseButtonClicked = value;
	}

	@Override
	public boolean isMainMouseButtonClicked()
	{
		return( _isMainMouseButtonClicked );
	}
*/
	protected boolean isMouseInPositionToResizeFrame()
	{
		boolean result = false;

		Rectangle frameBounds = a_parentFrame.getBounds();
		Point mousePosition = MouseFunctions.getMouseLocation();
		
		result = ViewFunctions.instance().isAtBorder( frameBounds, mousePosition, 5 );

		return( result );
	}

	@Override
	public boolean isResizeDragging()
	{
		return( isMouseInPositionToResizeFrame() && MouseFunctions.isMainButtonPressed() );
	}
/*
	protected void invokeLaterPurge( String functionName, Runnable runnable )
	{
		_invokeLaterPurge.invokeLater( functionName, runnable);
	}
*/
	@Override
	public void executeResizeRelocateItemRecursive(Component comp)
	{
		SwingUtilities.invokeLater( () ->
			executeResizeRelocateItemRecursiveInternal(comp)
		);
//		invokeLaterPurge( "executeResizeRelocateItemRecursive",
//			() -> executeResizeRelocateItemRecursiveInternal(comp) );
	}

	public void executeResizeRelocateItemRecursiveInternal(Component comp)
	{
		pickPreviousDataOrResizeOrChangeZoomFactor(RESIZE_OR_RELOCATE_PROCEDURE,
												comp,
												_newZoomParam, false );
	}

	public void executeResizeRelocate(Component comp, boolean forceExecution)
	{
		ResizeRelocateItem rri = getResizeRelocateComponentItem(comp);
		if( rri != null )
		{
			rri.setForceExecution( forceExecution );
			rri.execute( getZoomParam() );
		}
	}

	public List<ResizeRelocateItem> getListOfResizeRelocateItems()
	{
		return( _mapOfComponents.getListOfResizeRelocateItems() );
	}

	public MapOfComponents getMapOfComponents()
	{
		return( _mapOfComponents );
	}

	@Override
	public List<JPopupMenu> getPopupMenus() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void addPopupMenu(JPopupMenu jPopupMenu) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


	protected class SetComponentListenerThread extends Thread
	{
		protected JFrameInternationalization _this = null;
		protected Component _component;

		public SetComponentListenerThread( Component comp, JFrameInternationalization jfi )
		{
			_component = comp;
			_this = jfi;
		}

		@Override
		public void run()
		{
			_component.addComponentListener( _this );
		}
	}

	protected class GetFrameBorders implements GetResultEDT<Insets>
	{
		@Override
		public Insets getResultEDT()
		{
			return( getFrameBorders_internal() );
		}
	}

	protected class AdjustFrameSize implements GetResultEDT<Object>
	{
		protected boolean _setMinSize;
		
		public AdjustFrameSize( boolean setMinSize )
		{
			_setMinSize = setMinSize;
		}

		@Override
		public Object getResultEDT()
		{
			resizeFrameToContentsEDT(_setMinSize);
			return( null );
		}
	}

	protected static void registerInternationalString_own(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	protected static String getInternationalString_own(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	protected interface RunResizeRelocateItemProcedure
	{
		public void run( ResizeRelocateItem rri, ZoomParam zp );
	}

	@Override
	public boolean isResizeRelocateItemsResizeListenersBlocked()
	{
		return( _isResizeRelocateItemsResizeListenersBlocked );
	}

	public ColorThemeChangeableStatus getColorThemeChangeable( Component comp )
	{
		return( _mapOfComponents.getOrCreate(comp).getColorThemeChangeableStatus() );
	}

	public ColorThemeChangeableStatus getColorThemeChangeableOnTheFly( Component comp )
	{
		return( _mapOfComponents.getOrCreateOnTheFly(comp).getColorThemeChangeableStatus() );
	}
}
