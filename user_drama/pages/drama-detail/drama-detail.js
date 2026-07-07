// pages/drama-detail/drama-detail.js
var app = getApp()
var request = require('../../utils/request').request

Page({
  data: {
    drama: {},
    episodes: [],
    dramaId: '',
    loading: true,
    isLoggedIn: false,
    showPayDialog: false,
    selectedEpisodes: [],
    totalPrice: 0
  },

  onLoad: function (options) {
    var id = options.id || ''
    this.setData({ dramaId: id })
    this.checkLogin()
    this.fetchDetail(id)
  },

  onShow: function () {
    this.checkLogin()
  },

  checkLogin: function () {
    var token = app.globalData.token
    this.setData({ isLoggedIn: !!token })
  },

  fetchDetail: function (id) {
    var that = this
    this.setData({ loading: true })
    request({
      url: '/user/drama/detail/' + id,
      method: 'GET'
    }).then(function (res) {
      var data = res.data || {}
      var episodes = data.episodes || []
      // 格式化时长
      for (var i = 0; i < episodes.length; i++) {
        episodes[i].durationText = that.formatDuration(episodes[i].duration)
        episodes[i].selected = false
      }
      that.setData({
        drama: data,
        episodes: episodes,
        loading: false
      })
    }).catch(function () {
      that.setData({ loading: false })
    })
  },

  formatDuration: function (seconds) {
    if (!seconds) return '0秒'
    seconds = parseInt(seconds)
    var min = Math.floor(seconds / 60)
    var sec = seconds % 60
    if (min > 0) {
      return min + '分' + sec + '秒'
    }
    return sec + '秒'
  },

  onEpisodeTap: function (e) {
    var index = e.currentTarget.dataset.index
    var episode = this.data.episodes[index]
    if (!episode) return

    if (episode.isFree === 1 || episode.isUnlocked === 1) {
      wx.navigateTo({
        url: '/pages/player/player?episodeId=' + episode.id + '&dramaId=' + this.data.dramaId
      })
    } else {
      this.openPayDialogForEpisode(index)
    }
  },

  openPayDialogForEpisode: function (index) {
    var episodes = this.data.episodes.slice()
    episodes[index].selected = true
    var selectedIds = [episodes[index].id]
    var pricePerEpisode = this.data.drama.pricePerEpisode || 0
    this.setData({
      episodes: episodes,
      showPayDialog: true,
      selectedEpisodes: selectedIds,
      totalPrice: pricePerEpisode
    })
  },

  onOpenPayDialog: function () {
    // 重置所有选择状态
    var episodes = this.data.episodes.slice()
    for (var i = 0; i < episodes.length; i++) {
      episodes[i].selected = false
    }
    this.setData({
      episodes: episodes,
      showPayDialog: true,
      selectedEpisodes: [],
      totalPrice: 0
    })
  },

  onClosePayDialog: function () {
    this.setData({ showPayDialog: false })
  },

  toggleEpisodeSelection: function (e) {
    var index = e.currentTarget.dataset.index
    var episodes = this.data.episodes.slice()
    var selected = this.data.selectedEpisodes.slice()
    var episodeId = episodes[index].id

    if (episodes[index].selected) {
      episodes[index].selected = false
      var pos = selected.indexOf(episodeId)
      if (pos > -1) {
        selected.splice(pos, 1)
      }
    } else {
      episodes[index].selected = true
      selected.push(episodeId)
    }

    var pricePerEpisode = this.data.drama.pricePerEpisode || 0
    this.setData({
      episodes: episodes,
      selectedEpisodes: selected,
      totalPrice: selected.length * pricePerEpisode
    })
  },

  onWatchFirst: function () {
    if (!this.checkLoggedIn()) return

    var episodes = this.data.episodes
    for (var i = 0; i < episodes.length; i++) {
      if (episodes[i].isFree === 1 || episodes[i].isUnlocked === 1) {
        wx.navigateTo({
          url: '/pages/player/player?episodeId=' + episodes[i].id + '&dramaId=' + this.data.dramaId
        })
        return
      }
    }
    wx.showToast({ title: '暂无可观看的剧集', icon: 'none' })
  },

  onBuyAll: function () {
    if (!this.checkLoggedIn()) return

    var that = this
    wx.showLoading({ title: '创建订单中...' })
    request({
      url: '/user/pay/createOrder',
      method: 'POST',
      data: {
        dramaId: that.data.dramaId,
        payMethod: 2
      }
    }).then(function (res) {
      var orderNo = res.data.orderNo
      return request({
        url: '/user/pay/success',
        method: 'PUT',
        data: { orderNo: orderNo }
      })
    }).then(function () {
      wx.hideLoading()
      wx.showToast({ title: '购买成功', icon: 'success' })
      that.fetchDetail(that.data.dramaId)
    }).catch(function () {
      wx.hideLoading()
    })
  },

  onBuySelected: function () {
    if (!this.checkLoggedIn()) return

    var selected = this.data.selectedEpisodes
    if (selected.length === 0) {
      wx.showToast({ title: '请选择要购买的剧集', icon: 'none' })
      return
    }

    var that = this
    wx.showLoading({ title: '创建订单中...' })
    request({
      url: '/user/pay/createOrder',
      method: 'POST',
      data: {
        dramaId: that.data.dramaId,
        episodeIds: selected,
        payMethod: 2
      }
    }).then(function (res) {
      var orderNo = res.data.orderNo
      return request({
        url: '/user/pay/success',
        method: 'PUT',
        data: { orderNo: orderNo }
      })
    }).then(function () {
      wx.hideLoading()
      that.setData({ showPayDialog: false })
      wx.showToast({ title: '购买成功', icon: 'success' })
      that.fetchDetail(that.data.dramaId)
    }).catch(function () {
      wx.hideLoading()
    })
  },

  checkLoggedIn: function () {
    if (!app.globalData.token) {
      wx.navigateTo({ url: '/pages/login/login' })
      return false
    }
    return true
  },

  goToLogin: function () {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
