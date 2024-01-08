/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer;

import java.awt.Component;
import java.awt.Point;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DragAndDropEvent {
	protected DragAndDropEventType _type;
	protected Point _dropLocation;
	protected Component _source;

	public DragAndDropEvent( DragAndDropEventType type, Component source, Point dropLocation )
	{
		_type = type;
		_source = source;
		_dropLocation = dropLocation;
	}

	public Point getDropLocation() {
		return _dropLocation;
	}

	public Component getSource() {
		return _source;
	}

	public DragAndDropEventType getType() {
		return _type;
	}
}
