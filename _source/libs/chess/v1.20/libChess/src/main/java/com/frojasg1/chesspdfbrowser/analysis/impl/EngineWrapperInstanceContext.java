/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.chesspdfbrowser.analysis.impl;

import com.frojasg1.chesspdfbrowser.analysis.engine.EngineWrapperInstanceWrapper;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class EngineWrapperInstanceContext
{
	protected EngineWrapperInstanceWrapper _engineWrapperInstanceWrapper;
	protected boolean _isStopped = false;

	public EngineWrapperInstanceContext( EngineWrapperInstanceWrapper engineWrapperInstanceWrapper )
	{
		_engineWrapperInstanceWrapper = engineWrapperInstanceWrapper;
	}

	public boolean isStopped()
	{
		return( _isStopped );
	}

	public void setIsStopped( boolean value )
	{
		_isStopped = value;
	}

	public EngineWrapperInstanceWrapper getEngineWrapperInstanceWrapper()
	{
		return( _engineWrapperInstanceWrapper );
	}
}
