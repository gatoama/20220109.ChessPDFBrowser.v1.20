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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.impl;

import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGameHeaderInfo;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.BlockRegexBuilder;
import com.frojasg1.chesspdfbrowser.engine.tags.regex.RegexExpressionResult;
import com.frojasg1.chesspdfbrowser.model.regex.LineModel;
import com.frojasg1.chesspdfbrowser.model.regex.ProfileModel;
import com.frojasg1.chesspdfbrowser.model.regex.TagReplacementModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.chesspdfbrowser.model.regex.whole.items.ListOfRegexWholeFiles;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.string.StringFunctions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TagsExtractorImpl implements TagsExtractor
{
	protected static final int MAX_NUMBER_OF_LINES_TO_BROWSE = 20;

	protected static final String _saPIECES_OF_ALL_LANGUAGES = "KDVSJTLPQRBVFHWGCN";
	protected static final String _saREGEXP_FOR_GAME_MOVE = "((["+ _saPIECES_OF_ALL_LANGUAGES + "])?([a-h]?)([1-8]?))?([xX]?)([a-h][1-8])([=]?[" + _saPIECES_OF_ALL_LANGUAGES + "])?(\\+|\\+\\+|#)?";
	protected static final Pattern _saPatternForMove = Pattern.compile( _saREGEXP_FOR_GAME_MOVE );
/*
	_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( CZECH, "KDVSJ" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( DANISH, "KDTLS" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( DUTCH, "KDTLP" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( ENGLISH, "KQRBN" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( ESTONIAN, "KLVOR" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( FINNISH, "KDTLR" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( FRENCH, "RDTFC" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( GERMAN, "KDTLS" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( HUNGARIAN, "KVBFH" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( ICELANDIC, "KDHBR" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( ITALIAN, "RDTAC" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( NORWEGIAN, "KDTLS" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( POLISH, "KHWGS" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( PORTUGUESE, "RDTBC" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( ROMANIAN, "RDTNC" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( SPANISH, "RDTAC" ) );
		_vector.add( new ChessLanguageConfiguration.LanguageConfigurationData( SWEDISH, "KDTLS" ) );
*/

	protected RegexWholeFileModel _regexWholeContainer = null;

	protected ListOfRegexWholeFiles _listOfRegexFiles = null;

	protected Map<RegexExpressionResultMapKey, RegexExpressionResult> _cacheOfExpressionResult = new HashMap<>();
	protected Map<String, Pattern> _cacheOfPatterns = new HashMap<>();
/*
	public TagsExtractorImpl( RegexWholeFileModel regexWholeContainer )
	{
		_regexWholeContainer = regexWholeContainer;
	}
*/
	public TagsExtractorImpl( ListOfRegexWholeFiles listOfRegexFiles )
	{
		_listOfRegexFiles = listOfRegexFiles;
	}

	@Override
	public ChessGameHeaderInfo extractHeaderInfo(List<String> bufferOfGameHeaderStringLines) throws ChessParserException, CancellationException
	{
		ChessGameHeaderInfo result = null;

		for( RegexWholeFileModel rwc: _listOfRegexFiles.getCollectionOfModelItems() )
		{
			_regexWholeContainer = rwc;
			for( ProfileModel profile: rwc.getSetOfProfiles() )
			{
				ChessGameHeaderInfo partial = null;

				if( profile.isActive() )
					partial = getGameHeaderInfo( profile, bufferOfGameHeaderStringLines,
																MAX_NUMBER_OF_LINES_TO_BROWSE );

				result = chooseBest( result, partial );
			}
		}

		return( result );
	}
	
	protected ChessGameHeaderInfo chooseBest( ChessGameHeaderInfo first,
												ChessGameHeaderInfo second )
	{
		ChessGameHeaderInfo result = null;
		if( first == null )
			result = second;
		else if( second == null )
			result = first;
		else
		{
			if( first.size() > second.size() )
				result = first;
			else
				result = second;
		}

		return( result );
	}

	protected int getNumberOfOptionalLines( ProfileModel profile )
	{
		int result = 0;

		if( profile != null )
			for( LineModel lineModel: profile.getListOfLines() )
			{
				if( lineModel.isOptional() )
					result++;
			}

		return( result );
	}

	protected ChessGameHeaderInfo getGameHeaderInfo( ProfileModel profile,
										List<String> bufferOfGameHeaderStringLines,
										int maxNumberOfLinesToBrowse )
	{
		ChessGameHeaderInfo result = null;

		int numOfOptionalLines = getNumberOfOptionalLines( profile );

		int to = 1 << numOfOptionalLines;
		for( int ii=0; ii<to; ii++ )
		{
			List<LineModel> list = getLinesWithOptionals( profile, ii );

			ChessGameHeaderInfo partial = getGameHeaderInfo( list, bufferOfGameHeaderStringLines,
																maxNumberOfLinesToBrowse );

			result = chooseBest( result, partial );
		}

		if( result != null )
			result.setProfileModel(profile);

		return( result );
	}

	protected List<LineModel> getLinesWithOptionals( ProfileModel profile, int variation )
	{
		List<LineModel> result = new ArrayList<>();

		int weight = 1;
		for( LineModel lineModel: profile.getListOfLines() )
		{
			if( !lineModel.isOptional() || ( ( variation & weight)  > 0 ) )
			{
				result.add( lineModel );

				if( lineModel.isOptional() )
					weight = weight << 1;
			}
		}

		return( result );
	}

	protected ChessGameHeaderInfo getGameHeaderInfo( List<LineModel> lineModelList,
										List<String> bufferOfGameHeaderStringLines,
										int maxNumberOfLinesToBrowse )
	{
		ChessGameHeaderInfo result = null;

		int startOfMatchedLocation = bufferOfGameHeaderStringLines.size() - lineModelList.size();

		while( startOfMatchedLocation >= 0 )
		{
			startOfMatchedLocation = findLastMatchingLocation( lineModelList, bufferOfGameHeaderStringLines,
																	maxNumberOfLinesToBrowse,
																	startOfMatchedLocation );

			if( startOfMatchedLocation >= 0 )
			{
				ChessGameHeaderInfo tmpHeader = new ChessGameHeaderInfo();

				int offset = 0;
				for( LineModel lineModel: lineModelList )
				{
					String line = bufferOfGameHeaderStringLines.get( startOfMatchedLocation + offset );
/*
					String expression = lineModel.getSynchronizationRegexModel().getExpression();
					for( TagReplacementModel tagRepModel: lineModel.getMap().values() )
					{
						Pattern pattern = getPattern( expression, tagRepModel.getBlockToReplaceWith() );

						String tagResult = replace( pattern, line );

						if( tagResult != null )
							tmpHeader.put( tagRepModel.getTagName(), tagResult );
					}
*/
					Map<String, String> tagValues = getTagValues( line, lineModel );
					for( Map.Entry<String, String> pair: tagValues.entrySet() )
						tmpHeader.put( pair.getKey(), pair.getValue() );

					offset++;
				}

				if( hasToDiscard( tmpHeader ) )
					tmpHeader = null;

				result = chooseBest( result, tmpHeader );

				startOfMatchedLocation--;
			}
		}

		return( result );
	}

	protected List<String> getBlocksToReplaceWith( LineModel lineModel )
	{
		List<String> result = new ArrayList<>();

		for( TagReplacementModel trm: lineModel.getMap().values() )
			result.add( trm.getBlockToReplaceWith() );

		return( result );
	}

	protected Map<String, String> getLineTagMap( String line, LineModel lineModel,
													RegexExpressionResult rer )
	{
		Map<String, String> result = null;

		if( rer != null )
		{
			Pattern pattern = rer.getPattern();
			Matcher matcher = pattern.matcher(line);
			if( matcher.matches() )
			{
				result = new HashMap<>();

				matcher.find(0);
				List<String> replacementList = rer.getReplacementList();
				for( TagReplacementModel trm: lineModel.getMap().values() )
				{
					int index = replacementList.indexOf( trm.getBlockToReplaceWith() );
					if( index >= 0 )
					{
						String tagName = trm.getTagName();
						String tagValue = getMatchedValue( matcher, index, replacementList.size() );
						if( checkTagValue( tagName, tagValue ) )
						{
							result.put( tagName, tagValue );
						}
					}
				}
			}
		}

		return( result );
	}

	protected String getMatchedValue( Matcher matcher, int index, int numElems )
	{
		String result = null;

		int to = matcher.groupCount();
		for( int ii=index+1; (result==null) && (ii <= to); ii+=numElems)
			result = matcher.group( ii );

		return( result );
	}

	protected boolean checkTagValue( String tagName, String tagValue )
	{
		boolean result = ( tagValue != null ) && ! tagValue.isEmpty();

		if( result )
		{
			if( tagName.equals( ChessGameHeaderInfo.EVENT_TAG ) )
			{
				if( countDigits( tagValue ) > 3 )
					result = false;
			}
			else if( tagName.equals( ChessGameHeaderInfo.SITE_TAG ) )
			{
				if( countDigits( tagValue ) > 3 )
					result = false;
			}
			else if( tagName.equals( ChessGameHeaderInfo.DATE_TAG ) )
			{
				if( tagValue.length() < 3 )
					result = false;
			}
			else if( tagName.equals( ChessGameHeaderInfo.WHITE_TAG ) ||
						tagName.equals( ChessGameHeaderInfo.BLACK_TAG ) )
			{
				if( tagValue.length() < 4 )
					result = false;
				int digitCount = countDigits( tagValue );
				if( ( digitCount > 4 ) ||
					( digitCount > ( tagValue.length() - digitCount ) )
					)
				{
					result = false;
				}
			}
		}

		return( result );
	}

	protected int countDigits( String str )
	{
		return( StringFunctions.instance().countNumChars( str, "01234567890" ) );
	}

	protected Map<String, String> getTagValues( String line, LineModel lineModel )
	{
		Map<String, String> result = new HashMap<>();

		String expression = lineModel.getSynchronizationRegexModel().getExpression();
		List<String> list = getBlocksToReplaceWith( lineModel );
		int maxNumOfCombinations = 1;
		for( int ii=0; ii<maxNumOfCombinations; ii++ )
		{
			RegexExpressionResult rer = getExpressionResult( expression, list, ii );

			Map<String, String> tmpResult = getLineTagMap( line, lineModel, rer );

			result = chooseBest( result, tmpResult );

			if( maxNumOfCombinations == 1 )
				maxNumOfCombinations = 1 << rer.getNumberOfOptionalBlocks();
		}

		return( result );
	}

	protected Map<String, String> chooseBest( Map<String, String> first,
												Map<String, String> second )
	{
		Map<String, String> result = null;
		if( first == null )
			result = second;
		else if( second == null )
			result = first;
		else
		{
			if( first.size() > second.size() )
				result = first;
			else
				result = second;
		}

		return( result );
	}

	protected boolean hasToDiscard( ChessGameHeaderInfo headerInfo )
	{
		boolean result = true;
		
		if( ( headerInfo != null ) && ( headerInfo.size() > 0 ) )
		{
			if( ( headerInfo.get( ChessGameHeaderInfo.WHITE_TAG ) == null ) ||
					( headerInfo.get( ChessGameHeaderInfo.BLACK_TAG ) == null )
			  )
			{
				result = true;
			}
			else
			{
				int numberOfMoves = countNumberOfMoves( headerInfo );
				int numberOfSpaces = countNumberOfSpaces( headerInfo );

				int size = headerInfo.size();

				result = ( (numberOfSpaces / size ) >= 3 ) || ( numberOfMoves > 0 ) ||
						( numberOfMoves == 1 ) && ( ( ( 2 * numberOfSpaces ) / ( 5 * size ) ) >= 1 );
			}
		}

		return( result );
	}

	protected int countNumberOfSpaces( ChessGameHeaderInfo headerInfo )
	{
		int result = 0;
		for( String value: headerInfo.values() )
			result += numberOfSpaces( value );

		return( result );
	}

	protected int countNumberOfMoves( ChessGameHeaderInfo headerInfo )
	{
		int result = 0;
		for( String value: headerInfo.values() )
			result += numberOfMoves( value );

		return( result );
	}

	protected int numberOfMoves( String value )
	{
		int result = 0;
		Matcher matcher = _saPatternForMove.matcher(value);
		while( matcher.find() )
			result++;

		return( result );
	}

	protected int numberOfSpaces( String value )
	{
//		boolean result = false;

		int result = 0;
		int len = value.length();
		for( int ii=0; ii<len; ii++ )
		{
			if( value.charAt(ii) == ' ' )
				result++;

//			result = ( count > 5 );
		}

		return( result );
	}

	protected String replace( Pattern pattern, String line )
	{
		String result = StringFunctions.instance().regExReplaceWithFirstGroup(pattern, line);

		return( result );
	}

	public void clearCache()
	{
		_cacheOfExpressionResult.clear();
		_cacheOfPatterns.clear();
	}

	protected RegexExpressionResultMapKey createRegexExpressionResultMapKey( String expression,
												List<String> blocksToReplaceWit,
												int combinationOfOptionals )
	{
		return( new RegexExpressionResultMapKey( expression, blocksToReplaceWit, combinationOfOptionals ) );
	}

	protected RegexExpressionResult getExpressionResult( String expression, List<String> blocksToReplaceWith,
									int combinationOfOptionals )
	{
		RegexExpressionResultMapKey key = createRegexExpressionResultMapKey( expression, blocksToReplaceWith,
												combinationOfOptionals );
		RegexExpressionResult result = _cacheOfExpressionResult.get( key );

		if( result == null )
		{
			BlockRegexBuilder reBuilder = getBlockRegexBuilder();
			if( reBuilder != null )
				result = reBuilder.getRegexPattern( expression, blocksToReplaceWith,
													combinationOfOptionals );

			if( result != null )
				_cacheOfExpressionResult.put( key, result );
		}

		return( result );
	}

	protected BlockRegexBuilder getBlockRegexBuilder()
	{
		return( _regexWholeContainer.getBlockRegexBuilder() );
	}

	protected Pattern getPattern( String expression )
	{
		Pattern result = _cacheOfPatterns.get( expression );

		if( result == null )
		{
			BlockRegexBuilder reBuilder = _regexWholeContainer.getBlockRegexBuilder();
			if( reBuilder != null )
				result = reBuilder.getRegexPattern( expression );

			if( result != null )
				_cacheOfPatterns.put( expression, result );
		}

		return( result );
	}

	protected int findLastMatchingLocation( List<LineModel> lineModelList,
											List<String> bufferOfGameHeaderStringLines,
											int maxNumberOfLinesToBrowse,
											int start )
	{
		int result = -1;

		for( int ii=start, count = 0;
			( result == -1 ) && (ii >= 0) && ( count < maxNumberOfLinesToBrowse ); ii--, count++ )
		{
			boolean matched = false;
			int index = 0;
			for( LineModel lineModel: lineModelList )
			{
				Pattern pattern = getPattern( lineModel.getSynchronizationRegexModel().getExpression() );
				String line = bufferOfGameHeaderStringLines.get( ii + index );

				matched = pattern.matcher( line ).matches();

				if( ! matched )
					break;

				index++;
			}

			if( matched )
				result = ii;
		}

		return( result );
	}
}
