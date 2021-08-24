import axios from "axios";
import store from "@/store";
// import { Spin } from 'iview'
/**
 * 网络及系统异常底层统一处理，业务模块在异常部分，只需要进行异常处置，禁止进行弹窗处理。
 * author:linx
 */

export const handleNetError = (url, request) => {
  return new Promise(function(resolve, reject) {
    request
      .then(req => {
        if (req.status === 200) {
          // 如果返回参数加密，则解密参数
          if (req.data.encrypt) {
            req.data.data = encryptRequst.decrypt(req.data.data);
          }
          // 网络请求成功的
          if (req.data.code === 401) {
            // 返回 401 清除token信息并跳转到登录页面
            //store.commit("logout");
            setToken("");
            let msg = req.data.msg;
            if (msg.indexOf("need login") >= 0) {
              msg = "登录超时，请您重新登录";
            }
            if (
              getCookie("repeatError") === "true" ||
              getCookie("repeatError") === true
            ) {
              router.replace({
                path: "login",
                query: {
                  redirect: router.currentRoute.fullPath
                }
              });
              return;
            }
            Modal.error({
              title: "提示",
              content: msg,
              closable: false,
              maskClosable: false,
              onOk: () => {
                router.replace({
                  path: "login",
                  query: {
                    redirect: router.currentRoute.fullPath
                  }
                });
              }
            });
            setCookie("repeatError", true);
          } else if (req.data.code === 200) {
            //处理业务码,处理5XX开头的，为系统异常，非业务处理异常
            // 系统错误 500,400等
            resolve(req.data);
          } else if (req.data.code == "-1") {
            resolve(req.data);
          } else {
            printServerError(req.data, url);
            reject({
              msg: "业务代码异常"
            });
          }
        } else {
          if (req.status === 401) {
            // 返回 401 清除token信息并跳转到登录页面
            //store.commit("logout");
            setToken("");
            Modal.error({
              title: "提示",
              content: "登录超时，请您重新登录",
              closable: false,
              maskClosable: false,
              onOk: () => {
                router.replace({
                  path: "login",
                  query: {
                    redirect: router.currentRoute.fullPath
                  }
                });
              }
            });
          } else {
            // 系统错误 500,400等
            reject({
              msg: req.status
            });
          }
        }
      })
      .catch(err => {
        // 系统错误 500,400等
        if (err && err.response && err.response.data) {
          printServerError(err.response.data, url);
        } else {
          console.error("error at api : " + url);
          console.error(err);
        }
        reject(err);
      });
  });
};
const printServerError = (err, url) => {
  console.error("error at api : " + url);
  console.error("code:" + err.code);
  console.error("message:" + err.msg);
  console.error(err.data);
};

const addErrorLog = errorInfo => {
  const {
    statusText,
    status,
    request: { responseURL }
  } = errorInfo;
  let info = {
    type: "ajax",
    code: status,
    mes: statusText,
    url: responseURL
  };
  if (!responseURL.includes("save_error_logger"))
    store.dispatch("addErrorLog", info);
};

class HttpRequest {
  constructor(baseUrl = baseURL) {
    this.baseUrl = baseUrl;
    this.queue = {};
  }
  getInsideConfig() {
    const config = {
      baseURL: this.baseUrl,
      headers: {
        //
      }
    };
    return config;
  }
  destroy(url) {
    delete this.queue[url];
    if (!Object.keys(this.queue).length) {
      // Spin.hide()
    }
  }
  interceptors(instance, url) {
    // 请求拦截
    instance.interceptors.request.use(
      config => {
        // 添加全局的loading...
        if (!Object.keys(this.queue).length) {
          // Spin.show() // 不建议开启，因为界面不友好
        }
        this.queue[url] = true;
        return config;
      },
      error => {
        return Promise.reject(error);
      }
    );
    // 响应拦截
    instance.interceptors.response.use(
      res => {
        this.destroy(url);
        const { data, status } = res;
        return { data, status };
      },
      error => {
        this.destroy(url);
        let errorInfo = error.response;
        if (!errorInfo) {
          const {
            request: { statusText, status },
            config
          } = JSON.parse(JSON.stringify(error));
          errorInfo = {
            statusText,
            status,
            request: { responseURL: config.url }
          };
        }
        addErrorLog(errorInfo);
        return Promise.reject(error);
      }
    );
  }
  request(options) {
    const instance = axios.create();
    options = Object.assign(this.getInsideConfig(), options);
    this.interceptors(instance, options.url);
    return handleNetError(options.url, instance(options));
  }
}
export default HttpRequest;
