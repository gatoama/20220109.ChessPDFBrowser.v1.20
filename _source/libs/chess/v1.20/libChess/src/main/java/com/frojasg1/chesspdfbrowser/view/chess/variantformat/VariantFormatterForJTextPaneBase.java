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
package com.frojasg1.chesspdfbrowser.view.chess.variantformat;

import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.document.formatter.FormatterBase;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class VariantFormatterForJTextPaneBase extends FormatterBase
{
	protected static final String BOLD_BLACK = "BOLD_BLACK";
	protected static final String DEFAULT_STYLE_RESIZED = "DEFAULT_STYLE_RESIZED";
	protected static final String GREEN = "GREEN";
	protected static final String BOLD_RED = "BOLD_RED";
	protected static final String BOLD_BLUE = "BOLD_BLUE";

	protected static final int INVERTIBLE_BLACK_COLOR_INDEX = 0;
//	protected static final int INVERTIBLE_RED_COLOR_INDEX = 1;
//	protected static final int INVERTIBLE_BLUE_COLOR_INDEX = 2;

	protected static Color[] _originalPutOutableColorModeColors = new Color[] {
		Color.BLACK,
//		Color.RED,
//		Color.BLUE
	};

	protected double _currentZoomFactorForComponentResized = 1.0D;

	protected String _listOfMoves = null;

	public VariantFormatterForJTextPaneBase( JTextPane textPane )
	{
		super( textPane );
	}

	public void init()
	{
		addStyles();
	}

	@Override
	protected Integer calculateFontSize()
	{
		return( null );
	}

	protected void addParticularStyles( Integer defaultFontSize )
	{
		StyledDocument sd = getJTextPane().getStyledDocument();

		int fontSize = getJTextPane().getFont().getSize();

		Style defaultStyle = sd.getStyle(StyleContext.DEFAULT_STYLE);

		final Style defaultStyleResized = sd.addStyle(DEFAULT_STYLE_RESIZED, defaultStyle);
		StyleConstants.setFontSize(defaultStyle, fontSize );

		final Style boldBlack = sd.addStyle(BOLD_BLACK, defaultStyleResized);
//		StyleConstants.setFontSize(bold, StyleConstants.getFontSize( defaultStyleResized ) );
		StyleConstants.setForeground(boldBlack, getInvertibleColor(INVERTIBLE_BLACK_COLOR_INDEX) );
		StyleConstants.setBold(boldBlack, true);

		final Style boldRed = sd.addStyle(BOLD_RED, defaultStyleResized);
//		StyleConstants.setFontSize(bold, StyleConstants.getFontSize( defaultStyleResized ) );
		StyleConstants.setForeground(boldRed, Color.RED );
		StyleConstants.setBold(boldRed, true);

		final Style boldBlue = sd.addStyle(BOLD_BLUE, defaultStyleResized);
//		StyleConstants.setFontSize(bold, StyleConstants.getFontSize( defaultStyleResized ) );
		StyleConstants.setForeground(boldBlue, Color.BLUE );
		StyleConstants.setBold(boldBlue, true);
	}

	protected abstract void setListOfMovesChild( String listOfMoves );

	public void setListOfMoves( String listOfMoves )
	{
		_listOfMoves = listOfMoves;

		updateListOfMoves();
	}

	protected void updateListOfMoves()
	{
		setListOfMovesChild( _listOfMoves );
	}

	protected void updateEverything()
	{
		updateListOfMoves();
	}

	@Override
	protected void updateTexts()
	{
		updateEverything();
	}

	public void changeZoomFactor(double newZoomFactor)
	{
		if( _currentZoomFactorForComponentResized != newZoomFactor )
		{
			addStyles();
			updateEverything();
			_currentZoomFactorForComponentResized = newZoomFactor;
		}
	}

	public void dispose()
	{
		setNewJTextPane( null );
	}

	protected void clearText()
	{
		getJTextPane().setText("");
	}

	protected void append( String text, String styleName )
	{
		int start = getJTextPane().getStyledDocument().getLength();
		int length = text.length();

		append( text );

		Style style = getStyle( styleName );

		SwingUtilities.invokeLater( () -> giveStyleToText( text, start, length, style ) );
	}

	protected String translateText( String text )
	{
		String result = text;
		if( isDarkMode() )
			result = StringFunctions.instance().replaceSetOfChars(text, "♔♕♖♗♘♚♛♜♝♞", "♚♛♜♝♞♔♕♖♗♘");

		return( result );
	}

	protected synchronized void append( String text )
	{
		String newText = translateText( text );
		int start = getJTextPane().getStyledDocument().getLength();
		ExecutionFunctions.instance().safeMethodExecution( () -> getJTextPane().getStyledDocument().insertString(start, newText, null) );
	}

	protected Style getStyle( String styleName )
	{
		StyledDocument sd = getJTextPane().getStyledDocument();
		// Create and add the main document style
		Style result = sd.getStyle(styleName);

		return( result );
	}

	@Override
	protected Color[] createOriginalInvertibleColors() {
		return( _originalPutOutableColorModeColors );
	}
}
