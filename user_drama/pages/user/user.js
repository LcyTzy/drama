var app = getApp()
var request = require('../../utils/request').request

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    maskedPhone: ''
  },

  onLoad: function () {
    if (!app.checkLogin()) return
    this.setData({ isLoggedIn: true })
    this.fetchUserInfo()
  },

  onShow: function () {
    var token = app.globalData.token || wx.getStorageSync('token')
    if (token && !this.data.isLoggedIn) {
      this.setData({ isLoggedIn: true })
      this.fetchUserInfo()
    }
  },

  fetchUserInfo: function () {
    var that = this
    request({
      url: '/user/center/info',
      method: 'GET'
    }).then(function (res) {
      var userInfo = res.data
      var maskedPhone = that.maskPhone(userInfo.phone || '')
      that.setData({
        userInfo: userInfo,
        maskedPhone: maskedPhone
      })
    }).catch(function () {})
  },

  maskPhone: function (phone) {
    if (!phone || phone.length < 7) return phone
    return phone.substring(0, 3) + '****' + phone.substring(7)
  },

  onLoginTap: function () {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  onLogoutTap: function () {
    var that = this
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: function (res) {
        if (res.confirm) {
          request({
            url: '/user/auth/logout',
            method: 'POST'
          }).then(function () {
            that.clearLoginState()
          }).catch(function () {
            that.clearLoginState()
          })
        }
      }
    })
  },

  clearLoginState: function () {
    wx.removeStorageSync('token')
    app.globalData.token = ''
    app.globalData.userInfo = null
    wx.reLaunch({ url: '/pages/index/index' })
  },

  onMenuTap: function (e) {
    var page = e.currentTarget.dataset.page
    wx.navigateTo({
      url: page
    })
  }
})
