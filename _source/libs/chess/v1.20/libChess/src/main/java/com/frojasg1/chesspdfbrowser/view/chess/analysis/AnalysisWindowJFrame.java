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
package com.frojasg1.chesspdfbrowser.view.chess.analysis;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowView;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowViewController;
import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.scrollpane.ScrollPaneMouseListener;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.threads.ThreadFunctions;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AnalysisWindowJFrame extends InternationalizedJFrame
	implements AnalysisWindowView
{
	protected static final String a_configurationBaseFileName = "AnalysisWindowJFrame";

	public final static String CONF_ERROR_WHEN_SAVING_CONFIGURATION = "ERROR_WHEN_SAVING_CONFIGURATION";
	public final static String CONF_INTERNAL_ERROR = "INTERNAL_ERROR";
	protected static final String CONF_ANALYSIS_WINDOW = "ANALYSIS_WINDOW";


	protected JPanel _analysisPanel = null;

	protected Map< Integer, ChessEngineAnalysisJPanel > _map = null;

	protected int _currentNewIndex = 0;

	protected AnalysisWindowViewController _controller = null;

	protected List<String> _engineConfigurationItems = null;

	protected MapResizeRelocateComponentItem _analysisPanelsMapRRCI = null;

	protected ScrollPaneMouseListener _scrollPaneMouseListener = null;

	protected ChangeListener _viewPortChangeListener = (evt) -> viewportChange();

	protected int _viewportVisibleRectWidth = 0;

	/**
	 * Creates new form AnalysisWindowView
	 */
	public AnalysisWindowJFrame(BaseApplicationConfigurationInterface applicationConfiguration )
	{
		super( applicationConfiguration );
	}

	public void init( AnalysisWindowViewController controller )
	{
		_map = createMap();
		_controller = controller;
		_controller.setView( this );

		initComponents();

		initOwnComponents();

		addListeners();

		initContents();

		setWindowConfiguration();
	}

	@Override
	public ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}

	protected void initOwnComponents()
	{
		_analysisPanel = new JPanel() {

			@Override
			public void setSize( Dimension size )
			{
				super.setSize(size);
			}

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
		};
		_analysisPanel.setLayout(null);

		jScrollPane1.setViewportView(_analysisPanel);
	}

	protected void initContents()
	{
		jCB_alwaysOnTop.setSelected( getAppliConf().getChessAnalysisFrameAlwaysOnTop() );
        setAlwaysOnTop( jCB_alwaysOnTop.isSelected() );
	}

	public AnalysisWindowViewController getController() {
		return _controller;
	}

	protected Map<Integer, ChessEngineAnalysisJPanel> createMap()
	{
		return( new HashMap<>() );
	}

	protected void addListeners()
	{
		_scrollPaneMouseListener = this.createMouseWheelListener(jScrollPane1);
		_scrollPaneMouseListener.addListeners();

		jScrollPane1.getViewport().addChangeListener(  _viewPortChangeListener );
	}

	protected boolean viewportWidthHasChanged()
	{
		boolean result = false;
		int viewportVisibleRectWidth = this.getViewWidth();
		if( _viewportVisibleRectWidth != viewportVisibleRectWidth )
		{
			_viewportVisibleRectWidth = viewportVisibleRectWidth;
			result = true;
		}

		return( result );
	}

	protected void viewportChange()
	{
		if( viewportWidthHasChanged() )
			updateAnalysisPanelWidth(null);
	}

	protected void setWindowConfiguration( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = null;
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel2, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_WIDTH +
																ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.FILL_WHOLE_PARENT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = false;
		double zoomFactor = getAppliConf().getZoomFactor();
		boolean activateUndoRedoForTextComponents = true;
		boolean enableTextPopupMenu = true;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = null;

		createInternationalization( getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
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

		this.registerInternationalString( CONF_ERROR_WHEN_SAVING_CONFIGURATION, "Error when saving configuration: $1" );
		this.registerInternationalString( CONF_INTERNAL_ERROR, "Internal error when applying configuration: $1" );
		this.registerInternationalString( CONF_ANALYSIS_WINDOW, "Analysis Window" );
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
//		setMaximumSize( getSize() );
//		getInternationalization().setMaxWindowHeightNoLimit(false);

		updateChessEnginePanels(null);
	}


	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jB_add = new javax.swing.JButton();
        jCB_alwaysOnTop = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(790, 260));
        setName(""); // NOI18N
        getContentPane().setLayout(null);

        jPanel2.setLayout(null);

        jPanel1.setMinimumSize(new java.awt.Dimension(735, 190));
        jPanel1.setLayout(null);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(720, 120));
        jScrollPane1.setName(""); // NOI18N
        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(0, 0, 735, 115);

        jPanel2.add(jPanel1);
        jPanel1.setBounds(0, 25, 735, 115);

        jB_add.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_add.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_add.setName("name=jB_add,icon=com/frojasg1/generic/resources/addremovemodify/add.png"); // NOI18N
        jB_add.setPreferredSize(new java.awt.Dimension(20, 20));
        jB_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_addActionPerformed(evt);
            }
        });
        jPanel2.add(jB_add);
        jB_add.setBounds(5, 0, 20, 20);

        jCB_alwaysOnTop.setText("Always on top");
        jCB_alwaysOnTop.setName("jCB_alwaysOnTop"); // NOI18N
        jCB_alwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_alwaysOnTopActionPerformed(evt);
            }
        });
        jPanel2.add(jCB_alwaysOnTop);
        jCB_alwaysOnTop.setBounds(70, 0, 200, 23);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 735, 140);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jB_addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_addActionPerformed
        // TODO add your handling code here:

		addEmptyEngineAnalysis();

    }//GEN-LAST:event_jB_addActionPerformed

    private void jCB_alwaysOnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_alwaysOnTopActionPerformed
        // TODO add your handling code here:

        setAlwaysOnTop( jCB_alwaysOnTop.isSelected() );
		applyChangesAlwaysOnTop();

    }//GEN-LAST:event_jCB_alwaysOnTopActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_add;
    private javax.swing.JCheckBox jCB_alwaysOnTop;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jB_add = compMapper.mapComponent( jB_add );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );

		if( !hasBeenAlreadyMapped() )
			addListeners();
	}

	@Override
	public void resetSubvariants(Integer id)
	{
		ChessEngineAnalysisJPanel engineAnalysisJPanel = _map.get( id );
		if( engineAnalysisJPanel != null )
			engineAnalysisJPanel.resetSubvariants( id );
	}

	@Override
	public void updateSubvariant(Integer id, int index, SubvariantAnalysisResult subvariant) {
		ChessEngineAnalysisJPanel engineAnalysisJPanel = _map.get( id );
		if( engineAnalysisJPanel != null )
			engineAnalysisJPanel.updateSubvariant( id, index, subvariant );
	}

	protected void applyChanges()
	{
		applyChangesAlwaysOnTop();
	}

	protected void applyChangesAlwaysOnTop()
	{
		getAppliConf().setChessAnalysisFrameAlwaysOnTop( jCB_alwaysOnTop.isSelected( ) );
	}

	@Override
	public void releaseResources()
	{
		applyChanges();

		super.releaseResources();

		removeListeners();

		_analysisPanel = null;
		_map.clear();
		_map = null;
	}

	protected ChessEngineAnalysisJPanel createEngineAnalysisPanel(Integer id)
	{
		ChessEngineAnalysisJPanel result = new ChessEngineAnalysisJPanel(this);
		result.setScrollPaneMouseWheelListener(_scrollPaneMouseListener);

		result.init( id, () -> removeEngineAnalysisPanel(id) );

		return( result );
	}

	protected void removeEngineAnalysisPanel( Integer id )
	{
		ChessEngineAnalysisJPanel panel = _map.remove(id);
		if( panel != null )
		{
			_analysisPanel.remove(panel);
			_analysisPanel.revalidate();

			removeListeners(panel);

			updateChessEnginePanels(null);

			getController().closeAnalysisProcess(id);
		}
	}
