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

#include "wson.h"
#include <stdio.h>


union double_number{
    double  d;
    uint64_t l;
};

union float_number{
    float  f;
    uint32_t i;
};

#define WSON_BUFFER_SIZE  4096



void wson_io_buffer_resize(wson_io_buffer* buffer, size_t size) {
    if(size < buffer->length){
         if(buffer->length < 1024*8){
            size = 1024*8;
         }else{
            size = buffer->length * 2;
         }
    } else {
        size += WSON_BUFFER_SIZE;
    }
    size += buffer->length;
    buffer->data = realloc(buffer->data, size);
    buffer->length = size;
}


static inline int32_t wson_io_var_int32_Zag(uint32_t ziggedValue)
{
    int32_t value = (int32_t)ziggedValue;
    return (-(value & 0x01)) ^ ((value >> 1) & ~( 1<< 31));
}

static inline uint32_t wson_io_buffer_var_uint32_Zig(int32_t value)
{
    return (uint32_t)((value << 1) ^ (value >> 31));

}

 wson_io_buffer* wson_io_buffer_new(void){
     return wson_io_buffer_new_size(4096);
}

wson_io_buffer* wson_io_buffer_new_size(size_t size) {
    wson_io_buffer* ptr = malloc(sizeof(wson_io_buffer));
    ptr->data = malloc(sizeof(int8_t)*size);
    ptr->position = 0;
    ptr->length = size;
    return ptr;
}

wson_io_buffer* wson_io_buffer_from(void* data, uint32_t length){
    wson_io_buffer* ptr = malloc(sizeof(wson_io_buffer));
    ptr->data = data;
    ptr->position = 0;
    ptr->length = length;
    return ptr;
}





inline void wson_io_write_var_int(wson_io_buffer *buffer, int32_t value){
    uint32_t num = wson_io_buffer_var_uint32_Zig(value);
    wson_io_write_var_uint(buffer, num);
}

inline void wson_io_write_var_uint(wson_io_buffer *buffer, uint32_t num){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint32_t) + sizeof(uint8_t));
    uint8_t * data = ((uint8_t*)buffer->data + buffer->position);
    int size =0;
    do{
        data[size] = (uint8_t)((num & 0x7F) | 0x80);
        size++;
    }while((num >>= 7) != 0);
    data[size - 1] &=0x7F;
    buffer->position += size;
}

inline void wson_io_write_byte(wson_io_buffer *buffer, uint8_t bt){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = bt;
    buffer->position += sizeof(uint8_t);
}


inline void wson_io_write_ensure_size(wson_io_buffer *buffer, uint32_t dataSize){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint8_t)*dataSize);
}

inline void wson_io_write_uint64(wson_io_buffer *buffer, uint64_t num){
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint64_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    data[7] = (uint8_t)(num & 0xFF);
    data[6] = (uint8_t)((num >> 8) & 0xFF);
    data[5] = (uint8_t)((num >> 16) & 0xFF);
    data[4] = (uint8_t)((num >> 24) & 0xFF);
    data[3] = (uint8_t)((num >> 32) & 0xFF);
    data[2] = (uint8_t)((num >> 40) & 0xFF);
    data[1] = (uint8_t)((num >> 48) & 0xFF);
    data[0] = (uint8_t)((num >> 56) & 0xFF);
    buffer->position += sizeof(uint64_t);
}

void wson_io_write_double(wson_io_buffer *buffer, double num){
    union double_number ld;
    ld.d = num;
    wson_io_write_uint64(buffer, ld.l);
}

