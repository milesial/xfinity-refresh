from multiprocessing import Process
from elevate import elevate
from xfinity_refresh.activate_pass import activate_pass
from xfinity_refresh.change_mac import change_mac

if __name__ == '__main__':
    elevate(graphical=False)
    change_mac('wlx7ca7b0c5f422')
    p = Process(target=activate_pass)
    p.start()
    p.join()
