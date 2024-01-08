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

import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowView;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowViewController;
import com.frojasg1.chesspdfbrowser.analysis.EngineAnalysisProcessData;
import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.impl.SpinConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.updater.EngineInstanceConfigurationUpdater;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.SpinnerFunctions;
import com.frojasg1.general.desktop.view.buttons.ResizableImageJButton;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.desktop.view.icons.IconResources;
import com.frojasg1.general.desktop.view.scrollpane.ScrollPaneMouseListener;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.transform.TransformerException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineAnalysisJPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
	implements DesktopViewComponent, ComposedComponent,
				AnalysisWindowView
{
	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected LinkedList<SimpleVariantAnalysisJPanel> _simpleVariantPanelList = null;

	protected int _id = -1;

	protected Runnable _closeFunction = null;

	protected ChessEngineConfiguration _engineConf = null;

	protected AnalysisWindowJFrame _parent = null;

	protected MapResizeRelocateComponentItem _analysisPanelsMapRRCI = null;

	protected ScrollPaneMouseListener _scrollPaneMouseListener = null;

	protected MouseListener _leftButtonReleasedMouseListener;

	protected boolean _isFirstTime = true;

	protected boolean _hasToStop = true;

//	protected WholeEngineMasterComboBase _wholeEngineMasterCombo = null;

	/**
	 * Creates new form ChessEngineAnalysisJPanel
	 */
	public ChessEngineAnalysisJPanel(AnalysisWindowJFrame parent)
	{
		super.init();

		_parent = parent;
	}

	protected BaseApplicationConfigurationInterface getAppliConf()
	{
		return( _parent.getAppliConf() );
	}

	public void init( int id, Runnable closeFunction )
	{
		_id = id;
		_closeFunction = closeFunction;

		_simpleVariantPanelList = createLinkedList();

		initComponents();

		_leftButtonReleasedMouseListener = createLeftButtonReleaseMouseListener();

		setWindowConfiguration();
	}

	protected MouseListener createLeftButtonReleaseMouseListener()
	{
		return( new MouseAdapter() {
			@Override
			public void mouseReleased( MouseEvent evt )
			{
				// if leftButton released ...
				if( evt.getButton() == MouseEvent.BUTTON1 )
					updateAnalysisProcess();
			}
		});
	}

	protected <CC> LinkedList<CC> createLinkedList()
	{
		return( new LinkedList<>() );
	}

	public void setScrollPaneMouseWheelListener( ScrollPaneMouseListener scrollPaneMouseListener )
	{
		_scrollPaneMouseListener = scrollPaneMouseListener;
	}

	protected void addListeners()
	{
		SpinnerFunctions.instance().browseSpinnerButtons( jSp_timeToSpendInAnalysis,
															comp -> comp.addMouseListener(_leftButtonReleasedMouseListener) );
		jSl_timeToSpendInAnalysis.addMouseListener(_leftButtonReleasedMouseListener);
	}

	protected void removeListeners()
	{
		SpinnerFunctions.instance().browseSpinnerButtons( jSp_timeToSpendInAnalysis,
															comp -> comp.removeMouseListener(_leftButtonReleasedMouseListener) );
		jSl_timeToSpendInAnalysis.removeMouseListener(_leftButtonReleasedMouseListener);
	}
/*
	protected void createEngineCombobox()
	{
		_wholeEngineMasterCombo = new WholeEngineMasterComboForChessEngineConfiguration( getAppliConf(),
											_chessEngineConfigurationMap, this );
	processConfigurationDialogOnSuccess( ChessEngineConfiguration dialResult );
		_wholeEngineMasterCombo.init();

		getMasterComboForEngines().getCombo().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
				jCB_engineConfigurationsActionPerformed(evt);
            }
        });
	}

	protected WholeEngineMasterComboBase getWholeEngineMasterCombo()
	{
		return( _wholeEngineMasterCombo );
	}

	protected EngineComboControllerBase getEngineComboController()
	{
		return( getWholeEngineMasterCombo().getEngineComboControllerBase() );
	}

	protected MasterComboBoxJPanel getMasterComboForEngines()
	{
		return( getWholeEngineMasterCombo().getMasterComboBoxJPanel() );
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
        jP_engineAnalysis = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jSp_numberOfVariants = new javax.swing.JSpinner();
        jL_numberOfVariants = new javax.swing.JLabel();
        jCB_engineConfigurations = new javax.swing.JComboBox<>();
        jSp_timeToSpendInAnalysis = new javax.swing.JSpinner();
        jL_time = new javax.swing.JLabel();
        jSl_timeToSpendInAnalysis = new javax.swing.JSlider();
        jB_close = new javax.swing.JButton();
        jB_configuration = new javax.swing.JButton();
        jB_stopPlay = new javax.swing.JButton();
        jP_analysisContents = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(705, 120));
        setLayout(null);

        jPanel1.setName("InternalChessEngineAnalysisJPanel"); // NOI18N
        jPanel1.setLayout(null);

        jP_engineAnalysis.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Engine analysis"));
        jP_engineAnalysis.setName("jP_engineAnalysis"); // NOI18N
        jP_engineAnalysis.setLayout(null);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(null);

        jSp_numberOfVariants.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSp_numberOfVariantsStateChanged(evt);
            }
        });
        jPanel3.add(jSp_numberOfVariants);
        jSp_numberOfVariants.setBounds(140, 5, 35, 20);

        jL_numberOfVariants.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_numberOfVariants.setText("Number of variants :");
        jL_numberOfVariants.setName("jL_numberOfVariants"); // NOI18N
        jPanel3.add(jL_numberOfVariants);
        jL_numberOfVariants.setBounds(0, 10, 135, 14);

        jCB_engineConfigurations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_engineConfigurationsActionPerformed(evt);
            }
        });
        jPanel3.add(jCB_engineConfigurations);
        jCB_engineConfigurations.setBounds(180, 5, 165, 20);

        jSp_timeToSpendInAnalysis.setValue(new Integer(50));
        jSp_timeToSpendInAnalysis.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSp_timeToSpendInAnalysisStateChanged(evt);
            }
        });
        jPanel3.add(jSp_timeToSpendInAnalysis);
        jSp_timeToSpendInAnalysis.setBounds(460, 5, 50, 20);

        jL_time.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_time.setText("Time (s):");
        jL_time.setName("jL_time"); // NOI18N
        jPanel3.add(jL_time);
        jL_time.setBounds(374, 10, 85, 14);

        jSl_timeToSpendInAnalysis.setMaximum(1200);
        jSl_timeToSpendInAnalysis.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSl_timeToSpendInAnalysisStateChanged(evt);
            }
        });
        jPanel3.add(jSl_timeToSpendInAnalysis);
        jSl_timeToSpendInAnalysis.setBounds(510, 5, 180, 26);

        jB_close.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_close.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_close.setName("name=jB_close,icon=com/frojasg1/generic/resources/erase/erase.png"); // NOI18N
        jB_close.setPreferredSize(new java.awt.Dimension(20, 20));
        jB_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_closeActionPerformed(evt);
            }
        });
        jPanel3.add(jB_close);
        jB_close.setBounds(725, 5, 20, 20);

        jB_configuration.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_configuration.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_configuration.setName("name=jB_configuration,icon=com/frojasg1/generic/resources/othericons/configuration.png"); // NOI18N
        jB_configuration.setPreferredSize(new java.awt.Dimension(20, 20));
        jB_configuration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_configurationActionPerformed(evt);
            }
        });
        jPanel3.add(jB_configuration);
        jB_configuration.setBounds(350, 5, 20, 20);

        jB_stopPlay.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_stopPlay.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_stopPlay.setName("name=jB_stopPlay,icon=com/frojasg1/generic/resources/othericons/stop.png"); // NOI18N
        jB_stopPlay.setPreferredSize(new java.awt.Dimension(20, 20));
        jB_stopPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_stopPlayActionPerformed(evt);
            }
        });
        jPanel3.add(jB_stopPlay);
        jB_stopPlay.setBounds(690, 5, 20, 20);

        jP_engineAnalysis.add(jPanel3);
        jPanel3.setBounds(5, 15, 750, 35);

        javax.swing.GroupLayout jP_analysisContentsLayout = new javax.swing.GroupLayout(jP_analysisContents);
        jP_analysisContents.setLayout(jP_analysisContentsLayout);
        jP_analysisContentsLayout.setHorizontalGroup(
            jP_analysisContentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );
        jP_analysisContentsLayout.setVerticalGroup(
            jP_analysisContentsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        jP_engineAnalysis.add(jP_analysisContents);
        jP_analysisContents.setBounds(5, 50, 750, 65);

        jPanel1.add(jP_engineAnalysis);
        jP_engineAnalysis.setBounds(0, 0, 760, 120);

        add(jPanel1);
        jPanel1.setBounds(0, 0, 760, 120);
    }// </editor-fold>//GEN-END:initComponents

    private void jB_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_closeActionPerformed
        // TODO add your handling code here:

		closeCurrentPanel();

    }//GEN-LAST:event_jB_closeActionPerformed

    private void jSl_timeToSpendInAnalysisStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSl_timeToSpendInAnalysisStateChanged
        // TODO add your handling code here:

		if( !spinnerAndSliderEquals() )
			jSp_timeToSpendInAnalysis.setValue( jSl_timeToSpendInAnalysis.getValue() );

		if( !MouseFunctions.isMainButtonPressed() )
			updateAnalysisProcess();

    }//GEN-LAST:event_jSl_timeToSpendInAnalysisStateChanged

    private void jSp_timeToSpendInAnalysisStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSp_timeToSpendInAnalysisStateChanged
        // TODO add your handling code here:

		if( !spinnerAndSliderEquals() )
			jSl_timeToSpendInAnalysis.setValue( (Integer) jSp_timeToSpendInAnalysis.getValue() );
    }//GEN-LAST:event_jSp_timeToSpendInAnalysisStateChanged

    private void jB_configurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_configurationActionPerformed
        // TODO add your handling code here:

		launchConfigurationDialog();
    }//GEN-LAST:event_jB_configurationActionPerformed

    private void jSp_numberOfVariantsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSp_numberOfVariantsStateChanged
        // TODO add your handling code here:

		updateNumberOfVariants();

    }//GEN-LAST:event_jSp_numberOfVariantsStateChanged

    private void jB_stopPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_stopPlayActionPerformed
        // TODO add your handling code here:

		if( hasToStop() )
			buttonStopThinking();
		else
			buttonStartThinking();

    }//GEN-LAST:event_jB_stopPlayActionPerformed

    private void jCB_engineConfigurationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_engineConfigurationsActionPerformed
        // TODO add your handling code here:

        updateAnalysisProcess();
    }//GEN-LAST:event_jCB_engineConfigurationsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_close;
    private javax.swing.JButton jB_configuration;
    private javax.swing.JButton jB_stopPlay;
    private javax.swing.JComboBox<String> jCB_engineConfigurations;
    private javax.swing.JLabel jL_numberOfVariants;
    private javax.swing.JLabel jL_time;
    private javax.swing.JPanel jP_analysisContents;
    private javax.swing.JPanel jP_engineAnalysis;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSlider jSl_timeToSpendInAnalysis;
    private javax.swing.JSpinner jSp_numberOfVariants;
    private javax.swing.JSpinner jSp_timeToSpendInAnalysis;
    // End of variables declaration//GEN-END:variables

	protected boolean hasToStop()
	{
		return( _hasToStop );
	}

	protected void setHasToStop( boolean value )
	{
		_hasToStop = value;
	}

	protected void startThinking()
	{
		_parent.getController().startThinking( getId() );
	}

	public AnalysisWindowViewController getController()
	{
		return( _parent.getController() );
	}

	protected void updateNumberOfVariants()
	{
		Integer numberOfVariants = getNumberOfVariants();
		if( numberOfVariants != null )
		{
			if( numberOfVariants > _simpleVariantPanelList.size() )
				addNewVariants();
			else if( numberOfVariants < _simpleVariantPanelList.size() )
				removeVariantsExcess();

			updateAnalysisProcess();
		}
	}

	protected void addNewVariants()
	{
		_analysisPanelsMapRRCI = new MapResizeRelocateComponentItem();
		_parent.setPreventFromRepainting(true);

		Integer numberOfVariants = getNumberOfVariants();
		while( numberOfVariants > _simpleVariantPanelList.size() )
			addNewVariantPanelOnTheFly();

		revalidateAnalysisContents();

		SwingUtilities.invokeLater( () -> {
			updateChessParentEnginePanels();

			boolean setMinSize = false;
			SwingUtilities.invokeLater( () -> _parent.initResizeRelocateItemsOComponentOnTheFly(_simpleVariantPanelList,
																_analysisPanelsMapRRCI, setMinSize,
																null ) );
		});
	}

	protected void updateChessParentEnginePanels()
	{
		_parent.updateChessEnginePanels(null);
	}

	protected void addNewVariantPanelOnTheFly()
	{
		SimpleVariantAnalysisJPanel panel = createSimpleVariantAnalysisJPanel();
		_parent.setIgnoreRepaintRecursive(jP_analysisContents, true);
		jP_analysisContents.add( panel );

		relocateSimpleVariantAnalysisJPanelOnTheFly( panel, _parent.getZoomFactor() );

		_simpleVariantPanelList.add( panel );

		zoomSimpleVariantAnalysisJPanel( panel );
/*
		boolean setMinSize = false;
		_parent.initResizeRelocateItemsOComponentOnTheFly(_simpleVariantPanelList,
															_analysisPanelsMapRRCI, setMinSize,
															null );
*/
		addListeners( panel );
	}

	protected JFrameInternationalization getInternationalization()
	{
		JFrameInternationalization result = null;
		if( _parent != null )
			result = _parent.getInternationalization();

		return( result );
	}

	protected void relocateSimpleVariantAnalysisJPanelOnTheFly( SimpleVariantAnalysisJPanel panel,
																double zoomFactor )
	{
		int totalHeight = getAnalysisContentHeight( panel, zoomFactor );
		int height_ =  zoom( panel.getInternalBounds().height, zoomFactor );
		int yy = totalHeight - height_;

		panel.setBounds( 0, yy, jP_analysisContents.getWidth(), height_ );

		updateAnalysisContentsHeight( totalHeight );
	}

	protected int getAnalysisContentHeight(SimpleVariantAnalysisJPanel panel,
											double zoomFactor )
	{
		int yy = 0;

		for( SimpleVariantAnalysisJPanel pan: _simpleVariantPanelList )
			if( pan != panel )
				yy = yy + pan.getHeight();

		if( panel != null )
		{
			Dimension size = panel.getInternalSize();
			int height_ =  zoom( size.height, zoomFactor );
			yy += height_;
		}

		return( yy );
	}


	protected void updateAnalysisContentsHeight( SimpleVariantAnalysisJPanel panel,
																double zoomFactor )
	{
		int totalHeight = getAnalysisContentHeight(panel, zoomFactor);

		updateAnalysisContentsHeight( totalHeight );
	}

	protected void updateAnalysisContentsHeight( int totalHeight )
	{
		jP_analysisContents.setSize( jP_analysisContents.getWidth(), totalHeight );
	}

	protected int zoom( int value, double zoomFactor )
	{
		return( IntegerFunctions.zoomValueRound(value, zoomFactor) );
	}

	protected SimpleVariantAnalysisJPanel createSimpleVariantAnalysisJPanel()
	{
		SimpleVariantAnalysisJPanel result = new SimpleVariantAnalysisJPanel();
		result.init( this );

		return( result );
	}

	protected void zoomSimpleVariantAnalysisJPanel(SimpleVariantAnalysisJPanel panel)
	{
//		_analysisPanelsMapRRCI = new MapResizeRelocateComponentItem();

		boolean isAlreadyZoomed = true;
		boolean postponeInitialization = true;
		ResizeRelocateItem rri = ExecutionFunctions.instance().safeFunctionExecution(
							() -> _analysisPanelsMapRRCI.putResizeRelocateComponentItem( panel,
												ResizeRelocateItem.FILL_WHOLE_WIDTH,
												postponeInitialization,
												isAlreadyZoomed) );

		_analysisPanelsMapRRCI.putAll( _parent.zoomComponentOnTheFly( panel, rri ).getMapResizeRelocateComponentItem() );
	}

	protected void removeSimpleVariantAnalysisJPanel(SimpleVariantAnalysisJPanel panel)
	{
		removeListeners( panel );
		jP_analysisContents.remove( panel );
		panel.releaseResources();

		ComponentFunctions.instance().browseComponentHierarchy( panel,
			comp -> { _parent.getInternationalization().removeResizeRelocateComponentItem( comp ); return( null ); } );
	}

	protected void removeVariantsExcess()
	{
		Integer numberOfVariants = getNumberOfVariants();
		while( numberOfVariants < _simpleVariantPanelList.size() )
			removeSimpleVariantAnalysisJPanel( _simpleVariantPanelList.removeLast() );

		updateAnalysisContentsHeight(null, 1.0d );

		revalidateAnalysisContents();

		SwingUtilities.invokeLater( () -> updateChessParentEnginePanels() );
	}

	protected void revalidateAnalysisContents()
	{
		jP_analysisContents.revalidate();

		Insets insets = jP_engineAnalysis.getInsets();

		Dimension size = new Dimension( jP_engineAnalysis.getWidth(),
							jPanel3.getHeight() + jP_analysisContents.getHeight() +
							 insets.bottom + insets.top );
		jP_engineAnalysis.setSize( size );
		jPanel1.setSize( size );

		if( !_isFirstTime )
			setSize(size);

		_isFirstTime = false;
	}

	protected void updateAnalysisProcess()
	{
		if( getController() != null )
		{
			getController().setAnalysisProcess( getId(), createEngineAnalysisProcessData() );
			setStartStopButtonNextActionStop();
		}
	}

	protected void launchConfigurationDialog()
	{
		SwingUtilities.invokeLater( () -> launchConfigurationDialog_internal() );
	}

	protected void launchConfigurationDialog_internal()
	{
		if( _engineConf != null )
		{
			EngineInstanceConfigurationUpdater confUpdat = new EngineInstanceConfigurationUpdater( _parent ) {

				@Override
				protected void processOnSuccess( EngineInstanceConfiguration eic, ChessEngineConfiguration dialResult )
				{
					processConfigurationDialogOnSuccess( dialResult );
				}
			};

			confUpdat.init( (ChessEngineConfigurationPersistency) null, getAppliConf() );

			confUpdat.launchConfigurationDialog(_engineConf);
		}
	}

	protected void processConfigurationDialogOnSuccess( ChessEngineConfiguration dialResult )
	{
		updateEngineConfiguration( getId(), dialResult );

		applyNewConfiguration();

		ConfigurationItem ci = dialResult.getMap().get( "MultiPV" );
		if( ci instanceof SpinConfigurationItem )
		{
			SpinConfigurationItem sci = (SpinConfigurationItem) ci;

			setNumberOfVariants( sci.getValueWithDefaultValue() );
		}

		setStartStopButtonNextActionStart();
	}
