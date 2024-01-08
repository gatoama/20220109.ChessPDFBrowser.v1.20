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
package com.frojasg1.applications.common.components.internationalization.window;

import com.frojasg1.applications.common.components.internationalization.ExtendedZoomSemaphore;
import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.internationalization.window.result.ZoomComponentOnTheFlyResult;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageServerInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterListener;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterObserved;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.screen.ScreenFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.general.view.ViewComponent;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import com.frojasg1.general.ExecutionFunctions.UnsafeMethod;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.desktop.copypastepopup.TextCompPopupManager;
import com.frojasg1.general.desktop.startapp.GenericDesktopInitContext;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeChangeableBaseBuilder;
import com.frojasg1.general.desktop.view.color.factory.impl.ColorInversorFactoryImpl;
import com.frojasg1.general.desktop.view.color.impl.ColorThemeChangeableBase;
import com.frojasg1.general.desktop.view.scrollpane.ScrollPaneMouseListener;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import com.frojasg1.general.desktop.view.zoom.mapper.ContainerOfInternallyMappedComponentBase;
import com.frojasg1.general.update.Updateable;
import com.frojasg1.general.executor.worker.PullOfExecutorWorkers;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.threads.ThreadFunctions;
import com.frojasg1.general.zoom.ZoomParam;
import java.awt.Container;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JScrollPane;

/**
 *
 * @author Usuario
 */
public abstract class InternationalizedJDialog< CC extends GenericDesktopInitContext > extends javax.swing.JDialog
										implements InternationalizedWindow<CC>,
													ChangeLanguageClientInterface,
													ChangeLanguageServerInterface,
													ComponentListener,
													FocusListener,
													WindowStateListener,
													InternallyMappedComponent,
													ComponentMapper,
													GenericValidator
{

	protected static final int DEFAULT_NUMBER_OF_WORKERS_FOR_PULL = 1;

	protected static final String CONF_VALIDATION_ERROR = "VALIDATION_ERROR";

	protected JFrameInternationalization a_intern = null;

	protected BaseApplicationConfigurationInterface _appliConf = null;

//	protected boolean _previousValueOfHightlightFocus = false;
	protected boolean _alwaysHighlightFocus = false;

	protected Component _lastFocusedComponentDrawn = null;
	
	protected Component _highlightFocusComponent = null;

	protected LinkedList< Map<Component, Cursor> > _rollbackWaitCursorMapList = new LinkedList<>();

	protected Object _synchronizedLockForPaint = new Object();

	protected BufferedImage _overlappedImage = null;
	protected Point _overlappedImageLocation = null;

	protected Component _componentToHighLight = null;

	protected boolean _hasToClearMarkedComponent = false;

	protected ChangeZoomFactorServerInterface _zoomFactorServer = null;

	protected double _newZoomFactor = 1.0D;
	protected double _previousZoomFactor = 1.0D;

	protected CC _applicationContext = null;

	protected ComponentMapper _compMapper = null;

	protected volatile boolean _alreadyInitialized = false;

	protected boolean _hasBeenSuccessfullyValidated = false;

	protected Consumer<InternationalizationInitializationEndCallback> _initializationEndCallBack = null;

	protected int _hundredPerCentMinimumWidth = -1;

	protected volatile boolean _preventFromRepainting = false;
	protected Object _initializedLock = new Object();
	protected volatile boolean _isVisible = false;
	protected volatile boolean _isInitialized = false;

	protected PullOfExecutorWorkers _pullOfWorkers;

	protected List<ExtendedZoomSemaphore> _listOfZoomSemaphoresOnTheFly = new ArrayList<>();

	protected BufferedImage _lastWindowImage = null;
	protected double _zoomFactorOfLastWindowImage = 1.0D;

	protected ColorThemeChangeableBase _colorThemeStatus;
	protected ConfigurationParameterListener _colorThemeChangeListener;

	protected ColorInversor _colorInversor;

	protected boolean _alreadyMapped = false;

//	protected ComponentListener _contentPaneListener;

	protected ContainerOfInternallyMappedComponentBase _containerOfInternallyMappedComponents =
		new ContainerOfInternallyMappedComponentBase();

	/**
	 * Creates new form InternationalizedJDialog
	 */
	public InternationalizedJDialog(java.awt.Frame parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration )
	{
		this(parent, modal, applicationConfiguration, true);
	}

	public InternationalizedJDialog(java.awt.Frame parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									boolean initialPreventFromRepainting )
	{
		this(parent, modal, applicationConfiguration, null, initialPreventFromRepainting);
	}

	public InternationalizedJDialog(java.awt.Frame parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									CC applicationContext,
									boolean initialPreventFromRepainting )
	{
		this(parent, modal, applicationConfiguration, applicationContext, null,
			initialPreventFromRepainting);
	}

	public InternationalizedJDialog(java.awt.Frame parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									CC applicationContext,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									boolean initialPreventFromRepainting )
	{
		this(parent, modal, applicationConfiguration, applicationContext,
			initializationEndCallBack, true, initialPreventFromRepainting );
	}

	public InternationalizedJDialog(java.awt.Frame parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									CC applicationContext,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									boolean doInitComponents,
									boolean initialPreventFromRepainting )
	{
		super(parent, modal);

		this.setPreventFromRepainting(initialPreventFromRepainting);

		_colorThemeStatus = createColorThemeChangeableBase();

		if( doInitComponents )
			initComponents();

		setAppliConf( applicationConfiguration );

		_previousZoomFactor = applicationConfiguration.getZoomFactor();
		_applicationContext = applicationContext;

		setInternationalizationEndCallBack( initializationEndCallBack );

		_pullOfWorkers = createPullOfWorkers();
	}

	/**
	 * Creates new form InternationalizedJDialog
	 */
	public InternationalizedJDialog(javax.swing.JDialog parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration )
	{
		this(parent, modal, applicationConfiguration, true);
	}

	public InternationalizedJDialog(javax.swing.JDialog parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									boolean initialPreventFromRepainting )
	{
		this(parent, modal, applicationConfiguration, null, initialPreventFromRepainting);
	}

	public InternationalizedJDialog(javax.swing.JDialog parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									CC applicationContext,
									boolean initialPreventFromRepainting )
	{
		this(parent, modal, applicationConfiguration, applicationContext, null,
			initialPreventFromRepainting);
	}

	public InternationalizedJDialog(javax.swing.JDialog parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									CC applicationContext,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									boolean initialPreventFromRepainting )
	{
		this(parent, modal, applicationConfiguration, applicationContext,
			initializationEndCallBack, true, initialPreventFromRepainting);
	}

	public InternationalizedJDialog(javax.swing.JDialog parent, boolean modal,
									BaseApplicationConfigurationInterface applicationConfiguration,
									CC applicationContext,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack,
									boolean doInitComponents,
									boolean initialPreventFromRepainting )
	{
		super(parent, modal);

		this.setPreventFromRepainting(initialPreventFromRepainting);

		_colorThemeStatus = createColorThemeChangeableBase();

		if( doInitComponents )
			initComponents();

		setAppliConf( applicationConfiguration );

		_previousZoomFactor = applicationConfiguration.getZoomFactor();
		_applicationContext = applicationContext;

		setInternationalizationEndCallBack( initializationEndCallBack );

		_pullOfWorkers = createPullOfWorkers();
	}

	public <CC, RR> RR getIfNotNull( CC obj, Function<CC,RR> getter, RR defaultValue )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter, defaultValue) );
	}

	protected <CC, RR> RR getIfNotNull( CC obj, Function<CC, RR> getter )
	{
		return( NullFunctions.instance().getIfNotNull(obj, getter) );
	}

	protected ColorThemeChangeableBase createColorThemeChangeableBase()
	{
		return( ColorThemeChangeableBaseBuilder.instance().createColorThemeChangeableBase() );
	}

	protected int getNumberOfWorkersForPull()
	{
		return DEFAULT_NUMBER_OF_WORKERS_FOR_PULL;
	}

	protected PullOfExecutorWorkers createPullOfWorkers()
	{
		PullOfExecutorWorkers result = new PullOfExecutorWorkers( getClass().getName() );
		result.init(getNumberOfWorkersForPull());
		result.start();

		return( result );
	}

	public void setInternationalizationEndCallBack( Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		_initializationEndCallBack = initializationEndCallBack;
	}

	@Override
	public final void setAppliConf( BaseApplicationConfigurationInterface applicationConfiguration )
	{
		unregisterFromChangeLanguageAsObserver();

		_appliConf = applicationConfiguration;

		if( getAppliConf() != null )
		{
			getAppliConf().registerChangeLanguageObserver( this );
			registerToChangeZoomFactorAsObserver( getAppliConf() );
			registerToChangeColorThemeAsObserver( getAppliConf() );

			String resourceForIcon = getAppliConf().getResourceNameForApplicationIcon();
			if( resourceForIcon != null )
				setIcons( resourceForIcon );

			_newZoomFactor = _appliConf.getZoomFactor();
		}
	}