/*
	protected Rectangle getBoundsForNewPanel( ChessEngineAnalysisJPanel panel )
	{
		Dimension dimen = panel.getInternalSize();
		Rectangle result = new Rectangle( 0, 0, dimen.width, dimen.height );

		double unzoomFactor = 1.0d / getZoomFactor();
		int yy = 0;
		for( ChessEngineAnalysisJPanel pan: _map.values() )
		{
			int unzoomedHeight = zoom( pan.getInternalBounds().height, unzoomFactor );
			yy += unzoomedHeight;
		}

		result.y = yy;

		return( result );
	}
*/
	protected void addEmptyEngineAnalysis()
	{
		setPreventFromRepainting(true);

		ChessEngineAnalysisJPanel panel = createEngineAnalysisPanel(_currentNewIndex);
		this.setIgnoreRepaintRecursive(panel, true);

//		Rectangle bounds = getBoundsForNewPanel( panel );
		_analysisPanel.add( panel );
		addListeners( panel );

		updateChessEnginePanels(panel);
		_map.put( _currentNewIndex++, panel );

//		panel.setBounds( bounds );

		zoomEngineAnalysisPanels( panel );

		boolean setMinSize = false;
		if( _engineConfigurationItems != null )
			panel.updateConfigurationItems( _engineConfigurationItems );

		SwingUtilities.invokeLater( () -> {

			initResizeRelocateItemsOComponentOnTheFly(_map.values(), _analysisPanelsMapRRCI, setMinSize,
	//			() -> ThreadFunctions.instance().delayedInvokeEventDispatchThread(
					()-> panel.setNumberOfVariants(1)//,
	//				100 )
			);

			setScrollToBottom();
		});
	}

	protected void setScrollToBottom()
	{
		JScrollBar vsb = jScrollPane1.getVerticalScrollBar();
		vsb.setValue( vsb.getMaximum() - vsb.getVisibleAmount() );
	}

	protected void zoomEngineAnalysisPanels(ChessEngineAnalysisJPanel panel)
	{
		_analysisPanelsMapRRCI = new MapResizeRelocateComponentItem();

		boolean isAlreadyZoomed = true;
		boolean postponeInitialization = true;
		ResizeRelocateItem rri = ExecutionFunctions.instance().safeFunctionExecution(
							() -> _analysisPanelsMapRRCI.putResizeRelocateComponentItem( panel,
												ResizeRelocateItem.FILL_WHOLE_WIDTH,
												postponeInitialization,
												isAlreadyZoomed) );

		_analysisPanelsMapRRCI.putAll( zoomComponentOnTheFly( panel, rri ).getMapResizeRelocateComponentItem() );
	}

	protected int zoom( int value, double zoomFactor )
	{
		return( IntegerFunctions.zoomValueRound(value, zoomFactor) );
	}

	@Override
	public void updateConfigurationItems(List<String> engineConfigurationItems) {
		_engineConfigurationItems = engineConfigurationItems;

		for( ChessEngineAnalysisJPanel engineAnalysisPanel: _map.values() )
			engineAnalysisPanel.updateConfigurationItems(engineConfigurationItems);
	}

	@Override
	public void updateEngineConfiguration(Integer id, ChessEngineConfiguration engineConf)
	{
		ChessEngineAnalysisJPanel item = _map.get(id);
		if( item != null )
			item.updateEngineConfiguration(id, engineConf);
	}

	protected int getViewWidth()
	{
		return( jScrollPane1.getViewport().getVisibleRect().width );
	}
