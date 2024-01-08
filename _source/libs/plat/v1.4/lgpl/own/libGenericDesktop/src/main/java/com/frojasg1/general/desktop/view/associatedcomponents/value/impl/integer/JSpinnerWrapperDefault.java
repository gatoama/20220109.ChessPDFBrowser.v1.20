/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl.integer;

import com.frojasg1.general.desktop.view.associatedcomponents.value.BoundedComponentValueWrapperSimpleDefault;
import java.awt.Component;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JSpinnerWrapperDefault extends BoundedComponentValueWrapperSimpleDefault<JSpinner, Integer>
{
	protected ChangeListener _componentListener;

	protected Integer _defaultValue;

	public JSpinnerWrapperDefault(JSpinner component, Component parent ) {
		super(component, parent);
	}

	@Override
	protected void initChild()
	{
		_componentListener = createChangeListener();

		super.initChild();
	}

	public Integer getDefaultValue() {
		return _defaultValue;
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
		return( (Integer) getComponent().getValue() );
	}

	@Override
	public void setValue(Integer value) {
		_defaultValue = value;
		getComponent().setValue(value);
	}

	@Override
	public void setBounds(Integer min, Integer max) {
		SpinnerModel sm = new SpinnerNumberModel((Number) getDefaultValue(),
												min,
												max, 1); //default value,lower bound,upper bound,increment by
		getComponent().setModel(sm);
	}

	protected SpinnerNumberModel getNumberModel()
	{
		SpinnerNumberModel result = null;
		if( getComponent().getModel() instanceof SpinnerNumberModel )
			result = (SpinnerNumberModel) getComponent().getModel();

		return( result );
	}

	@Override
	public Integer getMin() {
		return( getIfNotNull( getNumberModel(), spinModel -> (Integer) spinModel.getMinimum() ) );
	}

	@Override
	public Integer getMax() {
		return( getIfNotNull( getNumberModel(), spinModel -> (Integer) spinModel.getMaximum() ) );
	}
}
