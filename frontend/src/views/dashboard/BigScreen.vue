<template>
  <div class="bigscreen-bg">
    <header class="bigscreen-header fade-in">
      <div class="bigscreen-title">
        <el-icon class="logo"><Promotion /></el-icon>
        <span>经营数据驾驶舱</span>
      </div>
      <div class="bigscreen-user">
          <el-button type="primary" @click="goLogin">后台管理</el-button>
      </div>
    </header>
    <main class="bigscreen-main fade-in">
      <section class="bigscreen-row">
        <div class="bigscreen-card kpi" v-for="kpi in kpis" :key="kpi.title">
          <div class="kpi-title">{{ kpi.title }}</div>
          <div class="kpi-value">{{ kpi.value }}</div>
        </div>
      </section>
      <section class="bigscreen-row charts">
        <div class="bigscreen-card chart" style="flex:2">
          <div class="chart-title">区域销售额对比</div>
          <v-chart :option="regionBarOption" autoresize class="echart" />
        </div>
        <div class="bigscreen-card chart" style="flex:1">
          <div class="chart-title">商品销售分布</div>
          <v-chart :option="productPieOption" autoresize class="echart" />
        </div>
      </section>
      <section class="bigscreen-row charts">
        <div class="bigscreen-card chart" style="flex:1">
          <div class="chart-title">员工业绩趋势</div>
          <v-chart :option="employeeLineOption" autoresize class="echart" />
        </div>
        <div class="bigscreen-card chart" style="flex:1">
          <div class="chart-title">商品销售趋势</div>
          <v-chart :option="productLineOption" autoresize class="echart" />
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';
import { Promotion } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue';
import { use } from 'echarts/core';
import VChart from 'vue-echarts';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, PieChart, LineChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components';

use([
  CanvasRenderer,
  BarChart,
  PieChart,
  LineChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent
]);

const userStore = useUserStore();
const router = useRouter();

const goLogin = () => {
  const token = localStorage.getItem('token');
  if (token) {
    router.push('/portal');
  } else {
    router.push('/login');
  }
};

const kpis = [
  { title: '今日销售额', value: '￥1,203,400' },
  { title: '本月拜访数', value: '3,245' },
  { title: '活跃员工数', value: '128' },
  { title: '商品总数', value: '56' }
];

// 区域销售额对比（柱状图）
const regionBarOption = ref({
  color: ['#3398DB'],
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: {
    type: 'category',
    data: ['华东', '华南', '华北', '西南', '西北'],
    axisTick: { alignWithLabel: true },
    axisLabel: { color: '#fff' }
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#fff' }
  },
  series: [
    {
      name: '销售额',
      type: 'bar',
      barWidth: '60%',
      data: [320000, 240000, 180000, 150000, 90000],
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#4fc3f7' },
            { offset: 1, color: '#1976d2' }
          ]
        }
      }
    }
  ]
});

// 商品销售分布（饼图）
const productPieOption = ref({
  tooltip: { trigger: 'item' },
  legend: {
    orient: 'vertical',
    left: '2%',
    bottom: '1%',
    textStyle: { color: '#fff', fontSize: 16 },
    itemWidth: 24,
    itemHeight: 16
  },
  series: [
    {
      name: '商品销售',
      type: 'pie',
      radius: ['55%', '80%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: 18, fontWeight: 'bold' } },
      labelLine: { show: false },
      center: ['60%', '55%'],
      data: [
        { value: 1048, name: '饮料' },
        { value: 735, name: '酒水' },
        { value: 580, name: '零食' },
        { value: 484, name: '乳制品' },
        { value: 300, name: '其他' }
      ]
    }
  ]
});

// 员工业绩趋势（折线图）
const employeeLineOption = ref({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    axisLabel: { color: '#fff' }
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#fff' }
  },
  series: [
    {
      name: '业绩',
      type: 'line',
      data: [12000, 14000, 10000, 18000, 16000, 21000, 19000],
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(33,150,243,0.5)' },
            { offset: 1, color: 'rgba(33,150,243,0.1)' }
          ]
        }
      },
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3, color: '#4fc3f7' }
    }
  ]
});