/*
	protected ComponentListener createContentPaneListener()
	{
		return( new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				onContentPaneResized();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
			
		});
	}

	protected void addContentPaneComponentListener()
	{
		if( _contentPaneListener == null )
			_contentPaneListener = createContentPaneListener();

		if( _contentPaneListener != null )
			getContentPane().addComponentListener(_contentPaneListener);
	}
*/
	protected void addListenersRoot()
	{
//		addContentPaneComponentListener();
		addComponentListener(this);
		addWindowStateListener(this);

		ComponentFunctions.instance().browseComponentHierarchy( this, (comp) ->
		{
			if( ( comp != this ) && ( comp.getParent() != null ) )
				comp.addFocusListener(this);
			return( null );
		} );
	}
/*
	protected void removeContentPaneComponentListener()
	{
		if( _contentPaneListener != null )
			getContentPane().removeComponentListener(_contentPaneListener);
	}
*/
	protected void removeListenersRoot()
	{
//		removeContentPaneComponentListener();
		removeComponentListener(this);
		removeWindowStateListener(this);

		ComponentFunctions.instance().browseComponentHierarchy( this, (comp) ->
		{
			if( ( comp != this ) && ( comp.getParent() != null ) )
				comp.removeFocusListener(this);
			return( null );
		} );
	}


	protected void createInternationalization(	String mainFolder,
												String applicationName,
												String group,
												String paquetePropertiesIdiomas,
												String configurationBaseFileName,
												Component parentFrame,
												Component parentParentFrame,
												Vector<JPopupMenu> vPUMenus,
												boolean hasToPutWindowPosition,
												MapResizeRelocateComponentItem mapRRCI
											)
	{
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		double zoomFactor = getAppliConf().getZoomFactor();
		boolean activateUndoRedoForTextComponents = true;
		boolean enableTextPopupMenu = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = null;

		createInternationalization( mainFolder, applicationName, group,
									paquetePropertiesIdiomas, configurationBaseFileName,
									parentFrame, parentParentFrame,
									vPUMenus, hasToPutWindowPosition,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									zoomFactor,
									activateUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);
	}

	protected void createInternationalization(	String mainFolder,
												String applicationName,
												String group,
												String paquetePropertiesIdiomas,
												String configurationBaseFileName,
												Component parentFrame,
												Component parentParentFrame,
												Vector<JPopupMenu> vPUMenus,
												boolean hasToPutWindowPosition,
												MapResizeRelocateComponentItem mapRRCI,
												boolean adjustSizeOfFrameToContents,
												boolean adjustMinSizeOfFrameToContents,
												double zoomFactor,
												boolean activateUndoRedoForTextComponents,
												boolean enableTextPopupMenu,
												boolean switchToZoomComponents,
												boolean internationalizeFont,
												Integer delayToInvokeCallback
											)
	{
/*
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jSPmainSplit, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jPanelNavigatorContainer1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jSPnavigatorSplit, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( _navigatorPanel, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jScrollPane2, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jTextPane1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jTListOfGames, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jPanelChessBoardContainer, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( _chessBoardPanel, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
*/
/*
		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PATH_PROPERTIES_IN_JAR,
													a_configurationBaseFileName,
													this,
													parent,
													a_vectorJpopupMenus,
													false,
													mapRRCI );
*/
		a_intern = new JFrameInternationalization( getAppliConf() );

		a_intern.setInitializationEndCallBack(_initializationEndCallBack);
		if( delayToInvokeCallback != null )
			a_intern.setDelayToInvokeCallback( delayToInvokeCallback );

		preInternationalizationInit();

		Vector<JPopupMenu> popupMenus = new Vector<>( addNotPopUpMenusNotPresent( vPUMenus, mapRRCI ) );

		a_intern.initialize(	mainFolder,
								applicationName,
								group,
								paquetePropertiesIdiomas,
								configurationBaseFileName,
								parentFrame,
								parentParentFrame,
								popupMenus,
								hasToPutWindowPosition,
								mapRRCI,
								adjustSizeOfFrameToContents,
								adjustMinSizeOfFrameToContents,
								zoomFactor,
								activateUndoRedoForTextComponents,
								enableTextPopupMenu,
								switchToZoomComponents,
								internationalizeFont );

		a_intern.registerInternationalString( CONF_VALIDATION_ERROR, "VALIDATION ERROR" );

/*
		try
		{
			changeFontSize( getAppliConf().getFontSizeFactor() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
*/
	}

	protected List<JPopupMenu> addNotPopUpMenusNotPresent( Vector<JPopupMenu> vPUMenus,
													MapResizeRelocateComponentItem mapRRCI )
	{
		List<JPopupMenu> result = addNotPopUpMenusNotPresent(vPUMenus);
		if( mapRRCI != null )
			result = addNotPopUpMenusNotPresent(mapRRCI.getPopupMenus());
		
		return( result );
	}

	// to keep backwards compatibility
	protected List<JPopupMenu> addNotPopUpMenusNotPresent( Collection<JPopupMenu> vPUMenus )
	{
		List<JPopupMenu> popupMenuList = _containerOfInternallyMappedComponents.getPopupMenuList();
		if( vPUMenus != null )
		{
			for( JPopupMenu jPopupMenu: vPUMenus )
				if( !popupMenuList.contains( jPopupMenu ) )
					addPopupMenu( jPopupMenu );
		}

		return( popupMenuList );
	}

	@Override
	public void addPopupMenu( JPopupMenu jPopupMenu )
	{
		_containerOfInternallyMappedComponents.addPopupMenu( jPopupMenu );
	}

	protected void preInternationalizationInit()
	{
		// derived classes can override it
	}

	@Override
	public JFrameInternationalization getInternationalization()
	{
		return( a_intern );
	}

	@Override
	public Object getSynchronizedLockForPaint()
	{
		return( _synchronizedLockForPaint );
	}

	@Override
	public boolean getAlreadyInitializedCallback()
	{
		return( _alreadyInitialized );
	}

	@Override
	public void setAlreadyInitializedAfterCallback()
	{
		_alreadyInitialized = true;
	}

	protected void setIcons( String resourceToImage )
	{
//		BufferedImage image = DesktopResourceFunctions.instance().loadResourceImage(resourceToImage);
//		if( image != null ) setIconImage( image );

		List<BufferedImage> list = ImageFunctions.instance().getListOfIcons(resourceToImage);
		setIconImages( list );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		formWindowClosingEvent( );
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

	protected void onContentPaneResized()
	{
		if( a_intern != null )
		{
			try
			{
////				a_intern.saveGeneralConfiguration();
//				prepareResizeOrRelocateToZoomInvokeLater();
//				SwingUtilities.invokeLater( this::executeResizeRelocateItemRecursiveInternal );
				executeResizeRelocateItemRecursive();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	protected void executeResizeRelocateItemRecursive()
	{
		if( a_intern != null )
			a_intern.executeResizeRelocateItemRecursive(this);
	}

	@Override
	public void unregisterFromChangeLanguageAsObserver()
	{
		if( getAppliConf() != null )
			getAppliConf().unregisterChangeLanguageObserver(this);
	}
	
	protected void releasePullOfWorkers()
	{
		if( _pullOfWorkers != null )
			_pullOfWorkers.hasToStop();
	}

	@Override
	public void releaseResources()
	{
		releasePullOfWorkers();

		unregisterFromChangeLanguageAsObserver();
		unregisterFromChangeZoomFactorAsObserver();

		ComponentFunctions.instance().browseComponentHierarchy( this.getContentPane(), comp -> { ComponentFunctions.instance().releaseResources(comp); return( null ); } );

		if( a_intern != null )
			a_intern.releaseResources();

		a_intern=null;	// for the garbage collector to free the memory of the internationallization object and after the memory of this form
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		if( a_intern != null )
		{
//			a_intern.saveLanguageConfiguration();
			a_intern.changeLanguage( language );
		}

		SwingUtilities.invokeLater( this::updateUpdateable );

		repaint();
	}

	@Override
	public String getLanguage()
	{
		String result;
		if( getAppliConf() != null )
			result = getAppliConf().getLanguage();
		else
			result = ( (a_intern!=null) ? a_intern.M_getLanguage() : null );

		return( result );
	}
/*
	@Override
	public void changeFontSize( float factor )
	{
		if( a_intern != null )
		{
			a_intern.M_changeFontSize(factor);
		}
	}
*/
	@Override
	public void applyConfiguration() throws ConfigurationException, InternException
	{
		try
		{
			if( getAppliConf() != null )
				changeLanguage( getAppliConf().getLanguage() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

/*
	protected void setConfigurationChanges()
	{
		
	}
*/

	@Override
	public void closeWindow()
	{
		formWindowClosing(true);
	}

	protected void saveInternalConfiguration()
	{
		if( ( a_intern != null ) && a_intern.isInitialized() )
		{
			try
			{
				a_intern.saveConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	@Override
	public void formWindowClosing( boolean closeWindow )
	{
/*
		setConfigurationChanges();

		if( _appliConf != null )
		{
			try
			{
				_appliConf.M_saveConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		if( _stringsConf != null )
		{
			try
			{
				_stringsConf.M_saveConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
*/
		saveInternalConfiguration();

		if( closeWindow )
		{
			removeListenersRoot();

			setVisible(false);
			dispose();
			releaseResources();
		}
	}

	@Override
	public BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _appliConf );
	}

	protected boolean hasToSetSize( Dimension dimen )
	{
		return( !Objects.equals( dimen, getSize() ) );
	}

	protected boolean hasToSetBounds( Rectangle bounds )
	{
		return( !Objects.equals( bounds, getBounds() ) );
	}

	protected void prepareResizeOrRelocateToZoom()
	{
		if( a_intern != null )
			a_intern.prepareResizeOrRelocateToZoom();
	}
/*
	protected void prepareResizeOrRelocateToZoomInvokeLater()
	{
		SwingUtilities.invokeLater( this::prepareResizeOrRelocateToZoom );
	}
*/
	@Override
	public void setSize( int width, int height )
	{
		if( hasToSetSize( new Dimension( width, height ) ) )
		{
//			prepareResizeOrRelocateToZoomInvokeLater();
			prepareResizeOrRelocateToZoom();

			super.setSize( width, height );
		}
	}

	@Override
	public void setSize( Dimension dim )
	{
		if( hasToSetSize( dim ) )
		{
//			prepareResizeOrRelocateToZoomInvokeLater();
//			prepareResizeOrRelocateToZoom();

			super.setSize( dim );
		}
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		if( hasToSetBounds( new Rectangle( xx, yy, width, height ) ) )
		{
//			prepareResizeOrRelocateToZoomInvokeLater();
			prepareResizeOrRelocateToZoom();

			super.setBounds( xx, yy, width, height );
		}
	}

	@Override
	public void setBounds( Rectangle rect )
	{
		if( hasToSetBounds( rect ) )
		{
//			prepareResizeOrRelocateToZoomInvokeLater();
//			prepareResizeOrRelocateToZoom();

			super.setBounds( rect );
		}
	}

	@Override
	public boolean getAlwaysHighlightFocus()
	{
		return( _alwaysHighlightFocus );
	}

	@Override
	public void focusAndHighlightComponent( ViewComponent vc )
	{
		if( vc instanceof DesktopViewComponent )
		{
			DesktopViewComponent dvc = (DesktopViewComponent) vc;
			Component comp = dvc.getComponent();

			if( ComponentFunctions.instance().getAncestor( comp ) == this )
			{
				_highlightFocusComponent = comp;
				SwingUtilities.invokeLater( () -> _highlightFocusComponent.requestFocus() );
			}

			repaint();
		}
	}

	@Override
	public void highlightComponent( ViewComponent vc )
	{
		if( vc instanceof DesktopViewComponent )
		{
			DesktopViewComponent dvc = (DesktopViewComponent) vc;
			_componentToHighLight = dvc.getComponent();

			repaint();
		}
	}

	@Override
	public void setAlwaysHighlightFocus(boolean value)
	{
//		_previousValueOfHightlightFocus = _alwaysHighlightFocus;

//		_highlightFocusComponent = JFrameInternationalization.getFocusedComponent();
		_alwaysHighlightFocus = value;

		repaint();
	}

	protected boolean markComponent_internal( Graphics gc, Component comp )
	{
		boolean result = false;

		if( ( comp != null ) &&
			getRootPane().isAncestorOf(comp) )
		{
			int thick = IntegerFunctions.zoomValueCeil( 2.01, getZoomFactor() );
			int gap = 2;

			Dimension size = comp.getSize();

			int width = (int) size.getWidth() + gap * 2;
			int height = (int) size.getHeight() + gap * 2;

			Point leftUpperCorner = comp.getLocationOnScreen();
			Point windowLeftUpperCorner = getLocationOnScreen();

			int xx = (int) ( leftUpperCorner.getX() - windowLeftUpperCorner.getX() - gap );
			int yy = (int) ( leftUpperCorner.getY() - windowLeftUpperCorner.getY() - gap );

			ImageFunctions.instance().drawRect( gc, xx, yy, width, height, Color.RED, thick );

			result = true;
		}
		return( result );
	}

	protected boolean markComponent( Graphics gc, Component component )
	{
		boolean result = false;

		if( !(component instanceof JButton) )
			result = markComponent_internal( gc, component );

		return( result );
	}

	protected boolean markFocusedComponent( Graphics gc )
	{
		boolean result = false;

		Component focusedComponent = ComponentFunctions.instance().getFocusedComponent();
		result = markComponent( gc, focusedComponent );

		_lastFocusedComponentDrawn = focusedComponent;

		return( result );
	}

	@Override
	public Component getLastFocusedComponentDrawn()
	{
		return( _lastFocusedComponentDrawn );
	}

	protected boolean doHighlightFocus( Graphics gc )
	{
		boolean result = false;

//		if( _alwaysHighlightFocus )
		{
//			Component focusedComponent = JFrameInternationalization.getFocusedComponent();
			if( _highlightFocusComponent != null )
			{
				markComponent( gc, _highlightFocusComponent );
//				_highlightFocusComponent = null;
//				_alwaysHighlightFocus = _previousValueOfHightlightFocus;
			}

			if( _alwaysHighlightFocus )
				result = markFocusedComponent( gc );
		}

		return( result );
	}

	protected void paintEmpty( Graphics gc )
	{
		gc.setColor( Color.GRAY.brighter().brighter() );
		gc.fillRect(0, 0, getWidth(), getHeight());
	}

	@Override
	public void paint( Graphics gc )
	{
		synchronized( _synchronizedLockForPaint )
		{
			if( getPreventFromRepainting() )
			{
//				paintEmpty(gc);
				paintLast(gc);
				return;
			}

			_lastWindowImage = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
			_zoomFactorOfLastWindowImage = getZoomFactor();

			Graphics grp2 = _lastWindowImage.createGraphics();

			super.paint( grp2 );

//		System.out.println( "Painting window : " + ++_debugCounter );

			boolean componentHasBeenMarked = false;

			if( _componentToHighLight != null )
			{
				componentHasBeenMarked = markComponent_internal( grp2, _componentToHighLight );
				_componentToHighLight = null;
			}

			if( ! componentHasBeenMarked )
				componentHasBeenMarked = doHighlightFocus( grp2 );

			_hasToClearMarkedComponent = componentHasBeenMarked;

			if( isThereOverlappedImage() )
			{
				ImageFunctions.instance().paintClippedImage( this, grp2, _overlappedImage, _overlappedImageLocation );
			}

			grp2.dispose();
			paint(gc, _lastWindowImage);
		}
	}

	public void setIgnoreRepaintRecursive( Component comp, boolean ignoreRepainting )
	{
		ComponentFunctions.instance().browseComponentHierarchy(comp, (com) -> {
			if( com != InternationalizedJDialog.this )
			{
				com.setIgnoreRepaint(ignoreRepainting);
				if( ignoreRepainting )
					com.removeNotify();
				else
					com.addNotify();
			}

			return( null );
		});
	}

	protected void paintLast(Graphics gc)
	{
		if( ( _lastWindowImage == null ) ||
			( _newZoomFactor != _zoomFactorOfLastWindowImage ) )
		{
			BufferedImage image = _lastWindowImage;
			image = createLastZoomedImage( _lastWindowImage,
											_newZoomFactor / _zoomFactorOfLastWindowImage );

			paint( gc, image );
		}
	}

	protected void paint(Graphics gc, BufferedImage image )
	{
		if( image != null )
			gc.drawImage( image, 0, 0, null );
	}

	protected BufferedImage createEmptyImage( double zoomFactor )
	{
		Dimension unzoomedDimen = ViewFunctions.instance().getNewDimension( getSize(), 1/zoomFactor );
		BufferedImage result = new BufferedImage( unzoomedDimen.width,
												unzoomedDimen.height,
												BufferedImage.TYPE_INT_ARGB );
		Graphics grp2 = result.createGraphics();

		grp2.setColor( getBackground() );
		grp2.fillRect(0, 0, unzoomedDimen.width, unzoomedDimen.height);

		grp2.dispose();

		return( result );
	}

	protected BufferedImage createLastZoomedImage( BufferedImage image, double zoomFactor )
	{
		Insets insets = a_intern.getFrameBorder();

		if( image == null )
			image = createEmptyImage(zoomFactor);

		BufferedImage imageToZoom = ImageFunctions.instance().cropImage( image, insets );
		BufferedImage zoomedImage = ImageFunctions.instance().resizeImage(imageToZoom, zoomFactor );

		BufferedImage result = new BufferedImage( zoomedImage.getWidth() + insets.left + insets.right,
													zoomedImage.getHeight() + insets.top + insets.bottom,+
													BufferedImage.TYPE_INT_ARGB );

		Graphics grp2 = result.createGraphics();

		grp2.drawImage( zoomedImage, insets.left, insets.top, null );

		grp2.dispose();

		return( result );
	}

	@Override
	public Rectangle getOverlappingImageBounds()
	{
		Rectangle result = null;

		Point tl = getLocationOnScreen_forOverlappingImage();
		Point point = _overlappedImageLocation;
		BufferedImage bi = _overlappedImage;
		if( ( tl != null ) && ( bi != null ) && ( point != null ) )
		{
			result = new Rectangle( tl.x + point.x,
									tl.y + point.y,
									_overlappedImage.getWidth(),
									_overlappedImage.getHeight() );
		}

		return( result );
	}

	@Override
	public boolean isThereOverlappedImage()
	{
		return( ( _overlappedImage != null ) && (_overlappedImageLocation != null ) );
	}

	@Override
	public void changeToWaitCursor()
	{
		if( a_intern != null )
			_rollbackWaitCursorMapList.addLast( a_intern.M_changeCursor(CursorFunctions._waitCursor ) );
	}

	@Override
	public void revertChangeToWaitCursor()
	{
		if( ( a_intern != null ) &&  !_rollbackWaitCursorMapList.isEmpty() )
		{
			a_intern.M_rollbackChangeCursor( _rollbackWaitCursorMapList.removeLast() );
		}
	}

	@Override
	public void setOverlappedImage( BufferedImage image, Point position )
	{
		synchronized( _synchronizedLockForPaint )
		{
			_overlappedImage = image;
			_overlappedImageLocation = position;
			repaint();
		}
	}

	@Override
	public Dimension getDimensionOfOverlappingImage()
	{
		Dimension result = null;

		BufferedImage bi = _overlappedImage;
		if( bi != null )
			result = new Dimension( _overlappedImage.getWidth(), _overlappedImage.getHeight() );

		return( result );
	}

	@Override
	public int getWidth_forOverlappingImage()
	{
		return( getWidth() );
	}

	@Override
	public int getHeight_forOverlappingImage()
	{
		return( getHeight() );
	}
	
	@Override
	public Point getLocationOnScreen_forOverlappingImage()
	{
		return( getLocationOnScreen() );
	}

	@Override
	public void registerToChangeLanguageAsObserver(ChangeLanguageServerInterface conf)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public void registerInternationalString( String label, String value )
	{
		a_intern.registerInternationalString(label, value);
	}

	public String getInternationalString( String label )
	{
		String result = null;

		if( a_intern != null )
			result = a_intern.getInternationalString(label);

		return( result );
	}

	@Override
	public void setLanguage(String language)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void registerChangeLanguageObserver(ChangeLanguageClientInterface requestor)
	{
		if( a_intern != null )
		{
			a_intern.getLanguageConfiguration().registerChangeLanguageObserver( requestor );
		}
	}

	@Override
	public void unregisterChangeLanguageObserver(ChangeLanguageClientInterface requestor)
	{
		if( a_intern != null )
		{
			a_intern.getLanguageConfiguration().unregisterChangeLanguageObserver( requestor );
		}
	}

	@Override
	public void activateChangeLanguageNotifications(boolean value)
	{
		if( a_intern != null )
		{
			a_intern.getLanguageConfiguration().activateChangeLanguageNotifications( value );
		}
	}

	@Override
	public boolean areChangeLanguageNotificationsActivated()
	{
		boolean result = false;

		if( a_intern != null )
		{
			result = a_intern.getLanguageConfiguration().areChangeLanguageNotificationsActivated();
		}

		return( result );
	}

	@Override
	public void formWindowClosingEvent( )
	{
		closeWindow();
	}

	@Override
	public Component getComponent()
	{
		return( this );
	}

	@Override
	public boolean hasToClearMarkedComponent()
	{
		return( _hasToClearMarkedComponent );
	}
/*
	protected void deiconify_nonEDT()
	{
		SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						deiconify();
					}
		});
	}
*/
	@Override
	public void deiconify()
	{
/*		if( !SwingUtilities.isEventDispatchThread() )
		{
			deiconify_nonEDT();
			return;
		}

		if( getState() == Frame.ICONIFIED )
			setState( Frame.NORMAL );
*/
	}

	@Override
	public double getZoomFactor()
	{
		return( getAppliConf().getZoomFactor() );
	}

	@Override
	public void changeZoomFactor(double zoomFactor)
	{
		_newZoomFactor = zoomFactor;

		Point center = null;
		changeZoomFactorPreventingToPaint( zoomFactor, center );

		SwingUtilities.invokeLater( this::updateUpdateable );

		_previousZoomFactor = zoomFactor;
	}

	protected void updateUpdateable()
	{
		for( InternallyMappedComponent elem: getInternallyMappedComponentListCopy() )
			if( elem instanceof Updateable )
				( (Updateable) elem).update();
	}

	protected void changeZoomFactorPreventingToPaint(double zoomFactor, Point center)
	{
		ExtendedZoomSemaphore ezs = null;
		
		try
		{
			ezs = newExtendedZoomSemaphoreToAll();
			this.setPreventFromRepainting(true);
			a_intern.changeZoomFactor( zoomFactor, center );
		}
		finally
		{
			unblockComponentsAfterHavingZoomed( ezs );
		}
	}

	protected void unblockComponentsAfterHavingZoomed( ExtendedZoomSemaphore ezs )
	{
		unblockComponentsAfterHavingZoomed( ezs, null );
	}

	protected void unblockComponentsAfterHavingZoomed( ExtendedZoomSemaphore ezs,
													Runnable executeAfterZooming )
	{
		unsetPreventFromRepaintingWithSemaphore( ezs, 1350, executeAfterZooming );
		repaint();
	}

	@Override
	public void changeZoomFactor_centerMousePointer(double zoomFactor )
	{
		Point mouseLocation = MouseFunctions.getMouseLocation();
		Point center = null;
		if( ScreenFunctions.isInsideComponent( this, mouseLocation ) )
		{
			center = mouseLocation;
		}

		if( a_intern != null )
			changeZoomFactorPreventingToPaint( zoomFactor, center );
	}

	@Override
	public void unregisterFromChangeZoomFactorAsObserver()
	{
		if( _zoomFactorServer != null )
		{
			_zoomFactorServer.unregisterZoomFactorObserver(this);
			_zoomFactorServer = null;
		}
	}

	@Override
	public void registerToChangeZoomFactorAsObserver(ChangeZoomFactorServerInterface conf)
	{
		unregisterFromChangeZoomFactorAsObserver();

		_zoomFactorServer = conf;
		if( _zoomFactorServer != null )
			_zoomFactorServer.registerZoomFactorObserver(this);
	}

	@Override
	public boolean isIconified()
	{
		return( false );
	}

	@Override
	public void iconify()
	{}

	@Override
	public Locale getOutputLocale()
	{
		Locale outputLocale = GenericFunctions.instance().getObtainAvailableLanguages().getLocaleOfLanguage( getLanguage() );

		return( outputLocale );
	}

	@Override
	public void fireChangeLanguageEvent() throws Exception
	{
	}

	protected void changeLanguage()
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> changeLanguage( getAppliConf().getLanguage() ) );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		addListenersRoot();

//		SwingUtilities.invokeLater( () -> changeLanguage() );
		changeLanguage();

		registerToChangeZoomFactorAsObserver( getAppliConf() );

		if( isMinimumSizeSet() )
			_hundredPerCentMinimumWidth = IntegerFunctions.zoomValueRound( getMinimumSize().width, 1 / getZoomFactor() );

		SwingUtilities.invokeLater( () -> setMaximumSize( getSize() ) );
	}

	@Override
	public DesktopViewComponent getParentViewComponent()
	{
		return( DesktopGenericFunctions.instance().getViewFacilities().getParentViewComponent(this) );
	}

	@Override
	public String createCustomInternationalString( String label, Object ... args )
	{
		String result = null;

		if( a_intern != null )
			result = a_intern.createCustomInternationalString(label, args);

		return( result );
	}

	protected void switchOffUndoRedoManager( JTextComponent jtc )
	{
		TextUndoRedoInterface urm = a_intern.getTextUndoRedoManager(jtc);
		if( urm != null )
			urm.stopManaging();
	}

	@Override
	public CC getApplicationContext()
	{
		return( _applicationContext );
	}

	protected void onWindowResized()
	{
	}

	protected void onWindowMoved()
	{
		
	}

	protected void onWindowShown()
	{
		
	}

	protected void onWindowHidden()
	{
		
	}

	protected void onWindowMinimized()
	{
		
	}

	protected void onWindowMaximized()
	{
		
	}

	protected void onWindowHorizMaximized()
	{
		
	}

	protected void onWindowVertMaximized()
	{
		
	}

	protected void onWindowNormalAgain()
	{
		
	}

	@Override
	public void componentResized(ComponentEvent arg0)
	{
		onWindowResized();

		SwingUtilities.invokeLater( () -> repaint() );
	}

	@Override
	public void componentMoved(ComponentEvent arg0)
	{
		onWindowMoved();
	}

	@Override
	public void componentShown(ComponentEvent arg0)
	{
		onWindowShown();
	}

	@Override
	public void componentHidden(ComponentEvent arg0)
	{
		onWindowHidden();
	}

	@Override
	public void windowStateChanged(WindowEvent evt)
	{
		if ((evt.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED)
		{
			onWindowMinimized();
		}
		else if ((evt.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH )
		{
			onWindowMaximized();
		}
		else if ((evt.getNewState() & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ )
		{
			onWindowHorizMaximized();
		}
		else if ((evt.getNewState() & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT )
		{
			onWindowVertMaximized();
		}
		else if ((evt.getNewState() & Frame.NORMAL) == Frame.NORMAL )
		{
			onWindowNormalAgain();
		}
	}

	@Override
	public void focusGained(FocusEvent e)
	{
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		if( ( e != null ) &&
			( ( _highlightFocusComponent == e.getComponent() ) ||
			ComponentFunctions.instance().isAnyParent( _highlightFocusComponent, e.getComponent() ) ) &&
			! ComponentFunctions.instance().isAnyParent( _highlightFocusComponent, e.getOppositeComponent() ) )
		{
			_highlightFocusComponent = null;
			repaint();
		}
	}

	protected abstract void translateMappedComponents( ComponentMapper compMapper );

	@Override
	public void setComponentMapper( ComponentMapper compMapper )
	{
		_compMapper = compMapper;

		translateMappedComponents( _compMapper );

		translateInternallyMappedComponents(compMapper);

		_alreadyMapped = true;
	}

	protected void translateInternallyMappedComponents( ComponentMapper compMapper )
	{
		for( InternallyMappedComponent imc: getInternallyMappedComponentListCopy() )
			imc.setComponentMapper(compMapper);
	}

	@Override
	public boolean hasBeenAlreadyMapped()
	{
		return( _alreadyMapped );
	}

	@Override
	public <CC> CC mapComponent( CC originalComp )
	{
		CC result = originalComp;
		if( _compMapper != null )
			result = _compMapper.mapComponent(result);

		return( result );
	}

	public boolean wasSuccessful()
	{
		return( _hasBeenSuccessfullyValidated );
	}

	// to be overriden by children
	protected void validateFormChild() throws ValidationException
	{
		
	}

	protected String validateForm()
	{
		return( validateForm( () -> validateFormChild() ) );
	}

	protected String validateForm( UnsafeMethod function )
	{
		String errorMessage = InternationalizedWindowFunctions.instance().validate( this, function );

		_hasBeenSuccessfullyValidated = ( errorMessage == null );

		return( errorMessage );
	}

	protected void setWasSuccessful( boolean value )
	{
		_hasBeenSuccessfullyValidated = value;
	}

	protected void doInternationalizationTasksOnTheFly( Component comp )
	{
		a_intern.doInternationalizationTasksOnTheFly(comp);
	}

	public boolean alreadyInitializedOnTheFly( Component comp )
	{
		return( a_intern.getResizeRelocateComponentItemOnTheFly(comp) != null );
	}

	public boolean alreadyInitialized( Component comp )
	{
		return( a_intern.getResizeRelocateComponentItem(comp) != null );
	}

	protected JPopupMenu getNonInheritedPopupMenu( JFrameInternationalization inter,
													Component comp )
	{
		JPopupMenu result = null;
		if( comp instanceof JTextComponent )
		{
			result = inter.getNonInheritedPopupMenu( (JTextComponent) comp );
		}
		return( result );
	}

	protected JPopupMenu getNonInheritedPopupMenu( Component comp )
	{
		return( getNonInheritedPopupMenu( a_intern, comp ) );
	}

	protected Component processComponentOnTheFlyForDefaultResizeRelocateItem( MapResizeRelocateComponentItem mapRrci,
										Component comp )
	{
		a_intern.createAndStoreResizeRelocateItemIntoMap( comp, mapRrci );
		Component result = getNonInheritedPopupMenu( comp );

		return( result );
	}

	public ZoomComponentOnTheFlyResult zoomComponentOnTheFly( Component comp, ResizeRelocateItem rri )
	{
		ZoomComponentOnTheFlyResult result = new ZoomComponentOnTheFlyResult();

		ExtendedZoomSemaphore ezs = createExtendedZoomSemaphore();

		MapResizeRelocateComponentItem mapRrci = new MapResizeRelocateComponentItem();
		if( ! alreadyInitializedOnTheFly( comp ) )
		{
			if( rri != null )
				mapRrci.put( comp, rri );

			if( comp instanceof ComposedComponent )
				mapRrci.putAll( ( (ComposedComponent) comp).getResizeRelocateInfo() );

			for( ResizeRelocateItem rri2: mapRrci.values() )
			{
				rri2.registerListeners();
				rri.setExtendedZoomSemaphore(ezs);
				ezs.increaseCount();
			}

			boolean isOnTheFly = true;
			a_intern.switchToZoomComponentsGen( comp, mapRrci, isOnTheFly );

			doInternationalizationTasksOnTheFly( comp );

			ComponentFunctions.instance().browseComponentHierarchy(comp, (co) -> processComponentOnTheFlyForDefaultResizeRelocateItem( mapRrci, co ) );
		}

		result.setMapResizeRelocateComponentItem(mapRrci);
		result.setExtendedZoomSemaphore( ezs );
		_listOfZoomSemaphoresOnTheFly.add( ezs );

		return( result );
	}

	protected ExtendedZoomSemaphore createExtendedZoomSemaphore()
	{
		ExtendedZoomSemaphore result = new ExtendedZoomSemaphore();
		result.init();

		return( result );
	}

	protected void setExtendedZoomSemaphoreToAll( ExtendedZoomSemaphore ezs )
	{
		for( ResizeRelocateItem rri: a_intern.getListOfResizeRelocateItems() )
		{
			rri.setExtendedZoomSemaphore(ezs);
			ezs.increaseCount();
		}
	}

	protected void waitForExtendedZoomSemaphoresOnTheFly( int ms )
	{
		long start = System.currentTimeMillis();
		
		while(!_listOfZoomSemaphoresOnTheFly.isEmpty() )
		{
			ExtendedZoomSemaphore ezs = _listOfZoomSemaphoresOnTheFly.remove(0);
			int millisToWaitForCurrent = (int) Math.max( 0, ms - System.currentTimeMillis() + start );
			ezs.tryAcquire( millisToWaitForCurrent );
		}
	}

	protected ExtendedZoomSemaphore newExtendedZoomSemaphoreToAll()
	{
		ExtendedZoomSemaphore result = createExtendedZoomSemaphore();
		setExtendedZoomSemaphoreToAll(result);

		return( result );
	}

	protected void unsetPreventFromRepaintingWithSemaphore( ExtendedZoomSemaphore ezs, int ms,
															Runnable executeAfterZooming )
	{
		if( ezs != null )
			executeTask(
				() -> {
					ezs.tryAcquire(ms);
					if( ezs.getSemaphore().getQueueLength() > 0 )
						System.out.println( "Semaphore queue length: " + ezs.getSemaphore().getQueueLength() );

					SwingUtilities.invokeLater( () -> {
						setPreventFromRepainting( false ); repaint();
						if( executeAfterZooming != null )
							executeAfterZooming.run();
					});
				});
	}

	public <CC extends Component> void initResizeRelocateItemsOComponentOnTheFly( Collection<CC> listOfRootComponents,
																				MapResizeRelocateComponentItem mapRrci,
																				boolean setMinSize,
																				Runnable executeAfterZooming )
	{
		boolean adjustMaximumSizeToContents = true;
		initResizeRelocateItemsOComponentOnTheFly( listOfRootComponents,
													mapRrci,
													setMinSize,
													executeAfterZooming,
													adjustMaximumSizeToContents );
	}

	public <CC extends Component> void initResizeRelocateItemsOComponentOnTheFly( Collection<CC> listOfRootComponents,
																				MapResizeRelocateComponentItem mapRrci,
																				boolean setMinSize,
																				Runnable executeAfterZooming,
																				boolean adjustMaximumSizeToContents )
	{
		JFrameInternationalization inter = a_intern;

		if( inter != null )
		{
			executeTask(() -> {
				waitForExtendedZoomSemaphoresOnTheFly( 350 );
				SwingUtilities.invokeLater(() -> {
					ExtendedZoomSemaphore ezs = newExtendedZoomSemaphoreToAll();

					try
					{
						setPreventFromRepainting( true );

						createComponentDataOnTheFly(listOfRootComponents);

						ZoomParam zp1 = new ZoomParam( 1.0D );
						ZoomParam zp = new ZoomParam( getAppliConf().getZoomFactor() );
						inter.addMapResizeRelocateComponents(mapRrci);
						for( Component rootComp: listOfRootComponents )
						{
							ComponentFunctions.instance().browseComponentHierarchy( rootComp,
								(comp) -> {
									ResizeRelocateItem rri = inter.getResizeRelocateComponentItem(comp);
									if( rri != null )
									{
										rri.newExpectedZoomParam(zp);
									}

									Component result = getNonInheritedPopupMenu( inter, comp );

									return( result );
								});

							ComponentFunctions.instance().browseComponentHierarchy( rootComp,
								(comp) -> {
									ResizeRelocateItem rri = inter.getResizeRelocateComponentItem(comp);
									if( rri != null )
										rri.execute(zp);

									Component result = getNonInheritedPopupMenu( inter, comp );

									return( result );
								});

							getColorInversor().setDarkMode(rootComp, wasLatestModeDark(),
									comp2 -> getIfNotNull(this.getInternationalization(),
												inter2 -> inter2.getColorThemeChangeable(comp2)));
						}

						ezs.setActivated(true);
						resizeFrameToContents(setMinSize);
					}
					finally
					{
						if( adjustMaximumSizeToContents )
							adjustMaximumSizeToContents();

						unsetPreventFromRepaintingWithSemaphore( ezs, 350, executeAfterZooming );
					}
				} );
			} );
		}
	}

	protected <CC extends Component> void createComponentDataOnTheFly(Collection<CC> listOfRootComponents)
	{
		for( Component comp: listOfRootComponents )
			ComponentFunctions.instance().browseComponentHierarchy(comp, comp2 -> {
				FrameworkComponentFunctions.instance().getComponentDataOnTheFly(comp2);
				return( null );
			});
	}

	public void executeTask( Runnable runnable )
	{
		_pullOfWorkers.addPendingNonStopableExecutor( runnable );
	}

	public void executeDelayedInvokeEventDispatchThread( Runnable runnable, int delayMs )
	{
		_pullOfWorkers.addPendingNonStopableExecutor( () -> ThreadFunctions.instance().delayedInvokeEventDispatchThread(runnable, delayMs) );
	}

	public void executeDelayedTask( Runnable runnable, int delayMs )
	{
		_pullOfWorkers.addPendingNonStopableExecutor( () -> ThreadFunctions.instance().invokeWithDelay(runnable, delayMs) );
	}

	protected void adjustMaximumSizeToContents()
	{
		SwingUtilities.invokeLater( () -> {
			setMaximumSize( getSize() );
			});
	}

	protected void resizeFrameToContents()
	{
		resizeFrameToContents( true );
	}

	protected void resizeFrameToContents( boolean setMinSize )
	{
		SwingUtilities.invokeLater( () -> {
			a_intern.resizeFrameToContents();

			if( isMinimumSizeSet() && setMinSize )
			{
				Dimension minSize = getMinimumSize();
				minSize.width = IntegerFunctions.zoomValueRound( _hundredPerCentMinimumWidth, getZoomFactor() );
				setMinimumSize( minSize );
			}
		});
	}

	protected ScrollPaneMouseListener createMouseWheelListener( JScrollPane sp )
	{
		return( new ScrollPaneMouseListener( sp ) );
	}

	public void applyConfigurationChanges()
	{
	}

	@Override
	public void validation( Supplier<String> validationFunction,
							Component comp,
							Function<String, String> errorMessageCreatorFunction )
		throws ValidationException
	{
		if( validationFunction != null )
		{
			String errorMessage = null;
			Exception exception = null;
			try
			{
				errorMessage = validationFunction.get();
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
				errorMessage = ex.getMessage();
			}

			if( errorMessage != null )
			{
				if( errorMessageCreatorFunction != null )
				{
					errorMessage = errorMessageCreatorFunction.apply(errorMessage);
				}
				throw( new ValidationException( errorMessage, comp ) );
			}
		}
	}

	protected Object getInitializedLock()
	{
		return( _initializedLock );
	}

	public void setPreventFromRepainting( boolean value )
	{
		synchronized( _initializedLock )
		{
			if( !_preventFromRepainting && value )
			{
				if( !isInitialized() )
					SwingUtilities.invokeLater( () -> super.setVisible( false ) );
				else
					SwingUtilities.invokeLater( () -> setIgnoreRepaintRecursive(true) );
			}

			boolean hasToUnblock = _preventFromRepainting && !value;
			_preventFromRepainting = value;
			if( hasToUnblock )
			{
				if( _isVisible && !isVisible() )
					SwingUtilities.invokeLater( () -> setVisibleWithLock( _isVisible ) );
				else
					SwingUtilities.invokeLater( () -> { setIgnoreRepaintRecursive(false); repaint(); } );
				
				revalidateEverything();
			}
		}
	}

	protected void revalidateEverything()
	{
		SwingUtilities.invokeLater( () -> {
			ComponentFunctions.instance().browseComponentHierarchy( this,
				(comp) -> {
					if( comp instanceof Container )
						( (Container) comp ).revalidate();
					return null;
				});
		});
/*
		SwingUtilities.invokeLater( () -> {
			ComponentFunctions.instance().browseComponentHierarchy( this,
				(comp) -> {
					if( comp instanceof JViewport )
					{
						JViewport vp = (JViewport) comp;
						JScrollPane jsp = ComponentFunctions.instance().getScrollPaneOfViewportView(vp);
						jsp.setViewportView( vp.getView() );
					}
					return null;
				});
		});
*/
		SwingUtilities.invokeLater( () -> revalidateChild() );
	}

	public void revalidateChild()
	{
		
	}

	protected void setIgnoreRepaintRecursive( boolean value )
	{
		setIgnoreRepaintRecursive( this, value );
	}

	protected boolean getPreventFromRepainting()
	{
		return( _preventFromRepainting );
	}

	public void setVisibleWithLock( boolean value )
	{
		synchronized( _initializedLock )
		{
			_isVisible = value;

			if( !getPreventFromRepainting() ) {
				if( SwingUtilities.isEventDispatchThread() )
					setVisible( value );
				else
					SwingUtilities.invokeLater( () -> super.setVisible( value ) );
			}
		}
	}
/*
	@Override
	public void setVisible( boolean value )
	{
		_isVisible = value;

		super.setVisible( value );
	}
*/

	public void windowSetVisible( boolean value )
	{
		super.setVisible( value );
	}

	@Override
	public void setVisible( boolean value )
	{
		synchronized( _initializedLock )
		{
			_isVisible = value;

			if( !getPreventFromRepainting() ) {
				if( SwingUtilities.isEventDispatchThread() )
					super.setVisible( value );
				else
					SwingUtilities.invokeLater( () -> super.setVisible( value ) );
			}
		}
	}

	protected boolean isInitialized()
	{
		synchronized( _initializedLock )
		{
			return( _isInitialized );
		}
	}

	@Override
	public void setInitialized()
	{
		synchronized( _initializedLock )
		{
			_isInitialized = true;
			setPreventFromRepainting(false);
			SwingUtilities.invokeLater( () -> repaint() );
		}
	}

	protected String getFinalUrl( String url )
	{
		return( GenericFunctions.instance().getApplicationFacilities().buildResourceCounterUrl( url ) );
	}

	protected List<Component> getComponentsIncludingPoint( Container cont,
		Point pointOnScreen ) {
		return( getComponentsMatchingGen(cont,
			(c, p) -> {
				boolean result = false;
				Rectangle bounds = ComponentFunctions.instance().getBoundsOnScreen(c);
				if( bounds != null )
					result = bounds.contains(p);

				return( result );
			},
			pointOnScreen) );
	}

	protected <MM> List<Component> getComponentsMatchingGen( Container cont,
		BiFunction<Component, MM, Boolean> matcher, MM magnitudeForComparison ) {
		return( ComponentFunctions.instance().getMatchingChildComponents(cont,
			matcher, magnitudeForComparison) );
	}

	protected List<Component> getComponentsMatchingLocation( Container cont,
		Point originPoint, int tolerance ) {
		return( getComponentsMatchingPosition(cont, c -> c.getLocation(),
			originPoint, tolerance) );
	}

	protected List<Component> getComponentsMatchingWidth( Container cont,
		int widthToCopmare, int tolerance ) {
		return( getComponentsMatchingPosition(cont, c -> c.getSize().width,
			widthToCopmare, tolerance) );
	}

	protected <MM> List<Component> getComponentsMatchingPosition( Container cont,
		Function<Component, MM> getter, MM magnitudeToCompare,
		int tolerance ) {

		List<Component> result = ComponentFunctions.instance().getMatchingChildComponents( cont, getter,
			magnitudeToCompare, tolerance );

		return( result );
	}

	@Override
	public boolean isDarkMode()
	{
		return( _colorThemeStatus.isDarkMode() );
	}

	@Override
	public void setDarkMode(boolean value, ColorInversor colorInversor)
	{
		_colorThemeStatus.setDarkMode( value, colorInversor );
	}

	@Override
	public boolean wasLatestModeDark()
	{
		return( _colorThemeStatus.wasLatestModeDark() );
	}

	@Override
	public void setLatestWasDark(boolean value) {
		_colorThemeStatus.setLatestWasDark(value);
	}

	protected boolean hasToInvertColors( boolean value )
	{
		return( wasLatestModeDark() != value );
	}

	protected void unregisterFromChangeColorThemeAsObserver()
	{
		if( ( _colorThemeChangeListener != null ) && ( getAppliConf() != null ) )
		{
			getAppliConf().removeConfigurationParameterListener(BaseApplicationConfiguration.CONF_IS_DARK_MODE_ACTIVATED,
																_colorThemeChangeListener);
			_colorThemeChangeListener = null;
		}
	}

	protected ColorInversor createColorInversor()
	{
		return( ColorInversorFactoryImpl.instance().createColorInversor() );
	}

	@Override
	public ColorInversor getColorInversor()
	{
		if( _colorInversor == null )
			_colorInversor = getIfNotNull( getApplicationContext(), ac -> ac.getColorInversor() );

		if( _colorInversor == null )
			_colorInversor = createColorInversor();

		return( _colorInversor );
	}

	protected void invertSingleWindowColors(ColorInversor colorInversor)
	{
		if( getIfNotNull( getInternationalization(), JFrameInternationalization::getVectorOfPopupMenus ) != null )
			for( JPopupMenu popupMenu: getInternationalization().getVectorOfPopupMenus() )
				colorInversor.setDarkMode( popupMenu, isDarkMode(), getInternationalization()::getColorThemeChangeable );

		colorInversor.invertSingleColorsGen(this);
		_colorThemeStatus.setLatestWasDark( isDarkMode() );

		invertUndoRedoPopups();
	}

	protected void invertUndoRedoPopups()
	{
		ComponentFunctions.instance().browseComponentHierarchy( this, (comp, res) -> {
			JPopupMenu menu = ExecutionFunctions.instance().safeSilentFunctionExecution( () ->
				getIfNotNull( this.getInternationalization()
					.getMapOfComponents().getOrCreateOnTheFly(comp).getTextCompPopupManager(),
					TextCompPopupManager::getJPopupMenu )
			);
			if( menu != null )
				getColorInversor().setDarkMode( menu, isDarkMode(),
					comp2 -> this.getInternationalization().getColorThemeChangeableOnTheFly(comp2) );
		});
	}

	protected void setDarkMode( boolean isDarkMode )
	{
		ColorInversor ci = getColorInversor();
		if( ci != null )
		{
			ci.setDarkMode(this, isDarkMode, null);
			invertSingleWindowColors(ci);
		}
	}

	public void configurationParameterColorThemeChanged( ConfigurationParameterObserved observed, String label,
															Object oldValue, Object newValue )
	{
		boolean isDarkMode = (Boolean) newValue;

		setDarkMode( isDarkMode );
	}

	@Override
	public void invokeConfigurationParameterColorThemeChanged()
	{
		if( getAppliConf() != null )
			configurationParameterColorThemeChanged( null, BaseApplicationConfiguration.CONF_IS_DARK_MODE_ACTIVATED,
													null,
													getAppliConf().isDarkModeActivated() );
	}

	public void registerToChangeColorThemeAsObserver(BaseApplicationConfigurationInterface conf)
	{
		unregisterFromChangeColorThemeAsObserver();

		if( conf != null )
		{
			_colorThemeChangeListener = this::configurationParameterColorThemeChanged;
			conf.addConfigurationParameterListener(BaseApplicationConfiguration.CONF_IS_DARK_MODE_ACTIVATED,
													_colorThemeChangeListener);
		}
	}

	protected Color invertColorIfNecessary( Color color )
	{
		Color result = color;

		if( getAppliConf().isDarkModeActivated() )
			result = getColorInversor().invertColor(color);

		return( result );
	}

	protected Color[] invertColorsIfNecessary( Color ... brightModeColors ) {
		Color[] result = brightModeColors;
		if( getAppliConf().isDarkModeActivated() )
			result = getColorInversor().invertColors(brightModeColors);

		return( result );
	}

	public void addComponentToCompletelyFillParent( Container parent, Component comp )
	{
		ContainerFunctions.instance().addComponentToCompletelyFillParent(parent, comp);
	}

	public Exception safeMethodExecution( ExecutionFunctions.UnsafeMethod run )
	{
		return( ExecutionFunctions.instance().safeMethodExecution(run) );
	}

	public <CC> CC safeFunctionExecution( ExecutionFunctions.UnsafeFunction<CC> run )
	{
		return( ExecutionFunctions.instance().safeFunctionExecution(run) );
	}

	protected List<InternallyMappedComponent> getInternallyMappedComponentListCopy() {
		return( _containerOfInternallyMappedComponents.getInternallyMappedComponentListCopy() );
	}

	@Override
	public void addInternallyMappedComponent( InternallyMappedComponent im )
	{
		_containerOfInternallyMappedComponents.addInternallyMappedComponent(im);
	}
}
