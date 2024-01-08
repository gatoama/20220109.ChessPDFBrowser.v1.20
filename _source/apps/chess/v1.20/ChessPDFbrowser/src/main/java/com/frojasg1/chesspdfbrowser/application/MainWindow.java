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

import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterListener;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterObserved;
import com.frojasg1.chesspdfbrowser.analysis.AnalysisWindowViewController;
import com.frojasg1.chesspdfbrowser.analysis.impl.AnalysisWindowViewControllerImpl;
import com.frojasg1.chesspdfbrowser.configuration.AppStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessGamePositionException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessMoveException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessPieceCreationException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessWriterException;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.io.file.implementation.PgnChessFile;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.PGNChessGameParser;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.RawChessGameParser;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.rawparser3.RawImprovedChessGameParser3;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.EnglishChessConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.libpdf.threads.LoadPDFThread;
import com.frojasg1.chesspdfbrowser.threads.LoadPGNThread;
import com.frojasg1.chesspdfbrowser.threads.SavePGNThread;
import com.frojasg1.chesspdfbrowser.view.chess.ChessBoardPanel;
import com.frojasg1.chesspdfbrowser.view.chess.ChessGameListTable;
import com.frojasg1.chesspdfbrowser.view.chess.ChessTreeGameTextPane;
import com.frojasg1.chesspdfbrowser.view.chess.editcomment.EditCommentFrame;
import com.frojasg1.general.desktop.view.panels.NavigatorControllerInterface;
import com.frojasg1.general.desktop.view.panels.NavigatorJPanel;
import com.frojasg1.chesspdfbrowser.view.chess.edittags.EditTAGsJFrame;
import com.frojasg1.chesspdfbrowser.view.chess.initialposition.InitialPositionDialog;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessMoveGenerator;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.impl.ImagePositionControllerBase;
import com.frojasg1.chesspdfbrowser.game.ChessGamePlayContext;
import com.frojasg1.chesspdfbrowser.game.player.PlayerContextBase;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.view.chess.multiwindowmanager.DetachedGameWindow;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.dialogs.DialogsWrapper;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import com.frojasg1.chesspdfbrowser.view.chess.multiwindowmanager.MultiwindowGameManager;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.clipboard.SystemClipboard;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.view.about.GenericAboutJDialog;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.view.ViewComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import com.frojasg1.chesspdfbrowser.threads.LoadChessControllerInterface;
import com.frojasg1.chesspdfbrowser.view.chess.analysis.AnalysisWindowJFrame;
import com.frojasg1.chesspdfbrowser.view.chess.analysis.game.AnalyzeGameAction;
import com.frojasg1.chesspdfbrowser.view.chess.gamedata.GameDataJDialog;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessBoardImages;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessFigure;
import com.frojasg1.chesspdfbrowser.view.chess.newgame.impl.desktop.NewGameCreatorDesktopImpl;
import com.frojasg1.general.DateFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.icons.ZoomIconBuilder;
import com.frojasg1.general.desktop.view.zoom.imp.ZoomIconImp;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import java.util.Collections;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author Usuario
 */
