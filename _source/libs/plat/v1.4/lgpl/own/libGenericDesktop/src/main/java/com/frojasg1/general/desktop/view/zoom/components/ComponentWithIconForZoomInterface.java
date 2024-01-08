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
package com.frojasg1.general.desktop.view.zoom.components;

import com.frojasg1.general.desktop.view.zoom.ZoomComponentInterface;
import com.frojasg1.general.desktop.view.zoom.mapper.CustomComponent;
import javax.swing.Icon;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ComponentWithIconForZoomInterface extends CustomComponent
{
	public Icon getDisabledIcon();
	public Icon getDisabledSelectedIcon();
	public Icon getIcon();
	public Icon getPressedIcon();
	public Icon getRolloverIcon();
	public Icon getRolloverSelectedIcon();
	public Icon getSelectedIcon();

	public Icon superGetDisabledIcon();
	public Icon superGetDisabledSelectedIcon();
	public Icon superGetIcon();
	public Icon superGetPressedIcon();
	public Icon superGetRolloverIcon();
	public Icon superGetRolloverSelectedIcon();
	public Icon superGetSelectedIcon();
}
