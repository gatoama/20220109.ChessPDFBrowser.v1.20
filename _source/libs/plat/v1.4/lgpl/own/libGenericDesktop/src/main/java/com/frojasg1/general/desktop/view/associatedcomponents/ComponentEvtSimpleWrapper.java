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
public interface ComponentEvtSimpleWrapper<CC extends Component, EV extends ComponentEvtEvent>
	extends ComponentEvtWrapper<EV>
{
	public CC getComponent();
}
