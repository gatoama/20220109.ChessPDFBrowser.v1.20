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
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.desktop.view.text.link.imp.JTextComponentGenLinkListener;
import com.frojasg1.general.desktop.view.text.link.imp.LinkListener;
import com.frojasg1.general.desktop.view.text.link.imp.LinkServer;
import com.frojasg1.general.document.formatted.FormattedString;
import com.frojasg1.general.document.formatted.text.FormattedText;
import com.frojasg1.general.document.formatted.text.imp.FormattedGenericText;
import com.frojasg1.general.document.formatted.text.imp.FormattedTextList;
import com.frojasg1.general.document.formatted.text.imp.FormattedTextListElem;
import com.frojasg1.general.document.formatted.text.imp.FormattedTextParagraph;
import com.frojasg1.general.document.formatter.FormatterUtils;
import com.frojasg1.general.number.IntegerReference;
import com.frojasg1.general.string.StringFunctions;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JTextPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class ZoomDocumentFormatAppender<MM> extends ZoomDocumentFormatter implements LinkServer<MM>
{
	protected Collection< FormatForText > _formatHistory = new ArrayList<>();

	protected JTextComponentGenLinkListener<MM> _linkListener = null;

	protected boolean _addToHistory = true;

	protected Class<MM> _mClass = null;

	public ZoomDocumentFormatAppender( JTextPane pane, ChangeZoomFactorServerInterface changeZoomFactorServer )
	{
		this( pane, changeZoomFactorServer, null );
	}

	public ZoomDocumentFormatAppender( JTextPane pane, ChangeZoomFactorServerInterface changeZoomFactorServer,
								Class<MM> mClass )
	{
		super( pane, changeZoomFactorServer );

		_mClass = mClass;
		_linkListener = createLinkListener();
		_linkListener.setJTextComponent(pane);
	}

	@Override
	protected String getStyleName( int position )
	{
		String result = null;

		FormatForText fft = _linkListener.getKey( position );
		if( fft != null )
			result = fft.getStyleName();

		if( result == null )
			result = getDefaultStyleName();

		return( result );
	}

	protected Dimension getDimensionOfOneChar( String ch, int position )
	{
		Dimension result = null;
/*		// JDK hangs, so commented.
		if( getViewTextComponent() != null )
		{
			Rectangle bounds = getViewTextComponent().getCharacterBounds( position );
			if( bounds != null )
				result = new Dimension( bounds.width, bounds.height );
		}
		else
*/
		{
			String styleName = getStyleName( position );
			if( styleName != null )
			{
				result = calculateTextDimensions(ch, styleName);
			}
		}
/*
		if( result == null )
		{
			result = FontFunctions.instance().getSizeOfText( _pane.getGraphics(), getFont(position), ch );
		}

		Dimension result2 = super.getDimensionOfOneChar(ch, position);

		if( result.width != result2.width )
			System.out.println( "size child: " + result + ", size parent: " + result2 );
*/
		return( result );
	}

	protected Font getFont( int position )
	{
		Font result = null;
		FormatForText fft = null;
		if( _linkListener != null )
			fft = _linkListener.getKey( position );

		String styleName = getDefaultStyleName();
		if( fft != null )
			styleName = fft.getStyleName();

		result = getFontFromStyleName( styleName );

		return( result );
	}

	protected JTextComponentGenLinkListener<MM> createLinkListener()
	{
		return( (_mClass == null) ? null : new JTextComponentGenLinkListener<>(_mClass) );
	}

	@Override
	public void setNewJTextPane( JTextPane jtp )
	{
		super.setNewJTextPane(jtp);

		if( _linkListener != null )
			_linkListener.setJTextComponent(jtp);
	}

	@Override
	public void addLinkListener(LinkListener<MM> listener)
	{
		_linkListener.addLinkListener(listener);
	}

	@Override
	public void removeLinkListener(LinkListener<MM> listener)
	{
		_linkListener.removeLinkListener(listener);
	}

	protected int appendText( String text )
	{
		return( appendText( text, null ) );
	}

	protected int appendText( String text, String styleName )
	{
		int offset = getTextLength();

		try
		{
/*			try
			{
				_pane.getStyledDocument().insertString( offset, text, null );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
*/
			_pane.getStyledDocument().insertString( offset, text, null );

			int position = _pane.getStyledDocument().getLength();
			try
			{
				_pane.setSelectionStart( position );
				_pane.setSelectionEnd( position );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}

			if( styleName != null )
				appendFormat( 0, text.length(), text, styleName, offset);
		}
		catch( Exception ex )
		{
			offset = -1;
			ex.printStackTrace();
		}

		return( offset );
	}

	protected void appendIndentation( String indentation )
	{
		if( ! StringFunctions.instance().isEmpty( indentation ) )
			appendText( indentation );
	}

	public void appendFormattedText( FormattedText formattedText )
	{
		appendFormattedText( formattedText, null, null, false );
	}

	protected void appendReturnCarriage()
	{
		appendText( "\n" );
	}

	protected void appendFormattedText( FormattedText formattedText, String indentation,
										IntegerReference elemIndex, boolean elementIndentationAlreadyApplied )
	{
		if( formattedText instanceof FormattedTextParagraph )
		{
			elementIndentationAlreadyApplied = appendFormattedStringWitnIndentation( formattedText,
																					indentation,
																					elementIndentationAlreadyApplied );
		}
		else if( formattedText instanceof FormattedGenericText )
		{
			elementIndentationAlreadyApplied = appendFormattedStringWitnIndentation( formattedText,
																					indentation,
																					elementIndentationAlreadyApplied );
		}
		else if( formattedText instanceof FormattedTextListElem )
		{
			if( ! ( formattedText.getParent() instanceof FormattedTextList ) )
			{
				throw( new RuntimeException( "Parent of FormattedTextListElem must be instanceof FormattedTextList" ) );
			}

			FormattedTextList ftList = (FormattedTextList) formattedText.getParent();

			String elementPrefix = ftList.getStringPrefixOfElement( elemIndex._value );
			elemIndex._value++;

			if( elementIndentationAlreadyApplied )
				appendReturnCarriage();

			applyIndentation( formattedText,  indentation );
			appendText( elementPrefix );
			appendFormattedText_internal( formattedText );

			elementIndentationAlreadyApplied = true;
		}
		else if( formattedText instanceof FormattedTextList )
		{
			if( indentation == null )
				indentation = getIndentationStepString();
			else
				indentation = getIndentationStepString() + indentation;

			elemIndex = new IntegerReference(1);
		}

		FormattedText childFt = null;
		for( int ii = 0; ( childFt = formattedText.getElem( ii ) ) != null; ii++ )
		{
			appendFormattedText( childFt, indentation, elemIndex, elementIndentationAlreadyApplied );
		}
	}

	protected void applyIndentation( FormattedText ft, String indentation )
	{
		if( ft != null )
		{
			int indentationInt = ft.getAttributes().getLeftIndentation();

			String totalIndent = indentation;
			if( indentationInt > 0 )
			{
				totalIndent = StringFunctions.instance().repeat( " ", indentationInt );

				if( indentation != null )
					totalIndent +=  indentation;
			}

			appendIndentation( totalIndent );
		}
	}

	protected boolean applyIndentationIfNotEmpty( FormattedText ft, String indentation )
	{
		boolean result = false;
		if( ft != null )
		{
			FormattedString fs = ft.getFormattedString();

			if( ! isEmpty( fs ) )
			{
				applyIndentation( ft, indentation );
				result = true;
			}
		}

		return( result );
	}

	protected void appendFormattedText_internal( FormattedText ft )
	{
		if( ft != null )
		{
			FormattedString fs = ft.getFormattedString();

			if( ! isEmpty( fs ) )
			{
				appendFormattedText( fs );
				appendReturnCarriage();
			}
		}
	}

	protected boolean appendFormattedStringWitnIndentation( FormattedText ft,
															String indentation,
															boolean elementIndentationAlreadyApplied )
	{
		boolean result = elementIndentationAlreadyApplied;

		if( result && ( ft != null ) && ( ft.getFormattedString() != null ) )
		{
			result = false;
		}
		else
			applyIndentationIfNotEmpty( ft,  indentation );

		appendFormattedText_internal( ft );

		return( result );
	}

	protected boolean isEmpty( FormattedString fs )
	{
		return( ( fs == null ) || ( fs.isEmpty() ) );
	}

	protected String getIndentationStepString()
	{
		return( "    " );
	}

	public void appendFormattedText( FormattedString fs )
	{
		if( ! isEmpty( fs ) )
		{
			appendFormattedText( fs.getString(), fs.getFormatCol() );
		}
	}

	protected Collection< FormatForText > setDefaultFormatForFormattedText( String text,
											Collection< FormatForText > formats,
											String defaultFormatStyleName )
	{
		Collection< FormatForText > result = formats;
		
		if( text != null )
		{
			result = new ArrayList<>();

			int position = 0;
			int length = 0;
			for( FormatForText item: formats )
			{
				length = item.getStart() - position;
				if( length > 0 )
					result.add( createFormatForText(text.substring( position, item.getStart() ),
													position,
													defaultFormatStyleName ) );

				result.add( item );

				position = item.getStart() + item.getLength();
			}

			if( position < text.length() )
				result.add( createFormatForText(text.substring( position ),
												position,
												defaultFormatStyleName ) );
		}

		return( result );
	}

	protected void appendFormattedText( String text, Collection< FormatForText > formats, String defaultFormatStyleName )
	{
		appendFormattedText( text, setDefaultFormatForFormattedText( text, formats, defaultFormatStyleName ) );
	}

//	protected Dimension appendFormattedText( String text, Collection< FormatForText > formats )
	protected void appendFormattedText( String text, Collection< FormatForText > formats )
	{
		try
		{
			int offset = appendText( text );

			if( formats != null )
			{
				for( FormatForText fft: formats )
				{
					appendFormat( fft, offset );
				}
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void appendFormattedText( String text, FormatForText fft )
	{
		try
		{
			int offset = appendText( text );

			if( fft != null )
			{
				appendFormat( fft, offset );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	public void appendNormalText( String text )
	{
		if( text != null )
		{
			appendText(text, getDefaultStyleName() );
		}
	}

	protected void appendFormattedText( FormatForText fft )
	{
		try
		{
			if( fft != null )
			{
				int offset = appendText( fft.getText() );

				appendFormat( fft, offset );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}

	protected void appendFormat( int start, int length, String text, String styleName, int offset )
	{
		FormatForText fft = this.createFormatForText(text, start + offset, styleName);

		appendFormat( fft );
	}

	protected void appendFormat( FormatForText fft, int offset )
	{
		if( offset != 0 )
			fft = offsetFormatForText( fft, offset );

		appendFormat( fft );
	}

	protected void appendFormat( FormatForText fft )
	{
		formatSubstring( fft );
		if( _addToHistory )
			addToHistory( fft );
	}

	protected void addedToHistory( FormatForText fft )
	{
		if( (_linkListener != null ) && ( fft != null ) )
		{
			_linkListener.addLink(fft, null );
		}
	}

	protected void addToHistory( FormatForText fft )
	{
		_formatHistory.add(fft);

		addedToHistory( fft );
	}

	protected FormatForText offsetFormatForText( FormatForText original, int offset )
	{
		return( FormatterUtils.instance().offsetFormatForText( original, offset ) );
	}

	public void reformat()
	{
		_addToHistory = false;
		formatText( _formatHistory );
		_addToHistory = true;
	}

	public void setEmptyString()
	{
		_pane.setText( "" );
		_formatHistory.clear();
		if( _linkListener != null )
			_linkListener.clear();
	}

}
