# libGen

Library that contains common classes used by my applications.

It is mainly divided into:

## libGeneric

It has not any particular dependency on View aspect of applications.

The only dependency is com.googlecode.juniversalchardet to be able to detect different charsets on text files.

It has an abstraction of view, that is not dependent on the environment (It can be used with swing, or may be even with android).

## libGenericDesktop

It is the library that have base classes and components for my swing applications.

### Main features:
* It is prepared to work with different languages
* It is prepared to zoom the tree of components with a chosen factor. 
* Base Splash window.
* Base About window.
* Base License window.
* Lens effect over a JPane.
* Base JTextPane formatters and mouse link detectors.
* Automatic Undo/Redo/Copy/Paste features for JTextComponents.

### Dependencies:
It has a strong dependency with swing classes.

JPanes and windows have been dessigned with Netbeans 8.1. So, if you want to change the existing dessign of components, it has also a strong dependency with Netbeans. If not, you can use any other IDE.
