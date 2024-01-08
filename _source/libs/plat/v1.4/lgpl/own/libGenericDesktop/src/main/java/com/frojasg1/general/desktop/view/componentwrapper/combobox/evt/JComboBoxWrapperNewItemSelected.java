/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.combobox.evt;

import com.frojasg1.general.desktop.view.componentwrapper.combobox.JComboBoxWrapperEventBase;
import com.frojasg1.general.desktop.view.componentwrapper.combobox.JComboBoxWrapperEventType;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JComboBoxWrapperNewItemSelected<RR> extends JComboBoxWrapperEventBase<RR>
{
	public static final long EVENT_TYPE = JComboBoxWrapperEventType.NEW_ITEM_SELECTED;

	protected RR _newItemSelected;

	public JComboBoxWrapperNewItemSelected()
	{
		super( EVENT_TYPE );
	}

	public RR getNewItemSelected() {
		return _newItemSelected;
	}

	public void setNewItemSelected(RR value) {
		_newItemSelected = value;
	}
}
