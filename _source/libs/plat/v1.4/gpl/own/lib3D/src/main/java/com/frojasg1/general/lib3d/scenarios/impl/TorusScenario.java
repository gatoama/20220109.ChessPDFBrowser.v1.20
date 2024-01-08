/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 */
package com.frojasg1.general.lib3d.scenarios.impl;

import com.frojasg1.general.lib3d.components.api.about.animation.torus.TorusAnimationInitContext;
import com.frojasg1.general.lib3d.figures.Torus;
import com.frojasg1.general.lib3d.scenarios.Scenario3dBase;
import com.frojasg1.general.lib3d.scenarios.api.Scenario3dApi;
import java.awt.Color;
import org.jogamp.java3d.AmbientLight;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3f;
import org.jogamp.java3d.Shape3D;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class TorusScenario extends Scenario3dBase implements Scenario3dApi
{

	protected TransformGroup _objRot = null;
	protected TransformGroup _objTrans = null;

	protected BranchGroup _root = null;

	protected Torus _solidTorus = null;
	protected BranchGroup _bgOfSolidTorus = null;
	protected Torus _linesTorus = null;
	protected BranchGroup _bgOfLinesTorus = null;

	protected boolean _isSolid = false;

	protected Torus createTorus(TorusAnimationInitContext initContext, boolean isSolid)
	{
		Torus result = new Torus( initContext, isSolid );
		result.setCapability(BranchGroup.ALLOW_DETACH);

		return( result );
	}

	protected void createToruses(TorusAnimationInitContext initContext)
	{
		boolean isSolid = true;
		_solidTorus = this.createTorus( initContext, isSolid );
		_bgOfSolidTorus = createBranchGroup(_solidTorus );

		isSolid = false;
		_linesTorus = this.createTorus( initContext, isSolid );
		_bgOfLinesTorus = createBranchGroup(_linesTorus );
	}

	protected BranchGroup createBranchGroup( Shape3D shape )
	{
		BranchGroup result = new BranchGroup();
		result.setCapability(BranchGroup.ALLOW_DETACH);
		result.addChild( shape );

		return( result );
	}

	@Override
	protected void setInitialPosition()
	{
		doRotation(0,0);
	}

	public void init(TorusAnimationInitContext initContext)
	{
		createToruses(initContext);
		setCanvas3D( isSolid() );

		setInitialPosition();
	}

	protected TorusAnimationInitContext createDefaultInitContext()
	{
		float majorRadius = 0.5f;
		float minorRadius = 0.15f;
		int majorSamples = 20;
		int minorSamples = 20;
		Color color = Color.green;

		TorusAnimationInitContext result = new TorusAnimationInitContext();
		result.setMajorRadius(majorRadius);
		result.setMinorRadius(minorRadius);
		result.setMajorSamples(majorSamples);
		result.setMinorSamples(minorSamples);
		result.setColor(color);

		return( result );
	}

	public void init()
	{
		init( createDefaultInitContext() );
	}

	@Override
	public void setIsSolid( boolean value )
	{
		_isSolid = value;
	}

	@Override
	public boolean isSolid()
	{
		return( _isSolid );
	}

	@Override
	public void updateCanvas3D()
	{
		setTorus(_isSolid);
	}

	@Override
	public void doRotation( float fi, float zeta )
	{
        _objRot.setTransform( get3dRotationTransform( calculateNormalVector(fi, zeta) ) );
	}

	protected BranchGroup getRootGroup()
	{
		if( _root == null )
			_root = new BranchGroup();

		return( _root );
	}

	protected BranchGroup getTorusBranchGroup( boolean isSolid )
	{
		BranchGroup result = null;
		if( isSolid )
			result = _bgOfSolidTorus;
		else
			result = _bgOfLinesTorus;

		return( result );
	}

	protected void setTorus( boolean isSolid )
	{
		if( ( _objRot != null ) && ( _objRot.getAllChildren().hasMoreElements() ) )
		{
			_objRot.removeChild(0);
		}

		BranchGroup torusBg = getTorusBranchGroup(isSolid);

		_objRot.addChild(torusBg);
	}

	protected void setCanvas3D( boolean isSolid )
	{
        Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		setCanvas3D( canvas );
        SimpleUniverse universe = setSimpleUniverse();

		_root = getRootGroup();

		_objRot = new TransformGroup();
		_objRot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		_objRot.setCapability( TransformGroup.ALLOW_CHILDREN_WRITE );
		_objRot.setCapability( TransformGroup.ALLOW_CHILDREN_EXTEND );

		setTorus(isSolid);

		_objTrans = new TransformGroup();
		_objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		_objTrans.addChild(_objRot);

//		root.addChild(new ColorCube());
		_root.addChild(_objTrans);

		// Create a red light that shines for 100m from the origin

//		Color3f light1Color = new Color3f(1.8f, 0.1f, 0.1f);
		Color3f light1Color = new Color3f(10f, 10f, 10f);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);
		Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
		DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
		light1.setInfluencingBounds(bounds);

		_root.addChild(light1);

		Color3f ambientColor = new Color3f(1.0f, 1.0f, 1.0f);

		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		_root.addChild(ambientLightNode);

		universe.addBranchGraph(_root);
		universe.getViewingPlatform().setNominalViewingTransform();
	}
}
