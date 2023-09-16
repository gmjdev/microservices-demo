import http from 'k6/http';
import { check, sleep } from 'k6';

export const GREET_URL = 'http://localhost:8080/greet-svc/greet?q=Girish&highCpu=false';

export const options = {
    vus: 20,
    duration: '10s',
    // define thresholds
    // Assert for performance with thresholds
    thresholds: {
        http_req_failed: ['rate<0.01'], // http errors should be less than 1%
        http_req_duration: ['p(99)<2000'], // 99% of requests should be below 1s
    },
};

export default function () {
  const res = http.get(GREET_URL);
  check(res, {
    'Response code 200' : (res) => res.status == 200,
  })
}
