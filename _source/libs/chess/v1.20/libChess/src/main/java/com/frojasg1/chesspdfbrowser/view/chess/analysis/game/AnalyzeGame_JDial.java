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
package com.frojasg1.chesspdfbrowser.view.chess.analysis.game;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.chesspdfbrowser.analysis.EngineAnalysisProcessData;
import com.frojasg1.chesspdfbrowser.analysis.SubvariantAnalysisResult;
import com.frojasg1.chesspdfbrowser.analysis.game.AnalyzeGameTask;
import com.frojasg1.chesspdfbrowser.analysis.game.contexts.AnalyzeGameTaskInputContext;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.enginewrapper.action.result.SuccessResult;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineComboControllerBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.WholeEngineMasterComboBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.impl.WholeEngineMasterComboForChessEngineConfiguration;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.SpinnerFunctions;
import com.frojasg1.general.desktop.view.combobox.MasterComboBoxJPanel;
import com.frojasg1.general.desktop.view.timer.JLabelTimerBySecondUpdater;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.general.progress.OperationCancellation;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class AnalyzeGame_JDial extends InternationalizedJDialog<ApplicationInitContext>
{
	public static final String sa_configurationBaseFileName = "AnalyzeGame_JDial";

	protected static final String CONF_ERROR_PROCESSING_SUBVARIANT = AnalyzeGameTask.ERROR_PROCESSING_SUBVARIANT;
	protected static final String CONF_TIMEOUT_WAITING_FOR_ENGINE_ANSWER = AnalyzeGameTask.TIMEOUT_WAITING_FOR_ENGINE_ANSWER;
	protected static final String CONF_EMPTY_ANSWER_FROM_ENGINE = AnalyzeGameTask.EMPTY_ANSWER_FROM_ENGINE;
	protected static final String CONF_CLOSE = "CLOSE";
	protected static final String CONF_ENGINE_NAME_NOT_VALID = "ENGINE_NAME_NOT_VALID";
	protected static final String CONF_YOU_HAVE_TO_CONFIGURE_AND_SELECT_AN_ENGINE = "YOU_HAVE_TO_CONFIGURE_AND_SELECT_AN_ENGINE";
	protected static final String CONF_CANCELLED_BY_USER = "CANCELLED_BY_USER";
	protected static final String CONF_GAME_ANALYZED_SUCCESSFULLY = "GAME_ANALYZED_SUCCESSFULLY";
	protected static final String CONF_ANALYSIS_CHESSPDFBROWSER = "ANALYSIS_CHESSPDFBROWSER";

	protected Component _parent = null;

	protected long _startTimeStamp = 0;

	protected int _totalNumberOfMoves = 0;

	protected OperationCancellation _operationCancellation = null;

	protected AnalyzeGameTask _analyzeGameTask = null;

	protected ChessGame _chessGame;
	protected ChessGame _result;

	protected WholeEngineMasterComboBase _wholeEngineMasterCombo = null;

	protected boolean _hasEnded = false;
	protected boolean _hasStarted = false;

	protected JLabelTimerBySecondUpdater _labelTimerUpdater = null;

	protected boolean _wasError = false;

	/**
	 * Creates new form PDFScanProgressJDial
	 */
	public AnalyzeGame_JDial(java.awt.Frame parent, boolean modal,
							ChessGame cg, ApplicationInitContext appliContext,
							Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		super(parent, modal, ApplicationConfiguration.instance(), appliContext,
			initializationEndCallBack, true);

		_chessGame = cg;

		initComponents();

		_labelTimerUpdater = createAndInitJLabelTimerBySecondUpdater();

		_parent = parent;

		boolean hasToCancel = false;
		_operationCancellation = new OperationCancellation(hasToCancel);

		_analyzeGameTask = createAnalyzeGameTask();

		addListeners();

		initializeOwnContents();

		setWindowConfiguration( );

		initializeComponentContents();

		( new Thread( _analyzeGameTask ) ).start();
	}

	protected JLabelTimerBySecondUpdater createAndInitJLabelTimerBySecondUpdater()
	{
		JLabelTimerBySecondUpdater result = new JLabelTimerBySecondUpdater( jL_timeProgress );
		result.init();

		return( result );
	}

	@Override
	public ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}

	protected AnalyzeGameTask createAnalyzeGameTask()
	{
		return( new AnalyzeGameTask( _operationCancellation,
					getChessEngineConfigurationPersistency(),
					getAppliConf() )
			);
	}

	protected ChessEngineConfigurationPersistency getChessEngineConfigurationPersistency()
	{
		return( getApplicationContext().getChessEngineConfigurationPersistency() );
	}

	protected void setMaximum( int value )
	{
		jPB_progress.setMaximum( value );
	}

	protected void addListeners()
	{
	}

	protected void initializeComponentContents()
	{
		jPB_progress.setMinimum( 0 );
		setMaximum( MoveTreeNodeUtils.instance().getNumberOfPliesOfMainLine(_chessGame ) );

		SpinnerFunctions.instance().setMinValue(jSp_secondsPerMove, 1);
		jSp_secondsPerMove.setValue(1);
	}

	protected void initializeOwnContents()
	{
		createEngineCombobox();
		ContainerFunctions.instance().addComposedComponentToParent(jPanel2, getMasterComboForEngines() );
	}

	protected void createEngineCombobox()
	{
		_wholeEngineMasterCombo = new WholeEngineMasterComboForChessEngineConfiguration( getAppliConf(),
											getChessEngineConfigurationPersistency(), this, null );
		_wholeEngineMasterCombo.init();
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

	protected void setWindowConfiguration()
	{
		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
/*
		a_intern = new JFrameInternationalization(	ApplicationConfiguration.sa_MAIN_FOLDER,
													ApplicationConfiguration.sa_APPLICATION_NAME,
													ApplicationConfiguration.sa_CONFIGURATION_GROUP,
													ApplicationConfiguration.sa_PROPERTIES_PATH_IN_JAR,
													a_configurationBaseFileName,
													this,
													parent,
													a_vectorJpopupMenus,
													false,
													mapRRCI );
*/
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel2, ResizeRelocateItem.RESIZE_TO_RIGHT );
//			mapRRCI.putResizeRelocateComponentItem( jCB_engineName, ResizeRelocateItem.RESIZE_TO_RIGHT );
//			mapRRCI.putResizeRelocateComponentItem( jB_engineConfiguration, ResizeRelocateItem.MOVE_TO_RIGHT );
			mapRRCI.putAll( getMasterComboForEngines().getResizeRelocateInfo() );
		}
		catch( Exception th )
		{
			th.printStackTrace();
		}

			createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_configurationBaseFileName,
									this,
									_parent,
									null,
									true,
									mapRRCI );

		registerInternationalString( CONF_CLOSE, "Close" );
		registerInternationalString( CONF_ENGINE_NAME_NOT_VALID, "Engine name not valid" );
		registerInternationalString( CONF_YOU_HAVE_TO_CONFIGURE_AND_SELECT_AN_ENGINE, "You have to configure and select an engine" );
		registerInternationalString( CONF_CANCELLED_BY_USER, "Cancelled by user" );
		registerInternationalString( CONF_GAME_ANALYZED_SUCCESSFULLY, "Game analyzed successfully" );
		registerInternationalString( CONF_ERROR_PROCESSING_SUBVARIANT, "Error processing subvariant" );
		registerInternationalString( CONF_TIMEOUT_WAITING_FOR_ENGINE_ANSWER, "Timeout waiting for engine answer" );
		registerInternationalString( CONF_EMPTY_ANSWER_FROM_ENGINE, "Empty answer from engine" );
		registerInternationalString( CONF_ANALYSIS_CHESSPDFBROWSER, "Analysis with ChessPdfBrowser" );

