// utils/util.js

// 格式化时间
const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return `${[year, month, day].map(formatNumber).join('/')} ${[hour, minute, second].map(formatNumber).join(':')}`
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : `0${n}`
}

// 格式化文件大小
const formatFileSize = bytes => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

// 通用上传文件样式
const uploadFileStyle = {
  container: `
    border: 2rpx dashed #e0e0e0;
    border-radius: 8rpx;
    padding: 40rpx 20rpx;
    background-color: #fafafa;
    text-align: center;
    min-height: 120rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  `,
  icon: `
    font-size: 48rpx;
    color: #999;
    margin-bottom: 16rpx;
  `,
  text: `
    font-size: 28rpx;
    color: #666;
  `
}

module.exports = {
  formatTime,
  formatFileSize,
  uploadFileStyle
}
