function getBaseUrl() {
  const app = getApp();
  return (app && app.globalData && app.globalData.apiBaseUrl) || 'http://127.0.0.1:8080';
}

function request({ url, method = 'GET', data }) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: `${getBaseUrl()}${url}`,
      method,
      data,
      header: {
        'content-type': 'application/json'
      },
      success(response) {
        const payload = response.data || {};
        if (response.statusCode >= 200 && response.statusCode < 300 && payload.code === 0) {
          resolve(payload.data);
          return;
        }
        reject(new Error(payload.message || `请求失败(${response.statusCode})`));
      },
      fail(error) {
        reject(new Error(error.errMsg || '网络请求失败'));
      }
    });
  });
}

function listStudents() {
  return request({
    url: '/api/students'
  });
}

function getStudent(studentId) {
  return request({
    url: `/api/students/${studentId}`
  });
}

function createStudent(data) {
  return request({
    url: '/api/students',
    method: 'POST',
    data
  });
}

function updateStudent(studentId, data) {
  return request({
    url: `/api/students/${studentId}`,
    method: 'PUT',
    data
  });
}

function getAssessmentStages() {
  return request({
    url: '/api/assessment/stages'
  });
}

function startAssessment(data) {
  return request({
    url: '/api/assessment/start',
    method: 'POST',
    data
  });
}

function getNextQuestion(sessionNo) {
  return request({
    url: `/api/assessment/${sessionNo}/next-question`
  });
}

function submitAnswer(sessionNo, data) {
  return request({
    url: `/api/assessment/${sessionNo}/answer`,
    method: 'POST',
    data
  });
}

function finishAssessment(sessionNo) {
  return request({
    url: `/api/assessment/${sessionNo}/finish`,
    method: 'POST'
  });
}

function getAssessmentResult(sessionNo) {
  return request({
    url: `/api/assessment/${sessionNo}/result`
  });
}

function getAssessmentHistory(studentId) {
  return request({
    url: `/api/assessment/history?studentId=${studentId}`
  });
}

module.exports = {
  createStudent,
  finishAssessment,
  getAssessmentHistory,
  getAssessmentResult,
  getAssessmentStages,
  getNextQuestion,
  getStudent,
  listStudents,
  startAssessment,
  submitAnswer,
  updateStudent
};
