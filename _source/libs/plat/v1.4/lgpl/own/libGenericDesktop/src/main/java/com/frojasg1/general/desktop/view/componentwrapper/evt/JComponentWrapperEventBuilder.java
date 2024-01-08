/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.componentwrapper.evt;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface JComponentWrapperEventBuilder<EV extends JComponentWrapperEvent> {

	public EV buildEvent( long eventType );
	public <EVT extends EV> EVT buildEvent( Class<EVT> clazz );
}
