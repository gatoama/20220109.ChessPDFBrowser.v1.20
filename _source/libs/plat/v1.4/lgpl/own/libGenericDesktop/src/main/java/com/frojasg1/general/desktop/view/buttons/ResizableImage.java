/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.buttons;

import java.awt.image.BufferedImage;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ResizableImage {
	public void resizeImage();
	public void setImage( BufferedImage image );
	public void setImageResource( String imageResource );
}
