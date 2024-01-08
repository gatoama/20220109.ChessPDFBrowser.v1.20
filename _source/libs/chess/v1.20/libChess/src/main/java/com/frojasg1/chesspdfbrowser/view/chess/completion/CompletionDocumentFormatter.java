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
package com.frojasg1.chesspdfbrowser.view.chess.completion;

import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.desktop.completion.base.CompletionDocumentFormatterBase;
import com.frojasg1.general.completion.PrototypeForCompletionBase;
import javax.swing.JTextPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CompletionDocumentFormatter extends CompletionDocumentFormatterBase<PrototypeForCompletionBase>
{

	public CompletionDocumentFormatter( JTextPane pane,
									ChangeZoomFactorServerInterface conf,
									InternationalizedStringConf translatorOfType )//,
//									BigMathHelp bmHelp )
	{
		super( pane, conf, translatorOfType, PrototypeForCompletionBase.class );
	}

/*
	protected static final String STYLE_FOR_TYPE_OF_COMPLETION = "TYPE_OF_COMPLETION";

	
	
	protected static final String ATTRIB_NORMAL = "NORMAL";

	// first attribute
	protected static final String ATTRIB_PRETEXT = "PRETEXT";

	// second attribute
	protected static final String ATTRIB_SELECTED = "SELECTED";



	protected static final Color SELECTED_BACKGROUND_COLOR = Colors.APPLE_GREEN;
	protected static final Color BLUE_COLOR = Color.BLUE;
//	protected static final Color BLUE_COLOR = new Color( 137, 216, 255 );
	protected static final Color BUTAN_COLOR = Colors.BUTAN;


	protected static final SetStyleAttribute _normalSetStyleAttribute = (style, defaultFontSize) ->{ } ;
	protected static final SetStyleAttribute _selectedSetStyleAttribute = (style, defaultFontSize) ->	StyleConstants.setBackground(style, SELECTED_BACKGROUND_COLOR );
	protected static final SetStyleAttribute _pretextSetStyleAttribute = (style, defaultFontSize) -> {
		StyleConstants.setBold(style, true);
		StyleConstants.setForeground(style, BLUE_COLOR );
	};
	protected static final SetStyleAttribute _pretextSelectedSetStyleAttribute = (style, defaultFontSize) ->{
		_pretextSetStyleAttribute.setStyleAttribute(style, defaultFontSize);
		_selectedSetStyleAttribute.setStyleAttribute(style, defaultFontSize);
	} ;


//	protected BigMathHelp _bmHelp = null;

	protected String _preText = null;
	protected PrototypeForCompletion _selected = null;
	protected PrototypeForCompletion[] _lastPrototypes = null;

	protected PrototypeForCompletion _currentPrototypeForCompletionToBeWritten = null;

	protected InternationalizedStringConf _translatorOfType = null;

	public CompletionDocumentFormatter( JTextPane pane,
									ChangeZoomFactorServerInterface conf,
									InternationalizedStringConf translatorOfType )//,
//									BigMathHelp bmHelp )
	{
		super( pane, conf, PrototypeForCompletion.class );

		_translatorOfType = translatorOfType;
//		_bmHelp = bmHelp;
	}

	@Override
	protected Style getLocalDefaultStyle( Integer fontSize )
	{
		Style result = super.getLocalDefaultStyle( fontSize );
		StyleConstants.setFontFamily(result, "Courier New" );

		return( result );
	}

	@Override
	protected void addParticularStyles( Integer defaultFontSize )
	{
		addParticularStyles( defaultFontSize, createStyleName( ATTRIB_NORMAL, ATTRIB_NORMAL ), _normalSetStyleAttribute );
		addParticularStyles( defaultFontSize, createStyleName( ATTRIB_NORMAL, ATTRIB_SELECTED ), _selectedSetStyleAttribute );
		addParticularStyles( defaultFontSize, createStyleName( ATTRIB_PRETEXT, ATTRIB_NORMAL ), _pretextSetStyleAttribute );
		addParticularStyles( defaultFontSize, createStyleName( ATTRIB_PRETEXT, ATTRIB_SELECTED ), _pretextSelectedSetStyleAttribute );
	}

	protected String createStyleName( String simpleStyleName, String ... attributeNames )
	{
		String result = simpleStyleName;

		for( String attName: attributeNames )
			result += "__" + attName;

		return( result );
	}

	protected void addParticularStyles( int defaultFontSize, String attribute, SetStyleAttribute ssa )
	{
		final Style styleLocalDefault = newFormattedStyleToBeModified(
						createStyleName( LOCAL_DEFAULT_STYLE, attribute )
															);
		ssa.setStyleAttribute(styleLocalDefault, defaultFontSize);

		final Style styleForFunctions = newFormattedStyleToBeModified(
						createStyleName( ExpressionExternalFormatter.STYLE_FOR_FUNCTIONS, attribute )
															);
		StyleConstants.setForeground(styleForFunctions, Color.ORANGE.darker() );
		StyleConstants.setBold(styleForFunctions, true);
		ssa.setStyleAttribute(styleForFunctions, defaultFontSize);

		final Style styleForVariables = newFormattedStyleToBeModified(
						createStyleName( ExpressionExternalFormatter.STYLE_FOR_VARIABLES, attribute )
															);
		StyleConstants.setForeground(styleForVariables, Color.CYAN.darker() );
		StyleConstants.setBold(styleForVariables, true);
		ssa.setStyleAttribute(styleForVariables, defaultFontSize);

		final Style styleForBigDecimal = newFormattedStyleToBeModified(
						createStyleName( ExpressionExternalFormatter.STYLE_FOR_BIGDECIMAL, attribute )
															);
		StyleConstants.setForeground(styleForBigDecimal, Color.GREEN.darker() );
		ssa.setStyleAttribute(styleForBigDecimal, defaultFontSize);

		final Style styleForConstant = newFormattedStyleToBeModified(
						createStyleName( ExpressionExternalFormatter.STYLE_FOR_CONSTANT, attribute )
															);
		StyleConstants.setForeground(styleForConstant, Color.GREEN.darker() );
		StyleConstants.setBold(styleForConstant, true);
		ssa.setStyleAttribute(styleForConstant, defaultFontSize);

		final Style styleForReservedWords = newFormattedStyleToBeModified(
						createStyleName( ExpressionExternalFormatter.STYLE_FOR_RESERVED_WORD, attribute )
															);
		StyleConstants.setForeground(styleForReservedWords, Color.GRAY );
		StyleConstants.setBold(styleForReservedWords, true);
		ssa.setStyleAttribute(styleForReservedWords, defaultFontSize);

		final Style styleForTypeOfCompletion = newFormattedStyleToBeModified(
						createStyleName( STYLE_FOR_TYPE_OF_COMPLETION, attribute )
															);
		int fontSize = IntegerFunctions.zoomValueCeil( defaultFontSize, 0.75D );
		StyleConstants.setBold(styleForTypeOfCompletion, true);
		StyleConstants.setFontFamily(styleForTypeOfCompletion, "Tahoma" );
		StyleConstants.setForeground(styleForTypeOfCompletion, BUTAN_COLOR );
		StyleConstants.setFontSize(styleForTypeOfCompletion, fontSize);
	}


	public void lineUp()
	{
		addIndexToSelected( -1 );
	}

	public void lineDown()
	{
		addIndexToSelected( 1 );
	}

	public void pageUp()
	{
		addIndexToSelected( -5 );
	}

	public void pageDown()
	{
		addIndexToSelected( 5 );
	}

	protected void addIndexToSelected( int amountToAddToSelectedIndex )
	{
		recalculateSelected( amountToAddToSelectedIndex );
		setPrototypes( _preText, _lastPrototypes );
	}

	protected void recalculateSelected( int amountToAddToIndex )
	{
		if( _lastPrototypes != null )
		{
			int index = ArrayFunctions.instance().getFirstIndexOfEquals(_lastPrototypes, _selected );

			index += amountToAddToIndex;

			index = IntegerFunctions.min( IntegerFunctions.max( 0, index ), _lastPrototypes.length - 1 );

			if( index >= 0 )
				_selected = _lastPrototypes[index];
		}
	}

	public PrototypeForCompletion getSelected()
	{
		return( _selected );
	}

	public void setPrototypes(String preText,
								PrototypeForCompletion[] prototypes )
	{
		setEmptyString();
		_preText = preText;
		_lastPrototypes = prototypes;

		_selected = calculateSelected( prototypes );

		if( ( prototypes != null ) && ( prototypes.length > 0 ) )
		{
			int caretPosition = 0;
			ViewTextComponent vtc = getViewTextComponent();
			int maxLength = calculateMaxLength( prototypes );
			for( PrototypeForCompletion prototype: prototypes )
			{
				if( ( prototype.equals( _selected ) ) && ( vtc != null ) )
					caretPosition = vtc.getLength();

				append( preText, prototype, maxLength );
			}

			if( vtc != null )
				vtc.setCaretPosition( caretPosition );
		}

		invokeListeners();
	}

	protected int calculateMaxLength( PrototypeForCompletion[] prototypes )
	{
		int result = 0;

		if( prototypes.length > 0 )
		{
			for( PrototypeForCompletion prototype: prototypes )
			{
				String text = createText( prototype, 0 );
				if( ( text != null ) && ( text.length() > result ) )
					result = text.length();
			}

			result += 6;
		}

		return( result );
	}

	protected void append( String preText, PrototypeForCompletion prototype, int maxLength )
	{
		if( prototype != null )
		{
			String styleName = null;

			String selOrNot = ATTRIB_NORMAL;
			if( prototype == _selected )
				selOrNot = ATTRIB_SELECTED;

			String text = createText( prototype, maxLength );

			_currentPrototypeForCompletionToBeWritten = prototype;
			try
			{
				if( ( prototype == _selected ) &&
					StringFunctions.instance().stringStartsWith( text, preText ) )
				{
					appendFormattedText( createFormatForText( preText, 0, createStyleName( LOCAL_DEFAULT_STYLE, ATTRIB_PRETEXT, selOrNot ) ) );
					appendFormattedText( createFormatForText( text.substring( preText.length() ), 0,
																createStyleName( LOCAL_DEFAULT_STYLE, ATTRIB_NORMAL, selOrNot ) )
										);
				}
				else
					appendText( text, createStyleName( LOCAL_DEFAULT_STYLE, ATTRIB_NORMAL, selOrNot ) );
			}
			finally
			{
				_currentPrototypeForCompletionToBeWritten = null;
			}

			appendText( "(" + translate(prototype.getType()) + ")",
						createStyleName( STYLE_FOR_TYPE_OF_COMPLETION, ATTRIB_NORMAL, ATTRIB_NORMAL ) );

			appendReturnCarriage();
		}
	}

	@Override
	protected void addedToHistory( FormatForText fft )
	{
		if( ( fft != null ) && ( _currentPrototypeForCompletionToBeWritten != null ) )
		{
			_linkListener.addLink(fft, _currentPrototypeForCompletionToBeWritten );
		}
		else
			super.addedToHistory(fft);
	}

	protected String translate( String text )
	{
		String result = text;
		if( _translatorOfType != null )
			result = _translatorOfType.getInternationalString(text);

		return( result );
	}

	protected String createText( PrototypeForCompletion prototype, int length )
	{
		String result = null;
		if( prototype != null )
		{
			StringBuilder sb = new StringBuilder();
			String type = prototype.getType();

//			if( !typeIsOperator( type ) )
				sb.append( prototype.getName() );

			if( length > 0 )
			{
				int remaining = length - sb.length();
				for( int ii=0; ii<remaining; ii++ )
					sb.append( " " );
			}

			result = sb.toString();
		}

		return( result );
	}

	protected PrototypeForCompletion calculateSelected( PrototypeForCompletion[] prototypes )
	{
		PrototypeForCompletion result = null;
		
		if( prototypes != null )
		{
			for( int ii=0; ( result == null) && ( ii<prototypes.length ); ii++ )
				if( prototypes[ii] == _selected )
					result = _selected;

			if( ( result == null ) && ( prototypes.length > 0 ) )
				result = prototypes[0];
		}

		return( result );
	}

	protected interface SetStyleAttribute
	{
		public void setStyleAttribute( Style style, int defaultFontSize );
	}
*/
}
