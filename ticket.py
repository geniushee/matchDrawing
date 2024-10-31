import time
from datetime import datetime
from selenium import webdriver
from selenium.webdriver.common.by import By

# 웹드라이버 초기화 (크롬 브라우저 사용 예제)
driver = webdriver.Chrome()  # 웹드라이버 경로 필요시 'chromedriver'의 절대 경로 지정

# 특정 시간에 버튼을 눌러야 할 URL로 이동
url = "https://tickets.interpark.com/goods/24015329"
driver.get(url)

# 지정된 클릭 시간 설정 (예: 15시 30분 0초)
target_time = "20:06:00"

def press_button():
    try:
        # data-check 속성이 'true'인 요소를 찾기
        button = driver.find_element(By.CSS_SELECTOR, 'a.sideBtn.is-primary')
        button.click()
        print("Button pressed at:", datetime.now())
    except Exception as e:
        print("Could not press the button:", e)

# 서버 시간 확인 루프
while True:
    current_time = datetime.now().strftime("%H:%M:%S")
    
    if current_time == target_time:  # 현재 시간이 목표 시간과 일치할 때만 클릭
        press_button()
        break  # 버튼을 클릭한 후 루프 종료

    time.sleep(0.1)  # 0.1초 간격으로 시간 확인
