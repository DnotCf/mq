import Cookies from 'js-cookie'
// cookie保存的天数
import config from '@/config'
import {
  forEach,
  hasOneOf,
  objEqual
} from '@/libs/tools'

export const TOKEN_KEY = 'token'

export const setCookie = (cookieName, cookieValue) => {
  Cookies.set(cookieName, cookieValue, {
    expires: config.cookieExpires || 1
  })
}

export const getCookie = (cookieName) => {
  const token = Cookies.get(cookieName)
  if (token) return token
  else return null
}

export const setSession = (sessionName, sessionValue) => {
  sessionStorage[sessionName] = sessionValue
}

export const getSession = (sessionName) => {
  const session = sessionStorage[sessionName]
  if (session) return session
  else return null
}

export const setToken = (token) => {
  var millisecond = new Date().getTime()
  var expiresTime = new Date(millisecond + 60 * 1000 * config.tokenExpires)
  Cookies.set(TOKEN_KEY, token, {
    expires: expiresTime
  })
}

export const getToken = () => {
  const token = Cookies.get(TOKEN_KEY)
  if (token === 'null') {
    return null
  }
  if (token) return token
  else return null
}

export const hasChild = (item) => {
  return item.children && item.children.length !== 0
}

const showThisMenuEle = (item, access) => {
  if (item.meta && item.meta.access && item.meta.access.length) {
    if (hasOneOf(item.meta.access, access)) return true
    else return false
  } else return true
}
/**
 * @param {Array} list 通过路由列表得到菜单列表
 * @returns {Array}
 */
export const getMenuByRouter = (list, access) => {
  let res = []
  forEach(list, item => {
    if (!item.meta || (item.meta && !item.meta.hideInMenu)) {
      let obj = {
        icon: (item.meta && item.meta.icon) || '',
        name: item.name,
        meta: item.meta
      }
      if ((hasChild(item) || (item.meta && item.meta.showAlways)) && showThisMenuEle(item, access)) {
        obj.children = getMenuByRouter(item.children, access)
      }
      if (item.meta && item.meta.href) obj.href = item.meta.href
      if (showThisMenuEle(item, access)) res.push(obj)
    }
  })
  return res
}

/**
 * @param {Array} routeMetched 当前路由metched
 * @returns {Array}
 */
export const getBreadCrumbList = (route, homeRoute) => {
  let homeItem = {
    ...homeRoute,
    icon: homeRoute.meta.icon
  }
  let routeMetched = route.matched
  if (routeMetched.some(item => item.name === homeRoute.name)) return [homeItem]
  let res = routeMetched.filter(item => {
    return item.meta === undefined || !item.meta.hide
  }).map(item => {
    let meta = {
      ...item.meta
    }
    if (meta.title && typeof meta.title === 'function') meta.title = meta.title(route)
    let obj = {
      icon: (item.meta && item.meta.icon) || '',
      name: item.name,
      meta: meta
    }
    return obj
  })
  res = res.filter(item => {
    return !item.meta.hideInMenu
  })
  return [{
    ...homeItem,
    to: homeRoute.path
  }, ...res]
}

export const getRouteTitleHandled = (route) => {
  let router = {
    ...route
  }
  let meta = {
    ...route.meta
  }
  let title = ''
  if (meta.title) {
    if (typeof meta.title === 'function') title = meta.title(router)
    else title = meta.title
  }
  meta.title = title
  router.meta = meta
  return router
}

export const showTitle = (item, vm) => {
  let title = item.meta.title
  if (vm.$config.useI18n) {
    if (title.includes('{{') && title.includes('}}') && vm.$config.useI18n) title = title.replace(/({{[\s\S]+?}})/, (m, str) => str.replace(/{{([\s\S]*)}}/, (m, _) => vm.$t(_.trim())))
    else title = vm.$t(item.name)
  } else title = (item.meta && item.meta.title) || item.name
  return title
}

/**
 * @description 本地存储和获取标签导航列表
 */
