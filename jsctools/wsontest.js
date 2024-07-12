
/**
 *  otool -L WsonTest
 *  install_name_tool -change ../../../mydl/build/Release/libmydl.dylib @executable_path/libmydl.dylib myexe
 * install_name_tool -change /System/Library/Frameworks/JavaScriptCore.framework/Versions/A/JavaScriptCore /Users/furture/Library/Developer/Xcode/DerivedData/WebKit-hjorogjvxdhlplcdpkxokomrpghd/Build/Products/Release/JavaScriptCore.framework/Versions/A/JavaScriptCore WsonTest
 
 install_name_tool -change /System/Library/Frameworks/JavaScriptCore.framework/Versions/A/JavaScriptCore ./JavaScriptCore.framework/Versions/A/JavaScriptCore  WsonTest
 *
 */

/**
 * util function for test
 * **/
var console = {};
console.log = log;
console.error = log;
console.warn = log;



function treeEquals(a, b) {
    if(a == null || b == null){
        return a == b;
    }
    if(a == b){
        return true;
    }
    if(Array.isArray(a) && Array.isArray(b)){
        if(a.length != b.length){
            console.log('not equals array length');
            return false;
        }
        for(var index in a){
            if(!treeEquals(a[index], b[index])){
                console.log('not equals index ' + index + "  " + a[index]  + "   " + b[index]);
                return false;
            }
        }
    }
    // Create arrays of property names
    var aProps = Object.getOwnPropertyNames(a);
    var bProps = Object.getOwnPropertyNames(b);

    var largeProps = aProps;
    if(aProps.length < bProps.length){
        largeProps = bProps;
    }
    
    for (var i = 0; i < largeProps.length; i++) {
        var propName = largeProps[i];
        if(!treeEquals(a[propName], b[propName])){
            var bjson = JSON.parse(JSON.stringify(b));
            console.log('not equals' + propName + "  " + a[propName]  + "   " + b[propName] + bjson[propName]
                         + treeEquals(bjson[propName], a[propName])
                         + bjson["media"]  + b["media"] +  b.media);
            return false;
        }
    }
    // If we made it this far, objects
    // are considered equivalent
    return true;
}

