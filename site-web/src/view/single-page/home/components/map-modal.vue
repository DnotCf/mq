
<template>
  <Modal
    class="map-modal"
    width="480"
    v-model="isShow"
    class-name="vertical-center-modal"
  >
    <div slot="header" style="color: #f60; text-align: center">
      <div class="page-title-common">
        <div class="page-title-name">
          <svg-icon name="title-icon" size="30" color="#B2B2B2"></svg-icon>
          <span class="title-name">{{ modalTitle }}</span>
        </div>
      </div>
    </div>
    <Form
      ref="formValidate"
      :model="formValidate"
      :rules="ruleValidate"
      :label-width="140"
      label-position="left"
    >
      <FormItem label="选择数据源" prop="fromServer.id">
        <Select
          v-model="formValidate.fromServer.id"
          placeholder=""
          @on-change="handleType"
        >
          <Option
            :value="item.value"
            v-for="(item, index) in sourceList"
            :key="index"
            >{{ item.label }}</Option
          >
        </Select>
      </FormItem>
      <FormItem label="IP映射地址" prop="toServer.ip">
        <Input v-model="formValidate.toServer.ip" placeholder=""></Input>
      </FormItem>
      <FormItem label="端口" prop="toServer.port">
        <Input v-model="formValidate.toServer.port" placeholder="" number></Input>
      </FormItem>
      <FormItem label="映射数据源协议" prop="toServer.type">
        <Select v-model="formValidate.toServer.type" placeholder="">
          <Option
            :value="item.value"
            v-for="(item, index) in typeProtocol"
            :key="index"
            >{{ item.label }}</Option
          >
        </Select>
      </FormItem>
      <FormItem label="topic" prop="toServer.topic">
        <Input v-model="formValidate.toServer.topic" placeholder=""></Input>
      </FormItem>
      <FormItem label="映射给予方" prop="toServer.name">
        <Input v-model="formValidate.toServer.name" placeholder=""></Input>
      </FormItem>
      <FormItem label="认证账号" prop="username">
        <Input v-model="formValidate.toServer.username" placeholder=""></Input>
      </FormItem>
      <FormItem label="认证密码" prop="password">
        <Input
          v-model="formValidate.toServer.password"
          type="password"
          autocomplete="new-password"
          placeholder=""
        ></Input>
      </FormItem>
      <FormItem label="过期时间">
        <FormItem prop="date">
          <Row>
            <Col span="24">
              <DatePicker
                type="date"
                style="width: 100%"
                placeholder=""
                v-model="formValidate.date"
              ></DatePicker> </Col
          ></Row>
        </FormItem>
      </FormItem>
    </Form>
    <div slot="footer">
      <template>
        <Button
          type="primary"
          @click="handleSubmit(1)"
          :loading="isLoad"
          v-if="isAdd"
          >{{ btnTitle }}</Button
        >
        <Button type="primary" @click="handleSubmit(0)" :loading="isLoad" v-else
          >消息检测</Button
        >
        <Button @click="handleReset" style="margin-left: 8px">重置</Button>
      </template>
      <div v-show="isTitleShow" class="demo-load-box">
        <div class="demo-load">
          <Spin fix>
            <svg-icon
              v-if="isLoad"
              name="icon-loading"
              class="demo-spin-icon-load"
            ></svg-icon>
            <Icon type="ios-close-circle" color="red" v-else-if="!isSuccess" />
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
  </Modal>
</template>
<script>
import dataSourceApi from "@/api/dataSourceApi";

