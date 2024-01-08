/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.codec.impl.helpers;

import com.frojasg1.general.analysers.GenListSplitterBase;
import com.frojasg1.general.structures.Pair;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AttributeSplitter extends GenListSplitterBase
{
	protected static final String SEPARATOR = ",";

	protected static final String NOTABLE_CHARS = "{}\"'";

	protected AtomicInteger _braceCount = new AtomicInteger();

	protected Pair<String, AttributeValueData> _nextPair;

	protected AttributeDecoder _attributeDecoder;

	public AttributeSplitter( String str, AttributeDecoder attributeDecoder )
	{
		super( str, SEPARATOR );

		_attributeDecoder = attributeDecoder;
	}

	public Pair<String, AttributeValueData> getPairToken()
	{
		if( _nextPair == null )
			_nextPair = calculateNextPair();

		return( _nextPair );
	}

	public Pair<String, AttributeValueData> nextPair()
	{
		_nextPair = null;

		return( getPairToken() );
	}

	protected Pair<String, AttributeValueData> calculateNextPair()
	{
		return( calculateNextPair( next() ) );
	}

	protected AttributeDecoder getAttributeDecoder()
	{
		return( _attributeDecoder );
	}

	protected Pair<String, AttributeValueData> calculateNextPair(String str)
	{
		return( getAttributeDecoder().decodeAttribute(str) );
	}

	protected void passWholeAttribute()
	{

		String ch = null;
		loop: while( ! hasEnded() )
		{
			ch = getNextNotableChar();
			if( ch == null )
				break;

			_pos++;

			switch( ch )
			{
				case "{": _braceCount.incrementAndGet(); break;
				case "}":
					_braceCount.decrementAndGet();
					if( _braceCount.get() == 0 )
						break loop;

				case "'":
				case "\"":
//					String ch2 = incPosAndGetMatchingChar(ch);
					String ch2 = getMatchingChar(ch);
					if( !Objects.equals( ch2, ch) )
						throw( new RuntimeException( "Matching quote not found at pos: " + _pos ) );
					_pos++;

				break;
			}
		}

		if( _braceCount.get() != 0 )
			throw( new RuntimeException( "Error: braces do not match" ) );

		skipSeparator();
	}

	protected String getNextNotableChar()
	{
		return( getNextNotableChar(NOTABLE_CHARS) );
	}

	public boolean isSimpleObjectValueString( String valueStr )
	{
		return( getAttributeDecoder().isSimpleObjectValueString( valueStr ) );
	}
}
