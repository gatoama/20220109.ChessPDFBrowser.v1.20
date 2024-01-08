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
package com.frojasg1.general.desktop.view.document.formatter;

import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import com.frojasg1.general.desktop.generic.view.SimpleViewTextComponent;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class FormatterBase implements ColorThemeInvertible
{
	protected final String LOCAL_DEFAULT_STYLE = "LOCAL_DEFAULT_STYLE";
	protected final int DEFAULT_FONT_SIZE = 16;

	protected Integer _fontSize_hundredPercent = null;

	protected JTextPane _pane = null;
	protected DesktopViewTextComponent _viewTextComponent = null;

	protected Color[] _originalInvertibleColorModeColors;
	protected Color[] _invertedInvertibleColorModeColors;
	protected Color[] _invertibleColorModeColors;
	protected AtomicReference<Color> _defaultStyleBackgroundColor = new AtomicReference<>();
	protected AtomicReference<Color> _defaultStyleForegroundColor = new AtomicReference<>();

	public FormatterBase( JTextPane pane )
	{
		_pane = pane;

		initialize();
	}

	protected boolean areInvertibleColorsInitialized()
	{
		return( _invertibleColorModeColors != null );
	}

	public void setNewJTextPane( JTextPane jtp )
	{
		if( jtp != _pane )
		{
			_pane = jtp;
			_viewTextComponent = createViewTextComponent( jtp );
		}
	}

	protected void resetText()
	{
		String text = _viewTextComponent.getText();
		_viewTextComponent.setEmptyText();
		_viewTextComponent.setText( text );
	}

	protected JScrollPane getScrollPane()
	{
		return( ComponentFunctions.instance().getScrollPane(getJTextPane()) );
	}

	public JTextPane getJTextPane()
	{
		return( _pane );
	}

	protected DesktopViewTextComponent createViewTextComponent( JTextPane jtp )
	{
		return( new SimpleViewTextComponent( jtp ) );
	}

	public DesktopViewTextComponent getViewTextComponent()
	{
		return( _viewTextComponent );
	}

	protected int getTextLength()
	{
		return( _pane.getStyledDocument().getLength() );
	}

	protected Color getDefaultStyleColor( AtomicReference<Color> defaultOriginalColor, Color currentColor )
	{
		Color result = getOriginalColor( defaultOriginalColor, currentColor );
		if( isDarkMode() )
			result = getColorInversor().invertColor( result );

		return( result );
	}

	protected Color getOriginalColor( AtomicReference<Color> defaultOriginalColor, Color currentColor )
	{
		Color result = defaultOriginalColor.get();
		if( result == null )
		{
			defaultOriginalColor.set(currentColor);
			result = currentColor;
		}
		return( result );
	}

	protected boolean isDarkMode( Component comp )
	{
		return( FrameworkComponentFunctions.instance().isDarkMode( comp ) );
	}

	protected boolean isDarkMode()
	{
		return( isDarkMode( getJTextPane() ) );
	}

	protected ColorInversor getColorInversor( Component comp )
	{
		return( FrameworkComponentFunctions.instance().getColorInversor( comp ) );
	}

	protected ColorInversor getColorInversor()
	{
		return( getColorInversor( getJTextPane() ) );
	}

	protected Color getOriginalColor( Component comp, Color color )
	{
		Color result = color;

		if( isDarkMode(comp) )
			result = getColorInversor(comp).invertColor(color);

		return( result );
	}

	protected void setOriginalDefaultStyleColors()
	{
		if( _defaultStyleForegroundColor.get() == null )
			_defaultStyleForegroundColor.set( getOriginalColor( _pane, _pane.getForeground() ) );

		if( _defaultStyleBackgroundColor.get() == null )
			_defaultStyleBackgroundColor.set( getOriginalColor( _pane, _pane.getBackground() ) );
	}

	protected Style getLocalDefaultStyle( Integer fontSize )
	{
		Style result = newFormattedStyleToBeModifiedGen( getDefaultStyleName(), StyleContext.DEFAULT_STYLE);
		if( fontSize != null )
			StyleConstants.setFontSize(result, fontSize );

		setOriginalDefaultStyleColors();

		Color foreground = getDefaultStyleColor( _defaultStyleForegroundColor, StyleConstants.getForeground(result) );
		Color background = getDefaultStyleColor( _defaultStyleBackgroundColor, StyleConstants.getBackground(result) );

		StyleConstants.setForeground( result, foreground );
		StyleConstants.setBackground( result, background );

		return( result );
	}

	protected Style newFormattedStyleToBeModifiedGen( String newStyleName, String styleNameToBeBasedOn )
	{
		StyledDocument sd = _pane.getStyledDocument();
		// Create and add the main document style
		Style styleToBeBasedOn = sd.getStyle(styleNameToBeBasedOn);

		Style result = sd.getStyle( newStyleName );
		if( result != null )
			sd.removeStyle(newStyleName);

		result = sd.addStyle( newStyleName, styleToBeBasedOn);

		return( result );
	}

	protected String getDefaultStyleName()
	{
		return( LOCAL_DEFAULT_STYLE );
	}

	protected Style newFormattedStyleToBeModified( String newStyleName )
	{
		return( newFormattedStyleToBeModifiedGen( newStyleName, getDefaultStyleName() ) );
	}

	protected Style getLocalDefaultStyle()
	{
		return( getLocalDefaultStyle( calculateFontSize() ) );
	}

	protected void addStyles( )
	{
		addStyles( calculateFontSize() );
	}

	protected void addStyles( Integer defaultFontSize )
	{
		if( getJTextPane() != null )
		{
			// to create LocalDefaultStyle
			getLocalDefaultStyle(defaultFontSize);

			addParticularStyles(defaultFontSize);
		}
	}

	protected abstract void addParticularStyles( Integer defaultFontSize );
	/*
	{
		StyledDocument sd = _pane.getStyledDocument();
		// Create and add the main document style
		final Style bold = sd.addStyle(RED_BOLD, defaultStyle);
		StyleConstants.setFontSize(bold, StyleConstants.getFontSize( defaultStyle ) + 2);
		StyleConstants.setForeground(bold, Color.RED );
		StyleConstants.setBold(bold, true);

		final Style green = sd.addStyle(GREEN, defaultStyle);
		StyleConstants.setForeground(green, Color.green.darker() );
		StyleConstants.setBold(green, false);

		final Style plain = sd.addStyle(PLAIN, defaultStyle);
		StyleConstants.setForeground(plain, Color.BLACK );
		StyleConstants.setBold(plain, false);
	}
*/

	protected Integer calculateFontSize()
	{
		int result = DEFAULT_FONT_SIZE;

		if( _fontSize_hundredPercent != null )
			result = _fontSize_hundredPercent;
		else
		{
			if( _pane != null )
			{
				Font font = _pane.getFont();
				if( font != null )
					result = font.getSize();
			}
		}

		return( result );
	}

	protected abstract Color[] createOriginalInvertibleColors();

	protected Color[] createInvertedInvertibleColors()
	{
		return( ArrayFunctions.instance().createEmptyArray(_originalInvertibleColorModeColors) );
	}

	protected final void initialize()
	{
		_fontSize_hundredPercent = calculateFontSize();
		_viewTextComponent = createViewTextComponent( _pane );

		_originalInvertibleColorModeColors = createOriginalInvertibleColors();
		_invertibleColorModeColors = copy( createOriginalInvertibleColors() );
		_invertedInvertibleColorModeColors = createInvertedInvertibleColors();
	}

	protected Color[] copy( Color[] array )
	{
		Color[] result = null;
		if( array != null )
			result = Arrays.copyOf(array, array.length);

		return( result );
	}

	protected void giveStyleToText( String substr, int start, int length, Style style )
	{
		if( ( _pane != null ) && ( substr != null ) )
			for( int ii=0; ii<length; ii++ )
				if( substr.charAt(ii) != '\n' )
					_pane.getStyledDocument().setCharacterAttributes(start+ii, 1, style, true);
	}

	protected String getPaneText() throws BadLocationException
	{
		return( _pane.getStyledDocument().getText(0, _pane.getStyledDocument().getLength() ) );
	}

	protected abstract void updateTexts();

	protected void updateStyles()
	{
		addStyles();

		SwingUtilities.invokeLater(() -> { updateTexts(); });
	}

	protected Color getInvertibleColor( int index )
	{
		return( _invertibleColorModeColors[index] );
	}

	protected void invertInvertibleColors( ColorInversor colorInversor )
	{
		Color[] result = copy( _originalInvertibleColorModeColors );
		if( isDarkMode() )
			result = colorInversor.invertColors( result );

		_invertibleColorModeColors = result;

		if( ( result != null ) &&
			( _originalInvertibleColorModeColors != null ) &&
			( _invertedInvertibleColorModeColors != null ) )
		{
			for( int ii=0; ii<_invertedInvertibleColorModeColors.length; ii++ )
			{
				Color inverted = _invertedInvertibleColorModeColors[ii];
				
				if( ( inverted != null ) && ( result[ii] != _originalInvertibleColorModeColors[ii] ) )
					result[ii] = inverted;
			}
		}
	}

	protected void invertColorsInternal( ColorInversor colorInversor )
	{
		invertInvertibleColors( colorInversor );
		updateStyles();
	}

	@Override
	public void invertColors( ColorInversor colorInversor )
	{
		SwingUtilities.invokeLater( () -> invertColorsInternal( colorInversor ) );
	}
}
