/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

import com.frojasg1.general.desktop.view.associatedcomponents.ComponentEvtEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ValueChangedEvent<VV> extends ComponentEvtEvent {

	public VV getPreviousValue();
	public void setPreviousValue(VV _previousValue);
	public VV getNewValue();
	public void setNewValue(VV newValue);
}
