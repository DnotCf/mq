<!--
	导入插件
	功能：
	  1、上传文件
	  2、下载模板
-->
<template>
  <Modal v-model="value" class="mic-importer-comp geo-modal" :title="title" @on-cancel="onCancel">
    <mic-file-upload v-model="uploadFile" render="list" :maxFileNum="1" :accept="accept" :format="format"
                     :hideUploadWhenMax="false"></mic-file-upload>
    <div v-if="templates.length > 0" class="mic-importer-comp-templates">
      <div>模板文件:</div>
      <ol>
        <li v-for="item in templates"><a :href="item.url">{{item.name}}</a></li>
      </ol>
    </div>
    <div slot="footer">
      <Button @click="onOk" type="primary" :loading="loading">提交</Button>
    </div>
  </Modal>
</template>

<script>
  import MicFileUpload from '../mic-file-upload'
  import {
    postDataToUrl
  } from '@/api/common'

  export default {
    props: {
      server: {
        type: String,
        default: null
      },
      templates: {
        type: Array,
        default: () => []
      },
      title: {
        type: String,
        default: '请选择文件'
      },
      accept: {
        type: String,
        default: "*"
      },
      format: {
        type: Array,
        default: () => []
      },
      value: {
        type: Boolean,
        default: false
      }
    },
    components: {
      MicFileUpload
    },
    data() {
      return {
        loading: false,
        uploadFile: ''
      };
    },
    created() {

    },
    mounted() {
    },
    methods: {
      onOk() {
        if (this.loading) {
          return;
        }
        this.loading = true;
        if (!this.uploadFile) {
          this.$Message.warning('请选择导入文件');
          this.loading = false;
          return;
        }
        return new Promise(function (resolve, reject) {

          postDataToUrl(this.server, {fid: this.uploadFile}).then(res => {
//					if (res.code === 0) {
//						this.$emit("on-close");
//					} else {
//            this.loading = false;
//						this.$Message.error(res.msg || '导入失败');
//					}
            if (res.code === 200) {
              this.$Message.success('导入成功!');
              this.loading = false;
              this.uploadFile = "";
              this.$emit("on-close");
              resolve(true);
            }
            else {
              resolve(true);
              this.$Message.error(res.msg || '导入失败!');
              this.loading = false;

            }
          }).catch(err => {
            this.$Message.error("网络异常");
            this.loading = false;
          })
        }.bind(this));


      },
      onCancel() {
        // 清空数据
        this.uploadFile = "";
        this.$emit("on-close");
      }
    }
  }
</script>

<style lang="less" scoped="scoped">
  .mic-importer-comp {
    &-templates ol {
      padding: 0px 1rem;
    }
  }
  .mic-file-upload-comp {
    margin: 20px;
  }
  .mic-importer-comp-templates {
    margin: 20px;
  }
</style>
