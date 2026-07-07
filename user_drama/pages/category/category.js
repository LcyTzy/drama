// pages/category/category.js
const { request } = require('../../utils/request')

Page({
  data: {
    categories: [],
    activeCategory: null,
    dramas: [],
    loading: false
  },

  onLoad() {
    const app = getApp()
    if (!app.checkLogin()) return
    this.fetchCategories()
  },

  // 获取分类列表
  fetchCategories() {
    request({
      url: '/user/category/list',
      method: 'GET',
      data: { type: 1 }
    }).then(res => {
      const list = res.data || []
      if (list.length > 0) {
        this.setData({
          categories: list,
          activeCategory: list[0]
        })
        this.fetchDramas(list[0].id)
      } else {
        this.setData({ categories: [] })
      }
    }).catch(() => {})
  },

  // 获取指定分类的短剧列表
  fetchDramas(categoryId) {
    if (this.data.loading) return
    this.setData({ loading: true })

    request({
      url: '/user/drama/recommend',
      method: 'GET',
      data: {
        categoryId: categoryId,
        page: 1,
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

  // 点击分类
  onCategoryTap(e) {
    const category = e.currentTarget.dataset.category
    if (this.data.activeCategory && category.id === this.data.activeCategory.id) return
    this.setData({
      activeCategory: category,
      dramas: []
    })
    this.fetchDramas(category.id)
  },

  // 跳转到短剧详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: '/pages/drama-detail/drama-detail?id=' + id
    })
  }
})
