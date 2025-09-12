<template>
  <el-card>
    <div class="statistics-container">
      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon total">
              <el-icon><Folder /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.totalProjects || 0 }}</div>
              <div class="stat-label">总项目数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon active">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.activeProjects || 0 }}</div>
              <div class="stat-label">进行中项目</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon completed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.completedProjects || 0 }}</div>
              <div class="stat-label">已完成项目</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon tasks">
              <el-icon><List /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ statistics.totalTasks || 0 }}</div>
              <div class="stat-label">总任务数</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20" class="charts-section">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>项目状态分布</span>
              </div>
            </template>
            <div ref="statusChartRef" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>项目进度统计</span>
              </div>
            </template>
            <div ref="progressChartRef" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="charts-section">
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>任务类型分布</span>
              </div>
            </template>
            <div ref="taskTypeChartRef" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>月度项目趋势</span>
              </div>
            </template>
            <div ref="trendChartRef" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 项目列表 -->
      <el-card class="project-list-card">
        <template #header>
          <div class="card-header">
            <span>项目概览</span>
            <el-button type="primary" size="small" @click="refreshData">刷新数据</el-button>
          </div>
        </template>
        <el-table :data="projectList" border stripe style="width: 100%" v-loading="loading">
          <el-table-column prop="projectName" label="项目名称" width="200" align="center" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="progress" label="进度" width="150" align="center">
            <template #default="scope">
              <el-progress :percentage="scope.row.progress || 0" :stroke-width="8" />
            </template>
          </el-table-column>
          <el-table-column prop="memberCount" label="成员数" width="80" align="center" />
          <el-table-column prop="taskCount" label="任务数" width="80" align="center" />
          <el-table-column prop="startDate" label="开始日期" width="120" align="center">
            <template #default="scope">
              {{ formatDate(scope.row.startDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="endDate" label="结束日期" width="120" align="center">
            <template #default="scope">
              {{ formatDate(scope.row.endDate) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Folder, Clock, Check, List } from '@element-plus/icons-vue'
import { projectApi, projectTaskApi } from '@/api/project'
import * as echarts from 'echarts'

// 响应式数据
const statistics = ref({})
const projectList = ref([])
const loading = ref(false)

// 图表引用
const statusChartRef = ref()
const progressChartRef = ref()
const taskTypeChartRef = ref()
const trendChartRef = ref()

// 图表实例
let statusChart = null
let progressChart = null
let taskTypeChart = null
let trendChart = null

// 生命周期
onMounted(() => {
  fetchStatistics()
  fetchProjectList()
  nextTick(() => {
    initCharts()
  })
})

// 获取统计数据
async function fetchStatistics() {
  try {
    const [projectStats, taskStats] = await Promise.all([
      projectApi.getStatistics(),
      projectTaskApi.getStatistics()
    ])
    
    if (projectStats.code === 200) {
      statistics.value = { ...statistics.value, ...projectStats.data }
    }
    
    if (taskStats.code === 200) {
      statistics.value = { ...statistics.value, ...taskStats.data }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 获取项目列表
async function fetchProjectList() {
  try {
    loading.value = true
    const response = await projectApi.getAll()
    if (response.code === 200) {
      projectList.value = response.data || []
      updateCharts()
    } else {
      ElMessage.error(response.message || '获取项目列表失败')
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
    ElMessage.error('获取项目列表失败')
  } finally {
    loading.value = false
  }
}

// 初始化图表
function initCharts() {
  initStatusChart()
  initProgressChart()
  initTaskTypeChart()
  initTrendChart()
}

// 项目状态分布图
function initStatusChart() {
  if (!statusChartRef.value) return
  
  statusChart = echarts.init(statusChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '项目状态',
        type: 'pie',
        radius: '50%',
        data: [
          { value: statistics.value.activeProjects || 0, name: '进行中' },
          { value: statistics.value.completedProjects || 0, name: '已完成' },
          { value: statistics.value.pausedProjects || 0, name: '已暂停' },
          { value: statistics.value.cancelledProjects || 0, name: '已取消' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  statusChart.setOption(option)
}

// 项目进度统计图
function initProgressChart() {
  if (!progressChartRef.value) return
  
  progressChart = echarts.init(progressChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: ['0-20%', '21-40%', '41-60%', '61-80%', '81-100%']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '项目数量',
        type: 'bar',
        data: [0, 0, 0, 0, 0],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  progressChart.setOption(option)
}

// 任务类型分布图
function initTaskTypeChart() {
  if (!taskTypeChartRef.value) return
  
  taskTypeChart = echarts.init(taskTypeChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '任务类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        data: [
          { value: 0, name: '开发' },
          { value: 0, name: '测试' },
          { value: 0, name: '设计' },
          { value: 0, name: '文档' },
          { value: 0, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  taskTypeChart.setOption(option)
}

// 月度项目趋势图
function initTrendChart() {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增项目', '完成项目']
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增项目',
        type: 'line',
        data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        smooth: true
      },
      {
        name: '完成项目',
        type: 'line',
        data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        smooth: true
      }
    ]
  }
  trendChart.setOption(option)
}

// 更新图表数据
function updateCharts() {
  updateProgressChart()
  updateTaskTypeChart()
  updateTrendChart()
}

// 更新进度统计图
function updateProgressChart() {
  if (!progressChart) return
  
  const progressRanges = [0, 0, 0, 0, 0] // 0-20%, 21-40%, 41-60%, 61-80%, 81-100%
  
  projectList.value.forEach(project => {
    const progress = project.progress || 0
    if (progress <= 20) progressRanges[0]++
    else if (progress <= 40) progressRanges[1]++
    else if (progress <= 60) progressRanges[2]++
    else if (progress <= 80) progressRanges[3]++
    else progressRanges[4]++
  })
  
  progressChart.setOption({
    series: [{
      data: progressRanges
    }]
  })
}

// 更新任务类型图
function updateTaskTypeChart() {
  if (!taskTypeChart) return
  
  // 这里需要从任务API获取数据，暂时使用模拟数据
  const taskTypeData = [
    { value: 15, name: '开发' },
    { value: 8, name: '测试' },
    { value: 5, name: '设计' },
    { value: 3, name: '文档' },
    { value: 2, name: '其他' }
  ]
  
  taskTypeChart.setOption({
    series: [{
      data: taskTypeData
    }]
  })
}

// 更新趋势图
function updateTrendChart() {
  if (!trendChart) return
  
  // 这里需要从历史数据计算，暂时使用模拟数据
  const trendData = {
    new: [2, 3, 1, 4, 2, 3, 2, 1, 3, 2, 1, 2],
    completed: [1, 2, 2, 3, 1, 2, 1, 2, 1, 3, 2, 1]
  }
  
  trendChart.setOption({
    series: [
      { data: trendData.new },
      { data: trendData.completed }
    ]
  })
}

// 获取状态类型
function getStatusType(status) {
  const statusMap = {
    '进行中': 'success',
    '已完成': 'info',
    '已暂停': 'warning',
    '已取消': 'danger'
  }
  return statusMap[status] || 'info'
}

// 格式化日期
function formatDate(date) {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 刷新数据
function refreshData() {
  fetchStatistics()
  fetchProjectList()
}
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.tasks {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-section {
  margin-bottom: 30px;
}

.chart-card {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.project-list-card {
  margin-top: 20px;
}

.el-table {
  margin-bottom: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-cards .el-col {
    margin-bottom: 16px;
  }
  
  .charts-section .el-col {
    margin-bottom: 16px;
  }
}
</style>
