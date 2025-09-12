Page({
  data: {
    progressItem: {}
  },
  onLoad: function(options) {
    if (options.progress) {
      const progress = JSON.parse(decodeURIComponent(options.progress));
      this.setData({ progressItem: progress });
    }
  },
  onPreviewAttachment: function(e) {
    const url = e.currentTarget.dataset.url;
    if (url) {
      wx.previewImage({
        urls: [url]
      });
    } else {
      wx.showToast({
        title: '无效的附件链接',
        icon: 'none'
      });
    }
  }
}); 