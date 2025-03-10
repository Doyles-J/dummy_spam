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
import axiosInst from '@/axios';

export default {
  data() {
    return {
      drills: [],
      selectedDrillId: null,
      departmentStats: [],
      loading: false,
      error: null
    }
  },
  
  async mounted() {
    await this.loadDrills();
  },
  
  methods: {
    async loadDrills() {
      try {
        const response = await axiosInst.get('/drill/list');
        this.drills = response.data;
        if (this.drills.length > 0) {
          this.selectedDrillId = this.drills[0].drillId;
          await this.loadDrillStats();
        }
      } catch (error) {
        console.error('훈련 목록 로드 실패:', error);
        this.error = '훈련 목록을 불러오는데 실패했습니다.';
      }
    },
    
    async loadDrillStats() {
      if (!this.selectedDrillId) return;
      
      try {
        this.loading = true;
        const response = await axiosInst.get(`/drill/${this.selectedDrillId}/stats`);
        this.departmentStats = response.data.map(stat => ({
          department: `부서 ${stat.deptId}`,
          total: stat.totalEmployees || 0,
          clicked: stat.openCount || 0,
          ratio: stat.deptOpenRatio / 100,
          rating: stat.deptRating
        }));
        
        this.$nextTick(() => {
          this.updateChart();
        });
      } catch (error) {
        console.error('통계 데이터 로드 실패:', error);
        this.error = '통계 데이터를 불러오는데 실패했습니다.';
      } finally {
        this.loading = false;
      }
    }
  },
  
  watch: {
    async selectedDrillId() {
      await this.loadDrillStats();
    }
  }
}
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


