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

import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.SegmentKey;

/**
 *
 * @author Usuario
 */
public class PreToken
{
	protected String _string = null;
	protected int _initialPosition = -1;
	protected TokenId _tokenId = null;
	protected int _lineNumber = -1;
	protected SegmentKey _segmentKey = null;
	protected InputImage _inputImage = null;

	public PreToken( TokenId tokenId, String string, int lineNumber, int initialPosition,
					SegmentKey segmentKey )
	{
		_tokenId = tokenId;
		_string = string;
		_initialPosition = initialPosition;
		_lineNumber = lineNumber;
		_segmentKey = segmentKey;
	}

	public PreToken( InputImage inputImage, int lineNumber )
	{
		_tokenId = TokenId.IMAGE;
		_string = "";
		_initialPosition = 0;
		_lineNumber = lineNumber;
		_inputImage = inputImage;
		_segmentKey = inputImage.getSegmentKey();
	}

	public String getString()			{	return( _string );	}
	public int getInitialPosition()		{	return( _initialPosition );	}
	public int getLineNumber()			{	return( _lineNumber );	}
	public TokenId getTokenId()			{	return( _tokenId );	}
	public InputImage getInputImage()	{	return( _inputImage ); }
	public SegmentKey getSegmentKey()	{	return( _segmentKey ); }
}
