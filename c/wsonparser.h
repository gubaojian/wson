//
// Created by furture on 2018/3/2.
//

#ifndef WSONTEST_WSONPARSER_H
#define WSONTEST_WSONPARSER_H

#include <stdbool.h>
#include "wson.h"

#ifdef __cplusplus
extern "C" {
#endif

enum Type{
    WNULL,
    WBool,
    WNumber,
    WString,
    WObject,
    WArray
};

typedef struct wson_parser{
    wson_buffer* buffer;
    enum Type  type;
    int8_t  _buffer_type;
    union parser_state{
        //recycle-list
        bool value;
    } state;
} wson_parser;

wson_parser* wson_parser_init(void *data, int length);

enum Type  wson_parser_next_type(wson_parser* parser);
int wson_parser_next_size(wson_parser* parser);
/**please free this object */
const char* wson_parser_next_string(wson_parser* parser);
const char* wson_parser_next_number(wson_parser* parser);
bool wson_parser_next_bool(wson_parser* parser);
void wson_parser_destory(wson_parser* parser);


#ifdef __cplusplus
}
#endif


#endif //WSONTEST_WSONPARSER_H
