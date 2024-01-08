/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl.helpers;

import com.frojasg1.general.structures.Pair;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AttributeDecoder
{
	protected static final Pattern ATTRIBUTE_PAIR_PATTERN = Pattern.compile( "^([A-Za-z0-9_]+):(.*)$" );
	protected static final Pattern ATTRIBUTE_VALUE_PATTERN = Pattern.compile( "^([^'\"{}]+)\\{.*$");

	protected static class LazyHolder
	{
		protected static final AttributeDecoder INSTANCE = new AttributeDecoder();
	}

	public static AttributeDecoder instance()
	{
		return( LazyHolder.INSTANCE );
	}

	public Pair<String, AttributeValueData> decodeAttribute( String str )
	{
		Pair<String, AttributeValueData> result = null;
		if( str != null )
		{
			str = str.trim();

			String attributeName = null;
			AttributeValueData avd = null;
			String avdStr;

			Matcher matcher = getAttributePairMatcher(str);
			if( matcher.find() )
			{
				attributeName = matcher.group(1);
				avdStr = matcher.group(2).trim();
			}
			else
			{
				throw( new RuntimeException( "Error when decoding composed attribute. It does not seem a composed attribute" ) );
			}

			avd = decodeValue( avdStr );

			result = new Pair( attributeName, avd );
		}

		return( result );
	}

	protected Matcher getAttributePairMatcher( String str )
	{
		return( ATTRIBUTE_PAIR_PATTERN.matcher(str) );
	}


	protected AttributeValueData decodeValue( String avdStr )
	{
		Matcher matcher = ATTRIBUTE_VALUE_PATTERN.matcher( avdStr );

		if( ! matcher.find() )
			throw( new RuntimeException( String.format( "Attribute value to decode: bad value '%s'", avdStr ) ) );

		AttributeValueData result = new AttributeValueData();
		result.setClassName( matcher.group(1) );
		result.setAllText(avdStr);

		return( result );
	}

	public boolean isSimpleObjectValueString( String valueStr )
	{
		return( ! getAttributePairMatcher(valueStr).find() );
	}
}
