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
package com.frojasg1.general.desktop.image.processing;

import java.util.function.DoubleBinaryOperator;

import hageldave.imagingkit.core.scientific.ColorImg;
import java.awt.image.BufferedImage;

/**
 *
 * https://gist.github.com/hageldave/0bf84b6e1bad76cbf3f764c3a29be9d5#file-radontransform-java
 * 
 * @Author: Hagel Dave
 */
public class RadonTransform {

	public BufferedImage process( BufferedImage image ) {
		//		ColorImg img = new ColorImg(300, 300, false);
		//		img.paint(g2d -> {
		//			g2d.setColor(Color.WHITE);
		//			g2d.fillRect(40, 20, 100, 170);
		//		});
//		Img loadedImg = ImageLoader.loadImgFromURL("https://upload.wikimedia.org/wikipedia/commons/5/5e/Lobster.jpg");

//		ColorImg img = new ColorImg(loadedImg, false);
		ColorImg img = new ColorImg(image);
//		ImageFrame.display(img.getRemoteBufferedImage());
		DoubleBinaryOperator samplerr = new DoubleBinaryOperator() {
			@Override
			public double applyAsDouble(double x, double y) {
				x = (x+1.0)/2.0;
				y = (y+1.0)/2.0;
				if(x < 0 || x > 1 || y < 0 || y > 1){
					return 0;
				}
				return img.interpolate(ColorImg.channel_r, x, y);
			}
		};
		DoubleBinaryOperator samplerg = new DoubleBinaryOperator() {
			@Override
			public double applyAsDouble(double x, double y) {
				x = (x+1.0)/2.0;
				y = (y+1.0)/2.0;
				if(x < 0 || x > 1 || y < 0 || y > 1){
					return 0;
				}
				return img.interpolate(ColorImg.channel_g, x, y);
			}
		};
		DoubleBinaryOperator samplerb = new DoubleBinaryOperator() {
			@Override
			public double applyAsDouble(double x, double y) {
				x = (x+1.0)/2.0;
				y = (y+1.0)/2.0;
				if(x < 0 || x > 1 || y < 0 || y > 1){
					return 0;
				}
				return img.interpolate(ColorImg.channel_b, x, y);
			}
		};

		ColorImg target = new ColorImg(image.getWidth(),image.getHeight(),false);
		transform(samplerr, samplerg, samplerb, target);
//		ImageFrame.display(target.getRemoteBufferedImage());
//		ImageSaver.saveImage(target.toBufferedImage(), "lobster_radon.png");

		return( target.toBufferedImage() );
	}

	public void transform(
			DoubleBinaryOperator sampler2dr, 
			DoubleBinaryOperator sampler2dg, 
			DoubleBinaryOperator sampler2db, 
			ColorImg targetImg)
	{
		targetImg.forEach(true, pixel -> {
			// alpha in 0..2pi
			double alpha = pixel.getY() * Math.PI * 2.0 / targetImg.getHeight();
			// s in -1..1
			double s = pixel.getX() * 2.0/targetImg.getWidth() -1.0;
			s *= 1.5;
			double sumr=0, sumg=0, sumb=0;
			int integration_steps = 500;
			for(int z_i = 0; z_i < integration_steps; z_i++){
				// z in -1..1
				double z = z_i * 2.0 / integration_steps -1;
				double x =  z*Math.sin(alpha) + s * Math.cos(alpha);
				double y = -z*Math.cos(alpha) + s * Math.sin(alpha);
				sumr += sampler2dr.applyAsDouble(x, y);
				sumg += sampler2dg.applyAsDouble(x, y);
				sumb += sampler2db.applyAsDouble(x, y);
			}
			sumr /= integration_steps;
			sumg /= integration_steps;
			sumb /= integration_steps;
			pixel.setRGB_fromDouble(sumr, sumg, sumb);
		});
		targetImg.forEach(px -> {
			double r = clamp((px.r_asDouble()-0.5)* 1.5 + 0.5, 0, 1);
			double g = clamp((px.g_asDouble()-0.5)* 1.5 + 0.5, 0, 1);
			double b = clamp((px.b_asDouble()-0.5)* 1.5 + 0.5, 0, 1);
			px.setRGB_fromDouble(r, g, b);
		});
	}




	static double clamp(double v, double lower, double upper) {
		return Math.max(lower, Math.min(upper, v));
	}


}