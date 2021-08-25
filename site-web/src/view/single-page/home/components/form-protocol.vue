<template>
  <div>
    <!-- AMQP start -->
    <div v-if="value.type === 'AMQP'">
      <FormItem label="用户名" :prop="propName + 'username'">
        <Input v-model="value.username" placeholder=""></Input>
      </FormItem>
      <FormItem label="密码" :prop="propName + 'password'">
        <Input
          v-model="value.password"
          placeholder=""
          type="password"
          autocomplete="new-password"
        ></Input>
      </FormItem>
    </div>
    <!-- AMQP end -->

    <!-- Aliyun_RocketMQ start -->
    <div v-if="value.type === 'Aliyun_RocketMQ'">
      <FormItem label="secretKey" :prop="propName + 'secretKey'">
        <Input v-model="value.secretKey"  placeholder="" ></Input>
      </FormItem>
      <FormItem label="accessKey" :prop="propName + 'accessKey'">
        <Input v-model="value.accessKey"  placeholder="" ></Input>
      </FormItem>
    </div>
    <!-- Aliyun_RocketMQ end -->


    <!-- HTTP RocketMQ 阿里云RocketMQ-->
    <div
      v-if="
        value.type === 'HTTP' ||
        value.type === 'RocketMQ' ||
        value.type === 'Aliyun_RocketMQ'
      "
    >
      <FormItem label="服务地址" :prop="propName + 'cluster'">
        <Input v-model="value.cluster" placeholder="" type="text"></Input>
      </FormItem>
    </div>

    <!-- MQTT AMQP-->
    <div v-if="value.type === 'MQTT' || value.type === 'AMQP'">
      <FormItem
        :label="value.type === 'AMQP' ? 'VirtualHost' : 'Client ID'"
        :prop="propName + 'clientName'"
      >
        <Input
          v-model="value.clientName"
          placeholder=""
        ></Input>
      </FormItem>
      <FormItem label="传输协议" :prop="propName + 'protocol'">
        <Select v-model="value.protocol" placeholder="">
          <Option
            :value="item.value"
            v-for="(item, index) in protocolList"
            :key="index"
            >{{ item.label }}</Option
          >
        </Select>
      </FormItem>
      <FormItem label="ip" :prop="propName + 'ip'">
        <Input
          v-model="value.ip"
          placeholder=""
        ></Input>
      </FormItem>
      <FormItem label="端口" :prop="propName + 'port'">
        <Input
          v-model="value.port"
          placeholder=""
          number
        ></Input>
      </FormItem>
    </div>

    <!-- 阿里云RocketMQ rocketMQ  -->
    <div v-if="value.type === 'RocketMQ'|| value.type === 'Aliyun_RocketMQ' ">
      <FormItem label="所属组" :prop="propName + 'group'">
        <Input
          v-model="value.group"
          placeholder=""
          type="text"
         autocomplete="off"
        ></Input>
      </FormItem>
      <FormItem label="tag" :prop='propName + "tag"'>
        <Input
          v-model="value.tag"
          placeholder=""
            type="text"
         autocomplete="new-password"
        ></Input>
      </FormItem>
    </div>

    <!-- MQTT  AMQP 阿里云RocketMQ  RocketMQ-->
    <div v-if="value.type !== 'HTTP'">
      <FormItem label="topic" :prop="propName + 'topic'">
        <Input v-model="value.topic" placeholder=""></Input>
      </FormItem>
   
    </div>
    <template v-if="value.type !== 'HTTP'">
       <FormItem label="其他参数" :prop="propName + 'defaultParam'">
        <Input
          v-model="value.defaultParam"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 5 }"
          placeholder=""
        ></Input>
      </FormItem>
    </template>
    <!-- EMQX start -->
    <template v-if="value.type === 'MQTT'">
      <FormItem label="用户名">
        <Input v-model="value.username" placeholder=""></Input>
      </FormItem>
      <FormItem label="密码">
        <Input
          v-model="value.password"
          placeholder=""
          type="password"
          autocomplete="new-password"
        ></Input>
      </FormItem>
    </template>
    <!-- EMQX end -->

    <!-- RocketMQ start-->
    <template v-if="value.type === 'RocketMQ'">
      <FormItem label="重试次数">
        <Input
          v-model="value.retry"
          placeholder=""
        ></Input>
      </FormItem>
    </template>
    <!-- RocketMQ end -->
  </div>
</template>
<script>
export default {
  name: "",
  components: {},
  data() {
    return {
      protocolList: [
        {
          label: "tcp协议",
          value: "tcp"
        },
        {
          label: "websocket协议",
          value: "ws"
        },
        {
          label: "http协议",
          value: "http"
        }
      ]
    };
  },
  props: {
    value: {
      default: null
    },
    propName: {
      default: ""
    }
  },
  created() {},
  mounted() {},
  computed: {},
  methods: {}
};
</script>
<style lang="less" scoped>
</style>
