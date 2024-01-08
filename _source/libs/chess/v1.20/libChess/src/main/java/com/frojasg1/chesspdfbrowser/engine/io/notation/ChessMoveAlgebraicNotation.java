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
package com.frojasg1.chesspdfbrowser.engine.io.notation;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessInternalError;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.movematcher.ChessMoveMatcher;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessBoard;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.NAG;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Bishop;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.ChessPiece;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.King;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Knight;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Pawn;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Queen;
import com.frojasg1.chesspdfbrowser.engine.model.chess.pieces.Rook;
import com.frojasg1.general.number.IntegerFunctions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Usuario
 */
public class ChessMoveAlgebraicNotation implements ChessMoveNotation, ChessMoveMatcher
{
	protected static final String _saREGEXP_FOR_GAME_MOVE = "(([__PIECES__])?([a-h]?)([1-8]?))?([xX]?)([a-h][1-8])([=]?[__PIECES__])?(\\+|\\+\\+|#)?";

	protected Pattern _chessMovePattern = null;

	protected ChessLanguageConfiguration _chessConfiguration = null;

	protected ChessPiece _piece = null;

	protected static ChessMoveAlgebraicNotation _instance = null;

/*
	// language is one of the static int values in ChessConfiguration class
	public ChessMoveAlgebraicNotation( ChessLanguageConfiguration chessConfiguration )
	{
		setChessLanguageConfiguration( chessConfiguration );
	}
*/

	// always in English
	protected ChessMoveAlgebraicNotation()
	{
		setChessLanguageConfiguration( ChessLanguageConfiguration.getConfiguration( ChessLanguageConfiguration.ENGLISH ) );
	}

	public static ChessMoveAlgebraicNotation getInstance()
	{
		if( _instance == null )
			_instance = new ChessMoveAlgebraicNotation();

		return( _instance );
	}

	public final void setChessLanguageConfiguration( ChessLanguageConfiguration clc )
	{
		if( ( clc != null ) && ( clc.getCharsForPieces() != null ) )
		{
			_chessConfiguration = clc;
			String regexp = _saREGEXP_FOR_GAME_MOVE.replaceAll( "__PIECES__", _chessConfiguration.getCharsForPieces() );
			_chessMovePattern = Pattern.compile( regexp );
		}
	}

