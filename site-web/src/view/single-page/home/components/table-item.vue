<template>
  <div class="list-content">
    <span class="list-nature">{{ label }}</span>
    <span class="value-ellipsis">{{ value }}</span>
  </div>
</template>
<script>
export default {
  name: "",
  components: {},
  props: {
    value: {
      default: "",
    },
    label: {
      default: "",
    },
  },
  data() {
    return {};
  },
  created() {},
  mounted() {},
  computed: {},
  methods: {},
};
</script>
<style lang="less" scoped>
.list-content {
  font-size: 14px;
  letter-spacing: 2px;
  display: flex;
  margin-top: 10px;
  padding-right: 20px;
  color: #4567A3;
  .list-nature {
    color: #a6b4cf;
    max-width: auto;
  }
  .value-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
  }
}
</style>
