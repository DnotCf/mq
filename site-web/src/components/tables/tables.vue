<template>
    <div class="geo-table-content">
        <!-- <header-pop :columns="columns"></header-pop> -->
        <div class="search-con search-con-top" ref="tableTop" v-if="searchable || (toolbtns.length > 0)">
            <!-- 搜索框 -->
            <div v-if="searchPlace === 'top' && !hideHead" class="search-bar">
                <div class="search-left" v-if="searchColumns.length > 0">
                  <span class="place-txt" :class="{'active': openActive}">
                    {{ searchBarTxt }}
                  </span>
                  <span v-if="searchSelectTerm.length > 0" @click="clearAllSearchCon">
                    <ColorIcons class="search-close-icon" type="table-select-del" :size="1"></ColorIcons>
                  </span>
                  <!-- <Icon v-if="searchSelectTerm.length > 0" class="search-close-icon" color="#0089FF" type="ios-close-circle" @click="clearAllSearchCon" /> -->
                  <Select v-model="searchKey" multiple class="search-col" placeholder="" @on-change="chooseSearchSelect" @on-open-change="onOpenChange" :label-in-value="true">
                      <Option v-for="item in searchColumns" v-if="item.key !== 'handle'" :value="item.key" :key="`search-col-${item.key}`">{{ item.title }}</Option>
                  </Select>
                </div>
                <div class="search-enter-box" v-if="searchColumns.length > 0">
                  <div v-for="(item, index) in searchSelectTerm" class="search-enter-item">
                    <div v-if="item.type === 'input'" class="enter-item-wrap">
                      <span class="pre-label">{{ item.label }}</span>
                      <Input @on-change="handleClear" clearable placeholder="输入关键字搜索" class="search-input"
                          v-model="searchModel[item.name]" :maxlength="searchInputMax" @on-enter="searchEnter" @on-clear="searchEnter" >
                      </Input>
                    </div>
                    <div v-if="item.type === 'select' && item.dicType != undefined" class="enter-item-wrap">
                      <span class="pre-label">{{ item.label }}</span>
                      <MicSelect ref="micSelectDic" @change="onSelectChange"
                          :type="item.dicType" class="iview-micselect inner" v-model="searchModel[item.name]" :key="'searchSelect01' + index">
                      </MicSelect> <!-- 下拉选择字典类型获取数据 -->
                    </div>
                    <div v-if="item.type === 'select' && item.dicUrl != undefined" class="enter-item-wrap">
                      <span class="pre-label">{{ item.label }}</span>
                      <MicSelect ref="micSelectUrl" @change="onSelectChange"
                          :url="item.dicUrl" class="iview-micselect inner" v-model="searchModel[item.name]" :key="'searchSelect02' + index">
                      </MicSelect> <!-- 下拉选择根据url获取数据 -->
                    </div>
                    <div v-if="item.type === 'select' && item.custom" class="enter-item-wrap">
                        <span class="pre-label">{{ item.label }}</span>
                        <MicSelect ref="micSelectUrl" @change="onSelectChange" class="iview-micselect inner" v-model="searchModel[item.name]" :key="'searchSelect02' + index">
                            <template v-if="item.options && item.options.length > 0 ">
                              <Option v-for="op in item.options" :value="op.value" :key="op.value">{{op.label}}
                              </Option>
                            </template>
                        </MicSelect> <!-- 下拉选择根据url获取数据 -->
                    </div>
                    <div  v-if="item.type === 'date'" class="enter-item-wrap">
                      <span class="pre-label">{{ item.label }}</span>
                      <DatePicker v-model="searchModel[item.name]" format="yyyy/MM/dd" type="daterange"
                          placement="bottom-start" :options="item.option"  placeholder="请选择时间" @on-change="timeSearchChange"></DatePicker>
                    </div>
                  </div>
                  <Button @click="handleSearch" class="search-btn" type="primary" icon="ios-search"></Button>
                </div>
            </div>
            <div class="tool-bars" v-if="toolbtns.length > 0">
                <div class="switchs">
                    <template v-for="item in switchs">
                        {{item.name}}
                        <i-switch :value="item.value" @on-change="item.onChange" :key="item.value">
                            <span slot="open">{{item.open}}</span>
                            <span slot="close">{{item.close}}</span>
                        </i-switch>
                    </template>
                </div>
                <slot name="toolbar"></slot>
                <!-- 按钮组 -->
                <template v-for="(btnItem, btnIndex) in toolbtns">
                  <!-- 渲染按钮并对按钮进行权限控制 -->
                  <template v-if="$canShowButton(btnItem.id) || (btnItem.selectBtnList && btnItem.selectBtnList.length > 0) || btnItem.notAuth">
                    <Button v-if="btnItem.type != 'select'" :icon="btnItem.icon" :type="btnItem.type" :loading="btnItem.loading"
                        @click="onBtnClick(btnItem,btnIndex)" :key="btnIndex">{{btnItem.name}}</Button>
                    <div class="btn-select-item" v-else-if="btnItem.dicType && btnItem.dicType != ''" :key="btnIndex">
                        <span>{{btnItem.name}}</span>
                        <MicSelect ref="micSelectDic" @change="btnSelectChange(btnItem.id,btnIndex)" :type="btnItem.dicType"
                            class="iview-micselect" v-model="btnItem.value">
                        </MicSelect> <!-- 下拉选择字典类型获取数据 -->
                    </div>
                    <div class="btn-select-item" v-else-if="btnItem.url && btnItem.url != ''" :key="btnIndex">
                        <span>{{btnItem.name}}</span>
                        <MicSelect ref="micSelectDic" @change="btnSelectChange(btnItem.id,btnIndex)" :url="btnItem.url"
                            class="iview-micselect" v-model="btnItem.value">
                        </MicSelect> <!-- 下拉选择字典类型获取数据 -->
                    </div>
                    <div v-else-if="btnItem.selectType && btnItem.selectType === 'moreSelectBtn'" class="more-btn-info">
                        <span class="more-text" @click="clickMoreBtn(btnIndex)" :class="{'active': btnItem.isOpen}" v-if="isShowMoreButton(btnItem.selectBtnList)">
                          {{ btnItem.name }}
                          <Icons class="icon" :type="btnItem.isOpen? 'table-more-btn-up' : 'table-more-btn'" :size="10" color="#0089FF"/>
                        </span>
                        <div class="more-btn-wrap" v-show="btnItem.isOpen" style="padding: 5px 5px;" >
                          <template v-for="item in btnItem.selectBtnList">
                            <Divider v-if="item.type==='divider' && $canShowButton(item.toFor) " style="margin: 3px 0;" />

                            <p class="item-btn" @click="onMoreSelectChange(btnItem, btnIndex, item.value)" v-if="item.type!=='divider' && ($canShowButton(item.id) || item.notAuth)" :value="item.value" :key="item.value">
                              <Icons class="icon" type="more-btn-hover" :size="12" color="#fff"/>
                              <span v-html="item.label"></span>
                            </p>
                          </template>
                        </div>
                    </div>
                    &nbsp;
                  </template>
                </template>
            </div>

        </div>
        <Table v-if="!hideHead" ref="tablesMain" :data="insideTableData" :columns="insideColumns" :stripe="stripe"
            :border="border" :show-header="showHeader" :width="width" :height="tableHeight" :loading="loading"
            :disabled-hover="disabledHover" :highlight-row="highlightRow" :row-class-name="rowClassName" :size="size"
            :no-data-text="noDataText" :no-filtered-data-text="noFilteredDataText" @on-current-change="onCurrentChange"
            @on-select="onSelect" @on-select-cancel="onSelectCancel" @on-select-all="onSelectAll" @on-selection-change="onSelectionChange"
            @on-sort-change="onSortChange" @on-filter-change="onFilterChange" @on-row-click="onRowClick"
            @on-row-dblclick="onRowDblclick" @on-expand="onExpand" :class="customHeader?'customer-table':''" class="geo-table">
            <slot name="header" slot="header"></slot>
            <slot name="footer" slot="footer"></slot>
            <slot name="loading" slot="loading"></slot>
             <template v-for="item in insideColumns" slot-scope="{ row }"
            :slot="item.slot">
                <slot :name="item.slot" :row="row">
                </slot>
            </template>
        </Table>

        <!-- 表头自定义 -->
        <Poptip ref="colPop" transfer placement="bottom" trigger="hover" v-model="isPopBoxShow" :style="'position: fixed;top:'+top+'px;left:'+left+'px'"
            @on-popper-hide="isPopBoxShow=false">
            <div class="api" slot="content">
                <Checkbox-group v-model="tableColumnsChecked" @on-change="changeTableColumns" v-for="(item,index) in columns"
                    :key="item.key">
                    <Checkbox :label="index" v-if="(item.title && columnKey == item.key && item.hidden != true)"
                        :disabled="true">{{item.title}}</Checkbox>
                    <Checkbox :label="index" v-else-if="(item.title && item.hidden != true)">{{item.title}}</Checkbox>
                </Checkbox-group>
            </div>
        </Poptip>

        <!--分页-->
        <div class="tables-pager" v-if="!hideHead && !hidePage">
            <page :total="pageTotal" :page-size="pageSize" @on-change="onPageNoChange" @on-page-size-change="onPageSizeChange"
                show-total show-elevator show-sizer />
        </div>
        <div v-if="searchable && searchPlace === 'bottom' && !hideHead" class="search-con search-con-top">
            <Select v-model="searchKey" class="search-col">
                <Option v-for="item in searchColumns" v-if="item.key !== 'handle'" :value="item.key" :key="`search-col-${item.key}`">{{ item.title }}</Option>
            </Select>
            <Input placeholder="输入关键字搜索" class="search-input" v-model="searchValue" />
            <Button class="search-btn" type="primary">
                <Icon type="search" />&nbsp;&nbsp;搜索</Button>
        </div>
        <a id="hrefToExportTable" style="display: none;width: 0px;height: 0px;"></a>
    </div>