export const setTagNavListInLocalstorage = list => {
  localStorage.tagNaveList = JSON.stringify(list)
}
/**
 * @returns {Array} 其中的每个元素只包含路由原信息中的name, path, meta三项
 */
export const getTagNavListFromLocalstorage = () => {
  const list = localStorage.tagNaveList
  return list ? JSON.parse(list) : []
}

/**
 * @param {Array} routers 路由列表数组
 * @description 用于找到路由列表中name为home的对象
 */
export const getHomeRoute = (routers, homeName = 'home') => {
  let i = -1
  let len = routers.length
  let homeRoute = {}
  while (++i < len) {
    let item = routers[i]
    if (item.children && item.children.length) {
      let res = getHomeRoute(item.children, homeName)
      if (res.name) return res
    } else {
      if (item.name === homeName) homeRoute = item
    }
  }
  return homeRoute
}

/**
 * @param {*} list 现有标签导航列表
 * @param {*} newRoute 新添加的路由原信息对象
 * @description 如果该newRoute已经存在则不再添加
 */
export const getNewTagList = (list, newRoute) => {
  const {
    name,
    path,
    meta
  } = newRoute
  let newList = [...list]

  if (newList.findIndex(item => item.name === name) >= 0) {
    return newList
  } else {
    newList.push({
      name,
      path,
      meta
    })
    return newList
  }
}

/**
 * @param {*} access 用户权限数组，如 ['super_admin', 'admin']
 * @param {*} route 路由列表
 */
const hasAccess = (access, route) => {
  if (route.meta && route.meta.access) return hasOneOf(access, route.meta.access)
  else return true
}

/**
 * 权鉴
 * @param {*} name 即将跳转的路由name
 * @param {*} access 用户权限数组
 * @param {*} routes 路由列表
 * @description 用户是否可跳转到该页
 */
export const canTurnTo = (name, access, routes) => {
  const routePermissionJudge = (list) => {
    return list.some(item => {
      if (item.children && item.children.length) {
        return routePermissionJudge(item.children)
      } else if (item.name === name) {
        return hasAccess(access, item)
      }
    })
  }

  return routePermissionJudge(routes)
}

/**
 * @param {String} url
 * @description 从URL中解析参数
 */
export const getParams = url => {
  const keyValueArr = url.split('?')[1].split('&')
  let paramObj = {}
  keyValueArr.forEach(item => {
    const keyValue = item.split('=')
    paramObj[keyValue[0]] = keyValue[1]
  })
  return paramObj
}

/**
 * @param {Array} list 标签列表
 * @param {String} name 当前关闭的标签的name
 */
export const getNextRoute = (list, route) => {
  let res = {}
  if (list.length === 2) {
    res = getHomeRoute(list)
    // 解决数据曲线分析页关闭失败没有返回上个路由的问题
    // getHomeRoute返回空的情况,则把res置为list[0] --hcj 12.19
    if (Object.keys(res).length === 0) {
      res = list[0]
    }
  } else {
    const index = list.findIndex(item => routeEqual(item, route))
    if (index === list.length - 1) res = list[list.length - 2]
    else res = list[index + 1]
  }
  if (list.length === 1) {
    res = getHomeRoute(list)
  }
  return res
}

export const getPrevRoute = (list, route) => {
  let res = {}
  if (list.length === 2) {
    res = getHomeRoute(list)
  } else {
    const index = list.findIndex(item => routeEqual(item, route))
    if (index === list.length - 1) res = list[list.length - 2]
    else res = list[index - 1]
  }
  return res
}

/**
 * @param {Number} times 回调函数需要执行的次数
 * @param {Function} callback 回调函数
 */
export const doCustomTimes = (times, callback) => {
  let i = -1
  while (++i < times) {
    callback(i)
  }
}

/**
 * @param {Object} file 从上传组件得到的文件对象
 * @returns {Promise} resolve参数是解析后的二维数组
 * @description 从Csv文件中解析出表格，解析成二维数组
 */
