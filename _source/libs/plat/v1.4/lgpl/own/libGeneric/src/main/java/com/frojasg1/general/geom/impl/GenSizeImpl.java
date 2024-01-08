/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.geom.impl;

import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.geom.GenSize;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class GenSizeImpl implements GenSize {
	protected int _width;
	protected int _height;

	public GenSizeImpl()
	{}

	public GenSizeImpl( int width, int height )
	{
		_width = width;
		_height = height;
	}

	public void GenSizeImpl( GenSizeImpl that )
	{
		_width = that._width;
		_height = that._height;
	}

	@Override
	public int getWidth() {
		return _width;
	}

	@Override
	public void setWidth(int _width) {
		this._width = _width;
	}

	@Override
	public int getHeight() {
		return _height;
	}

	@Override
	public void setHeight(int _height) {
		this._height = _height;
	}
}
