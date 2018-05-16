//
// Created by furture on 2018/5/15.
//

#ifndef WSON_UTIL_H
#define WSON_UTIL_H

#include <cstdint>
#include <string>

namespace wson{

    /**
     *  unicode to utf8 convertor with zero dependency inspired by java sdk character source
     * */
    void utf16_convert_to_utf8_string(uint16_t * utf16, int length, std::string& utf8);

    char* utf16_convert_to_utf8_cstr(uint16_t * utf16, int length);

    void utf16_convert_to_utf8_quote_string(uint16_t * utf16, int length, std::string& utf8);

    /**
     * support double float int32 int64 , not thread safe
     * */
    void str_append_number(std::string& str, double  num);
    void str_append_number(std::string& str, float  num);
    void str_append_number(std::string& str, int32_t  num);
    void str_append_number(std::string& str, int64_t  num);
}


#endif
