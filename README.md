# Black Rook Database Utilities

Copyright (c) 2013-2019 Black Rook Software. All rights reserved.  
[http://blackrooksoftware.com/projects.htm?name=db](http://blackrooksoftware.com/projects.htm?name=db)  
[https://github.com/BlackRookSoftware/DB](https://github.com/BlackRookSoftware/DB)

### Required Libraries

Black Rook Commons 2.32.0+  
[https://github.com/BlackRookSoftware/Common](https://github.com/BlackRookSoftware/Common)

### Required Java Modules

[java.base](https://docs.oracle.com/javase/10/docs/api/java.base-summary.html)  
[java.sql](https://docs.oracle.com/javase/10/docs/api/java.sql-summary.html)  

### Introduction

This library contains classes that aid in database querying and connection
pooling, two of the most tedious things to do with SQL databases. Queried rows
can be returned as specialized maps or POJOs with their values set via 
reflection.

This also contains support for some popular NoSQL servers. 

### Library

Contained in this release is a series of classes used for database querying, 
SQL database connection pooling, and other SQL-related tasks. The javadocs 
contain basic outlines of each package's contents plus class documentation.

This also contains a Redis client for interfacing with the Key-Value store,
[Redis](http:/redis.io/).

### Compiling with Ant

To download the dependencies for this project (if you didn't set that up yourself already), type:

	ant dependencies

A *build.properties* file will be created/appended to with the *dev.base* property set.
	
To compile this library with Apache Ant, type:

	ant compile

To make a JAR of this library, type:

	ant jar

And it will be placed in the *build/jar* directory.

### Other

This program and the accompanying materials
are made available under the terms of the GNU Lesser Public License v2.1
which accompanies this distribution, and is available at
http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html

A copy of the LGPL should have been included in this release 
(LICENSE.txt). If it was not, please contact us for a copy, or to 
notify us of a distribution that has not included it. 
