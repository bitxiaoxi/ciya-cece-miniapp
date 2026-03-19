Component({
  options: {
    styleIsolation: 'shared'
  },

  properties: {
    current: {
      type: Number,
      value: 1
    },
    total: {
      type: Number,
      value: 12
    },
    mode: {
      type: String,
      value: 'student'
    },
    stageLabel: {
      type: String,
      value: ''
    }
  },

  data: {
    progressWidth: 8,
    modeClass: 'is-student'
  },

  observers: {
    'current,total,mode': function (current, total, mode) {
      const width = total ? Math.max(8, Math.round((current / total) * 100)) : 8;
      this.setData({
        progressWidth: width,
        modeClass: mode === 'child' ? 'is-child' : 'is-student'
      });
    }
  },

  methods: {
    handleBack() {
      this.triggerEvent('back');
    }
  }
});
