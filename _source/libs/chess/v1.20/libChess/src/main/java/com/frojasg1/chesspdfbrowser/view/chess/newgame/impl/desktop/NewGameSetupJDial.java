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

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineComboControllerBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineMasterComboChangeListener;
import com.frojasg1.chesspdfbrowser.game.ChessGamePlayContext;
import com.frojasg1.chesspdfbrowser.view.chess.newgame.PlayerDataForNewGame;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.panels.AcceptCancelControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelPanel;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Dimension;
import java.util.Vector;
import java.util.function.Consumer;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class NewGameSetupJDial extends InternationalizedJDialog<ApplicationInitContext>
	implements AcceptCancelControllerInterface
{
	public final static String a_configurationBaseFileName = "NewGameSetupJDial";

	public static final String CONF_PLAYER_NAME_CANNOT_BE_EMPTY = PlayerNewGameSetupPanel.CONF_PLAYER_NAME_CANNOT_BE_EMPTY;
	public static final String CONF_IS_NOT_A_VALID_NUMBER = PlayerNewGameSetupPanel.CONF_IS_NOT_A_VALID_NUMBER;
	public static final String CONF_CONF_TOTAL_TIME_TOO_LOW = PlayerNewGameSetupPanel.CONF_TOTAL_TIME_TOO_LOW;
	public static final String CONF_YOU_MUST_SELECT_AN_ENGINE = PlayerNewGameSetupPanel.CONF_YOU_MUST_SELECT_AN_ENGINE;
	public static final String CONF_WHITE_PLAYER = "WHITE_PLAYER";
	public static final String CONF_BLACK_PLAYER = "BLACK_PLAYER";

	protected AcceptCancelPanel _acceptPanel = null;

	protected PlayerNewGameSetupPanel _whitePlayerPanel = null;
	protected PlayerNewGameSetupPanel _blackPlayerPanel = null;

	protected ChessGamePlayContext _previousChessGamePlayContext = null;

	/**
	 * Creates new form NewGameSetupJDial
	 */
	public NewGameSetupJDial(ApplicationInitContext initContext,
							java.awt.Frame parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		super(parent, modal, ApplicationConfiguration.instance(), initContext,
			initializationEndCallBack, true );
	}

	/**
	 * Creates new form NewGameSetupJDial
	 */
	public NewGameSetupJDial(ApplicationInitContext initContext,
							JDialog parent, boolean modal,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		super(parent, modal, ApplicationConfiguration.instance(), initContext,
			initializationEndCallBack, true );
	}

	public void init(ChessGamePlayContext previousChessGamePlayContext)
	{
		_previousChessGamePlayContext = previousChessGamePlayContext;

		initComponents();

		initOwnComponents();

		initContents();

		setWindowConfiguration();

		addListeners();
	}

	protected void addListeners()
	{
		addComboListeners();
	}

	protected void addComboListeners()
	{
		EngineMasterComboChangeListener listener = new EngineMasterComboChangeListener() {
			@Override
			public void addedElement(EngineComboControllerBase sender, String engineName) {
				updateCombosKeepingSelection();
			}

			@Override
			public void removedElement(EngineComboControllerBase sender, String engineName) {
				updateCombosKeepingSelection();
			}

			@Override
			public void modifiedElement(EngineComboControllerBase sender, String engineName) {
				updateCombosKeepingSelection();
			}
		};

		setComboListeners( listener );
	}

	protected void setComboListeners(EngineMasterComboChangeListener listener)
	{
		_whitePlayerPanel.addComboListener(listener);
		_blackPlayerPanel.addComboListener(listener);
	}

	protected void updateCombosKeepingSelection()
	{
		_whitePlayerPanel.getEngineComboController().updateCombosKeepingSelection();
		_blackPlayerPanel.getEngineComboController().updateCombosKeepingSelection();
	}

	protected void initContents()
	{
		if( _previousChessGamePlayContext != null )
			initContents( _previousChessGamePlayContext );
	}

	protected void initContents(ChessGamePlayContext context)
	{
		boolean createNewGame = context.hasToCreateNewGame();
		jRB_createNewGame.setSelected( createNewGame );
		jRB_continueFromCurrentPosition.setSelected( !createNewGame );
		jCB_makeMainLine.setSelected( context.isGameAsMainLine() );
	}

	protected PlayerDataForNewGame getWhitePlayerDataForNewGame()
	{
		return( ( _previousChessGamePlayContext == null ) ?
				null :
				_previousChessGamePlayContext.getWhitePlayerDataForNewGame()
			);
	}

	protected PlayerDataForNewGame getBlackPlayerDataForNewGame()
	{
		return( ( _previousChessGamePlayContext == null ) ?
				null :
				_previousChessGamePlayContext.getBlackPlayerDataForNewGame()
			);
	}

	protected void initOwnComponents()
	{
		_acceptPanel = createAcceptCancelRevertPanel();
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel4, _acceptPanel );

		boolean isWhite = true;
		_whitePlayerPanel = createPlayerPanel(isWhite, getWhitePlayerDataForNewGame() );
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel3, _whitePlayerPanel );

		isWhite = false;
		_blackPlayerPanel = createPlayerPanel(isWhite, getBlackPlayerDataForNewGame() );
		ContainerFunctions.instance().addComponentToCompletelyFillParent( jPanel1, _blackPlayerPanel );
	}

	protected AcceptCancelPanel createAcceptCancelRevertPanel()
	{
		return( new AcceptCancelPanel( this ) );
	}

	protected PlayerNewGameSetupPanel createPlayerPanel( boolean isWhite, PlayerDataForNewGame previousPlayerDataForGame )
	{
		PlayerNewGameSetupPanel result = new PlayerNewGameSetupPanel(getApplicationContext(), this);
		result.init( isWhite, this, this,
					getApplicationContext().getChessEngineConfigurationPersistency()
						.getModelContainer().getComboBoxContent().getListOfItems(),
					previousPlayerDataForGame );

		return( result );
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		SwingUtilities.invokeLater( () -> {
			_whitePlayerPanel.setPlayerColorLabelText( getInternationalString( CONF_WHITE_PLAYER ) );
			_blackPlayerPanel.setPlayerColorLabelText( getInternationalString( CONF_BLACK_PLAYER ) );

			Dimension size = getSize();
			size.width += 2;
			setSize( size );
		} );
	}

	protected void setWindowConfiguration( )
	{
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
		Vector<JPopupMenu> vectorJpopupMenus = null;

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
//			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.RESIZE_TO_RIGHT +
//															ResizeRelocateItem.MOVE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
															ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.MOVE_LEFT_SIDE_PROPORTIONAL +
															ResizeRelocateItem.MOVE_RIGHT_SIDE_PROPORTIONAL );

			mapRRCI.putResizeRelocateComponentItem( jPanel2, ResizeRelocateItem.FILL_WHOLE_WIDTH );
			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.MOVE_MID_HORIZONTAL_SIDE_PROPORTIONAL );

			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( _whitePlayerPanel, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putAll( _whitePlayerPanel.getResizeRelocateInfo() );

			mapRRCI.putResizeRelocateComponentItem( _blackPlayerPanel, ResizeRelocateItem.RESIZE_TO_RIGHT );
			mapRRCI.putAll( _blackPlayerPanel.getResizeRelocateInfo() );
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

		registerInternationalString( CONF_PLAYER_NAME_CANNOT_BE_EMPTY, "Player name cannot be empty" );
		registerInternationalString( CONF_IS_NOT_A_VALID_NUMBER, "Is not a valid number" );
		registerInternationalString( CONF_CONF_TOTAL_TIME_TOO_LOW, "Total time too low" );
		registerInternationalString( CONF_YOU_MUST_SELECT_AN_ENGINE, "You must select an engine" );
		registerInternationalString( CONF_WHITE_PLAYER, "White player" );
		registerInternationalString( CONF_BLACK_PLAYER, "Black player" );

		a_intern.setMaxWindowHeightNoLimit(false );
	}

	@Override
	public void setInitialized()
	{
		setMaximumSize( getSize() );
		a_intern.setMaxWindowHeightNoLimit(false );

		SwingUtilities.invokeLater( () -> {
			Dimension size = getSize();
			size.width -= 2;
			setSize( size );
		} );

		super.setInitialized();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jRB_createNewGame = new javax.swing.JRadioButton();
        jRB_continueFromCurrentPosition = new javax.swing.JRadioButton();
        jCB_makeMainLine = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();

        jToggleButton1.setText("jToggleButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 340, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(340, 0, 340, 210);

        jPanel2.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        jPanel4.setMinimumSize(new java.awt.Dimension(90, 50));
        jPanel4.setPreferredSize(new java.awt.Dimension(90, 50));
        jPanel4.setLayout(null);
        jPanel2.add(jPanel4);
        jPanel4.setBounds(295, 10, 90, 50);

        buttonGroup1.add(jRB_createNewGame);
        jRB_createNewGame.setSelected(true);
        jRB_createNewGame.setText("Create new game");
        jRB_createNewGame.setName("jRB_createNewGame"); // NOI18N
        jPanel2.add(jRB_createNewGame);
        jRB_createNewGame.setBounds(10, 5, 285, 28);

        buttonGroup1.add(jRB_continueFromCurrentPosition);
        jRB_continueFromCurrentPosition.setText("Continue from current position");
        jRB_continueFromCurrentPosition.setName("jRB_continueFromCurrentPosition"); // NOI18N
        jPanel2.add(jRB_continueFromCurrentPosition);
        jRB_continueFromCurrentPosition.setBounds(10, 25, 285, 28);

        jCB_makeMainLine.setText("Make game as main line");
        jCB_makeMainLine.setName("jCB_makeMainLine"); // NOI18N
        jPanel2.add(jCB_makeMainLine);
        jCB_makeMainLine.setBounds(30, 47, 260, 24);

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 210, 680, 70);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 340, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 210, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 0, 340, 210);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCB_makeMainLine;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRB_continueFromCurrentPosition;
    private javax.swing.JRadioButton jRB_createNewGame;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		buttonGroup1 = compMapper.mapComponent( buttonGroup1 );
		jCB_makeMainLine = compMapper.mapComponent( jCB_makeMainLine );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel2 = compMapper.mapComponent( jPanel2 );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
		jRB_continueFromCurrentPosition = compMapper.mapComponent( jRB_continueFromCurrentPosition );
		jRB_createNewGame = compMapper.mapComponent( jRB_createNewGame );
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		_whitePlayerPanel.validateChanges();
		_blackPlayerPanel.validateChanges();
	}

	protected void applyChanges()
	{
		
	}

	@Override
	public void accept(InformerInterface panel) {
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

	public PlayerDataForNewGame getWhitePlayerData()
	{
		return( _whitePlayerPanel );
	}

	public PlayerDataForNewGame getBlackPlayerData()
	{
		return( _blackPlayerPanel );
	}

	public boolean hasToCreateNewGame()
	{
		return( jRB_createNewGame.isSelected() );
	}

	public boolean gameAsMainLine()
	{
		return( jCB_makeMainLine.isSelected() );
	}

	@Override
	public void releaseResources()
	{
		setComboListeners( null );

		super.releaseResources();
	}
}
