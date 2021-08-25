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
            <Button
              type="primary"
              class="btn-box"
              @click="handleEnable(enableVal)"
            >
       {{enableVal==1?'停用所有映射':"启用所有映射"}}
            </Button>
            <Poptip class="home-poptip" placement="bottom">
              <Button type="primary" class="btn-box">
                修改
                <Icon type="ios-arrow-down"></Icon>
              </Button>
              <div slot="title"></div>
              <div slot="content">
                <div class="poptip-item" @click="handleModifySource">
                  数据源
                </div>
                <div class="poptip-item" @click="handleModifyMap">映射</div>
              </div>
            </Poptip>
            <Poptip class="home-poptip" placement="bottom">
              <Button type="primary" class="btn-box">
                删除
                <Icon type="ios-arrow-down"></Icon>
              </Button>
              <div slot="content">
                <div @click.stop="handleDelectSource">
                  <Poptip
                    v-model="isSource"
                    placement="bottom"
                    class="del-poptip cs"
                    confirm
                    :disabled="!isSource"
                    @on-ok="handleDelSource"
                  >
                    <div class="poptip-item">数据源</div>
                    <div slot="title" style="width: 150px; padding-top: 10px">
                      <svg-icon name="icon-warn"></svg-icon>
                      <span class="gray"> 你确定要删除吗？</span>
                    </div>
                  </Poptip>
                </div>
                <div @click="handleDelectMap">
                  <Poptip
                    v-model="isMap"
                    placement="bottom"
                    class="del-poptip"
                    :disabled="!isSource"
                    @on-ok="handleDelMap"
                    confirm
                  >
                    <div class="poptip-item">映射</div>
                    <div slot="title" style="width: 150px; padding-top: 10px">
                      <svg-icon name="icon-warn"></svg-icon>
                      <span class="gray"> 你确定要删除吗？</span>
                    </div>
                  </Poptip>
                </div>
              </div>
            </Poptip>
            <Button type="primary" class="btn-box" @click="handleAddMap">
              添加映射
            </Button>
          </div>
        </div>
        <div class="page-list-box">
          <Table
            :height="565"
            :data="list"
            :columns="columns"
            class="table-box"
            @on-selection-change="onSelectionChange"
          >
            <template slot-scope="{ row, index }" slot="name">{{
              row.fromServer.name
            }}</template>
            <template slot-scope="{ row, index }" slot="sourceProtocol">
              <div
                :style="{
                  overflow: row.isFold ? '' : 'hidden',
                  height:
                    row.isFold || row.fromServer.type === 'HTTP' ? '' : '95px',
                }"
                class="fold-box"
              >
                <div
                  @click="handleFold(row)"
                  class="fold"
                  v-show="row.fromServer.type !== 'HTTP'"
                >
                  <Icon
                    type="ios-arrow-down"
                    v-if="!row.isFold"
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
                  <table-item
                    label="数据源协议："
                    :value="row.fromServer.name"
                  ></table-item>
                <table-item-common :value="row.fromServer"></table-item-common>
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="network">
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
            </template>
            <template slot-scope="{ row, index }" slot="map">
              <div class="fold-box" v-if="row.toServer">
                <div
                  class="map-box"
                  :style="{
                    overflow: row.isFold ? '' : 'hidden',
                    height: row.isFold ? '' : '95px',
                  }"
                >
                  <table-item
                    label="映射给予方："
                    :value="row.toServer.name"
                  ></table-item>
                   <table-item
                    label="映射数据源协议："
                    :value="row.toServer.type"
                  ></table-item>
                  <table-item
                    label="过期时间："
                    :value="row.expireTime"
                  ></table-item>
                  <table-item-common :value="row.toServer"></table-item-common>
                 
                </div>
                <div class="source-status">
                  <div
                    :class="
                      row.status == 1
                        ? 'green'
                        : row.status == 4
                        ? 'orange'
                        : 'red'
                    "
                    class="status-radius"
                  ></div>
                </div>
                <div @click="handleFold(row)" class="fold">
                  <Icon
                    type="ios-arrow-down"
                    v-if="!row.isFold"
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
              <span v-else class="no-map">暂无映射</span>
            </template> 
            <template slot-scope="{ row, index }" slot="action">
              
              <Button @click="handleItemEnable(row.status,row.id)">{{row.status==1?'停用':'启用'}}</Button>
            </template>
          </Table>
          <div class="tables-pager">
            <page
              :total="pageTotal"
              :page-size="pageSize"
              @on-change="onPageNoChange"
              @on-page-size-change="onPageSizeChange"
              show-total
              show-elevator
            />
          </div>
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
              <form-protocol :value="formValidate"></form-protocol>
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
                  autocomplete="new-password"
                ></Input>
              </FormItem>
            </template>
          </Form>
          <div class="page-modal-footer">
            <div class="btn-box">
              <Button
                type="primary"
                @click="handleSubmit(1)"
                :loading="isLoad"
                v-if="isAdd"
                >{{ btnTitle }}</Button
              >
              <Button
                type="primary"
                @click="handleSubmit(0)"
                :loading="isLoad"
                v-else
                >消息检测</Button
              >
              <Button @click="handleReset" style="margin-left: 8px"
                >重置</Button
              >
            </div>
            <div v-show="isShow" class="demo-load-box">
              <div class="demo-load">
                <Spin fix>
                  <svg-icon
                    v-if="isLoad"
                    name="icon-loading"
                    class="demo-spin-icon-load"
                  ></svg-icon>
                  <Icon
                    type="ios-close-circle"
                    color="red"
                    v-else-if="!isSuccess"
                  />
                  <svg-icon v-else-if="isAdd" name="icon-success"></svg-icon>
                </Spin>
              </div>
              <span
                class="checkoutTitle"
                :class="isLoad ? 'blue' : isSuccess ? 'green' : 'red'"
                >{{ checkoutTitle }}</span
              >
            </div>
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
import MapModal from "./components/map-modal";
import FormProtocol from "./components/form-protocol";
import TableItemCommon from "./components/table-item-common";
import TableItem from "./components/table-item";

