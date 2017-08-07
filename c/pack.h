//
// Created by furture on 2017/8/4.
//

#ifndef HELLO_PACK_H
#define HELLO_PACK_H

#include <inttypes.h>
#include <stdlib.h>




#ifdef __cplusplus
extern "C" {
#endif

typedef struct msg_buffer{
    void* data;
    int32_t position;
    int32_t length;
} msg_buffer;




/**
 * object type, one byte type flag
 * number two byte flag, two show int or double, number small ENDIAN
 * */
#define  NULL_TYPE   '0'
#define  NUMBER_TYPE  'n'
#define  STRING_TYPE  's'
#define  BOOLEAN_TYPE 'b'
#define  NUMBER_INT_TYPE  'i'
#define  NUMBER_DOUBLE_TYPE  'd'
#define  ARRAY_TYPE  '['
#define  MAP_TYPE   '{'

/**
 * create msg buffer
 * */
msg_buffer* msg_buffer_new();

/**
 * push int to buffer, without type info
 * */
void msg_buffer_push_int(msg_buffer* buffer, uint32_t num);
void msg_buffer_push_varint(msg_buffer* buffer, int32_t num);
void msg_buffer_push_uint(msg_buffer* buffer, uint32_t num);
/**
 * push one byte to buffer, without type info
 * */
void msg_buffer_push_byte(msg_buffer* buffer, uint8_t bt);

/**
 * push double or long to buffer, without type info
 * */
void msg_buffer_push_double(msg_buffer* buffer, double num);
void msg_buffer_push_long(msg_buffer* buffer, uint64_t num);

/**
 * push double to buffer,  without type info
 * */
void msg_buffer_push_bytes(msg_buffer* buffer,  void* src, int32_t length);

/**
 * free  buffer
 * */
void msg_buffer_free(msg_buffer* buffer);


/**
 * parse buffer, parse data and move postion
 * */
int8_t msg_buffer_next_byte(msg_buffer* buffer);

int32_t msg_buffer_next_int(msg_buffer* buffer);
int32_t msg_buffer_next_varint(msg_buffer* buffer);
uint32_t msg_buffer_next_uint(msg_buffer* buffer);

double msg_buffer_next_double(msg_buffer* buffer);
uint64_t msg_buffer_next_long(msg_buffer* buffer);

uint8_t* msg_buffer_next_bts(msg_buffer* buffer, int length);



#ifdef __cplusplus
}
#endif

#endif //HELLO_PACK_H
