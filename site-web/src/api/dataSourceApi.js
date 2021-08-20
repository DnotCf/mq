import axios from "@/libs/api.request";
let dataSourceApi = {};
export default dataSourceApi;

/**
 * 分页获取列表
 */

dataSourceApi.getSoruceList = ({ pageNo, pageSize }) => {
  return axios.request({
    url: "/router/page?pageNo=" + pageNo + "&pageSize=" + pageSize,
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

