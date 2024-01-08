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
package com.frojasg1.chesspdfbrowser.analysis;

import com.frojasg1.chesspdfbrowser.engine.model.chess.game.tree.MoveTreeNode;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.ChessEngineConfiguration;
import java.io.IOException;
import javax.xml.transform.TransformerException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface AnalysisWindowViewController
{
	public void setView( AnalysisWindowView view );

	public void setAnalysisProcess( Integer id, EngineAnalysisProcessData engineData );
	public void applyEngineConfiguration( Integer id, ChessEngineConfiguration chessEngineConfiguration ) throws IOException, TransformerException;

	public void startThinking( Integer id );
	public void stop( Integer id );
	public void closeAnalysisProcess( Integer id );

	public void addSubvariantAnalysisToGame( Integer id, double score, MoveTreeNode mtn );

	public void setNewPosition( MoveTreeNode mtn );
}
