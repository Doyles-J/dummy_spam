<script setup>
import { onBeforeMount, ref } from 'vue';
import { useRouter } from 'vue-router';
import axiosInst from '@/axios';

const router = useRouter();
const employees = ref(null);

// 목록 데이터 사전 준비

</script>

<template>
  <div class="about">
    <h1>모의 악성메일을 이용한 보안 점검 페이지입니다.</h1>
    <h2>'발송'을 누르시면 수신자를 선택해 메일을 발송할 수 있고</h2>
    <h2>'결과확인'을 누르시면 악성메일 보안대응 결과를 확인할 수 있습니다.</h2>
    <h1>랜딩페이지는 나중에 건드리는걸로</h1>
  </div>
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
