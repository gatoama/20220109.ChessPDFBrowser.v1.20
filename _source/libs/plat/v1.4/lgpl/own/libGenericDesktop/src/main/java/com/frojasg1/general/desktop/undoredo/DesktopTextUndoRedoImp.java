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
package com.frojasg1.general.desktop.undoredo;

import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.string.StringFunctions;
import com.frojasg1.general.desktop.keyboard.listener.GenericKeyDispatcherInterface;
import com.frojasg1.general.desktop.keyboard.listener.KeyInterface;
import com.frojasg1.general.desktop.keyboard.listener.imp.KeyImp;
import com.frojasg1.general.desktop.view.ViewFunctions;
import com.frojasg1.general.desktop.generic.view.imp.DesktopViewFacilitiesImp;
import com.frojasg1.general.desktop.generic.view.DesktopViewTextComponent;
import com.frojasg1.general.executor.ExecutorInterface;
import com.frojasg1.general.executor.GenericExecutor;
import com.frojasg1.general.executor.imp.ExecutorBase;
import com.frojasg1.general.search.SearchReplaceTextInterface;
import com.frojasg1.general.undoredo.UndoRedoListener;
import com.frojasg1.general.undoredo.text.imp.ListOfTextUndoRedoListeners;
import com.frojasg1.general.undoredo.text.TextUndoRedoElementInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import com.frojasg1.general.undoredo.text.TextUndoRedoListener;
import com.frojasg1.general.undoredo.text.UndoRedoTextHistoryInterface;
import com.frojasg1.general.undoredo.text.imp.TextUndoRedoElementImp;
import com.frojasg1.general.undoredo.text.imp.UndoRedoTextHistoryImp;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DesktopTextUndoRedoImp
		implements TextUndoRedoInterface< TextUndoRedoElementInterface,
											SearchReplaceTextInterface.ReplaceResultInterface,
											DesktopViewTextComponent >,
					DocumentListener, CaretListener, ChangeListener, GenericKeyDispatcherInterface
{
	protected static final long TIME_TO_CORRELATE_EVENTS = 350;

	protected DesktopViewTextComponent _viewTextComponent;
	protected UndoRedoTextHistoryInterface<TextUndoRedoElementInterface> _undoRedoHistory;
	protected ListOfTextUndoRedoListeners _listeners;

	protected UndoRedoKeyListener _keyListener;
	protected boolean _alreadyStarted = false;

	protected TextUndoRedoElementInterface _lastElementForUndo;
	protected String _lastDocumentText;

//	protected Collection<TextUndoRedoElementInterface> _lastElementUndoDone;
//	protected Collection<TextUndoRedoElementInterface> _lastElementRedoDone;
//	protected TextUndoRedoElementInterface _lastElementUndoDone;
//	protected TextUndoRedoElementInterface _lastElementRedoDone;
	protected LastElementUndoneRedone _lastElementUndoDone = new LastElementUndoneRedone();
	protected LastElementUndoneRedone _lastElementRedoDone = new LastElementUndoneRedone();

	protected long _timestampLastSelectionChange = 0;
	protected int _selectionStartPos = -1;
	protected String _selectedText = null;
	protected TextUndoRedoElementInterface _undoForSelectionRemoval;

	public DesktopTextUndoRedoImp( JTextComponent textComp )
	{
		this((DesktopViewTextComponent) DesktopViewFacilitiesImp.instance().createTextViewComponent(textComp) );
	}

	public DesktopTextUndoRedoImp( DesktopViewTextComponent view )
	{
		registerViewDTUR( view );
	}

	public void initilize()
	{
		_listeners = new ListOfTextUndoRedoListeners();
//		_viewTextComponent.getComponent().getDocument().addDocumentListener(this);
//		_viewTextComponent.getComponent().addCaretListener(this);
		addKey(UndoRedoKeyListener.KEY_ID_FOR_UNDO,
				new KeyImp( KeyEvent.VK_Z, KeyEvent.CTRL_MASK ),
				new ExecutorBase() {
					@Override
					public void execute()
					{
						undo();
					}
				});
		addKey(UndoRedoKeyListener.KEY_ID_FOR_REDO,
				new KeyImp( KeyEvent.VK_Y, KeyEvent.CTRL_MASK ),
				new ExecutorBase() {
					@Override
					public void execute()
					{
						redo();
					}
				});
	}

	
	
	protected UndoRedoKeyListener getKeyListener()
	{
		if( _keyListener == null )
			_keyListener = new UndoRedoKeyListener();

		return( _keyListener );
	}

	protected final void registerViewDTUR( DesktopViewTextComponent view )
	{
		if( _viewTextComponent != null )
		{
			_viewTextComponent.getComponent().getCaret().removeChangeListener(this);
		}

		_viewTextComponent = view;
		if( _viewTextComponent != null )
		{
			_viewTextComponent.getComponent().getCaret().addChangeListener(this);
		}
	}

	public boolean hasElementsToUndo()
	{
		return( getHistory().hasElementsToUndo() );
	}

	public boolean hasElementsToRedo()
	{
		return( getHistory().hasElementsToRedo() );
	}

	protected void clearUndoList()
	{
		getHistory().clearUndoList();
		_listeners.undoListHasChanged();
	}

	protected void setNewUndoElement( TextUndoRedoElementInterface elem )
	{
		getHistory().setNewUndoElement(elem);
		_listeners.undoListHasChanged();
	}
	
	protected void setNewUndoElement( Collection<TextUndoRedoElementInterface> tureiCol )
	{
		getHistory().setNewUndoElement(tureiCol);
		_listeners.undoListHasChanged();
	}

	protected Collection<TextUndoRedoElementInterface> removeElementToUndo()
	{
		Collection<TextUndoRedoElementInterface> result = getHistory().removeElementToUndo();
		_listeners.undoListHasChanged();

		return( result );
	}

	protected void clearRedoList()
	{
		getHistory().clearRedoList();
		_listeners.redoListHasChanged();
	}

	protected void setNewRedoElement( TextUndoRedoElementInterface elem )
	{
		getHistory().setNewRedoElement(elem);
		_listeners.redoListHasChanged();
	}

	protected void setNewRedoElement( Collection<TextUndoRedoElementInterface> tureiCol )
	{
		getHistory().setNewRedoElement(tureiCol);
		_listeners.redoListHasChanged();
	}

	protected Collection<TextUndoRedoElementInterface> removeElementToRedo()
	{
		Collection<TextUndoRedoElementInterface> result = getHistory().removeElementToRedo();
		_listeners.redoListHasChanged();

		return( result );
	}

	@Override
	public boolean isStarted()
	{
		return( _alreadyStarted );
	}

	@Override
	public void setUndoRedoTextHistoryObject( UndoRedoTextHistoryInterface< TextUndoRedoElementInterface > urthi )
	{
		_undoRedoHistory = urthi;
	}

	protected UndoRedoTextHistoryInterface<TextUndoRedoElementInterface> getHistory()
	{
		if( _undoRedoHistory == null )
			_undoRedoHistory = new UndoRedoTextHistoryImp<TextUndoRedoElementInterface>();

		return( _undoRedoHistory );
	}

	@Override
	public void registerView( DesktopViewTextComponent view )
	{
		registerViewDTUR( view );
	}

	@Override
	public DesktopViewTextComponent getView()
	{
		return( _viewTextComponent );
	}

	@Override
	public void undo()
	{
		if( _alreadyStarted )
		{
			if( !SwingUtilities.isEventDispatchThread() )
			{
				SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						undo();
					}
				});
				
				return;
			}

			synchronized( this )
			{
				Collection<TextUndoRedoElementInterface> colToUndo = removeElementToUndo();

				if( colToUndo != null )
				{
//					_lastElementUndoDone = colToUndo;
					_lastElementForUndo = null;
					
					Iterator<TextUndoRedoElementInterface> it = colToUndo.iterator();
					while( it.hasNext() )
					{
						TextUndoRedoElementInterface elem = it.next();
						if( elem != null )
						{
							_lastElementUndoDone.setElem( elem );
							undo( elem );
						}
					}

					_lastElementUndoDone.setElem( null );

					try
					{
						CollectionFunctions.instance().reverseOrder( colToUndo );
					}
					catch( Throwable th )
					{
						th.printStackTrace();
					}

					setNewRedoElement( colToUndo );
				}
			}
		}
	}

	@Override
	public void undo( TextUndoRedoElementInterface elem )
	{
		if( _alreadyStarted )
		{
			if( !SwingUtilities.isEventDispatchThread() )
			{
				SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						undo(elem);
					}
				});
				return;
			}

			synchronized(this)
			{
				getView().replaceText(elem.getStartOfElement(), elem.getNewStringElement(), elem.getPreviousStringElement());
				ViewFunctions.instance().setFocusedComponent( getView().getComponent() );
			}
		}
	}

	@Override
	public void redo( TextUndoRedoElementInterface elem )
	{
		if( _alreadyStarted )
		{
			if( !SwingUtilities.isEventDispatchThread() )
			{
				SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						redo(elem);
					}
				});
				return;
			}

			synchronized(this)
			{
				getView().replaceText(elem.getStartOfElement(), elem.getPreviousStringElement(), elem.getNewStringElement());
				ViewFunctions.instance().setFocusedComponent( getView().getComponent() );
			}
		}
	}

	@Override
	public synchronized void redo()
	{
		if( _alreadyStarted )
		{
			if( !SwingUtilities.isEventDispatchThread() )
			{
				SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
						redo();
					}
				});
				return;
			}

			synchronized( this )
			{
				Collection<TextUndoRedoElementInterface> colToRedo = removeElementToRedo();

				if( colToRedo != null )
				{
//					_lastElementRedoDone = colToUndo;
					_lastElementForUndo = null;

					Iterator<TextUndoRedoElementInterface> it = colToRedo.iterator();
					while( it.hasNext() )
					{
						TextUndoRedoElementInterface elem = it.next();
						if( elem != null )
						{
							_lastElementRedoDone.setElem( elem );
							redo( elem );
						}
					}

					_lastElementRedoDone.setElem( null );
					
					try
					{
						CollectionFunctions.instance().reverseOrder( colToRedo );
					}
					catch( Throwable th )
					{
						th.printStackTrace();
					}

					setNewUndoElement( colToRedo );
				}
			}
		}
	}

	@Override
	public void startManaging()
	{
		if( ! _alreadyStarted )
		{
			getView().getComponent().addKeyListener( getKeyListener() );
			getView().getComponent().getDocument().addDocumentListener(this);
			getView().getComponent().addCaretListener(this);
			_lastDocumentText = getView().getText();
		}
		_alreadyStarted = true;
	}

	@Override
	public void stopManaging()
	{
		if( _alreadyStarted )
		{
			if( _keyListener != null )
				getView().getComponent().removeKeyListener(_keyListener);
			getView().getComponent().getDocument().removeDocumentListener(this);
			getView().getComponent().removeCaretListener(this);
//			getHistory().clearRedoList();
//			getHistory().clearUndoList();
		}
		_alreadyStarted = false;
	}

	@Override
	public void registerListener( TextUndoRedoListener rel )
	{
		_listeners.add(rel);
	}

	@Override
	public void unregisterListener( TextUndoRedoListener rel )
	{
		_listeners.remove(rel);
	}

	protected TextUndoRedoElementInterface getSingleElem( Collection<TextUndoRedoElementInterface> coll )
	{
		TextUndoRedoElementInterface result = null;
		if( ( coll != null ) && ( coll.size() == 1 ) )
		{
			Iterator<TextUndoRedoElementInterface> it = coll.iterator();
			if( it.hasNext() )
				result = it.next();
		}

		return( result );
	}

	protected boolean hasToStoreUndoAfterInsert( LastElementUndoneRedone lastElementUndone,
													LastElementUndoneRedone lastElementRedone,
													int start,
													String textToReplaceTo )
	{
		boolean result = false;

		int lastStart = -1;
		String lastStringReplacedTo = null;

		TextUndoRedoElementInterface lastUndoElem = lastElementUndone.getElem();
		TextUndoRedoElementInterface lastRedoElem = lastElementRedone.getElem();

		if( ( lastUndoElem != null ) &&
			!lastElementUndone.isRemovalChecked() &&
			lastElementUndone.timeStampMatches() )
		{
			lastStart = lastUndoElem.getStartOfElement();
			lastStringReplacedTo = lastUndoElem.getPreviousStringElement();
			lastElementUndone.removalChecked();
		}
		else if( ( lastRedoElem != null ) &&
				!lastElementRedone.isInsertionChecked() &&
				lastElementRedone.timeStampMatches() )
		{
			lastStart = lastRedoElem.getStartOfElement();
			lastStringReplacedTo = lastRedoElem.getNewStringElement();
			lastElementRedone.insertionChecked();
		}

		result = ( start != lastStart ) ||
				( ! textToReplaceTo.equals( lastStringReplacedTo ) );

		return( result );
	}

	protected boolean hasToStoreUndoAfterRemove( LastElementUndoneRedone lastElementUndone,
													LastElementUndoneRedone lastElementRedone,
													int start,
													int length )
	{
		boolean result = false;

		int lastStart = -1;
		int lastStringRemoved = -1;

		TextUndoRedoElementInterface lastUndoElem = lastElementUndone.getElem();
		TextUndoRedoElementInterface lastRedoElem = lastElementRedone.getElem();

		if( ( lastUndoElem != null ) &&
			! lastElementUndone.isInsertionChecked() &&
			lastElementUndone.timeStampMatches() )
		{
			lastStart = lastUndoElem.getStartOfElement();
			lastStringRemoved = lastUndoElem.getNewStringElement().length();
			lastElementUndone.insertionChecked();
		}
		else if( ( lastRedoElem != null ) &&
				! lastElementRedone.isRemovalChecked() &&
				lastElementRedone.timeStampMatches() )
		{
			lastStart = lastRedoElem.getStartOfElement();
			lastStringRemoved = lastRedoElem.getPreviousStringElement().length();
			lastElementRedone.removalChecked();
		}

		result = ( start != lastStart ) ||
				!( lastStringRemoved == length );

		return( result );
	}

	protected String getText( Document doc )
	{
		String result = _lastDocumentText;

		try
		{
			result = doc.getText( 0, doc.getLength() );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected boolean timeStampMatches( long timeStamp, long now, long maxDelay )
	{
		return( ( now - timeStamp ) <= maxDelay );
	}

	protected boolean timeStampMatches()
	{
		return( timeStampMatches( _timestampLastSelectionChange, System.currentTimeMillis(), TIME_TO_CORRELATE_EVENTS ) );
	}

	protected TextUndoRedoElementInterface getLastSingleUndoElement()
	{
		TextUndoRedoElementInterface result = null;

		if( getHistory().hasElementsToUndo() )
		{
			Collection< TextUndoRedoElementInterface > lastCollectionToUndo = getHistory().removeElementToUndo();

			if( ( lastCollectionToUndo != null ) &&
				( lastCollectionToUndo.size() == 1 ) )
			{
				result = lastCollectionToUndo.iterator().next();
			}

			getHistory().setNewUndoElement(lastCollectionToUndo);
		}

		return( result );
	}

	protected boolean equals( TextUndoRedoElementInterface undoElem,
								TextUndoRedoElementInterface other )
	{
		boolean result = false;

		if( ( undoElem != null ) && ( other != null ) )
		{
			result = ( undoElem.equals(other) );
		}

		return( result );
	}

	protected boolean comesFromSelectionReplacement( int pos )
	{
		boolean result = false;
		if( timeStampMatches() && ( _selectionStartPos == pos ) )
		{
			if( _lastElementForUndo == null )
			{
				result = equals( getLastSingleUndoElement(), _undoForSelectionRemoval );
			}
		}

		return( result );
	}

	protected void replaceSelectionRemoveUndoElement( String textToReplaceTo )
	{
		TextUndoRedoElementInterface lastUndoElem = getLastSingleUndoElement();

		if( ( lastUndoElem != null ) &&
			StringFunctions.instance().isEmpty( lastUndoElem.getNewStringElement() )
			)
		{
			lastUndoElem.setNewStringElement( textToReplaceTo );
		}
	}

	@Override
	public void insertUpdate(DocumentEvent evt)
	{
		_listeners.originalElementHasChanged();

		int pos = evt.getOffset();
		int length = evt.getLength();

//		_lastDocumentText = getView().getText();
		_lastDocumentText = getText( evt.getDocument() );
		String textToReplaceTo = _lastDocumentText.substring( pos, pos + length );

		if( ! hasToStoreUndoAfterInsert( _lastElementUndoDone, _lastElementRedoDone, pos, textToReplaceTo ) )
		{
			_lastElementUndoDone.setElem( null );
			_lastElementRedoDone.setElem( null );

			return;
		}

		clearRedoList();

		if( comesFromSelectionReplacement( pos ) )
		{
			replaceSelectionRemoveUndoElement( textToReplaceTo );
			_lastElementForUndo = null;
		}
		else if( length > 1 )
		{
			_lastElementForUndo = createTextUndoRedoElement( pos, "", textToReplaceTo );
			setNewUndoElement(_lastElementForUndo);
			_lastElementForUndo = null;
		}
		else if( length == 1 )
		{
			boolean newElem = true;
			if( _lastElementForUndo != null )
			{
				String newString = _lastElementForUndo.getNewStringElement();
				if( newString.length() > 0 )
				{
					if( pos == _lastElementForUndo.getStartOfElement() + newString.length() )
					{
						boolean newCharIsWhiteSpace = Character.isWhitespace(textToReplaceTo.charAt(0));
						boolean lastCharWasWhiteSpace = Character.isWhitespace(newString.charAt(newString.length() - 1));
						if( newCharIsWhiteSpace && !lastCharWasWhiteSpace )
						{
						}
						else if( !newCharIsWhiteSpace && lastCharWasWhiteSpace && ( newString.length() > 1 ) )
						{
						}
						else
						{
							_lastElementForUndo.setNewStringElement( newString + textToReplaceTo );		// we add the new char to the last undo element.
							newElem = false;
						}
					}
				}
			}

			if( newElem )
			{
				_lastElementForUndo = createTextUndoRedoElement( pos, "", textToReplaceTo );
				setNewUndoElement(_lastElementForUndo);
			}

			if( _lastElementForUndo.isEmpty() )
			{
				removeElementToUndo();
				_lastElementForUndo = null;
			}
		}

	}

	protected boolean replacesSelection( String textToReplaceFrom, int pos )
	{
		return( ( pos == _selectionStartPos ) && textToReplaceFrom.equals( _selectedText ) );
	}

	protected void saveUndoForSelectionReplacement( TextUndoRedoElementInterface undoForSelectionRemoval )
	{
		_timestampLastSelectionChange = System.currentTimeMillis();
		_undoForSelectionRemoval = undoForSelectionRemoval;
	}

	@Override
	public void removeUpdate(DocumentEvent evt)
	{
		_listeners.originalElementHasChanged();

		int pos = evt.getOffset();
		int length = evt.getLength();

		String textToReplaceFrom = _lastDocumentText.substring( pos, pos + length );
//		_lastDocumentText = getView().getText();
		_lastDocumentText = getText( evt.getDocument() );

		if( ! hasToStoreUndoAfterRemove( _lastElementUndoDone, _lastElementRedoDone, pos, length ) )
		{
			if( _lastElementUndoDone.isRemovalChecked() )
				_lastElementUndoDone.setElem( null );

			if( _lastElementRedoDone.isInsertionChecked() )
				_lastElementRedoDone.setElem( null );

			return;
		}

//		_lastElementForUndo = new TextUndoRedoElementImp( pos, textToReplaceFrom, "" );
//		getHistory().setNewUndoElement(_lastElementForUndo);
//		_lastElementForUndo = null;

		clearRedoList();

		if( replacesSelection( textToReplaceFrom, pos ) )
		{
			_lastElementForUndo = createTextUndoRedoElement( pos, textToReplaceFrom, "" );
			setNewUndoElement(_lastElementForUndo);

			saveUndoForSelectionReplacement( _lastElementForUndo );
			_lastElementForUndo = null;
		}
		else if( length > 1 )
		{
			_lastElementForUndo = createTextUndoRedoElement( pos, textToReplaceFrom, "" );
			setNewUndoElement(_lastElementForUndo);
			_lastElementForUndo = null;
		}
		else if( length == 1 )
		{
			boolean newElem = true;
			if( _lastElementForUndo != null )
			{
				String newString = _lastElementForUndo.getNewStringElement();
				char previousChar = ( pos == 0 ? 'a' : _lastDocumentText.charAt( pos-1 ) );
				String previousString = _lastElementForUndo.getPreviousStringElement();
				boolean removedCharIsWhiteSpace = Character.isWhitespace(textToReplaceFrom.charAt(0));
				boolean firstPreviousCharWasWhiteSpace = ( previousString.length() > 0 ? Character.isWhitespace(previousString.charAt(0)) : false );
				boolean previousCharIsWhiteSpace = Character.isWhitespace( previousChar );

				if( newString.length() == 0 )
				{
					if( pos == ( _lastElementForUndo.getStartOfElement() - 1 ) )
					{
						if( ( !removedCharIsWhiteSpace && firstPreviousCharWasWhiteSpace ) ||
								( previousCharIsWhiteSpace && removedCharIsWhiteSpace && !firstPreviousCharWasWhiteSpace ) )
						{
							
						}
						else
						{
							_lastElementForUndo.setPreviousStringElement( textToReplaceFrom + previousString );
							_lastElementForUndo.setStartOfElement(pos);
							newElem = false;
						}
					}
				}
				else if( pos == _lastElementForUndo.getStartOfElement() + newString.length() - 1 )
				{
					_lastElementForUndo.setNewStringElement( newString.substring( 0, newString.length() - 1 ) );		// we add the new char to the last undo element.
					newElem = false;
				}
			}

			if( newElem )
			{
				_lastElementForUndo = createTextUndoRedoElement( pos, textToReplaceFrom, "" );
				setNewUndoElement(_lastElementForUndo);
			}
			
			if( _lastElementForUndo.isEmpty() )
			{
				removeElementToUndo();
				_lastElementForUndo = null;
			}
		}

	}

	protected TextUndoRedoElementInterface createTextUndoRedoElement( int pos,
																	String textToReplaceFrom,
																	String textToReplaceTo )
	{
		return( new TextUndoRedoElementImp( pos, textToReplaceFrom, textToReplaceTo ) );
	}

	protected void resetUndoRedo_nonEDT()
	{
		SwingUtilities.invokeLater( new Runnable(){
				public void run()
				{
					resetUndoRedo();
				}
		});
	}

	@Override
	public void resetUndoRedo()
	{
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			resetUndoRedo_nonEDT();
			return;
		}

		clearRedoList();
		clearUndoList();
	}

	@Override
	public void changedUpdate(DocumentEvent evt)
	{
		//Plain text components do not fire these events
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		_listeners.caretHasChanged();
	}

	@Override
	public void registerListener(UndoRedoListener rel)
	{
		if( rel instanceof TextUndoRedoListener )
		{
			TextUndoRedoListener turl = (TextUndoRedoListener) rel;
			registerListener( turl );
		}
	}

	@Override
	public void unregisterListener(UndoRedoListener rel)
	{
		if( rel instanceof TextUndoRedoListener )
		{
			TextUndoRedoListener turl = (TextUndoRedoListener) rel;
			unregisterListener( turl );
		}
	}
/*
	protected class UndoRedoKeyListener implements KeyListener
	{
		@Override
		public void keyTyped(KeyEvent e)
		{
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			if( (e.getModifiers() & KeyEvent.CTRL_MASK) != 0 )	//CTRL
			{
				if( e.getKeyCode() == KeyEvent.VK_Z )
					undo();
				else if( e.getKeyCode() == KeyEvent.VK_Y )
					redo();
			}
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
		}
	}
*/

	// TypeOfKey is one of the public static final int in UndoRedoKeyListener class.
	@Override
	public void addKey(int typeOfKey, KeyInterface key, GenericExecutor executor)
	{
		removeKeyBinding( key );
		getKeyListener().addKey( typeOfKey, key, executor );
	}

	// https://tips4java.wordpress.com/2008/10/10/key-bindings/
	public void removeKeyBinding( KeyInterface key )
	{
		if( key != null )
		{
			KeyStroke ks = KeyStroke.getKeyStroke( key.getKeyCode(), key.getModifiers() );
			
			InputMap im = _viewTextComponent.getComponent().getInputMap();
			im.put(ks, "none" );
		}
	}

	// TypeOfKey is one of the public static final int in UndoRedoKeyListener class.
	@Override
	public void removeKey(int typeOfKey)
	{
		getKeyListener().removeKey( typeOfKey );
	}

	@Override
	public void dispatchKeyEvent(KeyEvent evt)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void changeKey(int typeOfKey, KeyInterface key)
	{
		getKeyListener().changeKey( typeOfKey, key );
	}

	@Override
	public void changeExecutor(int typeOfKey, GenericExecutor executor)
	{
		getKeyListener().changeExecutor( typeOfKey, executor );
	}

	protected Boolean matchesReplacement( SearchReplaceTextInterface.ReplaceResultInterface replacement,
											List<TextUndoRedoElementInterface> list,
											int position, int numElements )
	{
		Boolean result = null;
		
		if( ( numElements > 2 ) || ( list.size() < numElements ) )
			result = false;

		boolean canMatch = true;
		TextUndoRedoElementInterface elem = null;
		if( ( result == null ) && ( numElements >= 1 ) )
		{
			elem = list.get(position);
			if( elem.isPureInsertion() )
			{
				canMatch =	( elem.getStartOfElement() == replacement.getStart() ) &&
							StringFunctions.instance().stringsEquals( elem.getNewStringElement(), replacement.getStringToReplaceTo() );
				if( !canMatch )
					result = false;
			}
			else if( elem.isReplacement() )
			{
				result = ( StringFunctions.instance().stringsEquals( elem.getNewStringElement(), replacement.getStringToReplaceTo() ) &&
							StringFunctions.instance().stringsEquals( elem.getPreviousStringElement(), replacement.getMatchedString() ) &&
							( elem.getStartOfElement() == replacement.getStart() ) );
			}
			else
				result = false;
		}

		if( ( result == null ) && ( numElements >= 2 ) )
		{
			elem = list.get( position + 1 );
			if( elem.isPureRemoval() )
			{
				result = (  ( elem.getStartOfElement() == replacement.getStart() ) &&
							StringFunctions.instance().stringsEquals( elem.getPreviousStringElement(), replacement.getMatchedString() ) );
			}
			else
				result = false;
		}

		return( result );
	}

	protected Boolean matchesReplacement( SearchReplaceTextInterface.ReplaceResultInterface replacement,
											List<TextUndoRedoElementInterface> list )
	{
		Boolean result = matchesReplacement( replacement, list, 0, list.size() );

		return( result );
	}

	protected void giveBackUndoElements( List<TextUndoRedoElementInterface> list )
	{
		for( int ii=list.size() - 1; ii>=0; ii-- )
			getHistory().setNewUndoElement( list.get(ii) );
	}

	@Override
	public void overwriteUndoElement(SearchReplaceTextInterface.ReplaceResultInterface replacement)
	{
		List<TextUndoRedoElementInterface> list = new ArrayList<TextUndoRedoElementInterface>();

		Boolean matches = null;
		try
		{
			while( (matches = matchesReplacement( replacement, list ) ) == null )
			{
				TextUndoRedoElementInterface elem = getSingleElem( getHistory().removeElementToUndo() );
				list.add(elem);

				if( elem == null )
				{
					matches = false;
					break;
				}
			}

			if( matches )
			{
				getHistory().setNewUndoElement( createUndoElement( replacement ) );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		finally
		{
			if( ( matches != null ) && !matches )
				giveBackUndoElements( list );
		}
	}

	protected Boolean matchesArrayOfReplacements( SearchReplaceTextInterface.ReplaceResultInterface[] arrayOfReplacements,
													List<TextUndoRedoElementInterface> list )
	{
		Boolean result = null;

		int undoRedoIndex = 0;
		int replacementIndex = arrayOfReplacements.length - 1;
		Boolean resultTmp = null;

		while( (result == null) &&
				(undoRedoIndex < list.size() ) &&
				( replacementIndex >= 0 ) )
		{
			resultTmp = null;

			SearchReplaceTextInterface.ReplaceResultInterface replacement = arrayOfReplacements[ replacementIndex ];
			int undoRedoLength = 1;
			while( ( (undoRedoIndex + undoRedoLength - 1) < list.size() ) &&
					( resultTmp == null ) )
			{
				resultTmp = matchesReplacement( replacement, list, undoRedoIndex, undoRedoLength );
				undoRedoLength++;
			}

			if( ( resultTmp != null ) && !resultTmp )
				result = false;

			undoRedoIndex += undoRedoLength - 1;
			replacementIndex--;
		}

		if( ( replacementIndex == -1 ) && (result == null) &&
			( resultTmp != null ) && resultTmp )
		{
			result = true;
		}

		return( result );
	}

	@Override
	public void overwriteUndoElement(SearchReplaceTextInterface.ReplaceResultInterface[] arrayOfReplacements)
	{
		List<TextUndoRedoElementInterface> list = new ArrayList<TextUndoRedoElementInterface>();

		Boolean matches = null;
		try
		{
			while( (matches = matchesArrayOfReplacements( arrayOfReplacements, list ) ) == null )
			{
				TextUndoRedoElementInterface elem = getSingleElem( getHistory().removeElementToUndo() );
				list.add(elem);

				if( elem == null )
				{
					matches = false;
					break;
				}
			}

			if( matches )
			{
				getHistory().setNewUndoElement( createUndoElement( arrayOfReplacements ) );
			}
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
		finally
		{
			if( ( matches != null ) && !matches )
				giveBackUndoElements( list );
		}
	}

	protected TextUndoRedoElementInterface createUndoElement( SearchReplaceTextInterface.ReplaceResultInterface replacement )
	{
		TextUndoRedoElementInterface result = null;

		result = createTextUndoRedoElement( replacement.getStart(),
											replacement.getMatchedString(),
											replacement.getStringToReplaceTo() );

		return( result );
	}

	protected Collection<TextUndoRedoElementInterface> createUndoElement( SearchReplaceTextInterface.ReplaceResultInterface[] arrOfRepl )
	{
		Collection<TextUndoRedoElementInterface> result = new ArrayList<TextUndoRedoElementInterface>();

		if( arrOfRepl != null )
		{
			for( int ii=arrOfRepl.length-1; ii>=0; ii-- )
				result.add( createUndoElement( arrOfRepl[ii] ) );
		}

		return( result );
	}

	@Override
	public void caretUpdate(CaretEvent e)
	{
		JTextComponent jtc = _viewTextComponent.getComponent();

//		_timestampLastSelectionChange = System.currentTimeMillis();
		_selectionStartPos = jtc.getSelectionStart();
		_selectedText = jtc.getSelectedText();
	}

	protected static class LastElementUndoneRedone
	{
		protected TextUndoRedoElementInterface _elem = null;
		protected boolean _insertionChecked = false;
		protected boolean _removalChecked = false;

		protected long _timeStampOfUndoOrRedo = 0;

		public LastElementUndoneRedone()
		{
		}

		public TextUndoRedoElementInterface getElem()
		{
			return( _elem );
		}

		public void setElem( TextUndoRedoElementInterface elem )
		{
			_elem = elem;
			_timeStampOfUndoOrRedo = System.currentTimeMillis();

			_insertionChecked = true;
			_removalChecked = true;

			if( _elem != null )
			{
				if( _elem.isReplacement() )
				{
					_insertionChecked = false;
					_removalChecked = false;
				}
				else if( _elem.isPureInsertion() )
				{
					_insertionChecked = false;
					_removalChecked = true;
				}
				else if( _elem.isPureRemoval() )
				{
					_insertionChecked = true;
					_removalChecked = false;
				}
			}
		}

		public boolean isRemovalChecked()
		{
			return( _removalChecked );
		}

		public boolean isInsertionChecked()
		{
			return( _insertionChecked );
		}

		public void removalChecked()
		{
			_removalChecked = true;
		}

		public void insertionChecked()
		{
			_insertionChecked = true;
		}

		public boolean timeStampMatches()
		{
			return( ( System.currentTimeMillis() - _timeStampOfUndoOrRedo ) < TIME_TO_CORRELATE_EVENTS );
		}
	}
}
