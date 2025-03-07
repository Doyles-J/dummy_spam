<template>
  <div class="container mx-auto p-4 max-w-6xl">
    <h1 class="text-2xl font-bold text-center mb-6">모의 악성메일 발송 시스템</h1>
    
    <div v-if="showResults" class="bg-white rounded-lg border shadow-sm">
      <div class="flex flex-col space-y-1.5 p-6 border-b">
        <h3 class="font-semibold text-lg">발송 결과</h3>
      </div>
      <div class="p-6">
        <div class="space-y-4">
          <div class="p-4 border rounded-md bg-gray-100">
            <h3 class="font-medium mb-2">발송 완료</h3>
            <p>총 {{ recipients.length }}명의 사용자에게 모의 악성메일이 발송되었습니다.</p>
          </div>
          
          <div class="space-y-2">
            <h3 class="font-medium">수신자 목록:</h3>
            <ul class="space-y-1">
              <li v-for="recipient in recipients" :key="recipient.id" class="flex items-center gap-2">
                <span class="text-green-500">✓</span>
                <span>{{ recipient.name }}</span>
                <span class="text-gray-500">({{ recipient.email }})</span>
                <span class="text-sm text-gray-500 ml-2">{{ recipient.department }}</span>
              </li>
            </ul>
          </div>
          
          <button 
            @click="resetSystem" 
            class="w-full bg-gray-900 text-white rounded-md py-2 px-4 hover:bg-gray-800"
          >
            시스템 초기화
          </button>
        </div>
      </div>
    </div>
    
    <div v-else class="grid grid-cols-1 md:grid-cols-7 gap-4">
      <!-- Employee List -->
      <div class="md:col-span-3 bg-white rounded-lg border shadow-sm">
        <div class="flex flex-col space-y-1.5 p-6 border-b">
          <h3 class="font-semibold text-lg">사원명단</h3>
        </div>
        <div class="p-6">
          <div class="h-[400px] overflow-auto pr-4">
            <div class="space-y-2">
              <div 
                v-for="employee in employees" 
                :key="employee.id" 
                :class="[
                  'flex items-center space-x-2 p-2 rounded-md',
                  selectedEmployees.includes(employee.id) ? 'bg-gray-100' : ''
                ]"
              >
                <div class="flex items-center space-x-2">
                  <input 
                    type="checkbox" 
                    :id="`employee-${employee.id}`"
                    :checked="selectedEmployees.includes(employee.id)"
                    @change="toggleEmployeeSelection(employee.id)"
                    class="h-4 w-4"
                  />
                  <div class="grid gap-1">
                    <label 
                      :for="`employee-${employee.id}`"
                      class="font-medium cursor-pointer"
                    >
                      {{ employee.name }}
                    </label>
                    <div class="text-sm text-gray-500 flex flex-col sm:flex-row sm:gap-2">
                      <span>{{ employee.email }}</span>
                      <span>{{ employee.department }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="employees.length === 0" class="text-center py-4 text-gray-500">
                모든 사원이 수신자 명단에 추가되었습니다.
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Arrows -->
      <div class="flex md:flex-col items-center justify-center gap-4 md:col-span-1">
        <button 
          @click="addRecipients"
          :disabled="selectedEmployees.length === 0"
          class="border rounded-md p-2 hover:bg-gray-100 disabled:opacity-50"
          :class="{ 'cursor-not-allowed': selectedEmployees.length === 0 }"
        >
          <span class="block transform rotate-0">→</span>
          <span class="sr-only">수신자 추가</span>
        </button>
        <button 
          @click="removeRecipients"
          :disabled="selectedRecipients.length === 0"
          class="border rounded-md p-2 hover:bg-gray-100 disabled:opacity-50"
          :class="{ 'cursor-not-allowed': selectedRecipients.length === 0 }"
        >
          <span class="block transform rotate-0">←</span>
          <span class="sr-only">수신자 제거</span>
        </button>
      </div>
      
      <!-- Recipients List -->
      <div class="md:col-span-3 bg-white rounded-lg border shadow-sm">
        <div class="flex flex-col space-y-1.5 p-6 border-b">
          <h3 class="font-semibold text-lg">메일 수신자 명단</h3>
        </div>
        <div class="p-6">
          <div class="h-[400px] overflow-auto pr-4">
            <div class="space-y-2">
              <div 
                v-for="recipient in recipients" 
                :key="recipient.id" 
                :class="[
                  'flex items-center space-x-2 p-2 rounded-md',
                  selectedRecipients.includes(recipient.id) ? 'bg-gray-100' : ''
                ]"
              >
                <div class="flex items-center space-x-2">
                  <input 
                    type="checkbox" 
                    :id="`recipient-${recipient.id}`"
                    :checked="selectedRecipients.includes(recipient.id)"
                    @change="toggleRecipientSelection(recipient.id)"
                    class="h-4 w-4"
                  />
                  <div class="grid gap-1">
                    <label 
                      :for="`recipient-${recipient.id}`"
                      class="font-medium cursor-pointer"
                    >
                      {{ recipient.name }}
                    </label>
                    <div class="text-sm text-gray-500 flex flex-col sm:flex-row sm:gap-2">
                      <span>{{ recipient.email }}</span>
                      <span>{{ recipient.department }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="recipients.length === 0" class="text-center py-4 text-gray-500">
                수신자 명단이 비어 있습니다.
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Action Buttons -->
      <div class="md:col-span-7 flex justify-end gap-4 mt-4">
        <button 
          @click="setShowResults(true)"
          :disabled="recipients.length === 0"
          class="border rounded-md py-2 px-4 hover:bg-gray-100 disabled:opacity-50"
          :class="{ 'cursor-not-allowed': recipients.length === 0 }"
        >
          결과확인
        </button>
        <button 
          @click="handleSendEmail"
          :disabled="recipients.length === 0"
          class="bg-gray-900 text-white rounded-md py-2 px-4 hover:bg-gray-800 disabled:opacity-50 flex items-center"
          :class="{ 'cursor-not-allowed': recipients.length === 0 }"
        >
          <span class="mr-2">📤</span>
          발송
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      initialEmployees: [
        { id: 1, name: "김철수", email: "kim@example.com", department: "개발팀" },
        { id: 2, name: "이영희", email: "lee@example.com", department: "마케팅팀" },
        { id: 3, name: "박지민", email: "park@example.com", department: "인사팀" },
        { id: 4, name: "정민수", email: "jung@example.com", department: "영업팀" },
        { id: 5, name: "최유진", email: "choi@example.com", department: "개발팀" },
        { id: 6, name: "강현우", email: "kang@example.com", department: "마케팅팀" },
        { id: 7, name: "윤서연", email: "yoon@example.com", department: "인사팀" },
        { id: 8, name: "장준호", email: "jang@example.com", department: "영업팀" },
        { id: 9, name: "한미나", email: "han@example.com", department: "개발팀" },
        { id: 10, name: "송태호", email: "song@example.com", department: "마케팅팀" },
      ],
      employees: [],
      recipients: [],
      selectedEmployees: [],
      selectedRecipients: [],
      showResults: false
    }
  },
  created() {
    // Initialize employees with a copy of initialEmployees
    this.employees = [...this.initialEmployees];
  },
  methods: {
    addRecipients() {
      if (this.selectedEmployees.length === 0) return;

      const newRecipients = this.employees.filter(emp => this.selectedEmployees.includes(emp.id));
      const remainingEmployees = this.employees.filter(emp => !this.selectedEmployees.includes(emp.id));
      
      this.recipients = [...this.recipients, ...newRecipients];
      this.employees = remainingEmployees;
      this.selectedEmployees = [];
    },
    removeRecipients() {
      if (this.selectedRecipients.length === 0) return;

      const removedRecipients = this.recipients.filter(rec => this.selectedRecipients.includes(rec.id));
      const remainingRecipients = this.recipients.filter(rec => !this.selectedRecipients.includes(rec.id));
      
      this.employees = [...this.employees, ...removedRecipients];
      this.recipients = remainingRecipients;
      this.selectedRecipients = [];
    },
    toggleEmployeeSelection(id) {
      const index = this.selectedEmployees.indexOf(id);
      if (index === -1) {
        this.selectedEmployees.push(id);
      } else {
        this.selectedEmployees.splice(index, 1);
      }
    },
    toggleRecipientSelection(id) {
      const index = this.selectedRecipients.indexOf(id);
      if (index === -1) {
        this.selectedRecipients.push(id);
      } else {
        this.selectedRecipients.splice(index, 1);
      }
    },
    handleSendEmail() {
      if (this.recipients.length === 0) {
        alert("수신자를 선택해주세요.");
        return;
      }
      
      this.showResults = true;
      // In a real application, you would send the email here
    },
    setShowResults(value) {
      this.showResults = value;
    },
    resetSystem() {
      this.employees = [...this.initialEmployees];
      this.recipients = [];
      this.selectedEmployees = [];
      this.selectedRecipients = [];
      this.showResults = false;
    }
  }
}
</script>

