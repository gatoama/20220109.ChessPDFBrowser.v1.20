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

import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class ChessStringsConf extends ConfigurationParent
{
	public static final String CONF_ERROR_UPDATING_ADD_INFO = "ERROR_UPDATING_ADD_INFO";
	public static final String CONF_ERROR_DOING_OR_UNDOING_MOVE = "ERROR_DOING_OR_UNDOING_MOVE";
	public static final String CONF_ERROR_INCONSISTENT_CHESSBOARD = "ERROR_INCONSISTENT_CHESSBOARD";
	public static final String CONF_AMBIGUITY_IN_MOVE = "AMBIGUITY_IN_MOVE";
	public static final String CONF_NO_PIECE_CAN_MOVE = "NO_PIECE_CAN_MOVE";
	public static final String CONF_DESTINATION_SQUARE_NOT_VALID = "DESTINATION_SQUARE_NOT_VALID";
	public static final String CONF_ERROR_PARSING_MOVE = "ERROR_PARSING_MOVE";
	public static final String CONF_AFTER_THE_PIECE_CODE = "AFTER_THE_PIECE_CODE";
	public static final String CONF_UNRECOGNIZED_CHAR = "UNRECOGNIZED_CHAR";
	public static final String CONF_ADDITIONAL_CHARS_NOT_PARSED = "ADDITIONAL_CHARS_NOT_PARSED";
	public static final String CONF_ADDITIONAL_CHARS = "ADDITIONAL_CHARS";
	public static final String CONF_MOVE_NOT_RECOGNIZED = "MOVE_NOT_RECOGNIZED";
	public static final String CONF_ALREADY_PARSING = "ALREADY_PARSING";
	public static final String CONF_NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
	public static final String CONF_TOKEN_DIFFERENT_FROM_ATTRIBUTE_TOKEN = "TOKEN_DIFFERENT_FROM_ATTRIBUTE_TOKEN";
	public static final String CONF_ERROR_INSERTING_MOVES = "ERROR_INSERTING_MOVES";
	public static final String CONF_ERROR_DOING_MOVES = "ERROR_DOING_MOVES";
	public static final String CONF_NUMBER_OF_PLY_NOT_VALID = "NUMBER_OF_PLY_NOT_VALID";
	public static final String CONF_INTERNAL_ERROR = "INTERNAL_ERROR";
	public static final String CONF_NO_MAIN_MOVE = "NO_MAIN_MOVE";
	public static final String CONF_TOKEN_NOT_EXPECTED = "TOKEN_NOT_EXPECTED";
	public static final String CONF_TOKEN_EXPECTED_TEXT = "TOKEN_EXPECTED_TEXT";
	public static final String CONF_ERROR_DOING_MOVE = "ERROR_DOING_MOVE";
	public static final String CONF_TOKEN_NOT_IDENTIFIED = "TOKEN_NOT_IDENTIFIED";
	public static final String CONF_NOT_AN_OPEN_SQUARE_BRACE = "NOT_AN_OPEN_SQUARE_BRACE";
	public static final String CONF_ANOTHER_OPEN_SQUARE_BRACE_FOUND = "ANOTHER_OPEN_SQUARE_BRACE_FOUND";
	public static final String CONF_CLOSING_OF_COMMNENT_NOT_FOUND = "CLOSING_OF_COMMNENT_NOT_FOUND";
	public static final String CONF_NUMBER_NOT_FOLLOWED_BY_VALID_MOVE = "NUMBER_NOT_FOLLOWED_BY_VALID_MOVE";
	public static final String CONF_NOT_A_MOVE_PRETOKEN = "NOT_A_MOVE_PRETOKEN";
	public static final String CONF_ALL_PAWN_POSITIONS_FULL = "ALL_PAWN_POSITIONS_FULL";
	public static final String CONF_PIECE_ALREADY_PUT = "PIECE_ALREADY_PUT";
	public static final String CONF_BAD_ROW = "BAD_ROW";
	public static final String CONF_BAD_COLUMN = "BAD_COLUMN";
	public static final String CONF_EXPECTED_ENPASSANT_PAWN = "EXPECTED_ENPASSANT_PAWN";
	public static final String CONF_ILLEGAL_MOVE = "ILLEGAL_MOVE";
	public static final String CONF_EMPTY_PIECE_NOT_FOUND = "EMPTY_PIECE_NOT_FOUND";
	public static final String CONF_NO_PIECE_TO_UNTAKE = "NO_PIECE_TO_UNTAKE";
	public static final String CONF_ILLEGAL_MOVE_TO_UNDO = "ILLEGAL_MOVE_TO_UNDO";
	public static final String CONF_LAST_MOVED_PIECE_NOT_AT_DESTINATION_SQUARE = "LAST_MOVED_PIECE_NOT_AT_DESTINATION_SQUARE";
	public static final String CONF_NUM_OF_FIELDS_INCORRECT_FOR_FEN = "NUM_OF_FIELDS_INCORRECT_FOR_FEN";
	public static final String CONF_COLOR_TO_MOVE_NOT_RECOGNIZED_FEN = "COLOR_TO_MOVE_NOT_RECOGNIZED_FEN";
	public static final String CONF_CELL_FOR_ENPASSANT_NOT_VALID_FEN = "CELL_FOR_ENPASSANT_NOT_VALID_FEN";
	public static final String CONF_BAD_NUMBER_OF_PLIES_WITHOUT_PROGRESS_FEN = "BAD_NUMBER_OF_PLIES_WITHOUT_PROGRESS_FEN";
	public static final String CONF_NUMBER_OF_PLY_NOT_A_NUMBER = "NUMBER_OF_PLY_NOT_A_NUMBER";
	public static final String CONF_REPEATED_CHARACTER_IN_CASTLING_FIELD_FEN = "REPEATED_CHARACTER_IN_CASTLING_FIELD_FEN";
	public static final String CONF_UNRECOGNIZED_CHAR_IN_CASTLING_FIELD = "UNRECOGNIZED_CHAR_IN_CASTLING_FIELD";
	public static final String CONF_NUM_OF_ROWS_DIFFERENT_FROM_EIGHT_FEN = "NUM_OF_ROWS_DIFFERENT_FROM_EIGHT_FEN";
	public static final String CONF_UNRECOGNIZED_PIECE_FEN = "UNRECOGNIZED_PIECE_FEN";
	public static final String CONF_ROW_HAS_MORE_THAN_EIGHT_COLUMNS_FEN = "ROW_HAS_MORE_THAN_EIGHT_COLUMNS";
	public static final String CONF_ENPASSANT_CELL_OUT_OF_BOUNDS = "ENPASSANT_CELL_OUT_OF_BOUNDS";
	public static final String CONF_ENPASSANT_MOVE_DOES_NOT_MATCH = "ENPASSANT_MOVE_DOES_NOT_MATCH";
	public static final String CONF_BAD_PIECE_TYPE = "BAD_PIECE_TYPE";
	public static final String CONF_COORDINATE_OF_MOVE_OUT_OF_BOUNDS = "COORDINATE_OF_MOVE_OUT_OF_BOUNDS";
	public static final String CONF_LEVEL_OUT_OF_SEQUENCE = "LEVEL_OUT_OF_SEQUENCE";
	public static final String CONF_NAG_OUT_OF_RANGE = "NAG_OUT_OF_RANGE";
	public static final String CONF_BAD_FORMAT_FOR_NAG = "BAD_FORMAT_FOR_NAG";
	public static final String CONF_PIECE_DOES_NOT_MATCH_INITIAL_POSITION = "PIECE_DOES_NOT_MATCH_INITIAL_POSITION";
	public static final String CONF_ERROR_UNRECOGNIZED_PIECE_CODE = "ERROR_UNRECOGNIZED_PIECE_CODE";
	public static final String CONF_ERROR_SIZE_NOT_EXPECTED = "ERROR_SIZE_NOT_EXPECTED";
	public static final String CONF_PIECE_CODES_FIVE_CHARS = "PIECE_CODES_FIVE_CHARS";
	public static final String CONF_REPEATED_PIECE_CODE = "REPEATED_PIECE_CODE";

	public static final String CONF_WHITE_TO_PLAY = "WHITE_TO_PLAY";
	public static final String CONF_BLACK_TO_PLAY = "BLACK_TO_PLAY";
	public static final String CONF_CHECK = "CHECK";
	public static final String CONF_BLACK_RESIGNS = "BLACK_RESIGNS";
	public static final String CONF_WHITE_RESINGS = "WHITE_RESINGS";
	public static final String CONF_CHECK_MATE = "CHECK_MATE";
	public static final String CONF_WHITE_WINS = "WHITE_WINS";
	public static final String CONF_BLACK_WINS = "BLACK_WINS";
	public static final String CONF_DRAW = "DRAW";
	public static final String CONF_STALE_MATE = "STALE_MATE";
	public static final String CONF_THIRD_REPETITION = "THIRD_REPETITION";
	public static final String CONF_FIFTY_MOVES_WITHOUT_PROGRESS = "FIFTY_MOVES_WITHOUT_PROGRESS";
	public static final String CONF_BY_MUTUAL_AGREEMENT = "BY_MUTUAL_AGREEMENT";
	public static final String CONF_INSUFFICIENT_MATERIAL = "INSUFFICIENT_MATERIAL";

	public static final String CONF_INITIAL_POSITION_NOT_SET_1 = "INITIAL_POSITION_NOT_SET_1";
	public static final String CONF_INITIAL_POSITION_NOT_SET_2 = "INITIAL_POSITION_NOT_SET_2";
	public static final String CONF_INITIAL_POSITION_NOT_SET_3 = "INITIAL_POSITION_NOT_SET_3";

	public static final String CONF_ILLEGAL_POSITION_1 = "ILLEGAL_POSITION_1";
	public static final String CONF_ILLEGAL_POSITION_2 = "ILLEGAL_POSITION_2";

//	protected static final String APPLICATION_GROUP = "general";
//	protected static final String APPLICATION_NAME = "FileEncoder";
	public static final String GLOBAL_CONF_FILE_NAME = "ChessStringsConf.properties";

//	protected String a_pathPropertiesInJar = null;
	private static ChessStringsConf a_instance = null;

	BaseApplicationConfigurationInterface _baseConfiguration = null;

	private ChessStringsConf( BaseApplicationConfigurationInterface appliConf )
	{
		super( appliConf.getConfigurationMainFolder(),
				appliConf.getApplicationNameFolder(),
				appliConf.getApplicationGroup(),
				null,
				GLOBAL_CONF_FILE_NAME );

		registerToChangeLanguageAsObserver(appliConf);

		_baseConfiguration = appliConf;
//		a_pathPropertiesInJar = pathPropertiesInJar;
	}

	public static ChessStringsConf createInstance( BaseApplicationConfigurationInterface appliConf ) throws ConfigurationException
	{
		if( a_instance == null )
		{
			a_instance = new ChessStringsConf( appliConf );
			if( appliConf != null )
			{
				a_instance.changeLanguage( appliConf.getLanguage() );
			}
		}

		return( a_instance );
	}

	public static ChessStringsConf instance()
	{
		return( a_instance );
	}

	protected Properties M_getDefaultProperties2( String language )
	{
		Properties result = new Properties();

		result.setProperty(CONF_ERROR_UPDATING_ADD_INFO, "Internal ERROR while updating additional info: " );
		result.setProperty(CONF_ERROR_DOING_OR_UNDOING_MOVE, "Internal Error doing or undoing a move: " );
		result.setProperty(CONF_ERROR_INCONSISTENT_CHESSBOARD, "Internal Error: Inconsistent chessboard with moveToken to be parsed. MoveToken: " );
		result.setProperty(CONF_AMBIGUITY_IN_MOVE, "Two or more pieces could move to the same place. There was ambiguity in the move : " );
		result.setProperty(CONF_NO_PIECE_CAN_MOVE, "No piece of this kind could move to the destination square. MoveToken: " );
		result.setProperty(CONF_DESTINATION_SQUARE_NOT_VALID, "Error parsing move. The destination square is not valid. Move: " );
		result.setProperty(CONF_ERROR_PARSING_MOVE, "ERROR parsing chess move: " );
		result.setProperty(CONF_AFTER_THE_PIECE_CODE, "It seems that after the piece code" );
		result.setProperty(CONF_UNRECOGNIZED_CHAR, "there is some unrecognized character" );
		result.setProperty(CONF_ADDITIONAL_CHARS_NOT_PARSED, "It seems that additional characters are not being parsed." );
		result.setProperty(CONF_ADDITIONAL_CHARS, "It seems that there are additional characters." );
		result.setProperty(CONF_MOVE_NOT_RECOGNIZED, "ChessMove not recognized: " );
		result.setProperty(CONF_ALREADY_PARSING, "Already parsing." );
		result.setProperty(CONF_NOT_IMPLEMENTED, "not implemented" );
		result.setProperty(CONF_TOKEN_DIFFERENT_FROM_ATTRIBUTE_TOKEN, "ERROR: attribute in token different from AttributeToken." );
		result.setProperty(CONF_ERROR_INSERTING_MOVES, "ERROR inserting moves." );
		result.setProperty(CONF_ERROR_DOING_MOVES, "ERROR doing moves." );
		result.setProperty(CONF_NUMBER_OF_PLY_NOT_VALID, "found a number of ply not suitable: " );
		result.setProperty(CONF_INTERNAL_ERROR, "Internal ERROR" );
		result.setProperty(CONF_NO_MAIN_MOVE, "New variation with no main move." );
		result.setProperty(CONF_TOKEN_NOT_EXPECTED, "ERROR: token not expected: " );
		result.setProperty(CONF_TOKEN_EXPECTED_TEXT, "TokenId expected: %s at line %d starting at %d character" );
		result.setProperty(CONF_ERROR_DOING_MOVE, "Error doing move" );
		result.setProperty(CONF_TOKEN_NOT_IDENTIFIED, "Token not identified.: %s Line: %d. position: %d" );
		result.setProperty(CONF_NOT_AN_OPEN_SQUARE_BRACE, "The current char was not an open square brace (begin of comment)." );
		result.setProperty(CONF_ANOTHER_OPEN_SQUARE_BRACE_FOUND, "Another open square brace found inside a comment. We suppose that is a mistake. Aborting." +
															" The start of the comment was found at line: %d. Char position: %d" );
		result.setProperty(CONF_CLOSING_OF_COMMNENT_NOT_FOUND, "Closing of comment not found. The start of the comment was found at line: %d" +
																". Char position: %d" );
		result.setProperty(CONF_NUMBER_NOT_FOLLOWED_BY_VALID_MOVE, "ERROR: Number was not followed by a valid move. Line: %d. Number: %s" );
		result.setProperty(CONF_NOT_A_MOVE_PRETOKEN, "ERROR: the pretoken was not a move pretoken." );
		result.setProperty(CONF_ALL_PAWN_POSITIONS_FULL, "The Piece: %s could not be put because all pawn positions were full" );
		result.setProperty(CONF_PIECE_ALREADY_PUT, "The Piece: %s has been already put" );
		result.setProperty(CONF_BAD_ROW, "Bad row" );
		result.setProperty(CONF_BAD_COLUMN, "Bad column" );
		result.setProperty(CONF_EXPECTED_ENPASSANT_PAWN, "ERROR. Expected pawn to be captured en passant. Empty square found instead" );
		result.setProperty(CONF_ILLEGAL_MOVE, "Illegal move" );
		result.setProperty(CONF_EMPTY_PIECE_NOT_FOUND, "Empty piece not found. Impossible" );
		result.setProperty(CONF_NO_PIECE_TO_UNTAKE, "ERROR: There is no piece to untake." );
		result.setProperty(CONF_ILLEGAL_MOVE_TO_UNDO, "Illegal move to undo" );
		result.setProperty(CONF_LAST_MOVED_PIECE_NOT_AT_DESTINATION_SQUARE, "The last moved piece is not at the destination square. Last move" );
		result.setProperty(CONF_NUM_OF_FIELDS_INCORRECT_FOR_FEN, "Number of fields incorrect for FEN string: " );
		result.setProperty(CONF_COLOR_TO_MOVE_NOT_RECOGNIZED_FEN, "Color to move not recognized: %s. FEN string: %s" );
		result.setProperty(CONF_CELL_FOR_ENPASSANT_NOT_VALID_FEN, "Cell for enPassant not valid \"%s\". FEN string: %s" );
		result.setProperty(CONF_BAD_NUMBER_OF_PLIES_WITHOUT_PROGRESS_FEN, "numberOfPliesWithoutProgress is not a number. %s. FEN string: %s" );
		result.setProperty(CONF_NUMBER_OF_PLY_NOT_A_NUMBER, "numberOfPly is not a number. %s. FEN string: %s" );
		result.setProperty(CONF_REPEATED_CHARACTER_IN_CASTLING_FIELD_FEN, "Repeated character in casling field" );
		result.setProperty(CONF_UNRECOGNIZED_CHAR_IN_CASTLING_FIELD, "Unrecognized character in castling field" );
		result.setProperty(CONF_NUM_OF_ROWS_DIFFERENT_FROM_EIGHT_FEN, "Number of rows in FEN string different from 8" );
		result.setProperty(CONF_UNRECOGNIZED_PIECE_FEN, "Unrecognized piece '%c'. FEN string: %s" );
		result.setProperty(CONF_ROW_HAS_MORE_THAN_EIGHT_COLUMNS_FEN, "Row has more than 8 columns" );
		result.setProperty(CONF_ENPASSANT_CELL_OUT_OF_BOUNDS, "En passant cell out of bounds (%d, %d)" );
		result.setProperty(CONF_ENPASSANT_MOVE_DOES_NOT_MATCH, "En passant move does not match. WhiteToPlay: %b. En passant square: %s" );
		result.setProperty(CONF_BAD_PIECE_TYPE, "Bad pieceType" );
		result.setProperty(CONF_COORDINATE_OF_MOVE_OUT_OF_BOUNDS, "Coordinate of move out of bounds. Move" );
		result.setProperty(CONF_LEVEL_OUT_OF_SEQUENCE, "Cannot insert this move. It is out of sequence. Current level: %d. Level to try to insert: %d" );
		result.setProperty(CONF_NAG_OUT_OF_RANGE, "nag out of range" );
		result.setProperty(CONF_BAD_FORMAT_FOR_NAG, "Bad format for nag" );
		result.setProperty(CONF_PIECE_DOES_NOT_MATCH_INITIAL_POSITION, "Piece does not match the initial position. Piece: %s. Position: %s" );
		result.setProperty(CONF_ERROR_UNRECOGNIZED_PIECE_CODE, "ERROR: unrecognized piece code" );
		result.setProperty(CONF_ERROR_SIZE_NOT_EXPECTED, "Error. Size not expected" );
		result.setProperty(CONF_PIECE_CODES_FIVE_CHARS, "pieceCodes must have 5 characters, corresponding to the english: KQRBN" );
		result.setProperty(CONF_REPEATED_PIECE_CODE, "Repeated piececode" );

		result.setProperty(CONF_WHITE_TO_PLAY, "White to play" );
		result.setProperty(CONF_BLACK_TO_PLAY, "Black to play" );
		result.setProperty(CONF_CHECK, "CHECK!" );
		result.setProperty(CONF_BLACK_RESIGNS, "black resigns" );
		result.setProperty(CONF_WHITE_RESINGS, "white resigns" );
		result.setProperty(CONF_CHECK_MATE, "CHECK MATE!!" );
		result.setProperty(CONF_WHITE_WINS, "white wins" );
		result.setProperty(CONF_BLACK_WINS, "black wins" );
		result.setProperty(CONF_DRAW, "DRAW" );
		result.setProperty(CONF_STALE_MATE, "Stale mate" );
		result.setProperty(CONF_THIRD_REPETITION, "Third repetition of position" );
		result.setProperty(CONF_FIFTY_MOVES_WITHOUT_PROGRESS, "Fifty moves without progress" );
		result.setProperty(CONF_BY_MUTUAL_AGREEMENT, "by mutual agreement" );

		result.setProperty(CONF_INSUFFICIENT_MATERIAL, "insufficient material" );

		result.setProperty(CONF_INITIAL_POSITION_NOT_SET_1, "Initial" );
		result.setProperty(CONF_INITIAL_POSITION_NOT_SET_2, "position" );
		result.setProperty(CONF_INITIAL_POSITION_NOT_SET_3, "not set" );

		result.setProperty(CONF_ILLEGAL_POSITION_1, "Illegal" );
		result.setProperty(CONF_ILLEGAL_POSITION_2, "position" );

		return( result );
	}

	protected String M_getPropertiesNameFromClassPath( String language )
	{
		String result;
//		result = a_pathPropertiesInJar + sa_dirSeparator + language + sa_dirSeparator + a_configurationFileName;
		result = _baseConfiguration.getInternationalPropertiesPathInJar() + "/" + language + "/" + a_configurationFileName;
		return( result );
	}

	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = null;

		try
		{
			result = cargarPropertiesClassPath( M_getPropertiesNameFromClassPath( language ) );
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
			result = null;
		}

		if( result == null )
		{
			result = M_getDefaultProperties2(language);
		}
		else
		{
			result = M_makePropertiesAddingDefaults( result, M_getDefaultProperties2(language) );
		}

		return( result );
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException
	{
		super.changeLanguage(language);
	}
	
}
