from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from halo import Halo
import time
import os, pwd, grp


def activate_pass():
    if os.getuid() == 0:
        os.setuid(1000)

    options = FirefoxOptions()
    options.add_argument("--headless")
    browser = webdriver.Firefox(options=options)
    browser.implicitly_wait(40) # seconds

    with Halo(text='Going to the capture page', spinner='dots'):
        # 1. capture page
        browser.get('http://detectportal.firefox.com/success.txt')
        # green button
        url = browser.find_element_by_id('banner_green_text').get_attribute('href')

    with Halo(text='Choosing the free offer', spinner='dots'):
        # 2. choose free offer
        browser.get(url)
        browser.find_element_by_id('offersFreeList1').click()
        browser.find_element_by_id('continueButton').click()
        browser.find_element_by_id('upgradeOfferCancelButton').click()

        time.sleep(3)

    with Halo('Creating an account'):
        # 3. create account
        browser.find_element_by_id('firstName').send_keys('John')
        browser.find_element_by_id('lastName').send_keys('McCain')
        browser.find_element_by_id('userName').send_keys('McCain'+str(time.time()))
        browser.find_element_by_id('alternateEmail').send_keys(str(time.time())+'@gmail.com')
        browser.find_element_by_id('dk0-secretQuestion').click()
        time.sleep(1)
        browser.find_element_by_id('dk0-What-is your favorite movie?').click()
        browser.find_element_by_id('secretAnswer').send_keys('McCain')
        browser.find_element_by_id('password').send_keys('aqzsedfrde4512')
        browser.find_element_by_id('passwordRetype').send_keys('aqzsedfrde4512')
        browser.find_element_by_id('submitButton').click()

    with Halo('Activating pass') as s:
        # 4. activate pass
        try:
            time.sleep(5)
            browser.find_element_by_id('_orderConfirmationActivatePass').click()
        except:
            browser.refresh()
            time.sleep(5)
            browser.find_element_by_id('_orderConfirmationActivatePass').click()

        s.succeed(f'Pass activated on {time.ctime()}')


if __name__ == '__main__':
    activate_pass()