public class MainWindow extends InternationalizedJFrame<ApplicationInitContext>
						implements ChessGameControllerInterface, NavigatorControllerInterface,
									LoadChessControllerInterface, KeyListener
{
	public static final String sa_configurationBaseFileName = "MainWindow";

	protected static final String CONF_CURRENT_LIST_OF_GAMES_HAS_BEEN_MODIFIED_DO_YOU_WANT_TO_SAVE_CHANGES = "CURRENT_LIST_OF_GAMES_HAS_BEEN_MODIFIED_DO_YOU_WANT_TO_SAVE_CHANGES";
	protected static final String CONF_WARNING = "WARNING";
	protected static final String CONF_YES = "YES";
	protected static final String CONF_NO = "NO";
	protected static final String CONF_CANCEL = "CANCEL";
	protected static final String CONF_HELP_FILE_NAME = "HELP_FILE_NAME";
	protected static final String CONF_ERROR_OPENING_FILE = "ERROR_OPENING_FILE";
	protected static final String CONF_ERROR_OPENING_HELP = "ERROR_OPENING_HELP";
	protected static final String CONF_ERROR_WHEN_TRYING_TO_SET_GAME_RESULT = "ERROR_WHEN_TRYING_TO_SET_GAME_RESULT";
	protected static final String CONF_BAD_STATUS_FOR_RESIGNING = "BAD_STATUS_FOR_RESIGNING";
	protected static final String CONF_FIRST_MOVE_OF_GAME = "FIRST_MOVE_OF_GAME";
	protected static final String CONF_PASTED_GAME = "PASTED_GAME";
	protected static final String CONF_PASTED_POSITION = "PASTED_POSITION";

	protected static final String CONF_DEMOS_ANALYZING_POSITIONS_DEMO_URL = "DEMOS_ANALYZING_POSITIONS_DEMO_URL";
	protected static final String CONF_DEMOS_EDITING_INITIAL_POSITION_DEMO_URL = "DEMOS_EDITING_INITIAL_POSITION_DEMO_URL";
	protected static final String CONF_DEMOS_ADDING_UCI_CHESS_ENGINES_DEMO_URL = "DEMOS_ADDING_UCI_CHESS_ENGINES_DEMO_URL";
	protected static final String CONF_DEMOS_EDITING_COMMENTS_DEMO_URL = "DEMOS_EDITING_COMMENTS_DEMO_URL";
	protected static final String CONF_DEMOS_EDITING_TAGS_DEMO_URL = "DEMOS_EDITING_TAGS_DEMO_URL";
	protected static final String CONF_DEMOS_PLAYING_GAMES_DEMO_URL = "DEMOS_PLAYING_GAMES_URL";
	protected static final String CONF_DEMOS_POSITION_RECOGNIZER_OCR_DEMO_URL = "DEMOS_POSITION_RECOGNIZER_OCR_DEMO_URL";
	protected static final String CONF_DEMOS_WORKING_WITH_CLIPBOARD_DEMO_URL = "DEMOS_WORKING_WITH_CLIPBOARD_DEMO_URL";
	protected static final String CONF_DEMOS_WORKING_WITH_PDFS_DEMO_URL = "DEMOS_WORKING_WITH_PDFS_DEMO_URL";
	protected static final String CONF_DEMOS_WORKING_WITH_PGNS_DEMO_URL = "DEMOS_WORKING_WITH_PGNS_DEMO_URL";


//	protected String _language = null;
//	protected String _additionalLanguage = null;

	protected ChessGame _currentChessGame = null;

	protected List<ChessGame> _listOfGames = null;
	protected boolean _listOfGamesHasBeenModified = false;

	protected PgnChessFile _pgnFile = null;

	protected ChessTreeGameTextPane jTextPane1;
	protected ChessGameListTable jTListOfGames = null;
	protected ChessBoardPanel _chessBoardPanel = null;
	protected NavigatorJPanel _navigatorPanel = null;

	protected PdfViewerWindow _pdfViewerWindow = null;

	protected String _lastFileName = null;

	protected MultiwindowGameManager _multiWindowGameManager = null;
	protected MainWindow _this = null;

	protected AppStringsConf _appStrConf = null;

	protected Map< Frame, Integer > _stateOfWindows = null;

	protected double _currentZoomFactorForComponentResized = 1.0D;

	protected GenericAboutJDialog _aboutDial = null;
//	protected EditCommentFrame _editCommentFrame = null;
//	protected EditTAGsJFrame

	protected PdfDocumentWrapper _pdfDocument = null;

	protected List<ChessFigurineSelection> _chessFigurineSelectionList = null;

	protected AnalysisWindowViewController _analysisWindowViewController;
	protected AnalysisWindowJFrame _analysisWindow;

	protected ChessGamePlayContext _currentOngoingGame = null;

	protected MoveTreeNode _lastProcessedMoveTreeNode = null;

	protected ConfigurationParameterListener _languageToShowGameChangeListener = null;
	protected ConfigurationParameterListener _figureSetChangeListener = null;

	protected ImagePositionController _imagePositionController = null;

	protected volatile boolean _pdfLoadingPending = false;

	/**
	 * Creates new form ChessGameBrowserWindow
	 */
	public MainWindow( ApplicationInitContext initContext) throws ConfigurationException
	{
		super( ApplicationConfiguration.instance(), initContext, true );

		_imagePositionController = createImagePositionController();
		_analysisWindowViewController = createAnalysisWindowViewController();

		initContext.setChessGameController(this);

		_pdfDocument = getApplicationContext().getPdfFactory().createPdfDocumentWrapper();

		initConfiguration();

		initComponents();

		initOwnComponents();

		addListeners();

		setWindowConfiguration();

		_chessFigurineSelectionList = Collections.synchronizedList( new ArrayList<>() );
		_this = this;

//		createExternalWindows();
/*
		Splash.instance().setProgress( 100 );
		
		try
		{
			Thread.sleep(200);
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
		}
*/
//		applyNewApplicationConfiguration();
	}

	protected void addListeners()
	{
		getAppliConf().addConfigurationParameterListener( ApplicationConfiguration.CONF_CHESS_FIGURINE_SET, _languageToShowGameChangeListener);
		getAppliConf().addConfigurationParameterListener( ApplicationConfiguration.CONF_CHESS_FIGURINE_SET, _figureSetChangeListener);
	}

	protected ImagePositionController getImagePositionController()
	{
		return( _imagePositionController );
	}

	public AnalysisWindowViewController getAnalysisController()
	{
		return( _analysisWindowViewController );
	}

	protected void fillInChessFigurineSelectionList()
	{
		_chessFigurineSelectionList.clear();
		_chessFigurineSelectionList.add( new ChessFigurineSelection( FigureSet.HTML_SET, jRBMI_html_set) );
		_chessFigurineSelectionList.add( new ChessFigurineSelection( FigureSet.YURI_SET, jRBMI_yuri_set) );
		_chessFigurineSelectionList.add( new ChessFigurineSelection( FigureSet.VIRTUAL_PIECES_SET, jRBMI_virtualpiecesSet) );
	}

	protected AnalysisWindowViewController createAnalysisWindowViewController()
	{
		AnalysisWindowViewControllerImpl result = new AnalysisWindowViewControllerImpl(getApplicationContext().getChessEngineConfigurationPersistency(),
					getAppliConf());
		result.init( this );

		return( result );
	}

	protected void setChessFigurineSetRadioButtonSelection()
	{
		FigureSet chessFigurineSet = getAppliConf().getChessFigurineSet();

		for( ChessFigurineSelection cfs: _chessFigurineSelectionList )
			if( chessFigurineSet.equals( cfs.getConfigurationValue() ) )
			{
				cfs.getRadioButton().setSelected(true);
				break;
			}
	}

	protected void updateChessFigurineSetConfiguration()
	{
		for( ChessFigurineSelection cfs: _chessFigurineSelectionList )
			if( cfs.getRadioButton().isSelected() )
			{
				getAppliConf().setChessFigurineSet( cfs.getConfigurationValue() );
				ExecutionFunctions.instance().safeMethodExecution( () -> ChessBoardImages.instance().initialize( cfs.getConfigurationValue() ) );
//				SwingUtilities.invokeLater( () -> _chessBoardPanel.repaint() );
			}
	}

	protected void initConfiguration() throws ConfigurationException
	{
/*
		try
		{
			getAppliConf().M_openConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();

			DialogsWrapper.showMessageDialog(null, ce.getMessage() + " Exiting from application",
											"Configuration error", JOptionPane.ERROR_MESSAGE, null );
			throw( ce );
		}

		try
		{
			_appStrConf = AppStringsConf.createInstance( ApplicationConfiguration.sa_PROPERTIES_PATH_IN_JAR,
														getAppliConf() );
			_appStrConf.M_openConfiguration();
//			_appStrConf.changeLanguage( getAppliConf().M_getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
*/
		_appStrConf = AppStringsConf.instance();

		_languageToShowGameChangeListener = createLanguageToShowGameChangeListener();

		_figureSetChangeListener = new ConfigurationParameterListener() {
			@Override
			public <CC> void configurationParameterChanged(ConfigurationParameterObserved observed, String label, CC oldValue, CC newValue) {
				setChessFigurineSetRadioButtonSelection();
				updateChessFigurineSetConfiguration();
			}
		};
/*
		ChessStringsConf chessStrConf = null;
		try
		{
			chessStrConf = ChessStringsConf.createInstance( ApplicationConfiguration.sa_PROPERTIES_PATH_IN_JAR,
															getAppliConf() );
			chessStrConf.M_openConfiguration();
//			chessStrConf.changeLanguage( getAppliConf().M_getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
*/
	}

	protected ConfigurationParameterListener createLanguageToShowGameChangeListener()
	{
		return( new ConfigurationParameterListener() {
			@Override
			public <CC> void configurationParameterChanged( ConfigurationParameterObserved observed, String label,
													CC oldValue, CC newValue )
			{
				newChessGameChosen( _currentChessGame, true );
			}
		}
		);
	}

	protected void createExternalWindows()
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					InitialPositionDialog.createInstance(_this, getAppliConf() );
					EditCommentFrame.createInstance( _this, getAppliConf(), _this );
					EditTAGsJFrame.createInstance( _this, getAppliConf(), _this );

					SwingUtilities.invokeLater( () -> {
						setInitialGame();
						applyNewApplicationConfiguration();
					});
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
	}

	@Override
	public ApplicationConfiguration getAppliConf()
	{
		ApplicationConfiguration result = null;
		if( _appliConf instanceof ApplicationConfiguration )
			result = (ApplicationConfiguration) _appliConf;

		return( result );
	}

	@Override
	public void analyzeGame( ChessGame cg )
	{
		AnalyzeGameAction action = new AnalyzeGameAction();
		action.init( this, getApplicationContext(), cg,
					(cg1) -> jTListOfGames.insertGameAfter( cg1, cg ) );

		action.run();
	}

	protected void setInitialGame()
	{
		try
		{
			ChessGame cg = new ChessGame( getAppliConf() );

			List<ChessGame> listOfGames = new ArrayList<ChessGame>();
			listOfGames.add( cg );

			changeListOfChessGames(listOfGames, 0);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected void initOwnComponents()
	{
		try
		{
//			RBG_language.add(jRadioButtonMenuItemIdiomaEn);
//			RBG_language.add(jRadioButtonMenuItemIdiomaEs);
//			RBG_language.add(jRadioButtonMenuItemIdiomaOtro);
			
			_chessBoardPanel = new ChessBoardPanel();
			_chessBoardPanel.setFigureSetChangedObserved( getAppliConf() );
			_chessBoardPanel.setName( "chessBoardPanel" );
//			_currentChessGame = new ChessGame( getAppliConf() );

//			_chessBoardPanel.setChessBoard( _currentChessGame.getChessBoard() );
			_chessBoardPanel.setChessBoard( null );

//			jPanelChessBoardContainer.add( _chessBoardPanel );
//			_chessBoardPanel.setBounds( 0, 0, jPanelChessBoardContainer.getWidth(), jPanelChessBoardContainer.getHeight() );

	        jSPmainSplit.setRightComponent(_chessBoardPanel);
			_chessBoardPanel.setListener( this );

			_multiWindowGameManager = new MultiwindowGameManager( this, getAppliConf() );

			jTextPane1 = new ChessTreeGameTextPane(_chessBoardPanel, this, _multiWindowGameManager, true);
			jTextPane1.setChessGame(null);
			jTextPane1.setName( "jTextPanel" );

			_multiWindowGameManager.setParentChessTreeGameTextPane(jTextPane1);

			jScrollPane2.setViewportView(jTextPane1);

			jTListOfGames = new ChessGameListTable( this, getAppliConf(), _multiWindowGameManager,
													getAppliConf() );
			jScrollPane1.setViewportView(jTListOfGames);

			jTListOfGames.initialize();

			SwingUtilities.invokeLater( () -> {
						jPanel4.setSize( jPanelNavigatorContainer1.getWidth(), jPanel4.getHeight() );
						jPanel3.setSize( jPanelNavigatorContainer1.getWidth(), jPanel3.getHeight() );
						jScrollPane2.setSize( jPanelNavigatorContainer1.getWidth(), jScrollPane2.getHeight() );

						_navigatorPanel = new NavigatorJPanel( this );
						_navigatorPanel.setBounds(0, 0, jPanelNavigatorContainer1.getWidth(), 50);
						jPanel3.add( _navigatorPanel );
			});

			pack();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected void setWindowConfiguration( )
	{
		SwingUtilities.invokeLater( () -> setWindowConfiguration_internal( ) );
	}

	protected void setWindowConfiguration_internal( )
	{
		Vector<JPopupMenu> vectorJpopupMenus = new Vector<JPopupMenu>();
		vectorJpopupMenus.add( jTextPane1.getJPopupMenu() );
		vectorJpopupMenus.add( jTListOfGames.getJPopupMenu() );

		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jSPmainSplit, ResizeRelocateItem.UP_TO_RIGHT + ResizeRelocateItem.UP_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jPanelNavigatorContainer1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM, true );

//			mapRRCI.putResizeRelocateComponentItem( jSPnavigatorSplit, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jSPnavigatorSplit, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( jPanel4, ResizeRelocateItem.FILL_WHOLE_WIDTH );
			mapRRCI.putResizeRelocateComponentItem( jPanel3, ResizeRelocateItem.FILL_WHOLE_WIDTH );
			mapRRCI.putResizeRelocateComponentItem( _navigatorPanel, ResizeRelocateItem.FILL_WHOLE_PARENT );

			mapRRCI.putResizeRelocateComponentItem( jScrollPane2, ResizeRelocateItem.FILL_WHOLE_WIDTH + ResizeRelocateItem.UP_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jTextPane1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putResizeRelocateComponentItem( jTListOfGames, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

//			mapRRCI.putResizeRelocateComponentItem( jPanelChessBoardContainer, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( _chessBoardPanel, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
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
		setLanguageMenu( jM_languageSubmenu );
		setZoomMenu( jM_zoom );
		setDarkModeMenuItemIcon( jMI_darkMode );

		addIconsToFigureSetOptions();

		boolean adjustSizeOfFrameToContents = true;
		boolean adjustMinSizeOfFrameToContents = true;
		double zoomFactor = getAppliConf().getZoomFactor();
		boolean activateUndoRedoForTextComponents = true;
		boolean enableTextPopupMenu = false;
		boolean switchToZoomComponents = true;
		boolean internationalizeFont = true;
		Integer delayToInvokeCallback = null;

		createInternationalization( getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									sa_configurationBaseFileName,
									this, null,
									vectorJpopupMenus, true,
									mapRRCI,
									adjustSizeOfFrameToContents,
									adjustMinSizeOfFrameToContents,
									zoomFactor,
									activateUndoRedoForTextComponents,
									enableTextPopupMenu,
									switchToZoomComponents,
									internationalizeFont,
									delayToInvokeCallback );

		jTextPane1.setLanguageConfiguration( this );
		jTListOfGames.setLanguageConfiguration( this );

		this.registerInternationalString(CONF_CURRENT_LIST_OF_GAMES_HAS_BEEN_MODIFIED_DO_YOU_WANT_TO_SAVE_CHANGES,
											"Current list of games has been modified since last saved. Do you want to save it now?" );
		this.registerInternationalString(CONF_WARNING, "Warning" );
		this.registerInternationalString(CONF_YES, "Yes" );
		this.registerInternationalString(CONF_NO, "No" );
		this.registerInternationalString(CONF_CANCEL, "Cancel" );
		this.registerInternationalString(CONF_HELP_FILE_NAME, "../_documents/_manual/English/Chess PDF browser v1.0.User hadbook.docx" );
		this.registerInternationalString(CONF_ERROR_OPENING_FILE, "Error opening file: $1" );
		this.registerInternationalString(CONF_ERROR_OPENING_HELP, "Error opening help" );
		this.registerInternationalString(CONF_ERROR_WHEN_TRYING_TO_SET_GAME_RESULT, "Error when trying to set game result" );
		this.registerInternationalString(CONF_BAD_STATUS_FOR_RESIGNING, "Bad status for resigning" );
		this.registerInternationalString(CONF_FIRST_MOVE_OF_GAME, "First move of game. White player: $1 - Black player: $2" );
		this.registerInternationalString(CONF_PASTED_GAME, "Pasted game - $1" );
		this.registerInternationalString(CONF_PASTED_POSITION, "Pasted position" );

		this.registerInternationalString(CONF_DEMOS_ANALYZING_POSITIONS_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.4-analizar.posiciones.webm" );
		this.registerInternationalString(CONF_DEMOS_EDITING_INITIAL_POSITION_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.10.editar.la.posicion.inicial.webm" );
		this.registerInternationalString(CONF_DEMOS_ADDING_UCI_CHESS_ENGINES_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.3-anyadir.motores.uci.webm" );
		this.registerInternationalString(CONF_DEMOS_EDITING_COMMENTS_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.8.editar.comentarios.webm" );
		this.registerInternationalString(CONF_DEMOS_EDITING_TAGS_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.9.editar.tags.webm" );
		this.registerInternationalString(CONF_DEMOS_PLAYING_GAMES_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.5-jugar.partidas.webm" );
		this.registerInternationalString(CONF_DEMOS_POSITION_RECOGNIZER_OCR_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.7.reconocedor.de.posiciones.webm" );
		this.registerInternationalString(CONF_DEMOS_WORKING_WITH_CLIPBOARD_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.6.trabajar.con.el.portapapeles.webm" );
		this.registerInternationalString(CONF_DEMOS_WORKING_WITH_PDFS_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.2-trabajar.con.pdfs.webm" );
		this.registerInternationalString(CONF_DEMOS_WORKING_WITH_PGNS_DEMO_URL, "https://frojasg1.com/demos/aplicaciones/ChessPdfBrowser/v1.20.ES.1-trabajar.con.pgns.webm" );
//		setIcon( "com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png" );
	}

	protected void addIconsToFigureSetOptions()
	{
		// the function to fill list will be called again when Components have been switched to Zoom ones.
		fillInChessFigurineSelectionList();

		for( ChessFigurineSelection cfs: _chessFigurineSelectionList )
			cfs.getRadioButton().setIcon( cfs.getIcon() );
	}


	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chessFigurineButtonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jSPmainSplit = new javax.swing.JSplitPane();
        jSPnavigatorSplit = new javax.swing.JSplitPane();
        jPanelNavigatorContainer1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jCB_whitePlaysFromBottom = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jM_File = new javax.swing.JMenu();
        jMI_open = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMI_save = new javax.swing.JMenuItem();
        jMI_saveAs = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMI_exit = new javax.swing.JMenuItem();
        jM_Edit = new javax.swing.JMenu();
        jMI_copyFEN = new javax.swing.JMenuItem();
        jMI_copyPGN = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMI_paste = new javax.swing.JMenuItem();
        jM_game = new javax.swing.JMenu();
        jMI_newGame = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        jMI_pauseGame = new javax.swing.JMenuItem();
        jMI_resumeGame = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        jMI_gameData = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        jMI_whiteResigns = new javax.swing.JMenuItem();
        jMI_blackResigns = new javax.swing.JMenuItem();
        jMI_drawMutualAgreement = new javax.swing.JMenuItem();
        jMI_cleanGameResult = new javax.swing.JMenuItem();
        jSeparator24 = new javax.swing.JPopupMenu.Separator();
        jMI_analyzeGame = new javax.swing.JMenuItem();
        jMenuView = new javax.swing.JMenu();
        jM_chessFigurineSet = new javax.swing.JMenu();
        jRBMI_html_set = new javax.swing.JRadioButtonMenuItem();
        jRBMI_virtualpiecesSet = new javax.swing.JRadioButtonMenuItem();
        jRBMI_yuri_set = new javax.swing.JRadioButtonMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jM_zoom = new javax.swing.JMenu();
        jSeparator25 = new javax.swing.JPopupMenu.Separator();
        jMI_darkMode = new javax.swing.JMenuItem();
        jM_preferences = new javax.swing.JMenu();
        jM_languageSubmenu = new javax.swing.JMenu();
        jRadioButtonMenuItemIdiomaEn = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemIdiomaEs = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemIdiomaOtro = new javax.swing.JRadioButtonMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMI_preferences = new javax.swing.JMenuItem();
        jM_windows = new javax.swing.JMenu();
        jMI_editComment = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMI_editTAGsWindow = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMI_editInitialPosition = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMI_openCurrentGameInDetachedWindow = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        jMI_analysisWindow = new javax.swing.JMenuItem();
        jM_help = new javax.swing.JMenu();
        jM_demos = new javax.swing.JMenu();
        jMI_Demos_workingWithPgns = new javax.swing.JMenuItem();
        jMI_demos_workingWithPdfs = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        jMI_demos_addingUciChessEngines = new javax.swing.JMenuItem();
        jMI_demos_AnalizingPositions = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        jMI_demos_playingGames = new javax.swing.JMenuItem();
        jSeparator23 = new javax.swing.JPopupMenu.Separator();
        jMI_demos_workingWithClipboard = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        jMI_demos_positionRecognizer = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        jMI_demos_editingComments = new javax.swing.JMenuItem();
        jMI_demos_editingTags = new javax.swing.JMenuItem();
        jMI_demos_EditingInitialPosition = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        jMI_help = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMI_lookForNewVersion = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMI_whatIsNew = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMI_license = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMI_about = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAutoRequestFocus(false);
        setMinimumSize(new java.awt.Dimension(600, 415));
        setName(""); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowDeiconified(java.awt.event.WindowEvent evt) {
                formWindowDeiconified(evt);
            }
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });

        jPanel1.setMinimumSize(new java.awt.Dimension(584, 270));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 385));
        jPanel1.setLayout(null);

        jSPmainSplit.setDividerLocation(233);
        jSPmainSplit.setResizeWeight(0.2);
        jSPmainSplit.setContinuousLayout(true);
        jSPmainSplit.setName("jSPmainSplit"); // NOI18N

        jSPnavigatorSplit.setDividerLocation(240);
        jSPnavigatorSplit.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSPnavigatorSplit.setResizeWeight(0.8);
        jSPnavigatorSplit.setContinuousLayout(true);
        jSPnavigatorSplit.setName("jSPnavigatorSplit"); // NOI18N

        jPanelNavigatorContainer1.setMinimumSize(new java.awt.Dimension(230, 190));
        jPanelNavigatorContainer1.setName("jPanelNavigatorContainer1"); // NOI18N
        jPanelNavigatorContainer1.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setMinimumSize(new java.awt.Dimension(230, 70));
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(null);

        jCB_whitePlaysFromBottom.setSelected(true);
        jCB_whitePlaysFromBottom.setText("white plays from bottom");
        jCB_whitePlaysFromBottom.setName("jCB_whitePlaysFromBottom"); // NOI18N
        jCB_whitePlaysFromBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_whitePlaysFromBottomActionPerformed(evt);
            }
        });
        jPanel4.add(jCB_whitePlaysFromBottom);
        jCB_whitePlaysFromBottom.setBounds(10, 10, 190, 24);
        jPanel4.add(jSeparator3);
        jSeparator3.setBounds(50, 20, 0, 2);

        jPanelNavigatorContainer1.add(jPanel4);
        jPanel4.setBounds(0, 0, 210, 70);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setMinimumSize(new java.awt.Dimension(230, 50));
        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(null);
        jPanelNavigatorContainer1.add(jPanel3);
        jPanel3.setBounds(0, 70, 210, 50);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(230, 70));
        jScrollPane2.setName("jScrollPane2"); // NOI18N
        jScrollPane2.setPreferredSize(new java.awt.Dimension(230, 120));
        jScrollPane2.setRequestFocusEnabled(false);
        jPanelNavigatorContainer1.add(jScrollPane2);
        jScrollPane2.setBounds(0, 120, 210, 120);

        jSPnavigatorSplit.setLeftComponent(jPanelNavigatorContainer1);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(230, 70));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(230, 230));
        jSPnavigatorSplit.setBottomComponent(jScrollPane1);

        jSPmainSplit.setLeftComponent(jSPnavigatorSplit);

        jPanel1.add(jSPmainSplit);
        jSPmainSplit.setBounds(0, 0, 700, 479);

        jM_File.setText("File");
        jM_File.setName("jM_File"); // NOI18N

        jMI_open.setText("Open");
        jMI_open.setName("jMI_open"); // NOI18N
        jMI_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_openActionPerformed(evt);
            }
        });
        jM_File.add(jMI_open);
        jM_File.add(jSeparator4);

        jMI_save.setText("Save");
        jMI_save.setName("jMI_save"); // NOI18N
        jMI_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_saveActionPerformed(evt);
            }
        });
        jM_File.add(jMI_save);

        jMI_saveAs.setText("Save as");
        jMI_saveAs.setName("jMI_saveAs"); // NOI18N
        jMI_saveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_saveAsActionPerformed(evt);
            }
        });
        jM_File.add(jMI_saveAs);
        jM_File.add(jSeparator2);

        jMI_exit.setText("Exit");
        jMI_exit.setName("jMI_exit"); // NOI18N
        jMI_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_exitActionPerformed(evt);
            }
        });
        jM_File.add(jMI_exit);

        jMenuBar1.add(jM_File);

        jM_Edit.setText("Edit");
        jM_Edit.setName("jM_Edit"); // NOI18N

        jMI_copyFEN.setText("Copy FEN");
        jMI_copyFEN.setName("jMI_copyFEN"); // NOI18N
        jMI_copyFEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_copyFENActionPerformed(evt);
            }
        });
        jM_Edit.add(jMI_copyFEN);

        jMI_copyPGN.setText("Copy PGN");
        jMI_copyPGN.setName("jMI_copyPGN"); // NOI18N
        jMI_copyPGN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_copyPGNActionPerformed(evt);
            }
        });
        jM_Edit.add(jMI_copyPGN);
        jM_Edit.add(jSeparator1);

        jMI_paste.setText("Paste");
        jMI_paste.setName("jMI_paste"); // NOI18N
        jMI_paste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_pasteActionPerformed(evt);
            }
        });
        jM_Edit.add(jMI_paste);

        jMenuBar1.add(jM_Edit);

        jM_game.setText("Game");
        jM_game.setName("jM_game"); // NOI18N

        jMI_newGame.setText("New game");
        jMI_newGame.setName("jMI_newGame"); // NOI18N
        jMI_newGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_newGameActionPerformed(evt);
            }
        });
        jM_game.add(jMI_newGame);
        jM_game.add(jSeparator16);

        jMI_pauseGame.setText("Pause game");
        jMI_pauseGame.setName("jMI_pauseGame"); // NOI18N
        jMI_pauseGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_pauseGameActionPerformed(evt);
            }
        });
        jM_game.add(jMI_pauseGame);

        jMI_resumeGame.setText("Resume game");
        jMI_resumeGame.setName("jMI_resumeGame"); // NOI18N
        jMI_resumeGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_resumeGameActionPerformed(evt);
            }
        });
        jM_game.add(jMI_resumeGame);
        jM_game.add(jSeparator17);

        jMI_gameData.setText("Game data");
        jMI_gameData.setName("jMI_gameData"); // NOI18N
        jMI_gameData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_gameDataActionPerformed(evt);
            }
        });
        jM_game.add(jMI_gameData);
        jM_game.add(jSeparator13);

        jMI_whiteResigns.setText("White resigns");
        jMI_whiteResigns.setName("jMI_whiteResigns"); // NOI18N
        jMI_whiteResigns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_whiteResignsActionPerformed(evt);
            }
        });
        jM_game.add(jMI_whiteResigns);

        jMI_blackResigns.setText("Black resigns");
        jMI_blackResigns.setName("jMI_blackResigns"); // NOI18N
        jMI_blackResigns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_blackResignsActionPerformed(evt);
            }
        });
        jM_game.add(jMI_blackResigns);

        jMI_drawMutualAgreement.setText("Draw mutual agreement");
        jMI_drawMutualAgreement.setName("jMI_drawMutualAgreement"); // NOI18N
        jMI_drawMutualAgreement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_drawMutualAgreementActionPerformed(evt);
            }
        });
        jM_game.add(jMI_drawMutualAgreement);

        jMI_cleanGameResult.setText("Clean game result");
        jMI_cleanGameResult.setName("jMI_cleanGameResult"); // NOI18N
        jMI_cleanGameResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_cleanGameResultActionPerformed(evt);
            }
        });
        jM_game.add(jMI_cleanGameResult);
        jM_game.add(jSeparator24);

        jMI_analyzeGame.setText("Analyze game");
        jMI_analyzeGame.setName("jMI_analyzeGame"); // NOI18N
        jMI_analyzeGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_analyzeGameActionPerformed(evt);
            }
        });
        jM_game.add(jMI_analyzeGame);

        jMenuBar1.add(jM_game);

        jMenuView.setText("View");
        jMenuView.setName("jMenuView"); // NOI18N

        jM_chessFigurineSet.setText("Chess figurine set");
        jM_chessFigurineSet.setName("jM_chessFigurineSet"); // NOI18N

        chessFigurineButtonGroup.add(jRBMI_html_set);
        jRBMI_html_set.setText("HTML set");
        jRBMI_html_set.setName("jRBMI_html_set"); // NOI18N
        jRBMI_html_set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBMI_html_setActionPerformed(evt);
            }
        });
        jM_chessFigurineSet.add(jRBMI_html_set);

        chessFigurineButtonGroup.add(jRBMI_virtualpiecesSet);
        jRBMI_virtualpiecesSet.setSelected(true);
        jRBMI_virtualpiecesSet.setText("Virtual pieces set");
        jRBMI_virtualpiecesSet.setName("jRBMI_virtualpiecesSet"); // NOI18N
        jRBMI_virtualpiecesSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBMI_html_setActionPerformed(evt);
            }
        });
        jM_chessFigurineSet.add(jRBMI_virtualpiecesSet);

        chessFigurineButtonGroup.add(jRBMI_yuri_set);
        jRBMI_yuri_set.setText("Yuri set");
        jRBMI_yuri_set.setName("jRBMI_yuri_set"); // NOI18N
        jRBMI_yuri_set.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBMI_html_setActionPerformed(evt);
            }
        });
        jM_chessFigurineSet.add(jRBMI_yuri_set);

        jMenuView.add(jM_chessFigurineSet);
        jMenuView.add(jSeparator14);

        jM_zoom.setText("Zoom");
        jM_zoom.setName("jM_zoom"); // NOI18N
        jMenuView.add(jM_zoom);
        jMenuView.add(jSeparator25);

        jMI_darkMode.setText("Dark mode");
        jMI_darkMode.setName("jMI_darkMode"); // NOI18N
        jMI_darkMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_darkModeActionPerformed(evt);
            }
        });
        jMenuView.add(jMI_darkMode);

        jMenuBar1.add(jMenuView);

        jM_preferences.setText("Preferences");
        jM_preferences.setName("jM_preferences"); // NOI18N

        jM_languageSubmenu.setText("Language");
        jM_languageSubmenu.setName("jM_languageSubmenu"); // NOI18N

        jRadioButtonMenuItemIdiomaEn.setText("EN");
        jRadioButtonMenuItemIdiomaEn.setName(""); // NOI18N
        jRadioButtonMenuItemIdiomaEn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemIdiomaEnActionPerformed(evt);
            }
        });
        jM_languageSubmenu.add(jRadioButtonMenuItemIdiomaEn);

        jRadioButtonMenuItemIdiomaEs.setText("ES");
        jRadioButtonMenuItemIdiomaEs.setName(""); // NOI18N
        jM_languageSubmenu.add(jRadioButtonMenuItemIdiomaEs);

        jRadioButtonMenuItemIdiomaOtro.setText("CAT");
        jRadioButtonMenuItemIdiomaOtro.setName(""); // NOI18N
        jM_languageSubmenu.add(jRadioButtonMenuItemIdiomaOtro);

        jM_preferences.add(jM_languageSubmenu);
        jM_preferences.add(jSeparator9);

        jMI_preferences.setText("Preferences");
        jMI_preferences.setName("jMI_preferences"); // NOI18N
        jMI_preferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_preferencesActionPerformed(evt);
            }
        });
        jM_preferences.add(jMI_preferences);

        jMenuBar1.add(jM_preferences);

        jM_windows.setText("Windows");
        jM_windows.setName("jM_windows"); // NOI18N

        jMI_editComment.setText("Edit comment window");
        jMI_editComment.setName("jMI_editComment"); // NOI18N
        jMI_editComment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_editCommentActionPerformed(evt);
            }
        });
        jM_windows.add(jMI_editComment);
        jM_windows.add(jSeparator7);

        jMI_editTAGsWindow.setText("Edit header TAGs window");
        jMI_editTAGsWindow.setName("jMI_editTAGsWindow"); // NOI18N
        jMI_editTAGsWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_editTAGsWindowActionPerformed(evt);
            }
        });
        jM_windows.add(jMI_editTAGsWindow);
        jM_windows.add(jSeparator6);

        jMI_editInitialPosition.setText("Edit initial position");
        jMI_editInitialPosition.setName("jMI_editInitialPosition"); // NOI18N
        jMI_editInitialPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_editInitialPositionActionPerformed(evt);
            }
        });
        jM_windows.add(jMI_editInitialPosition);
        jM_windows.add(jSeparator10);

        jMI_openCurrentGameInDetachedWindow.setText("Open current game in detached window");
        jMI_openCurrentGameInDetachedWindow.setName("jMI_openCurrentGameInDetachedWindow"); // NOI18N
        jMI_openCurrentGameInDetachedWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_openCurrentGameInDetachedWindowActionPerformed(evt);
            }
        });
        jM_windows.add(jMI_openCurrentGameInDetachedWindow);
        jM_windows.add(jSeparator15);

        jMI_analysisWindow.setText("Analysis window");
        jMI_analysisWindow.setName("jMI_analysisWindow"); // NOI18N
        jMI_analysisWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_analysisWindowActionPerformed(evt);
            }
        });
        jM_windows.add(jMI_analysisWindow);

        jMenuBar1.add(jM_windows);

        jM_help.setText("Help");
        jM_help.setName("jM_help"); // NOI18N

        jM_demos.setText("Demos");
        jM_demos.setName("jM_demos"); // NOI18N

        jMI_Demos_workingWithPgns.setText("Working with PGNs");
        jMI_Demos_workingWithPgns.setName("jMI_Demos_workingWithPgns"); // NOI18N
        jMI_Demos_workingWithPgns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_Demos_workingWithPgns);

        jMI_demos_workingWithPdfs.setText("Working with PDFs");
        jMI_demos_workingWithPdfs.setName("jMI_demos_workingWithPdfs"); // NOI18N
        jMI_demos_workingWithPdfs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_workingWithPdfs);
        jM_demos.add(jSeparator21);

        jMI_demos_addingUciChessEngines.setText("Adding Uci Chess Engines");
        jMI_demos_addingUciChessEngines.setName("jMI_demos_addingUciChessEngines"); // NOI18N
        jMI_demos_addingUciChessEngines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_addingUciChessEngines);

        jMI_demos_AnalizingPositions.setText("Analyzing positions");
        jMI_demos_AnalizingPositions.setName("jMI_demos_AnalizingPositions"); // NOI18N
        jMI_demos_AnalizingPositions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_AnalizingPositions);
        jM_demos.add(jSeparator22);

        jMI_demos_playingGames.setText("Playing games");
        jMI_demos_playingGames.setName("jMI_demos_playingGames"); // NOI18N
        jMI_demos_playingGames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_playingGames);
        jM_demos.add(jSeparator23);

        jMI_demos_workingWithClipboard.setText("Working with clipboard");
        jMI_demos_workingWithClipboard.setName("jMI_demos_workingWithClipboard"); // NOI18N
        jMI_demos_workingWithClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_workingWithClipboard);
        jM_demos.add(jSeparator19);

        jMI_demos_positionRecognizer.setText("Position recognizer (ocr)");
        jMI_demos_positionRecognizer.setName("jMI_demos_positionRecognizer"); // NOI18N
        jMI_demos_positionRecognizer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_positionRecognizer);
        jM_demos.add(jSeparator20);

        jMI_demos_editingComments.setText("Editing comments");
        jMI_demos_editingComments.setName("jMI_demos_editingComments"); // NOI18N
        jMI_demos_editingComments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_editingComments);

        jMI_demos_editingTags.setText("Editing tags");
        jMI_demos_editingTags.setName("jMI_demos_editingTags"); // NOI18N
        jMI_demos_editingTags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_editingTags);

        jMI_demos_EditingInitialPosition.setText("Editing initial position");
        jMI_demos_EditingInitialPosition.setName("jMI_demos_EditingInitialPosition"); // NOI18N
        jMI_demos_EditingInitialPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_demos_EditingInitialPositionActionPerformed(evt);
            }
        });
        jM_demos.add(jMI_demos_EditingInitialPosition);

        jM_help.add(jM_demos);
        jM_help.add(jSeparator18);

        jMI_help.setText("Help");
        jMI_help.setName("jMI_help"); // NOI18N
        jMI_help.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_helpActionPerformed(evt);
            }
        });
        jM_help.add(jMI_help);
        jM_help.add(jSeparator8);

        jMI_lookForNewVersion.setText("Look for a new version");
        jMI_lookForNewVersion.setName("jMI_lookForNewVersion"); // NOI18N
        jMI_lookForNewVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_lookForNewVersionActionPerformed(evt);
            }
        });
        jM_help.add(jMI_lookForNewVersion);
        jM_help.add(jSeparator12);

        jMI_whatIsNew.setText("What is new");
        jMI_whatIsNew.setName("jMI_whatIsNew"); // NOI18N
        jMI_whatIsNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_whatIsNewActionPerformed(evt);
            }
        });
        jM_help.add(jMI_whatIsNew);
        jM_help.add(jSeparator11);

        jMI_license.setText("License");
        jMI_license.setName("jMI_license"); // NOI18N
        jMI_license.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_licenseActionPerformed(evt);
            }
        });
        jM_help.add(jMI_license);
        jM_help.add(jSeparator5);

        jMI_about.setText("About");
        jMI_about.setName("jMI_about"); // NOI18N
        jMI_about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMI_aboutActionPerformed(evt);
            }
        });
        jM_help.add(jMI_about);

        jMenuBar1.add(jM_help);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		boolean closeWindow = true;

		if( checkToSaveCurrentPGN() )
			formWindowClosing( closeWindow );

    }//GEN-LAST:event_formWindowClosing

    private void jMI_openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_openActionPerformed
        // TODO add your handling code here:

		String fileName = showFileChooserDialog( DialogsWrapper.OPEN );

		if( fileName != null )	openFile( fileName );

    }//GEN-LAST:event_jMI_openActionPerformed

    private void jMI_pasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_pasteActionPerformed
        // TODO add your handling code here:

		parsePastedText();
		
    }//GEN-LAST:event_jMI_pasteActionPerformed

    private void jMI_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_saveActionPerformed
        // TODO add your handling code here:

		save();
    }//GEN-LAST:event_jMI_saveActionPerformed

    private void jMI_saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_saveAsActionPerformed
        // TODO add your handling code here:

		String fileName = showFileChooserDialog( DialogsWrapper.SAVE );

		if( fileName != null )	saveAs( fileName );

    }//GEN-LAST:event_jMI_saveAsActionPerformed

    private void jMI_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_exitActionPerformed
        // TODO add your handling code here:

		boolean closeWindow = true;
		formWindowClosing( closeWindow );

    }//GEN-LAST:event_jMI_exitActionPerformed

    private void jMI_editCommentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_editCommentActionPerformed
        // TODO add your handling code here:

		showEditCommentWindow();

    }//GEN-LAST:event_jMI_editCommentActionPerformed

    private void jMI_openCurrentGameInDetachedWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_openCurrentGameInDetachedWindowActionPerformed
        // TODO add your handling code here:

		DetachedGameWindow dgw = _multiWindowGameManager.addOrFocusGameWindow(_currentChessGame);

    }//GEN-LAST:event_jMI_openCurrentGameInDetachedWindowActionPerformed

    private void jMI_editTAGsWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_editTAGsWindowActionPerformed
        // TODO add your handling code here:

		EditTAGsJFrame.instance().setVisible( true );
		EditTAGsJFrame.instance().setState ( Frame.NORMAL );

    }//GEN-LAST:event_jMI_editTAGsWindowActionPerformed

    private void jMI_preferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_preferencesActionPerformed
        // TODO add your handling code here:

		openConfiguration();

    }//GEN-LAST:event_jMI_preferencesActionPerformed

    private void jMI_copyPGNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_copyPGNActionPerformed
        // TODO add your handling code here:

		if( _currentChessGame != null )
		{
			try
			{
				PgnChessFile pcf = new PgnChessFile();
				String pgnString = pcf.saveGameToFile( _currentChessGame );

				SystemClipboard.instance().setClipboardContents( pgnString );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

    }//GEN-LAST:event_jMI_copyPGNActionPerformed

    private void jMI_copyFENActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_copyFENActionPerformed
        // TODO add your handling code here:

		if( _currentChessGame != null )
		{
			try
			{
				String pgnString = _currentChessGame.getChessBoard().getCurrentPosition().getFenString();

				SystemClipboard.instance().setClipboardContents( pgnString );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

    }//GEN-LAST:event_jMI_copyFENActionPerformed

    private void jMI_editInitialPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_editInitialPositionActionPerformed
        // TODO add your handling code here:

		editInitialPosition(_currentChessGame);

    }//GEN-LAST:event_jMI_editInitialPositionActionPerformed

    private void jMI_aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_aboutActionPerformed
        // TODO add your handling code here:

		_aboutDial = new AboutJDialog( this, true, getAppliConf(), iiec -> setVisibleAbout(),
			getApplicationContext().getAnimationForAboutFactory() );

    }//GEN-LAST:event_jMI_aboutActionPerformed

    private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
        // TODO add your handling code here:

		saveStateOfOpenWindowsAndIconifyThem();

    }//GEN-LAST:event_formWindowIconified

    private void formWindowDeiconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowDeiconified
        // TODO add your handling code here:

		restoreStateOfOpenWindows();

    }//GEN-LAST:event_formWindowDeiconified

    private void jMI_licenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_licenseActionPerformed
        // TODO add your handling code here:

		showLicense();

    }//GEN-LAST:event_jMI_licenseActionPerformed

    private void jCB_whitePlaysFromBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_whitePlaysFromBottomActionPerformed
        // TODO add your handling code here:

		_chessBoardPanel.setWhitePlaysFromTheBottom( jCB_whitePlaysFromBottom.isSelected() );

    }//GEN-LAST:event_jCB_whitePlaysFromBottomActionPerformed

    private void jMI_helpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_helpActionPerformed
        // TODO add your handling code here:

		String fileName = getInternationalString(CONF_HELP_FILE_NAME);
		try
		{
			GenericFunctions.instance().getSystem().openDocument( fileName );
		}
		catch( Exception ex )
		{
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this,
								createCustomInternationalString( CONF_ERROR_OPENING_FILE, fileName ),
								getInternationalString( CONF_ERROR_OPENING_HELP ),
									DialogsWrapper.ERROR_MESSAGE );
		}


    }//GEN-LAST:event_jMI_helpActionPerformed

    private void jMI_lookForNewVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_lookForNewVersionActionPerformed
        // TODO add your handling code here:

		checkForNewVersion();

    }//GEN-LAST:event_jMI_lookForNewVersionActionPerformed

    private void jMI_whatIsNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_whatIsNewActionPerformed
        // TODO add your handling code here:

		showWhatIsNew();

    }//GEN-LAST:event_jMI_whatIsNewActionPerformed

    private void jMI_whiteResignsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_whiteResignsActionPerformed
        // TODO add your handling code here:

		setGameResult( "0-1" );

    }//GEN-LAST:event_jMI_whiteResignsActionPerformed

    private void jMI_cleanGameResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_cleanGameResultActionPerformed
        // TODO add your handling code here:

		ChessGameMove cgm = getCurrentMove();
		if( cgm != null )
		{
			cgm.setResultOfGame( null );
			newChessGameChosen( _currentChessGame, true );
		}

    }//GEN-LAST:event_jMI_cleanGameResultActionPerformed

    private void jMI_blackResignsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_blackResignsActionPerformed
        // TODO add your handling code here:

		setGameResult( "1-0" );

    }//GEN-LAST:event_jMI_blackResignsActionPerformed

    private void jMI_gameDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_gameDataActionPerformed
        // TODO add your handling code here:

		editGameData();

    }//GEN-LAST:event_jMI_gameDataActionPerformed

    private void jRadioButtonMenuItemIdiomaEnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemIdiomaEnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonMenuItemIdiomaEnActionPerformed

    private void jRBMI_html_setActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBMI_html_setActionPerformed
        // TODO add your handling code here:

		updateChessFigurineSetConfiguration();
    }//GEN-LAST:event_jRBMI_html_setActionPerformed

    private void jMI_analysisWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_analysisWindowActionPerformed
        // TODO add your handling code here:

		openAnalysisWindow();
    }//GEN-LAST:event_jMI_analysisWindowActionPerformed

    private void jMI_pauseGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_pauseGameActionPerformed
        // TODO add your handling code here:

		pauseCurrentGame();

    }//GEN-LAST:event_jMI_pauseGameActionPerformed

    private void jMI_resumeGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_resumeGameActionPerformed
        // TODO add your handling code here:

		resumeGame();

    }//GEN-LAST:event_jMI_resumeGameActionPerformed

    private void jMI_newGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_newGameActionPerformed
        // TODO add your handling code here:

		startNewGame();
		
    }//GEN-LAST:event_jMI_newGameActionPerformed

    private void jMI_drawMutualAgreementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_drawMutualAgreementActionPerformed
        // TODO add your handling code here:

		setGameResult( "1/2-1/2" );

    }//GEN-LAST:event_jMI_drawMutualAgreementActionPerformed

    private void jMI_demos_EditingInitialPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_demos_EditingInitialPositionActionPerformed
        // TODO add your handling code here:

		demoActionPerformed( evt.getSource() );

    }//GEN-LAST:event_jMI_demos_EditingInitialPositionActionPerformed

    private void jMI_analyzeGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_analyzeGameActionPerformed
        // TODO add your handling code here:

		analyzeGame(_currentChessGame);

    }//GEN-LAST:event_jMI_analyzeGameActionPerformed

    private void jMI_darkModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMI_darkModeActionPerformed
        // TODO add your handling code here:

