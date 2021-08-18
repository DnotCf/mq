<template>
	<div :class="prefixCls">
		<Input v-model="currentQuery" size="small" :icon="!selfSearch ? icon : ''" :placeholder="placeholder" 
		:class="selfSearch ? 'seach-input' : ''" @on-click="handleClick" @on-enter="searchClick"/>
		<Button size="small" class="search-btn" type="primary" icon="ios-search" @click="searchClick" v-if="selfSearch"></Button>
		<!-- 搜索按钮 -->
	</div>
</template>
<script>
	export default {
		name: 'Search',
		components: {},
		props: {
			prefixCls: String,
			placeholder: String,
			query: String,
			selfSearch: Boolean
		},
		data() {
			return {
				currentQuery: this.query
			};
		},
		watch: {
			query(val) {
				this.currentQuery = val;
			},
			currentQuery(val) {
				if(!this.selfSearch){
					this.$emit('on-query-change', val);
				}
			}
		},
		computed: {
			icon() {
				return this.query === '' ? 'ios-search' : 'ios-close-circle';
			}
		},
		methods: {
			handleClick() {
				if (this.currentQuery === '') return;
				this.currentQuery = '';
				this.$emit('on-query-clear');
			},
			searchClick() {
				this.$emit('on-query-change', this.currentQuery);
			}
		}
	};
</script>
<style lang="less" scoped>
	@import './transfer.less';
</style>
