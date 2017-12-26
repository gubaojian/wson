cmake_minimum_required(VERSION 3.8)
project(WsonTest)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES main.cpp wson/wson.h wson/wson.c WsonTest.cpp WsonTest.h)
add_executable(WsonTest ${SOURCE_FILES})