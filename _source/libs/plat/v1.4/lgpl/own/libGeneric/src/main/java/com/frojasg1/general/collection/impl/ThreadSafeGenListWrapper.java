/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.collection.impl;

import com.frojasg1.general.collection.ThreadSafeListWrapperBase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ThreadSafeGenListWrapper<RR> extends ThreadSafeListWrapperBase<RR, List<RR>>
{
	public ThreadSafeGenListWrapper()
	{
		super( ArrayList::new );

		init();
	}
}
