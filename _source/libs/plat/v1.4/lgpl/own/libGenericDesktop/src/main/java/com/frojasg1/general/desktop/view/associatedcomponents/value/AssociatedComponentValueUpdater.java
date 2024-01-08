/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value;

import com.frojasg1.general.desktop.view.associatedcomponents.AssociatedComponentEvt;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface AssociatedComponentValueUpdater<VV, EV extends ValueChangedEvent<VV>,
												WW extends ComponentValueWrapper<VV, EV>>
	extends AssociatedComponentEvt<EV, WW>,
			ComponentValueWrapper<VV, EV>
{
	
}
