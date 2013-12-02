# Random Access, Tiny (RAT) XML.

RAT XML is a library for working with XML data to use minimum read-time memory. 
At read time XML structures and data are pulled on demand from an indexed file format - designed for fast access. The structure supports DOM style element traversal as well as access via XPath.

A file writer (and utilities) are provided for taking XML data and converting to the RAT XML (CDB) file format.

The reader API then provides a kind-of lightweight DOM-esque API for traversing elements/attributes etc.

## The gory details...
The key to Rat XML is the underlying file format which is simply a [CBD](http://cr.yp.to/cdb.html) file. CDB files are essentially hashtables that provide rapid file access to data stored in a key (two disk hits per read using the OS memory mapped random access file). If you want to know more about the internals of the CDB file format I recommend [this page](http://www.unixuser.org/~euske/doc/cdbinternals/)

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

However to compress the storage for keys we generate a "virtual" key table:

| Key                            | Data       |
|--------------------------------|------------|
| `/contacts[0]`                 | 1          |
| `/contacts[0]@src`             | 7          |
| `/contacts[0]/person[0]`       | 2          |
| `/contacts[0]/person[0]@id`    | 8          |
| `/contacts[0]/person[0]@type`  | 9          |
| `/contacts[0]/person[0]/name`  | 3          |
| `/contacts[0]/person[0]/email` | 4          |
| `/contacts[0]/person[1]`       | 5          |
| `/contacts[0]/person[1]@id`    | 10         |
| `/contacts[0]/person[1]/name`  | 6          |

And the following resultant data keys:
Where the key indicates this is element data for ID 3 etc.:

| Key    | Data       |
|--------|------------|
| `E:3`  | John Smith |
| `E:4`  | js@foo.bar |
| `E:6`  | Ian Brown  |

And for attributes the key indicates attribute data ID 7 etc.

| Key    | Data        |
|--------|-------------|
| `A:7`  | My Contacts |
| `A:8`  | j-smith     |
| `A:9`  | A           |
| `A:10` | i-brown     |


Finally to make traversal quicker (rather than searching keys) we store meta data about children in special keys; 
child elements (under the key `#E:<parent_id>`) and attributes (under the key `#A:<parent_id>`). 
In the meta data we also store the element ID to make child traversal quicker (no need to look up the ID in CDB). 

Continuing the example the following meta data would be stored:

| Key    | Data          |
|--------|---------------|
| `#E:0` | contacts[0]:1 |
| `#E:1` | person[0]:2   |
| `#A:1` | src:          |
| `#A:2` | id:           |
| `#A:2` | type:         |
| `#E:1` | person[1]:5   |
| `#A:5` | id:           |

The keys themselves are 9 bytes - 8 byte long and 1 byte for the "type" (A, E, #A, #E).

This format is fast/easy to access and does not involve large amounts of key searching when performing node traversal (parent:child, child:parent). But it does come at some cost. The size of the file. We duplicate paths a lot in the rat XML and that takes up a large (the largest) portion of the file. However, we do not load this into memory - we use a random access file and fancy pointers to load data (see the CDB file spec) to access data.  
