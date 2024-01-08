/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.view.panels.volume.equalizer;

import java.util.Objects;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EqualizerBandIdentifier {
	protected String _name;
	protected Float _lowerFrequency;
	protected Float _higherFrequency;

	public String getName() {
		return _name;
	}

	public EqualizerBandIdentifier setName(String _name) {
		this._name = _name;
		return( this );
	}

	public Float getLowerFrequency() {
		return _lowerFrequency;
	}

	public EqualizerBandIdentifier setLowerFrequency(Float _lowerFrequency) {
		this._lowerFrequency = _lowerFrequency;
		return( this );
	}

	public Float getHigherFrequency() {
		return _higherFrequency;
	}

	public EqualizerBandIdentifier setHigherFrequency(Float _higherFrequency) {
		this._higherFrequency = _higherFrequency;
		return( this );
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this._name);
		hash = 89 * hash + Objects.hashCode(this._lowerFrequency);
		hash = 89 * hash + Objects.hashCode(this._higherFrequency);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EqualizerBandIdentifier other = (EqualizerBandIdentifier) obj;
		if (!Objects.equals(this._name, other._name)) {
			return false;
		}
		if (!Objects.equals(this._lowerFrequency, other._lowerFrequency)) {
			return false;
		}
		if (!Objects.equals(this._higherFrequency, other._higherFrequency)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "EqualizerBandIdentifier{" + "_name=" + _name + ", _lowerFrequency=" + _lowerFrequency + ", _higherFrequency=" + _higherFrequency + '}';
	}
}
