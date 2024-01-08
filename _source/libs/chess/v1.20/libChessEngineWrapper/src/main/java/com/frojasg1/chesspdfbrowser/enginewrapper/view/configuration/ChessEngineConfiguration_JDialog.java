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
package com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ValueItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.ConfigurationItemJPanelBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.ConfigurationItemJPanelBuilder;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.items.ConfigurationItemToViewPair;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.scrollpane.ScrollPaneMouseListener;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineConfiguration_JDialog extends InternationalizedJDialog
	implements AcceptCancelRevertControllerInterface
{
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	public static final String a_configurationBaseFileName = "ChessEngineConfiguration_JDialog";

	
	protected ChessEngineConfiguration _model = null;
	protected ChessEngineConfiguration _result = null;

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected List<ConfigurationItemToViewPair> _listOfCiToPanelPairs = null;
	protected MapResizeRelocateComponentItem _ciPanelsMapRRCI = null;
	protected JPanel _optionsJPanel = null;

	protected ConfigurationItemJPanelBuilder _ciPanelBuilder = null;

	protected ComponentListener _viewPortComponentListener = null;

	protected ScrollPaneMouseListener _scrollPaneMouseListener = null;

	/**
	 * Creates new form ChessEngineConfiguration_JDialog
	 */
	public ChessEngineConfiguration_JDialog( JFrame parent, boolean modal,
							BaseApplicationConfigurationInterface applicationConfiguration,
							Consumer<InternationalizationInitializationEndCallback> callbackFun )
	{
		super(parent, modal, applicationConfiguration, null, callbackFun, true );
	}

	public ChessEngineConfiguration_JDialog( JDialog parent, boolean modal,
							BaseApplicationConfigurationInterface applicationConfiguration,
							Consumer<InternationalizationInitializationEndCallback> callbackFun )
	{
		super(parent, modal, applicationConfiguration, null, callbackFun, true );
	}

	public void init( ChessEngineConfiguration model )
	{
		_model = model;
		_ciPanelBuilder = createConfigurationItemJPanelBuilder();

		_result = createResult();

		initComponents();

		initOwnComponents();

		addListeners();

		initContents();

		setWindowConfiguration();
	}

	protected ComponentListener createViewportComponentListener()
	{
		ComponentListener result = new ComponentAdapter() {
			@Override
			public void componentResized( ComponentEvent evt )
			{
//				adjustOptionsJPanelPreferredSize();
			}
		};

		return( result );
	}

	protected void addViewportComponentListener()
	{
		if( _viewPortComponentListener == null )
		{
			_viewPortComponentListener = createViewportComponentListener();
			jScrollPane1.getViewport().addComponentListener(_viewPortComponentListener);
		}
	}

	protected void removeViewportComponentListener()
	{
		if( _viewPortComponentListener == null )
			jScrollPane1.getViewport().removeComponentListener(_viewPortComponentListener);
	}

	protected ConfigurationItemJPanelBuilder createConfigurationItemJPanelBuilder()
	{
		ConfigurationItemJPanelBuilder result = new ConfigurationItemJPanelBuilder();
		result.init();

		return( result );
	}

	protected void addListeners()
	{
//		addListenersForMouseToHierarchy( getContentPane() );
	}

	protected void removeListeners()
	{
		removeViewportComponentListener();
		jScrollPane1.removeMouseWheelListener(_scrollPaneMouseListener);
	}

	protected AcceptCancelRevertPanel createAcceptCancelRevertPanel()
	{
		return( new AcceptCancelRevertPanel( this ) );
	}

	protected JPanel createOptionsJPanel()
	{
		_listOfCiToPanelPairs = Collections.synchronizedList( new ArrayList<>() );
		_ciPanelsMapRRCI = new MapResizeRelocateComponentItem();
		JPanel result = new JPanel() {
			@Override
			public void setBounds( int xx, int yy, int width, int height )
			{
				super.setBounds( xx, yy, width, height );
			}

			@Override
			public void setBounds( Rectangle bounds )
			{
				super.setBounds( bounds );
			}

			@Override
			public void setPreferredSize( Dimension dimen )
			{
				super.setPreferredSize( dimen );
			}
		};
		result.setLayout( null );

		int yy = 0;
		int width = 0;
		for( ConfigurationItem ci: _result.getMap().values() )
		{
			ConfigurationItemToViewPair ciToPanelPair = createCiToPanelPair( ci );
			ConfigurationItemJPanelBase ciPanel = (ConfigurationItemJPanelBase) ciToPanelPair.getValue();
			_listOfCiToPanelPairs.add( ciToPanelPair );

			Dimension size = ciPanel.getInternalSize();
			width = size.width;
			result.add( ciPanel );
			ciPanel.setBounds( 0, yy, width, size.height );
			yy += size.height;

			boolean isAlreadyZoomed = false;
			boolean postponeInitialization = true;
			ResizeRelocateItem rri = ExecutionFunctions.instance().safeFunctionExecution(
								() -> _ciPanelsMapRRCI.putResizeRelocateComponentItem( ciPanel,
													ResizeRelocateItem.FILL_WHOLE_WIDTH,
													postponeInitialization,
													isAlreadyZoomed) );

			_ciPanelsMapRRCI.putAll( ciPanel.getResizeRelocateInfo() );
		}

		result.setPreferredSize( new Dimension( width, yy ) );
		result.setName( "OptionsJPanel" );

		return( result );
	}

	protected ConfigurationItemToViewPair createCiToPanelPair( ConfigurationItem ci )
	{
		ConfigurationItemToViewPair result = _ciPanelBuilder.createConfigurationItemJPanel(ci);

		return( result );
	}

	protected void initOwnComponents()
	{
		_optionsJPanel = createOptionsJPanel();

		jScrollPane1.setViewportView( _optionsJPanel );

		_acceptPanel = createAcceptCancelRevertPanel();
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel4, _acceptPanel );
	}

	protected void initContents()
	{
		if( _listOfCiToPanelPairs != null )
		{
			for( ConfigurationItemToViewPair pair: _listOfCiToPanelPairs )
			{
				( (ValueItem) pair.getValue() ).setValue( ( (ConfigurationItem) pair.getKey() ).getValueWithDefaultValue() );
			}
		}
	}

	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = null;
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jPanel2, ResizeRelocateItem.FILL_WHOLE_WIDTH +
																ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putResizeRelocateComponentItem( _optionsJPanel, ResizeRelocateItem.RESIZE_PROPORTIONAL );

			mapRRCI.putAll( _ciPanelsMapRRCI );

			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_WIDTH +
																ResizeRelocateItem.MOVE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		double zoomFactor = getAppliConf().getZoomFactor();
		boolean activateUndoRedoForTextComponents = true;
		boolean enableTextPopupMenu = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = 1500;

		createInternationalization( getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									LibConstants.sa_PROPERTIES_PATH_IN_JAR,
									a_configurationBaseFileName,
									this,
									getParent(),
									vectorJpopupMenus,
									true,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									zoomFactor,
									activateUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback);
