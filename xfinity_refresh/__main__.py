from multiprocessing import Process
from elevate import elevate
from argparse import ArgumentParser
from xfinity_refresh.activate_pass import activate_pass
from xfinity_refresh.change_mac import change_mac

if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    args = parser.parse_args()

    elevate(graphical=False)
    change_mac(args.iface)
    p = Process(target=activate_pass)
    p.start()
    p.join()
