<!--
下拉框选择器
-->
<template>
  <SelectMul
    :label-in-value="true"
    v-model="selectValue"
    :placement='placement'
    :filterable="filterable?true:false"
    :multiple="multiple"
    :disabled="disabled"
    :placeholder="disabled?'':placeholder"
    clearable ref="micSelect"
    @on-open-change="onOpenChange"
    @on-change="onSelectChange"
    :max-tag-count="maxTagCount"
    :max-tag-placeholder="maxTagPlaceholder">
    <slot></slot>
    <Option v-for="op in options" :value="op.value" :key="op.value">{{op.label}}</Option>
  </SelectMul>
</template>

<script>
import {
  getDict
} from '@/api/sysApi'
import SelectMul from './select'

import {
  getDataFromUrl
} from '@/api/common'

export default {
  components: {
    SelectMul
  },
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
      default: 'bottom-start'
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
    },
    maxTagCount: {
      type: Number
    },
    maxTagPlaceholder: {
      type: Function
    }

  },
  data () {
    return {
      options: [],
      selectValue: null,
      labelNames: []
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
      this.selectValue = newVal
    },
    url (newVal, oldVal) {
      if (newVal !== '') {
        this.loadDictOption()
      }
    }
  },
  methods: {
    initData () {
      this.selectValue = []
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
      this.options = []
      if (this.url) {
        getDataFromUrl(this.url, this.requestMethod).then(res => {
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
    onSelectChange (data) {
      this.labelNames = []
      this.$emit('input', this.selectValue)
      this.$emit('on-change', this.selectValue)
      if (this.multiple) {
        data.map((item) => {
          this.labelNames.push(item.label)
        })
      } else {
        this.labelNames.push(data.label)
      }
      this.$emit('labe-name-change', this.labelNames)
    },
    onOpenChange (val) {
      this.$emit('on-open-change', val)
      if (val) {
        setTimeout(() => {
          this.$refs.micSelect.$data.filterQueryChange = false
        }, 0)
      }
    }
  }
}
</script>

<style>

</style>
