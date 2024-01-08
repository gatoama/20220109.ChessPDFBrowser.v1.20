/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.volume;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChangeVolumeEvent {
	protected int _volumeIndB;
	protected boolean _isMuted;

	public float getFactor()
	{
		float result = 1.0f;

		if( isIsMuted() )
			result = 0.0f;
		else
			result = (float) Math.pow( 10, getVolumeIndB() / 20f );

		return( result );
	}

	public int getEffectiveVolumeInDb()
	{
		return( _isMuted ? 0 : _volumeIndB );
	}

	public int getVolumeIndB() {
		return _volumeIndB;
	}

	public ChangeVolumeEvent setVolumeIndB(int _volumeIndB) {
		this._volumeIndB = _volumeIndB;
		return( this );
	}

	public boolean isIsMuted() {
		return _isMuted;
	}

	public ChangeVolumeEvent setIsMuted(boolean _isMuted) {
		this._isMuted = _isMuted;
		return( this );
	}
}
