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
import com.frojasg1.applications.common.components.internationalization.window.InternationalizationInitializationEndCallback;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJDialog;
import com.frojasg1.applications.common.components.internationalization.window.InternationalizedJFrame;
import com.frojasg1.applications.common.components.internationalization.window.exceptions.ValidationException;
import com.frojasg1.applications.common.components.resizecomp.MapResizeRelocateComponentItem;
import com.frojasg1.applications.common.components.resizecomp.ResizeRelocateItem;
import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.chesspdfbrowser.configuration.AppStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertControllerInterface;
import com.frojasg1.general.desktop.view.panels.AcceptCancelRevertPanel;
import com.frojasg1.chesspdfbrowser.engine.init.ApplicationInitContext;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items.ChessEngineConfigurationMap;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.controller.EngineComboControllerBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.WholeEngineMasterComboBase;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.wholecombo.impl.WholeEngineMasterComboForEngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.RegexOfBlockModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.items.ListOfRegexWholeFiles;
import com.frojasg1.chesspdfbrowser.model.regex.whole.loader.RegexFilesPersistency;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.FlipBoardMode;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessBoardImages;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessFigure;
import com.frojasg1.general.dialogs.highlevel.HighLevelDialogs;
import com.frojasg1.chesspdfbrowser.view.chess.regex.controller.RegexComboControllerBase;
import com.frojasg1.chesspdfbrowser.view.chess.regex.managers.BlockComboManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.managers.FileComboManager;
import com.frojasg1.chesspdfbrowser.view.chess.regex.managers.ProfileComboManager;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.ResourceFunctions;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.combohistory.TextComboBoxContent;
import com.frojasg1.general.desktop.image.ImageFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.ContainerFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.buttons.ResizableImageJButton;
import com.frojasg1.general.desktop.view.combobox.MasterComboBoxJPanel;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentChildComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ChainedParentComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.ComboBoxGroupManager;
import com.frojasg1.general.desktop.view.combobox.chained.impl.ChainedParentChildComboBoxManagerBase;
import com.frojasg1.general.desktop.view.combobox.renderer.ComboCellRendererBase;
import com.frojasg1.general.desktop.view.combobox.utils.ComboBoxFunctions;
import com.frojasg1.general.desktop.view.panels.InformerInterface;
import com.frojasg1.general.desktop.view.panels.languages.ConfigurationServerForLanguage;
import com.frojasg1.general.desktop.view.panels.languages.LanguagesConfigurationJPanel;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.dialogs.FileChooserParameters;
import com.frojasg1.general.dialogs.filefilter.FilterForFile;
import com.frojasg1.general.structures.Pair;
import com.frojasg1.general.threads.ThreadFunctions;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 *
 * @author Usuario
 */
