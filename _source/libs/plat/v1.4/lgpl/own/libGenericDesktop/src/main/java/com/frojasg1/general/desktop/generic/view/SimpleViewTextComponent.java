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
package com.frojasg1.general.desktop.generic.view;

import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.view.ComponentFunctions;
import com.frojasg1.general.desktop.view.text.utils.TextViewFunctions;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import javax.accessibility.AccessibleContext;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledEditorKit;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class SimpleViewTextComponent implements DesktopViewTextComponent
{
	protected JTextComponent _textComp;

	public SimpleViewTextComponent( JTextComponent textComp )
	{
		_textComp = textComp;
	}

	@Override
	public JTextComponent getComponent()
	{
		return( _textComp );
	}

	@Override
	public String getText()
	{
		String result = null;
		Document doc = _textComp.getDocument();
		if( doc != null )
		{
			try
			{
				result = doc.getText( 0, doc.getLength() );
			}
			catch( Throwable th )
			{
				result = null;
				th.printStackTrace();
			}
		}

		if( result == null )
		{
			result = _textComp.getText();
		}

		return( result );
	}

	@Override
	public void setEmptyText()
	{
		Document doc = _textComp.getDocument();
		if( doc != null )
		{
			try
			{
				doc.remove( 0, doc.getLength() );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
			}
		}
	}


	@Override
	public void setText(String text)
	{
		_textComp.setText( text );
	}

	public void replaceText_nonEDT( int pos, int length, String strToReplaceTo )
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					replaceText( pos, length, strToReplaceTo );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
	}

	
	@Override
	public void replaceText(int pos, int length, String strToReplaceTo)
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			replaceText_nonEDT( pos, length, strToReplaceTo );
			return;
		}
		
		String text = getText();
		
		if( pos+length > text.length() )
			throw( new RuntimeException( "The text could not be replaced. Not long enough." ) );
