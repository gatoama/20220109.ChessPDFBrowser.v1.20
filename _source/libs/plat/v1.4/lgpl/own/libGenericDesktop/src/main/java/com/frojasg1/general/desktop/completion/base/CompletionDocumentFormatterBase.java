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
package com.frojasg1.general.desktop.completion.base;

import com.frojasg1.general.completion.PrototypeForCompletionBase;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;
import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.view.colors.Colors;
import com.frojasg1.general.desktop.view.document.formatter.ZoomDocumentFormatAppender;
import com.frojasg1.general.document.formatted.FormatForText;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.view.ViewTextComponent;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CompletionDocumentFormatterBase<MM extends PrototypeForCompletionBase> extends ZoomDocumentFormatAppender< MM >
{

	protected static final String STYLE_FOR_TYPE_OF_COMPLETION = "TYPE_OF_COMPLETION";

	
	
	protected static final String ATTRIB_NORMAL = "NORMAL";

	// first attribute
	protected static final String ATTRIB_PRETEXT = "PRETEXT";

	// second attribute
	protected static final String ATTRIB_SELECTED = "SELECTED";


	protected static final int INVERTIBLE_SELECTED_BACKGROUND_COLOR_INDEX = 0;
	protected static final int INVERTIBLE_BLUE_COLOR_INDEX = 1;
	protected static final int INVERTIBLE_BUTAN_COLOR_INDEX = 2;
	protected static final int INVERTIBLE_GREY_COLOR_INDEX = 3;

	protected static final Color SELECTED_BACKGROUND_COLOR = Colors.APPLE_GREEN;
	protected static final Color BLUE_COLOR = Color.BLUE;
//	protected static final Color BLUE_COLOR = new Color( 137, 216, 255 );
	protected static final Color BUTAN_COLOR = Colors.BUTAN;
	protected static final Color GREY_COLOR = Colors.GREY;

	protected static Color[] _originalInvertibleColorModeColors = new Color[] {
		SELECTED_BACKGROUND_COLOR,
		BLUE_COLOR,
		BUTAN_COLOR,
		GREY_COLOR
	};


	protected SetStyleAttribute _normalSetStyleAttribute = (style, defaultFontSize) ->{
		StyleConstants.setForeground(style, getInvertibleColor( INVERTIBLE_GREY_COLOR_INDEX ) );
	};
	protected SetStyleAttribute _selectedSetStyleAttribute = (style, defaultFontSize) ->	StyleConstants.setBackground(style, getInvertibleColor( INVERTIBLE_SELECTED_BACKGROUND_COLOR_INDEX ) );
	protected SetStyleAttribute _pretextSetStyleAttribute = (style, defaultFontSize) -> {
		StyleConstants.setBold(style, true);
		StyleConstants.setForeground(style, getInvertibleColor( INVERTIBLE_BLUE_COLOR_INDEX ) );
	};
	protected SetStyleAttribute _pretextSelectedSetStyleAttribute = (style, defaultFontSize) ->{
		_pretextSetStyleAttribute.setStyleAttribute(style, defaultFontSize);
		_selectedSetStyleAttribute.setStyleAttribute(style, defaultFontSize);
	} ;


//	protected BigMathHelp _bmHelp = null;

	protected String _preText = null;
	protected MM _selected = null;
	protected MM[] _lastPrototypes = null;

	protected MM _currentPrototypeForCompletionToBeWritten = null;

	protected InternationalizedStringConf _translatorOfType = null;
/*
	public CompletionDocumentFormatterBase( JTextPane pane,
									ChangeZoomFactorServerInterface conf,
									InternationalizedStringConf translatorOfType )//,
//									BigMathHelp bmHelp )
	{
		super( pane, conf, PrototypeForCompletionBase.class );

		_translatorOfType = translatorOfType;
//		_bmHelp = bmHelp;
	}
*/
	public CompletionDocumentFormatterBase( JTextPane pane,
									ChangeZoomFactorServerInterface conf,
									InternationalizedStringConf translatorOfType,
									Class<MM> prototypeForCompletionClass )
	{
		super( pane, conf, prototypeForCompletionClass );

		_translatorOfType = translatorOfType;
//		_bmHelp = bmHelp;
	}

	@Override
	protected void initializeZoomDocumentFormatter()
	{
		SwingUtilities.invokeLater( () -> super.initializeZoomDocumentFormatter() );
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
		if( ssa != null )
			ssa.setStyleAttribute(styleLocalDefault, defaultFontSize);
/*
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
*/
		final Style styleForTypeOfCompletion = newFormattedStyleToBeModified(
						createStyleName( STYLE_FOR_TYPE_OF_COMPLETION, attribute )
															);
		int fontSize = IntegerFunctions.zoomValueCeil( defaultFontSize, 0.75D );
		StyleConstants.setBold(styleForTypeOfCompletion, true);
		StyleConstants.setFontFamily(styleForTypeOfCompletion, "Tahoma" );
		StyleConstants.setForeground(styleForTypeOfCompletion, getInvertibleColor( INVERTIBLE_BUTAN_COLOR_INDEX ) );
		StyleConstants.setFontSize(styleForTypeOfCompletion, fontSize);
	}

/*
	@Override
	protected void reformat()
	{
	}
*/


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
		addIndexToSelected( -1 * getPageScrollSize() );
	}

	public void pageDown()
	{
		addIndexToSelected( getPageScrollSize() );
	}

	protected JScrollPane getJScrollPane()
	{
		JScrollPane result = null;
		Component comp = ExecutionFunctions.instance().safeFunctionExecution( () -> getJTextPane().getParent().getParent() );
		if( comp instanceof JScrollPane )
			result = (JScrollPane) comp;

		return( result );
	}

	protected int getPageScrollSize()
	{
		int result = 2;

		JScrollPane jsp = this.getJScrollPane();
		if( ( _lastPrototypes != null ) && ( jsp != null ) )
			result = IntegerFunctions.max( 2,
					( _lastPrototypes.length * jsp.getVerticalScrollBar().getVisibleAmount() )
						/ ( 4 * getJTextPane().getPreferredSize().height ) );

		return( result );
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

	public PrototypeForCompletionBase getSelected()
	{
		return( _selected );
	}

	public void setPrototypes(String preText,
								MM[] prototypes )
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
			for( MM prototype: prototypes )
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

	protected int calculateMaxLength( PrototypeForCompletionBase[] prototypes )
	{
		int result = 0;

		if( prototypes.length > 0 )
		{
			for( PrototypeForCompletionBase prototype: prototypes )
			{
				String text = createText( prototype, 0 );
				if( ( text != null ) && ( text.length() > result ) )
					result = text.length();
			}

			result += 6;
		}

		return( result );
	}

	protected void append( String preText, MM prototype, int maxLength )
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
/*
	protected boolean typeIsFunction( String type )
	{
		return( ( type.equals( PrototypeForCompletionFactory.FUNCTION ) ||
					type.equals( PrototypeForCompletionFactory.USER_FUNCTION ) ) );
	}

	protected boolean typeIsOperator( String type )
	{
		return( type.equals( PrototypeForCompletionFactory.OPERATOR ) );
	}
*/
	protected String createText( PrototypeForCompletionBase prototype, int length )
	{
		String result = null;
		if( prototype != null )
		{
			StringBuilder sb = new StringBuilder();
			String type = prototype.getType();

//			if( !typeIsOperator( type ) )
				sb.append( prototype.getName() );
/*
			if( typeIsFunction( type ) ||
				( prototype.getListOfParams().size() > 0 ) )
			{
				if( !typeIsOperator( type ) )
					sb.append( "( " );

				String separator = "";
				for( String param: prototype.getListOfParams() )
				{
					sb.append( separator ).append( param );

					if( typeIsOperator( type ) )
						separator = prototype.getName();
					else
						separator = ", ";
				}

				if( !typeIsOperator( type ) )
					sb.append( " )" );
			}
*/
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

	protected MM calculateSelected( MM[] prototypes )
	{
		MM result = null;
		
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

	@Override
	protected Color[] createOriginalInvertibleColors() {
		return( _originalInvertibleColorModeColors );
	}

	@Override
	protected Color[] createInvertedInvertibleColors()
	{
		Color[] result = super.createInvertedInvertibleColors();
		result[INVERTIBLE_SELECTED_BACKGROUND_COLOR_INDEX] = Colors.DARKER_APPLE_GREEN;

		return result;
	}

	protected interface SetStyleAttribute
	{
		public void setStyleAttribute( Style style, int defaultFontSize );
	}
}
