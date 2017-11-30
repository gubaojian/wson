//
// Created by furture on 2017/8/4.
//

#include "wson.h"
#include <stdio.h>


union number{
    double  d;
    uint64_t l;
};

#define WSON_BUFFER_SIZE  1024

#define WSON_BUFFER_ENSURE_SIZE(size)  {if((buffer->length) < (buffer->position + size)){\
                                           msg_buffer_resize(buffer, size);\
                                      }}

static void msg_buffer_resize(wson_buffer* buffer, uint32_t size){
    if(size < buffer->length){
         if(buffer->length < 1024*16){
            size = 1024*16;
         }else{
            size = buffer->length;
         }
    }else{
        size +=WSON_BUFFER_SIZE;
    }
    size += buffer->length;
    buffer->data = realloc(buffer->data, size);
    buffer->length = size;
}


static inline int32_t msg_buffer_varint_Zag(uint32_t ziggedValue)
{
    int32_t value = (int32_t)ziggedValue;
    return (-(value & 0x01)) ^ ((value >> 1) & ~( 1<< 31));
}

static inline uint32_t msg_buffer_varint_Zig(int32_t value)
{
    return (uint32_t)((value << 1) ^ (value >> 31));

}

 wson_buffer* wson_buffer_new(void){
    wson_buffer* ptr = malloc(sizeof(wson_buffer));
    ptr->data = malloc(sizeof(int8_t)*WSON_BUFFER_SIZE);
    ptr->position = 0;
    ptr->length = WSON_BUFFER_SIZE;
    return ptr;
}

wson_buffer* wson_buffer_from(void* data, uint32_t length){
    wson_buffer* ptr = malloc(sizeof(wson_buffer));
    ptr->data = data;
    ptr->position = 0;
    ptr->length = length;
    return ptr;
}



void wson_push_int(wson_buffer *buffer, int32_t value){
    uint32_t num = msg_buffer_varint_Zig(value);
    wson_push_uint(buffer, num);
}

void wson_push_uint(wson_buffer *buffer, uint32_t num){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint32_t) + sizeof(uint8_t));
    uint8_t * data = ((uint8_t*)buffer->data + buffer->position);
    int size =0;
    do{
        data[size] = (uint8_t)((num & 0x7F) | 0x80);
        size++;
    }while((num >>= 7) != 0);
    data[size - 1] &=0x7F;
    buffer->position += size;
}

void wson_push_byte(wson_buffer *buffer, uint8_t bt){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = bt;
    buffer->position += sizeof(uint8_t);
}

void wson_push_type(wson_buffer *buffer, uint8_t bt){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = bt;
    buffer->position += sizeof(uint8_t);
}


void wson_push_type_boolean(wson_buffer *buffer, uint8_t value){
      WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t) + sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    if(value){
       *data = WSON_BOOLEAN_TYPE_TRUE;
    }else{
        *data = WSON_BOOLEAN_TYPE_FALSE;
    }
    buffer->position += sizeof(uint8_t);
 }


void wson_push_type_int(wson_buffer *buffer, int32_t num){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NUMBER_INT_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_push_int(buffer, num);
}

void wson_push_type_double(wson_buffer *buffer, double num){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NUMBER_DOUBLE_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_push_double(buffer, num);
}

void wson_push_type_string(wson_buffer *buffer, const void *src, int32_t length){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_STRING_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_push_uint(buffer, length);
    wson_push_bytes(buffer, src, length);
}

void wson_push_property(wson_buffer *buffer, const void *src, int32_t length){
    wson_push_uint(buffer, length);
    wson_push_bytes(buffer, src, length);
}

void wson_push_type_string_length(wson_buffer *buffer, int32_t length){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_STRING_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_push_uint(buffer, length);
}

void wson_push_type_null(wson_buffer *buffer){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_NULL_TYPE;
    buffer->position += (sizeof(uint8_t));
}

void wson_push_type_map(wson_buffer *buffer, uint32_t size){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_MAP_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_push_uint(buffer, size);
}

void wson_push_type_array(wson_buffer *buffer, uint32_t size){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_ARRAY_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_push_uint(buffer, size);
}


void wson_push_type_extend(wson_buffer *buffer, const void *src, int32_t length){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = ((uint8_t*)buffer->data + buffer->position);
    *data = WSON_EXTEND_TYPE;
    buffer->position += (sizeof(uint8_t));
    wson_push_uint(buffer, length);
    wson_push_bytes(buffer, src, length);
}

void wson_push_ensure_size(wson_buffer *buffer, uint32_t dataSize){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t)*dataSize);
}

void wson_push_long(wson_buffer *buffer, uint64_t num){
    WSON_BUFFER_ENSURE_SIZE(sizeof(uint64_t));
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

void wson_push_double(wson_buffer *buffer, double num){
    union number ld;
    ld.d = num;
    wson_push_long(buffer, ld.l);
}


void wson_push_bytes(wson_buffer *buffer, const void *src, int32_t length){
    WSON_BUFFER_ENSURE_SIZE(length);
    void* dst = ((uint8_t*)buffer->data + buffer->position);
    memcpy(dst, src, length);
    buffer->position += length;
}

int8_t wson_next_type(wson_buffer *buffer){
    int8_t* ptr = (int8_t*)((uint8_t*)buffer->data + buffer->position);
    buffer->position += sizeof(int8_t);
    return *ptr;
}

int8_t wson_next_byte(wson_buffer *buffer){
    int8_t* ptr = (int8_t*)(((uint8_t*)buffer->data + buffer->position));
    buffer->position += sizeof(int8_t);
    return *ptr;
}


int32_t wson_next_int(wson_buffer *buffer){
    return msg_buffer_varint_Zag(wson_next_uint(buffer));
}

uint32_t wson_next_uint(wson_buffer *buffer){
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

uint64_t wson_next_long(wson_buffer *buffer){
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

double wson_next_double(wson_buffer *buffer){
    union number ld;
    ld.l = wson_next_long(buffer);
    return ld.d;
}


uint8_t* wson_next_bts(wson_buffer *buffer, uint32_t length){
    uint8_t * ptr = ((uint8_t*)buffer->data + buffer->position);
    buffer->position += length;
    return ptr;
}

void wson_buffer_free(wson_buffer *buffer){
    if(buffer->data){
        free(buffer->data);
        buffer->data = NULL;
    }
    if(buffer){
        free(buffer);
        buffer = NULL;
    }
}
