<template>
  <Modal class="disaster-type-warn-model-form-page" v-model="modalDialog" :mask-closable="false"
         :title="modalDialogTitle" class-name="geo-modal" @on-visible-change="onClose" width="800">
    <Alert type="error" show-icon v-show="errMessage != ''">{{ errMessage }}</Alert>
    <Form class="geo-form" ref="dataForm" :model="formData" :rules="ruleValidate" :label-width="120"
          :label-colon="true">
      <Col span="24">
        <FormItem label="监测内容" prop="contentCode" :label-width="90">
          <MicSelect ref="micSelect" v-model="formData.contentCode" type="monitor_content"
                     placeholder="请选择监测内容" style="width: 180px"></MicSelect>
        </FormItem>
        <FormItem label="判据名称" prop="name" :label-width="80">
          <Input v-model="formData.name" placeholder="" disabled></Input>
        </FormItem>
      </Col>
      <Row :gutter="8">
        <Col span="12" v-for="(warnLevel, index) in warnLevelColors" :key="index" class="warn-level-card">
          <Card>
            <div slot="title" :style="{'background-color': warnLevelColors[3 - index]['color']}">
              <p :style="{margin: '4px 8px', color: (4 - index) === 3 ? 'black' :'white'}">
                {{ warnLevelColors[3 - index]['name'] }}
              </p>
            </div>
            <div>
              <div style="margin: 4px 0;"><span>表达式</span>
                <hr/>
                <a style="position: absolute; right: 16px" @click="checkFormula(4 - index)">公式校验</a></div>
              <Input type="textarea" v-model="formData['expression' + (4 - index)]" :rows="2" placeholder="请编辑表达式"
                     @click.native="updateActiveWarnLevel(4 - index)"/>
              <div style="margin: 4px 0;"><span>描述</span>
                <hr/>
              </div>
              <Input type="textarea" v-model="formData['description' + (4 - index)]" :rows="2"
                     placeholder="请编辑表达式"/>
            </div>
          </Card>
        </Col>
        <Col span="24">
          <Card title="模型参数设置" class="model-parameter-setting">
            <div>
              <div><span>切线角：</span><span>V = </span><Input type="number" style="width: 80px"
                                                            placeholder="请编辑表达式"/><span> mm/d</span></div>
              <div><span>雨强预警：</span><span>K = </span><Input type="number" style="width: 80px"
                                                             placeholder="请编辑表达式"/><span> mm/d</span></div>
            </div>
          </Card>
        </Col>
        <Col span="24">
          <Card title="公式编辑器">
            <div>
              <Row :gutter="4">
                <Col v-for="(operation, index) in formulaOperation" :key="index"
                     :span="Math.ceil((operation.display.length + 2) / 2)" style="margin-top: 4px">
                  <div class="operation" @click="onOperationClick(operation)">
                    {{ operation.display }}
                  </div>
                </Col>
              </Row>
            </div>
          </Card>
        </Col>
      </Row>
    </Form>
    <div slot="footer">
      <Button @click="onSubmit" type="primary" :loading="loading">提交</Button>
    </div>
  </Modal>
</template>

<script>
import MicSelect from '@/components/mic-select'

