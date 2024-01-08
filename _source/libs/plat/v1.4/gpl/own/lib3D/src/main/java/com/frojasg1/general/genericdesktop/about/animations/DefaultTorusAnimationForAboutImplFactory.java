/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.genericdesktop.about.animations;

import com.frojasg1.general.lib3d.components.api.about.animation.AnimationForAbout;
import com.frojasg1.general.lib3d.components.api.about.animation.AnimationForAboutFactory;
import com.frojasg1.general.lib3d.components.api.about.animation.torus.TorusAnimationInitContext;
import java.awt.Color;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class DefaultTorusAnimationForAboutImplFactory implements AnimationForAboutFactory<TorusAnimationInitContext> {

	protected static final int EDGE_COLORS_INDEX = 0;

	protected Color[] _brightModeColors = { new Color( 1, 1, 1 ) };

	protected Color[] _currentColors = _brightModeColors;

	protected TorusAnimationInitContext createInitContext()
	{
		TorusAnimationInitContext result = new TorusAnimationInitContext();

		float majorRadius = 0.5f;
		float minorRadius = 0.15f;
		int majorSamples = 20;
		int minorSamples = 5;
		Color color = getColor(EDGE_COLORS_INDEX);

		result.setMajorRadius(majorRadius);
		result.setMinorRadius(minorRadius);
		result.setMajorSamples(majorSamples);
		result.setMinorSamples(minorSamples);
		result.setColor(color);
		result.setBrightModeColor(color);

		return( result );
	}

	@Override
	public AnimationForAbout<TorusAnimationInitContext> createAnimationForAbout() {
		TorusAnimationForAboutImpl result = new TorusAnimationForAboutImpl();
		result.init( createInitContext() );
		return( result );
	}

	@Override
	public Color[] getBrightModeColors() {
		return( _brightModeColors );
	}

	@Override
	public void setColors(Color[] colors) {
		_currentColors = colors;
	}

	protected Color getColor( int index )
	{
		return( _currentColors[index] );
	}
}
