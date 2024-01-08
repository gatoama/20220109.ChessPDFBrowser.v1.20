/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtEventBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ValueChangedEventBase<VV>
	extends ComponentEvtEventBase
	implements ValueChangedEvent<VV>
{
	protected VV _previousValue;
	protected VV _newValue;

	@Override
	public VV getPreviousValue() {
		return _previousValue;
	}

	@Override
	public void setPreviousValue(VV _previousValue) {
		this._previousValue = _previousValue;
	}

	@Override
	public VV getNewValue() {
		return _newValue;
	}

	@Override
	public void setNewValue(VV _newValue) {
		this._newValue = _newValue;
	}
}
