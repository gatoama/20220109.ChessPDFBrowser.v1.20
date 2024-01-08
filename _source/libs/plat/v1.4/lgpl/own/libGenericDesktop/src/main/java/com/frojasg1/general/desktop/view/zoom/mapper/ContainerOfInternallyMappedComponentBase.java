/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.zoom.mapper;

import com.frojasg1.general.collection.impl.ThreadSafeGenListWrapper;
import java.util.List;
import javax.swing.JPopupMenu;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ContainerOfInternallyMappedComponentBase implements ContainerOfInternallyMappedComponent
{
	protected ThreadSafeGenListWrapper<InternallyMappedComponent> _internallyMappedComponentList = new ThreadSafeGenListWrapper<>();
	protected ThreadSafeGenListWrapper<JPopupMenu> _jPopupMenus = new ThreadSafeGenListWrapper<>();

	public List<InternallyMappedComponent> getInternallyMappedComponentListCopy() {
		return( _internallyMappedComponentList.getListCopy() );
	}

	public List<InternallyMappedComponent> getInternallyMappedComponentList() {
		return _internallyMappedComponentList.getList();
	}

	@Override
	public void addInternallyMappedComponent( InternallyMappedComponent im )
	{
		_internallyMappedComponentList.add(im);
	}

	public List<JPopupMenu> getJPopupMenuListCopy() {
		return( _jPopupMenus.getListCopy() );
	}

	public List<JPopupMenu> getPopupMenuList() {
		return _jPopupMenus.getList();
	}

	@Override
	public void addPopupMenu(JPopupMenu jPopupMenu)
	{
		_jPopupMenus.add(jPopupMenu);
	}
}
