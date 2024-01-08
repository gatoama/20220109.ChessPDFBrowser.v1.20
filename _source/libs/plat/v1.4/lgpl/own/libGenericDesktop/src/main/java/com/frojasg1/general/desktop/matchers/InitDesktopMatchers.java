/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.frojasg1.general.desktop.matchers;

import com.frojasg1.general.matchers.impl.IntegerToleranceMatcher;
import java.awt.Point;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class InitDesktopMatchers {

	public void init()
	{
		initIntegerToleranceMatchers();
	}

	protected void initIntegerToleranceMatchers()
	{
		IntegerToleranceMatcher ins = IntegerToleranceMatcher.instance();

		ins.createAndPutGenMatcher(Point.class, p -> p.x, p -> p.y );
	}
}
