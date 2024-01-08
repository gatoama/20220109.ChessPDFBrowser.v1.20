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
package com.frojasg1.chesspdfbrowser.application;

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.chesspdfbrowser.configuration.AppStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.io.file.implementation.PgnChessFile;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.scangames.ScanGamesFunctions;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.chesspdfbrowser.recognizer.store.whole.ChessBoardRecognizerWhole;
import com.frojasg1.chesspdfbrowser.recognizer.threads.notifier.PendingItemsListener;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.chesspdfbrowser.threads.ScanPDFgamesThread;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.exceptions.ValidateException;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.OperationCancellation;
import com.frojasg1.general.progress.UpdatingProgress;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import com.frojasg1.chesspdfbrowser.threads.LoadChessControllerInterface;
import com.frojasg1.general.desktop.view.timer.JLabelTimerBySecondUpdater;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class PDFScanProgressJDial extends InternationalizedJDialog
								implements UpdatingProgress,
												LoadChessControllerInterface
{
	public static final String sa_configurationBaseFileName = "PDFScanProgressWindow";

	protected Component _parent = null;
	
	protected ScanPDFgamesThread _thread = null;
	protected LoadChessControllerInterface _gameLoadController = null;

//	protected long _beginTimeStamp = 0;

	protected int _totalNumberOfPages = 0;

//	protected UpdatingProgress _updatingProgress = null;
	protected OperationCancellation _operationCancellation = null;

	protected int _numberOfEndProgress = 0;

	protected Integer _initialPageToScanForGames = null;
	protected Integer _finalPageToScanForGames = null;

	protected PdfDocumentWrapper _pdfDocument = null;

	protected TagsExtractor _tagsExtractor = null;

	protected String _pdfBaseFileName = null;

	protected ChessGameControllerInterface _chessGameController = null;

	protected ChessBoardRecognizerWhole _recognizerWhole = null;

	protected PendingItemsListener _pendingItemsListener = null;

	protected JLabelTimerBySecondUpdater _labelTimerUpdater = null;

	/**
	 * Creates new form PDFScanProgressJDial
	 */
	public PDFScanProgressJDial(java.awt.Frame parent, boolean modal,
								LoadChessControllerInterface gameLoadController,
								PdfDocumentWrapper document,
								TagsExtractor tagsExtractor,
								String pdfBaseFileName,
								ChessGameControllerInterface chessGameController,
								ImagePositionController imagePositionController,
								ChessBoardRecognizerWhole recognizerWhole )
	{
		super(parent, modal, ApplicationConfiguration.instance());
		initComponents();

		_labelTimerUpdater = createAndInitJLabelTimerBySecondUpdater();

		_recognizerWhole = recognizerWhole;

		_chessGameController = chessGameController;

		_pdfBaseFileName = pdfBaseFileName;
		_tagsExtractor = tagsExtractor;
		_pdfDocument = document;

		_parent = parent;

		addListeners();

		setWindowConfiguration( );

		initializeComponentContents();

		try
		{
			changeLanguage( getAppliConf().getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		_gameLoadController = gameLoadController;
		_totalNumberOfPages = document.getNumberOfPages();

		boolean hasToCancel = false;
		_operationCancellation = new OperationCancellation(hasToCancel);
/*
		GeneralUpdatingProgress gup = new GeneralUpdatingProgress( );
		gup.up_setParentUpdatingProgress( this );
		gup.up_setOperationCancellation( _operationCancellation );
		gup.up_prepareNextSlice( _totalNumberOfPages );
		_updatingProgress = gup;
*/
//		_thread = new ScanPDFgamesThread( this, document, _updatingProgress );
		_thread = new ScanPDFgamesThread( this, document, this,
											ApplicationConfiguration.instance(),
											_tagsExtractor,
											imagePositionController,
											_pdfBaseFileName);

		jPB_progress.setMinimum( 0 );
		jPB_progress.setMaximum( (int) _totalNumberOfPages );

		_thread.start();
	}

	protected JLabelTimerBySecondUpdater createAndInitJLabelTimerBySecondUpdater()
	{
		JLabelTimerBySecondUpdater result = new JLabelTimerBySecondUpdater( jL_timeProgress );
		result.init();

		return( result );
	}

	protected void addListeners()
	{
		_pendingItemsListener = createPendingItemsListener();

		_recognizerWhole.getChessBoardRecognitionTrainingThread().addListenerGen(_pendingItemsListener);
	}

	protected PendingItemsListener createPendingItemsListener()
	{
		return( ( obs, num ) -> newPendingTrainingItems( num ) );
	}

	protected void newPendingTrainingItems( int num )
	{
		SwingUtilities.invokeLater( () -> {
			jLTrainingPending.setText( String.valueOf( num ) );
			jLTrainingPending.repaint();
		});
	}

	protected void initializeComponentContents()
	{
		boolean initialConfiguration = true;
		fillInComboBoxesOfChessLanguage( initialConfiguration );

		newPendingTrainingItems( _recognizerWhole.getChessBoardRecognitionTrainingThread().getNumPendingItems() );

		jCB_experimentalParser.setSelected( getAppliConf().getUseImprovedPdfGameParser() );
	}

	protected void setWindowConfiguration( )
	{
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
		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_configurationBaseFileName,
									this,
									_parent,
									null,
									true,
									null );

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
        jP_language = new javax.swing.JPanel();
        jL_language = new javax.swing.JLabel();
        jCB_languageToParseGamesFrom = new javax.swing.JComboBox();
        jTF_languageToParseGamesFromPiecesString = new javax.swing.JTextField();
        jP_progress = new javax.swing.JPanel();
        jPB_progress = new javax.swing.JProgressBar();
        jL_current = new javax.swing.JLabel();
        jL_currentProgress = new javax.swing.JLabel();
        jL_tiempo = new javax.swing.JLabel();
        jL_timeProgress = new javax.swing.JLabel();
        jLTrainingPending = new javax.swing.JLabel();
        jP_pageRange = new javax.swing.JPanel();
        jL_fromPage = new javax.swing.JLabel();
        jTF_fromPage = new javax.swing.JTextField();
        jL_toPage = new javax.swing.JLabel();
        jTF_toPage = new javax.swing.JTextField();
        jPParser = new javax.swing.JPanel();
        jCB_experimentalParser = new javax.swing.JCheckBox();
        jL_tagsExtractor = new javax.swing.JLabel();
        jB_tagsExtractor = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Scan for games");
        setMinimumSize(new java.awt.Dimension(400, 295));
        setName("PDFscanProgressDialog"); // NOI18N
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
        jB_cancel.setBounds(230, 230, 110, 23);

        jB_start.setText("Start");
        jB_start.setName("jB_start"); // NOI18N
        jB_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_startActionPerformed(evt);
            }
        });
        jPanel1.add(jB_start);
        jB_start.setBounds(80, 230, 110, 23);

        jP_language.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Language of games"));
        jP_language.setName("jP_language"); // NOI18N
        jP_language.setLayout(null);

        jL_language.setText("Language :");
        jL_language.setName("jL_language"); // NOI18N
        jP_language.add(jL_language);
        jL_language.setBounds(10, 20, 110, 14);

        jCB_languageToParseGamesFrom.setName(""); // NOI18N
        jCB_languageToParseGamesFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_languageToParseGamesFromActionPerformed(evt);
            }
        });
        jP_language.add(jCB_languageToParseGamesFrom);
        jCB_languageToParseGamesFrom.setBounds(130, 20, 140, 20);

        jTF_languageToParseGamesFromPiecesString.setName(""); // NOI18N
        jTF_languageToParseGamesFromPiecesString.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTF_languageToParseGamesFromPiecesStringKeyReleased(evt);
            }
        });
        jP_language.add(jTF_languageToParseGamesFromPiecesString);
        jTF_languageToParseGamesFromPiecesString.setBounds(280, 20, 80, 20);

        jPanel1.add(jP_language);
        jP_language.setBounds(0, 90, 390, 50);

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

        jLTrainingPending.setBackground(new java.awt.Color(255, 255, 229));
        jLTrainingPending.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLTrainingPending.setForeground(new java.awt.Color(237, 178, 1));
        jLTrainingPending.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLTrainingPending.setText("9999");
        jLTrainingPending.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jP_progress.add(jLTrainingPending);
        jLTrainingPending.setBounds(325, 20, 55, 30);

        jPanel1.add(jP_progress);
        jP_progress.setBounds(0, 0, 390, 90);

        jP_pageRange.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Range of pages"));
        jP_pageRange.setName("jP_pageRange"); // NOI18N
        jP_pageRange.setLayout(null);

        jL_fromPage.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_fromPage.setText("From page:");
        jL_fromPage.setName("jL_fromPage"); // NOI18N
        jP_pageRange.add(jL_fromPage);
        jL_fromPage.setBounds(10, 17, 110, 14);
        jP_pageRange.add(jTF_fromPage);
        jTF_fromPage.setBounds(120, 13, 50, 20);

        jL_toPage.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_toPage.setText("To page:");
        jL_toPage.setName("jL_toPage"); // NOI18N
        jP_pageRange.add(jL_toPage);
        jL_toPage.setBounds(180, 17, 100, 14);
        jP_pageRange.add(jTF_toPage);
        jTF_toPage.setBounds(280, 13, 50, 20);

        jPanel1.add(jP_pageRange);
        jP_pageRange.setBounds(0, 140, 390, 40);

        jPParser.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Game parser"));
        jPParser.setName("jPParser"); // NOI18N
        jPParser.setLayout(null);

        jCB_experimentalParser.setText("Experimental game extractor");
        jCB_experimentalParser.setName("jCB_experimentalParser"); // NOI18N
        jCB_experimentalParser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_experimentalParserActionPerformed(evt);
            }
        });
        jPParser.add(jCB_experimentalParser);
        jCB_experimentalParser.setBounds(5, 13, 255, 23);

        jL_tagsExtractor.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_tagsExtractor.setText("Tags extractor");
        jL_tagsExtractor.setName("jL_tagsExtractor"); // NOI18N
        jPParser.add(jL_tagsExtractor);
        jL_tagsExtractor.setBounds(260, 15, 100, 14);

        jB_tagsExtractor.setMaximumSize(new java.awt.Dimension(20, 20));
        jB_tagsExtractor.setMinimumSize(new java.awt.Dimension(20, 20));
        jB_tagsExtractor.setName("name=jB_tagsExtractor,icon=com/frojasg1/generic/resources/othericons/replace.png"); // NOI18N
        jB_tagsExtractor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_tagsExtractorActionPerformed(evt);
            }
        });
        jPParser.add(jB_tagsExtractor);
        jB_tagsExtractor.setBounds(363, 12, 20, 20);

        jPanel1.add(jPParser);
        jPParser.setBounds(0, 180, 390, 40);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 390, 260);

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

    private void jCB_languageToParseGamesFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_languageToParseGamesFromActionPerformed
        // TODO add your handling code here:

        updateNewComboSelection( (JComboBox) evt.getSource() );
    }//GEN-LAST:event_jCB_languageToParseGamesFromActionPerformed

    private void jTF_languageToParseGamesFromPiecesStringKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTF_languageToParseGamesFromPiecesStringKeyReleased
        // TODO add your handling code here:

        JTextField jtf = (JTextField) evt.getComponent();

        System.out.println( "Name : " + jtf.getName() );
        System.out.println( "Text : " + jtf.getText() );
    }//GEN-LAST:event_jTF_languageToParseGamesFromPiecesStringKeyReleased

    private void jCB_experimentalParserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_experimentalParserActionPerformed
        // TODO add your handling code here:

		getAppliConf().setUseImprovedPdfGameParser( jCB_experimentalParser.isSelected() );

    }//GEN-LAST:event_jCB_experimentalParserActionPerformed

    private void jB_tagsExtractorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_tagsExtractorActionPerformed
        // TODO add your handling code here:

		boolean openTagRegexConfiguration = true;
		_chessGameController.openConfiguration( openTagRegexConfiguration, null, this);

    }//GEN-LAST:event_jB_tagsExtractorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jB_cancel;
    private javax.swing.JButton jB_start;
    private javax.swing.JButton jB_tagsExtractor;
    private javax.swing.JCheckBox jCB_experimentalParser;
    private javax.swing.JComboBox jCB_languageToParseGamesFrom;
    private javax.swing.JLabel jLTrainingPending;
    private javax.swing.JLabel jL_current;
    private javax.swing.JLabel jL_currentProgress;
    private javax.swing.JLabel jL_fromPage;
    private javax.swing.JLabel jL_language;
    private javax.swing.JLabel jL_tagsExtractor;
    private javax.swing.JLabel jL_tiempo;
    private javax.swing.JLabel jL_timeProgress;
    private javax.swing.JLabel jL_toPage;
    private javax.swing.JProgressBar jPB_progress;
    private javax.swing.JPanel jPParser;
    private javax.swing.JPanel jP_language;
    private javax.swing.JPanel jP_pageRange;
    private javax.swing.JPanel jP_progress;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTF_fromPage;
    private javax.swing.JTextField jTF_languageToParseGamesFromPiecesString;
    private javax.swing.JTextField jTF_toPage;
    // End of variables declaration//GEN-END:variables

	protected void fillInOneComboBoxOfChessLanguage( JComboBox combo,
													Vector< ChessLanguageConfiguration.LanguageConfigurationData > vector,
													String defaultValue )
	{
		ChessLanguageConfiguration.LanguageConfigurationData selectedItem = null;
		if( defaultValue != null )
		{
			selectedItem = ChessLanguageConfiguration.getLanguageConfigurationData( defaultValue );
		}
		else if( ( combo.getModel() != null ) && ( combo.getModel().getSize() > 0 ) )
		{
			selectedItem = ( ChessLanguageConfiguration.LanguageConfigurationData ) combo.getSelectedItem();
		}
		DefaultComboBoxModel<ChessLanguageConfiguration.LanguageConfigurationData> dcbm =
			new DefaultComboBoxModel<ChessLanguageConfiguration.LanguageConfigurationData>( vector );
		combo.setModel( dcbm );
		combo.setSelectedItem( selectedItem );
	}

	protected void fillInComboBoxesOfChessLanguage( boolean initialConfiguration )
	{
		Vector< ChessLanguageConfiguration.LanguageConfigurationData > vector =
			ScanGamesFunctions.instance().getListOfLanguagesToParseFrom( getAppliConf().getLanguage() );

		fillInOneComboBoxOfChessLanguage( jCB_languageToParseGamesFrom, vector,
											( initialConfiguration ?
												getAppliConf().getConfigurationOfChessLanguageToParseTextFrom() :
												null ) );
	}

	protected String getChessLanguageForConfiguration( JComboBox combo )
	{
		String result = null;

		if( combo.getSelectedItem() != null )
		{
			ChessLanguageConfiguration.LanguageConfigurationData lcd =
				(ChessLanguageConfiguration.LanguageConfigurationData) combo.getSelectedItem();

			if( lcd == ChessLanguageConfiguration.getCustomLanguage() )
			{
				result = jTF_languageToParseGamesFromPiecesString.getText();
			}
			else
				result = lcd._languageName;
		}

		return( result );
	}
	
	protected void updateNewComboSelection( JComboBox combo )
	{
		ChessLanguageConfiguration.LanguageConfigurationData lcd =
			(ChessLanguageConfiguration.LanguageConfigurationData) combo.getSelectedItem();

		if( lcd == ChessLanguageConfiguration.getCustomLanguage() )
		{
			jTF_languageToParseGamesFromPiecesString.setText( "" );
			jTF_languageToParseGamesFromPiecesString.setEnabled( true );
			jTF_languageToParseGamesFromPiecesString.requestFocus( true );
		}
		else
		{
			jTF_languageToParseGamesFromPiecesString.setText( lcd._stringOfPieceCodes );
			jTF_languageToParseGamesFromPiecesString.setEnabled( false );
		}
	}

	protected void doCancel()
	{
		if( ( _numberOfEndProgress <= 2 ) && _thread.isAlive() )
		{
			_operationCancellation.setHasToCancel( true );

			_labelTimerUpdater.stop();
		}
		else
		{
			boolean closeWindow = true;
			formWindowClosing( closeWindow );
		}
	}

	protected void applyChanges()
	{
		ApplicationConfiguration appConf = ApplicationConfiguration.instance();
		
		String chessLanguageToParseFrom = getChessLanguageForConfiguration( jCB_languageToParseGamesFrom );
		if( chessLanguageToParseFrom != null )
			appConf.setConfigurationOfChessLanguageToParseTextFrom( chessLanguageToParseFrom );
	}

	@Override
	protected void validateFormChild() throws ValidationException
	{
		validateChessLanguageComboBoxChanges();

		validatePageRange();
	}

	protected void doStart()
	{
		try
		{
			boolean proceed = false;
/*
			try
			{
				validateChessLanguageComboBoxChanges();

				validatePageRange();

				proceed = true;
			}
			catch( ValidateException ve )
			{
				GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, ve.getMessage(),
												getAppStrConf().getProperty( AppStringsConf.CONF_CONFIGURATION_ERROR ),
												DialogsWrapper.ERROR_MESSAGE );

				Component comp = ve.getComponentNotValidated();
				if( comp != null )
				{
					boolean showFocus = true;
					a_intern.setFocus( comp, showFocus );
				}
			}
*/
			validateForm();
			proceed = this.wasSuccessful();

			if( proceed )
			{
				applyChanges();

				jCB_languageToParseGamesFrom.setEnabled( false );
				jTF_languageToParseGamesFromPiecesString.setEnabled( false );
				jB_start.setEnabled( false );

				_initialPageToScanForGames = getInitialPage();
				_finalPageToScanForGames = getFinalPage();

				_thread.setInitialPageToScanForGames( _initialPageToScanForGames );
				_thread.setFinalPageToScanForGames( _finalPageToScanForGames );

				_labelTimerUpdater.start();

				_thread.letsStart();
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected int getConfiguredPage( JTextField jtf, int defaultValue )
	{
		Integer result = null;
		
		try
		{
			result = validatePageNumber( jtf, null );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		if( result == null )
			result = defaultValue;

		return( result );
	}

	protected int getInitialPage()
	{
		return( getConfiguredPage( jTF_fromPage, 1 ) );
	}

	protected int getFinalPage()
	{
		return( getConfiguredPage(jTF_toPage, _pdfDocument.getNumberOfPages() ) );
	}

	@Override
	public void newChessListGameLoaded(List<ChessGame> list, PgnChessFile pgnFile)
	{
		_gameLoadController.newChessListGameLoaded( list, pgnFile );
	}

	@Override
	public void newPdfLoaded(PdfDocumentWrapper pdfDocument)
	{
	}

	@Override
	public void cancelLoading()
	{
		boolean closeWindow = true;
		formWindowClosing( closeWindow );
	}

	@Override
	public void startLoading()
	{
		_gameLoadController.startLoading();
	}

	@Override
	public void endLoading()
	{
		_gameLoadController.endLoading();
	}

	@Override
	public void showLoadingError(String message, String title)
	{
		_gameLoadController.showLoadingError( message, title );
	}

	protected void updateLabels( long completion )
	{
		jL_currentProgress.setText( completion + " / " + _totalNumberOfPages );
/*
		Date now = new Date();
		long elapsedTime = now.getTime() - _beginTimeStamp;

		Date elapsedTimeDate = new Date( elapsedTime );

		String timeString = DateFunctions.instance().formatDate( elapsedTimeDate, "HH:mm:ss",
																	TimeZone.getTimeZone("GMT") );

		jL_timeProgress.setText( timeString );
*/
	}

	@Override
	public void formWindowClosing( boolean closeWindow )
	{
		super.formWindowClosing( closeWindow );

		getAppliConf().setUseImprovedPdfGameParser( jCB_experimentalParser.isSelected() );

		while( _thread.isAlive() )
		{
			try
			{
				Thread.sleep( 100 );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
		_thread = null;

		setVisible( false );
	}

	protected void validatePageRange() throws ValidationException
	{
		Integer fromPage = validatePageNumber( jTF_fromPage, AppStringsConf.CONF_FROM_PAGE_MUST_BE_BETWEEN );
		Integer toPage = validatePageNumber( jTF_toPage, AppStringsConf.CONF_TO_PAGE_MUST_BE_BETWEEN );

		if( ( fromPage != null ) &&
			( toPage != null ) &&
			( fromPage > toPage ) )
		{
			throw( new ValidationException(
						getAppStrConf().getProperty(
											AppStringsConf.CONF_FROM_PAGE_MUST_BE_LESSER_OR_EQUAL_THAN_TO_PAGE ),
						jTF_fromPage )
				);
		}
	}

	protected Integer validatePageNumber( JTextField textField, String exceptionLabel ) throws ValidationException
	{
		Integer result = null;

		if( textField.getText().length() > 0 )
		{
			try
			{
				result = Integer.parseInt( textField.getText() );
			}
			catch( Exception ex )
			{
				throw( new ValidationException(
							getAppStrConf().getProperty( AppStringsConf.CONF_INVALID_INTEGER ) +
								": " + textField.getText(),
							textField )
					);
			}

			if( ( result < 1 ) || ( result > _totalNumberOfPages ) )
				throw( new ValidationException(
							getAppStrConf().getProperty( exceptionLabel ) +
							": [ " + 1 + ", " + _totalNumberOfPages + " ]",
							textField )
					);
		}

		return( result );
	}

	public void validateChessLanguageComboBoxChanges( ) throws ValidationException
	{
		if( jCB_languageToParseGamesFrom.getSelectedItem() != null )
		{
			ChessLanguageConfiguration.LanguageConfigurationData lcd =
				(ChessLanguageConfiguration.LanguageConfigurationData) jCB_languageToParseGamesFrom.getSelectedItem();

			if( lcd == ChessLanguageConfiguration.getCustomLanguage() )
			{
				ChessLanguageConfiguration clc = ChessLanguageConfiguration.getConfiguration( jTF_languageToParseGamesFromPiecesString.getText() );
						
				if( clc == null )
				{
					try
					{
						// if the data to create a new language configuration is not valid, it will throw an exception.
						ChessLanguageConfiguration.createConfiguration(null, jTF_languageToParseGamesFromPiecesString.getText());
					}
					catch( Throwable th )
					{
						th.printStackTrace();

						throw( new ValidationException( th.getMessage(), jTF_languageToParseGamesFromPiecesString ) );
					}
				}
			}
		}
	}

/*
	@Override
	public boolean hasBeenCancelled()
	{
		return( _operationCancellation.getHasToCancel() );
	}
*/
	@Override
	public void changeLanguage( String language ) throws ConfigurationException, InternException
	{
		super.changeLanguage(language);

		jB_cancel.setText( getAppStrConf().getProperty( AppStringsConf.CONF_CANCEL ) );
	}

	protected AppStringsConf getAppStrConf()
	{
		return( AppStringsConf.instance() );
	}

	public ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}

	@Override
	public void up_childStarts()
	{
/*
		if( _beginTimeStamp == 0 )
		{
			Date ts = new Date();
			_beginTimeStamp = ts.getTime();
		}
*/
		updateProgress( 0 );
	}

	@Override
	public void up_updateProgressFromChild(double completedOverOne) throws CancellationException
	{
		updateProgress( (int) (completedOverOne * _totalNumberOfPages) );

		if( completed( completedOverOne ) )
			updateTotalNumberOfPages();
	}

	protected boolean completed( double completedOverOne )
	{
		boolean result = false;

		result = ( Math.abs( 1.0D - completedOverOne ) < 0.00001D );

		return( result );
	}

	protected void updateTotalNumberOfPages()
	{
		setTotalNumberOfPages( _finalPageToScanForGames - _initialPageToScanForGames + 1 );
	}

	public void setTotalNumberOfPages( int totalNumberOfPages )
	{
		_totalNumberOfPages = totalNumberOfPages;

		jPB_progress.setMaximum( _totalNumberOfPages );
	}

	protected void updateProgress( int completion )
	{
		jPB_progress.setValue( (int) completion );

		updateLabels( completion );
	}
	
	@Override
	public void up_childEnds() throws CancellationException
	{
		try
		{
			updateProgress( _totalNumberOfPages );
		}
		catch( Throwable th )
		{}

		if( _numberOfEndProgress > 1 )
		{
			_labelTimerUpdater.stop();

			SwingUtilities.invokeLater( () -> {
				jB_cancel.setText( getAppStrConf().getProperty( AppStringsConf.CONF_CLOSE ) );
				jB_cancel.repaint();
			});
		}

		_numberOfEndProgress++;
	}

	@Override
	public OperationCancellation up_getOperationCancellation()
	{
		return( _operationCancellation );
	}

	@Override
	public void up_setDebug(boolean debug)
	{
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
		jCB_experimentalParser = compMapper.mapComponent(jCB_experimentalParser);
		jCB_languageToParseGamesFrom = compMapper.mapComponent(jCB_languageToParseGamesFrom);
		jL_current = compMapper.mapComponent(jL_current);
		jL_currentProgress = compMapper.mapComponent(jL_currentProgress);
		jL_fromPage = compMapper.mapComponent(jL_fromPage);
		jL_language = compMapper.mapComponent(jL_language);
		jL_tiempo = compMapper.mapComponent(jL_tiempo);
		jL_timeProgress = compMapper.mapComponent(jL_timeProgress);
		jL_toPage = compMapper.mapComponent(jL_toPage);
		jPB_progress = compMapper.mapComponent(jPB_progress);
		jPParser = compMapper.mapComponent(jPParser);
		jP_language = compMapper.mapComponent(jP_language);
		jP_pageRange = compMapper.mapComponent(jP_pageRange);
		jP_progress = compMapper.mapComponent(jP_progress);
		jPanel1 = compMapper.mapComponent(jPanel1);
		jTF_fromPage = compMapper.mapComponent(jTF_fromPage);
		jTF_languageToParseGamesFromPiecesString = compMapper.mapComponent(jTF_languageToParseGamesFromPiecesString);
		jTF_toPage = compMapper.mapComponent(jTF_toPage);
		jLTrainingPending = compMapper.mapComponent(jLTrainingPending);
	}

	@Override
	public void up_setOperationCancellation(OperationCancellation oc) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	protected void removeListeners()
	{
		_recognizerWhole.getChessBoardRecognitionTrainingThread().removeListenerGen(_pendingItemsListener);
	}

	@Override
	public void releaseResources()
	{
		_labelTimerUpdater.release();

		removeListeners();

		super.releaseResources();
	}
}