var wsonTestSuit = {
        /***
         * type test 
         */
        testDateType : function(){
            var date = new Date();
            var string = JSON.stringify(date);
            var wson = toWson(string);
            var back = parseWson(wson);
            if(string != back){
                quit("testDateType Failed " + string  + "  " + back);
            }else{
                console.log("pass data type test " + string + " back " + back);
            }
        },
        /**
         * test number 
         **/
        testNormal : function(value){
            var wson = toWson(value);
            var back = parseWson(wson);
            if(back !== value){
                quit("testNormalFailed " + value + "  " + back);
            }else{
                console.log("pass testNormal type test " + value + " back " + back);
            }
        },
         /**
         * test json file, should back euqlas with json file
         **/
        testJSONFile : function(fileName){
            var _self = this;
            var str = readFile(fileName);
            var json = JSON.parse(str);
            var wson = toWson(json);
            var back = parseWson(wson);
            if(!treeEquals(json, back)){
                quit("testJSONFileFailed  "  +  fileName + " back \n " );
            }else{
                console.log("pass JSONFile test " + fileName);
            }
        },
         /**
         * test wson file, should back euqlas with json file
         **/
        testWSONFile : function(fileWson,  fileJSON){
            var _self = this;
            var json = readFile(fileJSON);
            var wson = readFile(fileWson, 'binary');
            var back = parseWson(wson);
            var json = JSON.parse(json);
            if(!treeEquals(json, back)){
                quit("testWSONFileFailed  "  +  fileWson + "\n");
            }else{
                console.log("pass WSONFile test " + fileWson);
            }
        },
        /**
         * test number 
         **/
        testNumber : function(number){
            var _self = this;
            _self.testNormal(99999999999999999999999);
            _self.testNormal(99999999999999999);
            _self.testNormal(-10);
            _self.testNormal(0);
            _self.testNormal(-1);
            console.log("pass number type test ");
        },
    
        testString : function(){
            var _self = this;
            _self.testNormal("中国");
            _self.testNormal("hello world");
            _self.testNormal("normal string world");
            console.log("pass string type test ");
        },

        testJSONFileList: function(){
            var _self = this;
            var dir = "/Users/furture/code/pack/java/src/test/resources";
            _self.testJSONFile(dir + "/media.json");
            _self.testJSONFile(dir + "/media2.json");
            _self.testJSONFile(dir + "/middle.json");
            _self.testJSONFile(dir + "/weex.json");


            _self.testJSONFile(dir + "/bug/bigNumber.json");
            _self.testJSONFile(dir + "/bug/bugintdouble.json");
        },
        testWSONFileList:function(){
            var _self = this;
            var dir = "/Users/furture/code/pack/java/src/test/resources";
            /** */
            _self.testWSONFile(dir + "/media.wson", dir + "/media.json");
         
            _self.testWSONFile(dir + "/media2.wson" ,dir + "/media2.json");
            _self.testWSONFile(dir + "/middle.wson", dir + "/middle.json");
            _self.testWSONFile(dir + "/weex.wson", dir + "/weex.json");

            _self.testWSONFile(dir + "/bug/bigNumber.wson", dir + "/bug/bigNumber.json");
            _self.testWSONFile(dir + "/bug/bugintdouble.wson", dir + "/bug/bugintdouble.json");
            _self.testWSONFile(dir + "/data/2.wson", dir + "/data/2.json");
            _self.testWSONFile(dir + "/data/epub.wson", dir + "/data/epub.json");
            _self.testWSONFile(dir + "/data/group.wson", dir + "/data/group.json");
            _self.testWSONFile(dir + "/data/int_100.wson", dir + "/data/int_100.json");
            _self.testWSONFile(dir + "/data/int_500.wson", dir + "/data/int_500.json");
            _self.testWSONFile(dir + "/data/int_1000.wson", dir + "/data/int_1000.json");
            _self.testWSONFile(dir + "/data/int_10000.wson", dir + "/data/int_10000.json");
            _self.testWSONFile(dir + "/data/int_array_100.wson", dir + "/data/int_array_100.json");
            _self.testWSONFile(dir + "/data/int_array_200.wson", dir + "/data/int_array_200.json");
            _self.testWSONFile(dir + "/data/int_array_500.wson", dir + "/data/int_array_500.json");
            _self.testWSONFile(dir + "/data/int_array_1000.wson", dir + "/data/int_array_1000.json");
            _self.testWSONFile(dir + "/data/int_array_10000.wson", dir + "/data/int_array_10000.json");
            _self.testWSONFile(dir + "/data/maiksagill.wson", dir + "/data/maiksagill.json");
            _self.testWSONFile(dir + "/data/object_f_emptyobj_10000.wson", dir + "/data/object_f_emptyobj_10000.json");
            _self.testWSONFile(dir + "/data/object_f_false_10000.wson", dir + "/data/object_f_false_10000.json");
            _self.testWSONFile(dir + "/data/object_f_int_1000.wson", dir + "/data/object_f_int_1000.json");
            _self.testWSONFile(dir + "/data/object_f_int_10000.wson", dir + "/data/object_f_int_10000.json");
            _self.testWSONFile(dir + "/data/object_f_null_10000.wson", dir + "/data/object_f_null_10000.json");
            _self.testWSONFile(dir + "/data/object_f_string_10000.wson", dir + "/data/object_f_string_10000.json");
            _self.testWSONFile(dir + "/data/object_f_true_10000.wson", dir + "/data/object_f_true_10000.json");
            _self.testWSONFile(dir + "/data/page_model_cached.wson", dir + "/data/page_model_cached.json");
            _self.testWSONFile(dir + "/data/string_array_10000.wson", dir + "/data/string_array_10000.json");
            _self.testWSONFile(dir + "/data/trade.wson", dir + "/data/trade.json");
            _self.testWSONFile(dir + "/data/monitor.wson", dir + "/data/monitor.json");
            _self.testWSONFile(dir + "/data/wuyexiong.wson", dir + "/data/wuyexiong.json");
            _self.testWSONFile(dir + "/data/glossary.wson", dir + "/data/glossary.json");
            _self.testWSONFile(dir + "/data/menu.wson", dir + "/data/menu.json");
            _self.testWSONFile(dir + "/data/webapp.wson", dir + "/data/webapp.json");
            _self.testWSONFile(dir + "/data/widget.wson", dir + "/data/widget.json");
            _self.testWSONFile(dir + "/data/booleans.wson", dir + "/data/booleans.json");
            _self.testWSONFile(dir + "/data/floats.wson", dir + "/data/floats.json");
            _self.testWSONFile(dir + "/data/guids.wson", dir + "/data/guids.json");
            _self.testWSONFile(dir + "/data/integers.wson", dir + "/data/integers.json");
            _self.testWSONFile(dir + "/data/mixed.wson", dir + "/data/mixed.json");
            _self.testWSONFile(dir + "/data/nulls.wson", dir + "/data/nulls.json");
            _self.testWSONFile(dir + "/data/paragraphs.wson", dir + "/data/paragraphs.json");




            _self.testWSONFile(dir + "/taobao/aggregation.wson", dir + "/taobao/aggregation.json");
            _self.testWSONFile(dir + "/taobao/airborne.wson", dir + "/taobao/airborne.json");
            _self.testWSONFile(dir + "/taobao/airborne2.wson", dir + "/taobao/airborne2.json");
            _self.testWSONFile(dir + "/taobao/amp.wson", dir + "/taobao/amp.json");
            _self.testWSONFile(dir + "/taobao/appsearch.wson", dir + "/taobao/appsearch.json");
            _self.testWSONFile(dir + "/taobao/appsearch2.wson", dir + "/taobao/appsearch2.json");
            _self.testWSONFile(dir + "/taobao/avengers.wson", dir + "/taobao/avengers.json");
            _self.testWSONFile(dir + "/taobao/beenhive.wson", dir + "/taobao/beenhive.json");
            _self.testWSONFile(dir + "/taobao/biji.wson", dir + "/taobao/biji.json");
            _self.testWSONFile(dir + "/taobao/cloudvideo.wson", dir + "/taobao/cloudvideo.json");
            _self.testWSONFile(dir + "/taobao/config.wson", dir + "/taobao/config.json");
            _self.testWSONFile(dir + "/taobao/contract.wson", dir + "/taobao/contract.json");
            _self.testWSONFile(dir + "/taobao/cybertron.wson", dir + "/taobao/cybertron.json");
            _self.testWSONFile(dir + "/taobao/detail.wson", dir + "/taobao/detail.json");
            _self.testWSONFile(dir + "/taobao/detail2.wson", dir + "/taobao/detail2.json");
            _self.testWSONFile(dir + "/taobao/gooditem.wson", dir + "/taobao/gooditem.json");
            _self.testWSONFile(dir + "/taobao/gooditem2.wson", dir + "/taobao/gooditem2.json");
            _self.testWSONFile(dir + "/taobao/graphql.wson", dir + "/taobao/graphql.json");
            _self.testWSONFile(dir + "/taobao/guang.wson", dir + "/taobao/guang.json");
            _self.testWSONFile(dir + "/taobao/guang1.wson", dir + "/taobao/guang1.json");
            _self.testWSONFile(dir + "/taobao/guess.wson", dir + "/taobao/guess.json");
            _self.testWSONFile(dir + "/taobao/home.wson", dir + "/taobao/home.json");
            _self.testWSONFile(dir + "/taobao/location.wson", dir + "/taobao/location.json");
            _self.testWSONFile(dir + "/taobao/mclaren.wson", dir + "/taobao/mclaren.json");
            _self.testWSONFile(dir + "/taobao/meiri.wson", dir + "/taobao/meiri.json");
            _self.testWSONFile(dir + "/taobao/mytaobao.wson", dir + "/taobao/mytaobao.json");
            _self.testWSONFile(dir + "/taobao/navigation.wson", dir + "/taobao/navigation.json");
            _self.testWSONFile(dir + "/taobao/orange.wson", dir + "/taobao/orange.json");
            _self.testWSONFile(dir + "/taobao/orange2.wson", dir + "/taobao/orange2.json");


            _self.testWSONFile(dir + "/taobao/order.wson", dir + "/taobao/order.json");
            _self.testWSONFile(dir + "/taobao/orderlist.wson", dir + "/taobao/orderlist.json");

            _self.testWSONFile(dir + "/taobao/qiangdan.wson", dir + "/taobao/qiangdan.json");
            _self.testWSONFile(dir + "/taobao/qianggou1.wson", dir + "/taobao/qianggou1.json");
            _self.testWSONFile(dir + "/taobao/qianggou2.wson", dir + "/taobao/qianggou2.json");
            _self.testWSONFile(dir + "/taobao/qianggou3.wson", dir + "/taobao/qianggou3.json");
            _self.testWSONFile(dir + "/taobao/qianggou4.wson", dir + "/taobao/qianggou4.json");
            _self.testWSONFile(dir + "/taobao/qianqiu1.wson", dir + "/taobao/qianqiu1.json");
            _self.testWSONFile(dir + "/taobao/qianqiu2.wson", dir + "/taobao/qianqiu2.json");
            _self.testWSONFile(dir + "/taobao/qingdan3.wson", dir + "/taobao/qingdan3.json");
            _self.testWSONFile(dir + "/taobao/quanqiu.wson", dir + "/taobao/quanqiu.json");
            _self.testWSONFile(dir + "/taobao/querybought.wson", dir + "/taobao/querybought.json");
            _self.testWSONFile(dir + "/taobao/range.wson", dir + "/taobao/range.json");
            _self.testWSONFile(dir + "/taobao/recentcontact.wson", dir + "/taobao/recentcontact.json");
            _self.testWSONFile(dir + "/taobao/refresh.wson", dir + "/taobao/refresh.json");
            _self.testWSONFile(dir + "/taobao/searchmall.wson", dir + "/taobao/searchmall.json");


            _self.testWSONFile(dir + "/taobao/secondfloor.wson", dir + "/taobao/secondfloor.json");
            _self.testWSONFile(dir + "/taobao/serviceload.wson", dir + "/taobao/serviceload.json");
            _self.testWSONFile(dir + "/taobao/steins.wson", dir + "/taobao/steins.json");
            _self.testWSONFile(dir + "/taobao/steins3.wson", dir + "/taobao/steins3.json");
            _self.testWSONFile(dir + "/taobao/story.wson", dir + "/taobao/story.json");
            _self.testWSONFile(dir + "/taobao/suggest.wson", dir + "/taobao/suggest.json");

            _self.testWSONFile(dir + "/taobao/tejie.wson", dir + "/taobao/tejie.json");
            _self.testWSONFile(dir + "/taobao/time.wson", dir + "/taobao/time.json");
            _self.testWSONFile(dir + "/taobao/userinfo.wson", dir + "/taobao/userinfo.json");
            _self.testWSONFile(dir + "/taobao/video.wson", dir + "/taobao/video.json");
            _self.testWSONFile(dir + "/taobao/wsearch.wson", dir + "/taobao/wsearch.json");




        },
        testBugFile :function(){
            var _self = this;
            _self.testWSONFile(dir + "/data/Bug_2_Test.wson", dir + "/data/Bug_2_Test.json");
            _self.testWSONFile(dir + "/data/Bug_0_Test.wson", dir + "/data/Bug_0_Test.json");
            _self.testWSONFile(dir + "/data/json.wson", dir + "/data/json.json");
        },
        testWSONJSONBenchmarkOneFile : function(fileName){
            var _self = this;
            var json = readFile(fileName);
            console.log("WSONJSONBenchmarkOneFile " +  fileName);
            wsonJsonBenchmark(json);
        },
        testWSONJSONBenchmark : function(){
             var _self = this;
             var dir = "/Users/furture/code/pack/java/src/test/resources";
            _self.testWSONJSONBenchmarkOneFile(dir + "/media.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/media2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/middle.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/weex.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/group.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/epub.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/int_100.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/int_500.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/int_array_100.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/int_array_500.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/maiksagill.json");
            
            
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/page_model_cached.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/object_f_string_10000.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/object_f_null_10000.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/string_array_10000.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/trade.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/monitor.json");


            _self.testWSONJSONBenchmarkOneFile(dir + "/data/json.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/wuyexiong.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/glossary.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/menu.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/webapp.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/widget.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/booleans.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/floats.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/guids.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/integers.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/mixed.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/nulls.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/data/paragraphs.json");



            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/aggregation.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/airborne.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/airborne2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/amp.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/appsearch.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/appsearch2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/avengers.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/beenhive.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/biji.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/cloudvideo.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/config.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/contract.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/cybertron.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/detail.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/detail2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/gooditem.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/gooditem2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/graphql.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/guang.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/guang1.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/guess.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/home.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/location.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/mclaren.json");
            _self.testWSONJSONBenchmarkOneFile( dir + "/taobao/meiri.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/mytaobao.json");
            _self.testWSONJSONBenchmarkOneFile( dir + "/taobao/navigation.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/orange.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/orange2.json");


            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/order.json");
            _self.testWSONJSONBenchmarkOneFile( dir + "/taobao/orderlist.json");

            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qiangdan.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qianggou1.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qianggou2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qianggou3.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qianggou4.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qianqiu1.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qianqiu2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/qingdan3.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/quanqiu.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/querybought.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/range.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/recentcontact.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/refresh.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/searchmall.json");


            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/secondfloor.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/serviceload.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/steins.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/steins3.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/story.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/suggest.json");

            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/tejie.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/time.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/userinfo.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/video.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/wsearch.json");
        },
        testWSONJSONBenchmarkFailed : function(){
            var _self = this;
            var dir = "/Users/furture/code/pack/java/src/test/resources";
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/searchmall.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/mytaobao.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/home.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/guess.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/appsearch2.json");
            _self.testWSONJSONBenchmarkOneFile(dir + "/taobao/appsearch.json");
        },
}

wsonInit();
function runWsonTestSuit(){
    console.log("runing runWsonTestSuit start");
    var start = new Date().getTime();
    wsonTestSuit.testDateType();
    wsonTestSuit.testNumber();
    wsonTestSuit.testString();
    wsonTestSuit.testJSONFileList();
    wsonTestSuit.testWSONFileList();
    wsonTestSuit.testWSONJSONBenchmark();
    //wsonTestSuit.testWSONJSONBenchmarkFailed();
    var end = new Date().getTime();
    console.log("runing runWsonTestSuit end used " + (end - start)  + "ms");
}
runWsonTestSuit();
wsonDestroy();
"all test suit pase"
