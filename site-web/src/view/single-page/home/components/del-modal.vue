
<template>
 <div>
        <Modal v-model="isShow">
        <svg-icon  name="icon-warn"></svg-icon>你确定要删除吗？
         <div slot="footer">
            <Button  label="small" @click="handleReset">取消</Button>
            <Button label="small" type="primary" :loading="isLoad"  @click="handleSubmit">确定</Button>
        </div>
    </Modal>
 </div>
</template>
<script>
import dataSourceApi from "@/api/dataSourceApi";

export default {
  name: "MapModal",
  props: {
    modalTitle: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      isShow: false,
      isLoad: false,
      ids: []
    };
  },
  methods: {
    show(ids) {
      this.ids = ids;
      this.isShow = true;
    },
    hide() {
      this.isShow = false;
    },
    handleSubmit() {
      dataSourceApi
        .delSoruce(this.ids)
        .then(res => {
          if (res.code === 200) {
            this.$parent.formValidate = {};
            this.$Message.success("删除成功!");
            this.isShow = false;
          } else {
            this.$Message.error("删除数据源失败!");
          }
        })
        .catch(err => {
          this.$Message.error("删除数据源失败!");
        });
    },
    handleReset() {
      this.isShow = false;
    }
  }
};
</script>
<style lang="less" scoped>
.vertical-center-modal {
  display: flex;
  align-items: center;
  justify-content: center;

  .ivu-modal {
    top: 0;
  }
}
</style>
