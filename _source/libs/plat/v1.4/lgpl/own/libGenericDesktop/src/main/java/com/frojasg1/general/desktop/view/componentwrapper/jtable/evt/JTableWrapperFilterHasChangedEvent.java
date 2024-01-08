/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.evt;

import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventType;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperFilterHasChangedEvent<RR> extends JTableWrapperEventBase
{
	public static final long EVENT_TYPE = JTableWrapperEventType.FILTER_HAS_CHANGED;

	protected List<RR> _newFilteredList;

	public JTableWrapperFilterHasChangedEvent( Class<RR> recordClass )
	{
		super( EVENT_TYPE );
	}

	public List<RR> getNewFilteredList() {
		return _newFilteredList;
	}

	public void setNewFilteredList(List<RR> _newFilteredList) {
		this._newFilteredList = _newFilteredList;
	}
}
