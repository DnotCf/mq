<!--
	图片显示控件
	功能：
	  1、TODO 支持懒加载图片
	  2、图片加载异常处理
	  3、TODO 多种样式支持
-->
<template>
  <div class="mic-img-comp">
    <img :src="innerSrc"/>
  </div>
</template>

<script>
  import {
    getDataFromUrl
  } from '@/api/common'
  import {
    getToken
  } from '@/libs/util'
  export default {
    props: {
      value: {
        type: String,
        default: null
      },
      defaultSrc: {
        type: String,
        default: null
      },
      src: {
        type: String,
        default: null
      },
      errSrc: {
        type: String,
        default: null
      }
    },
    data() {
      return {
        innerSrc: '',
        headers: {
          "Authorization": getToken() || ""
        }
      };
    },
    created() {
      this.innerSrc = this.src;
    },
    mounted() {
      this._showBack();
    },
    methods: {

      _showBack() {
        // 初始化数据
        if (this.value) {
            getDataFromUrl('/sys/file/info/' + this.value).then(res => {
              if (res.code === 200) {
                this.innerSrc = this._getDownloadUrl(res.data);
              }
            }).catch(err => {
              console.error("文件回显错误");
              console.error(err);
            });
        } else {
         
        }
      },
      _getDownloadUrl(item) {
        return window.baseURL +
          "sys/file/download?id=" + item.id + "&token=" + getToken();
      },


    }
  }
</script>

<style lang="less">
.mic-img-comp {
  img{
    width: 100%;
  }
}
</style>
