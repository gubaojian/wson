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

static double now_ms(void) {
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000.0 * res.tv_sec + (double) res.tv_nsec / 1e6;
}


void test_bechmark(id val){
    
    NSData* fileData = [NSData dataWithContentsOfFile:@"/Users/furture/code/pack/java/src/test/resources/media2.json"];
    NSDictionary* json = [NSJSONSerialization JSONObjectWithData:fileData options:0 error:nil];
    
    double start = now_ms();
    for(int i=0; i<1000; i++){
        [NSJSONSerialization dataWithJSONObject:json options:0 error:nil];
    }
    NSLog(@"json serial used %f", (now_ms() - start));
    
    
    start = now_ms();
    for(int i=0; i<1000; i++){
       wson_buffer* buffer =  [WsonParser toWson:json];
        wson_buffer_free(buffer);
    }
    NSLog(@"wson serial used %f", (now_ms() - start));
    
    NSData* data  = [NSJSONSerialization dataWithJSONObject:json options:0 error:nil];
    
    start = now_ms();
    for(int i=0; i<1000; i++){
       [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    }
    NSLog(@"json parse used %f", (now_ms() - start));
    
    wson_buffer* buffer =  [WsonParser toWson:json];
    start = now_ms();
    for(int i=0; i<1000; i++){
        buffer->position =0;
        [WsonParser toVal:buffer];
    }
    NSLog(@"wson parse used %f", (now_ms() - start));
    buffer->position =0;
    NSLog(@"wson parse used %@",  [WsonParser toVal:buffer]);
    wson_buffer_free(buffer);
    //NSData* data = [string dataUsingEncoding:NSUTF8StringEncoding];
    //[NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
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
        
        test_bechmark(nil);
        
        
        
        
        
        
    }
    return 0;
}
