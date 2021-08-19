import axios from "@/libs/api.request";

/**
 * 分页获取列表
 */

export const getSoruceList = ({ pageNo, pageSize }) => {
  return axios.request({
    url: "/router/page?pageNo=" + pageNo + "&pageSize=" + pageSize,
    method: "post",
    data: {}
  });
};

/**
 * 新增/修改数据源
 */
export const saveSoruce = data => {
  return axios.request({
    url: "/server/save",
    data,
    method: "post"
  });
};
/**
 * 删除数据源
 */
export const delSoruce = ids => {
  ids = ids || [];
  return axios.request({
    url: "/server/delete?ids=" + ids.join(","),
    method: "post"
  });
};

/**
 * 测试数据源连接
 */
export const testSoruce = data => {
  return axios.request({
    url: "/server/testConnection",
    method: "post",
    data
  });
};
