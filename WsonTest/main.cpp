#include <iostream>
#include <fstream>
#include <JavaScriptCore/JavaScriptCore.h>
#include "wson/wson.h"

static void  testWson();
static void  testWsonWithJava();


int main() {
    testWson();
    testWsonWithJava();

    double b = 9.99999999999999999999991E+244;

    printf("double %lf", b);
    return 0;
}


static void  testWsonWithJava(){
    wson_buffer* buffer = wson_buffer_new();
    double d = 210.33576;
    wson_push_double(buffer, d);
    wson_push_int(buffer, 12345);
    wson_push_int(buffer, -1);
    wson_push_int(buffer, INT32_MAX);
    wson_push_int(buffer, INT32_MIN);
    wson_push_uint(buffer, 1);
    wson_push_uint(buffer, INT32_MAX);
    wson_push_type_long(buffer, 9223372036854775807l);
    wson_push_type_long(buffer, 9223372036854775806l);
    wson_push_type_long(buffer, -9223372036854775806l);
    std::ofstream os;
    os.open("/Users/furture/code/pack/java/src/test/resources/data.dat", std::ios_base::binary);
    os.write((char*)buffer->data, buffer->position);
    os.close();
    wson_buffer_free(buffer);

}

static void  testWson(){
    wson_buffer* buffer = wson_buffer_new();
    wson_push_double(buffer, 20.3356);
    wson_push_int(buffer, 12345);
    buffer->position = 0;
    double d = wson_next_double(buffer);
    if(d == 20.3356){
        printf("passed double\n");
    }else{
        printf("failed double\n");
    }
    int32_t  i = wson_next_int(buffer);
    if( i  == 12345){
        printf("pass int %d\n",  i);
    }else{
        printf("failed int\n");
    }

    if(buffer->position == 12){
        printf("passed position\n");
    }
    buffer->position = 0;
    wson_push_int(buffer, -123456);
    printf("pass varint %d\n",  buffer->position);
    buffer->position = 0;
    int32_t  varint = wson_next_int(buffer);
    if(varint == -123456){
        printf("pass varint %d\n",  varint);
    }else{
        printf("failed varint %d\n", varint);
    }


    buffer->position = 0;
    wson_push_type_long(buffer, 9223372036854775807l);
    printf("pass varint %d\n",  buffer->position);
    buffer->position = 0;
    wson_next_type(buffer);
    int64_t  ll = wson_next_long(buffer);
    if(ll == 9223372036854775807l){
        printf("pass varint %lld\n",  ll);
    }else{
        printf("failed varint %lld\n", ll);
    }
    wson_buffer_free(buffer);
}