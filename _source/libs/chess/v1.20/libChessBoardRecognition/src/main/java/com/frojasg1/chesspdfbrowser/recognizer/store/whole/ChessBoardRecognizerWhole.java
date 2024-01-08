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
package com.frojasg1.chesspdfbrowser.recognizer.store.whole;

import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterListener;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterObserved;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.raw.inputtext.impl.InputImage;
import com.frojasg1.chesspdfbrowser.engine.position.ChessGamePositionBase;
import com.frojasg1.chesspdfbrowser.recognizer.store.ChessBoardRecognitionStore;
import com.frojasg1.chesspdfbrowser.recognizer.store.set.loader.ChessBoardRecognitionPersistency;
import com.frojasg1.chesspdfbrowser.recognizer.threads.ChessBoardRecognizerCopyFenTask;
import com.frojasg1.chesspdfbrowser.recognizer.threads.ChessBoardRecognitionTrainingThread;
import com.frojasg1.chesspdfbrowser.recognizer.threads.ChessBoardRecognitionTrainingTaskData;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.executor.ExecutorInterface;
import com.frojasg1.general.executor.worker.PullOfExecutorWorkers;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.frojasg1.chesspdfbrowser.recognizer.configuration.ChessRecognizerApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.recognizer.recognizer.result.RecognitionResult;
import com.frojasg1.general.FileFunctions;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessBoardRecognizerWhole implements ConfigurationParameterListener
{
	protected ChessBoardRecognitionPersistency _chessBoardRecognitionPersistency = null;

	protected ChessBoardRecognitionTrainingThread _chessBoardRecognitionTrainingThread = null; 

	protected ChessBoardRecognizerCopyFenTask _chessBoardRecognitionFenRecognizerCopyThread = null;
	protected Thread _recognizerCopyThread = null;

	protected PullOfExecutorWorkers _recognizerThreads = null;

	protected ChessRecognizerApplicationConfiguration _appliConf = null;


	protected Map< InputImage, String > _cacheOfFens = null;
	protected Map< InputImage, BiConsumer<String, InputImage> > _mapForCallbackFunctions = null;

	public void init(ChessRecognizerApplicationConfiguration appliConf) throws ConfigurationException, IOException, ParserConfigurationException, SAXException
	{
		setAppliConf( appliConf );

		_chessBoardRecognitionPersistency = createChessBoardRecognitionPersistency();
		_chessBoardRecognitionPersistency.loadItems();

		_chessBoardRecognitionTrainingThread = createTrainingThread();
		_chessBoardRecognitionTrainingThread.start();

		_chessBoardRecognitionFenRecognizerCopyThread = createFenRecognizerCopyThread();
		_recognizerCopyThread = new Thread( _chessBoardRecognitionFenRecognizerCopyThread );
		_recognizerCopyThread.start();

		_recognizerThreads = createRecognizerThreads();
		_recognizerThreads.start();

		_cacheOfFens = createMapCacheOfFens();
		_mapForCallbackFunctions = createMapForCallbackFunctions();

		setNumberOfThreadsForBackgroundChessBoardRecognition( getAppliConf().getNumberOfThreadsForBackgroundChessBoardRecognition() );
		setIsChessBoardRecognitionActivated( getAppliConf().isChessBoardRecognizerActivated() );
	}

	protected void stopEverything()
	{
		_recognizerThreads.setHasToEnd();
		_chessBoardRecognitionFenRecognizerCopyThread.hasToStop(true);
	}

	public String longFolderNameForChessBoardRecognizerConfiguration()
	{
		String configurationFileNameForListOfPatternSets = getStore().getComboBoxContent().getConf().M_getConfigurationFileName();
		String result = FileFunctions.instance().getDirName( configurationFileNameForListOfPatternSets );

		return( result );
	}

	protected void eraseEverything() throws IOException
	{
		String dirToErase = longFolderNameForChessBoardRecognizerConfiguration();

		FileFunctions.instance().eraseDirCompletely( dirToErase );
	}

	public void resetErasingEverything() throws ConfigurationException, IOException, ParserConfigurationException, SAXException
	{
		stopEverything();
		eraseEverything();

		init( getAppliConf() );
	}

	public void reloadEverything() throws ConfigurationException, IOException, ParserConfigurationException, SAXException
	{
		stopEverything();

		init( getAppliConf() );
	}

	protected Map<InputImage, String> createMapCacheOfFens()
	{
		return( createConcurrentMap() );
	}

	protected Map<InputImage, BiConsumer<String, InputImage>> createMapForCallbackFunctions()
	{
		return( createConcurrentMap() );
	}

	protected <KK,VV> Map<KK, VV> createConcurrentMap()
	{
		return( new ConcurrentHashMap<>() );
	}

	public ChessBoardRecognitionStore getStore()
	{
		return( _chessBoardRecognitionPersistency.getChessBoardRecognitionStore() );
	}

	protected PullOfExecutorWorkers createRecognizerThreads()
	{
		PullOfExecutorWorkers result = new PullOfExecutorWorkers( "ChessBoardRecognizerWhole" );
		result.init( getAppliConf().getNumberOfThreadsForBackgroundChessBoardRecognition() );

		return( result );
	}

	protected ChessBoardRecognizerCopyFenTask createFenRecognizerCopyThread()
	{
		ChessBoardRecognizerCopyFenTask result = new ChessBoardRecognizerCopyFenTask(getAppliConf());
		return( result );
	}

	protected ChessBoardRecognitionTrainingThread createTrainingThread()
	{
		ChessBoardRecognitionTrainingThread result = new ChessBoardRecognitionTrainingThread();
		result.init( this );

		return( result );
	}

	protected ChessBoardRecognitionPersistency createChessBoardRecognitionPersistency()
	{
		ChessBoardRecognitionPersistency result = new ChessBoardRecognitionPersistency();
		result.init( getAppliConf() );

		return( result );
	}

	protected void setAppliConf( ChessRecognizerApplicationConfiguration appliConf )
	{
		_appliConf = appliConf;

		_appliConf.addConfigurationParameterListener( _appliConf.getConfigurationParameterNameForNumberOfThreadsForBackgroundRecognition(), this);
		_appliConf.addConfigurationParameterListener( _appliConf.getConfigurationParameterNameForIsChessRecognizerActivated(), this);
	}

	public ChessRecognizerApplicationConfiguration getAppliConf()
	{
		return( _appliConf );
	}

	public ChessBoardRecognitionPersistency getChessBoardRecognitionPersistency() {
		return _chessBoardRecognitionPersistency;
	}

	public void setChessBoardRecognitionPersistency(ChessBoardRecognitionPersistency chessBoardRecognitionPersistency) {
		this._chessBoardRecognitionPersistency = chessBoardRecognitionPersistency;
	}

	public ChessBoardRecognitionTrainingThread getChessBoardRecognitionTrainingThread() {
		return _chessBoardRecognitionTrainingThread;
	}

	public void setChessBoardRecognitionTrainingThread(ChessBoardRecognitionTrainingThread chessBoardRecognitionTrainingThread) {
		this._chessBoardRecognitionTrainingThread = chessBoardRecognitionTrainingThread;
	}

	public ChessBoardRecognizerCopyFenTask getChessBoardRecognizerCopyFenThread() {
		return _chessBoardRecognitionFenRecognizerCopyThread;
	}

	public void setChessBoardRecognizerCopyFenThread(ChessBoardRecognizerCopyFenTask _ocrRecognizerCopyFenThread) {
		this._chessBoardRecognitionFenRecognizerCopyThread = _ocrRecognizerCopyFenThread;
	}

	public PullOfExecutorWorkers getRecognizerThreads() {
		return _recognizerThreads;
	}

	public void setRecognizerThreads(PullOfExecutorWorkers _recognizerThreads) {
		this._recognizerThreads = _recognizerThreads;
	}

	public void clearTasks()
	{
		this.getRecognizerThreads().clearListOfPendingTasks();
		this.getRecognizerThreads().setIsPaused(true);
		this.getChessBoardRecognitionTrainingThread().clearListOfPendingTasks();

		_cacheOfFens.clear();
	}

	@Override
	public <CC> void configurationParameterChanged(ConfigurationParameterObserved observed, String label, CC oldValue, CC newValue)
	{
		if( label.equals( _appliConf.getConfigurationParameterNameForNumberOfThreadsForBackgroundRecognition() ) )
		{
			int newNumberOfThreads = (Integer) newValue;

			setNumberOfThreadsForBackgroundChessBoardRecognition( newNumberOfThreads );
		}
		else if( label.equals( _appliConf.getConfigurationParameterNameForIsChessRecognizerActivated() ) )
		{
			boolean isChessBoardRecognitionActivated = (Boolean) newValue;

			setIsChessBoardRecognitionActivated(isChessBoardRecognitionActivated );
		}
	}

	public void setNumberOfThreadsForBackgroundChessBoardRecognition( int value )
	{
		getRecognizerThreads().setNumberOfThreads(value);
	}

	public void setIsChessBoardRecognitionActivated( boolean isChessBoardRecognitionActivated )
	{
		getChessBoardRecognitionTrainingThread().setIsActivated(isChessBoardRecognitionActivated);

		// if isChessBoardRecognizerActivated, OcrTrainingThread will activate RecognizeThreads,
		// when its recognitions are over.
		if( !isChessBoardRecognitionActivated )
			getRecognizerThreads().setIsActivated(isChessBoardRecognitionActivated);
	}

	public void newImagePositionDetected(RecognitionResult result, InputImage image)
	{
		ChessGamePositionBase positionDetected = result.getDetectedPosition();

		String baseBoardFen = null;
		if( ( positionDetected != null ) && positionDetected.isComplete() )
			baseBoardFen = positionDetected.getFenBoardStringBase();

		if( baseBoardFen != null )
			_cacheOfFens.put(image, baseBoardFen);

		BiConsumer<String, InputImage> storedCallbackFunction = _mapForCallbackFunctions.remove( image );
		if( storedCallbackFunction != null )
		{
			storedCallbackFunction.accept(baseBoardFen, image);
		}
	}

	protected ExecutorInterface createPendingImagePositionExecutor( InputImage image )
	{
		ChessBoardRecognizerCopyFenTask result = new ChessBoardRecognizerCopyFenTask( getAppliConf() );
		boolean finishThreadAtEnd = true;
		result.recognize((pos, im) -> newImagePositionDetected( pos, im ), image,
							getChessBoardRecognitionPersistency().getChessBoardRecognitionStore(), finishThreadAtEnd );

		return( result );
	}

	public void recognizePosition( InputImage image, BiConsumer<String, InputImage> callbackFunction )
	{
		BiConsumer<String, InputImage> storedCallbackFunction = _mapForCallbackFunctions.get( image );

		if( storedCallbackFunction == null )
		{
			_mapForCallbackFunctions.put( image, callbackFunction );

			String cacheFen = _cacheOfFens.get( image );
			if( cacheFen != null )
				callbackFunction.accept(cacheFen, image);
			else
				getRecognizerThreads().addPendingExecutor( createPendingImagePositionExecutor( image ) );
		}
	}

	protected ChessBoardRecognitionTrainingTaskData createTraningTaskData( String baseBoardFen, InputImage image )
	{
		ChessBoardRecognitionTrainingTaskData result = new ChessBoardRecognitionTrainingTaskData( baseBoardFen, image,
										(fen, im) -> newImagePositionDetected( fen, im ) );
		return( result );
	}

	public void addTrainigPair(String baseBoardFen, InputImage image)
	{
		getChessBoardRecognitionTrainingThread().addTrainigTaskData( createTraningTaskData( baseBoardFen, image ) );
	}
}
