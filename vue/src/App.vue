<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <nav class="main-nav">
      <RouterLink to="/" class="nav-item">메인페이지</RouterLink>
      <RouterLink to="/sending" class="nav-item">발송</RouterLink>
      <RouterLink to="/result" class="nav-item">결과확인</RouterLink>
    </nav>
  </header>

  <RouterView />
</template>

<style scoped>
header {
  width: 100%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: white;
}

.main-nav {
  display: flex;
  justify-content: center;
  width: 100%;
  background-color: #f8f9fa;
  padding: 0;
  margin: 0;
}

.nav-item {
  flex: 1;
  text-align: center;
  padding: 1rem 0;
  color: #495057;
  text-decoration: none;
  font-weight: 500;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
}

.nav-item:hover {
  background-color: #e9ecef;
  color: #212529;
}

.router-link-exact-active {
  color: #3f51b5 !important;
  border-bottom: 3px solid #3f51b5;
  background-color: rgba(63, 81, 181, 0.05);
}

@media (max-width: 768px) {
  .nav-item {
    font-size: 0.9rem;
    padding: 0.8rem 0;
  }
}
</style>
