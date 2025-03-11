<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header class="header">
    <div class="header-container">
      <div class="logo">
        <img src="/logo.png" alt="Logo" class="logo-image" />
        <span class="logo-text">지능형 위협메일 모의훈련</span>
      </div>
      <nav class="main-nav">
        <RouterLink to="/" class="nav-item">메인페이지</RouterLink>
        <RouterLink to="/sending" class="nav-item">발송</RouterLink>
        <RouterLink to="/result" class="nav-item">결과확인</RouterLink>
      </nav>
    </div>
  </header>

  <main class="main-content">
    <RouterView />
  </main>

  <footer class="footer">
    <div class="footer-container">
        <span class="logo-text-footer">지능형 위협메일 모의훈련 v2025&nbsp;&nbsp;</span>
        <img src="/logo.png" alt="Logo" class="logo-image-footer" />
    </div>
  </footer>
</template>

<style scoped>
.header {
  width: 100%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: #1e2639;
  color: white;
}
.footer {
  width: 100%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
  background-color: #1e2639;
  color: white;
}

.footer-container {
  display: flex;
  align-items: right;
  justify-content: right;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.5rem 1rem;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0.5rem 1rem;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.logo-image {
  height: 32px;
}

.logo-image-footer {
  height: 20px;
}

.logo-text {
  font-size: 1.2rem;
  font-weight: 600;
}

.logo-text-footer {
  font-size: 1.0rem;
  font-weight: 600;
}

.main-nav {
  display: flex;
  gap: 0.5rem;
}

.nav-item {
  padding: 0.75rem 1.25rem;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-weight: 500;
  font-size: 1rem;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.router-link-exact-active {
  color: white !important;
  background-color: rgba(255, 255, 255, 0.15);
  position: relative;
}

.router-link-exact-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #ff3b30;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem;
}

@media (max-width: 768px) {
  .header-container {
    flex-direction: column;
    padding: 0.5rem;
  }
  
  .main-nav {
    width: 100%;
    justify-content: center;
    margin-top: 0.5rem;
  }
  
  .nav-item {
    font-size: 0.9rem;
    padding: 0.6rem 1rem;
  }
}
</style>

