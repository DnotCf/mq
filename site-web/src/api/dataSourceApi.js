import axios from "@/libs/api.request";
let dataSourceApi = {};
export default dataSourceApi;
/**
 * 分页获取映射列表
 */

dataSourceApi.getMapList = ({ pageNo, pageSize }) => {
  return axios.request({
    url: "/router/page?pageNo=" + pageNo + "&pageSize=" + pageSize,
    method: "post",
    data: {}
  });
};
/**
 * 分页获取数据源列表
 */

dataSourceApi.getSoruceList = ({ pageNo, pageSize }) => {
  return axios.request({
    url: "/server/page?pageNo=" + pageNo + "&pageSize=" + pageSize,
    method: "post",
    data: {}
  });
};
/**
 * 新增/修改数据源
 */
dataSourceApi.saveSoruce = data => {
  return axios.request({
    url: "/server/save",
    data,
    method: "post"
  });
};
/**
 * 删除数据源
 */
dataSourceApi.delSoruce = ids => {
  ids = ids || [];
  return axios.request({
    url: "/server/delete?ids=" + ids.join(","),
    method: "post"
  });
};
/**
 * 测试数据源连接
 */
dataSourceApi.testSoruce = data => {
  return axios.request({
    url: "/server/testConnection",
    method: "post",
    data
  });
};
/**
 * 添加映射
 */
dataSourceApi.saveMap = data => {
  return axios.request({
    url: "/router/save",
    method: "post",
    data
  });
};
/**
 * 测试映射连接
 */
dataSourceApi.testMap = data => {
  return axios.request({
    url: "/router/testConnection",
    method: "post",
    data
  });
};
/**
 * 删除映射
 */
dataSourceApi.delMap = ids => {
  ids = ids || [];
  return axios.request({
    url: "/router/delete?ids=" + ids.join(","),
    method: "post"
  });
};

/**
 * 启动所有映射
 */
dataSourceApi.startAll = () => {
  return axios.request({
    url: "/router/startAll",
    method: "get"
  });
};
/**
 * 停用
 */
dataSourceApi.stopAll = () => {
  return axios.request({
    url: "/router/stopAll",
    method: "get"
  });
};
/**
 * 启动单项映射
 */
dataSourceApi.startItem = id => {
  return axios.request({
    url: "/router/start?id=" + id,
    method: "get"
  });
};
/**
 * 停用
 */
dataSourceApi.stopItem = id => {
  return axios.request({
    url: "/router/stop?id=" + id,
    method: "get"
  });
};
