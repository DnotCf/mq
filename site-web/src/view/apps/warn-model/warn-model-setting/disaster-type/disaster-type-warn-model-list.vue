<template>
  <div class="disaster-type-warn-model-list-page">
    <Modal v-model="showModal" class="disaster-type-warn-model-list-page" :title="title" @on-ok="ok"
           @on-cancel="cancel" class-name="geo-modal" width="80">
      <div>
        <Tables ref="dataTables" editable searchable search-place="top" :offsetTop="0" :toolbtns="toolbtns"
                :border="true" :loadPageData="loadPageData" :columns="columns" :searchInputMax="20"
                @on-row-dblclick="tableColumnDblclick" :autoRefresh="false" height="500">
        </Tables>
      </div>
    </Modal>

    <disaster-type-warn-model-form ref="formDialog"></disaster-type-warn-model-form>
  </div>
</template>

<script>
import Tables from "_c/tables/tables";
import {getIcon} from "@/libs/util.js";
import DisasterTypeWarnModelForm
  from "@/view/apps/warn-model/warn-model-setting/disaster-type/disaster-type-warn-model-form";

export default {
  name: "disaster-type-warn-model-list",
  components: {DisasterTypeWarnModelForm, Tables},
  data() {
    return {
      showModal: false,
      title: '灾害类型预警模型编辑',
      disasterType: '',
      // region 字段
      columns: [{
        type: 'selection',
        width: 60,
        align: 'center'
      }, {
        title: '监测内容名称',
        key: 'contentName',
        ellipsis: true,
        tooltip: true,
        align: 'left',
        width: 200,
        search: {
          type: 'input',
          state: true
        }
      }, {
        title: '监测内容编号',
        key: 'contentCode',
        ellipsis: true,
        tooltip: true,
        align: 'left',
        search: {
          type: 'input',
          state: true
        }
      }, {
        title: '蓝色警告',
        key: 'expression4',
        ellipsis: true,
        tooltip: true,
        align: 'left',
      }, {
        title: '黄色警告',
        key: 'expression3',
        ellipsis: true,
        tooltip: true,
        align: 'left',
      }, {
        title: '橙色警告',
        key: 'expression2',
        ellipsis: true,
        tooltip: true,
        align: 'left',
      }, {
        title: '红色警告',
        key: 'expression1',
        ellipsis: true,
        tooltip: true,
        align: 'left',
      }],
      //endregion
      // region 按钮
      toolbtns: [
        // TOADD start 定义表格工具按钮
        {
          id: 'monitoring-project-add',
          name: '新建',
          type: 'primary',
          //icon: ' iconfont icon-com-file-add',
          icon: getIcon("Add"),
          //click: this.addData
        },
        {
          id: 'monitoring-project-edit',
          name: '修改',
          type: 'primary',
          //icon: ' iconfont icon-com-modify',
          icon: getIcon("Edit"),
          click: this.editData
        },
        {
          id: 'monitoring-project-delete',
          name: '删除',
          type: 'primary',
          //icon: ' iconfont icon-com-blod-del',
          icon: getIcon("Del"),
          //click: this.delData
        }]
      // endregion 按钮
    }
  },
  methods: {
    edit(disasterType) {
      this.show();
      this.disasterType = disasterType;
      this.$refs.dataTables.refreshPageData();
    },
    show() {
      this.showModal = true
    },
    close() {
      this.showModal = false
    },
    loadPageData(pageNo, pageSize, search) {
      return new Promise((resolve, reject) => {
        search['disasterType'] = this.disasterType;
        resolve({
          pageSize: 20,
          pageNo: 1,
          count: 3,
          list: [{
            contentName: '表面水平位移',
            contentCode: 'BC',
            expression4: 'New York No. 1 Lake Park',
            expression3: '2016-10-03',
            expression2: '',
            expression1: ''
          }, {
            contentName: '深部位移',
            contentCode: 'SSW',
            expression4: 'New York No. 1 Lake Park',
            expression3: '2016-10-03',
            expression2: '',
            expression1: ''
          }, {
            contentName: '降雨',
            contentCode: 'YL',
            expression4: 'New York No. 1 Lake Park',
            expression3: '2016-10-03',
            expression2: '',
            expression1: ''
          }]
        })
      })
      /*findPage({
        search: search,
        pageNo: pageNo,
        pageSize: pageSize
      })
        .then(req => {
          // 处理数据，如果存在异常则提示
          resolve(req.data)
        })
        .catch(err => {
          // 异常处理,不用提示
          reject(err)
        })
    })*/
    },
    editData(rows) {
      // 判断是否在详情页面
      let that = this
      if (!rows || rows.length === 0) {
        that.$Message.info('请先选择编辑的数据!')
        return
      }
      if (rows.length > 1) {
        that.$Message.info('只能选择一条数据!')
        return
      }
      // 显示编辑框
      that.$refs.formDialog.editData(rows[0])
      return true
    },
    tableColumnDblclick(row, index) {

    },
    ok() {
      this.$Message.info('Clicked ok');
    },
    cancel() {
      this.$Message.info('Clicked cancel');
    },
    mounted() {
    }
  }
}
</script>

<style scoped lang="less">
.disaster-type-warn-model-list-page {

}
</style>