//		ComponentFunctions.instance().browseComponentHierarchy( this, this::processComponent );

		toggleDarkMode();

    }//GEN-LAST:event_jMI_darkModeActionPerformed

   protected static PrintStream outputFile(String name) throws IOException
   {
       return new PrintStream(new BufferedOutputStream(new FileOutputStream(name)), true);
   }
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup chessFigurineButtonGroup;
    private javax.swing.JCheckBox jCB_whitePlaysFromBottom;
    private javax.swing.JMenuItem jMI_Demos_workingWithPgns;
    private javax.swing.JMenuItem jMI_about;
    private javax.swing.JMenuItem jMI_analysisWindow;
    private javax.swing.JMenuItem jMI_analyzeGame;
    private javax.swing.JMenuItem jMI_blackResigns;
    private javax.swing.JMenuItem jMI_cleanGameResult;
    private javax.swing.JMenuItem jMI_copyFEN;
    private javax.swing.JMenuItem jMI_copyPGN;
    private javax.swing.JMenuItem jMI_darkMode;
    private javax.swing.JMenuItem jMI_demos_AnalizingPositions;
    private javax.swing.JMenuItem jMI_demos_EditingInitialPosition;
    private javax.swing.JMenuItem jMI_demos_addingUciChessEngines;
    private javax.swing.JMenuItem jMI_demos_editingComments;
    private javax.swing.JMenuItem jMI_demos_editingTags;
    private javax.swing.JMenuItem jMI_demos_playingGames;
    private javax.swing.JMenuItem jMI_demos_positionRecognizer;
    private javax.swing.JMenuItem jMI_demos_workingWithClipboard;
    private javax.swing.JMenuItem jMI_demos_workingWithPdfs;
    private javax.swing.JMenuItem jMI_drawMutualAgreement;
    private javax.swing.JMenuItem jMI_editComment;
    private javax.swing.JMenuItem jMI_editInitialPosition;
    private javax.swing.JMenuItem jMI_editTAGsWindow;
    private javax.swing.JMenuItem jMI_exit;
    private javax.swing.JMenuItem jMI_gameData;
    private javax.swing.JMenuItem jMI_help;
    private javax.swing.JMenuItem jMI_license;
    private javax.swing.JMenuItem jMI_lookForNewVersion;
    private javax.swing.JMenuItem jMI_newGame;
    private javax.swing.JMenuItem jMI_open;
    private javax.swing.JMenuItem jMI_openCurrentGameInDetachedWindow;
    private javax.swing.JMenuItem jMI_paste;
    private javax.swing.JMenuItem jMI_pauseGame;
    private javax.swing.JMenuItem jMI_preferences;
    private javax.swing.JMenuItem jMI_resumeGame;
    private javax.swing.JMenuItem jMI_save;
    private javax.swing.JMenuItem jMI_saveAs;
    private javax.swing.JMenuItem jMI_whatIsNew;
    private javax.swing.JMenuItem jMI_whiteResigns;
    private javax.swing.JMenu jM_Edit;
    private javax.swing.JMenu jM_File;
    private javax.swing.JMenu jM_chessFigurineSet;
    private javax.swing.JMenu jM_demos;
    private javax.swing.JMenu jM_game;
    private javax.swing.JMenu jM_help;
    private javax.swing.JMenu jM_languageSubmenu;
    private javax.swing.JMenu jM_preferences;
    private javax.swing.JMenu jM_windows;
    private javax.swing.JMenu jM_zoom;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelNavigatorContainer1;
    private javax.swing.JRadioButtonMenuItem jRBMI_html_set;
    private javax.swing.JRadioButtonMenuItem jRBMI_virtualpiecesSet;
    private javax.swing.JRadioButtonMenuItem jRBMI_yuri_set;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemIdiomaEn;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemIdiomaEs;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemIdiomaOtro;
    private javax.swing.JSplitPane jSPmainSplit;
    private javax.swing.JSplitPane jSPnavigatorSplit;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JPopupMenu.Separator jSeparator23;
    private javax.swing.JPopupMenu.Separator jSeparator24;
    private javax.swing.JPopupMenu.Separator jSeparator25;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    // End of variables declaration//GEN-END:variables

	protected LicenseJDialog createLicenseJDialog( Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		return( new LicenseJDialog( this, getAppliConf(), initializationEndCallBack, false ) );
	}

	protected void showLicense()
	{
        createLicenseJDialog( this::showLicenseCallback );
	}

	protected void showLicenseCallback(InternationalizationInitializationEndCallback iiec)
	{
        LicenseJDialog ljd = (LicenseJDialog) iiec;
        ljd.setVisible(true);
        boolean closeWindow = true;
        ljd.formWindowClosing(closeWindow);
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		createExternalWindows();
//		jScrollPane1.setViewportView(jTListOfGames);

		SwingUtilities.invokeLater( () -> jTListOfGames.updateData() );

		setKeyListener( this );

		enableGameMenuItems();

		System.out.println( ViewFunctions.instance().traceComponentTreeSizes( this ) );
	}

	protected ChessGameMove getMove( MoveTreeNode mtn )
	{
		ChessGameMove result = null;
		if( mtn != null )
		{
			result = mtn.getMove();
		}

		return( result );
	}

	protected ChessGameMove getCurrentMove()
	{
		return( getMove( _currentChessGame.getCurrentMove() ) );
	}

	protected MoveTreeNode getCurrentMoveTreeGame()
	{
		MoveTreeNode result = null;

		if( _currentChessGame != null )
			result = _currentChessGame.getCurrentMove();

		return( result );
	}
	
	protected void startNewGame()
	{
		NewGameCreatorDesktopImpl newGameCreator = new NewGameCreatorDesktopImpl();
		newGameCreator.init( this, this, getApplicationContext(),
							_currentOngoingGame);

		newGameCreator.createNewGame( gpc -> setNewPlayedGame( gpc ) );
	}

	@Override
	public void setNewPlayedGame( ChessGamePlayContext gamePlayContext )
	{
		if( gamePlayContext != null )
		{
			if( thereIsOngoingGame() )
				clearGame();


			enableGameMenuItems();

			if( gamePlayContext != null )
			{
				if( gamePlayContext.hasToCreateNewGame() )
					createAndStartGame_internal( gamePlayContext );
				else
					gamePlayContext.setNewMove( _currentChessGame.getCurrentMove() );
			}

			_currentOngoingGame = gamePlayContext;

			enableDisableMenuItems( _currentChessGame.getCurrentMove(), _chessBoardPanel);
		}
	}

	protected boolean thereIsOngoingGame()
	{
		return( _currentOngoingGame != null );
	}

	protected void createAndStartGame_internal( ChessGamePlayContext gamePlayContext )
	{
		int unitsToAddToIndex = 0;
		jTListOfGames.createGame(unitsToAddToIndex);

		setGameData( gamePlayContext );
		newChessGameChosen( _currentChessGame, true );

		gamePlayContext.setNewMove( getCurrentMoveTreeGame() );
	}

	protected void setGameData( ChessGamePlayContext gamePlayContext )
	{
		ChessGameHeaderInfo header = _currentChessGame.getChessGameHeaderInfo();

		PlayerContextBase whitePc = gamePlayContext.getWhitePlayerContext();
		header.put( ChessGameHeaderInfo.WHITE_TAG, whitePc.getPlayerName() );
		if( whitePc.getPlayerElo() != null )
			header.put( ChessGameHeaderInfo.WHITEELO_TAG, whitePc.getPlayerElo().toString() );

		PlayerContextBase blackPc = gamePlayContext.getBlackPlayerContext();
		header.put( ChessGameHeaderInfo.BLACK_TAG, blackPc.getPlayerName() );
		if( blackPc.getPlayerElo() != null )
			header.put( ChessGameHeaderInfo.BLACKELO_TAG, blackPc.getPlayerElo().toString() );

		header.put( ChessGameHeaderInfo.EVENT_TAG, "ChessPdfBroswer" );
		header.put( ChessGameHeaderInfo.DATE_TAG, DateFunctions.instance().formatDate( new Date(), getOutputLocale() ) );
	}

	protected void enableGameMenuItems()
	{
		SwingUtilities.invokeLater( () -> enableGameMenuItems_internal() );
	}

	protected void enableGameMenuItems_internal()
	{
		if( ( _currentOngoingGame == null ) || _currentOngoingGame.isGameEnded() )
		{
			jMI_pauseGame.setEnabled( false );
			jMI_resumeGame.setEnabled( false );
		}
		else
		{
			boolean enablePauseGame = !_currentOngoingGame.isPaused();
			jMI_pauseGame.setEnabled( enablePauseGame );
			jMI_resumeGame.setEnabled( !enablePauseGame );
		}
	}

	protected AnalysisWindowJFrame createAnalysisWindow()
	{
		AnalysisWindowJFrame result = new AnalysisWindowJFrame( getAppliConf() );
		result.init( _analysisWindowViewController );
//		result.updateConfigurationItems( getApplicationContext().getChessEngineConfigurationPersistency().getModelContainer().getItemsConf().getList() );
		result.updateConfigurationItems( getApplicationContext().getChessEngineConfigurationPersistency()
						.getModelContainer().getComboBoxContent().getListOfItems() );

		return( result );
	}

	protected void openAnalysisWindow()
	{
		if( _analysisWindow == null )
			_analysisWindow = createAnalysisWindow();

		_analysisWindow.setVisible(true);
		_analysisWindow.setState( Frame.NORMAL );
	}

	@Override
	public ChessGame getCurrentChessGame()
	{
		return( _currentChessGame );
	}

	protected void editGameData( GameDataJDialog dial )
	{
		dial.setVisibleWithLock( true );
		if( dial.wasSuccessful() )
			newChessGameChosen( _currentChessGame, true );
	}

	protected void editGameData()
	{
		if( ( _currentChessGame != null ) && ( _listOfGames != null ) )
		{
			int index = _listOfGames.indexOf( _currentChessGame );
			ChessGameHeaderInfo header = _currentChessGame.getChessGameHeaderInfo();

			GameDataJDialog dial = new GameDataJDialog( this, true, getApplicationContext(), header, index + 1 );

			SwingUtilities.invokeLater( () -> editGameData( dial ) );
		}
	}

	protected void setVisibleAbout()
	{
		SwingUtilities.invokeLater( () -> _aboutDial.setVisibleWithLock(true) );
	}

	protected void setGameResult( String gameResult )
	{
//		if( _currentChessGame.getCurrentMove().getNumberOfChildren() > 0 )
//		{
//			HighLevelDialogs.instance().errorMessageDialog(this,
//					getInternationalString( CONF_ERROR_WHEN_TRYING_TO_SET_GAME_RESULT ) );
/*			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this,
								getInternationalString( CONF_ERROR_WHEN_TRYING_TO_RESIGN ),
								getInternationalString( CONF_BAD_STATUS_FOR_RESIGNING ),
									DialogsWrapper.ERROR_MESSAGE );
*/
//		}

		ChessGameMove cgm = getCurrentMove();
		if( cgm != null )
		{
			cgm.setResultOfGame( gameResult );
			newChessGameChosen( _currentChessGame, true );
			gameIsOver();
		}
	}

	protected void showEditCommentWindow()
	{
		editComment(_currentChessGame,
					(_currentChessGame != null ? _currentChessGame.getCurrentMove() : null ),
					null);
	}

	protected void updateEditCommentWindow()
	{
		EditCommentFrame.instance().setMove( _currentChessGame, _currentChessGame.getCurrentMove(), null );
	}

	protected void save()
	{
		String fileName = null;
		if( ( _pgnFile == null ) || ( _pgnFile.getFileName() == null ) )
			fileName = showFileChooserDialog( DialogsWrapper.SAVE );
		else
			fileName = _pgnFile.getFileName();

		if( fileName != null )
			saveAs( fileName );
	}
	
	/**
	 * 
	 * @param typeOfDialog		This parameter may take the values: DialogsWrapper.SAVE or DialogsWrapper.OPEN
	 * @return 
	 */
	protected String showFileChooserDialog( int typeOfDialog )
	{
		FilterForFile fnef1 = new FilterForFile( ".pdf : Portable document format", "pdf" );
		FilterForFile fnef2 = new FilterForFile( ".pgn : Chess game notation", "pgn" );

		List<FilterForFile> ffl = new ArrayList<FilterForFile>();
		ffl.add( fnef1 );
		ffl.add( fnef2 );

		String defaultFileName = null;

		if( typeOfDialog == DialogsWrapper.SAVE )
		{
			if( _pgnFile != null )
				defaultFileName = _pgnFile.getFileName();
		}

		String fileName = GenericFunctions.instance().getDialogsWrapper().showFileChooserDialog( this,
																					typeOfDialog,
																					ffl, defaultFileName );

		return( fileName );
	}

	protected PgnChessFile getPgnFile()
	{
		if( _pgnFile == null )
		{
//			_pgnFile = new PgnChessFile( ApplicationConfiguration.instance().getChessLanguageConfiguration() );
			_pgnFile = new PgnChessFile();
		}
		return( _pgnFile );
	}
