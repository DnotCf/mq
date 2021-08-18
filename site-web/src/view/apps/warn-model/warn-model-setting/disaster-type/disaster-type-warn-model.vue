<template>
  <div class="disaster-type-warn-model-page">
    <Row :gutter="16">
      <Col span="8" v-for="(model, index) in disasterTypeWarnModels" :key="index" style="margin-bottom: 16px">
        <Card>
          <p slot="extra">
            <Icon type="ios-create-outline" :size="24" color="#2d8cf0" @click="onModelCardEdit(model.disasterType)"/>
          </p>
          <div class="model-card-content">
            <div class="circle" :style="{'background-color': circleColors[index]}">
              <h2>{{ model.disasterTypeName.charAt(0) }}</h2>
            </div>
            <div class="model-card-info" style="width: calc(100% - 96px)">
              <h2>{{ model.disasterTypeName }}预警模型</h2>
              <p>
                <span v-if="!model.contents || model.contents.length === 0">未设置</span>
                <span v-else v-for="(content, contentIndex) in model.contents" :key="contentIndex">
                  {{ contentIndex > 0 ? '、' : '' }}{{ content.contentName }}
              </span>
              </p>
            </div>
          </div>
        </Card>
      </Col>
    </Row>

    <disaster-type-warn-model-list ref="formModal"/>
  </div>
</template>

<script>
import Icons from "_c/icons/icons";
import DisasterTypeWarnModelList
  from "@/view/apps/warn-model/warn-model-setting/disaster-type/disaster-type-warn-model-list";

export default {
  name: "disaster-type-warn-model",
  components: {DisasterTypeWarnModelList, Icons},
  data() {
    return {
      circleColors: ['#cb18f8', '#2db7f5', '#19be6b', '#ff9900', '#ed4014', '#DB51AD', '#864ADD', '#669FC8'],
      disasterTypeWarnModels: [{
        disasterType: '00',
        disasterTypeName: '滑坡',
        contents: [{
          contentName: '深部位移',
          contentCode: 'SSW'
        }, {
          contentName: '表面水平位移',
          contentCode: 'BC'
        }, {
          contentName: '降雨',
          contentCode: 'YL'
        }]
      }, {
        disasterType: '01',
        disasterTypeName: '泥石流',
        contents: []
      }, {
        disasterType: '02',
        disasterTypeName: '崩塌',
        contents: []
      }, {
        disasterType: '03',
        disasterTypeName: '地面塌陷',
        contents: []
      }, {
        disasterType: '04',
        disasterTypeName: '地裂缝',
        contents: []
      }, {
        disasterType: '05',
        disasterTypeName: '地面沉降',
        contents: []
      }, {
        disasterType: '06',
        disasterTypeName: '不稳定斜坡',
        contents: []
      }, {
        disasterType: '07',
        disasterTypeName: '其他',
        contents: []
      }]
    }
  },
  methods: {
    onModelCardEdit(disasterType) {
      this.$refs.formModal.edit(disasterType);
    }
  }
}
</script>

<style scoped lang="less">
.disaster-type-warn-model-page {

  .model-card-content {
    padding: 32px;

    div {
      display: inline-block;
    }

    .circle {
      width: 80px;
      height: 80px;
      background-color: #cb18f8;
      border-radius: 40px;
      font-size: 200%;
      text-align: center;
      vertical-align: top;

      h2 {
        line-height: 80px;
      }
    }

    .model-card-info {
      margin-left: 16px;

      p {
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
  }
}
</style>
