//
// Created by furture on 2018/5/16.
//

#ifndef WSONTEST_BENCH_H
#define WSONTEST_BENCH_H

#include <time.h>

namespace bench{

   inline double now_ms(void) {
        struct timespec res;
        clock_gettime(CLOCK_REALTIME, &res);
        return 1000.0 * res.tv_sec + (double) res.tv_nsec / 1e6;
    }

}



#endif //WSONTEST_BENCH_H
