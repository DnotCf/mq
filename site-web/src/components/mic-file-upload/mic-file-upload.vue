<!--
  下拉框选择器
-->
<template>
<div class="mic-file-upload-comp">
  <div :class="render+'-style'">
    <div v-if="uploadList && uploadList.length <= 0 && readOnly">
        <span>暂无数据</span>
    </div>
    <!-- 上传头像所需默认头像，不要删 -->
    <div v-if="render==='avatar' && isAddAvatar" class="avatar-container">
      <div class="avatar-wrap default" v-for="(item, index) in defaultAvatar" @click="selectDefalutAvatar(item)" :class="{'active': item.selected}">
          <ColorIcons class="defalut-icon" :type="item.name" :size="4"></ColorIcons>
          <ColorIcons class="selected" type="select-default-avatar" :size="1"></ColorIcons>
      </div>
    </div>
    <div v-if="((uploadList && uploadList.length <= 0) || isDefault) && render==='avatar'" class="avatar-container">
        <div class="avatar-wrap">
            <Icons class="icon" type="upload-avatar" :size="36" color="#E6E6E6"/>
        </div>
    </div>

    <div v-if="render==='normal'" class="demo-upload-list" v-for="(item, index) in uploadList" :key="index">
      <template v-if="item.status === 'finished'">
        <img v-if="item.iconUrl" :src="item.iconUrl">
        <div>{{ item.iconUrl }}</div>
        <div class="demo-upload-list-cover">
          <Icon type="ios-eye-outline" @click.native="handleView(item)"></Icon>
          <Icon v-if="!readOnly" type="ios-trash-outline" @click.native="handleRemove(item)"></Icon>
        </div>
      </template>
      <template v-else>
        <Progress v-if="item.showProgress" :percent="item.percentage" hide-info></Progress>
      </template>
    </div>
    <div v-else-if="render==='avatar'" class="avatar-list-wrap">
      <div v-if="!isDefault" class="demo-upload-list" v-for="(item, index) in uploadList" :key="index">
        <template v-if="item.status === 'finished'">
          <ColorIcons class="selected" type="select-default-avatar" :size="1"></ColorIcons>
          <img :src="item.iconUrl">
          <div class="demo-upload-list-cover">
            <Icon type="ios-eye-outline" @click.native="handleView(item)"></Icon>
            <Icon v-if="!readOnly" type="ios-trash-outline" @click.native="handleRemove(item)"></Icon>
          </div>
        </template>
        <template v-else>
          <Progress v-if="item.showProgress" :percent="item.percentage" hide-info></Progress>
        </template>
      </div>
    </div>
    <div v-else style="display: inline-block;" :style="item.status === 'finished'?'':'width:100px;'">
      <Tooltip v-if="item.status === 'finished'" max-width="200" :content="item.name" placement="bottom-start">
        <Tag @click.native="handleView(item)" closable @on-close="handleRemove(item)">{{abbrName(item.name)}}</Tag>
      </Tooltip>
      <template v-else>
        <Progress v-if="item.showProgress" :percent="item.percentage" hide-info></Progress>
      </template>
    </div>

    <Upload v-show="(!readOnly?showUploadBtn:!readOnly)" :disabled="disabled" ref="upload" :headers="headers" :action="uploadUrl"
     :show-upload-list="false" :max-size="maxFileSize" :accept="accept" :format="format" :multiple="multiple" :on-success="handleSuccess"
     :on-progress="handleProgress" :on-error="handleError" :on-format-error="handleFormatError" :on-exceeded-size="handleMaxSize"
     :before-upload="handleBeforeUpload" :style="renderStyle" class="upload-btn">
      <div v-if="render==='normal'" class="normal-file-button">
        <Icon type="ios-cloud-upload-outline" size="20"></Icon>
      </div>
      <div v-else-if="render === 'avatar'" class="avatar-file-button">
        <span>{{ uploadList && uploadList.length > 0 && !isDefault ? '重新上传' : '上传头像' }}</span>
      </div>
      <Button v-else icon="ios-cloud-upload-outline">{{uploadText}}</Button>
      <slot></slot>
    </Upload>
  </div>
  <Modal title="预览文件" v-model="preView" :transfer="transfer" class="img-preview-modal" width="900" center>
    <div class="img-preview-div">
      <div v-if="preViewType==='img'" class="img-preview-content">
       <span ref="bigSpan">
          <img :src="preViewFileUrl" @mousewheel="rollImg(this)" @DOMMouseScroll="rollImg(this)" ref="bigImage">
       </span>
        <Button type="text" @click="clickPrevBtn" class="prev-btn">
          <Icon :size="18" type=" iconfont icon-tab-left" />
        </Button>
        <Button type="text" @click="clickNextBtn" class="next-btn">
          <Icon :size="18" type=" iconfont icon-tab-right" />
        </Button>
      </div>
      <div v-else>
        该文件暂不支持在线预览,<span style="cursor: pointer;" @click="downloadFile(preViewFileUrl)">点击下载</span>
      </div>
    </div>
    <div slot="footer">
      <!--<Button @click="preView = false">取消</Button>-->
      <Button type="primary" @click="downloadFile(preViewFileUrl)">下载</Button>
    </div>
  </Modal>
  <Modal title="预览视频" v-model="preViewVideo" :transfer="transfer" class="img-preview-modal" width="900" center>
    <div class="img-preview-div">
      <div class="img-preview-content">
        <video :src="preViewFileUrl" controls autoplay style="height: 100%"></video>
      </div>
    </div>
    <div slot="footer">
      <!--<Button @click="preView = false">取消</Button>-->
      <Button type="primary" @click="downloadFile(preViewFileUrl)">下载</Button>
    </div>
  </Modal>
