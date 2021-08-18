<!--
	下拉框选择器
-->
<template>
	<RadioGroup v-model="selectValue" @on-change="onSelectChange">
		<slot></slot>
		<Radio v-for="op in options" :key="op.value" :label="op.value"><span>{{op.label}}</span></Radio>
	</RadioGroup>
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
				selectValue: null,
			};
		},
		mounted() {
			this.loadDictOption();
		},
		methods: {
			initData() {
				this.selectValue = null;
				this.options = [];
				this.loadDictOption();
			},
			clearData(opt) {
				this.selectValue = [];
			},
			loadDictOption() {
				if (this.url) {
					getDataFromUrl(this.url).then(res => {
						var data = res.data;
						this.options = [];
						data.forEach((item) => {
							this.options.push(item);
						});
						this.selectValue = this.value;
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
						this.selectValue = this.value;
					}).catch(err => {
						console.error("下拉框获取数据错误!");
						console.error(err);
					});
				} else {
					this.selectValue = this.value;
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
