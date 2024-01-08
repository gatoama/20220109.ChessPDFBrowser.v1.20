/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.lib3d.components.api.about.animation.torus;

import com.frojasg1.general.lib3d.components.api.about.animation.AnimationInitContext;
import java.awt.Color;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TorusAnimationInitContext implements AnimationInitContext
{
	protected float _majorRadius;
	protected float _minorRadius;
	protected int _majorSamples;
	protected int _minorSamples;
	protected Color _color;
	protected Color _originalColor;

	public float getMajorRadius() {
		return _majorRadius;
	}

	public void setMajorRadius(float _majorRadius) {
		this._majorRadius = _majorRadius;
	}

	public float getMinorRadius() {
		return _minorRadius;
	}

	public void setMinorRadius(float _minorRadius) {
		this._minorRadius = _minorRadius;
	}

	public int getMajorSamples() {
		return _majorSamples;
	}

	public void setMajorSamples(int _majorSamples) {
		this._majorSamples = _majorSamples;
	}

	public int getMinorSamples() {
		return _minorSamples;
	}

	public void setMinorSamples(int _minorSamples) {
		this._minorSamples = _minorSamples;
	}

	@Override
	public Color getColor() {
		return _color;
	}

	@Override
	public void setColor(Color _color) {
		this._color = _color;
	}


	public void setBrightModeColor( Color color )
	{
		_originalColor = color;
	}

	public Color getBrightModeColor()
	{
		return( _originalColor );
	}
}
