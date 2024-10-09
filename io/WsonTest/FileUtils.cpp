//
// Created by furture on 2018/5/15.
//

#include "FileUtils.h"
#include <stdio.h>
#include <stdlib.h>

char* FileUtils::readFile(char const *fileName) {
    FILE* fp;
    fp = fopen(fileName, "r");
    if(!fp){
        printf("error open %s \n", fileName);
        return NULL;
    }

    fseek(fp, 0, SEEK_END);
    long length = ftell(fp);
    fseek(fp, 0, SEEK_SET);
    char* buffer = (char*)malloc(sizeof(char)*length);
    while(length > 0){
        int read = fread(buffer, sizeof(char), length, fp);
        length -=read;
    }
    fclose(fp);
    return buffer;
}