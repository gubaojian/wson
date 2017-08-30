//
// Created by furture on 2017/8/4.
//

#include "tson.h"
#include <stdio.h>


union number{
    double  d;
    uint64_t l;
};

#define TSON_BUFFER_SIZE  1024

#define TSON_BUFFER_ENSURE_SIZE(size)  {if(buffer->length < buffer->position + size){\
                                           msg_buffer_resize(buffer, size);\
                                      }}

static void msg_buffer_resize(tson_buffer* buffer, int size){
    if(size < buffer->length){
         if(buffer->length < 1024*16){
            size = 1024*16;
         }else{
            size = buffer->length;
         }
    }else{
        size +=TSON_BUFFER_SIZE;
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

 tson_buffer* tson_buffer_new(){
    tson_buffer* ptr = malloc(sizeof(tson_buffer));
    ptr->data = malloc(sizeof(int8_t)*TSON_BUFFER_SIZE);
    ptr->position = 0;
    ptr->length = TSON_BUFFER_SIZE;
    return ptr;
}

tson_buffer* tson_buffer_from(void* data, int length){
    tson_buffer* ptr = malloc(sizeof(tson_buffer));
    ptr->data = data;
    ptr->position = 0;
    ptr->length = length;
    return ptr;
}



void tson_push_int(tson_buffer *buffer, int32_t value){
    uint32_t num = msg_buffer_varint_Zig(value);
    tson_push_uint(buffer, num);
}

void tson_push_uint(tson_buffer *buffer, uint32_t num){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint32_t) + sizeof(uint8_t));
    uint8_t * data = (buffer->data + buffer->position);
    int size =0;
    do{
        data[size] = (uint8_t)((num & 0x7F) | 0x80);
        size++;
    }while((num >>= 7) != 0);
    data[size - 1] &=0x7F;
    buffer->position += size;
}

void tson_push_byte(tson_buffer *buffer, uint8_t bt){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = bt;
    buffer->position += sizeof(uint8_t);
}

void tson_push_type(tson_buffer *buffer, uint8_t bt){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = bt;
    buffer->position += sizeof(uint8_t);
}


void tson_push_type_boolean(tson_buffer *buffer, uint8_t value){
      TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t) + sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_BOOLEAN_TYPE;
    *(data + 1) = value;
    buffer->position += (sizeof(uint8_t) + sizeof(uint8_t));
 }


void tson_push_type_int(tson_buffer *buffer, int32_t num){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_NUMBER_INT_TYPE;
    buffer->position += (sizeof(uint8_t));
    tson_push_int(buffer, num);
}

void tson_push_type_double(tson_buffer *buffer, double num){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_NUMBER_DOUBLE_TYPE;
    buffer->position += (sizeof(uint8_t));
    tson_push_double(buffer, num);
}

void tson_push_type_string(tson_buffer *buffer, const void *src, int32_t length){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_STRING_TYPE;
    buffer->position += (sizeof(uint8_t));
    tson_push_uint(buffer, length);
    tson_push_bytes(buffer, src, length);
}

void tson_push_type_string_length(tson_buffer *buffer, int32_t length){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_STRING_TYPE;
    buffer->position += (sizeof(uint8_t));
    tson_push_uint(buffer, length);
}

void tson_push_type_null(tson_buffer *buffer){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_NULL_TYPE;
    buffer->position += (sizeof(uint8_t));
}

void tson_push_type_map(tson_buffer *buffer, uint32_t size){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_MAP_TYPE;
    buffer->position += (sizeof(uint8_t));
    tson_push_uint(buffer, size);
}

void tson_push_type_array(tson_buffer *buffer, uint32_t size){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_ARRAY_TYPE;
    buffer->position += (sizeof(uint8_t));
    tson_push_uint(buffer, size);
}


void tson_push_type_extend(tson_buffer *buffer, const void *src, int32_t length){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = TSON_EXTEND_TYPE;
    buffer->position += (sizeof(uint8_t));
    tson_push_uint(buffer, length);
    tson_push_bytes(buffer, src, length);
}

void tson_push_ensure_size(tson_buffer *buffer, uint32_t dataSize){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint8_t)*dataSize);
}

void tson_push_long(tson_buffer *buffer, uint64_t num){
    TSON_BUFFER_ENSURE_SIZE(sizeof(uint64_t));
    uint8_t* data = (buffer->data + buffer->position);
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

void tson_push_double(tson_buffer *buffer, double num){
    union number ld;
    ld.d = num;
    tson_push_long(buffer, ld.l);
}


void tson_push_bytes(tson_buffer *buffer, const void *src, int32_t length){
    TSON_BUFFER_ENSURE_SIZE(length);
    void* dst = buffer->data + buffer->position;
    memcpy(dst, src, length);
    buffer->position += length;
}

int8_t tson_next_type(tson_buffer *buffer){
    int8_t* ptr = (buffer->data + buffer->position);
    buffer->position += sizeof(int8_t);
    return *ptr;
}

int8_t tson_next_byte(tson_buffer *buffer){
    int8_t* ptr = (buffer->data + buffer->position);
    buffer->position += sizeof(int8_t);
    return *ptr;
}


int32_t tson_next_int(tson_buffer *buffer){
    return msg_buffer_varint_Zag(tson_next_uint(buffer));
}

uint32_t tson_next_uint(tson_buffer *buffer){
    uint8_t *  ptr = (buffer->data + buffer->position);
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

uint64_t tson_next_long(tson_buffer *buffer){
    uint8_t* data = (buffer->data + buffer->position);
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

double tson_next_double(tson_buffer *buffer){
    union number ld;
    ld.l = tson_next_long(buffer);
    return ld.d;
}


uint8_t* tson_next_bts(tson_buffer *buffer, int length){
    uint8_t * ptr = (buffer->data + buffer->position);
    buffer->position += length;
    return ptr;
}

void tson_buffer_free(tson_buffer *buffer){
    if(buffer->data){
        free(buffer->data);
        buffer->data = NULL;
    }
    if(buffer){
        free(buffer);
        buffer = NULL;
    }
}

