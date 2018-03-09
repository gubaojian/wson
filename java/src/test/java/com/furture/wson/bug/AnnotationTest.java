package com.furture.wson.bug;

import com.alibaba.fastjson.JSON;
import com.efurture.wson.Wson;
import com.furture.wson.domain.AnnoPerson;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by 剑白(jianbai.gbj) on 2018/3/9.
 */
public class AnnotationTest extends TestCase {


    public void  testAnnoatation(){
        AnnoPerson person = new AnnoPerson();
        person.id = "2222";
        Assert.assertEquals(JSON.toJSONString(person), JSON.toJSONString(Wson.parse(Wson.toWson(person))));
    }

}
