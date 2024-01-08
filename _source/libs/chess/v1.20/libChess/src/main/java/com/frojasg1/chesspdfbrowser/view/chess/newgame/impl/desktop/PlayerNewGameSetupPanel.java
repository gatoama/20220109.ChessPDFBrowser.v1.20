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
package com.frojasg1.chesspdfbrowser.view.chess.newgame.impl.desktop;

import com.frojasg1.applications.common.components.internationalization.window.GenericValidator;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.updater.EngineInstanceConfigurationUpdater;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineComboControllerBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.WholeEngineMasterComboBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.impl.WholeEngineMasterComboForChessEngineConfiguration;
import com.frojasg1.chesspdfbrowser.view.chess.newgame.PlayerDataForNewGame;
import com.frojasg1.general.desktop.generic.view.DesktopViewComponent;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.SpinnerFunctions;
import com.frojasg1.general.desktop.view.combobox.MasterComboBoxJPanel;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.desktop.view.zoom.mapper.ComposedComponent;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.view.ViewComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineMasterComboChangeListener;
import com.frojasg1.general.ClassFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class PlayerNewGameSetupPanel extends com.frojasg1.general.desktop.view.panels.CustomJPanelBase
	implements DesktopViewComponent, ComposedComponent, PlayerDataForNewGame
{
	public static final String CONF_PLAYER_NAME_CANNOT_BE_EMPTY = "PLAYER_NAME_CANNOT_BE_EMPTY";
	public static final String CONF_IS_NOT_A_VALID_NUMBER = "IS_NOT_A_VALID_NUMBER";
	public static final String CONF_TOTAL_TIME_TOO_LOW = "TOTAL_TIME_TOO_LOW";
	public static final String CONF_YOU_MUST_SELECT_AN_ENGINE = "YOU_MUST_SELECT_AN_ENGINE";

	protected String _playerColorLabelText;
	protected List<String> _engineNames;

	protected MapResizeRelocateComponentItem _resizeRelocateInfo = null;

	protected InternationalizedStringConf _internationalStringsServer = null;
	protected GenericValidator _genValidator = null;

	protected boolean _isWhite = false;

	protected PlayerDataForNewGame _previousPlayerDataForGame = null;

	protected ApplicationInitContext _applicationContext = null;

	protected JDialog _parentJDial = null;
	protected JFrame _parentJFrame = null;

	protected WholeEngineMasterComboBase _wholeEngineMasterCombo = null;

	/**
	 * Creates new form PlayerNewGameSetupPanel
	 */
	public PlayerNewGameSetupPanel(ApplicationInitContext applicationContext, JDialog parentJDial) {
		_applicationContext = applicationContext;
		_parentJDial = parentJDial;
	}

	public PlayerNewGameSetupPanel(ApplicationInitContext applicationContext, JFrame parentJFrame) {
		_applicationContext = applicationContext;
		_parentJFrame = parentJFrame;
	}

	public void init( boolean isWhite,
						InternationalizedStringConf internationalStringsServer,
						GenericValidator genValidator,
						List<String> engineNames,
						PlayerDataForNewGame previousPlayerDataForGame )
	{
		super.init();

		_previousPlayerDataForGame = previousPlayerDataForGame;
		_isWhite = isWhite;
		setEngineNames( engineNames );
		_internationalStringsServer = internationalStringsServer;
		_genValidator = genValidator;

		initComponents();

		initOwnComponents();

		initContents();

		setWindowConfiguration();

		updateVisibilityOfComponents();
	}

	public void addComboListener( EngineMasterComboChangeListener listener )
	{
		getEngineComboController().addListener( listener );
	}

	protected void initOwnComponents()
	{
		createEngineCombobox();
		ContainerFunctions.instance().addComposedComponentToParent(jPanel1, getMasterComboForEngines() );
	}

	protected ApplicationInitContext getApplicationContext()
	{
		return( _applicationContext );
	}

	protected ApplicationConfiguration getAppliConf()
	{
		return( getApplicationContext().getApplicationConfiguration() );
	}

	protected ChessEngineConfigurationPersistency getChessEngineConfigurationPersistency()
	{
		return( getApplicationContext().getChessEngineConfigurationPersistency() );
	}

	protected void limitRange( JSpinner spinner, Integer min, Integer max )
	{
		SpinnerFunctions.instance().limitRange( spinner, min, max );
	}

	protected void configureComponents()
	{
		limitSpinners();
	}

	protected void limitSpinners()
	{
		limitRange( jSp_hours, 0, 11 );
		limitRange( jSp_minutes, 0, 59 );
		limitRange( jSp_seconds, 0, 59 );
		limitRange( jSp_increment, 0, null );
		limitRange( jSp_secondsPerMove, 0, null );
	}

	protected void initContents()
	{
		if( _previousPlayerDataForGame != null )
		{
			if( _previousPlayerDataForGame.isHuman() )
			{
				jRB_human.setSelected( true );

				jTF_name.setText( _previousPlayerDataForGame.getPlayerName() );
			}
			else if( _previousPlayerDataForGame.isEngine() )
			{
				jRB_engine.setSelected( true );

				getEngineComboBox().setSelectedItem( _previousPlayerDataForGame.getSelectedEngineName() );
			}

			Integer elo = _previousPlayerDataForGame.getElo();
			if( elo != null )
				jTF_elo.setText( elo.toString() );

			if( _previousPlayerDataForGame.isTimePerMove() )
			{
				jRB_timePerMove.setSelected(true);
				jSp_secondsPerMove.setValue( _previousPlayerDataForGame.getSecondsPerMove() );
			}
			else if( _previousPlayerDataForGame.isTimePlusIncrement() )
			{
				jRB_timePlusIncrement.setSelected(true);
				jSp_increment.setValue( _previousPlayerDataForGame.getIncrement() );

				int totalSeconds = _previousPlayerDataForGame.getTotalTime();
				int secs = totalSeconds % 60;
				int mins = ( totalSeconds / 60 ) % 60;
				int hours = totalSeconds / 3600;

				jSp_hours.setValue( hours );
				jSp_minutes.setValue( mins );
				jSp_seconds.setValue( secs );
			}

			updateVisibilityOfComponents();
		}
	}

	@Override
	public boolean isWhite()
	{
		return( _isWhite );
	}

	public void setPlayerColorLabelText( String playerColorLabelText )
	{
		_playerColorLabelText = playerColorLabelText;
		initPlayerColorText();
	}

	protected void setEngineNames( List<String> engineNames )
	{
		if( engineNames != null )
			_engineNames = engineNames;
		else
			_engineNames = new ArrayList<>();
	}

	protected void initPlayerColorText()
	{
		jL_playerColor.setText( _playerColorLabelText );
	}

	protected void setWindowConfiguration( )
	{
		_resizeRelocateInfo = new MapResizeRelocateComponentItem();
		MapResizeRelocateComponentItem mapRRCI = _resizeRelocateInfo;
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putResizeRelocateComponentItem( jL_playerColor, ResizeRelocateItem.FILL_WHOLE_WIDTH );
			mapRRCI.putResizeRelocateComponentItem( jP_player, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jP_timeControl, ResizeRelocateItem.RESIZE_TO_RIGHT );

			mapRRCI.putResizeRelocateComponentItem( jTF_name, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT );
//			mapRRCI.putResizeRelocateComponentItem( jCB_engineName, ResizeRelocateItem.RESIZE_TO_RIGHT );
//			mapRRCI.putResizeRelocateComponentItem( jB_engineConfiguration, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putAll( getMasterComboForEngines().getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( jRB_timePlusIncrement, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jRB_timePerMove, ResizeRelocateItem.RESIZE_TO_RIGHT );
			
			mapRRCI.putResizeRelocateComponentItem( jL_time, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jSp_hours, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jSp_minutes, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jSp_seconds, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jL_increment, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jSp_increment, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jL_seconds, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jL_time1, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jSp_secondsPerMove, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putResizeRelocateComponentItem( jL_seconds1, ResizeRelocateItem.MOVE_TO_RIGHT );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected ViewComponent getParentWindow()
	{
		ViewComponent result = ClassFunctions.instance().cast( _parentJDial, ViewComponent.class );
		if( result == null )
			result = ClassFunctions.instance().cast( _parentJFrame, ViewComponent.class );

		return( result );
	}

	protected void createEngineCombobox()
	{
		_wholeEngineMasterCombo = new WholeEngineMasterComboForChessEngineConfiguration( getAppliConf(),
											getChessEngineConfigurationPersistency(), getParentWindow(), null );
		_wholeEngineMasterCombo.init();
//		_engineComboController = createEngineComboControllerBase();
	}

	public EngineComboControllerBase getEngineComboController()
	{
		return( _wholeEngineMasterCombo.getEngineComboController() );
	}

	protected MasterComboBoxJPanel getMasterComboForEngines()
	{
		return( _wholeEngineMasterCombo.getMasterComboBoxJPanel() );
	}

	protected JComboBox getEngineComboBox()
	{
		return( getMasterComboForEngines().getCombo() );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup_playerType = new javax.swing.ButtonGroup();
        buttonGroup_timeControlModality = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        jL_playerColor = new javax.swing.JLabel();
        jP_player = new javax.swing.JPanel();
        jRB_human = new javax.swing.JRadioButton();
        jRB_engine = new javax.swing.JRadioButton();
        jTF_name = new javax.swing.JTextField();
        jL_name = new javax.swing.JLabel();
        jL_elo = new javax.swing.JLabel();
        jTF_elo = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jP_timeControl = new javax.swing.JPanel();
        jRB_timePlusIncrement = new javax.swing.JRadioButton();
        jRB_timePerMove = new javax.swing.JRadioButton();
        jL_time = new javax.swing.JLabel();
        jSp_hours = new javax.swing.JSpinner();
        jSp_minutes = new javax.swing.JSpinner();
        jSp_seconds = new javax.swing.JSpinner();
        jL_increment = new javax.swing.JLabel();
        jSp_increment = new javax.swing.JSpinner();
        jL_seconds = new javax.swing.JLabel();
        jL_time1 = new javax.swing.JLabel();
        jSp_secondsPerMove = new javax.swing.JSpinner();
        jL_seconds1 = new javax.swing.JLabel();

        setLayout(null);

        jPanel3.setLayout(null);

        jL_playerColor.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jL_playerColor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jL_playerColor.setText("White player");
        jL_playerColor.setName("jL_playerColor"); // NOI18N
        jPanel3.add(jL_playerColor);
        jL_playerColor.setBounds(5, 5, 330, 20);

        jP_player.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Player"));
        jP_player.setName("jP_player"); // NOI18N
        jP_player.setLayout(null);

        buttonGroup_playerType.add(jRB_human);
        jRB_human.setSelected(true);
        jRB_human.setText("Human");
        jRB_human.setName("jRB_human"); // NOI18N
        jRB_human.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_engineActionPerformed(evt);
            }
        });
        jP_player.add(jRB_human);
        jRB_human.setBounds(10, 25, 70, 23);

        buttonGroup_playerType.add(jRB_engine);
        jRB_engine.setText("Engine");
        jRB_engine.setName("jRB_engine"); // NOI18N
        jRB_engine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_engineActionPerformed(evt);
            }
        });
        jP_player.add(jRB_engine);
        jRB_engine.setBounds(10, 45, 70, 23);
        jP_player.add(jTF_name);
        jTF_name.setBounds(135, 15, 195, 20);

        jL_name.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_name.setText("Name :");
        jL_name.setName("jL_name"); // NOI18N
        jP_player.add(jL_name);
        jL_name.setBounds(79, 20, 55, 14);

        jL_elo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_elo.setText("ELO :");
        jL_elo.setName("jL_elo"); // NOI18N
        jP_player.add(jL_elo);
        jL_elo.setBounds(85, 50, 50, 14);
        jP_player.add(jTF_elo);
        jTF_elo.setBounds(135, 45, 45, 20);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jP_player.add(jPanel1);
        jPanel1.setBounds(135, 10, 200, 35);

        jPanel3.add(jP_player);
        jP_player.setBounds(0, 30, 340, 80);

        jP_timeControl.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Time control"));
        jP_timeControl.setName("jP_timeControl"); // NOI18N
        jP_timeControl.setLayout(null);

        buttonGroup_timeControlModality.add(jRB_timePlusIncrement);
        jRB_timePlusIncrement.setSelected(true);
        jRB_timePlusIncrement.setText("Time + increment");
        jRB_timePlusIncrement.setName("jRB_timePlusIncrement"); // NOI18N
        jRB_timePlusIncrement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_timePerMoveActionPerformed(evt);
            }
        });
        jP_timeControl.add(jRB_timePlusIncrement);
        jRB_timePlusIncrement.setBounds(10, 20, 140, 23);

        buttonGroup_timeControlModality.add(jRB_timePerMove);
        jRB_timePerMove.setText("Time per move");
        jRB_timePerMove.setName("jRB_timePerMove"); // NOI18N
        jRB_timePerMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_timePerMoveActionPerformed(evt);
            }
        });
        jP_timeControl.add(jRB_timePerMove);
        jRB_timePerMove.setBounds(10, 70, 140, 23);

        jL_time.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_time.setText("Time :");
        jL_time.setName("jL_time"); // NOI18N
        jP_timeControl.add(jL_time);
        jL_time.setBounds(160, 25, 50, 14);
        jP_timeControl.add(jSp_hours);
        jSp_hours.setBounds(214, 20, 35, 20);
        jP_timeControl.add(jSp_minutes);
        jSp_minutes.setBounds(255, 20, 35, 20);
        jP_timeControl.add(jSp_seconds);
        jSp_seconds.setBounds(295, 20, 35, 20);

        jL_increment.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_increment.setText("Increment :");
        jL_increment.setName("jL_increment"); // NOI18N
        jP_timeControl.add(jL_increment);
        jL_increment.setBounds(120, 50, 90, 14);
        jP_timeControl.add(jSp_increment);
        jSp_increment.setBounds(215, 45, 40, 20);

        jL_seconds.setText("seconds");
        jL_seconds.setName("jL_seconds"); // NOI18N
        jP_timeControl.add(jL_seconds);
        jL_seconds.setBounds(260, 50, 70, 14);

        jL_time1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_time1.setText("Time :");
        jL_time1.setName("jL_time1"); // NOI18N
        jP_timeControl.add(jL_time1);
        jL_time1.setBounds(155, 75, 55, 14);
        jP_timeControl.add(jSp_secondsPerMove);
        jSp_secondsPerMove.setBounds(215, 70, 40, 20);

        jL_seconds1.setText("seconds");
        jL_seconds1.setName("jL_seconds"); // NOI18N
        jP_timeControl.add(jL_seconds1);
        jL_seconds1.setBounds(260, 75, 70, 14);

        jPanel3.add(jP_timeControl);
        jP_timeControl.setBounds(0, 110, 340, 100);

        add(jPanel3);
        jPanel3.setBounds(0, 0, 340, 210);
    }// </editor-fold>//GEN-END:initComponents

    private void jRB_engineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_engineActionPerformed
        // TODO add your handling code here:

		updateVisibilityOfPlayerTypeComponents();

    }//GEN-LAST:event_jRB_engineActionPerformed

    private void jRB_timePerMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_timePerMoveActionPerformed
        // TODO add your handling code here:

		updateVisibilityOfTimeControlModalityComponents();

    }//GEN-LAST:event_jRB_timePerMoveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup_playerType;
    private javax.swing.ButtonGroup buttonGroup_timeControlModality;
    private javax.swing.JLabel jL_elo;
    private javax.swing.JLabel jL_increment;
    private javax.swing.JLabel jL_name;
    private javax.swing.JLabel jL_playerColor;
    private javax.swing.JLabel jL_seconds;
    private javax.swing.JLabel jL_seconds1;
    private javax.swing.JLabel jL_time;
    private javax.swing.JLabel jL_time1;
    private javax.swing.JPanel jP_player;
    private javax.swing.JPanel jP_timeControl;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRB_engine;
    private javax.swing.JRadioButton jRB_human;
    private javax.swing.JRadioButton jRB_timePerMove;
    private javax.swing.JRadioButton jRB_timePlusIncrement;
    private javax.swing.JSpinner jSp_hours;
    private javax.swing.JSpinner jSp_increment;
    private javax.swing.JSpinner jSp_minutes;
    private javax.swing.JSpinner jSp_seconds;
    private javax.swing.JSpinner jSp_secondsPerMove;
    private javax.swing.JTextField jTF_elo;
    private javax.swing.JTextField jTF_name;
    // End of variables declaration//GEN-END:variables

	protected EngineInstanceConfigurationUpdater createEngineInstanceConfigurationUpdater()
	{
		EngineInstanceConfigurationUpdater result = new EngineInstanceConfigurationUpdater( (JFrame) null );
		
		result.setParentJDial( _parentJDial );
		result.setParentJFrame(_parentJFrame );

		return( result );
	}

	protected void launchConfigurationDialog()
	{
		EngineInstanceConfigurationUpdater confUpdat = createEngineInstanceConfigurationUpdater();
		
		confUpdat.init( getChessEngineConfigurationPersistency(), getAppliConf() );

		confUpdat.launchConfigurationDialog( getSelectedEngineName() );
	}

	@Override
	public PlayerNewGameSetupPanel getComponent() {
		return( this );
	}

	@Override
	public void releaseResources() {
		_internationalStringsServer = null;
		_genValidator = null;
	}

	@Override
	public MapResizeRelocateComponentItem getResizeRelocateInfo() {
		return( _resizeRelocateInfo );
	}

	public JPanel getParentPanel()
	{
		return( jPanel3 );
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
	public void setComponentMapper(ComponentMapper compMapper)
	{
		buttonGroup_playerType = compMapper.mapComponent( buttonGroup_playerType );
		buttonGroup_timeControlModality = compMapper.mapComponent( buttonGroup_timeControlModality );
//		jCB_engineName = compMapper.mapComponent( jCB_engineName );
		jL_elo = compMapper.mapComponent( jL_elo );
		jL_increment = compMapper.mapComponent( jL_increment );
		jL_name = compMapper.mapComponent( jL_name );
		jL_playerColor = compMapper.mapComponent( jL_playerColor );
		jL_seconds = compMapper.mapComponent( jL_seconds );
		jL_seconds1 = compMapper.mapComponent( jL_seconds1 );
		jL_time = compMapper.mapComponent( jL_time );
		jL_time1 = compMapper.mapComponent( jL_time1 );
		jP_player = compMapper.mapComponent( jP_player );
		jP_timeControl = compMapper.mapComponent( jP_timeControl );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jRB_engine = compMapper.mapComponent( jRB_engine );
		jRB_human = compMapper.mapComponent( jRB_human );
		jRB_timePerMove = compMapper.mapComponent( jRB_timePerMove );
		jRB_timePlusIncrement = compMapper.mapComponent( jRB_timePlusIncrement );
		jSp_seconds = compMapper.mapComponent(jSp_seconds );
		jSp_hours = compMapper.mapComponent( jSp_hours );
		jSp_increment = compMapper.mapComponent( jSp_increment );
		jSp_minutes = compMapper.mapComponent( jSp_minutes );
		jSp_secondsPerMove = compMapper.mapComponent( jSp_secondsPerMove );
		jTF_elo = compMapper.mapComponent( jTF_elo );
		jTF_name = compMapper.mapComponent( jTF_name );
//		jB_engineConfiguration = compMapper.mapComponent( jB_engineConfiguration );

//		setComboBoxContents( _engineNames );
		configureComponents();
		if( !hasBeenAlreadyMapped() )
			initContents();

		super.setComponentMapper(compMapper);
	}
/*
	protected void setComboBoxContents( List<String> engineConfigurationItems )
	{hh
		ComboBoxFunctions.instance().fillComboBox(jCB_engineName,
				engineConfigurationItems.toArray( new String[engineConfigurationItems.size()]),
				null);
	}
*/
	protected void updateVisibilityOfComponents()
	{
		updateVisibilityOfPlayerTypeComponents();
		updateVisibilityOfTimeControlModalityComponents();
	}

	protected boolean thereIsAnyEngineConfigured()
	{
		return( ( _engineNames != null ) && !_engineNames.isEmpty() );
	}

	protected void updateVisibilityOfPlayerTypeComponents()
	{
		setVisibility( jRB_human.isSelected(), jTF_name );
//		setVisibility( jRB_engine.isSelected(), jCB_engineName, jB_engineConfiguration );
		setVisibility( jRB_engine.isSelected(), jPanel1 );
		jRB_engine.setEnabled( thereIsAnyEngineConfigured() );

		jRB_timePerMove.setEnabled( !jRB_human.isSelected() );
		if( jRB_human.isSelected() )
			jRB_timePlusIncrement.setSelected(true);
	}

	protected void updateVisibilityOfTimeControlModalityComponents()
	{
		setVisibility(jRB_timePlusIncrement.isSelected(), jL_time, jSp_hours, jSp_minutes,
						jSp_seconds, jL_increment, jSp_increment, jL_seconds );
		setVisibility( jRB_timePerMove.isSelected(), jL_time1, jSp_secondsPerMove, jL_seconds1 );
	}

	protected void setVisibility( boolean visible, Component ... comps )
	{
		for( Component comp: comps )
			comp.setVisible( visible );
	}

	public void validateChanges() throws ValidationException
	{
		validatePlayerData();
		validateTimeControl();
	}

	public String createCustomInternationalString( String label, Object ... args )
	{
		return( _internationalStringsServer.createCustomInternationalString(label, args) );
	}

	public String getInternationalString( String label )
	{
		return( _internationalStringsServer.getInternationalString(label) );
	}

	@Override
	public String getPlayerName()
	{
		return( jTF_name.getText() );
	}

	protected String getPlayerNameError()
	{
		String result = null;
		if( getPlayerName().isEmpty() )
			result = "Error";

		return( result );
	}

	protected boolean hasElo()
	{
		return( !jTF_elo.getText().isEmpty() );
	}

	@Override
	public Integer getElo()
	{
		Integer result = IntegerFunctions.parseInt( jTF_elo.getText() );
		return( result );
	}

	protected String getEloError()
	{
		String result = null;

		if( hasElo() )
		{
			Integer elo = getElo();
			if( ( elo == null ) || ( elo < 0 ) )
				result = "Error";
		}

		return( result );
	}

	protected void validateElo() throws ValidationException
	{
		_genValidator.validation( () -> getEloError(), jTF_elo,
									errStr -> getInternationalString( CONF_IS_NOT_A_VALID_NUMBER ) );
	}

	protected void validateHumanPlayerType() throws ValidationException
	{
		_genValidator.validation( () -> getPlayerNameError(), jTF_name,
									errStr -> getInternationalString( CONF_PLAYER_NAME_CANNOT_BE_EMPTY ) );
		validateElo();
	}

	public String getSelectedEngineName()
	{
//		return( (String) jCB_engineName.getSelectedItem() );
		return( getMasterComboForEngines().getSelectedItem() );
	}

	public String getSelectedEngineError()
	{
		String result = null;
		if( getSelectedEngineName() == null )
			result = "Error";
		
		return( result );
	}

	protected void validateEnginePlayerType() throws ValidationException
	{
		_genValidator.validation( () -> getSelectedEngineError(), getEngineComboBox(),
									errStr -> getInternationalString( CONF_YOU_MUST_SELECT_AN_ENGINE ) );
		validateElo();
	}

	protected void validatePlayerData() throws ValidationException
	{
		if( isHuman() )
			validateHumanPlayerType();
		
		if( isEngine() )
			validateEnginePlayerType();
	}

	@Override
	public boolean isHuman()
	{
		return( jRB_human.isSelected() );
	}

	@Override
	public boolean isEngine()
	{
		return( jRB_engine.isSelected() );
	}

	@Override
	public boolean isTimePlusIncrement()
	{
		return( jRB_timePlusIncrement.isSelected() );
	}

	@Override
	public boolean isTimePerMove()
	{
		return( jRB_timePerMove.isSelected() );
	}

	protected void validateTimeControl() throws ValidationException
	{
		if( isTimePlusIncrement() )
			validateTimePlusIncrement();

		if( isTimePerMove() )
			validateTimePerMove();
	}

	protected String getSpinnerContentsError( JSpinner spinner, Integer lowestValue, Integer highestValue )
	{
		String result = null;
		
		if( spinner != null )
		{
			Integer value = (Integer) spinner.getValue();
			if( ( lowestValue != null ) && ( value < lowestValue ) )
				result = "Error";
			else if( (highestValue != null ) && ( value > highestValue ) )
				result = "Error";
		}

		return( result );
	}

	protected int getSeconds( JSpinner spinner, int factor )
	{
		return( getEffectiveValue( spinner ) * factor );
	}

	protected int getEffectiveValue( JSpinner spinner )
	{
		Integer result = null;
		if( spinner != null )
			result = (Integer) spinner.getValue();

		if( result == null )
			result = 0;

		return( result );
	}

	@Override
	public int getIncrement()
	{
		return( getEffectiveValue( jSp_increment ) );
	}

	@Override
	public int getTotalTime()
	{
		int result = getSeconds( jSp_hours, 3600 ) + getSeconds( jSp_minutes, 60 ) +
					getSeconds( jSp_seconds, 1 );

		return( result );
	}

	protected String getTotalTimeError()
	{
		String result = null;
		if( getTotalTime() <= 1 )
			result = "Error";

		return( result );
	}

	protected void validateTimePlusIncrement() throws ValidationException
	{
		_genValidator.validation( () -> getSpinnerContentsError( jSp_hours, 0, 23),
									jSp_hours,
									errStr -> getInternationalString( CONF_IS_NOT_A_VALID_NUMBER ) );
		_genValidator.validation( () -> getSpinnerContentsError( jSp_minutes, 0, 59),
									jSp_minutes,
									errStr -> getInternationalString( CONF_IS_NOT_A_VALID_NUMBER ) );
		_genValidator.validation( () -> getSpinnerContentsError( jSp_seconds, 0, 59),
									jSp_seconds,
									errStr -> getInternationalString( CONF_IS_NOT_A_VALID_NUMBER ) );
		_genValidator.validation( () -> getTotalTimeError(),
									jSp_seconds,
									errStr -> getInternationalString( CONF_TOTAL_TIME_TOO_LOW ) );
	}

	protected void validateTimePerMove() throws ValidationException
	{
		_genValidator.validation( () -> getSpinnerContentsError( jSp_secondsPerMove, 2, 1000000),
									jSp_secondsPerMove,
									errStr -> getInternationalString( CONF_IS_NOT_A_VALID_NUMBER ) );
	}

	@Override
	public int getSecondsPerMove()
	{
		return( getEffectiveValue(jSp_secondsPerMove) );
	}
}
