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
package com.frojasg1.general.desktop.search;

import com.frojasg1.general.desktop.keyboard.listener.GenericKeyDispatcherInterface;
import com.frojasg1.general.desktop.keyboard.listener.imp.KeyImp;
import com.frojasg1.general.desktop.undoredo.UndoRedoKeyListener;
import com.frojasg1.general.desktop.view.search.DesktopSearchAndReplaceWindow;
import com.frojasg1.general.executor.imp.ExecutorBase;
import com.frojasg1.general.search.RegExException;
import com.frojasg1.general.search.SearchReplaceTextInterface.ReplaceResultInterface;
import com.frojasg1.general.search.SearchReplaceTextInterface.ReplaceSettingsInterface;
import com.frojasg1.general.search.imp.SearchReplaceText;
import com.frojasg1.general.undoredo.text.TextUndoRedoInterface;
import java.awt.event.KeyEvent;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * 
 * This Class is not dessigned to support multiple threads replacing strings on the same Text component.
 * It is designed to be used for only one thread each object and Text component of these class.
 */
public class DesktopSearchReplaceText extends SearchReplaceText
{
	protected Lock _lock = new ReentrantLock();
	protected Condition _conditionToObtainResult_replaceAll = _lock.newCondition();
	protected Condition _conditionToObtainResult_replace = _lock.newCondition();

	public DesktopSearchReplaceText( TextUndoRedoInterface undoRedoManagerOfTextComp )
	{
		super( undoRedoManagerOfTextComp );
	}

	@Override
	public void initialize()
	{
		super.initialize();

		setDefaultKeys();
	}

	protected void setDefaultKeys()
	{
		if( _undoRedoManager instanceof GenericKeyDispatcherInterface )
		{
			GenericKeyDispatcherInterface disp = (GenericKeyDispatcherInterface) _undoRedoManager;
			disp.addKey(UndoRedoKeyListener.KEY_ID_FOR_OPEN_SEARCH,
							new KeyImp( KeyEvent.VK_F, KeyEvent.CTRL_MASK ),
							new ExecutorBase() {
									public void execute()
									{
										String searchText = _textComp.getSelectedText();
										DesktopSearchAndReplaceWindow.instance().openForSearch(searchText);
									}
								});
			disp.addKey(UndoRedoKeyListener.KEY_ID_FOR_OPEN_REPLACE,
							new KeyImp( KeyEvent.VK_H, KeyEvent.CTRL_MASK ),
							new ExecutorBase() {
									public void execute()
									{
										String searchText = _textComp.getSelectedText();
										DesktopSearchAndReplaceWindow.instance().openForReplace(searchText);
									}
								});
			disp.addKey(UndoRedoKeyListener.KEY_ID_FOR_SEARCH_FORWARD_AGAIN,
							new KeyImp( KeyEvent.VK_F3, 0 ),
							new ExecutorBase() {
									public void execute()
									{
										boolean forward = true;
										DesktopSearchAndReplaceWindow.instance().search( forward );
									}
								});
			disp.addKey(UndoRedoKeyListener.KEY_ID_FOR_SEARCH_BACKWARDS_AGAIN,
							new KeyImp( KeyEvent.VK_F3, KeyEvent.SHIFT_MASK ),
							new ExecutorBase() {
									public void execute()
									{
										boolean forward = false;
										DesktopSearchAndReplaceWindow.instance().search( forward );
									}
								});
		}
	}
	
	protected ReplaceResultInterface[] replaceAll_forNonEDT(//TextUndoRedoInterface undoRedoManagerOfTextComp,
															ReplaceSettingsInterface replaceSettings )
	{
		_lock.lock();
		try
		{
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						_tmp_ree = null;
						_tmpResult_replaceAll = replaceAll( //undoRedoManagerOfTextComp,
															replaceSettings );
					}
					catch( RegExException ree )
					{
						_tmp_ree = ree;
						_tmpResult_replace = null;
					}
					catch( Throwable th )
					{
						th.printStackTrace();
						_tmpResult_replaceAll = null;
					}
					finally
					{
						_conditionToObtainResult_replaceAll.signal();
					}
				}
			});

			_conditionToObtainResult_replaceAll.await();
			return( _tmpResult_replaceAll );
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
			return( null );
		}
		finally
		{
			_lock.unlock();
		}
	}

	@Override
	public ReplaceResultInterface[] replaceAll(//TextUndoRedoInterface undoRedoManagerOfTextComp,
												ReplaceSettingsInterface replaceSettings ) throws RegExException
	{
		ReplaceResultInterface[] result = null;
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			result = replaceAll_forNonEDT( //undoRedoManagerOfTextComp,
											replaceSettings );
			if( _tmp_ree != null )
				throw( _tmp_ree );

			return( result );
		}

		result = super.replaceAll( //undoRedoManagerOfTextComp,
									replaceSettings);
		return( result );
	}

	protected ReplaceResultInterface replace_forNonEDT( ReplaceSettingsInterface settings )
	{
		_lock.lock();
		try
		{
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					try
					{
						_tmp_ree = null;
						_tmpResult_replace = replace( settings );
					}
					catch( RegExException ree )
					{
						_tmp_ree = ree;
						_tmpResult_replace = null;
					}
					catch( Throwable th )
					{
						th.printStackTrace();
						_tmpResult_replace = null;
					}
					finally
					{
						_conditionToObtainResult_replace.signal();
					}
				}
			});

			_conditionToObtainResult_replace.await();
			return( _tmpResult_replace );
		}
		catch( InterruptedException ie )
		{
			ie.printStackTrace();
			return( null );
		}
		finally
		{
			_lock.unlock();
		}
	}

	@Override
	public ReplaceResultInterface replace( ReplaceSettingsInterface settings ) throws RegExException
	{
		ReplaceResultInterface result = null;
		if( ! SwingUtilities.isEventDispatchThread() )
		{
			result = replace_forNonEDT( settings );
			if( _tmp_ree != null )
				throw( _tmp_ree );

			return( result );
		}

		result = super.replace( settings );
		return( result );
	}

}