export const getArrayFromFile = (file) => {
  let nameSplit = file.name.split('.')
  let format = nameSplit[nameSplit.length - 1]
  return new Promise((resolve, reject) => {
    let reader = new FileReader()
    reader.readAsText(file) // 以文本格式读取
    let arr = []
    reader.onload = function (evt) {
      let data = evt.target.result // 读到的数据
      let pasteData = data.trim()
      arr = pasteData.split((/[\n\u0085\u2028\u2029]|\r\n?/g)).map(row => {
        return row.split('\t')
      }).map(item => {
        return item[0].split(',')
      })
      if (format === 'csv') resolve(arr)
      else reject(new Error('[Format Error]:你上传的不是Csv文件'))
    }
  })
}

/**
 * @param {Array} array 表格数据二维数组
 * @returns {Object} { columns, tableData }
 * @description 从二维数组中获取表头和表格数据，将第一行作为表头，用于在iView的表格中展示数据
 */
export const getTableDataFromArray = (array) => {
  let columns = []
  let tableData = []
  if (array.length > 1) {
    let titles = array.shift()
    columns = titles.map(item => {
      return {
        title: item,
        key: item
      }
    })
    tableData = array.map(item => {
      let res = {}
      item.forEach((col, i) => {
        res[titles[i]] = col
      })
      return res
    })
  }
  return {
    columns,
    tableData
  }
}

export const findNodeUpper = (ele, tag) => {
  if (ele.parentNode) {
    if (ele.parentNode.tagName === tag.toUpperCase()) {
      return ele.parentNode
    } else {
      return findNodeUpper(ele.parentNode, tag)
    }
  }
}

export const findNodeUpperByClasses = (ele, classes) => {
  let parentNode = ele.parentNode
  if (parentNode) {
    let classList = parentNode.classList
    if (classList && classes.every(className => classList.contains(className))) {
      return parentNode
    } else {
      return findNodeUpperByClasses(parentNode, classes)
    }
  }
}

export const findNodeDownward = (ele, tag) => {
  const tagName = tag.toUpperCase()
  if (ele.childNodes.length) {
    let i = -1
    let len = ele.childNodes.length
    while (++i < len) {
      let child = ele.childNodes[i]
      if (child.tagName === tagName) return child
      else return findNodeDownward(child, tag)
    }
  }
}

export const showByAccess = (access, canViewAccess) => {
  return hasOneOf(canViewAccess, access)
}

/**
 * @description 根据name/params/query判断两个路由对象是否相等
 * @param {*} route1 路由对象
 * @param {*} route2 路由对象
 */
export const routeEqual = (route1, route2) => {
  const params1 = route1.params || {}
  const params2 = route2.params || {}
  const query1 = route1.query || {}
  const query2 = route2.query || {}
  /* ycz */
  if (params2.needOpenMore) {
    /* ycz --2020-05-11
     * objEqual方法只能判断对象的值只能是数字和字符串，
     * 如果直接包含uniqueSign字段，则直接比较这两个想不想等，如果不包含则使用objEqual来判断
     */
    if (params1.uniqueSign !== undefined || params2.uniqueSign !== undefined) {
      return (params1.uniqueSign === params2.uniqueSign)
    } else {
      let res = (route1.name === route2.name) && objEqual(params1, params2) && objEqual(query1, query2)
      return res
    }
  } else {
    return (route1.name === route2.name)
  }
}

/**
 * 判断打开的标签列表里是否已存在这个新添加的路由对象
 */
export const routeHasExist = (tagNavList, routeItem) => {
  let len = tagNavList.length
  let res = false
  doCustomTimes(len, (index) => {
    if (routeEqual(tagNavList[index], routeItem)) res = true
  })
  return res
}

export const localSave = (key, value) => {
  localStorage.setItem(key, value)
}

export const localRead = (key) => {
  return localStorage.getItem(key) || ''
}

export const getFirstMenu = (menus) => {
  if (menus.length > 0) {
    let menu = menus[0]
    if (menu.children && menu.children.length > 0) {
      getFirstMenu(menu.children)
    } else {
      setSession('homeName', menu.name)
    }
  }
}