/*
	protected void processConfigurationDialog( InternationalizationInitializationEndCallback iiec )
	{
		ChessEngineConfiguration_JDialog dial = (ChessEngineConfiguration_JDialog) iiec;

		dial.setVisibleWithLock( true );

		if( dial.wasSuccessful() )
		{
			updateEngineConfiguration( getId(), dial.getResult() );

			applyNewConfiguration();

			ConfigurationItem ci = dial.getResult().getMap().get( "MultiPV" );
			if( ci instanceof SpinConfigurationItem )
			{
				SpinConfigurationItem sci = (SpinConfigurationItem) ci;

				setNumberOfVariants( sci.getValueWithDefaultValue() );
			}
		}
	}
*/
	protected boolean spinnerAndSliderEquals()
	{
		return( jSl_timeToSpendInAnalysis.getValue() == (Integer) jSp_timeToSpendInAnalysis.getValue() );
	}

	public JPanel getParentPanel()
	{
		return( jPanel1 );
	}

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem(jP_engineAnalysis, ResizeRelocateItem.RESIZE_TO_RIGHT +
																ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jB_close, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jP_analysisContents, ResizeRelocateItem.RESIZE_TO_RIGHT +
																ResizeRelocateItem.RESIZE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo()
	{
		return( _resizeRelocateInfo );
	}

	@Override
	public Dimension getInternalSize()
	{
		return( getParentPanel().getSize() );
	}

	@Override
	public Rectangle getInternalBounds()
	{
		return( getParentPanel().getBounds() );
	}

	@Override
	public ChessEngineAnalysisJPanel getComponent()
	{
		return( this );
	}

	@Override
	public void setComponentMapper(ComponentMapper compMapper)
	{
		jB_close = compMapper.mapComponent( jB_close );
		jB_configuration = compMapper.mapComponent( jB_configuration );
		jCB_engineConfigurations = compMapper.mapComponent(jCB_engineConfigurations );
		jL_numberOfVariants = compMapper.mapComponent( jL_numberOfVariants );
		jL_time = compMapper.mapComponent( jL_time );
		jP_analysisContents = compMapper.mapComponent( jP_analysisContents );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jP_engineAnalysis = compMapper.mapComponent(jP_engineAnalysis );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jSl_timeToSpendInAnalysis = compMapper.mapComponent(jSl_timeToSpendInAnalysis );
		jSp_numberOfVariants = compMapper.mapComponent(jSp_numberOfVariants );
		jSp_timeToSpendInAnalysis = compMapper.mapComponent(jSp_timeToSpendInAnalysis );
		jB_stopPlay = compMapper.mapComponent(jB_stopPlay );

		if( !hasBeenAlreadyMapped() )
			addListeners();
/*
		if( getNumberOfVariants() == 0 )
		{
			ThreadFunctions.instance().delayedSafeInvoke(
				() -> SwingUtilities.invokeLater( () -> setNumberOfVariants( 1 ) ),
				1300
										);
		}
*/

		super.setComponentMapper(compMapper);
	}

	protected void setNumberOfVariants( int value )
	{
		jSp_numberOfVariants.setValue( value );
	}

	@Override
	public void resetSubvariants(Integer id)
	{
		for( SimpleVariantAnalysisJPanel simpleVariantPanel: _simpleVariantPanelList )
			simpleVariantPanel.resetSubvariants(id);
	}

	@Override
	public void updateSubvariant(Integer id, int index, SubvariantAnalysisResult subvariant)
	{
		SimpleVariantAnalysisJPanel panel = getPanel( index );
		if( panel != null )
			panel.updateSubvariant(id, index, subvariant);
	}

	protected SimpleVariantAnalysisJPanel getPanel( Integer id )
	{
		SimpleVariantAnalysisJPanel result = null;
		if( ( id != null ) && ( id >= 0 ) && ( id < _simpleVariantPanelList.size() ) )
			result = _simpleVariantPanelList.get(id);

		return( result );
	}

	@Override
	public void releaseResources() {
		_resizeRelocateInfo = null;
		_simpleVariantPanelList.clear();
		_simpleVariantPanelList = null;

		removeListeners(this);
		removeListeners();
		_scrollPaneMouseListener = null;
	}

	protected void removeListeners( Component component )
	{
		_scrollPaneMouseListener.removeListeners(component);
	}

	protected void addListeners( Component component )
	{
		_scrollPaneMouseListener.addListeners(component);
	}

	protected void closeCurrentPanel()
	{
		_closeFunction.run();
	}

	protected void setComboBoxContents( List<String> engineConfigurationItems )
	{
		ComboBoxFunctions.instance().fillComboBox(jCB_engineConfigurations,
				engineConfigurationItems.toArray( new String[engineConfigurationItems.size()]),
				null);
	}

	@Override
	public void updateConfigurationItems(List<String> engineConfigurationItems)
	{
		if( hasToClose( engineConfigurationItems ) )
			closeCurrentPanel();
		else
//			getEngineComboController().updateCombos();
			setComboBoxContents( engineConfigurationItems );
	}

	public String getSelectedEngineConfiguration()
	{
		return( (String) jCB_engineConfigurations.getSelectedItem() );
//		return( getMasterComboForEngines().getSelectedItem() );
	}

	protected boolean selectionExistsInList( List<String> engineConfigurationItems,
											String selectedItem )
	{
		return( engineConfigurationItems.contains( selectedItem ) );
	}

	protected boolean hasToClose( List<String> engineConfigurationItems )
	{
		boolean result = false;
		String selectedItem = getSelectedEngineConfiguration();
		if( selectedItem != null )
			result = !selectionExistsInList( engineConfigurationItems, selectedItem );

		return( result );
	}

	protected int getId()
	{
		return( _id );
	}

	protected Integer getNumberOfVariants()
	{
		return( (Integer) jSp_numberOfVariants.getValue() );
	}

	protected Integer getNumberOfSecondsToSpendInAnalysis()
	{
		return( jSl_timeToSpendInAnalysis.getValue() );
	}

	protected EngineAnalysisProcessData createEngineAnalysisProcessData()
	{
		String engineName = getSelectedEngineConfiguration();
		Integer numberOfVariants = getNumberOfVariants();
		Integer numberOfSecondsToSpendInAnalysis = getNumberOfSecondsToSpendInAnalysis();

		EngineAnalysisProcessData result = new EngineAnalysisProcessData( engineName,
																			numberOfVariants,
																			numberOfSecondsToSpendInAnalysis * 1000 );

		return( result );
	}

	@Override
	public void updateEngineConfiguration(Integer id, ChessEngineConfiguration engineConf)
	{
		_engineConf = engineConf;
	}

	protected void applyNewConfiguration()
	{
		String errorString = null;
		try
		{
			if( getController() != null )
				getController().applyEngineConfiguration( getId(), _engineConf );
		}
		catch( IOException|TransformerException ex1 )
		{
			ex1.printStackTrace();
			errorString = _parent.createCustomInternationalString( AnalysisWindowJFrame.CONF_ERROR_WHEN_SAVING_CONFIGURATION, ex1.getMessage() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			errorString = _parent.createCustomInternationalString( AnalysisWindowJFrame.CONF_INTERNAL_ERROR, ex.getMessage() );
		}

		if( errorString != null )
			HighLevelDialogs.instance().errorMessageDialog( _parent, errorString );
	}

	protected void buttonStartThinking()
	{
		startThinking();
		setStartStopButtonNextActionStop();
	}

	protected void setStartStopButtonNextAction( boolean nextValueForHasToStop,
												String imageResourceName )
	{
		if( jB_stopPlay instanceof ResizableImageJButton )
		{
			ResizableImageJButton imageButton = (ResizableImageJButton) jB_stopPlay;

			imageButton.setImageResource(imageResourceName );
			setHasToStop(nextValueForHasToStop);
		}
	}

	protected void setStartStopButtonNextActionStop()
	{
		setStartStopButtonNextAction( true, IconResources.STOP_ICON_RESOURCE );
	}

	protected void setStartStopButtonNextActionStart()
	{
		setStartStopButtonNextAction( false, IconResources.PLAY_ICON_RESOURCE );
	}

	protected void buttonStopThinking()
	{
		stopThinking();
		setStartStopButtonNextActionStart();
	}

	protected void stopThinking()
	{
		_parent.getController().stop( getId() );
	}

	@Override
	public void setBounds( Rectangle bounds )
	{
		super.setBounds( bounds );
	}

	@Override
	public void setBounds( int xx, int yy, int width, int height )
	{
		super.setBounds( xx, yy, width, height );
	}

	@Override
	public void setSize( Dimension dimen )
	{
		super.setSize( dimen );
	}
}
