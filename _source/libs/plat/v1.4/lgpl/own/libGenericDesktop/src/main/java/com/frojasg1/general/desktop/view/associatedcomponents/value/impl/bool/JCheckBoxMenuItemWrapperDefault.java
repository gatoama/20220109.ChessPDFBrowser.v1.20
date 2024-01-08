/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.value.impl.bool;

import com.frojasg1.general.desktop.view.associatedcomponents.value.ComponentValueWrapperSimpleDefault;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class JCheckBoxMenuItemWrapperDefault extends ComponentValueWrapperSimpleDefault<JCheckBoxMenuItem, Boolean>
{

	protected ActionListener _componentListener;

	public JCheckBoxMenuItemWrapperDefault(JCheckBoxMenuItem component, Component parent ) {
		super(component, parent);
	}

	@Override
	protected void initChild()
	{
		_componentListener = createComponentListener();

		super.initChild();
	}

	protected ActionListener createComponentListener()
	{
		return( evt -> internalValueChangedListener() );
	}

	public ActionListener getComponentListener() {
		return _componentListener;
	}

	@Override
	protected void addListener() {
		getComponent().addActionListener( getComponentListener() );
	}

	@Override
	protected void removeListeners() {
		getComponent().removeActionListener( getComponentListener() );
	}

	@Override
	public Boolean getValue() {
		return( getComponent().isSelected() );
	}

	@Override
	public void setValue(Boolean value) {
		getComponent().setSelected(value);
	}
}
