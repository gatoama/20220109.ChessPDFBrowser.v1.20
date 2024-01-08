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
package com.frojasg1.general.desktop.view.text;

import com.frojasg1.applications.common.components.internationalization.JFrameInternationalization;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.desktop.copypastepopup.TextCompPopupManager;
import com.frojasg1.general.desktop.mouse.CursorFunctions;
import com.frojasg1.general.desktop.mouse.MouseFunctions;
import com.frojasg1.general.desktop.screen.ScreenFunctions;
import com.frojasg1.general.desktop.view.text.link.JTextComponentMouseLinkListener;
import com.frojasg1.general.number.IntegerFunctions;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoListener;
import com.frojasg1.generic.GenericFunctions;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ScrollableJTextComponentUrlLauncher_old
	implements MouseListener, MouseMotionListener, MouseWheelListener,
				TextUndoRedoListener
{
	protected StringAndPosition _lastURLorEmailAddress = null;
	protected StringAndPosition _lastStringAndPosition = null;

//	protected JScrollPane jScrollPane1 = null;
	protected JTextComponent _textComp = null;

	protected JFrameInternationalization _intern;

	protected TextCompPopupManager _textPopupManager = null;

	public ScrollableJTextComponentUrlLauncher_old(  )
	{
	}

	public void setJTextComponent( JTextComponent textComp )
	{
		if( _textComp != null )
			removeListeners();

		_textComp = textComp;
		addListeners();
	}
/*
	public void setScrollPane( JScrollPane sp )
	{
		if( jScrollPane1 != null )
			removeListeners();

		jScrollPane1 = sp;
		addListeners();
	}

	public Component getView()
	{
		Component comp = null;
		if( jScrollPane1 != null )
			comp = jScrollPane1.getViewport().getView();

		return( comp );
	}
*/	
	public void addListeners()
	{
/*		_mapRRCI = new MapResizeRelocateComponentItem();
		try
		{
			_mapRRCI.putResizeRelocateComponentItem( jScrollPane1, ResizeRelocateItem.RESIZE_TO_RIGHT + ResizeRelocateItem.RESIZE_TO_BOTTOM );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
*/
		Component comp = _textComp;
		if( comp != null )
		{
			addListeners( comp );

			if( comp.getParent() instanceof JViewport )
			{
				addListeners( comp.getParent() );
			}
		}
	}

	protected void addListeners( Component comp )
	{
		comp.addMouseListener( this );
		comp.addMouseMotionListener( this );
		comp.addMouseWheelListener(this);
	}
	
	public void removeListeners()
	{
		Component comp = _textComp;
		if( comp != null )
		{
			removeListeners( comp );

			if( comp.getParent() instanceof JViewport )
			{
				removeListeners( comp.getParent() );
			}
		}
	}

	public void removeListeners( Component comp )
	{
		if( comp != null )
		{
			comp.removeMouseListener( this );
			comp.removeMouseMotionListener( this );
			comp.removeMouseWheelListener(this);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if( SwingUtilities.isLeftMouseButton(e) )
			doActions();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		moveMouse(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		moveMouse(e);
	}

	protected JViewport getViewport( Component comp )
	{
		JViewport result = null;

		if( comp instanceof JViewport )
			result = (JViewport) comp;

		return( result );
	}

	protected JViewport getAssociatedViewPort( Component comp )
	{
		JViewport result = getViewport( comp );
		
		if( ( result == null ) && ( comp != null ) )
			result = getViewport( comp.getParent() );

		return( result );
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent evt)
	{
		boolean pageHasBeenChanged = false;
//		int units = evt.getUnitsToScroll();
		int units = evt.getUnitsToScroll();

		Component comp = (Component) evt.getSource();
		JViewport jvp = getAssociatedViewPort(comp);
		if( jvp != null )
		{
			JScrollPane scrollPane = null;
			if( jvp.getParent() instanceof JScrollPane )
			{
				scrollPane = (JScrollPane) jvp.getParent();
			}

			if( scrollPane != null )
			{
				JScrollBar scrollBar = null;
				scrollBar = scrollPane.getVerticalScrollBar();

				incrementScrollBarValue( scrollBar, (int) ( Math.signum(units) * 1 / 3 * scrollBar.getBlockIncrement(1) ) );

	/*
				if (evt.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
				{
					incrementScrollBarValue( scrollBar, scrollBar.getBlockIncrement(1) );
	//				incrementScrollBarValue( scrollBar, evt.getUnitsToScroll() * scrollBar.getUnitIncrement() );
	//						incrementScrollBarValue( scrollBar, ( units * scrollBar.getVisibleAmount() ) / 12 );
				}
				else
				{ //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
					incrementScrollBarValue( scrollBar, scrollBar.getBlockIncrement(1) );
	//				incrementScrollBarValue( scrollBar, scrollBar.getBlockIncrement(1) );
	//						incrementScrollBarValue( scrollBar, scrollBar.getVisibleAmount() / 12 );
				}
	*/
			}
		}


		moveMouse(evt);
	}

	protected void incrementScrollBarValue( JScrollBar scrollBar, int increment )
	{
		setScrollBarValue( scrollBar, scrollBar.getValue() + increment );
	}

	protected void setScrollBarValue( JScrollBar scrollBar, int value )
	{
		int valueToSet = IntegerFunctions.min( scrollBar.getMaximum(),
												IntegerFunctions.max( scrollBar.getMinimum(), value) );

//		_scrollByProgram = true;
		scrollBar.setValue(valueToSet);
//		_scrollByProgram = false;
	}

	public void moveMouse( MouseEvent e )
	{
		Component parent = ((Component) e.getSource()).getParent();
		moveMouse( parent );
	}

	protected void moveMouse( Component parent )
	{

		if( parent instanceof JViewport )
		{
			JViewport jvp = (JViewport) parent;
			changeUrlCursor( jvp, -1 );
		}
	}

	protected void changeUrlCursor( JViewport textComponentParent, int oldScrollYYValue )
	{
		SwingUtilities.invokeLater( () -> { changeUrlCursor_internal( textComponentParent, oldScrollYYValue ); } );
	}

	protected void changeUrlCursor_internal( JViewport textComponentParent, int oldScrollYYValue )
	{
		if( textComponentParent.getComponent(0) instanceof JTextComponent )
		{
			JTextComponent jtc = (JTextComponent) textComponentParent.getComponent(0);

			Point pt = MouseFunctions.getMouseLocation();
			if( ScreenFunctions.isInsideComponent( textComponentParent, pt ) )
			{
				Point parentPosition = textComponentParent.getLocationOnScreen();

				if( textComponentParent.getParent() instanceof JScrollPane )
				{
					JScrollPane parent = (JScrollPane) textComponentParent.getParent();

					int scrollYYValue = parent.getVerticalScrollBar().getValue();
					
					pt = new Point( (int) Math.min( jtc.getWidth()-1,
													(pt.getX() - parentPosition.getX()) ),
									(int) Math.min( jtc.getHeight()-1,
													(double) (  ( pt.getY() - parentPosition.getY() ) *
															 parent.getVerticalScrollBar().getVisibleAmount()) /
														textComponentParent.getHeight() ) +
														scrollYYValue );
					int pos = jtc.viewToModel(pt);

					_lastURLorEmailAddress = getCurrentUrlOrEmailAddress( jtc, pos );

//					System.out.println( "pt:" + pt + " position: " + pos + "  url:" + _lastURLorEmailAddress );

					boolean cursorChanged = false;
					if( ( _lastURLorEmailAddress != null ) && !jtc.getCursor().getName().equals(CursorFunctions._handCursor.getName() ) )
					{
//						jScrollPane1.getViewport().getComponent(0).setCursor( CursorFunctions._handCursor );
						jtc.setCursor(CursorFunctions._handCursor );
						cursorChanged = true;
					}
					else if( (_lastURLorEmailAddress == null ) &&
							!jtc.getCursor().getName().equals(CursorFunctions._defaultCursor.getName() ) &&
							!jtc.getCursor().getName().equals(CursorFunctions._textCursor.getName() ) )
					{
//						jScrollPane1.getViewport().getComponent(0).setCursor( CursorFunctions._defaultCursor );
						if( jtc.isEnabled() && jtc.isEditable() )
							jtc.setCursor(CursorFunctions._textCursor );
						else
							jtc.setCursor(CursorFunctions._defaultCursor );

						cursorChanged = true;
					}

					if( cursorChanged && ( oldScrollYYValue != -1 ) &&
						( oldScrollYYValue != scrollYYValue ) )
					{
						if( textComponentParent.getParent() instanceof JScrollPane )
						{
							JScrollPane jScrollPane = (JScrollPane) textComponentParent.getParent();
							jScrollPane.getVerticalScrollBar().setValue( oldScrollYYValue );
							jScrollPane.getVerticalScrollBar().setValue( scrollYYValue );
//						jScrollPane1.getVerticalScrollBar().setValue( oldScrollYYValue );
//						jScrollPane1.getVerticalScrollBar().setValue( scrollYYValue );
/*						java.awt.EventQueue.invokeLater(new Runnable() {
							public void run() {
								_lensJPanel.repaint();
							}
						});
	*/
						}
					}
				}
			}
		}

		if( _textPopupManager != null )
			_textPopupManager.setUrlOrEmailAddress( _lastURLorEmailAddress );
	}

	protected JScrollPane getJScrollPane()
	{
		JScrollPane result = null;

		if( ( _textComp != null ) &&
			( _textComp.getParent() != null ) &&
			( _textComp.getParent().getParent() instanceof JScrollPane ) )
		{
			result = (JScrollPane) _textComp.getParent().getParent();
		}

		return( result );
	}

	public void changeUrlCursor( int oldScrollYYValue )
	{
		changeUrlCursor(getJScrollPane().getViewport(), oldScrollYYValue );
	}

	protected StringAndPosition getCurrentUrlOrEmailAddress( JTextComponent tc, int pos )
	{
		StringAndPosition result = null;
		
		StringAndPosition strAndPos = getMouseText( tc, pos );

		if( strAndPos != null )
		{
			if( GenericFunctions.instance().getSystem().isUrl(strAndPos.getString() ) )
				result = strAndPos;
			else if( GenericFunctions.instance().getSystem().isEmailAddress(strAndPos.getString()) )
				result = strAndPos;
		}

		return( result );
	}

	protected StringAndPosition getMouseText( JTextComponent tc, int pos )
	{
		StringAndPosition result = null;

		String word = null;
		String wholeText = null;
		int initialPosition = -1;
		int finalPosition = -1;

		try
		{
			wholeText = tc.getDocument().getText( 0, tc.getDocument().getLength() );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
			wholeText = "";
		}

		if( pos < wholeText.length() )
		{
			initialPosition = StringFunctions.instance().lastIndexOfAnyChar( wholeText, " \t\r\n\r", pos );
			initialPosition++;

			finalPosition = StringFunctions.instance().indexOfAnyChar( wholeText, " \t\r\n\r", pos );
			if( finalPosition == -1 )
				finalPosition = wholeText.length();

			if( finalPosition > initialPosition )
				word = wholeText.substring( initialPosition, finalPosition );
		}

		if( word != null )
		{
			if( ( _lastStringAndPosition == null ) ||
				( _lastStringAndPosition.getStart() != initialPosition ) ||
				( _lastStringAndPosition.getEnd() != finalPosition ) ||
				!word.equals( _lastStringAndPosition.getString() ) )
			{
				_lastStringAndPosition = new StringAndPosition( initialPosition,
																finalPosition,
																word );
			}
		}
		else
			_lastStringAndPosition = null;
		
		return( _lastStringAndPosition );
	}

	protected void doActions()
	{
		if( _lastURLorEmailAddress != null )
		{
			String address = _lastURLorEmailAddress.getString();
			if( GenericFunctions.instance().getSystem().isUrl(address) )
				GenericFunctions.instance().getSystem().browse(address);
			else if( GenericFunctions.instance().getSystem().isEmailAddress(address) )
				GenericFunctions.instance().getSystem().mailTo(address);
		}
	}

	public void setInternationalization( JFrameInternationalization intern )
	{
		_intern = intern;

		if( _intern != null )
		{
			_textPopupManager = _intern.getTextCompPopupManager( _textComp );

			if( _textPopupManager != null )
				_textPopupManager.setIsUrlTextComponent( true );

			TextUndoRedoInterface undoRedoInterface = _intern.getTextUndoRedoManager( _textComp );

			if( undoRedoInterface != null )
				undoRedoInterface.registerListener( this );
		}
	}

	@Override
	public void caretHasChanged()
	{
	}

	@Override
	public void undoListHasChanged()
	{
	}

	@Override
	public void redoListHasChanged()
	{
	}

	@Override
	public void originalElementHasChanged()
	{
		changeUrlCursor( -1 );
	}
}
