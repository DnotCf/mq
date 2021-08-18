<template>
  <div class="mic-progress">
    <Progress :percent="percent" hide-info/>
    <div class="process-choose-icon" :style="getIconStyle">
       <img src="/images/report/location.png" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'MicProgress',
  components: {

  },
  props: {
    percent: {
      type: Number,
      default: 0
    },
    step: {
      type: Number,
      default: 0,
    },
    total: {
      type: Number,
      default: 4
    }
  },
  computed: {
    getIconStyle () {
      let average = 100 / this.total
      let setp = parseInt(this.step / average)
      let _left = 0
      if (setp > 1) {
        _left = (setp - 1) * average + (average / 2)
      } else {
        _left = average / 2
      }
      return {
        'left': _left + '%',
        'transform': 'translate(' + '-' + _left + '%' + ',0)'
      }
    }
  },
  data () {
    return {

    }
  },
  methods: {

  },
  created () {

  },
  mounted () {

  }
}
</script>

<style lang="less">
  @import  './mic-progress.less';
</style>
