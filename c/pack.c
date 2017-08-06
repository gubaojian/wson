//
// Created by furture on 2017/8/4.
//

#include <string.h>
#include <printf.h>
#include "pack.h"

#define MSG_BUFFER_SIZE  256

#define MSG_BUFFER_ENSURE_SIZE(size)  {if(buffer->length < buffer->position + size){\
                                           msg_buffer_resize(buffer, size);\
                                      }}

//#define  MSG_PLATFORM_BIG_ENDIAN  1

static void msg_buffer_resize(msg_buffer* buffer, int addSize){
    if(addSize < MSG_BUFFER_SIZE){
        addSize = MSG_BUFFER_SIZE;
    } else{
        addSize +=MSG_BUFFER_SIZE;
    }
    addSize += buffer->length;
    buffer->data = realloc(buffer->data, addSize);
    buffer->length = addSize;
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
#ifdef MSG_PLATFORM_BIG_ENDIAN
    uint8_t* data = (buffer->data + buffer->position);
    data[0] = (uint8_t)(num);
    data[1] = (uint8_t)(num >>  8);
    data[2] = (uint8_t)(num >> 16);
    data[3] = (uint8_t)(num >> 24);
#else
    uint32_t * data = (buffer->data  + buffer->position);
    *data = num;
#endif
    buffer->position += sizeof(uint32_t);
}

void msg_buffer_push_varint(msg_buffer* buffer, int32_t value){
    uint32_t num = msg_buffer_varint_Zig(value);
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

static void msg_buffer_push_long(msg_buffer* buffer, uint64_t num){
    MSG_BUFFER_ENSURE_SIZE(sizeof(uint64_t));
#ifdef MSG_PLATFORM_BIG_ENDIAN
    uint8_t* data = (buffer->data + buffer->position);
    uint32_t part0 = (uint32_t)(num);
    uint32_t part1 = (uint32_t)(num >> 32);
    data[0] = (uint8_t)(part0);
    data[1] = (uint8_t)(part0 >>  8);
    data[2] = (uint8_t)(part0 >> 16);
    data[3] = (uint8_t)(part0 >> 24);
    data[4] = (uint8_t)(part1);
    data[5] = (uint8_t)(part1 >>  8);
    data[6] = (uint8_t)(part1 >> 16);
    data[7] = (uint8_t)(part1 >> 24);
#else
    uint64_t* data = (buffer->data + buffer->position);
    *data = num;
#endif
    buffer->position += sizeof(uint64_t);
}

void msg_buffer_push_double(msg_buffer* buffer, double num){
#ifdef MSG_PLATFORM_BIG_ENDIAN
    uint64_t lnum;
    memcpy(&lnum, &num, sizeof(double));
    msg_buffer_push_long(buffer, lnum);
#else
    MSG_BUFFER_ENSURE_SIZE(sizeof(double));
    double* data = (buffer->data + buffer->position);
    *data = num;
    buffer->position += sizeof(double);
#endif
}


void msg_buffer_push_bytes(msg_buffer* buffer,  void* src, int32_t length){
    MSG_BUFFER_ENSURE_SIZE(length);
    void* dst = buffer->data + buffer->position;
    memcpy(dst, src, length);
    buffer->position += length;
}

int8_t msg_buffer_next_byte(msg_buffer* buffer){
    int8_t * ptr = (buffer->data + buffer->position);
    buffer->position += sizeof(int8_t);
    return *ptr;
}

int32_t msg_buffer_next_int(msg_buffer* buffer){
    int32_t * ptr = (buffer->data + buffer->position);
    buffer->position += sizeof(int32_t);
    return *ptr;
}

int32_t msg_buffer_next_varint(msg_buffer* buffer){
    uint8_t *  ptr = (buffer->data + buffer->position);
    uint32_t num = *ptr;
    if((num & 0x80) == 0){
        buffer->position +=1;
        return  msg_buffer_varint_Zag(num);
    }
    num &=0x7F;
    uint8_t chunk =  ptr[1];
    num |= (chunk & 0x7F) << 7;
    if((chunk & 0x80) == 0){
        buffer->position += 2;
        return  msg_buffer_varint_Zag(num);
    }
    chunk = ptr[2];
    num |= (chunk & 0x7F) << 14;
    if((chunk & 0x80) == 0){
        buffer->position += 3;
        return  msg_buffer_varint_Zag(num);
    }

    chunk = ptr[3];
    num |= (chunk & 0x7F) << 21;
    if((chunk & 0x80) == 0){
        buffer->position += 4;
        return  msg_buffer_varint_Zag(num);
    }
    chunk = ptr[4];
    num |= (chunk & 0x0F) << 28;
    buffer->position += 5;
    return  msg_buffer_varint_Zag(num);
}

double msg_buffer_next_double(msg_buffer* buffer){
    double * ptr = (buffer->data + buffer->position);
    buffer->position += sizeof(double);
    return *ptr;
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

