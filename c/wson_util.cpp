//
// Created by furture on 2018/5/15.
//

#include "wson_util.h"
#include <stdio.h>


namespace wson {

    /**
     * see java jdk source to handle handle utf-16 in 4 byte
     * */
    static const u_int16_t  MIN_HIGH_SURROGATE = 0xD800;

    static const u_int16_t MAX_HIGH_SURROGATE = 0xDBFF;

    static const u_int16_t  MIN_LOW_SURROGATE  = 0xDC00;

    static const u_int16_t MAX_LOW_SURROGATE  = 0xDFFF;

    static const u_int32_t MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;

    static inline bool isHighSurrogate(u_int16_t ch) {
        return ch >= MIN_HIGH_SURROGATE && ch < (MAX_HIGH_SURROGATE + 1);
    }

    static inline bool isLowSurrogate(u_int16_t ch) {
        return ch >= MIN_LOW_SURROGATE && ch < (MAX_LOW_SURROGATE + 1);
    }

    static inline u_int32_t toCodePoint(u_int16_t high, u_int16_t low) {
        // Optimized form of:
        // return ((high - MIN_HIGH_SURROGATE) << 10)
        //         + (low - MIN_LOW_SURROGATE)
        //         + MIN_SUPPLEMENTARY_CODE_POINT;
        return ((high << 10) + low) + (MIN_SUPPLEMENTARY_CODE_POINT
                                       - (MIN_HIGH_SURROGATE << 10)
                                       - MIN_LOW_SURROGATE);
    }

