<script>
import axiosInst from '@/axios';
import Chart from 'chart.js/auto';

export default {
  data() {
    return {
      drills: [],
      selectedDrillId: null,
      departmentStats: [],
      loading: false,
      error: null,
      groupedDrills: {},
      selectedDate: null,
      chartInstance: null,
      chartAnimationInProgress: false
    }
  },
  
  async mounted() {
    await this.loadDrills();
  },
  
  beforeUnmount() {
    this.destroyChart();
  },
  
  methods: {
    destroyChart() {
      if (this.chartInstance) {
        if (this.chartInstance.options) {
          this.chartInstance.options.animation = false;
          this.chartInstance.options.animations = false;
          this.chartInstance.options.transitions = false;
        }
        
        try {
          this.chartInstance.stop();
          this.chartInstance.destroy();
        } catch (e) {
          console.error('차트 제거 중 오류:', e);
        }
        
        this.chartInstance = null;
      }
      this.chartAnimationInProgress = false;
    },

    async loadDrills() {
      try {
        console.log('훈련 목록 로딩 시작');
        const response = await axiosInst.get('/drill/list');
        console.log('서버 응답:', response.data);
        
        this.drills = response.data;
        this.groupedDrills = this.drills.reduce((groups, drill) => {
          const date = this.formatDate(drill.date);
          if (!groups[date]) {
            groups[date] = [];
          }
          groups[date].push(drill);
          return groups;
        }, {});
        
        console.log('그룹화된 데이터:', this.groupedDrills);
      } catch (error) {
        console.error('훈련 목록 로드 실패:', error);
        this.error = '훈련 목록을 불러오는데 실패했습니다.';
      }
    },
    
    formatDate(dateStr) {
      if (!dateStr) return '';
      return dateStr.split('T')[0]; // YYYY-MM-DD 부분만 반환
    },

    async loadDrillStats() {
      if (!this.selectedDrillId) return;
      
      try {
        this.loading = true;
        this.destroyChart();

        const response = await axiosInst.get(`/drill/${this.selectedDrillId}/stats`);
        
        if (!response.data || response.data.length === 0) {
          this.departmentStats = [];
          this.renderMessage('해당 훈련에 대한 부서별 통계가 없습니다.');
        } else {
          this.departmentStats = response.data.map(stat => ({
            deptId: stat.deptId,
            deptName: stat.deptName,
            totalEmployees: stat.totalEmployees || 0,
            clickedCount: stat.clickedCount || 0,
            openRatio: stat.openRatio || 0,
            rating: stat.rating || 'N/A'
          }));
          
          if (this.departmentStats.length > 0) {
            await this.$nextTick();
            await this.renderChart(false);
          }
        }
      } catch (error) {
        console.error('통계 데이터 로드 실패:', error);
        this.error = '통계 데이터를 불러오는데 실패했습니다.';
        this.renderMessage('통계 데이터를 불러오는데 실패했습니다.', '#dc2626');
      } finally {
        this.loading = false;
      }
    },

    renderMessage(message, color = '#666') {
      this.destroyChart();
      
      const canvas = this.$refs.chartCanvas;
      if (!canvas) {
        console.error('메시지 렌더링을 위한 캔버스를 찾을 수 없음');
        return;
      }
      
      const ctx = canvas.getContext('2d');
      if (!ctx) {
        console.error('메시지 렌더링을 위한 컨텍스트를 가져올 수 없음');
        return;
      }
      
      canvas.width = 600;
      canvas.height = 300;
      
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      
      ctx.font = '14px Arial';
      ctx.textAlign = 'center';
      ctx.fillStyle = color;
      ctx.fillText(message, canvas.width/2, canvas.height/2);
    },

    async renderChart(useAnimation = false) {
      try {
        this.destroyChart();
        
        if (!this.departmentStats || this.departmentStats.length === 0) {
          this.renderMessage('해당 훈련에 대한 부서별 통계가 없습니다.');
          return;
        }
        
        const canvas = this.$refs.chartCanvas;
        if (!canvas) {
          console.error('캔버스 요소를 찾을 수 없음');
          return;
        }
        
        canvas.width = 600;
        canvas.height = 300;
        
        const ctx = canvas.getContext('2d');
        if (!ctx) {
          console.error('캔버스 컨텍스트를 가져올 수 없음');
          return;
        }
        
        const labels = this.departmentStats.map(stat => stat.deptName || `부서 ${stat.deptId}`);
        const data = this.departmentStats.map(stat => stat.openRatio || 0);
        
        await this.$nextTick();
        
        this.chartAnimationInProgress = useAnimation;
        
        this.chartInstance = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: labels,
            datasets: [{
              label: '링크 미탐지율 (%)',
              data: data,
              backgroundColor: 'rgba(255, 59, 48, 0.7)',
              borderColor: 'rgba(255, 59, 48, 1)',
              borderWidth: 1
            }]
          },
          options: {
            responsive: false,
            maintainAspectRatio: false,
            animation: useAnimation ? {
              duration: 300,
              onComplete: () => {
                this.chartAnimationInProgress = false;
              }
            } : false,
            scales: {
              y: {
                beginAtZero: true,
                max: 100,
                grid: {
                  color: 'rgba(0, 0, 0, 0.05)'
                }
              },
              x: {
                grid: {
                  display: false
                }
              }
            },
            plugins: {
              legend: {
                display: true,
                position: 'top',
                labels: {
                  boxWidth: 15,
                  usePointStyle: true,
                  pointStyle: 'rect'
                }
              },
              title: {
                display: false
              }
            }
          }
        });
      } catch (error) {
        console.error('차트 렌더링 실패:', error);
        this.chartAnimationInProgress = false;
        this.renderMessage('차트 렌더링에 실패했습니다.', '#dc2626');
      }
    },

    findDateByDrillId(drillId) {
      for (const [date, drills] of Object.entries(this.groupedDrills)) {
        if (drills.some(drill => drill.id === drillId)) {
          return date;
        }
      }
      return null;
    }
  },
  
  computed: {
    dateOptions() {
      return Object.keys(this.groupedDrills).sort().reverse();
    }
  },
  
  watch: {
    selectedDrillId(newId) {
      this.destroyChart();
      if (newId) {
        this.loadDrillStats();
        this.selectedDate = this.findDateByDrillId(newId);
      } else {
        this.selectedDate = null;
      }
    }
  }
}
</script>