//		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jB_cancel = new javax.swing.JButton();
        jB_start = new javax.swing.JButton();
        jP_engine = new javax.swing.JPanel();
        jL_engine = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jP_progress = new javax.swing.JPanel();
        jPB_progress = new javax.swing.JProgressBar();
        jL_current = new javax.swing.JLabel();
        jL_currentProgress = new javax.swing.JLabel();
        jL_tiempo = new javax.swing.JLabel();
        jL_timeProgress = new javax.swing.JLabel();
        jSp_secondsPerMove = new javax.swing.JSpinner();
        jL_secondsPerMove = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Scan for games");
        setMinimumSize(new java.awt.Dimension(400, 295));
        setName("AnalyzeGame_JDial"); // NOI18N
        setResizable(false);
        getContentPane().setLayout(null);

        jPanel1.setMinimumSize(new java.awt.Dimension(390, 220));
        jPanel1.setName("kk"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(390, 220));
        jPanel1.setLayout(null);

        jB_cancel.setText("Cancel");
        jB_cancel.setName("jB_cancel"); // NOI18N
        jB_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_cancelActionPerformed(evt);
            }
        });
        jPanel1.add(jB_cancel);
        jB_cancel.setBounds(195, 180, 110, 23);

        jB_start.setText("Start");
        jB_start.setName("jB_start"); // NOI18N
        jB_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_startActionPerformed(evt);
            }
        });
        jPanel1.add(jB_start);
        jB_start.setBounds(35, 180, 110, 23);

        jP_engine.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Engine to use"));
        jP_engine.setName("jP_engine"); // NOI18N
        jP_engine.setLayout(null);

        jL_engine.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_engine.setText("Engine :");
        jL_engine.setName("jL_engine"); // NOI18N
        jP_engine.add(jL_engine);
        jL_engine.setBounds(10, 25, 70, 14);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(null);
        jP_engine.add(jPanel2);
        jPanel2.setBounds(80, 15, 245, 35);

        jPanel1.add(jP_engine);
        jP_engine.setBounds(0, 90, 340, 55);

        jP_progress.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Progress"));
        jP_progress.setName("jP_progress"); // NOI18N
        jP_progress.setLayout(null);

        jPB_progress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPB_progress.setStringPainted(true);
        jP_progress.add(jPB_progress);
        jPB_progress.setBounds(25, 20, 290, 30);

        jL_current.setText("current :");
        jL_current.setName("jL_current"); // NOI18N
        jP_progress.add(jL_current);
        jL_current.setBounds(25, 60, 60, 20);

        jL_currentProgress.setText("0 / 0");
        jP_progress.add(jL_currentProgress);
        jL_currentProgress.setBounds(95, 60, 90, 20);

        jL_tiempo.setText("Time :");
        jL_tiempo.setName("jL_tiempo"); // NOI18N
        jP_progress.add(jL_tiempo);
        jL_tiempo.setBounds(195, 60, 50, 20);

        jL_timeProgress.setText("0:00:00");
        jL_timeProgress.setName(""); // NOI18N
        jP_progress.add(jL_timeProgress);
        jL_timeProgress.setBounds(255, 60, 60, 20);

        jPanel1.add(jP_progress);
        jP_progress.setBounds(0, 0, 340, 90);
        jPanel1.add(jSp_secondsPerMove);
        jSp_secondsPerMove.setBounds(195, 155, 80, 20);

        jL_secondsPerMove.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_secondsPerMove.setText("Seconds per move :");
        jL_secondsPerMove.setName("jL_secondsPerMove"); // NOI18N
        jPanel1.add(jL_secondsPerMove);
        jL_secondsPerMove.setBounds(5, 155, 185, 14);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 340, 220);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jB_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_cancelActionPerformed
        // TODO add your handling code here:

        doCancel();
    }//GEN-LAST:event_jB_cancelActionPerformed

    private void jB_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_startActionPerformed
        // TODO add your handling code here:

        doStart();
    }//GEN-LAST:event_jB_startActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_cancel;
    private javax.swing.JButton jB_start;
    private javax.swing.JLabel jL_current;
    private javax.swing.JLabel jL_currentProgress;
    private javax.swing.JLabel jL_engine;
    private javax.swing.JLabel jL_secondsPerMove;
    private javax.swing.JLabel jL_tiempo;
    private javax.swing.JLabel jL_timeProgress;
    private javax.swing.JProgressBar jPB_progress;
    private javax.swing.JPanel jP_engine;
    private javax.swing.JPanel jP_progress;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSpinner jSp_secondsPerMove;
    // End of variables declaration//GEN-END:variables

	protected boolean isProceeding()
	{
		return( _hasStarted && wasSuccessful() );
	}

	protected void doCancel()
	{
		if( isProceeding() && !_hasEnded )
		{
			_operationCancellation.setHasToCancel( true );
		}
		else
		{
			boolean closeWindow = true;
			formWindowClosing( closeWindow );
		}
	}

	protected String getSelectedEngineName()
	{
		return( getEngineComboController().getSelectedEngineName() );
	}

	protected void validateEngine() throws ValidationException
	{
		if( getSelectedEngineName() == null )
		{
			String errorText;
			if( getEngineComboBox().getModel().getSize() == 0 )
				errorText = getInternationalString( CONF_YOU_HAVE_TO_CONFIGURE_AND_SELECT_AN_ENGINE );
			else
				errorText = getInternationalString( CONF_ENGINE_NAME_NOT_VALID );

			throw( new ValidationException( errorText, this.getMasterComboForEngines() ) );
		}
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		validateEngine();
	}

	protected void doStart()
	{
		try
		{
			validateForm();

			if( wasSuccessful() )
			{
//				applyChanges();
				jB_start.setEnabled( false );

				String stringForTaggingAnalyzedGames = this.getInternationalString( CONF_ANALYSIS_CHESSPDFBROWSER );
				_analyzeGameTask.start( createAnalyzeGameTaskInputContext(),
										stringForTaggingAnalyzedGames );
				_labelTimerUpdater.start();

				_hasStarted = true;
			}
		}
		catch( Exception th )
		{
			th.printStackTrace();
		}
	}

	protected AnalyzeGameTaskInputContext createAnalyzeGameTaskInputContext()
	{
		AnalyzeGameTaskInputContext result = new AnalyzeGameTaskInputContext();
		
		result.setEngineAnalysisProcessData( createEngineAnalysisProcessData() );
		result.setGameToAnalyze(_chessGame);
		result.setListener( this::newPositionAnalyzed );
		result.setOperationCancellation(_operationCancellation);

		return( result );
	}

	protected Integer getSecondsPerMove()
	{
		return( (Integer) jSp_secondsPerMove.getValue() );
	}

	protected EngineAnalysisProcessData createEngineAnalysisProcessData()
	{
		int numberOfVariants = 1;
		EngineAnalysisProcessData result = new EngineAnalysisProcessData( getSelectedEngineName(),
					numberOfVariants, getSecondsPerMove() * 1000 );

		return( result );
	}

	public void newPositionAnalyzed( MoveTreeNode analyzedNode, int progress,
									int total, SubvariantAnalysisResult sar,
									ChessGame resultGame,
									SuccessResult successResult )
	{
		if( wasCancelled() )
		{
			doEnd( progress, total );
		}
		else if( successResult.isAsuccess() )
		{
			updateProgress( progress, total );

			if( resultGame != null )
				_result = resultGame;

			if( successResult.isLast() )
				doEnd( progress, total );
		}
		else
		{
			HighLevelDialogs.instance().errorMessageDialog(this, getInternationalString( successResult.getErrorString() ) );
			setWasError( true );
			closeWindow();
		}
	}

	protected boolean wasError()
	{
		return( _wasError );
	}

	protected void setWasError( boolean value )
	{
		_wasError = value;
	}

	@Override
	public boolean wasSuccessful()
	{
		return( super.wasSuccessful() && !wasError() && !wasCancelled() );
	}

	protected void updateLabels( long completion, long total )
	{
		jL_currentProgress.setText( completion + " / " + total );
	}

	@Override
	public void formWindowClosing( boolean closeWindow )
	{
		super.formWindowClosing( closeWindow );

		setVisible( false );
	}

	protected void updateProgress( int completion, int total )
	{
		SwingUtilities.invokeLater( () -> {
			setMaximum(total);
			jPB_progress.setValue( (int) completion );
			updateLabels( completion, total );
		});
	}

	public boolean wasCancelled()
	{
		return( _operationCancellation.getHasToCancel() );
	}

	protected void showUserMessageAtEnd()
	{
		String message;
		if( wasCancelled() )
			message = getInternationalString(CONF_CANCELLED_BY_USER);
		else
			message = getInternationalString(CONF_GAME_ANALYZED_SUCCESSFULLY);

		HighLevelDialogs.instance().informationMessageDialog(this, message);
	}

	public void doEnd( int progress, int total )
	{
		_hasEnded = true;

		setWasSuccessful( !wasCancelled() );

		_labelTimerUpdater.stop();

		showUserMessageAtEnd();

		{
			SwingUtilities.invokeLater( () -> {
				jB_cancel.setText( getInternationalString(CONF_CLOSE ) );
				jB_cancel.repaint();
			});
		}
	}

	@Override
	public void formWindowClosingEvent( )
	{
		doCancel();
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jB_cancel = compMapper.mapComponent(jB_cancel);
		jB_start = compMapper.mapComponent(jB_start);
		jL_current = compMapper.mapComponent(jL_current);
		jL_currentProgress = compMapper.mapComponent(jL_currentProgress);
		jL_engine = compMapper.mapComponent(jL_engine);
		jL_tiempo = compMapper.mapComponent(jL_tiempo);
		jL_timeProgress = compMapper.mapComponent(jL_timeProgress);
		jPB_progress = compMapper.mapComponent(jPB_progress);
		jP_engine = compMapper.mapComponent(jP_engine);
		jP_progress = compMapper.mapComponent(jP_progress);
		jPanel1 = compMapper.mapComponent(jPanel1);
	}

	protected void removeListeners()
	{
	}

	@Override
	public void releaseResources()
	{
		removeListeners();

		_labelTimerUpdater.release();

		super.releaseResources();
	}

	public ChessGame getResult()
	{
		return( _result );
	}
}
