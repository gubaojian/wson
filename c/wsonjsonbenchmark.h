#ifndef wsonjsonbenchmark_h
#define wsonjsonbenchmark_h


#ifdef  __ANDROID__
#else
   #define LOGE(...)  printf(__VA_ARGS__)
#endif

//#define BENCH_WSON_VS_JSON true

#ifdef BENCH_WSON_VS_JSON

static double now_ms(void) {
    struct timespec res;
    clock_gettime(CLOCK_REALTIME, &res);
    return 1000.0 * res.tv_sec + (double) res.tv_nsec / 1e6;
}

static  void benchWsonVsJson(ExecState* exec, JSValue val){
    String json = JSONStringify(exec, val, 0);
    #ifdef  __ANDROID__
      LOGE("benchWsonVsJson content %s \n", json.utf8().data());
   #endif
    double start = now_ms();
    for(int i=0; i<1000; i++){
        json = JSONStringify(exec, val, 0);
    }
    double end = now_ms();
    LOGE("benchWsonVsJson json JSONStringify used %f ms \n", (end - start));

    
    wson_buffer* buffer = NULL;
    buffer = wson::toWson(exec, val);
    buffer->length = buffer->position;
    buffer->position = 0;
    JSValue wsonVal = wson::toJSValue(exec, buffer);
    wson_buffer_free(buffer);
    
    start = now_ms();
    for(int i=0; i<1000; i++){
        buffer = wson::toWson(exec, wsonVal);
        wson_buffer_free(buffer);
    }
    end = now_ms();
    LOGE("benchWsonVsJson wson wson::toWson used %f ms \n", (end - start));
    buffer = wson::toWson(exec, val);

    start = now_ms();
    for(int i=0; i<1000; i++){
        JSONParse(exec, json);
    }
    end = now_ms();
    LOGE("benchWsonVsJson json JSONParse used %f ms \n", (end - start));


    start = now_ms();
    for(int i=0; i<1000; i++){
        wson::toJSValue(exec, buffer->data, buffer->position);
    }
    end = now_ms();
    LOGE("benchWsonVsJson wson wson::toJSValue used %f ms \n", (end - start));
}

#endif

#endif