/*
	protected void relocatePanels( double zoomFactorForLinePanels )
	{
		int width = jPanel3.getWidth();

		Dimension size = _nameJPanel.getInternalSize();
		_nameJPanel.setBounds( 0, 0, size.width, size.height );
		int yy = size.height;

		for( LineOfTagsJPanel lineJPanel: getSortedListOfLineJPanels() )
		{
			size = lineJPanel.getInternalSize();
			int height_ = size.height;

			if( ( a_intern != null ) && !alreadyInitialized( lineJPanel ) )
				height_ = zoom( size.height, zoomFactorForLinePanels );

			lineJPanel.setBounds( 0, yy, width, height_ );
			lineJPanel.updateIndex();

			yy += height_;
		}

		jPanel3.setBounds( 0, yy, jPanel3.getWidth(), jPanel3.getHeight() );
//		_acceptPanel.setBounds( 0, 0, jPanel4.getWidth(), jPanel4.getHeight() );
	}
*/
	public void updateChessEnginePanels(ChessEngineAnalysisJPanel panel)
	{
		AtomicInteger totalHeight = new AtomicInteger(0);

		_map.entrySet().stream().sorted( (e1, e2) -> e1.getKey().compareTo( e2.getKey() ) )
			.map( e -> e.getValue() )
			.forEach( (p) ->
				{
					Dimension dimen = p.getSize();
					//ViewFunctions.instance().getNewDimension( p.getInternalSize(), getZoomFactor() );
					p.setBounds( 0, totalHeight.getAndAdd(dimen.height),+
								dimen.width, dimen.height );
				}
					);

		if( panel != null )
		{
			Dimension internalDimen = ViewFunctions.instance().getNewDimension( panel.getInternalSize(), getZoomFactor() );
			panel.setBounds( 0, totalHeight.getAndAdd(internalDimen.height),+
									internalDimen.width, internalDimen.height );
		}

		SwingUtilities.invokeLater( () -> updateAnalysisPanelWidth(panel) );
	}

	@Override
	public void formWindowClosing( boolean closeWindow )
	{
		super.formWindowClosing(false);

		setState( Frame.ICONIFIED );
	}

	protected void removeListeners()
	{
		_scrollPaneMouseListener.dispose();

		jScrollPane1.getViewport().removeChangeListener(_viewPortChangeListener);
	}

	protected void updateAnalysisPanelWidth(ChessEngineAnalysisJPanel panel)
	{
		AtomicInteger height = new AtomicInteger(0);
		AtomicInteger width = new AtomicInteger(getViewWidth() - 5);

		AtomicBoolean wasPresent = new AtomicBoolean(false);
		_map.entrySet().stream().sorted( (e1, e2) -> e1.getKey().compareTo( e2.getKey() ) )
			.map( e -> e.getValue() )
			.forEach( (p) -> {
					if( p == panel )
						wasPresent.set(true);
//					setMax( width, p.getMinimumSize().width );
					height.addAndGet( p.getSize().height );
						}
					);

		if( !wasPresent.get() && ( panel != null ) )
		{
			Dimension internalDimen = panel.getSize();			
//			setMax( width, internalDimen.width );
			height.addAndGet( internalDimen.height );
		}

		Dimension dimen = new Dimension( width.get(), height.get() );
		if( !dimen.equals( _analysisPanel.getPreferredSize() ) )
		{
			_analysisPanel.setPreferredSize( dimen );
			_analysisPanel.setSize( dimen );

			jScrollPane1.setViewportView(_analysisPanel);
		}
	}

	public void revalidateChild()
	{
		updateAnalysisPanelWidth(null);
	}

	protected void setMax( AtomicInteger ai, int value )
	{
		ai.set( IntegerFunctions.max( ai.get(), value ) );
	}

	protected void removeListeners( Component component )
	{
		_scrollPaneMouseListener.removeListeners(component);
	}

	protected void addListeners( Component component )
	{
		_scrollPaneMouseListener.addListeners(component);
	}

	protected void setTitle()
	{
		setTitle( getInternationalString( CONF_ANALYSIS_WINDOW ) );
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage( language );

		setTitle();
	}
}