/*
		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									a_configurationBaseFileName,
									this,
									getParent(),
									vectorJpopupMenus,
									true,
									mapRRCI );
*/
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

//		getInternationalization().setMaxWindowHeightNoLimit(false);

//		resizeFrameToContents();

//		SwingUtilities.invokeLater( () -> {
//				setMaximumSize( getSize() );
//				repaint();
//				});

		addViewportComponentListener();
//		adjustOptionsJPanelPreferredSize();
	}

	protected void adjustOptionsJPanelPreferredSize()
	{
		SwingUtilities.invokeLater( () -> { if( _optionsJPanel != null ) _optionsJPanel.setPreferredSize( getOptionsJPanelPreferredSize() ); } );
	}

	protected Dimension getOptionsJPanelPreferredSize()
	{
		int width = jScrollPane1.getViewport().getWidth();

		Dimension result = _optionsJPanel.getPreferredSize();
		result.width = width;

		return( result );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        jPanel3.setLayout(null);

        jPanel1.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        jPanel4.setLayout(null);
        jPanel1.add(jPanel4);
        jPanel4.setBounds(175, 10, 140, 50);

        jPanel3.add(jPanel1);
        jPanel1.setBounds(0, 80, 485, 70);

        jPanel2.setLayout(null);
        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(0, 0, 485, 80);

        jPanel3.add(jPanel2);
        jPanel2.setBounds(0, 0, 485, 80);

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 0, 485, 150);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

	@Override
	protected void validateFormChild() throws ValidationException
	{
		for( ConfigurationItemToViewPair citvPair: _listOfCiToPanelPairs )
			citvPair.validateChanges();
	}
 
	public ChessEngineConfiguration getResult()
	{
		return( _result );
	}

	protected ChessEngineConfiguration createResult()
	{
		ChessEngineConfiguration result = _copier.copy( _model );
		return( result );
	}

	protected void applyChanges()
	{
		for( ConfigurationItemToViewPair citvPair: _listOfCiToPanelPairs )
			citvPair.apply();
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper) {
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel2 = compMapper.mapComponent( jPanel2 );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );

		_optionsJPanel = compMapper.mapComponent( _optionsJPanel );

		for( ConfigurationItemToViewPair viewPair: _listOfCiToPanelPairs )
		{
			viewPair.setValue( compMapper.mapComponent( viewPair.getValue() ) );
		}

		_scrollPaneMouseListener = this.createMouseWheelListener(jScrollPane1);
		_scrollPaneMouseListener.addListeners();
	}

	@Override
	public void revert(InformerInterface panel)
	{
		initContents();
	}

	@Override
	public void accept(InformerInterface panel)
	{
		validateForm();
		if( wasSuccessful() )
		{
			applyChanges();
			formWindowClosing(true);
		}
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		setWasSuccessful( false );

		formWindowClosing(true);
	}

	@Override
	public void releaseResources()
	{
		super.releaseResources();
		removeListeners();
	}
}
