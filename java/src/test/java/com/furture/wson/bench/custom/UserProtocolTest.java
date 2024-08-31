package com.furture.wson.bench.custom;

import com.furture.wson.domain.User;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class UserProtocolTest  extends TestCase {

    @Test
    public void testUserProtocol() {
        User user = new User();
        user.name = "hello world";
        user.country = "中国";
        byte[] bts = UserProtocol.serialUser(user);
        User back = UserProtocol.deSerialUser(bts);
        System.out.println(back  + " " + user);
        Assert.assertEquals(user, back);
    }
}
