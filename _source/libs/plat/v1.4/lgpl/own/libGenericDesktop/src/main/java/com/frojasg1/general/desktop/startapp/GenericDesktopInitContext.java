/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.startapp;

import com.frojasg1.general.context.ApplicationContext;
import com.frojasg1.general.desktop.view.color.ColorInversor;
import com.frojasg1.general.lib3d.components.api.about.animation.AnimationForAboutFactory;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface GenericDesktopInitContext extends ApplicationContext {
	public void setApplicationName( String value );
	public String getApplicationName();
	public void setAnimationForAboutFactory( AnimationForAboutFactory animationFactory );
	public AnimationForAboutFactory getAnimationForAboutFactory();
	public void setColorInversor(ColorInversor ci);
	public ColorInversor getColorInversor();
}
