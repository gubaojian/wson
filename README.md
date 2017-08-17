# tson
tiny binary json libaray inspired by bson and message pack，less than 500 line code you can embed in your application, just copy to your application.


### tson data type
| type |  signature | format |
| -------- | -------- | -------- |
| number int     | 'i'    | signature + varint    |
| number double    | 'd'   | signature + 8 byte (big endian)|
| string   | 's'   | signature + length + bytes(utf-8)|
| null    | '0'   |  signature |
| boolean    | 'b'   | signature + 1(true)/0(false)|
| array    | '['   | signature + length + typed object valu|
| map    | '{'   | signature + size + key,  typed object value, key, typed object valu|


string length, map size ar store used usigned varint. map key is always string with out type

here is an example, data in json as follows:

```json
{
  "name" : "hello world"
}
```

in tson

![tson](https://raw.githubusercontent.com/gubaojian/tson/master/image/TSON.png)


#### reference

http://www.json.org/
http://www.json.org/json-zh.html
