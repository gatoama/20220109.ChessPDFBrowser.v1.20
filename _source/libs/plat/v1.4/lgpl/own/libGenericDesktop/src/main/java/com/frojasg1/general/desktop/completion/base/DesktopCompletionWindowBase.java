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
package com.frojasg1.general.desktop.completion.base;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.general.completion.PrototypeForCompletionBase;
import com.frojasg1.general.desktop.completion.api.InputTextCompletionManager;
import com.frojasg1.general.desktop.completion.api.CompletionWindow;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.desktop.completion.data.AlternativesForCompletionData;
import com.frojasg1.general.desktop.completion.data.CurrentParamForCompletionData;
import com.frojasg1.general.desktop.completion.data.TotalCompletionData;
import com.frojasg1.general.desktop.screen.ScreenFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.editorkits.WrapEditorKit;
import com.frojasg1.general.desktop.view.text.link.imp.LinkListener;
import com.frojasg1.general.desktop.view.text.link.imp.LinkServer;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class DesktopCompletionWindowBase< MM extends PrototypeForCompletionBase> extends InternationalizedJDialog//<ApplicationContextImp>
									implements CompletionWindow< Rectangle >,
									KeyListener, MouseWheelListener,
									FocusListener,
									LinkListener<MM> {

	public static final String sa_configurationBaseFileName = "DesktopCompletionWindow";
/*
	protected ComponentFunctions.ExecuteToComponent addFocusListenerExecutor = comp -> {
		comp.addFocusListener( this );
		return( null );
	};
*/
	protected CompletionDocumentFormatterBase _completionDocumentFormatter = null;
	protected CurrentParamDocumentFormatterBase _currentParamDocumentAppender = null;

	protected Rectangle _lastCharBounds = null;

	protected AlternativesForCompletionData<Rectangle> _lastAlternativesForCompletionData = null;
	protected CurrentParamForCompletionData<Rectangle> _lastCurrentParamForCompletionData = null;

	protected PrototypeForCompletionBase[] _lastCompletionPrototypes = null;
	protected PrototypeForCompletionBase _lastParamPrototype = null;

	protected PrototypeForCompletionBase _selectedParamPrototype = null;

	protected InputTextCompletionManager _completionManager = null;

	ResizeRelocateItem _rriForCompletionTextPane = null;
	ResizeRelocateItem _rriForCurrentParamTextPane = null;

	protected Component _componentFocusedForNotHiding = null;

	protected InternationalizedStringConf _translatorOfType = null;

	FocusListener _exitFocusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e)
		{
			int kk=1;
		}

		@Override
		public void focusLost(FocusEvent evt)
		{
			Component opposite = evt.getOppositeComponent();

			if( ( opposite == null ) ||
				!ComponentFunctions.instance().isAnyParent(DesktopCompletionWindowBase.this, opposite) &&
				!ComponentFunctions.instance().isAnyParent( _componentFocusedForNotHiding, opposite) )
			{
				hideEverything();
			}
		}
	};

	/**
	 * Creates new form DesktopCompletionWindow
	 */
	public DesktopCompletionWindowBase( JFrame frame,
									BaseApplicationConfigurationInterface applicationConfiguration,
									InternationalizedStringConf translatorOfType )
//									ApplicationContextImp appCtx ) {
	{
		super( frame, false, applicationConfiguration, null, null, false, true );

		_translatorOfType = translatorOfType;
/*
		setUndecorated( true );
		setAlwaysOnTop( true );

//		setAppliConf( applicationConfiguration );
//		_applicationContext = appCtx;

		initComponents();

		initOwnComponents();
		setListenersRecursive( this );

		setWindowConfiguration( );
*/
	}

	public DesktopCompletionWindowBase( JDialog dialog,
									BaseApplicationConfigurationInterface applicationConfiguration,
									InternationalizedStringConf translatorOfType )
