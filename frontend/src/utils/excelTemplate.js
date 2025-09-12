import request from '@/utils/request'

/**
 * Excel模版导出工具类
 * 提供统一的模版下载功能
 */
export class ExcelTemplateUtil {
  
  /**
   * 支持的导入类型映射
   */
  static IMPORT_TYPES = {
    EMPLOYEE: '员工导入',
    PRODUCT: '产品导入', 
    TERMINAL: '终端商户导入',
    WHOLESALER: '批发商导入',
    SALES: '成交记录导入',
    TASK_TARGET: '任务目标导入',
    VISIT_RECORD: '拜访记录导入'
  }

  /**
   * 下载Excel模版
   * @param {string} importType - 导入类型
   * @param {string} fileName - 文件名（可选，默认使用类型名称）
   */
  static async downloadTemplate(importType, fileName = null) {
    try {
      // 使用axios请求下载模版
      const response = await request({
        url: '/excel/template',
        method: 'get',
        params: { importType },
        responseType: 'blob' // 重要：设置响应类型为blob
      })
      
      // 检查响应状态
      if (response.status !== 200) {
        throw new Error(`下载失败，状态码: ${response.status}`)
      }
      
      // 创建blob对象
      const blob = new Blob([response.data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      })
      
      // 创建下载链接
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = fileName || `${importType}_模版.xlsx`
      link.style.display = 'none'
      
      // 添加到DOM并触发下载
      document.body.appendChild(link)
      link.click()
      
      // 清理DOM和URL对象
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      
      return { success: true, message: '模版下载成功' }
    } catch (error) {
      console.error('下载模版失败:', error)
      return { success: false, message: '下载模版失败: ' + error.message }
    }
  }

  /**
   * 获取支持的导入类型列表
   */
  static async getSupportedTypes() {
    try {
      const response = await request({
        url: '/excel/supported-types',
        method: 'get'
      })
      return response.data || []
    } catch (error) {
      console.error('获取支持的导入类型失败:', error)
      return []
    }
  }

  /**
   * 员工导入模版下载
   */
  static async downloadEmployeeTemplate() {
    return this.downloadTemplate(this.IMPORT_TYPES.EMPLOYEE, '员工信息导入模版.xlsx')
  }

  /**
   * 产品导入模版下载
   */
  static async downloadProductTemplate() {
    return this.downloadTemplate(this.IMPORT_TYPES.PRODUCT, '产品信息导入模版.xlsx')
  }

  /**
   * 终端商户导入模版下载
   */
  static async downloadTerminalTemplate() {
    return this.downloadTemplate(this.IMPORT_TYPES.TERMINAL, '终端商户导入模版.xlsx')
  }

  /**
   * 批发商导入模版下载
   */
  static async downloadWholesalerTemplate() {
    return this.downloadTemplate(this.IMPORT_TYPES.WHOLESALER, '批发商导入模版.xlsx')
  }

  /**
   * 成交记录导入模版下载
   */
  static async downloadSalesTemplate() {
    return this.downloadTemplate(this.IMPORT_TYPES.SALES, '成交记录导入模版.xlsx')
  }

  /**
   * 任务目标导入模版下载
   */
  static async downloadTaskTargetTemplate() {
    return this.downloadTemplate(this.IMPORT_TYPES.TASK_TARGET, '任务目标导入模版.xlsx')
  }

  /**
   * 拜访记录导入模版下载
   */
  static async downloadVisitRecordTemplate() {
    return this.downloadTemplate(this.IMPORT_TYPES.VISIT_RECORD, '拜访记录导入模版.xlsx')
  }
}

// 导出默认实例
export default ExcelTemplateUtil