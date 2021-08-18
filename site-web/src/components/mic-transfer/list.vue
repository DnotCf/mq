<template>
<div :class="classes" :style="listStyle">
  <div :class="prefixCls + '-header'">
    <!-- <Checkbox :value="checkedAll" :disabled="checkedAllDisabled" @on-change="toggleSelectAll"></Checkbox> -->
    <i class="ivu-transfer-title-icon iconfont" :class="titleIcon" v-if="titleIcon && titleIcon != ''"></i>
    <span :class="prefixCls + '-header-title'" @click="toggleSelectAll(!checkedAll)">{{ title }}</span>
    <!-- <span :class="prefixCls + '-header-count'">{{ count }}</span> -->
    <div :class="prefixCls + '-body-search-wrapper'" v-if="filterable">
      <Search :prefix-cls="prefixCls + '-search'" :query="query" @on-query-clear="handleQueryClear" @on-query-change="handleQueryChange"
       :placeholder="filterPlaceholder" :selfSearch="selfSearch"></Search>
    </div>
  </div>
  <div :class="bodyClasses">
    <ul class="transfer-list-title">
      <li>
        <span class="sensor-name">传感器名称</span>
        <span class="sensor-no">设备编号</span>
      </li>
    </ul>
    <ul :class="prefixCls + '-content'">
      <li v-for="(item, index) in filterData" v-html="showLabel(item)" :class="itemClasses(item)" @click.prevent="select(item)"
      @dblclick.prevent="doubleSelect(item)" :key="index">
        <!-- <Checkbox :value="isCheck(item)" :disabled="item.disabled"></Checkbox> -->
      </li>
      <li :class="prefixCls + '-content-not-found'">{{ notFoundText }}</li>
    </ul>
  </div>
  <div :class="prefixCls + '-footer'" v-if="showFooter">
    <slot></slot>
  </div>
</div>
</template>
<script>
import Search from './search.vue'
var time = null //  在这里定义time 为null

export default {
  name: 'TransferList',
  components: {
    Search
  },
  props: {
    prefixCls: String,
    data: Array,
    renderFormat: Function,
    checkedKeys: Array,
    listStyle: Object,
    title: [String, Number],
    filterable: Boolean,
    filterPlaceholder: String,
    filterMethod: Function,
    notFoundText: String,
    validKeysCount: Number,
    titleIcon: String,
    selfSearch: Boolean
  },
  data () {
    return {
      showItems: [],
      query: '',
      showFooter: true,
      isDbclick: false
    }
  },
  watch: {
    data () {
      this.updateFilteredData()
    }
  },
  computed: {
    classes () {
      return [
        `${this.prefixCls}`,
        {
          [`${this.prefixCls}-with-footer`]: this.showFooter
        }
      ]
    },
    bodyClasses () {
      return [
        `${this.prefixCls}-body`,
        {
          [`${this.prefixCls}-body-with-search`]: this.filterable,
          [`${this.prefixCls}-body-with-footer`]: this.showFooter
        }
      ]
    },
    count () {
      const validKeysCount = this.validKeysCount
      return (validKeysCount > 0 ? `${validKeysCount}/` : '') + `${this.data.length}`
    },
    checkedAll () {
      return this.filterData.filter(data => !data.disabled).length === this.validKeysCount && this.validKeysCount !== 0
    },
    checkedAllDisabled () {
      return this.filterData.filter(data => !data.disabled).length <= 0
    },
    filterData () {
      return this.showItems.filter(item => this.filterMethod(item, this.query))
    }
  },
  methods: {
    itemClasses (item) {
      if (item.isActive) {
        return [
          `${this.prefixCls}-content-item active`,
          {
            [`${this.prefixCls}-content-item-disabled`]: item.disabled
          }
        ]
      } else {
        return [
          `${this.prefixCls}-content-item`,
          {
            [`${this.prefixCls}-content-item-disabled`]: item.disabled
          }
        ]
      }
    },
    showLabel (item) {
      return this.renderFormat(item)
    },
    isCheck (item) {
      return this.checkedKeys.some(key => key === item.key)
    },
    select (item) {
      clearTimeout(time) // 首先清除计时器
      if (item.disabled) return
      let that = this
      time = setTimeout(() => {
        if (item.isActive) {
          item.isActive = false
        } else {
          item.isActive = true
        }
        const index = that.checkedKeys.indexOf(item.key)
        index > -1 ? that.checkedKeys.splice(index, 1) : that.checkedKeys.push(item.key)
        that.$parent.handleCheckedKeys()
      }, 300) // 大概时间300ms的延迟
    },
    doubleSelect (item) {
      clearTimeout(time) // 清除
      if (item.disabled) {
        return
      }
      const index = this.checkedKeys.indexOf(item.key)
      index > -1 ? this.checkedKeys.splice(index, 1) : this.checkedKeys.push(item.key)
      const sourceSelectedKeys = this.$parent.getValidKeys('left')
      const targetSelectedKeys = this.$parent.getValidKeys('right')
      if (sourceSelectedKeys.length > 0) {
        this.$parent.moveTo('right')
      } else if (targetSelectedKeys.length > 0) {
        this.$parent.moveTo('left')
      }
    },
    updateFilteredData () {
      this.showItems = this.data
    },
    toggleSelectAll (status) {
      const keys = status
        ? this.filterData.filter(data => !data.disabled || this.checkedKeys.indexOf(data.key) > -1).map(data => data.key)
        : this.filterData.filter(data => data.disabled && this.checkedKeys.indexOf(data.key) > -1).map(data => data.key)
      this.$emit('on-checked-keys-change', keys)
    },
    handleQueryClear () {
      this.query = ''
    },
    handleQueryChange (val) {
      if (!this.selfSearch) {
        this.query = val
      } else {
        this.$emit('on-self-search-click', val)
      }
    }
  },
  created () {
    this.updateFilteredData()
  },
  mounted () {
    this.showFooter = this.$slots.default !== undefined
  }
}
</script>
<style lang="less" scoped>
@import './transfer.less';
</style>
