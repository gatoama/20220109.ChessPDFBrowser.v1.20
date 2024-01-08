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
public class JTableWrapperSelectionHasChangedEvent extends JTableWrapperEventBase
{
	public static final long EVENT_TYPE = JTableWrapperEventType.SELECTION_HAS_CHANGED;

	protected int[] _newWholeModelSelection;
	protected int[] _newVisibleModelSelection;


	public JTableWrapperSelectionHasChangedEvent()
	{
		super( EVENT_TYPE );
	}

	public int[] getNewWholeModelSelection() {
		return _newWholeModelSelection;
	}

	public void setNewWholeModelSelection(int[] _newWholeModelSelection) {
		this._newWholeModelSelection = _newWholeModelSelection;
	}

	public int[] getNewVisibleModelSelection() {
		return _newVisibleModelSelection;
	}

	public void setNewVisibleModelSelection(int[] _newVisibleModelSelection) {
		this._newVisibleModelSelection = _newVisibleModelSelection;
	}
}
