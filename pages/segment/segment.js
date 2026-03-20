const {
  createStudent,
  getAssessmentHistory,
  getAssessmentStages,
  getStudent,
  listStudents,
  startAssessment,
  updateStudent
} = require('../../utils/api');
const { clearSession, saveSession } = require('../../utils/assessment-session');
const { GRADE_OPTIONS, getGradeLabel, getStagePresentation, mergeStageRule } = require('../../utils/stage-helper');

function showError(message) {
  wx.showToast({
    title: message,
    icon: 'none'
  });
}

Page({
  data: {
    stages: [],
    selectedId: '',
    selectedStage: null,
    students: [],
    selectedStudentId: null,
    selectedStudent: null,
    historyRecords: [],
    historyLoading: false,
    studentFormVisible: false,
    studentFormMode: 'create',
    studentFormTitle: '新建学生档案',
    studentForm: {
      id: null,
      studentName: '',
      gradeCode: 'P1',
      birthYear: ''
    },
    gradeOptions: GRADE_OPTIONS,
    gradeIndex: 1,
    currentGradeLabel: GRADE_OPTIONS[1].label,
    aiEnabled: true,
    loading: false,
    savingStudent: false,
    startingAssessment: false
  },

  onShow() {
    this.bootstrap();
  },

  async bootstrap() {
    if (this.data.loading) {
      return;
    }

    this.setData({
      loading: true
    });

    wx.showLoading({
      title: '加载中'
    });

    try {
      const [stageRules, students] = await Promise.all([
        getAssessmentStages(),
        listStudents()
      ]);
      const stages = (stageRules || []).map(mergeStageRule);
      const studentList = (students || []).map((student) => this.decorateStudent(student));
      const selectedId = this.pickSelectedStageId(stages);
      const selectedStage = stages.find((item) => item.id === selectedId) || null;
      const selectedStudentId = this.pickSelectedStudentId(studentList);
      const selectedStudent = studentList.find((item) => item.id === selectedStudentId) || null;

      this.setData({
        stages,
        selectedId,
        selectedStage,
        students: studentList,
        selectedStudentId,
        selectedStudent
      });

      if (selectedStudentId) {
        await this.loadHistory(selectedStudentId);
      } else {
        this.setData({
          historyRecords: [],
          historyLoading: false
        });
      }
    } catch (error) {
      showError(error.message || '加载失败');
    } finally {
      wx.hideLoading();
      this.setData({
        loading: false
      });
    }
  },

  pickSelectedStageId(stages) {
    if (this.data.selectedId && stages.some((item) => item.id === this.data.selectedId)) {
      return this.data.selectedId;
    }
    return stages.length ? stages[0].id : '';
  },

  pickSelectedStudentId(students) {
    if (this.data.selectedStudentId && students.some((item) => item.id === this.data.selectedStudentId)) {
      return this.data.selectedStudentId;
    }
    return students.length ? students[0].id : null;
  },

  decorateStudent(student) {
    const gradeLabel = getGradeLabel(student.gradeCode);
    const birthYearText = student.birthYear ? `${student.birthYear}年生` : '';
    return Object.assign({}, student, {
      gradeLabel,
      birthYearText,
      studentMeta: birthYearText ? `${gradeLabel} · ${birthYearText}` : gradeLabel
    });
  },

  async loadHistory(studentId) {
    this.setData({
      historyLoading: true
    });

    try {
      const historyRecords = await getAssessmentHistory(studentId);
      this.setData({
        historyRecords: (historyRecords || []).map((item) => {
          const stage = getStagePresentation(item.selectedStageCode);
          const estimatedStage = getStagePresentation(item.estimatedStageCode);
          return Object.assign({}, item, {
            selectedStageLabel: stage.shortLabel || item.selectedStageCode,
            estimatedStageLabel: estimatedStage.shortLabel || item.estimatedStageCode,
            finishedAtText: this.formatDateTime(item.finishedAt),
            confidenceScoreText: item.confidenceScore
          });
        })
      });
    } catch (error) {
      showError(error.message || '历史记录加载失败');
      this.setData({
        historyRecords: []
      });
    } finally {
      this.setData({
        historyLoading: false
      });
    }
  },

  handleSelectStage(event) {
    const { stageId } = event.detail;
    const selectedStage = this.data.stages.find((item) => item.id === stageId) || null;
    this.setData({
      selectedId: stageId,
      selectedStage
    });
  },

  handleSelectStudent(event) {
    const { studentId } = event.currentTarget.dataset;
    const selectedStudent = this.data.students.find((item) => item.id === Number(studentId)) || null;
    this.setData({
      selectedStudentId: selectedStudent ? selectedStudent.id : null,
      selectedStudent
    });

    if (selectedStudent) {
      this.loadHistory(selectedStudent.id);
    }
  },

  handleCreateStudent() {
    this.setData({
      studentFormVisible: true,
      studentFormMode: 'create',
      studentFormTitle: '新建学生档案',
      studentForm: {
        id: null,
        studentName: '',
        gradeCode: 'P1',
        birthYear: ''
      },
      gradeIndex: this.findGradeIndex('P1'),
      currentGradeLabel: this.getGradeOption('P1').label
    });
  },

  async handleEditStudent() {
    if (!this.data.selectedStudentId) {
      showError('请先选择学生档案');
      return;
    }

    wx.showLoading({
      title: '读取档案'
    });

    try {
      const student = await getStudent(this.data.selectedStudentId);
      this.setData({
        studentFormVisible: true,
        studentFormMode: 'edit',
        studentFormTitle: '编辑学生档案',
        studentForm: {
          id: student.id,
          studentName: student.studentName || '',
          gradeCode: student.gradeCode || 'P1',
          birthYear: student.birthYear ? String(student.birthYear) : ''
        },
        gradeIndex: this.findGradeIndex(student.gradeCode || 'P1'),
        currentGradeLabel: this.getGradeOption(student.gradeCode || 'P1').label
      });
    } catch (error) {
      showError(error.message || '读取学生档案失败');
    } finally {
      wx.hideLoading();
    }
  },

  handleStudentNameInput(event) {
    this.setData({
      'studentForm.studentName': event.detail.value
    });
  },

  handleBirthYearInput(event) {
    this.setData({
      'studentForm.birthYear': event.detail.value
    });
  },

  handleGradeChange(event) {
    const gradeIndex = Number(event.detail.value || 0);
    const gradeOption = this.data.gradeOptions[gradeIndex] || this.data.gradeOptions[0];
    this.setData({
      gradeIndex,
      currentGradeLabel: gradeOption.label,
      'studentForm.gradeCode': gradeOption.code
    });
  },

  handleCancelStudentForm() {
    this.setData({
      studentFormVisible: false
    });
  },

  async handleSaveStudent() {
    if (this.data.savingStudent) {
      return;
    }

    const form = this.data.studentForm;
    const studentName = (form.studentName || '').trim();
    if (!studentName) {
      showError('请填写学生姓名');
      return;
    }

    const payload = {
      studentName,
      gradeCode: form.gradeCode,
      birthYear: form.birthYear ? Number(form.birthYear) : null
    };

    this.setData({
      savingStudent: true
    });

    wx.showLoading({
      title: '保存中'
    });

    try {
      let savedStudent;
      if (this.data.studentFormMode === 'edit' && form.id) {
        savedStudent = await updateStudent(form.id, payload);
      } else {
        savedStudent = await createStudent(payload);
      }

      wx.showToast({
        title: '已保存',
        icon: 'success'
      });

      this.setData({
        studentFormVisible: false,
        selectedStudentId: savedStudent.id
      });

      await this.bootstrap();
    } catch (error) {
      showError(error.message || '保存失败');
    } finally {
      wx.hideLoading();
      this.setData({
        savingStudent: false
      });
    }
  },

  handleAiSwitchChange(event) {
    this.setData({
      aiEnabled: !!event.detail.value
    });
  },

  async handleContinue() {
    if (this.data.startingAssessment) {
      return;
    }

    if (!this.data.selectedStudentId) {
      showError('请先选择学生档案');
      return;
    }

    if (!this.data.selectedId) {
      showError('请先选择学习阶段');
      return;
    }

    this.setData({
      startingAssessment: true
    });

    wx.showLoading({
      title: '准备测评'
    });

    try {
      clearSession();
      const result = await startAssessment({
        studentId: this.data.selectedStudentId,
        selectedStageCode: this.data.selectedId,
        aiEnabled: this.data.aiEnabled
      });
      const stage = getStagePresentation(result.selectedStageCode, this.data.selectedStage ? this.data.selectedStage.label : result.selectedStageCode);
      saveSession({
        sessionNo: result.sessionNo,
        studentId: this.data.selectedStudentId,
        selectedStageCode: result.selectedStageCode,
        stage,
        totalQuestionCount: result.totalQuestionCount,
        pendingQuestion: result.firstQuestion || null,
        aiEnabled: this.data.aiEnabled,
        midCheckpointShown: false
      });

      wx.navigateTo({
        url: `/pages/quiz/quiz?sessionNo=${result.sessionNo}`
      });
    } catch (error) {
      showError(error.message || '开始测评失败');
    } finally {
      wx.hideLoading();
      this.setData({
        startingAssessment: false
      });
    }
  },

  handleOpenHistoryResult(event) {
    const { sessionNo, stageCode } = event.currentTarget.dataset;
    if (!sessionNo) {
      return;
    }

    wx.navigateTo({
      url: `/pages/result/result?sessionNo=${sessionNo}&stageCode=${stageCode || ''}`
    });
  },

  handleBack() {
    wx.navigateBack({
      delta: 1,
      fail() {
        wx.redirectTo({
          url: '/pages/home/home'
        });
      }
    });
  },

  findGradeIndex(gradeCode) {
    const gradeIndex = this.data.gradeOptions.findIndex((item) => item.code === gradeCode);
    return gradeIndex > -1 ? gradeIndex : 0;
  },

  getGradeOption(gradeCode) {
    const matched = this.data.gradeOptions.find((item) => item.code === gradeCode);
    return matched || this.data.gradeOptions[0];
  },

  formatDateTime(value) {
    if (!value) {
      return '未完成';
    }
    const text = String(value);
    return text.replace('T', ' ').slice(0, 16);
  }
});
