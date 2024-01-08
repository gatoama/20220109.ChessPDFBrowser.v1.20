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
package com.frojasg1.chesspdfbrowser.recognizer.threads;

import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.result.RecognitionResult;
import java.util.function.BiConsumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardRecognitionTrainingTaskData
{
	protected static final int NUMBER_OF_ATTEMPTS = 2;

	protected String _baseBoardFen = null;
	protected InputImage _image = null;
	protected BiConsumer<RecognitionResult, InputImage> _callbackFunction = null;

	protected int _numberOfRemainingAttempts = NUMBER_OF_ATTEMPTS;

	public ChessBoardRecognitionTrainingTaskData( String baseBoardFen, InputImage image,
								BiConsumer<RecognitionResult, InputImage> callbackFunction )
	{
		_baseBoardFen = baseBoardFen;
		_image = image;
		_callbackFunction = callbackFunction;

		_numberOfRemainingAttempts = NUMBER_OF_ATTEMPTS;
	}

	public String getBaseBoardFen() {
		return _baseBoardFen;
	}

	public InputImage getImage() {
		return _image;
	}

	public BiConsumer<RecognitionResult, InputImage> getCallbackFunction() {
		return _callbackFunction;
	}

	public void decNumberOfAttempts()
	{
		_numberOfRemainingAttempts--;
	}

	public int getNumberOfRemainingAttempts()
	{
		return( _numberOfRemainingAttempts );
	}
}