<template>
  <div class="result-container">
    <div class="page-header">
      <h1 class="page-title">모의 위협메일 대응 결과</h1>
      <p class="page-description">부서별 보안 인식 수준과 대응 결과를 확인하세요</p>
    </div>
    
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Dropdown for selecting drill date -->
        <div class="md:col-span-1">
          <div class="section-header">
            <div class="section-icon">📅</div>
            <h2 class="section-title">훈련 날짜 선택</h2>
          </div>
          
          <select 
            id="drill-select" 
            v-model="selectedDrillId" 
            class="select-control"
            @change="loadDrillStats"
          >
            <option value="">날짜를 선택하세요</option>
            <option 
              v-for="date in dateOptions" 
              :key="date"
              :value="groupedDrills[date][0].id"
            >
              {{ date }} ({{ groupedDrills[date].length }}회 실시)
            </option>
          </select>
          
          <!-- 선택된 날짜의 상세 정보 -->
          <div v-if="selectedDrillId && selectedDate && groupedDrills[selectedDate]" class="date-details">
            <h4 class="date-details-title">해당 날짜 훈련 정보</h4>
            <ul class="date-details-list">
              <li v-for="drill in groupedDrills[selectedDate]"
                  :key="drill.id"
                  class="date-details-item"
              >
                <span class="time-badge">{{ drill.date.split('T')[1].substring(0, 8) }}</span>
                <span class="recipient-count">{{ drill.recipients.length }}명 대상</span>
              </li>
            </ul>
          </div>
        </div>
        
        <!-- Bar chart -->
        <div class="md:col-span-2">
          <div class="section-header">
            <div class="section-icon">📊</div>
            <h2 class="section-title">부서별 링크 미탐지율</h2>
          </div>
          
          <div v-if="!selectedDrillId" class="chart-placeholder">
            <div class="placeholder-icon">📈</div>
            <p>왼쪽에서 훈련회차를 선택해주세요.</p>
          </div>
          
          <div v-else>
            <div class="chart-container">
              <div v-if="departmentStats.length > 0" class="debug-info">
                {{ departmentStats.length }}개 부서 데이터 로드됨
              </div>
              
              <canvas ref="chartCanvas" class="chart-canvas" width="600" height="300"></canvas>
              
              <div v-if="loading" class="chart-loading">
                <div class="loading-spinner"></div>
                <p>데이터 로딩 중...</p>
              </div>
            </div>
            
            <p class="chart-note">※링크 미탐지율 : 모의 악성메일 내 링크를 클릭한 인원의 비율</p>
          </div>
        </div>
      </div>
      
      <!-- Detailed results table -->
      <div v-if="selectedDrillId" class="results-table-section">
        <div class="section-header">
          <div class="section-icon">📋</div>
          <h2 class="section-title">상세 결과</h2>
        </div>
        
        <div v-if="departmentStats.length === 0" class="empty-state">
          <div class="empty-icon">🔍</div>
          <p>해당 훈련에 대한 부서별 통계가 없습니다.</p>
        </div>
        
        <div v-else class="table-container">
          <table class="results-table">
            <thead>
              <tr>
                <th>부서</th>
                <th>대상자 수</th>
                <th>클릭 수</th>
                <th>미탐지율</th>
                <th>보안등급</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="stat in departmentStats" :key="stat.deptId">
                <td>{{ stat.deptName }}</td>
                <td>{{ stat.totalEmployees }}명</td>
                <td>{{ stat.clickedCount }}명</td>
                <td>
                  <div class="ratio-display">
                    <div class="ratio-bar" :style="`width: ${stat.openRatio}%`"></div>
                    <span>{{ Number(stat.openRatio).toFixed(1) }}%</span>
                  </div>
                </td>
                <td>
                  <span class="security-rating" :class="`rating-${stat.rating}`">
                    {{ stat.rating }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.result-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.page-header {
  margin-bottom: 1rem;
}

.page-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1e2639;
  margin-bottom: 0.5rem;
}

