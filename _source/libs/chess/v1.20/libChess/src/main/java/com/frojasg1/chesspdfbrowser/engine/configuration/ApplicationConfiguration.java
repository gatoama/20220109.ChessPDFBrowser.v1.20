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
package com.frojasg1.chesspdfbrowser.engine.configuration;

import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSet;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedListener;
import com.frojasg1.chesspdfbrowser.engine.configuration.figureset.FigureSetChangedObserved;
import com.frojasg1.chesspdfbrowser.engine.io.notation.ChessMoveNotation;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.movematcher.ChessMoveMatcher;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.EnglishChessConfiguration;
import com.frojasg1.chesspdfbrowser.view.chess.completion.CompletionConfiguration;
import com.frojasg1.chesspdfbrowser.view.chess.editcomment.EditCommentConfiguration;
import com.frojasg1.chesspdfbrowser.view.chess.edittags.FilterTAGconfiguration;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.generic.GenericFunctions;
import java.io.File;
import java.util.Properties;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.ChessRecognizerApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.FlipBoardMode;
import com.frojasg1.chesspdfbrowser.view.chess.analysis.ChessAnalysisFrameConfiguration;
import com.frojasg1.general.listeners.ListOfListenersDefaultNotifierImp;

/**
 *
 * @author Usuario
 */
