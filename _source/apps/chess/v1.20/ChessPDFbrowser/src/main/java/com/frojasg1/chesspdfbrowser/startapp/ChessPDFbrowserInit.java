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
package com.frojasg1.chesspdfbrowser.startapp;

import com.frojasg1.chesspdfbrowser.application.language.ChessPDFbrowserApplicationLanguageResources;
import com.frojasg1.chesspdfbrowser.engine.configuration.ApplicationConfiguration;
import com.frojasg1.chesspdfbrowser.engine.init.LibChessEngineInit;
import com.frojasg1.chesspdfbrowser.enginewrapper.init.InitLibChessEngineWrapper;
import com.frojasg1.chesspdfbrowser.recognizer.init.InitLibChessBoardRecognition;
import com.frojasg1.chesspdfbrowser.view.chess.images.ChessBoardImages;
import com.frojasg1.chesspdfbrowser.view.chess.init.LibChessViewInit;
import com.frojasg1.general.desktop.init.InitGenericDesktop;
import com.frojasg1.libpdf.init.InitLibPdf;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessPDFbrowserInit
{
	public static void init()
	{
		InitLibPdf.init();
		InitGenericDesktop.init( ApplicationConfiguration.instance() );
		InitLibChessEngineWrapper.init();
		InitLibChessBoardRecognition.init();
		LibChessEngineInit.init();
		LibChessViewInit.init();
		ChessBoardImages.instance().registerToFigureSetChangedObserved( ApplicationConfiguration.instance() );

		ChessPDFbrowserApplicationLanguageResources.instance();
	}
}
