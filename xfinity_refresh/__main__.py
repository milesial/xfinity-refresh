from multiprocessing import Process
from elevate import elevate
from argparse import ArgumentParser
from xfinity_refresh.activate_pass import activate_pass
from xfinity_refresh.change_mac import change_mac
from halo import Halo
import time


def main():
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    parser.add_argument('--once', dest='once', action='store_true')
    args = parser.parse_args()

    while True:
        elevate(graphical=False)
        change_mac(args.iface)
        p = Process(target=activate_pass)
        p.start()
        p.join()
        if args.once:
            break
        else:
            with Halo(spinner='clock') as s:
                for i in reversed(range(60*59)):
                    s.text = f'Waiting {int(i/60)}m {i%60}s until next pass...'
                    time.sleep(1)


if __name__ == '__main__':
    main()
