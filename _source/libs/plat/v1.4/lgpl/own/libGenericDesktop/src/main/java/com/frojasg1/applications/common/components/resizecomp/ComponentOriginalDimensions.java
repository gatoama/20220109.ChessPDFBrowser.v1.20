/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.applications.common.components.resizecomp;

import com.frojasg1.general.desktop.view.ViewFunctions;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.metal.MetalScrollButton;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ComponentOriginalDimensions
{
	public static final Point ORIGIN = new Point( 0, 0 );

	protected Double _originalFontSize = null;
	protected Double _originalFontSizeOfTitledBorder = null;

	protected Dimension _originalMinimumSize = null;
	protected Dimension _originalMaximumSize = null;
	protected Dimension _originalPreferredSize = null;
	protected Rectangle _originalBounds = null;

	protected Insets _originalInsets = null;

	protected Component _component = null;

	public ComponentOriginalDimensions( Component comp )
	{
		this( comp, 1.0D );
	}

	public ComponentOriginalDimensions( Component comp, double zoomFactor )
	{
		_component = comp;
		if( ( _component instanceof JRadioButtonMenuItem ) &&
				( ( JRadioButtonMenuItem ) _component ).getText().equals( "75%" )
			)
		{
			int kk=0;
		}

		fillInAttributes_final( comp, zoomFactor );
	}

	protected Rectangle getOriginalBounds( Component comp )
	{
		Rectangle result = null;
		if( comp instanceof MetalScrollButton )
		{
			Dimension ps = comp.getPreferredSize();
			if( ps != null )
				result = new Rectangle( 0, 0, ps.width, ps.height );
		}

		if( result == null )
		{
			result = comp.getBounds();
		}

		return( result );
	}

	protected Dimension unzoom( Dimension dimen, double zoomFactor )
	{
		return( ViewFunctions.instance().getNewDimension( dimen, 1/zoomFactor ) );
	}

	protected Rectangle unzoom( Rectangle bounds, double zoomFactor )
	{
		Rectangle result = null;
		if( ( _component instanceof Frame ) ||
			( _component instanceof Dialog ) )
		{
			result = ViewFunctions.instance().calculateNewBoundsOnScreen( bounds, null, ORIGIN,
																		1/zoomFactor );
		}
		else
		{
			result = ViewFunctions.instance().calculateNewBounds( bounds, null, ORIGIN,
																		1/zoomFactor );
		}

		return( result );
	}

	protected final void fillInAttributes_final( Component comp, double zoomFactor )
	{
		if( comp != null )
		{
			Font font = comp.getFont();
			if( font != null )
				_originalFontSize = (double) font.getSize();

			_originalBounds = unzoom( getOriginalBounds( comp ), zoomFactor );
/*
			if( ( comp instanceof JLabel ) ||
				( comp instanceof JButton ) ||
				( comp instanceof JRadioButton ) ||
				( comp instanceof JCheckBox ) ||
				( comp instanceof JComboBox ) )
			{
				Dimension originalSize = unzoom( new Dimension( (int) _originalBounds.getWidth(),
														(int) _originalBounds.getHeight() ),
												zoomFactor );
				_originalMinimumSize = originalSize;
				_originalMaximumSize = originalSize;
				_originalPreferredSize = originalSize;
			}
			else
*/
			{
				_originalMinimumSize = comp.getMinimumSize();
				_originalMaximumSize = comp.getMaximumSize();
				_originalPreferredSize = comp.getPreferredSize();
			}

			if( comp instanceof JComponent )
			{
				JComponent jcomp = (JComponent) comp;
				if( jcomp.getBorder() instanceof TitledBorder )
				{
					TitledBorder tb = (TitledBorder) jcomp.getBorder();
					Font tbFont = tb.getTitleFont();
					if( tbFont != null )
						_originalFontSizeOfTitledBorder = (double) tbFont.getSize();
				}

				Border border = null;
				
				_originalInsets = jcomp.getInsets();
			}
		}
	}

	public Double getOriginalFontSizeOfTitledBorder()
	{
		return( _originalFontSizeOfTitledBorder );
	}

	public Double getOriginalFontSize()
	{
		return( _originalFontSize );
	}

	public Dimension getOriginalMinimumSize()
	{
		return( _originalMinimumSize );
	}

	public Dimension getOriginalMaximumSize()
	{
		return( _originalMaximumSize );
	}

	public Dimension getOriginalPreferredSize()
	{
		return( _originalPreferredSize );
	}

	public Rectangle getOriginalBounds()
	{
		return( _originalBounds );
	}

	public Insets getOriginalInsets()
	{
		return( _originalInsets );
	}
}
