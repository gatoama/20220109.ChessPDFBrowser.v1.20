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
package com.frojasg1.chesspdfbrowser.engine.model.chess.configuration;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author Usuario
 */
public class ChessLanguageConfiguration
{
	public static class LanguageConfigurationData // implements Comparable<LanguageConfigurationData>
	{
		public final boolean _isFigurineAlgebraicNotation;
		public final String _languageName;
		public final String _stringOfPieceCodes;
		public final String _stringOfPieceCodesForBlack;

		
		public LanguageConfigurationData( String languageName, String pieceCodes,
											String pieceCodesForBlack, boolean isFigurineAlgebraicNotation )
		{
			_languageName = languageName;
			_stringOfPieceCodes = pieceCodes;
			_stringOfPieceCodesForBlack = pieceCodesForBlack;
			_isFigurineAlgebraicNotation = isFigurineAlgebraicNotation;
		}

		public LanguageConfigurationData( String languageName, String pieceCodes )
		{
			_languageName = languageName;
			_stringOfPieceCodes = pieceCodes;
			_stringOfPieceCodesForBlack = pieceCodes;
			_isFigurineAlgebraicNotation = false;
		}



		@Override
		public String toString()		// the name of the language in the configured language
		{
			String result = _languageName;
			if( (_languageName != null ) && !_languageName.equals(_stringOfPieceCodes ) )
			{
				if( ChessStringsConf.instance() != null )
				{
					result = ChessStringsConf.instance().M_getStrParamConfiguration( _languageName.toUpperCase() );
					if( result == null )
						result = _languageName;
				}
			}
			return( result );
		}
/*
		@Override
		public int compareTo( LanguageConfigurationData another )		// to sort the names for the combobox
		{
			int result = 1;
			
			if( another != null )
			{
				if( ( _languageName == null ) && (another._languageName == null ) )
					result = 0;
				else if( another._languageName != null )
				{
					result = -1;
					if( _languageName != null )
						result = - another._languageName.toLowerCase().compareTo( _languageName.toLowerCase() );
				}
			}

			return( result );
		}
*/
	}

	public static final String GENERIC = "Generic";		// the generic language is really identified by the string of piedeCodes.

	public static final String ENGLISH = "English";
	public static final String SPANISH = "Spanish";
	public static final String CZECH = "Czech";
	public static final String DANISH = "Danish";
	public static final String DUTCH = "Dutch";
	public static final String ESTONIAN = "Estonian";
	public static final String FINNISH = "Finnish";
	public static final String FRENCH = "French";
	public static final String GERMAN = "German";
	public static final String HUNGARIAN = "Hungarian";
	public static final String ICELANDIC = "Icelandic";
	public static final String ITALIAN = "Italian";
	public static final String NORWEGIAN = "Norwegian";
	public static final String POLISH = "Polish";
	public static final String PORTUGUESE = "Portuguese";
	public static final String ROMANIAN = "Romanian";
	public static final String SWEDISH = "Swedish";
	public static final String ALGEBRAIC_FIGURINE_NOTATION = "FIGURINE";
	public static final String CUSTOM = "Custom";

	protected static final LanguageConfigurationData CUSTOM_languageConfigurationData = new LanguageConfigurationData( CUSTOM, "" );
	
	protected static final Comparator<LanguageConfigurationData> _COMPARATOR_FIGURINE_FIRST = (lcd1, lcd2 ) -> {
		int result = 0;
		if( lcd1._isFigurineAlgebraicNotation == lcd2._isFigurineAlgebraicNotation )
			result = CollectionFunctions.instance()._ASCENDANT_NO_CASE_SENSITIVE.compare( lcd1._languageName, lcd2._languageName );
		else if( lcd1._isFigurineAlgebraicNotation )
			result = -1;
		else
			result = 1;

		return( result );
	};

	protected String _charsForKing = null;
	protected String _charsForQueen = null;
	protected String _charsForRook = null;
	protected String _charsForBishop = null;
	protected String _charsForKnight = null;
	protected String _charsForPawn = null;
	
	protected String _charsForPieces = null;
	protected String _charsForPiecesForBlack = null;

