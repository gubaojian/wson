/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//
// Created by furture on 2018/5/15.
//

#include "wson_parser.h"
#include "wson.h"
#include "utf16.h"

wson_parser::wson_parser(const char *data) {

    this->buffer = wson_buffer_from((void *) data, 1024*1024);
}

wson_parser::wson_parser(const char *data, int length) {
    this->buffer = wson_buffer_from((void *) data, length);
}

wson_parser::~wson_parser() {
    if(buffer){
        buffer->data = nullptr;
        free(buffer);
        buffer = NULL;
    }
}

std::string wson_parser::nextMapKeyUTF8(){
    int keyLength = wson_next_uint(buffer);
    uint16_t * utf16 = ( uint16_t *)wson_next_bts(buffer, keyLength);
    std::string str;
    utf16::utf16_convert_to_utf8_string(utf16, keyLength/sizeof(uint16_t), str);
    return  str;
}

void wson_parser::toJSONtring(std::string &builder){
    uint8_t  type = wson_next_type(buffer);
    switch (type) {
        case WSON_STRING_TYPE:
        case WSON_NUMBER_BIG_INT_TYPE:
        case WSON_NUMBER_BIG_DECIMAL_TYPE: {
                int size = wson_next_uint(buffer);
                uint16_t *utf16 = (uint16_t *) wson_next_bts(buffer, size);
                utf16::utf16_convert_to_utf8_quote_string(utf16, size / sizeof(uint16_t), builder);
            }
            return;
        case WSON_NULL_TYPE:
            builder.append("\"undefined\"");
            break;
        case WSON_NUMBER_INT_TYPE: {
            int32_t num = wson_next_int(buffer);;
            builder.append(std::to_string(num));
        }
            return;
        case WSON_NUMBER_FLOAT_TYPE: {
            float num = wson_next_float(buffer);
            builder.append(std::to_string(num));
        }
            return;
        case WSON_NUMBER_DOUBLE_TYPE: {
            double num = wson_next_double(buffer);
            builder.append(std::to_string(num));
        }
            return;
        case WSON_NUMBER_LONG_TYPE: {
            int64_t num = wson_next_long(buffer);
            builder.append(std::to_string(num));
        }
            return;
        case WSON_BOOLEAN_TYPE_TRUE:
            builder.append("true");
            return;
        case WSON_BOOLEAN_TYPE_FALSE:
            builder.append("false");
            return;
        case WSON_MAP_TYPE:{
                    int length = wson_next_uint(buffer);
                    builder.append("{");
                    for(int i=0; i<length; i++){
                        int keyLength = wson_next_uint(buffer);
                        uint16_t * utf16 = ( uint16_t *)wson_next_bts(buffer, keyLength);
                        utf16::utf16_convert_to_utf8_quote_string(utf16, keyLength/sizeof(uint16_t), builder);
                        builder.append(":");
                        toJSONtring(builder);
                        if(i != (length - 1)){
                            builder.append(",");
                        }
                    }
                    builder.append("}");
            }
            return;
        case WSON_ARRAY_TYPE:{
                builder.append("[");
                int length = wson_next_uint(buffer);
                for(int i=0; i<length; i++){
                    toJSONtring(builder);
                    if(i != (length - 1)){
                        builder.append(",");
                    }
                }
                builder.append("]");
            }
            return;
        default:
            break;
    }
}

