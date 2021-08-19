
<template>
    <Modal
        :title="modalTitle"
        v-model="isShow"
        class-name="vertical-center-modal">
        <p>Content of dialog</p>
        <p>Content of dialog</p>
        <p>Content of dialog</p>
         <div slot="footer">
            <Button  size="large" :loading="isLoad" @click="handleSubmit">{{btnTitle}}</Button>
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
      default: ""
    }
  },
  data() {
    return {
      isShow: false,
      isLoad: false,
      btnTitle: "消息检测"
    };
  },
  methods: {
    show() {
      this.isShow = true;
    },
    hide() {
      this.isShow = false;
    },
    handleSubmit() {},
    handleReset(){

    }

  }
};
</script>
<style lang="less">
.vertical-center-modal {
  display: flex;
  align-items: center;
  justify-content: center;

  .ivu-modal {
    top: 0;
  }
}
</style>
