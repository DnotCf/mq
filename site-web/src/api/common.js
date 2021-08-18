import axios from '@/libs/api.request'


/**
 * 从指定URL获取数据
 * 
 */
export const getDataFromUrl = (url,method) => {
  return axios.request({
    url: url,
    method: method || 'get'
  });
}

/**
 * 从指定URL获取数据
 * 
 */
export const postDataToUrl = (url,data,method) => {
  return axios.request({
    url: url,
    data:data,
    method: method || 'post'
  });
}