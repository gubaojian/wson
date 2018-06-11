# wson
fast tiny binary json libaray inspired by bson and message pack，less than 1000 line code you can embed in your application with super speed to transfer data in local machine.


### wson data format
| type |  signature | format |
| -------- | -------- | -------- |
| number int     | 'i'    | signature + varint    |
| number double    | 'd'   | signature + 8 byte (big endian)|
| number float    | 'F'   | signature + 4 byte (big endian)|
| string   | 's'   | signature + var length + bytes( unicoder utf-16)|
| null    | '0'   |  signature |
| boolean    | 't' or 'f'   | signature |
| array    | '['   | signature + var length + elements|
| map    |  '{'   | signature + var size + key, value, key, value|

string length, map size ar store used usigned varint.

here is an example, data in json as follows:

```json
{
  "name" : "hello world"
}
```

in wson

![wson](https://raw.githubusercontent.com/gubaojian/tson/master/image/TSON.png)


### 1 quick start c++
#### 1.1 include header file
```c++
  #include "wson/wson_parser.h"
```
#### 1.2 read object map example
```c++
  const char* data = readFile("data.wson"); // binary wson data from some where
  wson_parser parser(data);
  int type = parser.nextType();
  if(parser.isMap(type)){
      int size = parser.nextMapSize();
      for(int i=0; i<size; i++){
          std::string key = parser.nextMapKeyUTF8();
          uint8_t  valueType = parser.nextType();
          std::string value = parser.nextStringUTF8(valueType);
          printf("map %s == %s \n", key.c_str(), value.c_str());
      }
  }
```
#### 1.3 read object array example

```c++
 const char* data = readFile("data.wson"); // binary wson data from some where
  wson_parser parser(data);
  int type = parser.nextType();
  if(parser.isArray(type)){
    int size = parser.nextArraySize();
    for(int i=0; i<size; i++){
        uint8_t  valueType = parser.nextType();
        std::string value = parser.nextStringUTF8(valueType);
        printf("array %d == %s \n", i, value.c_str());
    }
  }
```
  

### 2 quick start java
#### 2.1 convert java object to wson binary
```java
Person person = new Person();
byte[] bts = Wson.toWson(person);
```
#### 2.2 convert wson java object
```java
byte[] bts = readFile("person.wson");
Map map = (Map)Wson.parse(bts);
```

if you want more details; please see source and api