export default {
  name: "MapModal",
  props: {},
  data() {
    return {
      isShow: false,
      isLoad: false,
      btnTitle: "添加",
      formValidate: {
        fromServer: {},
        toServer: {}
      },
      ruleValidate: {
        "toServer.name": [
          { required: true, message: "请输入", trigger: "blur" }
        ],
        "toServer.ip": [{ required: true, message: "请输入", trigger: "blur" }],
        "toServer.topic": [
          { required: true, message: "请输入", trigger: "blur" }
        ],
        "toServer.port": [
          { required: true, message: "请输入", trigger: "blur", type: "number" }
        ],
        "fromServer.id": [
          { required: true, message: "请选择", trigger: "change" }
        ],
        "toServer.type": [
          { required: true, message: "请选择", trigger: "change" }
        ],
        date: [
          {
            required: true,
            type: "date",
            message: "请选择",
            trigger: "change"
          }
        ]
      },
      sourceList: [],
      isTitleShow: false,
      isSuccess: false,
      checkoutTitle: "消息转发正在检验中，请稍后…",
      isType: true,
      isSource: false,
      isAdd: false,
      modalTitle: "",
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
      ]
    };
  },
  mounted() {},
  methods: {
    show(val, row) {
      this.handleReset();
      this.getSourceData();
      if (val) {
        this.modalTitle = "修改映射";
        this.formValidate = row;
        this.formValidate.date = row.expireTime;
        this.btnTitle = "修改";
      } else {
        this.modalTitle = "添加映射";
      }
      this.isShow = true;
    },
    hide() {
      this.isShow = false;
      this.handleReset();
    },
    // 选择类型
    handleType(val) {
      if (!val) return;
      let rowData = this.sourceList.find(item => item.value === val);
      this.formValidate.fromServer = rowData.fromServer;
    },
    handleSubmit(val) {
      this.$refs["formValidate"].validate(valid => {
        if (valid) {
          this.isLoad = true;
          let isAdd = val;
          this.isTitleShow = true;
          let param = {};
          let port = "";
          if (isAdd) {
            port = "saveMap";
            param = this.formValidate;
            param.expireTime = new Date(this.formValidate.date).getTime(); //过期时间
            param.fromTopic = this.formValidate.fromServer.topic; //数据源topic
            param.toTopic = this.formValidate.toServer.topic;
          } else {
            port = "testMap";
            param = this.formValidate.toServer;
          }
          dataSourceApi[port](param)
            .then(res => {
              this.isLoad = false;
              if (res.code === 200) {
                let data = res.data;
                if (data) {
                  this.selectList = [];
                  if (val) {
                    this.checkoutTitle = "消息转发正在检验中，请稍后…";
                    this.isTitleShow = false;
                    this.hide();
                    this.isAdd = false;
                    this.$Message.success(this.btnTitle + "成功!");
                    this.btnTitle = "添加";

                    this.$parent.$refs.dataTables.refreshPageData();
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
                this.$Message.error("保存数据源失败!");
              }
            })
            .catch(err => {
              this.isLoad = false;
              this.$Message.error("服务器异常，请联系管理员!");
            });
        }
      });
    },
    handleReset() {
      this.formValidate = {
        fromServer: {},
        toServer: {}
      };
      this.isTitleShow = false;
      this.isAdd = false;
      this.isLoad = false;
      this.$refs["formValidate"].resetFields();
    },
    getSourceData() {
      dataSourceApi
        .getSoruceList({
          pageNo: "",
          pageSize: ""
        })
        .then(req => {
          if (req.code === 200) {
            let data = req.data;
            this.sourceList = data.list.map(item => {
              item.label = item.fromServer.name;
              item.value = item.fromServer.id;
              return item;
            });
          } else {
            this.$Message.error("获取数据源失败!");
          }
        })
        .catch(err => {
          this.$Message.error("服务器异常请联系管理员!");
        });
    }
  }
};
</script>
<style lang="less" scoped>
.map-modal {
  /deep/.ivu-modal-wrap {
    .ivu-modal {
      .ivu-modal-content {
        border-radius: 14px;

        .ivu-modal-header {
          padding: 0 !important;
        }
        .ivu-modal-footer {
          border-top: 1px solid transparent;
          padding: 0;
          height: 63px;
          padding-right: 30px;
        }
        .ivu-modal-body {
          padding: 32px 30px;
        }
      }
    }
  }
}

.vertical-center-modal {
  display: flex;
  align-items: center;
  justify-content: center;

  .ivu-modal {
    top: 0;
  }
}
.page-title-common {
  width: 100%;
  height: 50px;
  background: #d7e5fc;
  border-radius: 14px 14px 0 0;
  padding: 0 30px 0 15px;
  justify-content: space-between;
  display: flex;
  align-items: center;
  .page-title-name {
    display: flex;
    align-items: center;
    .title-name {
      margin-left: -5px;
      font-size: 16px;
      font-weight: 600;
      color: #060606;
      letter-spacing: 3px;
    }
  }
  .btn-layout {
    .dropdown-box {
      .ivu-dropdown-item:hover {
        background: #d7e5fc;
        color: #4567a3;
      }
    }
    .btn-box {
      margin-left: 16px;
      line-height: 32px;
      text-align: center;
      height: 32px;
      background: linear-gradient(303deg, #387bf4 0%, #6bc7fa 100%);
      border-radius: 7px;
      color: #ffffff;
      font-size: 14px;
      letter-spacing: 4px;
    }
  }
}
.demo-load-box {
  height: 30px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-end;

  .demo-spin-icon-load {
    animation: ani-demo-spin 1s linear infinite;
  }
  @keyframes ani-demo-spin {
    from {
      transform: rotate(0deg);
    }
    50% {
      transform: rotate(180deg);
    }
    to {
      transform: rotate(360deg);
    }
  }
  .checkoutTitle {
    font-size: 12px;
    letter-spacing: 2px;
  }
  .blue {
    color: #397cf5;
  }
  .green {
    color: #8ddd00;
  }
  .red {
    color: red;
  }
  .demo-load {
    width: 25px;
    height: 100%;
    position: relative;
  }
  .demo-spin-col {
    height: 100px;
    position: relative;
    border: 1px solid #eee;
  }
}
</style>