std::string wson_parser::nextStringUTF8(uint8_t type) {
    std::string str;
    switch (type) {
        case WSON_STRING_TYPE:
        case WSON_NUMBER_BIG_INT_TYPE:
        case WSON_NUMBER_BIG_DECIMAL_TYPE: {
            int size = wson_next_uint(buffer);
            uint16_t *utf16 = (uint16_t *) wson_next_bts(buffer, size);
            utf16::utf16_convert_to_utf8_string(utf16, size / sizeof(uint16_t), str);
            return str;
        }
        case WSON_NULL_TYPE:
            str.append("undefined");
            break;
        case WSON_NUMBER_INT_TYPE: {
              int32_t num = wson_next_int(buffer);;
              str.append(std::to_string(num));
            }
            return str;
        case WSON_NUMBER_FLOAT_TYPE: {
            float num = wson_next_float(buffer);
            str.append(std::to_string(num));
        }
            return str;
        case WSON_NUMBER_DOUBLE_TYPE: {
            double num = wson_next_double(buffer);
            str.append(std::to_string(num));
        }
            return str;
        case WSON_NUMBER_LONG_TYPE: {
            int64_t num = wson_next_long(buffer);
            str.append(std::to_string(num));
        }
            return str;
        case WSON_BOOLEAN_TYPE_TRUE:
            str.append("true");
            return str;
        case WSON_BOOLEAN_TYPE_FALSE:
            str.append("false");
            return str;
        case WSON_MAP_TYPE:
        case WSON_ARRAY_TYPE:
            buffer->position--;
            toJSONtring(str);
        default:
            break;
    }
    return str;
}

double wson_parser::nextNumber(uint8_t type) {
    switch (type) {
        case WSON_STRING_TYPE:
        case WSON_NUMBER_BIG_INT_TYPE:
        case WSON_NUMBER_BIG_DECIMAL_TYPE: {
            int size = wson_next_uint(buffer);
            std::string str;
            wson_next_bts(buffer, size);
            uint16_t *utf16 = (uint16_t *) wson_next_bts(buffer, size);
            utf16::utf16_convert_to_utf8_string(utf16, size / sizeof(uint16_t), str);
            return atof(str.c_str());
        }
        case WSON_NULL_TYPE:
            return  0;
        case WSON_NUMBER_INT_TYPE:{
              int32_t num = wson_next_int(buffer);
              return num;
            }
            break;
        case WSON_NUMBER_FLOAT_TYPE:{
                float num = wson_next_float(buffer);
                return num;
            }
            break;
        case WSON_NUMBER_DOUBLE_TYPE:{
                double num = wson_next_double(buffer);
                return num;
            }
            break;
        case WSON_NUMBER_LONG_TYPE:{
                int64_t num = wson_next_long(buffer);
                return num;
            }
            break;
        case WSON_BOOLEAN_TYPE_TRUE:
            return 1;
        case WSON_BOOLEAN_TYPE_FALSE:
            return 0;
        case WSON_MAP_TYPE:
        case WSON_ARRAY_TYPE:
        default:
            break;
    }
    skipValue(type);
    return 0;
}

bool wson_parser::nextBool(uint8_t type) {
    if(type == WSON_BOOLEAN_TYPE_FALSE || type == WSON_NULL_TYPE){
        return false;
    }
    skipValue(type);
    return true;
}

void wson_parser::skipValue(uint8_t type) {
    switch (type) {
        case WSON_STRING_TYPE:
        case WSON_NUMBER_BIG_INT_TYPE:
        case WSON_NUMBER_BIG_DECIMAL_TYPE: {
            int size = wson_next_uint(buffer);
            wson_next_bts(buffer, size);
            return;
        }
        case WSON_NULL_TYPE:
            break;
        case WSON_NUMBER_INT_TYPE:
             wson_next_int(buffer);
            return;
        case WSON_NUMBER_FLOAT_TYPE:
            wson_next_float(buffer);
            return;
        case WSON_NUMBER_DOUBLE_TYPE:
            wson_next_double(buffer);
            return;
        case WSON_NUMBER_LONG_TYPE:
             wson_next_long(buffer);
            return;
        case WSON_BOOLEAN_TYPE_TRUE:
            return;
        case WSON_BOOLEAN_TYPE_FALSE:
            return;
        case WSON_MAP_TYPE:{
                int length = wson_next_uint(buffer);
                for(int i=0; i<length; i++){
                    int keyLength = wson_next_uint(buffer);
                    wson_next_bts(buffer, keyLength);
                    skipValue(wson_next_type(buffer));
                }
            }
            return;
        case WSON_ARRAY_TYPE:{
                int length = wson_next_uint(buffer);
                for(int i=0; i<length; i++){
                    skipValue(wson_next_type(buffer));
                }
            }
            return;
        default:
            break;
    }
}