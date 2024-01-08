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
package com.frojasg1.general.desktop.view;

import com.frojasg1.general.ExecutionFunctions;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TableFunctions
{
	protected static TableFunctions _instance = null;

	public static TableFunctions instance()
	{
		if( _instance == null )
			_instance = new TableFunctions();

		return( _instance );
	}

	public Rectangle getColumnHeaderBounds( JTable table, int index )
	{
		JTableHeader header = table.getTableHeader();
//		int xx = header.getX();
		int xx = 0;
		int column = index; //the index of desired column
		for (int c = 0; c < column; c++){
			//adding all cell withds to come to your column
			xx = xx + header.getColumnModel().getColumn(c).getWidth();
		}
		
		int yy = header.getY();
		int width = header.getColumnModel().getColumn(index).getWidth();
		int height = header.getHeight();

		return( new Rectangle(xx, yy, width, height) );
	}

	public TableColumn getTableColumn( JTable table, Integer columnIndex )
	{
		TableColumn result = null;
		if( ( table != null ) && ( columnIndex != null ) )
		{
			Enumeration<TableColumn> enumeration = table.getColumnModel().getColumns();
			for( int ii=0; ii<=columnIndex && enumeration.hasMoreElements(); ii++ )
			{
				TableColumn tc = enumeration.nextElement();
				if( ii == columnIndex )
					result = tc;
			}
		}

		return( result );
	}

	// http://www.java2s.com/Tutorial/Java/0240__Swing/DeterminingIfaCellIsVisibleinaJTableComponent.htm
	public boolean isCellVisible(JTable table, int rowIndex, int vColIndex) {
		if (!(table.getParent() instanceof JViewport)) {
			return false;
		}
		JViewport viewport = (JViewport) table.getParent();
		Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
		Point pt = viewport.getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);

		return new Rectangle(viewport.getExtentSize()).contains(rect);
	}

	public boolean isRowVisible(JTable table, int rowIndex) {
		return( isCellVisible( table, rowIndex, getVisibleCentralColumn(table) ) );
	}

	public int rowAtPoint( JTable table, Point point )
	{
		int result = -1;
		if( ( table != null ) && ( point != null ) )
			result = table.rowAtPoint(point);

		return( result );
	}

	public int columnAtPoint( JTable table, Point point )
	{
		int result = -1;
		if( ( table != null ) && ( point != null ) )
			result = table.columnAtPoint(point);

		return( result );
	}

	protected Rectangle getClip( Component comp )
	{
		return( ComponentFunctions.instance().getClip(comp) );
	}

	protected Point getCenter( Rectangle rect )
	{
		return( ViewFunctions.instance().getCenter(rect) );
	}

	public int getVisibleCentralColumn( JTable table )
	{
		int result = -1;
		Rectangle rect = getClip( table );
		if( rect != null )
			result = columnAtPoint( table, getCenter( rect ) );

		return( result );
	}

	public int getVisibleCentralRow( JTable table )
	{
		int result = -1;
		Rectangle rect = getClip( table );
		if( rect != null )
			result = rowAtPoint( table, getCenter( rect ) );

		return( result );
	}

	public void makeRowBeVisible( JTable table, Integer viewRowIndex )
	{
		makeCellBeVisible( table, viewRowIndex, getVisibleCentralColumn(table) );
	}

	public void makeColumnBeVisible( JTable table, Integer viewColumnIndex )
	{
		makeCellBeVisible( table, getVisibleCentralRow(table), viewColumnIndex);
	}

	protected boolean checkValid( Integer viewIndex )
	{
		return( ( viewIndex != null ) && ( viewIndex != -1 ) );
	}

	public void makeCellBeVisible( JTable table, Integer viewRowIndex, Integer viewColumnIndex )
	{
		if( ( table != null ) && checkValid( viewRowIndex ) && checkValid( viewColumnIndex ) )
		{
			ExecutionFunctions.instance().safeSilentMethodExecution( () ->
				table.scrollRectToVisible(new Rectangle(table.getCellRect(viewRowIndex, viewColumnIndex, true)))
			);
		}
	}
}
