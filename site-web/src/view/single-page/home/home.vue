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
          <!-- <Table
            ref="selection"
            :columns="columns"
            :data="dataList"
            class="table-box"
          >
            <template slot-scope="{ row, index }" slot="permission">
              <div v-if="row.source == 'rabbitMQ' || row.source == 'emqx'">
                <div class="list-content">
                  <span class="list-nature">协议：</span>{{ row.permission }}
                </div>
                <div class="list-content">
                  <span class="list-nature">ip：</span>{{ row.permission }}
                </div>
                <div class="list-content">
                  <span class="list-nature">端口：</span>{{ row.permission }}
                </div>
                <div class="list-content">
                  <span class="list-nature">topic：</span>{{ row.permission }}
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
            <template slot-scope="{ row, index }" slot="name1">
              <div v-if="row.network">
                <div class="list-content">
                  <span class="list-nature">账号：</span>{{ row.permission }}
                </div>
                <div class="list-content">
                  <span class="list-nature">密码：</span>{{ row.permission }}
                </div>
              </div>
              <div v-else>
                <div class="list-content">
                  <span class="list-nature">地址：</span>{{ row.permission }}
                </div>
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="permission1">
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
          </Table> -->
          <list-tables ref="dataTables" :height="550" :border="true" :hidePage="true" :loadPageData="loadPageData" :columns="columns"></list-tables>
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
              <Select v-model="formValidate.type" placeholder="" @on-change="handleType">
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
                <FormItem label="客户端连接名称" prop="clientName">
                  <Input
                    v-model="formValidate.clientName"
                    placeholder=""
                    autocomplete="new_password"
                  ></Input>
                </FormItem>

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
              :class="formValidate.source ? 'margin-top-10' : ''"
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
import { getSoruceList, saveSoruce } from "@/api/dataSourceApi";
export default {
  name: "",
  components: { ListTables, SvgIcon, MapModal },
  data() {
    return {
      ceList: [
        {
          name1: "二绕数据源1",
          name2: "192.168.0.0",
          name3: "9990"
        },
        {
          name1: "二绕数据源2",
          name2: "192.168.0.0",
          name3: "9990"
        },
        {
          name1: "二绕数据源3",
          name2: "192.168.0.0",
          name3: "9990"
        }
      ],
      dataList: [
        {
          name: "二绕车辆协同数据",
          permission: "192.168.0.0",
          network: 1,
          map: 1,
          source: "rabbitMQ"
        },
        {
          name: "二绕车辆协同数据",
          permission: "192.168.0.0",
          network: 0,
          map: 0,
          source: "rocketMQ"
        }
      ],
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          title: "数据源名称",
          key: "name"
        },
        {
          title: "选择数据源协议",
          slot: "permission",
          filterMultiple: false,
          filters: [
            {
              label: "MQ",
              value: 1
            },
            {
              label: "HTTP",
              value: 2
            }
          ],
          filterMethod(value, row) {
            console.log(value, row, 22);
            if (value === 1) {
              return row.age > 25;
            } else if (value === 2) {
              return row.age < 25;
            }
          }
        },
        {
          title: "选择网络",
          slot: "name1",
          filterMultiple: false,
          filters: [
            {
              label: "MQ",
              value: 1
            },
            {
              label: "HTTP",
              value: 2
            }
          ],
          filterMethod(value, row) {
            return row.name.indexOf(value) > -1;
          }
        },
        {
          title: "映射",
          key: "permission",
          slot: "permission1"
        }
      ],
      isFold: true,
      formValidate: {
        name: "",
        type: "",
        networkType: ""
      },
      ruleValidate: {
        name: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        type: [
          {
            required: true,
            message: "请选择",
            trigger: "change"
          }
        ],
        cluster: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        protocol: [
          {
            required: true,
            message: "请选择",
            trigger: "change"
          }
        ],
        ip: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        port: [
          {
            required: true,
            message: "请选择",
            trigger: "change"
          }
        ],
        group: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        tag: [
          {
            required: true,
            message: "请选择",
            trigger: "change"
          }
        ],
        topic: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        networkType: [
          {
            required: true,
            message: "请选择",
            trigger: "change"
          }
        ],
        username: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        password: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        clientName: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        secretKey: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ],
        accessKey: [
          {
            required: true,
            message: "请输入",
            trigger: "blur"
          }
        ]
      },
      btnTitle: "添加",
      modalTitle: "添加数据源",
      typeProtocol: [
        {
          label: "RabbitMQ",
          value: "AMQP"
        },
        {
          label: "阿里云RocketMQ",
          value: "Aliyun_RocketMQ"
        },
        {
          label: "RocketMQ",
          value: "RocketMQ"
        },
        {
          label: "EMQX",
          value: "MQTT"
        },
        {
          label: "HTTP",
          value: "HTTP"
        }
      ],

      networkList: [
        {
          value: "0",
          label: "内网"
        },
        {
          value: "1",
          label: "外网"
        }
      ]
    };
  },
  created() {},
  mounted() {},
  computed: {},
  methods: {
    // 修改数据源
    handleModifySource() {},
    // 修改映射
    handleModifyMap() {
      this.$refs.mapNode.show();
    },
    // 删除数据源
    handleDelectSource() {
      console.log("删除数据源");
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
    // 列表数据刷新
    loadPageData(pageNo, pageSize, search) {
      return new Promise(function(resolve, reject) {
        getSoruceList({
          pageNo: pageNo,
          pageSize: pageSize
        })
          .then(req => {
            console.log("22222", req.data);
            // 处理数据，如果存在异常则提示
            resolve(req.data);
          })
          .catch(err => {
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
    // 提交
    handleSubmit(name) {
      this.$refs["formValidate"].validate(valid => {
        if (valid) {
          console.log(this.formValidate,"this.formValidate")
          saveSoruce(this.formValidate)
            .then(res => {
              if (req.code === 200) {
                this.$Message.success("保存成功!");
              } else {
                this.$Message.error("保存数据源失败!");
              }
            })
            .catch(err => {
              this.$Message.error("服务器异常，请联系管理员!");
            });
        } else {
        }
      });
    },
    // 重置
    handleReset(name) {
      this.formValidate = {};
      this.$refs["formValidate"].resetFields();
    }
  }
};
</script>
<style lang="less" scoped>
@import "./home.less";
</style>
