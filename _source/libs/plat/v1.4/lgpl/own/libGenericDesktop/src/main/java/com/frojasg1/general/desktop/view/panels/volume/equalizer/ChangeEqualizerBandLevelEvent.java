/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.volume.equalizer;

import com.frojasg1.general.desktop.view.panels.volume.ChangeVolumeEvent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChangeEqualizerBandLevelEvent {
	protected EqualizerBandIdentifier _eqBandIdentifier;
	protected ChangeVolumeEvent _volumeEvent;

	public EqualizerBandIdentifier getEqBandIdentifier() {
		return _eqBandIdentifier;
	}

	public ChangeEqualizerBandLevelEvent setEqBandIdentifier(EqualizerBandIdentifier _eqBandIdentifier) {
		this._eqBandIdentifier = _eqBandIdentifier;
		return( this );
	}

	public ChangeVolumeEvent getVolumeEvent() {
		return _volumeEvent;
	}

	public ChangeEqualizerBandLevelEvent setVolumeEvent(ChangeVolumeEvent _volumeEvent) {
		this._volumeEvent = _volumeEvent;
		return( this );
	}
}
