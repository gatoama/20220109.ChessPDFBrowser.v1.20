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

import com.frojasg1.general.desktop.view.menus.BaseJPopupMenu;
import com.frojasg1.general.desktop.view.zoom.mapper.ComponentMapper;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CopyPastePopup extends BaseJPopupMenu
{
		JMenuItem _menuItem_copy = null;
		JMenuItem _menuItem_copyAddress = null;
		JMenuItem _menuItem_cut = null;
		JMenuItem _menuItem_paste = null;
		JMenuItem _menuItem_remove = null;
		JMenuItem _menuItem_undo = null;
		JMenuItem _menuItem_redo = null;

		TextCompPopupManager _manager = null;
		
		public CopyPastePopup( TextCompPopupManager manager )
		{
			super(manager.getViewTextComponent().getComponent());

			_manager = manager;

			createMenuIfAnyChange();
		}

		protected void createMenuIfAnyChange()
		{
			if( _menuItem_copy == null )
			{
				empty();

				createMenu();
			}
		}

		protected void createMenu()
		{
			_menuItem_copy = new JMenuItem( "Copy" );
//			if( _manager.isUrlEmailAddressTextComponent() )
				_menuItem_copyAddress = new JMenuItem( "Copy Address" );

			_menuItem_cut = new JMenuItem( "Cut" );
			_menuItem_paste = new JMenuItem( "Paste" );
			_menuItem_remove = new JMenuItem( "Remove" );
			_menuItem_undo = new JMenuItem( "Undo" );
			_menuItem_redo = new JMenuItem( "Redo" );

			_menuItem_redo.setName( "_menuItem_redo" );

			setIconForMenuItem( _menuItem_copy, "com/frojasg1/generic/resources/menuicons/copy.png" );
			setIconForMenuItem( _menuItem_copyAddress, "com/frojasg1/generic/resources/menuicons/copy.png" );
			setIconForMenuItem( _menuItem_cut, "com/frojasg1/generic/resources/menuicons/cut.png" );
			setIconForMenuItem( _menuItem_paste, "com/frojasg1/generic/resources/menuicons/paste.png" );
			setIconForMenuItem( _menuItem_remove, "com/frojasg1/generic/resources/menuicons/delete.png" );
			setIconForMenuItem( _menuItem_undo, "com/frojasg1/generic/resources/menuicons/undo.png" );
			setIconForMenuItem( _menuItem_redo, "com/frojasg1/generic/resources/menuicons/redo.png" );

			addMenuComponent( _menuItem_undo );
			addMenuComponent( _menuItem_redo );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_copy );

			if( _menuItem_copyAddress != null )
				addMenuComponent(_menuItem_copyAddress );

			addMenuComponent( _menuItem_cut );
			addMenuComponent( _menuItem_paste );
			addMenuComponent( new JSeparator() );
			addMenuComponent( _menuItem_remove );

			addMouseListenerToAllComponents();
		}

		protected void preparePopupMenuItems()
		{
			if( _menuItem_copyAddress != null )
			{
				_menuItem_copyAddress.setVisible( _manager.isUrlEmailAddressTextComponent() );
				_menuItem_copyAddress.setText( ConfForTextPopupMenu.instance().M_getStrParamConfiguration( ConfForTextPopupMenu.CONF_COPY_ADDRESS ) );
			}

			_menuItem_copy.setText( ConfForTextPopupMenu.instance().M_getStrParamConfiguration( ConfForTextPopupMenu.CONF_COPY ) );
			_menuItem_cut.setText( ConfForTextPopupMenu.instance().M_getStrParamConfiguration( ConfForTextPopupMenu.CONF_CUT ) );
			_menuItem_paste.setText( ConfForTextPopupMenu.instance().M_getStrParamConfiguration( ConfForTextPopupMenu.CONF_PASTE ) );
			_menuItem_remove.setText( ConfForTextPopupMenu.instance().M_getStrParamConfiguration( ConfForTextPopupMenu.CONF_REMOVE ) );
			_menuItem_undo.setText( ConfForTextPopupMenu.instance().M_getStrParamConfiguration( ConfForTextPopupMenu.CONF_UNDO ) );
			_menuItem_redo.setText( ConfForTextPopupMenu.instance().M_getStrParamConfiguration( ConfForTextPopupMenu.CONF_REDO ) );

			if( _menuItem_copyAddress != null )
				_menuItem_copyAddress.setEnabled( _manager.isUrlEmailAddressEnabled() );
			_menuItem_copy.setEnabled( _manager.isCopyingEnabled() );
			_menuItem_cut.setEnabled( _manager.isCuttingEnabled() );
			_menuItem_paste.setEnabled( _manager.isPastingEnabled() );
			_menuItem_remove.setEnabled( _manager.isRemovingEnabled() );
			_menuItem_undo.setEnabled( _manager.isUndoingEnabled() );
			_menuItem_redo.setEnabled( _manager.isRedoingEnabled() );
		}

		public void setAllEnabled( boolean value )
		{
			if( _menuItem_copyAddress != null )
				_menuItem_copyAddress.setEnabled( value );
			_menuItem_copy.setEnabled(value);
			_menuItem_cut.setEnabled(value);
			_menuItem_paste.setEnabled(value);
			_menuItem_remove.setEnabled(value);
			_menuItem_undo.setEnabled(value);
			_menuItem_redo.setEnabled(value);
		}

		public void doCopy()
		{
			_manager.doCopy();
		}

		public void doCopyAddress()
		{
			_manager.doCopyAddress();
		}

		public void doCut()
		{
			_manager.doCut();
		}

		public void doPaste()
		{
			_manager.doPaste();
		}

		public void doUndo()
		{
			_manager.doUndo();
		}
		
		public void doRedo()
		{
			_manager.doRedo();
		}
		
		public void doRemove()
		{
			_manager.doRemove();
		}

		@Override
		public void mouseEntered(MouseEvent me)
		{
		}

		@Override
		public void actionPerformed( ActionEvent evt )
		{
			try
			{
				Component comp = (Component) evt.getSource();

				if( comp == _menuItem_copy )
					doCopy();
				if( comp == _menuItem_copyAddress )
					doCopyAddress();
				else if( comp == _menuItem_cut )
					doCut();
				else if( comp == _menuItem_paste )
					doPaste();
				else if( comp == _menuItem_remove )
					doRemove();
				else if( comp == _menuItem_undo )
					doUndo();
				else if( comp == _menuItem_redo )
					doRedo();
			}
			finally
			{
				setVisible(false);
			}
		}

		@Override
		public void mouseExited(MouseEvent me)
		{
			super.mouseExited(me);
		}

	public void doPopup( MouseEvent evt )
	{
//		createMenuIfAnyChange();

		preparePopupMenuItems();
		show(evt.getComponent(), evt.getX() - 10, evt.getY() - 10);
		setVisible(false);
		show(evt.getComponent(), evt.getX() - 10, evt.getY() - 10);
	}

	@Override
	public void setComponentMapper(ComponentMapper mapper) {
		_menuItem_copy = mapper.mapComponent(_menuItem_copy);
		_menuItem_copyAddress = mapper.mapComponent(_menuItem_copyAddress);
		_menuItem_cut = mapper.mapComponent(_menuItem_cut);
		_menuItem_paste = mapper.mapComponent(_menuItem_paste);
		_menuItem_remove = mapper.mapComponent(_menuItem_remove);
		_menuItem_undo = mapper.mapComponent(_menuItem_undo);
		_menuItem_redo = mapper.mapComponent(_menuItem_redo);

		super.setComponentMapper(mapper);
	}

}
