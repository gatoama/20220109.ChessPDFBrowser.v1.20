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

import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.FontFunctions;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import com.frojasg1.general.desktop.generic.view.SimpleViewTextComponent;
import com.frojasg1.general.desktop.view.FrameworkComponentFunctions;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.desktop.view.color.ColorThemeInvertible;
import com.frojasg1.general.document.formatter.ExternalTextFormatter;
import com.frojasg1.general.document.formatter.FormatterFactory;
import com.frojasg1.general.listeners.GenericListener;
import com.frojasg1.general.listeners.GenericObserved;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ZoomDocumentFormatter extends FormatterBase
									implements ChangeZoomFactorClientInterface,
														GenericObserved,
														SizeChangedObserved
{
	protected Integer _currentDefaultFontSize;

	protected Map< String, Style > _mapOfStyles = new HashMap<>();

	protected ExternalTextFormatter _textFormatter = null;
	protected ChangeZoomFactorServerInterface _changeZoomFactorServer;

	protected Map< String, Font > _mapOfFonts = new HashMap<>();

	protected ListOfDocumentFormatterListeners _listenerDocFormatContainer = new ListOfDocumentFormatterListeners();
	protected ListOfSizeChangedListeners _listenerSizeChangedContainer = new ListOfSizeChangedListeners();

	protected int _maxHorizontalDimensionForView = Integer.MAX_VALUE;

	protected boolean _formating = false;

	protected FormatterListener _formatterListener = null;

	public ZoomDocumentFormatter( JTextPane pane, ChangeZoomFactorServerInterface changeZoomFactorServer )
	{
		super( pane );
		_changeZoomFactorServer = changeZoomFactorServer;
		initializeZoomDocumentFormatter();
	}

	public void dispose()
	{
		stopFormatterListener();

		_formatterListener = null;
	}

	protected void startFormatterListener()
	{
		if( _formatterListener != null )
			_formatterListener.setNewJTextPane(_pane);
	}

	protected void stopFormatterListener()
	{
		if( _formatterListener != null )
			_formatterListener.dispose();
	}

	public FormatterListener getFormatterListener()
	{
		return( _formatterListener );
	}

	public void setFormatterListener( FormatterListener formatterListener )
	{
		stopFormatterListener();

		_formatterListener = formatterListener;

		startFormatterListener();
	}

	@Override
	public void setNewJTextPane( JTextPane jtp )
	{
		if( jtp != _pane )
		{
			super.setNewJTextPane( jtp );

			startFormatterListener();

			addStyles();

			if( _changeZoomFactorServer != null )
				changeZoomFactor( _changeZoomFactorServer.getZoomFactor() );

			resetText();
//			reformat();
		}
	}

	protected void shrinkTextJPane()
	{
		JScrollPane jsp = getScrollPane();
		if( jsp != null )
		{
			Dimension dimen = jsp.getSize();
			int delta = 3 * ( ( dimen.width % 2 ) * 2 - 1 );
			dimen.width = Math.max( 0, dimen.width + delta );

			jsp.setSize( dimen );
		}
	}

	public void setMaxHorizontalDimension( int maxHorizontalDimension )
	{
		_maxHorizontalDimensionForView = maxHorizontalDimension;
	}

	public int getMaxHorizontalDimension()
	{
		return( _maxHorizontalDimensionForView );
	}

	protected FormatForText createFormatForText( String text, int start, String styleName )
	{
		return( FormatterFactory.instance().createFormatForText(text, start, styleName ) );
	}

	public void setExternalTextFormatter( ExternalTextFormatter externalTextFormatter )
	{
		_textFormatter = externalTextFormatter;
	}

	protected Style newFormattedStyleToBeModifiedGen( String newStyleName, String styleNameToBeBasedOn )
	{
		Style result = super.newFormattedStyleToBeModifiedGen( newStyleName, styleNameToBeBasedOn );

		_mapOfStyles.put( newStyleName, result );

		return( result );
	}

	protected void addStyles( int defaultFontSize )
	{
		if( getJTextPane() != null )
		{
			_mapOfFonts.clear();

			super.addStyles( defaultFontSize );
		}
	}

	protected void initializeZoomDocumentFormatter()
	{
		addStyles();
		ChangeZoomFactorServerInterface changeZoomFactorServer = _changeZoomFactorServer;
		_changeZoomFactorServer = null;
		this.registerToChangeZoomFactorAsObserver(changeZoomFactorServer);

		if( _changeZoomFactorServer != null )
			this.changeZoomFactor( _changeZoomFactorServer.getZoomFactor() );
	}

	protected Style getStyle( String styleName )
	{
		return( _mapOfStyles.get( styleName ) );
	}

	@Override
	public double getZoomFactor()
	{
		double result = 1.0D;
		if( _changeZoomFactorServer != null )
			result = _changeZoomFactorServer.getZoomFactor();

		return( result );
	}

//	protected Dimension formatSubstring( FormatForText fft )
	protected void formatSubstring( FormatForText fft )
	{
//		Dimension result = null;
		String substr = null;

		if( fft != null )
		{
			Style style = getStyle( fft.getStyleName() );

			try
			{
				substr = _pane.getStyledDocument().getText( fft.getStart(), fft.getLength() );
				if( fft.getText() != null )
				{
					if( !substr.equals( fft.getText() ) )
						throw( new RuntimeException( "Error: Texts do not match. Text in document: " + substr +
														" . Text to be formatted: " + fft.getText() ) );
				}
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}

//			result = calculateTextDimensions( substr, fft.getStyleName() );
				giveStyleToText(substr, fft.getStart(), fft.getLength(), style);
		}

//		return( result );
	}

/*
//	protected Dimension formatSubstring( FormatForText fft )
	protected FormatForText formatSubstring( int start, int length, String text, String styleName )
	{
//		Dimension result = null;
		String substr = null;

		Style style = getStyle( styleName );

		try
		{
			substr = _pane.getStyledDocument().getText( start, length );
			if( text != null )
			{
				if( !substr.equals( text ) )
					throw( new RuntimeException( "Error: Texts do not match. Text in document: " + substr +
													" . Text to be formatted: " + fft.getText() ) );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
		_pane.getStyledDocument().setCharacterAttributes( start, length, style, true);
	}
*/

	public void reformat()
	{
		formatDocument();
	}

	public void formatDocument()
	{
		SwingUtilities.invokeLater( () -> {
			try
			{
				formatDocument(_pane.getDocument(), _pane.getCaretPosition() );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		});
	}

	protected void resetFormat()
	{
		_pane.getStyledDocument().setCharacterAttributes(0, _pane.getStyledDocument().getLength(), _pane.getStyle( getDefaultStyleName() ), true);
	}

	public synchronized void formatDocument( Document doc, int initialPosition ) throws ZoomDocumentFormatterOnTheFly_markingBrackets.CharacterAnalyserException, BadLocationException
	{
////			int endPosition = _pane.getSelectionEnd();
//		_pane.getStyledDocument().setCharacterAttributes(0, _pane.getStyledDocument().getLength(), _pane.getStyle( getDefaultStyleName() ), true);

		String text = getPaneText();

		invokeExternalFormatterToFormatText( text );
/*
		if( ( initialPosition >= 0 ) && ( initialPosition < _pane.getStyledDocument().getLength() ) )
			SwingUtilities.invokeLater( () -> _pane.setCaretPosition( initialPosition ) );
*/
	}

	protected Collection< FormatForText > formatTextWithExternalFormatter( String text )
	{
		Collection< FormatForText > result = null;
		if( ( _textFormatter != null ) && ( text != null ) )
			result = _textFormatter.formatText( text );

		return( result );
	}

	protected void invokeExternalFormatterToFormatText( String text ) throws BadLocationException
	{
		Collection< FormatForText > formatForTextCollection = formatTextWithExternalFormatter(text);

		if( formatForTextCollection != null )
		{
			resetFormat();
			formatText( formatForTextCollection );
		}
	}

	protected boolean isFormatting()
	{
		return( _formating );
	}

	protected void formatText( Collection<FormatForText> collecTextFormat )
	{
		_formating = true;
		Iterator<FormatForText> it = collecTextFormat.iterator();
		while( it.hasNext() )
		{
			FormatForText item = it.next();
			formatSubstring( item );
		}
		SwingUtilities.invokeLater( () -> _formating = false );
	}

	protected void invokeDocumentFormatterListeners()
	{
		_listenerDocFormatContainer.stylesChanged(this);
	}

	public void invokeSizeChangedListeners()
	{
		try
		{
			Dimension newDimension = estimateTotalDimension( _maxHorizontalDimensionForView );
			_listenerSizeChangedContainer.sizeChanged(this, newDimension);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void invokeListeners()
	{
		reformat();
		invokeDocumentFormatterListeners();

		SwingUtilities.invokeLater( () -> invokeSizeChangedListeners() );
	}

	protected void invokeStylesChangedListener()
	{
		reformat();
		_listenerDocFormatContainer.stylesChanged(this);
	}

	@Override
	public void changeZoomFactor( double zoomFactor )
	{
		_currentDefaultFontSize = IntegerFunctions.zoomValueCeil( _fontSize_hundredPercent, zoomFactor );
		
		updateStyles();
	}

	protected Integer calculateFontSize()
	{
		Integer result = _currentDefaultFontSize;
		if( result == null )
			result = super.calculateFontSize();

		return( result );
	}

	protected void updateTexts()
	{
		invokeListeners();
	}

	@Override
	public void changeZoomFactor_centerMousePointer( double zoomFactor )
	{
		
	}

	@Override
	public void unregisterFromChangeZoomFactorAsObserver()
	{
		if( _changeZoomFactorServer != null )
			_changeZoomFactorServer.unregisterZoomFactorObserver(this);
	}

	@Override
	public void registerToChangeZoomFactorAsObserver( ChangeZoomFactorServerInterface conf )
	{
		unregisterFromChangeZoomFactorAsObserver();

		_changeZoomFactorServer = conf;
		if( conf != null )
			conf.registerZoomFactorObserver(this);
	}

	protected Font createFontFromStyleName( String styleName )
	{
		Font result = null;

		StyledDocument sd = _pane.getStyledDocument();

		Style style = sd.getStyle(styleName);
		if( style != null )
		{
			result = sd.getFont(style);
		}

		return( result );
	}

	protected Font getFontFromStyleName( String styleName )
	{
		Font result = null;
		
		if( styleName != null )
		{
			result = _mapOfFonts.get( styleName );

			if( result == null )
			{
				result = createFontFromStyleName( styleName );
				if( result != null )
					_mapOfFonts.put(styleName, result);
			}
		}

		return( result );
	}

	protected Dimension calculateTextDimensions( String text, String styleName )
	{
		Dimension result = null;
		Font font = getFontFromStyleName( styleName );

		if( ( font != null ) && ( text != null ) )
		{
			result = new Dimension(0,0);
			String[] lines = StringFunctions.instance().split( text, "\\n" );
			int start = 0;
			int endPlusOne = -1;
			if( lines != null )
			{
				endPlusOne = lines.length;
				if( text.length() > 0 )
				{
					if ( text.charAt( text.length() - 1 ) == '\n' ) 
						endPlusOne--;

					if( ( text.length() > 1 ) && ( text.charAt( 0 ) == '\n' ) )
						start++;
				}

				for( int ii=start; ii<endPlusOne; ii++ )
				{
					String str = ( lines[ii].length() > 0 ? lines[ii] : " " );

					Dimension dimenToAdd = FontFunctions.instance().getSizeOfText( _pane.getGraphics(), font, str );
					result = ViewFunctions.instance().addLineDimension( dimenToAdd, result );
				}
			}
		}

//		result = result = ViewFunctions.instance().addLineDimension( new Dimension( 1000, 0 ), result );

		return( result );
	}

	protected String getStyleName( int position )
	{
		return( null );
	}

	protected Dimension getDimensionOfOneChar( String ch, int position )
	{
		Font font = _pane.getStyledDocument().getFont( _pane.getStyledDocument().getCharacterElement(position).getAttributes() );
		Dimension result = FontFunctions.instance().getSizeOfText( _pane.getGraphics(), font, ch );

		return( result );
	}

	public Dimension getDimensionOfLine( String lineStr, int startOfLine, int maxWidth )
	{
		Dimension result = null;

		int totalWidth = 0;
		int totalHeight = 0;

		int widthOfLine = 0;
		int heightOfLine = 0;
		if( lineStr != null )
		{
			if( lineStr.length() == 0 )
				result = FontFunctions.instance().getSizeOfText( _pane.getGraphics(), _pane.getFont(), " " );
			else
			{
				for( int ii=0; ii<lineStr.length(); ii++ )
				{
					String ch = lineStr.substring( ii, ii+1 );

					Dimension dimenToAdd = getDimensionOfOneChar( ch, startOfLine + ii );

					if( dimenToAdd != null )
					{
						if( heightOfLine < dimenToAdd.height )
							heightOfLine = dimenToAdd.height;

						if( ( widthOfLine + dimenToAdd.width ) > maxWidth )
						{
							if( totalWidth < widthOfLine )
								totalWidth = widthOfLine;

							widthOfLine = dimenToAdd.width;
							totalHeight += heightOfLine;
							heightOfLine = 0;
						}
						else
							widthOfLine += dimenToAdd.width;
					}
				}

				totalHeight += heightOfLine + 9;
				if( widthOfLine > totalWidth )
					totalWidth = widthOfLine + 9;

				result = new Dimension( totalWidth, totalHeight );
			}
		}

		return( result );
	}

	public Dimension estimateTotalDimension( int maxWidth )
	{
		Dimension result = null;

		String text = null;
		try
		{
			text = _pane.getStyledDocument().getText(0, _pane.getStyledDocument().getLength() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		if( text != null )
		{
			result = new Dimension(0,0);
			String[] lines = StringFunctions.instance().split( text, "\n" );
			int start = 0;
			int endPlusOne = -1;
			if( lines != null )
			{
				endPlusOne = lines.length;
				if( text.length() > 0 )
				{
					if ( text.charAt( text.length() - 1 ) == '\n' ) 
						endPlusOne--;

					if( ( text.length() > 1 ) && ( text.charAt( 0 ) == '\n' ) )
						start++;
				}

				int startOfLine = start;
				for( int ii=start; ii<endPlusOne; ii++ )
				{
					String str = ( lines[ii].length() > 0 ? lines[ii] : " " );

					Dimension dimenOfLine = getDimensionOfLine( lines[ii], startOfLine, maxWidth );
//					Dimension dimenToAdd = FontFunctions.instance().getSizeOfText( _pane.getGraphics(), font, str );
					result = ViewFunctions.instance().addLineDimension( dimenOfLine, result );

					startOfLine++;
					startOfLine += str.length();
				}
			}
/*
			Border border = _pane.getBorder();
			if( border != null )
			{
				Insets insets = border.getBorderInsets(_pane);

				if( insets != null )
				{
					result.width += insets.left + insets.right;
					result.height += insets.top + insets.bottom;
				}
			}

			if( _pane.getParent() instanceof JViewport )
			{
				result = ViewFunctions.instance().atLeastAsBigAsParent( result, _pane.getParent() );
			}
*/
		}

//		System.out.println( "Total dimension: " + result );

		return( result );
	}

	@Override
	public void addListenerGen( GenericListener listener )
	{
		if( listener instanceof DocumentFormatterListener )
			addListener( (DocumentFormatterListener) listener);
		else if( listener instanceof SizeChangedListener )
			addListener( (SizeChangedListener) listener);
		else
			throw( new IllegalArgumentException( "Listener type not supported" ) );
	}

	public void addListener( DocumentFormatterListener listener )
	{
		_listenerDocFormatContainer.add( listener);
	}

	@Override
	public void addListener( SizeChangedListener listener )
	{
		_listenerSizeChangedContainer.add( listener );
		Dimension size = estimateTotalDimension( _maxHorizontalDimensionForView );
		listener.sizeChanged(this, size);
	}

	@Override
	public void removeListenerGen( GenericListener listener )
	{
		if( listener instanceof DocumentFormatterListener )
			removeListener( (DocumentFormatterListener) listener);
		else if( listener instanceof SizeChangedListener )
			removeListener( (SizeChangedListener) listener);
		else
			throw( new IllegalArgumentException( "Listener type not supported" ) );
	}

	public void removeListener( DocumentFormatterListener listener )
	{
		_listenerDocFormatContainer.remove(listener);
	}

	@Override
	public void removeListener( SizeChangedListener listener )
	{
		_listenerSizeChangedContainer.remove(listener);
	}

	@Override
	protected void invertColorsInternal( ColorInversor colorInversor )
	{
		super.invertColorsInternal( colorInversor );
		if( (this != _textFormatter) && ( _textFormatter instanceof ExternalTextFormatterDesktop ) )
			( (ExternalTextFormatterDesktop) _textFormatter).invertColors( colorInversor );

		if( _currentDefaultFontSize != null )
			SwingUtilities.invokeLater( () -> updateStyles() );
	}
}
