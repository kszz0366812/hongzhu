// utils/test-report-api.js
const api = require('./api.js')

// 测试报告API接口
const testReportAPI = () => {
  console.log('🧪 开始测试报告API接口...')
  
  // 测试创建报告
  const testCreateReport = () => {
    console.log('📝 测试创建报告...')
    
         const reportData = {
       type: '1', // 1=日报
       title: '20240115日报',
       authorId: 1001, // 添加测试员工ID
       content: '这是测试报告的内容，包含今天的工作总结。',
       issues: '暂无问题',
       plan: '明天继续开发',
       reportDate: '2024-01-15'
     }
    
    return api.createReport(reportData)
      .then(res => {
        console.log('✅ 创建报告成功:', res)
        return res.data // 返回创建的报告ID
      })
      .catch(error => {
        console.error('❌ 创建报告失败:', error)
        throw error
      })
  }
  
  // 测试获取报告列表
  const testGetReportList = () => {
    console.log('📋 测试获取报告列表...')
    
    return api.getReportList({ page: 1, size: 10 })
      .then(res => {
        console.log('✅ 获取报告列表成功:', res)
        return res.data?.records || []
      })
      .catch(error => {
        console.error('❌ 获取报告列表失败:', error)
        throw error
      })
  }
  
  // 测试更新报告
  const testUpdateReport = (reportId) => {
    console.log('✏️ 测试更新报告...')
    
         const updateData = {
       id: reportId,
       type: '2', // 2=周报
       title: '20240115周报',
       authorId: 1001, // 添加测试员工ID
       content: '这是更新后的测试报告内容。',
       issues: '已解决问题',
       plan: '下周计划',
       reportDate: '2024-01-15'
     }
    
    return api.updateReport(updateData)
      .then(res => {
        console.log('✅ 更新报告成功:', res)
        return reportId
      })
      .catch(error => {
        console.error('❌ 更新报告失败:', error)
        throw error
      })
  }
  
  // 测试获取报告统计
  const testGetReportStatistics = () => {
    console.log('📊 测试获取报告统计...')
    
    return api.getReportStatistics()
      .then(res => {
        console.log('✅ 获取报告统计成功:', res)
        return res.data
      })
      .catch(error => {
        console.error('❌ 获取报告统计失败:', error)
        throw error
      })
  }
  
  // 测试删除报告
  const testDeleteReport = (reportId) => {
    console.log('🗑️ 测试删除报告...')
    
    return api.deleteReport(reportId)
      .then(res => {
        console.log('✅ 删除报告成功:', res)
        return res
      })
      .catch(error => {
        console.error('❌ 删除报告失败:', error)
        throw error
      })
  }
  
  // 执行完整的测试流程
  let createdReportId = null
  
  return testCreateReport()
    .then(reportId => {
      createdReportId = reportId
      return testGetReportList()
    })
    .then(() => {
      return testUpdateReport(createdReportId)
    })
    .then(() => {
      return testGetReportStatistics()
    })
    .then(() => {
      return testDeleteReport(createdReportId)
    })
    .then(() => {
      console.log('🎉 所有报告API测试完成！')
    })
    .catch(error => {
      console.error('💥 报告API测试失败:', error)
    })
}

// 导出测试函数
module.exports = {
  testReportAPI
}

// 如果在Node.js环境中直接运行此文件，则执行测试
if (typeof module !== 'undefined' && module.exports) {
  // 这里可以添加直接运行的逻辑
  console.log('报告API测试模块已加载')
} 