/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents.evt.impl;

import com.frojasg1.general.desktop.view.associatedcomponents.evt.ComponentEvtWrapperSimpleDefaultBase;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class MenuItemEvtWrapper extends ComponentEvtWrapperSimpleDefaultBase<JMenuItem> {

	protected ActionListener _componentListener;

	public MenuItemEvtWrapper(JMenuItem component, Component parent) {
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
	protected void addListener()
	{
		getComponent().addActionListener( getComponentListener() );
	}

	@Override
	protected void removeListeners()
	{
		getComponent().removeActionListener( getComponentListener() );
	}
}