</div>
</template>

<script>
import {
  getDataFromUrl
} from '@/api/common'
import Icons from '_c/icons'
import ColorIcons from '_c/color-icons'
import {
  getToken
} from '@/libs/util'
import encryptRequst from '@/libs/api.encrypt'

export default {
  components: {
    Icons,
    ColorIcons
  },
  props: {
    value: {
      default: null
    },
    multiple: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: null
    },
    uploadText: {
      type: String,
      default: '选择文件'
    },
    maxFileSize: {
      type: Number,
      default: null
    },
    maxFileNum: {
      type: Number,
      default: -1
    },
    accept: {
      type: String,
      default: '*'
    },
    format: {
      type: Array,
      default: () => []
    },
    url: {
      type: String,
      default: ''
    },
    render: {
      type: String,
      default: ''
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    },
    transfer: {
      type: Boolean,
      default: false
    },
    hideUploadWhenMax: {
      type: Boolean,
      default: true
    },
    needLogin: {
      type: Boolean,
      default: true
    },
    isAddAvatar: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      showUploadBtn: true,
      preView: false,
      preViewType: '',
      preViewFileUrl: '',
      preViewId: '',
      defaultList: [],
      uploadList: [],
      uploadUrl: window.baseURL + 'sys/file/upload?needLogin=' + (this.needLogin ? '1' : '0'),
      renderStyle: '',
      headers: {
        'Authorization': getToken() || ''
      },
      defaultAvatar: [],
      isDefault: false,
      preImgWidth: '100%',
      preViewVideo: false,
      preVideoSrc: ''
    }
  },
  mounted () {

    this.uploadList = this.$refs.upload.fileList
    if (this.render === 'normal') {
      this.renderStyle = 'width:58px;vertical-align: top;'
      if (!this.readOnly) {
        this.renderStyle += 'display: inline-block;'
      }
    } else {
      this.renderStyle = 'display: inline-block;'
    }
    this._showBack()
    for(let i = 0; i < 4; i++) {
      this.defaultAvatar.push({
        name: 'default-avatar' + (i + 1),
        id: i + 1,
        selected: false
      })
    }
  },
  watch: {
    value (newValue, oldValue) {
      // 初始化后，如果更改值，不会刷新回显的问题。
      this._showBack()
    },
    readOnly (newValue, oldValue) {}
  },
  methods: {
    //选择默认头像
    selectDefalutAvatar(param) {
      this.isDefault = true
      this.defaultAvatar.forEach(item => {
        item.selected = param.name === item.name ? true : false
      })
      this.uploadList = []
      // this._emitEvent()
      this.$emit('input', param.name.substr(param.name.length-1, 1))
    },
    // 判断头像是否是默认头像
    judgeIsDefult() {

    },
    // 清空头像list
    clearUploadList() {
      if (this.render === 'avatar') {
        this.uploadList = []
      }
    },
    // 清除默认头像选中状态
    clearDefaultSelect() {
      if (this.render === 'avatar') {
        this.defaultAvatar.forEach(item => {
          item.selected = false
        })
      }
    },
    // 回显默认头像选中状态
    defaultAvatarShowBack(id) {
      if (this.render === 'avatar'){
        this.defaultAvatar.forEach(item =>{
          if(id == item.id) {
            item.selected = true
          } else {
            item.selected = false
          }
        })
        // 判断当前头像是否是默认头像
        this.isDefault = (id != 1 && id != 2 && id != 3 && id != 4) ? false : true
      }
    },
    clearData (opt) {
      this.$refs.upload.fileList = []
      this.uploadList = []
    },
    _showBack () {
      // 初始化数据
      if (this.value) {
        const fileList = this.$refs.upload.fileList
        this.$refs.upload.fileList.splice(0, fileList.length)
        var tempIndex = 0
        const fileIds = this.value.split(',')
        if(this.url === ''){
          this.url = "/sys/file/info/";
        }else {
        }
        fileIds.forEach((item) => {
          getDataFromUrl(this.url + item).then(res => {
            if (res.code === 200) {
              this.uploadList.push({
                id: res.data.id,
                status: 'finished',
                percentage: 100,
                type: res.data.type,
                uid: Date.now() + tempIndex++,
                name: res.data.name,
                iconUrl: this._getIconUrl(res.data),
                url: this._getDownloadUrl(res.data)
              })
            }
          }).catch(err => {
          })
        })
        this.defaultAvatarShowBack(this.value)
      } else {
        if (this.$refs.upload.fileList && this.$refs.upload.fileList.length > 0) {
          this.$refs.upload.fileList.removeAll()
        }
        this.clearDefaultSelect()
        this.clearUploadList()
      }
      this.handleMaxFileNum()

    },
    abbrName (name) {
      if (name.length > 10) {
        return name.substring(0, 10) + '...'
      } else {
        return name
      }
    },
    handleView (file) {
      /* console.log('预览:')
      console.log(file) */
      this.preViewFileUrl = file.url + '&t=max'
      this.preViewId = file.id
      if (file.type === 'png' || file.type === 'jpg' || file.type === 'jpeg' || file.type === 'gif') {
        this.preViewType = 'img'
        this.preView = true
      } else if (file.type === 'mp4'){
        this.preViewVideo = true
        this.preVideoSrc = this.preViewFileUrl
      } else {
        // this.preViewType = 'other';
        // 其他的直接下载
        this.downloadFile(this.preViewFileUrl)
      }
    },
    downloadFile (url) {
      window.location.href = url
    },
    handleRemove (file) {
      this.$emit('clear-file', file)
      const fileList = this.$refs.upload.fileList
      this.$refs.upload.fileList.splice(fileList.indexOf(file), 1)
      // 清除头像
      this.clearUploadList()
      this.handleMaxFileNum()
      this._emitEvent()
    },
    handleSuccess (res, file) {
      this.isDefault = false
      // linx 解密
      if (res.encrypt) {
        res.data = encryptRequst.decrypt(res.data)
      }
      file.id = res.data.id
      file.type = res.data.type
      file.url = this._getDownloadUrl(res.data)
      file.iconUrl = this._getIconUrl(res.data)
      this.handleMaxFileNum()
      this._emitEvent()
      this.clearDefaultSelect()
    },
    handleFormatError (file) {
      this.$Message.warning('文件 ' + file.name + ' 类型错误,请重新选择.')
    },
    handleMaxSize (file) {
      // console.log("格式错误:")
      this.$Message.warning(file.name + ' 过大, 文件大小不能超过 ' + (this.maxFileSize / 1024).toFixed(1) + 'M.')
    },
    handleBeforeUpload () {
      // console.log("上传之前")
      // console.log(this.$refs.upload.fileList)
      this.uploadList = this.$refs.upload.fileList
      // 最大上传个数

      // 上传头像替换
      if (this.render === 'avatar') {
        if (this.uploadList.length > 0 ) {
          this.uploadList.splice(0,1)
        }
        return true
      } else{
        if (this.maxFileNum > 0) {
          const check = this.uploadList.length < this.maxFileNum
          if (!check) {
            this.$Message.warning('最多允许上传' + this.maxFileNum + '个文件!')
          }
          return check
        } else {
          return true
        }
      }

    },
    handleProgress (event, file, fileList) {
    },
    handleError (event, file, fileList) {
      this.$Notice.error({
        title: '上传文件异常',
        desc: file.msg
      })
    },
    handleMaxFileNum () {
      // console.log('Max',this.uploadList)
      if (!this.hideUploadWhenMax) {
        return
      }
      if (this.maxFileNum > 0) {
        const check = this.uploadList.length < this.maxFileNum
        if (!check) {
          this.showUploadBtn = false
        } else {
          this.showUploadBtn = true
        }
      } else {
        this.showUploadBtn = true
      }
    },
    _getDownloadUrl (item) {
      return window.baseURL + 'sys/file/download?id=' + item.id + '&token=' + getToken()
    },
    _getIconUrl (file) {
      if (file.type === 'png' || file.type === 'jpg' || file.type === 'jpeg' || file.type === 'gif') {
        return this._getDownloadUrl(file)
      } else {
        var typeIcon = { 'docx': 'word', 'doc': 'word', 'xls': 'excel', 'xlsx': 'excel', 'pdf': 'pdf', 'txt': 'txt', 'zip': 'zip', 'mp4': 'mp4' }
        var t = typeIcon[file.type]
        return '/images/file-type/' + (t || 'other') + '.png'
      }
    },
    _emitEvent () {
      var ids = []
      this.uploadList.forEach((item) => {
        if (item.status === 'finished') {
          ids.push(item.id)
        }
      })
      this.$emit('input', ids.join(','))
      this.$emit('change', ids.join(','))
    },
    clickPrevBtn () {
      let nowId = this.preViewId
      let index = this.uploadList.findIndex((item, index) => {
        return (item.id === nowId)
      })
      if (index > 0) {
        this.preViewFileUrl = window.baseURL + 'sys/file/download?id=' + this.uploadList[index - 1].id + '&token=' + getToken() + '&t=max'
        this.preViewId = this.uploadList[index - 1].id
      } else {
        this.preViewFileUrl = window.baseURL + 'sys/file/download?id=' + this.uploadList[this.uploadList.length - 1].id + '&token=' + getToken() + '&t=max'
        this.preViewId = this.uploadList[this.uploadList.length - 1].id
      }
    },
    clickNextBtn () {
      let nowId = this.preViewId
      let index = this.uploadList.findIndex((item, index) => {
        return (item.id === nowId)
      })
      if (index < this.uploadList.length - 1) {
        this.preViewFileUrl = window.baseURL + 'sys/file/download?id=' + this.uploadList[index + 1].id + '&token=' + getToken() + '&t=max'
        this.preViewId = this.uploadList[index + 1].id
      } else {
        this.preViewFileUrl = window.baseURL + 'sys/file/download?id=' + this.uploadList[0].id + '&token=' + getToken() + '&t=max'
        this.preViewId = this.uploadList[0].id
      }
    },
    scaleImgBtn(type) {
      let count = parseInt(this.preImgWidth.split('%')[0])
      if (type) {
        count += 10
      } else {
        count-= 10
      }
      this.preImgWidth = count + '%'
    },
    rollImg() {
      var delta = event.wheelDelta || (-event.detail * 24)
      var zoom = parseInt(this.$refs.bigImage.style.width) || 100;
      zoom += delta / 12;
      if (zoom >= 10 && zoom <250) {
        this.$refs.bigImage.style.width = zoom + "%";
      }
      return false;
    }
  }
}
</script>

