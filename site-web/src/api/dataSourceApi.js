import axios from "@/libs/api.request";

/**
 * 分页获取列表
 */

export const getSoruceList = ({ pageNo, pageSize }) => {
  return axios.request({
    url: "/analysisTemplate/page?pageNo=" + pageNo + "&pageSize=" + pageSize,
    method: "post"
  });
};


/**
 * 新增修改数据源
 */
 export const saveSoruce = (data) => {
    return axios.request({
      url: '/server/save',
      data,
      method: 'post'
    })
  }
