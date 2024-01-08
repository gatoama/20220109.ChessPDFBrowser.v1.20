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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation;

import com.frojasg1.chesspdfbrowser.engine.configuration.ChessStringsConf;
import com.frojasg1.chesspdfbrowser.engine.configuration.TagExtractorConfiguration;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessModelException;
import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.impl.TagsExtractorImpl;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.ImageToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.MoveToken;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens.Token;
import com.frojasg1.chesspdfbrowser.engine.model.chess.board.ChessGamePosition;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.ChessGameMove;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeGame;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNodeUtils;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.PDFPageSegmentatorInterface;
import com.frojasg1.general.NullFunctions;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.GeneralUpdatingProgress;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 *
 * @author Usuario
 */
public class RawChessGameParser extends PGNChessGameParser
{
	protected static final int UNITIALIZED_RESULT = -10;
	protected static final int ONLY_ONE_SKIPPED_VARIATION = -1;
	protected static final int NONE_OF_THE_SKIPPED_VARIATIONS_HAD_ONLY_ONE_PLY_WITHOUT_TAKING_INTO_ACCOUNT_THE_LAST_ONE = -2;
	protected static final int ONLY_THE_FIRST_SKIPPED_VARIATION_HAD_ONLY_ONE_PLY_AND_THERE_IS_A_CONTINUATION_AND_A_CONTINUATION_WITH_THE_SAME_PLY = -3;

	protected GeneralUpdatingProgress _gup = null;

	protected TagExtractorConfiguration _tagExtractorConfiguration = null;

	protected int _lastGamePage = -2;
	protected int _indexOfGameInCurrentPage = 1;

	protected PDFPageSegmentatorInterface _source = null;

	protected RawLexicalAnalyser _rawLex = null;

	protected MoveToken _nextMoveToken = null;
	protected Token _nextProperToken = null;

	protected ListOfSubVariationParents _listOfSubVariationParents = null;

	protected MoveToken _firstGameMoveToken = null;

	protected ArrayList<String> _bufferOfGameHeaderStringLines = null;

	protected Integer _indexOfBufferOfGameHeader = null;

	protected TagsExtractor _tagsExtractor = null;

	protected String _pdfBaseFileName = null;

	protected ImagePositionController _controller = null;

	protected InputImage _postponedInputImage = null;

	protected LinkedList<Token> _lastRetrievedTokens = new LinkedList<>();

//	protected List<Integer> _lastLinesWithMoves = new ArrayList<>();

	public RawChessGameParser( ChessLanguageConfiguration clc, GeneralUpdatingProgress gup,
								TagExtractorConfiguration tagExtractorConfiguration,
								TagsExtractor tagsExtractor,
								ImagePositionController controller,
								String pdfBaseFileName )
	{
		super( clc );

		_controller = controller;
		_tagExtractorConfiguration = tagExtractorConfiguration;
		_pdfBaseFileName = pdfBaseFileName;

		_tagsExtractor = tagsExtractor;
		_gup = gup;
	}

	@Override
	public List<ChessGame> parseChessGameText( String text, PDFPageSegmentatorInterface source,
										Integer initialPageToScanForGames,
										Integer finalPageToScanForGames) throws ChessParserException, CancellationException
	{
		if( _tagsExtractor instanceof TagsExtractorImpl )
			( (TagsExtractorImpl) _tagsExtractor).clearCache();

		List<ChessGame> result = new LinkedList<ChessGame>();

		checkAlreadyParsing();
		_isParsingString = true;

		if( source == null )
			_lexicalAnalyser = new RawLexicalAnalyser( _chessLanguageConfiguration, text );
		else
		{
			_gup.up_reset( 1.0D );
			_gup.up_prepareNextSlice( 1.0D );

			_lexicalAnalyser = new RawLexicalAnalyser( _chessLanguageConfiguration,
														source, _gup,
														initialPageToScanForGames,
														finalPageToScanForGames );

			_gup.up_performEnd();
		}

		result = parseRawText( source );

		return( result );
	}

	@Override
	public List<ChessGame> parseChessGameText( BufferedReader reader ) throws ChessParserException
	{
		throw( new ChessParserException( "RawChessGameParser.parseChessGameText( BufferedReader reader ) " +
										getChessStrConf().getProperty( ChessStringsConf.CONF_NOT_IMPLEMENTED ) ) );
	}

	public String getPdfBaseFileName() {
		return _pdfBaseFileName;
	}

	protected void resetBufferOfGameHeader()
	{
//		_lastLinesWithMoves.clear();
		_bufferOfGameHeaderStringLines = new ArrayList<>();
		_rawLex.setBufferOfGameHeaderStringLines(_bufferOfGameHeaderStringLines);
	}
/*
	protected void resetBufferOfGameHeader()
	{
		_rawLex.setBufferOfGameHeaderStringLines(null);
	}
*/
	protected List<ChessGame> parseRawText( PDFPageSegmentatorInterface source) throws ChessParserException, CancellationException
	{
		List<ChessGame> result = new ArrayList<ChessGame>();
		_aheadTokenList = new LinkedList<Token> ();

		_source = source;

		if( !( _lexicalAnalyser instanceof RawLexicalAnalyser ) )
			throw( new ChessParserException( "_lexicalAnalyser is not instanceof RawLexicalAnalyser" ) );

		_rawLex = (RawLexicalAnalyser) _lexicalAnalyser;

		if( _gup != null )		// third and last time we start the progress.
		{
			_gup.up_reset( 1.0D );
			_gup.up_prepareNextSlice( 1.0D );

			_gup.up_childStarts();
		}

		try
		{
//			List<String> bufferOfGameHeader = new ArrayList<>();
//			_rawLex.setBufferOfGameHeaderStringLines(bufferOfGameHeader);
			resetBufferOfGameHeader();
			while( hasNextMoveToken() )
			{
//				if( ( _bufferOfGameHeaderStringLines == null ) || ( !bufferOfGameHeader.isEmpty() ) )
//					_bufferOfGameHeaderStringLines = bufferOfGameHeader;

//				resetBufferOfGameHeader();
				ChessGame game = readChessGame();
				if( ( game != null ) &&
					( game.getNumberOfMovesOfMainLine() > 2 ) ||
					( game.getTotalNumberOfMoves() > 10 ) )
				{
					result.add( game );
					_indexOfGameInCurrentPage++;
				}

//				bufferOfGameHeader = new ArrayList<>();
//				_rawLex.setBufferOfGameHeaderStringLines(bufferOfGameHeader);
			}

			if( _gup != null )		// third and last time we start the progress.
				_gup.up_updateProgressFromChild(1.0d );
//				_gup.up_updateProgressFromChild(_source.getNumberOfPages() );
		}
		catch( CancellationException ce )
		{
			throw( ce );
		}
		catch( Exception th )
		{
			th.printStackTrace();
			throw( new ChessParserException( th.toString(), th ) );
		}

		if( _gup != null )
		{
			_gup.up_childEnds();
			_gup.up_performEnd();
		}
		
		return( result );
	}

