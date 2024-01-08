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
package com.frojasg1.chesspdfbrowser.engine.tags.regex;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.model.regex.BlockRegexConfigurationContainer;
import static com.frojasg1.chesspdfbrowser.model.regex.LineModel.GLOBAL_CONF_FILE_NAME;
import com.frojasg1.chesspdfbrowser.model.regex.RegexOfBlockModel;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexLexicalAnalyser;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexToken;
import com.frojasg1.chesspdfbrowser.model.regex.parser.RegexTokenId;
import com.frojasg1.general.string.StringFunctions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BlockRegexBuilder
{
	public static final String GLOBAL_CONF_FILE_NAME = "BlockRegexBuilder.properties";

	protected static final String CONF_TOO_MANY_OPTIONAL_BLOCKS = "TOO_MANY_OPTIONAL_BLOCKS";
	protected static final String CONF_PROBLEMS_GETTING_REGEX_CONFIGURATION = "PROBLEMS_GETTING_REGEX_CONFIGURATION";
	protected static final String CONF_NUMBER_OF_TOKENS_NOT_EXPECTED = "NUMBER_OF_TOKENS_NOT_EXPECTED";
	protected static final String CONF_EXPECTED_FLAT_STRING_TOKEN = "EXPECTED_FLAT_STRING_TOKEN";
	protected static final String CONF_REGEX_CONFIGURATION_WAS_EXPECTED_TO_BE_SIMPLE = "REGEX_CONFIGURATION_WAS_EXPECTED_TO_BE_SIMPLE";
	protected static final String CONF_BLOCK_WITH_NAME_DOES_NOT_EXIST = "BLOCK_WITH_NAME_DOES_NOT_EXIST";

	protected static final int MAX_NUMBER_OF_OPTIONAL_BLOCKS = 10;

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected BlockRegexConfigurationContainer _container = null;

	protected RegexLexicalAnalyser _lex = null;

//	protected Map<String, BlockToReplaceWith> _blockToReplaceWithMap = new HashMap<>();

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								ApplicationConfiguration.instance().getInternationalPropertiesPathInJar() );

		registerInternationalizedStrings();
	}

	public BlockRegexBuilder( BlockRegexConfigurationContainer container )
	{
		_container = container;
	}
/*
	public List<RegexToken> getListOfTokens( String regexConfigurationName )
	{
		return( _container.getListOfTokens( regexConfigurationName ) );
	}
*/
	protected RegexLexicalAnalyser createLexicalAnalyser()
	{
		return( new RegexLexicalAnalyser() );
	}

	protected RegexLexicalAnalyser getLexicalAnalyser()
	{
		if( _lex == null )
			_lex = createLexicalAnalyser();

		return( _lex );
	}

	public Pattern getRegexPattern( String expression )
	{
		Pattern result = null;

		List<RegexToken> list = getListOfTokens( expression );

		if( list != null )
		{
			int numberOfOptionalBlocks = countNumberOfOptionalBlocks( list );

			String regex = createRegexString( list, numberOfOptionalBlocks );

			// https://stackoverflow.com/questions/10894122/java-regex-for-support-unicode
			result = Pattern.compile( regex, Pattern.UNICODE_CHARACTER_CLASS );
		}

		return( result );
	}

	public RegexExpressionResult getRegexPattern( String expression, List<String> blocksToReplaceWith )
	{
		return( getRegexPattern( expression, blocksToReplaceWith, 0xFFFFFFFF ) );
	}

	public RegexExpressionResult getRegexPattern( String expression, List<String> blocksToReplaceWith,
									int combinationOfOptionalsOfTagRegex )
	{
		Pattern patternResult = null;

		RegexExpressionResult result = getListOfTokens( expression, blocksToReplaceWith,
														combinationOfOptionalsOfTagRegex );

		List<RegexToken> list = result.getListSimpleBlockRegexTokens();
		if( list != null )
		{
			int numberOfOptionalBlocks = countNumberOfOptionalBlocks( list );

			String regex = createRegexString( list, numberOfOptionalBlocks );

			// https://stackoverflow.com/questions/10894122/java-regex-for-support-unicode
			patternResult = Pattern.compile( regex, Pattern.UNICODE_CHARACTER_CLASS );
			result.setPattern( patternResult );
		}

		return( result );
	}

	protected int countNumberOfOptionalBlocks( List<RegexToken> list )
	{
		int result = 0;
		for( RegexToken token: list )
		{
			if( token.isOptional() )
				result++;
		}

		return( result );
	}

	protected String createRegexString( List<RegexToken> list, int numberOfOptionalBlocks )
	{
		StringBuilder sb = new StringBuilder();
		int numberOfCombinations = ( 1 << numberOfOptionalBlocks );

		if( numberOfOptionalBlocks > MAX_NUMBER_OF_OPTIONAL_BLOCKS )
			throw( new RuntimeException( createCustomInternationalString( CONF_TOO_MANY_OPTIONAL_BLOCKS, MAX_NUMBER_OF_OPTIONAL_BLOCKS ) ) );

		String separator = "";
		for( int ii=0; ii<numberOfCombinations; ii++ )
		{
			sb.append(separator).append( createRegexElement( list, ii ) );
			separator = "|";
		}

		return( sb.toString() );
	}

	protected String getTransformedStringOfSimpleToken( String regexConfigurationName )
	{
		String result = null;
		RegexOfBlockModel robc = _container.get( regexConfigurationName );

		if( robc == null )
			throw( new RuntimeException( createCustomInternationalString( CONF_PROBLEMS_GETTING_REGEX_CONFIGURATION, regexConfigurationName ) ) );

		List<RegexToken> list = getListOfTokens( robc.getExpression() );

		if( list == null )
			throw( new RuntimeException( createCustomInternationalString( CONF_PROBLEMS_GETTING_REGEX_CONFIGURATION, regexConfigurationName ) ) );

		if( list.size() != 1 )
			throw( new RuntimeException( createCustomInternationalString( CONF_NUMBER_OF_TOKENS_NOT_EXPECTED, list.size() ) ) );

		RegexToken first = list.get(0);
		if( ! first.getTokenId().equals( RegexTokenId.FLAT_STRING ) )
			throw( new RuntimeException( getInternationalString( CONF_EXPECTED_FLAT_STRING_TOKEN ) ) );

		if( !isSimple(robc.getExpression()) )
			throw( new RuntimeException( getInternationalString( CONF_REGEX_CONFIGURATION_WAS_EXPECTED_TO_BE_SIMPLE ) ) );

		result = first.getTransformedString();

		return( result );
	}

	protected String createRegexElement( List<RegexToken> list, int combinationOfOptionals )
	{
		StringBuilder sb = new StringBuilder();

		int bit = 1;
		for( RegexToken token: list )
		{
			String tmp = null;
			switch( token.getTokenId() )
			{
				case FLAT_STRING:
					tmp = token.getTransformedString();
				break;

				case BLOCK_NAME:
					if( !token.isOptional() || ( (combinationOfOptionals & bit) > 0 ) )
					{
						String regexConfigurationName = token.getTransformedString();
						tmp = getTransformedStringOfSimpleToken(regexConfigurationName);
					}
					else
						tmp = "";

					if( token.isOptional() )
						bit = bit << 1;
				break;

				default:
					tmp = "";
				break;
			}

			sb.append( tmp );
		}

		return( sb.toString() );
	}
