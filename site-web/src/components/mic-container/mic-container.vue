<!--
容器控件功能：
 1、支持动态计算高度-->
<template>
  <div class="mic-container" :style="getContainerStyle">
    <slot></slot>
  </div>
</template>

<script>
export default {
  props: {
    autoHeight: {
      type: Boolean,
      default: false
    },
    height: {
      type: Number,
      default: null
    },
    offsetTop: {
      type: Number,
      default: 0
    }
  },
  computed: {
    getContainerStyle () {
      let style = {}
      if (this.autoHeight) {
        style = {
          height: '100%'
        }
      } else {
        style = {
          height: this.containerHeight + 'px'
        }
      }
      return style
    }
  },
  data () {
    return {
      containerHeight: ''
    }
  },
  mounted () {
    if (this.height) {
      // 手动设置高度
      this.containerHeight = this.height
    } else {
      // 自动计算表格高度
      // 设置表格高度 = 浏览器高度 - 减去外部位移
      this.containerHeight = document.documentElement.clientHeight - this.$props.offsetTop
    }

    this.$emit('container-loaded', this.containerHeight)
    // this.$emit('table-loaded', document.documentElement.clientHeight - this.$props.offsetTop - 64 - 18 * 2 - 46);
  },
  methods: {}
}
</script>

<style lang="less">
.mic-container {
  .left-panel {
    background: #ffffff;height: 100%;
    padding: 10px;
    overflow: auto;
  }
  .right-panel {
    background: #ffffff;
    padding-left: 5px;
  }
}
</style>
