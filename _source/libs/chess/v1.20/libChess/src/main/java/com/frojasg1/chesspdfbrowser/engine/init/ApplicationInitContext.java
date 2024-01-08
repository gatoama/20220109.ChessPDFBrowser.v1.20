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
package com.frojasg1.chesspdfbrowser.engine.init;

import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.io.parsers.tags.TagsExtractor;
import com.frojasg1.chesspdfbrowser.engine.view.chess.interaction.ChessGameControllerInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.ChessEngineConfigurationPersistency;
import com.frojasg1.chesspdfbrowser.model.regex.whole.items.ListOfRegexWholeFiles;
import com.frojasg1.chesspdfbrowser.model.regex.whole.loader.RegexFilesPersistency;
import com.frojasg1.chesspdfbrowser.recognizer.store.whole.ChessBoardRecognizerWhole;
import com.frojasg1.libpdf.api.PdfFactory;
import com.frojasg1.chesspdfbrowser.view.chess.completion.WholeCompletionManager;
import com.frojasg1.general.desktop.startapp.impl.GenericDesktopInitContextImpl;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ApplicationInitContext extends GenericDesktopInitContextImpl
{
	protected ChessGameControllerInterface _chessGameController = null;

	protected ChessEngineConfigurationPersistency _chessEngineConfigurationPersistency = null;

	protected RegexFilesPersistency _regexWholeContainerConf = null;
//	protected RegexWholeFileModel _regexConfWholeContainer = null;

	protected WholeCompletionManager _wholeCompletionManager = null;

	protected TagsExtractor _tagsExtractor = null;

	protected PdfFactory _pdfFactory = null;

	protected ChessBoardRecognizerWhole _chessBoardRecognizerWhole = null;
/*
	public void setRegexConfWholeContainer( RegexWholeFileModel value )
	{
		_regexWholeContainerConf.setRegexWholeContainer(value);
	}

	public RegexWholeFileModel getRegexConfWholeContainer()
	{
		return( _regexWholeContainerConf.getRegexWholeContainer() );
	}
*/

	public void setChessGameController( ChessGameControllerInterface chessGameController )
	{
		_chessGameController = chessGameController;
	}

	public ChessGameControllerInterface getChessGameController()
	{
		return( _chessGameController );
	}

	public void setListOfRegexWholeFiles( ListOfRegexWholeFiles value )
	{
		_regexWholeContainerConf.setCurrentModelContainer(value);
	}

	public ListOfRegexWholeFiles getListOfRegexWholeFiles()
	{
		return( _regexWholeContainerConf.getModelContainer() );
	}

	public RegexFilesPersistency getRegexWholeContainerPersistency()
	{
		return( _regexWholeContainerConf );
	}

	public void setRegexWholeContainerPersistency( RegexFilesPersistency value )
	{
		_regexWholeContainerConf = value;
	}

	public ChessEngineConfigurationPersistency getChessEngineConfigurationPersistency()
	{
		return( _chessEngineConfigurationPersistency );
	}

	public void setChessEngineConfigurationPersistency( ChessEngineConfigurationPersistency value )
	{
		_chessEngineConfigurationPersistency = value;
	}

	public void setWholeCompletionManager( WholeCompletionManager wholeCompletionManager )
	{
		_wholeCompletionManager = wholeCompletionManager;
	}

	public WholeCompletionManager getWholeCompletionManager( )
	{
		return( _wholeCompletionManager );
	}

	public void setTagsExtractor( TagsExtractor tagsExtractor )
	{
		_tagsExtractor = tagsExtractor;
	}

	public TagsExtractor getTagsExtractor()
	{
		return( _tagsExtractor );
	}

	public void setPdfFactory( PdfFactory factory )
	{
		_pdfFactory = factory;
	}

	public PdfFactory getPdfFactory()
	{
		return( _pdfFactory );
	}

	public void setChessBoardRecognizerWhole(ChessBoardRecognizerWhole chessBoardRecobnizerWhole)
	{
		this._chessBoardRecognizerWhole = chessBoardRecobnizerWhole;
	}

	public ChessBoardRecognizerWhole getChessBoardRecognizerWhole()
	{
		return( _chessBoardRecognizerWhole );
	}

	public ApplicationConfiguration getApplicationConfiguration()
	{
		return( ApplicationConfiguration.instance() );
	}
}
