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
package com.frojasg1.general.desktop.view.editorkits;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import static javax.swing.text.html.HTML.Tag.I;

/**
 *
 * @author 
 * 
 * see: http://stackoverflow.com/questions/22128564/jtextpane-line-wrap
 */
public class WrapEditorKit extends StyledEditorKit
{
	ViewFactory defaultFactory=new WrapColumnFactory();
	public ViewFactory getViewFactory()
	{
		return defaultFactory;
	}

	public static class WrapColumnFactory implements ViewFactory
	{
		@Override
		public View create(Element elem)
		{
			String kind = elem.getName();
			if (kind != null)
			{
				if (kind.equals(AbstractDocument.ContentElementName))
				{
					return new WrapLabelView(elem);
				}
				else if (kind.equals(AbstractDocument.ParagraphElementName))
				{
					return new ParagraphView(elem);
				}
				else if (kind.equals(AbstractDocument.SectionElementName))
				{
					return new BoxView(elem, View.Y_AXIS);
				}
				else if (kind.equals(StyleConstants.ComponentElementName))
				{
					return new ComponentView(elem);
				}
				else if (kind.equals(StyleConstants.IconElementName))
				{
					return new IconView(elem);
				}
			}

			// default to text display
			return new LabelView(elem);
		}
	}

	public static class WrapLabelView extends LabelView 
	{
		public WrapLabelView(Element elem)
		{
			super(elem);
		}

		public float getMinimumSpan(int axis)
		{
			switch (axis)
			{
				case View.X_AXIS:
					return 0;
				case View.Y_AXIS:
					return super.getMinimumSpan(axis);
				default:
					throw new IllegalArgumentException("Invalid axis: " + axis);
			}
		}
	}
}