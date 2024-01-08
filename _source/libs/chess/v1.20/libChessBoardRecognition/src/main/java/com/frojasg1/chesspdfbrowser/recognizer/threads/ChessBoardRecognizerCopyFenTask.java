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

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.ChessRecognizerApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.recognizer.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.impl.ChessBoardPositionRecognizerWithStoreImpl;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.impl.ChessBoardPositionRecognizerWithStoreImplFlippingBoard;
import com.frojasg1.chesspdfbrowser.recognizer.store.ChessBoardRecognitionStore;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.executor.ExecutorInterface;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.result.RecognitionResult;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardRecognizerCopyFenTask implements Runnable, ExecutorInterface
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessBoardRecognizerCopyFenTask.properties";

	protected static final String CONF_THERE_IS_A_RECOGNITION_STARTED_CANNOT_START_A_NEW_RECOGNITION_UNTIL_PREVIOUS_ONE_ENDS = "THERE_IS_A_RECOGNITION_STARTED_CANNOT_START_A_NEW_RECOGNITION_UNTIL_PREVIOUS_ONE_ENDS";

	protected volatile BiConsumer<RecognitionResult, InputImage> _callbackFunction = null;
	protected volatile InputImage _image = null;

	protected static InternationalizedStringConfImp _internationalizedStringConf = null;

	protected ChessBoardRecognitionStore _store = null;
	protected ChessBoardPositionRecognizerWithStoreImpl _recognizer = null;

	protected volatile boolean _hasToStop = false;

	protected ReentrantLock _lock = new ReentrantLock(true);
	protected Condition _moreItemsAvailable = _lock.newCondition();

	protected boolean _finishThreadAtEnd = false;

	protected ChessRecognizerApplicationConfiguration _appliConf;

	static
	{
		_internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
								LibConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	public ChessBoardRecognizerCopyFenTask(ChessRecognizerApplicationConfiguration appliConf)
	{
		_appliConf = appliConf;
	}

	protected ChessRecognizerApplicationConfiguration getAppliConf()
	{
		return( _appliConf );
	}

	public void recognize( BiConsumer<RecognitionResult, InputImage> callbackFunction, InputImage image,
							ChessBoardRecognitionStore store, boolean finishThreadAtEnd )
	{
		try
		{
			_lock.lock();

			_finishThreadAtEnd = finishThreadAtEnd;
			initialCheck();

			_store = store;
			_callbackFunction = callbackFunction;
			_image = image;

			updateRecognizer();

			if( _lock.hasWaiters(_moreItemsAvailable) )
				_moreItemsAvailable.signal();
		}
		finally
		{
			_lock.unlock();
		}
	}

	protected void initialCheck()
	{
		if( isRecognizing() )
			throw( new RuntimeException( getInternationalString( CONF_THERE_IS_A_RECOGNITION_STARTED_CANNOT_START_A_NEW_RECOGNITION_UNTIL_PREVIOUS_ONE_ENDS ) ) );
	}

	public boolean isRecognizing()
	{
		return( _callbackFunction != null );
	}

	protected void updateRecognizer()
	{
		_recognizer = createRecognizer();
	}

	protected ChessBoardPositionRecognizerWithStoreImplFlippingBoard createRecognizer()
	{
		ChessBoardPositionRecognizerWithStoreImplFlippingBoard result = new ChessBoardPositionRecognizerWithStoreImplFlippingBoard(getAppliConf());
		result.init( _store );

		return( result );
	}

	protected InputImage getImage()
	{
		try
		{
			_lock.lock();

			if( _image == null )
				ExecutionFunctions.instance().safeMethodExecution( () -> _moreItemsAvailable.awaitNanos( 1000000000L ) );

			return( _image );
		}
		finally
		{
			_lock.unlock();
		}
	}

	@Override
	public void run()
	{
		while( ! _hasToStop )
		{
			try
			{
				InputImage image = getImage();
				if( image != null )
				{
					String fen = null;
					try
					{
						fen = ExecutionFunctions.instance().safeFunctionExecution( () -> _recognizer.recognizeBoardFen(image) );
					}
					finally
					{
						_callbackFunction.accept(_recognizer.getRecognitionResult(), image);
					}
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
			finally
			{
				_callbackFunction = null;
				_image = null;
			}

			if( _finishThreadAtEnd )
				break;
		}
	}

	@Override
	public void execute()
	{
		run();
	}

	public void hasToStop( boolean value )
	{
		_hasToStop = value;
	}

	@Override
	public void hasToStop()
	{
		hasToStop( true );
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

	protected static void registerInternationalizedStrings()
	{
		registerInternationalString( CONF_THERE_IS_A_RECOGNITION_STARTED_CANNOT_START_A_NEW_RECOGNITION_UNTIL_PREVIOUS_ONE_ENDS,
			"There is a recognition started. Cannot start a new recognition until the previous one ends." );
	}
}