public class ApplicationConfiguration	extends BaseApplicationConfiguration
										implements ChessViewConfiguration, EditCommentConfiguration,
													FilterTAGconfiguration, ParserConfiguration,
													TagExtractorConfiguration, CompletionConfiguration,
													ChessRecognizerApplicationConfiguration,
													ChessAnalysisFrameConfiguration,
													FigureSetChangedObserved
{
	ChessMoveNotation _chessMoveNotationForView = null;
	ChessMoveMatcher _chessMoveMatcherForView = null;

	public static final String VERSION="v1.20";

	protected static final String RESOURCE_FOR_APPLICATION_ICON="com/frojasg1/chesspdfbrowser/resources/icons/App.icon.png";

	// FIGURE_SETS
//	public static final String HTML_SET = "HTML_SET";
//	public static final String YURI_SET = "YURI_SET";
//	public static final String VIRTUAL_PIECES_SET = "VIRTUAL_PIECES_SET";
	

	public static final String CONF_LAST_DIRECTORY = "LAST_DIRECTORY";

//	protected static final String APPLICATION_GROUP = "general";
	public static final String GLOBAL_CONF_FILE_NAME = "GlobalConfiguration.properties";
/*
	protected static final String sa_PROPERTIES_PATH_IN_JAR =	"com" + ConfigurationParent.sa_dirSeparator + 
																"hotmail" + ConfigurationParent.sa_dirSeparator +
																"frojasg1" + ConfigurationParent.sa_dirSeparator + 
																"aplicaciones" + ConfigurationParent.sa_dirSeparator +
																"fileencoderapplication" + ConfigurationParent.sa_dirSeparator +
																"view" + ConfigurationParent.sa_dirSeparator +
																"internationalization" + ConfigurationParent.sa_dirSeparator +
																"properties";
*/
//	public static final String sa_PROPERTIES_PATH_IN_JAR="com/hotmail/frojasg1/applications/fileencoderapplication/view/internationalization/properties";
	public static final String sa_MAIN_FOLDER = ".frojasg1.apps";
	public static String sa_APPLICATION_NAME = null; //"ChessPDFBrowser";
	public static final String sa_CONFIGURATION_GROUP = "Configuration";
//	public static final String sa_PROPERTIES_PATH_IN_JAR="com/frojasg1/applications/chesspdfbrowser/internationalization/properties";
	public static final String sa_PROPERTIES_PATH_IN_JAR="com/frojasg1/app/chesspdfbrowser/inter";

	public static final String ES_LANGUAGE = "ES";	 // Spanish language
	public static final String EN_LANGUAGE = "EN";	 // English language

	public static final String CONF_LANGUAGE = "LANGUAGE";
	public static final String CONF_ADDITIONAL_LANGUAGE = "ADDITIONAL_LANGUAGE";
	public static final String CONF_HAS_TO_SHOW_COMMENTS = "HAS_TO_SHOW_COMMENTS";
	public static final String CONF_HAS_TO_SHOW_SEGMENTS = "HAS_TO_SHOW_SEGMENTS";
	public static final String CONF_EDIT_COMMENT_WINDOW_ALWAYS_ON_TOP = "EDIT_COMMENT_WINDOW_ALWAYS_ON_TOP";

	public static final String CONF_EDIT_TAG_MANDATORY_FILTER_SELECTED = "EDIT_TAG_MANDATORY_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_PLAYER_FILTER_SELECTED = "EDIT_TAG_PLAYER_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_EVENT_FILTER_SELECTED = "EDIT_TAG_EVENT_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_OPENING_FILTER_SELECTED = "EDIT_TAG_OPENING_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_TIME_AND_DATE_FILTER_SELECTED = "EDIT_TAG_TIME_AND_DATE_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_TIME_CONTROL_FILTER_SELECTED = "EDIT_TAG_TIME_CONTROL_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_GAME_CONCLUSION_FILTER_SELECTED = "EDIT_TAG_GAME_CONCLUSION_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_MISCELLANEOUS_FILTER_SELECTED = "EDIT_TAG_MISCELLANEOUS_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_ALTERNATIVE_STARTING_POSITION_FILTER_SELECTED = "EDIT_TAG_ALTERNATIVE_STARTING_POSITION_FILTER_SELECTED";
	public static final String CONF_EDIT_TAG_WINDOW_ALWAYS_ON_TOP = "EDIT_TAG_WINDOW_ALWAYS_ON_TOP";
	public static final String CONF_EDIT_TAG_FILTER_ITEM_FOR_TAG = "EDIT_TAG_FILTER_ITEM_FOR_TAG";

	public static final String CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_SHOW_GAMES = "STRING_TO_CONFIGURE_LANGUAGE_TO_SHOW_GAMES";
	public static final String CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_PARSE_TEXT_OF_GAMES_FROM = "STRING_TO_CONFIGURE_LANGUAGE_TO_PARSE_TEXT_OF_GAMES_FROM";

	public static final String CONF_SHOW_NAGS = "SHOW_NAGS";
	public static final String CONF_DETACHED_GAME_WINDOWS_ALWAYS_ON_TOP = "DETACHED_GAME_WINDOWS_ALWAYS_ON_TOP";

	public static final String CONF_LICENSES_HAVE_BEEN_ACCEPTED = "LICENSES_HAVE_BEEN_ACCEPTED";

	public static final String CONF_USE_IMPROVED_PDF_GAME_PARSER = "USE_IMPROVED_PDF_GAME_PARSER";

//	public static final String CONF_HOME_WEB_PAGE_URL = "HOME_WEB_PAGE_URL";
//	public static final String CONF_AUTHOR_EMAIL_ADDRESS = "AUTHOR_EMAIL_ADDRESS";

	public static final String CONF_WHITE_PLAYER_NAME_EXTRACTION_REGEX = "WHITE_PLAYER_NAME_EXTRACTION_REGEX";
	public static final String CONF_BLACK_PLAYER_NAME_EXTRACTION_REGEX = "BLACK_PLAYER_NAME_EXTRACTION_REGEX";
	public static final String CONF_WHITE_PLAYER_ELO_EXTRACTION_REGEX = "WHITE_PLAYER_ELO_EXTRACTION_REGEX";
	public static final String CONF_BLACK_PLAYER_ELO_EXTRACTION_REGEX = "BLACK_PLAYER_ELO_EXTRACTION_REGEX";
	public static final String CONF_VARIANT_EXTRACTION_REGEX = "VARIANT_EXTRACTION_REGEX";
	public static final String CONF_EVENT_EXTRATION_REGEX = "EVENT_EXTRATION_REGEX";
	public static final String CONF_SITE_EXTRACTION_REGEX = "SITE_EXTRACTION_REGEX";
	public static final String CONF_ROUND_EXTRACTION_REGEX = "ROUND_EXTRACTION_REGEX";
	public static final String CONF_DATE_EXTRACTION_REGEX = "DATE_EXTRACTION_REGEX";

	public static final String CONF_ACTIVATE_AUTOCOMPLETION_FOR_REGEX = "ACTIVATE_AUTOCOMPLETION_FOR_REGEX";

	public static final String CONF_NUMBER_OF_THREADS_FOR_BACKGROUND_OCR_RECOGNITION = "NUMBER_OF_THREADS_FOR_BACKGROUND_OCR_RECOGNITION";
	public static final String CONF_IS_CHESS_BOARD_RECOGNIZER_ACTIVATED = "IS_CHESS_BOARD_RECOGNIZER_ACTIVATED";

	public static final String CONF_CHESS_FIGURINE_SET = "CHESS_FIGURINE_SET";

	public static final String CONF_ANALYSIS_FRAME_ALWAYS_ON_TOP = "ANALYSIS_FRAME_ALWAYS_ON_TOP";

	public static final String CONF_CHESS_BOARD_RECOGNIZER_FLIP_BOARD_MODE = "CHESS_BOARD_RECOGNIZER_FLIP_BOARD_MODE";

/*
	public static final String CONF_WHITE_PLAYER_NAME_EXTRACTION_REGEX_DEFAULT = "^([^(]*)\\s*(\\([0-9]+\\))?\\s*-\\s*[^(]*\\s*(\\([0-9]+\\))?\\s*\\[[A-E][0-9]{2}\\]\\s*$";
	public static final String CONF_BLACK_PLAYER_NAME_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*([^(]*)\\s*(\\([0-9]+\\))?\\s*\\[[A-E][0-9]{2}\\]\\s*$|" +
																					"^[^(]*\\s*-\\s*([^(]*)\\s*(\\([0-9]+\\))?\\s*\\[[A-E][0-9]{2}\\]\\s*$";
	public static final String CONF_WHITE_PLAYER_ELO_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\(([0-9]+)\\)\\s*-\\s*[^(]*\\s*(\\([0-9]+\\))?\\s*\\[[A-E][0-9]{2}\\]\\s*$";
	public static final String CONF_BLACK_PLAYER_ELO_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\(([0-9]+)\\)\\s*\\[[A-E][0-9]{2}\\]\\s*$|"+
																				"^[^(]*\\s*-\\s*[^(]*\\s*\\(([0-9]+)\\)\\s*\\[[A-E][0-9]{2}\\]\\s*$";
	public static final String CONF_VARIANT_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\([0-9]+\\)\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^[^(]*\\s*-\\s*[^(]*\\s*\\([0-9]+\\)\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^[^(]*\\s*-\\s*[^(]*\\s*\\[([A-E][0-9]{2})\\]\\s*$";^;
*/
	public static final String CONF_WHITE_PLAYER_NAME_EXTRACTION_REGEX_DEFAULT = "^([^(]*)\\s*(\\([0-9]+\\))?\\s*-\\s*[^(]*\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|" +
																				"^\\([0-9]+\\)([^(]*)\\s*(\\([0-9]+\\))?\\s*-\\s*[^(]*\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$";;
	public static final String CONF_BLACK_PLAYER_NAME_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*([^(]*)\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|" +
																					"^[^(]*\\s*-\\s*([^(]*)\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|" +
																				"^\\([0-9]+\\)[^(]*\\s*\\([0-9]+\\)\\s*-\\s*([^(]*)\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|" +
																					"^\\([0-9]+\\)[^(]*\\s*-\\s*([^(]*)\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$";
	public static final String CONF_WHITE_PLAYER_ELO_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\(([0-9]+)\\)\\s*-\\s*[^(]*\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|" +
																				"\\([0-9]+\\)[^(]*\\s*\\(([0-9]+)\\)\\s*-\\s*[^(]*\\s*(\\([0-9]+\\))?\\s*(\\[[A-E][0-9]{2}\\])?\\s*$";
	public static final String CONF_BLACK_PLAYER_ELO_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\(([0-9]+)\\)\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|"+
																				"^[^(]*\\s*-\\s*[^(]*\\s*\\(([0-9]+)\\)\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|" +
																				"^\\([0-9]+\\)[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\(([0-9]+)\\)\\s*(\\[[A-E][0-9]{2}\\])?\\s*$|"+
																				"^\\([0-9]+\\)[^(]*\\s*-\\s*[^(]*\\s*\\(([0-9]+)\\)\\s*(\\[[A-E][0-9]{2}\\])?\\s*$";
	public static final String CONF_VARIANT_EXTRACTION_REGEX_DEFAULT = "^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\([0-9]+\\)\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^[^(]*\\s*-\\s*[^(]*\\s*\\([0-9]+\\)\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^[^(]*\\s*-\\s*[^(]*\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^\\([0-9]+\\)[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\([0-9]+\\)\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^\\([0-9]+\\)[^(]*\\s*-\\s*[^(]*\\s*\\([0-9]+\\)\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^\\([0-9]+\\)[^(]*\\s*\\([0-9]+\\)\\s*-\\s*[^(]*\\s*\\[([A-E][0-9]{2})\\]\\s*$|" +
																		"^\\([0-9]+\\)[^(]*\\s*-\\s*[^(]*\\s*\\[([A-E][0-9]{2})\\]\\s*$";
	public static final String CONF_EVENT_EXTRATION_REGEX_DEFAULT = "";
	public static final String CONF_SITE_EXTRACTION_REGEX_DEFAULT = "^([^(,]*)\\s*(\\([0-9\\.]+\\))?\\s*,\\s*[0-9\\.-]+\\s*$";
	public static final String CONF_ROUND_EXTRACTION_REGEX_DEFAULT = "^[^(,]*\\s*\\(([0-9\\.]+)\\)\\s*,\\s*[0-9\\.-]+\\s*$";
	public static final String CONF_DATE_EXTRACTION_REGEX_DEFAULT = "^[^(,]*\\s*,\\s*([0-9\\.-]+)\\s*$|^[^(,]*\\s*\\([0-9\\.]+\\)\\s*,\\s*([0-9\\.-]+)\\s*$";

	public static final String CONF_SHOW_PDF_GAME_WHEN_NEW_GAME_SELECTED = "SHOW_PDF_GAME_WHEN_NEW_GAME_SELECTED";

	private static ApplicationConfiguration a_instance = null;

	protected ListOfListenersDefaultNotifierImp<FigureSetChangedObserved,
												FigureSetChangedListener,
												FigureSet> _listOfFigureSetChangedListeners;

	private ApplicationConfiguration()
	{
		this( sa_MAIN_FOLDER, sa_APPLICATION_NAME, VERSION, sa_CONFIGURATION_GROUP, GLOBAL_CONF_FILE_NAME );
	}

	public ApplicationConfiguration( String mainFolder, String applicationName, String version, String group, String globalConfFileName )
	{
		super( mainFolder, applicationName + "/" + version, group, null, globalConfFileName );

		_listOfFigureSetChangedListeners = new ListOfListenersDefaultNotifierImp<>(this, (lsnr, obs, oldVal, newVal) -> lsnr.figureSetChanged(obs, oldVal, newVal) );
	}

	public static ApplicationConfiguration create( String applicationName )
	{
		sa_APPLICATION_NAME = applicationName;
		a_instance = new ApplicationConfiguration();

		return( a_instance );
	}

	public static ApplicationConfiguration instance()
	{
		return( a_instance );
	}

	public Properties M_getDefaultProperties( String language )
	{
		Properties result = super.M_getDefaultProperties( language );

		result.setProperty(CONF_HAS_TO_SHOW_COMMENTS, "1" );
		result.setProperty(CONF_HAS_TO_SHOW_SEGMENTS, "0" );
		result.setProperty(CONF_USE_IMPROVED_PDF_GAME_PARSER, "1" );
		result.setProperty(CONF_LANGUAGE, EN_LANGUAGE );
		result.setProperty(CONF_ADDITIONAL_LANGUAGE, "CAT" );
		result.setProperty(CONF_EDIT_COMMENT_WINDOW_ALWAYS_ON_TOP, "1" );

		result.setProperty(CONF_EDIT_TAG_MANDATORY_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_PLAYER_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_EVENT_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_OPENING_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_TIME_AND_DATE_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_TIME_CONTROL_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_GAME_CONCLUSION_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_ALTERNATIVE_STARTING_POSITION_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_MISCELLANEOUS_FILTER_SELECTED, "1" );
		result.setProperty(CONF_EDIT_TAG_WINDOW_ALWAYS_ON_TOP, "1" );

		result.setProperty(CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_SHOW_GAMES, ChessLanguageConfiguration.ALGEBRAIC_FIGURINE_NOTATION );
		result.setProperty(CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_PARSE_TEXT_OF_GAMES_FROM, "Spanish" );

		result.setProperty(CONF_SHOW_NAGS, "1" );
		result.setProperty(CONF_DETACHED_GAME_WINDOWS_ALWAYS_ON_TOP, "1" );
		result.setProperty(CONF_LICENSES_HAVE_BEEN_ACCEPTED, "0" );

//		result.setProperty(CONF_HOME_WEB_PAGE_URL, "https://frojasg1.com" );
//		result.setProperty(CONF_AUTHOR_EMAIL_ADDRESS, "frojasg1@hotmail.com" );

		result.setProperty(CONF_ACTIVATE_AUTOCOMPLETION_FOR_REGEX, "1" );

		result.setProperty(CONF_SHOW_PDF_GAME_WHEN_NEW_GAME_SELECTED, "1" );

		result.setProperty(CONF_NUMBER_OF_THREADS_FOR_BACKGROUND_OCR_RECOGNITION, "1" );
		result.setProperty(CONF_IS_CHESS_BOARD_RECOGNIZER_ACTIVATED, "1" );

		result.setProperty(CONF_CHESS_FIGURINE_SET, FigureSet.VIRTUAL_PIECES_SET.name() );

		result.setProperty(CONF_ANALYSIS_FRAME_ALWAYS_ON_TOP, "1" );

		result.setProperty(CONF_CHESS_BOARD_RECOGNIZER_FLIP_BOARD_MODE, "Auto" );

		setDefaultRegexForTagExtraction( result );

		return( result );
	}

	@Override
	public FlipBoardMode getChessBoardRecognizedFlipBoardMode()
	{
		return( FlipBoardMode.forName( M_getStrParamConfiguration( CONF_CHESS_BOARD_RECOGNIZER_FLIP_BOARD_MODE ) ) );
	}

	@Override
	public void setChessBoardRecognizedFlipBoardMode( FlipBoardMode value )
	{
		String name = "";
		if( value != null )
			name = value.getName();

		M_setStrParamConfiguration( CONF_CHESS_BOARD_RECOGNIZER_FLIP_BOARD_MODE, name );
	}

	@Override
	public FigureSet getChessFigurineSet()
	{
		return( FigureSet.valueOf( M_getStrParamConfiguration( CONF_CHESS_FIGURINE_SET ) ) );
	}

	public void setChessFigurineSet( FigureSet value )
	{
		FigureSet oldValue = getChessFigurineSet();
		M_setStrParamConfiguration( CONF_CHESS_FIGURINE_SET, value.name() );

		_listOfFigureSetChangedListeners.notifyListeners( oldValue, value );
	}
/*
	@Override
	public String getAuthorEmailAddress()
	{
		return( this.M_getStrParamConfiguration( CONF_AUTHOR_EMAIL_ADDRESS ) );
	}

	@Override
	public String getHomePageUrl()
	{
		return( this.M_getStrParamConfiguration( CONF_HOME_WEB_PAGE_URL ) );
	}
*/
	@Override
	public void setZoomFactorReference(DoubleReference zoomFactor) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public DoubleReference getZoomFactorReference() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public ChessLanguageConfiguration getChessLanguageConfiguration( String language )
	{
		ChessLanguageConfiguration result = EnglishChessConfiguration.instance();
		
		if( language != null )
		{
			result = ChessLanguageConfiguration.getConfiguration(language);
			if( result == null )
				result = ChessLanguageConfiguration.createConfiguration(language, language);		// in case language was not a name of a language, it would take the string with the piece codes of the language.
		}

		return( result );
//		return( ChessLanguageConfiguration.getConfiguration(language) );
	}

	@Override
	public String getDefaultLanguageBaseConfigurationFolder()
	{
		String result = System.getProperty("user.home") + File.separator  +
						getConfigurationMainFolder() + File.separator +
						getApplicationNameFolder() + File.separator +
						getApplicationGroup() + File.separator;

		return( result );
	}

	@Override
	public String getDefaultLanguageConfigurationFolder( String language )
	{
		String result = getDefaultLanguageBaseConfigurationFolder() + language;

		return( result );
	}

	@Override
	public String getDefaultLanguage()
	{
		return( GenericFunctions.instance().getObtainAvailableLanguages().getDefaultLanguage() );
	}

	@Override
	public String getApplicationNameFolder()
	{
		return( getApplicationName() + File.separator + getApplicationVersion() );
	}

	@Override
	public String getApplicationVersion()
	{
		return( VERSION );
	}

	@Override
	public boolean getLicensesHaveBeenAccepted()
	{
		return( this.M_getBoolParamConfiguration(CONF_LICENSES_HAVE_BEEN_ACCEPTED) );
	}

	@Override
	public void setLicensesHaveBeenAccepted( boolean value )
	{
		this.M_setBoolParamConfiguration( CONF_LICENSES_HAVE_BEEN_ACCEPTED, value );
	}

	@Override
	public String getInternationalPropertiesPathInJar()
	{
		return( sa_PROPERTIES_PATH_IN_JAR );
	}
	
	public String getAdditionalLanguage()
	{
		return( M_getStrParamConfiguration( CONF_ADDITIONAL_LANGUAGE ) );
	}
	
	public void setAdditionalLanguage( String value )
	{
		M_setStrParamConfiguration( CONF_ADDITIONAL_LANGUAGE, value );
	}

	@Override
	public String getConfigurationMainFolder()
	{
		return( sa_MAIN_FOLDER );
	}

	@Override
	public String getApplicationName()
	{
		return( sa_APPLICATION_NAME );
	}

	@Override
	public String getApplicationGroup()
	{
		return( sa_CONFIGURATION_GROUP );
	}

	@Override
	public String getResourceNameForApplicationIcon()
	{
		return( RESOURCE_FOR_APPLICATION_ICON );
	}

	@Override
	public ChessLanguageConfiguration getChessLanguageConfigurationToShow()
	{
		return( getChessLanguageConfiguration( getConfigurationOfChessLanguageToShow() ) );
	}

	@Override
	public ChessLanguageConfiguration getChessLanguageConfigurationToParseTextFrom()
	{
		return( getChessLanguageConfiguration( getConfigurationOfChessLanguageToParseTextFrom( ) ) );
	}
/*
	protected ChessMoveNotation buildChessMoveNotationForView()
	{
		return( new ChessMoveAlgebraicNotation(getChessLanguageConfiguration()) );
	}
	
	protected ChessMoveMatcher buildChessMoveMatcherForView()
	{
		return( new ChessMoveAlgebraicNotation(getChessLanguageConfiguration()) );
	}
*/	
	/**
	 *
	 * @return
	 */
//	@Override
//	public ChessMoveNotation getChessMoveNotationForView()
//	{
//		if( _chessMoveNotationForView == null )
//		{
//			_chessMoveNotationForView = buildChessMoveNotationForView();
//		}
//		return( _chessMoveNotationForView );
//	}

	/**
	 *
	 * @return
	 */
//	@Override
//	public ChessMoveMatcher getChessMoveMatcherForView()
//	{
//		if( _chessMoveMatcherForView == null )
//		{
//			_chessMoveMatcherForView = buildChessMoveMatcherForView();
//		}
//		return( _chessMoveMatcherForView );
//	}

	@Override
	public boolean getHasToShowComments()
	{
		return( M_getBoolParamConfiguration(CONF_HAS_TO_SHOW_COMMENTS) );
	}

	@Override
	public void setHasToShowComments( boolean hasToShowComments )
	{
		M_setBoolParamConfiguration( CONF_HAS_TO_SHOW_COMMENTS, hasToShowComments );
	}

	@Override
	public void setUseImprovedPdfGameParser( boolean value )
	{
		M_setBoolParamConfiguration( CONF_USE_IMPROVED_PDF_GAME_PARSER, value );
	}

	@Override
	public boolean getHasToShowSegments()
	{
		return( M_getBoolParamConfiguration(CONF_HAS_TO_SHOW_SEGMENTS) );
	}

	@Override
	public boolean getUseImprovedPdfGameParser()
	{
		return( M_getBoolParamConfiguration(CONF_USE_IMPROVED_PDF_GAME_PARSER) );
	}

	@Override
	public boolean getEditCommentWindowAlwaysOnTop() {
		return( M_getBoolParamConfiguration(CONF_EDIT_COMMENT_WINDOW_ALWAYS_ON_TOP) );
	}

	@Override
	public void setEditCommentWindowAlwaysOnTop(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_COMMENT_WINDOW_ALWAYS_ON_TOP, value );
	}

	@Override
	public boolean getMandatoryFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_MANDATORY_FILTER_SELECTED ) );
	}

	@Override
	public boolean getPlayerFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_PLAYER_FILTER_SELECTED ) );
	}

	@Override
	public boolean getEventFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_EVENT_FILTER_SELECTED ) );
	}

	@Override
	public boolean getOpeningFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_OPENING_FILTER_SELECTED ) );
	}

	@Override
	public boolean getTimeAndDateFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_TIME_AND_DATE_FILTER_SELECTED ) );
	}

	@Override
	public boolean getTimeControlFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_TIME_CONTROL_FILTER_SELECTED ) );
	}

	@Override
	public boolean getGameConclusionFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_GAME_CONCLUSION_FILTER_SELECTED ) );
	}

	@Override
	public boolean getMiscellaneousFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_MISCELLANEOUS_FILTER_SELECTED ) );
	}

	@Override
	public boolean getAlternativeStartingFilterSelected()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_ALTERNATIVE_STARTING_POSITION_FILTER_SELECTED ) );
	}

	@Override
	public String getFilterItemForTAGs(int index)
	{
		return( M_getStrParamConfiguration( CONF_EDIT_TAG_FILTER_ITEM_FOR_TAG + "_" + index ) );
	}

	@Override
	public boolean getEditTAGwindowAlwaysOnTop()
	{
		return( M_getBoolParamConfiguration( CONF_EDIT_TAG_WINDOW_ALWAYS_ON_TOP ) );
	}

	@Override
	public void setMandatoryFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_MANDATORY_FILTER_SELECTED, value );
	}

	@Override
	public void setPlayerFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_PLAYER_FILTER_SELECTED, value );
	}

	@Override
	public void setEventFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_EVENT_FILTER_SELECTED, value );
	}

	@Override
	public void setOpeningFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_OPENING_FILTER_SELECTED, value );
	}

	@Override
	public void setTimeAndDateFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_TIME_AND_DATE_FILTER_SELECTED, value );
	}

	@Override
	public void setTimeControlFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_TIME_CONTROL_FILTER_SELECTED, value );
	}

	@Override
	public void setGameConclusionFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_GAME_CONCLUSION_FILTER_SELECTED, value );
	}

	@Override
	public void setMiscellaneousFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_MISCELLANEOUS_FILTER_SELECTED, value );
	}

	@Override
	public void setAlternativeStartingFilterSelected(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_ALTERNATIVE_STARTING_POSITION_FILTER_SELECTED, value );
	}


	@Override
	public void setFilterItemForTAGs(int index, String valueOfFilter)
	{
		M_setStrParamConfiguration( CONF_EDIT_TAG_FILTER_ITEM_FOR_TAG + "_" + index, valueOfFilter );
	}

	@Override
	public void setEditTagWindowAlwaysOnTop(boolean value)
	{
		M_setBoolParamConfiguration( CONF_EDIT_TAG_WINDOW_ALWAYS_ON_TOP, value );
	}

	@Override
	public String getConfigurationOfChessLanguageToShow() {
		return( M_getStrParamConfiguration( CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_SHOW_GAMES ) );
	}

	@Override
	public String getConfigurationOfChessLanguageToParseTextFrom() {
		return( M_getStrParamConfiguration( CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_PARSE_TEXT_OF_GAMES_FROM ) );
	}

	@Override
	public void setConfigurationOfChessLanguageToShow(String value) {
		M_setStrParamConfiguration( CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_SHOW_GAMES, value );
	}

	@Override
	public void setConfigurationOfChessLanguageToParseTextFrom(String value) {
		M_setStrParamConfiguration( CONF_STRING_TO_CONFIGURE_LANGUAGE_TO_PARSE_TEXT_OF_GAMES_FROM, value );
	}

	@Override
	public boolean getHasToShowNAGs()
	{
		return( M_getBoolParamConfiguration( CONF_SHOW_NAGS ) );
	}

	@Override
	public void setHasToShowNAGs(boolean value)
	{
		this.M_setBoolParamConfiguration( CONF_SHOW_NAGS, value );
	}

	@Override
	public boolean isAutocompletionForRegexActivated()
	{
		return( M_getBoolParamConfiguration( CONF_ACTIVATE_AUTOCOMPLETION_FOR_REGEX ) );
	}

	@Override
	public void setIsAutocompletionForRegexActivated(boolean value)
	{
		this.M_setBoolParamConfiguration( CONF_ACTIVATE_AUTOCOMPLETION_FOR_REGEX, value );
	}

	@Override
	public boolean getDetachedGameWindowsAlwaysOnTop() {
		return( M_getBoolParamConfiguration( CONF_DETACHED_GAME_WINDOWS_ALWAYS_ON_TOP ) );
	}

	@Override
	public void setDetachedGameWindowsAlwaysOnTop(boolean value) {
		this.M_setBoolParamConfiguration( CONF_DETACHED_GAME_WINDOWS_ALWAYS_ON_TOP, value );
	}
