<!--
	下拉框选择器
-->
<template>
	<CheckboxGroup v-model="selectValue" @on-change="onSelectChange">
		<slot></slot>
		<Checkbox v-for="op in options" :key="op.value" :label="op.value"><span>{{op.label}}</span></Checkbox>
	</CheckboxGroup>
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
			url: {
				type: String,
				default: null
			},
			type: {
				type: String,
				default: null
			}
		},
		data() {
			return {
				options: [],
				selectValue: [],
			};
		},
		mounted() {
			this.loadDictOption();
		},
		methods: {
			initData() {
				this.options = [];
				this.loadDictOption();
			},
			clearData(opt) {
				this.selectValue = [];
			},
			loadDictOption() {
			  //清除多选框中的数据
        this.selectValue.splice(0,this.selectValue.length);
				if (this.url) {
					getDataFromUrl(this.url).then(res => {
						var data = res.data;
						this.options = [];
						data.forEach((item) => {
							this.options.push(item);
						});
						this.value.forEach((item) => {
							this.selectValue.push(item);
						});
					}).catch(err => {
						console.error("下拉框获取数据错误!");
						console.error(err);
					});
				} else if (this.type) {
					getDict(this.type, false, false).then(res => {
						var data = res.data;
						this.options = [];
						data.forEach((item) => {
							this.options.push(item);
						});
						this.value.forEach((item) => {
							this.selectValue.push(item);
						});
					}).catch(err => {
						console.error("下拉框获取数据错误!");
						console.error(err);
					});
				} else {
					this.value.forEach((item) => {
						this.selectValue.push(item);
					});
				}
			},
			onSelectChange() {
				this.$emit("input", this.selectValue);
				this.$emit("change", this.selectValue);
			}
		}
	}
</script>

<style>

</style>
