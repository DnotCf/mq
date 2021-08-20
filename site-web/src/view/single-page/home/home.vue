<template>
  <div class="page-home-box">
    <div class="page-home-title-box">
      <svg-icon name="icon-logo" size="32" color="#B2B2B2"></svg-icon>
      <span class="page-home-title">数据源管理</span>
    </div>
    <div class="page-home-content-box">
      <div class="page-home-content-layout page-content-common-box">
        <div class="page-title-common">
          <div class="page-title-name">
            <svg-icon name="title-icon" size="30" color="#B2B2B2"></svg-icon>
            <span class="title-name">表格管理</span>
          </div>
          <div class="btn-layout">
            <Dropdown class="dropdown-box">
              <Button type="primary" class="btn-box">
                修改
                <Icon type="ios-arrow-down"></Icon>
              </Button>
              <DropdownMenu slot="list" style="display: block">
                <DropdownItem @click.native="handleModifySource"
                  >数据源</DropdownItem
                >
                <DropdownItem @click.native="handleModifyMap"
                  >映射</DropdownItem
                >
              </DropdownMenu>
            </Dropdown>
            <Dropdown class="dropdown-box">
              <Button type="primary" class="btn-box">
                删除
                <Icon type="ios-arrow-down"></Icon>
              </Button>
              <DropdownMenu slot="list">
                <DropdownItem @click.native="handleDelectSource"
                  >数据源</DropdownItem
                >
                <DropdownItem @click.native="handleDelectMap"
                  >映射</DropdownItem
                >
              </DropdownMenu>
            </Dropdown>
            <Button type="primary" class="btn-box" @click="handleAddMap">
              添加映射
            </Button>
          </div>
        </div>
        <div class="page-list-box">
          <Table
            ref="selection"
            :columns="columns"
            :data="dataList"
            class="table-box"
            @on-select="handleSelect"
          >
            <template slot-scope="{ row, index }" slot="name">{{
              row.fromServer.name
            }}</template>
            <template slot-scope="{ row, index }" slot="sourceProtocol">
              <!-- Aliyun_RocketMQ -->
              <template v-if="row.fromServer.type === 'Aliyun_RocketMQ'">
                <div class="list-content">
                  <span class="list-nature">secretKey：</span
                  >{{ row.fromServer.secretKey }}
                </div>
                <div class="list-content">
                  <span class="list-nature">accessKey：</span
                  >{{ row.fromServer.accessKey }}
                </div>
              </template>

              <!-- RocketMQ -->
              <template v-if="row.fromServer.type === 'Aliyun_RocketMQ'">
                <div class="list-content">
                  <span class="list-nature">单次最大消费条数：</span
                  >{{ row.fromServer.total }}
                </div>
                <div class="list-content">
                  <span class="list-nature">重试次数：</span
                  >{{ row.fromServer.retry }}
                </div>
              </template>

              <!-- HTTP RocketMQ 阿里云RocketMQ -->
              <template
                v-if="
                  row.fromServer.type === 'HTTP' ||
                  row.fromServer.type === 'RocketMQ' ||
                  row.fromServer.type === 'Aliyun_RocketMQ'
                "
              >
                <div class="list-content">
                  <span class="list-nature">服务地址：</span
                  >{{ row.fromServer.cluster }}
                </div>
              </template>
              <!-- MQTT AMQP-->
              <div
                v-if="
                  row.fromServer.type == 'MQTT' || row.fromServer.type == 'AMQP'
                "
              >
                <div class="list-content">
                  <span class="list-nature">用户名：</span
                  >{{ row.fromServer.username }}
                </div>
                <div class="list-content">
                  <span class="list-nature">密码：</span
                  >{{ row.fromServer.password }}
                </div>
                <div class="list-content">
                  <span class="list-nature">协议：</span
                  >{{ row.fromServer.protocol }}
                </div>
                <div class="list-content">
                  <span class="list-nature">ip：</span>{{ row.fromServer.ip }}
                </div>
                <div class="list-content">
                  <span class="list-nature">端口：</span
                  >{{ row.fromServer.port }}
                </div>
              </div>
              <!-- 阿里云RocketMQ rocketMQ  -->
              <template
                v-if="
                  row.fromServer.type === 'Aliyun_RocketMQ' ||
                  row.fromServer.type === 'RocketMQ'
                "
              >
                <div class="list-content">
                  <span class="list-nature">所属组：</span
                  >{{ row.fromServer.group }}
                </div>
                <div class="list-content">
                  <span class="list-nature">tag：</span>{{ row.fromServer.tag }}
                </div>
              </template>
              <!-- MQTT  AMQP 阿里云RocketMQ  RocketMQ -->
              <template v-if="row.fromServer.type !== 'HTTP'">
                <div class="list-content">
                  <span class="list-nature">topic：</span
                  >{{ row.fromServer.topic }}
                </div>
                <div class="list-content">
                  <span class="list-nature">其他参数：</span
                  >{{ row.fromServer.defaultParam }}
                </div>
              </template>
            </template>
            <template slot-scope="{ row, index }" slot="newwork">
              <div v-if="!Number(row.fromServer.networkType)">
                <div class="list-content">
                  <span class="list-nature">账号：</span
                  >{{ row.fromServer.vpnAccount }}
                </div>
                <div class="list-content">
                  <span class="list-nature">密码：</span
                  >{{ row.fromServer.vpnAccount }}
                </div>
              </div>
              <div v-else>
                <div class="list-content">
                  <span class="list-nature">地址：</span
                  >{{ row.fromServer.retry }}
                </div>
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="map">
              <div class="fold-box" v-if="Number(row.fromServer.map)">
                <div
                  ref="mapNode"
                  class="map-box"
                  :style="{
                    overflow: isFold ? 'hidden' : '',
                    height: isFold ? '90px' : '',
                  }"
                >
                  <div
                    v-for="(item, index) in ceList"
                    :key="index"
                    class="list-content-box"
                  >
                    <div class="list-content">
                      <span class="list-nature">选择数据源：</span
                      >{{ item.name1 }}
                    </div>
                    <div class="list-content">
                      <span class="list-nature">IP地址：</span>{{ item.name2 }}
                    </div>
                    <div class="list-content">
                      <span class="list-nature"> 端口：</span>
                      {{ item.name3 }}
                    </div>
                  </div>
                </div>
                <div class="source-status">
                  <div class="status-radius"></div>
                </div>
                <div @click="handleFold" class="fold">
                  <Icon
                    type="ios-arrow-down"
                    v-if="isFold"
                    color="#C8D5EA"
                    size="24"
                    title="展开"
                  />
                  <Icon
                    type="ios-arrow-up"
                    v-else
                    color="#C8D5EA"
                    size="24"
                    title="收起"
                  />
                </div>
              </div>
              <span v-else>暂无映射</span>
            </template>
          </Table>
          <!-- <list-tables
            ref="dataTables"
            :height="550"
            :border="true"
            :hidePage="false"
            :loadPageData="loadPageData"
            :columns="columns"
          >
            <template slot-scope="{ row }" slot="name">{{
              row.fromServer.name
            }}</template>
            <template slot-scope="{ row }" slot="sourceProtocol">
              <div v-if="row.source == 'rabbitMQ' || row.source == 'emqx'">
                <div class="list-content">
                  <span class="list-nature">协议：</span
                  >{{ row.fromServer.protocol }}
                </div>
                <div class="list-content">
                  <span class="list-nature">ip：</span>{{ row.fromServer.ip }}
                </div>
                <div class="list-content">
                  <span class="list-nature">端口：</span
                  >{{ row.fromServer.port }}
                </div>
                <div class="list-content">
                  <span class="list-nature">topic：</span
                  >{{ row.fromServer.topic }}
                </div>
              </div>
              <template v-else>
                <div class="list-content">
                  <span class="list-nature">服务地址：</span
                  >{{ row.permission }}
                </div>
                <div class="list-content">
                  <span class="list-nature">服务地址：</span
                  >{{ row.permission }}
                </div>
              </template>
            </template>
            <template slot-scope="{ row }" slot="newwork">
              <div v-if="Number(row.networkType)">
                <div class="list-content">
                  <span class="list-nature">账号：</span
                  >{{ row.fromServer.vpnAccount }}
                </div>
                <div class="list-content">
                  <span class="list-nature">密码：</span
                  >{{ row.fromServer.vpnAccount }}
                </div>
              </div>
              <div v-else>
                <div class="list-content">
                  <span class="list-nature">地址：</span
                  >{{ row.fromServer.retry }}
                </div>
              </div>
            </template>
            <template slot-scope="{ row }" slot="map">
              <div class="fold-box" v-if="row.map">
                <div
                  ref="mapNode"
                  class="map-box"
                  :style="{
                    overflow: isFold ? 'hidden' : '',
                    height: isFold ? '90px' : '',
                  }"
                >
                  <div
                    v-for="(item, index) in ceList"
                    :key="index"
                    class="list-content-box"
                  >
                    <div class="list-content">
                      <span class="list-nature">选择数据源：</span
                      >{{ item.name1 }}
                    </div>
                    <div class="list-content">
                      <span class="list-nature">IP地址：</span>{{ item.name2 }}
                    </div>
                    <div class="list-content">
                      <span class="list-nature"> 端口：</span>
                      {{ item.name3 }}
                    </div>
                  </div>
                </div>
                <div class="source-status">
                  <div class="status-radius"></div>
                </div>
                <div @click="handleFold" class="fold">
                  <Icon
                    type="ios-arrow-down"
                    v-if="isFold"
                    color="#C8D5EA"
                    size="24"
                    title="展开"
                  />
                  <Icon
                    type="ios-arrow-up"
                    v-else
                    color="#C8D5EA"
                    size="24"
                    title="收起"
                  />
                </div>
              </div>
              <span v-else>暂无映射</span>
            </template>
          </list-tables> -->
        </div>
      </div>
      <div class="page-modal-data-source-box page-content-common-box">
        <div class="page-title-common">
          <div class="page-title-name">
            <svg-icon name="title-icon" size="30" color="#B2B2B2"></svg-icon>
            <span class="title-name">{{ modalTitle }}</span>
          </div>
        </div>
        <div class="page-modal-content">
          <Form
            ref="formValidate"
            :model="formValidate"
            :rules="ruleValidate"
            :label-width="140"
            class="page-modal-form"
          >
            <FormItem label="数据源名称" prop="name">
              <Input v-model="formValidate.name" placeholder=""></Input>
            </FormItem>
            <FormItem label="选择数据源协议" prop="type">
              <Select
                v-model="formValidate.type"
                placeholder=""
                @on-change="handleType"
              >
                <Option
                  :value="item.value"
                  v-for="(item, index) in typeProtocol"
                  :key="index"
                  >{{ item.label }}</Option
                >
              </Select>
            </FormItem>
            <div class="border-layout" v-show="formValidate.type">
              <!-- AMQP start -->
              <template v-if="formValidate.type === 'AMQP'">
                <FormItem label="用户名" prop="username">
                  <Input v-model="formValidate.username" placeholder=""></Input>
                </FormItem>
                <FormItem label="密码" prop="password">
                  <Input
                    v-model="formValidate.password"
                    placeholder=""
                    type="password"
                    autocomplete="new-password"
                  ></Input>
                </FormItem>
              </template>
              <!-- AMQP end -->

              <!-- Aliyun_RocketMQ start -->
              <template v-if="formValidate.type === 'Aliyun_RocketMQ'">
                <FormItem label="secretKey" prop="secretKey">
                  <Input
                    v-model="formValidate.secretKey"
                    type="text"
                    placeholder=""
                  ></Input>
                </FormItem>
                <FormItem label="accessKey" prop="accessKey">
                  <Input
                    v-model="formValidate.accessKey"
                    placeholder=""
                  ></Input>
                </FormItem>
              </template>
              <!-- Aliyun_RocketMQ end -->

              <!-- EMQX start -->
              <template v-if="formValidate.type === 'MQTT'">
                <FormItem label="用户名">
                  <Input v-model="formValidate.username" placeholder=""></Input>
                </FormItem>
                <FormItem label="密码">
                  <Input
                    v-model="formValidate.password"
                    placeholder=""
                    type="password"
                    autocomplete="new-password"
                  ></Input>
                </FormItem>
              </template>
              <!-- EMQX end -->

              <!-- RocketMQ start-->
              <template v-if="formValidate.type === 'RocketMQ'">
                <FormItem label="单次最大消费条数">
                  <Input v-model="formValidate.total" placeholder=""></Input>
                </FormItem>
                <FormItem label="重试次数">
                  <Input
                    v-model="formValidate.retry"
                    placeholder=""
                    type="password"
                    autocomplete="new-password"
                  ></Input>
                </FormItem>
              </template>
              <!-- RocketMQ end -->

              <!-- HTTP RocketMQ 阿里云RocketMQ-->
              <template
                v-if="
                  formValidate.type === 'HTTP' ||
                  formValidate.type === 'RocketMQ' ||
                  formValidate.type === 'Aliyun_RocketMQ'
                "
              >
                <FormItem label="协议地址" prop="cluster">
                  <Input v-model="formValidate.cluster" placeholder=""></Input>
                </FormItem>
              </template>

              <!-- MQTT AMQP-->
              <template
                v-if="
                  formValidate.type === 'MQTT' || formValidate.type === 'AMQP'
                "
              >
                <FormItem
                  :label="
                    formValidate.type === 'AMQP' ? 'VirtualHost' : 'Client ID'
                  "
                  prop="clientName"
                >
                  <Input
                    v-model="formValidate.clientName"
                    placeholder=""
                    autocomplete="new_password"
                  ></Input>
                </FormItem>
                <FormItem label="协议" prop="protocol">
                  <Input v-model="formValidate.protocol" placeholder=""></Input>
                </FormItem>
                <FormItem label="ip" prop="ip">
                  <Input
                    v-model="formValidate.ip"
                    placeholder=""
                    autocomplete="new_password"
                  ></Input>
                </FormItem>
                <FormItem label="端口" prop="port">
                  <Input v-model="formValidate.port" placeholder=""></Input>
                </FormItem>
              </template>

              <!-- 阿里云RocketMQ rocketMQ  -->
              <template
                v-if="
                  formValidate.type === 'Aliyun_RocketMQ' ||
                  formValidate.type === 'RocketMQ'
                "
              >
                <FormItem label="所属组" prop="group">
                  <Input v-model="formValidate.group" placeholder=""></Input>
                </FormItem>
                <FormItem label="tag" prop="tag">
                  <Input v-model="formValidate.tag" placeholder=""></Input>
                </FormItem>
              </template>

              <!-- MQTT  AMQP 阿里云RocketMQ  RocketMQ-->
              <template v-if="formValidate.type !== 'HTTP'">
                <FormItem label="topic" prop="topic">
                  <Input v-model="formValidate.topic" placeholder=""></Input>
                </FormItem>
                <FormItem label="其他参数" prop="defaultParam">
                  <Input
                    v-model="formValidate.defaultParam"
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 5 }"
                    placeholder=""
                  ></Input>
                </FormItem>
              </template>
            </div>

            <FormItem
              label="选择网络"
              prop="networkType"
              :class="formValidate.networkType ? 'margin-top-10' : ''"
            >
              <Select v-model="formValidate.networkType" placeholder="">
                <Option
                  :value="item.value"
                  v-for="(item, index) in networkList"
                  :key="index"
                  >{{ item.label }}</Option
                >
              </Select>
            </FormItem>
            <template v-if="formValidate.networkType === '0'">
              <FormItem label="VPN账号" prop="vpnAccount">
                <Input v-model="formValidate.vpnAccount" placeholder=""></Input>
              </FormItem>
              <FormItem label="VPN密码" prop="vpnPassword">
                <Input
                  v-model="formValidate.vpnPassword"
                  placeholder=""
                  type="password"
                  autocomplete="vpnPassword"
                ></Input>
              </FormItem>
            </template>
          </Form>
          <div class="page-modal-footer">
            <Button type="primary" @click="handleSubmit('formValidate')">{{
              btnTitle
            }}</Button>
            <Button
              @click="handleReset('formValidate')"
              style="margin-left: 8px"
              >重置</Button
            >
          </div>
        </div>
      </div>
    </div>
    <map-modal ref="mapNode"></map-modal>
  </div>
