//
//  main.m
//  WsonParser
//
//  Created by furture on 2018/5/30.
//  Copyright © 2018年 furture. All rights reserved.
//

#import <Foundation/Foundation.h>
#include "WsonParser.h"


void test_wson(id val){
    NSLog(@"val %@ ", val);
    wson_buffer* buffer = [WsonParser toWson:val];
    buffer->position = 0;
    id back  = [WsonParser toVal:buffer];
    NSLog(@"back %@ ", back);
    
    wson_buffer_free(buffer);
}

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        NSString* string = [NSString stringWithUTF8String:"中国"];
        test_wson(string);
        test_wson([NSNumber numberWithDouble:10]);
        test_wson([NSNumber numberWithInt:20]);
        test_wson([NSNumber numberWithFloat:10.01f]);
        test_wson([NSNumber numberWithChar:'c']);
        test_wson([NSDictionary dictionaryWithObjectsAndKeys: @"hello world", @"name", nil]);
        NSDate* date = [NSDate dateWithTimeIntervalSinceNow:0];
        NSNumber* time = [NSNumber numberWithLongLong:[date timeIntervalSince1970]*1000];
        NSDictionary* map = [NSDictionary dictionaryWithObjectsAndKeys: @"hello world", @"name", time, @"time",
                             [NSValue valueWithRect:NSMakeRect(0,0, 100, 100)], @"rect",nil];
        test_wson(map);
        
        NSArray* array = [NSArray arrayWithObjects:map,  date, nil];
        
         test_wson(array);
    }
    return 0;
}