	// always in English.
	@Override
	public String getMoveString( ChessBoard chessBoard, ChessGameMove cgm ) throws ChessParserException
	{
		String result = null;

		if( cgm.getMoveToken() != null )
		{
			result = cgm.getMoveToken().getString();
		}

		if( !cgm.hasBeenChecked() && ( cgm.getMoveToken() != null ) && ( chessBoard != null ) )
		{
			ChessGameMove tmpCgm = new ChessGameMove( cgm );
			setChessGameMove( chessBoard, cgm.getMoveToken(), tmpCgm );
			cgm.copy( tmpCgm );
		}
		else if( ( result == null ) &&
					!cgm.hasBeenChecked() &&
					( chessBoard != null ) &&
					chessBoard.isLegalThisMove(cgm)
				)
		{
			_piece = chessBoard.getPiece_fast(cgm._column1, cgm._row1 );

			if( _piece instanceof King )
			{
				King king = (King) _piece;

				if( king.legalMoveIsCastle(cgm) )
				{
					if( cgm._column2 == 7 )
					{
						result = "O-O";
					}
					else if( cgm._column2 == 3 )
					{
						result = "O-O-O";
					}
				}
			}

			if( result == null )
			{
				String suffixForPiece = getSuffixToSelectThePieceToMove( _piece, chessBoard, cgm );

				ChessPiece capturedPiece = getCapturedPiece( chessBoard, cgm  );
				boolean isCapture = ( capturedPiece != null );

				String capture = "";
				if( isCapture ) capture = "x";

				String pieceCode = getPieceCode( _piece, cgm, isCapture );

				String destinationSquare = cgm.getColumnLetter( cgm._column2 ) + cgm._row2;

				String promotion = "";
				if( ( cgm._promotionPiece != null ) && (cgm._promotionPiece.length() > 0 ) )
				{
					promotion = "=" + getPieceCode( cgm._promotionPiece );
				}

				result = pieceCode + suffixForPiece + capture + destinationSquare + promotion;
			}

			String check = "";
			try
			{
				chessBoard.move(cgm);
				if( chessBoard.isCheck() )
				{
					if( chessBoard.isCheckMate( chessBoard.isThereAnyLegalMove() ) )
					{
						check = "#";
					}
					else
					{
						check = "+";
					}
				}
				chessBoard.undoMove(cgm);
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				throw( new ChessInternalError( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_DOING_OR_UNDOING_MOVE ) + cgm  ) );
			}

			result = result + check;

			if( cgm.getMoveToken() == null )
			{
				MoveToken mt = new MoveToken();
				mt.setIsWhiteToMove( chessBoard.getIsWhitesTurn() );
				String englishMoveString = _chessConfiguration.translateMoveStringToEnglish(result, cgm);
				mt.setString( englishMoveString );
				cgm.setMoveToken(mt);	// we set the string so as not having to recalculate it again.
			}
		}
		return( result );
	}

	protected ChessPiece getCapturedPiece( ChessBoard chessBoard, ChessGameMove cgm  )
	{
		ChessPiece result = null;
		try
		{
			result = chessBoard.getCapturedPiece( cgm );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	public String getPieceCode( ChessPiece piece, ChessGameMove cgm, boolean isCapture )
	{
		String result = "";

		if( piece instanceof King )		result = _chessConfiguration.getCharForKing();
		else if( piece instanceof Queen )	result = _chessConfiguration.getCharForQueen();
		else if( piece instanceof Rook )	result = _chessConfiguration.getCharForRook();
		else if( piece instanceof Bishop )	result = _chessConfiguration.getCharForBishop();
		else if( piece instanceof Knight )	result = _chessConfiguration.getCharForKnight();
		else if( ( piece instanceof Pawn ) && isCapture )	result = cgm.getColumnLetter(cgm._column1);

		if( _chessConfiguration.isFigurineAlgebraicNotation() && !cgm.getMoveToken().isWhiteToMove() )
		{
			StringBuilder sb = new StringBuilder();
			sb.append( ( char ) ( result.charAt(0) + 6 ) );
		}

		return( result );
	}

	public String getPieceCode( String internalPieceCode )
	{
		String result = "";
		String ucaseInternalPieceCode = internalPieceCode.toUpperCase();

		if( "K".equals( ucaseInternalPieceCode ) )	result = _chessConfiguration.getCharForKing();
		else if( "Q".equals( ucaseInternalPieceCode ) )	result = _chessConfiguration.getCharForQueen();
		else if( "R".equals( ucaseInternalPieceCode ) )	result = _chessConfiguration.getCharForRook();
		else if( "B".equals( ucaseInternalPieceCode ) )	result = _chessConfiguration.getCharForBishop();
		else if( "N".equals( ucaseInternalPieceCode ) )	result = _chessConfiguration.getCharForKnight();

		return( result );
	}

	public boolean isAPieceCode( String pieceCode )
	{
		return( _chessConfiguration.getCharsForPieces().indexOf( pieceCode ) > -1 );
	}
	
	public String getSuffixToSelectThePieceToMove( ChessPiece piece, ChessBoard chessBoard, ChessGameMove cgm )
	{
		String result = "";

		// pawn has no suffix
		if( !( piece instanceof Pawn ) )
		{
			List<ChessPiece> listOfPiecesOfSameTypeSameColor = chessBoard.getPiecesOfTypeColor( piece, piece.getColor() );
			List<ChessPiece> listOfPiecesWhichCanMakeTheSameMove = new ArrayList<ChessPiece>();

			Iterator<ChessPiece> it = listOfPiecesOfSameTypeSameColor.iterator();
			ChessPiece tmpPiece = null;

			while( it.hasNext() )
			{
				tmpPiece = it.next();

				if( tmpPiece != piece )
				{
					ChessGameMove cgm2 = new ChessGameMove( tmpPiece.getColumn(), tmpPiece.getRow(), cgm._column2, cgm._row2 );
					if( cgm._hasCapturedPiece )
						cgm2.setHasCapturedPiece();

//					if( tmpPiece.canMoveTo( cgm._column2, cgm._row2 ) )
					try
					{
						if( tmpPiece.isLegalThisMove(cgm2) )
							listOfPiecesWhichCanMakeTheSameMove.add( tmpPiece );
					}
					catch( Throwable th )
					{
						th.printStackTrace();
					}
				}
			}

			if( listOfPiecesWhichCanMakeTheSameMove.size() > 0 )
			{
				boolean hasSameColumn = false;
				boolean hasSameRow = false;
				it = listOfPiecesWhichCanMakeTheSameMove.iterator();
				while( !( hasSameColumn && hasSameRow ) && it.hasNext() )
				{
					tmpPiece = it.next();
					if( tmpPiece.getColumn() == piece.getColumn() ) hasSameColumn = true;
					if( tmpPiece.getRow() == piece.getRow() )		hasSameRow = true;
				}

				if( hasSameColumn && hasSameRow )	result = cgm.getColumnLetter( piece.getColumn() ) + piece.getRow();
				else if( hasSameColumn )			result = String.valueOf( piece.getRow() );
				else								result = cgm.getColumnLetter( piece.getColumn() );
			}
		}

		return( result );
	}

	@Override
	public boolean isItAChessMoveString( String moveString )
	{
		boolean result = false;

		String str = extractNAGs( moveString );

		str = removeCheck( str );

		if( isACastlingMove( str ) )
			result = true;
		else
		{
			Matcher matcher = _chessMovePattern.matcher(str);
			result = matcher.matches();
		}

		return( result );
	}

	public boolean isCastleKingSide( String moveStr )
	{
		boolean result = false;

		if( moveStr != null )
			result = ( moveStr.equals( "O-O" ) ||
						moveStr.equals( "0-0" ) ||
				// and now with EN DASH ("\u2013")
						moveStr.equals( "O–O" ) ||
						moveStr.equals( "0–0" )
				);

		return( result );
	}

	public boolean isCastleQueenSide( String moveStr )
	{
		boolean result = false;

		if( moveStr != null )
			result = ( moveStr.equals( "O-O-O" ) ||
						moveStr.equals( "0-0-0" ) ||
				// and now with EN DASH ("\u2013")
						moveStr.equals( "O–O–O" ) ||
						moveStr.equals( "0–0–0" )
				);

		return( result );
	}

	public boolean isACastlingMove( String moveStr )
	{
		return( isCastleKingSide( moveStr ) || isCastleQueenSide( moveStr ) );
	}

	// it must be in English
	protected void setChessGameMove( ChessBoard cb, MoveToken moveToken, ChessGameMove result ) throws ChessParserException
	{
		if( result != null )
		{
			if( ( cb != null ) && ( cb.getIsWhitesTurn() != moveToken.isWhiteToMove() ) )
			{
				throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_INCONSISTENT_CHESSBOARD ) + moveToken ) );
			}

			MoveStringFields parsedMove = new MoveStringFields( _chessConfiguration, moveToken.getString() );
			
			if( result.getMoveToken() != null )
			{
				result.getMoveToken().setString( parsedMove._moveStringWithoutNAGs );
				result.getMoveToken().addAllNAGs( parsedMove.getNagIterator() );
			}

			result.addAllNAGs( parsedMove.getNagIterator() );

			if( cb != null )
			{
				MoveToken mt = result.getMoveToken();
				if( parsedMove._isCastleKingside )
				{
					if( cb.getIsWhitesTurn() )
					{
						result.copy( ChessGamePosition._saWhiteCastleKingSideMove );
					}
					else
					{
						result.copy( ChessGamePosition._saBlackCastleKingSideMove );
					}
					result.setMoveToken( mt );		// the static moves do have moveToken = null
				}
				else if( parsedMove._isCastleQueenside )
				{
					if( cb.getIsWhitesTurn() )
					{
						result.copy( ChessGamePosition._saWhiteCastleQueenSideMove );
					}
					else
					{
						result.copy( ChessGamePosition._saBlackCastleQueenSideMove );
					}
					result.setMoveToken( mt );		// the static moves do have moveToken = null
				}
				else
				{
					List<ChessPiece> listOfPiecesOfSameTypeSameColor = cb.getPiecesOfTypeColor( parsedMove._pieceToMove, cb.getColorToPlay() );

					ChessPiece piece = null;

					Iterator<ChessPiece> it = listOfPiecesOfSameTypeSameColor.iterator();
					ChessPiece tmpPiece = null;

					while( it.hasNext() )
					{
						tmpPiece = it.next();

						ChessGameMove cgm2 = new ChessGameMove( tmpPiece.getColumn(), tmpPiece.getRow(), parsedMove._destinationColumn, parsedMove._destinationRow );
//						cgm2._hasCapturedPiece = parsedMove._isACaptureMove;		This line is an error.
						try
						{
		//					if( tmpPiece.canMoveTo( parsedMove._destinationColumn, parsedMove._destinationRow ) &&
							if( ( ( parsedMove._originColumn == null ) || ( parsedMove._originColumn == tmpPiece.getColumn() ) ) &&
								( ( parsedMove._originRow == null ) || ( parsedMove._originRow == tmpPiece.getRow() ) ) &&
								tmpPiece.isLegalThisMove(cgm2, false) &&
								cgm2._hasCapturedPiece == parsedMove._isACaptureMove )		// this field is filled by function isLegalThisMove
							{
								if( piece == null )
									piece = tmpPiece;
								else
									throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_AMBIGUITY_IN_MOVE ) +
																		moveToken ) );
							}
						}
						catch( Throwable th )
						{
							th.printStackTrace();
							throw( new ChessParserException( th.toString() ) );
						}
					}

					if( piece == null )
						throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_NO_PIECE_CAN_MOVE ) + moveToken ) );

					result.setMoveCoordinates( piece.getColumn(), piece.getRow(), parsedMove._destinationColumn, parsedMove._destinationRow );

					if( parsedMove._isACaptureMove )
						result.setHasCapturedPiece();

					if( parsedMove._promotionPiece != null )
						result.setPromotionPiece( parsedMove._promotionPiece );
				}
			}