/*
	public Component getChessBoardContainer()
	{
		return( jPanelChessBoardContainer );
	}
*/

	public void releaseGame()
	{
		if( thereIsOngoingGame() )
			_currentOngoingGame.releaseResources();

		enableMenuItemsDealingWithGameResult();

		_currentOngoingGame = null;
	}

	@Override
	public void gameIsOver()
	{
		releaseGame();
	}

	@Override
	public void clearGame()
	{
		releaseGame();
	}

	@Override
	public void setRemainingTime( Integer msLeftWhite, Integer msLeftBlack )
	{
		_chessBoardPanel.setRemainingTime( getSeconds( msLeftWhite ),
											getSeconds( msLeftBlack) );
	}

	protected Integer getSeconds( Integer ms )
	{
		Integer result = null;
		if( ms != null )
			result = ms / 1000;

		return( result );
	}

	protected void pauseCurrentGame()
	{
		pauseCurrentGame((MoveTreeNode) null, null);
	}

	protected boolean ongoingGameIsPaused()
	{
		return( _currentOngoingGame.isPaused() );
	}

	protected boolean isAppropriateSourceToInformOngoingGame( ChessMoveGenerator source )
	{
		return( ( source == _currentOngoingGame ) || ( source == _chessBoardPanel ) );
	}

	protected boolean moveDoesNotBelongToOngoingGame( MoveTreeNode mtn, ChessMoveGenerator source )
	{
		return( ( _currentOngoingGame.getCurrentMove() != mtn ) &&
					( ( mtn == null ) ||
						!( ( _currentOngoingGame.getCurrentMove() == mtn.getParent() ) &&
							isAppropriateSourceToInformOngoingGame( source )
						)
					)
				);
	}

	protected boolean isARepetition( MoveTreeNode mtn )
	{
		return( _lastProcessedMoveTreeNode == mtn );
	}

	protected void pauseCurrentGame( MoveTreeNode mtn, ChessMoveGenerator source )
	{
		if( !isARepetition(mtn) && thereIsOngoingGame() &&
			moveDoesNotBelongToOngoingGame( mtn, source ) &&
			!ongoingGameIsPaused() )
		{
			pauseGameInternal();
		}
	}

	protected boolean chessGameIsNotTheOngoingGame( ChessGame cg )
	{
		return( ( _currentOngoingGame.getCurrentMove() != null ) &&
			( _currentOngoingGame.getCurrentMove().getChessGame() != cg )
				);
	}

	protected void pauseCurrentGame( ChessGame cg )
	{
		if( ( thereIsOngoingGame()) &&
			chessGameIsNotTheOngoingGame( cg ) &&
			!ongoingGameIsPaused()
			)
		{
			pauseGameInternal();
		}
	}

	protected void pauseGameInternal()
	{
		_currentOngoingGame.pauseGame();

		enableGameMenuItems();
	}

	protected void resumeGame()
	{
		if( ( thereIsOngoingGame() ) && ongoingGameIsPaused() )
			resumeGame_internal();
	}

	protected void resumeGame_internal()
	{
		_currentOngoingGame.resumeGame();

		enableGameMenuItems();
	}

	protected MoveTreeNode insertMove( MoveTreeNode currentMtn, ChessGameMove cgm )
	{
		MoveTreeNode result = currentMtn;

		if( cgm != null )
		{
			if( ( thereIsOngoingGame() ) &&
				! ongoingGameIsPaused() ) // it must be done after checking to pause ongoing game
			{
				if( !_currentOngoingGame.hasToCreateNewGame() &&
					_currentOngoingGame.nextMoveWillBeTheFirst() )
				{
					if( _currentOngoingGame.isGameAsMainLine() )
						result = currentMtn.insertFirst( cgm );
					else
						result = currentMtn.simpleInsert( cgm );
					String comment = getFirstMoveOfGameComment( _currentOngoingGame );
					result.setComment( comment );
				}
				else
					result = currentMtn.insertFirst( cgm, currentMtn.getLevel() + 1 );
			}
			else
				result = currentMtn.simpleInsert( cgm );
		}

		return( result );
	}

	protected String getFirstMoveOfGameComment( ChessGamePlayContext currentOngoingGame )
	{
		return( this.createCustomInternationalString( CONF_FIRST_MOVE_OF_GAME,
														currentOngoingGame.getWhitePlayerContext().getPlayerString(),
														currentOngoingGame.getBlackPlayerContext().getPlayerString() ) );
	}

	@Override