	protected String _firstCharForEveryPiece = null;

	protected String _language = null;
//	protected int _language = 0;

	protected static final Map<String, ChessLanguageConfiguration> _map;
	protected static final Vector<LanguageConfigurationData> _vector = new Vector< LanguageConfigurationData >();

	public final boolean _isFigurineAlgebraicNotation;

	static
	{
		_map = new HashMap< String, ChessLanguageConfiguration >();

		_vector.add( new LanguageConfigurationData( CZECH, "KDVSJ" ) );
		_vector.add( new LanguageConfigurationData( DANISH, "KDTLS" ) );
		_vector.add( new LanguageConfigurationData( DUTCH, "KDTLP" ) );
		_vector.add( new LanguageConfigurationData( ENGLISH, "KQRBN" ) );
		_vector.add( new LanguageConfigurationData( ESTONIAN, "KLVOR" ) );
		_vector.add( new LanguageConfigurationData( FINNISH, "KDTLR" ) );
		_vector.add( new LanguageConfigurationData( FRENCH, "RDTFC" ) );
		_vector.add( new LanguageConfigurationData( GERMAN, "KDTLS" ) );
		_vector.add( new LanguageConfigurationData( HUNGARIAN, "KVBFH" ) );
		_vector.add( new LanguageConfigurationData( ICELANDIC, "KDHBR" ) );
		_vector.add( new LanguageConfigurationData( ITALIAN, "RDTAC" ) );
		_vector.add( new LanguageConfigurationData( NORWEGIAN, "KDTLS" ) );
		_vector.add( new LanguageConfigurationData( POLISH, "KHWGS" ) );
		_vector.add( new LanguageConfigurationData( PORTUGUESE, "RDTBC" ) );
		_vector.add( new LanguageConfigurationData( ROMANIAN, "RDTNC" ) );
		_vector.add( new LanguageConfigurationData( SPANISH, "RDTAC" ) );
		_vector.add( new LanguageConfigurationData( SWEDISH, "KDTLS" ) );
		_vector.add( new LanguageConfigurationData( ALGEBRAIC_FIGURINE_NOTATION, "♔♕♖♗♘", "♚♛♜♝♞", true ) );

		_map.put( ENGLISH, EnglishChessConfiguration.instance() );
		Iterator< LanguageConfigurationData > it = _vector.iterator();
		while( it.hasNext() )
		{
			LanguageConfigurationData lcd = it.next();
			
			_map.put( lcd._languageName, new ChessLanguageConfiguration( lcd._languageName, lcd._stringOfPieceCodes,
																		lcd._stringOfPieceCodesForBlack, lcd._isFigurineAlgebraicNotation ) );
		}
	}

	protected ChessLanguageConfiguration( String language, String pieceCodes )
	{
		this( language, pieceCodes, pieceCodes, false );
	}

	/**
	 * 
	 * @param language		// string which represents the language
	 * @param pieceCodes	// String of 5 characters corresponding to the english: KQRBN
	 */
	protected ChessLanguageConfiguration( String language, String pieceCodes, String pieceCodesForBlack,
										boolean isFigurineAlgebraicNotation )
	{
		_language = language;
		_isFigurineAlgebraicNotation = isFigurineAlgebraicNotation;
		_charsForPiecesForBlack = pieceCodesForBlack;

		if( ( pieceCodes == null ) || ( pieceCodes.length() != 5 ) )
			throw( new RuntimeException( getChessStrConf().getProperty( ChessStringsConf.CONF_PIECE_CODES_FIVE_CHARS ) ) );

		String king = StringFunctions.instance().getNotRepeatedChar( pieceCodes, 0 );
		String queen = StringFunctions.instance().getNotRepeatedChar( pieceCodes, 1 );
		String rook = StringFunctions.instance().getNotRepeatedChar( pieceCodes, 2 );
		String bishop = StringFunctions.instance().getNotRepeatedChar( pieceCodes, 3 );
		String knight = StringFunctions.instance().getNotRepeatedChar( pieceCodes, 4 );

		if( ( king == null ) || ( queen == null ) || (rook == null) ||
			(bishop == null ) || (knight == null ) )
		{
			throw( new RuntimeException( getChessStrConf().getProperty( ChessStringsConf.CONF_REPEATED_PIECE_CODE ) +
										" " + pieceCodes ) );
		}
		putCharsForKing( king );
		putCharsForQueen( queen );
		putCharsForRook( rook );
		putCharsForBishop( bishop );
		putCharsForKnight( knight );
		putCharsForPawn( "abcdefgh" );
	}

