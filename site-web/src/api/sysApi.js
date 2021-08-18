import axios from '@/libs/api.request'

/**
 * 获取字典值
 */
export const getDict = (dict,tree,pid,ajax) => {
  return axios.request({
    url: '/sys/dict/load?type='+dict+"&pid="+pid+"&sync="+(ajax?"true":"false")+"&tree="+(tree?"true":"false"),
    method: 'get'
  });
}
