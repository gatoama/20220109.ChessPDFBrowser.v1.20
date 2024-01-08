/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

import java.awt.Component;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface AssociatedComponentEvt<EV extends ComponentEvtEvent, WW extends ComponentEvtWrapper<EV>>
	extends ComponentEvtWrapper<EV>
{
	public void addComponentToWrap( Component component );
	public void addComponentToWrap( Component component, Component parent );
	public void addComponentEvtWrapper( WW wrapper );

	public void setEnabled( boolean value );
}
