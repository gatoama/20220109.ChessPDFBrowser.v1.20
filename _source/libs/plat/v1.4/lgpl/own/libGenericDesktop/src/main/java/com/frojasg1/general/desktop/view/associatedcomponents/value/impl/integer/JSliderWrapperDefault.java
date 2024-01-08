/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl.integer;

import com.frojasg1.general.desktop.view.associatedcomponents.value.BoundedComponentValueWrapperSimpleDefault;
import java.awt.Component;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JSliderWrapperDefault extends BoundedComponentValueWrapperSimpleDefault<JSlider, Integer>
{

	protected ChangeListener _componentListener;

	public JSliderWrapperDefault(JSlider component, Component parent ) {
		super(component, parent);
	}

	@Override
	protected void initChild()
	{
		_componentListener = createChangeListener();

		super.initChild();
	}

	protected ChangeListener createChangeListener()
	{
		return( evt -> internalValueChangedListener() );
	}

	public ChangeListener getComponentListener() {
		return _componentListener;
	}

	@Override
	protected void addListener() {
		getComponent().addChangeListener( getComponentListener() );
	}

	@Override
	protected void removeListeners() {
		getComponent().removeChangeListener( getComponentListener() );
	}

	@Override
	public Integer getValue() {
		return( getComponent().getValue() );
	}

	@Override
	public void setValue(Integer value) {
		getComponent().setValue(value);
	}

	@Override
	public void setBounds(Integer min, Integer max) {
		getComponent().setMinimum(min);
		getComponent().setMaximum(max);
	}

	@Override
	public Integer getMin() {
		return( getComponent().getMinimum() );
	}

	@Override
	public Integer getMax() {
		return( getComponent().getMaximum() );
	}
}
