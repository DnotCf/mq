<template>
  <div class="warn-model-setting-page">
    <Tabs :value="tabVal">
      <TabPane label="灾害类型预警模型" name="disasterType">
        <disaster-type-warn-model></disaster-type-warn-model>
      </TabPane>
      <TabPane label="隐患点预警模型" name="hiddenDanger">

      </TabPane>
    </Tabs>
  </div>
</template>

<script>
import DisasterTypeWarnModel from "@/view/apps/warn-model/warn-model-setting/disaster-type/disaster-type-warn-model";

export default {
  name: "warn-model-setting",
  components: {DisasterTypeWarnModel},
  data() {
    return {
      tabVal: 'disasterType'
    }
  }
}
</script>

<style scoped lang="less">

</style>
