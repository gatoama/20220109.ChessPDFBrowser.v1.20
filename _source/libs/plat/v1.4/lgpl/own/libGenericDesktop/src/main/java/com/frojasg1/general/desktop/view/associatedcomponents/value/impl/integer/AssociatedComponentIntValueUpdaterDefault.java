/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl.integer;

import com.frojasg1.general.desktop.view.associatedcomponents.value.BoundedComponentValueWrapper;
import com.frojasg1.general.desktop.view.associatedcomponents.value.ValueChangedEvent;
import com.frojasg1.general.desktop.view.associatedcomponents.value.impl.AssociatedBoundedComponentValueUpdaterDefault;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class AssociatedComponentIntValueUpdaterDefault
	extends AssociatedBoundedComponentValueUpdaterDefault<Integer,
														BoundedComponentValueWrapper<Integer, ValueChangedEvent<Integer>>>
{
	public AssociatedComponentIntValueUpdaterDefault(Integer initialValue,
													Integer min,
													Integer max) {
		super(ComponentIntValueWrapperBuilder.instance()::apply,
			initialValue, min, max);
	}

}
