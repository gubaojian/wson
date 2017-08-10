//
// Created by furture on 2017/8/4.
//

#include <string.h>
#include "pack.h"


union number{
    double  d;
    uint64_t l;
};

#define MSG_BUFFER_SIZE  512

#define MSG_BUFFER_ENSURE_SIZE(size)  {if(buffer->length < buffer->position + size){\
                                           msg_buffer_resize(buffer, size);\
                                      }}

static void msg_buffer_resize(msg_buffer* buffer, int size){
    if(size < buffer->length){
         size = buffer->length;
        if( size > 1024*16){
            size = 1024*16;
        }
    }else{
        size +=MSG_BUFFER_SIZE;
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

 msg_buffer* msg_buffer_new(){
    msg_buffer* ptr = malloc(sizeof(msg_buffer));
    ptr->data = malloc(sizeof(int8_t)*MSG_BUFFER_SIZE);
    ptr->position = 0;
    ptr->length = MSG_BUFFER_SIZE;
    return ptr;
}



void msg_buffer_push_int(msg_buffer* buffer, uint32_t num){
     MSG_BUFFER_ENSURE_SIZE(sizeof(uint32_t));
    uint8_t* data = (buffer->data + buffer->position);
    data[3] = (uint8_t) (num & 0xFF);
    data[2] = (uint8_t) ((num >>  8) &  0xFF);
    data[1] = (uint8_t) ((num >> 16) & 0xFF);
    data[0] = (uint8_t) ((num >> 24) & 0xFF);
    buffer->position += sizeof(uint32_t);
}

void msg_buffer_push_varint(msg_buffer* buffer, int32_t value){
    uint32_t num = msg_buffer_varint_Zig(value);
    msg_buffer_push_uint(buffer, num);
}

void msg_buffer_push_uint(msg_buffer* buffer, uint32_t num){
    MSG_BUFFER_ENSURE_SIZE(sizeof(uint32_t) + sizeof(uint8_t));
    uint8_t * data = (buffer->data + buffer->position);
    int size =0;
    do{
        data[size] = (uint8_t)((num & 0x7F) | 0x80);
        size++;
    }while((num >>= 7) != 0);
    data[size - 1] &=0x7F;
    buffer->position += size;
}

void msg_buffer_push_byte(msg_buffer* buffer, uint8_t bt){
    MSG_BUFFER_ENSURE_SIZE(sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = bt;
    buffer->position += sizeof(uint8_t);
}


 void msg_buffer_push_boolean(msg_buffer* buffer,  uint8_t value){
      MSG_BUFFER_ENSURE_SIZE(sizeof(uint8_t) + sizeof(uint8_t));
    uint8_t* data = (buffer->data + buffer->position);
    *data = MSG_BUFFER_BOOLEAN_TYPE;
    *(data + 1) = value;
    buffer->position += (sizeof(uint8_t) + sizeof(uint8_t));
 }


void msg_buffer_push_long(msg_buffer* buffer, uint64_t num){
    MSG_BUFFER_ENSURE_SIZE(sizeof(uint64_t));
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

void msg_buffer_push_double(msg_buffer* buffer, double num){
    union number ld;
    ld.d = num;
    msg_buffer_push_long(buffer, ld.l);
}


void msg_buffer_push_bytes(msg_buffer* buffer,  const void* src, int32_t length){
    MSG_BUFFER_ENSURE_SIZE(length);
    void* dst = buffer->data + buffer->position;
    memcpy(dst, src, length);
    buffer->position += length;
}

int8_t msg_buffer_next_byte(msg_buffer* buffer){
    int8_t* ptr = (buffer->data + buffer->position);
    buffer->position += sizeof(int8_t);
    return *ptr;
}



int32_t msg_buffer_next_int(msg_buffer* buffer){
    uint8_t* data = (buffer->data + buffer->position);
    buffer->position += sizeof(int32_t);
    return  ((int32_t)data[3] & 0xFF)
             + (((int32_t)data[2] & 0xFF) << 8)
             + (((int32_t)data[1] & 0xFF) << 16)
             + (((int32_t)data[0] & 0xFF) << 24);
}

int32_t msg_buffer_next_varint(msg_buffer* buffer){
    return msg_buffer_varint_Zag(msg_buffer_next_uint(buffer));
}

uint32_t msg_buffer_next_uint(msg_buffer* buffer){
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

uint64_t msg_buffer_next_long(msg_buffer* buffer){
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

double msg_buffer_next_double(msg_buffer* buffer){
    union number ld;
    ld.l = msg_buffer_next_long(buffer);
    return ld.d;
}


uint8_t* msg_buffer_next_bts(msg_buffer* buffer, int length){
    uint8_t * ptr = (buffer->data + buffer->position);
    buffer->position += length;
    return ptr;
}



void msg_buffer_free(msg_buffer* buffer){
    if(buffer->data){
        free(buffer->data);
        buffer->data = NULL;
    }
    if(buffer){
        free(buffer);
        buffer = NULL;
    }
}

