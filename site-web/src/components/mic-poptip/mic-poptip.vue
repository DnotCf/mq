<template>
  <div class="ivu-poptip-popper chrats-poptips mic-poptip"
  :style="'position: absolute;top:'+innerOffsetTop+'px;left:'+innerOffsetLeft+'px;width:'+width+'px;z-index:999999999999;'"
    @click.stop.prevent="micPopClick"
    @mouseover="micPopMouseHover"
    @mouseleave="micPopMouseLeave">
    <div class="ivu-poptip-content" v-show="value" ref="micPopTipRef">
      <div class="ivu-poptip-arrow"></div>
      <div class="ivu-poptip-inner">
        <div class="ivu-poptip-title">
          <slot name="title"></slot>
        </div>
        <div class="ivu-poptip-body">
          <div class="ivu-poptip-body-content">
            <slot name="content"></slot>
          </div>
        </div>
        <div class="bottom-extra"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MicPoptip',
  props: {
    value: {
      type: Boolean,
      default: false
    },
    transfer: {
      type: Boolean,
      default: false
    },
    topOffset: {
      type: [Number, String],
      default: 0
    },
    leftOffset: {
      type: [Number, String],
      default: 0
    },
    width: {
      ype: [Number, String],
      default: 400
    }
  },
  watch: {
    topOffset (newValue, oldValue) {
      if (newValue) {
        this.$nextTick(() => {
          let contentHeight = this.$refs.micPopTipRef.clientHeight
          let bodyHeight = document.body.clientHeight
          let topoff = parseInt(bodyHeight - this.contentHeight)
          let resultTop = parseInt(newValue - contentHeight + 10)
          if (resultTop > topoff) {
            resultTop = topoff
          }
          this.innerOffsetTop = resultTop
        })
      } else {
        this.innerOffsetTop = 0
      }
    },
    leftOffset (newValue, oldValue) {
      if (newValue) {
        this.$nextTick(() => {
          let bodyWidth = document.body.clientWidth
          let leftoff = parseInt(bodyWidth - this.width)
          let resultLeft = parseInt(newValue - this.width / 2 - 2)
          if (resultLeft > leftoff) {
            resultLeft = leftoff
          }
          this.innerOffsetLeft = resultLeft
        })
      } else {
        this.innerOffsetLeft = 0
      }
    }
  },
  data () {
    return {
      innerOffsetTop: this.topOffset,
      innerOffsetLeft: this.leftOffset
    }
  },
  methods: {
    micPopClick () {

    },
    micPopMouseHover () {
    },
    micPopMouseLeave () {
      this.$emit('input', false)
    }
  },
  mounted () {
    if (this.transfer) {
      this.$nextTick(() => {
        const body = document.querySelector('body')
        if (body.append) {
          body.append(this.$el)
        } else {
          body.appendChild(this.$el)
        }

        this.$nextTick(() => {
          let contentHeight = this.$refs.micPopTipRef.clientHeight
          let bodyWidth = document.body.clientWidth
          let bodyHeight = document.body.clientHeight
          let leftoff = parseInt(bodyWidth - this.width)
          let topoff = parseInt(bodyHeight - this.contentHeight)
          let resultTop = parseInt(this.innerOffsetTop - contentHeight + 10)
          let resultLeft = parseInt(this.innerOffsetLeft - this.width / 2 - 2)
          if (resultTop > topoff) {
            resultTop = topoff
          }
          if (resultLeft > leftoff) {
            resultLeft = leftoff
          }
          this.innerOffsetTop = resultTop
          this.innerOffsetLeft = resultLeft
        })
      })
    } else {
      let contentHeight = this.$refs.micPopTipRef.clientHeight
      let bodyWidth = document.body.clientWidth
      let bodyHeight = document.body.clientHeight
      let leftoff = parseInt(bodyWidth - this.width)
      let topoff = parseInt(bodyHeight - this.contentHeight)
      let resultTop = parseInt(this.innerOffsetTop - contentHeight + 10)
      let resultLeft = parseInt(this.innerOffsetLeft - this.width / 2 - 2)
      if (resultTop > topoff) {
        resultTop = topoff
      }
      if (resultLeft > leftoff) {
        resultLeft = leftoff
      }
      this.innerOffsetTop = resultTop
      this.innerOffsetLeft = resultLeft
    }
  },
  beforeDestroy () {
    this.innerOffsetTop = 0
    this.innerOffsetLeft = 0
  }
}
</script>

<style lang="less">
  @import './mic-poptip.less';
</style>