public class JDial_applicationConfiguration extends InternationalizedJDialog<ApplicationInitContext>
										implements AcceptCancelRevertControllerInterface,
													ConfigurationServerForLanguage
{
	protected static final int TAG_REGEX_CONFIGURATION_TAB_INDEX = 3;

	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();
	protected final static String CONF_IT_WAS_A_FOLDER_CANNOT_EXPORT = "IT_WAS_A_FOLDER_CANNOT_EXPORT";
	protected final static String CONF_XML_FILE_IMPORTED_CORRECTLY = "FILE_XML_IMPORTED_CORRECTLY";
	protected final static String CONF_XML_FILE_EXPORTED_CORRECTLY = "FILE_XML_EXPORTED_CORRECTLY";
	protected final static String CONF_ERROR_IMPORTING_XML_FILE = "ERROR_IMPORTING_XML_FILE";
	protected final static String CONF_ERROR_EXPORTING_XML_FILE = "ERROR_EXPORTING_XML_FILE";
	protected final static String CONF_INFORMATION = "INFORMATION";
	protected final static String CONF_ERROR = "ERROR";
	protected final static String CONF_FILE_EXISTS_OVERWRITE = "FILE_EXISTS_OVERWRITE";
	protected static final String CONF_A_FILE_WITH_THAT_NAME_ALREADY_EXISTS_CANNOT_IMPORT = "A_FILE_WITH_THAT_NAME_ALREADY_EXISTS_CANNOT_IMPORT";
	protected static final String CONF_NOT_A_VALID_INT = "NOT_A_VALID_INT";
	protected static final String CONF_VALUE_MUST_BE_GREATER_OR_EQUAL_THAN_ONE = "VALUE_MUST_BE_GREATER_OR_EQUAL_THAN_ONE";

	protected static final String CONF_THIS_ACTION_INVOLVES_ERASING_THIS_FOLDER_COMPLETELY_DO_YOU_AGREE_TO_PROCEED = "THIS_ACTION_INVOLVES_ERASING_THIS_FOLDER_COMPLETELY_DO_YOU_AGREE_TO_PROCEED";
	protected static final String CONF_ERROR_RESETING_CHESS_BOARD_RECOGNIZER = "ERROR_RESETING_CHESS_BOARD_RECOGNIZER";
	protected static final String CONF_ERROR_RELOADING_CHESS_BOARD_RECOGNIZER = "ERROR_RELOADING_CHESS_BOARD_RECOGNIZER";
	protected static final String CONF_SUCCESSFUL_RESET_OF_CHESS_BOARD_RECOGNIZER = "SUCCESSFUL_RESET_OF_CHESS_BOARD_RECOGNIZER";
	protected static final String CONF_SUCCESSFUL_RELOAD_OF_CHESS_BOARD_RECOGNIZER = "SUCCESSFUL_RELOAD_OF_CHESS_BOARD_RECOGNIZER";

	protected AcceptCancelRevertPanel _acceptPanel = null;

	protected boolean a_userHasPressedOK = false;
	protected String a_languageInConstructor = null;
	protected final static String a_configurationBaseFileName = "JDial_applicationConfiguration";

	protected Map<Component, ComboBoxOfChessLanguageData> _mapComboBoxes = null;

	protected boolean _modifiedByProgram = false;

	protected LanguagesConfigurationJPanel _languagesConfigurationJPanel = null;

	protected InternationalizedJFrame _mainWindowParent = null;

//	protected MasterComboBoxJPanel _masterComboForEngines = null;

	protected MasterComboBoxJPanel _masterComboForFiles = null;
	protected MasterComboBoxJPanel _masterComboForBlocks = null;
	protected MasterComboBoxJPanel _masterComboForProfiles = null;
//	protected MasterComboBoxJPanel _masterComboForTags = null;

//	protected RegexWholeFileModel _lastConfirmedWholeRegexModel = null;
//	protected RegexWholeFileModel _wholeRegexModel = null;
	protected ListOfRegexWholeFiles _lastListOfRegexFiles = null;
	protected ListOfRegexWholeFiles _listOfRegexFiles = null;

	protected ChessEngineConfigurationMap _lastChessEngineConfigurationMap = null;
	protected ChessEngineConfigurationMap _chessEngineConfigurationMap = null;

	protected RegexFilesPersistency _regexFilesPersistency = null;

	protected RegexComboControllerBase _regexComboController = null;
//	protected EngineComboControllerBase _engineComboController = null;
	protected WholeEngineMasterComboBase _wholeEngineMasterCombo = null;

	protected ProfileModel _profileModel = null;

	protected boolean _openRegexConfiguration = false;

	protected Pair<JRadioButton, FlipBoardMode>[] _flipBoardModeRadioButtonArray;



	public JDial_applicationConfiguration(java.awt.Frame parent, boolean modal,
											ApplicationInitContext applicationContext )
	{
		this( parent, modal, applicationContext, false, null, null );
	}

	public JDial_applicationConfiguration(JDialog dialog, boolean modal,
											ApplicationInitContext applicationContext )
	{
		this( dialog, modal, applicationContext, false, null, null );
	}

	/**
	 * Creates new form JDial_applicationConfiguration
	 */
	public JDial_applicationConfiguration(java.awt.Frame parent, boolean modal,
											ApplicationInitContext applicationContext,
											boolean openRegexConfiguration,
											ProfileModel profileModel,
											Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack
											)
	{
		super(parent, modal, ApplicationConfiguration.instance(), applicationContext,
			initializationEndCallBack, true );
		init( parent, modal, openRegexConfiguration, profileModel );
	}

	public JDial_applicationConfiguration(JDialog parent, boolean modal,
											ApplicationInitContext applicationContext,
											boolean openRegexConfiguration,
											ProfileModel profileModel,
											Consumer<InternationalizationInitializationEndCallback> initializationEndCallBack
											)
	{
		super(parent, modal, ApplicationConfiguration.instance(), applicationContext,
			initializationEndCallBack, true );
		init( parent, modal, openRegexConfiguration, profileModel );
	}

	protected ChessEngineConfigurationPersistency getChessEngineConfigurationPersistency()
	{
		return( getApplicationContext().getChessEngineConfigurationPersistency() );
	}

	protected final void init( Component parent, boolean modal,
								boolean openRegexConfiguration, ProfileModel profileModel)
	{
			setPreventFromRepainting(true);

			_profileModel = profileModel;
			_openRegexConfiguration = openRegexConfiguration;

			_regexFilesPersistency = getApplicationContext().getRegexWholeContainerPersistency();
			_listOfRegexFiles = _regexFilesPersistency.getModelContainer();
			_lastListOfRegexFiles = _copier.copy( _listOfRegexFiles );

			_chessEngineConfigurationMap = getChessEngineConfigurationPersistency().getModelContainer();
			_lastChessEngineConfigurationMap = _copier.copy( _chessEngineConfigurationMap );

	//		_wholeRegexModel = _regexFilesPersistency.getRegexWholeContainer();

	//		_lastConfirmedWholeRegexModel = _copier.copy(_wholeRegexModel );
	//		ExecutionFunctions.instance().safeMethodExecution( () -> _wholeRegexModel.save() );

			initComponents();
			initOwnComponents();

			_appliConf = ApplicationConfiguration.instance();

			a_languageInConstructor = _appliConf.getLanguage();

			setWindowConfiguration();

			M_initializeComponentContents();

			boolean initialConfiguration = true;
			fillInComboBoxesOfChessLanguage( initialConfiguration );
	//		float factor = ApplicationConfiguration.instance().M_getApplicationFontSize();

			if( modal )
				setAlwaysOnTop( true );

			if( parent instanceof InternationalizedJFrame )
				_mainWindowParent = (InternationalizedJFrame) parent;
	}

	protected void initOwnComponents()
	{
		System.out.println( "jDial_applicationConfiguration" );
		_acceptPanel = new AcceptCancelRevertPanel( this );
		ContainerFunctions.instance().addComponentToCompletelyFillParent(jPanel5, _acceptPanel );

		_languagesConfigurationJPanel = new LanguagesConfigurationJPanel( this, getAppliConf() );
		ContainerFunctions.instance().addComponentToCompletelyFillParent(jP_language, _languagesConfigurationJPanel );

		createRegexComboboxes();
		ContainerFunctions.instance().addComposedComponentToParent(jPanel6, _masterComboForFiles );
		ContainerFunctions.instance().addComposedComponentToParent(jPanel2, _masterComboForBlocks );
		ContainerFunctions.instance().addComposedComponentToParent(jPanel4, _masterComboForProfiles );

		createEngineCombobox();
		ContainerFunctions.instance().addComposedComponentToParent(jPanel7, getMasterComboForEngines() );

		jL_engineDownloadWebPage.setFont( FontFunctions.instance().getUnderlinedFont( jL_engineDownloadWebPage.getFont() ) );

//		ContainerFunctions.instance().addComponentToCompletelyFillParent(jPanel3, _masterComboForTags );


//		_masterComboForProfiles = createMasterComboBoxForProfiles();
//		_masterComboForTags = createMasterComboBoxForTags();
/*
		jPanel2.
	protected MasterComboBoxJPanel _masterComboForBlocks = null;
	protected MasterComboBoxJPanel _masterComboForProfiles = null;
	protected MasterComboBoxJPanel _masterComboForTags = null;

	protected RegexWholeFileModel _wholeRegexModel = null;
*/		
	}

	@Override
	public void internationalizationInitializationEndCallback()
	{
		super.internationalizationInitializationEndCallback();

		_flipBoardModeRadioButtonArray = createFilpBoardModeRadioButtonArray();
		updateFlipBoardModeContents();

		if( _openRegexConfiguration )
		{
			SwingUtilities.invokeLater( ()->openProfile( _profileModel ) );
		}

		ThreadFunctions.instance().delayedInvoke( () -> {
			updateFigureSample();
		}, 1000);
	}

	protected Pair<JRadioButton, FlipBoardMode>[] createFilpBoardModeRadioButtonArray()
	{
		Pair<JRadioButton, FlipBoardMode>[] result = ArrayFunctions.instance().createArray(
			new Pair( jRB_FlipBoardMode_Auto, FlipBoardMode.AUTO ),
			new Pair( jRB_FlipBoardMode_WhiteOnBottom1, FlipBoardMode.WHITE_ON_THE_BOTTOM ),
			new Pair( jRB_FlipBoardMode_BlackPlayerOnBottom1, FlipBoardMode.BLACK_ON_THE_BOTTOM )
		);

		return( result );
	}

	protected FlipBoardMode getSelectedFlipBoardMode()
	{
		FlipBoardMode result = null;
		if( _flipBoardModeRadioButtonArray != null )
		{
			for( int ii=0; ii<_flipBoardModeRadioButtonArray.length; ii++ )
			{
				Pair<JRadioButton, FlipBoardMode> elem = _flipBoardModeRadioButtonArray[ii];
				if( elem.getKey().isSelected() )
				{
					result = elem.getValue();
					break;
				}
			}
		}

		return( result );
	}

	protected MasterComboBoxJPanel createMasterComboBoxJPanel( ComboBoxGroupManager cbgm, boolean init )
	{
		MasterComboBoxJPanel result = new MasterComboBoxJPanel( cbgm );
		if( init )
			result.init();

		return( result );
	}

	protected ChainedParentChildComboBoxGroupManager createProfileComboBoxGroupManager( ChainedParentComboBoxGroupManager parent,
																						RegexComboControllerBase controller )
	{
		String key = null;
		ProfileComboManager result = new ProfileComboManager(parent);
		result.init( controller );

		return( result );
	}

/*
	protected ChainedParentChildComboBoxManagerBase createProfileComboBoxGroupManager( TextComboBoxContent content,
//																ChainedParentForChildComboContentServer contentServerForChildren,
																ChainedParentComboBoxGroupManager parent)
	{
		String key = null;
		ChainedParentChildComboBoxManagerBase result = new ChainedParentChildComboBoxManagerBase( key,
																				content,
//																				contentServerForChildren,
																				null,
																				parent ) {

			@Override
			protected ListCellRenderer createRendererForCombos( JComboBox combo )
			{
				return( new ComboCellRenderer( combo ) );
			}
		};
		result.init();

		return( result );
	}

	protected ChainedParentChildComboBoxManagerBase createRegexBlockComboBoxGroupManager( //TextComboBoxContent content,
//																ChainedParentForChildComboContentServer contentServerForChildren,
																ChainedParentComboBoxGroupManager parent)
	{
		String key = null;
		ChainedParentChildComboBoxManagerBase result = new ChainedParentChildComboBoxManagerBase( key,
//																				content,
																				null,
//																				contentServerForChildren,
																				null,
																				parent );
		result.init();

		return( result );
	}
*/
	protected ChainedParentChildComboBoxManagerBase createRegexBlockComboBoxGroupManager( ChainedParentComboBoxGroupManager parent )
	{
		String key = null;
		ChainedParentChildComboBoxManagerBase result = new BlockComboManager( parent );
		result.init();

		return( result );
	}

	protected ChainedParentComboBoxGroupManager createRegexFileComboBoxGroupManager( TextComboBoxContent content,
																RegexComboControllerBase controller )
	{
		String key = null;
		FileComboManager result = new FileComboManager( content,
														controller );
		result.init(controller);

		return( result );
	}

	protected void createEngineCombobox()
	{
		_wholeEngineMasterCombo = new WholeEngineMasterComboForEngineInstanceConfiguration( getAppliConf(),
											_chessEngineConfigurationMap, this );
		_wholeEngineMasterCombo.init();
//		_engineComboController = createEngineComboControllerBase();
	}

	protected EngineComboControllerBase getEngineComboController()
	{
		return( _wholeEngineMasterCombo.getEngineComboController() );
	}

	protected MasterComboBoxJPanel getMasterComboForEngines()
	{
		return( _wholeEngineMasterCombo.getMasterComboBoxJPanel() );
	}
/*
	protected EngineComboControllerBase createEngineComboControllerBase()
	{
		EngineComboControllerBase result = new EngineComboControllerBase();
		ChainedParentChildComboBoxManagerBase engineCbgMan = new ChainedParentChildComboBoxManagerBase( null,
											_chessEngineConfigurationMap.getComboBoxContent(),
											null );
		engineCbgMan.init();

		_masterComboForEngines = createMasterComboBoxJPanel( engineCbgMan, false );

		result.init( getAppliConf(), _chessEngineConfigurationMap,
					engineCbgMan, this );

		_masterComboForEngines.init();

		return( result );
	}
*/
	protected void createRegexComboboxes()
	{
		_regexComboController = createRegexComboControllerBase();
	}

	protected RegexComboControllerBase createRegexComboControllerBase()
	{
		RegexComboControllerBase result = new RegexComboControllerBase();
		ChainedParentComboBoxGroupManager filesCbgMan = createRegexFileComboBoxGroupManager( _listOfRegexFiles.getComboBoxContent(),
																							result );

		ChainedParentChildComboBoxGroupManager blockCbMan = createRegexBlockComboBoxGroupManager( filesCbgMan );

		_masterComboForBlocks = createMasterComboBoxJPanel( blockCbMan, true );

		ChainedParentChildComboBoxGroupManager profileCbMan = createProfileComboBoxGroupManager( filesCbgMan, result );
		_masterComboForProfiles = createMasterComboBoxJPanel( profileCbMan, true );

		_masterComboForFiles = createMasterComboBoxJPanel( filesCbgMan, false );
//		ComboBoxGroupManager tagCbgMan = createProfileComboBoxGroupManager( null, null, profileCbMan );
//		_masterComboForTags = createMasterComboBoxJPanel( tagCbgMan );
		// at the end -> for the chain of combos work correctly

		result.init(_listOfRegexFiles, filesCbgMan, blockCbMan, profileCbMan, this,
					getApplicationContext().getWholeCompletionManager() );

		_masterComboForFiles.init();

//		_masterComboForProfiles = createMasterComboBoxForProfiles();
//		_masterComboForTags = createMasterComboBoxForTags();

		return( result );
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


		MapResizeRelocateComponentItem mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			mapRRCI.putResizeRelocateComponentItem( jPanel1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
//			mapRRCI.putResizeRelocateComponentItem( jTP_tabbedPane, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
//			mapRRCI.putResizeRelocateComponentItem( jPanel5, ResizeRelocateItem.MOVE_TO_BOTTOM );
			mapRRCI.putResizeRelocateComponentItem( jPanel5, 0 );

//			mapRRCI.putResizeRelocateComponentItem( _masterComboForFiles, ResizeRelocateItem.FILL_WHOLE_PARENT );
//			mapRRCI.putResizeRelocateComponentItem( _masterComboForBlocks, ResizeRelocateItem.FILL_WHOLE_PARENT );
//			mapRRCI.putResizeRelocateComponentItem( _masterComboForProfiles, ResizeRelocateItem.FILL_WHOLE_PARENT );

//			mapRRCI.putResizeRelocateComponentItem( _masterComboForTags, ResizeRelocateItem.FILL_WHOLE_PARENT );
			mapRRCI.putResizeRelocateComponentItem( _acceptPanel, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );

			mapRRCI.putAll( _acceptPanel.getResizeRelocateInfo() );
			mapRRCI.putAll( _masterComboForFiles.getResizeRelocateInfo() );
			mapRRCI.putAll( _masterComboForBlocks.getResizeRelocateInfo() );
			mapRRCI.putAll( _masterComboForProfiles.getResizeRelocateInfo() );
			mapRRCI.putAll( getMasterComboForEngines().getResizeRelocateInfo() );

		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		createInternationalization(	getAppliConf().getConfigurationMainFolder(),
									getAppliConf().getApplicationNameFolder(),
									getAppliConf().getApplicationGroup(),
									getAppliConf().getInternationalPropertiesPathInJar(),
									a_configurationBaseFileName,
									this,
									getParent(),
									null,
									true,
									mapRRCI );

		this.registerInternationalString(CONF_IT_WAS_A_FOLDER_CANNOT_EXPORT, "$1 is a folder. Cannot export" );
		this.registerInternationalString(CONF_XML_FILE_IMPORTED_CORRECTLY, "xml file imported correctly" );
		this.registerInternationalString(CONF_XML_FILE_EXPORTED_CORRECTLY, "xml file exported correctly" );
		this.registerInternationalString(CONF_ERROR_IMPORTING_XML_FILE, "Error importing xml file: $1" );
		this.registerInternationalString(CONF_ERROR_EXPORTING_XML_FILE, "Error exporting xml file: $1" );
		this.registerInternationalString(CONF_INFORMATION, "Information" );
		this.registerInternationalString(CONF_ERROR, "Error" );
		this.registerInternationalString(CONF_FILE_EXISTS_OVERWRITE, "File exists. Ok to overwrite?" );
		this.registerInternationalString(CONF_A_FILE_WITH_THAT_NAME_ALREADY_EXISTS_CANNOT_IMPORT, "A file with that name ($1) already exists.\nCannot import" );
		this.registerInternationalString( CONF_NOT_A_VALID_INT, "Not a valid int" );
		this.registerInternationalString( CONF_VALUE_MUST_BE_GREATER_OR_EQUAL_THAN_ONE, "Value must be greater or equal than 1" );
		this.registerInternationalString( CONF_THIS_ACTION_INVOLVES_ERASING_THIS_FOLDER_COMPLETELY_DO_YOU_AGREE_TO_PROCEED,
											"This action involves erasing completely this folder:\n$1\nDo you agree to proceed?" );
		this.registerInternationalString( CONF_ERROR_RESETING_CHESS_BOARD_RECOGNIZER, "Error reseting chess board recognizer: $1" );
		this.registerInternationalString( CONF_ERROR_RELOADING_CHESS_BOARD_RECOGNIZER, "Error reloading chess board recognizer: $1" );
		this.registerInternationalString( CONF_SUCCESSFUL_RESET_OF_CHESS_BOARD_RECOGNIZER, "Successful reset of chess board recognizer" );
		this.registerInternationalString( CONF_SUCCESSFUL_RELOAD_OF_CHESS_BOARD_RECOGNIZER, "Successful reload of chess board recognizer" );

		this.registerInternationalString(FigureSet.HTML_SET.name(), "HTML set" );
		this.registerInternationalString(FigureSet.VIRTUAL_PIECES_SET.name(), "Yuri's set" );
		this.registerInternationalString(FigureSet.YURI_SET.name(), "VirtualPieces' set" );
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator2 = new javax.swing.JSeparator();
        bG_flipBoardMode = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTP_tabbedPane = new javax.swing.JTabbedPane();
        jP_chessView = new javax.swing.JPanel();
        jCB_showComments = new javax.swing.JCheckBox();
        jCB_showNAGs = new javax.swing.JCheckBox();
        jL_languageToShowGames1 = new javax.swing.JLabel();
        jCB_languageToShowGames1 = new javax.swing.JComboBox();
        jTF_languageToShowGamesPiecesString1 = new javax.swing.JTextField();
        jCB_detachedWindowsAlwaysOnTop = new javax.swing.JCheckBox();
        jL_setOfFigures = new javax.swing.JLabel();
        jCB_setOfFigures = new javax.swing.JComboBox<>();
        jB_pieceSample = new javax.swing.JButton();
        jP_language = new javax.swing.JPanel();
        jP_pdfParser = new javax.swing.JPanel();
        jP_pdfParser2 = new javax.swing.JPanel();
        jCB_experimentalParser = new javax.swing.JCheckBox();
        jP_languageForChessGames = new javax.swing.JPanel();
        jL_languageToParseGamesFrom = new javax.swing.JLabel();
        jCB_languageToParseGamesFrom = new javax.swing.JComboBox();
        jTF_languageToParseGamesFromPiecesString = new javax.swing.JTextField();
        jL_languageToShowGames = new javax.swing.JLabel();
        jCB_languageToShowGames = new javax.swing.JComboBox();
        jTF_languageToShowGamesPiecesString = new javax.swing.JTextField();
        jP_gameTagsExtractor = new javax.swing.JPanel();
        jP_RegexFileConfiguration = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jL_RegexFile = new javax.swing.JLabel();
        jB_enableAllFiles = new javax.swing.JButton();
        jB_disableAllFiles = new javax.swing.JButton();
        jB_import = new javax.swing.JButton();
        jB_export = new javax.swing.JButton();
        jB_importInitialRegexConfiguration = new javax.swing.JButton();
        jP_RegexFile = new javax.swing.JPanel();
        jP_BlockRegexConf = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jL_blockRegexes = new javax.swing.JLabel();
        jP_tagRegexProfileConfiguration = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jL_profiles = new javax.swing.JLabel();
        jB_enableAll = new javax.swing.JButton();
        jB_enable = new javax.swing.JButton();
        jB_disableAll = new javax.swing.JButton();
        jP_chessBoardRecognizer = new javax.swing.JPanel();
        jP_configurationForThreads = new javax.swing.JPanel();
        jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition = new javax.swing.JLabel();
        jS_ocrNumberOfThreads = new javax.swing.JSpinner();
        jCB_activateChessBoardRecognition = new javax.swing.JCheckBox();
        jP_chessBoardRecognizer_reset = new javax.swing.JPanel();
        jB_chessBoardRecognizerReset = new javax.swing.JButton();
        jL_chessBoardRecognizerReset1 = new javax.swing.JLabel();
        jB_chessBoardRecognizer_reload = new javax.swing.JButton();
        jL_chessBoardRecognizer_reload1 = new javax.swing.JLabel();
        jP_FlipBoardMode = new javax.swing.JPanel();
        jRB_FlipBoardMode_Auto = new javax.swing.JRadioButton();
        jRB_FlipBoardMode_WhiteOnBottom1 = new javax.swing.JRadioButton();
        jRB_FlipBoardMode_BlackPlayerOnBottom1 = new javax.swing.JRadioButton();
        jP_chessEngines = new javax.swing.JPanel();
        jP_chessEngineConfiguration = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jL_Engine = new javax.swing.JLabel();
        jL_engineDownloadWebPage = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Configuration window");
        setMinimumSize(new java.awt.Dimension(590, 455));
        setName("ConfigurationDialog"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setLayout(null);

        jTP_tabbedPane.setName("jTP_tabbedPane"); // NOI18N

        jP_chessView.setName("jP_chessView"); // NOI18N
        jP_chessView.setLayout(null);

        jCB_showComments.setText("Show comments");
        jCB_showComments.setName("jCB_showComments"); // NOI18N
        jP_chessView.add(jCB_showComments);
        jCB_showComments.setBounds(50, 25, 320, 24);

        jCB_showNAGs.setText("Show NAGs");
        jCB_showNAGs.setName("jCB_showNAGs"); // NOI18N
        jP_chessView.add(jCB_showNAGs);
        jCB_showNAGs.setBounds(50, 70, 340, 24);

        jL_languageToShowGames1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jL_languageToShowGames1.setText("Language to show games :");
        jL_languageToShowGames1.setName("jL_languageToShowGames"); // NOI18N
        jP_chessView.add(jL_languageToShowGames1);
        jL_languageToShowGames1.setBounds(50, 245, 270, 15);

        jCB_languageToShowGames1.setMinimumSize(new java.awt.Dimension(33, 20));
        jCB_languageToShowGames1.setName("jCB_languageToShowGames1"); // NOI18N
        jCB_languageToShowGames1.setPreferredSize(new java.awt.Dimension(33, 20));
        jCB_languageToShowGames1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_languageToShowGames1ActionPerformed(evt);
            }
        });
        jP_chessView.add(jCB_languageToShowGames1);
        jCB_languageToShowGames1.setBounds(50, 275, 170, 20);

        jTF_languageToShowGamesPiecesString1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTF_languageToShowGamesPiecesString1FocusLost(evt);
            }
        });
        jP_chessView.add(jTF_languageToShowGamesPiecesString1);
        jTF_languageToShowGamesPiecesString1.setBounds(230, 275, 80, 20);

        jCB_detachedWindowsAlwaysOnTop.setText("Show chess game detached windows always on top");
        jCB_detachedWindowsAlwaysOnTop.setName("jCB_detachedWindowsAlwaysOnTop"); // NOI18N
        jP_chessView.add(jCB_detachedWindowsAlwaysOnTop);
        jCB_detachedWindowsAlwaysOnTop.setBounds(50, 115, 500, 24);

        jL_setOfFigures.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jL_setOfFigures.setText("Set of figures :");
        jL_setOfFigures.setName("jL_setOfFigures"); // NOI18N
        jP_chessView.add(jL_setOfFigures);
        jL_setOfFigures.setBounds(50, 160, 205, 20);

        jCB_setOfFigures.setMinimumSize(new java.awt.Dimension(33, 20));
        jCB_setOfFigures.setName("jCB_setOfFigures"); // NOI18N
        jCB_setOfFigures.setPreferredSize(new java.awt.Dimension(33, 20));
        jCB_setOfFigures.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_setOfFiguresActionPerformed(evt);
            }
        });
        jP_chessView.add(jCB_setOfFigures);
        jCB_setOfFigures.setBounds(55, 195, 250, 20);

        jB_pieceSample.setName("name=jB_pieceSample,icon=com/frojasg1/chesspdfbrowser/resources/chess/images/virtualpieces/processed.white.queen.png"); // NOI18N
        jP_chessView.add(jB_pieceSample);
        jB_pieceSample.setBounds(5, 175, 40, 40);

        jTP_tabbedPane.addTab("Chess view", jP_chessView);

        jP_language.setName("jP_language"); // NOI18N
        jP_language.setLayout(null);
        jTP_tabbedPane.addTab("Language", jP_language);

        jP_pdfParser.setName("jP_pdfParser"); // NOI18N
        jP_pdfParser.setLayout(null);

        jP_pdfParser2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "PDF parser"));
        jP_pdfParser2.setName("jP_pdfParser2"); // NOI18N
        jP_pdfParser2.setLayout(null);

        jCB_experimentalParser.setText("Use experimental parser");
        jCB_experimentalParser.setName("jCB_experimentalParser"); // NOI18N
        jP_pdfParser2.add(jCB_experimentalParser);
        jCB_experimentalParser.setBounds(40, 40, 470, 24);

        jP_pdfParser.add(jP_pdfParser2);
        jP_pdfParser2.setBounds(10, 30, 570, 110);

        jP_languageForChessGames.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Language for chess game moves"));
        jP_languageForChessGames.setName("jP_languageForChessGames"); // NOI18N
        jP_languageForChessGames.setLayout(null);

        jL_languageToParseGamesFrom.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jL_languageToParseGamesFrom.setText("Language to parse games from:");
        jL_languageToParseGamesFrom.setName("jL_languageToParseGamesFrom"); // NOI18N
        jP_languageForChessGames.add(jL_languageToParseGamesFrom);
        jL_languageToParseGamesFrom.setBounds(20, 30, 270, 15);

        jCB_languageToParseGamesFrom.setMinimumSize(new java.awt.Dimension(33, 20));
        jCB_languageToParseGamesFrom.setPreferredSize(new java.awt.Dimension(33, 20));
        jCB_languageToParseGamesFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_languageToShowGames1ActionPerformed(evt);
            }
        });
        jP_languageForChessGames.add(jCB_languageToParseGamesFrom);
        jCB_languageToParseGamesFrom.setBounds(290, 30, 170, 20);
        jP_languageForChessGames.add(jTF_languageToParseGamesFromPiecesString);
        jTF_languageToParseGamesFromPiecesString.setBounds(470, 30, 80, 24);

        jL_languageToShowGames.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jL_languageToShowGames.setText("Language to show games :");
        jL_languageToShowGames.setName("jL_languageToShowGames"); // NOI18N
        jP_languageForChessGames.add(jL_languageToShowGames);
        jL_languageToShowGames.setBounds(20, 60, 270, 15);

        jCB_languageToShowGames.setMinimumSize(new java.awt.Dimension(33, 20));
        jCB_languageToShowGames.setPreferredSize(new java.awt.Dimension(33, 20));
        jCB_languageToShowGames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCB_languageToShowGames1ActionPerformed(evt);
            }
        });
        jP_languageForChessGames.add(jCB_languageToShowGames);
        jCB_languageToShowGames.setBounds(290, 60, 170, 20);

        jTF_languageToShowGamesPiecesString.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTF_languageToShowGamesPiecesString1FocusLost(evt);
            }
        });
        jP_languageForChessGames.add(jTF_languageToShowGamesPiecesString);
        jTF_languageToShowGamesPiecesString.setBounds(470, 60, 80, 24);

        jP_pdfParser.add(jP_languageForChessGames);
        jP_languageForChessGames.setBounds(10, 190, 570, 100);

        jTP_tabbedPane.addTab("PDF Parser", jP_pdfParser);

        jP_gameTagsExtractor.setName("jP_gameTagsExtractor"); // NOI18N
        jP_gameTagsExtractor.setLayout(null);

        jP_RegexFileConfiguration.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Regex files configuration"));
        jP_RegexFileConfiguration.setName("jP_RegexFileConfiguration"); // NOI18N
        jP_RegexFileConfiguration.setLayout(null);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel6.setLayout(null);
        jP_RegexFileConfiguration.add(jPanel6);
        jPanel6.setBounds(170, 30, 235, 30);

        jL_RegexFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_RegexFile.setText("Regex file :");
        jL_RegexFile.setName("jL_RegexFile"); // NOI18N
        jP_RegexFileConfiguration.add(jL_RegexFile);
        jL_RegexFile.setBounds(5, 40, 165, 16);

        jB_enableAllFiles.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_enableAllFiles.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_enableAllFiles.setName("name=jB_enableAllFiles,icon=com/frojasg1/generic/resources/enable/enable_all.png"); // NOI18N
        jB_enableAllFiles.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_enableAllFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_enableAllFilesActionPerformed(evt);
            }
        });
        jP_RegexFileConfiguration.add(jB_enableAllFiles);
        jB_enableAllFiles.setBounds(410, 35, 25, 25);

        jB_disableAllFiles.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_disableAllFiles.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_disableAllFiles.setName("name=jB_disableAllFiles,icon=com/frojasg1/generic/resources/enable/disable_all.png"); // NOI18N
        jB_disableAllFiles.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_disableAllFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_disableAllFilesActionPerformed(evt);
            }
        });
        jP_RegexFileConfiguration.add(jB_disableAllFiles);
        jB_disableAllFiles.setBounds(440, 35, 25, 25);

        jB_import.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_import.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_import.setName("name=jB_import,icon=com/frojasg1/generic/resources/othericons/import.png"); // NOI18N
        jB_import.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_import.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_importActionPerformed(evt);
            }
        });
        jP_RegexFileConfiguration.add(jB_import);
        jB_import.setBounds(500, 35, 25, 25);

        jB_export.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_export.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_export.setName("name=jB_export,icon=com/frojasg1/generic/resources/othericons/export.png"); // NOI18N
        jB_export.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_exportActionPerformed(evt);
            }
        });
        jP_RegexFileConfiguration.add(jB_export);
        jB_export.setBounds(470, 35, 25, 25);

        jB_importInitialRegexConfiguration.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_importInitialRegexConfiguration.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_importInitialRegexConfiguration.setName("name=jB_importInitialRegexConfiguration,icon=com/frojasg1/generic/resources/othericons/import.png"); // NOI18N
        jB_importInitialRegexConfiguration.setOpaque(false);
        jB_importInitialRegexConfiguration.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_importInitialRegexConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_importInitialRegexConfigurationActionPerformed(evt);
            }
        });
        jP_RegexFileConfiguration.add(jB_importInitialRegexConfiguration);
        jB_importInitialRegexConfiguration.setBounds(530, 35, 25, 25);

        jP_gameTagsExtractor.add(jP_RegexFileConfiguration);
        jP_RegexFileConfiguration.setBounds(10, 15, 560, 70);

        jP_RegexFile.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Regex file"));
        jP_RegexFile.setToolTipText("");
        jP_RegexFile.setName("jP_RegexFile"); // NOI18N
        jP_RegexFile.setLayout(null);

        jP_BlockRegexConf.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Block regex configuration"));
        jP_BlockRegexConf.setName("jP_BlockRegexConf"); // NOI18N
        jP_BlockRegexConf.setLayout(null);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(null);
        jP_BlockRegexConf.add(jPanel2);
        jPanel2.setBounds(155, 30, 295, 30);

        jL_blockRegexes.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_blockRegexes.setText("Block regexes:");
        jL_blockRegexes.setName("jL_blockRegexes"); // NOI18N
        jP_BlockRegexConf.add(jL_blockRegexes);
        jL_blockRegexes.setBounds(5, 40, 150, 16);

        jP_RegexFile.add(jP_BlockRegexConf);
        jP_BlockRegexConf.setBounds(15, 25, 535, 70);

        jP_tagRegexProfileConfiguration.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Tag regex profile configuration"));
        jP_tagRegexProfileConfiguration.setName("jP_tagRegexProfileConfiguration"); // NOI18N
        jP_tagRegexProfileConfiguration.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setLayout(null);
        jP_tagRegexProfileConfiguration.add(jPanel4);
        jPanel4.setBounds(155, 30, 245, 30);

        jL_profiles.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_profiles.setText("Profiles :");
        jL_profiles.setName("jL_profiles"); // NOI18N
        jP_tagRegexProfileConfiguration.add(jL_profiles);
        jL_profiles.setBounds(5, 40, 150, 16);

        jB_enableAll.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_enableAll.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_enableAll.setName("name=jB_enableAll,icon=com/frojasg1/generic/resources/enable/enable_all.png"); // NOI18N
        jB_enableAll.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_enableAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_enableAllActionPerformed(evt);
            }
        });
        jP_tagRegexProfileConfiguration.add(jB_enableAll);
        jB_enableAll.setBounds(425, 35, 25, 25);

        jB_enable.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_enable.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_enable.setName("name=jB_enable,icon=com/frojasg1/generic/resources/enable/enable.png"); // NOI18N
        jB_enable.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_enable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_enableActionPerformed(evt);
            }
        });
        jP_tagRegexProfileConfiguration.add(jB_enable);
        jB_enable.setBounds(485, 35, 25, 25);

        jB_disableAll.setMaximumSize(new java.awt.Dimension(25, 25));
        jB_disableAll.setMinimumSize(new java.awt.Dimension(25, 25));
        jB_disableAll.setName("name=jB_disableAll,icon=com/frojasg1/generic/resources/enable/disable_all.png"); // NOI18N
        jB_disableAll.setPreferredSize(new java.awt.Dimension(25, 25));
        jB_disableAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_disableAllActionPerformed(evt);
            }
        });
        jP_tagRegexProfileConfiguration.add(jB_disableAll);
        jB_disableAll.setBounds(455, 35, 25, 25);

        jP_RegexFile.add(jP_tagRegexProfileConfiguration);
        jP_tagRegexProfileConfiguration.setBounds(15, 125, 535, 70);

        jP_gameTagsExtractor.add(jP_RegexFile);
        jP_RegexFile.setBounds(10, 95, 560, 215);

        jTP_tabbedPane.addTab("Game tags extractor", jP_gameTagsExtractor);

        jP_chessBoardRecognizer.setName("jP_chessBoardRecognizer"); // NOI18N
        jP_chessBoardRecognizer.setLayout(null);

        jP_configurationForThreads.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Threads"));
        jP_configurationForThreads.setName("jP_configurationForThreads"); // NOI18N
        jP_configurationForThreads.setOpaque(false);
        jP_configurationForThreads.setLayout(null);

        jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition.setText("Maximum number of threads to be used for chess board recognitions");
        jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition.setName("jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition"); // NOI18N
        jP_configurationForThreads.add(jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition);
        jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition.setBounds(100, 30, 460, 20);

        jS_ocrNumberOfThreads.setMinimumSize(new java.awt.Dimension(37, 20));
        jS_ocrNumberOfThreads.setPreferredSize(new java.awt.Dimension(37, 20));
        jP_configurationForThreads.add(jS_ocrNumberOfThreads);
        jS_ocrNumberOfThreads.setBounds(30, 30, 40, 20);

        jP_chessBoardRecognizer.add(jP_configurationForThreads);
        jP_configurationForThreads.setBounds(10, 125, 565, 70);

        jCB_activateChessBoardRecognition.setText("Activate chess board position ocr recognition");
        jCB_activateChessBoardRecognition.setName("jCB_activateChessBoardRecognition"); // NOI18N
        jP_chessBoardRecognizer.add(jCB_activateChessBoardRecognition);
        jCB_activateChessBoardRecognition.setBounds(40, 10, 320, 24);

        jP_chessBoardRecognizer_reset.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Reset"));
        jP_chessBoardRecognizer_reset.setName("jP_chessBoardRecognizer_reset"); // NOI18N
        jP_chessBoardRecognizer_reset.setLayout(null);

        jB_chessBoardRecognizerReset.setText("Reset");
        jB_chessBoardRecognizerReset.setName("jB_chessBoardRecognizerReset"); // NOI18N
        jB_chessBoardRecognizerReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_chessBoardRecognizerResetActionPerformed(evt);
            }
        });
        jP_chessBoardRecognizer_reset.add(jB_chessBoardRecognizerReset);
        jB_chessBoardRecognizerReset.setBounds(30, 25, 100, 25);

        jL_chessBoardRecognizerReset1.setText("Reset recognizer. Erase all learned patterns anytime");
        jL_chessBoardRecognizerReset1.setName("jL_chessBoardRecognizerReset1"); // NOI18N
        jP_chessBoardRecognizer_reset.add(jL_chessBoardRecognizerReset1);
        jL_chessBoardRecognizerReset1.setBounds(135, 30, 420, 15);

        jB_chessBoardRecognizer_reload.setText("Reload");
        jB_chessBoardRecognizer_reload.setName("jB_chessBoardRecognizer_reload"); // NOI18N
        jB_chessBoardRecognizer_reload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_chessBoardRecognizer_reloadActionPerformed(evt);
            }
        });
        jP_chessBoardRecognizer_reset.add(jB_chessBoardRecognizer_reload);
        jB_chessBoardRecognizer_reload.setBounds(30, 65, 100, 25);

        jL_chessBoardRecognizer_reload1.setText("Reload and discard all patterns learned in current session");
        jL_chessBoardRecognizer_reload1.setName("jL_chessBoardRecognizer_reload1"); // NOI18N
        jP_chessBoardRecognizer_reset.add(jL_chessBoardRecognizer_reload1);
        jL_chessBoardRecognizer_reload1.setBounds(135, 70, 420, 16);

        jP_chessBoardRecognizer.add(jP_chessBoardRecognizer_reset);
        jP_chessBoardRecognizer_reset.setBounds(10, 200, 565, 110);

        jP_FlipBoardMode.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Flip board mode"));
        jP_FlipBoardMode.setName("jP_FlipBoardMode"); // NOI18N
        jP_FlipBoardMode.setLayout(null);

        bG_flipBoardMode.add(jRB_FlipBoardMode_Auto);
        jRB_FlipBoardMode_Auto.setSelected(true);
        jRB_FlipBoardMode_Auto.setText("Automatic");
        jRB_FlipBoardMode_Auto.setName("jRB_FlipBoardMode_Auto"); // NOI18N
        jP_FlipBoardMode.add(jRB_FlipBoardMode_Auto);
        jRB_FlipBoardMode_Auto.setBounds(25, 15, 275, 28);

        bG_flipBoardMode.add(jRB_FlipBoardMode_WhiteOnBottom1);
        jRB_FlipBoardMode_WhiteOnBottom1.setText("White player always on the bottom");
        jRB_FlipBoardMode_WhiteOnBottom1.setName("jRB_FlipBoardMode_WhiteOnBottom1"); // NOI18N
        jP_FlipBoardMode.add(jRB_FlipBoardMode_WhiteOnBottom1);
        jRB_FlipBoardMode_WhiteOnBottom1.setBounds(25, 35, 275, 28);

        bG_flipBoardMode.add(jRB_FlipBoardMode_BlackPlayerOnBottom1);
        jRB_FlipBoardMode_BlackPlayerOnBottom1.setText("Black player always on the bottom");
        jRB_FlipBoardMode_BlackPlayerOnBottom1.setName("jRB_FlipBoardMode_BlackPlayerOnBottom1"); // NOI18N
        jP_FlipBoardMode.add(jRB_FlipBoardMode_BlackPlayerOnBottom1);
        jRB_FlipBoardMode_BlackPlayerOnBottom1.setBounds(25, 55, 275, 28);

        jP_chessBoardRecognizer.add(jP_FlipBoardMode);
        jP_FlipBoardMode.setBounds(10, 40, 310, 85);

        jTP_tabbedPane.addTab("Chess Board ocr", jP_chessBoardRecognizer);

        jP_chessEngines.setName("jP_chessEngines"); // NOI18N
        jP_chessEngines.setLayout(null);

        jP_chessEngineConfiguration.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Chess engine configuration"));
        jP_chessEngineConfiguration.setName("jP_chessEngineConfiguration"); // NOI18N
        jP_chessEngineConfiguration.setLayout(null);

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.setLayout(null);
        jP_chessEngineConfiguration.add(jPanel7);
        jPanel7.setBounds(170, 35, 310, 30);

        jL_Engine.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jL_Engine.setText("Engine :");
        jL_Engine.setName("jL_Engine"); // NOI18N
        jP_chessEngineConfiguration.add(jL_Engine);
        jL_Engine.setBounds(5, 45, 165, 16);

        jP_chessEngines.add(jP_chessEngineConfiguration);
        jP_chessEngineConfiguration.setBounds(10, 100, 560, 90);

        jL_engineDownloadWebPage.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jL_engineDownloadWebPage.setForeground(new java.awt.Color(102, 153, 255));
        jL_engineDownloadWebPage.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jL_engineDownloadWebPage.setText("Visit web page to download engines");
        jL_engineDownloadWebPage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jL_engineDownloadWebPage.setName("name=jL_engineDownloadWebPage,url=http://freechess.50webs.com/engines.html"); // NOI18N
        jP_chessEngines.add(jL_engineDownloadWebPage);
        jL_engineDownloadWebPage.setBounds(10, 25, 555, 16);

        jTP_tabbedPane.addTab("Chess engines", jP_chessEngines);

        jPanel1.add(jTP_tabbedPane);
        jTP_tabbedPane.setBounds(0, 0, 590, 350);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 204)));
        jPanel5.setPreferredSize(new java.awt.Dimension(130, 50));
        jPanel5.setLayout(null);
        jPanel1.add(jPanel5);
        jPanel5.setBounds(230, 360, 130, 50);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 590, 420);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

		cancel( null );

    }//GEN-LAST:event_formWindowClosing

    private void jTF_languageToShowGamesPiecesString1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTF_languageToShowGamesPiecesString1FocusLost
        // TODO add your handling code here:

        Object obj = evt.getSource();

        if( ( _mapComboBoxes != null ) && ( obj instanceof JTextField ) )
        {
            JTextField jtf = (JTextField) obj;

            ComboBoxOfChessLanguageData data = _mapComboBoxes.get( jtf );

            if( data != null )
            {
                data = _mapComboBoxes.get( data.getLinked() );

                if( data != null )
                {
                    JTextField jtf2 = data.getLinkedTextField();

                    jtf2.setText( jtf.getText() );
                }
            }
        }
    }//GEN-LAST:event_jTF_languageToShowGamesPiecesString1FocusLost

    private void jCB_languageToShowGames1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_languageToShowGames1ActionPerformed
        // TODO add your handling code here:

        if( ! _modifiedByProgram )
        {
            Object obj = evt.getSource();

            if( obj instanceof JComboBox )
            {
                JComboBox combo = (JComboBox) obj;

                ChessLanguageConfiguration.LanguageConfigurationData lcd =
                (ChessLanguageConfiguration.LanguageConfigurationData) combo.getSelectedItem();

                boolean isLinkedCombo = false;
                _modifiedByProgram = true;
                updateNewComboSelection( combo, lcd, isLinkedCombo );
                _modifiedByProgram = false;
            }
        }
    }//GEN-LAST:event_jCB_languageToShowGames1ActionPerformed

    private void jB_enableAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_enableAllActionPerformed
        // TODO add your handling code here:

		enableAllProfiles( getWholeFileModel() );

    }//GEN-LAST:event_jB_enableAllActionPerformed

    private void jB_disableAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_disableAllActionPerformed
        // TODO add your handling code here:

		disableAllProfiles( getWholeFileModel() );

    }//GEN-LAST:event_jB_disableAllActionPerformed

    private void jB_enableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_enableActionPerformed
        // TODO add your handling code here:

		enableSelectedProfile();

    }//GEN-LAST:event_jB_enableActionPerformed

    private void jB_enableAllFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_enableAllFilesActionPerformed
        // TODO add your handling code here:

		enableAllProfiles();

    }//GEN-LAST:event_jB_enableAllFilesActionPerformed

    private void jB_disableAllFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_disableAllFilesActionPerformed
        // TODO add your handling code here:

		disableAllProfiles();

    }//GEN-LAST:event_jB_disableAllFilesActionPerformed

    private void jB_importActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_importActionPerformed
        // TODO add your handling code here:

		importRegexXml();

    }//GEN-LAST:event_jB_importActionPerformed

    private void jB_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_exportActionPerformed
        // TODO add your handling code here:

		exportCurrentRegexXml();

    }//GEN-LAST:event_jB_exportActionPerformed

    private void jB_importInitialRegexConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_importInitialRegexConfigurationActionPerformed
        // TODO add your handling code here:

		importOriginalRegexXmlFile();

    }//GEN-LAST:event_jB_importInitialRegexConfigurationActionPerformed

    private void jB_chessBoardRecognizerResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_chessBoardRecognizerResetActionPerformed
        // TODO add your handling code here:

		resetChessBoardRecognizer();

    }//GEN-LAST:event_jB_chessBoardRecognizerResetActionPerformed

    private void jB_chessBoardRecognizer_reloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_chessBoardRecognizer_reloadActionPerformed
        // TODO add your handling code here:

		reloadChessBoardRecognizer();

    }//GEN-LAST:event_jB_chessBoardRecognizer_reloadActionPerformed

    private void jCB_setOfFiguresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCB_setOfFiguresActionPerformed
        // TODO add your handling code here:

		updateFigureSample();

    }//GEN-LAST:event_jCB_setOfFiguresActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bG_flipBoardMode;
    private javax.swing.JButton jB_chessBoardRecognizerReset;
    private javax.swing.JButton jB_chessBoardRecognizer_reload;
    private javax.swing.JButton jB_disableAll;
    private javax.swing.JButton jB_disableAllFiles;
    private javax.swing.JButton jB_enable;
    private javax.swing.JButton jB_enableAll;
    private javax.swing.JButton jB_enableAllFiles;
    private javax.swing.JButton jB_export;
    private javax.swing.JButton jB_import;
    private javax.swing.JButton jB_importInitialRegexConfiguration;
    private javax.swing.JButton jB_pieceSample;
    private javax.swing.JCheckBox jCB_activateChessBoardRecognition;
    private javax.swing.JCheckBox jCB_detachedWindowsAlwaysOnTop;
    private javax.swing.JCheckBox jCB_experimentalParser;
    private javax.swing.JComboBox jCB_languageToParseGamesFrom;
    private javax.swing.JComboBox jCB_languageToShowGames;
    private javax.swing.JComboBox jCB_languageToShowGames1;
    private javax.swing.JComboBox<String> jCB_setOfFigures;
    private javax.swing.JCheckBox jCB_showComments;
    private javax.swing.JCheckBox jCB_showNAGs;
    private javax.swing.JLabel jL_Engine;
    private javax.swing.JLabel jL_RegexFile;
    private javax.swing.JLabel jL_blockRegexes;
    private javax.swing.JLabel jL_chessBoardRecognizerReset1;
    private javax.swing.JLabel jL_chessBoardRecognizer_reload1;
    private javax.swing.JLabel jL_engineDownloadWebPage;
    private javax.swing.JLabel jL_languageToParseGamesFrom;
    private javax.swing.JLabel jL_languageToShowGames;
    private javax.swing.JLabel jL_languageToShowGames1;
    private javax.swing.JLabel jL_profiles;
    private javax.swing.JLabel jL_setOfFigures;
    private javax.swing.JLabel jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition;
    private javax.swing.JPanel jP_BlockRegexConf;
    private javax.swing.JPanel jP_FlipBoardMode;
    private javax.swing.JPanel jP_RegexFile;
    private javax.swing.JPanel jP_RegexFileConfiguration;
    private javax.swing.JPanel jP_chessBoardRecognizer;
    private javax.swing.JPanel jP_chessBoardRecognizer_reset;
    private javax.swing.JPanel jP_chessEngineConfiguration;
    private javax.swing.JPanel jP_chessEngines;
    private javax.swing.JPanel jP_chessView;
    private javax.swing.JPanel jP_configurationForThreads;
    private javax.swing.JPanel jP_gameTagsExtractor;
    private javax.swing.JPanel jP_language;
    private javax.swing.JPanel jP_languageForChessGames;
    private javax.swing.JPanel jP_pdfParser;
    private javax.swing.JPanel jP_pdfParser2;
    private javax.swing.JPanel jP_tagRegexProfileConfiguration;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JRadioButton jRB_FlipBoardMode_Auto;
    private javax.swing.JRadioButton jRB_FlipBoardMode_BlackPlayerOnBottom1;
    private javax.swing.JRadioButton jRB_FlipBoardMode_WhiteOnBottom1;
    private javax.swing.JSpinner jS_ocrNumberOfThreads;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTF_languageToParseGamesFromPiecesString;
    private javax.swing.JTextField jTF_languageToShowGamesPiecesString;
    private javax.swing.JTextField jTF_languageToShowGamesPiecesString1;
    private javax.swing.JTabbedPane jTP_tabbedPane;
    // End of variables declaration//GEN-END:variables

	protected void updateFigureSample()
	{
		SwingUtilities.invokeLater( () -> {
				BufferedImage image = ExecutionFunctions.instance().safeFunctionExecution( () -> ImageFunctions.instance().loadImageFromJar( getResourceNameForFigureSampleIcon() ) );
				if( image != null )
				{
					Integer transparentColor = image.getRGB(0, 0);
					image = ImageFunctions.instance().resizeImage( image, image.getWidth(), image.getHeight(), transparentColor, null, null );
					setSampleImage( image );
				}
		});
	}

	protected void setSampleImage( BufferedImage image )
	{
		if( (image != null ) && ( jB_pieceSample instanceof ResizableImageJButton ) )
		{
			ResizableImageJButton rib = (ResizableImageJButton) jB_pieceSample;

			rib.setImage( image );
		}
	}

	protected String getResourceNameForFigureSampleIcon()
	{
		return( getSelectedFigureSet().getResourcePath() + "/"
			+ ChessBoardImages.instance().getSimpleResourceName( ChessFigure.WHITE_QUEEN ) );
	}

	protected FigureSet getSelectedFigureSet()
	{
		return( (FigureSet) jCB_setOfFigures.getSelectedItem() );
	}

	protected void resetChessBoardRecognizer()
	{
		String folderNameToErase = getApplicationContext().getChessBoardRecognizerWhole().longFolderNameForChessBoardRecognizerConfiguration();
		int answer = HighLevelDialogs.instance().yesNoCancelDialog(this,
							createCustomInternationalString( CONF_THIS_ACTION_INVOLVES_ERASING_THIS_FOLDER_COMPLETELY_DO_YOU_AGREE_TO_PROCEED,
																folderNameToErase ),
							null,
							null );

		if( answer == HighLevelDialogs.YES )		// yes
		{
			try
			{
				getApplicationContext().getChessBoardRecognizerWhole().resetErasingEverything();

				HighLevelDialogs.instance().informationMessageDialog(this,
					getInternationalString( CONF_SUCCESSFUL_RESET_OF_CHESS_BOARD_RECOGNIZER ) );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();

				HighLevelDialogs.instance().errorMessageDialog(this,
					createCustomInternationalString( CONF_ERROR_RESETING_CHESS_BOARD_RECOGNIZER,
													ex.getMessage() ) );

				ExecutionFunctions.instance().safeMethodExecution( () -> getApplicationContext().getChessBoardRecognizerWhole().init( getAppliConf() ) );
			}
		}
	}

	protected void reloadChessBoardRecognizer()
	{
		try
		{
			getApplicationContext().getChessBoardRecognizerWhole().reloadEverything();

			HighLevelDialogs.instance().informationMessageDialog(this,
				getInternationalString( CONF_SUCCESSFUL_RELOAD_OF_CHESS_BOARD_RECOGNIZER ) );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();

			HighLevelDialogs.instance().errorMessageDialog(this,
				createCustomInternationalString( CONF_ERROR_RELOADING_CHESS_BOARD_RECOGNIZER,
												ex.getMessage() ) );

			ExecutionFunctions.instance().safeMethodExecution( () -> getApplicationContext().getChessBoardRecognizerWhole().init( getAppliConf() ) );
		}
	}

	protected String getOriginalRegexXmlNameImported() throws IOException, TransformerException, ParserConfigurationException, SAXException
	{
		String result = _regexFilesPersistency.getDefaultGlobalRegexConfigurationFileName();
		canImport( result );
		_regexFilesPersistency.importOriginalXmlFile( result );

		return( result );
	}

	protected void importOriginalRegexXmlFile( )
	{
		updateImportedRegexXml(() -> ExecutionFunctions.instance().runtimeExceptionFunctionExecution(() -> getOriginalRegexXmlNameImported() ) );
	}

	protected void canImport( String xmlFileName )
	{
		String singleFileName = FileFunctions.instance().getBaseName(xmlFileName);
		singleFileName = FileFunctions.instance().addExtension(singleFileName, "xml" );

		if( _listOfRegexFiles.get( singleFileName ) != null )
			throw( new RuntimeException( createCustomInternationalString(CONF_A_FILE_WITH_THAT_NAME_ALREADY_EXISTS_CANNOT_IMPORT,
													singleFileName ) ) );
	}

	protected String getCustomRegexXmlNameImported() throws IOException, TransformerException, ParserConfigurationException, SAXException
	{
		String result = null;
		String xmlFileName = showFileChooserDialog( DialogsWrapper.OPEN, null );
		if( xmlFileName != null )
		{
			canImport( xmlFileName );
			result = _regexFilesPersistency.importXmlFile( xmlFileName ).getFileName();
		}

		return( result );
	}

	protected void importRegexXml( )
	{
		updateImportedRegexXml(() -> ExecutionFunctions.instance().runtimeExceptionFunctionExecution(() -> getCustomRegexXmlNameImported() ) );
	}

	protected void updateImportedRegexXml( Supplier<String> getImportedSingleFileName )
	{
		try
		{
			String singleFileName = getImportedSingleFileName.get();
			if( singleFileName != null )
			{
				_regexComboController.getFilesComboBoxGroupManager().getComboBoxContent().newItemSelected(singleFileName);
				_regexComboController.updateCombos();
				HighLevelDialogs.instance().informationMessageDialog(this,
						getInternationalString( CONF_XML_FILE_IMPORTED_CORRECTLY ) );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			HighLevelDialogs.instance().errorMessageDialog(this,
					createCustomInternationalString(CONF_ERROR_IMPORTING_XML_FILE,
													ex.getMessage() ) );
		}
	}

	protected RegexWholeFileModel getSelectedRegexWholeFileModel()
	{
		RegexWholeFileModel result = null;
		String fileName = _regexComboController.getSelectedFileName();
		if( fileName != null )
			result = _listOfRegexFiles.get(fileName);

		return( result );
	}

	protected String getFileNameForExport()
	{
		String result = null;
		String xmlFileName = showFileChooserDialog( DialogsWrapper.SAVE, null );

		if( FileFunctions.instance().isDirectory( xmlFileName ) )
		{
			HighLevelDialogs.instance().errorMessageDialog(this,
					createCustomInternationalString( CONF_IT_WAS_A_FOLDER_CANNOT_EXPORT, xmlFileName ) );
		}
		else if( FileFunctions.instance().isFile( xmlFileName ) )
		{
			int answer = HighLevelDialogs.instance().yesNoCancelDialog(this,
														getInternationalString( CONF_FILE_EXISTS_OVERWRITE ),
														null,
														HighLevelDialogs.NO );
			if( answer == HighLevelDialogs.YES )		// yes
				result = xmlFileName;
		}
		else
			result = xmlFileName;

		return( result );
	}

	protected void exportCurrentRegexXml()
	{
		try
		{
			String xmlFileName = getFileNameForExport();
			if( xmlFileName != null )
			{
				_regexFilesPersistency.exportXmlFile( getSelectedRegexWholeFileModel(), xmlFileName );
				HighLevelDialogs.instance().informationMessageDialog(this,
						getInternationalString( CONF_XML_FILE_EXPORTED_CORRECTLY ) );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			HighLevelDialogs.instance().errorMessageDialog(this,
					createCustomInternationalString(CONF_ERROR_EXPORTING_XML_FILE,
													ex.getMessage() ) );
		}
	}

	protected String showFileChooserDialog( int typeOfDialog, String defaultFileName )
	{
		FilterForFile fnef1 = new FilterForFile( ".xml : Extensible markup language", "xml" );

		List<FilterForFile> ffl = new ArrayList<FilterForFile>();
		ffl.add( fnef1 );

		FileChooserParameters fcp = new FileChooserParameters();
		fcp.setDefaultFileName(defaultFileName);
		fcp.setListOfFilterForFile(ffl);
//		fcp.setMode( mode );
		fcp.setOpenOrSaveDialog(typeOfDialog);

		fcp.setChosenFilterForFile(fnef1);

		String fileName = GenericFunctions.instance().getDialogsWrapper().showFileChooserDialog( this, fcp,
																									ApplicationConfiguration.instance() );

		return( fileName );
	}

	protected RegexWholeFileModel getWholeFileModel()
	{
		return( _regexComboController.getRegexWholeFileModel() );
	}

	protected void updateNewComboSelection( JComboBox combo,
							ChessLanguageConfiguration.LanguageConfigurationData lcd,
							boolean isLinkedCombo )
	{
		ComboBoxOfChessLanguageData data = getIfNotNull( _mapComboBoxes, map -> map.get( combo ) );

		if( isLinkedCombo )
		{
			combo.setSelectedItem(lcd);
		}

		if( data != null )
		{
			JTextField jtf = data.getLinkedTextField();

			if( lcd != null )
			{
				if(  jtf != null )
				{
					if( lcd == ChessLanguageConfiguration.getCustomLanguage() )
					{
						jtf.setText( "" );
						jtf.setEnabled( true );
						if( !isLinkedCombo )
							jtf.requestFocus( true );
					}
					else
					{
						jtf.setText( lcd._stringOfPieceCodes );
						jtf.setEnabled( false );
					}
				}
			}

			if( !isLinkedCombo && ( data.getLinked() != null ) )
			{
				updateNewComboSelection( data.getLinked(), lcd, true );
			}
		}
	}

	protected void M_initializeComponentContents()
	{
		_languagesConfigurationJPanel.M_initializeComponentContents();

		jCB_showComments.setSelected( getAppliConf().getHasToShowComments() );
		jCB_showNAGs.setSelected( getAppliConf().getHasToShowNAGs());
		jCB_detachedWindowsAlwaysOnTop.setSelected( getAppliConf().getDetachedGameWindowsAlwaysOnTop() );

		jCB_activateChessBoardRecognition.setSelected( getAppliConf().isChessBoardRecognizerActivated() );
		jS_ocrNumberOfThreads.setValue( getAppliConf().getNumberOfThreadsForBackgroundChessBoardRecognition() );

		jCB_experimentalParser.setSelected( getAppliConf().getUseImprovedPdfGameParser() );

		updateFlipBoardModeContents();

		fillInSetOfFiguresCombo();
	}

	protected void fillInSetOfFiguresCombo()
	{
		ComboBoxFunctions.instance().fillComboBoxGen(jCB_setOfFigures,
			getAppliConf().getChessFigurineSet(),
			FigureSet.HTML_SET, FigureSet.VIRTUAL_PIECES_SET, FigureSet.YURI_SET );

		jCB_setOfFigures.setRenderer( new ComboCellRendererBase(jCB_setOfFigures) {
			@Override
			protected String toString( Object value )
			{
				return( getInternationalString( ((FigureSet) value ).name() ) );
			}
		});
		if( isDarkMode() )
			getColorInversor().invertRendererColors(jCB_setOfFigures);

//		updateFigureSample();
	}

	protected void updateFlipBoardModeContents()
	{
		FlipBoardMode fbm = getAppliConf().getChessBoardRecognizedFlipBoardMode();
		updateFlipBoardModeContents(fbm);
	}

	protected void updateFlipBoardModeContents(FlipBoardMode fbm)
	{
		if( _flipBoardModeRadioButtonArray != null )
		{
			for( int ii=0; ii<_flipBoardModeRadioButtonArray.length; ii++ )
			{
				Pair<JRadioButton, FlipBoardMode> elem = _flipBoardModeRadioButtonArray[ii];
				if( elem.getValue().equals( fbm ) )
				{
					elem.getKey().setSelected(true);
					break;
				}
			}
		}
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
				ComboBoxOfChessLanguageData data = _mapComboBoxes.get( combo );

				if( data != null )
				{
					JTextField jtf = data.getLinkedTextField();

					if(  jtf != null )
					{
						result = jtf.getText();
					}
				}
			}
			else
				result = lcd._languageName;
		}

		return( result );
	}

	protected String getSelectedLanguage()
	{
		return( _languagesConfigurationJPanel.getSelectedLanguage() );
	}

	protected void applyChanges()
	{
		String language = getSelectedLanguage();

		try
		{
			if( language != null )
				getAppliConf().changeLanguage(language);
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

/*
		float factor = 0.0F;
		if( jCb_fontSize.getSelectedIndex() == 0 )	factor = SA_FACTOR_NORMAL_FONT_SIZE;
		else if( jCb_fontSize.getSelectedIndex() == 1 )	factor = SA_FACTOR_LARGE_FONT_SIZE;

		if( factor > 0.0F )
		{
			appConf.M_setStrParamConfiguration( ApplicationConfiguration.CONF_APPLICATION_FONT_SIZE, Float.toString(factor) );
		}
*/

		String chessLanguageToParseFrom = getChessLanguageForConfiguration( jCB_languageToParseGamesFrom );
		String chessLanguageToShow = getChessLanguageForConfiguration( jCB_languageToShowGames );

		if( chessLanguageToParseFrom != null )
			getAppliConf().setConfigurationOfChessLanguageToParseTextFrom( chessLanguageToParseFrom );

		if( chessLanguageToShow != null )
			getAppliConf().setConfigurationOfChessLanguageToShow( chessLanguageToShow );

		getAppliConf().setHasToShowComments( jCB_showComments.isSelected() );
		getAppliConf().setHasToShowNAGs( jCB_showNAGs.isSelected() );
		getAppliConf().setDetachedGameWindowsAlwaysOnTop( jCB_detachedWindowsAlwaysOnTop.isSelected() );

		getAppliConf().setUseImprovedPdfGameParser( jCB_experimentalParser.isSelected() );

		getAppliConf().setNumberOfThreadsForBackgroundChessBoardRecognition( ( Integer ) jS_ocrNumberOfThreads.getValue() );
		getAppliConf().setIsChessBoardRecognizerActivated(jCB_activateChessBoardRecognition.isSelected() );

		getAppliConf().setChessBoardRecognizedFlipBoardMode( getSelectedFlipBoardMode() );

		getAppliConf().setChessFigurineSet( getSelectedFigureSet() );
		try
		{
			GenericFunctions.instance().getObtainAvailableLanguages().updateLocaleLanguagesToDisk();

//			if( _mainWindowParent != null )
//				_mainWindowParent.updateRadioButtonMenus();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		saveRegexes();
		saveEngines();
	}

	protected void saveEngines()
	{
		Exception ex = null;
		try
		{
			getChessEngineConfigurationPersistency().setPreviousXmlModelContainer(_lastChessEngineConfigurationMap);
			getChessEngineConfigurationPersistency().setCurrentModelContainer(_chessEngineConfigurationMap);
			getChessEngineConfigurationPersistency().save();
//			ex = _wholeRegexModel.save();
		}
		catch( Exception ex2 )
		{
			ex = ex2;
			ex2.printStackTrace();
		}

		if( ex != null )
		{
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, ex.getMessage(),
											getAppStrConf().getProperty( AppStringsConf.CONF_ERROR ),
											DialogsWrapper.ERROR_MESSAGE );
		}
	}

	protected void saveRegexes()
	{
		Exception ex = null;
		try
		{
			_regexFilesPersistency.setPreviousXmlModelContainer(_lastListOfRegexFiles);
			_regexFilesPersistency.setCurrentModelContainer(_listOfRegexFiles);
			_regexFilesPersistency.save();
//			ex = _wholeRegexModel.save();
		}
		catch( Exception ex2 )
		{
			ex = ex2;
			ex2.printStackTrace();
		}

		if( ex != null )
		{
			GenericFunctions.instance().getDialogsWrapper().showMessageDialog( this, ex.getMessage(),
											getAppStrConf().getProperty( AppStringsConf.CONF_ERROR ),
											DialogsWrapper.ERROR_MESSAGE );
		}
	}

	protected void M_copyLanguageConfigurationFileFromJarToAdditionalLanguageFolder( 
							String originLanguage,
							String destinationLanguage,
							String fileName )
	{
		String longFileNameInJar = ApplicationConfiguration.sa_PROPERTIES_PATH_IN_JAR + "/" + originLanguage + "/" + fileName;
		String longFileNameInDisk = getAppliConf().getDefaultLanguageConfigurationFolder( destinationLanguage ) +
									ConfigurationParent.sa_dirSeparator + fileName;

//		M_copyPropertiesFileFromJarToDisk( longFileNameInJar, longFileNameInDisk );

		ResourceFunctions.instance().copyBinaryResourceToFile(longFileNameInJar, longFileNameInDisk );
	}

	protected void M_saveInternationalization()
	{
		try
		{
			if( a_intern != null ) a_intern.saveConfiguration();
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	public void validateChessLanguageComboBoxChanges( JComboBox combo ) throws ValidationException
	{
		if( combo.getSelectedItem() != null )
		{
			ChessLanguageConfiguration.LanguageConfigurationData lcd =
				(ChessLanguageConfiguration.LanguageConfigurationData) combo.getSelectedItem();

			if( lcd == ChessLanguageConfiguration.getCustomLanguage() )
			{
				ComboBoxOfChessLanguageData data = _mapComboBoxes.get( combo );

				if( data != null )
				{
					JTextField jtf = data.getLinkedTextField();

					if(  jtf != null )
					{
						ChessLanguageConfiguration clc = ChessLanguageConfiguration.getConfiguration( jtf.getText() );
						
						if( clc == null )
						{
							try
							{
								// if the data to create a new language configuration is not valid, it will throw an exception.
								ChessLanguageConfiguration.createConfiguration(null, jtf.getText());
							}
							catch( Throwable th )
							{
								th.printStackTrace();
								throw( new ValidationException( th.getMessage(), jtf ) );
							}
						}
					}
				}
			}
		}
	}

	protected void validateRegexes() throws ValidationException
	{
		for( RegexWholeFileModel wholeFileModel: _listOfRegexFiles.getCollectionOfModelItems() )
			validateRegexes( wholeFileModel );
	}

	protected void validateRegexes( RegexWholeFileModel wholeFileModel ) throws ValidationException
	{
		wholeFileModel.invalidateCaches();

		validateBlocks( wholeFileModel.getBlockConfigurationContainer() );
		validateProfiles( wholeFileModel.getSetOfProfiles() );
	}

	protected void validateProfiles( Collection<ProfileModel> profCol ) throws ValidationException
	{
		for( ProfileModel profile: profCol )
			validateProfile( profile );
	}

	protected void validateProfile( ProfileModel profile ) throws ValidationException
	{
		LineModel exceptionLineModel = null;
		try
		{
			for( LineModel lm: profile.getListOfLines() )
			{
				exceptionLineModel = lm;
				validateLineModel( lm );
			}
		}
		catch( Exception ex )
		{
			boolean validateAtOnce = true;
			ProfileModel profileModel = exceptionLineModel.getParent();
			SwingUtilities.invokeLater( () -> _regexComboController.modifyProfile( profileModel, validateAtOnce, null ) );
			ex.printStackTrace();
			throw( new ValidationException( ex.getMessage(), null, ex, true ) );
		}
	}

	protected void validateLineModel( LineModel lm )
	{
		lm.validate();
	}

	protected void validateBlocks( BlockRegexConfigurationContainer brcc ) throws ValidationException
	{
		for( String blockName: brcc.getComboBoxContent().getListOfItems() )
		{
			RegexOfBlockModel bm  = brcc.get( blockName );

			if( bm != null )
				validateBlockModel( brcc.getParent(), bm );
		}
	}

	protected Pattern getRegexPattern( RegexWholeFileModel rwfm, String expression )
	{
		return( rwfm.getBlockRegexBuilder().getRegexPattern( expression ) );
	}

	protected void validateBlockModel( RegexWholeFileModel rwfm, RegexOfBlockModel bm ) throws ValidationException
	{
		try
		{
			getRegexPattern( rwfm, bm.getExpression() );
		}
		catch( Exception ex )
		{
			boolean validateAtOnce = true;
			SwingUtilities.invokeLater( () -> _regexComboController.modifyBlock( rwfm.getFileName(), bm.getName(), validateAtOnce, null ) );
			ex.printStackTrace();
			throw( new ValidationException( ex.getMessage(), null, ex, true ) );
		}
	}

	protected void validateIntFieldGreaterThanZero( Component comp, Integer value ) throws ValidationException
	{
		if( value == null )
			throw( new ValidationException( getInternationalString( CONF_NOT_A_VALID_INT ), comp ) );
		else if( value < 1 )
			throw( new ValidationException( getInternationalString( CONF_VALUE_MUST_BE_GREATER_OR_EQUAL_THAN_ONE ), comp ) );
	}

//	public void validateChanges() throws ValidateException
	public void validateFormChild() throws ValidationException
	{
		validateChessLanguageComboBoxChanges( jCB_languageToParseGamesFrom );
		validateChessLanguageComboBoxChanges( jCB_languageToShowGames );
		validateIntFieldGreaterThanZero( jS_ocrNumberOfThreads, (Integer) jS_ocrNumberOfThreads.getValue() );

		validateRegexes();
	}

	@Override
	public void accept(InformerInterface panel)
	{
		boolean proceed = false;
/*
		try
		{
			validateChanges();
			proceed = true;
		}
		catch( ValidateException ve )
		{
			if( ! ve.getDoNotShowWarning() )
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
		proceed = wasSuccessful();
			

		if( proceed )
		{
			applyChanges();

			try
			{
				ApplicationConfiguration.instance().M_saveConfiguration();
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
/*
			if( M_hasToCreateAdditionalLanguageFolder() )
			{
				M_createFolderAndCopyLanguageConfigurationFilesFromEnglish();
			}
*/
			M_saveInternationalization();

			SwingUtilities.invokeLater(
				() -> { if( _mainWindowParent != null )
							_mainWindowParent.applyConfigurationChanges(); }
										);

			a_userHasPressedOK = true;
			setVisible(false);
		}
	}

	@Override
	public void cancel(InformerInterface panel)
	{
		ExecutionFunctions.instance().safeMethodExecution( () -> revert( null ) );

		// we load again the Strings configuration with the language which with was called the dialogue
		try
		{
			AppStringsConf.instance().changeLanguage( a_languageInConstructor );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		try
		{
			ChessStringsConf.instance().changeLanguage( a_languageInConstructor );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		M_saveInternationalization();
        setVisible(false);
	}

	protected void revertRegexes()
	{
		// revert regexes
		_copier.copy(_listOfRegexFiles, _lastListOfRegexFiles );

//		_masterComboForFiles.setComboBoxContent(_listOfRegexFiles.getComboBoxContent());
		_regexComboController.updateCombos();
	}

	protected void revertEngines()
	{
		// revert regexes
		_copier.copy(_chessEngineConfigurationMap, _lastChessEngineConfigurationMap );

//		_masterComboForEngines.setComboBoxContent( _chessEngineConfigurationMap.getComboBoxContent() );
		getEngineComboController().updateCombos();
	}

	@Override
	public void revert(InformerInterface panel)
	{
		try
		{
			if( a_intern != null )
				a_intern.changeLanguage( ApplicationConfiguration.instance().getLanguage() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		revertRegexes();
		revertEngines();

		M_initializeComponentContents();

		boolean initialConfiguration = true;
		fillInComboBoxesOfChessLanguage( initialConfiguration );
	}

	public boolean getUserHasPressedOK()
	{
		return( a_userHasPressedOK );
	}

	public void releaseResources()
	{
		super.releaseResources();
//		a_intern=null;	// for the garbage collector to free the memory of the internationallization object and after the memory of this form
	}

	protected void fillInComboBoxesOfChessLanguage( boolean initialConfiguration )
	{
		if( _mapComboBoxes == null )
		{
			_mapComboBoxes = new HashMap<Component, ComboBoxOfChessLanguageData>();
			_mapComboBoxes.put( jCB_languageToParseGamesFrom,
							new ComboBoxOfChessLanguageData( jCB_languageToParseGamesFrom,
															null,
															jTF_languageToParseGamesFromPiecesString ) );
			_mapComboBoxes.put( jCB_languageToShowGames,
							new ComboBoxOfChessLanguageData( jCB_languageToShowGames,
															jCB_languageToShowGames1,
															jTF_languageToShowGamesPiecesString ) );
			_mapComboBoxes.put( jCB_languageToShowGames1,
							new ComboBoxOfChessLanguageData( jCB_languageToShowGames1,
															jCB_languageToShowGames,
															jTF_languageToShowGamesPiecesString1 ) );
			_mapComboBoxes.put( jTF_languageToShowGamesPiecesString,
							new ComboBoxOfChessLanguageData( jCB_languageToShowGames,
															jCB_languageToShowGames1,
															jTF_languageToShowGamesPiecesString ) );
			_mapComboBoxes.put( jTF_languageToShowGamesPiecesString1,
							new ComboBoxOfChessLanguageData( jCB_languageToShowGames1,
															jCB_languageToShowGames,
															jTF_languageToShowGamesPiecesString1 ) );
		}

		Locale locale = getLocale( initialConfiguration ?
									getAppliConf().getLanguage() :
									getSelectedLanguage() );

		Vector< ChessLanguageConfiguration.LanguageConfigurationData > vector =
			ChessLanguageConfiguration.getListOfLanguages( locale );

		fillInOneComboBoxOfChessLanguage( jCB_languageToShowGames, vector,
											( initialConfiguration ?
												getAppliConf().getConfigurationOfChessLanguageToShow() :
												null ) );
		fillInOneComboBoxOfChessLanguage( jCB_languageToShowGames1, vector,
											( initialConfiguration ?
												getAppliConf().getConfigurationOfChessLanguageToShow() :
												null ) );

		Vector< ChessLanguageConfiguration.LanguageConfigurationData > vector1 = ChessLanguageConfiguration.getListOfLanguagesToParseFrom( locale );

		fillInOneComboBoxOfChessLanguage( jCB_languageToParseGamesFrom, vector1,
											( initialConfiguration ?
												getAppliConf().getConfigurationOfChessLanguageToParseTextFrom() :
												null ) );
	}

	protected Locale getLocale( String language )
	{
		Locale result = null;
		if( language != null )
			result = GenericFunctions.instance().getObtainAvailableLanguages().getLocaleOfLanguage(language);

		return( result );
	}

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

	@Override
	public void localChangeLanguage( String language )
	{
//		changeLanguageOfLocaleComboBoxes( language );
/*
		try
		{
			StringsConfiguration.instance().changeLanguage( language );
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}

		// after loading the strings of the new language we have to refresh the table.
		M_refreshJTable();
*/
		try
		{
			if( a_intern != null )
			{
				a_intern.changeLanguage( language );
			}
		}
		catch( InternException ie )
		{
			ie.printStackTrace();
		}
	}

	@Override
	public void addLanguageActionPerformed( String language )
	{
        if( getParent() instanceof MainWindow )
        {
            MainWindow mw = (MainWindow) getParent();
            mw.addLanguageToMenu(language);
        }
	}

	@Override
	protected void translateMappedComponents(ComponentMapper compMapper)
	{
		jB_disableAll = compMapper.mapComponent( jB_disableAll );
		jB_disableAllFiles = compMapper.mapComponent( jB_disableAllFiles );
		jB_enable = compMapper.mapComponent( jB_enable );
		jB_enableAll = compMapper.mapComponent( jB_enableAll );
		jB_enableAllFiles = compMapper.mapComponent( jB_enableAllFiles );
		jB_export = compMapper.mapComponent( jB_export );
		jB_import = compMapper.mapComponent( jB_import );
		jB_importInitialRegexConfiguration = compMapper.mapComponent( jB_importInitialRegexConfiguration );
		jCB_activateChessBoardRecognition = compMapper.mapComponent(jCB_activateChessBoardRecognition );
		jCB_detachedWindowsAlwaysOnTop = compMapper.mapComponent( jCB_detachedWindowsAlwaysOnTop );
		jCB_experimentalParser = compMapper.mapComponent( jCB_experimentalParser );
		jCB_languageToParseGamesFrom = compMapper.mapComponent( jCB_languageToParseGamesFrom );
		jCB_languageToShowGames = compMapper.mapComponent( jCB_languageToShowGames );
		jCB_languageToShowGames1 = compMapper.mapComponent( jCB_languageToShowGames1 );
		jCB_showComments = compMapper.mapComponent( jCB_showComments );
		jCB_showNAGs = compMapper.mapComponent( jCB_showNAGs );
		jL_RegexFile = compMapper.mapComponent( jL_RegexFile );
		jL_blockRegexes = compMapper.mapComponent( jL_blockRegexes );
		jL_languageToParseGamesFrom = compMapper.mapComponent( jL_languageToParseGamesFrom );
		jL_languageToShowGames = compMapper.mapComponent( jL_languageToShowGames );
		jL_languageToShowGames1 = compMapper.mapComponent( jL_languageToShowGames1 );
		jL_profiles = compMapper.mapComponent( jL_profiles );
		jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition = compMapper.mapComponent(jLbl_maxNumberOfThreadsToBeUsedForChessBoardRecognition );
		jP_BlockRegexConf = compMapper.mapComponent( jP_BlockRegexConf );
		jP_RegexFile = compMapper.mapComponent( jP_RegexFile );
		jP_RegexFileConfiguration = compMapper.mapComponent( jP_RegexFileConfiguration );
		jP_chessBoardRecognizer = compMapper.mapComponent(jP_chessBoardRecognizer );
		jP_chessView = compMapper.mapComponent( jP_chessView );
		jP_configurationForThreads = compMapper.mapComponent( jP_configurationForThreads );
		jP_gameTagsExtractor = compMapper.mapComponent( jP_gameTagsExtractor );
		jP_language = compMapper.mapComponent( jP_language );
		jP_languageForChessGames = compMapper.mapComponent( jP_languageForChessGames );
		jP_pdfParser = compMapper.mapComponent( jP_pdfParser );
		jP_pdfParser2 = compMapper.mapComponent( jP_pdfParser2 );
		jP_tagRegexProfileConfiguration = compMapper.mapComponent( jP_tagRegexProfileConfiguration );
		jPanel1 = compMapper.mapComponent( jPanel1 );
		jPanel2 = compMapper.mapComponent( jPanel2 );
		jPanel4 = compMapper.mapComponent( jPanel4 );
		jPanel5 = compMapper.mapComponent( jPanel5 );
		jPanel6 = compMapper.mapComponent( jPanel6 );
		jS_ocrNumberOfThreads = compMapper.mapComponent( jS_ocrNumberOfThreads );
		jSeparator2 = compMapper.mapComponent( jSeparator2 );
		jTF_languageToParseGamesFromPiecesString = compMapper.mapComponent( jTF_languageToParseGamesFromPiecesString );
		jTF_languageToShowGamesPiecesString = compMapper.mapComponent( jTF_languageToShowGamesPiecesString );
		jTF_languageToShowGamesPiecesString1 = compMapper.mapComponent( jTF_languageToShowGamesPiecesString1 );
		jTP_tabbedPane = compMapper.mapComponent( jTP_tabbedPane );

		jL_setOfFigures = compMapper.mapComponent( jL_setOfFigures );
		jB_pieceSample = compMapper.mapComponent( jB_pieceSample );
		jCB_setOfFigures = compMapper.mapComponent( jCB_setOfFigures );

//		_languagesConfigurationJPanel.translateMappedComponents( compMapper );
//		_masterComboForBlocks = compMapper.mapComponent(_masterComboForBlocks);
//		_masterComboForProfiles = compMapper.mapComponent(_masterComboForProfiles);
//		_masterComboForTags = compMapper.mapComponent(_masterComboForTags);

		jP_chessEngineConfiguration = compMapper.mapComponent( jP_chessEngineConfiguration );
		jP_chessEngines = compMapper.mapComponent( jP_chessEngines );
		jL_Engine = compMapper.mapComponent( jL_Engine );
		jPanel7 = compMapper.mapComponent( jPanel7 );
		_masterComboForFiles = compMapper.mapComponent( _masterComboForFiles );
		_masterComboForBlocks = compMapper.mapComponent( _masterComboForBlocks );
		_masterComboForProfiles = compMapper.mapComponent( _masterComboForProfiles );
//		_masterComboForEngines = compMapper.mapComponent( _masterComboForEngines );
			
		_regexComboController.setComponentMapper(compMapper);
		_regexComboController.updateCombos();

		getEngineComboController().setComponentMapper(compMapper);
		getEngineComboController().updateCombos();

//		boolean forceExecution = true;
//		getInternationalization().executeResizeRelocate(_masterComboForEngines, forceExecution);
	}

	protected void selectFileOfProfile( ProfileModel profileModel )
	{
		if( profileModel != null )
		{
			String fileName = profileModel.getParent().getFileName();
			_regexComboController.getFilesComboBoxGroupManager().getComboBoxContent().newItemSelected( fileName );
		}
	}

	protected void openProfile( ProfileModel profileModel )
	{
		SwingUtilities.invokeLater(() -> {
			selectFileOfProfile( profileModel );

			SwingUtilities.invokeLater(() -> {
				String panelName = "jP_gameTagsExtractor";
				ComponentFunctions.instance().selectJTabbedPaneIndex( jTP_tabbedPane, panelName );

//				jTP_tabbedPane.setSelectedIndex(TAG_REGEX_CONFIGURATION_TAB_INDEX );
				boolean validateAtOnce = false;
				if( profileModel != null )
					_regexComboController.modifyProfile( profileModel.getProfileName(), validateAtOnce, null );
				});
		});
	}

	protected static class ComboBoxOfChessLanguageData
	{
		protected JComboBox _own = null;
		protected JComboBox _linked = null;
		protected JTextField _linkedTextField = null;

		public ComboBoxOfChessLanguageData( JComboBox own, JComboBox linked, JTextField linkedTextField )
		{
			_own = own;
			_linked = linked;
			_linkedTextField = linkedTextField;
		}
		
		public JComboBox getOwn()
		{
			return( _own );
		}
		
		public JComboBox getLinked()
		{
			return( _linked );
		}
		
		public JTextField getLinkedTextField()
		{
			return( _linkedTextField );
		}
	}
	
	protected AppStringsConf getAppStrConf()
	{
		return( AppStringsConf.instance() );
	}

	@Override
	public ApplicationConfiguration getAppliConf()
	{
		return( (ApplicationConfiguration) super.getAppliConf() );
	}
/*
	protected boolean isProfileActivated( String profileName )
	{
		boolean result = false;
		ProfileModel profileModel = getWholeFileModel().get( profileName );

		if( profileModel != null )
			result = profileModel.isActive();

		return( result );
	}
*/
	protected void setAllProfilesEnabled( RegexWholeFileModel rwfm, boolean value )
	{
		rwfm.getSetOfProfiles().forEach( (pm) -> pm.setActive( value ) );
	}

	protected void enableAllProfiles( RegexWholeFileModel rwfm )
	{
		setAllProfilesEnabled( rwfm, true );
	}

	protected void disableAllProfiles( RegexWholeFileModel rwfm )
	{
		setAllProfilesEnabled( rwfm, false );
	}

	protected void setAllProfilesEnabled( boolean value )
	{
		for( RegexWholeFileModel rwfm: _listOfRegexFiles.getCollectionOfModelItems() )
			setAllProfilesEnabled( rwfm, value );
	}

	protected void enableAllProfiles(  )
	{
		setAllProfilesEnabled( true );
	}

	protected void disableAllProfiles(  )
	{
		setAllProfilesEnabled( false );
	}

	protected void enableSelectedProfile()
	{
		ProfileModel pm = getSelectedProfile();
		if( pm != null )
			pm.setActive(true);
	}

	protected ProfileModel getSelectedProfile()
	{
		ProfileModel result = null;
		String profileName = (String) _masterComboForProfiles.getCombo().getSelectedItem();
		RegexWholeFileModel rwfm = getWholeFileModel();
		if( rwfm != null )
			result = rwfm.get(profileName);

		return( result );
	}
}
