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
package com.frojasg1.chesspdfbrowser.view.chess.variantformat.impl;

import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.view.chess.variantformat.VariantFormatterForJTextPaneBase;
import com.frojasg1.general.ExecutionFunctions;
import javax.swing.JTextPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class VariantFormatterForJTextPaneForEngineAnalysis extends VariantFormatterForJTextPaneBase
{
	protected ChessGame _chessGame = null;

	protected double _score = 0.0D;


	public VariantFormatterForJTextPaneForEngineAnalysis( JTextPane textPane )
	{
		super( textPane );
	}

	public void init()
	{
		super.init();
	}

	@Override
	protected void setListOfMovesChild( String listOfMoves )
	{
		append( listOfMoves, BOLD_BLACK );
	}

	public void setSubvariantInfo( double score, ChessGame cg )
	{
		setScore( score );
		setChessGame( cg );

		clearText();

		paintScore( score );
		append( "  " );
		setListOfMoves( getVariantString( cg ) );
	}

	protected String getVariantString( ChessGame cg )
	{
		String result = null;

		if( cg != null )
			result = ExecutionFunctions.instance().safeFunctionExecution( () -> cg.getMoveListString( cg.getMoveTreeGame().getEndOfMainLine() ) );

		if( result == null )
			result = "";

		return( result );
	}

	protected String formatScore( double score )
	{
		return( String.format( "%.2f", score ) );
	}

	protected void paintScore( double score )
	{
		String styleName = (score < 0) ? BOLD_RED : BOLD_BLUE;

		append( formatScore( score ), styleName );
	}

	protected void updateEverything()
	{
		setSubvariantInfo( getScore(), getChessGame() );
	}

	public double getScore() {
		return _score;
	}

	public ChessGame getChessGame() {
		return _chessGame;
	}

	public void setScore(double _score) {
		this._score = _score;
	}

	public void setChessGame(ChessGame _chessGame) {
		this._chessGame = _chessGame;
	}
}
