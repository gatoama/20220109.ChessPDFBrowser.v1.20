/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.jtable.evt;

import com.frojasg1.general.collection.functions.ListStateContextBase;
import com.frojasg1.general.desktop.view.componentwrapper.jtable.JTableWrapperEventBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 * @param <RR>	- Record of model
 * @param <CC>	- ListStateContext derived class
 */
public abstract class JTableWrapperRecordsModifiedEventBase<RR, CC extends ListStateContextBase<RR>>
	extends JTableWrapperEventBase
{
	protected CC _previous;
	protected CC _after;

	public JTableWrapperRecordsModifiedEventBase(long eventType)
	{
		super( eventType );
	}

	public CC getPrevious() {
		return _previous;
	}

	public void setPrevious(CC _previous) {
		this._previous = _previous;
	}

	public CC getAfter() {
		return _after;
	}

	public void setAfter(CC _after) {
		this._after = _after;
	}
}
