<template>
  <div class="mic-alert">
    <Alert :type="type" :show-icon="showIcon" v-show="value != ''">
    {{value}}
    <div class="close-icon" @click.stop="closeAlert">
      <Icon type="ios-close" />
    </div>
    </Alert>
  </div>
</template>

<script>
export default {
  name: 'MicAlert',
  props: {
    value: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'error'
    },
    showIcon: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    closeAlert () {
      this.$emit('input', '')
    }
  }
}
</script>

<style lang="less" scoped>
.mic-alert {
  .close-icon {
    position: absolute;
    right: 10px;
    top: 50%;
    transform: translate(0, -50%);
    .ivu-icon {
      font-size: 2rem;
    }
    .ivu-icon:hover {
       color: #19be6b;
    }
  }
}
</style>
