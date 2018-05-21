var console = {};
console.log = log;
console.error = log;
console.warn = log;


console.log(Proxy);
console.log(Proxy.toString());
var b = {};
var a = new Proxy(b, {
    get: function get (target, methodName) {
        if (methodName in target) {
            return target[methodName]
          }
          console.warn("[JS Framework] using unregistered method");
    }
});

b.cc;