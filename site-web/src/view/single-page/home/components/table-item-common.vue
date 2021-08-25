<template>
  <div>
    <!-- Aliyun_RocketMQ -->
    <template v-if="value.type === 'Aliyun_RocketMQ'">
      <table-item label="secretKey：" :value="value.secretKey"></table-item>
      <table-item label="accessKey：" :value="value.accessKey"></table-item>
    </template>

    <!-- RocketMQ -->
    <template v-if="value.type === 'Aliyun_RocketMQ'">
      <table-item label="重试次数：" :value="value.retry"></table-item>
    </template>

    <!-- HTTP RocketMQ 阿里云RocketMQ Kafka-->
    <template
      v-if="
        value.type === 'HTTP' ||
        value.type === 'RocketMQ' ||
        value.type === 'Aliyun_RocketMQ'||
        value.type === 'Kafka'
      "
    >
      <table-item label="服务地址：" :value="value.cluster"></table-item>
    </template>
    <!-- MQTT AMQP-->
    <div v-if="value.type == 'MQTT' || value.type == 'AMQP'">
      <table-item label="用户名：" :value="value.username"></table-item>
      <table-item label="密码：" :value="value.password"></table-item>
      <table-item label="协议：" :value="value.protocol"></table-item>
      <table-item label="ip：" :value="value.ip"></table-item>
      <table-item label="端口：" :value="value.port"></table-item>
    </div>
    <!-- 阿里云RocketMQ rocketMQ  -->
    <template
      v-if="value.type === 'Aliyun_RocketMQ' || value.type === 'RocketMQ'|| value.type === 'Kafka'"
    >
      <table-item label="所属组：" :value="value.group"></table-item>
      <table-item label="tag：" :value="value.tag"></table-item>
    </template>
    <!-- MQTT  AMQP 阿里云RocketMQ  RocketMQ -->
    <template v-if="value.type !== 'HTTP'">
      <table-item label="topic：" :value="value.topic"></table-item>
    </template>
  </div>
</template>
<script>
import TableItem from "./table-item";

export default {
  name: "",
  components: {
    TableItem
  },
  props: {
    value: {
      default: null
    }
  },
  data() {
    return {};
  },
  created() {},
  mounted() {},
  computed: {},
  methods: {}
};
</script>
<style lang="less" scoped>
</style>
