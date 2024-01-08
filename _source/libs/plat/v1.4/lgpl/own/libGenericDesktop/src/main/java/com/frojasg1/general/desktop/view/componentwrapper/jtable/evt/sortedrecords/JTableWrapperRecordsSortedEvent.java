/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.sortedrecords;

import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventType;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperRecordsModifiedEventBase;
import com.frojasg1.general.collection.functions.ListStateContextBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperRecordsSortedEvent<RR> extends JTableWrapperRecordsModifiedEventBase<RR, ListStateContextBase<RR>>
{
	public static final long EVENT_TYPE = JTableWrapperEventType.RECORDS_SORTED;

	public JTableWrapperRecordsSortedEvent()
	{
		super( EVENT_TYPE );
	}
}
