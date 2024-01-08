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

import com.frojasg1.applications.common.configuration.application.ChangeZoomFactorServerInterface;
import com.frojasg1.general.completion.PrototypeForCompletionBase;
import com.frojasg1.general.desktop.view.document.formatter.ZoomDocumentFormatAppender;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class CurrentParamDocumentFormatterBase<MM extends PrototypeForCompletionBase> extends ZoomDocumentFormatAppender< MM >
{
	protected static final String STYLE_FOR_TYPE_OF_COMPLETION = "TYPE_OF_COMPLETION";

	protected static final Color BUTAN_COLOR = new Color( 254, 169, 0);

	protected static final String ATTRIB_NORMAL = "NORMAL";

	// first attribute
	protected static final String ATTRIB_REMARKED = "REMARKED";


	protected static final SetStyleAttribute _normalSetStyleAttribute = (style, defaultFontSize) ->{ } ;
	protected static final SetStyleAttribute _remarkedSetStyleAttribute = (style, defaultFontSize) -> {
		int fontSize = IntegerFunctions.zoomValueCeil( defaultFontSize, 1.33D );
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBold(style, true);
	};

//	protected BigMathHelp _bmHelp = null;


	public CurrentParamDocumentFormatterBase( JTextPane pane,
									ChangeZoomFactorServerInterface conf,
									Class<MM> prototypeForCompletionClass )//,
//									BigMathHelp bmHelp )
	{
		super( pane, conf, prototypeForCompletionClass );
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
		addParticularStyles( defaultFontSize, ATTRIB_NORMAL, _normalSetStyleAttribute );
		addParticularStyles( defaultFontSize, ATTRIB_REMARKED, _remarkedSetStyleAttribute );
	}

	protected String createStyleName( String simpleStyleName, String ... attributeNames )
	{
		String result = simpleStyleName;

		for( String attName: attributeNames )
			result += "__" + attName;

		return( result );
	}

	protected abstract void addParticularStyles( int defaultFontSize, String attribute, SetStyleAttribute ssa );
/*
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
*/

/*
	@Override
	public void reformat()
	{
	}
*/
	public void setCurrentParam( PrototypeForCompletionBase prototype, int index )
	{
		setEmptyString();

		if( prototype != null )
		{
			append( prototype, index );
		}

		invokeListeners();
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
	protected abstract void append( PrototypeForCompletionBase prototype, int index );
/*
	{
		if( prototype != null )
		{
			String attrib = ATTRIB_NORMAL;
			String styleName = createStyleName( LOCAL_DEFAULT_STYLE, attrib );

			String type = prototype.getType();
			if( ! typeIsOperator( type ) )
				appendText( prototype.getName(), styleName );

			if( typeIsFunction( type ) ||
				( prototype.getListOfParams().size() > 0 ) )
			{
				if( !typeIsOperator( type ) )
					appendText( "( ", styleName );

				int ii=0;
				String separator = null;
				for( String param: prototype.getListOfParams() )
				{
					if( separator != null )
					{
						appendText( separator, styleName );
					}

					if( typeIsOperator( type ) )
						separator = " " + prototype.getName() + " ";
					else
						separator = ", ";

					if( ii==index )
						appendText( param, createStyleName( LOCAL_DEFAULT_STYLE, ATTRIB_REMARKED ) );
					else
						appendText( param, styleName );

					ii++;
				}

				if( !typeIsOperator( type ) )
					appendText( " )", styleName );
			}

			appendText( "     ", styleName );
			appendText( "(" + translate(prototype.getType()) + ")",
						createStyleName( STYLE_FOR_TYPE_OF_COMPLETION, ATTRIB_NORMAL ) );

			getViewTextComponent().setCaretPosition(0);
		}
	}
*/
	protected String translate( String text )
	{
//		return( _bmHelp.getContext().getTranslator().translate( text ) );
		return( null );
	}

	protected interface SetStyleAttribute
	{
		public void setStyleAttribute( Style style, int defaultFontSize );
	}
}
