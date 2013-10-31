# Random Access, Tiny (RAT) XML.

RAT XML is a library for working with XML data to use minimum read-time memory. 
At read time XML structures and data are pulled on demand from an indexed file format - designed for fast access. The structure supports DOM style element traversal as well as access via XPath.

A file writer (and utilities) are provided for taking XML data and converting to the RAT XML (CDB) file format.

The reader API then provides a kind-of lightweight DOM-esque API for traversing elements/attributes etc.

## The gory details...
The key to Rat XML is the underlying file format which is simply a [CBD](http://cr.yp.to/cdb.html) file. CDB files are essentially hashtables that provide rapid file access to data stored in a key (two disk hits per read using the OS memory mapped random access file). If you want to know more about the internals of the CDB file format I recommend [this page](www.unixuser.org/~euske/doc/cdbinternals/)

In RAT XML we store element & attribute data in the CDB table using a key that represents the "path" to that element or attribute. In addition we store meta data about the structure of the XML (what children an element has).

For example consider a simple XML file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<contacts src="My Contacts">
   <person id="j-smith" type="A">
      <name>John Smith</name>
      <email>js@foo.bar</email>
   </person>
   <person id="i-brown">
      <name>Ian Brown</name>
   </person>
</contacts>

```
We would store the following element data:

| Key                            | Data       |
|--------------------------------|------------|
| `/contacts[0]`                 |            |
| `/contacts[0]/person[0]`       |            |
| `/contacts[0]/person[0]/name`  | John Smith |
| `/contacts[0]/person[0]/email` | js@foo.bar |
| `/contacts[0]/person[1]`       |            |
| `/contacts[0]/person[1]/name`  | Ian Brown  |

We would store the following attribute data:

| Key                            | Data        |
|--------------------------------|-------------|
| `/contacts[0]@src`             | My Contacts |
| `/contacts[0]/person[0]@id`    | j-smith     |
| `/contacts[0]/person[0]@type`  | A           |
| `/contacts[0]/person[1]@id`    | i-brown     |

Finally to make traversal quicker (rather than searching keys) we store meta data about a child's elements (under the key `<path>#?children`) and attributes (under the key `<path>#?attrs` in special meta data keys. Continuing the example the following meta data would be stored:

| Key                                | Data        |
|------------------------------------|-------------|
| `#?children`                       | contacts[0] |
| `/contacts[0]#?children`           | person[0]   |
| `/contacts[0]#?attrs`              | src         |
| `/contacts[0]/person[0]#?attrs`    | id          |
| `/contacts[0]/person[0]#?attrs`    | type        |
| `/contacts[0]/person[1]#?attrs`    | id          |

This format is fast/easy to access and does not involve large amounts of key searching when performing node traversal (parent:child, child:parent). But it does come at some cost. The size of the file. We duplicate paths a lot in the rat XML and that takes up a large (the largest) portion of the file. However, we do not load this into memory - we use a random access file and fancy pointers to load data (see the CDB file spec) to access data.

Maybe one day I'll make it more efficient ...see the corresponding issue on GitHub for discussion of this topic.  