	public void putCharsForKing( String value )
	{
		_charsForKing = value;
		updateCharsForPieces();
	}

	public void putCharsForQueen( String value )
	{
		_charsForQueen = value;
		updateCharsForPieces();
	}

	public void putCharsForRook( String value )
	{
		_charsForRook = value;
		updateCharsForPieces();
	}

	public void putCharsForBishop( String value )
	{
		_charsForBishop = value;
		updateCharsForPieces();
	}

	public void putCharsForKnight( String value )
	{
		_charsForKnight = value;
		updateCharsForPieces();
	}

	public void putCharsForPawn( String value )
	{
		_charsForPawn = value;
		updateCharsForPieces();
	}

	protected void appendFirstCharWithDefault( StringBuilder sb, String c1, String defaultChar )
	{
		String charToUse = "\0";

		if( ( c1 != null ) && ( c1.length() > 0 ) )
			charToUse = c1.substring( 0, 1 );
		else if( ( defaultChar != null ) && ( defaultChar.length() > 0 ) )
			charToUse = defaultChar.substring( 0, 1 );

		sb.append( charToUse );
	}
	
	protected void updateCharsForPieces()
	{
		StringBuilder sb = new StringBuilder();
		
		ChessLanguageConfiguration englishLanguageConfiguration = this;
		if( !_language.equals( ENGLISH ) )
			englishLanguageConfiguration = EnglishChessConfiguration.instance();

		appendFirstCharWithDefault(sb, _charsForKing, englishLanguageConfiguration._charsForKing );
		appendFirstCharWithDefault(sb, _charsForQueen, englishLanguageConfiguration._charsForQueen );
		appendFirstCharWithDefault(sb, _charsForRook, englishLanguageConfiguration._charsForRook );
		appendFirstCharWithDefault(sb, _charsForBishop, englishLanguageConfiguration._charsForBishop );
		appendFirstCharWithDefault(sb, _charsForKnight, englishLanguageConfiguration._charsForKnight );

		_charsForPieces = sb.toString();
	}

	protected String getFirstCharOfString( String str )
	{
		String result = new String();

		if( ( str != null ) && ( str.length() > 0 ) )
			result = str.substring( 0, 1 );

		return( result );
	}

	public String getCharForKing()
	{
		return( getFirstCharOfString( _charsForKing ) );
	}

	public String getCharForQueen()
	{
		return( getFirstCharOfString( _charsForQueen ) );
	}

	public String getCharForRook()
	{
		return( getFirstCharOfString( _charsForRook ) );
	}

	public String getCharForBishop()
	{
		return( getFirstCharOfString( _charsForBishop ) );
	}

	public String getCharForKnight()
	{
		return( getFirstCharOfString( _charsForKnight ) );
	}

	public String getCharForPawn()
	{
		return( getFirstCharOfString( _charsForPawn ) );
	}

	public String getCharsForPieces()
	{
		return( _charsForPieces );
	}

	public String getCharsForPiecesForBlack()
	{
		return( _charsForPiecesForBlack );
	}

	public boolean isFigurineAlgebraicNotation() {
		return _isFigurineAlgebraicNotation;
	}

	public String translateMoveStringToAnotherLanguage( String moveString, ChessLanguageConfiguration anotherClc,
														ChessGameMove cgm )
	{
		return( translateMoveStringFromOneLanguageToAnotherOne( moveString, this, anotherClc, cgm ) );
	}

	public String translateMoveStringToEnglish( String moveString, ChessGameMove cgm )
	{
		return( translateMoveStringToAnotherLanguage( moveString, EnglishChessConfiguration.instance(), cgm ) );
	}

