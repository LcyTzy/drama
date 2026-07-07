// pages/history/history.js
const { request } = require('../../utils/request')

Page({
  data: {
    histories: [],
    loading: false
  },

  onLoad() {
    this.fetchHistory()
  },

  onShow() {
    this.fetchHistory()
  },

  async fetchHistory() {
    this.setData({ loading: true })
    try {
      const res = await request({
        url: '/user/play/history',
        method: 'GET',
        data: { page: 1, pageSize: 50 }
      })
      this.setData({ histories: res.data || [] })
    } catch (err) {
      console.error('获取观看历史失败', err)
    } finally {
      this.setData({ loading: false })
    }
  },

  goToDetail(e) {
    const dramaId = e.currentTarget.dataset.dramaId
    wx.navigateTo({
      url: `/pages/drama-detail/drama-detail?id=${dramaId}`
    })
  }
})
