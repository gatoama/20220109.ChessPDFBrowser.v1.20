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
package com.frojasg1.chesspdfbrowser.engine.io.parsers;

import com.frojasg1.chesspdfbrowser.engine.exception.ChessParserException;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.PDFPageSegmentatorInterface;
import com.frojasg1.general.progress.CancellationException;
import java.io.BufferedReader;
import java.util.List;

/**
 *
 * @author Usuario
 */
public interface ChessGameParser
{
	public List<ChessGame> parseChessGameText( String text,
												PDFPageSegmentatorInterface source,
												Integer initialPageToScanForGames,
												Integer finalPageToScanForGames ) throws ChessParserException, CancellationException;

	public List<ChessGame> parseChessGameText( BufferedReader reader) throws ChessParserException;
}
