// pages/purchased/purchased.js
const { request } = require('../../utils/request')

Page({
  data: {
    dramas: [],
    loading: false
  },

  onLoad() {
    this.fetchPurchased()
  },

  // 获取已购短剧列表
  fetchPurchased() {
    this.setData({ loading: true })

    request({
      url: '/user/center/purchased',
      method: 'GET'
    }).then(res => {
      const list = res.data || []
      this.setData({
        dramas: list,
        loading: false
      })
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  // 跳转到短剧详情
  goToDetail(e) {
    const dramaId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/drama-detail/drama-detail?id=' + dramaId
    })
  }
})
