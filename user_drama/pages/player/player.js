// pages/player/player.js
var request = require('../../utils/request').request

Page({
  data: {
    episodeId: '',
    dramaId: '',
    videoUrl: '',
    episodeTitle: '',
    episodeNum: 0,
    duration: 0,
    episodes: [],
    danmakus: [],
    activeDanmakus: [],
    danmakuInput: '',
    currentSeconds: 0,
    videoContext: null,
    loading: true,
    currentEpisodeIndex: 0,
    lastProgressTime: 0,
    swipedDanmakuId: '',
    touchStartX: 0,
    showDanmaku: true,
    danmakuTimer: null,
    shownDanmakuIds: {}
  },

  onLoad: function (options) {
    var episodeId = options.episodeId || ''
    var dramaId = options.dramaId || ''

    this.setData({
      episodeId: episodeId,
      dramaId: dramaId
    })

    if (episodeId) {
      this.fetchPlayUrl(episodeId)
      this.fetchDanmakus(episodeId)
    }

    if (dramaId) {
      this.fetchEpisodeList(dramaId)
    }
  },

  // 获取播放地址
  fetchPlayUrl: function (episodeId) {
    var that = this
    this.setData({ loading: true })

    request({
      url: '/user/play/getUrl?episodeId=' + episodeId,
      method: 'GET'
    }).then(function (res) {
      var data = res.data || {}
      that.setData({
        videoUrl: data.videoUrl || '',
        duration: data.duration || 0,
        loading: false
      })

      // 创建视频上下文
      if (!that.data.videoContext) {
        var ctx = wx.createVideoContext('videoPlayer')
        that.setData({ videoContext: ctx })
      }
    }).catch(function () {
      that.setData({ loading: false })
    })
  },

  // 获取剧集列表
  fetchEpisodeList: function (dramaId) {
    var that = this
    request({
      url: '/user/drama/detail/' + dramaId,
      method: 'GET'
    }).then(function (res) {
      var data = res.data || {}
      var episodes = data.episodes || []

      // 找到当前集数的索引
      var currentIndex = 0
      for (var i = 0; i < episodes.length; i++) {
        if (episodes[i].id === that.data.episodeId) {
          currentIndex = i
          break
        }
      }

      that.setData({
        episodes: episodes,
        currentEpisodeIndex: currentIndex,
        episodeTitle: episodes[currentIndex].title || '',
        episodeNum: episodes[currentIndex].episodeNum || 0
      })
    }).catch(function () {})
  },

  // 获取弹幕列表
  fetchDanmakus: function (episodeId) {
    var that = this
    console.log('获取弹幕列表, episodeId:', episodeId)
    request({
      url: '/user/danmaku/list?episodeId=' + episodeId,
      method: 'GET'
    }).then(function (res) {
      var list = res.data || []
      console.log('弹幕列表返回:', list.length, '条', list)
      that.setData({ danmakus: list, shownDanmakuIds: {} })
    }).catch(function (err) {
      console.error('获取弹幕失败:', err)
    })
  },

  // 根据当前播放时间更新活跃弹幕
  updateActiveDanmakus: function () {
    if (!this.data.showDanmaku) return

    var currentSeconds = this.data.currentSeconds
    var danmakus = this.data.danmakus
    var shownIds = this.data.shownDanmakuIds || {}
    var activeDanmakus = []
    var topPositions = [20, 60, 100, 140, 180, 220, 260, 300]

    for (var i = 0; i < danmakus.length; i++) {
      var d = danmakus[i]
      // 显示当前时间前后5秒内的弹幕，且未显示过
      if (Math.abs(d.timestamp - currentSeconds) <= 5 && !shownIds[d.id]) {
        shownIds[d.id] = true
        var topIdx = activeDanmakus.length % topPositions.length
        activeDanmakus.push({
          id: d.id,
          content: d.content,
          color: d.color || '#FFFFFF',
          top: topPositions[topIdx],
          duration: 4 + Math.random() * 2,
          animClass: 'danmaku-move'
        })
      }
    }

    this.setData({
      activeDanmakus: activeDanmakus,
      shownDanmakuIds: shownIds
    })

    // 3秒后清除已显示的弹幕
    var that = this
    setTimeout(function () {
      that.setData({ activeDanmakus: [] })
    }, 6000)
  },

  // 切换弹幕显示
  toggleDanmaku: function () {
    var show = !this.data.showDanmaku
    this.setData({
      showDanmaku: show,
      activeDanmakus: show ? this.data.activeDanmakus : []
    })
    if (show) {
      this.updateActiveDanmakus()
    }
  },

  // 发送弹幕
  onSendDanmaku: function () {
    var that = this
    var content = this.data.danmakuInput.trim()

    if (!content) {
      wx.showToast({ title: '请输入弹幕内容', icon: 'none' })
      return
    }

    // 检查是否已登录
    var token = wx.getStorageSync('token')
    console.log('当前 token:', token ? '有' : '无')
    if (!token) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      wx.reLaunch({ url: '/pages/login/login' })
      return
    }

    console.log('发送弹幕:', content, 'episodeId:', this.data.episodeId, 'timestamp:', this.data.currentSeconds)

    request({
      url: '/user/danmaku/send',
      method: 'POST',
      data: {
        episodeId: this.data.episodeId,
        content: content,
        timestamp: this.data.currentSeconds
      }
    }).then(function (res) {
      console.log('发送弹幕响应:', res)
      wx.showToast({ title: '发送成功', icon: 'success' })
      that.setData({ danmakuInput: '' })
      // 立即刷新弹幕列表
      that.fetchDanmakus(that.data.episodeId)
    }).catch(function (err) {
      console.error('发送弹幕失败:', err)
      wx.showToast({ title: '发送失败: ' + (err.msg || '未知错误'), icon: 'none' })
    })
  },

  // 弹幕输入
  onDanmakuInput: function (e) {
    this.setData({ danmakuInput: e.detail.value })
  },

  // 删除弹幕
  onDeleteDanmaku: function (e) {
    var that = this
    var id = e.currentTarget.dataset.id

    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条弹幕吗？',
      success: function (res) {
        if (res.confirm) {
          request({
            url: '/user/danmaku?id=' + id,
            method: 'DELETE'
          }).then(function () {
            wx.showToast({ title: '删除成功', icon: 'success' })
            that.setData({ swipedDanmakuId: '' })
            that.fetchDanmakus(that.data.episodeId)
          }).catch(function () {})
        }
      }
    })
  },

  // 弹幕触摸开始
  onDanmakuTouchStart: function (e) {
    this.setData({
      touchStartX: e.touches[0].clientX
    })
  },

  // 弹幕触摸移动
  onDanmakuTouchMove: function (e) {
    var moveX = e.touches[0].clientX
    var diff = this.data.touchStartX - moveX
    var id = e.currentTarget.dataset.id

    // 左滑超过 50px 显示删除按钮
    if (diff > 50) {
      this.setData({ swipedDanmakuId: id })
    } else if (diff < -50) {
      // 右滑收起删除按钮
      if (this.data.swipedDanmakuId === id) {
        this.setData({ swipedDanmakuId: '' })
      }
    }
  },

  // 弹幕触摸结束
  onDanmakuTouchEnd: function () {
    // 不做处理，保持当前状态
  },

  // 点击弹幕列表空白区域收起删除按钮
  onDanmakuListTap: function () {
    if (this.data.swipedDanmakuId) {
      this.setData({ swipedDanmakuId: '' })
    }
  },

  // 视频错误处理
  onVideoError: function (e) {
    console.error('视频加载失败:', e.detail)
    wx.showToast({ title: '视频加载失败', icon: 'none' })
    this.setData({ loading: false })
  },

  // 视频时间更新
  onTimeUpdate: function (e) {
    var currentTime = e.detail.currentTime || 0
    this.setData({ currentSeconds: Math.floor(currentTime) })

    // 每秒检查是否有弹幕需要显示
    this.updateActiveDanmakus()

    // 每 10 秒上报一次进度
    var now = Date.now()
    if (now - this.data.lastProgressTime >= 10000) {
      this.setData({ lastProgressTime: now })
      this.reportProgress()
    }
  },

  // 上报播放进度
  reportProgress: function () {
    request({
      url: '/user/play/progress',
      method: 'POST',
      data: {
        episodeId: this.data.episodeId,
        currentSeconds: this.data.currentSeconds
      }
    }).catch(function () {})
  },

  // 视频播放结束
  onVideoEnded: function () {
    this.onNextEpisode()
  },

  // 切换剧集
  onSwitchEpisode: function (e) {
    var id = e.currentTarget.dataset.id
    if (id === this.data.episodeId) return

    // 检查是否已付费
    var episodes = this.data.episodes
    for (var i = 0; i < episodes.length; i++) {
      if (episodes[i].id === id) {
        // 免费集或已解锁集可以直接播放
        if (episodes[i].isFree === 1 || episodes[i].isUnlocked === 1) {
          this.setData({ episodeId: id, videoUrl: '' })
          this.fetchPlayUrl(id)
          this.fetchDanmakus(id)
          this.updateCurrentEpisodeInfo(id)
        } else {
          // 付费集，停止播放并返回详情页
          if (this.data.videoContext) {
            this.data.videoContext.stop()
          }
          wx.showToast({ title: '该集需要付费观看', icon: 'none' })
          setTimeout(function () {
            wx.navigateBack()
          }, 1500)
        }
        break
      }
    }
  },

  // 更新当前剧集信息
  updateCurrentEpisodeInfo: function (episodeId) {
    var episodes = this.data.episodes
    for (var i = 0; i < episodes.length; i++) {
      if (episodes[i].id === episodeId) {
        this.setData({
          currentEpisodeIndex: i,
          episodeTitle: episodes[i].title || '',
          episodeNum: episodes[i].episodeNum || 0
        })
        break
      }
    }
  },

  // 上一集
  onPrevEpisode: function () {
    var index = this.data.currentEpisodeIndex
    if (index <= 0) {
      wx.showToast({ title: '已经是第一集了', icon: 'none' })
      return
    }

    var prevEpisode = this.data.episodes[index - 1]
    // 检查是否已付费
    if (prevEpisode.isFree === 1 || prevEpisode.isUnlocked === 1) {
      this.onSwitchEpisode({ currentTarget: { dataset: { id: prevEpisode.id } } })
    } else {
      // 停止视频播放
      if (this.data.videoContext) {
        this.data.videoContext.stop()
      }
      wx.showToast({ title: '上一集需要付费观看', icon: 'none' })
    }
  },

  // 下一集
  onNextEpisode: function () {
    var index = this.data.currentEpisodeIndex
    if (index >= this.data.episodes.length - 1) {
      wx.showToast({ title: '已经是最后一集了', icon: 'none' })
      return
    }

    var nextEpisode = this.data.episodes[index + 1]
    // 检查是否已付费
    if (nextEpisode.isFree === 1 || nextEpisode.isUnlocked === 1) {
      this.onSwitchEpisode({ currentTarget: { dataset: { id: nextEpisode.id } } })
    } else {
      // 停止视频播放
      if (this.data.videoContext) {
        this.data.videoContext.stop()
      }
      wx.showToast({ title: '下一集需要付费观看', icon: 'none' })
    }
  }
})