/*
	protected BlockToReplaceWith getBlockToReplaceWith( String blockToReplaceWithStr )
	{
		BlockToReplaceWith expressionResult = _blockToReplaceWithMap.get( blockToReplaceWithStr );

		if( expressionResult == null )
		{
			expressionResult = new BlockToReplaceWith();
			expressionResult.init( blockToReplaceWithStr );

			_blockToReplaceWithMap.put( blockToReplaceWithStr, expressionResult );
		}

		return( expressionResult );
	}
*/

	protected String formatBlockToReplaceWith( String blockName, int index )
	{
		return( String.format( "%s[%d]", blockName, index ) );
	}

	protected RegexExpressionResult getExpressionWithBlockToReplaceWith( String expression,
														List<String> blocksToReplaceWith,
														int combinationOfOptionalsOfTagRegex )
	{
		StringBuilder expressionResult = new StringBuilder();
		int combination = combinationOfOptionalsOfTagRegex;
//		List<BlockToReplaceWith> targetBlockList = getTargetBlockLlist( blocksToReplaceWith );

		Map<String, AtomicInteger> indexMap = new HashMap<>();
		List<String> replacementListResult = new ArrayList<>();

		boolean hasToInclude = false;
		int weight = 1;
		int index = 0;
		RegexLexicalAnalyser lex = getLexicalAnalyser();
		List<RegexToken> tokenList = lex.getListOfTokens(expression);
		boolean added = false;
		for( RegexToken token: tokenList )
		{
			added = false;
			hasToInclude = true;
			if( token.getTokenId().equals( RegexTokenId.BLOCK_NAME ) )
			{
				String blockName = StringFunctions.instance().removeAtEnd( token.getString(), "?" );

				index = incrementIndex( indexMap, blockName );
				String blockToReplaceWith = formatBlockToReplaceWith( blockName, index );

				hasToInclude = ( !token.isOptional() || ( combination & weight ) != 0 );
				if( hasToInclude && ( blocksToReplaceWith != null ) &&
					blocksToReplaceWith.contains( blockToReplaceWith ) )
				{
					replacementListResult.add( blockToReplaceWith );
					expressionResult.append( "(" ).append( blockName ).append( ")" );
//					if( token.isOptional() )
//						expressionResult.append( "?" );
					added = true;
				}

				if( token.isOptional() )
					weight = weight << 1;
			}

			if( hasToInclude && !added )
				expressionResult.append( token.getString() );
		}

		RegexExpressionResult result = new RegexExpressionResult();
		result.setParticularTagRegexExpression( expressionResult.toString() );
		result.setReplacementList(replacementListResult);
		result.setNumberOfOptionalBlocks( this.countNumberOfOptionalBlocks(tokenList) );

		return( result );
	}

	protected int incrementIndex( Map<String, AtomicInteger> indexMap, String blockName )
	{
		AtomicInteger ai = indexMap.get( blockName );
		if( ai == null )
		{
			ai = new AtomicInteger(0);
			indexMap.put( blockName, ai );
		}

		int result = ai.addAndGet( 1 );

		return( result );
	}

	public List<RegexToken> getListOfTokens( String expression )
	{
		List<RegexToken> result = null;

		String expressionMod = expression;
		if( expressionMod != null )
		{
			String expressionOfSimpleBlocks = getExpressionOfSimpleBlocks(null, expressionMod);

			RegexLexicalAnalyser lex = getLexicalAnalyser();

			result = lex.getListOfTokens(expressionOfSimpleBlocks);
		}

		return( result );
	}

	public RegexExpressionResult getListOfTokens( String expression, List<String> blocksToReplaceWith,
									int combinationOfOptionalsOfTagRegex )
	{
		RegexExpressionResult result = null;

		List<RegexToken> listOfTokensResult = null;

		String expressionMod = expression;
//		if( ( blocksToReplaceWith != null ) && !blocksToReplaceWith.isEmpty() )
		{
			result = getExpressionWithBlockToReplaceWith( expression, blocksToReplaceWith,
															combinationOfOptionalsOfTagRegex );
			expressionMod = result.getParticularTagRegexExpression();
		}

		if( expressionMod != null )
		{
			String expressionOfSimpleBlocks = getExpressionOfSimpleBlocks(null, expressionMod);

			RegexLexicalAnalyser lex = getLexicalAnalyser();

			listOfTokensResult = lex.getListOfTokens(expressionOfSimpleBlocks);

			result.setListSimpleBlockRegexTokens(listOfTokensResult);
		}

		return( result );
	}

	protected List<RegexToken> getListOfCurrentTokens( String expression )
	{
		RegexLexicalAnalyser lex = getLexicalAnalyser();

		List<RegexToken> result = lex.getListOfTokens( expression );

		return( result );
	}

	public String calculateExpressionOfSimpleBlocks( String expression )
	{
		StringBuilder sb = new StringBuilder();

		List<RegexToken> list = getListOfCurrentTokens( expression );

		for( RegexToken token: list )
		{
			switch( token.getTokenId() )
			{
				case FLAT_STRING:
					sb.append( token.getString() );
				break;

				case BLOCK_NAME:
					String blockName = token.getTransformedString();
					RegexOfBlockModel block = _container.get( blockName );
					if( block == null )
						throw( new RuntimeException( createCustomInternationalString( CONF_BLOCK_WITH_NAME_DOES_NOT_EXIST, blockName ) ) );
					sb.append( getExpressionOfSimpleBlocks(block.getName(), block.getExpression() ) );
					if( token.isOptional() )
						sb.append( "?" );
				break;
			}
		}

		return( sb.toString() );
	}

	public String getExpressionOfSimpleBlocks( String name, String expression )
	{
		String result = null;
		if( ( name != null ) && isSimple(expression) )
			result = "%" + name + "%";
		else
			result = calculateExpressionOfSimpleBlocks(expression);

		return( result );
	}

	protected boolean isSimple(String expression)
	{
		boolean result = false;

		List<RegexToken> list = getListOfCurrentTokens(expression);

		for( RegexToken token: list )
		{
			result = true;
			if( !token.getTokenId().equals( RegexTokenId.FLAT_STRING ) )
			{
				result = false;
				break;
			}
		}

		return( result );
	}

	public BlockRegexConfigurationContainer getBlockContainer()
	{
		return( _container );
	}

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_TOO_MANY_OPTIONAL_BLOCKS, "Too many optional blocks. The number of optional blocks cannot be greater than $1" );
		registerInternationalString(CONF_PROBLEMS_GETTING_REGEX_CONFIGURATION, "Problems getting regexConfiguration for $1" );
		registerInternationalString(CONF_NUMBER_OF_TOKENS_NOT_EXPECTED, "Number of tokens not expected $1 . Expected: 1" );
		registerInternationalString(CONF_EXPECTED_FLAT_STRING_TOKEN, "Expected flat string token, and it is not." );
		registerInternationalString(CONF_REGEX_CONFIGURATION_WAS_EXPECTED_TO_BE_SIMPLE, "RegexConfiguration was expected to be simple." );
		registerInternationalString(CONF_BLOCK_WITH_NAME_DOES_NOT_EXIST, "Block with name: $1 does not exist." );
	}

	public static void registerInternationalString(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	public String getInternationalString(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}

	public String createCustomInternationalString(String label, Object... args) {
		return( _internationalizedStringConf.createCustomInternationalString( label, args ) );
	}
}
