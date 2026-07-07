// app.js
App({
  globalData: {
    baseUrl: 'http://localhost:8088/api',
    token: '',
    userInfo: null
  },

  onLaunch() {
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
    }
  },

  checkLogin() {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.reLaunch({ url: '/pages/login/login' })
      return false
    }
    this.globalData.token = token
    return true
  }
})
