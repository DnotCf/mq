<template>
  <!-- <i :class="`iconfont icon-${type}`" :style="styles"></i> -->
  <svg class="menuIcon alSvgIcon" aria-hidden="true"  :style="styles">
      <use :xlink:href="`#icon-${type}`"></use>
  </svg>
</template>

<script>
export default {
  name: 'Icons',
  props: {
    type: {
      type: String,
      required: true
    },
    size: {
      type: Number,
      default: 5
    }
  },
  computed: {
    styles () {
      return {
        // fontSize: `${this.size}px`,
        width: `${this.size}em`,
        height: `${this.size}em`
      }
    }
  }
}
</script>

<style>

</style>
