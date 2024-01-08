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

import com.frojasg1.applications.common.components.internationalization.InternException;
import com.frojasg1.general.desktop.view.jtable.JTableFunctions;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Usuario
 */
public class ResizeRelocateItem_JTable extends ResizeRelocateItem
{
	protected int _originalRowHeightForTable = 1;

	protected Integer _originalCellPreferredHeight = null;

	public ResizeRelocateItem_JTable( JTable comp, int flags, ResizeRelocateItem_parent parent,
								boolean postpone_initialization, boolean isAlreadyZoomed ) throws InternException
	{
		super( comp,flags, parent, postpone_initialization, isAlreadyZoomed );

		initializeWithJTable( getComponent() );
	}

	@Override
	public JTable getComponent()
	{
		return( (JTable) super.getComponent() );
	}

	public final void initializeWithJTable( JTable table ) throws InternException
	{
		_originalRowHeightForTable = table.getRowHeight();

		TableCellRenderer tcr = getTableCellRenderer(0);
		if( tcr instanceof Component )
		{
			Component tcrComp = (Component) tcr;
			Dimension ps = tcrComp.getPreferredSize();
			if( ps != null )
				_originalCellPreferredHeight = ps.height;
		}
	}

	protected TableCellRenderer getTableCellRenderer( int column )
	{
		return( JTableFunctions.instance().getTableCellRenderer( getComponent(), column ) );
	}

	protected void updateJTable( double zoomFactor )
	{
		if( SwingUtilities.isEventDispatchThread() )
		{
			JTableFunctions.instance().zoomTableRows( getComponent(),
													_originalCellPreferredHeight,
													_originalRowHeightForTable,
													zoomFactor );
		}
		else
			SwingUtilities.invokeLater( () -> updateJTable( zoomFactor ) );
	}

	@Override
	protected Dimension execute_basic( double zoomFactor )
	{
		Dimension result = super.execute_basic( zoomFactor );

		if( ( result != null ) ||
			( _previousZoomFactor != zoomFactor ) ) // if it does not change, we do not change anything.
			updateJTable( zoomFactor );

		return( result );
	}
}
