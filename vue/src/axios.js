import axios from 'axios';

// axios cros origin 설정
let config = {
  baseURL: '/api',
  timeout: 5000,
  withCredentials: true
}
const axiosInst = axios.create(config);

export default axiosInst;