rat-xml
=======

Random Access Tiny XML. A library that takes XML and blats it into a model in a CDB file. The reader API then provides a
kind-of lightweight DOM-esque API for traversing elements/attributes etc.


Current issues:

* No support for XPaths for "random" element access (need to work out from root)
* File size is 3X original XML
	* The keys are massive and repeat a lot 
		* could ZIP results?
		* could keep a table of path to long IDs and then use the long as key (would mean two phase lookup) 
* Does not do namespaces in the XML
* JAXEN support is WIP
