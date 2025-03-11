<script>
import axiosInst from '@/axios';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarController,
  BarElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

// Chart.js 컴포넌트 등록
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarController,
  BarElement,
  Title,
  Tooltip,
  Legend
);

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
      chartInstance: null
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
        this.chartInstance.destroy();
        this.chartInstance = null;
      }
      // 기존 캔버스 제거
      const container = this.$refs.chartContainer;
      if (container) {
        container.innerHTML = '';
      }
    },

    createCanvas() {
      const container = this.$refs.chartContainer;
      if (!container) return null;
      
      // 기존 캔버스 제거
      container.innerHTML = '';
      
      const canvas = document.createElement('canvas');
      // 명시적 크기 설정
      canvas.style.width = '100%';
      canvas.style.height = '100%';
      container.appendChild(canvas);
      
      // 실제 캔버스 크기 설정
      const rect = container.getBoundingClientRect();
      canvas.width = rect.width;
      canvas.height = rect.height;
      
      return canvas;
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
            this.renderChart();
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
      const canvas = this.createCanvas();
      const ctx = canvas.getContext('2d');
      
      // 캔버스 크기 설정
      canvas.style.width = '100%';
      canvas.style.height = '100%';
      canvas.width = canvas.offsetWidth;
      canvas.height = canvas.offsetHeight;
      
      ctx.font = '14px Arial';
      ctx.textAlign = 'center';
      ctx.fillStyle = color;
      ctx.fillText(message, canvas.width/2, canvas.height/2);
    },

    renderChart() {
      try {
        this.destroyChart();
        
        // 캔버스 생성 및 컨텍스트 확인
        const canvas = this.createCanvas();
        if (!canvas) {
          console.error('캔버스 생성 실패');
          return;
        }
        
        const ctx = canvas.getContext('2d');
        if (!ctx) {
          console.error('캔버스 컨텍스트 획득 실패');
          return;
        }

        const labels = this.departmentStats.map(stat => stat.deptName || `부서 ${stat.deptId}`);
        const data = this.departmentStats.map(stat => stat.openRatio || 0);

        // requestAnimationFrame을 사용하여 DOM 업데이트 후 차트 생성
        requestAnimationFrame(() => {
          this.chartInstance = new ChartJS(ctx, {
            type: 'bar',
            data: {
              labels: labels,
              datasets: [{
                label: '링크 미탐지율 (%)',
                data: data,
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
              }]
            },
            options: {
              responsive: true,
              maintainAspectRatio: false,
              animation: {
                duration: 0 // 애니메이션 비활성화
              },
              scales: {
                y: {
                  beginAtZero: true,
                  max: 100
                }
              },
              plugins: {
                legend: {
                  display: true,
                  position: 'top'
                },
                title: {
                  display: false
                }
              }
            }
          });
        });
      } catch (error) {
        console.error('차트 렌더링 실패:', error);
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
      } else {
        this.selectedDate = null;
      }
    }
  }
}
</script>

<template>
  <div class="bg-white rounded-lg border shadow-sm p-6">
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Dropdown for selecting drill date -->
      <div class="md:col-span-1">
        <label for="drill-select" class="block text-sm font-medium text-gray-700 mb-2">훈련 날짜 선택</label>
        <select 
          id="drill-select" 
          v-model="selectedDrillId" 
          class="w-full rounded-md border border-gray-300 p-2"
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
        <div v-if="selectedDrillId && groupedDrills[selectedDate]" class="mt-4">
          <h4 class="text-sm font-medium text-gray-700 mb-2">해당 날짜 훈련 정보</h4>
          <ul class="text-sm text-gray-600">
            <li v-for="drill in groupedDrills[selectedDate]"
                :key="drill.id"
                class="mb-1"
            >
              {{ drill.date.split('T')[1].substring(0, 8) }} - {{ drill.recipients.length }}명 대상
            </li>
          </ul>
        </div>
      </div>
      
      <!-- Bar chart -->
      <div class="md:col-span-2">
        <h3 class="font-medium mb-4">부서별 링크 미탐지율</h3>
        
        <div v-if="!selectedDrillId" class="text-center py-10 text-gray-500">
          왼쪽에서 훈련회차를 선택해주세요.
        </div>
        
        <div v-else>
          <div class="relative h-80 border rounded-md p-4">
            <div ref="chartContainer" class="w-full h-full"></div>
          </div>
          
          <p class="text-xs text-gray-500 mt-2">※링크 미탐지율 : 모의 악성메일 내 링크를 클릭한 인원의 비율</p>
        </div>
      </div>
    </div>
    
    <!-- Detailed results table -->
    <div v-if="selectedDrillId" class="mt-8">
      <h3 class="font-medium mb-4">상세 결과</h3>
      <div v-if="departmentStats.length === 0" class="text-center py-4 text-gray-500">
        해당 훈련에 대한 부서별 통계가 없습니다.
      </div>
      <div v-else class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">부서</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">대상자 수</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">클릭 수</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">미탐지율</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">보안등급</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="stat in departmentStats" :key="stat.deptId">
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.deptName }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.totalEmployees }}명</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.clickedCount }}명</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.openRatio.toFixed(1) }}%</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.rating }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>


