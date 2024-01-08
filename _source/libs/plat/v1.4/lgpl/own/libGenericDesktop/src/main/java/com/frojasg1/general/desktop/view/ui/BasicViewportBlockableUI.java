/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.ui;

import com.frojasg1.general.view.Blockable;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicViewportUI;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class BasicViewportBlockableUI extends BasicViewportUI implements Blockable
{
	protected BlockablePainter _blockablePainter;

	public BasicViewportBlockableUI()
	{
		super();
	}

	public BasicViewportBlockableUI init()
	{
		_blockablePainter = new BlockablePainter( this::superPaint );

		return( this );
	}

	@Override
	public void paint( Graphics grp, JComponent jComponent )
	{
		_blockablePainter.paint( grp, jComponent );
	}

	protected void superPaint( Graphics grp, JComponent jComponent )
	{
		super.paint( grp, jComponent );
	}

	@Override
	public void block()
	{
		_blockablePainter.block();
	}

	@Override
	public void unblock()
	{
		_blockablePainter.unblock();
	}

	@Override
	public void setBlocked(boolean value)
	{
		_blockablePainter.setBlocked( value );
	}

	@Override
	public boolean isBlocked() {
		return( _blockablePainter.isBlocked() );
	}
}
