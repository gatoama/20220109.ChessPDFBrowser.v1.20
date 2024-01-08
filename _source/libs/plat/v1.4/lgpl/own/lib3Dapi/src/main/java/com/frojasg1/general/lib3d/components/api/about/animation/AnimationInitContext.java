/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.lib3d.components.api.about.animation;

import java.awt.Color;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface AnimationInitContext {
	
	public Color getColor();
	public void setColor( Color color );

	public void setBrightModeColor( Color color );
	public Color getBrightModeColor();
}
