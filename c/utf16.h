//
// Created by furture on 2018/5/15.
//

#ifndef WSONTEST_UTF16_H
#define WSONTEST_UTF16_H

#include <cstdint>
#include <string>
/**
 *  unicode to utf8 convertor with zero dependency inspired by java sdk character source
 * */
namespace utf16{

    void utf16_convert_to_utf8_string(uint16_t * utf16, int length, std::string& utf8);

    char* utf16_convert_to_utf8_cstr(uint16_t * utf16, int length);

    void utf16_convert_to_utf8_quote_string(uint16_t * utf16, int length, std::string& utf8);
}


#endif //WSONTEST_UTF16_H