	public String translateMoveStringFromAnotherLanguage( String moveString, ChessLanguageConfiguration anotherClc,
															ChessGameMove cgm )
	{
		return( translateMoveStringFromOneLanguageToAnotherOne( moveString, anotherClc, this, cgm ) );
	}

	public String translateMoveStringFromEnglish( String moveString, ChessGameMove cgm )
	{
		return( translateMoveStringFromAnotherLanguage( moveString, EnglishChessConfiguration.instance(), cgm ) );
	}

	public String translateMoveStringFromOneLanguageToAnotherOne( String moveString, ChessLanguageConfiguration originClc,
																	ChessLanguageConfiguration destinationClc,
																	ChessGameMove cgm )
	{
		String result = moveString;

		String originSetOfChars = originClc.getCharsForPieces();
		String originSetOfCharsForBlack = originClc.getCharsForPiecesForBlack();
		String destinationSetOfChars = destinationClc.getCharsForPieces();
		String destinationSetOfCharsForBlack = destinationClc.getCharsForPiecesForBlack();


		if( ( cgm != null ) && ( cgm.getMoveToken() != null ) && ! cgm.getMoveToken().isWhiteToMove() )
			result = StringFunctions.instance().replaceSetOfChars( result, originSetOfCharsForBlack, destinationSetOfCharsForBlack );
		else
			result = StringFunctions.instance().replaceSetOfChars( result, originSetOfChars, destinationSetOfChars );
			
		if( ( cgm == null ) && originClc.isFigurineAlgebraicNotation() && !destinationClc.isFigurineAlgebraicNotation() )
			result = StringFunctions.instance().replaceSetOfChars( result, originSetOfCharsForBlack, destinationSetOfCharsForBlack );


		return( result );
	}

	public static ChessLanguageConfiguration getConfiguration( String language )
	{
		ChessLanguageConfiguration result = _map.get( language );

		return( result );
	}

	public static ChessLanguageConfiguration createConfiguration( String language, String pieceCodes )
	{
		ChessLanguageConfiguration result = null;
		
		if( language != null )
			result = _map.get( language );
		else
			language = pieceCodes;

		if( result == null )
		{
			result = new ChessLanguageConfiguration( language, pieceCodes );
			_map.put( language, result );
			_vector.add( new LanguageConfigurationData( language, pieceCodes ) );
			Collections.sort(_vector, _COMPARATOR_FIGURINE_FIRST);
		}

		return( result );
	}

	protected static int score( boolean value )
	{
		return( value ? -1 : 1 );
	}

	public static Vector< LanguageConfigurationData > getListOfLanguagesToParseFrom( Locale locale )
	{
		Vector< ChessLanguageConfiguration.LanguageConfigurationData > vector1 = new Vector();
		getListOfLanguages( locale ).stream().filter( (lcd) -> !lcd._isFigurineAlgebraicNotation ).forEach( (lcd) -> vector1.add( lcd ) );

		return( vector1 );
	}

	public static Vector< LanguageConfigurationData > getListOfLanguages( Locale locale )
	{
		Vector< LanguageConfigurationData > result = new Vector();
		result.add( CUSTOM_languageConfigurationData );

		Vector< LanguageConfigurationData > tmpVec = new Vector( _vector );
		Comparator comp = CollectionFunctions.instance().getLocaleStringComparatorNoCaseSensitive( locale );
		Collections.sort(tmpVec, (lcd1, lcd2) -> {
			int res = score(lcd1._isFigurineAlgebraicNotation) - score( lcd2._isFigurineAlgebraicNotation );
			if( res == 0 )
				res = comp.compare(lcd1, lcd2); 

			return( res );
		});

		result.addAll( tmpVec );
		
		return( result );
	}

	public static LanguageConfigurationData getCustomLanguage()
	{
		return( CUSTOM_languageConfigurationData );
	}
	
	public static LanguageConfigurationData getLanguageConfigurationData( String language )
	{
		LanguageConfigurationData result = null;

		Iterator<LanguageConfigurationData> it = _vector.iterator();
		
		while( ( it.hasNext() ) && ( result == null ) )
		{
			LanguageConfigurationData current = it.next();
			if( current._languageName.equals( language ) )
			{
				result = current;
			}
		}

		return( result );
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}
}
