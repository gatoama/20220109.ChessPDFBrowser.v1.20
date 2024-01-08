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
package com.frojasg1.general.desktop.view.text.listeners;

import com.frojasg1.general.string.StringFunctions;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class NumberFormatterFactoryDocumentListener extends JFormattedTextField.AbstractFormatterFactory
									implements DocumentListener, KeyListener
{
	protected JFormattedTextField _textField = null;
	protected NumberFormatter _formatter = null;
	protected int _lastPressedKeyCode = -1;

	public NumberFormatterFactoryDocumentListener( JFormattedTextField textField, Locale locale )
	{
		_formatter = new NumberFormatter( locale );
		_textField = textField;
		_textField.setFormatterFactory(this);
		_textField.getDocument().addDocumentListener(this);
		_textField.addKeyListener(this);
	}

	@Override
	public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf)
	{
		_formatter.uninstall();
		_formatter.install( tf );
		return( _formatter );
	}

	// it counts the chars of the text that are included in chars parameter, from the position
	// to the right (if increment == -1), or to the left (if increment == 1).
	protected int countCharsGeneric( String text, String chars,
									int position, int increment )
	{
		int result = -1;

		checkIncrement( increment );

		if( ( text != null ) && ( text.length() >= position ) )
		{
			int initialValue = -1;

			if( increment == -1 )
				initialValue = text.length() - 1;
			else if( increment == 1 )
				initialValue = 0;

			result = 0;
			for( int ii=initialValue;
				(increment == -1 ) && ( ii>=position ) ||
				( increment == 1 ) && ( ii<position );
				ii+=increment )
			{
				if( StringFunctions.instance().isAnyChar(text.substring( ii, ii+1 ), chars) )
					result++;
			}
		}

		return( result );
	}

	protected int countDigitsOnTheRight( String text, int position )
	{
		return( countCharsGeneric( text, "0123456789", position, -1 ) );
	}

	protected int countDigitsOnTheLeft( String text, int position )
	{
		return( countCharsGeneric( text, "0123456789", position, 1 ) );
	}

	protected void checkIncrement( int increment )
	{
		if( ( increment != -1 ) && ( increment != 1 ) )
			throw( new IllegalArgumentException( "increment was: " + increment +
												". must be equal to 1 or to -1" ) );
	}

	// it gets the position in the string with a number of chars included in chars parameter
	// on the right (increment=-1) or on the left (increment=1)
	protected int getPositionOfCharsGeneric( String text, String chars,
												int numberOfMatchingChars,
												int increment )
	{
		int result = -1;

		checkIncrement( increment );

		if( text != null )
		{
			int ii = -1;
			
			if( increment == -1 )
				ii = text.length();
			else if( increment == 1 )
				ii = 0;

			while( ( numberOfMatchingChars > 0 ) &&
					(	( increment == -1 ) && ( ii > 0 ) ||
						( increment == 1 ) && ( ii < text.length() )))
			{
				if( increment == 1 )
					ii++;

				if( StringFunctions.instance().isAnyChar(text.substring( ii-1, ii ), chars) )
					numberOfMatchingChars--;

				if( increment == -1 )
					ii--;
			}

			result = ii;
		}

		return( result );
	}

	protected int getPositionOfDigitsOnTheRight( String text, int numberOfMatchingCharsOnTheRight )
	{
		return( getPositionOfCharsGeneric( text, "0123456789", numberOfMatchingCharsOnTheRight, -1 ) );
	}

	protected int getPositionOfDigitsOnTheLeft( String text, int numberOfMatchingCharsOnTheLeft )
	{
		return( getPositionOfCharsGeneric( text, "0123456789", numberOfMatchingCharsOnTheLeft, 1 ) );
	}

	protected void formatRight()
	{
		format( -1 );
	}

	protected void formatLeft()
	{
		format( 1 );
	}

	protected void format( int increment )
	{
		SwingUtilities.invokeLater( new Runnable() {
			
			@Override
			public void run()
			{
				try
				{
					int caretPosition = _textField.getCaretPosition();
					String text = _textField.getText();
					int numberOfDigits = 0;
					
					if( increment == -1 )
						numberOfDigits = countDigitsOnTheRight( text, caretPosition );
					else
						numberOfDigits = countDigitsOnTheLeft( text, caretPosition );

					Long value = (Long) _formatter.stringToValue(text);
					String formattedText = _formatter.valueToString( value );

					int newCaretPosition = 0;

					if( increment == -1 )
						newCaretPosition = getPositionOfDigitsOnTheRight( formattedText, numberOfDigits );
					else
						newCaretPosition = getPositionOfDigitsOnTheLeft( formattedText, numberOfDigits );

//					if( ! text.equals( formattedText ) )
					if( ! value.equals( _textField.getValue() ) ||
						! text.equals( formattedText ) )
					{
						_textField.setValue( value );
						_textField.setText( formattedText );
						_textField.setCaretPosition( newCaretPosition );
					}
				}
				catch( Exception ex )
				{
					ex.printStackTrace();
				}
			}
			
		});
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		formatLeft();
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		if( _lastPressedKeyCode == KeyEvent.VK_DELETE )
			formatRight();
		else
			formatLeft();
	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{
		formatLeft();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		_lastPressedKeyCode = e.getKeyCode();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	public static class NumberFormatter extends JFormattedTextField.AbstractFormatter
	{
		protected Locale _locale = null;

		public NumberFormatter( Locale locale )
		{
			_locale = locale;
		}

		@Override
		public Object stringToValue(String text) throws ParseException
		{
			Long result = -1L;

			boolean throwException = false;
			int errorOffset = -1;
			text = StringFunctions.instance().removeAllCharactersDifferentFromChars(text, "0123456789,.");

			try
			{
				NumberFormat nf = NumberFormat.getInstance( _locale );
				result = nf.parse(text).longValue();
			}
			catch( ParseException pe )
			{
				throwException = true;
				errorOffset = pe.getErrorOffset();
			}
			catch( Exception ex )
			{
				throwException = true;
			}

			if( throwException )
				result = 0L;
//				throw( new ParseException( "Error parsing to long: " + text, errorOffset ) );

			return( result );
		}

		@Override
		public String valueToString(Object value) throws ParseException
		{
			String result = null;
			
			if( value instanceof Long )
			{
				NumberFormat nf = NumberFormat.getInstance( _locale );
				result = nf.format((Long) value);
			}
			else
				throw( new ParseException( "the value was not a Long", 0 ) );

			return( result );
		}

		public Locale getLocale()
		{
			return( _locale );
		}
	}
}