    static inline void utf16_convert_to_utf8_string(u_int32_t codePoint, std::string& utf8){
        if (codePoint <= 0x7F)
        {
            // Plain single-byte ASCII.
            utf8.push_back((char)codePoint);
        }
        else if (codePoint <= 0x7FF)
        {
            // Two bytes.
            utf8.push_back(0xC0 | (codePoint >> 6));
            utf8.push_back(0x80 | ((codePoint >> 0) & 0x3F));
        }
        else if (codePoint <= 0xFFFF)
        {
            // Three bytes.
            utf8.push_back(0xE0 | (codePoint >> 12));
            utf8.push_back((0x80 | ((codePoint >> 6) & 0x3F)));
            utf8.push_back((0x80 | ((codePoint >> 0) & 0x3F)));
        }
        else if (codePoint <= 0x1FFFFF)
        {
            // Four bytes.
            utf8.push_back(0xF0 | (codePoint >> 18));
            utf8.push_back(0x80 | ((codePoint >> 12) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 6) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 0) & 0x3F));
        }
        else if (codePoint <= 0x3FFFFFF)
        {
            // Five bytes.
            utf8.push_back(0xF8 | (codePoint >> 24));
            utf8.push_back(0x80 | ((codePoint >> 18) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 12) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 6) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 0) & 0x3F));
        }
        else if (codePoint  <= 0x7FFFFFFF)
        {
            // Six bytes.
            utf8.push_back(0xFC | (codePoint >> 30));
            utf8.push_back(0x80 | ((codePoint >> 24) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 18) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 12) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 6) & 0x3F));
            utf8.push_back(0x80 | ((codePoint >> 0) & 0x3F));
        }else{
            // error unhandle code
            printf("error handle code  %d \n", codePoint);
        }
    }

    static inline int utf16_convert_to_utf8_cstr(u_int32_t codePoint, char* utf8){
        if (codePoint <= 0x7F)
        {
            // Plain single-byte ASCII.
            utf8[0] = ((char)codePoint);
            return 1;
        }
        else if (codePoint <= 0x7FF)
        {
            // Two bytes.
            utf8[0] = (0xC0 | (codePoint >> 6));
            utf8[1] = (0x80 | ((codePoint >> 0) & 0x3F));
            return 2;
        }
        else if (codePoint <= 0xFFFF)
        {
            // Three bytes.
            utf8[0] = (0xE0 | (codePoint >> 12));
            utf8[1] = ((0x80 | ((codePoint >> 6) & 0x3F)));
            utf8[2] = ((0x80 | ((codePoint >> 0) & 0x3F)));
            return 3;
        }
        else if (codePoint <= 0x1FFFFF)
        {
            // Four bytes.
            utf8[0] = (0xF0 | (codePoint >> 18));
            utf8[1] = (0x80 | ((codePoint >> 12) & 0x3F));
            utf8[2] = (0x80 | ((codePoint >> 6) & 0x3F));
            utf8[3] = (0x80 | ((codePoint >> 0) & 0x3F));
            return 4;
        }
        else if (codePoint <= 0x3FFFFFF)
        {
            // Five bytes.
            utf8[0] = (0xF8 | (codePoint >> 24));
            utf8[1] = (0x80 | ((codePoint >> 18) & 0x3F));
            utf8[2] = (0x80 | ((codePoint >> 12) & 0x3F));
            utf8[3] = (0x80 | ((codePoint >> 6) & 0x3F));
            utf8[4] = (0x80 | ((codePoint >> 0) & 0x3F));
            return 5;
        }
        else if (codePoint  <= 0x7FFFFFFF)
        {
            // Six bytes.
            utf8[0] = (0xFC | (codePoint >> 30));
            utf8[1] = (0x80 | ((codePoint >> 24) & 0x3F));
            utf8[2] = (0x80 | ((codePoint >> 18) & 0x3F));
            utf8[3] = (0x80 | ((codePoint >> 12) & 0x3F));
            utf8[4] = (0x80 | ((codePoint >> 6) & 0x3F));
            utf8[5] = (0x80 | ((codePoint >> 0) & 0x3F));
            return 6;
        }else{
            // error unhandle code
            printf("error handle code  %d \n", codePoint);
        }
        return 0;
    }

    void utf16_convert_to_utf8_string(uint16_t * utf16, int length, std::string& utf8){
        for(int i=0; i<length; i++){
            u_int32_t  codePoint = utf16[i];
            if(isHighSurrogate(utf16[i])){
                i++;
                if(i < length){
                    u_int16_t c2 = utf16[i];
                    if (isLowSurrogate(c2)) {
                        codePoint =  toCodePoint(utf16[i-1], c2);
                    }else{
                        i--;
                    }
                }
            }
            utf16_convert_to_utf8_string(codePoint, utf8);
        }
    }

    void utf16_convert_to_utf8_quote_string(uint16_t * utf16, int length, std::string& utf8){
        utf8.push_back('"');
        for(int i=0; i<length; i++){
            u_int32_t  codePoint = utf16[i];
            if(isHighSurrogate(utf16[i])){
                i++;
                if(i < length){
                    u_int16_t c2 = utf16[i];
                    if (isLowSurrogate(c2)) {
                        codePoint =  toCodePoint(utf16[i-1], c2);
                    }else{
                        i--;
                    }
                }
            }
            if(codePoint < 0x7F){
                if(codePoint == '"' || codePoint == '\\'){
                    utf8.push_back('\\');
                }else{
                    if(codePoint <= 0x1F){
                        if(codePoint == '\t' || codePoint == '\r' || codePoint == '\n' || codePoint == '\f' || codePoint == '\b'){
                            utf8.push_back('\\');
                        }
                    }
                }
            }
            utf16_convert_to_utf8_string(codePoint, utf8);
        }
        utf8.push_back('"');
    }

    char* utf16_convert_to_utf8_cstr(uint16_t * utf16, int length){
        char* src = (char*)malloc(sizeof(char)*length*3);
        char* utf8 = src;
        for(int i=0; i<length; i++){
            u_int32_t  codePoint = utf16[i];
            if(isHighSurrogate(utf16[i])){
                i++;
                if(i < length){
                    u_int16_t c2 = utf16[i];
                    if (isLowSurrogate(c2)) {
                        codePoint =  toCodePoint(utf16[i-1], c2);
                    }else{
                        i--;
                    }
                }
            }
            utf8 += utf16_convert_to_utf8_cstr(codePoint, utf8);
        }
        *utf8 = '\0';
        return src;
    }


    /** min size is 32 + 1 = 33 */
    inline void number_to_buffer(char* buffer, int32_t num){
        snprintf(buffer, 32,"%d", num);
    }

    /** min size is 64 + 1 = 65 */
    inline void number_to_buffer(char* buffer, float num){
        snprintf(buffer, 64, "%f", num);
    }

    /** min size is 64 + 1 = 65 */
    inline void number_to_buffer(char* buffer, double num){
        snprintf(buffer, 64, "%f", num);
    }

    /** min size is 64 + 1 = 65 */
    inline void number_to_buffer(char* buffer, int64_t num){
        snprintf(buffer, 64, "%lld", num);
    }


    void str_append_number(std::string& str, double  num){
        char src[64 + 2];
        char* buffer = src;
        number_to_buffer(buffer, num);
        str.append(src);
    }

    void str_append_number(std::string& str, float  num){
        char src[64 + 2];
        char* buffer = src;
        number_to_buffer(buffer, num);
        str.append(src);
    }

    void str_append_number(std::string& str, int32_t  num){
        char src[32 + 2];
        char* buffer = src;
        number_to_buffer(buffer, num);
        str.append(src);
    }

    void str_append_number(std::string& str, int64_t  num){
        char src[64 + 2];
        char* buffer = src;
        number_to_buffer(buffer, num);
        str.append(src);
    }


}