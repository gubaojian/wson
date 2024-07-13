优先级并不高：
1、移除对fastjson依赖, 优先级不高。
2、支持普通类的序列化及反序列化, 优先级不高。
3、改造成utf8编码，对于jsc utf-16兼容后续废弃，单也可以，指定选项开启，以方便兼容其它平台
4、过滤transient 过滤

主要用于跨语言数据交换，自定义数据格式

单纯序列化反序列化还要补充不少内容。

json可以采用压缩，二进制数据也一样，因此数据大小只是一方面。

由于牺牲了可读性， 速度要快fastjson几倍才有意义，后续想想如何加速这块。

二进制协议增加类型，需要升级相应的库，不然版本会不兼容，一个稳定协议需要很久才能支持。
json可以作为默认降级逻辑。json也是必须的，作为版本兼容及降级。

根据需要增加copy到特定buffer行为。

后续想想如何加速这块，如何提升3倍以上。

序列化性能：
FASTJSON toJSON used 2185
WSON toWSON used 1057

bench /data/group.json
FASTJSON toJSON used 40
WSON toWSON used 49

bench /data/epub.json
FASTJSON toJSON used 3497
WSON toWSON used 2068

FASTJSON toJSON used 449
WSON toWSON used 317

file name /data.json
FastJSON parse used 2059
WSON parse used 940

file name /data.json

WSON parse used 985
FastJSON parse used 1721

file name /middle.json
FastJSON parse used 9
WSON parse used 8
file name /middle.json
WSON parse used 10
FastJSON parse used 3

file name /media.json
FastJSON parse used 17
WSON parse used 14
file name /media.json
WSON parse used 14
FastJSON parse used 5

file name /weex.json
FastJSON parse used 4
WSON parse used 16
file name /weex.json
WSON parse used 1
FastJSON parse used 5

file name /home.json
FastJSON parse used 503
WSON parse used 114
file name /home.json
WSON parse used 123
FastJSON parse used 259

file name /media2.json
FastJSON parse used 4
WSON parse used 2
file name /media2.json
WSON parse used 3
FastJSON parse used 4