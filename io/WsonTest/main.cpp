#include <iostream>
#include <fstream>
#include <JavaScriptCore/JavaScriptCore.h>
#include "wson/wson.h"
#include "wson/wson_protocol.h"
#include "wson/wson_util.h"

static void test_wson_io();
static void test_protocol_wson_with_java();
static void test_utf8_convert();
static void test_ptr_illegal();

int main() {

    
    int program = 1;
    
    
    switch (program) {
        case 1:
            test_wson_io();
            break;
        case 2:
            test_protocol_wson_with_java();
            break;
        case 3:
            test_utf8_convert();
            break;
        case 4:
            test_ptr_illegal();
            break;
        default:
            break;
    }
    std::cout << "program run done " << program << std::endl;
    return 0;
}

static void test_ptr_illegal() {
    int* ptr = (int*)malloc(1);
    int* mv  = ptr + 100;
    std::cout << "ptr 100 " <<  *mv  << "ptr" << mv << std::endl;
   
    free(ptr);
}

static void test_utf8_convert() {
    std::wstring china = L"中国";

    std::cout << "length " << china.length() << std::endl;


    //printf("cstr %s \n", china.c_str());


    size_t length = china.length();
    wchar_t* data = (wchar_t *) china.c_str();

    printf("data测试  %d   %d  %ld\n", data[1], china.at(1), sizeof(wchar_t));

    std::string utf8;


    wson::utf16_convert_to_utf8_string((uint16_t *)data, length*2, utf8);


    printf("cstr %s \n", utf8.c_str());


    //testWson();
    //testWsonWithJava();

    //double b = 9.99999999999999999999991E+244;

   // printf("double %lf", b);
}

static void  test_protocol_wson_with_java() {
    wson_io_buffer* buffer = wson_io_buffer_new();
    double d = 210.33576;
    wson_io_write_double(buffer, d);
    wson_io_write_var_int(buffer, 12345);
    wson_io_write_var_int(buffer, -1);
    wson_io_write_var_int(buffer, INT32_MAX);
    wson_io_write_var_int(buffer, INT32_MIN);
    wson_io_write_var_uint(buffer, 1);
    wson_io_write_var_uint(buffer, INT32_MAX);
    wson_io_write_fixed_int64_with_tag(buffer, 9223372036854775807l);
    wson_io_write_fixed_int64_with_tag(buffer, 9223372036854775806l);
    wson_io_write_fixed_int64_with_tag(buffer, -9223372036854775806l);
    std::ofstream os;
    os.open("/Users/furture/code/pack/java/src/test/resources/data.dat", std::ios_base::binary);
    os.write((char*)buffer->data, buffer->position);
    os.close();
    wson_io_buffer_free(buffer);

}

static void test_wson_io() {
    wson_io_buffer* buffer = wson_io_buffer_new();
    wson_io_write_double(buffer, 20.3356);
    wson_io_write_var_int(buffer, 12345);
    buffer->position = 0;
    double d = wson_io_read_double(buffer);
    if(d == 20.3356){
        printf("passed double\n");
    }else{
        printf("failed double\n");
    }
    int32_t  i = wson_io_read_var_int(buffer);
    if( i == 12345){
        printf("pass int %d\n",  i);
    }else{
        printf("failed int\n");
    }

    if(buffer->position == 12){
        printf("passed position\n");
    }
    buffer->position = 0;
    wson_io_write_var_int(buffer, -123456);
    printf("pass varint buffer position  %d\n",  buffer->position);
    buffer->position = 0;
    int32_t  varint = wson_io_read_var_int(buffer);
    if(varint == -123456){
        printf("pass varint %d\n",  varint);
    }else{
        printf("failed varint %d\n", varint);
    }


    buffer->position = 0;
    wson_io_write_fixed_int64_with_tag(buffer, 9223372036854775807l);
    printf("pass varint %d\n",  buffer->position);
    buffer->position = 0;
    wson_io_read_type(buffer);
    int64_t  ll = wson_io_read_long(buffer);
    if(ll == 9223372036854775807l){
        printf("pass varint %lld\n",  ll);
    }else{
        printf("failed varint %lld\n", ll);
    }
    wson_io_buffer_free(buffer);
}