//	public void newPositionInTheMovesTree( List<ChessGameMove> lcgm )
	public synchronized void newPositionInTheMovesTree( MoveTreeNode currentMtn, ChessGameMove cgm,
											ChessMoveGenerator source )
	{
		try
		{
			if( thereIsOngoingGame() )
				_currentOngoingGame.stopClock();

			if( ( currentMtn == null ) &&
				( _currentChessGame != null ) )
			{
				currentMtn = _currentChessGame.getCurrentMove();
			}

			if( ( currentMtn != null ) &&
				_currentChessGame != currentMtn.getChessGame() )
			{
				newChessGameChosen(currentMtn.getChessGame(), false);
			}

			if( thereIsOngoingGame() &&
				( _currentOngoingGame.getCurrentMove() != currentMtn ) )
			{
				_chessBoardPanel.setRemainingTime( null, null );
			}

//			MoveTreeNode node = _currentChessGame.findNode( lcgm );
			MoveTreeNode newMtn = insertMove( currentMtn, cgm );

			pauseCurrentGame( newMtn, source );
			_lastProcessedMoveTreeNode = newMtn;

			_currentChessGame.setCurrentListOfMoves( newMtn );
			_currentChessGame.updateAdditionalInfo();
			_listOfGamesHasBeenModified = true;

			boolean everyThing = true;
			_multiWindowGameManager.updateChessGameWindows(_currentChessGame, everyThing);
//			jTextPane1.update( everyThing );

			ExecutionFunctions.instance().safeMethodExecution( () -> updateEditCommentWindow() );

			_analysisWindowViewController.setNewPosition( _currentChessGame.getCurrentMove() );

			enableDisableMenuItems( newMtn, source );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	protected boolean ongoingGameIsStarted()
	{
		return( ( _currentOngoingGame != null ) && _currentOngoingGame.gameStarted() );
	}

	protected void enableDisableMenuItems( MoveTreeNode newMtn, ChessMoveGenerator source )
	{
		updateOngoingGameAndBlockBoardPanelIfNecessary( newMtn, source );
	}

	protected void enableMenuItemsDealingWithGameResult()
	{
		enableMenuItemsDealingWithGameResult( _currentChessGame.getCurrentMove() );
	}

	protected boolean whitePlayerIsAHuman()
	{
		return( ! _currentOngoingGame.getWhitePlayerContext().isEngine() );
	}

	protected boolean blackPlayerIsAHuman()
	{
		return( ! _currentOngoingGame.getBlackPlayerContext().isEngine() );
	}

	protected boolean anyOfThePlayersIsAnEngine()
	{
		return( ( _currentOngoingGame != null ) &&
				( _currentOngoingGame.getWhitePlayerContext().isEngine() ||
					_currentOngoingGame.getBlackPlayerContext().isEngine()
				)
			);
	}

	protected boolean currentGameIsNotActiveOrWhiteIsAHuman()
	{
		return( !ongoingGameIsStarted() || ongoingGameIsPaused() ||
				whitePlayerIsAHuman() );
	}

	protected boolean currentGameIsNotActiveOrBlackIsAHuman()
	{
		return( !ongoingGameIsStarted() || ongoingGameIsPaused() ||
				blackPlayerIsAHuman() );
	}

	protected boolean currentGameIsProceeding()
	{
		return( ongoingGameIsStarted() && !ongoingGameIsPaused() );
	}

	protected boolean currentGameIsActiveAndAnyOfThePlayersIsAnEngine()
	{
		return( currentGameIsProceeding() && anyOfThePlayersIsAnEngine() );
	}

	protected void enableMenuItemsDealingWithGameResult( MoveTreeNode newMtn )
	{
		boolean enabled = currentGameIsNotActiveOrWhiteIsAHuman();
		jMI_whiteResigns.setEnabled(enabled);

		enabled = currentGameIsNotActiveOrBlackIsAHuman();
		jMI_blackResigns.setEnabled(enabled);

		enabled = !currentGameIsActiveAndAnyOfThePlayersIsAnEngine();
		jMI_drawMutualAgreement.setEnabled(enabled);

		enabled = hasGameResult( newMtn );
		jMI_cleanGameResult.setEnabled(enabled);

		enabled = ! currentGameIsProceeding();
		jMI_analyzeGame.setEnabled(enabled);
	}

	protected boolean hasGameResult( MoveTreeNode newMtn )
	{
		boolean result = false;
		ChessGameMove cgm = this.getMove(newMtn);

		if( cgm != null )
			result = ( cgm.getResultOfGame() != null );

		return( result );
	}

	protected boolean isEnded( MoveTreeNode newMtn )
	{
		boolean result = false;

		ChessGameMove cgm = this.getMove(newMtn);
		if( cgm != null )
			result = ( cgm.getResultOfGame() != null );

		return( result );
	}

	protected void updateOngoingGameAndBlockBoardPanelIfNecessary( MoveTreeNode newMtn,
																	ChessMoveGenerator source )
	{
		boolean blockBoardPanel = false;
		if( thereIsOngoingGame() )
		{
			blockBoardPanel = !isEnded( newMtn ) && !ongoingGameIsPaused() &&
								!moveDoesNotBelongToOngoingGame( newMtn, source );
			if( isAppropriateSourceToInformOngoingGame( source ) )
				_currentOngoingGame.setNewMove(newMtn);

			if( blockBoardPanel )
				blockBoardPanel = _currentOngoingGame.nextMoveIsOfEngine();
		}
		_chessBoardPanel.setBlocked( blockBoardPanel );
		enableGameMenuItems();
	}

	@Override
	public void releaseResources()
	{
		getAppliConf().removeConfigurationParameterListener( ApplicationConfiguration.CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_SHOW_GAMES,
														_languageToShowGameChangeListener );
		_languageToShowGameChangeListener = null;

		getAppliConf().removeConfigurationParameterListener( ApplicationConfiguration.CONF_CHESS_FIGURINE_SET,
														_figureSetChangeListener );
		_figureSetChangeListener = null;
		
		super.releaseResources();
//		a_intern=null;	// for the garbage collector to free the memory of the internationallization object and after the memory of this form
	}

	protected String getDescriptionOfGame( ChessGame cg )
	{
		return( cg.getChessGameHeaderInfo().getDescriptionOfGame() );
	}

	protected void releaseGameIfDeleted()
	{
		if( thereIsOngoingGame() )
		{
			MoveTreeNode mtn = _currentOngoingGame.getCurrentMove();
			if( mtn != null )
			{
				ChessGame ongoingChessGame = mtn.getChessGame();
				if( !_listOfGames.contains( ongoingChessGame ) )
					gameIsOver();
			}
		}
	}

	protected void changeCurrentChessGame( ChessGame chessGame )
								throws ChessMoveException, ChessPieceCreationException,
										ChessGamePositionException, ChessModelException,
										ChessWriterException
	{
//		if( chessGame != _currentChessGame )
		{
			_currentChessGame = chessGame;

			releaseGameIfDeleted();

			jTListOfGames.setChessGame(chessGame);
			pauseCurrentGame( _currentChessGame );

			setTitle(getDescriptionOfGame( _currentChessGame ) );

			_currentChessGame.setChessViewConfiguration( ApplicationConfiguration.instance() );
			_currentChessGame.updateAdditionalInfo();

//			_currentChessGame.start();

			_chessBoardPanel.setChessBoard( _currentChessGame.getChessBoard() );
			_chessBoardPanel.setRemainingTime( null, null );

			jTextPane1.setChessGame( _currentChessGame );
			boolean everyThing = true;
//			jTextPane1.update( everyThing );
			jTextPane1.updateEverythingFully();

			_multiWindowGameManager.updateChessGameWindowsFully(chessGame);

			jTListOfGames.updateTableRow( _currentChessGame );

			if( EditTAGsJFrame.instance() != null )
				EditTAGsJFrame.instance().setChessGame( _currentChessGame );

			if( EditCommentFrame.instance() != null )
				EditCommentFrame.instance().setMove( _currentChessGame,
													( _currentChessGame != null ?
														_currentChessGame.getCurrentMove() :
														null ),
													null
													);

			ChessGameHeaderInfo headerInfo = _currentChessGame.getChessGameHeaderInfo();

			if( getAppliConf().getShowPdfGameWhenNewGameSelected() )
				_multiWindowGameManager.showPage( headerInfo.getPdfBaseFileName(),
													headerInfo.getPageIndexAtPdf() );

			_chessBoardPanel.setWhitePlayerName( _currentChessGame.getChessGameHeaderInfo().get( ChessGameHeaderInfo.WHITE_TAG ) );
			_chessBoardPanel.setBlackPlayerName( _currentChessGame.getChessGameHeaderInfo().get( ChessGameHeaderInfo.BLACK_TAG ) );
			_chessBoardPanel.setWhitePlayerElo( _currentChessGame.getChessGameHeaderInfo().get( ChessGameHeaderInfo.WHITEELO_TAG ) );
			_chessBoardPanel.setBlackPlayerElo( _currentChessGame.getChessGameHeaderInfo().get( ChessGameHeaderInfo.BLACKELO_TAG ) );

			enableDisableMenuItems( _currentChessGame.getCurrentMove(), null);

			getAnalysisController().setNewPosition( chessGame.getCurrentMove() );

			SwingUtilities.invokeLater( () -> _chessBoardPanel.repaint() );
		}
	}

	public void changeListOfChessGames( List<ChessGame> list, int indexForCurrentGame )
								throws ChessMoveException, ChessPieceCreationException,
										ChessGamePositionException, ChessModelException,
										ChessWriterException
	{
		if( ( list != null ) && ( list.size() > 0 ) )
		{
//			System.out.println( "actualizando lista de partidas ..." );

			_multiWindowGameManager.closeAllWindows();

//			if( _listOfGames != list )
			{
				_listOfGamesHasBeenModified = false;
				_listOfGames = list;
				jTListOfGames.setChessGameList(list);

//			System.out.println( "Actualizando partida ..." );

				indexForCurrentGame = IntegerFunctions.min( list.size()-1, indexForCurrentGame );
				changeCurrentChessGame( _listOfGames.get(indexForCurrentGame) );
			
//			System.out.println( "Partida actualizada ..." );
			}
		}
	}

	public void openFile( String fileName )
	{
		_lastFileName = fileName;
		String extension = FileFunctions.instance().getExtension( fileName ).toLowerCase();

		Thread loadThread = null;
		if( extension.equals( "pgn" ) )
			loadThread = new LoadPGNThread( this, fileName );
		else if( extension.equals( "pdf" ) )
		{
			_pdfLoadingPending = true;
			loadThread = new LoadPDFThread( this, fileName, _pdfDocument );
//			jCB_renderSegments.setSelected( false );
		}

		loadThread.start();
	}

	public void saveAs( String fileName )
	{
		String extension = "pgn";
		if( fileName != null )
		{
			_lastFileName = fileName;
			extension = FileFunctions.instance().getExtension( fileName ).toLowerCase();
		}

		if( (_listOfGames == null ) || ( _listOfGames.size() == 0 ) )
		{
			_listOfGames = new ArrayList< ChessGame >();
			_listOfGames.add( jTextPane1.getChessGame() );
		}

		if( !extension.equals( "pgn" ) )
			fileName = fileName + ".pgn";

		Thread saveThread = saveThread = new SavePGNThread( this, getPgnFile(), _listOfGames, fileName );
		saveThread.run();
	}

	@Override
	public void newChessGameChosen( ChessGame chessGame, boolean hasBeenModified )
	{
		try
		{
			_listOfGamesHasBeenModified = hasBeenModified;

			changeCurrentChessGame( chessGame );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog(this, th.getMessage(),
											getAppStrConf().getProperty(AppStringsConf.CONF_PROBLEM_SWITCHING_TO_NEW_GAME ),
											DialogsWrapper.ERROR_MESSAGE );
		}
	}

	@Override
	public void editInitialPosition( ChessGame cg )
	{
		ChessGamePosition oldPosition = cg.getInitialPosition();
		int oldLevel = cg.getMoveTreeGame().getLevel();

		InitialPositionDialog.instance().setChessGame( cg );

		InitialPositionDialog.instance().setVisibleWithLock( true );

		ChessGamePosition newPosition = InitialPositionDialog.instance().getPositionResult();
		if( newPosition != null )
		{
			int moveNumber = 1;
			if( cg.getMoveTreeGame().getNumberOfChildren() > 0 )
				moveNumber = cg.getMoveTreeGame().getChild(0).getMoveNumber();

//			newPosition.setMoveNumberToInitialPosition( moveNumber );

			cg.setInitialPosition(newPosition);
			try
			{
				cg.start();
				cg.updateAdditionalInfo();
				newChessGameChosen( cg, true );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				cg.setInitialPosition(oldPosition);
				try
				{
					cg.updateAdditionalInfo();
				}
				catch( Throwable th2 )
				{
					th2.printStackTrace();
				}
				if( oldPosition == null )
					cg.getMoveTreeGame().setLevel( oldLevel );
			}
		}
	}

	@Override
	public void editComment( ChessGame cg, MoveTreeNode mtn, Boolean isTypeOfCommentOfMove )
	{
		EditCommentFrame.instance().setMove( cg, mtn, isTypeOfCommentOfMove );
		EditCommentFrame.instance().setVisible(true);
		EditCommentFrame.instance().setState ( Frame.NORMAL );
	}

	protected void parsePastedText( String text )
	{
		boolean success = false;
		List<ChessGame> listOfNewGames = null;
		ChessGame newGame = null;

		try
		{
			PGNChessGameParser pgnParser = new PGNChessGameParser( EnglishChessConfiguration.instance() );

			listOfNewGames = pgnParser.parseChessGameText( text, null, null, null );
			success = true;
		}
		catch( Throwable th )
		{
		}

		if( !success )
		{
			try
			{
				ChessGamePosition cgp = new ChessGamePosition( text );
				newGame = new ChessGame( getAppliConf() );
				
				newGame.setInitialPosition( cgp );

				success = true;
			}
			catch( Throwable th )
			{
			}
		}

		if( !success )
		{
			try
			{
				RawChessGameParser rcgp = null;

				if( getAppliConf().getUseImprovedPdfGameParser() )
				{
					rcgp = new RawImprovedChessGameParser3( getAppliConf().getChessLanguageConfigurationToParseTextFrom(),
																	null, getAppliConf(), getAppliConf(), getTagsExtractor(),
																	getImagePositionController(),
																	"pasted text" );
				}
				else
				{
					rcgp = new RawChessGameParser( getAppliConf().getChessLanguageConfigurationToParseTextFrom(),
																	null, getAppliConf(), getTagsExtractor(),
																	getImagePositionController(),
																	"pasted text" );
				}

				listOfNewGames = rcgp.parseChessGameText( text, null, null, null );

				success = true;
			}
			catch( Throwable th )
			{
			}
		}

		if( !success )
		{
			showLoadingError( getAppStrConf().getProperty( AppStringsConf.CONF_ERROR ),
					getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_SCANNING_TEXT ) );			
		}
		else
		{
			int indexForCurrentGame = _listOfGames.size();

			if( listOfNewGames != null )
			{
				int index = 1;
				Iterator<ChessGame> it = listOfNewGames.iterator();
				while( it.hasNext() )
				{
					it.next().getChessGameHeaderInfo().put( ChessGameHeaderInfo.CONTROL_NAME_TAG,
															createCustomInternationalString( CONF_PASTED_GAME, index ) );
					index++;
				}

				_listOfGames.addAll( listOfNewGames );
			}

			if( newGame != null )
			{
				newGame.getChessGameHeaderInfo().put( ChessGameHeaderInfo.CONTROL_NAME_TAG,
													getInternationalString( CONF_PASTED_POSITION ) );
				_listOfGames.add( newGame );
			}

			try
			{
				changeListOfChessGames( _listOfGames, indexForCurrentGame );
				_listOfGamesHasBeenModified = true;	// this line must be the last one, because in changeListOfChessGames, it is restores to false.
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}

	protected void parsePastedText()
	{
		String pasteText = SystemClipboard.instance().getClipboardContents();
		if( pasteText != null )
			parsePastedText( pasteText );
		else
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this,
											getAppStrConf().getProperty( AppStringsConf.CONF_CLIPBOARD_CONTENT_NOT_A_STRING ),
											getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_PASTING_TEXT ),
												DialogsWrapper.ERROR_MESSAGE );
	}

	@Override
	public void startLoading()
	{
		SwingUtilities.invokeLater( this::startLoadingInternal );
	}

	public void startLoadingInternal()
	{
		jM_File.setEnabled( false );
		jM_Edit.setEnabled( false );
		jMenuView.setEnabled( false );
		jM_preferences.setEnabled( false );
		jM_windows.setEnabled( false );
		jM_help.setEnabled( false );
		jM_game.setEnabled( false );

		changeToWaitCursor();
		_multiWindowGameManager.changeToWaitCursor();
		EditTAGsJFrame.instance().changeToWaitCursor();
		EditCommentFrame.instance().changeToWaitCursor();

		if( _pdfViewerWindow != null )
			_pdfViewerWindow.block();
	}

	@Override
	public void endLoading()
	{
		SwingUtilities.invokeLater( this::endLoadingInternal );
	}

	public void endLoadingInternal()
	{
		if( !_pdfLoadingPending )
		{
			jM_File.setEnabled( true );
			jM_Edit.setEnabled( true );
			jMenuView.setEnabled( true );
			jM_preferences.setEnabled( true );
			jM_windows.setEnabled( true );
			jM_help.setEnabled( true );
			jM_game.setEnabled( true );

			revertChangeToWaitCursor();
			_multiWindowGameManager.revertChangeToWaitCursor();
			EditTAGsJFrame.instance().revertChangeToWaitCursor();
			EditCommentFrame.instance().revertChangeToWaitCursor();

			if( _pdfViewerWindow != null )
				_pdfViewerWindow.unblock();
		}
	}

	@Override
	public void showLoadingError( String message, String title )
	{
		GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, message, title,
															DialogsWrapper.ERROR_MESSAGE );
		if( _pdfLoadingPending )
			noLoadsPending();
	}

	@Override
	public void newChessListGameLoaded( List<ChessGame> list, PgnChessFile pgnFile )
	{
		try
		{
			if( thereIsOngoingGame() )
				gameIsOver();

//			System.out.println( "newChessListGameLoaded ..." );

			// if the list comes from pdf extraction ...
			boolean listComesFromPdfExtraction = ( pgnFile == null );
			if( listComesFromPdfExtraction && getAppliConf().isChessBoardRecognizerActivated() )
			{
				getApplicationContext().getChessBoardRecognizerWhole().getRecognizerThreads().setIsPaused(false);
			}

			changeListOfChessGames( list, 0 );
			_pgnFile = pgnFile;
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			showLoadingError( th.getMessage(), "Error while loading the new list of games" );
		}
	}

	@Override
	public void navigator_start( InformerInterface panel )
	{
        try
        {
            _currentChessGame.start();
			updateMoveNavigator();
        }
        catch( Throwable th )
        {
            th.printStackTrace();
        }
	}

	@Override
	public void navigator_end( InformerInterface panel )
	{
        try
        {
            _currentChessGame.end();
			updateMoveNavigator();
        }
        catch( Throwable th )
        {
            th.printStackTrace();
        }
	}

	@Override
	public void navigator_previous( InformerInterface panel )
	{
        try
        {
            _currentChessGame.back();
			updateMoveNavigator();
        }
        catch( Throwable th )
        {
            th.printStackTrace();
        }
	}

	@Override
	public void navigator_next( InformerInterface panel )
	{
        try
        {
            _currentChessGame.forward();
			updateMoveNavigator();
        }
        catch( Throwable th )
        {
            th.printStackTrace();
        }
	}

	public void updateMoveNavigator()
	{
		MoveTreeNode currentMtn = _currentChessGame.getCurrentMove();

		boolean everyThing = false;
		jTextPane1.update( everyThing );

		ChessMoveGenerator source = null;
		pauseCurrentGame( currentMtn, source );
		enableDisableMenuItems( currentMtn, source );

		updateEditCommentWindow();

		getAnalysisController().setNewPosition( currentMtn );

		SwingUtilities.invokeLater( () -> _chessBoardPanel.repaint() );
	}

	protected PdfViewerWindow getPdfViewerWindow( Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack )
	{
		if( _pdfViewerWindow == null )
		{
			_pdfViewerWindow = new PdfViewerWindow(getApplicationContext(), initializationEndCallBack);
			_pdfViewerWindow.init(this, this, getTagsExtractor(), _imagePositionController);

			_multiWindowGameManager.setPdfViewer(_pdfViewerWindow);
		}

		return( _pdfViewerWindow );
	}

	@Override
	public void newPdfLoaded(PdfDocumentWrapper pdfDocument)
	{
		boolean alreadyCreated = ( _pdfViewerWindow != null );

		PdfViewerWindow pdfViewerWindow = getPdfViewerWindow( win -> noLoadsPending() );

		pdfViewerWindow.setNewPDF( pdfDocument );

		getApplicationContext().getChessBoardRecognizerWhole().clearTasks();

		try
		{
			pdfViewerWindow.changeLanguage( getAppliConf().getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		pdfViewerWindow.setVisible( true );
		if( pdfViewerWindow.getState() == Frame.ICONIFIED )
			pdfViewerWindow.setState( Frame.NORMAL );

		if( alreadyCreated )
			noLoadsPending();
	}

	protected void noLoadsPending()
	{
		_pdfLoadingPending = false;
		endLoading();
	}


	@Override
	public void cancelLoading()
	{
	}

	/** Handle the key-released event from the text field. */
	@Override
    public void keyReleased(KeyEvent e)
	{
    }

	/** Handle the key typed event from the text field. */
	@Override
    public void keyTyped(KeyEvent event)
	{
    }

    /** Handle the key-pressed event from the text field. */
	@Override
    public void keyPressed(KeyEvent event)
	{
		if( (event.getKeyCode() == KeyEvent.VK_UP)
			|| (event.getKeyCode() == KeyEvent.VK_KP_UP) )
		{
			goToPreviousVariation();
		}
		else if (event.getKeyCode() == KeyEvent.VK_DOWN
			|| (event.getKeyCode() == KeyEvent.VK_KP_DOWN) )
		{
			goToNextVariation();
		}
		else if (event.getKeyCode() == KeyEvent.VK_LEFT
			|| (event.getKeyCode() == KeyEvent.VK_KP_LEFT) )
		{
			goToPreviousMove();
		}
		else if (event.getKeyCode() == KeyEvent.VK_RIGHT
			|| (event.getKeyCode() == KeyEvent.VK_KP_RIGHT) )
		{
			goToNextMove();
		}
    }

	protected void setNewCurrentMoveTreeNode( MoveTreeNode mtn )
	{
		if( mtn != null )
		{
			try
			{
//				_currentChessGame.setCurrentListOfMoves( mtn.getGameMoveList() );
				_currentChessGame.setCurrentMove( mtn );
				updateEditCommentWindow();

				boolean everyThing = false;
	            jTextPane1.update( everyThing );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this,
										getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_CHANGING_CURRENT_MOVE ) + th.getMessage(),
										getAppStrConf().getProperty( AppStringsConf.CONF_ERROR_BROWSING_THE_TREE ),
										DialogsWrapper.ERROR_MESSAGE );
			}
		}
	}

	protected void updateCurrentChessGameListOfMoves( MoveTreeNode mtn )
	{
		try
		{
//			_currentChessGame.setCurrentListOfMoves( mtn.getGameMoveList() );
			_currentChessGame.setCurrentListOfMoves( mtn );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void goToPreviousVariation()
	{
		if( _currentChessGame != null )
		{
			MoveTreeNode newPositionToBeSet = _currentChessGame.getCurrentMove().getPreviousVariationMoveNode();
			setNewCurrentMoveTreeNode(newPositionToBeSet);

			// to update the variation to be taken for forward
			updateCurrentChessGameListOfMoves( newPositionToBeSet );
		}
	}

	protected void goToNextVariation()
	{
		if( _currentChessGame != null )
		{
			MoveTreeNode newPositionToBeSet = _currentChessGame.getCurrentMove().getNextVariationMoveNode();
			setNewCurrentMoveTreeNode(newPositionToBeSet);

			// to update the variation to be taken for forward
			updateCurrentChessGameListOfMoves( newPositionToBeSet );
		}
	}

	protected void goToPreviousMove()
	{
		if( _currentChessGame != null )
		{
			try
			{
//			MoveTreeNode newPositionToBeSet = _currentChessGame.getCurrentMove().getParent();
				_currentChessGame.back();
				MoveTreeNode newPositionToBeSet = _currentChessGame.getCurrentMove();

				setNewCurrentMoveTreeNode(newPositionToBeSet);
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	protected void goToNextMove()
	{
		if( _currentChessGame != null )
		{
			try
			{
//			MoveTreeNode newPositionToBeSet = _currentChessGame.getCurrentMove().getChild(0);
				_currentChessGame.forward();
				MoveTreeNode newPositionToBeSet = _currentChessGame.getCurrentMove();

				setNewCurrentMoveTreeNode(newPositionToBeSet);
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	protected void setKeyListener( Component comp )
	{
		if( comp instanceof Container	)
		{
			Container contnr = (Container) comp;
			if( !( contnr instanceof JMenu ) )
			{
				for( int ii=0; ii<contnr.getComponentCount(); ii++ )
				{
					setKeyListener( contnr.getComponent(ii) );
				}
			}
		}

		comp.addKeyListener(this);
	}
/*
	protected void marcaIdiomaEnMenu( String idioma )
	{
		try
		{
			if( ( idioma.compareTo(jRadioButtonMenuItemIdiomaEs.getText()) == 0 ) &&
				! jRadioButtonMenuItemIdiomaEs.isSelected() )
					jRadioButtonMenuItemIdiomaEs.setSelected(true);
			else if ( ( idioma.compareTo( jRadioButtonMenuItemIdiomaEn.getText()) == 0 )  &&
				! jRadioButtonMenuItemIdiomaEn.isSelected() )
					jRadioButtonMenuItemIdiomaEn.setSelected(true);
			else if ( ( idioma.compareTo( jRadioButtonMenuItemIdiomaOtro.getText() ) == 0 )  &&
				! jRadioButtonMenuItemIdiomaOtro.isSelected() )
					jRadioButtonMenuItemIdiomaOtro.setSelected(true);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
*/
	protected void applyNewApplicationConfiguration()
	{
		setLanguageInMenu( getAppliConf().getLanguage() );

		_chessBoardPanel.refresh();
		_multiWindowGameManager.applyNewConfiguration();

		EditCommentFrame.instance().updateWindow();

//		float factor = getAppliConf().M_getApplicationFontSize();
//		M_changeFontSizeInApplication( factor );
	}
/*
	protected void M_changeFontSizeInApplication( float factor )
	{
		if( a_intern != null )
		{
			a_intern.M_changeFontSize( factor );
		}

		JInternalFrame[] ifArray = jMainDesktopPane.getAllFrames();
		for( int ii=0; ii<ifArray.length; ii++ )
		{
			if( ifArray[ii] instanceof FileJInternalFrame)
			{
				FileJInternalFrame jif = (FileJInternalFrame) ifArray[ii];
				
				jif.M_changeFontSize( factor );
			}
		}
	}
*/

	public void formWindowClosing( boolean closeWindow )
	{
		_multiWindowGameManager.closeAllWindows();
		if( _pdfViewerWindow != null )
			_pdfViewerWindow.closeWindow();

		if( closeWindow )
		{
			EditCommentFrame.instance().formWindowClosing(closeWindow);
			EditTAGsJFrame.instance().formWindowClosing(closeWindow);
			InitialPositionDialog.instance().formWindowClosing(closeWindow);
		}

//		setConfigurationChanges();

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

		if( getAppStrConf() != null )
		{
			try
			{
				getAppStrConf().M_saveConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		if( ChessStringsConf.instance() != null )
		{
			try
			{
				ChessStringsConf.instance().M_saveConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}

		try
		{
			getApplicationContext().getChessBoardRecognizerWhole().getChessBoardRecognitionPersistency().save();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		super.formWindowClosing( closeWindow );

		if( closeWindow )
			System.exit(0);
	}

/*
	// https://blogs.oracle.com/moonocean/entry/change_properties_of_java_tooltip
	static class ToolTipLookAndFeel extends MetalLookAndFeel
	{
		protected void initSystemColorDefaults(UIDefaults table)
		{        
			super.initSystemColorDefaults(table);        
			table.put("info", new ColorUIResource(255, 247, 200));    
		}

		protected void initComponentDefaults(UIDefaults table) {
			super.initComponentDefaults(table);

		Border border = BorderFactory.createLineBorder(new Color(76,79,83));
		table.put("ToolTip.border", border);
		}
	}
*/
/*
	protected void setConfigurationChanges() throws ConfigurationException
	{
		getAppliConf().changeLanguage(_language );
	}
*/
	protected AppStringsConf getAppStrConf()
	{
		return( _appStrConf );
	}

	protected void setWindowsNotAlwaysOnTop()
	{
		_multiWindowGameManager.setWindowsAlwaysOnTop( false );
		EditCommentFrame.instance().setAlwaysOnTop( false );
		EditTAGsJFrame.instance().setAlwaysOnTop( false );
	}

	protected void restoreWindowsAlwaysOnTop()
	{
		_multiWindowGameManager.setWindowsAlwaysOnTop( getAppliConf().getDetachedGameWindowsAlwaysOnTop() );
		EditCommentFrame.instance().updateAlwaysOnTop();
		EditTAGsJFrame.instance().updateAlwaysOnTop();
	}

	public void setHasBeenModified( boolean value )
	{
		_listOfGamesHasBeenModified |= value;
	}

	public boolean checkToSaveCurrentPGN()
	{
		boolean result = true;

		if( _listOfGamesHasBeenModified )
		{
/*
			Object[] options = new Object[] {
				getInternationalString( CONF_YES ),
				getInternationalString( CONF_NO ),
				getInternationalString( CONF_CANCEL )
			};

			int answer = GenericFunctions.instance().getDialogsWrapper().showOptionDialog( this,
														getInternationalString( CONF_CURRENT_LIST_OF_GAMES_HAS_BEEN_MODIFIED_DO_YOU_WANT_TO_SAVE_CHANGES ),
														getInternationalString( CONF_WARNING ),
														DialogsWrapper.YES_NO_CANCEL_OPTION,
														DialogsWrapper.WARNING_MESSAGE,
														options,
														null );
*/
			int answer = HighLevelDialogs.instance().yesNoCancelDialog(this,
														getInternationalString( CONF_CURRENT_LIST_OF_GAMES_HAS_BEEN_MODIFIED_DO_YOU_WANT_TO_SAVE_CHANGES ),
														null,
														null );
			if( ( answer == -1 ) || ( answer == HighLevelDialogs.CANCEL ) )		// cancelLoading
			{
				result = false;
			}
			else if( answer == HighLevelDialogs.YES )		// yes
			{
				save();
				result = !_listOfGamesHasBeenModified;
			}
			else		// no
				_listOfGamesHasBeenModified = false;		// we mark the list of games as not modified.
		}

		return( result );
	}

	protected void saveStateOfWindowAndSetNewState( Frame frame, Integer state )
	{
		if( frame != null )
		{
			int previousState = frame.getState();

			if( previousState != Frame.ICONIFIED )
				_stateOfWindows.put( frame, previousState );

			if( state != null )
				frame.setState( state );
		}
	}

	protected void saveStateOfOpenWindowsAndIconifyThem()
	{
		_stateOfWindows = new HashMap< Frame, Integer >();

		saveStateOfWindowAndSetNewState( EditCommentFrame.instance(), Frame.ICONIFIED );
		saveStateOfWindowAndSetNewState( EditTAGsJFrame.instance(), Frame.ICONIFIED );
		saveStateOfWindowAndSetNewState( _pdfViewerWindow, Frame.ICONIFIED );
		
		_multiWindowGameManager.saveStateOfOpenWindowsAndIconifyThem();
	}

	protected void restoreStateOfOpenWindows()
	{
		if( _stateOfWindows != null )
		{
			Iterator< Map.Entry< Frame, Integer> > it = _stateOfWindows.entrySet().iterator();

			while( it.hasNext() )
			{
				Map.Entry< Frame, Integer > entry = it.next();
				Frame frame = entry.getKey();

				if( frame.isVisible() )
					frame.setState( entry.getValue() );
			}

			_stateOfWindows = null;

			_multiWindowGameManager.restoreStateOfOpenWindows();
		}
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		chessFigurineButtonGroup = compMapper.mapComponent( chessFigurineButtonGroup );
		jCB_whitePlaysFromBottom = compMapper.mapComponent( jCB_whitePlaysFromBottom );
		jMI_Demos_workingWithPgns = compMapper.mapComponent( jMI_Demos_workingWithPgns );
		jMI_about = compMapper.mapComponent( jMI_about );
		jMI_analysisWindow = compMapper.mapComponent( jMI_analysisWindow );
		jMI_blackResigns = compMapper.mapComponent( jMI_blackResigns );
		jMI_cleanGameResult = compMapper.mapComponent( jMI_cleanGameResult );
		jMI_copyFEN = compMapper.mapComponent( jMI_copyFEN );
		jMI_copyPGN = compMapper.mapComponent( jMI_copyPGN );
		jMI_demos_AnalizingPositions = compMapper.mapComponent( jMI_demos_AnalizingPositions );
		jMI_demos_EditingInitialPosition = compMapper.mapComponent( jMI_demos_EditingInitialPosition );
		jMI_demos_addingUciChessEngines = compMapper.mapComponent( jMI_demos_addingUciChessEngines );
		jMI_demos_editingComments = compMapper.mapComponent( jMI_demos_editingComments );
		jMI_demos_editingTags = compMapper.mapComponent( jMI_demos_editingTags );
		jMI_demos_playingGames = compMapper.mapComponent( jMI_demos_playingGames );
		jMI_demos_positionRecognizer = compMapper.mapComponent( jMI_demos_positionRecognizer );
		jMI_demos_workingWithClipboard = compMapper.mapComponent( jMI_demos_workingWithClipboard );
		jMI_demos_workingWithPdfs = compMapper.mapComponent( jMI_demos_workingWithPdfs );
		jMI_drawMutualAgreement = compMapper.mapComponent( jMI_drawMutualAgreement );
		jMI_editComment = compMapper.mapComponent( jMI_editComment );
		jMI_editInitialPosition = compMapper.mapComponent( jMI_editInitialPosition );
		jMI_editTAGsWindow = compMapper.mapComponent( jMI_editTAGsWindow );
		jMI_exit = compMapper.mapComponent( jMI_exit );
		jMI_gameData = compMapper.mapComponent( jMI_gameData );
		jMI_help = compMapper.mapComponent( jMI_help );
		jMI_license = compMapper.mapComponent( jMI_license );
		jMI_lookForNewVersion = compMapper.mapComponent( jMI_lookForNewVersion );
		jMI_newGame = compMapper.mapComponent( jMI_newGame );
		jMI_open = compMapper.mapComponent( jMI_open );
		jMI_openCurrentGameInDetachedWindow = compMapper.mapComponent( jMI_openCurrentGameInDetachedWindow );
		jMI_paste = compMapper.mapComponent( jMI_paste );
		jMI_pauseGame = compMapper.mapComponent( jMI_pauseGame );
		jMI_preferences = compMapper.mapComponent( jMI_preferences );
		jMI_resumeGame = compMapper.mapComponent( jMI_resumeGame );
		jMI_save = compMapper.mapComponent( jMI_save );
		jMI_saveAs = compMapper.mapComponent( jMI_saveAs );
		jMI_whatIsNew = compMapper.mapComponent( jMI_whatIsNew );
		jMI_whiteResigns = compMapper.mapComponent( jMI_whiteResigns );
		jM_Edit = compMapper.mapComponent( jM_Edit );
		jM_File = compMapper.mapComponent( jM_File );
		jM_chessFigurineSet = compMapper.mapComponent( jM_chessFigurineSet );
		jM_demos = compMapper.mapComponent( jM_demos );
		jM_game = compMapper.mapComponent( jM_game );
		jM_help = compMapper.mapComponent( jM_help );
		jM_languageSubmenu = compMapper.mapComponent( jM_languageSubmenu );
		jM_preferences = compMapper.mapComponent( jM_preferences );
		jM_windows = compMapper.mapComponent( jM_windows );
		jM_zoom = compMapper.mapComponent( jM_zoom );
		jMenuBar1 = compMapper.mapComponent( jMenuBar1 );
		jMenuView = compMapper.mapComponent( jMenuView );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel3 = compMapper.mapComponent( jPanel3 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
		jPanelNavigatorContainer1 = compMapper.mapComponent( jPanelNavigatorContainer1 );
		jRBMI_html_set = compMapper.mapComponent( jRBMI_html_set );
		jRBMI_virtualpiecesSet = compMapper.mapComponent( jRBMI_virtualpiecesSet );
		jRBMI_yuri_set = compMapper.mapComponent( jRBMI_yuri_set );
		jRadioButtonMenuItemIdiomaEn = compMapper.mapComponent( jRadioButtonMenuItemIdiomaEn );
		jRadioButtonMenuItemIdiomaEs = compMapper.mapComponent( jRadioButtonMenuItemIdiomaEs );
		jRadioButtonMenuItemIdiomaOtro = compMapper.mapComponent( jRadioButtonMenuItemIdiomaOtro );
		jSPmainSplit = compMapper.mapComponent( jSPmainSplit );
		jSPnavigatorSplit = compMapper.mapComponent( jSPnavigatorSplit );
		jScrollPane1 = compMapper.mapComponent( jScrollPane1 );
		jScrollPane2 = compMapper.mapComponent( jScrollPane2 );
		jSeparator1 = compMapper.mapComponent( jSeparator1 );
		jSeparator10 = compMapper.mapComponent( jSeparator10 );
		jSeparator11 = compMapper.mapComponent( jSeparator11 );
		jSeparator12 = compMapper.mapComponent( jSeparator12 );
		jSeparator13 = compMapper.mapComponent( jSeparator13 );
		jSeparator14 = compMapper.mapComponent( jSeparator14 );
		jSeparator15 = compMapper.mapComponent( jSeparator15 );
		jSeparator16 = compMapper.mapComponent( jSeparator16 );
		jSeparator17 = compMapper.mapComponent( jSeparator17 );
		jSeparator18 = compMapper.mapComponent( jSeparator18 );
		jSeparator19 = compMapper.mapComponent( jSeparator19 );
		jSeparator2 = compMapper.mapComponent( jSeparator2 );
		jSeparator20 = compMapper.mapComponent( jSeparator20 );
		jSeparator21 = compMapper.mapComponent( jSeparator21 );
		jSeparator22 = compMapper.mapComponent( jSeparator22 );
		jSeparator23 = compMapper.mapComponent( jSeparator23 );
		jSeparator3 = compMapper.mapComponent( jSeparator3 );
		jSeparator4 = compMapper.mapComponent( jSeparator4 );
		jSeparator5 = compMapper.mapComponent( jSeparator5 );
		jSeparator6 = compMapper.mapComponent( jSeparator6 );
		jSeparator7 = compMapper.mapComponent( jSeparator7 );
		jSeparator8 = compMapper.mapComponent( jSeparator8 );
		jSeparator9 = compMapper.mapComponent( jSeparator9 );

		if( !hasBeenAlreadyMapped() )
		{
			jM_game.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					enableMenuItemsDealingWithGameResult();
				}

				@Override
				public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				}

				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
				}
			});

			fillInChessFigurineSelectionList();
			setChessFigurineSetRadioButtonSelection();
			updateChessFigurineSetConfiguration();
		}
	}

	protected TagsExtractor getTagsExtractor()
	{
		return( getApplicationContext().getTagsExtractor() );
	}

	protected void openConfiguration()
	{
		openConfiguration( (consumerInitCallback)-> createConfigurationWindowFactory( false, null, this, consumerInitCallback ) );
	}

	protected void openConfiguration(Function<Consumer<InternationalizationInitializationEndCallback>,
														JDial_applicationConfiguration> fac ) {
		try
		{
			JDial_applicationConfiguration dialog = fac.apply( this::showConfigurationWindow );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void showConfigurationWindow( InternationalizationInitializationEndCallback window )
	{
		JDial_applicationConfiguration dialog = (JDial_applicationConfiguration) window;

		try
		{
			dialog.setVisibleWithLock(true);

			if( dialog.getUserHasPressedOK() )
			{
				applyNewApplicationConfiguration();
			}
		}
		finally
		{
			if( dialog != null )
			{
				dialog.releaseResources();
				dialog.dispose();
				dialog = null;
			}
		}
	}

	protected JDial_applicationConfiguration createConfigurationWindowFactory( boolean openTagRegexConfiguration,
									ProfileModel profileModel,
									ViewComponent parentWindowVc,
									Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack
								)
	{
		JDial_applicationConfiguration result = null;

		Component parentWindow = ViewFunctions.instance().getComponent( parentWindowVc );

		if( parentWindow instanceof JFrame )
		{
			result = new JDial_applicationConfiguration( (JFrame) parentWindow, true,
														getApplicationContext(),
														openTagRegexConfiguration,
														profileModel,
														initializationEndCallBack);
		}
		else if( parentWindow instanceof JDialog )
		{
			result = new JDial_applicationConfiguration( (JDialog) parentWindow, true,
														getApplicationContext(),
														openTagRegexConfiguration,
														profileModel,
														initializationEndCallBack);
		}
		else
		{
			result = new JDial_applicationConfiguration( (JFrame) null, true,
														getApplicationContext(),
														openTagRegexConfiguration,
														profileModel,
														initializationEndCallBack);
		}

		return( result );
	}

	@Override
	public void openConfiguration( boolean openTagRegexConfiguration, ProfileModel profileModel,
									ViewComponent parentWindow )
	{

		if( !openTagRegexConfiguration )
			openConfiguration();
		else
			openConfiguration( (consumerInitCallback) -> createConfigurationWindowFactory( 
														openTagRegexConfiguration,
														profileModel, parentWindow,
														consumerInitCallback) );
	}

	public void newImagePositionOfChessGameUpdated(ChessGame cg)
	{
		if( ( cg != null ) && ( cg == getCurrentChessGame() ) )
		{
			boolean hasBeenModified = true;
			SwingUtilities.invokeLater( () -> newChessGameChosen(cg, hasBeenModified) );
		}
	}

	protected ImagePositionController createImagePositionController()
	{
		ImagePositionController result = new ImagePositionControllerBase( getApplicationContext(),
			this::newImagePositionOfChessGameUpdated);

		return( result );
	}

	@Override
	public void applyConfigurationChanges()
	{
		super.applyConfigurationChanges();

		if( _analysisWindow != null )
			_analysisWindow.updateConfigurationItems(
				getApplicationContext().getChessEngineConfigurationPersistency().getModelContainer().getItemsConf().getList()
													);
	}

	protected void demoActionPerformed( Object source )
	{
		String labelOfUrl = null;
		String url = null;
		if( source == jMI_demos_AnalizingPositions )
			labelOfUrl = CONF_DEMOS_ANALYZING_POSITIONS_DEMO_URL;
		else if( source == jMI_demos_EditingInitialPosition )
			labelOfUrl = CONF_DEMOS_EDITING_INITIAL_POSITION_DEMO_URL;
		else if( source == jMI_demos_addingUciChessEngines )
			labelOfUrl = CONF_DEMOS_ADDING_UCI_CHESS_ENGINES_DEMO_URL;
		else if( source == jMI_demos_editingComments )
			labelOfUrl = CONF_DEMOS_EDITING_COMMENTS_DEMO_URL;
		else if( source == jMI_demos_editingTags )
			labelOfUrl = CONF_DEMOS_EDITING_TAGS_DEMO_URL;
		else if( source == jMI_demos_playingGames )
			labelOfUrl = CONF_DEMOS_PLAYING_GAMES_DEMO_URL;
		else if( source == jMI_demos_positionRecognizer )
			labelOfUrl = CONF_DEMOS_POSITION_RECOGNIZER_OCR_DEMO_URL;
		else if( source == jMI_demos_workingWithClipboard )
			labelOfUrl = CONF_DEMOS_WORKING_WITH_CLIPBOARD_DEMO_URL;
		else if( source == jMI_demos_workingWithPdfs )
			labelOfUrl = CONF_DEMOS_WORKING_WITH_PDFS_DEMO_URL;
		else if( source == jMI_Demos_workingWithPgns )
			labelOfUrl = CONF_DEMOS_WORKING_WITH_PGNS_DEMO_URL;

		if( ( url == null ) && ( labelOfUrl != null ) )
			url = getInternationalString( labelOfUrl );
		
		if( url != null )
			GenericFunctions.instance().getSystem().browse( getFinalUrl(url) );
	}
/*
	protected Component processComponent( Component comp )
	{
		if( comp instanceof ZoomComponentInterface )
			ZoomFunctions.instance().updateZoomUI( (ZoomComponentInterface) comp,
				new DoubleReference( getZoomFactor() ) );
		else if( comp instanceof JComponent )
			( ( JComponent ) comp ).updateUI();

		if( comp.isVisible() )
			SwingUtilities.invokeLater( comp::repaint );

		return( null );
	}
*/

	protected static class ChessFigurineSelection
	{
		protected FigureSet _configurationValue;
		protected JRadioButtonMenuItem _radioButton;

		public ChessFigurineSelection( FigureSet configurationValue,
										JRadioButtonMenuItem radioButton )
		{
			_configurationValue = configurationValue;
			_radioButton = radioButton;
		}

		public FigureSet getConfigurationValue() {
			return _configurationValue;
		}

		public String getResourcePath() {
			return _configurationValue.getResourcePath();
		}

		public JRadioButtonMenuItem getRadioButton() {
			return _radioButton;
		}

		protected String getResourceNameForIcon()
		{
			return( getResourcePath() + "/" + ChessBoardImages.instance().getSimpleResourceName( ChessFigure.WHITE_QUEEN ) );
		}

		public ZoomIconImp getIcon()
		{
			ZoomIconImp result = null;
			try
			{
				result = ZoomIconBuilder.instance().createSquaredHundredPercentZoomIconDefaultForMenuItem( getResourceNameForIcon() );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}

			return( result );
		}
	}

}
