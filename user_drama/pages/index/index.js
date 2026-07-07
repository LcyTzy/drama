// pages/index/index.js
const { request } = require('../../utils/request')

Page({
  data: {
    categories: [{ id: 0, name: '推荐' }],
    dramas: [],
    activeCategoryId: '',
    page: 1,
    loading: false
  },

  onLoad() {
    const app = getApp()
    if (!app.checkLogin()) return
    this.fetchCategories()
    this.fetchDramas()
  },

  // 获取分类列表
  fetchCategories() {
    request({
      url: '/user/category/list',
      method: 'GET',
      data: { type: 1 }
    }).then(res => {
      const list = res.data || []
      const categories = [{ id: 0, name: '推荐' }, ...list]
      this.setData({ categories })
    }).catch(() => {})
  },

  // 获取推荐短剧列表
  fetchDramas() {
    if (this.data.loading) return
    this.setData({ loading: true })

    const { activeCategoryId, page } = this.data
    request({
      url: '/user/drama/recommend',
      method: 'GET',
      data: {
        categoryId: activeCategoryId,
        page: page,
        pageSize: 20
      }
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

  // 切换分类标签
  onTabChange(e) {
    const id = e.currentTarget.dataset.id
    if (id === this.data.activeCategoryId) return
    this.setData({
      activeCategoryId: id,
      page: 1,
      dramas: []
    })
    this.fetchDramas()
  },

  // 跳转到短剧详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/drama-detail/drama-detail?id=' + id
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({ page: 1, dramas: [] })
    this.fetchDramas()
    wx.stopPullDownRefresh()
  }
})
