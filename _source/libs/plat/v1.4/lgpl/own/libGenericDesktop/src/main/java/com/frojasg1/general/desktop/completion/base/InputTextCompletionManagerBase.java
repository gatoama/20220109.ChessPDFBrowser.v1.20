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
import com.frojasg1.general.completion.PrototypeManagerBase;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.desktop.completion.api.InputTextCompletionManager;
import com.frojasg1.general.desktop.completion.api.CompletionWindow;
import com.frojasg1.general.desktop.completion.data.AlternativesForCompletionData;
import com.frojasg1.general.desktop.completion.data.CurrentParamForCompletionData;
import com.frojasg1.general.desktop.completion.data.TotalCompletionData;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.view.ViewTextComponent;
import javax.swing.SwingUtilities;
import javax.swing.text.LabelView;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public abstract class InputTextCompletionManagerBase< LL > implements InputTextCompletionManager< LL >
{
//	protected CompletionConfiguration _conf;

	protected String _inputText = null;
	protected int _caretPos = -1;

	protected CompletionWindow< LL > _completionWindow = null;
	protected ViewTextComponent< LL > _inputTextComponent = null;

	protected boolean _escapePressed = false;

	protected PrototypeManagerBase _prototypeManager = null;

//	protected BigMathHelp _bmHelp = null;
/*
	public InputTextCompletionManagerBase( CompletionConfiguration conf )//TextCompletionConfiguration conf )
	{
		_conf = conf;
	}
*/
	public void setPrototypeManager( PrototypeManagerBase prototypeManager )
	{
		_prototypeManager = prototypeManager;
	}

	public void setCompletionWindow( CompletionWindow window )
	{
		_completionWindow = window;
	}

	public void setInputTextComponent( ViewTextComponent inputTextComponent )
	{
		_inputTextComponent = inputTextComponent;
		_escapePressed = false;
	}
/*
	public void setBigMathHelp( BigMathHelp bmHelp )
	{
		_bmHelp = bmHelp;
	}
*/
	@Override
	public void resetCompletion()
	{
		_completionWindow.resetCompletion();
	}

	@Override
	public void processTypedInputText( String inputText, int caretPos, LL locationControl )
	{
		if( (locationControl != null ) &&
			( ( _caretPos != caretPos ) ||
				!StringFunctions.instance().stringsEquals( inputText, _inputText ) ) )
		{
			_inputText = inputText;
			_caretPos = caretPos;

//			updateCompletionTextComponent( inputText, caretPos, locationControl );
//			updateCurrentParamPrototype( inputText, caretPos, locationControl );
			updateTotalCompletion( inputText, caretPos, locationControl );
		}
	}

	@Override
	public void newCaretPosition( String inputText, int caretPos, LL locationControl )
	{
		if( ( _caretPos != caretPos ) ||
			!StringFunctions.instance().stringsEquals( inputText, _inputText ) )
		{
			_inputText = inputText;
			_caretPos = caretPos;

////			hideCompletionTextComponent();
//			updateCompletionTextComponent( inputText, caretPos, locationControl );
//			updateCurrentParamPrototype( inputText, caretPos, locationControl );
			updateTotalCompletion( inputText, caretPos, locationControl );
		}
	}
/*
	protected void updateCompletionTextComponent( String inputText, int caretPos, LL locationControl )
	{
		if( this.hasToShowCompletionWindow() )
		{
			String preText = getCaretWord(inputText, caretPos);

			PrototypeForCompletionBase[] possibilities = null;
			if( ! StringFunctions.instance().isEmpty(preText) )
			{
//				possibilities = _bmHelp.getPrototypeRange(preText);
				possibilities = _prototypeManager.getPrototypeRange(preText);
			}

			setListOfAlternativesKeepingSelection(preText, possibilities, locationControl);
		}
		else
			_completionWindow.hideCompletionTextComponent();
	}

	protected void updateCurrentParamPrototype( String inputText, int caretPos, LL locationControl )
	{
		if( this.hasToShowCurrentParameterWindow() )
		{
			CurrentParamResult cpr = getCurrentParam( inputText, caretPos );

			if( cpr != null )
			{
				setCurrentParamPrototype( cpr.getPrototypeForCompletion(),
											cpr.getParamIndex(),
											locationControl );
			}
			else
			{
				setCurrentParamPrototype( null, -1, locationControl );
			}
		}
		else
			_completionWindow.hideCurrentParameterHelp();
	}
*/
	protected TotalCompletionData<LL> createTotalCompletionData()
	{
		return( new TotalCompletionData<>() );
	}

	protected void updateTotalCompletion( String inputText, int caretPos, LL locationControl )
	{
		String preText = "empty";
		TotalCompletionData<LL> totalCompletionData = createTotalCompletionData();
		if( this.hasToShowCompletionWindow() )
		{
			AlternativesForCompletionData<LL> altData = totalCompletionData.createAndSetAlternativesForCompletionData();
			preText = getCaretWord(inputText, caretPos);

			PrototypeForCompletionBase[] possibilities = null;
			if( ! StringFunctions.instance().isEmpty(preText) )
			{
//				possibilities = _bmHelp.getPrototypeRange(preText);
				possibilities = _prototypeManager.getPrototypeRange(preText);
			}
			altData.setPreText(preText);
			altData.setPrototypes(possibilities);
			altData.setLocationControl(locationControl);
//			setListOfAlternativesKeepingSelection(preText, possibilities, locationControl);
		}
		else
			_completionWindow.hideCompletionTextComponent();

		if( this.hasToShowCurrentParameterWindow() )
		{
			CurrentParamForCompletionData<LL> currParData = totalCompletionData.createAndSetCurrentParamForCompletionData();
			CurrentParamResult cpr = getCurrentParam( inputText, caretPos );

			if( cpr != null )
			{
//				setCurrentParamPrototype( cpr.getPrototypeForCompletion(),
//											cpr.getParamIndex(),
//											locationControl );
				currParData.setPrototype( cpr.getPrototypeForCompletion() );
				currParData.setCurrentParamIndex( cpr.getParamIndex() );
				currParData.setLocationControl(locationControl);
			}
			else
			{
//				setCurrentParamPrototype( null, -1, locationControl );
				currParData.setPrototype( null );
				currParData.setCurrentParamIndex( -1 );
				currParData.setLocationControl(locationControl);
			}
		}
		else
			_completionWindow.hideCurrentParameterHelp();

		setTotalCompletionData( preText, totalCompletionData );
	}

	protected abstract CurrentParamResult getCurrentParam( String inputText, int caretPos );
/*
	{
		String name = null;
		int index = 0;

		int pos = caretPos;

		String allOperators = null;//_bmHelp.getContext().getPrototypeManager().getStringOfAllOperators();

		// look backwards
		String allSeparators = allOperators + "(),";

		if( ( pos > -1 ) && ( pos < inputText.length() ) )
		{
			String ch = inputText.substring( pos, pos + 1 );
			if( ch.equals( "(" ) )
				pos = pos -1;
			else if( StringFunctions.instance().isAnyChar(ch, allOperators ) )
			{
				index = 0;
				name = ch;
			}
		}

		while( ( pos > -1 ) && ( name == null ) )
		{
			pos = StringFunctions.instance().lastIndexOfAnyChar( inputText, allSeparators, pos );

			if( pos > -1 )
			{
				char separator = inputText.charAt( pos );
				
				switch( separator )
				{
					case '(':	if( pos > 0 ) name = getCaretWord(inputText, pos);		break;

					case ')':	pos = skipWholeParenthesisExpression( inputText, pos, -1 );	break;

					case ',':	index++;	break;

					// operator
					default:
					{
						if( index == 0 )
						{
							name = Character.toString( separator );
							index = 1;
						}
					}
					break;
				}
			}

			pos--;
		}

		// look forward.
		if( StringFunctions.instance().isEmpty( name ) )
		{
			allSeparators = allOperators + "()";

			pos = StringFunctions.instance().indexOfAnyChar( inputText, allSeparators, caretPos );

			boolean end = false;
			boolean nextEnd = false;
			while( !end && ( pos > -1 ) && ( pos < inputText.length() ) &&
				StringFunctions.instance().isEmpty( name ) )
			{
				char separator = inputText.charAt( pos );

				end = nextEnd;
				switch( separator )
				{
					case '(':	pos = skipWholeParenthesisExpression( inputText, pos, 1 );
								nextEnd = true;
					break;
					case ')':	end = true;			break;

					// operator
					default:
					{
						index = 0;	// first parameter of operator
						name = Character.toString( separator );
					}
					break;
				}
				pos++;
			}
		}

		CurrentParamResult result = createCurrentParamResult(name, index );

		return( result );
	}
*/
	protected int skipWholeParenthesisExpression( String inputText, int pos, int increment )
	{
		int result = -1;

		if( ( pos >= 0 ) && ( pos < inputText.length() ) )
			result = pos;

		int closed = 0;
		do
		{
			if( result > -1 )
			{
				char ch = inputText.charAt( result );

				switch( ch )
				{
					case ')': closed++; break;
					case '(': closed--; break;
					default:
						break;
				}
			}

			result += increment;
		}
		while( ( closed > 0 ) && ( result >= 0 ) && ( result < inputText.length() ) );

		return( result );
	}

	protected abstract CurrentParamResult createCurrentParamResult( String name, int paramCount );
/*
	{
		CurrentParamResult result = null;

		if( name != null )
		{
			PrototypeForCompletionBase[] pfcArray = null;//_bmHelp.getContext().getPrototypeManager().getPrototypeRange(name);
			if( pfcArray != null )
			{
				for( int ii=0; ii<pfcArray.length; ii++ )
				{
					PrototypeForCompletionBase elem = pfcArray[ii];
					if( ( ( elem.getType().equals( PrototypeForCompletionFactory.FUNCTION ) ) ||
							( elem.getType().equals( PrototypeForCompletionFactory.USER_FUNCTION ) ) ||
							( elem.getType().equals( PrototypeForCompletionFactory.OPERATOR ) ||
							( elem.getListOfParams().size() > 0 ) )
						) &&
						name.equals( elem.getName() )
						)
					{
						result = new CurrentParamResult( elem, paramCount );
					}
				}
			}
		}

		return( result );
	}
*/
	protected void completionSelected( String previousText, String completedText, int start )
	{
		_inputTextComponent.replaceText(start, previousText, completedText );
		SwingUtilities.invokeLater( () -> _completionWindow.hideEverything() );

//		SwingUtilities.invokeLater( () -> setCaretPositionAndRequestFocus( start + completedText.length() ) );
		setCaretPositionAndRequestFocus( start + completedText.length() );
	}

	protected abstract void setCaretPositionAndRequestFocus( int caretPosition );


	@Override
	public void lineUp()
	{
		_completionWindow.lineUp();
	}

	@Override
	public void lineDown()
	{
		_completionWindow.lineDown();
	}

	@Override
	public void pageUp()
	{
		_completionWindow.pageUp();
	}

	@Override
	public void pageDown()
	{
		_completionWindow.pageDown();
	}

	@Override
	public void selectCurrent()
	{
		PrototypeForCompletionBase foop = _completionWindow.getSelectedCompletion();

		LabelView lv;

		String functionName = foop.getName();
		String caretWord = getCaretWord( _inputTextComponent.getText(),
										_inputTextComponent.getCaretPosition() );

		if( StringFunctions.instance().stringStartsWith(functionName, caretWord ) )
		{
			int start = _caretPos - caretWord.length();
			completionSelected(caretWord, functionName, start);
		}
	}

	protected String getCaretWord( String text, int caretPos )
	{
		String result = null;

		if( ( text != null ) && ( caretPos <= text.length() ) && ( caretPos > 0 ) )
		{
			int pos = StringFunctions.instance().lastIndexOfAnyChar( text, " \t),=", caretPos - 1 );

			if( pos >= 0 )
			{
				char ch = text.charAt( pos );
				
				if( ( ch == ')' ) || ( ch == ',' ) || ( ch == '=' ) )
					pos = caretPos - 1;	// for result be empty string.
			}

			result = text.substring( pos + 1, caretPos );
		}

		return( result );
	}

	@Override
	public void hideEverything()
	{
		_completionWindow.hideEverything();
	}

	@Override
	public void showCompletionWindow()
	{
		_completionWindow.showCompletionWindow();
	}

	@Override
	public void hideCompletionTextComponent()
	{
		_completionWindow.hideCompletionTextComponent();
	}

	@Override
	public void showCurrentParameterHelp()
	{
		_completionWindow.showCurrentParameterHelp();
	}

	@Override
	public void hideCurrentParameterHelp()
	{
		_completionWindow.hideCurrentParameterHelp();
	}

	protected abstract boolean hasToShowCompletionWindow();
/*
	{
		boolean result = false;
		if( _conf != null )
			result = _conf.isAutocompletionForRegexActivated();

		return( result ); //_conf.hasToShowCompletionWindow() );
	}
*/
	protected boolean hasToShowCurrentParameterWindow()
	{
		return( false );//_conf.hasToShowCurrentParameterWindow() );
	}

	protected void setTotalCompletionData( String preText,
										TotalCompletionData<LL> totalCompletionData )
	{
		if( StringFunctions.instance().isEmpty( preText ) )
			_escapePressed = false;

		if( ! _escapePressed )
			_completionWindow.setTotalCompletionData( totalCompletionData );
	}
/*
	protected void setListOfAlternativesKeepingSelection( String preText,
															PrototypeForCompletionBase [] functionPrototypes,
															LL locationControl )
	{
		if( StringFunctions.instance().isEmpty( preText ) )
			_escapePressed = false;

		if( ! _escapePressed )
			_completionWindow.setListOfAlternativesKeepingSelection( preText, functionPrototypes, locationControl );
	}

	protected void setCurrentParamPrototype( PrototypeForCompletionBase prototype,
												int currentParamIndex,
												LL locationControl )
	{
		if( ! _escapePressed )
			_completionWindow.setCurrentParamPrototype(prototype, currentParamIndex, locationControl );
	}
*/
	@Override
	public void escape() {
		_completionWindow.escape();
		_escapePressed = true;
	}

	@Override
	public CompletionWindow<LL> getCompletionWindow()
	{
		return( _completionWindow );
	}

	@Override
	public void relocateCompletionWindow()
	{
		LL charBounds = ExecutionFunctions.instance().safeSilentFunctionExecution( () -> _inputTextComponent.getCharacterBounds( _inputTextComponent.getCaretPosition() ));
		if( charBounds != null )
			_completionWindow.locateWindow( charBounds );
	}

	@Override
	public ViewTextComponent getInputTextComponent()
	{
		return( _inputTextComponent );
	}

	protected static class CurrentParamResult
	{
		protected PrototypeForCompletionBase _pfc = null;
		protected int _paramIndex = -1;

		public CurrentParamResult( PrototypeForCompletionBase pfc, int paramIndex )
		{
			_pfc = pfc;
			_paramIndex = paramIndex;
		}

		public PrototypeForCompletionBase getPrototypeForCompletion()
		{
			return( _pfc );
		}

		public int getParamIndex()
		{
			return( _paramIndex );
		}
	}
}
