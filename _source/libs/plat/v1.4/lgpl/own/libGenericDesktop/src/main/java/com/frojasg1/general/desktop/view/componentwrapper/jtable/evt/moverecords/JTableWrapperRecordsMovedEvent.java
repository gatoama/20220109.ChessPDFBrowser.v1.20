/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.moverecords;

import com.frojasg1.general.collection.functions.ListRecordMoveContext;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventType;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.evt.JTableWrapperRecordsModifiedEventBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperRecordsMovedEvent<RR>
	extends JTableWrapperRecordsModifiedEventBase<RR, ListRecordMoveContext<RR>>
{
	public static final long EVENT_TYPE = JTableWrapperEventType.RECORDS_MOVED;

	public JTableWrapperRecordsMovedEvent()
	{
		super( EVENT_TYPE );
	}
}