.page-description {
  color: #6b7280;
  font-size: 1rem;
}

.card {
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06);
  padding: 1.5rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.section-icon {
  font-size: 1.25rem;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e2639;
}

.select-control {
  width: 100%;
  padding: 0.625rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  background-color: white;
  font-size: 0.875rem;
  color: #1f2937;
  margin-bottom: 1rem;
  transition: border-color 0.15s ease-in-out;
}

.select-control:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.date-details {
  background-color: #f9fafb;
  border-radius: 0.375rem;
  padding: 1rem;
  margin-top: 1rem;
}

.date-details-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #4b5563;
  margin-bottom: 0.75rem;
}

.date-details-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.date-details-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.date-details-item:last-child {
  border-bottom: none;
}

.time-badge {
  background-color: #e5e7eb;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  color: #4b5563;
}

.recipient-count {
  font-size: 0.875rem;
  color: #6b7280;
}

.chart-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  background-color: #f9fafb;
  border-radius: 0.375rem;
  border: 1px dashed #d1d5db;
}

.placeholder-icon {
  font-size: 2.5rem;
  color: #9ca3af;
  margin-bottom: 1rem;
}

.chart-container {
  position: relative;
  height: 300px;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
  padding: 1rem;
  background-color: white;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chart-canvas {
  max-width: 100%;
  height: 300px;
  display: block;
}

.debug-info {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 0, 0, 0.1);
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 10px;
  color: #666;
  z-index: 10;
}

.chart-loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.8);
}

.loading-spinner {
  width: 2rem;
  height: 2rem;
  border: 3px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 0.5rem;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.chart-note {
  font-size: 0.75rem;
  color: #6b7280;
  margin-top: 0.5rem;
}

.results-table-section {
  margin-top: 2rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 0;
  background-color: #f9fafb;
  border-radius: 0.375rem;
  border: 1px dashed #d1d5db;
}

.empty-icon {
  font-size: 2.5rem;
  color: #9ca3af;
  margin-bottom: 1rem;
}

.table-container {
  overflow-x: auto;
  border-radius: 0.375rem;
  border: 1px solid #e5e7eb;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
}

.results-table th {
  background-color: #f9fafb;
  padding: 0.75rem 1rem;
  text-align: left;
  font-size: 0.75rem;
  font-weight: 600;
  color: #4b5563;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  border-bottom: 1px solid #e5e7eb;
}

.results-table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e5e7eb;
  font-size: 0.875rem;
  color: #1f2937;
}

.results-table tr:last-child td {
  border-bottom: none;
}

.ratio-display {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.ratio-bar {
  position: absolute;
  height: 0.5rem;
  background-color: rgba(255, 59, 48, 0.2);
  border-radius: 0.25rem;
  z-index: 0;
}

.ratio-display span {
  position: relative;
  z-index: 1;
  padding-left: 0.5rem;
}

.security-rating {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-weight: 600;
  font-size: 0.75rem;
}

.rating-A {
  background-color: #10b981;
  color: white;
}

.rating-B {
  background-color: #3b82f6;
  color: white;
}

.rating-C {
  background-color: #f59e0b;
  color: white;
}

.rating-D {
  background-color: #ef4444;
  color: white;
}

.rating-F {
  background-color: #7f1d1d;
  color: white;
}

@media (max-width: 768px) {
  .card {
    padding: 1rem;
  }
  
  .results-table th,
  .results-table td {
    padding: 0.5rem;
  }
}
</style>

