<script>
import List from './list.vue'
import Operation from './operation.vue'

const prefixCls = 'ivu-transfer'
export default {
  name: 'MicTransfer',
  render (h) {
    function cloneVNode (vnode) {
      const clonedChildren = vnode.children && vnode.children.map(vnode => cloneVNode(vnode))
      const cloned = h(vnode.tag, vnode.data, clonedChildren)
      cloned.text = vnode.text
      cloned.isComment = vnode.isComment
      cloned.componentOptions = vnode.componentOptions
      cloned.elm = vnode.elm
      cloned.context = vnode.context
      cloned.ns = vnode.ns
      cloned.isStatic = vnode.isStatic
      cloned.key = vnode.key
      return cloned
    }
    const vNodes = this.$slots.default === undefined ? [] : this.$slots.default
    const clonedVNodes = this.$slots.default === undefined ? [] : vNodes.map(vnode => cloneVNode(vnode))
    return h('div', {
      'class': this.classes
    }, [
      h(List, {
        ref: 'left',
        props: {
          prefixCls: 'left ' + this.prefixCls + '-list',
          data: this.leftData,
          renderFormat: this.renderFormat,
          checkedKeys: this.leftCheckedKeys,
          validKeysCount: this.leftValidKeysCount,
          listStyle: this.listStyle,
          title: this.localeTitles[0],
          filterable: this.filterable,
          filterPlaceholder: this.localeFilterPlaceholder,
          filterMethod: this.filterMethod,
          notFoundText: this.localeNotFoundText,
          titleIcon: this.titleIcons[0],
          selfSearch: this.selfSearch
        },
        on: {
          'on-checked-keys-change': this.handleLeftCheckedKeysChange,
          'on-self-search-click': this.selfSeachClick
        }
      }, vNodes),
      h(Operation, {
        props: {
          prefixCls: this.prefixCls,
          operations: this.operations,
          leftActive: this.leftValidKeysCount > 0,
          rightActive: this.rightValidKeysCount > 0
        }
      }),
      h(List, {
        ref: 'right',
        props: {
          prefixCls: 'right ' + this.prefixCls + '-list',
          data: this.rightData,
          renderFormat: this.renderFormat,
          checkedKeys: this.rightCheckedKeys,
          validKeysCount: this.rightValidKeysCount,
          listStyle: this.listStyle,
          title: this.localeTitles[1],
          filterable: this.filterable,
          filterPlaceholder: this.localeFilterPlaceholder,
          filterMethod: this.filterMethod,
          notFoundText: this.localeNotFoundText,
          titleIcon: this.titleIcons[1],
          selfSearch: false
        },
        on: {
          'on-checked-keys-change': this.handleRightCheckedKeysChange,
          'on-self-search-click': this.selfSeachClick
        }
      }, clonedVNodes)
    ])
  },
  props: {
    data: {
      type: Array,
      default () {
        return []
      }
    },
    renderFormat: {
      type: Function,
      default (item) {
        return item.label || item.key
      }
    },
    targetKeys: {
      type: Array,
      default () {
        return []
      }
    },
    selectedKeys: {
      type: Array,
      default () {
        return []
      }
    },
    listStyle: {
      type: Object,
      default () {
        return {}
      }
    },
    titles: {
      type: Array
    },
    titleIcons: {
      type: Array,
      default () {
        return []
      }
    },
    selfSearch: {
      type: Boolean,
      default: false
    },
    operations: {
      type: Array,
      default () {
        return []
      }
    },
    filterable: {
      type: Boolean,
      default: false
    },
    filterPlaceholder: {
      type: String
    },
    filterMethod: {
      type: Function,
      default (data, query) {
        const type = ('label' in data) ? 'label' : 'key'
        return data[type].indexOf(query) > -1
      }
    },
    notFoundText: {
      type: String
    }
  },
  data () {
    return {
      prefixCls: prefixCls,
      leftData: [],
      rightData: [],
      leftCheckedKeys: [],
      rightCheckedKeys: []
    }
  },
  computed: {
    classes () {
      return [
        `${prefixCls}`
      ]
    },
    leftValidKeysCount () {
      return this.getValidKeys('left').length
    },
    rightValidKeysCount () {
      return this.getValidKeys('right').length
    },
    localeFilterPlaceholder () {
      if (this.filterPlaceholder === undefined) {
        // return this.t('i.transfer.filterPlaceholder');
        return '请输入搜索内容'
      } else {
        return this.filterPlaceholder
      }
    },
    localeNotFoundText () {
      if (this.notFoundText === undefined) {
        // return this.t('i.transfer.notFoundText');
        return '暂无数据'
      } else {
        return this.notFoundText
      }
    },
    localeTitles () {
      if (this.titles === undefined) {
        // return [this.t('i.transfer.titles.source'), this.t('i.transfer.titles.target')];
        return ['源列表', '目的列表']
      } else {
        return this.titles
      }
    }
  },
  methods: {
    getValidKeys (direction) {
      return this[`${direction}Data`].filter(data => !data.disabled && this[`${direction}CheckedKeys`].indexOf(data.key) > -1).map(data => data.key)
    },
    splitData (init = false) {
      this.leftData = [...this.data]
      this.rightData = []
      if (this.targetKeys.length > 0) {
        this.targetKeys.forEach((targetKey) => {
          const filteredData = this.leftData.filter((data, index) => {
            if (data.key === targetKey) {
              this.leftData.splice(index, 1)
              return true
            }
            return false
          })
          if (filteredData && filteredData.length > 0) this.rightData.push(filteredData[0])
        })
      }
      if (init) {
        this.splitSelectedKey()
      }
    },
    splitSelectedKey () {
      const selectedKeys = this.selectedKeys
      if (selectedKeys.length > 0) {
        this.leftCheckedKeys = this.leftData
          .filter(data => selectedKeys.indexOf(data.key) > -1)
          .map(data => data.key)
        this.rightCheckedKeys = this.rightData
          .filter(data => selectedKeys.indexOf(data.key) > -1)
          .map(data => data.key)
      }
    },
    moveTo (direction) {
      const targetKeys = this.targetKeys
      const opposite = direction === 'left' ? 'right' : 'left'
      const moveKeys = this.getValidKeys(opposite)
      const newTargetKeys = direction === 'right'
        ? moveKeys.concat(targetKeys)
        : targetKeys.filter(targetKey => !moveKeys.some(checkedKey => targetKey === checkedKey))
      this.$refs[opposite].toggleSelectAll(false)
      this.$emit('on-change', newTargetKeys, direction, moveKeys)
      this.dispatch('FormItem', 'on-form-change', {
        tarketKeys: newTargetKeys,
        direction: direction,
        moveKeys: moveKeys
      })
      /* 对所有选中状态进行清空处理 */
      for (var i = 0; i < this.data.length; i++) {
        this.data[i].isActive = false
      }
    },
    handleLeftCheckedKeysChange (keys) {
      this.leftCheckedKeys = keys
    },
    handleRightCheckedKeysChange (keys) {
      this.rightCheckedKeys = keys
    },
    selfSeachClick (val) {
      this.$emit('self-search-click', val)
    },
    handleCheckedKeys () {
      const sourceSelectedKeys = this.getValidKeys('left')
      const targetSelectedKeys = this.getValidKeys('right')
      this.$emit('on-selected-change', sourceSelectedKeys, targetSelectedKeys)
    },
    dispatch (componentName, eventName, params) {
      let parent = this.$parent || this.$root
      let name = parent.$options.name

      while (parent && (!name || name !== componentName)) {
        parent = parent.$parent

        if (parent) {
          name = parent.$options.name
        }
      }
      if (parent) {
        parent.$emit.apply(parent, [eventName].concat(params))
      }
    },
    clearData () {
      this.$children.map((item) => {
        if (item.handleQueryClear) {
          item.handleQueryClear()
        }
      })
    }
  },
  watch: {
    targetKeys () {
      this.splitData(false)
    },
    data () {
      this.splitData(false)
    }
  },
  mounted () {
    this.splitData(true)
  }
}
</script>
<style lang="less" scoped>
@import './transfer.less';
</style>
