package com.furture.wson;

import com.alibaba.fastjson2.JSON;
import com.github.gubaojian.wson.Wson;
import com.furture.wson.compatible.CompatibleUtils;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/15.
 */
public class PrepareWsonFile extends TestCase {



    /**
     * prepare wson file
     * */
    public void testPrepareWson() throws IOException {
        String[] files = {"/data.json",
                "/home.json",
                "/middle.json",
                "/tiny.json",
                "/media.json",
                "/media2.json",
                "/weex.json",
                "/weex2.json",
                "/weex3.json",
                "/weex4.json",
                "/weex5.json",
                "/bug/bigNumber.json",
                "/bug/bugintdouble.json",
                "/data/2.json",
                "/data/epub.json",
                "/data/group.json",
                "/data/int_100.json",
                "/data/int_500.json",
                "/data/int_1000.json",
                "/data/int_10000.json",
                "/data/int_array_100.json",
                "/data/int_array_200.json",
                "/data/int_array_500.json",
                "/data/int_array_1000.json",
                "/data/int_array_10000.json",
                "/data/maiksagill.json",
                "/data/object_f_emptyobj_10000.json",
                "/data/object_f_false_10000.json",
                "/data/object_f_int_1000.json",
                "/data/object_f_int_10000.json",
                "/data/object_f_null_10000.json",
                "/data/object_f_string_10000.json",
                "/data/object_f_true_10000.json",
                "/data/page_model_cached.json",
                "/data/string_array_10000.json",
                "/data/trade.json",
                "/data/monitor.json",
                "/data/Bug_2_Test.json",
                "/data/Bug_0_Test.json",
                "/data/json.json",
                "/data/wuyexiong.json",
                "/data/glossary.json",
                "/data/menu.json",
                "/data/sample.json",
                "/data/webapp.json",
                "/data/widget.json",
                "/data/booleans.json",
                "/data/floats.json",
                "/data/guids.json",
                "/data/integers.json",
                "/data/mixed.json",
                "/data/nulls.json",
                "/data/paragraphs.json",



                "/taobao/aggregation.json",
                "/taobao/airborne.json",
                "/taobao/airborne2.json",
                "/taobao/amp.json",
                "/taobao/appsearch.json",
                "/taobao/appsearch2.json",
                "/taobao/avengers.json",
                "/taobao/beenhive.json",
                "/taobao/biji.json",
                "/taobao/cloudvideo.json",
                "/taobao/config.json",
                "/taobao/contract.json",
                "/taobao/cybertron.json",
                "/taobao/detail.json",
                "/taobao/detail2.json",
                "/taobao/gooditem.json",
                "/taobao/gooditem2.json",
                "/taobao/graphql.json",
                "/taobao/guang.json",
                "/taobao/guang1.json",
                "/taobao/guess.json",
                "/taobao/home.json",
                "/taobao/location.json",
                "/taobao/mclaren.json",
                "/taobao/meiri.json",
                "/taobao/mytaobao.json",
                "/taobao/navigation.json",
                "/taobao/orange.json",
                "/taobao/orange2.json",
                "/taobao/order.json",
                "/taobao/orderlist.json",
                "/taobao/qiangdan.json",
                "/taobao/qianggou1.json",
                "/taobao/qianggou2.json",
                "/taobao/qianggou3.json",
                "/taobao/qianggou4.json",
                "/taobao/qianqiu1.json",
                "/taobao/qianqiu2.json",
                "/taobao/qingdan2.json",
                "/taobao/qingdan3.json",
                "/taobao/quanqiu.json",
                "/taobao/querybought.json",
                "/taobao/range.json",



                "/taobao/recentcontact.json",
                "/taobao/refresh.json",
                "/taobao/searchmall.json",
                "/taobao/secondfloor.json",
                "/taobao/serviceload.json",
                "/taobao/steins.json",
                "/taobao/steins3.json",
                "/taobao/story.json",
                "/taobao/suggest.json",

                "/taobao/tejie.json",
                "/taobao/time.json",
                "/taobao/userinfo.json",
                "/taobao/video.json",
                "/taobao/wsearch.json"
        };


        for(String file : files) {
            convertToWson(file);
        }
    }


    public void testPrepareWsonError() throws IOException {
        String[] files = {
                "/taobao/video.json",
        };

        double a = 0;
        System.out.println((0.0 == a) + "  " + Double.toString(0)  + "  "  + Double.toString(0.0));



        for(String file : files) {
            convertToWson(file);
        }
    }


    public void  testShowFormatFile() throws IOException {
        String data = readFile("/data/Bug_2_Test.json");
        Object map = JSON.parse(data);
        System.out.println(JSON.toJSONString(map));


        System.out.println(JSON.toJSONString("𝄞"));

        convertToWson("/plus/parser.json");


        System.out.println(JSON.toJSONString(null));
    }



    public void testShowWsonFileContent() throws IOException {
        byte[] bts = readFileBytes("/data/trade.wson");
        Object object = Wson.parse(bts);
        System.out.println(JSON.toJSONString(object));

    }

    public void  convertToProtobuf(String jsonFile) throws IOException {
        String data = readFile(jsonFile);
        Object map = JSON.parse(data);
        byte[] wson = Wson.toWson(map);
        String wsonFile = "src/test/resources/" + (jsonFile.substring(1, jsonFile.indexOf('.')) + ".wson");
        saveFile(wsonFile, wson);
    }





    public void  convertToWson(String jsonFile) throws IOException {
        String data = readFile(jsonFile);
        try{
            Object map = JSON.parse(data);
            byte[] wson = Wson.toWson(map);
            System.out.println(Wson.parse(wson).toString());
            try{
                Assert.assertEquals(jsonFile + " convert not equals", Wson.parse(wson).toString(), JSON.parse(JSON.toJSONString(map)).toString());
            }catch (Exception e){
                e.printStackTrace();
                CompatibleUtils.checkDiff(map);
            }
            String wsonFile = "src/test/resources/" + (jsonFile.substring(1, jsonFile.indexOf('.')) + ".wson");
            saveFile(wsonFile, wson);
        }catch (Throwable e){
            System.out.println("fileName " + jsonFile);
            e.printStackTrace();
           //String sjsonFile = "src/test/resources/" + (jsonFile.substring(1, jsonFile.indexOf('.')) + ".json");
           //saveFile(sjsonFile, JSON.parseObject(data).toJSONString().getBytes("UTF-8"));
       }
    }

    public void testJsonToWson(){
        String a = "[{\"args\":[\"67\",\"input\",{\"timeStamp\":1542864306658,\"value\":\"\uD83D\uDE33\uD83D\uDE33\"},{\"attrs\":{\"value\":\"\uD83D\uDE33\uD83D\uDE33\"}}],\"method\":\"fireEvent\"}]";
        System.out.println(a);
    }

    private String readFile(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.toByteArray());
    }


    private byte[] readFileBytes(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  outputStream.toByteArray();
    }

    private void saveFile(String file, byte[] bts) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bts);
        outputStream.close();
    }
}