//									ApplicationContextImp appCtx ) {
	{
		super( dialog, false, applicationConfiguration, null, null, false, true );

		_translatorOfType = translatorOfType;

/*
		setUndecorated( true );
		setAlwaysOnTop( true );

//		setAppliConf( applicationConfiguration );
//		_applicationContext = appCtx;

		initComponents();

		initOwnComponents();
		setListenersRecursive( this );

		setWindowConfiguration( );
*/

	}

	public void init()
	{
		setUndecorated( true );
		setAlwaysOnTop( true );

//		setAppliConf( applicationConfiguration );
//		_applicationContext = appCtx;

		initComponents();

		initOwnComponents();
//		setListenersRecursive( this );

		setWindowConfiguration( );
	}

	@Override
	public boolean isVisible()
	{
		return( super.isVisible() );
	}

	public void setCompletionManager( InputTextCompletionManager completionManager )
	{
		_completionManager = completionManager;
	}

	protected void setListenersRecursive( Component comp )
	{
		comp.addMouseWheelListener(this);
		comp.addKeyListener(this);

		if( comp instanceof Container )
		{
			Container cont = (Container) comp;

			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				setListenersRecursive( cont.getComponent(ii) );
		}
	}

	protected void removeListenersRecursive( Component comp )
	{
		comp.removeMouseWheelListener(this);
		comp.removeKeyListener(this);

		if( comp instanceof Container )
		{
			Container cont = (Container) comp;

			for( int ii=0; ii<cont.getComponentCount(); ii++ )
				removeListenersRecursive( cont.getComponent(ii) );
		}
	}

	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = null;

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, 0 );
//			mapRRCI.putResizeRelocateComponentItem(jScrollPane1, ResizeRelocateItem.SKIP_CHANGE_BOUNDS_AND_REPOSITION );
//			mapRRCI.putResizeRelocateComponentItem( jTP_completion, ResizeRelocateItem.RESIZE_SCROLLABLE_HORIZONTAL_FREE );
			_rriForCompletionTextPane = mapRRCI.createResizeRelocateItem( jTP_completion,
														ResizeRelocateItem.RESIZE_SCROLLABLE_HORIZONTAL_FREE +
														ResizeRelocateItem.DO_NOT_ZOOM_FONT );
			mapRRCI.putResizeRelocateComponentItem( _rriForCompletionTextPane );

			mapRRCI.putResizeRelocateComponentItem( jPanel2, 0 );
//			mapRRCI.putResizeRelocateComponentItem(jScrollPane2, ResizeRelocateItem.SKIP_CHANGE_BOUNDS_AND_REPOSITION );
			_rriForCurrentParamTextPane = mapRRCI.createResizeRelocateItem( jTP_currentParam,
														ResizeRelocateItem.RESIZE_SCROLLABLE_HORIZONTAL_FREE +
														ResizeRelocateItem.DO_NOT_ZOOM_FONT );
			mapRRCI.putResizeRelocateComponentItem( _rriForCurrentParamTextPane );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_configurationBaseFileName,
									this,
									null,
									vectorJpopupMenus,
									true,
									mapRRCI );
	}


	protected void initOwnComponents()
	{
		jTP_completion.setEditorKit( new WrapEditorKit() );
		jTP_currentParam.setEditorKit( new WrapEditorKit() );

		_currentParamDocumentAppender = createCurrentParamDocumentFormatter( jTP_currentParam );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		_completionDocumentFormatter = createCompletionDocumentFormatter( jTP_completion );
		_completionDocumentFormatter.addLinkListener(this);

//		_completionDocumentFormatter.setNewJTextPane( jTP_completion );
		_completionDocumentFormatter.addListener( _rriForCompletionTextPane );

		if( isDarkMode() )
			invertFormattersColor( getColorInversor() );

		if( isCurrentParamActive() )
		{
			_currentParamDocumentAppender.setNewJTextPane( jTP_currentParam );
			_currentParamDocumentAppender.addListener( _rriForCurrentParamTextPane );
		}

		setListenersRecursive( this );
//		addRecursiveFocusListener( this );
	}
/*
	protected void addRecursiveFocusListener( Component comp )
	{
		ComponentFunctions.instance().browseComponentHierarchy( comp, addFocusListenerExecutor );
	}
*/
	protected abstract CompletionDocumentFormatterBase<MM> createCompletionDocumentFormatter( JTextPane textPane );
/*
	{
		return( new CompletionDocumentFormatterBase<>( textPane, getAppliConf(),
													_translatorOfType ) );//, getApplicationContext().getBigMathHelp() ) );
	}
*/
	protected abstract CurrentParamDocumentFormatterBase createCurrentParamDocumentFormatter( JTextPane textPane );
