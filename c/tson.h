//
// Created by furture on 2017/8/4.
//

#ifndef TSON_H
#define TSON_H

#include <inttypes.h>
#include <stdlib.h>
#include <string.h>




#ifdef __cplusplus
extern "C" {
#endif

typedef struct tson_buffer{
    void* data;
    int32_t position;
    int32_t length;
} tson_buffer;




/**
 * tson data type
 * */
#define  TSON_NULL_TYPE   '0'
#define  TSON_STRING_TYPE  's'
#define  TSON_BOOLEAN_TYPE 'b'
#define  TSON_NUMBER_INT_TYPE  'i'
#define  TSON_NUMBER_DOUBLE_TYPE  'd'
#define  TSON_ARRAY_TYPE  '['
#define  TSON_MAP_TYPE   '{'
#define  TSON_EXTEND_TYPE   'e'

/**
 * create tson buffer
 * */
tson_buffer* tson_buffer_new();


/**
 * push value with type signature; 1 true, 0 false, with type TSON_BOOLEAN_TYPE
 * signature  + byte
 */
void tson_push_type_boolean(tson_buffer *buffer, uint8_t value);
void tson_push_type_int(tson_buffer *buffer, int32_t num);
void tson_push_type_double(tson_buffer *buffer, double num);
void tson_push_type_string(tson_buffer *buffer, const void *src, int32_t length);
void tson_push_type_null(tson_buffer *buffer);
void tson_push_type_map(tson_buffer *buffer, uint32_t size);
void tson_push_type_array(tson_buffer *buffer, uint32_t size);
void tson_push_type_extend(tson_buffer *buffer, const void *src, int32_t length);
void tson_push_ensure_size(tson_buffer *buffer, uint32_t dataSize);
void tson_push_type_string_length(tson_buffer *buffer, int32_t length);
/**
 * push int, varint uint byte int double bts to buffer, without type signature
 * */
void tson_push_int(tson_buffer *buffer, int32_t num);
void tson_push_uint(tson_buffer *buffer, uint32_t num);
void tson_push_byte(tson_buffer *buffer, uint8_t bt);
void tson_push_type(tson_buffer *buffer, uint8_t bt);
void tson_push_double(tson_buffer *buffer, double num);
void tson_push_long(tson_buffer *buffer, uint64_t num);
void tson_push_bytes(tson_buffer *buffer, const void *src, int32_t length);


/**
 * free  buffer
 * */
void tson_buffer_free(tson_buffer *buffer);


/**
 * parse buffer, return data from current position not include signature
 * */
int8_t tson_next_byte(tson_buffer *buffer);
int8_t tson_next_type(tson_buffer *buffer);
int32_t tson_next_int(tson_buffer *buffer);
uint32_t tson_next_uint(tson_buffer *buffer);
double tson_next_double(tson_buffer *buffer);
uint64_t tson_next_long(tson_buffer *buffer);
uint8_t* tson_next_bts(tson_buffer *buffer, int length);

/** constructor with data */
tson_buffer* tson_buffer_from(void* data, int length);

#ifdef __cplusplus
}
#endif

#endif //TSON_H