/*
		StringBuilder sb = new StringBuilder();
		sb.append( text.substring( 0, pos ) );
		sb.append( strToReplaceTo );
		sb.append( text.substring(pos+length) );

		setText( sb.toString() );
*/

		synchronized( _textComp )
		{
/*			if( pos <= _textComp.getDocument().getLength() )
				_textComp.setSelectionStart(pos);
			else
				System.out.println( "pos > _textComp.getDocument().getLength()" );

			if( (pos+length) <= _textComp.getDocument().getLength() )
				_textComp.setSelectionEnd(pos+length);
			else
				System.out.println( "(pos+length) > _textComp.getDocument().getLength()" );

			_textComp.replaceSelection(strToReplaceTo);
*/
			try
			{
				if( pos<0 )
					System.out.println( "pos < 0    " + pos + " < 0" );

				if( (pos+length) > _textComp.getDocument().getLength() )
					System.out.println( "(pos+length) > _textComp.getDocument().getLength()   " +
						" ( " + pos + " + " + length + " ) = " + (pos+length) + " > " +
						_textComp.getDocument().getLength() );

				System.out.println( "getDocument().getLength() = " + _textComp.getDocument().getLength() + "     remove: pos = " + pos + ", length = " + length );
				_textComp.getDocument().remove( pos, length );

				System.out.println( "getDocument().getLength() = " + _textComp.getDocument().getLength() + "     insert: pos = " + pos + ", string = " + strToReplaceTo );
				_textComp.getDocument().insertString( pos, strToReplaceTo, null );

				Integer newCaretPosition = (pos + strToReplaceTo.length());
				SwingUtilities.invokeLater( () -> { _textComp.setCaretPosition( newCaretPosition ); } );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void replaceText(int pos, String strToReplaceFrom, String strToReplaceTo)
	{
		String text = getText();
		if( pos > text.length() )
			System.out.println( "pos: " + pos + ". text.length(): " + text.length() );
		String realTextToReplace = text.substring( pos, pos + strToReplaceFrom.length() );

		StyledEditorKit ed;
		if( !realTextToReplace.equals( strToReplaceFrom ) )
			throw( new RuntimeException( "The text to be replaced does not match the real text to be replaced" ) );

		replaceText( pos, strToReplaceFrom.length(), strToReplaceTo );
	}

	@Override
	public int getSelectionStart()
	{
		return( _textComp.getSelectionStart() );
	}
	
	@Override
	public int getSelectionEnd()
	{
		return( _textComp.getSelectionEnd() );
	}

	public void setSelectionBounds_nonEDT( int start, int length )
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					setSelectionBounds( start, length );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setSelectionBounds(int start, int length)
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			setSelectionBounds_nonEDT( start, length );
			return;
		}

		Component focusOwner = JFrameInternationalization.getFocusedComponent();
		ViewFunctions.instance().setFocusedComponent( _textComp );

		_textComp.setSelectionStart( start );
		_textComp.setSelectionEnd( start + length );
		_textComp.repaint();

		ViewFunctions.instance().setFocusedComponent(focusOwner);
	}

	public String getSelectedText()
	{
		String result = null;
		
		int length = _textComp.getText().length();
		int selStart = getSelectionStart();
		int selEnd = getSelectionEnd();
		
		if( (selStart>=0) && (selEnd>selStart) &&
			(selEnd<=length) )
		{
			try
			{
				result = _textComp.getText( selStart, selEnd-selStart );
			}
			catch ( Throwable th )
			{
				th.printStackTrace();
			}
		}

		return( result );
	}

	@Override
	public int getCaretPosition()
	{
		return( _textComp.getCaretPosition() );
	}

	public void setCaretPosition_nonEDT( int position )
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					setCaretPosition( position );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setCaretPosition(int position)
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			setCaretPosition_nonEDT( position );
			return;
		}

		_textComp.setCaretPosition( position );
	}

	public boolean isEnabled()
	{
		return( _textComp.isEnabled() );
	}

	public boolean isEditable()
	{
		return( _textComp.isEditable() );
	}

	public void setEnabled_nonEDT( boolean value )
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					setEnabled( value );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
	}

	public void setEnabled( boolean value )
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			setEnabled_nonEDT( value );
			return;
		}

		_textComp.setEnabled( value );
	}

	public void setEditable_nonEDT( boolean value )
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					setEditable( value );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
	}

	public void setEditable( boolean value )
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			setEditable_nonEDT( value );
			return;
		}

		_textComp.setEditable( value );
	}

	public void setVisible_nonEDT( boolean value )
	{
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					setVisible( value );
				}
				catch( Throwable th )
				{
					th.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setVisible(boolean value)
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			setVisible_nonEDT( value );
			return;
		}

		_textComp.setVisible( value );
	}

	@Override
	public DesktopViewComponent getParentViewComponent()
	{
		return( DesktopGenericFunctions.instance().getViewFacilities().getParentViewComponent(this) );
	}
/*
	@Override
	public Rectangle getCharacterBounds(int index)
	{
		Rectangle result = null;
		
		if( ComponentFunctions.instance().isVisible( _textComp ) )
		{
			AccessibleContext accessibleContext = _textComp.getAccessibleContext();

			BasicTextPaneUI ui;
			if( accessibleContext instanceof JTextComponent.AccessibleJTextComponent )
			{
				int length = getLength();
				if( index >= length )
					index = length - 1;

				if( index >= 0 )
				{
					try
					{
						JTextComponent.AccessibleJTextComponent cjtc = (JTextComponent.AccessibleJTextComponent) accessibleContext;
						result = cjtc.getCharacterBounds(index);

						Point location = _textComp.getLocationOnScreen();

						result = new Rectangle( result.x + location.x, result.y + location.y,
													result.width, result.height );
					}
					catch( Exception ex )
					{
						ex.printStackTrace();
					}
				}
			}
		}

		return( result );
	}
*/

	@Override
	public Rectangle getCharacterBounds(int index)
	{
		Rectangle result = TextViewFunctions.instance().getCharacterBoundsOnScreen(_textComp, index);

		return( result );
	}

	@Override
	public int getLength()
	{
		int result = TextViewFunctions.instance().getLength(_textComp);

		return( result );
	}

	@Override
	public void requestFocus()
	{
		_textComp.requestFocus();
	}

	@Override
	public boolean isFocusable()
	{
		return( _textComp.isFocusable() );
	}

	@Override
	public boolean hasFocus()
	{
		return( _textComp.hasFocus() );
	}

	@Override
	public void releaseResources()
	{
		_textComp = null;
	}
}


