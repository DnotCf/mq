<template>
	<div class="mic-tree-selector-comp">
		<div class="selected-value-showback">
			<template v-for="(item,index) in selectedItem">
				<Tag :closable="!readOnly" @on-close="onItemRemove(index)">
					{{item.title}}
				</Tag>
			</template>
			<div v-show="!readOnly" @click="show()" style="display: inline-block;">
				<slot name="button">
					<Icon type="ios-add-circle-outline" />
				</slot>
			</div>
		</div>

		<Modal v-model="modalDialog" :title="modalDialogTitle" @on-ok="onOK">
			<Spin fix v-show="loading"></Spin>
			<Tree ref="treeComp" :data="treeData" :multiple="multiple"></Tree>
		</Modal>
	</div>
</template>

<script>
	import {
		getDict
	} from '@/api/sysApi'

	import {
		getDataFromUrl
	} from '@/api/common'
	export default {
		props: {
			value: {
				default: null
			},
			title: {
				type: String,
				default: '选择'
			},
			multiple: {
				type: Boolean,
				default: false
			},
			vdata: {
				type: Array,
				default: () => []
			},
			url: {
				type: String,
				default: null
			},
			type: {
				type: String,
				default: null
			},
			sync: {
				type: Boolean,
				default: true
			},
			echoRender: {
				type: Function,
				default: null
			}
		},
		data() {
			return {
				readOnly: false,
				loading: false,
				modalDialog: false,
				modalDialogTitle: this.title,
				selectedItem: [],
				treeData: []
			}
		},
		methods: {
			initData(opt) {
				opt = opt || {};
				this.readOnly = (opt.readOnly === undefined) ? this.readOnly : opt.readOnly;
				// 用于回显数据
				this.selectedItem = [];
				this.loadTreeData(true);
			},
			clearData(opt) {
				this.selectedItem = [];
			},
			show() {
				this.modalDialog = true;
				this.loadTreeData();
			},
			_clearData() {
				this.treeData.splice(0, this.treeData.length);
				//this.selectedItem.splice(0, this.selectedItem.length);
			},
			_echoRender(item) {
				if (this.echoRender && typeof(this.echoRender) === 'function') {
					if (this.echoRender(item, this.value)) {
						this.selectedItem.push(item);
					}
				} else {
					if (this.value.indexOf(item.value) > -1) {
						this.selectedItem.push(item);
					}
				}
				if (item.children && item.children.length > 0) {
					item.children.forEach((childItem) => {
						this._echoRender(childItem);
					})
				}
			},
			loadTreeData(showback) {
				this.loading = true;
				// 查询后端的数据
				if (this.url) {
					getDataFromUrl(this.url).then(res => {
						var data = res.data;
						this._clearData();
						data.forEach((item) => {
							this.treeData.push(item);
							// 回显数据
							if (showback) {
								this._echoRender(item);
							}
						});
						this.loading = false;
					}).catch(err => {
						this.loading = false;
					});
				} else if (this.type) {
					getDict((this.type === 'all' ? '' : this.type), true, "0", this.sync).then(res => {
						var data = res.data;
						this._clearData();
						data.forEach((item) => {
							this.treeData.push(item);
							// 回显数据
							if (showback) {
								this._echoRender(item);
							}
						});
						this.loading = false;
					}).catch(err => {
						console.error("下拉框获取数据错误!");
						console.error(err);
						this.loading = false;
					});
				} else {
					this._clearData();
					this.selectedItem.splice(0, this.selectedItem.length);
					this.vdata.forEach((item) => {
						this.treeData.push(item);
						// 回显数据
						if (showback) {
							this._echoRender(item);
						}
					});
					this.loading = false;
				}
			},
			onOK() {
				var items = this.$refs["treeComp"].getSelectedNodes();

				if (!this.multiple) {
					// 单选则清除上一次选择
					this.selectedItem.splice(0, this.selectedItem.length);
				}

				items.forEach((item) => {
					var has = false;
					for (var i = 0; i < this.selectedItem.length; i++) {
						if (this.selectedItem[i].value === item.value) {
							has = true;
							break;
						}
					}
					if (!has) {
						this.selectedItem.push(item);
					}
				});

				var ids = [];
				this.selectedItem.forEach((item) => {
					ids.push(item.value);
				});
				this._emitEvent(ids);
			},
			onItemRemove(index) {
				this.selectedItem.splice(index, 1);
				var ids = [];
				this.selectedItem.forEach((item) => {
					ids.push(item.value);
				});
				this._emitEvent(ids);
			},
			getSelectedNodes() {
				return this.selectedItem;
			},
			_emitEvent(data) {
				var r = this.multiple ? data : (data.length > 0 ? data[0] : '');
				this.$emit('input', r);
				this.$emit('change', r);
			}
		}
	}
</script>

<style>

</style>
