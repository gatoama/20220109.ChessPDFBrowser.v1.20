/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.associatedcomponents;

import com.frojasg1.general.desktop.view.zoom.mapper.InternallyMappedComponent;
import java.util.function.Consumer;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ComponentEvtWrapper<EV extends ComponentEvtEvent> extends InternallyMappedComponent
{
	public void addComponentEvtListener( Consumer<EV> listener );
	public void removeComponentEvtListener( Consumer<EV> listener );
	public void setEnabled( boolean value );
}
