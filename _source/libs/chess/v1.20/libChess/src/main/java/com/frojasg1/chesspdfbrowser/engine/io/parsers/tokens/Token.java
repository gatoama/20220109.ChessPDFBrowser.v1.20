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
package com.frojasg1.chesspdfbrowser.engine.io.parsers.tokens;

import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.SegmentKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Usuario
 */
public class Token implements Serializable
{
	protected TokenId _tokenId = null;
	protected int _initialPosition = -1;
	protected int _finalPosition = -1;
	protected String _string = null;
	protected int _lineNumber = -1;
	protected SegmentKey _segmentKey = null;

	public Token()	{}

	public Token( PreToken preToken )
	{
		_tokenId = preToken.getTokenId();
		_initialPosition = preToken.getInitialPosition();
		_finalPosition = preToken.getInitialPosition() + preToken.getString().length() - 1;
		_string = preToken.getString();
		_lineNumber = preToken.getLineNumber();
		_segmentKey = preToken.getSegmentKey();
	}

	public Token( TokenId tokenId )
	{
		_tokenId = tokenId;
	}

	public void setTokenId( TokenId tokenId )				{	_tokenId = tokenId;	}
	public void setInitialPosition( int position )			{	_initialPosition = position;	}
	public void setFinalPosition( int position )			{	_finalPosition = position;	}
	public void setString( String str )						{	_string = str;	}
	public void setLineNumber( int value )					{	_lineNumber = value;	}
	public void setSegmentKey( SegmentKey value )			{	_segmentKey = value;	}
	
	public TokenId getTokenId()								{	return( _tokenId );	}
	public int getInitialPosition()							{	return( _initialPosition );	}
	public int getFinalPosition()							{	return( _finalPosition );	}
	public String getString()								{	return( _string );	}
	public int getLineNumber()								{	return( _lineNumber );	}
	public SegmentKey setSegmentKey()						{	return( _segmentKey );	}

	public static Token deepCopy( Token token )
	{
		Token result = null;

		try
		{
			ByteArrayOutputStream bos= new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream (bos);
			os.writeObject(token);  // this es de tipo DatoUdp
			os.close();
			byte[] bytes =  bos.toByteArray(); // devuelve byte[]

			ByteArrayInputStream bis= new ByteArrayInputStream(bytes); // bytes es el byte[]
			ObjectInputStream is = new ObjectInputStream(bis);
			result = (Token)is.readObject();
			is.close();
		}
		catch( Throwable th ) // throws IOException, ClassNotFoundException
		{
			th.printStackTrace();
		}
		
		return( result );
	}
	
	public static String tokenIdToString( TokenId tokenId )
	{
		String result = null;

		switch( tokenId )
		{
			case ATTRIBUTE:			result = "ATTRIBUTE";		break;
			case NUMBER:			result = "NUMBER";		break;
			case DOT:				result = "DOT";		break;
			case MOVE:				result = "MOVE";		break;
			case COMMENT:			result = "COMMENT";		break;
			case STRING:			result = "STRING";		break;
			case RESULT:			result = "RESULT";		break;
			case OPEN_BRACKET:		result = "OPEN_BRACKET";		break;
			case CLOSE_BRACKET:		result = "CLOSE_BRACKET";		break;
			case OPEN_BRACE:		result = "OPEN_BRACE";		break;
			case CLOSE_BRACE:		result = "CLOSE_BRACE";		break;
			case RETURN:			result = "RETURN";		break;
			case IMAGE:				result = "IMAGE";		break;
			case EOF:				result = "EOF";		break;
			default:				result = "Unknown";
		}

		return( result );
	}
}
