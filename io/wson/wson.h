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

#ifndef WSON_H
#define WSON_H

#include <inttypes.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>


#define WSON_IO_BUFFER_ENSURE_SIZE(size)  {if((buffer->length) < (buffer->position + (size))){\
                                           wson_io_buffer_resize(buffer, (uint32_t)(size));\
                                      }}



#ifdef __cplusplus
extern "C" {
#endif

typedef struct wson_io_buffer{
    void* data;
    size_t position;
    size_t length;
} wson_io_buffer;

/**
 * FIXME 通过inline提升性能，代码整理
 */



/**
 * create wson buffer
 * */
wson_io_buffer* wson_io_buffer_new(void);

wson_io_buffer* wson_io_buffer_new_size(size_t size);



/** constructor with data */
wson_io_buffer* wson_io_buffer_from(void* data, uint32_t length);


void wson_io_buffer_resize(wson_io_buffer* buffer, size_t size);


inline void wson_io_buffer_require(wson_io_buffer *buffer, size_t size){
    WSON_IO_BUFFER_ENSURE_SIZE(size*sizeof(uint8_t));
}
    
/**
 * push int, varint uint byte int double bts to buffer, without type signature
 * */
void wson_io_write_var_int(wson_io_buffer *buffer, int32_t num);
void wson_io_write_var_uint(wson_io_buffer *buffer, uint32_t num);
void wson_io_write_byte(wson_io_buffer *buffer, uint8_t bt);
void wson_io_write_type(wson_io_buffer *buffer, uint8_t bt);
void wson_io_write_double(wson_io_buffer *buffer, double num);
void wson_io_write_float(wson_io_buffer *buffer, float num);
void wson_io_write_uint64(wson_io_buffer *buffer, uint64_t num);
void wson_io_write_bytes(wson_io_buffer *buffer, const void *src, int32_t length);


/**
 * free  buffer and data
 * */
void wson_io_buffer_free(wson_io_buffer *buffer);

/**
 * only free  buffer, data none free
 * */
void wson_io_buffer_free_except_data(wson_io_buffer *buffer);



/**
 * parse buffer, return data from current position not include signature
 * */
int8_t wson_io_read_byte(wson_io_buffer *buffer);
int8_t wson_io_read_type(wson_io_buffer *buffer);
int32_t wson_io_read_var_int(wson_io_buffer *buffer);
uint32_t wson_io_read_var_uint(wson_io_buffer *buffer);
double wson_io_read_double(wson_io_buffer *buffer);
float wson_io_read_float(wson_io_buffer *buffer);
int64_t wson_io_read_long(wson_io_buffer *buffer);
uint64_t wson_io_read_fixed_uint64(wson_io_buffer *buffer);
uint8_t* wson_io_read_bts(wson_io_buffer *buffer, uint32_t length);

inline void wson_io_to_zero_position(wson_io_buffer *buffer) {
    buffer->position = 0;
}

inline void wson_io_to_position(wson_io_buffer *buffer, size_t position) {
    buffer->position = position;
}

inline bool wson_io_has_next(wson_io_buffer *buffer){
    return buffer->position < buffer->length;
}



#ifdef __cplusplus
}
#endif

#endif //WSON_H