<style lang="less" scoped="scoped">
  .pswp--open{
      z-index: 9999;
  }
.mic-file-upload-comp {
  .demo-upload-list {
    display: inline-block;
    width: 60px;
    height: 60px;
    text-align: center;
    line-height: 60px;
    border: 1px solid transparent;
    border-radius: 4px;
    overflow: hidden;
    background: #fff;
    position: relative;
    box-shadow: 0 1px 1px rgba(0, 0, 0, .2);
    margin-right: 4px;
  }

  .demo-upload-list img {
    width: 100%;
    height: 100%;
  }

  .demo-upload-list-cover {
    display: none;
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, .6);
  }

  .demo-upload-list:hover .demo-upload-list-cover {
    display: block;
  }

  .demo-upload-list-cover i {
    color: #fff;
    font-size: 20px;
    cursor: pointer;
    margin: 0 2px;
  }

  .normal-file-button {
    width: 58px;
    height: 58px;
    line-height: 58px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid #dcdee2;
    border-radius: 4px;
    cursor: pointer;
  }
  // 上传头像样式
  .avatar-style{
    // overflow: hidden;
    display: inline-block;
    .upload-btn{
      float: right;
      margin-right: 23px;
      margin-bottom: -14px;
    }
    .avatar-list-wrap{
      display: inline-block;
    }
    .demo-upload-list{
      display: block;
      width: 65px;
      height: 65px;
      border: 1px solid #08C828;
      overflow: visible;
      vertical-align: top;
      .selected{
        position: absolute;
        top: -5px;
        right: -4px;
        display: inline-block;
      }
    }
    .avatar-container{
      display: inline-block;
      vertical-align: top;
    }
    .demo-upload-list{
      display: inline-block;
      margin-right: 10px;
    }
    .avatar-wrap{
      width: 65px;
      height: 65px;
      border: 1px solid #E6E6E6;
      text-align: center;
      line-height: 65px;
      border-radius: 5px;
      display: inline-block;
      margin-right: 10px;
      position: relative;
      &.default{
        cursor: pointer;
        &.active{
          border: 1px solid #08C828;
          .selected{
            position: absolute;
            top: -5px;
            right: -4px;
            display: inline-block;
          }
        }
      }
      .selected{
        display: none;
      }
      .defalut-icon{
        vertical-align: middle;
      }
    }
  }
  .avatar-file-button{
    display: inline-block;
    color: #0089Ff;
    padding: 0 6px;
    border: 1px solid #0089FF;
    text-align: center;
    font-size: 1rem;
    border-radius: 2rem;
    line-height: 1.6rem;
    cursor: pointer;
  }

  .normal-file-button:hover {
    border-color: #57a3f3;
  }

  /** 预览内容*/
  .ivu-modal-body {
    text-align: center;
    vertical-align: top;
  }

  .normal-style {
    display: inline-block;
  }

  /** 列表样式*/
  .list-style {
    width: calc(~"100% - 110px");
    display: inline-block;
    margin-right: 10px;
    border: 1px solid #eee;
    border-radius: 5px;
    padding: 2px 2px;
    min-height: 32px;
    vertical-align: top;
  }
  .upload-btn {
    display: inline-block;
  }
}

.img-preview-modal {
  .ivu-modal-header{
    text-align: center;
  }
  .ivu-modal-footer{
    div{
      display: flex;
      justify-content: center;
    }
  }
  .ivu-modal-body {
    width: 100%;
    height: 500px;
  }

  .img-preview-div {
    width: 100%;
    height: 100%;
  }

  .img-preview-content {
    width: 100%;
    height: 500px;
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    overflow: hidden;
    img{
      position: absolute;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);

    }

    .ivu-btn {
      position: absolute;
      padding: 0;
      box-shadow: none;
      background-color: transparent;
    }

    .prev-btn {
      left: 0;
      top: 50%;
      transform: translate(0, -50%);
    }

    .next-btn {
      right: 0;
      top: 50%;
      transform: translate(0, -50%);
    }
  }
}
</style>