import dataSourceApi from "@/api/dataSourceApi";
export default {
  name: "",
  components: {
    ListTables,
    SvgIcon,
    MapModal,
    FormProtocol,
    TableItemCommon,
    TableItem
  },
  data() {
    return {
      columns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          title: "数据源名称",
          slot: "name",
          align: "center",
          width: 100
        },
        {
          title: "选择数据源协议",
          slot: "sourceProtocol",
          filterMultiple: false,
          width: 350,
          filters: [
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
          filterMethod(value, row) {
            return row.fromServer.type == value;
          }
        },
        {
          title: "选择网络",
          slot: "network",
          filterMultiple: false,
          width: 200,

          filters: [
            {
              label: "内网",
              value: "0"
            },
            {
              label: "外网",
              value: "1"
            }
          ],
          filterMethod(value, row) {
            return row.fromServer.networkType.indexOf(value) > -1;
          }
        },
        {
          title: "映射",
          slot: "map",
          width: 350
        },
        {
          title: "最新同步时间",
          align: "center",
          key: "updateDate",
          width: 200
        },
        {
          title: "操作",
          align: "center",
          width: 100,
          slot: "action"
        }
      ],
      isFold: true,
      formValidate: {},
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
            message: "请输入",
            trigger: "blur",
            type: "number"
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
            message: "请输入",
            trigger: "blur"
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
            // validator: this.validatepasswd,
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
        ],
        vpnPassword: [
          {
            required: false,
            message: "请输入",
            // validator: this.validatepasswd,
            trigger: "blur"
          }
        ]
      },
      isAdd: false,
      modalTitle: "添加数据源",
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
      ],
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
        },
        {
          label: "Kafka",
          value: "Kafka"
        }
      ],

      networkList: [
        {
          label: "内网",
          value: "0"
        },
        {
          label: "外网",
          value: "1"
        }
      ],
      isLoad: false,
      isShow: false,
      isSuccess: false,
      btnTitle: "添加",
      checkoutTitle: "消息转发正在检验中，请稍后…",
      isSource: false,
      isMap: false,
      selectList: [],
      list: [],
      pageNo: 1,
      pageSize: 10,
      pageTotal: 0,
      enableVal: 1
    };
  },
  created() {},
  mounted() {
    this.getListData();
  },
  computed: {},
  methods: {
    getListData() {
      dataSourceApi
        .getMapList({
          pageNo: this.pageNo,
          pageSize: this.pageSize
        })
        .then(req => {
          if (req.code === 200) {
            let data = req.data;
            this.list = data.list.map(item => {
              item.isFold = false;
              return item;
            });
            this.pageTotal = data.count;
            this.enableVal = Number(this.list[0].status);
          } else {
            this.$Message.error(err.msg || "数据源列表获取失败!");
          }
        })
        .catch(err => {
          this.$Message.error(err.msg || "服务器异常请联系管理员!");
        });
    },
    // 分页回调
    onPageNoChange(pageNo) {
      this.pageNo = pageNo;
      this.getListData();
    },
    onPageSizeChange(pageSize) {
      this.pageSize = pageSize;
      this.getListData();
    },
    onSelectionChange(selection) {
      this.selectList = selection;
    },
    // 获取列表勾选数据
    getList() {
      if (!this.selectList.length) {
        this.isSource = false;
        this.isMap = false;
        this.$Message.warning("请选择数据");
        return false;
      } else {
        return true;
      }
    },
    // 修改数据源
    handleModifySource() {
      if (!this.getList()) {
        return;
      }

      if (this.selectList.length > 1) {
        return this.$Message.warning("只能选择一条数据");
      }
      this.modalTitle = "修改数据源";
      this.btnTitle = "修改";
      this.handleReset();

      this.formValidate = { ...this.selectList[0].fromServer };
    },
    // 修改映射
    handleModifyMap() {
      // 获取列表勾选数据
      if (!this.getList()) {
        return;
      }
      if (this.selectList.length > 1) {
        this.$Message.warning("只能选择一条数据");
        return;
      }
      if (!this.selectList[0].toServer) {
        this.$Message.warning("暂无映射");
        return;
      }
      this.$refs.mapNode.show();
      this.$refs.mapNode.formData.toServer = { ...this.selectList[0].toServer };
      this.$refs.mapNode.id = this.selectList[0].fromServer.id;
      this.$refs.mapNode.formData.fromServer = {
        ...this.selectList[0].fromServer
      };
      this.$refs.mapNode.formData.date = this.selectList[0].expireTime;
    },

    // 删除数据源
    handleDelectSource() {
      // 获取列表勾选数据
      if (!this.getList()) {
        return;
      }

      this.isSource = true;
    },
    handleDelSource() {
      let ids = this.selectList.map(item => {
        return item.fromServer.id;
      });
      dataSourceApi
        .delSoruce(ids)
        .then(res => {
          if (res.code === 200) {
            this.formValidate = {};
            this.$Message.success("删除成功!");
            // this.$refs.dataTables.refreshPageData();
            this.getListData();
          } else {
            this.$Message.error("删除数据源失败!");
          }
        })
        .catch(err => {
          this.$Message.error("服务器异常请联系管理员!");
        });
    },
    // 删除映射
    handleDelectMap() {
      // 获取列表勾选数据
      if (!this.getList()) {
        return;
      }

      if (!this.selectList[0].toServer) {
        this.$Message.warning("暂无映射");
        return;
      }
      this.isMap = true;
      console.log("删除映射");
    },
    handleDelMap() {
      let ids = this.selectList.map(item => {
        return item.id;
      });
      dataSourceApi
        .delMap(ids)
        .then(res => {
          if (res.code === 200) {
            this.formValidate = {};
            this.$Message.success("删除成功!");
            // this.$refs.dataTables.refreshPageData();
            this.getListData();
          } else {
            this.$Message.error("删除数据源失败!");
          }
        })
        .catch(err => {
          this.$Message.error("服务器异常请联系管理员!");
        });
    },
    // 启用停用所有映射
    handleEnable(val) {
      this.enableVal = !val;
      let port = "";
      if (val) {
        port = "stopAll";
      } else {
        port = "startAll";
      }
      dataSourceApi[port]()
        .then(res => {
          if (res.code === 200) {
            this.$Message.success(
              this.enableVal ? "启用所有映射" : "停用所有映射" + "成功"
            );
            this.getListData();
          } else {
            this.$Message.success(
              this.enableVal ? "启用所有映射" : "停用所有映射" + "失败"
            );
          }
        })
        .catch(err => {
          this.$Message.error(err.msg || "服务器异常，请联系管理员!");
        });
    },
    //启用停用单项
    handleItemEnable(val, id) {
      let port = "";
      if (Number(val)) {
        port = "stopItem";
      } else {
        port = "startItem";
      }
      dataSourceApi[port](id)
        .then(res => {
          if (res.code === 200) {
            this.$Message.success(this.enableVal ? "启用" : "停用" + "成功");
            this.getListData();
          } else {
            this.$Message.success(this.enableVal ? "启用" : "停用" + "失败");
          }
        })
        .catch(err => {
          this.$Message.error(err.msg || "服务器异常，请联系管理员!");
        });
    },
    // 添加映射
    handleAddMap() {
      this.$nextTick(() => {
        this.$refs.mapNode.show(0);
      });
    },
    // 校验密码
    validatepasswd(rule, value, callback) {
      let regExp = new RegExp(/^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$/);
      if (!value) {
        callback(new Error("请输入密码"));
      }
      if (!regExp.test(value)) {
        callback(new Error("请输入6-12包含数字、大小写的密码"));
      }
      callback();
    },
    // 列表数据刷新
    loadPageData(pageNo, pageSize, search) {
      return new Promise(function(resolve, reject) {
        dataSourceApi
          .getMapList({
            pageNo: pageNo,
            pageSize: pageSize
          })
          .then(req => {
            let data = req.data;
            data.list.map(item => {
              item.isFold = false;
              return item;
            });
            // 处理数据，如果存在异常则提示
            resolve(data);
          })
          .catch(err => {
            // 异常处理,不用提示
            reject(err);
          });
      });
    },
    // 列表映射展开收起
    handleFold(val) {
      val.isFold = !val.isFold;
    },
    // 选择数据源协议改变
    handleType(val) {
      if (!val) return;
      let initForm = {
        clientName: "",
        username: "",
        password: "",
        secretKey: "",
        accessKey: "",
        total: "",
        retry: "",
        cluster: "",
        protocol: "",
        ip: "",
        port: "",
        group: "",
        tag: "",
        topic: "",
        defaultParam: ""
      };

      this.formValidate = { ...this.formValidate, ...initForm };
      this.$refs["formValidate"].resetFields();
      console.log(this.formValidate, val);
    },
    // 提交
    handleSubmit(val) {
      this.$refs["formValidate"].validate(valid => {
        if (valid) {
          let isAdd = val;
          this.isLoad = true;
          this.isShow = true;
          let port = "";
          if (isAdd) {
            port = "saveSoruce";
          } else {
            port = "testSoruce";
          }
          dataSourceApi[port](this.formValidate)
            .then(res => {
              this.isLoad = false;
              if (res.code === 200) {
                let data = res.data;
                if (data) {
                  this.selectList = [];
                  if (val) {
                    this.checkoutTitle = "消息转发正在检验中，请稍后…";
                    this.isShow = false;
                    this.handleReset();
                    this.isAdd = false;
                    this.$Message.success(this.btnTitle + "成功!");
                    this.modalTitle = "添加数据源";
                    this.btnTitle = "添加";

                    // this.$refs.dataTables.refreshPageData();
                    this.getListData();
                  } else {
                    this.checkoutTitle = "消息转发检验成功！";
                    this.isAdd = true;
                    this.isSuccess = true;
                  }
                } else {
                  this.checkoutTitle = "消息转发检验失败！";
                  this.isAdd = false;
                  this.isSuccess = false;
                }
              } else {
                this.isLoad = false;
                this.isShow = false;
                this.$Message.error(res.msg || "保存数据源失败!");
                console.log(res);
              }
            })
            .catch(err => {
              console.log(err);
              this.isShow = false;
              this.isLoad = false;
              this.$Message.error(err.msg || "服务器异常，请联系管理员!");
            });
        }
      });
    },
    // 重置
    handleReset() {
      this.$refs["formValidate"].resetFields();
      this.formValidate = {};
      this.isShow = false;
      this.isAdd = false;
      this.isLoad = false;
      this.isSuccess = false;
    }
  }
};
</script>
<style lang="less" scoped>
@import "./home.less";
</style>
