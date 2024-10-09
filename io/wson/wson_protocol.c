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
#include "wson_protocol.h"
#include <inttypes.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "wson.h"


inline void wson_io_write_type(wson_io_buffer *buffer, uint8_t bt){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = bt;
    buffer->position += sizeof(uint8_t);
}


inline void wson_io_write_boolean_with_tag(wson_io_buffer *buffer, uint8_t value){
      WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t) + sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    if(value){
       *data = WSON_BOOLEAN_TYPE_TRUE;
    }else{
        *data = WSON_BOOLEAN_TYPE_FALSE;
    }
    buffer->position += sizeof(uint8_t);
 }


inline void wson_io_write_var_int_with_tag(wson_io_buffer *buffer, int32_t num){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NUMBER_INT_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_var_int(buffer, num);
}

inline void wson_io_write_double_with_tag(wson_io_buffer *buffer, double num){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NUMBER_DOUBLE_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_double(buffer, num);
}

inline void wson_io_write_float_with_tag(wson_io_buffer *buffer, float num) {
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NUMBER_FLOAT_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_float(buffer, num);
}

inline void wson_io_write_fixed_int64_with_tag(wson_io_buffer *buffer, int64_t num){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NUMBER_LONG_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_uint64(buffer, num);
}

inline void wson_io_write_string_with_tag(wson_io_buffer *buffer, const void *src, int32_t length){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_STRING_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_var_uint(buffer, length);
    wson_io_write_bytes(buffer, src, length);
}

inline void wson_io_write_uint8_string_with_tag(wson_io_buffer *buffer, const uint8_t *src, int32_t length){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_UINT8_STRING_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_var_uint(buffer, length);
    wson_io_write_bytes(buffer, src, length);
}

inline void wson_io_write_map_key(wson_io_buffer *buffer, const void *src, int32_t length){
    wson_io_write_var_uint(buffer, length);
    wson_io_write_bytes(buffer, src, length);
}

inline void wson_io_write_type_string_length(wson_io_buffer *buffer, int32_t length){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_STRING_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_var_uint(buffer, length);
}

inline void wson_io_write_type_null(wson_io_buffer *buffer){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NULL_TYPE;
    buffer->position += (sizeof(uint8_t));
}

inline void wson_io_write_type_map(wson_io_buffer *buffer, uint32_t size){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_MAP_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_var_uint(buffer, size);
}

inline void wson_io_write_type_array(wson_io_buffer *buffer, uint32_t size){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_ARRAY_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_var_uint(buffer, size);
}


inline void wson_io_write_type_extend(wson_io_buffer *buffer, const void *src, int32_t length){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_EXTEND_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_io_write_var_uint(buffer, length);
    wson_io_write_bytes(buffer, src, length);
}