/*
	{
		return( new CurrentParamDocumentFormatterBase( textPane, getAppliConf(), getApplicationContext().getBigMathHelp() ) );
	}
*/
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTP_completion = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTP_currentParam = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(null);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jTP_completion.setEditable(false);
        jTP_completion.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        jTP_completion.setMinimumSize(new java.awt.Dimension(400, 110));
        jTP_completion.setName("jTP_completion"); // NOI18N
        jScrollPane1.setViewportView(jTP_completion);

        jPanel1.add(jScrollPane1);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 400, 80);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setMinimumSize(new java.awt.Dimension(100, 100));

        jTP_currentParam.setEditable(false);
        jTP_currentParam.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        jTP_currentParam.setFocusable(false);
        jTP_currentParam.setMinimumSize(new java.awt.Dimension(400, 40));
        jTP_currentParam.setName("jTP_currentParam"); // NOI18N
        jScrollPane2.setViewportView(jTP_currentParam);

        jPanel2.add(jScrollPane2);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 80, 400, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTP_completion;
    private javax.swing.JTextPane jTP_currentParam;
    // End of variables declaration//GEN-END:variables


	@Override
	public PrototypeForCompletionBase getSelectedCompletion()
	{
		PrototypeForCompletionBase result = null;
		if( _selectedParamPrototype != null )
		{
			result = _selectedParamPrototype;
			_selectedParamPrototype = null;
		}
		else
			result = _completionDocumentFormatter.getSelected();
		return( result );
	}

	protected boolean setListOfAlternativesKeepingSelection(AlternativesForCompletionData<Rectangle> alternativesForCompletionData)
	{
		boolean hasToSetFocus = false;
		_lastAlternativesForCompletionData = alternativesForCompletionData;

		if( alternativesForCompletionData != null )
		{
			if( _completionDocumentFormatter != null )
			{
				String preText = alternativesForCompletionData.getPreText();
				PrototypeForCompletionBase[] prototypes = alternativesForCompletionData.getPrototypes();
				Rectangle locationControl = alternativesForCompletionData.getLocationControl();

				_lastCompletionPrototypes = prototypes;

				setVisibleInvokeLater( !StringFunctions.instance().isEmpty( preText ) &&
							( prototypes != null ) && ( prototypes.length > 0 ) );

				_completionDocumentFormatter.setPrototypes(preText, prototypes);

				locateWindow( locationControl );
				hasToSetFocus = setVisibility();
			}
			else
			{
				SwingUtilities.invokeLater( () -> setListOfAlternativesKeepingSelection( alternativesForCompletionData ) );
			}
		}

		return( hasToSetFocus );
	}

	protected boolean isCurrentParamActive()
	{
		return( _currentParamDocumentAppender != null );
	}

	protected boolean setCurrentParamPrototype(CurrentParamForCompletionData<Rectangle> currentParamForCompletionData)
	{
		boolean hasToSetFocus = false;
		_lastCurrentParamForCompletionData = currentParamForCompletionData;

		if( currentParamForCompletionData != null )
		{
			PrototypeForCompletionBase prototype = currentParamForCompletionData.getPrototype();
			int currentParamIndex = currentParamForCompletionData.getCurrentParamIndex();
			Rectangle locationControl = currentParamForCompletionData.getLocationControl();

			_lastParamPrototype = prototype;

			setVisibleInvokeLater( isVisible() || ( prototype != null ) );

			if( isCurrentParamActive() )
				_currentParamDocumentAppender.setCurrentParam(prototype, currentParamIndex);

			locateWindow( locationControl );
			hasToSetFocus = setVisibility();
		}

		return( hasToSetFocus );
	}

	@Override
	public void setTotalCompletionData( TotalCompletionData<Rectangle> totalCompletionData )
	{
		if( totalCompletionData != null )
		{
			boolean hasToSetFocus = setListOfAlternativesKeepingSelection(totalCompletionData.getAlternativesForCompletionData());
			hasToSetFocus = setCurrentParamPrototype(totalCompletionData.getCurrentParamForCompletionData()) || hasToSetFocus;

			if( hasToSetFocus )
				invokeLaterSetFocusToMainTextComponent();
		}
	}

	protected void setVisibleInvokeLater( boolean value )
	{
		boolean hasToSetFocus = false;
		setVisibleInvokeLater( value, hasToSetFocus );
	}

	protected void setVisibleInvokeLater( boolean value, boolean hasToSetFocus )
	{
		SwingUtilities.invokeLater( () -> {
			setVisible( value );
			if( hasToSetFocus )
				SwingUtilities.invokeLater( () ->
					_completionManager.getInputTextComponent().requestFocus()
											);
			});
	}

	protected boolean setVisibility()
	{
		boolean hasToSetFocus = false;
		if( ( _lastCompletionPrototypes != null ) && ( _lastCompletionPrototypes.length > 0 ) ||
			( _lastParamPrototype != null ) )
		{
			hasToSetFocus = !isVisible();
			setVisibleInvokeLater( true, hasToSetFocus );
		}
		else
		{
			hasToSetFocus = isVisible();
			setVisibleInvokeLater( false, hasToSetFocus );
		}
/*
		if( hasToSetFocus )
			SwingUtilities.invokeLater( () ->
				_completionManager.getInputTextComponent().requestFocus()
										);
*/
		return( hasToSetFocus );
	}

	public void invokeLaterSetFocusToMainTextComponent()
	{
		SwingUtilities.invokeLater( this::setFocusToMainTextComponent );
	}

	public void setFocusToMainTextComponent()
	{
		_completionManager.getInputTextComponent().requestFocus();
	}

	@Override
	public void lineUp()
	{
		_completionDocumentFormatter.lineUp();
	}

	@Override
	public void lineDown()
	{
		_completionDocumentFormatter.lineDown();
	}

	@Override
	public void pageUp()
	{
		_completionDocumentFormatter.pageUp();
	}

	@Override
	public void pageDown()
	{
		_completionDocumentFormatter.pageDown();
	}

	@Override
	public void hideEverything()
	{
		SwingUtilities.invokeLater( () -> {
				_lastCharBounds = null;
				setVisible( false );
		});
	}

	@Override
	public void showCompletionWindow()
	{
		setVisibleInvokeLater( true );
	}

	@Override
	public void hideCompletionTextComponent()
	{
	}

	@Override
	public void showCurrentParameterHelp()
	{
		setVisibleInvokeLater( true );
	}

	@Override
	public void hideCurrentParameterHelp()
	{

	}

	@Override
	public void resetCompletion()
	{

	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		switch( e.getExtendedKeyCode() )
		{
			case KeyEvent.VK_PAGE_UP:	pageUp();	break;
			case KeyEvent.VK_PAGE_DOWN:	pageDown();	break;
			case KeyEvent.VK_UP:	lineUp();	break;
			case KeyEvent.VK_DOWN:	lineDown();	break;
			case KeyEvent.VK_ESCAPE:	_completionManager.escape();	break;
			case KeyEvent.VK_ENTER:
			{
				if( _completionManager.getCompletionWindow().isVisible() )
					_completionManager.selectCurrent();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event)
	{
		if( event.getWheelRotation() < 0 )
			pageUp();
		else if( event.getWheelRotation() > 0 )
			pageDown();
		
		event.consume();
	}

	@Override
	public void locateWindow( Rectangle charBounds )
	{
		if( charBounds != null )
		{
			Point newLocation = calculateNewLocation( charBounds );

			if( ( _lastCharBounds == null ) || ( _lastCharBounds.y != charBounds.y  ) )
				setLocation( newLocation );
		}
	}

	protected Point calculateNewLocation( Rectangle charBounds )
	{
		Dimension screenDimen = ScreenFunctions.getScreenSize( this, true );

		Dimension dimen = getSize();

		int xx = IntegerFunctions.max( IntegerFunctions.min( charBounds.x, screenDimen.width - dimen.width ), 0 );
		int yy = charBounds.y - 13 - dimen.height;

		if( yy < 0 )
			yy = charBounds.y + charBounds.height + 13;

		Point result = new Point( xx, yy );

		return( result );
	}

	@Override
	public void escape()
	{
		hideEverything();
	}

	@Override
	public void changeZoomFactor( double zoomFactor )
	{
		super.changeZoomFactor(zoomFactor);

		reformat();
//		setVisible( false );
	}

	protected void reformat()
	{
		SwingUtilities.invokeLater( () -> {
			_completionDocumentFormatter.reformat();
			if( isCurrentParamActive() )
				_currentParamDocumentAppender.reformat(); } );
	}

	@Override
	public void setVisible( boolean value )
	{
		if( value != isVisible() )
		{
			super.setVisible( value );
//			setAlwaysOnTop(true);
		}
	}

	@Override
	public void linkDetected(LinkServer source, PrototypeForCompletionBase linkObject)
	{
		_selectedParamPrototype = linkObject;
		_completionManager.selectCurrent();
	}

	@Override
	public void focusGained(FocusEvent evt)
	{
		super.focusGained( evt );
 		Component previousFocusedComponent = evt.getOppositeComponent();
/*
		System.out.println( String.format( "%s ------> %s ( %s )",
							ComponentFunctions.instance().getComponentString( previousFocusedComponent ),
							this.getClass().getName(),
							ComponentFunctions.instance().getComponentString( evt.getComponent() ) ));
*/
//		if( !ComponentFunctions.instance().isAnyParentInstanceOf( DesktopCompletionWindowBase.class, previousFocusedComponent )
		if( ComponentFunctions.instance().isAnyParent( _componentFocusedForNotHiding, previousFocusedComponent )
			&& ( ComponentFunctions.instance().getAncestor( previousFocusedComponent ) != null )
			&& !( evt.getComponent() instanceof JScrollBar ) )
		{
			setFocusToMainTextComponent();
//			previousFocusedComponent.requestFocus();
		}
//			SwingUtilities.invokeLater( () -> previousFocusedComponent.requestFocus() );
	}

	protected boolean bigEnoughTimeElapsed( long lastMs, long nowMs )
	{
		return( enoughTimeElapsedGen( lastMs, nowMs, 150 ) );
	}

	protected boolean enoughTimeElapsed( long lastMs, long nowMs )
	{
		return( enoughTimeElapsedGen( lastMs, nowMs, 15 ) );
	}

	protected boolean enoughTimeElapsedGen( long lastMs, long nowMs, long minDiff )
	{
		return( lastMs - nowMs >= minDiff );
	}

	@Override
	public void focusLost(FocusEvent evt)
	{
		super.focusLost(evt);

		Component opposite = evt.getOppositeComponent();
		Component focused = ComponentFunctions.instance().getFocusedComponent();
		long now = System.currentTimeMillis();
		if( (
				( opposite == null ) && ( focused != _componentFocusedForNotHiding ) ||
				!ComponentFunctions.instance().isAnyParent( _componentFocusedForNotHiding, opposite) &&
				!ComponentFunctions.instance().isAnyParent( this, opposite)
			)
		  )
		{
			hideEverything();
		}
		
/*
		System.out.println( String.format( "%s ( %s ) ------> %s",
							this.getClass().getName(),
							ComponentFunctions.instance().getComponentString( evt.getComponent() ),
							ComponentFunctions.instance().getComponentString( opposite ) ));
*/
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel2 = compMapper.mapComponent( jPanel2 );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );
		jScrollPane2 = compMapper.mapComponent( jScrollPane2 );
		jTP_completion = compMapper.mapComponent( jTP_completion );
		jTP_currentParam = compMapper.mapComponent( jTP_currentParam );
	}

	protected void addFocusListenersForMainWindow( Component mainWindow )
	{
		ComponentFunctions.instance().browseComponentHierarchy( mainWindow, (comp) -> { if( comp.getParent() != null ) comp.addFocusListener(_exitFocusListener); return( null ); } );
	}

	protected void removeFocusListenersForMainWindow( Component mainWindow )
	{
		ComponentFunctions.instance().browseComponentHierarchy( mainWindow, (comp) -> { comp.removeFocusListener(_exitFocusListener); return( null ); } );
	}

	public void setComponentForNotHiding( Component comp )
	{
		removeFocusListenersForMainWindow( _componentFocusedForNotHiding );
		_componentFocusedForNotHiding = comp;
		addFocusListenersForMainWindow( _componentFocusedForNotHiding );
	}

	@Override
	public void releaseResources()
	{
		super.releaseResources();

		removeFocusListenersForMainWindow( _componentFocusedForNotHiding );
		removeListenersRecursive( this );
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage(language);

		updateFormatters();
	}

	protected void updateFormatters()
	{
		setListOfAlternativesKeepingSelection(_lastAlternativesForCompletionData);
		setCurrentParamPrototype( _lastCurrentParamForCompletionData );
	}

	protected void invertFormattersColor(ColorInversor colorInversor)
	{
		if( _completionDocumentFormatter != null )
			_completionDocumentFormatter.invertColors(colorInversor);

		if( _currentParamDocumentAppender != null )
			_currentParamDocumentAppender.invertColors(colorInversor);
	}

	@Override
	public void setDarkMode(boolean value, ColorInversor colorInversor)
	{
		super.setDarkMode( value, colorInversor );

		if( this.wasLatestModeDark() != value )
			invertFormattersColor(colorInversor);
	}
}
