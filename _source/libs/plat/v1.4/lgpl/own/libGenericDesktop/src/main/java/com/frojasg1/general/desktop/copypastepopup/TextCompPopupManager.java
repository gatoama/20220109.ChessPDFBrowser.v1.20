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
package com.frojasg1.general.desktop.copypastepopup;

import com.frojasg1.general.clipboard.SystemClipboard;
import com.frojasg1.general.desktop.generic.DesktopGenericFunctions;
import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import com.frojasg1.general.desktop.view.text.StringAndPosition;
import com.frojasg1.general.undoredo.UndoRedoInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import java.awt.event.MouseEvent;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TextCompPopupManager
{
	protected DesktopViewTextComponent _textComp;
	protected CopyPastePopup _popupMenu = null;
//	protected boolean _enablePopupMenu = true;
	protected ClickListener _clickListener = null;
	protected TextUndoRedoInterface _undoRedoManager = null;

	protected boolean _isUrlTextComponent = false;

	protected StringAndPosition _urlOrEmailAddress = null;

	public TextCompPopupManager( DesktopViewTextComponent textComp,
									TextUndoRedoInterface urm )
	{
		_textComp = textComp;
		_undoRedoManager = urm;
	}
/*
	public void setEnablePopupMenu( boolean value )
	{
		_enablePopupMenu = value;
	}
*/
	public JPopupMenu getJPopupMenu()
	{
		return( _popupMenu );
	}

	public DesktopViewTextComponent getViewTextComponent()
	{
		return( _textComp );
	}

	public void setIsUrlTextComponent( boolean value )
	{
		_isUrlTextComponent = value;
	}

	public TextUndoRedoInterface getUndoRedoManager()
	{
		return( _undoRedoManager );
	}

	public boolean isUrlEmailAddressTextComponent()
	{
		return( _isUrlTextComponent );
	}

	public boolean isUrlEmailAddressEnabled()
	{
		return( _urlOrEmailAddress != null );
	}

	public boolean isCuttingEnabled()
	{
		boolean result = isCopyingEnabled() && _textComp.isEditable();

		return( result );
	}

	public boolean isCopyingEnabled()
	{
		JTextComponent tc = _textComp.getComponent();
		boolean result = !( _textComp.getComponent() instanceof JPasswordField ) &&
							tc.isEnabled() && isThereAValidSelection();

		return( result );
	}

	public boolean isPastingEnabled()
	{
		JTextComponent tc = _textComp.getComponent();
		String strToPaste = SystemClipboard.instance().getClipboardContents();
		boolean result = tc.isEnabled() && tc.isEditable() && ( strToPaste.length() > 0 );
		return( result );
	}

	public boolean isRemovingEnabled()
	{
		JTextComponent tc = _textComp.getComponent();
		boolean result = tc.isEnabled() && isThereAValidSelection() && _textComp.isEditable();

		return( result );
	}

	public boolean isUndoingEnabled()
	{
		return( ( _undoRedoManager != null ) &&
				_undoRedoManager.hasElementsToUndo() &&
				_undoRedoManager.isStarted() &&
				_textComp.isEditable() );
	}

	public boolean isRedoingEnabled()
	{
		return( ( _undoRedoManager != null ) &&
				_undoRedoManager.hasElementsToRedo() &&
				_undoRedoManager.isStarted() &&
				_textComp.isEditable() );
	}

	public void doCut()
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						doCut();
					}
			});
		}
		else if( isCuttingEnabled() )
		{
			doCopy();
			doRemove();
		}
	}

	public void doCopy()
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						doCopy();
					}
			});
		}
		else if( isCopyingEnabled() )
		{
			int start = _textComp.getSelectionStart();
			int end = _textComp.getSelectionEnd();
			String strToCopy = _textComp.getText().substring( start, end );
			SystemClipboard.instance().setClipboardContents( strToCopy );
		}
	}

	public void doCopyAddress()
	{
		StringAndPosition urlOrEmailAddress = _urlOrEmailAddress;
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						doCopyAddress();
					}
			});
		}
		else if( urlOrEmailAddress != null )
		{
			int start = urlOrEmailAddress.getStart();
			int end = urlOrEmailAddress.getEnd();
			String strToCopy = _textComp.getText().substring( start, end );
			SystemClipboard.instance().setClipboardContents( strToCopy );

			if( _textComp.isEnabled() )
			{
				if( _textComp.isEditable() )
					_textComp.setCaretPosition(start);

				_textComp.setSelectionBounds(start, end - start);
			}
		}
	}

	public void doPaste()
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						doPaste();
					}
			});
		}
		else if( isPastingEnabled() )
		{
			String strToPaste = SystemClipboard.instance().getClipboardContents();
			if( strToPaste.length() != 0 )
			{
				if( isThereAValidSelection() )
				{
					_textComp.replaceText(_textComp.getSelectionStart(), _textComp.getSelectionEnd() - _textComp.getSelectionStart(), strToPaste);
				}
				else if( isPositionInsideBoundariesOfText( _textComp.getCaretPosition() ) )
				{
					_textComp.replaceText( _textComp.getCaretPosition(), 0, strToPaste);
				}
			}
		}
	}

	public void doRemove()
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						doRemove();
					}
			});
		}
		else if( isRemovingEnabled() )
		{
			_textComp.replaceText(_textComp.getSelectionStart(), _textComp.getSelectionEnd() - _textComp.getSelectionStart(), "");
		}
	}

	public void doUndo()
	{
		if( isUndoingEnabled() )
		{
			_undoRedoManager.undo();
		}
	}
	
	public void doRedo()
	{
		if( isRedoingEnabled() )
		{
			_undoRedoManager.redo();
		}
	}

	protected boolean isPositionInsideBoundariesOfText( int position )
	{
		return( ( position >= 0 ) && ( position <= _textComp.getText().length() ) );
	}

	protected boolean isThereAValidSelection()
	{
		boolean result = isPositionInsideBoundariesOfText( _textComp.getSelectionStart() ) &&
						isPositionInsideBoundariesOfText( _textComp.getSelectionEnd() ) &&
						( _textComp.getSelectionEnd() > _textComp.getSelectionStart() );

		return( result );
	}

	public void startPopupMenu()
	{
		if( _popupMenu == null )
		{
			_popupMenu = new CopyPastePopup( this );
			_clickListener = new ClickListener();
			_textComp.getComponent().addMouseListener( _clickListener );
		}
	}

	public void stopPopupMenu()
	{
//		if(  _enablePopupMenu )
		{
			_textComp.getComponent().removeMouseListener( _clickListener );
		}
	}

	protected UndoRedoInterface getOrCreateUndoRedoManager()
	{
		if( _undoRedoManager == null )
			_undoRedoManager = DesktopGenericFunctions.instance().getViewFacilities().createTextUndoRedoObject( _textComp );
		
		return( _undoRedoManager );
	}

	public void startUndoRedoManager()
	{
		if( !getOrCreateUndoRedoManager().isStarted() )
			getOrCreateUndoRedoManager().startManaging();
	}

	public boolean undoRedoManagerIsStarted()
	{
		return( (_undoRedoManager != null ) && _undoRedoManager.isStarted() );
	}

	public void stopUndoRedoManager()
	{
		if( undoRedoManagerIsStarted() )
			_undoRedoManager.stopManaging();
	}

	public void setUrlOrEmailAddress( StringAndPosition urlOrEmailAddress )
	{
		_urlOrEmailAddress = urlOrEmailAddress;
	}

	protected class ClickListener extends com.frojasg1.general.desktop.controller.ClickListener
	{
		public ClickListener( )
		{
		}

		@Override
		public void rightClick( MouseEvent evt )
		{
//			if( isCopyingEnabled() || isCuttingEnabled() || isPastingEnabled() )
				_popupMenu.doPopup( evt );
		}
	}

}
