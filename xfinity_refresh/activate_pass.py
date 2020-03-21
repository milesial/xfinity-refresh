from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities
from selenium.common.exceptions import WebDriverException
from halo import Halo
import time
import os, pwd, grp


def activate_pass():
    if os.getuid() == 0:
        os.setuid(1000)

    chrome_options = Options()
    chrome_options.add_argument("--headless")
    browser = webdriver.Chrome(options=chrome_options, executable_path='/usr/bin/chromedriver',
                               service_args=["--verbose", "--log-path=/tmp/chrome.log"])
    wait = WebDriverWait(browser, 30)

    with Halo(text='Going to the capture page', spinner='dots') as s:
        try:
            # 1. capture page
            browser.get('http://gstatic.com/generate_204')
            # green button
            element = wait.until(EC.presence_of_element_located((By.ID, 'banner_green_text')))
            url = element.get_attribute('href')
        except WebDriverException as e:
            s.fail(e.msg)
            raise e

    with Halo(text='Choosing the free offer', spinner='dots') as s:
        try:
            # 2. choose free offer
            browser.get(url)
            wait.until(EC.element_to_be_clickable((By.ID, 'offersFreeList1'))).click()
            wait.until(EC.element_to_be_clickable((By.ID, 'continueButton'))).click()
            wait.until(EC.element_to_be_clickable((By.ID, 'upgradeOfferCancelButton'))).click()
        except WebDriverException as e:
            s.fail(e.msg)
            raise e

    with Halo('Creating an account') as s:
        try:
            # 3. create account
            wait.until(EC.element_to_be_clickable((By.ID, 'submitButton')))
            wait.until(EC.element_to_be_clickable((By.ID, 'firstName'))).send_keys('John')
            wait.until(EC.element_to_be_clickable((By.ID, 'lastName'))).send_keys('McCain')
            wait.until(EC.element_to_be_clickable((By.ID, 'userName'))).send_keys('McCain'+str(time.time()))
            wait.until(EC.element_to_be_clickable((By.ID, 'alternateEmail'))).send_keys(str(time.time())+'@gmail.com')
            wait.until(EC.element_to_be_clickable((By.ID, 'dk0-secretQuestion'))).click()
            wait.until(EC.element_to_be_clickable((By.ID, 'dk0-What-is your favorite movie?'))).click()
            wait.until(EC.element_to_be_clickable((By.ID, 'secretAnswer'))).send_keys('McCain')
            wait.until(EC.element_to_be_clickable((By.ID, 'password'))).send_keys('aqzsedfrde4512')
            wait.until(EC.element_to_be_clickable((By.ID, 'passwordRetype'))).send_keys('aqzsedfrde4512')
            wait.until(EC.element_to_be_clickable((By.ID, 'submitButton'))).click()
        except WebDriverException as e:
            s.fail(e.msg)
            raise e

    with Halo('Activating pass') as s:
        # 4. activate pass
        try:
            try:
                wait.until(EC.element_to_be_clickable((By.ID, '_orderConfirmationActivatePass'))).click()
            except:
                browser.refresh()
                wait.until(EC.element_to_be_clickable((By.ID, '_orderConfirmationActivatePass'))).click()

        except WebDriverException as e:
            s.fail(e.msg)
            raise e

        s.succeed('Pass activated on {}'.format(time.ctime()))


if __name__ == '__main__':
    activate_pass()