//			result.setMoveToken( null );
			result._hasCapturedPiece = parsedMove._isACaptureMove;
		}
	}

	@Override
	public ChessGameMove getChessGameMove( ChessBoard cb, MoveToken moveToken ) throws ChessParserException
	{
		ChessGameMove result = null;

//		moveToken.setString( _chessConfiguration.translateMoveStringToEnglish( moveToken.getString() ) );	// we translate it to english, so that it can be saved in a pgn file
		result = new ChessGameMove( moveToken );	// if there is no initial position, then we cannot check if the move is correct.
													// when the initial position is set, then the moves can be transformed to real moves.
//		if( cb != null )
		try
		{
			setChessGameMove( cb, moveToken, result );
		}
		catch( Throwable th )
		{
			// to accept illegal moves.
			// when we represent it over the screen, we will mark it in red background.
		}

		return( result );
	}

	protected static String removeCheck( String str )
	{
		String result = str;

		String twoLastChars = str.substring( IntegerFunctions.max( 0, str.length() - 2 ), str.length() );
		String lastChar = str.substring( IntegerFunctions.max( 0, str.length() - 1 ), str.length() );

		if( twoLastChars.equals( "++" ) )
			result = str.substring( 0, str.length() - 2 );
		else if(	lastChar.equals( "+" ) ||
					lastChar.equals( "#" ) )
			result = str.substring( 0, str.length() - 1 );

		return( result );
	}
