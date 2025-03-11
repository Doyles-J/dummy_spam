<template>
  <div class="sending-container">
    <div class="page-header">
      <h1 class="page-title">모의 위협메일 발송 시스템</h1>
      <p class="page-description">사이버 공격 대응훈련을 위한 모의 위협메일을 발송합니다</p>
    </div>

    <div v-if="showResults" class="card results-card">
      <div class="card-header">
        <div class="section-icon">✅</div>
        <h3 class="section-title">발송 결과</h3>
      </div>
      <div class="card-body">
        <div class="success-message">
          <div class="success-icon">✓</div>
          <div class="success-content">
            <h3 class="success-title">발송 완료</h3>
            <p class="success-description">
              총 {{ recipients.length }}명의 사용자에게 모의 위협메일이
              발송되었습니다.
            </p>
          </div>
        </div>

        <div class="recipients-list">
          <h3 class="recipients-title">수신자 목록:</h3>
          <ul class="recipients-items">
            <li
              v-for="recipient in recipients"
              :key="recipient.id"
              class="recipient-item"
            >
              <span class="success-check">✓</span>
              <span class="recipient-name">{{ recipient.name }}</span>
              <span class="recipient-email">({{ recipient.email }})</span>
              <span class="recipient-dept">{{ recipient.department }}</span>
            </li>
          </ul>
        </div>

        <button
          @click="resetSystem"
          class="btn btn-primary btn-full"
        >
          처음으로
        </button>
      </div>
    </div>

    <div v-else class="grid-layout">
      <!-- Employee List -->
      <div class="card">
        <div class="card-header">
          <div class="section-icon">👥</div>
          <h3 class="section-title">사원명단</h3>
        </div>
        <div class="card-controls">
          <select
            v-model="selectedDepartment"
            class="select-control"
          >
            <option value="all">전체 부서</option>
            <option
              v-for="dept in departmentList"
              :key="dept"
              :value="dept.replace('부서 ', '')"
            >
              {{ dept }}
            </option>
          </select>

          <div class="button-group">
            <button
              @click="selectAllInDepartment"
              class="btn btn-secondary btn-sm"
            >
              전체 선택
            </button>
            <button
              @click="unselectAll"
              class="btn btn-secondary btn-sm"
            >
              선택 해제
            </button>
          </div>
        </div>

        <div class="card-body">
          <div class="list-container">
            <div
              v-for="employee in filteredEmployees"
              :key="employee.id"
              :class="[
                'list-item',
                selectedEmployees.includes(employee.id) ? 'list-item-selected' : '',
              ]"
            >
              <div class="list-item-content">
                <input
                  type="checkbox"
                  :id="`employee-${employee.id}`"
                  :checked="selectedEmployees.includes(employee.id)"
                  @change="toggleEmployeeSelection(employee.id)"
                  class="checkbox"
                />
                <div class="list-item-details">
                  <label
                    :for="`employee-${employee.id}`"
                    class="list-item-name"
                  >
                    {{ employee.name }}
                  </label>
                  <div class="list-item-meta">
                    <span class="list-item-email">{{ employee.email }}</span>
                    <span class="list-item-dept">{{ employee.department }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div
              v-if="employees.length === 0"
              class="empty-list"
            >
              {{
                selectedDepartment === "all"
                  ? "모든 사원이 수신자 명단에 추가되었습니다."
                  : "선택한 부서에 사원이 없습니다."
              }}
            </div>
          </div>
        </div>
      </div>

      <!-- Arrows -->
      <div class="transfer-controls">
        <button
          @click="addRecipients"
          :disabled="selectedEmployees.length === 0"
          class="transfer-button"
          :class="{ 'disabled': selectedEmployees.length === 0 }"
        >
          <span class="transfer-icon">→</span>
          <span class="sr-only">수신자 추가</span>
        </button>
        <button
          @click="removeRecipients"
          :disabled="selectedRecipients.length === 0"
          class="transfer-button"
          :class="{ 'disabled': selectedRecipients.length === 0 }"
        >
          <span class="transfer-icon">←</span>
          <span class="sr-only">수신자 제거</span>
        </button>
      </div>

      <!-- Recipients List -->
      <div class="card">
        <div class="card-header">
          <div class="section-icon">📧</div>
          <h3 class="section-title">메일 수신자 명단</h3>
        </div>
        <div class="card-body">
          <div class="list-container">
            <div
              v-for="recipient in recipients"
              :key="recipient.id"
              :class="[
                'list-item',
                selectedRecipients.includes(recipient.id) ? 'list-item-selected' : '',
              ]"
            >
              <div class="list-item-content">
                <input
                  type="checkbox"
                  :id="`recipient-${recipient.id}`"
                  :checked="selectedRecipients.includes(recipient.id)"
                  @change="toggleRecipientSelection(recipient.id)"
                  class="checkbox"
                />
                <div class="list-item-details">
                  <label
                    :for="`recipient-${recipient.id}`"
                    class="list-item-name"
                  >
                    {{ recipient.name }}
                  </label>
                  <div class="list-item-meta">
                    <span class="list-item-email">{{ recipient.email }}</span>
                    <span class="list-item-dept">{{ recipient.department }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div
              v-if="recipients.length === 0"
              class="empty-list"
            >
              수신자 명단이 비어 있습니다.
            </div>
          </div>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="action-buttons">
        <button
          @click="resetRecipients"
          :disabled="recipients.length === 0"
          class="btn btn-secondary"
          :class="{ 'disabled': recipients.length === 0 }"
        >
          초기화
        </button>
        <button
          @click="handleSendEmail"
          :disabled="recipients.length === 0"
          class="btn btn-primary"
          :class="{ 'disabled': recipients.length === 0 }"
        >
          <span class="btn-icon">📤</span>
          발송
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axiosInst from "@/axios";

export default {
  data() {
    return {
      employees: [],
      recipients: [],
      selectedEmployees: [],
      selectedRecipients: [],
      emailSubject: "[ALP-B]접속 테스트용 이메일입니다",
      emailBody: "아래 링크에 접속해주세요",
      showResults: false,
      loading: false,
      selectedDepartment: "all",
      departments: [],
    };
  },
  computed: {
    // 부서별로 필터링된 직원 목록
    filteredEmployees() {
      if (this.selectedDepartment === "all") {
        return this.employees;
      }
      return this.employees.filter(
        (emp) => emp.department === `부서 ${this.selectedDepartment}`
      );
    },
    // 부서 목록 생성
    departmentList() {
      const depts = new Set(this.employees.map((emp) => emp.department));
      return Array.from(depts).sort();
    },
  },
  mounted() {
    this.loadEmployees();
  },
  methods: {
    loadEmployees() {
      axiosInst
        .get("/recipients")
        .then((response) => {
          console.log("첫 번째 데이터 구조:", response.data[0]);
          this.employees = response.data.map((emp) => {
            console.log("처리중인 데이터:", emp);
            return {
              id: emp.empId || emp.id,
              name: emp.empName || emp.name,
              email: emp.empMail || emp.email,
              department: `부서 ${emp.deptId || emp.department}`,
              rank: emp.empRank || emp.rank,
            };
          });
        })
        .catch((error) => {
          console.error("직원 데이터 로드 실패:", error);
        });
    },

    addRecipients() {
      console.log("addRecipients 호출됨"); // 디버깅용
      if (this.selectedEmployees.length === 0) return;

      const newRecipients = this.employees.filter((emp) =>
        this.selectedEmployees.includes(emp.id)
      );
      const remainingEmployees = this.employees.filter(
        (emp) => !this.selectedEmployees.includes(emp.id)
      );

      this.recipients = [...this.recipients, ...newRecipients];
      this.employees = remainingEmployees;
      this.selectedEmployees = [];
    },

    removeRecipients() {
      console.log("removeRecipients 호출됨"); // 디버깅용
      if (this.selectedRecipients.length === 0) return;

      const removedRecipients = this.recipients.filter((rec) =>
        this.selectedRecipients.includes(rec.id)
      );
      const remainingRecipients = this.recipients.filter(
        (rec) => !this.selectedRecipients.includes(rec.id)
      );

      this.employees = [...this.employees, ...removedRecipients];
      this.recipients = remainingRecipients;
      this.selectedRecipients = [];
    },

    toggleEmployeeSelection(id) {
      console.log("toggleEmployeeSelection 호출됨:", id); // 디버깅용
      const index = this.selectedEmployees.indexOf(id);
      if (index === -1) {
        this.selectedEmployees.push(id);
      } else {
        this.selectedEmployees.splice(index, 1);
      }
    },

    toggleRecipientSelection(id) {
      console.log("toggleRecipientSelection 호출됨:", id); // 디버깅용
      const index = this.selectedRecipients.indexOf(id);
      if (index === -1) {
        this.selectedRecipients.push(id);
      } else {
        this.selectedRecipients.splice(index, 1);
      }
    },

    async handleSendEmail() {
      if (this.recipients.length === 0) {
        alert("수신자를 선택해주세요.");
        return;
      }

      try {
        // 로딩 상태 표시
        this.loading = true;
        
        // 타임아웃 없이 요청 보내기
        const response = await axiosInst.post('/drill/send', {
          recipients: this.recipients,
          subject: this.emailSubject,
          body: this.emailBody
        }, { 
          timeout: 0  // 타임아웃 비활성화
        });

        // 응답 처리
        if (response.data.success) {
          localStorage.setItem('lastDrillId', response.data.drillId);
          this.showResults = true;
        } else {
          alert(response.data.message || '메일 발송에 실패했습니다.');
        }
      } catch (error) {
        console.error('훈련 메일 발송 실패:', error);
        
        // 오류 메시지 개선
        let errorMessage = '메일 발송 중 오류가 발생했습니다.';
        if (error.code === 'ECONNABORTED') {
          errorMessage += ' 요청 시간이 초과되었습니다.';
        } else if (error.response) {
          // 서버에서 응답이 왔지만 오류 상태코드인 경우
          errorMessage += ` (${error.response.status}: ${error.response.data.message || '서버 오류'})`;
        } else if (error.message) {
          errorMessage += ` (${error.message})`;
        }
        
        alert(errorMessage);
      } finally {
        // 로딩 상태 해제
        this.loading = false;
      }
    },

    setShowResults(value) {
      this.showResults = value;
    },

    resetSystem() {
      this.loadEmployees(); // initialEmployees 대신 데이터를 다시 로드
      this.recipients = [];
      this.selectedEmployees = [];
      this.selectedRecipients = [];
      this.showResults = false;
    },
    selectAllInDepartment() {
      const employeesToSelect = this.filteredEmployees
        .filter((emp) => !this.recipients.some((r) => r.id === emp.id))
        .map((emp) => emp.id);
      this.selectedEmployees = employeesToSelect;
    },

    // 전체 선택 해제 메소드 추가
    unselectAll() {
      this.selectedEmployees = [];
    },

    resetRecipients() {
      // 선택된 수신자들을 다시 employees 목록으로 되돌림
      this.employees = [...this.employees, ...this.recipients];
      // 수신자 목록 비우기
      this.recipients = [];
      // 선택 상태도 초기화
      this.selectedEmployees = [];
      this.selectedRecipients = [];
    },
  },
};
</script>

<style scoped>
.sending-container {
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

.grid-layout {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 1rem;
}

.card {
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
}

.results-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.section-icon {
  font-size: 1.25rem;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e2639;
}

.card-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e5e7eb;
  background-color: #f9fafb;
}

.select-control {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  background-color: white;
  font-size: 0.875rem;
  color: #1f2937;
  min-width: 150px;
}

.button-group {
  display: flex;
  gap: 0.5rem;
}

.card-body {
  padding: 1rem;
  flex: 1;
  overflow: hidden;
}

.list-container {
  height: 400px;
  overflow-y: auto;
  padding-right: 0.5rem;
}

.list-item {
  padding: 0.75rem;
  border-radius: 0.375rem;
  transition: background-color 0.2s ease;
  margin-bottom: 0.5rem;
}

.list-item:hover {
  background-color: #f9fafb;
}

.list-item-selected {
  background-color: #f3f4f6;
}

.list-item-content {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
}

.checkbox {
  width: 1rem;
  height: 1rem;
  border-radius: 0.25rem;
  border: 1px solid #d1d5db;
  margin-top: 0.25rem;
}

.list-item-details {
  flex: 1;
}

.list-item-name {
  font-weight: 600;
  color: #1f2937;
  display: block;
  margin-bottom: 0.25rem;
  cursor: pointer;
}

.list-item-meta {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.875rem;
  color: #6b7280;
}

.list-item-email {
  word-break: break-all;
}

.list-item-dept {
  color: #4b5563;
}

.empty-list {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6b7280;
  text-align: center;
  padding: 2rem;
}

.transfer-controls {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1rem;
  padding: 1rem 0;
}

.transfer-button {
  width: 2.5rem;
  height: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  background-color: white;
  color: #1f2937;
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.transfer-button:hover:not(.disabled) {
  background-color: #f3f4f6;
  border-color: #9ca3af;
}

.transfer-button.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-buttons {
  grid-column: span 3;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.625rem 1.25rem;
  font-weight: 500;
  border-radius: 0.375rem;
  transition: all 0.2s ease;
  cursor: pointer;
  border: none;
}

.btn-sm {
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
}

.btn-primary {
  background-color: #1e2639;
  color: white;
}

.btn-primary:hover:not(.disabled) {
  background-color: #2d3748;
}

.btn-secondary {
  background-color: white;
  color: #1f2937;
  border: 1px solid #d1d5db;
}

.btn-secondary:hover:not(.disabled) {
  background-color: #f9fafb;
  border-color: #9ca3af;
}

.btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-full {
  width: 100%;
}

.btn-icon {
  margin-right: 0.5rem;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

/* Success Results Styling */
.success-message {
  display: flex;
  align-items: center;
  gap: 1rem;
  background-color: #f0fdf4;
  border: 1px solid #dcfce7;
  border-radius: 0.5rem;
  padding: 1.25rem;
  margin-bottom: 1.5rem;
}

.success-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
  background-color: #22c55e;
  color: white;
  border-radius: 50%;
  font-size: 1.25rem;
  font-weight: bold;
}

.success-content {
  flex: 1;
}

.success-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #166534;
  margin-bottom: 0.25rem;
}

.success-description {
  color: #166534;
}

.recipients-list {
  margin-bottom: 1.5rem;
}

.recipients-title {
  font-size: 1rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 0.75rem;
}

.recipients-items {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
}

.recipient-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.recipient-item:last-child {
  border-bottom: none;
}

.success-check {
  color: #22c55e;
  margin-right: 0.5rem;
  font-weight: bold;
}

.recipient-name {
  font-weight: 500;
  color: #1f2937;
  margin-right: 0.5rem;
}

.recipient-email {
  color: #6b7280;
  margin-right: 0.5rem;
}

.recipient-dept {
  color: #4b5563;
  margin-left: auto;
  font-size: 0.875rem;
}

@media (max-width: 768px) {
  .grid-layout {
    grid-template-columns: 1fr;
  }
  
  .transfer-controls {
    flex-direction: row;
    justify-content: center;
    padding: 0.5rem 0;
  }
  
  .action-buttons {
    grid-column: 1;
  }
  
  .list-container {
    height: 300px;
  }
}
</style>

