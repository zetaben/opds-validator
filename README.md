OPDS Validor
============

This program is a specialized validator for the Open Publication Distribution System (http://http://opds-spec.org/).

It's based on Jing (http://code.google.com/p/jing-trang/).


Compile
-------


 `$ ./gradlew build`

The resulting distribution will be in `build/distributions`

Usage
-----
```
 usage: bin/opds-validator [options] file
 Options:
 -h              This help message
 -v opds_version OPDSVersion to use (default 1.2)
 -e encoding     File encoding (passed to jing)
 -f format       Error output format (default text, avail : json)
```