void wson_io_write_float(wson_io_buffer *buffer, float f){
    union float_number fn;
    fn.f = f;
    uint32_t num = fn.i;
    WSON_IO_BUFFER_ENSURE_SIZE(sizeof(uint32_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    data[3] = (uint8_t)(num & 0xFF);
    data[2] = (uint8_t)((num >> 8) & 0xFF);
    data[1] = (uint8_t)((num >> 16) & 0xFF);
    data[0] = (uint8_t)((num >> 24) & 0xFF);
    buffer->position += sizeof(uint32_t);
}


inline void wson_io_write_bytes(wson_io_buffer *buffer, const void *src, int32_t length){
    WSON_IO_BUFFER_ENSURE_SIZE(length);
    void* dst = ((uint8_t*)buffer->data + buffer->position);
    memcpy(dst, src, length);
    buffer->position += length;
}

inline int8_t wson_io_read_type(wson_io_buffer *buffer){
    int8_t* ptr = (int8_t*)((uint8_t*)buffer->data + buffer->position);
    buffer->position += sizeof(int8_t);
    return *ptr;
}

inline int8_t wson_io_read_byte(wson_io_buffer *buffer){
    int8_t* ptr = (int8_t*)(((uint8_t*)buffer->data + buffer->position));
    buffer->position += sizeof(int8_t);
    return *ptr;
}


int32_t wson_io_read_var_int(wson_io_buffer *buffer){
    return wson_io_var_int32_Zag(wson_io_read_var_uint(buffer));
}

uint32_t wson_io_read_var_uint(wson_io_buffer *buffer){
    uint8_t *  ptr = ((uint8_t*)buffer->data + buffer->position);
    uint32_t num = *ptr;
    if((num & 0x80) == 0){
        buffer->position +=1;
        return  num;
    }
    num &=0x7F;
    uint8_t chunk =  ptr[1];
    num |= (chunk & 0x7F) << 7;
    if((chunk & 0x80) == 0){
        buffer->position += 2;
        return  num;
    }
    chunk = ptr[2];
    num |= (chunk & 0x7F) << 14;
    if((chunk & 0x80) == 0){
        buffer->position += 3;
        return  num;
    }

    chunk = ptr[3];
    num |= (chunk & 0x7F) << 21;
    if((chunk & 0x80) == 0){
        buffer->position += 4;
        return  num;
    }
    chunk = ptr[4];
    num |= (chunk & 0x0F) << 28;
    buffer->position += 5;
    return  num;
}

int64_t wson_io_read_long(wson_io_buffer *buffer){
    return wson_io_read_fixed_uint64(buffer);
}

inline uint64_t wson_io_read_fixed_uint64(wson_io_buffer *buffer){
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    buffer->position += sizeof(uint64_t);
    return (((uint64_t)data[7]) & 0xFF)
           + ((((uint64_t)data[6]) & 0xFF) << 8)
           + ((((uint64_t)data[5]) & 0xFF) << 16)
           + ((((uint64_t)data[4]) & 0xFF) << 24)
           + ((((uint64_t)data[3]) & 0xFF) << 32)
           + ((((uint64_t)data[2]) & 0xFF) << 40)
           + ((((uint64_t)data[1]) & 0xFF) << 48)
           + ((((uint64_t)data[0]) & 0xFF) << 56);
}

double wson_io_read_double(wson_io_buffer *buffer){
    union double_number ld;
    ld.l = wson_io_read_long(buffer);
    return ld.d;
}

inline float wson_io_read_float(wson_io_buffer *buffer){
    union float_number fn;
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    fn.i = ((data[3]) & 0xFF)
           + (((data[2]) & 0xFF) << 8)
           + (((data[1]) & 0xFF) << 16)
           + (((data[0]) & 0xFF) << 24);
    buffer->position += sizeof(uint32_t);
    return fn.f;
}


inline uint8_t* wson_io_read_bts(wson_io_buffer *buffer, uint32_t length){
    uint8_t * ptr = ((uint8_t*)buffer->data + buffer->position);
    buffer->position += length;
    return ptr;
}

void wson_io_buffer_free(wson_io_buffer *buffer){
    if(buffer->data){
        free(buffer->data);
        buffer->data = NULL;
    }
    if(buffer){
        free(buffer);
        buffer = NULL;
    }
}

void wson_io_buffer_free_except_data(wson_io_buffer *buffer){
    if(buffer->data){
        buffer->data = NULL;
    }
    if(buffer){
        free(buffer);
        buffer = NULL;
    }
}

