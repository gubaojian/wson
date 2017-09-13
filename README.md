# tson
tiny binary json libaray inspired by bson and message pack，less than 500 line code you can embed in your application.


### tson data type
| type |  signature | format |
| -------- | -------- | -------- |
| number int     | 'i'    | signature + varint    |
| number double    | 'd'   | signature + 8 byte (big endian)|
| string   | 's'   | signature + var length + bytes(utf-8)|
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

in tson

![tson](https://raw.githubusercontent.com/gubaojian/tson/master/image/TSON.png)
