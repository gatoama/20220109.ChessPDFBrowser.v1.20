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
package com.frojasg1.applications.common.components.internationalization.window;

import com.frojasg1.general.desktop.view.ViewFunctions;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MouseListenerForRepaint implements MouseListener
{
	protected static final int ADD = 0;
	protected static final int REMOVE = 1;

	protected Component _comp = null;

	protected ComponentWithOverlappedImage _ancestror = null;

	public MouseListenerForRepaint( Component comp )
	{
		_comp = comp;

		setMouseListener( _comp, ADD );
	}

	public void releaseResources()
	{
		removeListeners();
		_comp = null;
		_ancestror = null;
	}

	public void removeListeners()
	{
		setMouseListener( _comp, REMOVE );
	}

	protected void setMouseListener( Component comp, int addOrRemove )
	{
		if( comp != null )
		{
			if( addOrRemove == ADD )
				comp.addMouseListener(this);
			else if( addOrRemove == REMOVE )
				comp.removeMouseListener(this);

			if( comp instanceof JTabbedPane )
			{
				JTabbedPane tp = (JTabbedPane) comp;

				for( int ii=0; ii<tp.getTabCount(); ii++ )
					setMouseListener( tp.getComponentAt( ii ), addOrRemove );
			}
			else if( comp instanceof JDesktopPane )
			{
				// we skip DesktopPane children.
			}
			else if( comp instanceof Container )
			{
				Container cont = (Container) comp;

				for( int ii=0; ii<cont.getComponentCount(); ii++ )
					setMouseListener( cont.getComponent( ii ), addOrRemove );
			}

			if( comp instanceof JMenu )
			{
				setMouseListener( ( (JMenu) comp ).getPopupMenu(), addOrRemove );
			}
			else if( comp instanceof JFrame )
			{
				setMouseListener( ( (JFrame) comp ).getJMenuBar(), addOrRemove );
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
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
		execute( e.getComponent() );
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		execute( e.getComponent() );
	}

	protected ComponentWithOverlappedImage getAncestror()
	{
		if( _ancestror == null )
		{
			Component comp = _comp;
			while( ( _ancestror == null ) && ( comp != null ) )
			{
				if( comp instanceof ComponentWithOverlappedImage )
					_ancestror = (ComponentWithOverlappedImage) comp;
				else
					comp = comp.getParent();
			}
		}

		return( _ancestror );
	}

	protected boolean hasToRepaint( Component comp )
	{
		boolean result = false;

		if( !( comp instanceof JPanel ) &&
			_ancestror.isThereOverlappedImage() )
		{
			Rectangle rect = _ancestror.getOverlappingImageBounds();
			result = ViewFunctions.instance().componentOverlapsRectangle( comp, rect );
		}

		return( result );
	}

	protected void execute( Component comp )
	{
		ComponentWithOverlappedImage ancestror = getAncestror();
		if( ancestror != null )
		{
//			System.out.println( "execute" );
			if( hasToRepaint( comp ) )
			{
//				System.out.println( "there is overlappedImage" );

				_ancestror.repaint();
/*
				SwingUtilities.invokeLater( new Runnable(){
					@Override
					public void run()
					{
//						System.out.println( "repaint" );
						_ancestror.repaint();
					}
				});
*/
			}
		}
	}
}
