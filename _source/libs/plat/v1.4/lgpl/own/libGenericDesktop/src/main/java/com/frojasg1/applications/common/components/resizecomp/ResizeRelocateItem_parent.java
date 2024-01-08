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
package com.frojasg1.applications.common.components.resizecomp;

import com.frojasg1.applications.common.components.internationalization.window.InternationalizedWindow;
import java.awt.Component;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 *
 * @author Usuario
 */
public interface ResizeRelocateItem_parent
{
	public InternationalizedWindow getInternationalizedWindow();

	public ResizeRelocateItem getResizeRelocateComponentItem( Component comp );
	public ResizeRelocateItem getResizeRelocateComponentItemOnTheFly( Component comp );

	public boolean isResizeRelocateItemsResizeListenersBlocked();

//	public void setIsMainMouseButtonClicked( boolean value );
//	public boolean isMainMouseButtonClicked( );
	public boolean isResizeDragging();

	public JPopupMenu getNonInheritedPopupMenu( JComponent jcomp );

	public void executeResizeRelocateItemRecursive( Component comp );

	public List<JPopupMenu> getPopupMenus();
	public void addPopupMenu( JPopupMenu jPopupMenu );
}
