/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.color;

import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public interface ImageInversor extends BiFunction<ColorInversor, BufferedImage, BufferedImage>
{
	
}
