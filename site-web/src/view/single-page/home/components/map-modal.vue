
<template>
    <Modal
     :closable="false"
       class="map-modal"
        v-model="isShow"
        class-name="vertical-center-modal">
        <div slot="header" style="color:#f60;text-align:center">
                <div class="page-title-common">
          <div class="page-title-name">
            <svg-icon name="title-icon" size="30" color="#B2B2B2"></svg-icon>
            <span class="title-name">{{ modalTitle }}</span>
          </div>
        </div>
        </div>
         <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="120">
         <FormItem label="选择数据源" prop="city">
            <Select v-model="formValidate.city" placeholder="">
                <Option value="beijing">New York</Option>
                <Option value="shanghai">London</Option>
                <Option value="shenzhen">Sydney</Option>
            </Select>
        </FormItem>
        <FormItem label="IP映射地址" prop="name">
            <Input v-model="formValidate.name" placeholder=""></Input>
        </FormItem>
          <FormItem label="topic" prop="name">
            <Input v-model="formValidate.name" placeholder=""></Input>
        </FormItem>
          <FormItem label="端口" prop="name">
            <Input v-model="formValidate.name" placeholder=""></Input>
        </FormItem>
          <FormItem label="映射给予方" prop="name">
            <Input v-model="formValidate.name" placeholder=""></Input>
        </FormItem>
           <FormItem label="认证账号" prop="name">
            <Input v-model="formValidate.name" placeholder=""></Input>
        </FormItem>
          <FormItem label="认证密码" prop="name">
            <Input v-model="formValidate.name" placeholder=""></Input>
        </FormItem>
        <FormItem label="过期时间">
            <Row>
                <Col span="11">
                    <FormItem prop="date">
                        <DatePicker type="date" style="width:100%" placeholder="" v-model="formValidate.date"></DatePicker>
                    </FormItem>
                </Col>
            </Row>
        </FormItem>
        
    </Form>
         <div slot="footer">
            <Button  size="large"  type="primary" :loading="isLoad" @click="handleSubmit">{{btnTitle}}</Button>
            <Button size="large"  @click="handleReset">重置</Button>

        </div>
    </Modal>
</template>
<script>
export default {
  name: "MapModal",
  props: {
    modalTitle: {
      type: String,
      default: "添加映射"
    }
  },
  data() {
    return {
      isShow: false,
      isLoad: false,
      btnTitle: "消息检测",
      formValidate: {
        name: "",
        mail: "",
        city: "",
        gender: "",
        interest: [],
        date: "",
        time: "",
        desc: ""
      },
      ruleValidate: {
        name: [{ required: true, message: "请输入", trigger: "blur" }],
        city: [{ required: true, message: "请选择", trigger: "change" }],
        date: [
          { required: true, type: "date", message: "请选择", trigger: "change" }
        ]
      }
    };
  },
  methods: {
    show() {
      this.isShow = true;
    },
    hide() {
      this.isShow = false;
    },
    handleSubmit() {
      this.$refs["formValidate"].validate(valid => {
        if (valid) {
          this.$Message.success("Success!");
        } else {
        }
      });
    },
    handleReset() {
      this.$refs["formValidate"].resetFields();
    }
  }
};
</script>
<style lang="less" scoped>
.map-modal {
  .ivu-modal-header {
    padding: 0;
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
</style>
