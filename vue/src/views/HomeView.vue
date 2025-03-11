<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const navigateToSending = () => {
  router.push('/sending');
};

const navigateToResults = () => {
  router.push('/result');
};
</script>

<template>
  <div class="home-container">
    <div class="hero-section">
      <h1 class="hero-title">지능형 위협메일 모의훈련 솔루션</h1>
      <p class="hero-subtitle">위협메일 대응훈련과 모니터링을 통한 정보 보안 강화</p>
      
      <div class="hero-actions">
        <button @click="navigateToSending" class="btn btn-primary">
          <span class="icon">📤</span>
          모의 위협메일 발송
        </button>
        <button @click="navigateToResults" class="btn btn-secondary">
          <span class="icon">📊</span>
          대응 결과 확인
        </button>
      </div>
    </div>
    
    <div class="info-section">
      <div class="info-card">
        <div class="info-icon">🔒</div>
        <h3>모의 위협메일 발송</h3>
        <p>'발송' 메뉴를 통해 수신자를 선택하고<br>모의 위협메일을 발송할 수 있습니다.</p>
      </div>
      
      <div class="info-card">
        <div class="info-icon">📈</div>
        <h3>결과 확인</h3>
        <p>'결과확인' 메뉴를 통해<br>부서별 위협메일 보안대응 결과를<br>확인할 수 있습니다.</p>
      </div>
      
      <div class="info-card">
        <div class="info-icon">🛡️</div>
        <h3>보안 강화</h3>
        <p>모의 훈련을 통해<br>조직의 보안 인식을 높이고<br>실제 공격에 대비할 수 있습니다.</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  gap: 3rem;
}

.hero-section {
  background-color: #1e2639;
  color: white;
  padding: 3rem 2rem;
  border-radius: 0.5rem;
  text-align: center;
  background-image: linear-gradient(rgba(30, 38, 57, 0.9), rgba(30, 38, 57, 0.9)), 
                    url('/placeholder.svg?height=400&width=1200');
  background-size: cover;
  background-position: center;
}

.hero-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 0.375rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.btn-primary {
  background-color: #ff3b30;
  color: white;
}

.btn-primary:hover {
  background-color: #e0352b;
}

.btn-secondary {
  background-color: rgba(255, 255, 255, 0.15);
  color: white;
}

.btn-secondary:hover {
  background-color: rgba(255, 255, 255, 0.25);
}

.info-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.info-card {
  background-color: white;
  border-radius: 0.5rem;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.info-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.info-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: #1e2639;
}

.info-card p {
  color: #4b5563;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .btn {
    width: 100%;
    justify-content: center;
  }
}
</style>

