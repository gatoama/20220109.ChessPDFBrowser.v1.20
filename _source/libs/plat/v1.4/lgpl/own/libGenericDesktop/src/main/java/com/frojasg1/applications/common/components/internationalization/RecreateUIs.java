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
package com.frojasg1.applications.common.components.internationalization;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ButtonUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class RecreateUIs
{
	protected Component _comp = null;

	public RecreateUIs( Component comp )
	{
		_comp = comp;
	}

	public void execute()
	{
		executeRecursive( _comp );
	}

	protected void executeSimple( Component comp )
	{
		if( comp instanceof JComponent )
		{
			JComponent jc = (JComponent) comp;
			ButtonUI jb;
//			comp.get
		}
	}

	protected void executeRecursive( Component comp )
	{
		executeSimple( comp );
		
		if( comp instanceof Container )
		{
			Container cont = (Container) comp;
			
			if( cont instanceof JFrame )
			{
				JFrame frame = (JFrame) cont;
				executeRecursive( frame.getJMenuBar() );
			}
			else if( cont instanceof JTabbedPane )
			{
				JTabbedPane tpane = (JTabbedPane) cont;

				for( int ii=0; ii<tpane.getTabCount(); ii++ )
				{
					Component tab = tpane.getComponentAt( ii );
					executeRecursive( tab );
				}
			}
			else if( cont instanceof JMenu )
			{
				JMenu jm = (JMenu) cont;
				executeRecursive( jm.getPopupMenu() );
			}

			if( !( cont instanceof JDesktopPane ) )
			{
				for( int ii=0; ii<cont.getComponentCount(); ii++ )
				{
					executeRecursive( cont.getComponent(ii) );
				}
			}
		}
	}

}