export const deepClone = (obj) => {
  // 判断拷贝的要进行深拷贝的是数组还是对象，是数组的话进行数组拷贝，对象的话进行对象拷贝
  var objClone = Array.isArray(obj) ? [] : {}
  // 进行深拷贝的不能为空，并且是对象或者是
  if (obj && typeof obj === 'object') {
    for (let key in obj) {
      if (obj.hasOwnProperty(key)) {
        if (obj[key] && typeof obj[key] === 'object') {
          objClone[key] = deepClone(obj[key])
        } else {
          objClone[key] = obj[key]
        }
      }
    }
  }
  return objClone
}

export const getIcon = (btnMode) => {
  switch (btnMode) {
    case 'Add': // 添加
      return ' iconfont icon-com-cycle-add'
    case 'Edit': // 修改
      return ' iconfont icon-com-edit'
    case 'Del': // 删除
      return ' iconfont icon-com-cycle-del'
    case 'ListEnd': // 完结
      return ' iconfont icon-com-status-end'
    case 'End': // 按钮完结
      return ' iconfont  icon-com-cycle-success'
    case 'Wifi': // 传感器设置
      return ' iconfont icon-com-wifi'
    case 'Down': // 下载
      return ' iconfont icon-com-download'
    case 'Up': // 上传
      return ' iconfont icon-com-upload'
    case 'Model': // 预警模型
      return ' iconfont icon-com-model'
    case 'Share': // 测点分布
      return ' iconfont icon-com-share'
    case 'Copy': // 复制
      return ' iconfont icon-com-copy'
    case 'Tool': // 维修 工具icon-com-setting
      return ' iconfont icon-com-setting'
    case 'Stop': // 停用
      return ' iconfont icon-com-stop'
    case 'Import': // 导入 批量导入
      return ' iconfont icon-com-mult-impot'
    case 'Read': // 已读
      return ' iconfont icon-com-read'
    case 'Option': // 发表意见
      return ' iconfont icon-com-express-opinion'
    case 'Write': // 下结论
      return ' iconfont icon-com-wirte'
    case 'Gen': // 生成
      return ' iconfont icon-com-gen'
    case 'Qgen': // 快速生成
      return ' iconfont icon-com-cycle-gen'
    case 'Arch': // 归档
      return ' iconfont icon-com-archive'
    case 'Diradd': // 新增文件夹
      return ' iconfont icon-com-dir-add'
    case 'Taskstop': // 中止
      return ' iconfont icon-com-task-stop'
    case 'Feedback': // 反馈
      return ' iconfont icon-com-feedback'
    case 'Person': // 人员分配
      return ' iconfont icon-common-person'
    case 'Use': // 启用
      return ' iconfont icon-staff-use'
    case 'Disable': // 禁用
      return ' iconfont icon-staff-disable'
    case 'ManWarn': // 人工预警
      return ' iconfont icon-man-warn'
    case 'MapView': // 地图视图
      return ' iconfont icon-map-view'
    case 'Export': // 导出
      return ' iconfont icon-an-daochuexcel'
    default:
      return ''
  }
}

/**
 * @description 获取所有菜单的name
 * @param {arrayList} 菜单集合
 */
export const getMenuNameList = (arrayList) => {
  let nameList = []
  const getChildrenName = (childList) => {
    for (let i = 0; i < childList.length; i++) {
      nameList.push(childList[i].name)
      if (childList[i].children && childList[i].children.length > 0) {
        getChildrenName(childList[i].children)
      }
    }
  }
  getChildrenName(arrayList)
  return nameList
}

/**
 *  判断某个菜单是否存在 用于根据菜单权限，控制窗口或按钮
 *  内部取菜单list
 */
export const menuExist = (that,targetUrl) => {
  let nameList = getMenuNameList(that.$store.state.user.menuList)||[];
  return nameList.indexOf(targetUrl)>=0;
}


