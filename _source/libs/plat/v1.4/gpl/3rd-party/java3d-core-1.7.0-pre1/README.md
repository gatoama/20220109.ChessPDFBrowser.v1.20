Java3D Readme
===
The source code for the j3d-core project is copyrighted code that is
licensed to individuals or companies who download or otherwise access
the code.

The copyright notice for this project is in COPYRIGHT.txt

The source code license information for this project is in LICENSE.txt

Additional information and license restrictions for third party source
code are found in the THIRDPARTY-LICENSE-*.txt files.

##Building Java3D

The instructions below are based on eclipse, however they should work on most common IDE's

Clone the repo using 
https://github.com/philjord/java3d-core.git
Make note of the root folder you use  
Right click on the Project Explorer view and select Import  
Expand the Maven group and select Existing Maven Project and click Next  
For Root directory click Browse and find the root folder that you cloned the repo into  
There should be a pom.xml that is ticked in the Projects area  
Click Finish  
Repeat for java3d-utils using
https://github.com/philjord/java3d-utils.git
Repeat for vecmath using
https://github.com/philjord/vecmath.git

Once that's done it's likely you'll need to 
Right click on pom.xml -> Run As -> Maven clean
then
Right click on pom.xml -> Run As -> Maven generate sources
This should place a derived version of VersionInfo.java into /java3d-core/target/generated-sources/java-templates


If you are new to Java3D then it might be good to repeat the clone import step for java3d-examples too, using
https://github.com/philjord/java3d-examples.git 

Now if you right click on  
org.jdesktop.j3d.examples.hello_universe.HelloUniverse.java  
Run as ... Java Application  
You should see Java3D working  

##Documentation

###Javadocs  
.  
<http://download.java.net/media/java3d/javadoc/1.5.0/>  
or  
<https://github.com/scijava/java3d-javadoc>  
though neither is correctly up to date  

###Basic overview guide  

For the best kick off point see [/docs/tutorial/](/docs/tutorial/)   
For a description of performance see [/docs/perf_guide.txt](/docs/perf_guide.txt)  
Old Java3d project site with lots of information <https://java.net/projects/java3d>
General technical tips <https://java.net/projects/java3d/pages/Java3DApplicationDev>  
Using the newer ES2/ES3 pipeline [/docs/GL2ES2Pipeline usage guide.md](/docs/GL2ES2Pipeline usage guide.md)   

###Tutorials  
.  
<http://www.java3d.org/tutorial.html>  

###FAQ  
.  
<http://jogamp.org/wiki/index.php/Java3D_FAQ>  

###General assistance  
contact the forum at  
<http://forum.jogamp.org/>  