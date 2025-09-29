<template>
	<view class="container">
		<view class="camera-container" v-if="!showPreview">
			<view class="camera-placeholder">
				<uni-icons type="camera" size="50" color="#ccc"></uni-icons>
				<text class="placeholder-text">点击下方按钮拍照</text>
			</view>
		</view>
		
		<view class="preview-container" v-else>
			<image 
				class="preview-image" 
				:src="photoPath" 
				mode="aspectFit"
			></image>
			
			<view class="preview-actions">
				<button class="action-btn cancel-btn" @click="cancelPreview">重拍</button>
				<button class="action-btn confirm-btn" @click="confirmPhoto">使用照片</button>
			</view>
		</view>
		
		<view class="bottom-bar">
			<view class="capture-btn" @click="takePhoto">
				<view class="capture-inner"></view>
			</view>
		</view>
	</view>
</template>

<script>
	import { mapState, mapActions } from 'vuex'
	
	export default {
		computed: {
			...mapState('camera', ['photoPath', 'showPreview'])
		},
		methods: {
			...mapActions('camera', ['takePhoto', 'cancelPreview', 'confirmPhoto'])
		}
	}
</script>

<style lang="scss">
	.container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #000;
	}
	
	.camera-container {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		
		.camera-placeholder {
			display: flex;
			flex-direction: column;
			align-items: center;
			
			.placeholder-text {
				color: #ccc;
				margin-top: 20px;
				font-size: 16px;
			}
		}
	}
	
	.preview-container {
		flex: 1;
		display: flex;
		flex-direction: column;
		
		.preview-image {
			flex: 1;
			width: 100%;
		}
		
		.preview-actions {
			display: flex;
			justify-content: space-around;
			padding: 20px;
			background-color: rgba(0, 0, 0, 0.5);
			
			.action-btn {
				padding: 10px 20px;
				border-radius: 5px;
				font-size: 16px;
			}
			
			.cancel-btn {
				background-color: #fff;
				color: #333;
				border: 1px solid #ccc;
			}
			
			.confirm-btn {
				background-color: #007aff;
				color: #fff;
				border: none;
			}
		}
	}
	
	.bottom-bar {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 20px;
		
		.capture-btn {
			width: 70px;
			height: 70px;
			border-radius: 50%;
			border: 3px solid #fff;
			display: flex;
			align-items: center;
			justify-content: center;
			
			.capture-inner {
				width: 60px;
				height: 60px;
				border-radius: 50%;
				background-color: #fff;
			}
		}
	}
</style>