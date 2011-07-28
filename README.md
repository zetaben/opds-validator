OPDS Validor
============

This program is a specialized validator for the Open Publication Distribution System (http://http://opds-spec.org/).
It's based on Jing (http://code.google.com/p/jing-trang/).


Compile
-------

The validator depends on jing and relaxng-datatype jars which at the moment should be located : 

```
/usr/share/jing/lib/jing.jar
/usr/share/relaxng-datatype/lib/relaxngDatatype.jar
```

Compilation should be as easy as  

 `$ ant`

Usage
-----
```
 usage: java -jar OPDSValidator [options] file
 Options:
 -h              This help message
 -v opds_version OPDSVersion to use (default 1.0)
 -e encoding     File encoding (passed to jing)
 -f format       Error output format (default text, avail : json)
```


