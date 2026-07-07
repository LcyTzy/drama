var app = getApp()
var request = require('../../utils/request').request

Page({
  data: {
    phone: '',
    code: '',
    countdown: 0,
    loading: false,
    showPhoneLogin: false,
    avatarUrl: '',
    nickname: ''
  },

  

  _timer: null,

  // 选择头像
  onChooseAvatar: function (e) {
    this.setData({ avatarUrl: e.detail.avatarUrl })
  },

  // 点击允许 — 微信授权登录
  onAllow: function () {
    var that = this
    wx.login({
      success: function (res) {
        if (res.code) {
          that.setData({ loading: true })
          request({
            url: '/user/auth/login',
            method: 'POST',
            data: {
              code: res.code,
              nickname: that.data.nickname || '微信用户',
              avatarUrl: that.data.avatarUrl
            }
          }).then(function (res) {
            var token = res.data.token
            wx.setStorageSync('token', token)
            app.globalData.token = token
            app.globalData.userInfo = res.data.userInfo || null
            that.setData({ loading: false })
            wx.switchTab({ url: '/pages/index/index' })
          }).catch(function () {
            that.setData({ loading: false })
            wx.showToast({ title: '登录失败，请重试', icon: 'none' })
          })
        }
      }
    })
  },

  // 拒绝授权
  onReject: function () {
    wx.showToast({ title: '需要授权才能使用', icon: 'none' })
  },

  // 切换到手机号登录
  showPhoneLogin: function () {
    this.setData({ showPhoneLogin: true })
  },

  // 返回微信登录
  hidePhoneLogin: function () {
    this.setData({ showPhoneLogin: false })
  },

  onPhoneInput: function (e) {
    this.setData({ phone: e.detail.value })
  },

  onCodeInput: function (e) {
    this.setData({ code: e.detail.value })
  },

  sendCode: function () {
    var that = this
    var phone = this.data.phone

    if (!phone || phone.length !== 11) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }

    if (this.data.countdown > 0) return

    request({
      url: '/user/auth/sendCode',
      method: 'POST',
      data: { phone: phone }
    }).then(function () {
      wx.showToast({ title: '验证码已发送', icon: 'success' })
      that.startCountdown()
    }).catch(function () {})
  },

  startCountdown: function () {
    var that = this
    this.setData({ countdown: 60 })
    this._timer = setInterval(function () {
      if (that.data.countdown <= 1) {
        clearInterval(that._timer)
        that.setData({ countdown: 0 })
      } else {
        that.setData({ countdown: that.data.countdown - 1 })
      }
    }, 1000)
  },

  login: function () {
    var that = this
    var phone = this.data.phone
    var code = this.data.code

    if (!phone || phone.length !== 11) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }

    if (!code || code.length < 4) {
      wx.showToast({ title: '请输入验证码', icon: 'none' })
      return
    }

    this.setData({ loading: true })

    request({
      url: '/user/auth/loginByPhone',
      method: 'POST',
      data: { phone: phone, code: code }
    }).then(function (res) {
      var token = res.data.token
      wx.setStorageSync('token', token)
      app.globalData.token = token
      app.globalData.userInfo = res.data.userInfo || null
      that.setData({ loading: false })
      wx.switchTab({ url: '/pages/index/index' })
    }).catch(function () {
      that.setData({ loading: false })
    })
  },

  onUnload: function () {
    if (this._timer) {
      clearInterval(this._timer)
    }
  }
})
