package com.furture.wson.bench.custom;

import com.furture.wson.domain.User;
import com.github.gubaojian.wson.io.Input;
import com.github.gubaojian.wson.io.Pool;
import com.github.gubaojian.wson.io.Output;

/**
 * 对于已只类型数据，自定义协议，双方约定好，简单通用也方便跨语言实现。性能最好, 数据量也最小。
 * 引入的库也小，基本不需要引入任何库，也能根据数据版本兼容及升级。
 * 类似 parcel，只要语言直接约定好即可。
 * 根据网络传输，还是本地性能调用进行有场景的优化，android parcel就不考虑网络传输及持久化。
 * 协议不支持也可以添加方式。
 * FlatBuffer就没考虑压缩。ProtoBuf压缩考虑的多，自定义连tag都不存储，更小，严格按约定的来。
 * https://android.googlesource.com/platform/frameworks/native/+/jb-dev/libs/binder/Parcel.cpp
 * */
public class UserProtocol {

    /*
     * 用LocalBuffer复用缓冲器减少内存分配开销，提升性能。直接性能提升1-2倍
     */
    public static byte[] serialUser(User user) {
        Output output = new Output(Pool.requireBuffer(1024));
        byte version = 1;
        output.writeByte(version);
        output.writeStringUTF8(user.name);
        output.writeStringUTF8(user.country);
        Pool.returnBuffer(output.getBuffer());
        return  output.toBytes();
    }

    public static byte[] serialUser2(User user, LockFreePool<byte[]> pool) {
        PoolObject<byte[]> poolObject = pool.getPoolObject();
        Output output = new Output(poolObject.getObject());
        byte version = 1;
        output.writeByte(version);
        output.writeStringUTF8(user.name);
        output.writeStringUTF8(user.country);
        pool.returnPoolObject(poolObject);
        return output.toBytes();
    }

    public static User deSerialUser(byte[] bts) {
        User user = new User();
        Input input = new Input(bts);
        byte version = input.readByte();
        user.name = input.readStringUTF8();
        user.country = input.readStringUTF8();
        return user;
    }

}
