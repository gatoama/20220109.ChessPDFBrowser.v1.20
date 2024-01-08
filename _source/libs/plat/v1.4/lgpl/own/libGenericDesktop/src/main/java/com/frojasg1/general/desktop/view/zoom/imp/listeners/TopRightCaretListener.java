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
package com.frojasg1.general.desktop.view.zoom.imp.listeners;

import com.frojasg1.general.desktop.view.text.utils.TextViewFunctions;
import com.frojasg1.general.number.IntegerFunctions;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TopRightCaretListener
{
	protected CaretListener _caretListener = null;
	protected DocumentListener _docListener = null;

	protected JTextComponent _textComp = null;

	public TopRightCaretListener( JTextComponent textComp )
	{
		_textComp = textComp;
	}

	public void init()
	{
		addListeners();
	}

	public void releaseResources()
	{
		removeListeners();
	}

	protected void addListeners()
	{
		_caretListener = (evt) -> modifyScrollsToRight();
		_textComp.addCaretListener( _caretListener );

		_docListener = new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				modifyScrollsToRight();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				modifyScrollsToRight();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
			
		};
		_textComp.getDocument().addDocumentListener( _docListener );
	}

	protected void removeListeners()
	{
		_textComp.removeCaretListener( _caretListener );
		_textComp.getDocument().removeDocumentListener( _docListener );
	}

	protected void modifyScrollsToRight()
	{
		SwingUtilities.invokeLater( () -> modifyScrollsToRight_internal() );
	}

	protected void modifyScrollsToRight_internal()
	{
		Rectangle charBounds = null;
		int pos = _textComp.getCaretPosition();

		// if we are at the top right, we place the horizontal scrollbar at the top right.
		if( ( charBounds = notTotallyVisibleCurrentChar(pos) ) != null )
		{
			JScrollBar horBar = null;
			Component spComp = getParent( _textComp );
			if( spComp instanceof JScrollPane )
				horBar = ( (JScrollPane) spComp ).getHorizontalScrollBar();

			if( horBar != null )
			{
/*				if( pos == _textComp.getDocument().getLength() )
					newValue = horBar.getMaximum() - horBar.getVisibleAmount();
				else
*/
				int newValue = calculateNewValueForHorizScroller( horBar, charBounds );

				horBar.setValue( newValue );
			}
		}
	}

	protected Rectangle notTotallyVisibleCurrentChar( int pos )
	{
		Rectangle result = null;

		Rectangle tmp = TextViewFunctions.instance().getCharacterBounds(_textComp, pos);

		if( tmp != null )
		{
			JScrollBar horBar = null;
			Component spComp = getParent( _textComp );
			if( spComp instanceof JScrollPane )
				horBar = ( (JScrollPane) spComp ).getHorizontalScrollBar();

			if( horBar != null )
			{
				int visibleTopRight = horBar.getValue() + horBar.getVisibleAmount();
				int charTopRight = tmp.x + tmp.width;
				boolean isTotallyVisible = ( visibleTopRight >= charTopRight );

				if( !isTotallyVisible )
					result = tmp;
			}
		}

		return( result );
	}

	protected int calculateNewValueForHorizScroller( JScrollBar horBar, Rectangle charBounds )
	{
		int topRightValue = horBar.getMaximum() - horBar.getVisibleAmount();
		int charTopRight = charBounds.x + charBounds.width;
		int targetValue = charTopRight + 10 - horBar.getVisibleAmount();

		int result = IntegerFunctions.limit( targetValue, 0, topRightValue );

		return( result );
	}

	protected Component getParent( Component comp )
	{
		Component result = comp.getParent();

		if( result instanceof JViewport )
			result = result.getParent();

		return( result );
	}
}
