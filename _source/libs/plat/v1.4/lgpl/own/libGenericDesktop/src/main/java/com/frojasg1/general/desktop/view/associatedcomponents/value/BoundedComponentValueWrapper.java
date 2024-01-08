/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface BoundedComponentValueWrapper<VV, EV extends ValueChangedEvent<VV>>
	extends ComponentValueWrapper<VV, EV>,
			BoundedValueWrapper<VV> {
}
