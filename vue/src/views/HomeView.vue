<script setup>
import { onBeforeMount, ref } from 'vue';
import { useRouter } from 'vue-router';
import axiosInst from '@/axios';

const employees = ref(null);
const router = useRouter();

// 목록 데이터 사전 준비
onBeforeMount(()=>{
  getData();
});

// 백앤드에서 데이터 가져오기
const getData = () => {
  // backend server: http://localhost:8080/emp
  // proxy server: http://localhost:5137/api/emp
  axiosInst.get("/emp").then((res)=>{
      // console.log(res.data);
      employees.value = res.data;
  });
}

// 수정 화면 전환
const goModiry= (id) => {
  router.push("/modify?id="+id);
}

// 백앤드에 삭제 요청
const deleteEmployee = (id) => {
  axiosInst.delete("/emp/"+id).then((res)=>{
    if (res.status === 200) getData();
  });
}
</script>

<template>
  <div class="about">
    <h1>This is an home page</h1>
  </div>
  <table>
    <thead>
      <tr>
        <th>ID</th>
        <th>first name</th>
        <th>last name</th>
        <th>Manage</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="employee in employees" :key="employee.employeeId">
        <td>{{ employee.employeeId }}</td>
        <td>{{ employee.firstName }}</td>
        <td>{{ employee.lastName }}</td>
        <td>
          <button @click="goModiry(employee.employeeId)">수정</button>
          <button @click="deleteEmployee(employee.employeeId)">삭제</button>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<style scoped>
table {
  width: 50%;
  border: 1px solid #ccc;
  margin: 0 auto;
}
th {
  background-color: #f0f0f0;
  width: 25%;
  height: 25px;
}
td {
  text-align: center;
}
</style>
