<template>
  <div class="echarts-container"></div>
</template>

<script>
export default {
  props: ['options', 'styles'],
  name: 'echarts',
  watch: {
  	options: {
  		handler (options) {
        if (options.series && options.series.length > 0) {
          /** 当option的series发生变化以后动态更新图表**/
          this.chart.setOption(options, true)// true重绘
          this.chart.resize()
        }
  		},
  		deep: true
    }
  },
  data () {
    return {
      chart: null
    }
  },
  mounted () {
    this.initChart()
    window.addEventListener('resize', e => {
      this.chart.resize()
    }, false)
  },
  methods: {
    initChart () {
      this.$el.style.width = (this.styles.width || 99) + '%'
      this.$el.style.height = (this.styles.height || 99) + '%'
      this.chart = this.$echarts.init(this.$el)
      this.chart.setOption(this.options, true)
    },
    resizeChart () {
      this.chart.resize()
    },
    clearData (opt) {

    }
  }
}
</script>

<style lang="less" scoped>
    .echarts-container {
        max-width: 100%;
        max-height: 100%;
        margin:0 auto;
    }
</style>