/*
	protected static String getNAG( String str )
	{
		String result = "";

		String twoLastChars = str.substring( IntegerFunctions.max( 0, str.length() - 2 ), str.length() );
		String lastChar = str.substring( IntegerFunctions.max( 0, str.length() - 1 ), str.length() );

		if( twoLastChars.equals( "!!" ) ||
			twoLastChars.equals( "!?" ) ||
			twoLastChars.equals( "?!" ) ||
			twoLastChars.equals( "??" )
		  )
			result = twoLastChars;
		else if(	lastChar.equals( "!" ) ||
					lastChar.equals( "?" ) )
			result = lastChar;

		return( result );
	}
*/
	protected String extractNAGs( String moveString )
	{
		String nagStr = "";
		String result = moveString;
		while( ( nagStr = NAG.getLastNAGStr(result) ).length() > 0 )
		{
			result = result.substring( 0, result.length() - nagStr.length() );
		}

		return( result ) ;
	}

	protected class MoveStringFields
	{
//	_saREGEXP_FOR_GAME_MOVE = "(([__PIECES__])([a-h]?)([1-8]?))?([a-h]?)([xX]?)([a-h][1-8])([=][__PIECES__])?(+|++|#)?";

		String _moveStringWithoutNAGs = null;
		String _pieceToMove = null;
		Integer _originColumn = null;
		Integer _originRow = null;
		int _destinationColumn = -1;
		int _destinationRow = -1;
		String _promotionPiece = null;

		boolean _isCastleKingside = false;
		boolean _isCastleQueenside = false;

		boolean _isACaptureMove = false;

		LinkedList<NAG> _nagList = null;

		protected ChessLanguageConfiguration _chessLanguageConfiguration;
		
		public MoveStringFields( ChessLanguageConfiguration clc, String moveString ) throws ChessParserException
		{
			_nagList = new LinkedList<NAG>();

			_chessLanguageConfiguration = clc;
			initialize( moveString );
		}

		protected boolean isAPieceCode( String pieceCode )
		{
			return( _chessLanguageConfiguration.getCharsForPieces().indexOf( pieceCode ) > -1 );
		}

		public Iterator<NAG> getNagIterator()
		{
			return( _nagList.iterator() );
		}

		protected String extractNAGs( String moveString )
		{
			String nagStr = "";
			String result = moveString;
			while( ( nagStr = NAG.getLastNAGStr(result) ).length() > 0 )
			{
				result = result.substring( 0, result.length() - nagStr.length() );
				try
				{
					NAG nag = new NAG( nagStr );
					_nagList.addFirst( nag );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}

			return( result ) ;
		}

		protected void initialize(  String moveString ) throws ChessParserException
		{
			String str = extractNAGs( moveString );

			_moveStringWithoutNAGs = str;
			
			str = removeCheck( str );

			if( isCastleKingSide( str ) )
				_isCastleKingside = true;
			else if( isCastleQueenSide( str ) )
				_isCastleQueenside = true;
			else
			{
				String regexp = _saREGEXP_FOR_GAME_MOVE.replaceAll( "__PIECES__", _chessLanguageConfiguration.getCharsForPieces() );
				Pattern pattern = Pattern.compile( regexp );
				Matcher matcher = pattern.matcher( str );

				if( matcher.matches() )
				{
					// we check if the last character is a piece.
					// in this case, we consider as if it was the "=" even if there is no "="
					if( str.length() > 1 )
					{
						String promotionPieceCandidate = str.substring( str.length() - 1, str.length() );
						if( isAPieceCode( promotionPieceCandidate ) )
						{
							_promotionPiece = promotionPieceCandidate;
							str = str.substring( 0, str.length() - 1 );

							if( str.substring( str.length() - 1, str.length() ).equals("=" ) )
							{
								str = str.substring( 0, str.length() - 1 );
							}
						}
					}

					int index = str.length() - 1;
					if( str.length() > 1 )
					{
						char rowChar = str.charAt( index );
						char columnChar = str.charAt( index - 1 );
						
						if( ( rowChar < '0' ) || ( rowChar > '9' ) ||
							( columnChar < 'a' ) || ( columnChar > 'h' ) )
							throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_DESTINATION_SQUARE_NOT_VALID ) +
															moveString ) );
						_destinationRow = rowChar - '0';
						_destinationColumn = columnChar - 'a' + 1;

						str = str.substring( 0, index - 1 );
						index = str.length()-1;
						if( str.length() > 0 )
						{
							if( str.toLowerCase().charAt( index ) == 'x' )
							{
								_isACaptureMove = true;
								str = str.substring( 0, index );
								index--;
							}

							if( str.length() > 0 )
							{
								char pieceChar = str.charAt(0);
								if( isAPieceCode( String.valueOf( pieceChar ) ) )
								{
									_pieceToMove = String.valueOf( pieceChar );

									if( str.length() > 1 )
									{
										String str2 = str.substring( 1 );
										char char1 = str2.charAt(0);
										if( ( char1 >= 'a' ) && ( char1 <= 'h' ) )
										{
											str = str2;
											_originColumn = char1 - 'a' + 1;
										}
									}

									if( str.length() > 1 )
									{
										str = str.substring( 1 );
										char char2 = str.charAt(0);
										if( ( char2 >= '1' ) && ( char2 <= '8' ) )
										{
											_originRow = char2 - '0';
										}
										else
											throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_PARSING_MOVE ) +
																			moveString + " ." +
																			getChessStrConf().getProperty( ChessStringsConf.CONF_AFTER_THE_PIECE_CODE ) +
																			" (" + pieceChar + ") " +
																			getChessStrConf().getProperty( ChessStringsConf.CONF_UNRECOGNIZED_CHAR ) +
																			" (" + char2 + ")" ) );
									}
									
									if( str.length() != 1 )
										throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_PARSING_MOVE ) +
																		moveString + ". " +
																		getChessStrConf().getProperty( ChessStringsConf.CONF_ADDITIONAL_CHARS_NOT_PARSED ) ) );
								}
								else if( ( str.length() == 1 ) && ( pieceChar >= 'a' ) && ( pieceChar <= 'h' ) )
								{
									_pieceToMove = "P";
									_originColumn = pieceChar - 'a' + 1;
									if( index != 0 )
										throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_ERROR_PARSING_MOVE ) +
																		moveString + ". " +
																		getChessStrConf().getProperty( ChessStringsConf.CONF_ADDITIONAL_CHARS ) ) );
								}
								else
								{
									throw( new ChessParserException( getChessStrConf().getProperty( ChessStringsConf.CONF_MOVE_NOT_RECOGNIZED ) +
																	moveString ) );
								}
							}
							else
							{
								_pieceToMove = "P";
							}
						}
						else
						{
							_pieceToMove = "P";
						}
					}
				}
				else
					System.out.println( "ERROR matching the move string." );
			}
		}
	}
	
	public static void main( String[] args ) throws ChessParserException
	{
		ChessLanguageConfiguration langConf = ChessLanguageConfiguration.getConfiguration(ChessLanguageConfiguration.ENGLISH);
//		ChessMoveAlgebraicNotation object = new ChessMoveAlgebraicNotation( langConf );
		
		ChessMoveAlgebraicNotation object = getInstance();

		String move = "Nf3";

		System.out.println( "string: " + move + " is " + ( object.isItAChessMoveString(move) ? "" : "not" ) +  " a move") ;

		String regexp = _saREGEXP_FOR_GAME_MOVE.replaceAll( "__PIECES__", langConf.getCharsForPieces() );

		System.out.println( "regexp: " + regexp + "  chars for pieces: " + langConf.getCharsForPieces() );

//		ChessMoveAlgebraicNotation cman = new ChessMoveAlgebraicNotation( ChessLanguageConfiguration.getConfiguration( ChessLanguageConfiguration.ENGLISH  ) );
////		MoveStringFields msf = new MoveStringFields( ChessLanguageConfiguration.getConfiguration( ChessLanguageConfiguration.ENGLISH  ), "O-O+" );

		System.out.println( "Hola" );	
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
