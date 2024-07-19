# pson
parcel binary object nation, which is schema less with symbol table support. aim to be fastest serializable binary protocol.


https://amzn.github.io/ion-docs/docs/binary.html#5-decimal


# pbson data fotmat

every value is composed by type and encoding value as belows

       7       4 3       0
      +---------+---------+
      |    T   |    V    |
      +---------+---------+
we will introduction each as belows:

### 1.1 constant value type

current standard for const such as true/false/null/nan/undefined
       7       4 3       0
      +---------+---------+
      |    0   |    V    |
      +---------+---------+
T 0    V 0 false 1 true 2 NULL 3 N

### 1.2 ints number value type

       7       4 3       0
      +---------+---------+
      |    1   |    V    |
      +---------+---------+

 int long     

 ### 1.3 float/double number value type

       7       4 3       0
      +---------+---------+
      |    2   |    4/8    |
      +---------+---------+
 4 for float 8 for double
 double may use varint or compact
### 1.4 string type
       7       4 3       0
      +---------+---------+
      |    3   |    V    |
      +---------+---------+

 v for string length less than 14, others use var length data format

 ### 1.5 array/list type

       7       4 3       0
      +---------+---------+
      |    4   |    V    |
      +---------+---------+
0 empty array
varints array
float array
double array
byte array
string array

value array

table  array(~ checks)



 ### 1.6 object type
       7       4 3       0
      +---------+---------+
      |    5   |    V    |
      +---------+---------+

V standard for the size of property.

property string name / value
property string name / value

is property should symbol name


 ### 1.7 symbol value
        7       4 3       0
      +---------+---------+
      |    6   |    V    |
      +---------+---------+

 standard for symol table id
 like id name
   
 ### 1.8 table value // improve version
        7       4 3       0
      +---------+---------+
      |    7   |    V    |
      +---------+---------+

 standard for length table header
 string property header
 varlint row length
 row values


 
 like id name
   

## 7-43-0


pbon perfert

fast tiny pack binary json with meta data support. inspired by bson and message pack and android binary. 10 faster than normal json library with half data size.less. you can embed in your application with super speed and reduce trafic


### wson data format
| type |  signature | format |
| -------- | -------- | -------- |
| number int     | 'i'    | signature + varint    |
| number double    | 'd'   | signature + 8 byte (big endian)|
| number float    | 'F'   | signature + 4 byte (big endian)|
| string   | 's'   | signature + var length + bytes( unicoder utf-8)|
| null    | '0'   |  signature |
| boolean    | 't' or 'f'   | signature |
| array    | '['   | signature + var length + elements|
| object    |  '{'   | signature + var size + key, value, key, value|
| meta    |  'm'   | signature + varint|

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