/*
	@Override
	protected void changeLanguage_internal_simple( String language ) throws ConfigurationException
	{
		
	}
*/
	@Override
	public boolean getShowPdfGameWhenNewGameSelected() {
		return( M_getBoolParamConfiguration( CONF_SHOW_PDF_GAME_WHEN_NEW_GAME_SELECTED ) );
	}

	@Override
	public void setShowPdfGameWhenNewGameSelected(boolean value) {
		this.M_setBoolParamConfiguration( CONF_SHOW_PDF_GAME_WHEN_NEW_GAME_SELECTED, value );
	}

	public void setDefaultRegexForTagExtraction( Properties result )
	{
		result.setProperty(CONF_WHITE_PLAYER_NAME_EXTRACTION_REGEX, CONF_WHITE_PLAYER_NAME_EXTRACTION_REGEX_DEFAULT );
		result.setProperty(CONF_BLACK_PLAYER_NAME_EXTRACTION_REGEX, CONF_BLACK_PLAYER_NAME_EXTRACTION_REGEX_DEFAULT );
		result.setProperty(CONF_WHITE_PLAYER_ELO_EXTRACTION_REGEX, CONF_WHITE_PLAYER_ELO_EXTRACTION_REGEX_DEFAULT );
		result.setProperty(CONF_BLACK_PLAYER_ELO_EXTRACTION_REGEX, CONF_BLACK_PLAYER_ELO_EXTRACTION_REGEX_DEFAULT );
		result.setProperty(CONF_VARIANT_EXTRACTION_REGEX, CONF_VARIANT_EXTRACTION_REGEX_DEFAULT );
		result.setProperty(CONF_EVENT_EXTRATION_REGEX, CONF_EVENT_EXTRATION_REGEX_DEFAULT );
		result.setProperty(CONF_SITE_EXTRACTION_REGEX, CONF_SITE_EXTRACTION_REGEX_DEFAULT );
		result.setProperty(CONF_ROUND_EXTRACTION_REGEX, CONF_ROUND_EXTRACTION_REGEX_DEFAULT );
		result.setProperty(CONF_DATE_EXTRACTION_REGEX, CONF_DATE_EXTRACTION_REGEX_DEFAULT );
	}

	@Override
	public void setDefaultRegexForTagExtraction() {
		setDefaultRegexForTagExtraction( this );
	}

	@Override
	public void setWhitePlayerExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_WHITE_PLAYER_NAME_EXTRACTION_REGEX, regex );
	}

	@Override
	public void setBlackPlayerExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_BLACK_PLAYER_NAME_EXTRACTION_REGEX, regex );
	}

	@Override
	public void setWhiteEloExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_WHITE_PLAYER_ELO_EXTRACTION_REGEX, regex );
	}

	@Override
	public void setBlackEloExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_BLACK_PLAYER_ELO_EXTRACTION_REGEX, regex );
	}

	@Override
	public void setVariantExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_VARIANT_EXTRACTION_REGEX, regex );
	}

	@Override
	public void setEventExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_EVENT_EXTRATION_REGEX, regex );
	}

	@Override
	public void setSiteExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_SITE_EXTRACTION_REGEX, regex );
	}

	@Override
	public void setRoundExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_ROUND_EXTRACTION_REGEX, regex );
	}

	@Override
	public void setDateExtractionRegex(String regex) {
		M_setStrParamConfiguration( CONF_DATE_EXTRACTION_REGEX, regex );
	}

	@Override
	public String getWhitePlayerExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_WHITE_PLAYER_NAME_EXTRACTION_REGEX ) );
	}

	@Override
	public String getBlackPlayerExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_BLACK_PLAYER_NAME_EXTRACTION_REGEX ) );
	}

	@Override
	public String getWhiteEloExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_WHITE_PLAYER_ELO_EXTRACTION_REGEX ) );
	}

	@Override
	public String getBlackEloExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_BLACK_PLAYER_ELO_EXTRACTION_REGEX ) );
	}

	@Override
	public String getVariantExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_VARIANT_EXTRACTION_REGEX ) );
	}

	@Override
	public String getEventExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_EVENT_EXTRATION_REGEX ) );
	}

	@Override
	public String getSiteExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_SITE_EXTRACTION_REGEX ) );
	}

	@Override
	public String getRoundExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_ROUND_EXTRACTION_REGEX ) );
	}

	@Override
	public String getDateExtractionRegex() {
		return( M_getStrParamConfiguration( CONF_DATE_EXTRACTION_REGEX ) );
	}

	@Override
	public void setNumberOfThreadsForBackgroundChessBoardRecognition(int value)
	{
		this.M_setIntParamConfiguration(CONF_NUMBER_OF_THREADS_FOR_BACKGROUND_OCR_RECOGNITION, value);
	}

	@Override
	public boolean isChessBoardRecognizerActivated()
	{
		return( this.M_getBoolParamConfiguration(CONF_IS_CHESS_BOARD_RECOGNIZER_ACTIVATED) );
	}

	@Override
	public void setIsChessBoardRecognizerActivated(boolean value)
	{
		this.M_setBoolParamConfiguration(CONF_IS_CHESS_BOARD_RECOGNIZER_ACTIVATED, value);
	}

	@Override
	public int getNumberOfThreadsForBackgroundChessBoardRecognition() {
		return( this.M_getIntParamConfiguration(CONF_NUMBER_OF_THREADS_FOR_BACKGROUND_OCR_RECOGNITION) );
	}

	@Override
	public String getConfigurationParameterNameForNumberOfThreadsForBackgroundRecognition()
	{
		return( CONF_NUMBER_OF_THREADS_FOR_BACKGROUND_OCR_RECOGNITION );
	}

	@Override
	public String getConfigurationParameterNameForIsChessRecognizerActivated()
	{
		return( CONF_IS_CHESS_BOARD_RECOGNIZER_ACTIVATED );
	}

	@Override
	public boolean getChessAnalysisFrameAlwaysOnTop() {
		return( this.M_getBoolParamConfiguration(CONF_ANALYSIS_FRAME_ALWAYS_ON_TOP) );
	}

	@Override
	public void setChessAnalysisFrameAlwaysOnTop(boolean value) {
		this.M_setBoolParamConfiguration(CONF_ANALYSIS_FRAME_ALWAYS_ON_TOP, value);
	}

	@Override
	public void addFigureSetChangedListener(FigureSetChangedListener listener) {
		_listOfFigureSetChangedListeners.add( listener );
	}

	@Override
	public void removeFigureSetChangedListener(FigureSetChangedListener listener) {
		_listOfFigureSetChangedListeners.remove(listener);
	}
}
