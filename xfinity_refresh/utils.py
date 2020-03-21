from xfinity_refresh import activate_pass, change_mac
from multiprocessing import Process
import time

def change_activate(interface):
    exitcode = 1
    while exitcode:
        new_mac = change_mac(interface)
        time.sleep(15) # wait for DHCP and routing stuff
        p = Process(target=activate_pass)
        p.start()
        p.join()
        exitcode = p.exitcode
    return new_mac