// 商品销售趋势（折线图）
const productLineOption = ref({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月'],
    axisLabel: { color: '#fff' }
  },
  yAxis: {
    type: 'value',
    axisLabel: { color: '#fff' }
  },
  series: [
    {
      name: '销量',
      type: 'line',
      data: [320, 400, 350, 500, 420, 600, 550],
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(79,195,247,0.5)' },
            { offset: 1, color: 'rgba(79,195,247,0.1)' }
          ]
        }
      },
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3, color: '#4fc3f7' }
    }
  ]
});
</script>

<style scoped>
.bigscreen-bg {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%);
  color: #fff;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  overflow-x: hidden;
}
:global(body), :global(html) {
  margin: 0;
  padding: 0;
  background: transparent;
  width: 100vw;
  overflow-x: hidden;
}
.bigscreen-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px 48px 0 48px;
}
.bigscreen-title {
  display: flex;
  align-items: center;
  font-size: 2.2rem;
  font-weight: bold;
  letter-spacing: 2px;
  color: #fff;
  text-shadow: 0 2px 8px #000a;
}
.logo {
  width: 48px;
  height: 48px;
  margin-right: 16px;
  font-size: 48px;
}
.bigscreen-user {
  display: flex;
  align-items: center;
  gap: 12px;
}
.nickname {
  font-size: 1.1rem;
  margin-left: 8px;
  color: #fff;
  text-shadow: 0 1px 4px #0008;
}
.bigscreen-main {
  padding: 32px 48px 0 48px;
}
.bigscreen-row {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;
}
.bigscreen-card {
  background: rgba(32, 58, 67, 0.85);
  border-radius: 18px;
  box-shadow: 0 4px 24px 0 rgba(0,0,0,0.18);
  padding: 32px 24px;
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  transition: box-shadow 0.3s, transform 0.3s;
  animation: fadeInUp 0.8s;
}
.bigscreen-card:hover {
  box-shadow: 0 8px 32px 0 rgba(25, 108, 181, 0.25);
  transform: translateY(-4px) scale(1.02);
}
.kpi {
  align-items: center;
  justify-content: center;
  text-align: center;
  animation: fadeIn 1s;
}
.kpi-title {
  font-size: 1.1rem;
  color: #b0e0ff;
  margin-bottom: 8px;
  text-shadow: 0 1px 4px #0006;
}
.kpi-value {
  font-size: 2.2rem;
  font-weight: bold;
  color: #4fc3f7;
  letter-spacing: 1px;
  animation: fadeIn 1.2s;
  text-shadow: 0 2px 8px #000a;
}
.charts .chart {
  min-height: 320px;
}
.chart-title {
  font-size: 1.1rem;
  color: #b0e0ff;
  margin-bottom: 16px;
  text-shadow: 0 1px 4px #0006;
}
.echart {
  width: 100%;
  height: 220px;
  background: transparent;
  border-radius: 12px;
  animation: fadeIn 1.2s;
}
.fade-in {
  animation: fadeIn 1.2s;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: none; }
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: none; }
}

/* 移动端适配 */
@media (max-width: 900px) {
  .bigscreen-header {
    flex-direction: column;
    align-items: flex-start;
    padding: 24px 16px 0 16px;
  }
  .bigscreen-main {
    padding: 16px 4px 0 4px;
  }
  .bigscreen-title {
    font-size: 1.3rem;
    margin-bottom: 12px;
  }
  .logo {
    width: 32px;
    height: 32px;
    font-size: 32px;
    margin-right: 8px;
  }
  .bigscreen-row {
    flex-direction: column;
    gap: 16px;
    margin-bottom: 16px;
  }
  .bigscreen-card {
    padding: 16px 8px;
    border-radius: 12px;
  }
  .echart {
    height: 180px;
  }
}
</style>

<script>
export default {};
</script> 