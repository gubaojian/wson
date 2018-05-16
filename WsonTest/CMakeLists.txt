cmake_minimum_required(VERSION 3.8)
project(WsonTest)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES main.cpp wson/wson.h wson/wson.c wson/wson_parser.h wson/wson_parser.cpp wson/wson_util.cpp wson/wson_util.h WsonTest.cpp WsonTest.h)
add_executable(WsonTest ${SOURCE_FILES})


add_executable(utf16Text wson/wson_util.cpp bench.cpp utf16_test.cpp)


add_executable(wsonParserTest  FileUtils.cpp wson/wson_util.cpp wson/wson_parser.cpp wson/wson.c wson_parser_test.cpp)