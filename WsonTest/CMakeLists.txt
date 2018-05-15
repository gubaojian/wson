cmake_minimum_required(VERSION 3.8)
project(WsonTest)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES main.cpp wson/wson.h wson/wson.c wson/wson_parser.h wson/wson_parser.cpp wson/utf16.cpp wson/utf16.h WsonTest.cpp WsonTest.h)
add_executable(WsonTest ${SOURCE_FILES})


add_executable(utf16Text wson/utf16.cpp utf16_test.cpp)


add_executable(wsonParserTest  FileUtils.cpp wson/utf16.cpp wson/wson_parser.cpp wson/wson.c wson_parser_test.cpp)