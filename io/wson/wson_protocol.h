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
// Created by furture on 2017/8/4.
//

#ifndef WSON_PROTOCOL_H
#define WSON_PROTOCOL_H

#include <inttypes.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#include "wson.h"


#ifdef __cplusplus
extern "C" {
#endif


    /**
     * wson custom protocol type
     * */
    #define  WSON_NULL_TYPE   '0'
    #define  WSON_STRING_TYPE  's'
    #define  WSON_UINT8_STRING_TYPE 'u'
    #define  WSON_BOOLEAN_TYPE_TRUE 't'
    #define  WSON_BOOLEAN_TYPE_FALSE 'f'
    #define  WSON_NUMBER_INT_TYPE  'i'
    #define  WSON_NUMBER_FLOAT_TYPE  'F'
    #define  WSON_NUMBER_DOUBLE_TYPE  'd'
    #define  WSON_NUMBER_LONG_TYPE  'l'
    #define  WSON_NUMBER_BIG_INT_TYPE  'g'
    #define  WSON_NUMBER_BIG_DECIMAL_TYPE  'e'
    #define  WSON_ARRAY_TYPE  '['
    #define  WSON_MAP_TYPE   '{'
    #define  WSON_EXTEND_TYPE   'b'


    /**
     * push value with type signature; 1 true, 0 false, with type WSON_BOOLEAN_TYPE
     * signature  + byte
     */
    void wson_io_write_boolean_with_tag(wson_io_buffer *buffer, uint8_t value);
    void wson_io_write_var_int_with_tag(wson_io_buffer *buffer, int32_t num);
    void wson_io_write_fixed_int64_with_tag(wson_io_buffer *buffer, int64_t num);
    void wson_io_write_double_with_tag(wson_io_buffer *buffer, double num);
    void wson_io_write_float_with_tag(wson_io_buffer *buffer, float num);
    void wson_io_write_string_with_tag(wson_io_buffer *buffer, const void *src, int32_t length);
    void wson_io_write_uint8_string_with_tag(wson_io_buffer *buffer, const uint8_t *src, int32_t length);
    void wson_io_write_type_null(wson_io_buffer *buffer);
    void wson_io_write_type_map(wson_io_buffer *buffer, uint32_t size);
    void wson_io_write_type_array(wson_io_buffer *buffer, uint32_t size);
    void wson_io_write_type_extend(wson_io_buffer *buffer, const void *src, int32_t length);
    void wson_io_write_ensure_size(wson_io_buffer *buffer, uint32_t dataSize);
    void wson_io_write_type_string_length(wson_io_buffer *buffer, int32_t length);
    void wson_io_write_map_key(wson_io_buffer *buffer, const void *src, int32_t length);
       

#ifdef __cplusplus
}
#endif

#endif //WSON_PROTOCOL_H