	protected boolean setGameResult( MoveTreeNode currentNode,
										Token nextProperToken )
	{
		boolean result = true;
		if( currentNode != null )
		{
			ChessGameMove cgm = currentNode.getMove();

			if( cgm != null )
				cgm.setResultOfGame( nextProperToken.getString() );

//			if( currentNode.isMainLine() )
				result=false;
		}

		return( result );
	}

/*
	protected MoveToken peekFirstMoveOfGame() throws ChessParserException, CancellationException
	{
		MoveToken moveToken = null;
		
		while( ( moveToken == null ) && hasNext() )
		{
			Token token = next();
			if( token instanceof MoveToken )
			{
				moveToken = (MoveToken) token;
				giveBack( token );
			}
		}

		return( moveToken );
	}

	protected void removeFirsts( ArrayList<String> list, int firstLinesToRemove )
	{
		for( int ii=0; ii<firstLinesToRemove; ii++ )
			list.remove(0);
	}
*/
	@Override
	public ChessGame readChessGame() throws ChessParserException, ChessModelException, CancellationException
	{
//		MoveToken firstMove = peekFirstMoveOfGame();
		MoveToken firstMove = _nextMoveToken;
		if( firstMove == null )
		{
			firstMove = nextMoveToken();
		}

//		if( _lastLinesWithMoves.size() > 1 )
//			removeFirsts( _bufferOfGameHeaderStringLines, _lastLinesWithMoves.get(0) );

		ChessGameHeaderInfo cghi = readChessGameHeader( firstMove, _bufferOfGameHeaderStringLines );

//		ChessGamePosition cgp = new ChessGamePosition( cghi.get( ChessGameHeaderInfo.FEN_TAG ) );

		ChessGamePosition cgp = null;
		if( startsFromFirstMoveOfGame( firstMove ) )
		{
			cgp = new ChessGamePosition();
			cgp.setInitialPosition();
		}

		MoveTreeGame mtg = new MoveTreeGame( cgp );

		ChessGame result = new ChessGame( cghi, mtg, null );

		resetBufferOfGameHeader();

		parseGameMoves( result );

		setGameResult( result );

		result.start();

		return( result );
	}

	protected void setGameResult( ChessGame cg )
	{
		ChessGameHeaderInfo cghi = cg.getChessGameHeaderInfo();

		MoveTreeGame mtg = cg.getMoveTreeGame();

		MoveTreeNode lastMove = getLastMoveOfMainVariation(mtg);

		if( ( lastMove != null ) && ( lastMove.getMove() != null ) )
			setGameResult( cghi, lastMove.getMove().getResultOfGame() );
	}

	protected void setGameResult( ChessGameHeaderInfo cghi, String result )
	{
		if( ( cghi != null ) && ( result != null ) )
		{
			cghi.put( ChessGameHeaderInfo.RESULT_TAG, result );
		}
	}

	protected MoveTreeNode getLastMoveOfMainVariation( MoveTreeGame mtg )
	{
		return( MoveTreeNodeUtils.instance().getLastMoveOfMainVariation(mtg) );
	}

	protected boolean startsFromFirstMoveOfGame( MoveToken firstMoveToken )
	{
		return( firstMoveToken.getNumberOfPly() == 1 );
	}

	protected ChessGameHeaderInfo readChessGameHeader( MoveToken firstMoveToken,
														List<String> bufferOfGameHeaderStringLines ) throws ChessParserException, CancellationException
	{
		ChessGameHeaderInfo result = null;

		_firstGameMoveToken = firstMoveToken;
		String currentGameDescription = getCurrentGameDescription( _firstGameMoveToken );
		if( _tagsExtractor != null )
		{
			result = _tagsExtractor.extractHeaderInfo(bufferOfGameHeaderStringLines);
		}

		if( result == null )
			result = new ChessGameHeaderInfo();

		result.setControlName( currentGameDescription );
		result.setPdfBaseFileName( getPdfBaseFileName() );

//		getPlayerNamesElosAndVariant( bufferOfGameHeaderStringLines, result );
//		getEventSiteDateAndRound( bufferOfGameHeaderStringLines, result );

		return( result );
	}

	protected void resetIndexOfGameHeader()
	{
		_indexOfBufferOfGameHeader = null;
	}
/*
	protected void getPlayerNamesElosAndVariant( List<String> bufferOfGameHeaderStringLines, ChessGameHeaderInfo result )
	{
		if( _tagExtractorConfiguration != null )
		{
			resetIndexOfGameHeader();

			setHeaderParameter( result, ChessGameHeaderInfo.EVENT_TAG,
								_tagExtractorConfiguration.getEventExtractionRegex(),
								bufferOfGameHeaderStringLines );
			setHeaderParameter( result, ChessGameHeaderInfo.SITE_TAG,
								_tagExtractorConfiguration.getSiteExtractionRegex(),
								bufferOfGameHeaderStringLines );
			setHeaderParameter( result, ChessGameHeaderInfo.ROUND_TAG,
								_tagExtractorConfiguration.getRoundExtractionRegex(),
								bufferOfGameHeaderStringLines );
			setHeaderParameter( result, ChessGameHeaderInfo.DATE_TAG,
								_tagExtractorConfiguration.getDateExtractionRegex(),
								bufferOfGameHeaderStringLines );

			int index = -1;
			if( _indexOfBufferOfGameHeader != null )
				index = _indexOfBufferOfGameHeader - 1;

			setHeaderParameterWithIndexPreference( result, ChessGameHeaderInfo.WHITE_TAG,
												_tagExtractorConfiguration.getWhitePlayerExtractionRegex(),
												bufferOfGameHeaderStringLines, index );
			setHeaderParameterWithIndexPreference( result, ChessGameHeaderInfo.BLACK_TAG,
												_tagExtractorConfiguration.getBlackPlayerExtractionRegex(),
												bufferOfGameHeaderStringLines, index );
			setHeaderParameterWithIndexPreference( result, ChessGameHeaderInfo.WHITEELO_TAG,
												_tagExtractorConfiguration.getWhiteEloExtractionRegex(),
												bufferOfGameHeaderStringLines, index );
			setHeaderParameterWithIndexPreference( result, ChessGameHeaderInfo.BLACKELO_TAG,
												_tagExtractorConfiguration.getBlackEloExtractionRegex(),
												bufferOfGameHeaderStringLines, index );
			setHeaderParameterWithIndexPreference( result, ChessGameHeaderInfo.ECO_TAG,
												_tagExtractorConfiguration.getVariantExtractionRegex(),
												bufferOfGameHeaderStringLines, index );
		}
	}

	protected void setHeaderParameterWithIndexPreference( ChessGameHeaderInfo result, String tagname,
										String regex,
										List<String> bufferOfGameHeaderStringLines, int index )
	{
		if( ( result != null ) && !StringFunctions.instance().isEmpty( regex ) )
		{
			try
			{
				if( !StringFunctions.instance().isEmpty( regex ) )
				{
					Pattern pattern = Pattern.compile( regex );

					String value = null;
					if( index >= 0 )
						value = lookForRegex( bufferOfGameHeaderStringLines.get( index ), pattern, index );
//					else
//						value = lookForRegex( bufferOfGameHeaderStringLines, pattern );

					if( value != null )
						result.put( tagname, value );
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	protected void setHeaderParameter( ChessGameHeaderInfo result, String tagname,
										String regex,
										List<String> bufferOfGameHeaderStringLines )
	{
		if( ( result != null ) && !StringFunctions.instance().isEmpty( regex ) )
		{
			try
			{
				String value = lookForRegex( bufferOfGameHeaderStringLines, regex );
				if( value != null )
					result.put( tagname, value );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	protected String lookForRegex( List<String> bufferOfGameHeaderStringLines, String regex )
	{
		String result = null;
		if( !StringFunctions.instance().isEmpty( regex ) )
		{
			Pattern pattern = Pattern.compile( regex );
			result = lookForRegex( bufferOfGameHeaderStringLines, pattern );
		}

		return( result );
	}

	protected String lookForRegex( List<String> bufferOfGameHeaderStringLines, Pattern pattern )
	{
		String result = null;

		int index = 0;
		for( String line: bufferOfGameHeaderStringLines )
		{
			String tmpStr = lookForRegex( line, pattern, index );
			if( tmpStr != null )
				result = tmpStr;

			// if it is not null, we continue looking for a subsequent match in a later line.
			index++;
		}

		return( result );
	}

	protected String lookForRegex( String line, Pattern pattern, int index )
	{
		String result = StringFunctions.instance().regExReplaceWithFirstGroup(pattern, line);

		if( !StringFunctions.instance().isEmpty(result) )
		{
			if( _indexOfBufferOfGameHeader == null )
				_indexOfBufferOfGameHeader = index;
			if( _indexOfBufferOfGameHeader != index )
				_indexOfBufferOfGameHeader = -1;
		}
		else
			result = null;

		return( result );
	}
*/
	// call to this method makes the progress be updated (with the last value), and if cancel button has been clicked, then the process will be cancelled.
	protected int getCurrentPageAndUpdateProgress( ) throws CancellationException
	{
		return( getCurrentPageAndUpdateProgress( _firstGameMoveToken ) );
	}

