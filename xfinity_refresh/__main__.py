from multiprocessing import Process
from elevate import elevate
from argparse import ArgumentParser
from xfinity_refresh.activate_pass import activate_pass
from xfinity_refresh.change_mac import change_mac
from halo import Halo
from select import select
import sys
import time


def main():
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    parser.add_argument('--once', dest='once', action='store_true')
    args = parser.parse_args()

    while True:
        elevate(graphical=False)
        exitcode = 1
        while exitcode:
            change_mac(args.iface)
            p = Process(target=activate_pass)
            p.start()
            p.join()
            exitcode = p.exitcode

        if args.once:
            break
        else:
            with Halo(spinner='clock') as s:
                for i in reversed(range(60*59)):
                    s.text = f'Waiting {int(i/60)}m {i%60}s until next pass... [q]uit | [r]efresh'
                    rlist, _, _ = select([sys.stdin], [], [], 0)
                    if rlist:
                        key = sys.stdin.readline()[0]
                        if key == 'q':
                            return
                        elif key == 'r':
                            break
                        else:
                            s.text = f'Command not recognized: {key}'

                    time.sleep(1)


if __name__ == '__main__':
    main()
