/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.column;

import javax.swing.table.TableColumnModel;
import com.frojasg1.general.codec.SelfStringCodec;
import com.frojasg1.general.collection.impl.ThreadSafeArrayListWrapperSelfStringCodec;
import java.util.Enumeration;
import javax.swing.table.TableColumn;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableArrangeableColumnsContext<RR>
	extends ThreadSafeArrayListWrapperSelfStringCodec<TableColumnData<RR>>
	implements SelfStringCodec
{
	public synchronized void update( TableColumnModel columnModel )
	{
		clear();
		
		Enumeration<TableColumn> columns = columnModel.getColumns();
		while( columns.hasMoreElements() )
		{
			TableColumnData tcd = getTableColumnData( columns.nextElement() );
			if( tcd != null )
				add( tcd );
		}
	}

	protected TableColumnData<RR> getTableColumnData( TableColumn tc )
	{
		TableColumnData result = null;
		if( ( tc != null ) && ( tc.getHeaderValue() instanceof TableColumnData ) )
			result = (TableColumnData) tc.getHeaderValue();

		return( result );
	}
}
