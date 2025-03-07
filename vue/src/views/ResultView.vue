
<!-- <script setup>
import { onBeforeMount, ref } from 'vue';
import {useRoute, useRouter} from 'vue-router';
import axiosInst from '@/axios';
import { onBeforeMount, ref } from 'vue';
import {useRoute, useRouter} from 'vue-router';
import axiosInst from '@/axios';

const firstName = ref("");
const lastName = ref("");
const route = useRoute();
const router = useRouter();
const id = Number(route.query.id);

데이터 사전 준비
onBeforeMount(()=>{
  getData();
});

const getData = () => {
  axiosInst.get("/emp/"+id).then((res)=>{
      firstName.value = res.data.firstName;
      lastName.value = res.data.lastName;
  });
}
백앤드에 수정 요청
const onModifyHandler = () => {
  const employee = {
    firstName: firstName.value,
    lastName: lastName.value,
  }
  axiosInst.put("/emp/"+id, employee).then((res)=>{
    if(res.status === 200) router.push("/");
  });
}
</script> -->
<script>

export default {
  data() {
    return {
      drills: [],
      selectedDrillId: null,
      chartContext: null,
      chart: null,
      colors: [
        '#4C51BF', '#ED8936', '#48BB78', '#38B2AC', 
        '#4299E1', '#667EEA', '#9F7AEA', '#ED64A6'
      ]
    }
  },
  computed: {
    selectedDrill() {
      if (!this.selectedDrillId) return null;
      return this.drills.find(drill => drill.id === this.selectedDrillId);
    }
  },
  mounted() {
    this.loadDrills();
    if (this.drills.length > 0) {
      this.selectedDrillId = this.drills[0].id;
      this.$nextTick(() => {
        this.initChart();
      });
    }
  },
  methods: {
    loadDrills() {
      // In a real app, this would be an API call
      const savedDrills = JSON.parse(localStorage.getItem('mockEmailDrills') || '[]');
      this.drills = savedDrills;
      
      // If no drills exist, create a sample one
      if (this.drills.length === 0) {
        this.createSampleDrill();
      }
    },
    createSampleDrill() {
      const departments = ['개발팀', '마케팅팀', '인사팀', '영업팀'];
      const departmentStats = departments.map(dept => {
        const total = Math.floor(Math.random() * 20) + 5;
        const clicked = Math.floor(Math.random() * total);
        return {
          department: dept,
          total,
          clicked,
          ratio: clicked / total
        };
      });
      
      const sampleDrill = {
        id: new Date().getTime(),
        date: new Date().toISOString(),
        recipients: [],
        departmentStats
      };
      
      this.drills = [sampleDrill];
      this.selectedDrillId = sampleDrill.id;
      
      // Save to localStorage
      localStorage.setItem('mockEmailDrills', JSON.stringify(this.drills));
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    },
    initChart() {
      const canvas = this.$refs.chartCanvas;
      if (!canvas) return;
      
      this.chartContext = canvas.getContext('2d');
      this.updateChart();
    },
    updateChart() {
      if (!this.chartContext || !this.selectedDrill) return;
      
      const ctx = this.chartContext;
      const stats = this.selectedDrill.departmentStats;
      const width = ctx.canvas.width;
      const height = ctx.canvas.height;
      
      // Clear canvas
      ctx.clearRect(0, 0, width, height);
      
      // Draw chart
      const barWidth = (width - 100) / stats.length;
      const maxRatio = Math.max(...stats.map(s => s.ratio));
      const scale = (height - 60) / (maxRatio || 1);
      
      // Draw axes
      ctx.beginPath();
      ctx.moveTo(50, 20);
      ctx.lineTo(50, height - 30);
      ctx.lineTo(width - 20, height - 30);
      ctx.strokeStyle = '#CBD5E0';
      ctx.stroke();
      
      // Draw bars
      stats.forEach((stat, index) => {
        const x = 50 + (index * barWidth) + 10;
        const barHeight = stat.ratio * scale;
        const y = height - 30 - barHeight;
        
        // Draw bar
        ctx.fillStyle = this.colors[index % this.colors.length];
        ctx.fillRect(x, y, barWidth - 20, barHeight);
        
        // Draw percentage
        ctx.fillStyle = '#2D3748';
        ctx.font = '12px Arial';
        ctx.textAlign = 'center';
        ctx.fillText(`${(stat.ratio * 100).toFixed(1)}%`, x + (barWidth - 20) / 2, y - 5);
        
        // Draw department name
        ctx.fillText(stat.department, x + (barWidth - 20) / 2, height - 10);
      });
      
      // Draw y-axis labels
      ctx.fillStyle = '#718096';
      ctx.textAlign = 'right';
      ctx.fillText('0%', 45, height - 30);
      ctx.fillText('50%', 45, height - 30 - (0.5 * scale));
      ctx.fillText('100%', 45, height - 30 - (1 * scale));
    }
  },
  watch: {
    selectedDrillId() {
      this.$nextTick(() => {
        this.updateChart();
      });
    }
  }
};
</script>

<template>
  <div class="bg-white rounded-lg border shadow-sm p-6">
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Dropdown for selecting drill session -->
      <div class="md:col-span-1">
        <label for="drill-select" class="block text-sm font-medium text-gray-700 mb-2">훈련회차 선택</label>
        <select 
          id="drill-select" 
          v-model="selectedDrillId" 
          class="w-full rounded-md border border-gray-300 p-2"
          @change="updateChart"
        >
          <option v-for="drill in drills" :key="drill.id" :value="drill.id">
            {{ formatDate(drill.date) }} ({{ drill.recipients.length }}명)
          </option>
        </select>
        
        <div v-if="selectedDrill" class="mt-6">
          <h3 class="font-medium mb-2">훈련 정보</h3>
          <div class="text-sm text-gray-600">
            <p><strong>일시:</strong> {{ formatDate(selectedDrill.date) }}</p>
            <p><strong>대상자 수:</strong> {{ selectedDrill.recipients.length }}명</p>
            <p><strong>부서 수:</strong> {{ selectedDrill.departmentStats.length }}개</p>
          </div>
        </div>
      </div>
      
      <!-- Bar chart -->
      <div class="md:col-span-2">
        <h3 class="font-medium mb-4">부서별 링크 미탐지율</h3>
        
        <div v-if="!selectedDrill" class="text-center py-10 text-gray-500">
          왼쪽에서 훈련회차를 선택해주세요.
        </div>
        
        <div v-else>
          <div class="relative h-80 border rounded-md p-4">
            <canvas ref="chartCanvas" class="w-full h-full"></canvas>
          </div>
          
          <p class="text-xs text-gray-500 mt-2">※링크 미탐지율 : 모의 악성메일 내 링크를 클릭한 인원의 비율</p>
        </div>
      </div>
    </div>
    
    <!-- Detailed results table -->
    <div v-if="selectedDrill" class="mt-8">
      <h3 class="font-medium mb-4">상세 결과</h3>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">부서</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">대상자 수</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">클릭 수</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">미탐지율</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="stat in selectedDrill.departmentStats" :key="stat.department">
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.department }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.total }}명</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ stat.clicked }}명</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ (stat.ratio * 100).toFixed(1) }}%</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>


