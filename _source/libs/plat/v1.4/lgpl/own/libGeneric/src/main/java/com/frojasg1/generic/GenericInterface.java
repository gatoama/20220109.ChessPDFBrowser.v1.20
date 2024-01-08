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
package com.frojasg1.generic;

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.dialogs.DialogsWrapper;
import com.frojasg1.general.files.GenericFileFacilities;
import com.frojasg1.general.keyboard.GenericKeyboard;
import com.frojasg1.general.view.ViewFacilities;
import com.frojasg1.generic.application.ApplicationFacilitiesInterface;
import com.frojasg1.generic.languages.ObtainAvailableLanguages_int;
import com.frojasg1.generic.system.SystemInterface;
import com.frojasg1.generic.zoom.ZoomFactorsAvailable;

/**
 *
 * @author Usuario
 */
public interface GenericInterface
{
	public ObtainAvailableLanguages_int getObtainAvailableLanguages();
	public BaseApplicationConfigurationInterface getAppliConf();
	public DialogsWrapper getDialogsWrapper();
	public GenericFileFacilities getFileFacilities();
	public GenericKeyboard getKeyboardFacilities();
	public ZoomFactorsAvailable getZoomFactorsAvailable();
	public SystemInterface getSystem();
	public ApplicationFacilitiesInterface getApplicationFacilities();

	public ViewFacilities getViewFacilities();
}
