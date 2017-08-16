# tson
tiny binary json libaray inspired by bson and message pack


### tson data type
| type |  signature | format |
| -------- | -------- | -------- |
| number int     | 'i'    | signature + varint    |
| number double    | 'd'   | signature + 8 byte (big endian)|
| string   | 's'   | signature + length + bytes(utf-8)|
| null    | '0'   |  signature |
| boolean    | 'b'   | signature + 1(true)/0(false)|
| array    | '['   | signature + length + elements|
| object(map)    | '{'   | signature + size + key, value, key, value|


string length, map size ar store used usigned varint.

here is an example, data in json as follows:

```json
{
  "name" : "hello world"
}
```

in tson



like http://msgpack.org/  but more small and simple
