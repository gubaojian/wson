//
// Created by furture on 2018/5/15.
//

///Users/furture/Library/Android/sdk/ndk-bundle/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-addr2line -piCfe libweexjss.so 00052d7d

#include "wson/wson.h"
#include "wson/wson_parser.h"
#include "FileUtils.h"
#include "bench.h"


void test_big_unicode(){
    const char* src = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/bug/bigUnicode.dat");
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/bug/bigUnicode.wson");
    wson_parser parser(data);
    int type = parser.nextType();
    std::string json = parser.nextStringUTF8(type);
    if(strncmp(json.c_str(), src, strlen(json.c_str())) == 0){
        printf("pass test_big_unicode %s %s ", json.c_str(), src);
    }else{
        printf("failed test_big_unicode %s %s ", json.c_str(), src);
    }
    free((void*)data);
    free((void*)src);
}


void test_bench_example(){
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/weex2.wson");
    wson_parser parser(data);
    double start = bench::now_ms();
    for(int i=0; i<100000; i++){
        int type = parser.nextType();
        parser.nextStringUTF8(type);
        parser.resetState();
    }
    printf("bench end used %f ms \n", (bench::now_ms() - start));

    free((void*)data);
}

void test_map_example(){
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/weex2.wson");
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
    }else{
        printf("wson data error %s \n", parser.nextStringUTF8(type).c_str());
    }
    free((void*)data);
}

void test_add_element_example(){
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/addElement.wson");
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
    }else{
        printf("wson data error %s \n", parser.nextStringUTF8(type).c_str());
    }
    free((void*)data);
}



void test_array_example(){
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/data/int_array_100.wson");
    wson_parser parser(data);
    int type = parser.nextType();
    if(parser.isArray(type)){
        int size = parser.nextArraySize();
        for(int i=0; i<size; i++){
            uint8_t  valueType = parser.nextType();
            std::string value = parser.nextStringUTF8(valueType);
            printf("array %d == %s \n", i, value.c_str());
        }
    }else{
        printf("wson data error %s \n", parser.nextStringUTF8(type).c_str());
    }
    free((void*)data);
}


void test_to_string_example(){
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/data/int_array_100.wson");
    wson_parser parser(data);
    printf("toString %s \n", parser.toStringUTF8().c_str());
}


void test_quote_example(){
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/plus/parser.wson");
    wson_parser parser(data);
    printf("toString %s \n", parser.toStringUTF8().c_str());
}

void test_next_line_example(){
    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/plus/parser.wson");
    wson_parser parser(data);
    printf("toString %s \n", parser.toStringUTF8().c_str());
}


int main(){
    test_add_element_example();
    test_bench_example();
    test_big_unicode();
    test_map_example();
    test_array_example();
    test_to_string_example();

    test_quote_example();


    char*  floatBuffer = new char[64];

    snprintf(floatBuffer, 64, "%d", 10);

    printf("floatBuffer %s ", floatBuffer);

    const char* data = FileUtils::readFile("/Users/furture/code/pack/java/src/test/resources/bug/bigUnicode.wson");
    printf("read file succes type \n");
    wson_parser parser(data);
    printf("create file succes type \n");
    uint8_t  type = parser.nextType();
    printf("next type %d \n", type);
    if(parser.isMap(type)){
        int size = parser.nextMapSize();
        for(int i=0; i<size; i++){
            std::string key = parser.nextMapKeyUTF8();
            std::string value = parser.nextStringUTF8(parser.nextType());
            printf("map key %s \n", key.c_str());
            printf("map value %s \n", value.c_str());
        }
    }else{
        printf("object value %s \n", parser.nextStringUTF8(type).c_str());
    }
    free((void*)data);


}