</template>
<script>
import SvgIcon from "@/components/svg-icon/svg-icon";
import ListTables from "@/components/tables/tables";
import MapModal from "./map-modal";
import {
  getSoruceList,
  saveSoruce,
  delSoruce,
  testSoruce,
} from "@/api/dataSourceApi";
export default {
  name: "",
  components: { ListTables, SvgIcon, MapModal },
  data() {
    return {
      ceList: [
        // {
        //   name1: "二绕数据源1",
        //   name2: "192.168.0.0",
        //   name3: "9990"
        // },
        // {
        //   name1: "二绕数据源2",
        //   name2: "192.168.0.0",
        //   name3: "9990"
        // },
        // {
        //   name1: "二绕数据源3",
        //   name2: "192.168.0.0",
        //   name3: "9990"
        // }
      ],
      dataList: [
        // {
        //   name: "二绕车辆协同数据",
        //   permission: "192.168.0.0",
        //   network: 1,
        //   map: 1,
        //   source: "rabbitMQ"
        // },
        // {
        //   name: "二绕车辆协同数据",
        //   permission: "192.168.0.0",
        //   network: 0,
        //   map: 0,
        //   source: "rocketMQ"
        // }
      ],
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center",
        },
        {
          title: "数据源名称",
          slot: "name",
        },
        {
          title: "选择数据源协议",
          slot: "sourceProtocol",
          // filterMultiple: false,
          // filters: [
          //   {
          //     label: "MQ",
          //     value: 1,
          //   },
          //   {
          //     label: "HTTP",
          //     value: 2,
          //   },
          // ],
          // filterMethod(value, row) {
          //   console.log(value, row, 22);
          //   if (value === 1) {
          //     return row.age > 25;
          //   } else if (value === 2) {
          //     return row.age < 25;
          //   }
          // },
        },
        {
          title: "选择网络",
          slot: "newwork",
          // filterMultiple: false,
          // filters: [
          //   {
          //     label: "MQ",
          //     value: 1,
          //   },
          //   {
          //     label: "HTTP",
          //     value: 2,
          //   },
          // ],
          // filterMethod(value, row) {
          //   return row.name.indexOf(value) > -1;
          // },
        },
        {
          title: "映射",
          slot: "map",
        },
      ],
      isFold: true,
      formValidate: {
        // name: "",
        // accessKey: "", //阿里云RocketMq认证参数accesKey
        // secretKey: "",
        // clientName: "", //客户端连接名称
        // cluster: "", //服务地址（协议+ip+端口或ip+端口）
        // type: "", //MQ映射类型
        // topic: "", //mq消费topic
        // group: "", //mq消费的groupId
        // tag: "", //mq消费的tag
        // ip: "", //ip地址
        // port: 0, //端口
        // username: "", //用户名
        // password: "", //密码
        // protocol: "", //协议
        // retry: "", //重试次数
        // networkType: "", //网络类型
        // vpnAccount: "", //vpn账号
        // vpnPassword: "", //vpn密码
        // defaultParam: "{}"
      },
      ruleValidate: {
        name: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        type: [
          {
            required: true,
            message: "请选择",
            trigger: "change",
          },
        ],
        cluster: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        protocol: [
          {
            required: true,
            message: "请选择",
            trigger: "change",
          },
        ],
        ip: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        port: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        group: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        tag: [
          {
            required: true,
            message: "请选择",
            trigger: "change",
          },
        ],
        topic: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        networkType: [
          {
            required: true,
            message: "请选择",
            trigger: "change",
          },
        ],
        username: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        clientName: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        secretKey: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
        accessKey: [
          {
            required: true,
            message: "请输入",
            trigger: "blur",
          },
        ],
      },
      btnTitle: "消息检测",
      modalTitle: "添加数据源",
      typeProtocol: [
        {
          label: "RabbitMQ",
          value: "AMQP",
        },
        {
          label: "阿里云RocketMQ",
          value: "Aliyun_RocketMQ",
        },
        {
          label: "RocketMQ",
          value: "RocketMQ",
        },
        {
          label: "EMQX",
          value: "MQTT",
        },
        {
          label: "HTTP",
          value: "HTTP",
        },
      ],

      networkList: [
        {
          label: "内网",
          value: "0",
        },
        {
          label: "外网",
          value: "1",
        },
      ],
      selectList: [],
    };
  },
  created() {},
  mounted() {
    this.getSoruceList();
  },
  computed: {},
  methods: {
    // 选中列表
    handleSelect(list) {
      console.log(list, "选中列表");
      this.selectList = list;
    },
    // 修改数据源
    handleModifySource() {
      if (this.selectList.length > 1) {
        this.$Message.warning("只能选择一条数据");
      }
      this.modalTitle = "修改数据源";
      console.log(this.selectList[0], 999);
      this.formValidate = this.selectList[0].fromServer;
      this.formValidate.port = String(this.formValidate.port);
    },
    // 修改映射
    handleModifyMap() {
      this.$refs.mapNode.show();
    },
    // 删除数据源
    handleDelectSource() {
      let ids = this.selectList.map((item) => {
        return item.fromServer.id;
      });
      console.log("删除数据源", ids);

      delSoruce(ids)
        .then((res) => {
          if (res.code === 200) {
            this.formValidate = {};
            this.$Message.success("删除成功!");
          } else {
            this.$Message.error("删除数据源失败!");
          }
        })
        .catch((err) => {
          this.$Message.error("删除数据源失败!");
        });
    },
    // 删除映射
    handleDelectMap() {
      console.log("删除映射");
    },
    // 添加映射
    handleAddMap() {
      console.log("添加映射");
      this.$refs.mapNode.show();
    },
    getSoruceList() {
      getSoruceList({
        pageNo: 1,
        pageSize: 20,
      })
        .then((req) => {
          console.log("22222", req.data);
          this.dataList = req.data.list;
        })
        .catch((err) => {});
    },
    // 列表数据刷新
    loadPageData(pageNo, pageSize, search) {
      return new Promise(function (resolve, reject) {
        getSoruceList({
          pageNo: pageNo,
          pageSize: pageSize,
        })
          .then((req) => {
            console.log("22222", req.data);
            // 处理数据，如果存在异常则提示
            resolve(req.data);
          })
          .catch((err) => {
            // 异常处理,不用提示
            reject(err);
          });
      });
    },
    // 列表映射展开收起
    handleFold() {
      this.isFold = !this.isFold;
    },
    // 选择数据源协议改变
    handleType() {
      console.log("选择数据源协议改变");
      // this.formValidate = {
      //   clientName: "",
      //   username: "",
      //   password: "",
      //   secretKey: "",
      //   accessKey: "",
      //   total: "",
      //   retry: "",
      //   cluster: "",
      //   protocol: "",
      //   ip: "",
      //   port: "",
      //   group: "",
      //   tag: ""
      // };
    },
    // 提交 testSoruce saveSoruce
    handleSubmit(name) {
      this.$refs["formValidate"].validate((valid) => {
        if (valid) {
          console.log(this.formValidate, "this.formValidate");

          saveSoruce(this.formValidate)
            .then((res) => {
              if (res.code === 200) {
                this.selectList = [];
                this.handleReset();
                this.$Message.success("保存成功!");
              } else {
                this.$Message.error("保存数据源失败!");
              }
            })
            .catch((err) => {
              console.log(err);
              this.$Message.error("服务器异常，请联系管理员!");
            });
        } else {
        }
      });
    },
    // 重置
    handleReset() {
      this.$refs["formValidate"].resetFields();
      this.formValidate = {};
    },
  },
};
</script>
<style lang="less" scoped>
@import "./home.less";
</style>
