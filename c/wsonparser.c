//
// Created by furture on 2018/3/5.
//
#include <stdlib.h>
#include "wsonparser.h"
#include "wson.h"

wson_parser* wson_parser_init(void *data, int length){
    wson_parser* parser = malloc(sizeof(parser));
    parser->buffer = wson_buffer_from(data, length);
    parser->type = 0;
}

enum Type  wson_parser_next_type(wson_parser* parser){
    int8_t buffer_type  = wson_next_type(parser->buffer);
    enum Type  type;
    switch (buffer_type){
        case WSON_NULL_TYPE:
            type = WNULL;
            break;
        case WSON_STRING_TYPE:
            type = WString;
            break;
        case WSON_BOOLEAN_TYPE_TRUE: //
            type = WString;
            break;
        case WSON_BOOLEAN_TYPE_FALSE:
            type = WString;
            break;
        case WSON_NUMBER_INT_TYPE:
            type = WString;
            break;
        case  WSON_NUMBER_FLOAT_TYPE:
            type = WString;
            break;
        case  WSON_NUMBER_DOUBLE_TYPE:
            type = WString;
            break;
        case  WSON_NUMBER_LONG_TYPE:
            type = WString;
            break;
        case  WSON_NUMBER_BIG_INT_TYPE:
            type = WString;
            break;
        case  WSON_NUMBER_BIG_DECIMAL_TYPE:
            type = WString;
            break;
        case  WSON_ARRAY_TYPE:
            type = WString;
            break;
        case  WSON_MAP_TYPE:
            type = WString;
            break;
        default:
            //FIXME
            break;
    }

    return parser->type = type;
}
int wson_parser_next_size(wson_parser* parser);
/**please free this object */
const char* wson_parser_next_string(wson_parser* parser);
const char* wson_parser_next_number(wson_parser* parser);
bool wson_parser_next_bool(wson_parser* parser);
void wson_parser_destory(wson_parser* parser);

