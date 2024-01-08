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

import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.chesspdfbrowser.engine.io.notation.ChessMoveAlgebraicNotation;
import com.frojasg1.chesspdfbrowser.engine.model.chess.game.ChessGame;
import com.frojasg1.chesspdfbrowser.pdf.textsegmentation.implementation.Range;
import com.frojasg1.chesspdfbrowser.view.chess.variantformat.VariantFormatterForJTextPaneBase;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Component;
import javax.swing.JTextPane;
import javax.swing.text.Style;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class VariantFormatterForJTextPaneForCommentWindow extends VariantFormatterForJTextPaneBase
{
	protected ChessGame _chessGame = null;

	public VariantFormatterForJTextPaneForCommentWindow( JTextPane textPane )
	{
		super( textPane );
	}

	public void init()
	{
		super.init();
	}

	public void setChessGame( ChessGame chessGame )
	{
		_chessGame = chessGame;
	}

	protected Range getLastMoveRange( String listOfMoves )
	{
		Range result = new Range(0,0);

		if( ( listOfMoves != null ) &&
			( listOfMoves.length() > 2 ) )
		{
			int end = listOfMoves.length() - 2;
			boolean found = false;

			while( ( end > -1 ) && !found )
			{
				int start = StringFunctions.instance().lastIndexOfAnyChar(listOfMoves, " .", end ) + 1;
				if( start < 0 )
					start = 0;

				if( start <= end )
				{
					found = isAMove( listOfMoves.substring( start, end + 1 ) );

					if( found )
						result = new Range( start, end );
				}

				end = start - 2;
			}
		}

		return( result );
	}

	protected boolean isAMove( String text )
	{
		boolean result = false;

		if( _chessGame != null )
		{
			String translatedText = null;
			
			translatedText = _chessGame.getChessViewConfiguration().getChessLanguageConfigurationToShow().translateMoveStringToEnglish(text, null);
			result = ChessMoveAlgebraicNotation.getInstance().isItAChessMoveString(translatedText);
		}

		return( result );
	}

	@Override
	protected void setListOfMovesChild( String listOfMoves )
	{
		Range rangeOfLastMove = getLastMoveRange( listOfMoves );

		if( ( rangeOfLastMove != null ) && ( listOfMoves != null ) )
		{
			clearText();
			String lastMoveStr = listOfMoves.substring( rangeOfLastMove.getInitial(), rangeOfLastMove.getFinal() + 1 );
			String previousMovesStr = listOfMoves.substring( 0, rangeOfLastMove.getInitial() );

			append( previousMovesStr, DEFAULT_STYLE_RESIZED );
			append( lastMoveStr, BOLD_BLACK );

			Component focusOwner = JFrameInternationalization.getFocusedComponent();
			getJTextPane().requestFocus();

			getJTextPane().setSelectionStart( rangeOfLastMove.getInitial() );
			getJTextPane().setSelectionEnd( rangeOfLastMove.getFinal() + 1 );
			getJTextPane().repaint();

			if( focusOwner != null )
				focusOwner.requestFocus();
		}
	}
}