	protected int getCurrentPageAndUpdateProgress( MoveToken firstMoveToken ) throws CancellationException
	{
//		int currentPageNumber = _rawLex.getPageOfPosition( firstMoveToken.getInitialPosition() );
		int currentPage = firstMoveToken.getPageNumber();

		if( _gup != null )		// third and last time we start the progress.
			_gup.up_updateProgressFromChild( _rawLex.getProgress(currentPage) );

		return( currentPage );
	}

	protected String getCurrentGameDescription( MoveToken firstMoveToken ) throws CancellationException
	{
		int currentPageNumber = getCurrentPageAndUpdateProgress( firstMoveToken );

		if( _lastGamePage != currentPageNumber )
			_indexOfGameInCurrentPage=1;

		_lastGamePage = currentPageNumber;
		String result = String.format("Page:%d Game:%s", currentPageNumber, _indexOfGameInCurrentPage );

		return( result );
	}
/*
	protected MoveToken nextMoveToken() throws ChessParserException, CancellationException
	{
		_nextMoveToken = null;

		do
		{
			next();
			if( _nextToken instanceof MoveToken )
			{
				_nextMoveToken = (MoveToken) _nextToken;
			}
		}
		while( ( _nextMoveToken == null ) && hasNext() );

		return( _nextMoveToken );
	}
*/
	protected MoveToken nextMoveToken() throws ChessParserException, CancellationException
	{
		_nextMoveToken = null;

		do
		{
			nextProperToken();
			if( _nextProperToken instanceof MoveToken )
			{
				_nextMoveToken = (MoveToken) _nextProperToken;
			}
		}
		while( ( _nextMoveToken == null ) && hasNextProperToken() );

		return( _nextMoveToken );
	}
/*
	protected void addLastLineWithMove()
	{
		if( ! _lastLinesWithMoves.contains( _bufferOfGameHeaderStringLines.size() ) )
		{
			if( _lastLinesWithMoves.size() > 1 )
				_lastLinesWithMoves.remove(0);

			_lastLinesWithMoves.add( _bufferOfGameHeaderStringLines.size() );
		}
	}
*/
	protected boolean hasNextProperToken() throws ChessParserException, CancellationException
	{
		boolean result = false;

		if( _nextProperToken == null )
			nextProperToken();

		result = ( _nextProperToken != null );

		return( result );
	}

	protected boolean hasNextMoveToken() throws ChessParserException, CancellationException
	{
		boolean result = false;

		if( _nextMoveToken == null )
			nextMoveToken();

		result = ( _nextMoveToken != null );

		return( result );
	}

	@Override
	public void giveBack( Token token )
	{
		super.giveBack( token );

		_nextProperToken = null;
		_nextMoveToken = null;
	}

	protected boolean doesMoveTokenBelongToThisGame( MoveToken moveToken, int previousNumberOfPly, ListOfSubVariationParents list )
	{
		boolean result = false;

		if( moveToken != null )
		{
			if( moveToken.getNumberOfPly() == previousNumberOfPly )
				result = true;
			else if( moveToken.getNumberOfPly() == (previousNumberOfPly + 1 ) )
				result = true;
			else if( (previousNumberOfPly>0) && ( moveToken.getNumberOfPly() == (previousNumberOfPly + 2 ) ) )
				result = true;			// move to skip.
			else if( previousNumberOfPly == 0 )
				result = true;			// is the first move of the game, so it start in any position.
			else if( moveToken.getNumberOfPly() == 1 )
				result = false;			// if the number of ply is 1, we consider a new game, instead of a variation from the first move.
			else
			{
				ListOfSubVariationParents.Result continuationResult = list.isContinuation(moveToken);
				if( ! continuationResult.getResult().equals( ResultOfScanToAPreviousSubVariant.NOTHING ) )
				{
					result = true;
				}
			}
		}

		return( result );
	}

	protected ResultOfScanToAPreviousSubVariant ask( MoveToken currentMoveToken, MoveTreeNode currentLineNode )
	{
		return( ResultOfScanToAPreviousSubVariant.DISCARD );
	}