</template>

<script>
import TablesEdit from "./edit.vue";
import handleBtns from "./handle-btns";
import HeaderPop from "./customer/headerPop.vue";
import MicSelect from "@/components/mic-select";
import ColorIcons from "_c/color-icons";
import Icons from "_c/icons";

export default {
  name: "Tables",
  components: {
    HeaderPop,
    MicSelect,
    ColorIcons,
    Icons
  },
  props: {
    columns: {
      type: Array,
      default() {
        return [];
      }
    },
    autoRefresh: {
      type: Boolean,
      default: true
    },
    customHeader: {
      type: Boolean,
      default: false
    },
    size: String,
    width: {
      type: [Number, String]
    },
    height: {
      type: [Number, String]
    },
    searchInputMax: {
      type: Number,
      default: 100
    },
    stripe: {
      type: Boolean,
      default: false
    },
    border: {
      type: Boolean,
      default: false
    },
    showHeader: {
      type: Boolean,
      default: true
    },
    highlightRow: {
      type: Boolean,
      default: true
    },
    rowClassName: {
      type: Function,
      default() {
        return "";
      }
    },
    context: {
      type: Object
    },
    noDataText: {
      type: String
    },
    noFilteredDataText: {
      type: String
    },
    disabledHover: {
      type: Boolean
    },
    excessHeight: {
      type: Number,
      default: 0
    },
    /**
     * @description 全局设置是否可编辑
     */
    editable: {
      type: Boolean,
      default: false
    },
    /**
     * @description 是否可搜索
     */
    searchable: {
      type: Boolean,
      default: false
    },
    /**
     * @description 搜索控件所在位置，'top' / 'bottom'
     */
    searchPlace: {
      type: String,
      default: "top"
    },
    /**
     * @description 加载数据函数（外部设置),页码、分页变化时会调用
     */
    loadPageData: {
      type: Function,
      default: null
    },
    toolbtns: {
      type: Array,
      default: () => []
    },
    offsetTop: {
      type: Number,
      default: 0
    },
    popHeaderX: {
      type: Number,
      default: 0
    },
    popHeaderY: {
      type: Number,
      default: 0
    },
    hideHead: {
      type: Boolean,
      default: false
    },
    switchs: {
      type: Array,
      default: () => []
    },
    /**
     * @description 隐藏分页栏
     */
    hidePage: {
      type: Boolean,
      default: false
    }
  },
  /**
   * Events
   * @on-start-edit 返回值 {Object} ：同iview中render函数中的params对象 { row, index, column }
   * @on-cancel-edit 返回值 {Object} 同上
   * @on-save-edit 返回值 {Object} ：除上面三个参数外，还有一个value: 修改后的数据
   */
  data() {
    return {
      value: [],
      insideColumns: [],
      numberColumns: [], // 要转换为数字的列
      searchColumns: [],
      insideTableData: [],
      edittingCellId: "",
      edittingText: "",
      searchValue: "",
      searchValueDate: [],
      searchIsDate: false,
      showSearch: {
        input: false,
        select: false,
        date: false,
        box: false
      },
      searchKey: [],
      searchType: ["input", "select", "date", "box"],
      searchTypeObj: {},
      searchKeyOption: {}, // linx 每一个字段的搜索配置
      searchDicType: {},
      searchDicUrl: {},
      searchSelectSelf: {},
      searchOptions: [],
      tableHeight: "auto",
      pageNo: 1,
      pageSize: 20,
      pageTotal: 0,
      loading: false,
      currentSelectRows: [],
      loadPageDataHandler: this.loadPageData,
      tableColumnsChecked: [], // 当前选中的列
      columnKey: "", // 当前打开自定义列的列key
      isPopBoxShow: false, // 自定义列弹出框状态
      top: 0,
      left: 0,
      selectKey: [],
      slectDicType: "",
      slectDicUrl: "",
      hasFilter: false,
      searchSelfObject: {},
      searchObject: {}, // 查询条件  //供外部调用
      timer: null, // linx 用来区分双击引发单击的问题
      searchSelectTerm: [],
      searchModel: {},
      openActive: false,
      searchBarTxt: "查询条件",
      btnIndex: 0,
      searchModelDefault: {}
    };
  },
  methods: {
    suportEdit(item, index) {
      item.render = (h, params) => {
        return h(TablesEdit, {
          props: {
            params: params,
            value: this.insideTableData[params.index][params.column.key],
            edittingCellId: this.edittingCellId,
            editable: this.editable
          },
          on: {
            input: val => {
              this.edittingText = val;
            },
            "on-start-edit": params => {
              this.edittingCellId = `editting-${params.index}-${
                params.column.key
              }`;
              this.$emit("on-start-edit", params);
            },
            "on-cancel-edit": params => {
              this.edittingCellId = "";
              this.$emit("on-cancel-edit", params);
            },
            "on-save-edit": params => {
              this.value[params.row.initRowIndex][
                params.column.key
              ] = this.edittingText;
              this.$emit("input", this.value);
              this.$emit(
                "on-save-edit",
                Object.assign(params, {
                  value: this.edittingText
                })
              );
              this.edittingCellId = "";
            }
          }
        });
      };
      return item;
    },
    surportHandle(item) {
      let options = item.options || [];
      let insideBtns = [];
      options.forEach(item => {
        if (handleBtns[item]) insideBtns.push(handleBtns[item]);
      });
      let btns = item.button ? [].concat(insideBtns, item.button) : insideBtns;
      item.render = (h, params) => {
        params.tableData = this.value;
        return h("div", btns.map(item => item(h, params, this)));
      };
      return item;
    },
    surportCustomerHead(item, index) {
      let vm = this;
      item.renderHeader = (h, params) => {
        return h(HeaderPop, {
          props: {
            columnName: params.column.title,
            columnNumber: index,
            isPopBoxShow: this.isPopBoxShow
          },
          on: {
            "show-pop": e => {
              vm.columnKey = vm.columns[e.targetColKeyIx].key;
              vm.top = e.clientY;
              vm.left = e.clientX;

              if (vm.popHeaderX && vm.popHeaderX !== 0) {
                vm.left = vm.left - vm.popHeaderX;
              }
              if (vm.popHeaderY && vm.popHeaderY !== 0) {
                vm.top = vm.top - vm.popHeaderY;
              }

              vm.isPopBoxShow = true;
            },
            "on-sorted": (index, sort) => {
              // linx 默认是本地搜索，需要远程排序，单独指定
              if (!params.column.remote) {
                if (sort === 1) {
                  vm.$refs.tablesMain.handleSort(index, "asc");
                } else if (sort === 2) {
                  vm.$refs.tablesMain.handleSort(index, "desc");
                }
                return;
              }

              // 排序规则，升序还是降序
              let sortType = "asc";
              if (sort === 1) {
                // TODO linx 需要改成后台异步搜索
                sortType = "asc";
              } else if (sort === 2) {
                sortType = "desc";
              }
              // 搜索的key
              let keySearch = params.column.key;
              if (params.column.search && params.column.search.key) {
                keySearch = params.column.search.key;
              }
              // 判断是否已经有搜索条件
              let flag = !(
                vm.searchKey === undefined ||
                vm.searchKey === "" ||
                vm.searchKey == null ||
                vm.searchKey === "false"
              );
              if (flag) {
                if (vm.hasFilter) {
                  vm.searchSelfObject.orderBy = {
                    key: keySearch,
                    type: sortType
                  };

                  vm.refreshPageData(vm.searchSelfObject);
                } else {
                  vm.refreshPageData({
                    orderBy: {
                      key: keySearch,
                      type: sortType
                    }
                  });
                }
              } else {
                vm.refreshPageData({
                  orderBy: {
                    key: keySearch,
                    type: sortType
                  }
                });
              }
            }
          }
        });
      };
      return item;
    },
    changeTableColumns() {
      var cols = [];
      this.tableColumnsChecked
        .sort(function(a, b) {
          return a - b;
        })
        .forEach(key => {
          cols.push(this.columns[key]);
        });
      this.insideColumns = cols;
    },
    handleColumns(columns) {
      const that = this;
      that.insideColumns = [];
      that.tableColumnsChecked = [];
      this.numberColumns = [];
      this.searchKey = [];
      this.searchModelDefault = {};
      columns.forEach(function(item, index) {
        if (!item.hidden) {
          let res = item;
          const _flag = !item.noEditHeader;
          if (res.editable) res = that.suportEdit(res, index);
          if (res.key === "handle") res = that.surportHandle(res);
          if (that.customHeader && _flag) {
            res = that.surportCustomerHead(res, index);
            that.tableColumnsChecked.push(index);
          }
          that.insideColumns.push(res);
        }
        if (item.type === "number") {
          that.numberColumns.push(item.key);
        }
        /* numberColumns */
      });
      var hasSearch = false;
      columns.map(item => {
        if (item.search && this.searchType.indexOf(item.search.type) > -1) {
          var _item = Object.assign({}, item);
          if (item.search.key && item.search.key !== "") {
            _item.key = item.search.key;
          }

          this.searchKeyOption[_item.key] = _item.search.option;
          this.searchTypeObj[_item.key] = _item.search.type;
          if (_item.search.dicType && _item.search.dicType !== "") {
            this.searchDicType[_item.key] = _item.search.dicType;
          } else if (_item.search.url && _item.search.url !== "") {
            this.searchDicUrl[_item.key] = _item.search.url;
          } else if (
            _item.search.custom &&
            _item.search.options &&
            _item.search.options.length > 0
          ) {
            this.searchSelectSelf[_item.key] = {
              custom: _item.search.custom,
              options: _item.search.options
            };
          }

          // 回显查询条件
          if (_item.search.choose) {
            this.searchKey.push(_item.key);
            this.searchModelDefault[_item.key] = _item.search.chooseValue;
          }

          this.searchColumns.push(_item);
          // console.log(this.searchColumns)
          if (!hasSearch) {
            hasSearch = true;
          }
        }
      });
      // if (hasSearch) {
      //   this.searchColumns.push({
      //     key: 'false',
      //     search: false,
      //     title: '查询条件'
      //   })
      // }
    },
    setDefaultSearchKey() {
      if (this.columns && this.columns.length > 0) {
        this.searchKey =
          this.columns[0].key !== "handle"
            ? this.columns[0].key
            : this.columns.length > 1
              ? this.columns[1].key
              : "";
      } else {
        this.searchKey = [];
      }
    },
    handleClear(e) {
      if (e.target.value === "") this.insideTableData = this.value;
    },
    searchEnter() {
      this.handleSearch();
    },
    timeSearchChange(v, t) {
      this.handleSearch();
    },
    handleSearch() {
      // TODO linx 需要改成后台异步搜索
      // linx fix bug#461 无条件时点击搜索无反应。改成查询全部，也可作为刷新使用
      if (this.hasFilter) {
        this.refreshPageData(this.searchSelfObject);
      } else {
        this.refreshPageData();
      }
      // this.insideTableData = this.value.filter(item => item[this.searchKey].indexOf(this.searchValue) > -1)
    },
    handleTableData() {
      if (this.numberColumns.length > 0) {
        this.numberColumns.map(numberItem => {
          this.insideTableData = this.value.map((item, index) => {
            let res = item;
            if (res[numberItem]) {
              res[numberItem] = parseFloat(res[numberItem]);
            }
            res.initRowIndex = index;
            return res;
          });
        });
      } else {
        this.insideTableData = this.value.map((item, index) => {
          let res = item;
          res.initRowIndex = index;
          return res;
        });
      }
    },
    refreshPageData(searchSelf) {
      // 刷新数据就清理选择项
      this.currentSelectRows = [];
      let pageNo = this.pageNo;
      let pageSize = this.pageSize;
      // let search = {}
      // let flag = !((this.searchKey === 'false' || this.searchKey === undefined || this.searchKey === ''))
      // if (flag) {
      //   if (!this.searchIsDate) {
      //     search[this.searchKey] = this.searchValue
      //   } else {
      //     search[this.searchKey] = this.searchValueDate
      //     /* 当选择的时间为空的时候进行处理 */
      //     if (this.searchValueDate.length !== 2 || this.searchValueDate[0] === '' || this.searchValueDate[1] === '') {
      //       search[this.searchKey] = []
      //     }
      //   }
      // }
      let searchObject = {};
      for (let key in this.searchModel) {
        if (this.searchModel[key] === "" || this.searchModel[key] === null) {
          delete this.searchModel[key];
        }
      }
      if (searchSelf) {
        this.hasFilter = true;
        this.searchSelfObject = JSON.parse(JSON.stringify(searchSelf));
        searchObject = searchSelf;
        if (Object.keys(this.searchModel) !== 0) {
          for (let key in this.searchModel) {
            // linx 支持 obj.key.key 的多重点方法选择的字符串转换为对象
            if (key.indexOf(".") > -1) {
              this.parserObjStr(
                searchObject,
                key.split("."),
                this.searchModel[key]
              );
            } else {
              searchObject[key] = this.searchModel[key];
            }
          }
        }
      } else {
        this.hasFilter = false;
        this.searchSelfObject = {};
        for (let key in this.searchModel) {
          // linx 支持 obj.key.key 的多重点方法选择的字符串转换为对象
          if (key.indexOf(".") > -1) {
            this.parserObjStr(
              searchObject,
              key.split("."),
              this.searchModel[key]
            );
          } else {
            searchObject[key] = this.searchModel[key];
          }
        }
      }
      // for (let key in searchObject) {
      //   if (key === this.searchKey){
      //     continue;
      //   }else {
      //     this.searchColumns.forEach(element => {
      //       if (element.key ===key) {
      //         delete searchObject[key]
      //       }
      //     });
      //   }
      // }
      this.changeSearchObject(searchObject);
      this.searchObject = searchObject; // 供外部调用

      this.loading = true;
      this.loadPageDataHandler(pageNo, pageSize, searchObject)
        .then(data => {
          this.pageTotal = data.count;
          this.value = data.list;
          this.loading = false;
        })
        .catch(err => {
          this.pageTotal = 0;
          this.value = [];
          this.loading = false;
          this.$Message.error(err.message || "服务异常");
        });
    },
    toLastTime(date) {
      let result = null;
      if (date) {
        result = new Date(
          date.getFullYear() +
            "/" +
            (date.getMonth() + 1) +
            "/" +
            date.getDate() +
            " 23:59:59"
        );
      }
      return result;
    },
    changeSearchObject(searchObject) {
      this.columns.map(item => {
        if (item.search) {
          if (item.search.type === "date") {
            let key = item.search.key ? item.search.key : item.key;
            if (searchObject[key]) {
              searchObject[key][1] = this.toLastTime(searchObject[key][1]);
            }
          }
        }
      });
    },
    exportCsv(params) {
      this.$refs.tablesMain.exportCsv(params);
    },
    clearCurrentRow() {
      this.$refs.tablesMain.clearCurrentRow();
    },
    // hcj更多按钮change回调
    onMoreSelectChange(btnItem, index, value) {
      // this.expandMoreBtn = false
      this.toolbtns[index].isOpen = false;
      if (value) {
        btnItem.selectBtnList.forEach(item => {
          if (
            value === item.value &&
            item.click &&
            typeof item.click === "function"
          ) {
            item.click(this.currentSelectRows);
          }
        });
      }
    },
    clickMoreBtn(btnIndex) {
      // this.toolbtns[index].isOpen = !this.toolbtns[index].isOpen
      this.btnIndex = btnIndex;
      this.toolbtns.forEach((item, index) => {
        if (btnIndex == index) {
          item.isOpen = !item.isOpen;
        } else {
          item.isOpen = false;
        }
      });
    },
    onCurrentChange(currentRow, oldCurrentRow) {
      this.$emit("on-current-change", currentRow, oldCurrentRow);
    },
    onSelect(selection, row) {
      // this.$refs.tablesMain.selectAll(false)
      // linx 单选模式下，有选择时，吧其他的给取消选中
      this.$emit("on-select", selection, row);
    },
    onSelectCancel(selection, row) {
      this.$emit("on-select-cancel", selection, row);
    },
    onSelectAll(selection) {
      this.$emit("on-select-all", selection);
    },
    onSelectionChange(selection) {
      console.log("111",selection)
      this.currentSelectRows = selection;
      this.$emit("on-selection-change", selection);
    },
    onSortChange(column, key, order) {
      this.$emit("on-sort-change", column, key, order);
    },
    onFilterChange(row) {
      this.$emit("on-filter-change", row);
    },
    onRowClick(row, index) {
      row.select = true;
      // linx 单击时，取消选中的，然后重新勾选
      this.$refs.tablesMain.selectAll(false);
      this.$refs.tablesMain.toggleSelect(index);
      this.$emit("on-row-click", row, index);
      // linx 单击行时，延时执行，如果时双击，会清除单击的执行
      /* this.timer && clearTimeout(this.timer)
      this.timer = setTimeout(()=>{
          this.$refs.tablesMain.selectAll(false);
          this.$refs.tablesMain.toggleSelect(index);
          this.$emit('on-row-click', row, index)
      },200) */
    },
    onRowDblclick(row, index) {
      // linx 如果触发了双击，则清除单击执行
      // this.timer && clearTimeout(this.timer)
      this.$emit("on-row-dblclick", row, index);
    },
    onExpand(row, status) {
      this.$emit("on-expand", row, status);
    },
    // 分页回调
    onPageNoChange(pageNo) {
      this.pageNo = pageNo;
      if (this.hasFilter) {
        this.refreshPageData(this.searchSelfObject);
      } else {
        this.refreshPageData();
      }
    },
    onPageSizeChange(pageSize) {
      this.pageSize = pageSize;
      if (this.hasFilter) {
        this.refreshPageData(this.searchSelfObject);
      } else {
        this.refreshPageData();
      }
    },
    // 工具条按钮点击回调
    onBtnClick(btnItem, ix) {
      if (btnItem.click && typeof btnItem.click === "function") {
        btnItem.loading = true;
        this.$set(this.toolbtns, ix, btnItem);
        var rtn = btnItem.click(this.currentSelectRows);
        // 更新按钮状态
        if (rtn && rtn.constructor === Promise) {
          rtn
            .then(() => {
              btnItem.loading = false;
              this.$set(this.toolbtns, ix, btnItem);
            })
            .catch(() => {
              btnItem.loading = false;
              this.$set(this.toolbtns, ix, btnItem);
            });
        } else {
          btnItem.loading = false;
          this.$set(this.toolbtns, ix, btnItem);
        }
      }
    },
    clearAllSearchCon() {
      this.searchSelectTerm = [];
      this.searchModel = {};
      this.searchKey = [];
      this.searchBarTxt = "查询条件";
      this.handleSearch();
    },
    chooseSearchSelect(val) {
      let needRefresh = false;
      this.searchSelectTerm = [];
      var selectedList = [];
      val.forEach(item => {
        selectedList.push(item.value);
        this.searchSelectTerm.push({
          label: item.label,
          name: item.value,
          option: this.searchKeyOption[item.value] || {},
          type: this.searchTypeObj[item.value],
          dicType: this.searchDicType[item.value],
          dicUrl: this.searchDicUrl[item.value],
          custom:
            this.searchSelectSelf[item.value] &&
            this.searchSelectSelf[item.value].custom,
          options:
            this.searchSelectSelf[item.value] &&
            this.searchSelectSelf[item.value].options
        });
        if (
          this.searchModel[item.value] === "" ||
          this.searchModel[item.value] === null ||
          this.searchModel[item.value] === undefined
        ) {
          if (
            this.searchModelDefault[item.value] ||
            this.searchModelDefault[item.value] === 0 ||
            this.searchModelDefault[item.value] === "0"
          ) {
            this.searchModel[item.value] = this.searchModelDefault[item.value];
            needRefresh = true;
          } else if (this.searchTypeObj[item.value] === "date") {
            this.searchModel[item.value] = [];
          } else {
            this.searchModel[item.value] = null;
          }
        }
      });
      for (const key in this.searchModel) {
        if (selectedList.indexOf(key) < 0) {
          delete this.searchModel[key];
        }
      }
      if (this.searchSelectTerm.length > 0) {
        this.searchBarTxt = "已选中" + this.searchSelectTerm.length + "项";
      } else {
        this.searchBarTxt = "查询条件";
      }
      // var flag = val !== 'false'

      // for (var key in this.showSearch) {
      //   this.showSearch[key] = false
      // }
      // this.searchValue = ''
      // this.searchIsDate = false
      // if (flag && this.searchTypeObj[val]) {
      //   this.showSearch[this.searchTypeObj[val]] = true
      //   if (this.searchTypeObj[val] === 'input') { /* 普通的输入框类型 */

      //   } else if (this.searchTypeObj[val] === 'select') { /* 下拉选择类型 */
      //     this.slectDicType = this.searchDicType[val]
      //     this.slectDicUrl = this.searchDicUrl[val]
      //     if (this.slectDicType && this.slectDicType !== '') {
      //       this.slectDicUrl = ''
      //     } else if (this.slectDicUrl && this.slectDicUrl !== '') {
      //       this.slectDicType = ''
      //     } else {
      //       this.slectDicType = ''
      //       this.slectDicUrl = ''
      //     }
      //   } else if (this.searchTypeObj[val] === 'date') { /* 时间类型 */
      //     this.searchIsDate = true
      //   }
      // } else {
      //   this.slectDicType = ''
      //   this.slectDicUrl = ''
      // }
      this.$nextTick(() => {
        this.caculHeight();
        if (needRefresh) {
          this.handleSearch();
        }
      });
    },
    onOpenChange(val) {
      this.openActive = !!val;
    },
    onSelectChange() {
      this.handleSearch();
    },
    btnSelectChange(type, index) {
      this.$emit("toolbtn-select-change", type, index);
    },
    parserObjStr(obj, props, val) {
      let key = props[0];
      if (!obj[key]) {
        obj[key] = {};
      }
      if (props.length <= 1) {
        obj[key] = val;
      } else {
        props.splice(0, 1);
        obj[key] = this.parserObjStr({}, props, val);
      }
      return obj;
    },
    caculHeight() {
      if (this.height && this.height !== "auto") {
        // 手动设置高度
        this.tableHeight = this.height;
      } else {
        // 自动计算表格高度
        // 设置表格高度 = 浏览器高度 - 顶部标题 - 内容区域上下内边距 - 表格工具条区域 - 底部分页控件（如果有） - 减去外部位移
        let headerHeight = document.getElementById("geoHeaderRef").clientHeight;
        let tableToolHeight = this.$refs.tableTop.offsetHeight;
        if (!headerHeight) {
          headerHeight = 50;
        }
        this.tableHeight =
          document.documentElement.clientHeight -
          headerHeight -
          18 * 2 -
          tableToolHeight -
          38 -
          this.$props.offsetTop;
      }

      if (this.excessHeight > 0) {
        this.tableHeight = this.tableHeight - this.excessHeight;
      }
    },
    // 给类型为"更多"的下拉按钮增加 是否显示isOpen 属性
    handleToolbtns() {
      this.toolbtns.forEach(item => {
        if (item.selectType === "moreSelectBtn") {
          item.isOpen = false;
        }
      });
    },
    // 点击除"更多"按钮以外的页面将其隐藏
    handleShowMoreBtn() {
      document.addEventListener("click", e => {
        const cName = e.target.className;
        const pName =
          (e.target.parentNode && e.target.parentNode.className) || "";
        if (
          pName !== "more-btn-info" &&
          cName !== "icon iconfont icon-table-more-btn" &&
          cName !== "icon iconfont icon-table-more-btn-up"
        ) {
          this.handleToolbtns();
        }
      });
    },
    setTableWidthChange() {
      let len = this.columns.length;
      this.columns.forEach((item, index) => {
        if (item.title && !item.width && index < len - 1) {
          this.$set(this.columns[index], "width", item.title * 14 + 72);
        }
        this.$set(this.columns[index], "resizable", true);
      });
    },
    // 新旧column长度不一致为页面内改变column的情况，不是拖动改变列宽的情况，才运行处理column的函数 hcj-1220
    // column改变后要重新设置resizable属性 hcj-1220
    pushColumnsInTable(newColumns, oldColumns) {
      if (newColumns.length !== oldColumns.length) {
        this.handleColumns(newColumns);
        this.setTableWidthChange();
      }
    },
    /**
     * @param {Array} 按钮
     * @description 表格按钮是更多下拉类型的时候判断内部按钮是否有可以显示的，
     * 如果内部全部不显示，则更多也不现实
     */
    isShowMoreButton(btnList) {
      for (let i = 0; i < btnList.length; i++) {
        if (btnList[i].notAuth) {
          return true;
        } else if (this.$canShowButton(btnList[i].id)) {
          return true;
        }
      }
      return false;
    }
  },
  watch: {
    columns: {
      handler(newColumns, oldColumns) {
        // this.handleColumns(newColumns)
        // this.setDefaultSearchKey()
        this.pushColumnsInTable(newColumns, oldColumns);
      },
      deep: true
    },
    value(val) {
      this.handleTableData();
      // this.handleSearch()
    },
    height(val) {
      this.tableHeight = val;
    }
  },
  mounted() {
    this.caculHeight();
    if (this.autoRefresh) {
      this.refreshPageData();
    }
    this.setTableWidthChange();

    this.handleColumns(this.columns);
    // this.setDefaultSearchKey()
    this.handleTableData();

    for (var key in this.$refs) {
      if (key.indexOf("micSelect") === 0) {
        this.selectKey.push(key);
      }
    }

    this.$emit("table-loaded", this.tableHeight);
    // this.$emit('table-loaded', document.documentElement.clientHeight - this.$props.offsetTop - 64 - 18 * 2 - 46);
    this.handleToolbtns();
    this.handleShowMoreBtn();
  },
  created() {
    // this.toolbtns.map(item=>{
    //  item.loading = false;
    // });
  }
};
</script>
<style lang="less">
/**
     *  linx fix bug#298 每个页面的筛选框被遮挡了部分
     */
.geo-table-content {
  .search-con {
    .ivu-select-single .ivu-select-selection .ivu-select-placeholder,
    .ivu-select-single .ivu-select-selection .ivu-select-selected-value {
      height: 28px !important;
      border-radius: 5px 0 0 5px !important;
    }
  }
}
</style>
