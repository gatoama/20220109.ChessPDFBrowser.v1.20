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
package com.frojasg1.libpdf.view.api;

import com.frojasg1.general.desktop.view.panels.NavigatorControllerInterface;
import com.frojasg1.general.number.DoubleReference;
import com.frojasg1.libpdf.view.PdfViewerContext;
import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface PdfViewerMaster extends NavigatorControllerInterface
{
	public void newPageSelected( int newPageIndex );
	public void newPdfZoomFactorSelected( DoubleReference newFactor );

	public void newPageSet( BufferedImage image, DoubleReference factor, int pageIndex );

	public PdfViewerContext getPdfViewerContext();
}
