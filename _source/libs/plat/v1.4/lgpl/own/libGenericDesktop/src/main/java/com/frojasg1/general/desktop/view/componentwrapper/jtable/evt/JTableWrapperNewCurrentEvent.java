/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.evt;

import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventType;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JTableWrapperNewCurrentEvent extends JTableWrapperEventBase
{
	public static final long EVENT_TYPE = JTableWrapperEventType.NEW_CURRENT_ROW;

	protected int _newCurrentRow;

	public JTableWrapperNewCurrentEvent()
	{
		super( EVENT_TYPE );
	}

	public int getNewCurrentRow() {
		return _newCurrentRow;
	}

	public void setNewCurrentRow(int _newCurrentRow) {
		this._newCurrentRow = _newCurrentRow;
	}
}
