<template>
	<div class="header-pop">
		<span class="table-header-world">{{this.columnName}}</span>
		<span class="ivu-table-sort" @mouseenter="toShowSort()" @mouseleave="toHideSort()">
			<i class="ivu-icon ivu-icon-md-arrow-dropup" :class="resultSortNum === 1?'on':''" @click="sortTable(1)" v-show="showSort"></i>
			<i class="ivu-icon ivu-icon-md-arrow-dropdown" :class="resultSortNum === 2?'on':''" @click="sortTable(2)" v-show="showSort"></i>
		</span>
		<a class="showpop-button" href="javascript:void(0)" @mouseenter="toShowPop" @mouseleave="toHidePop" @click="toShowPopWin" :class="showPop?'showPopButton':''">
			&nbsp;
			<Icon type="ios-arrow-down" v-show="showPop"></Icon>
		</a>
	</div>

</template>

<script>
	export default {
		name: 'HeaderPop',
		props: {
			columnName: {
				type: String,
				default: ''
			},
			columnNumber: {
				type: Number,
				default: 1
			},
			isPopBoxShow: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				resultSortNum: 0,
				showSort: false,
				showPop: false,
			}
		},
		methods: {

			sortTable(index) {
				this.resultSortNum = index;
				this.$emit('on-sorted', this.columnNumber, index);
			},
			toShowSort() {
				this.showSort = true;
			},
			toHideSort() {
				this.showSort = false;
			},
			toShowPop(e) {
				this.showPop = true;
			},
			toShowPopWin(e){
				e.targetColKeyIx = this.columnNumber;
				this.$emit('show-pop', e);
			},
			toHidePop() {
				this.showPop = false;
			}

		},
		created() {
		}
	}
</script>

<style lang="less">
	@import './headerPop.less';
</style>