	/**
	 * 
	 * @param currentMoveToken
	 * @param listOfAheadTokens
	 * @return							Number of plies discarded.
	 * @throws ChessParserException
	 * @throws CancellationException 
	 */
	protected int discardVariation( MoveToken currentMoveToken, LinkedList<MoveToken> listOfAheadTokens ) 
		throws ChessParserException, CancellationException
	{
		int numberOfPliesDiscarded = 1;
		ListOfSubVariationParents list_osvp = new ListOfSubVariationParents();

		_nextMoveToken = currentMoveToken;
		boolean loop = true;
		int currentPly = _nextMoveToken.getNumberOfPly();

		while( loop && ( nextMoveToken() != null ) )
		{
			MoveToken cMoveToken = _nextMoveToken;
			int nextPly = _nextMoveToken.getNumberOfPly();

			if( listOfAheadTokens != null )
			{
				listOfAheadTokens.addFirst( cMoveToken );
			}

			if( (nextPly != currentMoveToken.getNumberOfPly() ) &&
				doesMoveTokenBelongToThisGame( _nextMoveToken, currentPly, list_osvp ) )
			{
				if( nextPly == ( currentPly + 2 ) )
				{
					continue; // discarded movetoken
				}
				else if( ( currentPly == nextPly ) && ( numberOfPliesDiscarded == 0) )
				{
					numberOfPliesDiscarded++;
					loop = false;		// is the end of the variation (with only one ply).
				}
				else
				{
					numberOfPliesDiscarded++;

					ListOfSubVariationParents.Result continuationResult = list_osvp.isContinuation(_nextMoveToken);
					ResultOfScanToAPreviousSubVariant ros = continuationResult.getResult();

					if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE ) )
					{
						ros = ResultOfScanToAPreviousSubVariant.NEW_VARIATION;
						SubVariantParent svp = list_osvp.getNode(nextPly - 1);
						continuationResult.setSubVariantParent(svp);
					}
					else if( nextPly == ( currentPly + 1) )
					{
						MoveToken nextMoveToken = nextMoveToken();
						if( nextMoveToken != null )
						{
							giveBack( nextMoveToken );

							if( doesMoveTokenBelongToThisGame( nextMoveToken, nextPly, list_osvp ) )
							{
								if( nextPly == nextMoveToken.getNumberOfPly() )
								{
									// in this case, currentMoveToken is a continuation of the lastMove,
									// and as the next move has the same ply number, we must add the parent to the listOfSubVariants
									list_osvp.addFirst(new SubVariantParent( currentPly ) );
									ros = ResultOfScanToAPreviousSubVariant.NOTHING;
								}
								if( nextMoveToken.getNumberOfPly() == (nextPly + 1) )
									ros = ResultOfScanToAPreviousSubVariant.NOTHING;// we suppose that in this case also is a continuation of last move avoiding the continuation of SubVariantParent
							}
						}
					}

					if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION ) )
					{
						currentPly = continuationResult.getSubVariantParent().getPlyNumber() + 1;
						list_osvp.popNewerNodes( currentPly - 1 );
					}
					else if( ros.equals( ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE ) )
					{
						currentPly = continuationResult.getSubVariantParent().getPlyNumber() + 2;
						list_osvp.popNewerNodes( currentPly  - 3 );	// so that the parent is removed.

						MoveToken nextMoveToken = nextMoveToken();
						if( nextMoveToken != null )
						{
							giveBack( nextMoveToken );

							if( nextMoveToken.getNumberOfPly() == nextPly )
								list_osvp.add( currentPly );
						}
					}
					else
						currentPly++;
				}
			}
			else
			{
				loop = false;
			}
		}

		return( numberOfPliesDiscarded );
	}

	/**
	 *		This function discards all subsequent variations of the same level (ply number).
	 *		It returns information for the parser to know some important information
	 *		to take measures for the parsing.
	 *		If a listOfAheadTokens is passed, then it stores all the read tokens for them to
	 *		be returned for them to be able to be parsed again.
	 * 
	 * @param currentMoveToken
	 * @param listOfAheadTokens
	 * @return					
	 * 
	 * @throws ChessParserException
	 * @throws CancellationException 
	 */
	protected ResultOfDiscardVariations dicardAllVariationsOfSamePlyNumber( MoveToken currentMoveToken, LinkedList<MoveToken> listOfAheadTokens )
		 throws ChessParserException, CancellationException
	{
		_nextMoveToken = currentMoveToken;

		boolean firstDiscardedVariationHadOnlyOnePly = false;
		boolean lastDiscardedVariationHadOnlyOnePly = false;
		int numberOfDiscardedVariationsOfSamePly = 0;
		boolean afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly = false;
		int numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation = 0;
		int numberOfDiscardedVariationsWithTwoPlies = 0;
		int numberOfPlyOfNextVariation = -1;
		boolean afterDiscardItWasNewGame = false;

		int numberOfPliesLastVariation = 0;
		while( (_nextMoveToken != null ) &&
			( _nextMoveToken.getNumberOfPly() == currentMoveToken.getNumberOfPly() ) )
		{
			if( numberOfDiscardedVariationsOfSamePly > 1 )
			{
				if( numberOfPliesLastVariation == 1 )
					numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation++;
			}
			else
				firstDiscardedVariationHadOnlyOnePly = ( numberOfPliesLastVariation == 1 );

			numberOfPliesLastVariation = discardVariation( _nextMoveToken, listOfAheadTokens );

			if( numberOfPliesLastVariation == 2 )
				numberOfDiscardedVariationsWithTwoPlies++;

			numberOfDiscardedVariationsOfSamePly++;
		}

		if( _nextMoveToken != null )
			numberOfPlyOfNextVariation = _nextMoveToken.getNumberOfPly();

		if( ( numberOfPlyOfNextVariation == ( currentMoveToken.getNumberOfPly() + 1 ) ) )
		{
			discardVariation( _nextMoveToken, listOfAheadTokens );
			
			if( _nextMoveToken != null )
			{
				if( _nextMoveToken.getNumberOfPly() == currentMoveToken.getNumberOfPly() )
					afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly = true;
			}
		}
		
		if( _nextMoveToken == null )
		{
			afterDiscardItWasNewGame = true;
		}
		else
		{
			afterDiscardItWasNewGame = !doesMoveTokenBelongToThisGame( _nextMoveToken, -1, _listOfSubVariationParents );
		}

		ResultOfDiscardVariations result = new ResultOfDiscardVariations(
											firstDiscardedVariationHadOnlyOnePly,
											(numberOfPliesLastVariation==1),
											numberOfDiscardedVariationsOfSamePly,
											afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly,
											numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation,
											numberOfDiscardedVariationsWithTwoPlies,
											numberOfPlyOfNextVariation,
											afterDiscardItWasNewGame );

		return( result );
	}

	protected void giveBackListOfTokens( List<MoveToken> listOfAheadTokens )
	{
		Iterator<MoveToken> it = listOfAheadTokens.iterator();
		while( it.hasNext() )
		{
			giveBack( it.next() );
		}
	}

	/**
	 *		This function skips all subsequent variations of the same level (ply number).
	 *		It returns information for the parser to know some important information
	 *		to take measures for the parsing.
	 * 
	 * @param currentMoveToken
	 * @return					
	 * 
	 * @throws ChessParserException
	 * @throws CancellationException 
	 */
	protected ResultOfDiscardVariations skipAllVariationsOfSamePlyNumber( MoveToken currentMoveToken )
		 throws ChessParserException, CancellationException
	{
		ResultOfDiscardVariations result = null;
		
		_nextMoveToken = currentMoveToken;

		LinkedList<MoveToken> listOfAheadTokens = new LinkedList<MoveToken>();

		result = dicardAllVariationsOfSamePlyNumber( currentMoveToken, listOfAheadTokens );

		MoveToken lastMt = null;
		if( listOfAheadTokens.size() > 0 )
			listOfAheadTokens.getLast();

		giveBackListOfTokens( listOfAheadTokens );

		_nextMoveToken = lastMt;

		return( result );
	}

	protected ResultOfScanToAPreviousSubVariant getIsNewVariationOrContinuationDisambiguation( MoveToken currentMoveToken )
		 throws ChessParserException, CancellationException
	{
		ResultOfScanToAPreviousSubVariant result = ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE;

		ResultOfDiscardVariations resultOfDiscard = skipAllVariationsOfSamePlyNumber( currentMoveToken );

		if( resultOfDiscard.getAfterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly() )
		{
			result = ResultOfScanToAPreviousSubVariant.NEW_VARIATION;
			if( resultOfDiscard.getNumberOfDiscardedVariationsOfSamePly() == 1 )
				result = ResultOfScanToAPreviousSubVariant.LAST_NEW_VARIATION;
		}
		else if( resultOfDiscard.getNumberOfDiscardedVariationsOfSamePly() == 1 )
		{
			result = ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE;		// we think we can suppose it.
		}
		else if( ! resultOfDiscard.getFirstDiscardedVariationHadOnlyOnePly() &&
				( resultOfDiscard.getNumberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation() == 0 )
			   )
		{
			result = ResultOfScanToAPreviousSubVariant.NEW_VARIATION;
		}
		else if ( resultOfDiscard.getNumberOfPlyOfNextVariation() == ( currentMoveToken.getNumberOfPly() - 1 ) )
		{
			result = ResultOfScanToAPreviousSubVariant.NEW_VARIATION;
		}

		return( result );
	}

	protected void parseGameMoves( ChessGame cg ) throws ChessParserException, CancellationException
	{
		_listOfSubVariationParents = new ListOfSubVariationParents();

//		LinkedList<ChessGameMove> listOfMoves = mtn.getGameMoveList();
//		doMoves( cg, listOfMoves );

		List<MoveTreeNode> listOfNodesForContinuation = new ArrayList<MoveTreeNode>();
		MoveTreeNode currentLineNode = null;
		MoveToken currentMoveToken = null;

		try
		{
			MoveTreeGame mtg = cg.getMoveTreeGame();

			currentLineNode = mtg;

			boolean hasToContinueMainLine = false;

			int previousPly = currentLineNode.getLevel();  // level is identified with the ply number.
			boolean mustDiscardCurrentVariation = false;
			while( hasNextMoveToken() &&
					doesMoveTokenBelongToThisGame( _nextMoveToken, currentLineNode.getLevel(),
													_listOfSubVariationParents )
				)
			{
				currentMoveToken = _nextMoveToken;
				MoveToken nextMoveToken = null;
				mustDiscardCurrentVariation = false;

	/*			if( previousPly == 0 )
				{
					// first move of this game. After the if/elses it will be inserted in the gameTree.
				}
				else
	*/
				if( currentMoveToken.getNumberOfPly() == (previousPly + 2) )
				{
					System.out.println( "Discarding move." );
					nextMoveToken = nextMoveToken();
					while( nextMoveToken != null )
					{
						if( nextMoveToken.getNumberOfPly() == ( previousPly+2 ) )
							nextMoveToken = nextMoveToken();
						else if( nextMoveToken.getNumberOfPly() == previousPly )
						{
							SubVariantParent svp = _listOfSubVariationParents.getNode( currentLineNode.getLevel() - 1 );
							if( ( svp == null ) && ( currentLineNode.getParent() != null ) )
								_listOfSubVariationParents.addFirst( new SubVariantParent( currentLineNode.getParent() ) );
							break;
						}
						else
							break;
					}
					continue;
				}
				else
				{
					ListOfSubVariationParents.Result continuationResult = _listOfSubVariationParents.isContinuation(_nextMoveToken);
					ResultOfScanToAPreviousSubVariant ros = continuationResult.getResult();

					if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE ) )
					{
						SubVariantParent svpCont = _listOfSubVariationParents.getNode( currentMoveToken.getNumberOfPly() - 2 );
						if( ( svpCont != null ) && ( svpCont.getSubVariantParentNode() != null ) &&
							( listOfNodesForContinuation.contains( svpCont.getSubVariantParentNode() ) )
						  )
						{
							listOfNodesForContinuation.remove( svpCont.getSubVariantParentNode() );
							continuationResult.setSubVariantParent( svpCont );
							ros = ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE;
						}
						else
						{
							ros = getIsNewVariationOrContinuationDisambiguation( _nextMoveToken );

							if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE ) )
								ros = ask( currentMoveToken, currentLineNode );		// if it is ambiguous, we must ask.
							else 
							{
								int plyNumberToFetch = -1;
								if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION ) )
									plyNumberToFetch = currentMoveToken.getNumberOfPly() - 1;
								else if( ros.equals( ResultOfScanToAPreviousSubVariant.LAST_NEW_VARIATION ) )
								{
									plyNumberToFetch = currentMoveToken.getNumberOfPly() - 1;
									SubVariantParent svp = _listOfSubVariationParents.getNode( plyNumberToFetch - 1 );
									if( ( svp != null ) && (svp.getSubVariantParentNode() != null ) )
									{
										listOfNodesForContinuation.add( svp.getSubVariantParentNode() );
									}
									ros = ResultOfScanToAPreviousSubVariant.NEW_VARIATION;
								}
								else if( ros.equals( ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE ) )
									plyNumberToFetch = currentMoveToken.getNumberOfPly() - 2;

								SubVariantParent svp = _listOfSubVariationParents.getNode( plyNumberToFetch );
								continuationResult.setSubVariantParent( svp );
							}
						}
					}
					else if( ( previousPly == 0 ) || ( currentMoveToken.getNumberOfPly() == (previousPly + 1) ) )
					{
						boolean asked = false;
						nextMoveToken = nextMoveToken();
						if( nextMoveToken != null )
						{
							giveBack( nextMoveToken );

							if( doesMoveTokenBelongToThisGame( nextMoveToken, currentMoveToken.getNumberOfPly(),
																_listOfSubVariationParents )
								)
							{
								if( ros.equals( ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE ) )
								{
									ros = ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE;

									if( hasToContinueMainLine )
									{
										ros = ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE;
										hasToContinueMainLine = false;
									}
									else if( currentMoveToken.getNumberOfPly() == nextMoveToken.getNumberOfPly() )
									{
										ResultOfDiscardVariations resultOfDiscard = skipAllVariationsOfSamePlyNumber( currentMoveToken );

										if( resultOfDiscard.getAfterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly() ||
											( ! resultOfDiscard.getFirstDiscardedVariationHadOnlyOnePly() &&
												( resultOfDiscard.getNumberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation() == 0 )
											 )
										  )
										{
											// these are cases of continuation of the previous move, being the first NEW_VARIANT. (not a continuation of the preceding SubVariantParent).
											_listOfSubVariationParents.addFirst( new SubVariantParent( currentLineNode ) );
											ros = ResultOfScanToAPreviousSubVariant.NOTHING;
										}
										else
										{
											ros = ask( currentMoveToken, currentLineNode );
											if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION ) )
											{
												ros = ResultOfScanToAPreviousSubVariant.NOTHING;
											}
											asked = true;
										}
									}
									else if( currentLineNode.getLevel() == nextMoveToken.getNumberOfPly() )
									{
										// this case is a normal case. It is a continuation of the previous move, and it is not a new Parent to the listOfSubvariat
										ros = ResultOfScanToAPreviousSubVariant.NOTHING;
									}
									else
									{
										// this case is complex.
										// we will do some checkings to see if we can avoid the ambiguity.

										if( nextMoveToken.getNumberOfPly() == ( currentMoveToken.getNumberOfPly() + 1 ) )
										{
											ResultOfDiscardVariations rodv = skipAllVariationsOfSamePlyNumber( currentMoveToken );
											if( ( ( !rodv.getFirstDiscardedVariationHadOnlyOnePly() ) &&
												  ( rodv.getNumberOfDiscardedVariationsOfSamePly() > 1 ) )
											   )
											{
												ros = ResultOfScanToAPreviousSubVariant.NOTHING;
											}
											else if( ( ( rodv.getNumberOfDiscardedVariationsOfSamePly() == 1 ) &&
														rodv.getAfterDiscardItWasNewGame() ) ||
													 ( ( !rodv.getFirstDiscardedVariationHadOnlyOnePly() &&
															rodv.getLastDiscardedVariationHadOnlyOnePly() &&
															( rodv.getNumberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation() == 0 ) ) &&
														rodv.getAfterDiscardItWasNewGame() )
													)
											{
												ros = ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE;
											}
											else if( rodv.getNumberOfPlyOfNextVariation() == ( currentMoveToken.getNumberOfPly() - 1 ) )
											{
												ros = ResultOfScanToAPreviousSubVariant.NOTHING;
											}
										}

										// this case is ambiguous, so we must ask.   As asked = false, it will be asked below.
									}
								}
								else if( currentMoveToken.getNumberOfPly() == nextMoveToken.getNumberOfPly() )
								{
									nextMoveToken = nextMoveToken();

									MoveToken nextNextMoveToken = nextMoveToken();
									if( ( nextNextMoveToken != null ) &&
										( nextNextMoveToken.getNumberOfPly() == ( nextMoveToken.getNumberOfPly() + 1 ) )
										)
									{
										ResultOfDiscardVariations rodv = skipAllVariationsOfSamePlyNumber( nextNextMoveToken );
										if( rodv.getFirstDiscardedVariationHadOnlyOnePly() )
										{
											ChessGameMove cgm = getChessGameMove( cg, currentMoveToken );
											simpleInsert( currentLineNode, cgm, currentMoveToken.getNumberOfPly() );
											// this case is ambiguous
											break;		// to change the game.
										}
									}
									giveBack( nextNextMoveToken );
									giveBack( nextMoveToken );

									// normal case of new variation without ambiguity
									_listOfSubVariationParents.addFirst( new SubVariantParent( currentLineNode ) );
									ros = ResultOfScanToAPreviousSubVariant.NOTHING;
								}
							}
						}

						if( !asked && ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE ) )
						{
							ros = ask( currentMoveToken, currentLineNode );
							if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION ) )
							{
								ros = ResultOfScanToAPreviousSubVariant.NOTHING;
							}
						}
					}

					if( ros.equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION ) )
					{
						currentLineNode = continuationResult.getSubVariantParent().getSubVariantParentNode();

						hasToContinueMainLine = isLastNewVariation(currentMoveToken);

						_listOfSubVariationParents.popNewerNodes( currentLineNode.getLevel() );
						//doMoves( cg, currentLineNode.getGameMoveList() );
					}
					else if( ros.equals( ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE ) )
					{
						currentLineNode = continuationResult.getSubVariantParent().getSubVariantParentNode().getChild(0);
						//doMoves( cg, currentLineNode.getGameMoveList() );

	//					if( continuationResult.getResult().equals( ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE ) )
	//						currentLineNode = currentLineNode.getParent();
						_listOfSubVariationParents.popNewerNodes( currentLineNode.getLevel() - 2 );	// so that the parent is removed.

						nextMoveToken = nextMoveToken();
						if( nextMoveToken != null )
						{
							giveBack( nextMoveToken );

							if( nextMoveToken.getNumberOfPly() == currentMoveToken.getNumberOfPly() )
							{
								nextMoveToken = nextMoveToken();

								MoveToken nextNextMoveToken = nextMoveToken();
								if( ( nextNextMoveToken != null ) &&
									( nextNextMoveToken.getNumberOfPly() == ( nextMoveToken.getNumberOfPly() + 1 ) )
									)
								{
									ResultOfDiscardVariations rodv = skipAllVariationsOfSamePlyNumber( nextNextMoveToken );
									if( rodv.getFirstDiscardedVariationHadOnlyOnePly() )
									{
										ChessGameMove cgm = getChessGameMove( cg, currentMoveToken );
										simpleInsert( currentLineNode, cgm, currentMoveToken.getNumberOfPly() );
										// this case is ambiguous
										break;		// to change the game.
									}
								}
								giveBack( nextNextMoveToken );
								giveBack( nextMoveToken );

								// normal case of new variation without ambiguity
								_listOfSubVariationParents.addFirst( new SubVariantParent( currentLineNode ) );
								ros = ResultOfScanToAPreviousSubVariant.NOTHING;
							}
						}
					}
					else if( ros.equals( ResultOfScanToAPreviousSubVariant.DISCARD ) )
					{
						mustDiscardCurrentVariation = true;
					}
				}

				if( mustDiscardCurrentVariation )
				{
					SubVariantParent svp = _listOfSubVariationParents.getNode( currentMoveToken.getNumberOfPly() - 2 );
					if( svp != null )
					{
						if( svp.getSubVariantParentNode().isMainLine() )
						{
							_nextMoveToken = currentMoveToken;
							break;
						}
					}
					dicardAllVariationsOfSamePlyNumber( currentMoveToken, null );
				}
				else
				{
	/*
					if( ( mtg.getLevel() + 1 ) == currentMoveToken.getNumberOfPly() )
					{
						_listOfSubVariationParents.clear();
						_listOfSubVariationParents.add( mtg );
						currentLineNode = mtg;
					}
	*/
					int currentPly = currentMoveToken.getNumberOfPly();

					ChessGameMove cgm = getChessGameMove( cg, currentMoveToken );

					simpleInsert( currentLineNode, cgm, currentPly );

					previousPly = currentPly;

					//doMove( cg, cgm, cgm.getMoveToken() );

						if( currentMoveToken.getComment() != null )
						{
							currentLineNode.setComment( currentMoveToken.getComment() );
						}

	/*
					Iterator<NAG> it = currentMoveToken.nagIterator();
					while( it.hasNext() )
					{
						currentLineNode.addNag( it.next() );
					}
		*/
					nextMoveToken();
				}
			}
		}
		catch( CancellationException ce )
		{
			throw( ce );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			currentLineNode.remove();	// in case of exception doing moves, new game.
//			giveBack( currentMoveToken );	// we comment it to discard the last move to avoid a possible infinite looping.
			_nextMoveToken = null;		// we remove the _nextMoveToken
		}

		currentMoveToken = _nextMoveToken;
		MoveToken nextMoveToken = nextMoveToken();
		if( ( currentMoveToken == null ) || ! currentMoveToken.equals( nextMoveToken ) )
		{
			giveBack( nextMoveToken );
		}
		_nextMoveToken = currentMoveToken;
	}

	protected boolean isLastNewVariation( MoveToken currentMoveToken )
	{
		boolean result = false;

		try
		{
			ResultOfDiscardVariations rodv = skipAllVariationsOfSamePlyNumber( currentMoveToken );

			int discardedVariations = rodv.getNumberOfDiscardedVariationsOfSamePly();
			
			if( !rodv.getFirstDiscardedVariationHadOnlyOnePly() &&
					rodv.getLastDiscardedVariationHadOnlyOnePly() &&
					( rodv.getNumberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation() == (discardedVariations-2) ) &&
					rodv.getAfterDiscardItWasNewGame() )
			{
				result = true;
			}
			else if( ( discardedVariations == 1 ) &&
					! rodv.getFirstDiscardedVariationHadOnlyOnePly() &&
					( rodv.getNumberOfPlyOfNextVariation() != ( currentMoveToken.getNumberOfPly() + 1 ) ) &&
					rodv.getAfterDiscardItWasNewGame() )
			{
				SubVariantParent svp_continuationOfMainLine = _listOfSubVariationParents.getNode( currentMoveToken.getNumberOfPly() - 1 );

				if( svp_continuationOfMainLine != null )
				{
					MoveToken preservedMoveToken = currentMoveToken;

					MoveToken nextMoveToken = nextMoveToken();

					ResultOfDiscardVariations rodv1 = skipAllVariationsOfSamePlyNumber( nextMoveToken );

					int discardedVariations1 = rodv.getNumberOfDiscardedVariationsOfSamePly();

					if( discardedVariations1 == 1 )
					{
						result = true;
					}
					
					giveBack( nextMoveToken );

					_nextMoveToken = preservedMoveToken;
				}
				
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}

		return( result );
	}
	
	protected Token getIfNotMoveProperToken( Token token )
	{
		Token result = null;
		if( token != null )
		{
			switch( token.getTokenId() )
			{
				case RESULT:
					result = token;
				break;
			}
		}

		return( result );
	}

	protected Token getLastRetrievedToken()
	{
		Token result = null;
		if( !_lastRetrievedTokens.isEmpty() )
			result = _lastRetrievedTokens.getLast();

		return( result );
	}

	protected void addToLastRetrievedTokens( Token token )
	{
		if( !Objects.equals( token, getLastRetrievedToken() ) )
			_lastRetrievedTokens.addLast(token);

		while( _lastRetrievedTokens.size() > 2 )
			_lastRetrievedTokens.removeFirst();
	}

	protected Token nextProperToken() throws ChessParserException, CancellationException
	{
		_nextProperToken = null;
		_nextMoveToken = null;

		do
		{
			next();
			if( _nextToken != null )
				addToLastRetrievedTokens( _nextToken );

			if( _nextToken instanceof MoveToken )
			{
				_nextProperToken = _nextToken;
				_nextMoveToken = (MoveToken) _nextToken;

//				addLastLineWithMove();
			}
			else if( _nextToken != null )
			{
				_nextProperToken = getIfNotMoveProperToken( _nextToken );
			}
		}
		while( ( _nextProperToken == null ) && hasNext() );

		return( _nextProperToken );
	}

	public static class ResultOfDiscardVariations
	{
		protected boolean _firstDiscardedVariationHadOnlyOnePly;
		protected boolean _lastDiscardedVariationHadOnlyOnePly;
		protected int _numberOfDiscardedVariationsOfSamePly;
		protected boolean _afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly;
		protected int _numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation;
		protected int _numberOfDiscardedVariationsWithTwoPlies;
		protected int _numberOfPlyOfNextVariation;
		protected boolean _afterDiscardItWasNewGame;

		public ResultOfDiscardVariations( boolean firstDiscardedVariationHadOnlyOnePly,
											boolean lastDiscardedVariationHadOnlyOnePly,
											int numberOfDiscardedVariationsOfSamePly,
											boolean afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly,
											int numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation,
											int numberOfDiscardedVariationsWithTwoPlies,
											int numberOfPlyOfNextVariation,
											boolean afterDiscardItWasNewGame
											)
		{
			_firstDiscardedVariationHadOnlyOnePly = firstDiscardedVariationHadOnlyOnePly;
			_lastDiscardedVariationHadOnlyOnePly = lastDiscardedVariationHadOnlyOnePly;
			_numberOfDiscardedVariationsOfSamePly = numberOfDiscardedVariationsOfSamePly;
			_afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly = afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly;
			_numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation =
				numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation;
			_numberOfDiscardedVariationsWithTwoPlies = numberOfDiscardedVariationsWithTwoPlies;
			_numberOfPlyOfNextVariation = numberOfPlyOfNextVariation;
			_afterDiscardItWasNewGame = afterDiscardItWasNewGame;
		}

		public boolean getFirstDiscardedVariationHadOnlyOnePly()
		{
			return( _firstDiscardedVariationHadOnlyOnePly );
		}

		public int getNumberOfDiscardedVariationsOfSamePly()
		{
			return( _numberOfDiscardedVariationsOfSamePly );
		}

		public boolean getAfterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly()
		{
			return( _afterANextPlyVariationThereWasAnotherVariationOfTheOriginalPly );
		}

		public int getNumberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation()
		{
			return( _numberOfDiscardedVariationsWithOnlyOnePlyWithoutTakingIntoAccountTheFirstAndLastDiscardedVariation );
		}

		public int getNumberOfDiscardedVariationsWithTwoPlies()
		{
			return( _numberOfDiscardedVariationsWithTwoPlies );
		}

		public int getNumberOfPlyOfNextVariation()
		{
			return( _numberOfPlyOfNextVariation );
		}

		public boolean getAfterDiscardItWasNewGame()
		{
			return( _afterDiscardItWasNewGame );
		}

		public boolean getLastDiscardedVariationHadOnlyOnePly()
		{
			return( _lastDiscardedVariationHadOnlyOnePly );
		}
	}

	protected static class SubVariantParent
	{
		protected MoveTreeNode _subVariantParentNode = null;
		protected int _plyNumber = -1;

		public SubVariantParent( MoveTreeNode subVariantParentNode )
		{
			setSubVariantParentNode( subVariantParentNode );
		}

		public SubVariantParent( int plyNumber )
		{
			_plyNumber = plyNumber;
		}

		public void setSubVariantParentNode( MoveTreeNode subVariantParentNode )
		{
			_subVariantParentNode = subVariantParentNode;
			_plyNumber = subVariantParentNode.getLevel();
		}

		public MoveTreeNode getSubVariantParentNode()		{	return( _subVariantParentNode );	}
		public int getPlyNumber()
		{
			if( ( _plyNumber <= 0 ) && ( _subVariantParentNode != null ) )
			{
				_plyNumber = _subVariantParentNode.getLevel();
			}
			return( _plyNumber );
		}
	}

	public static enum ResultOfScanToAPreviousSubVariant
	{
		NOTHING,
		CONTINUATION_OF_MAIN_LINE,
		NEW_VARIATION,
		LAST_NEW_VARIATION,
		NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE,
		DISCARD
	}

	protected static class ListOfSubVariationParents extends LinkedList<SubVariantParent>
	{
		public static class Result
		{
			protected ResultOfScanToAPreviousSubVariant _result;
			protected SubVariantParent _subVariantParent;

			public Result( ResultOfScanToAPreviousSubVariant result, SubVariantParent svp )
			{
				_result = result;
				_subVariantParent = svp;
			}

			public ResultOfScanToAPreviousSubVariant getResult()
			{
				return( _result );
			}

			public SubVariantParent getSubVariantParent()
			{
				return( _subVariantParent );
			}
			
			public void setSubVariantParent( SubVariantParent svp )
			{
				_subVariantParent = svp;
			}
		}

		public void popNewerNodes( int plyNumber )
		{
			ListIterator<SubVariantParent> it = listIterator(0);
			
			boolean iterate = true;
			while( iterate && it.hasNext() )
			{
				SubVariantParent svp = it.next();
				if( svp.getPlyNumber() > plyNumber )
					it.remove();
				else
					iterate = false;
			}
		}
		
		public void add( MoveTreeNode mtn )
		{
			SubVariantParent svp = new SubVariantParent( mtn );
			add( svp );
		}

		public void add( int plyNumber )
		{
			SubVariantParent svp = new SubVariantParent( plyNumber );
			add( svp );
		}

		public SubVariantParent getNode( int plyNumber )
		{
			SubVariantParent result = null;

			Iterator<SubVariantParent> it = iterator();
			while( it.hasNext() && ( result==null ) )
			{
				SubVariantParent current = it.next();
				int current_plyNumber = current.getPlyNumber();
				
				if( current_plyNumber == plyNumber )
					result = current;
			}

			return( result );
		}

		public Result isContinuation( MoveToken mt )
		{
			ResultOfScanToAPreviousSubVariant sostsv = ResultOfScanToAPreviousSubVariant.NOTHING;
			SubVariantParent svp = null;

			int nextPlyNumber = mt.getNumberOfPly();

			SubVariantParent svp_newVariation = getNode( nextPlyNumber - 1 );
			SubVariantParent svp_continuationOfMainLine = getNode( nextPlyNumber - 2 );

			if( ( svp_newVariation != null ) && (svp_continuationOfMainLine!=null) )
				sostsv = ResultOfScanToAPreviousSubVariant.NEW_VARIATION_OR_CONTINUATION_OF_MAIN_LINE;
			else if( svp_newVariation != null )
			{
				sostsv = ResultOfScanToAPreviousSubVariant.NEW_VARIATION;
				svp = svp_newVariation;
			}
			else if( svp_continuationOfMainLine != null )
			{
				sostsv = ResultOfScanToAPreviousSubVariant.CONTINUATION_OF_MAIN_LINE;
				svp = svp_continuationOfMainLine;
			}

			Result result = new Result( sostsv, svp );

			return( result );
		}
	}

	protected static ChessStringsConf getChessStrConf()
	{
		return( ChessStringsConf.instance() );
	}

	protected void addImageToRecognizeOrToTrain( MoveTreeNode mtn, int numberOfPly)
	{
		InputImage image = _rawLex.getInputImage();

		if( ( image == null ) && ( _postponedInputImage != null ) )
			image = _postponedInputImage;

		if( image != null )
		{
			boolean sameSegment = ( _rawLex.getLastTextSegmentKey() == image.getSegmentKey() );
			_postponedInputImage = null;
			if( sameSegment )
			{
				if( isImageGap( mtn ) )
					addPositionToMoveTreeNode( mtn, numberOfPly, image );
				else
					_postponedInputImage = image;
			}
			else
			{
				image = getReallyLastInputImage();
				if( image != null )
					addPositionToMoveTreeNode( mtn, numberOfPly, image );
			}
		}
	}

	protected InputImage getReallyLastInputImage()
	{
		InputImage result = null;
		Iterator<Token> it = _lastRetrievedTokens.descendingIterator();
		while( ( result == null ) && it.hasNext() )
		{
			Token item = it.next();
			if( item instanceof ImageToken )
				result = ( (ImageToken) item ).getInputImage();
		}

		return( result );
	}

	protected void addPositionToMoveTreeNode( MoveTreeNode mtn, int numberOfPly, InputImage image )
	{
		if( ( mtn instanceof MoveTreeGame ) )
		{
			if( numberOfPly > 1 ) 
				_controller.putNewPendingImagePosition( image, mtn.getChessGame() );
		}
		else if( mtn.isMainLine() &&
				( mtn.getLevel() < 46 ) &&
				MoveTreeNodeUtils.instance().startsFromInitialPosition( mtn ) )
		{
//						String fen = MoveTreeNodeUtils.instance().getFen( mtn.getEndOfMainLine() );
			String fen = MoveTreeNodeUtils.instance().getFen( mtn );
			if( fen != null )
			{
				_controller.newImagePositionForTraining(fen.split( "\\s" )[0], image);
			}
		}
	}

	protected MoveToken getMoveToken( MoveTreeNode mtn )
	{
		return( getMoveToken( (ChessGameMove) NullFunctions.instance().getIfNotNull(mtn, o -> o.getMove() ) ) );
	}

	protected MoveToken getMoveToken( ChessGameMove cgm )
	{
		return( NullFunctions.instance().getIfNotNull(cgm, o -> o.getMoveToken() ) );
	}

	protected boolean isImageGap( MoveTreeNode mtn )
	{
		return( isImageGap( getMoveToken( mtn ) ) );
	}

	public boolean isImageGap( ChessGameMove cgm )
	{
		return( isImageGap( getMoveToken( cgm ) ) );
	}

	protected boolean isImageGap( Token moveToken )
	{
		boolean result = ( _lexicalAnalyser.getNumberOfPendingPreTokensOrTokensWithLineNumberLessThanLastCreatedPretoken() == 0 );
		result = result &&
			( ( moveToken == _nextToken ) ||
				( _nextToken.getLineNumber() == _lexicalAnalyser.getLineNumberOfLastReadPretoken() ) );

		return( result );
	}

	@Override
	public MoveTreeNode simpleInsert(MoveTreeNode currentLineNode, ChessGameMove cgm,
										int numberOfPly )
	{
		MoveTreeNode result = super.simpleInsert( currentLineNode, cgm, numberOfPly );

		addImageToRecognizeOrToTrain( currentLineNode, numberOfPly );

		return( result );
	}

	public MoveTreeNode insertFirst(MoveTreeNode currentLineNode, ChessGameMove cgm,
										int numberOfPly )
	{
		MoveTreeNode result = super.insertFirst( currentLineNode, cgm, numberOfPly );

		addImageToRecognizeOrToTrain( currentLineNode, numberOfPly );

		return( result );
	}
}
