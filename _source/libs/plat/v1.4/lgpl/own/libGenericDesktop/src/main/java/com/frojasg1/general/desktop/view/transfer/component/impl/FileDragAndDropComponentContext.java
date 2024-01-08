/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.transfer.component.impl;

import com.frojasg1.general.desktop.view.transfer.component.DragAndDropComponentContextBase;
import com.frojasg1.general.desktop.view.transfer.file.FileDragAndDropEvent;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class FileDragAndDropComponentContext extends DragAndDropComponentContextBase<FileDragAndDropEvent>
{
	public FileDragAndDropComponentContext( JComponent component )
	{
		super( component );
	}

	public FileDragAndDropComponentContext init()
	{
		super.init();

		return( this );
	}
}
