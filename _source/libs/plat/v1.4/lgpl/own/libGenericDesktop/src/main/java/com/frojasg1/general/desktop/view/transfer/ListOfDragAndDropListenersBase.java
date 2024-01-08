/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer;

import com.frojasg1.general.listeners.filterevents.ListOfDefaultEventContextListenersBase;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfDragAndDropListenersBase<EV extends DragAndDropEvent>
	extends ListOfDefaultEventContextListenersBase<EV, DragAndDropEventType>
{
	public ListOfDragAndDropListenersBase() {
		super(DragAndDropEventType::getLongValue, DragAndDropEvent::getType);
	}
}
