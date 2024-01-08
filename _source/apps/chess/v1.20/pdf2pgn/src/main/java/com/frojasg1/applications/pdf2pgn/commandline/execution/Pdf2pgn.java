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
package com.frojasg1.applications.pdf2pgn.commandline.execution;

import com.frojasg1.applications.pdf2pgn.commandline.args.Pdf2pgnApplicationContext;
import com.frojasg1.applications.pdf2pgn.commandline.helpers.ScanGamesForCommandLineFunctions;
import com.frojasg1.applications.pdf2pgn.commandline.result.Pdf2pgnResult;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.io.file.implementation.PgnChessFile;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.impl.ImagePositionControllerBase;
import com.frojasg1.chesspdfbrowser.recognizer.threads.notifier.PendingItemsListener;
import com.frojasg1.chesspdfbrowser.threads.LoadChessControllerInterface;
import com.frojasg1.chesspdfbrowser.threads.SavePGNThread;
import com.frojasg1.chesspdfbrowser.threads.ScanPDFgamesThread;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.UpdatingProgress;
import com.frojasg1.general.streams.InOutErrStreamFunctions;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.libpdf.threads.LoadPDFThread;
import com.frojasg1.libpdf.threads.LoadPdfControllerInterface;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class Pdf2pgn
{
	protected static final String LOADING_PDF = "Loading Pdf";
	protected static final String SCANNING_GAMES = "Scanning games";
	protected static final String SAVING_PGN = "Saving Pgn";

	protected PdfDocumentWrapper _pdfDocument = null;

	protected Pdf2pgnApplicationContext _applicationContext = null;
	protected Consumer<Pdf2pgnResult> _callback = null;

	protected ScanPDFgamesThread _thread = null;

	protected UpdatingProgress _updatingProgress = null;

	protected PendingItemsListener _pendingItemsListener = null;

	protected volatile boolean _hasSavedPgn = false;
	protected volatile boolean _hasFinishedScanning = false;
	protected volatile int _pendingTrainingItems = 0;
	protected List<ChessGame> _listOfGames = null;

	public Pdf2pgn( Pdf2pgnApplicationContext applicationContext,
					Consumer<Pdf2pgnResult> callback,
					UpdatingProgress updatingProgress )
	{
		_updatingProgress = updatingProgress;
		_callback = callback;
		_applicationContext = applicationContext;
	}

	public void execute()
	{
		out().println( "execute" );
		addListeners();
		_pdfDocument = createPdfDocument();
		loadPdf();
	}

	protected Pdf2pgnApplicationContext getApplicationContext()
	{
		return( _applicationContext );
	}

	protected String getInputFileName()
	{
		return( getApplicationContext().getInputFileName() );
	}

	protected String getBaseInputFileName()
	{
		return( FileFunctions.instance().getBaseName( getInputFileName() ) );
	}

	protected LoadChessControllerInterface createLoadChessControllerForScanGames()
	{
		return(  new LoadChessController(SCANNING_GAMES) );
	}

	public void newChessListGameLoadedInternal(String type, List<ChessGame> list) {
		if( type == SCANNING_GAMES )
			waitForRecognizerToFinish(list);
		else if( type == SAVING_PGN )
			successToCallback(list);
	}

	protected synchronized void waitForRecognizerToFinish(List<ChessGame> list)
	{
		if( _pendingTrainingItems == 0 )
			savePgn( list );
		else
		{
			_hasFinishedScanning = true;
			_listOfGames = list;
			out().println( "Waiting for pending images to be processed" );
		}
	}

	protected void savePgn(List<ChessGame> list)
	{
		savePgn( list, getApplicationContext().getOutputFileName() );
	}

	protected void errorLoadingGames(String message, String title)
	{
		errorToCallback( "Error loading PDF. " + title + " - " + message );
	}

	protected LoadPdfControllerInterface createLoadPdfController()
	{
		return( new LoadPdfController(LOADING_PDF) );
	}

	protected TagsExtractor getTagsExtractor()
	{
		return( getApplicationContext().getTagsExtractor() );
	}

	protected void pdfLoaded( String type, PdfDocumentWrapper doc )
	{
		if( type == LOADING_PDF )
		{
			startScanningGames( doc );
		}
	}

	protected String getChessLanguageForConfiguration( String languageToParseGames,
														String lettersForPiecesToParseGames )
	{
		return( ScanGamesForCommandLineFunctions.instance().getChessLanguageForConfiguration( languageToParseGames,
														lettersForPiecesToParseGames ) );
	}

	protected void updateConfiguration( ChessViewConfiguration config )
	{
		String chessLanguageToParseFrom = getChessLanguageForConfiguration( getApplicationContext().getLanguageToParseGames(),
																			getApplicationContext().getLettersForPiecesToParseGames() );
		if( chessLanguageToParseFrom != null )
			config.setConfigurationOfChessLanguageToParseTextFrom( chessLanguageToParseFrom );

		out().println( "ChessLanguage to use: " + config.getConfigurationOfChessLanguageToParseTextFrom() );
	}

	protected void startScanningGames( PdfDocumentWrapper doc )
	{
		ApplicationConfiguration config = getApplicationContext().getApplicationConfiguration();
		updateConfiguration(config);

		_thread = new ScanPDFgamesThread( createLoadChessControllerForScanGames(), doc,
											getUpdatingProgress(),
											config,
											getTagsExtractor(),
											createImagePositionController(),
											getBaseInputFileName() );

		_thread.setInitialPageToScanForGames( getApplicationContext().getInitialPageNumber() );
		_thread.setFinalPageToScanForGames( getApplicationContext().getFinalPageNumber() );

		_thread.start();
		_thread.letsStart();
	}

	protected PrintStream out()
	{
		return( InOutErrStreamFunctions.instance().getOriginalOutStream() );
	}

	protected PrintStream err()
	{
		return( InOutErrStreamFunctions.instance().getOriginalErrStream() );
	}

	protected void startLoadingPdf(String type)
	{
		out().println( "Starting ... " + type );
	}

	protected void finishedLoading(String type)
	{
		out().println( "Finished ... " + type );
	}

	protected void pdfLoadCancelled(String type)
	{
		errorToCallback( "Internal error " + type );
	}

	protected void errorLoading(String type, String message, String title)
	{
		errorToCallback( "Error " + type + " | " + title + " - " + message );
	}

	protected void loadPdf()
	{
		out().println( "loadPdf, inputFileName: " +  getInputFileName() );
		Thread loadThread = new LoadPDFThread( createLoadPdfController(), getInputFileName(), _pdfDocument );
		loadThread.start();
	}

	protected PdfDocumentWrapper createPdfDocument()
	{
		PdfDocumentWrapper doc = _applicationContext.getPdfFactory().createPdfDocumentWrapper();

		return( doc );
	}

	protected void errorToCallback( String errorMessage )
	{
		errorToCallback( null, errorMessage );
	}

	protected void errorToCallback( Exception ex, String errorMessage )
	{
		invokeCallback( createErrorResult( ex, errorMessage ) );
	}

	protected Pdf2pgnResult createResult()
	{
		return( new Pdf2pgnResult() );
	}

	protected Pdf2pgnResult createErrorResult( Exception ex, String errorMessage )
	{
		Pdf2pgnResult result = createResult();
		result.setWasSuccessful(false);
		result.setErrorMessage(errorMessage);
		result.setException(ex);

		return( result );
	}

	protected Pdf2pgnResult createSuccessResult( List<ChessGame> list )
	{
		Pdf2pgnResult result = createResult();
		result.setWasSuccessful(true);
		result.setListOfGames(list);

		return( result );
	}

	protected void successToCallback( List<ChessGame> list )
	{
		invokeCallback( createSuccessResult( list ) );
	}

	protected void invokeCallback( Pdf2pgnResult result )
	{
		if( _callback != null )
			_callback.accept(result);
	}

	protected ImagePositionController createImagePositionController()
	{
		return( new ImagePositionControllerBase( getApplicationContext() ) );
	}
/*
	protected ChessGameControllerInterface createImagePositionController()
	{
		return( null );
	}
*/
	protected UpdatingProgress getUpdatingProgress()
	{
		return( _updatingProgress );
	}

	protected PgnChessFile createPgnChessFile()
	{
		return( new PgnChessFile() );
	}

	protected void savePgn( List<ChessGame> list, String pgnFileName )
	{
		Thread saveThread = saveThread = new SavePGNThread( createLoadChessControllerForSavePgn(),
															createPgnChessFile(),
															list, pgnFileName );
		saveThread.start();
	}

	protected LoadChessControllerInterface createLoadChessControllerForSavePgn()
	{
		LoadChessControllerInterface result = new LoadChessController(SAVING_PGN);

		return( result );
	}

	protected class LoadPdfController implements LoadPdfControllerInterface
	{
		String _type = null;

		public LoadPdfController( String type )
		{
			_type = type;
		}

		public String getType() {
			return _type;
		}

		@Override
		public void newPdfLoaded(PdfDocumentWrapper pdfDocument) {
			pdfLoaded(_type, pdfDocument);
		}

		@Override
		public void startLoading() {
			startLoadingPdf(_type);
		}

		@Override
		public void endLoading() {
			finishedLoading(_type);
		}

		@Override
		public void cancelLoading() {
			pdfLoadCancelled(_type);
		}

		@Override
		public void showLoadingError(String message, String title) {
			errorLoading( _type, message, title );
		}
	}

	protected class LoadChessController extends LoadPdfController
												implements LoadChessControllerInterface
	{
		public LoadChessController( String type )
		{
			super( type );
		}

		@Override
		public void newChessListGameLoaded(List<ChessGame> list, PgnChessFile pgnFile) {
			newChessListGameLoadedInternal(getType(), list);
		}
	}

	protected void addListeners()
	{
		_pendingItemsListener = createPendingItemsListener();

		getApplicationContext().getChessBoardRecognizerWhole().getChessBoardRecognitionTrainingThread().addListenerGen(_pendingItemsListener);
	}

	protected void removeListeners()
	{
		getApplicationContext().getChessBoardRecognizerWhole().getChessBoardRecognitionTrainingThread().removeListenerGen(_pendingItemsListener);
	}

	protected PendingItemsListener createPendingItemsListener()
	{
		return( ( obs, num ) -> newPendingTrainingItems( num ) );
	}

	protected synchronized void newPendingTrainingItems( int num )
	{
		_pendingTrainingItems = num;
		if( _hasFinishedScanning && !_hasSavedPgn )
		{
			outputPendingTrainingProgress();
			if( _pendingTrainingItems == 0 )
			{
				_hasSavedPgn = true;
				savePgn( _listOfGames );
			}
		}
	}

	protected void outputPendingTrainingProgress()
	{
		out().println( String.format( "Pending images:   %d", _pendingTrainingItems ) );
	}

	public void releaseResources()
	{
		removeListeners();
	}
}
