<!--
	下拉框选择器
-->
<template>
  <Select v-if="placement!='' " :placement='placement' :filterable="filterable?true:false" v-model="selectValue"
    :multiple="multiple" @on-change="onSelectChange" :disabled="disabled" :placeholder="disabled?'':placeholder" clearable ref="micSelect" @on-open-change="onOpenChange">
    <slot></slot>
    <Option v-for="op in options" :value="op.value" :key="op.value" :title="op.label">{{op.label}}</Option>
  </Select>
  <Select v-else v-model="selectValue" :multiple="multiple" :filterable="filterable?true:false" @on-change="onSelectChange"
    :disabled="disabled" :placeholder="disabled?'':placeholder" clearable ref="micSelect" @on-open-change="onOpenChange">
    <slot></slot>
    <Option v-for="op in options" :value="op.value" :key="op.value" :title="op.label">{{op.label}}</Option>
  </Select>
</template>

<script>
import {
  getDict
} from '@/api/sysApi'

import {
  getDataFromUrl
} from '@/api/common'

export default {
  props: {
    value: {
      default: null
    },
    multiple: {
      type: Boolean,
      default: false
    },
    url: {
      type: String,
      default: null
    },
    type: {
      type: String,
      default: null
    },
    disabled: {
      type: Boolean,
      default: false
    },
    placement: {
      type: String,
      default: ''
    },
    filterable: {
      type: Boolean,
      default: false
    },
    placeholder: {
      type: String,
      default: '请选择'
    },
    requestMethod: {
      type: String,
      default: 'get'
    },
    defaultIndex: {
      type: Number,
      default: null
    }
  },
  data () {
    return {
      options: [],
      selectValue: null
    }
  },
  mounted () {
    this.loadDictOption()
  },
  watch: {
    type (newVal, oldVal) {
      this.loadDictOption()
    },
    value (newVal, oldVal) {
      if (this.multiple) {
        this.selectValue = []
        this.selectValue.push(newVal)
      } else {
        this.selectValue = newVal
      }
    },
    url (newVal, oldVal) {
      if (newVal != '') {
        this.loadDictOption()
      }
    }
  },
  methods: {
    initData () {
      this.selectValue = []
      // this.loadDictOption();
    },
    clearData (opt) {
      this.selectValue = []
    },
    _showBack () {
      if (this.multiple) {
        this.selectValue = []
        if (typeof (this.value) === 'string') {
          this.selectValue.push(this.value)
        } else {
          var v = this.value || []
          v.forEach((item) => {
            this.selectValue.push(item)
          })
        }
      } else {
        this.selectValue = this.value
      }
    },
    loadDictOption () {
      // this.options.slice(0,this.options.length);
      this.options = []
      if (this.url) {
        getDataFromUrl(this.url, this.requestMethod).then(res => {
          var data = res.data || []
          data.forEach((item) => {
            this.options.push(item)
          })
          // --- 如果有设置默认选中第几项的参数的话，将该项选择 ycz-2019-10-17 --- //
          if ((this.defaultIndex || this.defaultIndex === 0) && (!this.value && this.value !== 0)) {
            if (data.length > 0) {
              this.defaultIndex = parseInt(this.defaultIndex)
              this.selectValue = data[this.defaultIndex].value
              this.$emit('input', this.selectValue)
              this.$emit('change', this.selectValue)
            }
          }
          this._showBack()
        }).catch(err => {
          console.error('下拉框获取数据错误!')
          console.error(err)
        })
      } else if (this.type) {
        getDict(this.type, false, false).then(res => {
          var data = res.data
          data.forEach((item) => {
            this.options.push(item)
          })
          // --- 如果有设置默认选中第几项的参数的话，将该项选择 ycz-2019-10-17 --- //
          if ((this.defaultIndex || this.defaultIndex === 0) && (!this.value && this.value !== 0)) {
            if (data.length > 0) {
              this.defaultIndex = parseInt(this.defaultIndex)
              this.selectValue = data[this.defaultIndex].value
              this.$emit('input', this.selectValue)
              this.$emit('change', this.selectValue)
            }
          }
          this._showBack()
        }).catch(err => {
          console.error('下拉框获取数据错误!')
          console.error(err)
        })
      } else {
        this._showBack()
      }
    },
    onSelectChange () {
      this.$emit('input', this.selectValue)
      this.$emit('change', this.selectValue)
    },
    onOpenChange (val) {
      // linx 2020-03-13 fix bug#1306 下拉框搜索问题 问题2
      /* if (val) {
          setTimeout(() => {
            this.$refs.micSelect.$data.filterQueryChange = false
          }, 0)
        } */
    }
  }
}
</script>

<style>

</style>
