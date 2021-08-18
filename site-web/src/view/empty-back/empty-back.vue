<template>
  <div class="empty-back">
    <div class="content">
      <Icons type="kaifazhong" style="margin: 0 auto; font-size: 64px !important;"></Icons>
      <div style="font-size: 16px">开发中...</div>
    </div>
  </div>
</template>

<script>
import Icons from "_c/icons/icons";

export default {
  name: 'emptyback',
  components: {Icons}
}
</script>

<style lang="less">
.empty-back {
  text-align: center;
  height: 100%;
  padding: 20% 0 0 0;

  .content {
    margin: 0 auto;
  }
}
</style>
