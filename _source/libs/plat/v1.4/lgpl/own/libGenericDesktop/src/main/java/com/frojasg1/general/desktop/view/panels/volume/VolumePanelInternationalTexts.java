/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.volume;

import com.frojasg1.applications.common.configuration.imp.InternationalizedStringConfImp;
import com.frojasg1.general.desktop.GenericDesktopConstants;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class VolumePanelInternationalTexts extends InternationalizedStringConfImp {
	public static final String GLOBAL_CONF_FILE_NAME = "VolumePanelInternationalTexts.properties";

	public static final String CONF_MUTE_BUTTON_MUTE_HINT = "jTB_mute.HINT.Mute";
	public static final String CONF_MUTE_BUTTON_ACTIVATE_HINT = "jTB_mute.HINT.Activate";

	protected static class LazyHolder
	{
		protected static final VolumePanelInternationalTexts INSTANCE = new VolumePanelInternationalTexts();
	}

	public static VolumePanelInternationalTexts instance()
	{
		return( LazyHolder.INSTANCE );
	}

	protected VolumePanelInternationalTexts()
	{
		super( GLOBAL_CONF_FILE_NAME, GenericDesktopConstants.sa_PROPERTIES_PATH_IN_JAR );

		registerInternationalizedStrings();
	}

	protected void registerInternationalizedStrings()
	{
		registerInternationalString(CONF_MUTE_BUTTON_MUTE_HINT, "Mute" );
		registerInternationalString(CONF_MUTE_BUTTON_ACTIVATE_HINT, "Activate sound" );
	}
}
