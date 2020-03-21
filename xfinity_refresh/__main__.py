import sys
import time
from argparse import ArgumentParser
from select import select

from elevate import elevate
from halo import Halo

from xfinity_refresh import change_activate


def main():
    parser = ArgumentParser()
    parser.add_argument('iface', metavar='interface', type=str)
    parser.add_argument('--once', dest='once', action='store_true')
    args = parser.parse_args()

    elevate(graphical=False)
    while True:
        change_activate(args.iface)

        if args.once:
            break
        else:
            with Halo(spinner='clock') as s:
                for i in reversed(range(60 * 59)):
                    s.text = 'Waiting {}m {}s until next pass... [q]uit | [r]efresh'.format(int(i / 60), i % 60)
                    rlist, _, _ = select([sys.stdin], [], [], 0)
                    if rlist:
                        key = sys.stdin.readline()[0]
                        if key == 'q':
                            return
                        elif key == 'r':
                            break
                        else:
                            s.text = 'Command not recognized: {}'.format(key)

                    time.sleep(1)


if __name__ == '__main__':
    main()