export default {
  name: "disaster-type-warn-model-form",
  components: {
    MicSelect
  },
  data() {
    return {
      loading: false,
      modalDialog: false,
      modalDialogTitle: '',
      errMessage: '',
      activeWarnLevelCard: '',
      /**
       * 表单值
       */
      warnLevelColors: [{
        name: '红色预警',
        level: 1,
        color: `rgba(212, 48, 48, 1)`
      }, {
        name: '橙色预警',
        level: 2,
        color: `rgba(255, 141, 26, 1)`
      }, {
        name: '黄色预警',
        level: 3,
        color: `rgba(255, 235, 59, 1)`
      }, {
        name: '蓝色预警',
        level: 4,
        color: `rgba(74, 142, 217, 1)`
      }],
      formData: {
        id: '',
        contentCode: '',
        contentName: null, // 名称
        expression1: null, // 一级预警
        description1: null, // 一级预警
        expression2: null, // 二级预警
        description2: null, // 二级预警
        expression3: null, // 三级预警
        description3: null, // 三级预警
        expression4: null, // 四级预警
        description4: null, // 四级预警
        // end
      },
      /**
       * 表单校验规则
       */
      ruleValidate: {
        contentCode: [{
          required: true,
          message: '请选择监测内容',
          trigger: 'blur'
        }]
        // end
      },
      /**
       * 表单默认值
       */
      defaultFormData: {
        type: '1',
        hidden: false,
        enable: true,
        preset: false,
        // end
      },
      // region 编辑器操作
      formulaOperation: [{
        display: '(',
        code: '('
      }, {
        display: ')',
        code: ')'
      }, {
        display: '>',
        code: '>'
      }, {
        display: '<',
        code: '<'
      }, {
        display: '≥',
        code: '>='
      }, {
        display: '≤',
        code: '<='
      }, {
        display: '+',
        code: '+'
      }, {
        display: '-',
        code: '-'
      }, {
        display: 'x',
        code: '*'
      }, {
        display: '÷',
        code: '/'
      }, {
        display: '%',
        code: '%'
      }, {
        display: '删除',
        code: 'delete'
      }, {
        display: '0',
        code: '0'
      }, {
        display: '1',
        code: '1'
      }, {
        display: '2',
        code: '2'
      }, {
        display: '3',
        code: '3'
      }, {
        display: '4',
        code: '4'
      }, {
        display: '5',
        code: '5'
      }, {
        display: '6',
        code: '6'
      }, {
        display: '7',
        code: '7'
      }, {
        display: '8',
        code: '8'
      }, {
        display: '9',
        code: '9'
      }, {
        display: '.',
        code: '.'
      }, {
        display: '=',
        code: '=='
      }, {
        display: '或',
        code: '||'
      }, {
        display: '且',
        code: '&&'
      }, {
        display: '雨强(mm/h)',
        code: 'hourRain',
        unit: 'mm/h',
        result: true
      }, {
        display: '场次雨(mm)',
        code: 'theRain',
        unit: 'mm',
        result: true
      }]
      // endregion
    };
  },
  methods: {
    formulaValidator(rule, value, callback) {
      if (value === '') {
        callback(new Error('Please enter your password'));
      } else {
        if (this.formCustom.passwdCheck !== '') {
          // 对第二个密码框单独验证
          this.$refs.formCustom.validateField('passwdCheck');
        }
        callback();
      }
    },
    /**
     * 新增对象
     */
    addData(id, name) {

      this.show("新增预警模型")
    },
    /**
     * 编辑对象
     */
    editData(data) {

      this.clearForm();
      // 设置数据
      for (let key in data) {
        this.formData[key] = JSON.parse(JSON.stringify(data[key]));
      }
      // start 处理特殊的表单属性
      // end
      this.show("编辑预警模型");
    },
    /**
     * 查看对象
     */
    viewData(data) {
      this.clearForm();
      for (let key in data) {
        this.formData[key] = JSON.parse(JSON.stringify(data[key]));
      }
      // start 处理特殊的表单属性
      // end
      // 设置数据
      this.show("预警模型信息");
    },
    updateActiveWarnLevel(level) {
      this.activeWarnLevelCard = level;
    },
    onOperationClick(operation) {
      if (!this.formData['expression' + this.activeWarnLevelCard]) {
        this.formData['expression' + this.activeWarnLevelCard] = ''
      }
      let display = operation.display
      if (!/\d/.test(operation.code)) {
        display = ' ' + display + ' ';
      }
      if (operation.code === 'delete') {
        let length = this.formData['expression' + this.activeWarnLevelCard].length
        this.formData['expression' + this.activeWarnLevelCard] = this.formData['expression' + this.activeWarnLevelCard].substr(0, length - 1)
        while (this.formData['expression' + this.activeWarnLevelCard].endsWith(' ')) {
          length = this.formData['expression' + this.activeWarnLevelCard].length
          this.formData['expression' + this.activeWarnLevelCard] = this.formData['expression' + this.activeWarnLevelCard].substr(0, length - 1)
        }
      } else {
        this.formData['expression' + this.activeWarnLevelCard] += display;
      }
      this.formData['description' + this.activeWarnLevelCard] = this.formData['expression' + this.activeWarnLevelCard].replaceAll(' ', '');
    },
    checkFormula(level) {
      let expression = this.formData['expression' + level];
      // 公式转义
      expression = this.convertFormula(expression);
      // 声明变量
      let variableDeclaration = ''
      this.formulaOperation.forEach((operation, index) => {
        if (operation.result) {
          variableDeclaration += 'let ' + operation.code + ' = 1.0;'
        }
      });
      expression = "(function() { return " + expression + " }())"
      expression = variableDeclaration + expression;
      try {
        eval(expression)
      } catch (e) {
        console.error("预警模型表达式计算失败。公式为:" + expression)
      }
    },
    /**
     * 将界面展示的公式转化为计算机公式
     */
    convertFormula(expression) {
      this.formulaOperation.forEach((operation, index) => {
        expression = expression.replaceAll(' ' + operation.display + ' ', ' ' + operation.code + ' ')
      })
      return expression
    },
    show(title) {
      this.modalDialogTitle = title;
      this.modalDialog = true;
    },
    hide() {
      this.modalDialog = false;
    },

    /**
     * 清理表单
     */
    clearForm() {
      for (let key in this.formData) {
        if (this.defaultFormData[key]) {
          // 采用设置的默认值
          this.formData[key] = JSON.parse(JSON.stringify(this.defaultFormData[key]));
        } else {
          // 系统自动默认
          let type = typeof (this.formData[key]);
          if (type === "string") {
            this.formData[key] = "";
          } else if (type === "number") {
            this.formData[key] = 0;
          } else if (type === "boolean") {
            this.formData[key] = false;
          }
          // 数组,时间自行重置
          // end
        }
      }
      this.resetForm();
    },
    /**
     * 重置表单
     */
    resetForm() {
      this.$refs['dataForm'].resetFields();
    },
    /**
     * 提交表单
     */
    onSubmit() {
      // 屏蔽提交按钮
      this.loading = true;
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          this.loading = false;
          return;
        }
        // TOADD start 特殊检验规则,不通过请return
        // end
        this.formData.isShow = this.formData.isShow ? '1' : '0';
        save(this.formData).then((req) => {
          this.loading = false;
          if (req.code === 200) {
            this.hide();
            this.$emit("on-opt-success", req.data);
            this.$Message.success('保存成功!');
          } else {
            this.errMessage = req.msg || "";
          }
        }).catch((err) => {
          this.loading = false;
        });
      });
    },
    /**
     * 取消操作
     */
    onCancel() {
      this.selectTab = 'baseInfo';
      this.hide();
    },

    onClose() {
      this.selectTab = 'baseInfo';
    }
  },
}
</script>

<style scoped lang="less">
.disaster-type-warn-model-form-page {

  /deep/ .ivu-modal-body {
    padding: 8px 16px;

    .ivu-card {
      background: rgba(246, 246, 246, 1);
      margin-top: 8px;

      .ivu-card-head {
        padding: 4px 8px;
        background-color: rgba(222, 225, 245, 1);
        border-bottom: 0;
      }

      .ivu-card-body {
        padding: 8px;
      }
    }

    .warn-level-card {
      .ivu-card-head {
        padding: 0;
      }
    }


    hr {
      width: 32px;
      display: inline-block;
      vertical-align: middle;
      margin-left: 4px;
      border: 0;
      background-color: darkgrey;
      height: 1px;
    }

    .model-parameter-setting {
      .ivu-card-head {
      }
    }

    .operation {
      text-align: center;
      line-height: 30px;
      font-weight: bold;
      display: inline-block;
      width: 100%;
      border: 1px solid rgba(204, 204, 204, 1);
      border-radius: 5px;

      &:hover {
        border: 1px solid rgba(162, 172, 252, 1);
        cursor: pointer
      }
    }
  }

  .ivu-col {
    .ivu-form-item {
      display: inline-block;
      margin-bottom: 0px;
    }
  }


}

</style>
