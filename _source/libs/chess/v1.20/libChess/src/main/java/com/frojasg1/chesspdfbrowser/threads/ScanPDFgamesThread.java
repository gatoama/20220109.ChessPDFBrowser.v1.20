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
package com.frojasg1.chesspdfbrowser.threads;

import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.ParserConfiguration;
import com.frojasg1.chesspdfbrowser.engine.configuration.TagExtractorConfiguration;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.ChessGameParser;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.RawChessGameParser;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.implementation.rawparser3.RawImprovedChessGameParser3;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessLanguageConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.configuration.ChessViewConfiguration;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ImagePositionController;
import com.frojasg1.libpdf.api.PdfDocumentWrapper;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation.PDFPageSegmentator;
import com.frojasg1.general.progress.CancellationException;
import com.frojasg1.general.progress.GeneralUpdatingProgress;
import com.frojasg1.general.progress.UpdatingProgress;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public class ScanPDFgamesThread extends Thread
{
	protected LoadChessControllerInterface _loadController = null;
	protected List<ChessGame> _list = null;

	protected PdfDocumentWrapper _pdfDocument = null;

	protected GeneralUpdatingProgress _gup = null;

	protected boolean _letsStart = false;

	protected ApplicationConfiguration _config = null;

//	protected ParserConfiguration _config = null;
//	protected ChessLanguageConfiguration _chessLanguageConfig;
//	protected ChessViewConfiguration _chessViewConfig;
//	protected TagExtractorConfiguration _tagExtractorConfig;

	protected Integer _initialPageToScanForGames = null;
	protected Integer _finalPageToScanForGames = null;

	protected TagsExtractor _tagsExtractor = null;

	protected String _pdfBaseFileName = null;

	protected ImagePositionController _controller = null;

	public ScanPDFgamesThread( LoadChessControllerInterface loadController,
								PdfDocumentWrapper document,
								UpdatingProgress up,
								ApplicationConfiguration config,
//								ParserConfiguration config,
//								ChessLanguageConfiguration clc,
//								ChessViewConfiguration cvc,
//								TagExtractorConfiguration tec,
								TagsExtractor tagsExtractor,
								ImagePositionController controller,
								String pdfBaseFileName )
	{
		_tagsExtractor = tagsExtractor;
		_pdfBaseFileName = pdfBaseFileName;

		_controller = controller;
		_loadController = loadController;
		_pdfDocument = document;

		_gup = new GeneralUpdatingProgress( );
		_gup.up_setParentUpdatingProgress( up );
//		gup.up_prepareNextSlice( _totalNumberOfPages );

		_config = config;
	}

	public void setInitialPageToScanForGames( Integer initialPageToScanForGames )
	{
		_initialPageToScanForGames = initialPageToScanForGames;
	}

	public void setFinalPageToScanForGames( Integer finalPageToScanForGames )
	{
		_finalPageToScanForGames = finalPageToScanForGames;
	}

	public void letsStart()
	{
		_letsStart = true;
	}

	protected ChessGameParser createPdfParser()
	{
		ChessGameParser result = null;
/*
		if( _config.getUseImprovedPdfGameParser() )
			result = new RawImprovedChessGameParser3( ApplicationConfiguration.instance().getChessLanguageConfigurationToParseTextFrom(), _gup,
														ApplicationConfiguration.instance(),
														ApplicationConfiguration.instance(),
														_tagsExtractor,
														_controller,
														_pdfBaseFileName );
		else
			result = new RawChessGameParser( ApplicationConfiguration.instance().getChessLanguageConfigurationToParseTextFrom(),
											_gup, ApplicationConfiguration.instance(),
											_tagsExtractor,
											_controller,
											_pdfBaseFileName );
*/
		if( _config.getUseImprovedPdfGameParser() )
			result = new RawImprovedChessGameParser3( _config.getChessLanguageConfigurationToParseTextFrom(), _gup,
														_config,
														_config,
														_tagsExtractor,
														_controller,
														_pdfBaseFileName );
		else
			result = new RawChessGameParser( _config.getChessLanguageConfigurationToParseTextFrom(),
											_gup, _config,
											_tagsExtractor,
											_controller,
											_pdfBaseFileName );

		return( result );
	}

	@Override
	public void run()
	{
		boolean cancelDone = false;

		while( !_letsStart && !cancelDone )
		{
			try
			{
				Thread.sleep( 100 );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}

			cancelDone = false;
			if( ( _gup != null ) && ( _gup.up_getOperationCancellation() != null ) )
				cancelDone = _gup.up_getOperationCancellation().getHasToCancel();
		}

		if( ! cancelDone )
		{
			try
			{
//				ApplicationConfiguration conf = ApplicationConfiguration.instance();
				ApplicationConfiguration conf = _config;
				if( _loadController != null )	_loadController.startLoading();

				PDFPageSegmentator pageSegmentator = null;
				try
				{
					pageSegmentator = new PDFPageSegmentator( conf );
					pageSegmentator.setChessViewConfiguration( conf );

					_gup.up_reset( 1.0D );
					_gup.up_prepareNextSlice( 1.0D );
					pageSegmentator.initialize(_pdfDocument, 3, 30, _gup );
					_gup.up_performEnd();
				}
				catch( CancellationException ce )
				{
					cancelDone = true;
				}
				catch( Throwable th )
				{
					th.printStackTrace();
					if( _loadController != null )
					{
						_loadController.showLoadingError( "Error scanning pdf. ERROR: " + th.getMessage(), "Error scanning pdf" );
					}
				}

				try
				{
//					_gup.up_prepareNextSlice( 1.0D );
//					_gup.up_reset( 1.0D );
//					_gup.up_prepareNextSlice( 1.0D );
					ChessGameParser pdfGameParser = createPdfParser();
//					_gup.up_performEnd();

					_list = pdfGameParser.parseChessGameText( null, pageSegmentator,
															_initialPageToScanForGames,
															_finalPageToScanForGames );

					if( _loadController != null )
						_loadController.newChessListGameLoaded(_list, null); 
				}
				catch( CancellationException ce )
				{
					cancelDone = true;
				}
				catch( Throwable th )
				{
					th.printStackTrace();
					if( _loadController != null )
					{
						_loadController.showLoadingError( "ERROR: " + th.getMessage(), "Error scanning pdf" );
					}
				}
			}
			finally
			{
				if( _loadController != null )
					_loadController.endLoading();
			}
		}

		if( cancelDone && ( _loadController != null ) )
		{
			SwingUtilities.invokeLater(new Thread(){
				public void run()
				{
					_loadController.cancelLoading();
				}
			});
		}
	}

	public List<ChessGame> getGameList()
	{
		return( _list );
	}
}
