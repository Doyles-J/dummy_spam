<script setup>
import { onBeforeMount, ref } from 'vue';
import {useRoute, useRouter} from 'vue-router';
import axiosInst from '@/axios';

const firstName = ref("");
const lastName = ref("");
const route = useRoute();
const router = useRouter();
const id = Number(route.query.id);

// 데이터 사전 준비
onBeforeMount(()=>{
  getData();
});

const getData = () => {
  axiosInst.get("/emp/"+id).then((res)=>{
      firstName.value = res.data.firstName;
      lastName.value = res.data.lastName;
  });
}
// 백앤드에 수정 요청
const onModifyHandler = () => {
  const employee = {
    firstName: firstName.value,
    lastName: lastName.value,
  }
  axiosInst.put("/emp/"+id, employee).then((res)=>{
    if(res.status === 200) router.push("/");
  });
}
</script>

<template>
  <div class="about">
    <h1>This is an Modify page</h1>
    <form @submit.prevent>
      <label for="employeeId">employeeId</label>
      <input type="text" id="employeeId" v-model="id" readonly />
      <br />
      <label for="firstName">firstName</label>
      <input type="text" id="firstName" v-model="firstName" />
      <br />
      <label for="lastName">lastName</label>
      <input type="text" id="lastName" v-model="lastName" />
      <button type="button" @click="onModifyHandler">수정</button>
    </form>
  </div>
</template>

