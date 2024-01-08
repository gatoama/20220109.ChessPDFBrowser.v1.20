GL2ES2Pipeline
===
The main change if Java3D 1.7 is the addition of a pipeline that is compatible with the ES2 and ES3 OpenGL drivers.
These drivers are generally found less powerful OS and hardware, for example iOS, Android and Raspbian.

Note that this release of Java3D is still tightly bound to the awt package and so cannot run on any thing that does not support that package e.g. Android

In addition this release does not yet fully support compressed textures, which are mandatory on all apps that are of significant size due to memory constraints. You can however extend it to support them, contact the forum to find out how.


##How to use the GL2ES2 pipeline
Put this property in either the command line 

-Dj3d.rend=jogl2es2

or at the beginning of your main

public static main(String[] args){System.setProperty("j3d.rend", "jogl2es2"); …

You will probably also want these 2 other properties at least:

com.frojasg1.sun.awt.noerasebackground=true

j3d.displaylist=false 

##Where to get examples of its use, including shader code
Please look in the [java3d-examples](https://github.com/philjord/java3d-examples.git) project particularly under the org.jdesktop.j3d.examples.gl2es2pipeline package

The examples include:
* texture coordinate generation
* texture + lighting
* Phong lighting
* Blinn-Phong lighting
* various material usages
* information on fog data that's available

A good place to start is with:
* SimpleShaderAppearance.java 
* fixed_function_shader.vert 
* fixed_function_shader.frag


There is a nice comparison in the org.jdesktop.j3d.examples.sphere_motion package of FFP, glsl with \#version 100 built-in variables and the gl2es2pipeline equivalent. This shows that the output is nearly identical and shows how easy it is to go from a built-in using shader to the gl2es2pipeline.

##What variables can be used in the shaders
In creating the GL2ES2Pipeline, the mechanism was setup such that the some new variables would be available that will work like the earlier built-in variables from glsl \#version 100 and that they would flow through from the scenegraph/pipeline into the shader, in a manner that should  allow simple conversion and a clear understanding.

The built-in gl\_\* type variables have been replicated by having a gl\* equivalent.
To access these new variables they need to be defined in the shader, this is unlike the built-in equivalents.  

This occurs for attributes and uniforms e.g.

gl\_Vertex can be accessed via glVertex, so long as glVertex is defined in the shader like so:

attribute vec4 glVertex;

It will supply exactly the same value as gl\_Vertex did.

Note that Light, Material and Fog data require a struct to be defined before they can be used, see examples for more information.

These example shaders attempt to explain the new usable built-in equivalent variables and some information on how to calculate other built-in variables that may be required.

/j3dexamples/src/classes/org/jdesktop/j3d/examples/gl2es2pipeline/fixed\_function\_shader.frag

/j3dexamples/src/classes/org/jdesktop/j3d/examples/gl2es2pipeline/fixed\_function\_shader.vert

Or

<https://github.com/philjord/java3d-examples/blob/dev1.7.0/src/classes/org/jdesktop/j3d/examples/gl2es2pipeline/fixed_function_shader.frag>

<https://github.com/philjord/java3d-examples/blob/dev1.7.0/src/classes/org/jdesktop/j3d/examples/gl2es2pipeline/fixed_function_shader.vert>

A note of caution:
If you are using a shader with a low \#version number it will not complain if you accidentally mis-type one of the new GL2ES2Pipline variables as it’s equivalent fixed function built-in e.g.

glVertex 

as

gl_Vertex

The shader will compile and run fine, but you will get no data in that variable. The solution is to set a higher version number e.g. \#version 150 or to be very careful.


##GL2ES2Pipeline limitations  
These limitations will be written out to standard err if they can be detected by the pipeline. In many cases they cannot, and will either be ignored or throw an exception.
The GL2ES2 pipeline only supports a subset of the Geometry data types and formats.
* Coordinates must be defined and float type, colors must be float type, if defined. 
* J3DGraphics2D of Canvas3D is not supported 
* Rasters and Decaling is not supported. 
* Model Clip is not supported and must be re-implemented in shaders 
* QuadArray or IndexedQuadArray cannot be supported. 
* Texture Coordinate generation cannot be supported. 
* Texture Lod, Filter, Sharpen and Combine cannot be supported
* Texture3D cannot be supported. 
* Accum style anti-aliasing cannot be supported. 
* RasterOps from RenderingAttributes cannot be used. 
* It is strongly recommended that you use the format GeometryArray.USE\_NIO\_BUFFER = true. 
* Note LineArray and LineStripArray will not render as nicely as the fixed function pipeline.
* Antialiasing enable/disable GL\_MULTI\_SAMPLE is gone, but the method glSampleCoverage exists still, so if the caps support it then it must be on
GL\_SAMPLE\_ALPHA\_TO\_COVERAGE and GL\_SAMPLE\_COVERAGE can be enabled under ES2
* Display lists are removed, so if you are not using by_ref geometry you are likely to need to add System.setProperty("j3d.displaylist", "false");.
* TextureFillBackground is not supported, but geometry Background works
* Line patterns cannot be supported
* Screen door transparency cannot be supported
* AutoMipMaps are disabled as pure ES2 has a separate system glGenMipMaps but GL2ES2 doesn’t support this
* Image formats without Alpha (like TYPE\_INT\_BGR) will not have alpha forced to 1, so alpha will be undefined
* Texture boundary colors gone (and therefore CLAMP\_TO\_BOUNDARY)
* ImageComponentRetained.TYPE\_BYTE\_ABGR and Texture.INTENSITY andImageComponentRetained.TYPE\_INT\_BGR cannot be supported



